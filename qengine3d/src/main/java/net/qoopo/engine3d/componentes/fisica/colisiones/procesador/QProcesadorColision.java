/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.procesador;

import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;

/**
 *
 * @author alberto
 */
public abstract class QProcesadorColision {

    public abstract void procesarColision(QObjetoRigido obj1, QObjetoRigido obj2);
}
