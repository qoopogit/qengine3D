/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.terreno.QTerreno;

/**
 * Define una esfera contenedora del objeto
 *
 * @author alberto
 */
public class QColisionTerreno extends QFormaColision {

    private QTerreno terreno;

    public QColisionTerreno(QTerreno terreno) {
        this.terreno = terreno;
    }

    @Override
    public boolean verificarColision(QFormaColision otro) {

        boolean b = false;

//validar contra otro estera
        if (otro instanceof QColisionTerreno) {
        }

        return b;

    }

    @Override
    public void destruir() {
    }

    public QTerreno getTerreno() {
        return terreno;
    }

    public void setTerreno(QTerreno terreno) {
        this.terreno = terreno;
    }

}
