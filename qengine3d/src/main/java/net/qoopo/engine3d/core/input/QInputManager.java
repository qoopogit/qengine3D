/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.input;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.util.ArrayList;
import net.qoopo.engine3d.componentes.interaccion.QMouseReceptor;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;

/**
 *
 * @author alberto
 */
public class QInputManager {

    private static ArrayList<QTecladoReceptor> listaReceptoresTeclado = new ArrayList<>();
    private static ArrayList<QMouseReceptor> listaReceptoresMouse = new ArrayList<>();

    private static boolean shitf = false;
    private static boolean ctrl = false;
    private static boolean alt = false;

    private static int prevX = -1;
    private static int prevY = -1;

    private static int deltaX = 0;
    private static int deltaY = 0;

    private static Robot robot;

    {
        try {
            robot = new Robot();
        } catch (Exception ex) {
        }
    }

    public static ArrayList<QTecladoReceptor> getListaReceptoresTeclado() {
        return listaReceptoresTeclado;
    }

    public static void setListaReceptoresTeclado(ArrayList<QTecladoReceptor> listaReceptoresTeclado) {
        QInputManager.listaReceptoresTeclado = listaReceptoresTeclado;
    }

    public static ArrayList<QMouseReceptor> getListaReceptoresMouse() {
        return listaReceptoresMouse;
    }

    public static void setListaReceptoresMouse(ArrayList<QMouseReceptor> listaReceptoresMouse) {
        QInputManager.listaReceptoresMouse = listaReceptoresMouse;
    }

    public static void agregarListenerMouse(QMouseReceptor receptor) {
        listaReceptoresMouse.add(receptor);
    }

    public static void eliminarListenerMouse(QMouseReceptor receptor) {
        listaReceptoresMouse.remove(receptor);
    }

    public static void agregarListenerTeclado(QTecladoReceptor receptor) {
        listaReceptoresTeclado.add(receptor);
    }

    public static void eliminarListenerTeclado(QTecladoReceptor receptor) {
        listaReceptoresTeclado.remove(receptor);
    }

    public static void procesarListenersTeclado(java.awt.event.KeyEvent evt, int tipo) {
        for (QTecladoReceptor receptor : listaReceptoresTeclado) {
            switch (tipo) {
                case 1:
                    receptor.keyPressed(evt);
                    break;
                case 2:
                    receptor.keyReleased(evt);
                    break;
            }
        }
    }

    public static void procesarListenersMouse(MouseEvent evt, int tipo) {
        for (QMouseReceptor receptor : listaReceptoresMouse) {
            switch (tipo) {
                case 1:
                    receptor.mouseEntered(evt);
                    break;
                case 2:
                    receptor.mousePressed(evt);
                    break;
                case 3:
                    receptor.mouseReleased(evt);
                    break;
                case 4:
                    receptor.mouseDragged(evt);
                    break;
                case 5:
                    receptor.mouseWheelMoved((MouseWheelEvent) evt);
                    break;
                case 6:
                    receptor.mouseMoved(evt);
                    break;
            }
        }
    }

    public static boolean isShitf() {
        return shitf;
    }

    public static void setShitf(boolean shitf) {
        QInputManager.shitf = shitf;
    }

    public static boolean isCtrl() {
        return ctrl;
    }

    public static void setCtrl(boolean ctrl) {
        QInputManager.ctrl = ctrl;
    }

    public static boolean isAlt() {
        return alt;
    }

    public static void setAlt(boolean alt) {
        QInputManager.alt = alt;
    }

    public static int getPrevX() {
        return prevX;
    }

    public static void setPrevX(int prevX) {
        QInputManager.prevX = prevX;
    }

    public static int getPrevY() {
        return prevY;
    }

    public static void setPrevY(int prevY) {
        QInputManager.prevY = prevY;
    }

    public static int getDeltaX() {
        return deltaX;
    }

    public static void setDeltaX(int deltaX) {
        QInputManager.deltaX = deltaX;
    }

    public static int getDeltaY() {
        return deltaY;
    }

    public static void setDeltaY(int deltaY) {
        QInputManager.deltaY = deltaY;
    }

    public static void warpMouse(int x, int y) {
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
