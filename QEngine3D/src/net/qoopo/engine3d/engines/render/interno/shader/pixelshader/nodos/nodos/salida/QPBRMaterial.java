/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QNodoPBR;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;

/**
 * Este nodo debe ser la salida o nodo raiz del material
 *
 * @author alberto
 */
public class QPBRMaterial extends QNodoPBR {

    private QPerColor saColor;
    private QPerColor enColor;

    public QPBRMaterial() {
        enColor = new QPerColor(QColor.WHITE);
        enColor.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enColor);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    @Override
    public void procesar(QMotorRender render, QPixel pixel) {
        //esta activada la opción de material
        if (render.opciones.material) {
            enColor.procesarEnlaces(render, pixel);
            saColor.setColor(enColor.getColor());
        }

    }

    public QPerColor getSaColor() {
        return saColor;
    }

    public void setSaColor(QPerColor saColor) {
        this.saColor = saColor;
    }

    public QPerColor getEnColor() {
        return enColor;
    }

    public void setEnColor(QPerColor enColor) {
        this.enColor = enColor;
    }

}
