/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.math;

import java.awt.Color;
import java.io.Serializable;

/**
 * Valores de colores aRGB con limites de 0 a 1 en cada canal
 *
 * @author alberto
 */
public class QColor implements Serializable {

    //Colores constantes
    public static final QColor WHITE = new QColor(Color.WHITE);
    public static final QColor YELLOW = new QColor(Color.YELLOW);
    public static final QColor BLUE = new QColor(Color.BLUE);
    public static final QColor RED = new QColor(Color.RED);
    public static final QColor GREEN = new QColor(Color.GREEN);
    public static final QColor GRAY = new QColor(Color.GRAY);
    public static final QColor CYAN = new QColor(Color.CYAN);
    public static final QColor BLACK = new QColor(Color.BLACK);
    public static final QColor LIGHT_GRAY = new QColor(Color.LIGHT_GRAY);
    public static final QColor MAGENTA = new QColor(Color.MAGENTA);
    public static final QColor PINK = new QColor(Color.PINK);
    public static final QColor DARK_GRAY = new QColor(Color.DARK_GRAY);
    public static final QColor BROWN = new QColor(1, 0.25f, 0.16f, 0.16f);
    public static QColor toQARGB(int rgb) {
//        return new QColor(1,
//                ((rgb >> 16) & 0xFF) / 255,
//                ((rgb >> 8) & 0xFF) / 255,
//                (rgb & 0xFF) / 255
//        );
return new QColor(new Color(rgb));
    }

    public float a, r, g, b;

    public QColor() {
        a = r = g = b = 1.0f;
    }

    public QColor(float r, float g, float b) {
        this.a = 1;
        this.r = valida(r);
        this.g = valida(g);
        this.b = valida(b);
    }

    public QColor(float a, float r, float g, float b) {
        this.a = valida(a);
        this.r = valida(r);
        this.g = valida(g);
        this.b = valida(b);
    }
    public QColor(int rgb) {
        this(new Color(rgb));
    }
    public QColor(Color color) {
        this(color.getAlpha() / 255.0f, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
    }

    public void set(QColor color) {
        this.set(color.a, color.r, color.g, color.b);
    }

    public void set(float a, float r, float g, float b) {
        this.a = valida(a);
        this.r = valida(r);
        this.g = valida(g);
        this.b = valida(b);
    }

    private float valida(float valor) {
        return  Math.max(valor, 0.0f);
    }


    public Color getColor() {        
        return new Color(toARGB());
    }

    public QColor add(QColor rgb) {
        return new QColor(a + rgb.a, r + rgb.r, g + rgb.g, b + rgb.b);
    }

    public QColor addLocal(QColor rgb) {
        a = valida(a + rgb.a);
        r = valida(r + rgb.r);
        g = valida(g + rgb.g);
        b = valida(b + rgb.b);
        return this;
    }

    public QColor add(float valueR, float valueG, float valueB) {
        r = valida(r + valueR);
        g = valida(g + valueG);
        b = valida(b + valueB);
        return this;
    }

    public QColor add(float value) {
        r = valida(r + value);
        g = valida(g + value);
        b = valida(b + value);
        return this;
    }

    public QColor subtract(QColor rgb) {
        return new QColor(a - rgb.a, r - rgb.r, g - rgb.g, b - rgb.b);
    }

    public QColor scale(float scale) {
        return new QColor(a, r * scale, g * scale, b * scale);
    }

    public QColor scaleLocal(float scale) {
        r = valida(r * scale);
        g = valida(g * scale);
        b = valida(b * scale);
        return this;
    }

    public QColor scale(float scaleR, float scaleG, float scaleB) {
        r = valida(r * scaleR);
        g = valida(g * scaleG);
        b = valida(b * scaleB);
        return this;
    }

    public QColor scale(QColor otro) {
        r = valida(r * otro.r);
        g = valida(g * otro.g);
        b = valida(b * otro.b);
        return this;
    }

    public QColor invert() {
        r = valida(1.0f - r);
        g = valida(1.0f - g);
        b = valida(1.0f - b);
        return this;
    }

    private int toInt(float value) {
        return (value < 0.0) ? 0 : (value > 1.0) ? 255 : (int) (value * 255.0);
    }

//    public int toARGB() {
//        return (toInt(a) << 24) | (toInt(r) << 16) | (toInt(g) << 8) | toInt(b);
//    }
//    
    public int toARGB() {
//        return QColor.toARGB(toInt(a), toInt(r), toInt(g), toInt(b));
//        return QColor.toRGB(toInt(r), toInt(g), toInt(b));
        return new Color(toInt(r), toInt(g), toInt(b), toInt(a)).getRGB();
    }

    public int toRGB() {
//        return (toInt(r) << 16) | (toInt(g) << 8) | toInt(b);
        return new Color(toInt(r), toInt(g), toInt(b)).getRGB();
    }


//    public static QColor toQARGB(int argb) {
//        return new QColor(
//                ((argb >> 24) & 0xFF) / 255,
//                ((argb >> 16) & 0xFF) / 255,
//                ((argb >> 8) & 0xFF) / 255,
//                (argb & 0xFF) / 255
//        );
//    }
//    
//    public static int toRGB(int r, int g, int b) {
//        int rgb = r;
//        rgb = (rgb << 8) + g;
//        rgb = (rgb << 8) + b;
//        return rgb;
//    }
//
//    public static int toARGB(int a, int r, int g, int b) {
//        int rgb = a;
//        rgb = (rgb << 8) + r;
//        rgb = (rgb << 8) + g;
//        rgb = (rgb << 8) + b;
//        return rgb;
//    }
    @Override
    public String toString() {
        return "QColor{" + "a=" + a + ", r=" + r + ", g=" + g + ", b=" + b + '}';
    }

    public QColor clone() {
        return new QColor(a, r, g, b);
    }

    public QVector3 rgb() {
        return new QVector3(r, g, b);
    }

    public QVector2 rg() {
        return new QVector2(r, g);
    }

    public QVector2 rb() {
        return new QVector2(r, b);
    }

    public QVector2 gb() {
        return new QVector2(g, b);
    }
}
