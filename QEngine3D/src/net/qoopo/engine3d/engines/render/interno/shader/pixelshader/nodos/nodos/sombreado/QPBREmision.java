/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QNodoPBR;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerFlotante;

/**
 *
 * @author alberto
 */
public class QPBREmision extends QNodoPBR {

    private QPerColor saColor;
    private QPerColor enColor;
    private QPerFlotante enIntensidad;

    public QPBREmision(QColor color, float intensidad) {
        enColor = new QPerColor(color);
        enColor.setNodo(this);
        enIntensidad = new QPerFlotante(intensidad);
        enIntensidad.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enColor);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    @Override
    public void procesar(QMotorRender render, QPixel pixel) {
        if (render.opciones.material //esta activada la opción de material
                ) {
            enColor.procesarEnlaces(render, pixel);
            enIntensidad.procesarEnlaces(render, pixel);
            saColor.setColor(enColor.getColor().scale(enIntensidad.getValor()));
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

    public QPerFlotante getEnIntensidad() {
        return enIntensidad;
    }

    public void setEnIntensidad(QPerFlotante enIntensidad) {
        this.enIntensidad = enIntensidad;
    }

}
