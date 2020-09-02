/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QShaderNodo;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerFlotante;

/**
 *
 * @author alberto
 */
public class QNodoColorMix extends QShaderNodo {

    private QPerFlotante enfactor;
    private QPerColor enColor1;
    private QPerColor enColor2;
    private QPerColor saColor;

    public QNodoColorMix() {
        enColor1 = new QPerColor(QColor.WHITE);
        enColor1.setNodo(this);
        enColor2 = new QPerColor(QColor.WHITE);
        enColor2.setNodo(this);
        enfactor = new QPerFlotante(0.5f);
        enfactor.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enColor1);
        entradas.add(enColor2);
        entradas.add(enfactor);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    public QNodoColorMix(float factor) {
        this();
        enfactor.setValor(factor);
    }

    public QNodoColorMix(QColor color1, QColor color2, float factor) {
        enColor1 = new QPerColor(color1);
        enColor2 = new QPerColor(color2);
        enfactor = new QPerFlotante(factor);
        enfactor.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        enColor1.setNodo(this);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enColor1);
        entradas.add(enColor2);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    @Override
    public void procesar(QMotorRender render, QPixel pixel) {
        if (render.opciones.isMaterial()) {
            //toma el color de entrada        
            enColor1.procesarEnlaces(render, pixel);
            enColor2.procesarEnlaces(render, pixel);
            QColor color = QMath.mix(enColor1.getColor(), enColor2.getColor(), enfactor.getValor());
            saColor.setColor(color);
        }
    }

    public QPerFlotante getEnfactor() {
        return enfactor;
    }

    public void setEnfactor(QPerFlotante enfactor) {
        this.enfactor = enfactor;
    }

    public QPerColor getEnColor1() {
        return enColor1;
    }

    public void setEnColor1(QPerColor enColor1) {
        this.enColor1 = enColor1;
    }

    public QPerColor getEnColor2() {
        return enColor2;
    }

    public void setEnColor2(QPerColor enColor2) {
        this.enColor2 = enColor2;
    }

    public QPerColor getSaColor() {
        return saColor;
    }

    public void setSaColor(QPerColor saColor) {
        this.saColor = saColor;
    }

}
