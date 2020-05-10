/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.basicos;

import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorCel;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorContraste;

/**
 * Realiza un efecto de realce de contraste
 *
 * @author alberto
 */
public class QEfectoCel extends QRenderEfectos {

    private QProcesadorCel procesador = null;

    public QEfectoCel() {
    }

    @Override
    public QFrameBuffer ejecutar(QFrameBuffer buffer) {
        try {
            if (procesador == null || (procesador.getBufferSalida().getAncho() != buffer.getAncho() && procesador.getBufferSalida().getAlto() != buffer.getAlto())) {
                procesador = new QProcesadorCel(buffer.getAncho(), buffer.getAlto());
            }
            procesador.procesar(buffer);
            return procesador.getBufferSalida();
        } catch (Exception e) {
            return buffer;
        }
    }
}
