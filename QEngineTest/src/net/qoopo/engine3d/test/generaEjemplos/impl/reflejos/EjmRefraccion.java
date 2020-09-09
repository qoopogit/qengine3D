/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.reflejos;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjmRefraccion extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QEntidad ob1 = crear("Tetera", QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot.obj")).get(0)));
        ob1.mover(-3, 0, 0);
        mundo.agregarEntidad(ob1);

//        QEntidad ob2 = crear("Dragon", QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/dragon.obj")).get(0)));
//        ob2.mover(0, 0, 0);
//        ob2.escalar(0.15f, 0.15f, 0.15f);
//        mundo.agregarEntidad(ob2);
        QEntidad ob3 = crear("Mona", QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/Mona.obj")).get(0)));
        ob3.mover(3, 0, 0);
        mundo.agregarEntidad(ob3);

    }

    private QEntidad crear(String nombre, QGeometria malla) {
        QEntidad objeto = new QEntidad(nombre);
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        QMaterialBas matReflejo = new QMaterialBas(nombre);
        matReflejo.setColorBase(QColor.WHITE);
        matReflejo.setMetalico(1f);
        matReflejo.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaEntorno()));
        matReflejo.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objeto.agregarComponente(QMaterialUtil.aplicarMaterial(malla, matReflejo));
        objeto.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1f, 1.45f);
        return objeto;
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
