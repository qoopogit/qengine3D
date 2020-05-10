/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos;

import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;

/**
 *
 * @author alberto
 */
public abstract class QRenderEfectos {

    public abstract QFrameBuffer ejecutar(QFrameBuffer buffer);
}
