/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.animacion;

import java.io.Serializable;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;

/**
 *
 * @author alberto
 */
public class QParAnimacion implements Serializable {

    private QEntidad entidad;
    private QTransformacion transformacion;

    public QParAnimacion() {
    }

    public QParAnimacion(QEntidad entidad, QTransformacion transformacion) {
        this.entidad = entidad;
        this.transformacion = transformacion;
    }

    public QEntidad getEntidad() {
        return entidad;
    }

    public void setEntidad(QEntidad entidad) {
        this.entidad = entidad;
    }

    public QTransformacion getTransformacion() {
        return transformacion;
    }

    public void setTransformacion(QTransformacion transformacion) {
        this.transformacion = transformacion;
    }

}
