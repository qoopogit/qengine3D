package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Este shader toma en cuenta las luces del entorno, textura y sombreado phong
 *
 * @author alberto
 */
public class QIluminadoShaderBAS extends QShader {

    private QColor pixelColor;
    private QColor pixelEspecular = QColor.WHITE.clone();

    public QIluminadoShaderBAS(QMotorRender render) {
        super(render);
    }

    @Override
    public QColor colorearPixel(QPixel currentPixel, int x, int y) {
        if (currentPixel == null) {
            return null;
        }
        if (!currentPixel.isDibujar()) {
            return null;
        }

        boolean pixelTransparente = false;
        boolean pixelTransparente2 = false;
//        float a = 1;

        if (((QMaterialBas) currentPixel.material).getMapaDifusa() == null || !render.opciones.material) {
            // si no hay textura usa el color del material
            r = ((QMaterialBas) currentPixel.material).getColorDifusa().r;
            g = ((QMaterialBas) currentPixel.material).getColorDifusa().g;
            b = ((QMaterialBas) currentPixel.material).getColorDifusa().b;
        } else {
            //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas
            if (!((QMaterialBas) currentPixel.material).isDifusaProyectada()) {
                pixelColor = ((QMaterialBas) currentPixel.material).getMapaDifusa().get_QARGB(currentPixel.u, currentPixel.v);
            } else {
                pixelColor = ((QMaterialBas) currentPixel.material).getMapaDifusa().get_QARGB((float) x / (float) render.getFrameBuffer().getAncho(), -(float) y / (float) render.getFrameBuffer().getAlto());
            }

            switch (((QMaterialBas) currentPixel.material).getMapaDifusa().getModo()) {
                case QProcesadorTextura.MODO_REMPLAZAR:
                    r = pixelColor.r;
                    g = pixelColor.g;
                    b = pixelColor.b;
                    break;
                case QProcesadorTextura.MODO_COMBINAR:
                    r = (pixelColor.r + ((QMaterialBas) currentPixel.material).getColorDifusa().r) / 2;
                    g = (pixelColor.g + ((QMaterialBas) currentPixel.material).getColorDifusa().g) / 2;
                    b = (pixelColor.b + ((QMaterialBas) currentPixel.material).getColorDifusa().b) / 2;
                default:
                    break;
            }
            pixelTransparente2 = ((QMaterialBas) currentPixel.material).isTransparencia() && ((QMaterialBas) currentPixel.material).getColorTransparente() != null && pixelColor.toRGB() == ((QMaterialBas) currentPixel.material).getColorTransparente().toRGB();//sin alfa

            //solo activa la transparencia si tiene el canal alfa y el color es negro (el negro es el color transparente)
            pixelTransparente = pixelColor.a < 1 || pixelTransparente2;//transparencia imagenes png

            if (pixelTransparente) {
                return null;
            }
        }

        calcularIluminacion(iluminacion, currentPixel);

        // Set diffuse illumination
        r = r * iluminacion.dR;
        g = g * iluminacion.dG;
        b = b * iluminacion.dB;

        if (((QMaterialBas) currentPixel.material).getTransAlfa() < 1) {
            //si el material tiene transparencia
            r = (1 - ((QMaterialBas) currentPixel.material).getTransAlfa()) * QMath.byteToFloat(render.getFrameBuffer().getRenderedBytes((y * render.getFrameBuffer().getAncho() + x) * 3 + 2)) / 255 + ((QMaterialBas) currentPixel.material).getTransAlfa() * r;
            g = (1 - ((QMaterialBas) currentPixel.material).getTransAlfa()) * QMath.byteToFloat(render.getFrameBuffer().getRenderedBytes((y * render.getFrameBuffer().getAncho() + x) * 3 + 1)) / 255 + ((QMaterialBas) currentPixel.material).getTransAlfa() * g;
            b = (1 - ((QMaterialBas) currentPixel.material).getTransAlfa()) * QMath.byteToFloat(render.getFrameBuffer().getRenderedBytes((y * render.getFrameBuffer().getAncho() + x) * 3)) / 255 + ((QMaterialBas) currentPixel.material).getTransAlfa() * b;
        }

        // Agrega Luz especular.
        r += iluminacion.sR;
        g += iluminacion.sG;
        b += iluminacion.sB;

        // Clamp rgb to 1.
        if (r > 1) {
            r = 1;
        }
        if (g > 1) {
            g = 1;
        }
        if (b > 1) {
            b = 1;
        }

        return new QColor(r, g, b);
    }

//    @Override
    protected void calcularIluminacion(QIluminacion illumination, QPixel currentPixel) {
        currentPixel.normal.normalize();
        iluminacionDifusa = 0;
        iluminacionEspecular = 0;

        //usa el mapa de iluminacion con el ambiente
        if (((QMaterialBas) currentPixel.material).getMapaEmisivo() != null && render.opciones.material) {
            QColor colorEmisivo = ((QMaterialBas) currentPixel.material).getMapaEmisivo().get_QARGB(currentPixel.u, currentPixel.v);
            illumination.dR = colorEmisivo.r + render.getEscena().getLuzAmbiente();
            illumination.dG = colorEmisivo.g + render.getEscena().getLuzAmbiente();
            illumination.dB = colorEmisivo.b + render.getEscena().getLuzAmbiente();
        } else {
            // usa el color emisivo + el ambiente
            illumination.dR = (((QMaterialBas) currentPixel.material).getFactorEmision() + render.getEscena().getLuzAmbiente());
            illumination.dG = (((QMaterialBas) currentPixel.material).getFactorEmision() + render.getEscena().getLuzAmbiente());
            illumination.dB = (((QMaterialBas) currentPixel.material).getFactorEmision() + render.getEscena().getLuzAmbiente());
        }

        //tomo el valor del mapa especular, si existe
        if (((QMaterialBas) currentPixel.material).getMapaEspecular() != null) {
            pixelEspecular = ((QMaterialBas) currentPixel.material).getMapaEspecular().get_QARGB(currentPixel.u, currentPixel.v);
        } else {
            pixelEspecular = QColor.WHITE;//equivale a multiplicar por 1
        }
        TempVars tv = TempVars.get();
        try {
            illumination.sR = 0;
            illumination.sG = 0;
            illumination.sB = 0;
            tmpPixelPos.set(currentPixel.ubicacion.getVector3());
            tmpPixelPos.normalize();

            // solo si hay luces y si la vista es desde textura hacia arriba
            if (!render.getLuces().isEmpty()
                    && render.opciones.material) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {
                            vectorLuz.setXYZ(currentPixel.ubicacion.x - luz.entidad.getTransformacion().getTraslacion().x, currentPixel.ubicacion.y - luz.entidad.getTransformacion().getTraslacion().y, currentPixel.ubicacion.z - luz.entidad.getTransformacion().getTraslacion().z);

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

                            distanciaLuz = vectorLuz.x * vectorLuz.x + vectorLuz.y * vectorLuz.y + vectorLuz.z * vectorLuz.z;

                            iluminacionDifusa = -vectorLuz.dotProduct(currentPixel.normal) * luz.energia / distanciaLuz;
                            //luz difusa
                            if (iluminacionDifusa > 0) {
                                illumination.dR += (float) luz.color.r * iluminacionDifusa;
                                illumination.dG += (float) luz.color.g * iluminacionDifusa;
                                illumination.dB += (float) luz.color.b * iluminacionDifusa;
                            }
                            //luz especular
                            // si la luz esta en de frente ejeY no detras de la cara ejeY que no estemos en sombre de esa luz
                            if (vectorLuz.dotProduct(currentPixel.normal) < 0) {
                                vectorLuz.normalize();
                                tempVector.copyXYZ(currentPixel.normal);
                                tempVector.multiply(-2 * vectorLuz.dotProduct(currentPixel.normal));
                                vectorLuz.add(tempVector);

                                iluminacionEspecular = -vectorLuz.dotProduct(tmpPixelPos);
                                if (render.opciones.verCarasTraseras) {
                                    iluminacionEspecular = Math.abs(iluminacionEspecular);
                                }
                                //solo para valores positivos, si tiene 0 no calculo la luz especular
                                if (((QMaterialBas) currentPixel.material).getSpecularExponent() > 0) {
                                    if (iluminacionEspecular > 0) {
                                        iluminacionEspecular = (float) Math.pow(iluminacionEspecular, ((QMaterialBas) currentPixel.material).getSpecularExponent());
                                        iluminacionEspecular = iluminacionEspecular * luz.energia / distanciaLuz;
                                        illumination.sR += pixelEspecular.r * ((QMaterialBas) currentPixel.material).getColorEspecular().r * iluminacionEspecular;
                                        illumination.sG += pixelEspecular.g * ((QMaterialBas) currentPixel.material).getColorEspecular().g * iluminacionEspecular;
                                        illumination.sB += pixelEspecular.b * ((QMaterialBas) currentPixel.material).getColorEspecular().b * iluminacionEspecular;
                                    }
                                }
                            }
                        } else if (luz instanceof QLuzDireccional) {
                            vectorLuz.copyXYZ(((QLuzDireccional) luz).getDirection());
                            vectorLuz.normalize();
                            iluminacionDifusa = -vectorLuz.dotProduct(currentPixel.normal);
                            if (iluminacionDifusa > 0) {
                                illumination.dR += luz.energia * (float) luz.color.r * iluminacionDifusa;
                                illumination.dG += luz.energia * (float) luz.color.g * iluminacionDifusa;
                                illumination.dB += luz.energia * (float) luz.color.b * iluminacionDifusa;
                            }
                            if (vectorLuz.dotProduct(currentPixel.normal) < 0) {
                                tempVector.copyXYZ(currentPixel.normal);
                                tempVector.multiply(-2 * vectorLuz.dotProduct(currentPixel.normal));
                                vectorLuz.add(tempVector);
                                iluminacionEspecular = -vectorLuz.dotProduct(tmpPixelPos);
                                if (iluminacionEspecular > 0) {
                                    iluminacionEspecular = (float) Math.pow(iluminacionEspecular, ((QMaterialBas) currentPixel.material).getSpecularExponent());
                                    illumination.sR += pixelEspecular.r * luz.energia * ((QMaterialBas) currentPixel.material).getColorEspecular().r * iluminacionEspecular;
                                    illumination.sG += pixelEspecular.g * luz.energia * ((QMaterialBas) currentPixel.material).getColorEspecular().g * iluminacionEspecular;
                                    illumination.sB += pixelEspecular.b * luz.energia * ((QMaterialBas) currentPixel.material).getColorEspecular().b * iluminacionEspecular;
                                }
                            }
                        }
                    }
                }
            } else {
                //iluminaion default cuando no hay luces se asume una luz central
                illumination.dR += -tmpPixelPos.dotProduct(currentPixel.normal);
                illumination.dG += -tmpPixelPos.dotProduct(currentPixel.normal);
                illumination.dB += -tmpPixelPos.dotProduct(currentPixel.normal);
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

}
