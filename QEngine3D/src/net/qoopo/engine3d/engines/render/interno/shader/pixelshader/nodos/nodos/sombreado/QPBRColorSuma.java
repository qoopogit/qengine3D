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

/**
 *
 * @author alberto
 */
public class QPBRColorSuma extends QNodoPBR {

    private QPerColor enColor1;
    private QPerColor enColor2;
    private QPerColor saColor;

    public QPBRColorSuma() {
        enColor1 = new QPerColor(QColor.WHITE);
        enColor1.setNodo(this);
        enColor2 = new QPerColor(QColor.WHITE);
        enColor2.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enColor1);
        entradas.add(enColor2);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    public QPBRColorSuma(QColor color1, QColor color2) {
        enColor1 = new QPerColor(color1);
        enColor2 = new QPerColor(color2);

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

        if (render.opciones.material //esta activada la opción de material
                ) {
            //toma el color de entrada        
            enColor1.procesarEnlaces(render, pixel);
            enColor2.procesarEnlaces(render, pixel);

            saColor.setColor(new QColor(
                    enColor1.getColor().a + enColor2.getColor().a,
                    enColor1.getColor().r + enColor2.getColor().r,
                    enColor1.getColor().g + enColor2.getColor().g,
                    enColor1.getColor().b + enColor2.getColor().b));
        }
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
