/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemploPBRTextura extends GeneraEjemplo {

    public EjemploPBRTextura() {

    }

    public void iniciar(QEscena escena) {
        this.mundo = escena;
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

        int nrRows = 7;
        int nrColumns = 7;
        float spacing = 2.5f;

        //la entidad reflexion se encargara de renderizar el mapa de reflejos
        QEntidad reflexion = new QEntidad();
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
//                material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
//                material.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        reflexion.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1.0f, 0);
        escena.agregarEntidad(reflexion);

        for (int row = 0; row < nrRows; ++row) {
            for (int col = 0; col < nrColumns; ++col) {
                QMaterialBas material = new QMaterialBas("PBR");
                material.setColorBase(QColor.RED);
//                material.setRugosidad(QMath.clamp((float) col / (float) nrColumns, 0.05f, 1.0f));
//                material.setMetalico((float) row / (float) nrRows);
                material.setMapaColor(new QProcesadorSimple(albedo));
                material.setMapaNormal(new QProcesadorSimple(normal));
                material.setMapaRugosidad(new QProcesadorSimple(rugoso));
                material.setMapaMetalico(new QProcesadorSimple(metalico));
//                material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
//                material.setMapaIrradiacion(new QProcesadorSimple(mapa.getTexturaIrradiacion()));
                QEntidad objeto = new QEntidad("PBR");
                objeto.mover((col - (nrColumns / 2)) * spacing, (row - (nrRows / 2)) * spacing, 0);
                objeto.rotar(0, Math.toRadians(180), 0);
                objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(1.0f), material));

//                QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
//                material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
//                material.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
//                objeto.agregarComponente(mapa);
//                mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 0.8f, 0);
                escena.agregarEntidad(objeto);
            }
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
