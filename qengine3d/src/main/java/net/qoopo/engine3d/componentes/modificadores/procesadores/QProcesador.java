/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.procesadores;

import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public abstract class QProcesador extends QComponente {

    public abstract void preProceso();

    public abstract void procesar(QMotorRender render, QEscena universo);

    public abstract void postProceso();

}
