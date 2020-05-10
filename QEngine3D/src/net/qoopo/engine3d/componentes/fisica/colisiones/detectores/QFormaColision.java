/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores;

import net.qoopo.engine3d.componentes.QComponente;

/**
 * Envoltorio que Detecta una colisión
 *
 * @author alberto
 */
public abstract class QFormaColision extends QComponente {

    public abstract boolean verificarColision(QFormaColision otro);
}
