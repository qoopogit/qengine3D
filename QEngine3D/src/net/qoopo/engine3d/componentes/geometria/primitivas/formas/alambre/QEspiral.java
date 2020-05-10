/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas.alambre;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;

/**
 *
 * @author alberto
 */
public class QEspiral extends QForma {

    private float radio;// el radio de la espiral
    private float largo; // el largo de la espiral
    private float nSegmentos;// el numero de segmentos

    public QEspiral(float radio, float largo, float nSegmentos) {
        nombre = "Espiral";
        material = new QMaterialBas("Espiral");
        this.radio = radio;
        this.largo = largo;
        this.nSegmentos = nSegmentos;
        tipo = QGeometria.GEOMETRY_TYPE_WIRE;
        construir();
    }

    @Override
    public void construir() {
        eliminarDatos();

        double delta = largo / nSegmentos;
        for (int i = 0; i < nSegmentos + 1; i++) {
//            transformables.add(new Segment(new Vertex(i * delta, 0, 0), new Vertex((i + 1) * delta, 0, 0)));
            this.agregarVertice((float) (i * delta), 0, 0);
        }
        for (int i = 0; i < nSegmentos - 1; i++) {
            try {
                this.agregarLinea(material, i, i + 1);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

//        for (int i = 0; i < this.listaVertices.length; i++) {
//            calculateSpirale(listaVertices[i], radio, largo);
//        }
    }

    private void calculateSpirale(QVertice v, double r, double h) {
        double x;
        //save old x value
        x = v.ubicacion.x;

        //calculate
        v.ubicacion.x = (float) (h * x / Math.PI);
        v.ubicacion.y = (float) (r * Math.cos(x));
        v.ubicacion.z = (float) (r * Math.sin(x));
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public float getLargo() {
        return largo;
    }

    public void setLargo(float largo) {
        this.largo = largo;
    }

    public float getnSegmentos() {
        return nSegmentos;
    }

    public void setnSegmentos(float nSegmentos) {
        this.nSegmentos = nSegmentos;
    }

}
