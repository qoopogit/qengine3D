/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.antialiasing;

import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.antialiasing.QMSAA;

/**
 * Realiza una correcion antialiasing MSAA
 *
 * @author alberto
 */
public class QAntialiasing extends QRenderEfectos {

    private QMSAA filtro = null;

    public QAntialiasing() {
    }

    @Override
    public QTextura ejecutar(QTextura buffer) {
        try {
            if (filtro == null || (filtro.getBufferSalida().getAncho() != buffer.getAncho() && filtro.getBufferSalida().getAlto() != buffer.getAlto())) {
                filtro = new QMSAA(buffer.getAncho(), buffer.getAlto(), 4);
            }
            filtro.procesar(buffer);
            return filtro.getBufferSalida();
        } catch (Exception e) {
            return null;
        }
//         return buffer;
    }
}
