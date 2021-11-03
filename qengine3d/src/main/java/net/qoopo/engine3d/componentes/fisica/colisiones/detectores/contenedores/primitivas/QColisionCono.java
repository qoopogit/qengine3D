/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;

/**
 * Define una cilindro contenedora del objeto
 *
 * @author alberto
 */
public class QColisionCono extends QFormaColision {

    private float altura;
    private float radio;

    public QColisionCono() {
    }

    public QColisionCono(float altura, float radio) {
        this.altura = altura;
        this.radio = radio;
    }

    @Override
    public boolean verificarColision(QFormaColision otro) {

        boolean b = false;

//validar contra otro estera
        if (otro instanceof QColisionCono) {
        }

        return b;

    }

    public float getAltura() {
        return altura;
    }

    public void setAltura(float altura) {
        this.altura = altura;
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
