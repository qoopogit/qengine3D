/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur;

import java.awt.image.BufferedImage;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;

/**
 * Realiza una modificación del color final de la imagen realzando el contraste
 *
 * @author alberto
 */
public class QProcesadorBlur extends QPostProceso {

//    private static float[] pesos = {0.00598f, 0.060626f, 0.241843f, 0.383103f, 0.241843f, 0.060626f, 0.00598f};
//11
    //con sigma 2
//    private static float[] pesos = {0.0093f, 0.028002f, 0.065984f, 0.121703f, 0.175713f, 0.198596f, 0.175713f, 0.121703f, 0.065984f, 0.028002f, 0.0093f};
    //con sigma 4
//    private static float[] pesos = {0.055037f,	0.072806f,	0.090506f,	0.105726f,	0.116061f,	0.119726f,	0.116061f,	0.105726f,	0.090506f,	0.072806f,	0.055037f};
    //con sigma 10
    private static final float[] PESOS = {0.084264f, 0.088139f, 0.091276f, 0.093585f, 0.094998f, 0.095474f, 0.094998f, 0.093585f, 0.091276f, 0.088139f, 0.084264f};
    private static final int KERNEL_SIZE = 11;
//    private static final int MITAD = KERNEL_SIZE / 2;
    private static final int MITAD = 5;
    private float ESCALA = 0.25f;
    private int repeticiones = 1;
    private int ancho = -1, alto = -1;

    //31
//    private static float[] pesos = {0.000001f, 0.000003f, 0.000012f, 0.000048f, 0.000169f, 0.000538f, 0.001532f, 0.003906f, 0.00892f, 0.018246f, 0.033431f, 0.054865f, 0.080656f, 0.106209f, 0.125279f, 0.132368f, 0.125279f, 0.106209f, 0.080656f, 0.054865f, 0.033431f, 0.018246f, 0.00892f, 0.003906f, 0.001532f, 0.000538f, 0.000169f, 0.000048f, 0.000012f, 0.000003f, 0.000001f};
//    private static int kernel_size = 31;
//    private static int mitad = 15;
    private boolean usarEscala = false;

    public QProcesadorBlur(float escala, int repeticiones) {
        this.ESCALA = escala;
        this.repeticiones = repeticiones;
        usarEscala = true;
    }

    public QProcesadorBlur(int ancho, int alto, int repeticiones) {
        this.ancho = ancho;
        this.alto = alto;
        this.repeticiones = repeticiones;
        usarEscala = false;
    }

    @Override
    public void procesar(QTextura... buffer) {
        try {
            bufferSalida = new QTextura();
            bufferSalida.cargarTextura(buffer[0].getImagen());
            if (usarEscala) {
                ancho = (int) (bufferSalida.getAncho() * ESCALA);
                alto = (int) (bufferSalida.getAlto() * ESCALA);
            }
            for (int i = 1; i <= repeticiones; i++) {
                bufferSalida = transpuestodHBlur(transpuestodHBlur(bufferSalida, ancho, alto), alto, ancho);
            }
        } catch (Exception e) {

        }
    }

    //https://stackoverflow.com/questions/43743998/how-to-make-smooth-blur-effect-in-java
    public static QTextura transpuestodHBlur(QTextura textura, int ancho, int alto) {
        // Resultado es transpuesot, asi que el ancho/alto estan cambiados
        QTextura salida = new QTextura(alto, ancho);
        QColor pixel = new QColor();
        // horizontal blur, transpose result
        for (int y = 0; y < textura.getAlto(); y++) {
            for (int x = 0; x < textura.getAncho() ; x++) {
                pixel.set(1, 0, 0, 0);
                for (int dx = 0; dx < KERNEL_SIZE; dx++) {
                    pixel = pixel.add(textura.getColor(x + dx - MITAD, y).scale(PESOS[dx]));
                }
                // transpose result!
                salida.setQColor((salida.getAncho() * y) / textura.getAlto(), (salida.getAlto() * x) / textura.getAncho(), pixel);
            }
        }
        return salida;
    }

    public static BufferedImage transposedHBlur(BufferedImage im) {
        int height = im.getHeight();
        int width = im.getWidth();
        // result is transposed, so the width/height are swapped
        BufferedImage temp = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        // horizontal blur, transpose result
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float r = 0, g = 0, b = 0;
                for (int i = 0; i < KERNEL_SIZE; i++) {
                    if (x + i - MITAD > 0 && x + i - MITAD < width) {
                        int pixel = im.getRGB(x + i - MITAD, y);
                        b += (pixel & 0xFF) * PESOS[i];
                        g += ((pixel >> 8) & 0xFF) * PESOS[i];
                        r += ((pixel >> 16) & 0xFF) * PESOS[i];
                    }
                }
                int p = (int) b + ((int) g << 8) + ((int) r << 16);
                // transpose result!
                temp.setRGB(y, x, p);
            }
        }

        return temp;
    }

//    public static BufferedImage transposedHBlur(BufferedImage im) {
//        int height = im.getHeight();
//        int width = im.getWidth();
//        // result is transposed, so the width/height are swapped
//        BufferedImage temp = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
//        float[] k = {0.00598f, 0.060626f, 0.241843f, 0.383103f, 0.241843f, 0.060626f, 0.00598f};
//        // horizontal blur, transpose result
//        for (int y = 0; y < height; y++) {
//            for (int x = 3; x < width - 3; x++) {
//                float r = 0, g = 0, b = 0;
//                for (int i = 0; i < 7; i++) {
//                    int pixel = im.getRGB(x + i - 3, y);
//                    b += (pixel & 0xFF) * k[i];
//                    g += ((pixel >> 8) & 0xFF) * k[i];
//                    r += ((pixel >> 16) & 0xFF) * k[i];
//                }
//                int p = (int) b + ((int) g << 8) + ((int) r << 16);
//                // transpose result!
//                temp.setRGB(y, x, p);
//            }
//        }
//        return temp;
//    }
}
