/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.animacion;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.core.math.QMatriz4;

/**
 *
 * @author alberto
 */
public class QEsqueleto extends QComponente {

//    private QEntidad huesoRaiz;
    private List<QHueso> huesos = new ArrayList<>();
    private boolean mostrar = false;
    private boolean superponer = false;

    public QEsqueleto() {
    }

    public QEsqueleto(List<QHueso> huesos) {
        this.huesos = huesos;
    }

    public List<QHueso> getHuesos() {
        return huesos;
    }

    public void setHuesos(List<QHueso> huesos) {
        this.huesos = huesos;
    }

    public void agregarHueso(QHueso hueso) {
        huesos.add(hueso);
    }

    public QHueso getHueso(int i) {
//        return huesos.get(i);
        for (QHueso hueso : huesos) {
            if (hueso.getIndice() == i) {
                return hueso;
            }
        }
//        System.out.println("Hueso no encontrado (i)" + i);
        return null;
    }

    public QHueso getHueso(String nombre) {
//        return huesos.get(i);
        for (QHueso hueso : huesos) {
            // los nombres en este motor siempre se agrea un espacio con un número
            if (hueso.getNombre().equals(nombre) || hueso.getNombre().startsWith(nombre + " ")) {
                return hueso;
            }
        }
//        System.out.println("Hueso no encontrado (n) " + nombre);
        return null;

    }

    @Override
    public void destruir() {
        for (QHueso hueso : huesos) {
            hueso.destruir();
        }
        huesos.clear();
        huesos = null;
    }

    /**
     * Debe ser llamado una sola vez despuesde la creaci[on de los esqueletos.
     * Calcula las inversas de las trasnformaciones de cada hueso para deshacer
     * la trasnformacion de los vertices y aplicar la trasnformacion de cada
     * animacion
     */
    public void calcularMatricesInversas() {
//        System.out.println("Calculando inversas ");
        for (QHueso hueso : huesos) {
            //si es el hueso raiz
            if (hueso.getPadre() == null) {
//                System.out.println("Hueso raiz=" + hueso.getNombre());
                hueso.calcularTransformacionInversa(new QMatriz4());
//                return;
            }
        }
//        System.out.println("No se encontr[o hueso raiz");
    }

    public boolean isMostrar() {
        return mostrar;
    }

    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    public boolean isSuperponer() {
        return superponer;
    }

    public void setSuperponer(boolean superponer) {
        this.superponer = superponer;
    }

}
