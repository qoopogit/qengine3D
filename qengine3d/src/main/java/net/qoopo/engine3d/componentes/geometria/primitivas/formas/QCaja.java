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
import net.qoopo.engine3d.core.util.QUtilNormales;

/**
 *
 * @author alberto
 */
public class QCaja extends QForma {

    //variables usadas para tener los valores anterior y solo construir en caso de que cambie
//    private float anchoAnt;
//    private float altoAnt;
//    private float largoAnt;
    private float ancho;
    private float alto;
    private float largo;

    public QCaja() {
        material = new QMaterialBas("Caja");
        nombre = "Caja";
        ancho = 1;
        alto = 1;
        largo = 1;
        construir();
    }

    public QCaja(float lado) {
        material = new QMaterialBas("Caja");
        nombre = "Caja";
        this.ancho = lado;
        this.alto = lado;
        this.largo = lado;
        construir();
    }

    public QCaja(float alto, float ancho, float largo) {
        material = new QMaterialBas("Caja");
        nombre = "Caja";
        this.ancho = ancho;
        this.alto = alto;
        this.largo = largo;
        construir();
    }

    @Override
    public void construir() {
        try {
//            if (anchoAnt == ancho && altoAnt == alto && largoAnt == largoAnt) {
//                return;
//            }
            eliminarDatos();

            //Cubo con mapa UV en forma de skybox (14 vertices)
//                  ----------
//                  |         |
//                  |   UP    |
//                  |         |
//        ----------------------------------------
//        |         |         |         |        |
//        | BACK    | LEFT    | FRONT   | RIGHT  |
//        |         |         |         |        |
//        ----------------------------------------
//                  |         |
//                  | DOWN    |
//                  |         |
//                  ---------- 
//
//
///                 0---------1        
//                  |         |
//                  |   UP    |
//                  |         |
//        2---------3---------4---------5--------6
//        |         |         |         |        |
//        | BACK    | LEFT    | FRONT   | RIGHT  |
//        |         |         |         |        |
//        7---------8---------9---------10-------11
//                  |         |
//                  | DOWN    |
//                  |         |
//                  12-------13
//
//primer paso generar vertices
//primera linea
            this.agregarVertice(ancho / 2, alto / 2, -largo / 2, 0.25f, 1.0f);
            this.agregarVertice(ancho / 2, alto / 2, largo / 2, 0.5f, 1.0f);
//segunda linea
            this.agregarVertice(ancho / 2, alto / 2, -largo / 2, 0.f, 0.666f);
            this.agregarVertice(-ancho / 2, alto / 2, -largo / 2, 0.25f, 0.666f);
            this.agregarVertice(-ancho / 2, alto / 2, largo / 2, 0.50f, 0.666f);
            this.agregarVertice(ancho / 2, alto / 2, largo / 2, 0.75f, 0.666f);
            this.agregarVertice(ancho / 2, alto / 2, -largo / 2, 1f, 0.666f);
//tercera linea
            this.agregarVertice(ancho / 2, -alto / 2, -largo / 2, 0.f, 0.333f);
            this.agregarVertice(-ancho / 2, -alto / 2, -largo / 2, 0.25f, 0.333f);
            this.agregarVertice(-ancho / 2, -alto / 2, largo / 2, 0.50f, 0.333f);
            this.agregarVertice(ancho / 2, -alto / 2, largo / 2, 0.75f, 0.333f);
            this.agregarVertice(ancho / 2, -alto / 2, -largo / 2, 1f, 0.333f);
//cuarta linea
            this.agregarVertice(ancho / 2, -alto / 2, -largo / 2, 0.25f, 0);
            this.agregarVertice(ancho / 2, -alto / 2, largo / 2, 0.5f, 0);

//segundo paso generar caras
//cara superior
            this.agregarPoligono(material, 1, 0, 3);
            this.agregarPoligono(material, 3, 4, 1);
//trasera
            this.agregarPoligono(material, 3, 2, 7);
            this.agregarPoligono(material, 7, 8, 3);
//izquierda
            this.agregarPoligono(material, 4, 3, 8);
            this.agregarPoligono(material, 8, 9, 4);
//frente
            this.agregarPoligono(material, 5, 4, 9);
            this.agregarPoligono(material, 9, 10, 5);
//derecha
            this.agregarPoligono(material, 6, 5, 10);
            this.agregarPoligono(material, 10, 11, 6);
//abajo
            this.agregarPoligono(material, 9, 8, 12);
            this.agregarPoligono(material, 12, 13, 9);
//            anchoAnt = ancho;
//            largoAnt = largo;
//            altoAnt = alto;
            QUtilNormales.calcularNormales(this);
        } catch (Exception ex) {
            Logger.getLogger(QCaja.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

    public float getAlto() {
        return alto;
    }

    public void setAlto(float alto) {
        this.alto = alto;
    }

    public float getLargo() {
        return largo;
    }

    public void setLargo(float largo) {
        this.largo = largo;
    }

}
