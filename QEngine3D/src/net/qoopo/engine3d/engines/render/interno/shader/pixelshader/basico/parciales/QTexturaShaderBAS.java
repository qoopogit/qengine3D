/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Este shader toma en cuenta la textura con sombreado phong. Sin iluminacion
 * del entorno
 *
 * @author alberto
 */
public class QTexturaShaderBAS extends QShader {

    private QColor pixelColor;

    public QTexturaShaderBAS(QMotorRender render) {
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

        //toma en cuenta la luz ambiente
        illumination.dR = render.getEscena().getLuzAmbiente();
        illumination.dG = render.getEscena().getLuzAmbiente();
        illumination.dB = render.getEscena().getLuzAmbiente();

        illumination.sR = 0;
        illumination.sG = 0;
        illumination.sB = 0;
        tmpPixelPos.set(currentPixel.ubicacion.getVector3());
        tmpPixelPos.normalize();

        //Iluminacion default no toma en cuenta las luces del entorno
        illumination.dR += -tmpPixelPos.dotProduct(currentPixel.normal);
        illumination.dG += -tmpPixelPos.dotProduct(currentPixel.normal);
        illumination.dB += -tmpPixelPos.dotProduct(currentPixel.normal);

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
    }

}
