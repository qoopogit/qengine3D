/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.QMotor3D;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.test.generaEjemplos.impl.textura.EjmTexturaEsfera;

/**
 *
 * @author alberto
 */
public class EjemploAnim3 {

    public static void main(String[] args) {
        QMotor3D motor = new QMotor3D();
        GeneraEjemplo em = new EjmTexturaEsfera();
        em.iniciar(motor.getEscena());
        QCamara cam = new QCamara();
        motor.getEscena().agregarEntidad(cam);
        motor.configurarRenderer(800, 600, cam);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(EjemploAnim3.class.getName()).log(Level.SEVERE, null, ex);
        }

        QEntidad esfera = new QEntidad("esfera");
        esfera.agregarComponente(new QEsfera(0.2f));
        esfera.agregarComponente(new QLuzPuntual(8.5f, new QColor(1, 0, 0.5f), 5, false, false));
        motor.getEscena().agregarEntidad(esfera);
        esfera.mover(new QVector3(-4, esfera.getTransformacion().getTraslacion().y, -4));
        motor.iniciar();
        float difX = 0.5f;
        float difZ = 0.5f;
        while (true) {
            int iteraciones = 0;
            //en x
            while (iteraciones < 16) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EjemploAnim3.class.getName()).log(Level.SEVERE, null, ex);
                }
                esfera.aumentarX(difX);
                iteraciones++;
            }
            //en z
            iteraciones = 0;
            while (iteraciones < 16) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EjemploAnim3.class.getName()).log(Level.SEVERE, null, ex);
                }
//                esferaGuia.aumentarX(difZ);
                esfera.aumentarZ(difZ);
                iteraciones++;
            }

            difX *= -1;
            difZ *= -1;
        }
    }
}
