/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos;

import java.util.List;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoPeriferico;

/**
 *
 * @author alberto
 */
public abstract class QNodoPBR {

    protected List<QNodoPeriferico> entradas;
    protected List<QNodoPeriferico> salidas;

    public abstract void procesar(QMotorRender render,QPixel pixel);

    public List<QNodoPeriferico> getEntradas() {
        return entradas;
    }

    public void setEntradas(List<QNodoPeriferico> entradas) {
        this.entradas = entradas;
    }

    public List<QNodoPeriferico> getSalidas() {
        return salidas;
    }

    public void setSalidas(List<QNodoPeriferico> salidas) {
        this.salidas = salidas;
    }

}
