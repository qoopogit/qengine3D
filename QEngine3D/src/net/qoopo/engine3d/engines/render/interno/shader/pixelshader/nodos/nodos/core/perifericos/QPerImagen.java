/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoPeriferico;

/**
 *
 * @author alberto
 */
public class QPerImagen extends QNodoPeriferico {

    private QProcesadorTextura textura;

    public QPerImagen(QProcesadorTextura textura) {
        this.textura = textura;
        
    }

    public QProcesadorTextura getTextura() {
        return textura;
    }

    public void setTextura(QProcesadorTextura textura) {
        this.textura = textura;
    }

    @Override
    public void procesarEnlaces(QMotorRender render, QPixel pixel) {
        super.procesarEnlaces(render, pixel);

    }

}
