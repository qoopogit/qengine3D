package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Este shader toma en cuenta las luces del entorno, textura y sombreado phong
 *
 * @author alberto
 */
public class QIluminadoShaderBAS extends QShader {

    private QColor colorDifuso;
    private QColor colorEspecular = QColor.WHITE.clone();
    private float transparencia;

    public QIluminadoShaderBAS(QMotorRender render) {
        super(render);
    }

    @Override
    public QColor colorearPixel(QPixel pixel, int x, int y) {
        if (pixel == null) {
            return null;
        }
        if (!pixel.isDibujar()) {
            return null;
        }

        boolean pixelTransparente = false;
        boolean pixelTransparente2 = false;

        //TOMA EL VALOR DE LA TRANSPARENCIA        
        if (((QMaterialBas) pixel.material).isTransparencia()) {
            //si tiene un mapa de transparencia
            if (((QMaterialBas) pixel.material).getMapaTransparencia() != null) {
                // es una imagen en blanco y negro, toma cualquier canal de color
                transparencia = ((QMaterialBas) pixel.material).getMapaTransparencia().get_QARGB(pixel.u, pixel.v).r;
            } else {
                //toma el valor de transparencia del material
                transparencia = ((QMaterialBas) pixel.material).getTransAlfa();
            }
        } else {
            transparencia = 1;
        }

        if (((QMaterialBas) pixel.material).getMapaDifusa() == null || !render.opciones.isMaterial()) {
            // si no hay textura usa el color del material
            color.set(((QMaterialBas) pixel.material).getColorDifusa());
        } else {
            //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas
            if (!((QMaterialBas) pixel.material).getMapaDifusa().isProyectada()) {
                colorDifuso = ((QMaterialBas) pixel.material).getMapaDifusa().get_QARGB(pixel.u, pixel.v);
            } else {
                colorDifuso = ((QMaterialBas) pixel.material).getMapaDifusa().get_QARGB((float) x / (float) render.getFrameBuffer().getAncho(), -(float) y / (float) render.getFrameBuffer().getAlto());
            }

            switch (((QMaterialBas) pixel.material).getMapaDifusa().getModo()) {
                case QProcesadorTextura.MODO_COMBINAR:
                    color.r = (colorDifuso.r + ((QMaterialBas) pixel.material).getColorDifusa().r) / 2;
                    color.g = (colorDifuso.g + ((QMaterialBas) pixel.material).getColorDifusa().g) / 2;
                    color.b = (colorDifuso.b + ((QMaterialBas) pixel.material).getColorDifusa().b) / 2;
                    break;
                case QProcesadorTextura.MODO_REMPLAZAR:
                default:
                    color.set(colorDifuso);
                    break;
            }
            pixelTransparente2 = ((QMaterialBas) pixel.material).isTransparencia() && ((QMaterialBas) pixel.material).getColorTransparente() != null && colorDifuso.toRGB() == ((QMaterialBas) pixel.material).getColorTransparente().toRGB();//sin alfa

            //solo activa la transparencia si tiene el canal alfa y el color es negro (el negro es el color transparente)
            pixelTransparente = colorDifuso.a < 1 || pixelTransparente2;//transparencia imagenes png

            if (pixelTransparente) {
                return null;
            }
        }

        calcularIluminacion(iluminacion, pixel);
        color.scale(iluminacion.getColorAmbiente());
        color.addLocal(iluminacion.getColorLuz());
        //***********************************************************
        //******                    TRANSPARENCIA
        //***********************************************************
        if (((QMaterialBas) pixel.material).isTransparencia() && transparencia < 1) {
            QColor tmp = render.getFrameBuffer().getColor(x, y);//el color actual en el buffer para mezclarlo
            color.r = (1 - transparencia) * tmp.r + transparencia * color.r;
            color.g = (1 - transparencia) * tmp.g + transparencia * color.g;
            color.b = (1 - transparencia) * tmp.b + transparencia * color.b;
            tmp = null;
        }

        return color;
    }

    protected void calcularIluminacion(QIluminacion illumination, QPixel pixel) {
        pixel.normal.normalize();
        iluminacion.setColorLuz(QColor.BLACK.clone());
        //usa el mapa de iluminacion con el ambiente
        if (((QMaterialBas) pixel.material).getMapaEmisivo() != null && render.opciones.isMaterial()) {
            QColor colorEmisivo = ((QMaterialBas) pixel.material).getMapaEmisivo().get_QARGB(pixel.u, pixel.v);
            iluminacion.setColorAmbiente(colorEmisivo.add(render.getEscena().getLuzAmbiente()));
        } else {
            // si tiene factor de emision toma ese valor solamente
            if (((QMaterialBas) pixel.material).getFactorEmision() > 0) {
//                illumination.dR = ((QMaterialBas) pixel.material).getFactorEmision();
                float factorEmision = ((QMaterialBas) pixel.material).getFactorEmision();
                iluminacion.setColorAmbiente(new QColor(factorEmision, factorEmision, factorEmision));
                return;//no hago mas calculos 
            } else {
//                illumination.dR = render.getEscena().getLuzAmbiente();
                iluminacion.setColorAmbiente(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));
            }
        }

        TempVars tv = TempVars.get();
        try {
            
            float reflectancia = 1.0f - ((QMaterialBas) pixel.material).getRugosidad();
            // solo si hay luces y si las opciones de la vista tiene activado el material
            if (render.opciones.isMaterial() && !render.getLuces().isEmpty()) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {

                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {
                            vectorLuz.setXYZ(pixel.ubicacion.x - luz.entidad.getTransformacion().getTraslacion().x, pixel.ubicacion.y - luz.entidad.getTransformacion().getTraslacion().y, pixel.ubicacion.z - luz.entidad.getTransformacion().getTraslacion().z);
                            //solo toma en cuenta  a los puntos  q estan en el area de afectacion
                            if (vectorLuz.length() > luz.radio) {
                                continue;
                            }
                            //si es Spot valido que este dentro del cono
                            if (luz instanceof QLuzSpot) {
                                QVector3 coneDirection = ((QLuzSpot) luz).getDirection().clone().normalize();
                                tv.vector3f2.set(vectorLuz);
                                if (coneDirection.angulo(tv.vector3f2.normalize()) > ((QLuzSpot) luz).getAngulo()) {
                                    continue;
                                }
                            }
                            distanciaLuz = vectorLuz.x * vectorLuz.x + vectorLuz.y * vectorLuz.y + vectorLuz.z * vectorLuz.z;
                            QColor colorLuz = QMath.calcularColorLuz(color.clone(), colorEspecular.clone(), luz.color, luz.energia, pixel.ubicacion.getVector3(), vectorLuz.normalize().invert(), pixel.normal, ((QMaterialBas) pixel.material).getSpecularExponent(), reflectancia);
                            colorLuz.scaleLocal(1.0f / distanciaLuz);
                            iluminacion.getColorLuz().addLocal(colorLuz);
                        } else if (luz instanceof QLuzDireccional) {
                            vectorLuz.copyXYZ(((QLuzDireccional) luz).getDirection());
                            iluminacion.getColorLuz().addLocal(QMath.calcularColorLuz(color, colorEspecular, luz.color, luz.energia, pixel.ubicacion.getVector3(), vectorLuz.normalize().invert(), pixel.normal, ((QMaterialBas) pixel.material).getSpecularExponent(), reflectancia));
                        }
                    }
                }
            } else {
                //iluminacion default cuando no hay luces se asume una luz central
                tmpPixelPos.set(pixel.ubicacion.getVector3());
                tmpPixelPos.normalize();
                iluminacion.getColorAmbiente().add(-tmpPixelPos.dotProduct(pixel.normal));
            }
        } finally {
            tv.release();
        }
    }

}
