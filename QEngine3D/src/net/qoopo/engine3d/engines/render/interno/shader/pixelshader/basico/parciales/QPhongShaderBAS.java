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
    public QColor colorearPixel(QPixel pixel, int x, int y) {
        if (pixel == null) {
            return null;
        }
        if (!pixel.isDibujar()) {
            return null;
        }

        //No procesa textura , usa el color del material
        color.set(((QMaterialBas) pixel.material).getColorDifusa());

        calcularIluminacion(iluminacion, pixel);

        // Iluminacion difusa
        color.scale(iluminacion.dR, iluminacion.dG, iluminacion.dB);

             // Agrega Luz especular.
        color.add(iluminacion.sR, iluminacion.sG, iluminacion.sB);

      return color;
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
