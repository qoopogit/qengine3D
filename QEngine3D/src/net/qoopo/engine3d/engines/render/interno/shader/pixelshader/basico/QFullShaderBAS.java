/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTexturaUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Calcula el color e iluminación de cada pixel
 *
 * @author alberto
 */
public class QFullShaderBAS extends QShader {

    private QColor colorDifuso;
    private QColor colorEspecular = QColor.WHITE.clone();
    private QColor colorReflejo;
    private QColor colorRefraccion;

    private QColor colorEntorno = QColor.WHITE.clone();
//    private QColor colorDesplazamiento;
    private float transparencia;
    private float factorEntorno = 0;
    private float factorFresnel = 0;

    public QFullShaderBAS(QMotorRender render) {
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

        //modifica los valores xyz del pixel de acuerdo al mapa de desplazamiento
//        if (((QMaterialBas) currentPixel.material).getMapaDesplazamiento() != null) {
//            colorDesplazamiento = ((QMaterialBas) currentPixel.material).getMapaDesplazamiento().get_QARGB(currentPixel.u, currentPixel.v);
//            // el mapa de desplazamiento es uno a escalas de grises, sin emabargo aun hare el promedio de los 3 canales 
//            float fac = (colorDesplazamiento.r + colorDesplazamiento.g + colorDesplazamiento.b) / 3.0f * ((QMaterialBas) currentPixel.material).getFactorNormal();
//            QVector3 vec = new QVector3(currentPixel.x, currentPixel.y, currentPixel.z);
//            vec.add(currentPixel.normal.clone().multiply(fac));
//            currentPixel.x = vec.x;
//            currentPixel.y = vec.y;
//            currentPixel.z = vec.z;
//        }
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

        /**
         * ********************************************************************************
         * COLOR DIFUSO
         * ********************************************************************************
         */
        if (((QMaterialBas) pixel.material).getMapaDifusa() == null || !render.opciones.material) {
            // si no hay textura usa el color del material
            color.set(((QMaterialBas) pixel.material).getColorDifusa());
        } else {
            if (!((QMaterialBas) pixel.material).isDifusaProyectada()) {
                //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas 
                colorDifuso = ((QMaterialBas) pixel.material).getMapaDifusa().get_QARGB(pixel.u, pixel.v);
            } else {
                //si es proyectada se asume que la textura es el resultado de un renderizador
                // por lo tanto coresponde a una pantalla y debemos tomar las mismas coordenadas
                // que llegan en X y Y, sin embargo las coordenadas UV estan normalizadas de 0 a 1
                // por lo tanto convertimos las coordeandas XyY a coordenadas UV 
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

            // si se configuro un color transparente para la textura
            pixelTransparente2 = ((QMaterialBas) pixel.material).isTransparencia()
                    && ((QMaterialBas) pixel.material).getColorTransparente() != null
                    && colorDifuso.toRGB() == ((QMaterialBas) pixel.material).getColorTransparente().toRGB();//sin alfa

            //solo activa la transparencia si tiene el canal alfa 
            pixelTransparente = colorDifuso.a < 1 || pixelTransparente2;//transparencia imagenes png
            if (pixelTransparente) {
                return null;
            }
        }

        //tomo el valor del mapa especular, si existe
        // es usado en el calculo de la iluminacion y en el reflejo/refraccion del entorno
        if (((QMaterialBas) pixel.material).getMapaEspecular() != null) {
            colorEspecular = ((QMaterialBas) pixel.material).getMapaEspecular().get_QARGB(pixel.u, pixel.v);
            factorEntorno = colorEspecular.r;
        } else {
            colorEspecular = QColor.WHITE;//equivale a multiplicar por 1
            factorEntorno = ((QMaterialBas) pixel.material).getFactorEntorno();
        }

        calcularEntorno(pixel);

//        //calculo la niebla antes del calculo de iluminacion
//   try {
//            if (QEscena.INSTANCIA != null) {
//                QColor resul = calcularNeblina(new QColor(r, g, b), currentPixel, QEscena.INSTANCIA.neblina);
//                if (resul != null) {
//                    r = resul.r;
//                    g = resul.g;
//                    b = resul.b;
//                }
//            }
//        } catch (Exception e) {
//        }
        calcularIluminacion(iluminacion, pixel);

        // Iluminacion difusa
        color.scale(iluminacion.dR, iluminacion.dG, iluminacion.dB);
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
        color.add(iluminacion.sR, iluminacion.sG, iluminacion.sB);

        //lugar de la reflexion despues de la iluminacion
        //calculo la niebla al final del calculo de iluminacion
        try {
            if (QEscena.INSTANCIA != null) {
                QColor resul = calcularNeblina(color, pixel, QEscena.INSTANCIA.neblina);
                if (resul != null) {
                    color.set(resul);
                }
            }
        } catch (Exception e) {
        }
        return color;
//        render.getFrameBuffer().setQColor(x, y, color);
    }

    /**
     * Calcula la Reflexión y Refracción utilizando un mapa de entorno (puede
     * ser generado con un mapa de cubo)
     *
     * @param currentPixel
     */
    private void calcularEntorno(QPixel currentPixel) {
        // Reflexion y refraccion del entorno (en caso de materiales con refraccion (transparentes)
        if (((QMaterialBas) currentPixel.material).getMapaEntorno() != null //tiene un mapa de entorno
                && render.opciones.material //esta activada la opción de material
                && (((QMaterialBas) currentPixel.material).isReflexion() || ((QMaterialBas) currentPixel.material).isRefraccion()) //tien habilitada la reflexión y/o la refración
                ) {
            TempVars tm = TempVars.get();
            try {
                //*********************************************************************************************
                //******                    VECTOR VISION 
                //*********************************************************************************************
                // el pixel ya esta calculado en el espacio de la camara (primero se transformo los vertices, luego se interpolo),
                //la cámara esta en la posición (0,0,0) , por lo tanto es el vector visión
                tm.vector3f1.set(currentPixel.ubicacion.getVector3());
//                tm.vector3f1.subtract(render.getCamara().getMatrizTransformacion(QGlobal.tiempo).toTranslationVector());
//                tm.vector3f1.multiply(-1.0f);
                //quito la transformacion
//                tm.vector3f1.set(QTransformar.transformarVectorInversa(tm.vector3f1, currentPixel.entidad, render.getCamara()));
//                tm.vector3f1.normalize();
                //calculo vector vision método 2, segun video https://www.youtube.com/watch?v=xutvBtrG23A minuto 9:14
//                //quito la transformación Vista*Modelo (aunq mantiene la transformación de proyección)
//                tm.vector3f1.set(QTransformar.transformarVectorInversa(tm.vector3f1, currentPixel.entidad, render.getCamara()));
//                tm.vector3f1.set(QTransformar.transformarVectorModelo(tm.vector3f1, currentPixel.entidad));//aplica transformación de la entidad solamente
//                //luego resto de la posicion de la camara
//                tm.vector3f1.add(render.getCamara().getMatrizTransformacion(QGlobal.tiempo).toTranslationVector().multiply(-1.0f));
                tm.vector3f1.normalize();
                //normal
                tm.vector3f2.set(currentPixel.normal);
                //quito la transformacion
//                tm.vector3f2.set(QTransformar.transformarVectorNormalInversa(tm.vector3f2, currentPixel.entidad, render.getCamara()));
//                tm.vector3f2.set(QTransformar.transformarVectorNormal(tm.vector3f2, currentPixel.entidad.getMatrizTransformacion(QGlobal.tiempo)));//aplica transformación de la entidad solamente
                tm.vector3f2.normalize();

                //QMapaCubo mapaCubo = QUtilComponentes.getMapaCubo(currentPixel.entidad);
                //************************************************************
                //******                    REFLEXION
                //************************************************************
                if (((QMaterialBas) currentPixel.material).isReflexion()) {
                    tm.vector3f3.set(QMath.reflejarVector(tm.vector3f1, tm.vector3f2));
                    //como estamos en el espacio de la cámara quitamos esa transformación del vector reflejo
                    //tm.vector3f3.set(QTransformar.transformarVectorNormal(tm.vector3f3, currentPixel.entidad, render.getCamara()));
                    tm.vector3f3.set(QTransformar.transformarVectorNormalInversa(tm.vector3f3, currentPixel.entidad, render.getCamara()));// funciona siempre y cuando el objeto no este rotado
                    //convertirmos del espacio de la cámara al espacio del mundo 
//                    tm.vector3f3.set(QTransformar.transformarVectorNormal(tm.vector3f3, render.getCamara().getMatrizTransformacion(QGlobal.tiempo).invert())); //funciona cuando se rota el objeto pero hay un error con lados invertidos
                    colorReflejo = QTexturaUtil.getColorMapaEntorno(tm.vector3f3, ((QMaterialBas) currentPixel.material).getMapaEntorno(), ((QMaterialBas) currentPixel.material).getTipoMapaEntorno());
                } else {
                    colorReflejo = null;
                }
                //***********************************************************
                //******                    REFRACCION
                //***********************************************************
                if (((QMaterialBas) currentPixel.material).isRefraccion() && ((QMaterialBas) currentPixel.material).getIndiceRefraccion() > 0) {
                    if (((QMaterialBas) currentPixel.material).getIndiceRefraccion() > 0) {
                        tm.vector3f4.set(QMath.refractarVectorGL(tm.vector3f1, tm.vector3f2, 1.0f / ((QMaterialBas) currentPixel.material).getIndiceRefraccion())); //indice del aire sobre indice del material
                    } else {
                        tm.vector3f4.set(QMath.refractarVectorGL(tm.vector3f1, tm.vector3f2, 0.0f));
                    }
                    //tm.vector3f4.set(QMath.refractarVector(tm.vector3f1, tm.vector3f2, 1.0f / ((QMaterialBas) currentPixel.material).getIndiceRefraccion())); //indice del aire sobre indice del material
                    //tm.vector3f4.set(QMath.refractarVector3(tm.vector3f1, tm.vector3f2, 1.0f / ((QMaterialBas) currentPixel.material).getIndiceRefraccion())); //indice del aire sobre indice del material
                    //como estamos en el espacio de la cámara quitamos esa transformación del vector refractado
                    //tm.vector3f4.set(QTransformar.transformarVectorNormal(tm.vector3f4, currentPixel.entidad, render.getCamara()));
                    tm.vector3f4.set(QTransformar.transformarVectorNormalInversa(tm.vector3f4, currentPixel.entidad, render.getCamara()));// funciona siempre y cuando el objeto no este rotado
                    //convertirmos del espacio de la cámara al espacio del mundo
//                    tm.vector3f4.set(QTransformar.transformarVectorNormal(tm.vector3f4, render.getCamara().getMatrizTransformacion(QGlobal.tiempo).invert()));//funciona cuando se rota el objeto pero hay un error con lados invertidos
                    colorRefraccion = QTexturaUtil.getColorMapaEntorno(tm.vector3f4, ((QMaterialBas) currentPixel.material).getMapaEntorno(), ((QMaterialBas) currentPixel.material).getTipoMapaEntorno());
                } else {
                    colorRefraccion = null;
                }
                //APLICACION DEL COLOR DEL ENTORNO              

                //mezclo el color de reflexion con el de refraccion
                if (colorReflejo != null && colorRefraccion != null) {
                    if (QGlobal.REFLEJOS_CALCULAR_FRESNEL) {
//                        factorFresnel = QMath.factorFresnel(tm.vector3f1, tm.vector3f2, 0);
                        factorFresnel = QMath.factorFresnel(tm.vector3f2, tm.vector3f1, 0);
                    } else {
                        factorFresnel = 0.5f;//mezcla equilibrada entre reflejo y refraccion
                    }
//                    colorEntorno = QMath.mix(colorRefraccion, colorReflejo, factorFresnel);
                    colorEntorno.r = QMath.mix(colorRefraccion.r, colorReflejo.r, factorFresnel);
                    colorEntorno.g = QMath.mix(colorRefraccion.g, colorReflejo.g, factorFresnel);
                    colorEntorno.b = QMath.mix(colorRefraccion.b, colorReflejo.b, factorFresnel);
                } else if (colorReflejo != null) {
                    colorEntorno = colorReflejo.clone();
                } else if (colorRefraccion != null) {
                    colorEntorno = colorRefraccion.clone();
                }
                //agrega el color entorno
//                r += ((QMaterialBas) currentPixel.material).getFactorReflexion() * colorEspecular.r* colorEntorno.r;
//                g += ((QMaterialBas) currentPixel.material).getFactorReflexion() * colorEspecular.g*colorEntorno.g;
//                b += ((QMaterialBas) currentPixel.material).getFactorReflexion() * colorEspecular.b*colorEntorno.b;
                //mezcla el color del entorno
                color.r = QMath.mix(color.r, colorEntorno.r, factorEntorno);
                color.g = QMath.mix(color.g, colorEntorno.g, factorEntorno);
                color.b = QMath.mix(color.b, colorEntorno.b, factorEntorno);
            } catch (Exception e) {
//                System.out.println("error reflexion " + e.getMessage());
            } finally {
                tm.release();
            }
        }
    }

    /**
     * 07/02/2018. Se implementa la iluminacion de Bling-Phong que mejora los
     * tiempos y es el default de OpenGL y Directx
     * https://en.wikipedia.org/wiki/Blinn%E2%80%93Phong_shading_model que
     * mejora
     *
     * @param illumination
     * @param pixel
     */
//    @Override
    protected void calcularIluminacion(QIluminacion illumination, QPixel pixel) {
        pixel.normal.normalize();
        iluminacionDifusa = 0;
        iluminacionEspecular = 0;

        //usa el mapa de iluminacion con el ambiente
        if (((QMaterialBas) pixel.material).getMapaEmisivo() != null && render.opciones.material) {
            QColor colorEmisivo = ((QMaterialBas) pixel.material).getMapaEmisivo().get_QARGB(pixel.u, pixel.v);
            illumination.dR = colorEmisivo.r + render.getEscena().getLuzAmbiente();
            illumination.dG = colorEmisivo.g + render.getEscena().getLuzAmbiente();
            illumination.dB = colorEmisivo.b + render.getEscena().getLuzAmbiente();
        } else {

            // usa el color emisivo + el ambiente
//            illumination.dR = (((QMaterialBas) pixel.material).getFactorEmision() + render.getUniverso().luzAmbiente);
//            illumination.dG = (((QMaterialBas) pixel.material).getFactorEmision() + render.getUniverso().luzAmbiente);
//            illumination.dB = (((QMaterialBas) pixel.material).getFactorEmision() + render.getUniverso().luzAmbiente);
//            if (((QMaterialBas) pixel.material).getFactorEmision() > 0) {
//                return;//no hago mas calculos 
//            }
            // si tiene factor de emision toma ese valor solamente
            if (((QMaterialBas) pixel.material).getFactorEmision() > 0) {
                illumination.dR = ((QMaterialBas) pixel.material).getFactorEmision();
                illumination.dG = ((QMaterialBas) pixel.material).getFactorEmision();
                illumination.dB = ((QMaterialBas) pixel.material).getFactorEmision();
                return;//no hago mas calculos 
            } else {
                illumination.dR = render.getEscena().getLuzAmbiente();
                illumination.dG = render.getEscena().getLuzAmbiente();
                illumination.dB = render.getEscena().getLuzAmbiente();
            }
        }

        TempVars tv = TempVars.get();
        try {
            illumination.sR = 0;
            illumination.sG = 0;
            illumination.sB = 0;
            tmpPixelPos.set(pixel.ubicacion.getVector3());
            tmpPixelPos.normalize();

            float factorSombra = 1;//1= no sombra
            float factorSombraSAO = 1;//factor de oclusion ambiental con el mapa SAO

            if (((QMaterialBas) pixel.material).getMapaSAO() != null && render.opciones.material) {
                factorSombraSAO = ((QMaterialBas) pixel.material).getMapaSAO().get_QARGB(pixel.u, pixel.v).r;
            }

            // solo si hay luces y si las opciones de la vista tiene activado el material
            if (!render.getLuces().isEmpty() && render.opciones.material) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
                        factorSombra = 1;
                        /*
                         *****************************************************************
                         * LUZ PUNTUAL Y LUZ SPOT
                         *****************************************************************
                         */
                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {
                            vectorLuz.setXYZ(pixel.ubicacion.x - luz.entidad.getTransformacion().getTraslacion().x, pixel.ubicacion.y - luz.entidad.getTransformacion().getTraslacion().y, pixel.ubicacion.z - luz.entidad.getTransformacion().getTraslacion().z);
//                            QVector3 pos = luz.entidad.getMatrizTransformacionTMP().toTranslationVector();
//                            vectorLuz.setXYZ(pixel.x - pos.x, pixel.y - pos.y, pixel.z - pos.z);
//                            vectorLuz.multiply(-1f);
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

                            QProcesadorSombra proc = render.getProcesadorSombras().get(luz.entidad.getNombre());
                            if (proc != null && render.opciones.sombras && ((QMaterialBas) pixel.material).isSombrasRecibir()) {
                                tv.vector3f1.set(pixel.ubicacion.getVector3());
                                factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(tv.vector3f1, pixel.entidad, render.getCamara()), pixel.entidad);
                            }

//                        if (!verificarSombra(vectorLuz, light.entidad.transformacion.getTraslacion().x, light.entidad.transformacion.getTraslacion().y, light.entidad.transformacion.getTraslacion().z, x, y, z)) {
                            distanciaLuz = vectorLuz.x * vectorLuz.x + vectorLuz.y * vectorLuz.y + vectorLuz.z * vectorLuz.z;
//                            vectorLuz.normalize();
                            float vlDotNormal = vectorLuz.dotProduct(pixel.normal);

                            iluminacionDifusa = -vlDotNormal * luz.energia / distanciaLuz;

                            //luz difusa
                            if (iluminacionDifusa > 0) {
                                illumination.dR += (float) luz.color.r * iluminacionDifusa * factorSombra * factorSombraSAO;
                                illumination.dG += (float) luz.color.g * iluminacionDifusa * factorSombra * factorSombraSAO;
                                illumination.dB += (float) luz.color.b * iluminacionDifusa * factorSombra * factorSombraSAO;
                            }
                            //luz especular
                            // si la luz esta en de frente ejeY no detras de la cara ejeY que no estemos en sombre de esa luz
                            if (vlDotNormal < 0) {
                                vectorLuz.normalize();
                                //reflejo
                                vectorLuz.set(QMath.reflejarVector(vectorLuz, pixel.normal));
                                iluminacionEspecular = -vectorLuz.dotProduct(tmpPixelPos);

                                if (render.opciones.verCarasTraseras) {
                                    iluminacionEspecular = Math.abs(iluminacionEspecular);
                                }
                                //solo para valores positivos, si tiene 0 no calculo la luz especular
                                if (((QMaterialBas) pixel.material).getSpecularExponent() > 0) {
                                    if (iluminacionEspecular > 0) {
                                        iluminacionEspecular = (float) Math.pow(iluminacionEspecular, ((QMaterialBas) pixel.material).getSpecularExponent());
                                        iluminacionEspecular = iluminacionEspecular * luz.energia / distanciaLuz;
                                        illumination.sR += colorEspecular.r * ((QMaterialBas) pixel.material).getColorEspecular().r * iluminacionEspecular * factorSombra * factorSombraSAO;
                                        illumination.sG += colorEspecular.g * ((QMaterialBas) pixel.material).getColorEspecular().g * iluminacionEspecular * factorSombra * factorSombraSAO;
                                        illumination.sB += colorEspecular.b * ((QMaterialBas) pixel.material).getColorEspecular().b * iluminacionEspecular * factorSombra * factorSombraSAO;
                                    }
                                }
                            }

                            /*
                             *****************************************************************
                             *                       LUZ DIRECCIONAL
                             *****************************************************************
                             */
                        } else if (luz instanceof QLuzDireccional) {
                            vectorLuz.copyXYZ(((QLuzDireccional) luz).getDirection());
                            QProcesadorSombra proc = render.getProcesadorSombras().get(luz.entidad.getNombre());
                            if (proc != null && render.opciones.sombras && ((QMaterialBas) pixel.material).isSombrasRecibir()) {
                                tv.vector3f1.set(pixel.ubicacion.getVector3());
                                factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(tv.vector3f1, pixel.entidad, render.getCamara()), pixel.entidad);
                            }

                            vectorLuz.normalize();
                            float vlDotNormal = vectorLuz.dotProduct(pixel.normal);
                            iluminacionDifusa = -vlDotNormal;
                            if (iluminacionDifusa > 0) {
                                illumination.dR += luz.energia * (float) luz.color.r * iluminacionDifusa * factorSombra * factorSombraSAO;
                                illumination.dG += luz.energia * (float) luz.color.g * iluminacionDifusa * factorSombra * factorSombraSAO;
                                illumination.dB += luz.energia * (float) luz.color.b * iluminacionDifusa * factorSombra * factorSombraSAO;
                            }
                            if (vlDotNormal < 0) {
                                //puse el reflejo en su lugar
                                vectorLuz.set(QMath.reflejarVector(vectorLuz, pixel.normal));
                                iluminacionEspecular = -vectorLuz.dotProduct(tmpPixelPos);
                                if (iluminacionEspecular > 0) {
                                    iluminacionEspecular = (float) Math.pow(iluminacionEspecular, ((QMaterialBas) pixel.material).getSpecularExponent());
                                    illumination.sR += colorEspecular.r * luz.energia * ((QMaterialBas) pixel.material).getColorEspecular().r * iluminacionEspecular * factorSombra * factorSombraSAO;
                                    illumination.sG += colorEspecular.g * luz.energia * ((QMaterialBas) pixel.material).getColorEspecular().g * iluminacionEspecular * factorSombra * factorSombraSAO;
                                    illumination.sB += colorEspecular.b * luz.energia * ((QMaterialBas) pixel.material).getColorEspecular().b * iluminacionEspecular * factorSombra * factorSombraSAO;
                                }
                            }
                        }
                    }
                }
            } else {
                //iluminacion default cuando no hay luces se asume una luz central
                illumination.dR += -tmpPixelPos.dotProduct(pixel.normal);
                illumination.dG += -tmpPixelPos.dotProduct(pixel.normal);
                illumination.dB += -tmpPixelPos.dotProduct(pixel.normal);
            }

            if (illumination.dR < 0) {
                illumination.dR = 0;
            }
            if (illumination.dG < 0) {
                illumination.dG = 0;
            }
            if (illumination.dB < 0) {
                illumination.dB = 0;
            }

            if (illumination.sR < 0) {
                illumination.sR = 0;
            }
            if (illumination.sG < 0) {
                illumination.sG = 0;
            }
            if (illumination.sB < 0) {
                illumination.sB = 0;
            }
        } finally {
            tv.release();
        }
    }

//
//    /**
//     * Verifica si un punto pertenece al vector de la luz Este metodo de
//     * calcular sombrasRayos es realista pero muy muy lento
//     *
//     * @param vectorLuz
//     * @param x coordenada de la luz
//     * @param y coordenada de la luz
//     * @param z coordenada de la luz
//     * @param x1 coordenada del punto
//     * @param y1 coordenada del punto
//     * @param z1 coordenada del punto
//     * @return
//     */
//    private boolean verificarSombra(QVector3 vectorLuz, float x, float y, float z, float x1, float y1, float z1) {
//        //solo procesamos si es que esta habilitada la opcion de sombrasRayos
//
//        if (render.sombrasRayos && render.opciones.sombras) {
////            long tInicio = System.currentTimeMillis();
////            System.out.println("inicio calculo pixel " + tInicio);
//            //debemos recorrer todos los puntos (al menos lo svertices ejeY ver si no hay alguno que este cruzado por el vector
//            QPixel actual;
//            float componenx = 0;
//            float componeny = 0;
//            float componenz = 0;
//            for (int cy = 0; cy < render.camara.screenHeight; cy++) {
//                for (int cx = 0; cx < render.camara.screenWidth; cx++) {
////                    actual = pixel[cy][cx];
//                    actual = render.getFrameBuffer().getPixel(cy, cx);
//                    if (actual != null && actual.dibujar) {
//                        //si no es el mismo pixel porq el que quiero saber si esta con alguna sombra
//                        if (actual.x != x1 && actual.y != y1 && actual.z != z1) {
//                            componenx = (actual.x - x) / vectorLuz.x;
//                            componeny = (actual.y - y) / vectorLuz.y;
//                            componenz = (actual.z - z) / vectorLuz.z;
//                            if (componenx == componeny && componenx == componenz) {
//                                //hasta ahorita sabemos que pertenece a la misma recta falta determinar si esta en el modulo de esa recta
//                                System.out.println("si hay sombreados");
//                                return true;
//                            }
//                        }
//                    }
//                }
//            }
//
////            long tFin = System.currentTimeMillis();
////            System.out.println("fin calculo pixel " + (tInicio - tInicio) + "ms");
//        }
//        return false;
//    }
//     protected int getColorTexturaHDRI(QVector3 vector) {
//        int hdriWidth = hdri.getWidth();
//        int hdriHeight = hdri.getHeight();
//
//        int newX = (int) (hdriWidth * ((Math.PI - Math.atan2(vector.x, vector.z)) / (2 * Math.PI)));
//        int newY = (int) (hdriHeight * (Math.acos(vector.y / vector.length()) / Math.PI));
//
//        newX = rotateNumber(newX, hdriWidth);
//        newY = rotateNumber(newY, hdriHeight);
//        return hdri.getRGB(newX, newY);
//    }
}
