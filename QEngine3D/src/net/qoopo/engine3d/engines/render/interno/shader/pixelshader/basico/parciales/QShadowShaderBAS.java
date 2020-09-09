/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
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
        if (((QMaterialBas) pixel.material).getMapaColor() == null || !render.opciones.isMaterial()) {
            // si no hay textura usa el color del material
            color.set(((QMaterialBas) pixel.material).getColorBase());
        } else {

            //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas
            if (!((QMaterialBas) pixel.material).getMapaColor().isProyectada()) {
                colorDifuso = ((QMaterialBas) pixel.material).getMapaColor().get_QARGB(pixel.u, pixel.v);
            } else {
                colorDifuso = ((QMaterialBas) pixel.material).getMapaColor().get_QARGB((float) x / (float) render.getFrameBuffer().getAncho(), -(float) y / (float) render.getFrameBuffer().getAlto());
            }
            color.set(colorDifuso);
            if (colorDifuso.a < 1 || (((QMaterialBas) pixel.material).isTransparencia() && ((QMaterialBas) pixel.material).getColorTransparente() != null && colorDifuso.toRGB() == ((QMaterialBas) pixel.material).getColorTransparente().toRGB())) {
                return null;
            }
        }

        calcularIluminacion(pixel);
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

//    @Override
    protected void calcularIluminacion(QPixel pixel) {

        //La iluminacion se calcula en el sistema de coordenadas de la camara
        pixel.normal.normalize();
        iluminacion.setColorLuz(QColor.BLACK.clone());
        QMaterialBas material = (QMaterialBas) pixel.material;
        //usa el mapa de iluminacion con el ambiente
        if (material.getMapaEmisivo() != null && render.opciones.isMaterial()) {
            QColor colorEmisivo = material.getMapaEmisivo().get_QARGB(pixel.u, pixel.v);
            iluminacion.setColorAmbiente(colorEmisivo.clone().add(render.getEscena().getLuzAmbiente()));
        } else {
            // si tiene factor de emision toma ese valor solamente
            if (material.getFactorEmision() > 0) {
//                illumination.dR = material.getFactorEmision();
                float factorEmision = material.getFactorEmision();
                iluminacion.setColorAmbiente(new QColor(factorEmision, factorEmision, factorEmision));
                return;//no hago mas calculos 
            } else {
                iluminacion.setColorAmbiente(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));
            }
        }

        TempVars tv = TempVars.get();
        try {
            float factorSombra = 1;//1= no sombra
            float factorSombraSAO = 1;//factor de oclusion ambiental con el mapa SAO
            float rugosidad = material.getRugosidad();
            if (render.opciones.isMaterial() && material.getMapaRugosidad() != null) {
                rugosidad = material.getMapaRugosidad().get_QARGB(pixel.u, pixel.v).r;
            }

            float reflectancia = 1.0f - rugosidad;

            if (render.opciones.isMaterial() && material.getMapaSAO() != null) {
                factorSombraSAO = material.getMapaSAO().get_QARGB(pixel.u, pixel.v).r;
            }

            // solo si hay luces y si las opciones de la vista tiene activado el material
            if (render.opciones.isMaterial() && !render.getLuces().isEmpty()) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
                        factorSombra = 1;
                        QProcesadorSombra proc = luz.getSombras();
                        if (proc != null && render.opciones.isSombras() && material.isSombrasRecibir()) {
                            factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(pixel.ubicacion.getVector3(), pixel.entidad, render.getCamara()), pixel.entidad);
                        }

                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {
//                            vectorLuz.set(pixel.ubicacion.x - luz.entidad.getTransformacion().getTraslacion().x, pixel.ubicacion.y - luz.entidad.getTransformacion().getTraslacion().y, pixel.ubicacion.z - luz.entidad.getTransformacion().getTraslacion().z);
                            vectorLuz.set(pixel.ubicacion.getVector3().clone().subtract(QTransformar.transformarVector(QVector3.zero, luz.entidad, render.getCamara())));
                            distanciaLuz = vectorLuz.length();
                            //solo toma en cuenta a los puntos  q estan en el area de afectacion
                            if (distanciaLuz > luz.radio) {
                                continue;
                            }

                            float alfa = 0.0f;
                            //si es Spot valido que este dentro del cono
                            if (luz instanceof QLuzSpot) {
                                QVector3 coneDirection = ((QLuzSpot) luz).getDirectionTransformada().normalize();
                                alfa = coneDirection.angulo(vectorLuz.clone().normalize());
                                if (alfa > ((QLuzSpot) luz).getAnguloExterno()) {
                                    continue;
                                }
                            }

                            QColor colorLuz = QMath.calcularColorLuz(color, colorEspecular, luz.color, luz.energia * factorSombra * factorSombraSAO, pixel.ubicacion.getVector3(), vectorLuz.invert().normalize(), pixel.normal, material.getSpecularExponent(), reflectancia);
                            //atenuacion                           
//                            float attenuationInv = light.att.constant + light.att.linear * distance + light.att.exponent * distance * distance;
                            colorLuz.scaleLocal(1.0f / (luz.coeficientesAtenuacion.x + luz.coeficientesAtenuacion.y * distanciaLuz + luz.coeficientesAtenuacion.z * distanciaLuz * distanciaLuz));

                            //si la luz es spot, realiza una atenuacion adicional dependiendo del angulo 
                            if (luz instanceof QLuzSpot) {
                                // lo siguiente es.. la diferencia entre alfa y el angulo externo divido con la diferencia entre el angulo interno y el angulo externo (se agrega una validacion para no permitir la division por cero)
                                colorLuz.scaleLocal(QMath.clamp((alfa - ((QLuzSpot) luz).getAnguloExterno()) / Math.min(((QLuzSpot) luz).getAnguloInterno() - ((QLuzSpot) luz).getAnguloExterno(), -0.0001f), 0.0f, 1.0f));
                            }

                            iluminacion.getColorLuz().addLocal(colorLuz);
                        } else if (luz instanceof QLuzDireccional) {
                            vectorLuz.set(((QLuzDireccional) luz).getDirectionTransformada());
                            iluminacion.getColorLuz().addLocal(QMath.calcularColorLuz(color, colorEspecular, luz.color, luz.energia * factorSombra * factorSombraSAO, pixel.ubicacion.getVector3(), vectorLuz.invert().normalize(), pixel.normal, material.getSpecularExponent(), reflectancia));
                        }
                    }
                }
            } else {
                //iluminacion default cuando no hay luces se asume una luz central
                tmpPixelPos.set(pixel.ubicacion.getVector3());
                tmpPixelPos.normalize();
                iluminacion.getColorAmbiente().add(-tmpPixelPos.dot(pixel.normal));
            }
        } finally {
            tv.release();
        }
    }

}
