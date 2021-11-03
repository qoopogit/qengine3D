/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.material;

import java.io.Serializable;

/**
 *
 * @author alberto
 */
public class QMaterial implements Serializable {

    protected String nombre = "Material_";

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
