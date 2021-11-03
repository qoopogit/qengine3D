/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.basicos;

import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorCel;

/**
 * Realiza un efecto de realce de contraste
 *
 * @author alberto
 */
public class QEfectoCel extends QRenderEfectos {

    private QProcesadorCel filtro = null;

    public QEfectoCel() {
    }

    @Override
    public QTextura ejecutar(QTextura buffer) {
        try {
            if (filtro == null || (filtro.getBufferSalida().getAncho() != buffer.getAncho() && filtro.getBufferSalida().getAlto() != buffer.getAlto())) {
                filtro = new QProcesadorCel(buffer.getAncho(), buffer.getAlto());
            }
            filtro.procesar(buffer);
            return filtro.getBufferSalida();
        } catch (Exception e) {
            return null;
        }
    }
}
