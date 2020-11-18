/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QUtilNormales;

/**
 * Crea una esfera a partir de un cubo
 *
 * @author alberto
 */
public class QCuboEsfera extends QCaja {

    private float radio = 1.0f;
    private int divisiones = 3;

    public QCuboEsfera() {
        super(0.5f);
        material = new QMaterialBas("CuboEsfera");
        construir();
    }

    public QCuboEsfera(float radio) {
        super(radio);
        this.radio = radio;
        material = new QMaterialBas("CuboEsfera");
        construir();
    }

    public QCuboEsfera(float radio, int divisiones) {
        super(radio);
        this.radio = radio;
        this.divisiones = divisiones;
        material = new QMaterialBas("CuboEsfera");
        construir();
    }

    @Override
    public void construir() {
        try {
            setAlto(radio);
            setAncho(radio);
            setLargo(radio);
            super.construir();
            QUtilNormales.calcularNormales(this);
            dividir(divisiones);
            inflar(radio);
            //el objeto es suavizado
            QMaterialUtil.suavizar(this, true);
            QMaterialUtil.aplicarMaterial(this, material);
        } catch (Exception ex) {
            Logger.getLogger(QCuboEsfera.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public int getDivisiones() {
        return divisiones;
    }

    public void setDivisiones(int divisiones) {
        this.divisiones = divisiones;
    }

}
