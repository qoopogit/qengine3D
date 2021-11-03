/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.compuesta;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.core.math.QVector3;

/**
 * Define una esfera contenedora del objeto
 *
 * @author alberto
 */
public class QColisionCompuestaHija extends QFormaColision {

    private QFormaColision forma;
    private QVector3 ubicacion;

    public QColisionCompuestaHija(QFormaColision formaColision, QVector3 ubicacion) {
        this.forma = formaColision;
        this.ubicacion = ubicacion;
    }

    public QFormaColision getForma() {
        return forma;
    }

    public void setForma(QFormaColision forma) {
        this.forma = forma;
    }

    public QVector3 getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(QVector3 ubicacion) {
        this.ubicacion = ubicacion;
    }

    @Override
    public boolean verificarColision(QFormaColision otro) {

        boolean b = false;

//validar contra otro estera
        if (otro instanceof QColisionCompuestaHija) {
        }

        return b;

    }

    @Override
    public void destruir() {
    }

}
