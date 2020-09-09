/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import net.qoopo.engine3d.core.math.QColor;
import static net.qoopo.engine3d.core.math.QMath.rotateNumber;

/**
 *
 * @author alberto
 */
public class QTextura implements Serializable {

    private QImagen imagen;

    private int ancho = 0;
    private int alto = 0;

    private float c0, c1, c2, c3, alphaX, alphaY;

    //sera usado para obtener coordenadas diferentes de las solicitadas por el renderer
    // y estos valores seran alterados por modificadores como
    // el procesador de Agua que alterara estos valores para simular movimiento del agua
    //en una superficie plana
    private float offsetX = 0;
    private float offsetY = 0;

    //para poder reflejar la textura
    private int signoX = 1;
    private int signoY = 1;
    // indica cuantas muestras tendr ala textura. Si se requiere que la textura se repita 10 veces en el eje x se coloca muestraU=10.0f
    private float muestrasU = 1.0f;
    private float muestrasV = 1.0f;

    /*
     
      Coordenadas UV, son las coordenadas para mapear la textura sobre un poligono, van de 0 a 1
    
      (0,1) ___________________________ (1,1)
           \                          \
           \                          \
           \                          \
           \                          \
           \                          \
           \                          \
           \                          \
      (0,0) ___________________________ (1,0)
     */
    public QTextura() {
    }

    /**
     * Prepara una textura con las dimensiones especificadas y con el tipo de
     * imagen ARGB
     *
     * @param ancho
     * @param alto
     */
    public QTextura(int ancho, int alto) {
        cargarTextura(new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB));
    }

    /**
     *
     * @param img
     *
     */
    public QTextura(BufferedImage img) {
        cargarTextura(img);
    }

    public void cargarTextura(QImagen img) {
        ancho = img.getAncho();
        alto = img.getAlto();
        this.imagen = img;
    }

    public void cargarTextura(BufferedImage img) {
        try {
            ancho = img.getWidth();
            alto = img.getHeight();
            if (imagen == null) {
                imagen = new QImagen(img);
            } else {
                imagen.setImage(img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Coordenadas normalizadas
     *
     * @param x
     * @param y
     * @return
     */
    public int getARGB(float x, float y) {
        try {
            x *= signoX;
            y *= signoY;
            x += offsetX;
            y += offsetY;
            x *= muestrasU;
            y *= muestrasV;
            return imagen.getPixel(rotateNumber((int) (x * ancho), ancho), rotateNumber((int) ((1 - y) * alto), alto));
        } catch (Exception e) {
            return (0 << 24 + 0 << 16 + 0 << 8 + 0);
        }
    }

    /**
     * Coordenadas normalizadas
     *
     * @param x
     * @param y
     * @return
     */
    public QColor getQColor(float x, float y) {
        try {
            x *= signoX;
            y *= signoY;
            x += offsetX;
            y += offsetY;
            x *= muestrasU;
            y *= muestrasV;
            return imagen.getPixelQARGB(rotateNumber((int) (x * ancho), ancho), rotateNumber((int) ((1 - y) * alto), alto));
        } catch (Exception e) {
            return new QColor((0 << 24) / 255.0f, (0 << 16) / 255.0f, (0 << 8) / 255.0f, 0);
        }
    }

    /**
     * Coordenadas normalizadas
     *
     * @param x
     * @param y
     * @param argb
     */
    public void setARGB(float x, float y, int argb) {
        try {
            x *= signoX;
            y *= signoY;
            x += offsetX;
            y += offsetY;
            x *= muestrasU;
            y *= muestrasV;
            imagen.setPixel(rotateNumber((int) (x * ancho), ancho), rotateNumber((int) ((1 - y) * alto), alto), argb);
        } catch (Exception e) {

        }
    }

    /**
     * Coordenadas normalizadas
     *
     * @param x
     * @param y
     * @param color
     */
    public void setQColor(float x, float y, QColor color) {
        try {
            x *= signoX;
            y *= signoY;
            x += offsetX;
            y += offsetY;
            x *= muestrasU;
            y *= muestrasV;
            imagen.setPixel(rotateNumber((int) (x * ancho), ancho), rotateNumber((int) ((1 - y) * alto), alto), color);
        } catch (Exception e) {

        }
    }

    /**
     * Coordenadas reales
     *
     * @param x
     * @param y
     * @param color
     */
    public void setQColor(int x, int y, QColor color) {
        try {
            imagen.setPixel(x, y, color);
        } catch (Exception e) {

        }
    }

    /**
     * Coordenadas reales
     *
     * @param x
     * @param y
     * @return
     */
    public QColor getColor(int x, int y) {
        try {
            return imagen.getPixelQARGB(x, y);
        } catch (Exception e) {
            return new QColor((0 << 24) / 255.0f, (0 << 16) / 255.0f, (0 << 8) / 255.0f, 0);
        }
    }

    public float getA(float x, float y) {
        return getQColor(x, y).a;
    }

    public float getR(float x, float y) {
        return getQColor(x, y).r;
    }

    public float getG(float x, float y) {
        return getQColor(x, y).g;
    }

    public float getB(float x, float y) {
        return getQColor(x, y).b;
    }

    public float getRInterpolated(float x, float y) {
        x *= signoX;
        y *= signoY;
        x += offsetX;
        y += offsetY;
        c0 = getR(x, y);
        c1 = getR(x + 1f / ancho, y);
        c2 = getR(x + 1f / ancho, y + 1f / alto);
        c3 = getR(x, y + 1f / alto);
        alphaX = (x * ancho) % 1;
        alphaY = (y * alto) % 1;
        return (1 - alphaY) * (alphaX * c1 + (1 - alphaX) * c0) + alphaY * (alphaX * c2 + (1 - alphaX) * c3);
    }

    public float getGInterpolated(float x, float y) {
        x *= signoX;
        y *= signoY;
        x += offsetX;
        y += offsetY;
        c0 = getG(x, y);
        c1 = getG(x + 1f / ancho, y);
        c2 = getG(x + 1f / ancho, y + 1f / alto);
        c3 = getG(x, y + 1f / alto);
        alphaX = (x * ancho) % 1;
        alphaY = (y * alto) % 1;
        return (1 - alphaY) * (alphaX * c1 + (1 - alphaX) * c0) + alphaY * (alphaX * c2 + (1 - alphaX) * c3);
    }

    public float getBInterpolated(float x, float y) {
        x *= signoX;
        y *= signoY;
        x += offsetX;
        y += offsetY;
        c0 = getB(x, y);
        c1 = getB(x + 1f / ancho, y);
        c2 = getB(x + 1f / ancho, y + 1f / alto);
        c3 = getB(x, y + 1f / alto);
        alphaX = (x * ancho) % 1;
        alphaY = (y * alto) % 1;
        return (1 - alphaY) * (alphaX * c1 + (1 - alphaX) * c0) + alphaY * (alphaX * c2 + (1 - alphaX) * c3);
    }

    public float getNormalX(float x, float y) {
        return getR(x, y);
    }

    public float getNormalY(float x, float y) {
        return getG(x, y);
    }

    public float getNormalZ(float x, float y) {
        return getB(x, y);
    }

    public float getNormalXInterpolated(float x, float y) {
        c0 = getNormalX(x, y);
        c1 = getNormalX(x + 1f / ancho, y);
        c2 = getNormalX(x + 1f / ancho, y + 1f / alto);
        c3 = getNormalX(x, y + 1f / alto);
        alphaX = (x * ancho) % 1;
        alphaY = (y * ancho) % 1;
        return (1 - alphaY) * (alphaX * c1 + (1 - alphaX) * c0) + alphaY * (alphaX * c2 + (1 - alphaX) * c3);
    }

    public float getNormalYInterpolated(float x, float y) {
        x *= signoX;
        y *= signoY;
        x += offsetX;
        y += offsetY;
        c0 = getNormalY(x, y);
        c1 = getNormalY(x + 1f / ancho, y);
        c2 = getNormalY(x + 1f / ancho, y + 1f / alto);
        c3 = getNormalY(x, y + 1f / alto);
        alphaX = (x * ancho) % 1;
        alphaY = (y * ancho) % 1;
        return (1 - alphaY) * (alphaX * c1 + (1 - alphaX) * c0) + alphaY * (alphaX * c2 + (1 - alphaX) * c3);
    }

    public float getNormalZInterpolated(float x, float y) {
        x *= signoX;
        y *= signoY;
        x += offsetX;
        y += offsetY;
        c0 = getNormalZ(x, y);
        c1 = getNormalZ(x + 1f / ancho, y);
        c2 = getNormalZ(x + 1f / ancho, y + 1f / alto);
        c3 = getNormalZ(x, y + 1f / alto);
        alphaX = (x * ancho) % 1;
        alphaY = (y * ancho) % 1;
        return (1 - alphaY) * (alphaX * c1 + (1 - alphaX) * c0) + alphaY * (alphaX * c2 + (1 - alphaX) * c3);
    }

    public void setQImagen(QImagen imagen) {
        this.imagen = imagen;
    }

    public QImagen getQImagen() {
        return imagen;
    }

    public BufferedImage getImagen() {
        if (imagen == null) {
            return null;
        }
        return imagen.getBi();
    }

    public BufferedImage getImagen(Dimension size) {
        if (imagen == null) {
            return null;
        }
        if (size == null) {
            return getImagen();//devuelve la imagen original y sin escalar
        }
        BufferedImage newImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        QColor pix = null;
        for (int y = 0; y < newImage.getHeight(); y++) {
            for (int x = 0; x < newImage.getWidth(); x++) {
                float newX = (float) x / size.width;
                float newY = (float) y / size.height;
//                pix = getColor(newX, newY);
                pix = getQColor(newX, 1.0f - newY);//la imagen estaba saliendo invertida por el orden inverso de las coordenadas uv en el eje y
                newImage.setRGB(x, y, pix.toARGB());
            }
        }
        return newImage;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public int getSignoX() {
        return signoX;
    }

    public void setSignoX(int signoX) {
        this.signoX = signoX;
    }

    public int getSignoY() {
        return signoY;
    }

    public void setSignoY(int signoY) {
        this.signoY = signoY;
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

    public void destruir() {
        imagen.destruir();
        imagen = null;
    }

    public void llenarColor(QColor color) {
        imagen.llenarColor(color);
    }

    public float getMuestrasU() {
        return muestrasU;
    }

    public void setMuestrasU(float muestrasU) {
        this.muestrasU = muestrasU;
    }

    public float getMuestrasV() {
        return muestrasV;
    }

    public void setMuestrasV(float muestrasV) {
        this.muestrasV = muestrasV;
    }

}
