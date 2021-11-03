/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.inteligencia.interno;

import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.engines.inteligencia.QMotorInteligencia;

/**
 * Este moror será el encargado de procesar los scripts de inteligencia y
 * comportamiento
 *
 * @author alberto
 */
public class QInteligencia extends QMotorInteligencia {

    public QInteligencia(QEscena universo) {
        super(universo);
    }

    @Override
    public void iniciar() {

    }

    @Override
    public void detener() {

    }

    @Override
    public long update() {
        tiempoPrevio = System.currentTimeMillis();
        return tiempoPrevio;
    }

}
