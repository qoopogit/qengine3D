/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.procesadores;

import java.util.List;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;

/**
 *
 * @author alberto
 */
public abstract class QProcesador extends QComponente {

//    protected QEscena mundo;
    //lista de objetos a los cuales se va a aplicar el proceso
    protected List<QGeometria> objetos;

    public abstract void preProceso();

    public abstract void procesar(QCamara camara, QEscena universo);

    public abstract void postProceso();

}
