/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.restricciones;

import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;

/**
 *
 * @author alberto
 */
public class QRestriccion extends QComponente {

    protected QObjetoRigido a;
    protected QObjetoRigido b;

    @Override
    public void destruir() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public QObjetoRigido getA() {
        return a;
    }

    public void setA(QObjetoRigido a) {
        this.a = a;
    }

    public QObjetoRigido getB() {
        return b;
    }

    public void setB(QObjetoRigido b) {
        this.b = b;
    }

}
