/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.textura;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjmTexturaCubo extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

////        QTextura textura = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/exteriores/cubemap.jpg"));
//        QTextura textura = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/cube_map.png"));
////        QTextura textura = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/cube_map2.png"));
//        QProcesadorMipMap mimap = new QProcesadorMipMap(textura, 5, QProcesadorMipMap.TIPO_BLUR);
////        QProcesadorMipMap mimap = new QProcesadorMipMap(textura, 5);
//        mimap.setNivel(3);

        QEntidad cubo = new QEntidad("cubo");
        QMaterialBas material = new QMaterialBas();
        material.setMapaColor(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/testNormal/rocasColor.jpg"))));
        material.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/testNormal/rocasNormal.jpg"))));
//        material.setMapaMetalico(new QProcesadorSimple(mimap.getTexturaAtlas()));
//        material.setMapaRugosidad(mimap);
        cubo.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), material));
        mundo.agregarEntidad(cubo);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
