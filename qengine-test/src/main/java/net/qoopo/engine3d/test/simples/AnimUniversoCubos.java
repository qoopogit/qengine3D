/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.QEngine3D;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.test.generaEjemplos.impl.simple.UniversoCubos;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class AnimUniversoCubos {

    public static void main(String[] args) {
        QEngine3D motor = new QEngine3D();
        GeneraEjemplo em = new UniversoCubos();
        em.iniciar(motor.getEscena());
        QCamara cam = new QCamara();
        motor.getEscena().agregarEntidad(cam);
        motor.configurarRenderer(800, 600, QMotorRender.RENDER_INTERNO, cam, false);
        motor.iniciar();
       
        float angulo = 0;
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
            }
            for (QEntidad entidad : motor.getEscena().getListaEntidades()) {
                if (!entidad.getNombre().equals(cam.getNombre())) {
                    entidad.rotar(angulo, angulo, angulo);

                }
            }
            angulo += (float) Math.toRadians(10);
        }
    }
}
