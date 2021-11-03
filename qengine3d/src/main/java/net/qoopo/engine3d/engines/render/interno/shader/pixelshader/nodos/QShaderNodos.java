/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Calcula el color e iluminación de cada pixel
 *
 * @author alberto
 */
public class QShaderNodos extends QShader {

    public QShaderNodos(QMotorRender render) {
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
        if (((QMaterialNodo) pixel.material).getNodo() != null) {
            QMaterialNodo material = ((QMaterialNodo) pixel.material);
            material.getNodo().procesar(render, pixel);
            color = material.getNodo().getSaColor().getColor();
//            for (QNodoPeriferico salida : material.getNodo().getSalidas()) {
//                if (salida instanceof QPerColor) {
//                    color = ((QPerColor) salida).getColor();
//                }
//            }
        }
        return color;
    }

}
