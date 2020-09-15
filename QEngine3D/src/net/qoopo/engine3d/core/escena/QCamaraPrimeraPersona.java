/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.escena;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import javax.swing.SwingUtilities;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.interaccion.QMouseReceptor;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;
import net.qoopo.engine3d.core.input.QInputManager;
import net.qoopo.engine3d.core.input.control.gizmo.QGizmo;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QCamaraPrimeraPersona extends QCamara {

    // varables usados para detectar movimiento en la pantalla
    public int prevX = -1;
    public int prevY = -1;

    public boolean ctrl = false;
    public boolean shift = false;
    public boolean alt = false;

    private Robot robot;

    public QCamaraPrimeraPersona() {
        super();
        configurar();
    }

    public QCamaraPrimeraPersona(String nombre) {
        super(nombre);
        configurar();
    }

    private void configurar() {
        try {
            robot = new Robot();
        } catch (Exception ex) {
        }

        QInputManager.agregarListenerMouse(new QMouseReceptor() {

            private QEntidad selectedObject;

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

                int deltaX = 0;
                int deltaY = 0;

                if (prevX >= 0) {
                    deltaX = evt.getXOnScreen() - prevX;
                }
                if (prevY >= 0) {
                    deltaY = evt.getYOnScreen() - prevY;
                }

                if (SwingUtilities.isLeftMouseButton(evt)) {
                    // activo los Gizmos
                    if (selectedObject != null) {
                        if (selectedObject instanceof QGizmo) {
                            ((QGizmo) selectedObject).mouseMove(deltaX, -deltaY);
//                        } else if (selectedObject instanceof QGizmoParte) {
//                            ((QGizmoParte) selectedObject).mouseMove(deltaX, -deltaY);
                        }
                    }
                }

                if (SwingUtilities.isMiddleMouseButton(evt)) {
                    if (!shift && !ctrl && !alt) {
                        //rota camara 
                        aumentarRotY((float) Math.toRadians(-deltaX / 2));
                        aumentarRotX((float) Math.toRadians(-deltaY / 2));

                    } else if (shift && !ctrl && !alt) {
                        //mueve la camara 
                        moverDerechaIzquierda(-deltaX / 100.0f);
                        moverArribaAbajo(deltaY / 100.0f);
//                        mouseMoveCamara(deltaX, deltaY);
                    }
                }

                prevX = evt.getXOnScreen();
                prevY = evt.getYOnScreen();
                warpMouse(evt.getXOnScreen(), evt.getYOnScreen());
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent evt) {

                if (evt.getWheelRotation() < 0) {
                    if (!shift) {
                        moverAdelanteAtras(0.2f);
                    } else {
                        moverAdelanteAtras(1f);
                    }
                } else {
                    if (!shift) {
                        moverAdelanteAtras(-0.2f);
                    } else {
                        moverAdelanteAtras(-1f);
                    }
                }

            }

            @Override
            public void mouseMoved(MouseEvent evt) {

            }

            @Override
            public void destruir() {

            }
        });
        QInputManager.agregarListenerTeclado(new QTecladoReceptor() {
            @Override
            public void keyPressed(KeyEvent evt) {

                switch (evt.getKeyCode()) {

                    case KeyEvent.VK_NUMPAD1:
                        lookAtTarget(new QVector3(0, 0, 10), QVector3.zero, QVector3.unitario_y);
                        break;
                    case KeyEvent.VK_NUMPAD3:
                        lookAtTarget(new QVector3(10, 0, 0), QVector3.zero, QVector3.unitario_y);
                        break;
                    case KeyEvent.VK_NUMPAD7:
                        lookAtTarget(new QVector3(0, 10, 0), QVector3.zero, QVector3.unitario_y);
                        break;

                    case KeyEvent.VK_J:
//                    case KeyEvent.VK_NUMPAD5:
                        setOrtogonal(!isOrtogonal());
                        break;

                    case KeyEvent.VK_Q:
                        if (!shift) {
                            aumentarY(0.2f);
                        } else {
                            aumentarY(0.8f);
                        }

                        break;
                    case KeyEvent.VK_E:
                        if (!shift) {
                            aumentarY(-0.2f);
                        } else {
                            aumentarY(-0.8f);
                        }
                        break;
                    case KeyEvent.VK_W:
                        //ir hacia adelante
                        if (!shift) {
                            moverAdelanteAtras(0.2f);
                        } else {
                            moverAdelanteAtras(0.8f);
                        }
                        break;
                    case KeyEvent.VK_S:
                        if (!shift) {
                            moverAdelanteAtras(-0.2f);
                        } else {
                            moverAdelanteAtras(-0.8f);
                        }
                        break;
                    case KeyEvent.VK_D:
                        //aumentarZ(1);
                        if (!shift) {
                            moverDerechaIzquierda(0.2f);
                        } else {
                            moverDerechaIzquierda(0.8f);
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (!shift) {
                            moverDerechaIzquierda(-0.2f);
                        } else {
                            moverDerechaIzquierda(-0.8f);
                        }
                        break;
                    case KeyEvent.VK_UP:
                        aumentarRotX((float) Math.toRadians(5));
                        break;
                    case KeyEvent.VK_DOWN:
                        aumentarRotX((float) Math.toRadians(-5));
                        break;
                    case KeyEvent.VK_RIGHT:
                        aumentarRotY((float) Math.toRadians(-5));
                        break;
                    case KeyEvent.VK_LEFT:
                        aumentarRotY((float) Math.toRadians(5));
                        break;
                    case KeyEvent.VK_CONTROL:
                        ctrl = true;
                        break;
                    case KeyEvent.VK_SHIFT:
                        shift = true;
                        break;
                    case KeyEvent.VK_ALT:
                        alt = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_CONTROL:
                        ctrl = false;
                        break;
                    case KeyEvent.VK_SHIFT:
                        shift = false;
                        break;
                    case KeyEvent.VK_ALT:
                        alt = false;
                        break;
                }
            }
        });
    }

    public void warpMouse(int x, int y) {
        if (robot != null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (x >= screenSize.width - 1) {
                x = 1;
                prevX = 0;
            }
            if (y >= screenSize.height - 1) {
                y = 1;
                prevY = 0;
            }
            if (x <= 0) {
                x = screenSize.width - 2;
                prevX = screenSize.width - 1;
            }
            if (y <= 0) {
                y = screenSize.height - 2;
                prevY = screenSize.height - 1;
            }
            robot.mouseMove(x, y);
        }
    }

}
