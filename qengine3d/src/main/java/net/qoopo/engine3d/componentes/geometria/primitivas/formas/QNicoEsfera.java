/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QUtilNormales;

/**
 * Geoesfera o Icoesfera
 *
 * @author alberto
 */
public class QNicoEsfera extends QForma {

    private float radio;
    private int divisiones = 3;

    public QNicoEsfera() {
        nombre = "NicoEsfera";
        material = new QMaterialBas("NicoEsfera");
        radio = 1;
        construir();
    }

    public QNicoEsfera(float radio) {
        nombre = "NicoEsfera";
        material = new QMaterialBas("NicoEsfera");
        this.radio = radio;
        construir();
    }

    public QNicoEsfera(float radio, int divisiones) {
        nombre = "NicoEsfera";
        material = new QMaterialBas("NicoEsfera");
        this.radio = radio;
        this.divisiones = divisiones;
        construir();
    }

    /**
     * Construye una esfera http://www.songho.ca/opengl/gl_sphere.html
     */
    @Override
    public void construir() {
        eliminarDatos();
        //paso 1.- generar el icosaedro origen
        crearOriginal();
        // ahora armamos las caras
        QUtilNormales.calcularNormales(this);

        //paso 2         
//        for (int i = 0; i < divisiones; i++) {
//            dividir();
//            inflar(radio);
//        }
        dividir(divisiones);
        inflar(radio);

        //el objeto es suavizado
        QMaterialUtil.suavizar(this, true);
//        QMaterialUtil.aplicarMaterial(this, material);
    }

    /**
     * Crea la forma original
     */
    private void crearOriginal() {
        try {
            QMaterialBas blanco = new QMaterialBas("blanco");
            blanco.setColorBase(QColor.WHITE);
            QMaterialBas rojo = new QMaterialBas("rojo");
            rojo.setColorBase(QColor.RED);
            QMaterialBas azul = new QMaterialBas("azul");
            azul.setColorBase(QColor.BLUE);
            QMaterialBas amarillo = new QMaterialBas("amarillo");
            amarillo.setColorBase(QColor.YELLOW);

            agregarVertice(radio / 2, radio / 2, radio / 2);//0
            agregarVertice(-radio / 2, -radio / 2, radio / 2);//1
            agregarVertice(-radio / 2, radio / 2, -radio / 2);//2
            agregarVertice(radio / 2, -radio / 2, -radio / 2);//3
            agregarVertice(-radio / 2, radio / 2, -radio / 2);//4

            agregarPoligono(blanco, 1, 0, 2);
            agregarPoligono(rojo, 0, 1, 3);
            agregarPoligono(amarillo, 3, 4, 0);
            agregarPoligono(azul, 2, 3, 1);
        } catch (Exception ex) {
            Logger.getLogger(QNicoEsfera.class.getName()).log(Level.SEVERE, null, ex);
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
