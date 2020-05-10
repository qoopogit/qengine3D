/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.dinamica;

import net.qoopo.engine3d.componentes.fisica.colisiones.QComponenteColision;

/**
 *
 * @author alberto
 */
public abstract class QObjetoDinamico extends QComponenteColision {

    /**
     * Cuerpos que estan siempre en la misma posicion. Tienen masa infinita y no
     * son afectados ni por gravedad ni por otros cuerpos
     */
    public static final int ESTATICO = 1;
    /**
     * Cuerpos que son sometidos a las leyes fisicas. Tienen masa finita. Son
     * afectados por la gravedad y otros cuerpos
     */
    public static final int DINAMICO = 2;
    /**
     * Tienen masa infinita pero no son afectados por gravedad ni otros cuerpos.
     * Si tienen velocidad
     */
    public static final int CINEMATICO = 3;

    public int tipoDinamico;

    public QObjetoDinamico(int tipoDinamico) {
        this.tipoDinamico = tipoDinamico;
    }

}
