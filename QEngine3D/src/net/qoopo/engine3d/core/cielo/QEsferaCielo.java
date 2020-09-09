/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.cielo;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorMix;

/**
 *
 * @author alberto
 */
public class QEsferaCielo extends QCielo {

    private QTextura texturaDia;
    private QTextura texturaNoche;
    private float radio;
    private QProcesadorMix procesadorTextura;

    public QEsferaCielo(QTextura texturaDia, QTextura texturaNoche, float radio) {
        this.texturaDia = texturaDia;
        this.texturaNoche = texturaNoche;
        this.radio = radio;
        construir();
    }

    private void construir() {
        entidad = new QEntidad("Cielo");
        QGeometria cieloG = new QEsfera(radio, 32);
//        QGeometria cieloG = new QEsfera(radio, 8);
//        QGeometria cieloG = new QEsfera(radio, 4);
        QMaterialBas material = new QMaterialBas();
        material.setFactorEmision(1);//finge emision de luz para no ser afectado por las luces
        procesadorTextura = new QProcesadorMix(texturaDia, texturaNoche);
        procesadorTextura.setRazon(0);
        material.setMapaColor(procesadorTextura);
        QMaterialUtil.aplicarMaterial(cieloG, material);
        QUtilNormales.invertirNormales(cieloG);
        entidad.agregarComponente(cieloG);
        System.out.println("entidad del cielo construida");
    }

    @Override
    public void setRazon(float razon) {
        procesadorTextura.setRazon(razon);
        procesadorTextura.procesar();
    }

    /**
     * actualiza el color del cielo en funcion de la hora
     */
    private void atualizarCielo() {

    }

    public QTextura getTexturaDia() {
        return texturaDia;
    }

    public void setTexturaDia(QTextura texturaDia) {
        this.texturaDia = texturaDia;
    }

    public QTextura getTexturaNoche() {
        return texturaNoche;
    }

    public void setTexturaNoche(QTextura texturaNoche) {
        this.texturaNoche = texturaNoche;
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public QProcesadorMix getProcesadorTextura() {
        return procesadorTextura;
    }

    public void setProcesadorTextura(QProcesadorMix procesadorTextura) {
        this.procesadorTextura = procesadorTextura;
    }

}
