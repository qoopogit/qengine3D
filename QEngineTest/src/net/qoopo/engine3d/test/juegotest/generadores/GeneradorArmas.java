/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest.generadores;

import java.io.File;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;

/**
 * Clase para crear personajes del juego
 *
 * @author alberto
 */
public class GeneradorArmas {

    public boolean shift = false;

    public static QEntidad crearAK47() {

//        QGeometria geometria = CargaEstatica.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/ARMAS/AK47_Free/ak_body/aknewlow.obj")).get(0);
//
//        QEntidad arma = new QEntidad("pistola");
//        arma.agregarComponente(geometria);
//        return arma;
        QEntidad arma = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/ARMAS/AK47_Free/ak_body/aknewlow.obj")).get(0);
        arma.setNombre("Pistola");
        return arma;
    }

}
