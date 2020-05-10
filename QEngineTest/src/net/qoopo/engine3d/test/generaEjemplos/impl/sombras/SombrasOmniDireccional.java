/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.sombras;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QMalla;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QToro;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 *
 * @author alberto
 */
public class SombrasOmniDireccional extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        //luces
        QEntidad luzCentral = new QEntidad("Luz_central");
//        luzEntidad.mover(0, 0.5f, 10f);
        QLuzPuntual luz = new QLuzPuntual(1f, QColor.WHITE, true, 30);
        luz.setProyectarSombras(true);
        luz.setResolucionMapaSombra(QGlobal.SOMBRAS_OMNI_DIRECCIONAL_MAPA_ANCHO);
        luzCentral.agregarComponente(luz);
        mundo.agregarEntidad(luzCentral);

//
//        QEntidad luz2 = new QEntidad("luz_2");
//        luz2.mover(-2, 0.5f, 2f);
//        luz2.agregarComponente(new QLuzPuntual(1f, QColor.WHITE, true, 30));
//        mundo.agregarEntidad(luz2);
//
//        QEntidad luz3 = new QEntidad("luz_3");
//        luz3.mover(-2, 1.5f, -2f);
//        luz3.agregarComponente(new QLuzPuntual(1f, QColor.WHITE, true, 30));
//        mundo.agregarEntidad(luz3);
//
//        QEntidad luz4 = new QEntidad("luz_4");
//        luz4.mover(2, -1f, -2f);
//        luz4.agregarComponente(new QLuzPuntual(1f, QColor.WHITE, true, 30));
//        mundo.agregarEntidad(luz4);
//
//
        QEntidad toro = new QEntidad("toro");
        toro.agregarComponente(new QToro(1, 0.4f));
        toro.mover(0, -2.5f, 0);
        mundo.agregarEntidad(toro);

        QEntidad cuboEntidad = new QEntidad("cubo");
        cuboEntidad.mover(-3, 1, 0);
        QMaterialBas mat1 = new QMaterialBas();
        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));
        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaNormal.jpg"))));

        cuboEntidad.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), mat1));
        mundo.agregarEntidad(cuboEntidad);

        QEntidad cuboEntidad2 = new QEntidad("cubo 2");
        cuboEntidad2.mover(0, 2, -2);
        QMaterialBas mat2 = new QMaterialBas();
        mat2.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa2", new File(QGlobal.RECURSOS + "texturas/fuego/fuego4.png"))));
        cuboEntidad2.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), mat2));

        mundo.agregarEntidad(cuboEntidad2);

        QEntidad esfera = new QEntidad("esfera");
        esfera.getTransformacion().trasladar(2f, 0, 0f);
        esfera.agregarComponente(new QEsfera(mundo.UM.convertirPixel(25, QUnidadMedida.CENTIMETRO)));
        mundo.agregarEntidad(esfera);

        QEntidad tetera = new QEntidad("tetera");
        tetera.agregarComponente(QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/tetera_1/teapot_n_glass.obj")).get(4)));
        tetera.mover(2f, -2f, -2f);
        tetera.rotar(0, Math.toRadians(80), 0);
        mundo.agregarEntidad(tetera);

        QEntidad cubo = new QEntidad("cubo");
        cubo.agregarComponente(new QCaja(0.5f));
        cubo.mover(0, 0, 1);
        mundo.agregarEntidad(cubo);

        QEntidad cubo2 = new QEntidad("cubo2");
        cubo2.agregarComponente(new QCaja(0.5f));
        cubo2.mover(-1.5f, 0.5f, 0);
        mundo.agregarEntidad(cubo2);

        QEntidad malla = new QEntidad("malla");
        malla.agregarComponente(new QMalla(true, 5, 5, 5f, 5));
        mundo.agregarEntidad(malla);

        QEntidad pared1 = new QEntidad("Caja");
//        pared1.mover(0, -1, 0);
        pared1.agregarComponente(QUtilNormales.invertirNormales(new QCaja(30f, 30f, 30f)));
        mundo.agregarEntidad(pared1);

//        QEntidad piso = new QEntidad("piso");
//        piso.mover(0, -5, 0);
//        piso.agregarComponente(new QCaja(0.1f, 10f, 10f));
//        mundo.agregarEntidad(piso);
//
//        QEntidad pared1 = new QEntidad("pared1");
//        pared1.mover(-5, 0, 0);
//        pared1.agregarComponente(new QCaja(10, 0.1f, 10f));
//        mundo.agregarEntidad(pared1);
//
//        QEntidad pared2 = new QEntidad("pared2");
//        pared2.mover(0, 0, -5);
//        pared2.agregarComponente(new QCaja(10, 10, 0.1f));
//        mundo.agregarEntidad(pared2);
//
//        QEntidad pared3 = new QEntidad("pared3");
//        pared3.mover(5, 0, 0);
//        pared3.agregarComponente(new QCaja(10, 0.1f, 10f));
//        mundo.agregarEntidad(pared3);
//
//        QEntidad techo = new QEntidad("techo");
//        techo.mover(0, 5, 0);
//        techo.agregarComponente(new QCaja(0.1f, 10f, 10f));
//        mundo.agregarEntidad(techo);
//        Thread hilo = new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                float angulo = (float) Math.toRadians(1);
////        float angz = 0;
//
//                while (true) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException ex) {
//                    }
//                    luz1.direction.rotateX(angulo);
////                    sol.direction.rotateZ(angulo);
//                }
//            }
//        });
//        hilo.start();
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
