/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import net.qoopo.engine3d.core.material.basico.QMaterialBas;

/**
 *
 * @author alberto
 */
public class QCilindro
        extends QPrisma //        extends QForma 
{

    private float radio;
//    private float alto;
    private int secciones;

    public QCilindro() {
        material = new QMaterialBas("Cilindro");
        nombre = "Cilindro";
        radio = 1;
        alto = 1;
        secciones = 36;
        construir();
    }

    public QCilindro(float alto, float radio) {
        material = new QMaterialBas("Cilindro");
        nombre = "Cilindro";
        this.radio = radio;
        this.alto = alto;
        secciones = 36;
        construir();
    }

    public QCilindro(float alto, float radio, int secciones) {
        material = new QMaterialBas("Cilindro");
        nombre = "Cilindro";
        this.radio = radio;
        this.alto = alto;
        this.secciones = secciones;
        construir();
    }

    @Override
    public void construir() {
        eliminarDatos();
        super.setSecciones(secciones);
        super.setSeccionesVerticales(3);
        super.setRadioSuperior(radio);
        super.setRadioInferior(radio);
        super.setAlto(alto);
        super.construir();

//primer paso generar vertices
//        this.agregarVertice(0, alto / 2, 0, 0, 1);
//        this.agregarVertice(radio, alto / 2, 0, 0, 1);
//        this.agregarVertice(radio, -alto / 2, 0, 0, 0);
//        this.agregarVertice(0, -alto / 2, 0, 0, 0);
//        QGenerador.generarRevolucion(this, secciones, false, false, true, false);
//        QUtilNormales.calcularNormales(this);
//        QMaterialUtil.aplicarMaterial(this, material);
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public float getAlto() {
        return alto;
    }

    public void setAlto(float alto) {
        this.alto = alto;
    }

    public int getSecciones() {
        return secciones;
    }

    public void setSecciones(int secciones) {
        this.secciones = secciones;
    }

}
