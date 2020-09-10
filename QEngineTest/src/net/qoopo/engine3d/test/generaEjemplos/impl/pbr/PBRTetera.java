/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class PBRTetera extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QMaterialBas material = new QMaterialBas();

        QGeometria malla = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot.obj")).get(0));

        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_COL_2K_METALNESS.jpg"));
        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_NRM_2K_METALNESS.jpg"));
        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_ROUGHNESS_2K_METALNESS.jpg"));
        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_METALNESS_2K_METALNESS.jpg"));
        material.setMapaColor(new QProcesadorSimple(albedo));
        material.setMapaNormal(new QProcesadorSimple(normal));
        material.setMapaRugosidad(new QProcesadorSimple(rugoso));
        material.setMapaMetalico(new QProcesadorSimple(metalico));
        QEntidad objeto = new QEntidad("tetera");
        objeto.agregarComponente(QMaterialUtil.aplicarMaterial(malla, material));
        mundo.agregarEntidad(objeto);
        //-------------------------------------
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaEntorno()));
        material.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objeto.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 0.8f, 0);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
