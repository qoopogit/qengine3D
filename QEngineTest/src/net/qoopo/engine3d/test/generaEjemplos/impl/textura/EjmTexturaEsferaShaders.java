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
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.shader.QShaderComponente;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales.QFlatShaderBAS;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales.QSimpleShaderBAS;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales.QTexturaShaderBAS;

/**
 *
 * @author alberto
 */
public class EjmTexturaEsferaShaders extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QMaterialBas mat1 = new QMaterialBas();
        mat1.setFactorNormal(0.5f);
        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/planetas/tierra/text3/earthmap1k.jpg"))));

        QEntidad esfera = new QEntidad("Esfera1");
        esfera.mover(5, 5, 5);
        esfera.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2.5f, 36), mat1));
        mundo.agregarEntidad(esfera);

        QEntidad esfera2 = new QEntidad("Esfera2");
        esfera2.mover(-5, 5, 5);
        esfera2.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2.5f, 36), mat1));
        esfera2.agregarComponente(new QShaderComponente(new QFlatShaderBAS(null)));
        mundo.agregarEntidad(esfera2);

        QEntidad esfera3 = new QEntidad("Esfera3");
        esfera3.mover(-5, -5, 5);
        esfera3.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2.5f, 36), mat1));
        esfera3.agregarComponente(new QShaderComponente(new QSimpleShaderBAS(null)));
        mundo.agregarEntidad(esfera3);

        QEntidad esfera4 = new QEntidad("Esfera4");
        esfera4.mover(5, -5, 5);
        esfera4.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2.5f, 36), mat1));
        esfera4.agregarComponente(new QShaderComponente(new QTexturaShaderBAS(null)));
        mundo.agregarEntidad(esfera4);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
