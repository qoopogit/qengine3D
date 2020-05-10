/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura.procesador;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.math.QColor;

/**
 * Mezcla 2 texturas de acuerdo a la razon dada
 *
 * @author alberto
 */
public class QProcesadorMixAnterior extends QProcesadorTextura {

    private QTextura texturaA;
    private QTextura texturaB;
    private QTextura textura;
    private BufferedImage imageA;
    private BufferedImage imageB;
    private BufferedImage image;

    private float razon = 0;

    public QProcesadorMixAnterior(QTextura texturaA, QTextura texturaB) {
        this.texturaA = texturaA;
        this.texturaB = texturaB;
        textura = new QTextura();
        actualizarImagenes();
    }

    public void actualizarImagenes() {
        imageA = texturaA.getImagen(new Dimension(texturaA.getAncho(), texturaA.getAlto()));
//        imageB = texturaB.getImagen(new Dimension(texturaB.getWidth(), texturaB.getHeight()));
        imageB = texturaB.getImagen(new Dimension(texturaA.getAncho(), texturaA.getAlto()));
        image = texturaA.getImagen(new Dimension(texturaA.getAncho(), texturaA.getAlto()));

        procesar();
    }

    private void resetesarImage() {
        if (image != null && imageA != null) {
            image.getGraphics().drawImage(imageA, 0, 0, null);
        }
    }

    @Override
    public void procesar() {
        try {
            resetesarImage();
            Graphics2D g2d = image.createGraphics();
            g2d.setComposite(AlphaComposite.SrcOver.derive(razon));
            g2d.drawImage(imageB, 0, 0, null);
            g2d.dispose();
            textura.cargarTextura(image);
        } catch (Exception e) {

        }
    }

    public QTextura getTexturaA() {
        return texturaA;
    }

    public void setTexturaA(QTextura texturaA) {
        this.texturaA = texturaA;
    }

    public QTextura getTexturaB() {
        return texturaB;
    }

    public void setTexturaB(QTextura texturaB) {
        this.texturaB = texturaB;
    }

    public float getRazon() {
        return razon;
    }

    public void setRazon(float razon) {
        this.razon = razon;
    }

    @Override
    public int get_ARGB(float x, float y) {
        return textura.getARGB(x, y);
    }

    @Override
    public QColor get_QARGB(float x, float y) {
        return textura.getQColor(x, y);
    }

    @Override
    public float getNormalX(float x, float y) {
        return textura.getNormalX(x, y);
    }

    @Override
    public float getNormalY(float x, float y) {
        return textura.getNormalY(x, y);
    }

    @Override
    public float getNormalZ(float x, float y) {

        return textura.getNormalZ(x, y);
    }

    @Override
    public BufferedImage getTexture(Dimension size) {
        return textura.getImagen(size);
    }

    @Override
    public BufferedImage getTexture() {
        return textura.getImagen();
    }

    @Override
    public void destruir() {
        image = null;
        imageA = null;
        imageB = null;
        if (textura != null) {
            textura.destruir();
            textura = null;
        }

        if (texturaA != null) {
            texturaA.destruir();
            texturaA = null;
        }

        if (texturaB != null) {
            texturaB.destruir();
            texturaB = null;
        }
    }

    @Override
    public void setMuestrasU(float muestras) {
    }

    @Override
    public void setMuestrasV(float muestras) {
    }

    public QTextura getTextura() {
        return textura;
    }

    public void setTextura(QTextura textura) {
        this.textura = textura;
    }

}
