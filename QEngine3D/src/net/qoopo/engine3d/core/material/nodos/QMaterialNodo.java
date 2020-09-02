/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.material.nodos;

import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QNodoMaterial;

/**
 *
 * @author alberto
 */
public class QMaterialNodo extends QMaterial {

    private QNodoMaterial nodo;

    public QMaterialNodo() {
    }

    public QMaterialNodo(String nombre) {
        this.nombre = nombre;
    }

    public QNodoMaterial getNodo() {
        return nodo;
    }

    public void setNodo(QNodoMaterial nodo) {
        this.nodo = nodo;
    }

}
