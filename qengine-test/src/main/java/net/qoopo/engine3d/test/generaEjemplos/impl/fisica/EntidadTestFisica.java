/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.fisica;

import net.qoopo.engine3d.componentes.QEntidad;

/**
 *
 * @author alberto
 */
public class EntidadTestFisica extends QEntidad {

    public EntidadTestFisica() {
        super();
        construir();
    }

    private void construir() {

    }

    @Override
    public void comprobarMovimiento() {
//        if (transformacion.getTraslacion().y < 0.5f) {
//            transformacion.getTraslacion().y = 0.5f;
//        }
    }

}
