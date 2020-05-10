/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura.procesador;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;

/**
 * Mezcla 2 texturas de acuerdo a la razon dada
 *
 * @author alberto
 */
public class QProcesadorMix extends QProcesadorTextura {

    private QTextura texturaA;
    private QTextura texturaB;
    private QTextura textura;

    private float razon = 0;

    public QProcesadorMix(QTextura texturaA, QTextura texturaB) {
        this.texturaA = texturaA;
        this.texturaB = texturaB;
        textura = new QTextura();
    }

    @Override
    public void procesar() {

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
        return QMath.mix(texturaA.getQColor(x, y), texturaB.getQColor(x, y), razon);
    }

    @Override
    public float getNormalX(float x, float y) {
        return QMath.mix(texturaA.getNormalX(x, y), texturaB.getNormalX(x, y), razon);
    }

    @Override
    public float getNormalY(float x, float y) {
        return QMath.mix(texturaA.getNormalY(x, y), texturaB.getNormalY(x, y), razon);
    }

    @Override
    public float getNormalZ(float x, float y) {
        return QMath.mix(texturaA.getNormalZ(x, y), texturaB.getNormalZ(x, y), razon);
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
