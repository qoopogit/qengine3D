/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.geometria.QGeometria;

/**
 * Define una esfera contenedora del objeto
 *
 * @author alberto
 */
public class QColisionMallaConvexa extends QFormaColision {

    private QGeometria malla;

    public QColisionMallaConvexa(QGeometria geometria) {
        this.malla = geometria;
    }

    @Override
    public boolean verificarColision(QFormaColision otro) {

        boolean b = false;

//validar contra otro estera
        if (otro instanceof QColisionMallaConvexa) {
        }

        return b;

    }

    @Override
    public void destruir() {
    }

    public QGeometria getMalla() {
        return malla;
    }

    public void setMalla(QGeometria malla) {
        this.malla = malla;
    }

}
