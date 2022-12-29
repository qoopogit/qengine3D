/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.audio;

import net.qoopo.engine3d.QEngine;

import net.qoopo.engine3d.core.escena.QEscena;

/**
 * Motor de audio. Se encarga de gestionar los componentes de audio. Actualizar
 * las coordenadas de acuerdo a las coordenadas de la entidad
 *
 *
 * @author alberto
 */
public abstract class QMotorAudio extends QEngine {

    protected QEscena escena;

//    protected long stopTime;
    public QMotorAudio(QEscena escena) {
        this.escena = escena;
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
