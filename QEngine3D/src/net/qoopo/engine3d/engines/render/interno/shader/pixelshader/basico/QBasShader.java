/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTexturaUtil;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorMipMap;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Calcula el color e iluminación de cada pixel, calcula la reflexion y
 * refraccion, iluminacion de entorno, sombras, textura y sombreado de phong
 *
 * @author alberto
 */
public class QBasShader extends QShader {

    private QColor colorEspecular = QColor.WHITE.clone();
    private QColor colorReflejo;
    private QColor colorRefraccion;
    private QColor colorEntorno = QColor.WHITE.clone();
//    private QColor colorDesplazamiento;
    private float transparencia;
    private float factorMetalico = 0;
    private float factorFresnel = 0;

    public QBasShader(QMotorRender render) {
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
        QMaterialBas material = (QMaterialBas) pixel.material;

        //modifica los valores xyz del pixel de acuerdo al mapa de desplazamiento
//        if (material.getMapaDesplazamiento() != null) {
//            colorDesplazamiento = material.getMapaDesplazamiento().get_QARGB(currentPixel.u, currentPixel.v);
//            // el mapa de desplazamiento es uno a escalas de grises, sin emabargo aun hare el promedio de los 3 canales 
//            float fac = (colorDesplazamiento.r + colorDesplazamiento.g + colorDesplazamiento.b) / 3.0f * material.getFactorNormal();
//            QVector3 vec = new QVector3(currentPixel.x, currentPixel.y, currentPixel.z);
//            vec.add(currentPixel.normal.clone().multiply(fac));
//            currentPixel.x = vec.x;
//            currentPixel.y = vec.y;
//            currentPixel.z = vec.z;
//        }
//        //TOMA EL VALOR DE LA TRANSPARENCIA        
        if (material.isTransparencia()) {
            //si tiene un mapa de transparencia
            if (material.getMapaTransparencia() != null) {
                // es una imagen en blanco y negro, toma cualquier canal de color
                transparencia = material.getMapaTransparencia().get_QARGB(pixel.u, pixel.v).r;
            } else {
                //toma el valor de transparencia del material
                transparencia = material.getTransAlfa();
            }
        } else {
            transparencia = 1;
        }
        /**
         * ********************************************************************************
         * COLOR DIFUSO /BASE
         * ********************************************************************************
         */
        if (material.getMapaColor() == null || !render.opciones.isMaterial()) {
            // si no hay textura usa el color del material
            color.set(material.getColorBase());
        } else {
            if (!material.getMapaColor().isProyectada()) {
                //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas 
                color = material.getMapaColor().get_QARGB(pixel.u, pixel.v);
            } else {
                //si es proyectada se asume que la textura es el resultado de un renderizador
                // por lo tanto coresponde a una pantalla y debemos tomar las mismas coordenadas
                // que llegan en X y Y, sin embargo las coordenadas UV estan normalizadas de 0 a 1
                // por lo tanto convertimos las coordeandas XyY a coordenadas UV 
                color = material.getMapaColor().get_QARGB((float) x / (float) render.getFrameBuffer().getAncho(), -(float) y / (float) render.getFrameBuffer().getAlto());
            }

            // si se configuro un color transparente para la textura           
            //solo activa la transparencia si tiene el canal alfa 
            if (color.a < 1 || (material.isTransparencia() && material.getColorTransparente() != null && color.toRGB() == material.getColorTransparente().toRGB())) {
                return null;
            }
        }

        //tomo el valor del mapa especular, si existe
        // es usado en el calculo de la iluminacion y en el reflejo/refraccion del entorno
        if (material.getMapaEspecular() != null) {
            colorEspecular = material.getMapaEspecular().get_QARGB(pixel.u, pixel.v);
        } else {
            colorEspecular = QColor.WHITE;//equivale a multiplicar por 1
        }

        if (material.getMapaMetalico() != null) {
            factorMetalico = material.getMapaMetalico().get_QARGB(pixel.u, pixel.v).r;
        } else {
            factorMetalico = material.getMetalico();
        }

        calcularEntorno(pixel);
        calcularIluminacion(pixel);

        // Iluminacion ambiente
        color.scale(iluminacion.getColorAmbiente());
//        // Agrega color de la luz
        color.addLocal(iluminacion.getColorLuz());

        //***********************************************************
        //******                    TRANSPARENCIA
        //***********************************************************
        if (material.isTransparencia() && transparencia < 1) {
            QColor tmp = render.getFrameBuffer().getColor(x, y);//el color actual en el buffer para mezclarlo
            color.r = (1 - transparencia) * tmp.r + transparencia * color.r;
            color.g = (1 - transparencia) * tmp.g + transparencia * color.g;
            color.b = (1 - transparencia) * tmp.b + transparencia * color.b;
            tmp = null;
        }
//
//        //lugar de la reflexion despues de la iluminacion
//        //calculo la niebla al final del calculo de iluminacion
//        try {
//            if (QEscena.INSTANCIA != null) {
//                QColor resul = calcularNeblina(color, pixel, QEscena.INSTANCIA.neblina);
//                if (resul != null) {
//                    color.set(resul);
//                }
//            }
//        } catch (Exception e) {
//        }

        return color;
    }

    /**
     * 07/02/2018.Se implementa la iluminacion de Bling-Phong que mejora los
     * tiempos y es el default de OpenGL y Directx
     * https://en.wikipedia.org/wiki/Blinn%E2%80%93Phong_shading_model que
     * mejora
     *
     * @param pixel
     */
    protected void calcularIluminacion(QPixel pixel) {

        //La iluminacion se calcula en el sistema de coordenadas de la camara
        pixel.normal.normalize();
        iluminacion.setColorLuz(QColor.BLACK.clone());
        QMaterialBas material = (QMaterialBas) pixel.material;
        //usa el mapa de iluminacion con el ambiente
        if (material.getMapaEmisivo() != null && render.opciones.isMaterial()) {
            QColor colorEmisivo = material.getMapaEmisivo().get_QARGB(pixel.u, pixel.v);
            iluminacion.setColorAmbiente(colorEmisivo.clone().add(render.getEscena().getColorAmbiente()));
        } else {
            // si tiene factor de emision toma ese valor solamente
            if (material.getFactorEmision() > 0) {
//                illumination.dR = material.getFactorEmision();
                float factorEmision = material.getFactorEmision();
                iluminacion.setColorAmbiente(new QColor(factorEmision, factorEmision, factorEmision));
                return;//no hago mas calculos 
            } else {
                iluminacion.setColorAmbiente(render.getEscena().getColorAmbiente().clone());
            }
        }

        TempVars tv = TempVars.get();
        try {
            float factorSombra = 1;//1= no sombra
            float ao = 1;//factor de oclusion ambiental con el mapa SAO
            float rugosidad = material.getRugosidad();
            if (render.opciones.isMaterial() && material.getMapaRugosidad() != null) {
                rugosidad = material.getMapaRugosidad().get_QARGB(pixel.u, pixel.v).r;
            }

            float reflectancia = 1.0f - rugosidad;

            if (render.opciones.isMaterial() && material.getMapaSAO() != null) {
                ao = material.getMapaSAO().get_QARGB(pixel.u, pixel.v).r;
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

                            QColor colorLuz = QMath.calcularColorLuz(color, colorEspecular, luz.color, luz.energia * factorSombra * ao, pixel.ubicacion.getVector3(), vectorLuz.invert().normalize(), pixel.normal, material.getSpecularExponent(), reflectancia);
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
                            iluminacion.getColorLuz().addLocal(QMath.calcularColorLuz(color, colorEspecular, luz.color, luz.energia * factorSombra * ao, pixel.ubicacion.getVector3(), vectorLuz.invert().normalize(), pixel.normal, material.getSpecularExponent(), reflectancia));
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

    /**
     * Calcula la Reflexión y Refracción utilizando un mapa de entorno (puede
     * ser generado con un mapa de cubo)
     *
     * @param pixel
     */
    private void calcularEntorno(QPixel pixel) {
        // Reflexion y refraccion del entorno (en caso de materiales con refraccion (transparentes)
        QMaterialBas material = (QMaterialBas) pixel.material;
        //verifica que el mimap del mapa de entorno este en nivel 1
        if (material.getMapaEntorno() instanceof QProcesadorMipMap) {
            int nivel = ((QProcesadorMipMap) material.getMapaEntorno()).getNivel();
            if (nivel != 1) {
                ((QProcesadorMipMap) material.getMapaEntorno()).setNivel(1);
            }
        }

        if (render.opciones.isMaterial()
                && //esta activada la opción de material
                material.getMapaEntorno() != null //tiene un mapa de entorno
                && (material.isReflexion() || material.isRefraccion()) //tien habilitada la reflexión y/o la refración
                ) {
            TempVars tm = TempVars.get();
            try {

                //*********************************************************************************************
                //******                    VECTOR NORMAL  
                //*********************************************************************************************
                //la normal del pixel, quitamos la transformacion de la ubicacion y volvemos a calcularla en las coordenadas del mundo
//                          tm.vector3f2.set(pixel.normal);
                tm.vector3f2.set(QTransformar.transformarVectorNormal(QTransformar.transformarVectorNormalInversa(pixel.normal, pixel.entidad, render.getCamara()), pixel.entidad.getMatrizTransformacion(QGlobal.tiempo)));
                tm.vector3f2.normalize();

                //*********************************************************************************************
                //******                    VECTOR VISION 
                //*********************************************************************************************
                //para obtener el vector vision quitamos la transformacion de la ubicacion y volvemos a calcularla en las coordenadas del mundo
//                tm.vector3f1.set(currentPixel.ubicacion.getVector3());                
                tm.vector3f1.set(QTransformar.transformarVector(QTransformar.transformarVectorInversa(pixel.ubicacion, pixel.entidad, render.getCamara()), pixel.entidad).getVector3());
                //ahora restamos la posicion de la camara a la posicion del mundo
                tm.vector3f1.subtract(render.getCamara().getMatrizTransformacion(QGlobal.tiempo).toTranslationVector());
                tm.vector3f1.normalize();

                //************************************************************
                //******                    REFLEXION
                //************************************************************
                if (material.isReflexion()) {
                    tm.vector3f3.set(QMath.reflejarVector(tm.vector3f1, tm.vector3f2));
                    colorReflejo = QTexturaUtil.getColorMapaEntorno(tm.vector3f3, material.getMapaEntorno(), material.getTipoMapaEntorno());
//                    colorReflejo = QTexturaUtil.getColorMapaEntorno(tm.vector3f2, material.getMapaEntorno(), material.getTipoMapaEntorno());
                } else {
                    colorReflejo = null;
                }
                //***********************************************************
                //******                    REFRACCION
                //***********************************************************
                if (material.isRefraccion() && material.getIndiceRefraccion() > 0) {
                    tm.vector3f4.set(QMath.refractarVector(tm.vector3f1, tm.vector3f2, material.getIndiceRefraccion()));
                    colorRefraccion = QTexturaUtil.getColorMapaEntorno(tm.vector3f4, material.getMapaEntorno(), material.getTipoMapaEntorno());
                } else {
                    colorRefraccion = null;
                }
                //APLICACION DEL COLOR DEL ENTORNO              

                //mezclo el color de reflexion con el de refraccion
                if (colorReflejo != null && colorRefraccion != null) {
                    factorFresnel = QMath.factorFresnel(tm.vector3f1, tm.vector3f2, 0);
//                    factorFresnel = QMath.factorFresnel(tm.vector3f2, tm.vector3f1, 0);
                    colorEntorno = QMath.mix(colorRefraccion, colorReflejo, factorFresnel);
//                    colorEntorno.r = QMath.mix(colorRefraccion.r, colorReflejo.r, factorFresnel);
//                    colorEntorno.g = QMath.mix(colorRefraccion.g, colorReflejo.g, factorFresnel);
//                    colorEntorno.b = QMath.mix(colorRefraccion.b, colorReflejo.b, factorFresnel);
                } else if (colorReflejo != null) {
                    colorEntorno = colorReflejo.clone();
                } else if (colorRefraccion != null) {
                    colorEntorno = colorRefraccion.clone();
                }

                //mezcla el color del entorno                
//                color.r = QMath.mix(color.r, colorEntorno.r,  Math.min(factorMetalico,0.9f));
//                color.g = QMath.mix(color.g, colorEntorno.g,  Math.min(factorMetalico,0.9f));
//                color.b = QMath.mix(color.b, colorEntorno.b,  Math.min(factorMetalico,0.9f));
                color = QMath.mix(color, colorEntorno, Math.min(factorMetalico, 0.9f));
            } catch (Exception e) {
//                System.out.println("error reflexion " + e.getMessage());
            } finally {
                tm.release();
            }
        }
    }

}
