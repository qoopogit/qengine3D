/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import java.util.Random;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.QMotor3D;
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
        QMotor3D motor = new QMotor3D();

        GeneraEjemplo em = new UniversoCubos();
        em.iniciar(motor.getEscena());

        QCamara cam = new QCamara();
        motor.getEscena().agregarCamara(cam);

//        motor.configurarRenderer(800, 600, cam);
//        motor.configurarRenderer(800, 600, QMotorRender.RENDER_JAVA3D, cam, false);
        motor.configurarRenderer(800, 600, QMotorRender.RENDER_INTERNO, cam, false);

//        motor.getRenderer().actualizarObjetosYLuces();
        motor.iniciar();
        Random rnd = new Random();
        while (true) {
            for (QEntidad obj : motor.getEscena().getListaEntidades()) {
                if (!obj.getNombre().equals(cam.getNombre())) {
                obj.getTransformacion().getRotacion().aumentarRotX((float) Math.toRadians(rnd.nextFloat() * 10));
                obj.getTransformacion().getRotacion().aumentarRotY((float) Math.toRadians(rnd.nextFloat() * 10));
                obj.getTransformacion().getRotacion().aumentarRotZ((float) Math.toRadians(rnd.nextFloat() * 10));
//                    obj.getTransformacion().getRotacion().aumentarRotX((float) Math.toRadians(5));
//                    obj.getTransformacion().getRotacion().aumentarRotY((float) Math.toRadians(5));
//                    obj.getTransformacion().getRotacion().aumentarRotZ((float) Math.toRadians(5));
                    
                }
            }
            try {
//                Thread.sleep(50);
                Thread.sleep(10);
            } catch (Exception ex) {
            }
        }
    }
}
