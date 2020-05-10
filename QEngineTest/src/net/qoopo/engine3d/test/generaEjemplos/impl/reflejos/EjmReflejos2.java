/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.reflejos;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjmReflejos2 extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;
        QEntidad esfera = new QEntidad("Esfera");
        QMaterialBas mat = new QMaterialBas(QGestorRecursos.cargarTextura("text1", QGlobal.RECURSOS + "texturas/entorno/hdri/interior_hdri_32.jpg"));
        esfera.agregarComponente(QMaterialUtil.aplicarMaterial(QUtilNormales.invertirNormales(new QEsfera(20)), mat));
        mundo.agregarEntidad(esfera);
        //-----
        
        
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
