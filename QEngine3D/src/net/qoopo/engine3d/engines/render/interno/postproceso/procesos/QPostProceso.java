/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos;

import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;

/**
 * Permite la modificación de la imagen generada por el render
 *
 * @author alberto
 */
public abstract class QPostProceso {

    protected QFrameBuffer bufferSalida;

    public abstract void procesar(QFrameBuffer... buffer);

    public QFrameBuffer getBufferSalida() {
        return bufferSalida;
    }

    public void setBufferSalida(QFrameBuffer bufferSalida) {
        this.bufferSalida = bufferSalida;
    }

}
