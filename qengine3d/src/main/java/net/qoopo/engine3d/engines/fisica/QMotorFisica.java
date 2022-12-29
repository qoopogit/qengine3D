/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.fisica;

import net.qoopo.engine3d.QEngine;
import net.qoopo.engine3d.core.escena.QEscena;

/**
 * Motor de simulacion física. Se encarga de aplicar las fuerzas a los objetos
 * del universo para modificar sus propiedades
 *
 * @author alberto
 */
public abstract class QMotorFisica extends QEngine {

    protected QEscena universo;

    public static final int FISICA_INTERNO = 1;
    public static final int FISICA_JBULLET = 2;

    public QMotorFisica(QEscena universo) {
        this.universo = universo;
    }

    @Override
    public void iniciar() {
        ejecutando = true;
    }

    @Override
    public void detener() {
        ejecutando = false;
//        try {
//            Thread.sleep(10);
//        } catch (Exception e) {
//
//        }
    }

    @Override
    public abstract long update();

}
