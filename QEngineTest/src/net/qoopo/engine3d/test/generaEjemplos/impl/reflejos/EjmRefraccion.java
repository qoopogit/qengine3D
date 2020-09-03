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
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindro;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCono;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QToro;
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

        //a cada entidad le agrego su generador de mapa de reflexion con un mapa cubico
        QGeometria malla = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot.obj")).get(0));
        QEntidad objetoReflejo = new QEntidad("Entidad Reflejo");
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        QMaterialBas matReflejo = new QMaterialBas("Reflexion real");
        matReflejo.setColorDifusa(QColor.WHITE);
        matReflejo.setMetalico(1f);
        matReflejo.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
        matReflejo.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objetoReflejo.agregarComponente(QMaterialUtil.aplicarMaterial(malla, matReflejo));
        objetoReflejo.agregarComponente(mapa);
        objetoReflejo.mover(-2, 0, 0);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1f, 1.45f);
        mundo.agregarEntidad(objetoReflejo);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
