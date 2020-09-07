/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.basicos;

import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur.QProcesadorBlur;

/**
 *
 * @author alberto
 */
public class QEfectoBlur extends QRenderEfectos {

    private QProcesadorBlur filtro = null;

    public QEfectoBlur() {
//        contraste = new QProcesadorContraste(0.1f);
    }

    @Override
    public QFrameBuffer ejecutar(QFrameBuffer buffer) {
        try {
            //blur
            if (filtro == null) {
                filtro = new QProcesadorBlur(0.5f, 2);
            }
            filtro.procesar(buffer.getTextura());
            buffer.setTextura(filtro.getBufferSalida());
        } catch (Exception e) {
//            return buffer;
        }
        return buffer;
    }

}
