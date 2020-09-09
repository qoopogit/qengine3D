/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.basicos;

import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur.QProcesadorDepthOfField;

/**
 *
 * @author alberto
 */
public class QEfectoDepthOfField extends QRenderEfectos {

    private QProcesadorDepthOfField filtro = null;
    private int tipo;
    private float distanciaFocal = 0.5f;

    public QEfectoDepthOfField(int tipo, float distancia) {
//        contraste = new QProcesadorContraste(0.1f);
        this.tipo = tipo;
        this.distanciaFocal = distancia;
    }

    @Override
    public QTextura ejecutar(QTextura buffer) {
        try {
            //blur
            if (filtro == null) {
                filtro = new QProcesadorDepthOfField(distanciaFocal, tipo, 1.0f, 1);
            }
            filtro.procesar(buffer);
            return filtro.getBufferSalida();
        } catch (Exception e) {
            return null;
        }
    }

}
