/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color;

import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;

/**
 * Realiza una modificación del color final de la imagen realzando el contraste
 *
 * @author alberto
 */
public class QProcesadorBrillo extends QPostProceso {

    public QProcesadorBrillo(int ancho, int alto) {
        bufferSalida = new QTextura(ancho, alto);
    }

    @Override
    public void procesar(QTextura... buffer) {
        QColor color;
        float brillo;
        try {
            for (int x = 0; x < buffer[0].getAncho(); x++) {
                for (int y = 0; y < buffer[0].getAlto(); y++) {
                    color = buffer[0].getColor(x, y);
                    brillo = color.r * 0.2126f + color.g * 0.7152f + color.b * 0.0722f;
                    color = color.scale(brillo);
//                    if (brillo < 0.7f) {
//                        color = QColor.BLACK;
//                    }
                    bufferSalida.setQColor(x, y, color);
//                    bufferSalida.setQColorNormalizado((float) x / buffer[0].getAncho(), (float) y / buffer[0].getAlto(), color);
                }
            }
        } catch (Exception e) {

        }
//        bufferSalida.actualizarTextura();
    }

}
