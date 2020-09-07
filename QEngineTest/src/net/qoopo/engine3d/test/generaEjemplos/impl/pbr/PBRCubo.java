/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import net.qoopo.engine3d.test.generaEjemplos.impl.textura.*;
import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
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
public class PBRCubo extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QMaterialBas mat1 = new QMaterialBas();
        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/testNormal/rocasColor.jpg"))));
        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/testNormal/rocasNormal.jpg"))));

        QTextura albedo = QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_basecolor.png"));
        QTextura normal = QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_normal.png"));
        QTextura rugoso = QGestorRecursos.cargarTextura("rugoso", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_roughness.png"));
        QTextura metalico = QGestorRecursos.cargarTextura("metalico", new File(QGlobal.RECURSOS + "texturas/PBR/freepbr/rustediron1-alt2-bl/rustediron2_metallic.png"));

        mat1.setMapaDifusa(new QProcesadorSimple(albedo));
        mat1.setMapaNormal(new QProcesadorSimple(normal));
        mat1.setMapaRugosidad(new QProcesadorSimple(rugoso));
        mat1.setMapaMetalico(new QProcesadorSimple(metalico));
        QEntidad cubo = new QEntidad("cubo");
        cubo.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), mat1));

        mundo.agregarEntidad(cubo);

//        QEntidad malla = new QEntidad("Malla");
//        malla.agregarComponente(new QMalla(true,30, 30, 1));
//        mundo.agregarEntidad(malla);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
