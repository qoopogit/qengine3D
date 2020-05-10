/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

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
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QPBRMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBRColorIluminacion;

/**
 *
 * @author alberto
 */
public class PBRSimple extends GeneraEjemplo {

    public PBRSimple() {

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QPBRColorIluminacion ilum1 = new QPBRColorIluminacion(QColor.RED);
        QPBRMaterial nodosalida = new QPBRMaterial();
        QNodoEnlace enlace = new QNodoEnlace(ilum1.getSaColor(), nodosalida.getEnColor());
//
        QMaterialNodo material = new QMaterialNodo("PBR_Esfera");
        material.setNodo(nodosalida);

        QEntidad esfera = new QEntidad("esfera");
        esfera.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2), material));
        esfera.mover(-5, 5, 0);
        mundo.agregarEntidad(esfera);
//-----------------------

        QPBRColorIluminacion ilum2 = new QPBRColorIluminacion(QColor.BLUE);
        QPBRMaterial nodosalida2 = new QPBRMaterial();
        QNodoEnlace enlace2 = new QNodoEnlace(ilum2.getSaColor(), nodosalida2.getEnColor());

        QMaterialNodo materialCubo = new QMaterialNodo("PBR_Cubo");
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
