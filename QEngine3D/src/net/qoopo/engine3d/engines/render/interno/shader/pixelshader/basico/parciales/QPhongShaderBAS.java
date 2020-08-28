/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Shader con sombrado de phong. sin Textura
 *
 * @author alberto
 */
public class QPhongShaderBAS extends QShader {

    public QPhongShaderBAS(QMotorRender render) {
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

        //No procesa textura , usa el color del material
        r = ((QMaterialBas) currentPixel.material).getColorDifusa().r;
        g = ((QMaterialBas) currentPixel.material).getColorDifusa().g;
        b = ((QMaterialBas) currentPixel.material).getColorDifusa().b;

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
        illumination.dR = (render.getEscena().getLuzAmbiente() + ((QMaterialBas) currentPixel.material).getFactorEmision());
        illumination.dG = (render.getEscena().getLuzAmbiente() + ((QMaterialBas) currentPixel.material).getFactorEmision());
        illumination.dB = (render.getEscena().getLuzAmbiente() + ((QMaterialBas) currentPixel.material).getFactorEmision());

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
