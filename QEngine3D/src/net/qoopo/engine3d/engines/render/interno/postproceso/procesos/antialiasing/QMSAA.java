/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.antialiasing;

import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;

/**
 * Realiza un efecto de realce de contraste
 *
 * @author alberto
 */
public class QMSAA extends QPostProceso {

    private int muestras = 4;

    public QMSAA(int ancho, int alto, int muestras) {
        this.muestras = muestras;
        bufferSalida = new QFrameBuffer(ancho, alto, null);
    }

    @Override
    public void procesar(QFrameBuffer... buffer) {
        QColor color;
        try {
            for (int x = 0; x < buffer[0].getAncho(); x++) {
                for (int y = 0; y < buffer[0].getAlto(); y++) {
                    color = buffer[0].getColor(x, y);
//                      color=QColor.BLACK.clone();
//                    color.addLocal(QColor.BLUE);
//                    //metodo con la sombra suave
                    for (int row = -1; row <= 1; ++row) {
                        for (int col = -1; col <= 1; ++col) {
                            try {
                                color.addLocal(buffer[0].getColor(x + col, y + row));
                            } catch (Exception e) {
                            }
                        }
                    }
//                    color.scaleLocal(1.0f / 9.0f);
                    bufferSalida.setQColor(x, y, color);
                }
            }
        } catch (Exception e) {

        }
        bufferSalida.actualizarTextura();
    }

}
