/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.basicos;

import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;

/**
 * No realiza ningun efecto post procesamiento
 *
 * @author alberto
 */
public class QEfectosDefault extends QRenderEfectos {

    public QEfectosDefault() {
    }

    @Override
    public QFrameBuffer ejecutar(QFrameBuffer buffer) {
        return buffer;
    }

}
