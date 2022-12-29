/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import net.qoopo.engine3d.QEngine3D;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.test.generaEjemplos.impl.carga.EjemCargaColladaDAE;
import net.qoopo.engine3d.test.generaEjemplos.impl.simple.EjemploSol;

/**
 *
 * @author alberto
 */
public class EjemploCargaCollada {

    public static void main(String[] args) {
        QEngine3D motor = new QEngine3D();
        QCamara cam = new QCamara();
        motor.getEscena().agregarEntidad(cam);
        cam.lookAtTarget(new QVector3(5, 10, 10), new QVector3(-5, 0, 0), QVector3.unitario_y);
        motor.configurarRenderer(800, 600, cam);
        motor.setearSalirESC();
        motor.iniciar();
        motor.getRenderer().setCargando(true);
        new EjemCargaColladaDAE().iniciar(motor.getEscena());
        new EjemploSol().iniciar(motor.getEscena());
        motor.getRenderer().setCargando(false);

    }
}
