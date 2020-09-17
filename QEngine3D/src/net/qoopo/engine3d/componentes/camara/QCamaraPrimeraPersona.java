/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.camara;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.interaccion.QMouseReceptor;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.input.QInputManager;

/**
 * Componenete que permite controlar la camara con el mouse en modo de primera persona
 * @author alberto
 */
public class QCamaraPrimeraPersona extends QComponente {

    private QCamara camara;
    private QMouseReceptor mouseReceptor;
    private QTecladoReceptor tecladoReceptor;

    public QCamaraPrimeraPersona(QCamara camara) {
        this.camara = camara;
        configurar();
    }

    public QCamara getCamara() {
        return camara;
    }

    public void setCamara(QCamara camara) {
        this.camara = camara;
    }

    private void configurar() {
        mouseReceptor = new QMouseReceptor() {

            @Override
            public void mouseEntered(MouseEvent evt) {

            }

            @Override
            public void mousePressed(MouseEvent evt) {
                if (SwingUtilities.isLeftMouseButton(evt)) {

                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {

            }

            @Override
            public void mouseDragged(MouseEvent evt) {

                if (SwingUtilities.isLeftMouseButton(evt)) {

                }

                if (SwingUtilities.isMiddleMouseButton(evt)) {
                    if (QInputManager.isShitf() && QInputManager.isCtrl() && !QInputManager.isAlt()) {
                        //rota camara en su propio eje
                        camara.aumentarRotY((float) Math.toRadians(-QInputManager.getDeltaX() / 2));
                        camara.aumentarRotX((float) Math.toRadians(-QInputManager.getDeltaY() / 2));
                    } else if (QInputManager.isShitf() && !QInputManager.isCtrl() && !QInputManager.isAlt()) {
                        //mueve la camara 
                        camara.moverDerechaIzquierda(-QInputManager.getDeltaX() / 100.0f);
                        camara.moverArribaAbajo(QInputManager.getDeltaY() / 100.0f);
                    }
                }
                QInputManager.warpMouse(evt.getXOnScreen(), evt.getYOnScreen());
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent evt) {
                if (evt.getWheelRotation() < 0) {
                    if (!QInputManager.isShitf()) {
                        camara.moverAdelanteAtras(0.2f);
                    } else {
                        camara.moverAdelanteAtras(1f);
                    }
                } else {
                    if (!QInputManager.isShitf()) {
                        camara.moverAdelanteAtras(-0.2f);
                    } else {
                        camara.moverAdelanteAtras(-1f);
                    }
                }

            }

            @Override
            public void mouseMoved(MouseEvent evt) {

            }

            @Override
            public void destruir() {

            }
        };

        tecladoReceptor = new QTecladoReceptor() {
            @Override
            public void keyPressed(KeyEvent evt) {

            }

            @Override
            public void keyReleased(KeyEvent evt) {

            }
        };

        QInputManager.agregarListenerMouse(mouseReceptor);
        QInputManager.agregarListenerTeclado(tecladoReceptor);
    }

    @Override
    public void destruir() {
        QInputManager.eliminarListenerMouse(mouseReceptor);
        QInputManager.eliminarListenerTeclado(tecladoReceptor);
    }

}
