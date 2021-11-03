/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.vehiculo;

import java.awt.event.KeyEvent;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;
import net.qoopo.engine3d.core.input.QInputManager;
import net.qoopo.engine3d.core.math.QMath;

/**
 *
 * @author alberto
 */
public class QVehiculoControl extends QComponente {

    private QVehiculo vehiculo;

//    private float maxEngineForce = 5000.f;//this should be engine/velocity dependent
    private float maxEngineForce = 1000.f;//this should be engine/velocity dependent
//    private static float maxEngineForce = 100.f;//this should be engine/velocity dependent
//    private static float maxEngineForce = 20.f;//this should be engine/velocity dependent
    private float maxBreakingForce = 100.f;
//    private float steeringIncrement = 0.04f;
//    private float steeringClamp = 0.3f;
    private float steeringIncrement = 0.5f;
    private float steeringClamp = (float) Math.toRadians(45);

    public QVehiculoControl(QVehiculo vehiculo) {
        this.vehiculo = vehiculo;

        //crea controladres con teclado
//        vehiculo.entidad.agregarComponente(
        QInputManager.agregarListenerTeclado(new QTecladoReceptor() {
            @Override
            public void keyPressed(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_SPACE:
                        vehiculo.setgBreakingForce(maxBreakingForce);
                        break;
                    case KeyEvent.VK_NUMPAD8://adelanta
                        vehiculo.setgEngineForce(QMath.clamp(vehiculo.getgEngineForce() + 400, -maxEngineForce, maxEngineForce));
//                        System.out.println("acelerando " + vehiculo.getgEngineForce());
                        break;
                    case KeyEvent.VK_NUMPAD5://retrocede
                        vehiculo.setgEngineForce(QMath.clamp(vehiculo.getgEngineForce() - 400, -maxEngineForce, maxEngineForce));
//                        System.out.println("retrocediento " + vehiculo.getgEngineForce());
                        break;
                    case KeyEvent.VK_NUMPAD6:
                        girarDerecha();
                        break;
                    case KeyEvent.VK_NUMPAD4:
                        girarIzquierda();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_NUMPAD8:
                        vehiculo.setgEngineForce(0);
                        break;
                    case KeyEvent.VK_NUMPAD5:
                        vehiculo.setgEngineForce(0);
                    case KeyEvent.VK_SPACE:
                        vehiculo.setgBreakingForce(0);
                        break;
                    case KeyEvent.VK_NUMPAD6:
//                        girarDerecha();
                        vehiculo.setgVehicleSteering(0);
                        break;
                    case KeyEvent.VK_NUMPAD4:
//                        girarIzquierda();
                        vehiculo.setgVehicleSteering(0);
                        break;
                }
            }
        }
        );
    }

    public void girarIzquierda() {
        vehiculo.setgVehicleSteering(QMath.clamp(vehiculo.getgVehicleSteering() + steeringIncrement, -steeringClamp, steeringClamp));
    }

    public void girarDerecha() {
        vehiculo.setgVehicleSteering(QMath.clamp(vehiculo.getgVehicleSteering() - steeringIncrement, -steeringClamp, steeringClamp));
    }

    @Override
    public void destruir() {

    }

}
