/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.util.generador.QGenerador;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;

/**
 *
 * @author alberto
 */
public class QCilindroZ extends QForma {

    private float radio;
    private float alto;
    private int secciones;

    public QCilindroZ() {
        material = new QMaterialBas("Cilindro");
        nombre = "CilindroZ";
        radio = 1;
        alto = 1;
        secciones = 36;
        construir();
    }

    public QCilindroZ(float alto, float radio) {
        material = new QMaterialBas("Cilindro");
        nombre = "CilindroZ";
        this.radio = radio;
        this.alto = alto;
        secciones = 36;
        construir();
    }

    public QCilindroZ(float alto, float radio, int secciones) {
        material = new QMaterialBas("Cilindro");
        nombre = "CilindroZ";
        this.radio = radio;
        this.alto = alto;
        this.secciones = secciones;
        construir();
    }

    @Override
    public void construir() {
        eliminarDatos();

//primer paso generar vertices
        this.agregarVertice(0, alto / 2, 0);
        this.agregarVertice(radio, alto / 2, 0);
        this.agregarVertice(radio, -alto / 2, 0);
        this.agregarVertice(0, -alto / 2, 0);
        QGenerador.generarRevolucion(this, secciones);
        QUtilNormales.calcularNormales(this);
        QMaterialUtil.aplicarMaterial(this, material);
        //ahora roto los vertices para que se alineen al eje x
        QTransformacion tra = new QTransformacion();
        tra.getRotacion().rotarX((float) Math.toRadians(90));
        tra.getRotacion().actualizarCuaternion();
        QVector3 tmp = new QVector3();
        for (QVertice vertice : listaVertices) {
            tmp.setXYZ(vertice.ubicacion.x, vertice.ubicacion.y, vertice.ubicacion.z);
            tmp = tra.toTransformMatriz().mult(tmp);
            vertice.setXYZ(tmp.x, tmp.y, tmp.z);
        }
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
