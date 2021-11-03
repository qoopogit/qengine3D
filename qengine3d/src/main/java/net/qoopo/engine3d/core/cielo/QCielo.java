/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.cielo;

import net.qoopo.engine3d.componentes.QEntidad;

/**
 *
 * @author alberto
 */
public abstract class QCielo {

    protected QEntidad entidad;

    public abstract void setRazon(float razon);

//    protected List<Q
    public QEntidad getEntidad() {
        return entidad;
    }

    public void setEntidad(QEntidad entidad) {
        this.entidad = entidad;
    }
}
