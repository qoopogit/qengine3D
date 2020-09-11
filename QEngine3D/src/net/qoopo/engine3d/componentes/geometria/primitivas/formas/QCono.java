/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.util.generador.QGenerador;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;

/**
 *
 * @author alberto
 */
public class QCono extends QForma {

    private float radio;
    private float alto;
    private int secciones;

    public QCono() {
        material = new QMaterialBas("Cono");
        nombre = "Cono";
        radio = 1;
        alto = 1;
        secciones = 36;
        construir();
    }

    public QCono(float alto, float radio) {
        nombre = "Cono";
        material = new QMaterialBas("Cono");
        this.radio = radio;
        this.alto = alto;
        secciones = 36;
        construir();
    }

    public QCono(float alto, float radio, int secciones) {
        nombre = "Cono";
        material = new QMaterialBas("Cono");
        this.radio = radio;
        this.alto = alto;
        this.secciones = secciones;
        construir();
    }

    @Override
    public void construir() {
        eliminarDatos();
        //primer paso generar vertices
        this.agregarVertice(0, alto / 2, 0, 0, 1);
        this.agregarVertice(radio, -alto / 2, 0, 0, 0);
        this.agregarVertice(0, -alto / 2, 0, 0, 0);
        QGenerador.generarRevolucion(this, secciones);
       for(int i=0;i<this.listaPrimitivas.length;i+=2)
       {
           ((QPoligono)listaPrimitivas[i]).setSmooth(true);
       }
//        QGenerador.generarRevolucion(this, secciones, false, false, true, true);
        QUtilNormales.calcularNormales(this);
        QMaterialUtil.aplicarMaterial(this, material);
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
