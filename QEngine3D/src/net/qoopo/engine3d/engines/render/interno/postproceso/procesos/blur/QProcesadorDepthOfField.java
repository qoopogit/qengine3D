/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur;

import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;
import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;

/**
 * Realiza un efecto blur solo a ciertas partes d ela imagen cuando cumple una
 * distancia
 *
 * @author alberto
 */
public class QProcesadorDepthOfField extends QPostProceso {

    private static float[] pesos = {0.084264f, 0.088139f, 0.091276f, 0.093585f, 0.094998f, 0.095474f, 0.094998f, 0.093585f, 0.091276f, 0.088139f, 0.084264f};
    private static int kernel_size = 11;
    private static int mitad = 5;
    private float escala = 0.25f;
    private int repeticiones = 1;
    private float distanciaFocal = 0.5f;//de 0 a 1;
    public static int DESENFOQUE_LEJOS = 1;// desenfoca lo que esta mas lejos de la distancia
    public static int DESENFOQUE_CERCA = 2; // desenfoa lo que esta mas ceca de la distancia
    public static int DESENFOQUE_EXCLUYENTE = 3;// desenfoca lo que no este en la distancia
    private int tipo = DESENFOQUE_LEJOS;
    private static float margen = 0.05f;

    public QProcesadorDepthOfField(float distanciaFocal, int tipoDesenfoque, float escala, int repeticiones) {
        this.distanciaFocal = distanciaFocal;
        this.tipo = tipoDesenfoque;
        this.escala = escala;
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

        buffer.calcularMaximosMinimosZBuffer();
//        float mitadBuffer = (buffer.getMaximo()+ buffer.getMinimo()) / 2.0f;
        // horizontal blur, transpose result
        for (int y = 0; y < height; y++) {
            for (int x = mitad; x < width - mitad; x++) {
                if ( //                        buffer.getZBuffer(y, x) > mitadBuffer
                        (tipo == DESENFOQUE_LEJOS && buffer.getZBuffer(y, x) / buffer.getMaximo() >= distanciaFocal)
                        || (tipo == DESENFOQUE_CERCA && buffer.getZBuffer(y, x) / buffer.getMaximo() <= distanciaFocal)
                        || (tipo == DESENFOQUE_EXCLUYENTE && Math.abs(buffer.getZBuffer(y, x) / buffer.getMaximo() - distanciaFocal) > margen)) {
                    pixel.set(1, 0, 0, 0);
                    for (int i = 0; i < kernel_size; i++) {
                        pixel = pixel.add(buffer.getColor(x + i - mitad, y).scale(pesos[i]));
                    }
                } else {
                    pixel = buffer.getColor(x, y);//no realiza el efecto blur
                }
                // transpose result!
                bufferReturn.setQColor((int) (y * escala), (int) (x * escala), pixel);
                //setea la profundidad
                bufferReturn.setZBuffer(x, y, buffer.getZBuffer(y, x));
            }
        }

        /*
        temp = transposedHBlur(input);
        result = transposedHBlur(temp);
         */
        return bufferReturn;
    }
//https://stackoverflow.com/questions/43743998/how-to-make-smooth-blur-effect-in-java

//    public  BufferedImage transposedHBlur(BufferedImage im) {
//        int height = im.getHeight();
//        int width = im.getWidth();
//        // result is transposed, so the width/height are swapped
//        BufferedImage temp = new BufferedImage((int) (height * escala), (int) (width * escala), BufferedImage.TYPE_INT_RGB);
//
//        // horizontal blur, transpose result
//        for (int y = 0; y < height; y++) {
//            for (int x = mitad; x < width - mitad; x++) {
//                float r = 0, g = 0, b = 0;
//                for (int i = 0; i < kernel_size; i++) {
//                    int pixel = im.getRGB(x + i - mitad, y);
//                    b += (pixel & 0xFF) * pesos[i];
//                    g += ((pixel >> 8) & 0xFF) * pesos[i];
//                    r += ((pixel >> 16) & 0xFF) * pesos[i];
//                }
//                int p = (int) b + ((int) g << 8) + ((int) r << 16);
//                // transpose result!
//                temp.setRGB((int) (y * escala), (int) (x * escala), p);
//            }
//        }
//
//        /*
//        
//        temp = transposedHBlur(input);
//result = transposedHBlur(temp);
//         */
//        return temp;
//    }
}
