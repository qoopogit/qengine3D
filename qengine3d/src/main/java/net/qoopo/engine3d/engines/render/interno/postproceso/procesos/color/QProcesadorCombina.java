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
public class QProcesadorCombina extends QPostProceso {

    public QProcesadorCombina(int ancho, int alto) {
        bufferSalida = new QTextura(ancho, alto);
    }

    @Override
    public void procesar(QTextura... buffer) {
        QColor color;
        QColor color2;
        QColor color3;
        QTextura textura1 = buffer[0];
        QTextura textura2 = buffer[1];
        try {
            for (int x = 0; x < textura1.getAncho(); x++) {
                for (int y = 0; y < textura1.getAlto(); y++) {
                    color = textura1.getColor(x, y);
                    color2 = textura2.getColor(textura2.getAncho() * x / textura1.getAncho(), textura2.getAlto() * y / textura1.getAlto());
                    color3 = color.add(color2);
                    bufferSalida.setQColor(bufferSalida.getAncho() * x / textura1.getAncho(), bufferSalida.getAlto() * y / textura1.getAlto(), color3);
                }
            }
        } catch (Exception e) {

        }
//        bufferSalida.actualizarTextura();
    }

}
