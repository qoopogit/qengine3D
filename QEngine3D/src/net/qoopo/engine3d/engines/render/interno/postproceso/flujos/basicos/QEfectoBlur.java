/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.basicos;

import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur.QProcesadorBlur;

/**
 *
 * @author alberto
 */
public class QEfectoBlur extends QRenderEfectos {

    private QProcesadorBlur filtro = null;

    public QEfectoBlur(int ancho, int alto) {
        filtro = new QProcesadorBlur(ancho, alto, 10);
    }

    public QEfectoBlur() {
        filtro = new QProcesadorBlur(1.0f, 10);
    }

    @Override
    public QTextura ejecutar(QTextura buffer) {
        try {
            filtro.procesar(buffer);
            return filtro.getBufferSalida();
        } catch (Exception e) {
            return null;
        }
    }

}
