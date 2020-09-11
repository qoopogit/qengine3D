/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import net.qoopo.engine3d.QMotor3D;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.test.generaEjemplos.impl.carga.EjemCargaMD5;
import net.qoopo.engine3d.test.generaEjemplos.impl.simple.EjemploSol;

/**
 *
 * @author alberto
 */
public class EjemploCargaMD5 {

    public static void main(String[] args) {
        QMotor3D motor = new QMotor3D();
        QCamara cam = new QCamara();
        motor.getEscena().agregarEntidad(cam);
        cam.lookAtPosicionObjetivo(new QVector3(15, 5, 5), new QVector3(1, 5, -10), QVector3.unitario_y);
        motor.configurarRenderer(800, 600, cam);
        motor.setearSalirESC();
        motor.iniciar();
        motor.getRenderer().setCargando(true);
        new EjemCargaMD5().iniciar(motor.getEscena());
        new EjemploSol().iniciar(motor.getEscena());
        motor.getRenderer().setCargando(false);

    }
}
