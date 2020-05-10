/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import net.qoopo.engine3d.QMotor3D;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.test.generaEjemplos.impl.carga.EjemCargaMD5;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class EjemploCargaMD5 {

    public static void main(String[] args) {
        QMotor3D motor = new QMotor3D();

        GeneraEjemplo em = new EjemCargaMD5();

        QCamara cam = new QCamara();
        motor.getEscena().agregarCamara(cam);
        motor.getEscena().setLuzAmbiente(0.6f);
        cam.lookAtPosicionObjetivo(new QVector3(50, 50, 50), new QVector3(0, 0, 0), QVector3.unitario_y);
        motor.configurarRenderer(800, 600, cam);
        motor.setearSalirESC();
        motor.iniciar();

//        motor.setearSalirESC();
//        motor.configurarRenderer(1024, 768, cam);
//---
        motor.getRenderer().setCargando(true);

        em.iniciar(motor.getEscena());
        motor.getRenderer().setCargando(false);
// monstruo
//        cam.lookAtPosicionObjetivo(new QVector3(0, 200, 100), new QVector3(0, 0, 100), QVector3.unitario_z); // ok de lado
//        cam.lookAtPosicionObjetivo(new QVector3(0, -200, 100), new QVector3(0, 0, 100), QVector3.unitario_z); // ok de lado
//        cam.lookAtPosicionObjetivo(new QVector3(200, 0, 100), new QVector3(0, 0, 0), QVector3.unitario_z); //ok de frente
// hombre de la lampara
//        cam.lookAtPosicionObjetivo(new QVector3(0, -200, 100), new QVector3(0, 0, 100), QVector3.unitario_z); // ok de lado
//        cam.lookAtPosicionObjetivo(new QVector3(200, 0, 100), new QVector3(0, 0, 0), QVector3.unitario_z); //ok de frente
//        motor.iniciarAnimaciones();
//        motor.setIniciarAnimaciones(false);
//        motor.iniciarFisica();
    }
}
