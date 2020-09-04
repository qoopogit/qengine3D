/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.PBR;

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
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Calcula el color e iluminación de cada pixel, calcula la reflexion y
 * refraccion, iluminacion de entorno, sombras, textura y sombreado de phong
 *
 * @author alberto
 */
public class QPBRShader extends QShader {

    private QColor colorDifuso;

    private QColor colorEspecular = QColor.WHITE.clone();//color metalico
    private QColor colorReflejo;
    private QColor colorRefraccion;

    private QColor colorEntorno = QColor.WHITE.clone();
//    private QColor colorDesplazamiento;
    private float transparencia;
    private float factorMetalico = 0;
    private float factorFresnel = 0;

    public QPBRShader(QMotorRender render) {
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

        //TOMA EL VALOR DE LA TRANSPARENCIA        
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
        if (material.getMapaDifusa() == null || !render.opciones.isMaterial()) {
            // si no hay textura usa el color del material
            color.set(material.getColorDifusa());
        } else {
            if (!material.getMapaDifusa().isProyectada()) {
                //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas 
                color = material.getMapaDifusa().get_QARGB(pixel.u, pixel.v);
            } else {
                //si es proyectada se asume que la textura es el resultado de un renderizador
                // por lo tanto coresponde a una pantalla y debemos tomar las mismas coordenadas
                // que llegan en X y Y, sin embargo las coordenadas UV estan normalizadas de 0 a 1
                // por lo tanto convertimos las coordeandas XyY a coordenadas UV 
                color = material.getMapaDifusa().get_QARGB((float) x / (float) render.getFrameBuffer().getAncho(), -(float) y / (float) render.getFrameBuffer().getAlto());
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

//        calcularEntorno(pixel);
        calcularIluminacion(pixel);

//        // Iluminacion ambiente
//        color.scale(iluminacion.getColorAmbiente());
//        // Agrega color de la luz
//        color.addLocal(iluminacion.getColorLuz());
//        //***********************************************************
//        //******                    TRANSPARENCIA
//        //***********************************************************
//        if (material.isTransparencia() && transparencia < 1) {
//            QColor tmp = render.getFrameBuffer().getColor(x, y);//el color actual en el buffer para mezclarlo
//            color.r = (1 - transparencia) * tmp.r + transparencia * color.r;
//            color.g = (1 - transparencia) * tmp.g + transparencia * color.g;
//            color.b = (1 - transparencia) * tmp.b + transparencia * color.b;
//            tmp = null;
//        }
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
     * Calcula la Reflexión y Refracción utilizando un mapa de entorno (puede
     * ser generado con un mapa de cubo)
     *
     * @param pixel
     */
    private void calcularEntorno(QPixel pixel) {
        // Reflexion y refraccion del entorno (en caso de materiales con refraccion (transparentes)

        QMaterialBas material = (QMaterialBas) pixel.material;

        if (render.opciones.isMaterial()
                && //esta activada la opción de material
                material.getMapaEntorno() != null //tiene un mapa de entorno
                && (material.isReflexion() || material.isRefraccion()) //tien habilitada la reflexión y/o la refración
                ) {
            TempVars tm = TempVars.get();
            try {

                //la normal del pixel
                tm.vector3f2.set(pixel.normal);
                tm.vector3f2.normalize();

                //*********************************************************************************************
                //******                    VECTOR VISION 
                //*********************************************************************************************
                //para obtener el vector vision quitamos la trasnformacion de la ubicacion y volvemos a calcularla en las coordenadas del mundo
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
                } else {
                    colorReflejo = null;
                }
                //***********************************************************
                //******                    REFRACCION
                //***********************************************************
                if (material.isRefraccion() && material.getIndiceRefraccion() > 0) {
                    tm.vector3f4.set(QMath.refractarVector(tm.vector3f1, tm.vector3f2, material.getIndiceRefraccion())); //indice del aire sobre indice del material
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
//                color.r = QMath.mix(color.r, colorEntorno.r, factorMetalico);
//                color.g = QMath.mix(color.g, colorEntorno.g, factorMetalico);
//                color.b = QMath.mix(color.b, colorEntorno.b, factorMetalico);
                color = QMath.mix(color, colorEntorno, factorMetalico);
            } catch (Exception e) {
//                System.out.println("error reflexion " + e.getMessage());
            } finally {
                tm.release();
            }
        }
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

            //*********************************************************************************************
            //******                    VECTOR VISION 
            //*********************************************************************************************
            //para obtener el vector vision quitamos la trasnformacion de la ubicacion y volvemos a calcularla en las coordenadas del mundo             
            QVector3 V = QTransformar.transformarVector(QTransformar.transformarVectorInversa(pixel.ubicacion, pixel.entidad, render.getCamara()), pixel.entidad).getVector3();

//            QVector3 WorldPos = tm.vector3f1.clone();
            //ahora restamos la posicion de la camara a la posicion del mundo
            V.subtract(render.getCamara().getMatrizTransformacion(QGlobal.tiempo).toTranslationVector());
            V.normalize();

            //*********************************************************************************************
            //******                    VECTOR NORMAL 
            //*********************************************************************************************
            QVector3 N = pixel.normal;
            N.normalize();

            QVector3 F0 = new QVector3(0.04f, 0.04f, 0.04f);
            F0 = QMath.mix(F0, color.rgb(), factorMetalico);

            //ecuacion reflectancia
            QVector3 Lo = QVector3.zero.clone();

            float factorSombra = 1;//1= no sombra
            float factorSombraSAO = 1;//factor de oclusion ambiental con el mapa SAO
            float reflectancia = 1.0f - material.getRugosidad();

            float rugosidad = material.getRugosidad();

            if (render.opciones.isMaterial() && material.getMapaSAO() != null) {
                factorSombraSAO = material.getMapaSAO().get_QARGB(pixel.u, pixel.v).r;
            }
            if (render.opciones.isMaterial() && material.getMapaRugosidad() != null) {
                rugosidad = material.getMapaRugosidad().get_QARGB(pixel.u, pixel.v).r;
            }

            // solo si hay luces y si las opciones de la vista tiene activado el material
            if (render.opciones.isMaterial() && !render.getLuces().isEmpty()) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
//                        factorSombra = 1;
//                        QProcesadorSombra proc = luz.getSombras();
//                        if (proc != null && render.opciones.isSombras() && material.isSombrasRecibir()) {
//                            tm.vector3f1.set(pixel.ubicacion.getVector3());
//                            factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(tm.vector3f1, pixel.entidad, render.getCamara()), pixel.entidad);
//                        }

                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {
                            vectorLuz.set(pixel.ubicacion.getVector3().clone().subtract(QTransformar.transformarVector(luz.entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector(), luz.entidad, render.getCamara())));

                            //solo toma en cuenta  a los puntos  q estan en el area de afectacion
                            if (vectorLuz.length() > luz.radio) {
                                continue;
                            }

                            //si es Spot valido que este dentro del cono
                            if (luz instanceof QLuzSpot) {
                                QVector3 coneDirection = ((QLuzSpot) luz).getDirectionTransformada().clone().normalize();
                                if (coneDirection.angulo(vectorLuz.clone().normalize()) > ((QLuzSpot) luz).getAnguloExterno()) {
                                    continue;
                                }
                            }
                        } else if (luz instanceof QLuzDireccional) {
                            vectorLuz.set(((QLuzDireccional) luz).getDirectionTransformada());
                        }

                        // calculate per-light radiance
                        QVector3 L = vectorLuz.clone().invert().normalize(); //L=normalize(lightPositions[i] - WorldPos);
                        QVector3 H = V.clone().add(L).normalize();//H= normalize(V + L);
                        float distance = vectorLuz.length(); //float distance = length(lightPositions[i] - WorldPos);
                        float attenuation = 1.0f / (distance * distance);
                        QVector3 radiance = luz.color.rgb().multiply(attenuation); //vec3 radiance = lightColors[i] * attenuation;

                        // cook-torrance brdf
                        float NDF = QMath.DistributionGGX(N, H, rugosidad); // N y H
                        float G = QMath.GeometrySmith(N, V, L, rugosidad); // N, V, L
                        QVector3 F = QMath.fresnelSchlick(Math.max(H.dotProduct(V), 0.0f), F0); // H,V

                        QVector3 kS = F;
                        QVector3 kD = QVector3.unitario_xyz.clone().add(kS.clone().invert());
                        kD.multiply(1.0f - factorMetalico);

                        QVector3 numerator = F.clone().multiply(NDF * G);
                        float denominator = 4.0f * Math.max(N.dotProduct(V), 0.0f) * Math.max(N.dotProduct(L), 0.0f); // N, V -- N ,L
                        QVector3 specular = numerator.clone().multiply(1.0f / Math.max(denominator, 0.001f));

                        // add to outgoing radiance Lo
                        float NdotL = Math.max(N.dotProduct(L), 0.0f);//N,L
                        //Lo += (kD * albedo / PI + specular) * radiance * NdotL; 
                        Lo.add(kD.multiply(color.rgb().multiply(1.0f / QMath.PI)).add(specular).multiply(radiance.multiply(NdotL)));
                    }
                }

//                vec3 ambient = vec3(0.03) * albedo * ao;
//                QVector3 ambient = new QVector3(0.03f, 0.03f, 0.03f).multiply(color.rgb().multiply(iluminacion.getColorAmbiente().rgb()) ).multiply(factorSombraSAO);
                QVector3 ambient = new QVector3(0.03f, 0.03f, 0.03f).multiply(color.rgb()).multiply(factorSombraSAO);
//                vec3 color = ambient + Lo;               
//                color = color / (color + vec3(1.0));
//                color = pow(color, vec3(1.0 / 2.2));                                
//                FragColor = vec4(color, 1.0);
                QVector3 tmpColor = ambient.add(Lo);
                tmpColor.divide(tmpColor.add(QVector3.unitario_xyz));
                float tt = 1.0f / 2.2f;
                tmpColor.set(QMath.pow(tmpColor.x, tt), QMath.pow(tmpColor.y, tt), QMath.pow(tmpColor.z, tt));
                color.set(1.0f, tmpColor.x, tmpColor.y, tmpColor.z);

            } else {
                //iluminacion default cuando no hay luces se asume una luz central
                tmpPixelPos.set(pixel.ubicacion.getVector3());
                tmpPixelPos.normalize();
                iluminacion.getColorAmbiente().add(-tmpPixelPos.dotProduct(pixel.normal));

                // Iluminacion ambiente
                color.scale(iluminacion.getColorAmbiente());
//        // Agrega color de la luz
                color.addLocal(iluminacion.getColorLuz());
            }
        } finally {
            tv.release();
        }
    }

}
