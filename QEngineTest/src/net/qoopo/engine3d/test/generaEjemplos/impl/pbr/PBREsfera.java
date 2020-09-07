/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
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
public class PBREsfera extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

//        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/poliigon/metal/MetalCorrodedHeavy001/METALNESS/3K/MetalCorrodedHeavy001_COL_3K_METALNESS.jpg"));
//        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/poliigon/metal/MetalCorrodedHeavy001/METALNESS/3K/MetalCorrodedHeavy001_NRM_3K_METALNESS.jpg"));
//        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/poliigon/metal/MetalCorrodedHeavy001/METALNESS/3K/MetalCorrodedHeavy001_ROUGHNESS_3K_METALNESS.jpg"));
//        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/poliigon/metal/MetalCorrodedHeavy001/METALNESS/3K/MetalCorrodedHeavy001_METALNESS_3K_METALNESS.jpg"));
//------------------------------------
        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_basecolor.png"));
        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_normal.png"));
        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_roughness.png"));
        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_metallic.png"));
//------------------------------------      
//        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/light-gold-bl/lightgold_albedo.png"));
//        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/light-gold-bl/lightgold_normal-ogl.png"));
//        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/light-gold-bl/lightgold_roughness.png"));
//        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/light-gold-bl/lightgold_metallic.png"));

        QMaterialBas mat = new QMaterialBas();

        mat.setMapaDifusa(new QProcesadorSimple(albedo));
        mat.setMapaNormal(new QProcesadorSimple(normal));
        mat.setMapaRugosidad(new QProcesadorSimple(rugoso));
        mat.setMapaMetalico(new QProcesadorSimple(metalico));

        QEntidad objeto = new QEntidad("Esfera PBR");
        objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(1f), mat));
        //-------------------------------------
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        mat.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
        mat.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objeto.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 0.8f, 0);

        mundo.agregarEntidad(objeto);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
