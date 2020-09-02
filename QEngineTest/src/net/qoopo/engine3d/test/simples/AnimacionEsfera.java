/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.QMotor3D;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.test.generaEjemplos.impl.animado.EsferaAnimada;

/**
 *
 * @author alberto
 */
public class AnimacionEsfera {

    public static void main(String[] args) {
        QMotor3D motor = new QMotor3D();
        GeneraEjemplo em = new EsferaAnimada();
        em.iniciar(motor.getEscena());
        QCamara cam = new QCamara();

        cam.lookAt(new QVector3(0, 2, 8), new QVector3(0, 0.1f, 0.1f), QVector3.unitario_y.clone());
//        cam.setTipo(QCamara.FIJA_OBJETIVO);
        motor.getEscena().agregarCamara(cam);
        motor.configurarRenderer(800, 600, cam);
        motor.getRenderer().opciones.setVerCarasTraseras(true);//para ver la sombra completa, al usar una esfera como sombra calcula las caras visibles y solo se ve media esfera
        motor.iniciar();
        while (true) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ex) {
                Logger.getLogger(EjemploAnim3.class.getName()).log(Level.SEVERE, null, ex);
            }
//          
        }
    }
}
