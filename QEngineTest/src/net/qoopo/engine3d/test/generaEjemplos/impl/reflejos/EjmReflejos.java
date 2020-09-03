/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.reflejos;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindro;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCono;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QToro;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjmReflejos extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;



        QEntidad cilindroBeto = new QEntidad("cilindro");
        cilindroBeto.agregarComponente(new QCilindro(2, 1));
        cilindroBeto.getTransformacion().getTraslacion().x += 5;
        cilindroBeto.getTransformacion().getTraslacion().z += 5;
        mundo.agregarEntidad(cilindroBeto);

        QEntidad esfera = new QEntidad("esfera");
        esfera.agregarComponente(new QEsfera(2));
        esfera.getTransformacion().getTraslacion().x += 5;
        esfera.getTransformacion().getTraslacion().z -= 5;
        mundo.agregarEntidad(esfera);
//
        QEntidad toro = new QEntidad("toro");
        toro.agregarComponente(new QToro(4, 2));
        toro.getTransformacion().getTraslacion().x -= 5;
        toro.getTransformacion().getTraslacion().z -= 5;
        mundo.agregarEntidad(toro);

        QEntidad cono = new QEntidad("cono");
        cono.agregarComponente(new QCono(4, 2));
        cono.getTransformacion().getTraslacion().x -= 5;

        cono.getTransformacion().getTraslacion().z += 5;
        mundo.agregarEntidad(cono);

        //a cada entidad le agrego su generador de mapa de reflexion con un mapa cubico
        QEntidad objetoReflejo = new QEntidad("Entidad Reflejo");
        QMapaCubo mapa = new QMapaCubo(200);
        QMaterialBas matReflejo = new QMaterialBas("Reflexion real");
        matReflejo.setColorDifusa(QColor.YELLOW);
        matReflejo.setMetalico(0.8f);
        matReflejo.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
        matReflejo.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        objetoReflejo.agregarComponente(QMaterialUtil.aplicarMaterial(new QToro(2,0.35f), matReflejo));
        objetoReflejo.agregarComponente(mapa);
        objetoReflejo.mover(-2, 0, 0);        
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 0);        
        mundo.agregarEntidad(objetoReflejo);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
