/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.personalizados;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;

/**
 * Realiza una lista de efectos en el orden establecido No puede ejecutar
 * procesos que requieran mas de 1 parametro como combinar
 *
 * @author alberto
 */
public class QEfectoLista extends QRenderEfectos {

    private List<QPostProceso> procesos = new ArrayList<>();

    public QEfectoLista() {
    }

    @Override
    public QTextura ejecutar(QTextura buffer) {//
        try {
            procesos.get(0).procesar(buffer);
            for (int i = 1; i < procesos.size(); i++) {
                procesos.get(i).procesar(procesos.get(i - 1).getBufferSalida());
            }
            return procesos.get(procesos.size() - 1).getBufferSalida();
        } catch (Exception e) {
            return null;
        }
    }

}
