/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.procesador.impl;

import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.colisiones.procesador.QProcesadorColision;

/**
 * Rebote simple
 *
 * @author alberto
 */
public class QProcesadorImplF2 extends QProcesadorColision {

    @Override
    public void procesarColision(QObjetoRigido obj1, QObjetoRigido obj2) {
        obj1.agregarFuerzas(obj1.velocidadLinear.clone().multiply(-0.453f));
    }
}
