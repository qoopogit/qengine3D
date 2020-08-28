/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Shader simple, sin calculo de iluminacion ni textura
 *
 * @author alberto
 */
public class QSimpleShaderBAS extends QShader {

    public QSimpleShaderBAS(QMotorRender render) {
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
        r = Math.min(((QMaterialBas) currentPixel.material).getColorDifusa().r, 1);
        g = Math.min(((QMaterialBas) currentPixel.material).getColorDifusa().g, 1);
        b = Math.min(((QMaterialBas) currentPixel.material).getColorDifusa().b, 1);

        return new QColor(r, g, b);
    }

}
