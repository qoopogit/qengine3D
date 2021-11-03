/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest.generadores;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindro;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCono;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.modificadores.particulas.humo.QEmisorHumo;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.math.QColor;

/**
 * Clase para crear personajes del juego
 *
 * @author alberto
 */
public class GeneradorCasas {

    private static final QTextura TEXT_MURO = QGestorRecursos.cargarTextura("muro", QGlobal.RECURSOS + "texturas/muro/muro2.jpg");

//    private static QGeometria 
    private static QMaterialBas materialPisos = new QMaterialBas(TEXT_MURO, 0);
    private static QGeometria pisoG = QMaterialUtil.aplicarMaterial(new QCaja(2.5f, 5f, 5f), materialPisos);
    private static QGeometria ventanaG = QMaterialUtil.aplicarColor(new QPlano(0.5f, 0.75f), 0.8f, 1, 1, 0, 1, 1, 1, 1, 64);
    private static QGeometria puertaG = QMaterialUtil.aplicarColor(new QPlano(1f, 0.5f), 1f, QColor.GREEN, QColor.WHITE, 0, 64);
    private static QGeometria techoG = QMaterialUtil.aplicarColor(new QCono(1, 5f, 4), 1, new QColor(1, 139f / 255f, 99f / 255f, 55f / 255f), QColor.GREEN, 0, 0);
    private static QGeometria chimeneaG = QMaterialUtil.aplicarColor(new QCilindro(0.6f, 0.25f, 4), 1, QColor.GRAY, QColor.GRAY, 0, 0);

    public QEntidad casa1() {
        QEntidad objeto = new QEntidad("Casa 1P");
        QEntidad piso1 = piso1();
        QEntidad techo = techo();
        techo.mover(0, 1.25f + 0.5f, 0);
        objeto.agregarHijo(piso1);
        objeto.agregarHijo(techo);
        return objeto;
    }

    public QEntidad casa2Pisos() {
        QEntidad objeto = new QEntidad("Casa 2P");
        QEntidad piso1 = piso1();
        QEntidad piso2 = piso();
        piso2.mover(0, 1.25f + 1.25f, 0);
        QEntidad techo = techo();
        techo.mover(0, 1.25f + 0.5f + 2.5f, 0);
        objeto.agregarHijo(piso1);
        objeto.agregarHijo(piso2);
        objeto.agregarHijo(techo);
        return objeto;
    }

    private QEntidad piso() {
        QEntidad piso = new QEntidad();

        piso.agregarComponente(pisoG);

        QEntidad ventana1 = new QEntidad("ventana");
        ventana1.agregarComponente(ventanaG);
        ventana1.agregarComponente(new QLuzPuntual(0.5f, QColor.YELLOW, 10, false, false));
        ventana1.mover(-1.5f, 0.5f, 2.51f);
        piso.agregarHijo(ventana1);

        QEntidad ventana2 = new QEntidad("ventana");
        ventana2.agregarComponente(ventanaG);
        ventana2.agregarComponente(new QLuzPuntual(0.5f, QColor.YELLOW, 10, false, false));
        ventana2.mover(1.5f, 0.5f, 2.51f);
        piso.agregarHijo(ventana2);

        return piso;
    }

    private QEntidad piso1() {
        QEntidad piso1 = piso();
        QEntidad puerta = new QEntidad("puerta");
        puerta.agregarComponente(puertaG);
        puerta.mover(0, -0.75f, 2.51f);
        piso1.agregarHijo(puerta);
        return piso1;
    }

    private QEntidad techo() {
        QEntidad techo = new QEntidad("techo");
        techo.agregarComponente(techoG);
        techo.rotar(0, Math.toRadians(45), 0);
        QEntidad chimenea = chimenea();
        chimenea.mover(-1f, 0f, 0.75f);
        techo.agregarHijo(chimenea);
        return techo;
    }

    private QEntidad chimenea() {
        QEntidad chimenea = new QEntidad("chimenea");
        chimenea.agregarComponente(chimeneaG);
        AABB ambitoHumo = new AABB(new QVertice(-0.15f, 0, -0.15f), new QVertice(.15f, 3.5f, .15f));//
        QEmisorHumo emisor = new QEmisorHumo(ambitoHumo, 5000, 400, 4);
        QEntidad emisorEn = new QEntidad();
        emisorEn.agregarComponente(emisor);
        emisorEn.mover(0, 0.3f, 0);
        chimenea.agregarHijo(emisorEn);
        return chimenea;
    }

}
