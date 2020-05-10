/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QNodoPBR;

/**
 *
 * @author alberto
 */
public class QNodoPeriferico {

    protected QNodoPBR nodo;//el nodo al que pertenece

    protected List<QNodoEnlace> enlaces = new ArrayList<>();

    public QNodoPBR getNodo() {
        return nodo;
    }

    public boolean tieneEnlaces() {
        return getEnlaces() != null && !getEnlaces().isEmpty();
    }

    public void setNodo(QNodoPBR nodo) {
        this.nodo = nodo;
    }

    public List<QNodoEnlace> getEnlaces() {
        return enlaces;
    }

    public void setEnlaces(List<QNodoEnlace> enlaces) {
        this.enlaces = enlaces;
    }

    /**
     * Si tiene enlaces (cuando es de entrada) procesa las entradas y toma los
     * valores de salida
     *
     * @param render
     * @param pixel
     */
    public void procesarEnlaces(QMotorRender render, QPixel pixel) {
        if (enlaces != null && !enlaces.isEmpty()) {
            for (QNodoEnlace enlace : enlaces) {
                //si yo soy un periferico de entrada, debería estar en la salida del enlace
                if (enlace.getSalida().equals(this)) {
                    enlace.getEntrada().getNodo().procesar(render, pixel);
                }
            }
        }
    }
}
