/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoEnlace;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoPeriferico;

/**
 *
 * @author alberto
 */
public class QPerColor extends QNodoPeriferico {

    private QColor color;

    public QPerColor(QColor color) {
        this.color = color;
    }

    public QColor getColor() {
        return color;
    }

    public void setColor(QColor color) {
        this.color = color;
    }

    @Override
    public void procesarEnlaces(QMotorRender render, QPixel pixel) {
        super.procesarEnlaces(render, pixel);
        if (enlaces != null && !enlaces.isEmpty()) {
            for (QNodoEnlace enlace : enlaces) {
                //si yo soy un periferico de entrada, debería estar en la salida del enlace
                if (enlace.getSalida().equals(this)) {
                    //del nodo de entrada tomo su salida
                    if (enlace.getEntrada() instanceof QPerColor) {
                        color = ((QPerColor) enlace.getEntrada()).getColor();
                        return;
                    }
                }
            }
        }
    }

}
