/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.interaccion;

import net.qoopo.engine3d.componentes.QComponente;

/**
 *
 * @author alberto
 */
public abstract class QTecladoReceptor extends QComponente {

    public abstract void keyPressed(java.awt.event.KeyEvent evt);

    public abstract void keyReleased(java.awt.event.KeyEvent evt);

    @Override
    public void destruir() {

    }

}
