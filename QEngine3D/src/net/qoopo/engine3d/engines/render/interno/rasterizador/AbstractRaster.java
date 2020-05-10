/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.rasterizador;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.transformacion.QVerticesBuffer;
import net.qoopo.engine3d.core.math.QVector4;

/**
 * Realiza la rasterización de los polígonos
 *
 * @author alberto
 */
public abstract class AbstractRaster {

//    public abstract AbstractRaster(QMotorRender render);
    /**
     *
     * @param primitiva
     * @param p1
     * @param p2
     */
    public abstract void dibujarLinea(QPrimitiva primitiva, QVector4 p1, QVector4 p2);

    /**
     * Realiza la rasterización de un polígono
     *
     * @param bufferVertices
     * @param primitiva
     * @param wire
     * @param siempreTop
     */
    public abstract void procesarPoligono(QVerticesBuffer bufferVertices, QPrimitiva primitiva, boolean wire, boolean siempreTop);

}
