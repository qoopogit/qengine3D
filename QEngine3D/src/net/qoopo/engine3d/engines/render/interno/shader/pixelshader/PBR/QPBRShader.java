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
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.textura.QTexturaUtil;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;

/**
 * Sombreado PBR
 *
 * https://www.youtube.com/watch?v=5p0e7YNONr8
 *
 * https://learnopengl.com/PBR/Theory https://learnopengl.com/PBR/Lighting
 *
 * @author alberto
 */
public class QPBRShader extends QShader {
    
    private QColor colorBase = new QColor();//color default, blanco

//    private QColor colorEspecular = QColor.WHITE.clone();//color metalico
    private QColor colorReflejo;
    private QColor colorRefraccion;

//    private QColor colorEntorno = QColor.WHITE.clone();
    private QColor colorEntorno = QColor.BLACK.clone();
//    private QColor colorDesplazamiento;
//    private float transparencia;
    private float rugosidad = 0;
    private float metallic = 0;
    private float factorFresnel = 0;
    
    private static final float exponenteGamma = 1.0f / 2.2f;
    
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
//        if (material.isTransparencia()) {
//            //si tiene un mapa de transparencia
//            if (material.getMapaTransparencia() != null) {
//                // es una imagen en blanco y negro, toma cualquier canal de color
//                transparencia = material.getMapaTransparencia().get_QARGB(pixel.u, pixel.v).r;
//            } else {
//                //toma el valor de transparencia del material
//                transparencia = material.getTransAlfa();
//            }
//        } else {
//            transparencia = 1;
//        }
        /**
         * ********************************************************************************
         * COLOR DIFUSO /BASE
         * ********************************************************************************
         */
        if (material.getMapaDifusa() == null || !render.opciones.isMaterial()) {
            // si no hay textura usa el color del material
            colorBase.set(material.getColorDifusa());
        } else {
            if (!material.getMapaDifusa().isProyectada()) {
                //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas 
                colorBase = material.getMapaDifusa().get_QARGB(pixel.u, pixel.v);
            } else {
                //si es proyectada se asume que la textura es el resultado de un renderizador
                // por lo tanto coresponde a una pantalla y debemos tomar las mismas coordenadas
                // que llegan en X y Y, sin embargo las coordenadas UV estan normalizadas de 0 a 1
                // por lo tanto convertimos las coordeandas XyY a coordenadas UV 
                colorBase = material.getMapaDifusa().get_QARGB((float) x / (float) render.getFrameBuffer().getAncho(), -(float) y / (float) render.getFrameBuffer().getAlto());
            }
            // corrijo canal de material albedo 
            colorBase.set(1.0f, (float) Math.pow(colorBase.r, 2.2f), (float) Math.pow(colorBase.g, 2.2f), (float) Math.pow(colorBase.b, 2.2f));
        }

        //tomo el valor del mapa especular, si existe
        // es usado en el calculo de la iluminacion y en el reflejo/refraccion del entorno
//        if (material.getMapaEspecular() != null) {
//            colorEspecular = material.getMapaEspecular().get_QARGB(pixel.u, pixel.v);
//        } else {
//            colorEspecular = QColor.WHITE;//equivale a multiplicar por 1
//        }
        if (material.getMapaMetalico() != null) {
            metallic = material.getMapaMetalico().get_QARGB(pixel.u, pixel.v).r;
        } else {
            metallic = material.getMetalico();
        }
        
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
     * 07/02/2018.Se implementa la iluminacion de Bling-Phong que mejora los
     * tiempos y es el default de OpenGL y Directx
     * https://en.wikipedia.org/wiki/Blinn%E2%80%93Phong_shading_model que
     * mejora
     *
     * @param pixel
     */
    protected void calcularIluminacion(QPixel pixel) {
//        pixel.normal.normalize();
        iluminacion.setColorLuz(QColor.BLACK.clone());
        QMaterialBas material = (QMaterialBas) pixel.material;
//        usa el mapa de iluminacion con el ambiente
        if (material.getMapaEmisivo() != null && render.opciones.isMaterial()) {
            QColor colorEmisivo = material.getMapaEmisivo().get_QARGB(pixel.u, pixel.v);
            iluminacion.setColorAmbiente(colorEmisivo.clone().add(render.getEscena().getLuzAmbiente()));
        } else {
            // si tiene factor de emision toma ese valor solamente
            if (material.getFactorEmision() > 0) {
                color = colorBase.scale(material.getFactorEmision());
                return;//no hago mas calculos 
            } else {
                iluminacion.setColorAmbiente(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));
            }
        }

//        iluminacion.setColorAmbiente(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));
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
            V.invert();//  Cam-WordPos
            V.normalize();

            //*********************************************************************************************
            //******                    VECTOR NORMAL 
            //*********************************************************************************************
            QVector3 N = pixel.normal;
            N.normalize();

            // el vector F0 debe tener los valores segun el nivel de metalico 
            /*
            Material                     F0 (Linear)                F0 (sRGB)	
                Water                   (0.02, 0.02, 0.02)	(0.15, 0.15, 0.15)  	
                Plastic / Glass (Low)	(0.03, 0.03, 0.03)	(0.21, 0.21, 0.21)	
                Plastic High            (0.05, 0.05, 0.05)	(0.24, 0.24, 0.24)	
                Glass (high) / Ruby	(0.08, 0.08, 0.08)	(0.31, 0.31, 0.31)	
                Diamond                 (0.17, 0.17, 0.17)	(0.45, 0.45, 0.45)	
                Iron                    (0.56, 0.57, 0.58)	(0.77, 0.78, 0.78)	
                Copper                  (0.95, 0.64, 0.54)	(0.98, 0.82, 0.76)	
                Gold                    (1.00, 0.71, 0.29)	(1.00, 0.86, 0.57)	
                Aluminium               (0.91, 0.92, 0.92)	(0.96, 0.96, 0.97)	
                Silver                  (0.95, 0.93, 0.88)	(0.98, 0.97, 0.95)	

             */
            //calcula la reflectancia como normal incidente; Si es material diaeletrico, como el plastico, usa la refletividad base (0.04f), 
            // si es metalico usa el color albeto como reflectividad base, esto quiere decir que el color resultante (del reflejo) se mescla con el color base
            QVector3 F0 = new QVector3(0.03f, 0.03f, 0.03f);
            
            QVector3 albedo = colorBase.rgb();
            
            F0 = QMath.mix(F0, albedo, metallic);

            //ecuacion reflectancia
            QVector3 Lo = QVector3.zero.clone();
            
            float factorSombra = 1;//1= no sombra
            float factorSombraSAO = 1;//factor de oclusion ambiental con el mapa SAO

            rugosidad = material.getRugosidad();
            
            if (render.opciones.isMaterial() && material.getMapaSAO() != null) {
                factorSombraSAO = material.getMapaSAO().get_QARGB(pixel.u, pixel.v).r;
            }
            if (render.opciones.isMaterial() && material.getMapaRugosidad() != null) {
                rugosidad = material.getMapaRugosidad().get_QARGB(pixel.u, pixel.v).r;
            }
            
            rugosidad = Math.max(rugosidad, 0.001f);
            metallic = Math.min(metallic, 0.999f);
            
            float NdotV = Math.max(N.dot(V), 0.00001f);

            // solo si hay luces y si las opciones de la vista tiene activado el material
            if (render.opciones.isMaterial() && !render.getLuces().isEmpty()) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
                        factorSombra = 1;
                        QProcesadorSombra proc = luz.getSombras();
                        if (proc != null && render.opciones.isSombras() && material.isSombrasRecibir()) {
                            factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(pixel.ubicacion, pixel.entidad, render.getCamara()).getVector3(), pixel.entidad);
                        }
                        
                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {
                            vectorLuz.set(pixel.ubicacion.getVector3().clone().subtract(QTransformar.transformarVector(QVector3.zero, luz.entidad, render.getCamara())));
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
                        distanciaLuz = vectorLuz.length(); //float distance = length(lightPositions[i] - WorldPos);
//                        float attenuation = 1.0f / (distanciaLuz * distanciaLuz);
                        float attenuation = 1.0f / (luz.coeficientesAtenuacion.x + luz.coeficientesAtenuacion.y * distanciaLuz + luz.coeficientesAtenuacion.z * distanciaLuz * distanciaLuz);
                        QVector3 radiance = luz.color.rgb().multiply(luz.energia * attenuation); //vec3 radiance = lightColors[i] * attenuation;

                        float NdotL = Math.max(N.dot(L), 0.00001f);
                        float HdotV = Math.max(H.dot(V), 0.00f);
                        float NdotH = Math.max(N.dot(H), 0.00f);
                        // cook-torrance brdf
                        float NDF = QMath.DistributionGGX(NdotH, rugosidad); // N y H
                        float G = QMath.GeometrySmith(NdotV, NdotL, rugosidad); // N, V, L
                        QVector3 F = QMath.fresnelSchlick(HdotV, F0); // H,V, F0  (OK)

                        QVector3 numerator = F.clone().multiply(NDF * G);
                        float denominator = 4.0f * NdotV * NdotL; // N, V -- N ,L                        
                        QVector3 specular = numerator.multiply(1.0f / Math.max(denominator, 0.00001f));

                        // para la conservacion de energia la luz difusa y especular no pueden  estar sobre 1.0f (a menos que la superficie emita luz );
                        // para preservar esta relacion el componenete de luz difusa (kD) deberia ser igual a 1.0f -kS (componenete especular)
                        QVector3 kD = QVector3.unitario_xyz.clone().subtract(F); //F es igual al componenete especular (kS)
                        //multiplicamos kD por el inverso del factor metalico solo los no metales tiene luz difusa, o una mezcla lineal si es parcialmente metalico 
                        kD.multiply(1.0f - metallic);

                        //sombra
                        specular.multiply(factorSombra);
                        kD.multiply(factorSombra);

                        // add to outgoing radiance Lo
                        // notar que :
                        // 1) el angulo de la luz a la superficie afecta especular, no solo a la luz difusa
                        // 2) mezclamos albedo con difusa, pero no con expecular
                        //Lo += (kD * albedo / PI + specular) * radiance * NdotL; 
                        Lo.add(kD.multiply(albedo.clone().multiply(1.0f / QMath.PI)).add(specular).multiply(radiance.multiply(NdotL)));
                    }
                }
            }
//                vec3 ambient = vec3(0.03) * albedo * ao;
            QVector3 ambient = iluminacion.getColorAmbiente().rgb().multiply(albedo).multiply(factorSombraSAO);
//                QVector3 ambient = new QVector3(0.03f, 0.03f, 0.03f).multiply(albedo.clone().multiply(iluminacion.getColorAmbiente().rgb())).multiply(factorSombraSAO);
//                QVector3 ambient = new QVector3(0.03f, 0.03f, 0.03f).multiply(color.rgb()).multiply(factorSombraSAO);
//             

            //--------------------  ILUMINACION AMBIENTAL --------------------------------
            // consigue el ambiente del mapa de entorno (si lo tiene) (IBL - Image Based Limunation)
            if (material.getMapaEntorno() != null) {

                /*
                    vec3 F = FresnelSchlickRoughness(max(dot(N, V), 0.0), F0, roughness);
                    vec3 kS = F;
                    vec3 kD = 1.0 - kS;
                    kD *= 1.0 - metallic;	  

                    vec3 irradiance = texture(irradianceMap, N).rgb;
                    vec3 diffuse    = irradiance * albedo;

                    const float MAX_REFLECTION_LOD = 4.0;
                    vec3 prefilteredColor = textureLod(prefilterMap, R,  roughness * MAX_REFLECTION_LOD).rgb;   
                    vec2 envBRDF  = texture(brdfLUT, vec2(max(dot(N, V), 0.0), roughness)).rg;
                    vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);

                    vec3 ambient = (kD * diffuse + specular) * ao; 
                 */
                QVector3 kS = QMath.fresnelSchlick(NdotV, F0, rugosidad);
//                QVector3 kS = QMath.fresnelSchlick(NdotV, F0);
                QVector3 kD = QVector3.unitario_xyz.clone().subtract(kS);
                kD.multiply(1.0f - metallic);
                //toma el color del mapa de irradiacion usando el vector nomal (en el mapa de reflejos se usa el vector reflejado)
                QVector3 irradiance = QTexturaUtil.getColorMapaEntorno(N, material.getMapaIrradiacion(), material.getTipoMapaEntorno()).rgb();
                QVector3 difuso = irradiance.multiply(albedo);
//                QVector3 specular = QVector3.zero;
                calcularEntorno(pixel);
//                QVector3 specular = colorEntorno.rgb().multiply(kS);
                QVector3 specular = colorEntorno.rgb().multiply(QMath.EnvBRDFApprox(kS, rugosidad, NdotV)); //https://www.unrealengine.com/en-US/blog/physically-based-shading-on-mobile
                ambient = difuso.multiply(kD).add(specular).multiply(factorSombraSAO);
            }

//                   vec3 color = ambient + Lo;               
//                color = color / (color + vec3(1.0));
//                color = pow(color, vec3(1.0 / 2.2));                                
//                FragColor = vec4(color, 1.0);
            QVector3 tmpColor = ambient.add(Lo);
            //HDR tonemapping 
            tmpColor.divide(tmpColor.clone().add(QVector3.unitario_xyz));
            //correccion de gamma

            tmpColor.set(QMath.pow(tmpColor.x, exponenteGamma), QMath.pow(tmpColor.y, exponenteGamma), QMath.pow(tmpColor.z, exponenteGamma));
            //cambia el color
            color.set(1.0f, tmpColor.x, tmpColor.y, tmpColor.z);
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
//                color = QMath.mix(color, colorEntorno, Math.min(factorMetalico, 0.9f));
            } catch (Exception e) {
//                System.out.println("error reflexion " + e.getMessage());
            } finally {
                tm.release();
            }
        }
    }
    
}