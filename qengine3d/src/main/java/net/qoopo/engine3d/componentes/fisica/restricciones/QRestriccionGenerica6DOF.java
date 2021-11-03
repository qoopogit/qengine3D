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
public class QRestriccionGenerica6DOF extends QRestriccion {

    private boolean usarReferenciaLinearA = false;

    public QRestriccionGenerica6DOF() {
    }

    public QRestriccionGenerica6DOF(QObjetoRigido a) {
        this.a = a;
    }

    public QRestriccionGenerica6DOF(QObjetoRigido a, QObjetoRigido b) {
        this.a = a;
        this.b = b;
    }

    public boolean isUsarReferenciaLinearA() {
        return usarReferenciaLinearA;
    }

    public void setUsarReferenciaLinearA(boolean usarReferenciaLinearA) {
        this.usarReferenciaLinearA = usarReferenciaLinearA;
    }

}
