/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 * @author alberto
 */
public class EjmDivision extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QTextura text = QGestorRecursos.cargarTextura("asd1", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/tierra/text3/earthmap1k.jpg"));

        QEntidad obj1 = new QEntidad("esfera1");
        obj1.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(), new QMaterialBas(text)));
        obj1.mover(new QVector3(-3, 0, 0));
        mundo.agregarEntidad(obj1);

        QEntidad obj2 = new QEntidad("esfera2");
        QGeometria esfera2 = new QCaja();
        esfera2.dividir().suavizar();
        obj2.agregarComponente(QMaterialUtil.aplicarMaterial(esfera2, new QMaterialBas(text)));
        obj2.mover(new QVector3(0, 0, 0));
        mundo.agregarEntidad(obj2);

        QEntidad obj3 = new QEntidad("esfera3");
        QGeometria esfera3 = new QCaja();
        esfera3.dividirCatmullClark().suavizar();
        obj3.agregarComponente(QMaterialUtil.aplicarMaterial(esfera3, new QMaterialBas(text)));
        obj3.mover(new QVector3(3, 0, 0));
        mundo.agregarEntidad(obj3);

//        QEntidad obj4 = new QEntidad("esfera4");
//        QGeometria esfera4 = new QCaja();
//        esfera4.dividirCatmullClark().eliminarVerticesDuplicados().suavizar();
//        obj4.agregarComponente(QMaterialUtil.aplicarMaterial(esfera4, new QMaterialBas(text)));
//        obj4.mover(new QVector3(6, 0, 0));
//        mundo.agregarEntidad(obj4);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
