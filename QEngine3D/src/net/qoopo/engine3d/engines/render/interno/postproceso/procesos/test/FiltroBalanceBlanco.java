/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.test;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class FiltroBalanceBlanco implements Filtro {

    public int porcentajeRuido = 1;

    public FiltroBalanceBlanco() {
    }

    public FiltroBalanceBlanco(int porcentajeRuido) {
        this.porcentajeRuido = porcentajeRuido;
    }

    public BufferedImage filtrar(BufferedImage bi) {
        BufferedImage biDestino
                = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
                        .createCompatibleImage(bi.getWidth(), bi.getHeight(), Transparency.OPAQUE);
        int histograma[][] = new int[3][256];
        int total = 0;
        //Calcula los histogramas de cada canal
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                Color c1 = new Color(bi.getRGB(x, y));
                int rgb[] = {
                    c1.getRed(),
                    c1.getGreen(),
                    c1.getBlue()
                };
                for (int canal = 0; canal < 3; canal++) {
                    histograma[canal][rgb[canal]]++;
                }
                total++;
            }
        }

        //Obtiene el tono mínimo y máximo eliminando el N% de puntos (por posibles ruidos en la foto).
        int min[] = {-1, -1, -1};
        int acumMin[] = {0, 0, 0};
        int max[] = {-1, -1, -1};
        int acumMax[] = {0, 0, 0};
        for (int canal = 0; canal < 3; canal++) {
            for (int tono = 0; tono < 256; tono++) {
                if (min[canal] == -1) {
                    acumMin[canal] += histograma[canal][tono];
                    if (acumMin[canal] > total * porcentajeRuido / 100) {
                        min[canal] = tono;
                    }
                }
                if (max[canal] == -1) {
                    acumMax[canal] += histograma[canal][255 - tono];
                    if (acumMax[canal] > total * porcentajeRuido / 100) {
                        max[canal] = 255 - tono;
                    }
                }
            }
        }

        //Genera el nuevo color estirando el 96% de los tonos en toda la imagen.
        for (int x = 0; x < bi.getWidth(); x++) {
            for (int y = 0; y < bi.getHeight(); y++) {
                Color c1 = new Color(bi.getRGB(x, y));
                int rgb[] = {
                    c1.getRed(),
                    c1.getGreen(),
                    c1.getBlue()
                };
                for (int canal = 0; canal < 3; canal++) {
                    double factor = 256.0 / (max[canal] - min[canal]);
                    rgb[canal] = (int) ((rgb[canal] - min[canal]) * factor);
                    rgb[canal] = Math.min(255, Math.max(0, rgb[canal]));
                }
                biDestino.setRGB(x, y, new Color(rgb[0], rgb[1], rgb[2]).getRGB());
            }
        }
        return biDestino;
    }
}
