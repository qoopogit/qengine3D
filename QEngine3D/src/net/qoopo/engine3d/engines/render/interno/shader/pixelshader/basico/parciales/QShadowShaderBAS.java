/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Shader con textura que procesa sombras, iluminacion de entorno, texturas y
 * sombreado phong
 *
 * @author alberto
 */
public class QShadowShaderBAS extends QShader {

    private QColor colorDifuso;
    private QColor colorEspecular = QColor.WHITE.clone();
    private float transparencia;

    public QShadowShaderBAS(QMotorRender render) {
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
//        float a = 1;

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
//            a = pixelColor.a;
//            pixelTransparente = b == 0 && g == 0 && r == 0;// por el momento para prueba a una textura cuando tiene color negro lo hacemos transparente
            if (pixelTransparente) {
//                si es totalmente transparente no se dibuja
                return null;
            }
        }

        calcularIluminacion(iluminacion, pixel);

        // Iluminacion difusa
        color.scale(iluminacion.getColorDifuso());

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

        // Agrega Luz especular.
        color.addLocal(iluminacion.getColorEspecular());

        return color;
    }

//    @Override
    protected void calcularIluminacion(QIluminacion illumination, QPixel pixel) {
        pixel.normal.normalize();

        iluminacionDifusa = 0;
        iluminacionEspecular = 0;

        //usa el mapa de iluminacion con el ambiente
        if (((QMaterialBas) pixel.material).getMapaEmisivo() != null && render.opciones.isMaterial()) {
            QColor colorEmisivo = ((QMaterialBas) pixel.material).getMapaEmisivo().get_QARGB(pixel.u, pixel.v);
            iluminacion.setColorDifuso(colorEmisivo.add(render.getEscena().getLuzAmbiente()));
        } else {

            // si tiene factor de emision toma ese valor solamente
            if (((QMaterialBas) pixel.material).getFactorEmision() > 0) {
//                illumination.dR = ((QMaterialBas) pixel.material).getFactorEmision();
                iluminacion.setColorDifuso(new QColor(((QMaterialBas) pixel.material).getFactorEmision(), ((QMaterialBas) pixel.material).getFactorEmision(), ((QMaterialBas) pixel.material).getFactorEmision()));
                return;//no hago mas calculos 
            } else {
//                illumination.dR = render.getEscena().getLuzAmbiente();
                iluminacion.setColorDifuso(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));
            }
        }

        //tomo el valor del mapa especular, si existe
        if (((QMaterialBas) pixel.material).getMapaEspecular() != null) {
            colorEspecular = ((QMaterialBas) pixel.material).getMapaEspecular().get_QARGB(pixel.u, pixel.v);
        } else {
            colorEspecular = QColor.WHITE;//equivale a multiplicar por 1
        }

        TempVars tv = TempVars.get();
        try {
            iluminacion.setColorEspecular(QColor.BLACK.clone());
            tmpPixelPos.set(pixel.ubicacion.getVector3());
            tmpPixelPos.normalize();

            float factorSombra = 1;

            // solo si hay luces y si la vista es desde textura hacia arriba
            if (!render.getLuces().isEmpty() && render.opciones.isMaterial()) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {

                            vectorLuz.setXYZ(pixel.ubicacion.x - luz.entidad.getTransformacion().getTraslacion().x, pixel.ubicacion.y - luz.entidad.getTransformacion().getTraslacion().y, pixel.ubicacion.z - luz.entidad.getTransformacion().getTraslacion().z);

                            //solo toma en cuenta  a los puntos  q estan en el area de afectacion
                            if (vectorLuz.length() > luz.radio) {
                                continue;
                            }

                            //si es spot valido que este dentro del cono
                            if (luz instanceof QLuzSpot) {
                                QVector3 coneDirection = ((QLuzSpot) luz).getDirection().clone().normalize();
                                tv.vector3f2.set(vectorLuz);
                                if (coneDirection.angulo(tv.vector3f2.normalize()) > ((QLuzSpot) luz).getAngulo()) {
                                    continue;
                                }
                            }

                            QProcesadorSombra proc = luz.getSombras();
                            if (proc != null && render.opciones.isSombras() && ((QMaterialBas) pixel.material).isSombrasRecibir()) {
                                tv.vector3f1.set(pixel.ubicacion.getVector3());
                                factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(tv.vector3f1, pixel.entidad, render.getCamara()), pixel.entidad);
                            }

//                        if (!verificarSombra(vectorLuz, light.entidad.transformacion.getTraslacion().x, light.entidad.transformacion.getTraslacion().y, light.entidad.transformacion.getTraslacion().z, x, y, z)) {
                            distanciaLuz = vectorLuz.x * vectorLuz.x + vectorLuz.y * vectorLuz.y + vectorLuz.z * vectorLuz.z;

                            iluminacionDifusa = -vectorLuz.dotProduct(pixel.normal) * luz.energia / distanciaLuz;
                            //luz difusa
                            if (iluminacionDifusa > 0) {
//                                illumination.dR += (float) luz.color.r * iluminacionDifusa * factorSombra;
                                iluminacion.getColorDifuso().addLocal(luz.color.scale(iluminacionDifusa * factorSombra));
                            }
                            //luz especular
                            // si la luz esta en de frente ejeY no detras de la cara ejeY que no estemos en sombre de esa luz
                            if (vectorLuz.dotProduct(pixel.normal) < 0) {
                                vectorLuz.normalize();
                                tempVector.copyXYZ(pixel.normal);
                                tempVector.multiply(-2 * vectorLuz.dotProduct(pixel.normal));
                                vectorLuz.add(tempVector);

                                iluminacionEspecular = -vectorLuz.dotProduct(tmpPixelPos);
                                if (render.opciones.isVerCarasTraseras()) {
                                    iluminacionEspecular = Math.abs(iluminacionEspecular);
                                }
                                //solo para valores positivos, si tiene 0 no calculo la luz especular
                                if (((QMaterialBas) pixel.material).getSpecularExponent() > 0) {
                                    if (iluminacionEspecular > 0) {
                                        iluminacionEspecular = (float) Math.pow(iluminacionEspecular, ((QMaterialBas) pixel.material).getSpecularExponent());
                                        iluminacionEspecular = iluminacionEspecular * luz.energia / distanciaLuz;
//                                        illumination.sR += colorEspecular.r * ((QMaterialBas) pixel.material).getColorEspecular().r * iluminacionEspecular * factorSombra;
                                        iluminacion.getColorEspecular().addLocal(colorEspecular.clone().scale(((QMaterialBas) pixel.material).getColorEspecular().scale((1.0f - ((QMaterialBas) pixel.material).getRugosidad()) * iluminacionEspecular * factorSombra)));
                                    }
                                }
//                            }
                            }
                        } else if (luz instanceof QLuzDireccional) {
                            vectorLuz.copyXYZ(((QLuzDireccional) luz).getDirection());

                            QProcesadorSombra proc = luz.getSombras();
                            if (proc != null && render.opciones.isSombras()) {
                                tv.vector3f1.set(pixel.ubicacion.getVector3());
                                factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(tv.vector3f1, pixel.entidad, render.getCamara()), pixel.entidad);
                            }

//                        if (!verificarSombra(vectorLuz, light.entidad.transformacion.getTraslacion().x, light.entidad.transformacion.getTraslacion().y, light.entidad.transformacion.getTraslacion().z, x, y, z)) {
                            vectorLuz.normalize();
                            iluminacionDifusa = -vectorLuz.dotProduct(pixel.normal);
                            if (iluminacionDifusa > 0) {
//                                illumination.dR += luz.energia * (float) luz.color.r * iluminacionDifusa * factorSombra;
                                iluminacion.getColorDifuso().addLocal(((QLuzDireccional) luz).color.scale(luz.energia * iluminacionDifusa * factorSombra));
                            }
                            if (vectorLuz.dotProduct(pixel.normal) < 0) {
                                tempVector.copyXYZ(pixel.normal);
                                tempVector.multiply(-2 * vectorLuz.dotProduct(pixel.normal));
                                vectorLuz.add(tempVector);
                                iluminacionEspecular = -vectorLuz.dotProduct(tmpPixelPos);
                                if (iluminacionEspecular > 0) {
                                    iluminacionEspecular = (float) Math.pow(iluminacionEspecular, ((QMaterialBas) pixel.material).getSpecularExponent());
                                    iluminacion.getColorEspecular().addLocal(colorEspecular.clone().scale(((QMaterialBas) pixel.material).getColorEspecular().scale((1.0f - ((QMaterialBas) pixel.material).getRugosidad()) * iluminacionEspecular * factorSombra * luz.energia)));
                                }
                            }
//                        }

                        }
                    }
                }
            } else {
                //iluminaion default cuando no hay luces se asume una luz central
                iluminacion.getColorDifuso().add(-tmpPixelPos.dotProduct(pixel.normal));
            }

        } finally {
            tv.release();
        }
    }

}
