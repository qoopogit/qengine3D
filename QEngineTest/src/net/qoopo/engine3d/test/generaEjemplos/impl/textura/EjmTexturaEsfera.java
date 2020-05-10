/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.textura;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjmTexturaEsfera extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QMaterialBas mat1 = new QMaterialBas();
        mat1.setFactorNormal(0.5f);
        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/planetas/tierra/text3/earthmap1k.jpg"))));
//        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/planetas/tierra/text3/earthnormal1k.png"))));

//        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/poliigon/metal/MetalCorrodedHeavy001/SPECULAR/3K/MetalCorrodedHeavy001_COL_3K_SPECULAR.jpg"))));
//        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/poliigon/metal/MetalCorrodedHeavy001/SPECULAR/3K/MetalCorrodedHeavy001_NRM_3K_SPECULAR.jpg"))));
//        mat1.setMapaEspecular(new QProcesadorSimple(QGestorRecursos.cargarTextura("especular", new File(QGlobal.RECURSOS + "texturas/poliigon/metal/MetalCorrodedHeavy001/SPECULAR/3K/MetalCorrodedHeavy001_REFL_3K_SPECULAR.jpg"))));
//        mat1.setMapaRugosidad(new QProcesadorSimple(QGestorRecursos.cargarTextura("rugosa", new File(QGlobal.RECURSOS + "texturas/poliigon/metal/MetalCorrodedHeavy001/SPECULAR/3K/MetalCorrodedHeavy001_GLOSS_3K_SPECULAR.jpg"))));
        QEntidad esfera = new QEntidad("Esfera");

        QGeometria tierra = new QEsfera(2.5f, 36);
        tierra.nombre = "Tierra";
        QMaterialUtil.aplicarMaterial(tierra, mat1);

        esfera.agregarComponente(tierra);
        mundo.agregarEntidad(esfera);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
