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
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;

/**
 * Crea un Plano
 *
 * @author alberto
 */
public class QPlano extends QForma {

    private float alto;
    private float ancho;

    public QPlano(float alto, float ancho) {
        nombre = "Plano";
        this.alto = alto;
        this.ancho = ancho;
        material = new QMaterialBas("Plano");
        construir();
    }

    @Override
    public void construir() {
        try {
            eliminarDatos();

//primer paso generar vertices
            this.agregarVertice(-ancho / 2, 0, -alto / 2, 0, 1); //primer vertice superiro
            this.agregarVertice(ancho / 2, 0, -alto / 2, 1, 1); //tercer vertice superior
            this.agregarVertice(ancho / 2, 0, alto / 2, 1, 0); //cuarto vertice superio
            this.agregarVertice(-ancho / 2, 0, alto / 2, 0, 0); //segundo vertice superior        

//segundo paso generar caras
            this.agregarPoligono(material, 3, 2, 1, 0);//superior
            QMaterialUtil.aplicarMaterial(this, material);
            QUtilNormales.calcularNormales(this);
        } catch (Exception ex) {
            Logger.getLogger(QPlano.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public float getAlto() {
        return alto;
    }

    public void setAlto(float alto) {
        this.alto = alto;
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

}
