/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.procesador.impl;

import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.colisiones.procesador.QProcesadorColision;

/**
 * Detiene el movimiento de los objetos
 *
 * @author alberto
 */
public class QProcesadorImplF1 extends QProcesadorColision {

    @Override
    public void procesarColision(QObjetoRigido obj1, QObjetoRigido obj2) {

        //metodo 1 dejar de mover no hay rebote ni se aplican fuerzas
        obj1.detener();// le digo que ya no se mueva si tenia una fuerza aplicandolo como la peso
        obj2.detener();
        obj1.velocidadLinear.setXYZ(0, 0, 0);
        obj2.velocidadLinear.setXYZ(0, 0, 0);

    }
}
