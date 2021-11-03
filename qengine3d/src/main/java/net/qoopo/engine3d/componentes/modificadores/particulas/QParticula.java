/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.particulas;

import net.qoopo.engine3d.componentes.QEntidad;

/**
 * Paríicula a ser emitida por el Emisor de Partículas
 *
 * @author alberto
 */
public class QParticula {

//    public QObjetoRigido objeto;
    public QEntidad objeto;

    private long tiempoCreacion;
    private float tiempoVida;

    public QParticula() {

    }

    public void iniciarVida() {
        tiempoCreacion = System.currentTimeMillis();
    }

    public float getTiempoVida() {
        return tiempoVida;
    }

    public void setTiempoVida(float tiempoVida) {
        this.tiempoVida = tiempoVida;
    }

    public long getTiempoCreacion() {
        return tiempoCreacion;
    }

    public void setTiempoCreacion(long tiempoCreacion) {
        this.tiempoCreacion = tiempoCreacion;
    }

}
