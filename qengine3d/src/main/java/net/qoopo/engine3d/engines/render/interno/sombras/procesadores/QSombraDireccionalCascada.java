/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.sombras.procesadores;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.engines.render.interno.sombras.renders.QRenderSombras;

/**
 * Procesador de sombras en cascada para Luces Direccionales.
 *
 * @author alberto
 */
public class QSombraDireccionalCascada extends QProcesadorSombra {

    //varios rende de sombras
    private QRenderSombras[] renderSombra;
    private int numeroCascadas = 3;

    public QSombraDireccionalCascada(int numeroCascadas, QEscena mundo, QLuzDireccional luz, QCamara camaraRender, int ancho, int alto) {
        super(mundo, luz, ancho, alto);
        this.numeroCascadas = numeroCascadas;
        renderSombra = new QRenderSombras[numeroCascadas];
        for (int i = 0; i < numeroCascadas; i++) {
            try {
                renderSombra[i] = new QRenderSombras(QRenderSombras.DIRECIONALES, mundo, luz, camaraRender, ancho, alto);
                renderSombra[i].setCascada(true);
                renderSombra[i].setCascada_indice(i + 1);
//                renderSombra[i].setCascada_indice(i);
                renderSombra[i].setCascada_tamanio(numeroCascadas);
                renderSombra[i].setNombre("Cascada " + (i + 1));
                renderSombra[i].resize();
                renderSombra[i].limpiar();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public float factorSombra(QVector3 vector, QEntidad entidad) {
        float factor = 0;
//        float tmp = 0;
//        int n = 0;
        try {
            for (int i = 0; i < numeroCascadas; i++) {
                factor += renderSombra[i].factorSombra(vector, entidad);
//                if (vector.z < renderSombra[i].getDistanciaCascada()) {
//                factor = renderSombra[i].factorSombra(vector, entidad);
//                if (factor > 0) {
//                    break;
//                }
//                tmp = renderSombra[i].factorSombra(vector, entidad);
//                factor += tmp;
//                if (tmp > 0) {
//                    n++;
//                }
            }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        return factor / n;
        return factor / numeroCascadas;
//        return factor;
    }

    @Override
    public void generarSombras() {
        try {
            if (actualizar || dinamico) {
                for (int i = 0; i < numeroCascadas; i++) {
                    renderSombra[i].update();
                }
            }
            actualizar = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void actualizarDireccion(QVector3 direccion) {
        try {
            for (int i = 0; i < numeroCascadas; i++) {
                renderSombra[i].setDireccion(direccion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destruir() {
        renderSombra = null;
    }

}
