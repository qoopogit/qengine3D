/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.antialiasing;

import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;

/**
 * Realiza un proceso de antialiasing
 *
 * @author alberto
 */
public class QMSAA extends QPostProceso {

    private int muestras = 4;

    public QMSAA(int ancho, int alto, int muestras) {
        this.muestras = muestras;
        bufferSalida = new QTextura(ancho, alto);
    }

    @Override
    public void procesar(QTextura... buffer) {
        QColor color;
        try {
            for (int x = 0; x < buffer[0].getAncho(); x++) {
                for (int y = 0; y < buffer[0].getAlto(); y++) {
                    color = buffer[0].getColor(x, y);
                    for (int row = -1; row <= 1; ++row) {
                        for (int col = -1; col <= 1; ++col) {
                            try {
                                color.addLocal(buffer[0].getColor(x + col, y + row));
                            } catch (Exception e) {
                            }
                        }
                    }
                    color.scaleLocal(1.0f / 9.0f);
                    bufferSalida.setQColor(bufferSalida.getAncho() * x / buffer[0].getAncho(), bufferSalida.getAlto() * y / buffer[0].getAlto(), color);
                }
            }
        } catch (Exception e) {

        }
//        bufferSalida.actualizarTextura();
    }

}
