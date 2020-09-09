/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.complejos;

import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorBloom;

/**
 * Realiza le efecto de postprocesamiento de Bloom
 *
 * @author alberto
 */
public class QEfectoBloom extends QRenderEfectos {

    private QProcesadorBloom filtro = null;

    public QEfectoBloom() {
    }

    @Override
    public QTextura ejecutar(QTextura buffer) {
        try {
            if (filtro == null || (filtro.getBufferSalida().getAncho() != buffer.getAncho() && filtro.getBufferSalida().getAlto() != buffer.getAlto())) {
                filtro = new QProcesadorBloom(buffer.getAncho(), buffer.getAlto());
            }
            filtro.procesar(buffer);
            return filtro.getBufferSalida();
        } catch (Exception e) {
            return null;
        }
    }

}
