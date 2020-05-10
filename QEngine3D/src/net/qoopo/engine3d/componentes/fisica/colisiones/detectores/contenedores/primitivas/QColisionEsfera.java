/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;

/**
 * Define una esfera contenedora del objeto
 *
 * @author alberto
 */
public class QColisionEsfera extends QFormaColision {

    private float radio;

    public QColisionEsfera() {
        radio = 1.0f;
    }

    public QColisionEsfera(float radio) {
        this.radio = radio;
    }

    @Override
    public boolean verificarColision(QFormaColision otro) {

        boolean b = false;

//validar contra otro estera
        if (otro instanceof QColisionEsfera) {
        }

        return b;

    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    @Override
    public void destruir() {
    }
}
