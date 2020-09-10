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
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
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

//        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/fabric/leather_red_02_2k_jpg/leather_red_02_coll1_2k.jpg"));
//        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/fabric/leather_red_02_2k_jpg/leather_red_02_nor_2k.jpg"));
//        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/fabric/leather_red_02_2k_jpg/leather_red_02_rough_2k.jpg"));
////        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/fabric/leather_red_02_2k_jpg/"));
//        QTextura sombras = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/fabric/leather_red_02_2k_jpg/leather_red_02_ao_2k.jpg"));
        
        //-------------------------
//        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/floor/floor_tiles_06_2k_jpg/floor_tiles_06_diff_2k.jpg"));
//        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/floor/floor_tiles_06_2k_jpg/floor_tiles_06_nor_2k.jpg"));
//        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/floor/floor_tiles_06_2k_jpg/floor_tiles_06_rough_2k.jpg"));
//        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/floor/floor_tiles_06_2k_jpg/floor_tiles_06_rough_2k.jpg"));
//        QTextura sombras = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/floor/floor_tiles_06_2k_jpg/floor_tiles_06_AO_2k.jpg"));
//        
        //-------------------------
        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_COL_2K_METALNESS.jpg"));
        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_NRM_2K_METALNESS.jpg"));
        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_ROUGHNESS_2K_METALNESS.jpg"));
        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/metal/MetalSpottyDiscoloration001/METALNESS/2K/MetalSpottyDiscoloration001_METALNESS_2K_METALNESS.jpg"));
//        QTextura sombras = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/floor/TilesRectangularMirrorGray001/3K/"));
        


        QMaterialBas material = new QMaterialBas();
        
        material.setColorBase(QColor.RED);
        material.setMapaColor(new QProcesadorSimple(albedo));
        material.setMapaNormal(new QProcesadorSimple(normal));
        material.setMapaRugosidad(new QProcesadorSimple(rugoso));
        material.setMapaMetalico(new QProcesadorSimple(metalico));
//        material.setMapaSAO(new QProcesadorSimple(sombras));

        QEntidad objeto = new QEntidad("Esfera PBR");
        objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(1f), material));
        //-------------------------------------
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        mapa.setGenerarIrradiacion(true);
        material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaEntorno()));
        material.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objeto.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 0.8f, 0);
        
        mundo.agregarEntidad(objeto);
    }
    
    @Override
    public void accion(int numAccion, QMotorRender render) {
    }
    
}
