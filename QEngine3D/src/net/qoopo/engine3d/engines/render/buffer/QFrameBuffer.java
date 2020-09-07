/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.buffer;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.QTextura;

/**
 * Framebuffer Utiliza una textura como buffer de color
 *
 * @author alberto
 */
public class QFrameBuffer {

    //este buffer adicional tiene información de material, entidad (transformación y demas) por cada pixel
    // no tiene información de color
    protected final QPixel[][] pixelBuffer;

    // este buffer es el de color que se llena despues de procesar los pixeles
    private final QTextura bufferColor;
    //buffer de profundidad
    protected float[][] zBuffer;
    private float minimo = 0, maximo = 0;
    private int ancho, alto;
    //esta textura si es diferente de nulo, dibujamos sobre ella tambien
    protected QTextura textura;

    public QFrameBuffer(int ancho, int alto, QTextura texturaSalida) {
        this.ancho = ancho;
        this.alto = alto;
        zBuffer = new float[ancho][alto];
        bufferColor = new QTextura(ancho, alto);
        pixelBuffer = new QPixel[ancho][alto];
        for (QPixel[] row : pixelBuffer) {
            for (int i = 0; i < row.length; i++) {
                row[i] = new QPixel();
            }
        }
        this.textura = texturaSalida;
    }

    /**
     * Actualiza el bufferedimage y carga a la textura de salida
     */
    public void actualizarTextura() {
        if (textura != null) {
            textura.cargarTextura(bufferColor.getQImagen());
        }
    }

    public QPixel[][] getPixelBuffer() {
        return pixelBuffer;
    }

    public BufferedImage getRendered() {
        return bufferColor.getImagen();
    }

    public float[][] getzBuffer() {
        return zBuffer;
    }

    public void setzBuffer(float[][] zBuffer) {
        this.zBuffer = zBuffer;
    }

    /**
     * Limpia el buffer de profundidad
     */
    public void limpiarZBuffer() {
        for (float[] row : zBuffer) {
            Arrays.fill(row, Float.POSITIVE_INFINITY);
        }
    }

    /**
     * Llena el buffer de color con el color solicitado
     *
     * @param color
     */
    public void llenarColor(QColor color) {
        bufferColor.llenarColor(color);
    }

    public QPixel getPixel(int x, int y) {
        try {
            return pixelBuffer[x][y];
        } catch (Exception e) {
            return null;
        }
    }

    public float getZBuffer(int x, int y) {
        if (x < zBuffer.length && y < zBuffer[0].length) {
            return zBuffer[x][y];
        } else {
            return Float.POSITIVE_INFINITY;
        }
    }

    public void setZBuffer(int x, int y, float valor) {
        zBuffer[x][y] = valor;
    }

    public void setRGB(int x, int y, float r, float g, float b) {
        setQColor(x, y, new QColor(r, g, b));
    }

    public QColor getColor(int x, int y) {
        return bufferColor.getColor(x, y);
    }

    public void setQColor(int x, int y, QColor color) {
        bufferColor.setQColor(x, y, color);
    }

    public QColor getColorNormalizado(float x, float y) {
        return bufferColor.getQColor(x, -y);
    }

    public void setQColorNormalizado(float x, float y, QColor color) {
        bufferColor.setQColor(x, -y, color);
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public void calcularMaximosMinimosZBuffer() {
        minimo = Float.POSITIVE_INFINITY;
        maximo = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < zBuffer.length; i++) {
            for (int j = 0; j < zBuffer[0].length; j++) {
                if (zBuffer[i][j] > maximo && zBuffer[i][j] != Float.POSITIVE_INFINITY) {
                    maximo = zBuffer[i][j];
                }
                if (zBuffer[i][j] < minimo && zBuffer[i][j] != Float.NEGATIVE_INFINITY) {
                    minimo = zBuffer[i][j];
                }
            }
        }
    }

    /**
     * Pinta el mapa de profundidad en lugar de los colores rgb.Lo pinta en
     * escala de grises
     *
     * @param farPlane diastancia maxima de la camara
     */
    public void pintarMapaProfundidad(float farPlane) {
        try {
            float r = 0, g = 0, b = 0;
            for (int x = 0; x < zBuffer.length; x++) {
                for (int y = 0; y < zBuffer[0].length; y++) {
//                    b = g = r = ((zBuffer[y][x] - minimo) / maximo);
                    b = g = r = (zBuffer[x][y] / farPlane);
                    setRGB(x, y, r, g, b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QTextura getTextura() {
        return textura;
    }

    public void setTextura(QTextura textura) {
        this.textura = textura;
    }

    public float getMinimo() {
        return minimo;
    }

    public void setMinimo(float minimo) {
        this.minimo = minimo;
    }

    public float getMaximo() {
        return maximo;
    }

    public void setMaximo(float maximo) {
        this.maximo = maximo;
    }

    /**
     * Realiza una copia de un buffer a otro con dimensiones diferentes
     *
     * @param buffer
     * @param ancho
     * @param alto
     * @return
     */
    public static QFrameBuffer copiar(QFrameBuffer buffer, int ancho, int alto) {
//        QFrameBuffer nuevo = new QFrameBuffer(ancho, alto, buffer.getTextura());
        QFrameBuffer nuevo = new QFrameBuffer(ancho, alto, null);
        QColor color;
        try {
            //si es expandir
            if (buffer.getAncho() < ancho) {
                for (int x = 0; x < nuevo.getAncho(); x++) {
                    for (int y = 0; y < nuevo.getAlto(); y++) {
                        color = buffer.getColorNormalizado((float) x / nuevo.getAncho(), (float) y / nuevo.getAlto());
                        nuevo.setQColor(x, y, color);
                    }
                }
            } else {
                //si es encojer
                for (int x = 0; x < buffer.getAncho(); x++) {
                    for (int y = 0; y < buffer.getAlto(); y++) {
                        color = buffer.getColor(x, y);
                        nuevo.setQColorNormalizado((float) x / buffer.getAncho(), (float) y / buffer.getAlto(), color);
                    }
                }
            }
        } catch (Exception e) {

        }

        return nuevo;
    }

}
