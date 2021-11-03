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

    private float umbral = 0.7f;

    public QProcesadorBrillo(int ancho, int alto) {
        bufferSalida = new QTextura(ancho, alto);
    }

    public QProcesadorBrillo(int ancho, int alto, float umbral) {
        bufferSalida = new QTextura(ancho, alto);
        this.umbral = umbral;
    }

    @Override
    public void procesar(QTextura... buffer) {
        QColor color;
        float brillo;
        QTextura textura = buffer[0];
        try {
            for (int x = 0; x < textura.getAncho(); x++) {
                for (int y = 0; y < textura.getAlto(); y++) {
                    color = textura.getColor(x, y);
                    brillo = color.r * 0.2126f + color.g * 0.7152f + color.b * 0.0722f;
                    color = color.scale(brillo);
                    if (brillo < umbral) {
                        color = QColor.BLACK;
                    }
                    bufferSalida.setQColor(bufferSalida.getAncho() * x / textura.getAncho(), bufferSalida.getAlto() * y / textura.getAlto(), color);
                }
            }
        } catch (Exception e) {

        }
//        bufferSalida.actualizarTextura();
    }

    public float getUmbral() {
        return umbral;
    }

    public void setUmbral(float umbral) {
        this.umbral = umbral;
    }

}
