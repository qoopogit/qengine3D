/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.restricciones;

import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;

/**
 *
 * @author alberto
 */
public class QRestriccionPunto2Punto extends QRestriccion {

    public QRestriccionPunto2Punto() {
    }

    public QRestriccionPunto2Punto(QObjetoRigido a) {
        this.a = a;
    }

    public QRestriccionPunto2Punto(QObjetoRigido a, QObjetoRigido b) {
        this.a = a;
        this.b = b;
    }

}
