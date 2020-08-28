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
import net.qoopo.engine3d.core.math.QVector2;

/**
 * Mezcla 2 texturas de acuerdo a la razon dada ademas de un mapa de ruido
 *
 * @author alberto
 */
public class QProcesadorMixAgua extends QProcesadorTextura {

    private QTextura texturaA;
    private QTextura texturaB;
    private QTextura texturaDistorcion;
    private QTextura textura;

    private float razon = 0;
    private float fuerzaOla = 0.02f;
    private float factorTiempo = 0.0f;

    public QProcesadorMixAgua(QTextura texturaA, QTextura texturaB, QTextura distorcion) {
        this.texturaA = texturaA;
        this.texturaB = texturaB;
        this.texturaDistorcion = distorcion;
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
//       QVector2 distor1 = texturaDistorcion.getQColor(x + factorTiempo, y).rg().multiply(2).subtract(1).multiply(fuerzaOla);
//        QVector2 distor2 = texturaDistorcion.getQColor(-x + factorTiempo, y + factorTiempo).rg().multiply(2).subtract(1).multiply(fuerzaOla);
//        QVector2 distorTotal = distor1.add(distor2);
    //-----------------------------------------
//        QVector2 distor = texturaDistorcion.getQColor(x + factorTiempo, y).rg().multiply(0.1f);
//        distor.setXY(x+distor.x, y+distor.y+factorTiempo);
//        QVector2 distorTotal = texturaDistorcion.getQColor(distor.x, distor.y).rg().multiply(2).subtract(1).multiply(fuerzaOla);

    @Override
    public QColor get_QARGB(float x, float y) {
        QVector2 distor = texturaDistorcion.getQColor(x + factorTiempo, y).rg().multiply(0.1f);
        distor.setXY(x + distor.x, y + distor.y + factorTiempo);
        QVector2 distorTotal = texturaDistorcion.getQColor(distor.x, distor.y).rg().multiply(2).subtract(1).multiply(fuerzaOla);
        x += distorTotal.x;
        y += distorTotal.y;
        x = QMath.clamp(x, 0.001f, 0.999f);
        y = QMath.clamp(y, -0.999f, -0.001f);
        return QMath.mix(texturaA.getQColor(x, y), texturaB.getQColor(x, y), razon);
    }

    @Override
    public float getNormalX(float x, float y) {
        QVector2 distor = texturaDistorcion.getQColor(x + factorTiempo, y).rg().multiply(0.1f);
        distor.setXY(x + distor.x, y + distor.y + factorTiempo);
        QVector2 distorTotal = texturaDistorcion.getQColor(distor.x, distor.y).rg().multiply(2).subtract(1).multiply(fuerzaOla);
        x += distorTotal.x;
        y += distorTotal.y;
        x = QMath.clamp(x, 0.001f, 0.999f);
        y = QMath.clamp(y, -0.999f, -0.001f);
        return QMath.mix(texturaA.getNormalX(x, y), texturaB.getNormalX(x, y), razon);
    }

    @Override
    public float getNormalY(float x, float y) {
        QVector2 distor = texturaDistorcion.getQColor(x + factorTiempo, y).rg().multiply(0.1f);
        distor.setXY(x + distor.x, y + distor.y + factorTiempo);
        QVector2 distorTotal = texturaDistorcion.getQColor(distor.x, distor.y).rg().multiply(2).subtract(1).multiply(fuerzaOla);
        x += distorTotal.x;
        y += distorTotal.y;
        x = QMath.clamp(x, 0.001f, 0.999f);
        y = QMath.clamp(y, -0.999f, -0.001f);
        return QMath.mix(texturaA.getNormalY(x, y), texturaB.getNormalY(x, y), razon);
    }

    @Override
    public float getNormalZ(float x, float y) {
        QVector2 distor = texturaDistorcion.getQColor(x + factorTiempo, y).rg().multiply(0.1f);
        distor.setXY(x + distor.x, y + distor.y + factorTiempo);
        QVector2 distorTotal = texturaDistorcion.getQColor(distor.x, distor.y).rg().multiply(2).subtract(1).multiply(fuerzaOla);
        x += distorTotal.x;
        y += distorTotal.y;
        x = QMath.clamp(x, 0.001f, 0.999f);
        y = QMath.clamp(y, -0.999f, -0.001f);
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

    public QTextura getTexturaDistorcion() {
        return texturaDistorcion;
    }

    public void setTexturaDistorcion(QTextura texturaDistorcion) {
        this.texturaDistorcion = texturaDistorcion;
    }

    public float getFuerzaOla() {
        return fuerzaOla;
    }

    public void setFuerzaOla(float fuerzaOla) {
        this.fuerzaOla = fuerzaOla;
    }

    public float getFactorTiempo() {
        return factorTiempo;
    }

    public void setFactorTiempo(float factorTiempo) {
        this.factorTiempo = factorTiempo;
    }

}
