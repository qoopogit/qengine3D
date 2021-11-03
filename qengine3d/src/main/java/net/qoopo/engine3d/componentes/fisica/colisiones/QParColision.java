/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones;

/**
 *
 * @author alberto
 */
public class QParColision {

    private QComponenteColision ob1;
    private QComponenteColision ob2;

    public QParColision(QComponenteColision ob1, QComponenteColision ob2) {
        this.ob1 = ob1;
        this.ob2 = ob2;
    }

    public QComponenteColision getOb1() {
        return ob1;
    }

    public void setOb1(QComponenteColision ob1) {
        this.ob1 = ob1;
    }

    public QComponenteColision getOb2() {
        return ob2;
    }

    public void setOb2(QComponenteColision ob2) {
        this.ob2 = ob2;
    }

}
