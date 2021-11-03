/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.listeners;

import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;

/**
 *
 * @author alberto
 */
public abstract class QListenerColision extends QComponente {

    public abstract void colision(QEntidad ob1, QEntidad ob2);

    @Override
    public void destruir() {
    }

}
