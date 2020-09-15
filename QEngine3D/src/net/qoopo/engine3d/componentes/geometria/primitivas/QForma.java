/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas;

import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.material.QMaterial;

/**
 *
 * @author alberto
 */
public abstract class QForma extends QGeometria {

    protected QMaterial material;

    protected void eliminarDatos() {
        this.destroy();
        this.vertices = new QVertice[0];
        this.primitivas = new QPrimitiva[0];
    }

    public abstract void construir();

    public QMaterial getMaterial() {
        return material;
    }

    public void setMaterial(QMaterial material) {
        this.material = material;
    }

}
