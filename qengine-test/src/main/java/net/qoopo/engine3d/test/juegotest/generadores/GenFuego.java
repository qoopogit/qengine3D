/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest.generadores;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.modificadores.particulas.fuego.QEmisorFuego;
import net.qoopo.engine3d.componentes.modificadores.particulas.humo.QEmisorHumo;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class GenFuego {

    public static QEntidad crearFogata1() {
        QEntidad fogata = new QEntidad();

        AABB ambito = new AABB(new QVertice(-0.15f, 0, -0.15f), new QVertice(.15f, 1.5f, .15f));//
        QEmisorFuego emisorFuego = new QEmisorFuego(ambito, 2000, 50, 5, QGlobal.gravedad.clone().multiply(-1f), false);
        fogata.agregarComponente(emisorFuego);
        fogata.getTransformacion().trasladar(0, 0.5f, 0);

        QEntidad enLuz = new QEntidad();
        enLuz.mover(0, 0.25f, 0);
        enLuz.agregarComponente(new QLuzPuntual(0.8f, QColor.YELLOW,  10f, true, true));
        fogata.agregarHijo(enLuz);

        QEntidad emisorHu = new QEntidad();
        emisorHu.mover(0, 0.5f, 0);
        AABB ambitoHumo = new AABB(new QVertice(-0.15f, 0, -0.15f), new QVertice(.15f, 3.5f, .15f));//
        QEmisorHumo emisorHumo = new QEmisorHumo(ambitoHumo, 5000, 400, 4);
        emisorHu.agregarComponente(emisorHumo);
        fogata.agregarHijo(emisorHu);

        return fogata;
    }

    public static QEntidad crearFogataConLuces() {
        QEntidad fogata = new QEntidad();

        AABB ambito = new AABB(new QVertice(-0.15f, 0, -0.15f), new QVertice(.15f, 1.5f, .15f));//
        QEmisorFuego emisorFuego = new QEmisorFuego(ambito, 2000, 50, 5, QGlobal.gravedad.clone().multiply(-1f), true);
        fogata.agregarComponente(emisorFuego);
        fogata.getTransformacion().trasladar(0, 0.5f, 0);

        QEntidad enLuz = new QEntidad();
        enLuz.mover(0, 0.25f, 0);
        enLuz.agregarComponente(new QLuzPuntual(0.8f, QColor.YELLOW,  10f, true, true));
        fogata.agregarHijo(enLuz);

        QEntidad emisorHu = new QEntidad();
        emisorHu.mover(0, 0.5f, 0);
        AABB ambitoHumo = new AABB(new QVertice(-0.15f, 0, -0.15f), new QVertice(.15f, 3.5f, .15f));//
        QEmisorHumo emisorHumo = new QEmisorHumo(ambitoHumo, 5000, 400, 4);
        emisorHu.agregarComponente(emisorHumo);
        fogata.agregarHijo(emisorHu);

        return fogata;
    }
}
