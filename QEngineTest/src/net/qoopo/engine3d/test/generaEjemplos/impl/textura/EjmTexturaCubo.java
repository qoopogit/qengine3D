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

        QMaterialBas mat1 = new QMaterialBas();
        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/testNormal/rocasColor.jpg"))));
        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/testNormal/rocasNormal.jpg"))));
//        mat1.getMapaDifusa().setMuestrasU(10);
//        mat1.getMapaDifusa().setMuestrasV(10);
//        mat1.getMapaNormal().setMuestrasU(10);
//        mat1.getMapaNormal().setMuestrasV(10);
//-----------------------
//        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/poliigon/madera/WoodWallCabin003/1K/WoodWallCabin003_COL_VAR1_1K.jpg"))));
//        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/poliigon/madera/WoodWallCabin003/1K/WoodWallCabin003_NRM_1K.jpg"))));
//        mat1.setMapaEspecular(new QProcesadorSimple(QGestorRecursos.cargarTextura("especular", new File(QGlobal.RECURSOS + "texturas/poliigon/madera/WoodWallCabin003/1K/WoodWallCabin003_REFL_1K.jpg"))));
//-----------------------------
//        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/poliigon/metal/MetalCorrodedHeavy001/SPECULAR/3K/MetalCorrodedHeavy001_COL_3K_SPECULAR.jpg"))));
//        mat1.setMapaRugosidad(new QProcesadorSimple(QGestorRecursos.cargarTextura("rugosidad", new File(QGlobal.RECURSOS + "texturas/poliigon/metal/MetalCorrodedHeavy001/SPECULAR/3K/MetalCorrodedHeavy001_GLOSS_3K_SPECULAR.jpg"))));        
//        mat1.setMapaEspecular(new QProcesadorSimple(QGestorRecursos.cargarTextura("especular", new File(QGlobal.RECURSOS + "texturas/poliigon/metal/MetalCorrodedHeavy001/SPECULAR/3K/MetalCorrodedHeavy001_REFL_3K_SPECULAR.jpg"))));

        QEntidad cubo = new QEntidad("cubo");
        cubo.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(5), mat1));

        mundo.agregarEntidad(cubo);

//        QEntidad malla = new QEntidad("Malla");
//        malla.agregarComponente(new QMalla(true,30, 30, 1));
//        mundo.agregarEntidad(malla);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
