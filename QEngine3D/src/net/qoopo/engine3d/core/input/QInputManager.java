/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.input;

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

    public static void eliminarListenerTeclador(QTecladoReceptor receptor) {
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

}
