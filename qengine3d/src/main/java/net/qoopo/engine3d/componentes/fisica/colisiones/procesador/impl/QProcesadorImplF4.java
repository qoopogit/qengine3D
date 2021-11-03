/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.procesador.impl;

import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.colisiones.procesador.QProcesadorColision;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QProcesadorImplF4 extends QProcesadorColision {

    @Override
    public void procesarColision(QObjetoRigido obj1, QObjetoRigido obj2) {
        QObjetoRigido objeto1 = obj1;
        QObjetoRigido objeto2 = obj2;

        //metodo 3-- calculo la fuerza de colision elastica (http://acer.forestales.upm.es/basicas/udfisica/asignaturas/fisica/dinamsist/colisiones.html)
        // usando el ejemplo visto en el curso de fisica
        QVector3 vI1 = objeto1.velocidadLinear.clone();
        QVector3 vI2 = objeto2.velocidadLinear.clone();

        QVector3 fM1V1 = vI1.clone().multiply(objeto1.getMasa());

        QVector3 fM2V2 = vI2.clone().multiply(objeto2.getMasa());

        QVector3 fM2V1 = vI1.clone().multiply(objeto2.getMasa());
//            QVector3 fM2V1 = vI2.clone().multiply(objeto1.getMasa());

        /*
                vpp1 es la velocidad resultande despues de la colision
                vpp2 es la velocidad resultante depus de la colision 
         */
        //       float  vpp1 = (m1 * v1 + m2 * v2 - m2 * v1 + m2 * v2) / (1 * m1 + 1 * m2);
        //         float vpp2 = v1 - v2 + (m1 * v1 + m2 * v2 - m2 * v1 + m2 * v2) / (1 * m1 + 1 * m2);
        //CALCULO PARA EL OBJETO 1
        QVector3 velocidadLineal1 = fM1V1.clone().add(fM2V2, fM2V1.clone().multiply(-1f), fM2V2);
        velocidadLineal1.multiply(1 / (objeto1.getMasa() + objeto2.getMasa()));

        //calculo para el objeto 2
        QVector3 velocidadLineal2 = vI1.clone().add(vI2.clone().multiply(-1f), velocidadLineal1.clone());

        //aplico las velocidades nuevas
        if (objeto1.tipoDinamico == QObjetoDinamico.DINAMICO) {
            objeto1.velocidadLinear = velocidadLineal1.clone();
        }
        if (objeto2.tipoDinamico == QObjetoDinamico.DINAMICO) {
            objeto2.velocidadLinear = velocidadLineal2.clone();
        }

    }
}
