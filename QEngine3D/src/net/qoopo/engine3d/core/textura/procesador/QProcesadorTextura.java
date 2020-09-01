/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura.procesador;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public abstract class QProcesadorTextura implements Serializable {

    public static final int MODO_REMPLAZAR = 1;
    public static final int MODO_COMBINAR = 2;

    // referencia al objeto convertido para java 3D.
    public Object objetoJava3D;

    private int modo = MODO_REMPLAZAR;

    //esta bandera la uso para identificar si es un reflejo y debo trabajar con su proyeccion 
    //en lugar del mapeo normal
    //el calculo lo hace el render
    //solo tenemos la bandera por logistica
    protected boolean proyectada = false;

    public abstract void procesar();

    public abstract int get_ARGB(float x, float y);

    public abstract QColor get_QARGB(float x, float y);

    public abstract float getNormalX(float x, float y);

    public abstract float getNormalY(float x, float y);

    public abstract float getNormalZ(float x, float y);

    /**
     * Devuelve la textura actual. Puede ser resultado de un proceso
     * implementado
     *
     * @param size
     * @return
     */
    public abstract BufferedImage getTexture(Dimension size);

    /**
     * Devuelve la textura actual
     *
     * @return
     */
    public abstract BufferedImage getTexture();

    public int getModo() {
        return modo;
    }

    public void setModo(int modo) {
        this.modo = modo;
    }

    public abstract void destruir();

    public abstract void setMuestrasU(float muestras);

    public abstract void setMuestrasV(float muestras);

    public boolean isProyectada() {
        return proyectada;
    }

    public void setProyectada(boolean proyectada) {
        this.proyectada = proyectada;
    }

}
