/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.escena;

import net.qoopo.engine3d.componentes.terreno.QTerreno;

/**
 * Representa un nivel de videojuego
 * @author alberto
 */
public abstract class QEscenario {

    protected QTerreno terreno;

    public abstract void cargar(QEscena universo);

    public QTerreno getTerreno() {
        return terreno;
    }

    public void setTerreno(QTerreno terreno) {
        this.terreno = terreno;
    }

}
