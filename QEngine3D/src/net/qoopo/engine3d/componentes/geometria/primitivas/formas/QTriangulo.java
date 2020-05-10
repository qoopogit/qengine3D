/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;

/**
 *
 * @author alberto
 */
public class QTriangulo extends QForma {

    private float lado;

    public QTriangulo(float lado) {
        nombre = "Triangulo";
        material = new QMaterialBas("Triángulo");
        this.lado = lado;
        construir();
    }

    @Override
    public void construir() {
        try {
            eliminarDatos();
            /*
            P1
            /\
            /  \
            /    \
            P2/_____ \P3
             */
            //primer paso generar vertices
            this.agregarVertice(0, lado, 0, 0.5f, 1);
            this.agregarVertice(-lado / 2, 0, 0, 0, 0);
            this.agregarVertice(lado / 2, 0, 0, 1, 0);

            //coordenadas UV
//        UVCoordinate[] coordenadasCara = new UVCoordinate[3];
//        //segundo paso generar caras
////        coordenadasCara[0] = new UVCoordinate(0, 0);
//        coordenadasCara[0] = new UVCoordinate(0.5f, 0);
//        coordenadasCara[1] = new UVCoordinate(0, 1);
//        coordenadasCara[2] = new UVCoordinate(1, 1);
            this.agregarPoligono(material, 0, 1, 2);
            this.agregarPoligono(material, 0, 2, 1);

            QUtilNormales.calcularNormales(this);
        } catch (Exception ex) {
            Logger.getLogger(QTriangulo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public float getLado() {
        return lado;
    }

    public void setLado(float lado) {
        this.lado = lado;
    }

}
