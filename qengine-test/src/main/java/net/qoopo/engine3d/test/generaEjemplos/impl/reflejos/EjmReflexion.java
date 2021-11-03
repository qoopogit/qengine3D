/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.reflejos;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QTeapot;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 * @author alberto
 */
public class EjmReflexion extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QEntidad objeto = new QEntidad("Reflexion");
        objeto.mover(0, 3, 0);
        QMaterialBas mat4 = new QMaterialBas("Reflexion");
        mat4.setColorBase(QColor.BLUE);
        mat4.setMetalico(0.8f);
        QMapaCubo mapa = new QMapaCubo();
        mat4.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaEntorno()));
        mat4.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QTeapot(), mat4));
        objeto.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 0.9f, 0);
        mundo.agregarEntidad(objeto);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
