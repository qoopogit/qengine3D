/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core;

/**
 *
 * @author alberto
 */
public class QNodoEnlace {

    private QNodoPeriferico entrada;
    private QNodoPeriferico salida;

    public QNodoEnlace(QNodoPeriferico entrada, QNodoPeriferico salida) {
        this.entrada = entrada;
        this.salida = salida;
        // a los perifericos les agrego el enlace
        if (entrada != null) {
            entrada.getEnlaces().add(this);
        }
        if (salida != null) {
            salida.getEnlaces().add(this);
        }
    }

    public QNodoEnlace() {
    }

    public QNodoPeriferico getEntrada() {
        return entrada;
    }

    public void setEntrada(QNodoPeriferico entrada) {
        this.entrada = entrada;
    }

    public QNodoPeriferico getSalida() {
        return salida;
    }

    public void setSalida(QNodoPeriferico salida) {
        this.salida = salida;
    }

}
