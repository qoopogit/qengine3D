/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color;

import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;
import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;

/**
 * Realiza una modificación del color final de la imagen realzando el contraste
 *
 * @author alberto
 */
public class QProcesadorCombina extends QPostProceso {

    public QProcesadorCombina(int ancho, int alto) {
        bufferSalida = new QFrameBuffer(ancho, alto, null);
    }

    @Override
    public void procesar(QFrameBuffer... buffer) {
        QColor color;
        QColor color2;
        QColor color3;
//        float fx = 0;
//        float fy = 0;
        try {
            for (int x = 0; x < buffer[0].getAncho(); x++) {
//                fx = (float) x / buffer[0].getAncho();
                for (int y = 0; y < buffer[0].getAlto(); y++) {
//                    fy = (float) y / buffer[0].getAlto();
                    color = buffer[0].getColor(x, y);
                    color2 = buffer[1].getColor(x, y);
//                    color2 = buffer[1].getColorNormalizado(fx, fy);
                    color3 = color.add(color2);
                    bufferSalida.setQColor(x, y, color3);
//                    bufferSalida.setQColorNormalizado(fx, fy, color3);
                }
            }
        } catch (Exception e) {

        }
        bufferSalida.actualizarTextura();
    }

}
