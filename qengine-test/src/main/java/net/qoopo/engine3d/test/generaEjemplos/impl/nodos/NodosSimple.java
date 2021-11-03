/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.nodos;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoEnlace;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QNodoMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorIluminacion;

/**
 *
 * @author alberto
 */
public class NodosSimple extends GeneraEjemplo {

    public NodosSimple() {

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QNodoColorIluminacion ilum1 = new QNodoColorIluminacion(QColor.RED);
        QNodoMaterial nodosalida = new QNodoMaterial();
        QNodoEnlace enlace = new QNodoEnlace(ilum1.getSaColor(), nodosalida.getEnColor());
//
        QMaterialNodo material = new QMaterialNodo("Nodo_Esfera");
        material.setNodo(nodosalida);

        QEntidad esfera = new QEntidad("esfera");
        esfera.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2), material));
        esfera.mover(-5, 5, 0);
        mundo.agregarEntidad(esfera);
//-----------------------

        QNodoColorIluminacion ilum2 = new QNodoColorIluminacion(QColor.BLUE);
        QNodoMaterial nodosalida2 = new QNodoMaterial();
        QNodoEnlace enlace2 = new QNodoEnlace(ilum2.getSaColor(), nodosalida2.getEnColor());

        QMaterialNodo materialCubo = new QMaterialNodo("Nodo_Cubo");
        materialCubo.setNodo(nodosalida2);

        QEntidad cubo = new QEntidad("Cubo");
        cubo.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), materialCubo));
        cubo.mover(5, 5, 0);
        mundo.agregarEntidad(cubo);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
