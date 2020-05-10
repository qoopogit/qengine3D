/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.superficie;

/**
 *
 * @author aigarcia
 */
public class Superficie {

    private QJPanel componente;

    public Superficie() {
    }

    public Superficie(QJPanel componente) {
        this.componente = componente;
    }

    public QJPanel getComponente() {
        return componente;
    }

    public void setComponente(QJPanel componente) {
        this.componente = componente;
    }

    public int getWidth() {
        return this.componente.getWidth();
    }

    public int getHeight() {
        return this.componente.getHeight();
    }
}
