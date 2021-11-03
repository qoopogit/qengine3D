/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos;

import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public abstract class GeneraEjemplo {

    protected QEscena mundo;

    public abstract void iniciar(QEscena mundo);

    public abstract void accion(int numAccion, QMotorRender render);
}
