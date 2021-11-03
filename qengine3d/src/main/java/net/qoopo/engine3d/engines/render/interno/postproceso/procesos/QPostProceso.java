/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos;

import net.qoopo.engine3d.core.textura.QTextura;

/**
 * Permite la modificación de la imagen generada por el render
 *
 * @author alberto
 */
public abstract class QPostProceso {

    protected QTextura bufferSalida;

    public abstract void procesar(QTextura... buffer);

    public QTextura getBufferSalida() {
        return bufferSalida;
    }

    public void setBufferSalida(QTextura bufferSalida) {
        this.bufferSalida = bufferSalida;
    }

}
