/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.simples;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.QMotor3D;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.test.generaEjemplos.impl.textura.EjmTexturaSistemaSolar;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class EjemploAnim2 {
    
    public static void main(String[] args) {
        QMotor3D motor = new QMotor3D();
//        motor.setTipo_ejecucion(QMotor3D.HILOS_INDEPENDIENTE);
        
//        GeneraEjemplo em = new EjmTexturaCubo();
        GeneraEjemplo em = new EjmTexturaSistemaSolar();
        em.iniciar(motor.getEscena());
        
        QCamara cam = new QCamara();
        motor.getEscena().agregarCamara(cam);        
        motor.configurarRenderer(800, 600, cam);
        motor.iniciar();        

//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException ex) {
//            Logger.getLogger(EjemploAnim2.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        motor.iniciarFisica();
//        motor.iniciarFisica();

        QEntidad esfera = new QEntidad("esfera");
        esfera.agregarComponente(QUtilNormales.invertirNormales(new QEsfera(1)));
        
        esfera.agregarComponente(new QLuzPuntual(8.5f, new QColor(1, 0, 0.5f),  5f, false, false));

        //al escena se agregan todos los objetos
        motor.getEscena().agregarEntidad(esfera);
        
        esfera.mover(new QVector3(-10, esfera.getTransformacion().getTraslacion().y, -10));
        
        float difX = 0.5f;
        float difZ = 0.5f;
        while (true) {
            int iteraciones = 0;

            //en x
            while (iteraciones < 20) {
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EjemploAnim2.class.getName()).log(Level.SEVERE, null, ex);
                }
                esfera.aumentarX(difX);
                
                iteraciones++;
            }
            
            esfera.getTransformacion().getRotacion().aumentarRotY((float) (Math.PI / 2));

            //en z
            iteraciones = 0;
            while (iteraciones < 20) {
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EjemploAnim2.class.getName()).log(Level.SEVERE, null, ex);
                }
                esfera.aumentarZ(difZ);
                iteraciones++;
            }

            //erotacion
            iteraciones = 0;
            while (iteraciones < 72) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EjemploAnim2.class.getName()).log(Level.SEVERE, null, ex);
                }
                esfera.aumentarRotY((float) Math.toRadians(20f));
                iteraciones++;
            }
            difX *= -1;
            difZ *= -1;
            
        }
    }
    
}
