/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.sombras.procesadores;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.engines.render.interno.sombras.renders.QRenderSombras;

/**
 * Procesador de sombras para luces puntuales. Crea un mapa cúbico
 *
 * @author alberto
 */
public class QSombraOmnidireccional extends QProcesadorSombra {

    /**
     * Se realiza un Mapeo cubico, 6 renderizadores de sombras uno por cada lado
     * del cubo y 6 mapa de sombras
     */
    private QRenderSombras[] renderSombras;
    private QVector3[] direcciones;

    public QSombraOmnidireccional(QEscena mundo, QLuzPuntual luz, QCamara camaraRender, int ancho, int alto) {
        super(mundo, luz, ancho, alto);
        direcciones = new QVector3[6];
        //-------------------------------------------------------
        direcciones[0] = new QVector3(0, 1, 0);  //arriba
        direcciones[1] = new QVector3(0, -1, 0); //abajo
        direcciones[2] = new QVector3(0, 0, 1);  //adelante
        direcciones[3] = new QVector3(0, 0, -1); //atras
        direcciones[4] = new QVector3(-1, 0, 0); //izquierda
        direcciones[5] = new QVector3(1, 0, 0);  //derecha

        QVector3[] direccionesArriba = new QVector3[6];
        direccionesArriba[0] = new QVector3(0, 0, -1);  //arriba
        direccionesArriba[1] = new QVector3(0, 0, 1); //abajo
        direccionesArriba[2] = new QVector3(0, 1, 0);  //adelante
        direccionesArriba[3] = new QVector3(0, 1, 0); //atras
        direccionesArriba[4] = new QVector3(0, 1, 0); //izquierda
        direccionesArriba[5] = new QVector3(0, 1, 0);  //derecha

        String[] nombres = {"Arriba", "Abajo", "Frente", "Atras", "Izquierda", "Derecha"};
        renderSombras = new QRenderSombras[6];
        for (int i = 0; i < 6; i++) {
            renderSombras[i] = new QRenderSombras(QRenderSombras.NO_DIRECCIONALES, mundo, luz, camaraRender, ancho, alto, direcciones[i], direccionesArriba[i]);
            renderSombras[i].setNombre(nombres[i]);
            renderSombras[i].resize();
            renderSombras[i].limpiar();
        }
    }

    @Override
    public float factorSombra(QVector3 vector, QEntidad entidad) {
        float factor = 1.0f;
        float factorTmp = 0;
        int c = 0;
        /**
         * Tenemos un inconveniente. Cuando el punto no esta en el lado del
         * cubo, el factor viene como 1 indicando que debe iluminarse, sin
         * embargo no deberiamos tomarlo en cuenta pues sabemos que 1 significa
         * q no esta en ese lado y no deberia iluminarse
         */

//        System.out.println("");
//        System.out.println("");
//        System.out.println("");
//        System.out.println("voy a tomar los valores para un punto");
        try {
            for (int i = 0; i < 6; i++) {
                factorTmp = renderSombras[i].factorSombra(vector, entidad);
                if (factorTmp != 1.0f) {
                    c++;
                    factor = factorTmp;
                }
//                factor += factorTmp;
//                System.out.println("I=" + i + " dio factor:" + factorTmp);
//                if (factorTmp != 1.0f) {
//                    c++;
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println("los puntos q me dieron diferentes valores son " + c);
        return factor;
//        return factor / 6.0f;
//        return factor / c;
    }

    @Override
    public void generarSombras() {
        if (actualizar || dinamico) {
            for (int i = 0; i < 6; i++) {
                try {
                    renderSombras[i].update();
                } catch (Exception e) {

                }
            }
        }
        actualizar = false;
    }

    @Override
    public void destruir() {
        renderSombras = null;
    }

}
