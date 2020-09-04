/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest.generadores;

import java.io.File;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindro;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCono;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.math.QColor;

/**
 * Clase para crear personajes del juego
 *
 * @author alberto
 */
public class GeneradorLamparas {

//    private QEntidad lampara;
//    private QEntidad cono;
//    private QEntidad tronco;
//    private QEntidad base;
    private static int secciones = 10;

    public boolean shift = false;

    public static QEntidad crearLamparaVelador() {

        QEntidad lampara = new QEntidad("Lampara");

        QEntidad cono = new QEntidad("cono");
        cono.mover(0, 0.35f, 0);//a metro y medio de altura
        cono.agregarComponente(QMaterialUtil.aplicarColor(new QCono(0.15f, 0.15f, secciones), 0.7f, 1f, 1f, 0, 1, 1, 1, 1, 64));
        cono.agregarComponente(new QLuzPuntual(.5f, QColor.YELLOW, 5f, false, false));
//        cono.agregarComponente(new QLuz(QLuz.LUZ_PUNTUAL, .5f, 255, 255, 255, true));

        QEntidad tronco = new QEntidad("tronco");
        tronco.mover(0, 0.15f, 0);
        tronco.agregarComponente(new QCilindro(0.35f, 0.01f, secciones));

        QEntidad base = new QEntidad("Base");
        base.agregarComponente(new QCilindro(0.02f, 0.15f, secciones));

        lampara.agregarHijo(cono);
        lampara.agregarHijo(tronco);
        lampara.agregarHijo(base);

        return lampara;
    }

    public static QEntidad crearLinterna() {

        QEntidad lampara = new QEntidad("Linterna");

        QEntidad cono = new QEntidad("li-cono");
        cono.mover(0, 0.35f, 0);
//        cono.agregarComponente(QMaterialUtil.aplicarColor(new QCono(0.15f, 0.05f, secciones), 1f, QColor.YELLOW, QColor.WHITE, 1, 64));
        cono.agregarComponente(QMaterialUtil.aplicarColor(new QEsfera(0.03f, 8), 1f, QColor.YELLOW, QColor.WHITE, 1, 64));
        cono.agregarComponente(new QLuzSpot(2.5f, QColor.YELLOW, 20f, new QVector3(0, 0, -1), (float) Math.toRadians(30f), (float) Math.toRadians(25f), false, false));
//        cono.agregarComponente(new QLuzPuntual(2.5f, QColor.YELLOW, true, 20));

//        cono.agregarComponente(new QLuzPuntual(2.5f, QColor.YELLOW, true, 20f));
        QEntidad tronco = new QEntidad("li-tronco");
        tronco.mover(0, 0.15f, 0);
        tronco.agregarComponente(new QCilindro(0.35f, 0.01f, 8));

        lampara.agregarHijo(cono);
        lampara.agregarHijo(tronco);

        return lampara;
    }

    public static QEntidad crearLamparaPiso2() {
        QEntidad lampara = new QEntidad("Lampara");
        QEntidad cono = new QEntidad("cono");
        cono.mover(0, 3f, 0);

        cono.agregarComponente(new QLuzPuntual(1f, QColor.YELLOW, 10, false, false));

        lampara.agregarHijo(cono);
        QEntidad cuerpo = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/ARQUITECTURA/baja_calidad/lampara/lamp.obj")).get(0);
        cuerpo.escalar(0.3f, 0.3f, 0.3f);

        lampara.agregarHijo(cuerpo);
        //se agrga la animacino a la entidad
//        personaje.agregarComponente(animacion);
        return lampara;
    }

    public static QEntidad crearLamparaPiso() {
        QEntidad lampara = new QEntidad("Lampara");

        QEntidad cono = new QEntidad("l-foco");
        cono.mover(0, 2.25f, 0);

//        cono.agregarComponente(QNormales.invertirNormales(QMaterialUtil.aplicarColor(new QEsfera(0.25f, 8), 0.7f, 1f, 1f, 0, 1, 1, 1, 64)));
//        cono.agregarComponente(QMaterialUtil.aplicarColor(new QEsfera(0.5f, 8), 0.15f, QColor.YELLOW, QColor.YELLOW, 1, 1000000));//el efecto aura
        cono.agregarComponente(QMaterialUtil.aplicarColor(new QEsfera(0.25f, 8), 1f, QColor.YELLOW, QColor.WHITE, 1, 64));//el real
        cono.agregarComponente(new QLuzPuntual(2.5f, QColor.YELLOW, 30, false, false));
//        cono.agregarComponente(new QLuz(QLuz.LUZ_PUNTUAL, .5f, 255, 255, 255, true));

        QEntidad tronco = new QEntidad("l-faro");
        tronco.mover(0, 1f, 0);
        tronco.agregarComponente(new QCilindro(2f, 0.01f, 8));

        QEntidad base = new QEntidad("l-base");
        base.agregarComponente(new QCilindro(0.02f, 0.15f, secciones));

        lampara.agregarHijo(cono);
        lampara.agregarHijo(tronco);
        lampara.agregarHijo(base);

        //se agrga la animacino a la entidad
//        personaje.agregarComponente(animacion);
        return lampara;
    }

    public static QEntidad crearLamparaPisoMonstruos() {
        QEntidad lampara = new QEntidad("Lampara");

        QEntidad cono = new QEntidad("l-foco");
        cono.mover(0, 2.25f, 0);

//        cono.agregarComponente(QNormales.invertirNormales(QMaterialUtil.aplicarColor(new QEsfera(0.25f, 8), 0.7f, 1f, 1f, 0, 1, 1, 1, 64)));
//        cono.agregarComponente(QMaterialUtil.aplicarColor(new QEsfera(0.5f, 8), 0.15f, QColor.YELLOW, QColor.YELLOW, 1, 1000000));//el efecto aura
        cono.agregarComponente(QMaterialUtil.aplicarColor(new QEsfera(0.25f, 8), 1f, QColor.YELLOW, QColor.WHITE, 1, 64));//el real
        cono.agregarComponente(new QLuzPuntual(100f, QColor.YELLOW, 1000, false, false));
//        cono.agregarComponente(new QLuz(QLuz.LUZ_PUNTUAL, .5f, 255, 255, 255, true));

        QEntidad tronco = new QEntidad("l-faro");
        tronco.mover(0, 1f, 0);
        tronco.agregarComponente(new QCilindro(2f, 0.01f, 8));

        QEntidad base = new QEntidad("l-base");
        base.agregarComponente(new QCilindro(0.02f, 0.15f, secciones));

        lampara.agregarHijo(cono);
        lampara.agregarHijo(tronco);
        lampara.agregarHijo(base);

        //se agrga la animacino a la entidad
//        personaje.agregarComponente(animacion);
        return lampara;
    }

}
