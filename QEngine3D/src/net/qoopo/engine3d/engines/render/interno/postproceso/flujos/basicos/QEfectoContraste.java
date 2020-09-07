/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.basicos;

import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorContraste;

/**
 * Realiza un efecto de realce de contraste
 *
 * @author alberto
 */
public class QEfectoContraste extends QRenderEfectos {

    private QProcesadorContraste filtro = null;

    public QEfectoContraste() {
    }

    @Override
    public QFrameBuffer ejecutar(QFrameBuffer buffer) {
        try {
            if (filtro == null || (filtro.getBufferSalida().getAncho() != buffer.getAncho() && filtro.getBufferSalida().getAlto() != buffer.getAlto())) {
                filtro = new QProcesadorContraste(buffer.getAncho(), buffer.getAlto(), 0.2f);
            }
            filtro.procesar(buffer.getTextura());
            buffer.setTextura(filtro.getBufferSalida());
        } catch (Exception e) {
//            return buffer;
        }
        return buffer;
    }
}
