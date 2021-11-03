/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.inteligencia;

import net.qoopo.engine3d.QMotor;

import net.qoopo.engine3d.core.escena.QEscena;

/**
 * Motor para ejecutar la inteligencia de los personajes del universo para
 * modificar sus propiedades
 *
 * @author alberto
 */
public abstract class QMotorInteligencia extends QMotor {

    protected QEscena universo;

//    protected long stopTime;
    public QMotorInteligencia(QEscena universo) {
        this.universo = universo;
    }

    @Override
    public void iniciar() {
        ejecutando = true;
    }

    @Override
    public void detener() {
        ejecutando = false;
        try {
            Thread.sleep(300);
        } catch (Exception e) {

        }
    }

    @Override
    public abstract long update();

}
