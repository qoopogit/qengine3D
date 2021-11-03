/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.inteligencia.acciones;

import java.io.Serializable;

/**
 *
 * @author alberto
 */
public abstract class QAccion implements Serializable {

    private String nombre;

    public QAccion(String nombre) {
        this.nombre = nombre;
    }

    public abstract void ejecutar(Object... parametros);

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}
