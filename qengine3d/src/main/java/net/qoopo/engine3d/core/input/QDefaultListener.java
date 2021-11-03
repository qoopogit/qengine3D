/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 *
 * @author alberto
 */
public class QDefaultListener implements MouseMotionListener, MouseWheelListener, MouseListener, KeyListener {

    public static QDefaultListener INSTANCIA = new QDefaultListener();

    @Override
    public void mouseDragged(MouseEvent e) {
        QInputManager.procesarListenersMouse(e, 4);

        if (QInputManager.getPrevX() >= 0) {
            QInputManager.setDeltaX(e.getXOnScreen() - QInputManager.getPrevX());
        }
        if (QInputManager.getPrevY() >= 0) {
            QInputManager.setDeltaY(e.getYOnScreen() - QInputManager.getPrevY());
        }

        QInputManager.setPrevX(e.getXOnScreen());
        QInputManager.setPrevY(e.getYOnScreen());

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        QInputManager.procesarListenersMouse(e, 6);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        QInputManager.procesarListenersMouse(e, 5);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        QInputManager.procesarListenersMouse(e, 2);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        QInputManager.procesarListenersMouse(e, 3);
        QInputManager.setPrevX(-1);
        QInputManager.setPrevY(-1);

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        QInputManager.procesarListenersMouse(e, 1);
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        QInputManager.procesarListenersTeclado(e, 1);

        switch (e.getKeyCode()) {

            case KeyEvent.VK_CONTROL:
                QInputManager.setCtrl(true);
                break;
            case KeyEvent.VK_SHIFT:
                QInputManager.setShitf(true);
                break;
            case KeyEvent.VK_ALT:
                QInputManager.setAlt(true);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        QInputManager.procesarListenersTeclado(e, 2);

        switch (e.getKeyCode()) {

            case KeyEvent.VK_CONTROL:
                QInputManager.setCtrl(false);
                break;
            case KeyEvent.VK_SHIFT:
                QInputManager.setShitf(false);
                break;
            case KeyEvent.VK_ALT:
                QInputManager.setAlt(false);
                break;
        }
    }

}
