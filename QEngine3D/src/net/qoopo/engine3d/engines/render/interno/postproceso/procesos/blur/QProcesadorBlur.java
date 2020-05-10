/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur;

import java.awt.image.BufferedImage;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;
import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;

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
    private static float[] pesos = {0.084264f, 0.088139f, 0.091276f, 0.093585f, 0.094998f, 0.095474f, 0.094998f, 0.093585f, 0.091276f, 0.088139f, 0.084264f};

    private static int kernel_size = 11;
    private static int mitad = 5;
    private static float escala = 0.25f;
    private int repeticiones = 1;

    //31
//    private static float[] pesos = {0.000001f, 0.000003f, 0.000012f, 0.000048f, 0.000169f, 0.000538f, 0.001532f, 0.003906f, 0.00892f, 0.018246f, 0.033431f, 0.054865f, 0.080656f, 0.106209f, 0.125279f, 0.132368f, 0.125279f, 0.106209f, 0.080656f, 0.054865f, 0.033431f, 0.018246f, 0.00892f, 0.003906f, 0.001532f, 0.000538f, 0.000169f, 0.000048f, 0.000012f, 0.000003f, 0.000001f};
//    private static int kernel_size = 31;
//    private static int mitad = 15;
    public QProcesadorBlur(float escala, int repeticiones) {
        QProcesadorBlur.escala = escala;
        this.repeticiones = repeticiones;
    }

    @Override
    public void procesar(QFrameBuffer... buffer) {

        bufferSalida = buffer[0];
        for (int i = 1; i <= repeticiones; i++) {
            bufferSalida = transposedHBlur(transposedHBlur(bufferSalida));
        }
        bufferSalida.actualizarTextura();
    }

    private QFrameBuffer transposedHBlur(QFrameBuffer buffer) {
        int height = buffer.getAlto();
        int width = buffer.getAncho();

        int nuevoAncho = (int) (height * escala);
        int nuevoAlto = (int) (width * escala);
        if (nuevoAncho <= 0) {
            nuevoAncho = 1;
        }
        if (nuevoAlto <= 0) {
            nuevoAlto = 1;
        }

        // result is transposed, so the width/height are swapped
//        System.out.println("ancho=" + nuevoAncho + " alto=" + nuevoAlto);
        QFrameBuffer bufferReturn = new QFrameBuffer(nuevoAncho, nuevoAlto, null);
        QColor pixel = new QColor();

        // horizontal blur, transpose result
        for (int y = 0; y < height; y++) {
            for (int x = mitad; x < width - mitad; x++) {
                pixel.set(1, 0, 0, 0);
                for (int i = 0; i < kernel_size; i++) {
                    pixel = pixel.add(buffer.getColor(x + i - mitad, y).scale(pesos[i]));
                }
                // transpose result!
                bufferReturn.setQColor((int) (y * escala), (int) (x * escala), pixel);
            }
        }

        /*
        temp = transposedHBlur(input);
        result = transposedHBlur(temp);
         */
        return bufferReturn;
    }
//https://stackoverflow.com/questions/43743998/how-to-make-smooth-blur-effect-in-java

    public static BufferedImage transposedHBlur(BufferedImage im) {
        int height = im.getHeight();
        int width = im.getWidth();
        // result is transposed, so the width/height are swapped
        BufferedImage temp = new BufferedImage((int) (height * escala), (int) (width * escala), BufferedImage.TYPE_INT_RGB);

        // horizontal blur, transpose result
        for (int y = 0; y < height; y++) {
            for (int x = mitad; x < width - mitad; x++) {
                float r = 0, g = 0, b = 0;
                for (int i = 0; i < kernel_size; i++) {
                    int pixel = im.getRGB(x + i - mitad, y);
                    b += (pixel & 0xFF) * pesos[i];
                    g += ((pixel >> 8) & 0xFF) * pesos[i];
                    r += ((pixel >> 16) & 0xFF) * pesos[i];
                }
                int p = (int) b + ((int) g << 8) + ((int) r << 16);
                // transpose result!
                temp.setRGB((int) (y * escala), (int) (x * escala), p);
            }
        }

        /*
        
        temp = transposedHBlur(input);
result = transposedHBlur(temp);
         */
        return temp;
    }

}
