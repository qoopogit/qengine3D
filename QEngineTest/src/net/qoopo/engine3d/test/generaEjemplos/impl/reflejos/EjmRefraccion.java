/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.reflejos;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QSuzane;
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
public class EjmRefraccion extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;
        QEntidad ob1 = crear("Tetera",new QTeapot());
        ob1.mover(-3, 0, 0);
        mundo.agregarEntidad(ob1);
        QEntidad ob3 = crear("Mona", new QSuzane());
        ob3.mover(3, 0, 0);
        mundo.agregarEntidad(ob3);

    }

    private QEntidad crear(String nombre, QGeometria malla) {
        QEntidad objeto = new QEntidad(nombre);
        QMapaCubo mapa = new QMapaCubo();
        QMaterialBas material = new QMaterialBas(nombre);
        material.setColorBase(QColor.WHITE);
        material.setMetalico(1f);
        material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaEntorno()));
        material.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objeto.agregarComponente(QMaterialUtil.aplicarMaterial(malla, material));
        objeto.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1f, 1.45f);
        return objeto;
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
