/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.sombras;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QVector3;

/**
 * Clase abstracta de procesador de sombra. Un procesador de sombra crea el mapa
 * de sombras y calcula el factor de sombra para cada pixel
 *
 * @author alberto
 */
public abstract class QProcesadorSombra {

    protected QLuz luz;
    protected QEscena universo;
    protected int ancho;
    protected int alto;
    protected boolean actualizar = true;//bandera que indica si se debe actualizar el mapa de sombras
    protected boolean dinamico = false;//si esta bandera esta activada, se actualiza el mapa de sombra en cada pasada

    public QProcesadorSombra(QEscena mundo, QLuz luz, int ancho, int alto) {
        this.luz = luz;
        this.universo = mundo;
        this.ancho = ancho;
        this.alto = alto;
    }

    public abstract float factorSombra(QVector3 vector, QEntidad entidad);

    public abstract void generarSombras();

    public abstract void destruir();

    public QLuz getLuz() {
        return luz;
    }

    public void setLuz(QLuz luz) {
        this.luz = luz;
    }

    public boolean isActualizar() {
        return actualizar;
    }

    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

    public boolean isDinamico() {
        return dinamico;
    }

    public void setDinamico(boolean dinamico) {
        this.dinamico = dinamico;
    }

}
