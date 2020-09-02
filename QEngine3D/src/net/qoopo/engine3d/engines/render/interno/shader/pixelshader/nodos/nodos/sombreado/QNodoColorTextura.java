/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QShaderNodo;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerImagen;

/**
 *
 * @author alberto
 */
public class QNodoColorTextura extends QShaderNodo {

    private QPerColor saColor;
    private QPerImagen enImagen;

    public QNodoColorTextura(QProcesadorTextura textura) {
        enImagen = new QPerImagen(textura);
        enImagen.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enImagen);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    @Override
    public void procesar(QMotorRender render, QPixel pixel) {
        if (render.opciones.isMaterial()) {
            saColor.setColor(enImagen.getTextura().get_QARGB(pixel.u, pixel.v));
        }
    }

    public QPerColor getSaColor() {
        return saColor;
    }

    public void setSaColor(QPerColor saColor) {
        this.saColor = saColor;
    }

    public QPerImagen getEnImagen() {
        return enImagen;
    }

    public void setEnImagen(QPerImagen enImagen) {
        this.enImagen = enImagen;
    }

}
