/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class Entorno extends GeneraEjemplo {

    public Entorno() {

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        //agrego una esfera para cargar un mapa como entorno
        QEntidad entorno = new QEntidad("Entorno");
        QMaterialBas matEntorno = new QMaterialBas("Entorno");
//        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("entornoDifuso", new File(QGlobal.RECURSOS + "texturas/cielo/esfericos/atardecer.jpg"))));
//        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("entornoDifuso", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/interiores/interior_hdri_32.jpg"))));        
//        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("entornoDifuso", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/exteriores/from_cubemap.jpg"))));
        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("entornoDifuso", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/exteriores/Tropical_Beach/Tropical_Beach_8k.jpg"))));
//        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("entornoDifuso", new File(QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo4.jpg"))));
//        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("entornoDifuso", new File(QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo.jpg"))));
        entorno.agregarComponente(QMaterialUtil.aplicarMaterial(QUtilNormales.invertirNormales(new QEsfera(50)), matEntorno));
        mundo.agregarEntidad(entorno);


    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
