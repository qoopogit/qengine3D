/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.sombras;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QMalla;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 *
 * @author alberto
 */
public class SombrasDireccional extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QLuzDireccional sol = new QLuzDireccional(1.5f, QColor.WHITE,  50, new QVector3(0, -1f, 0), true, true);        
        QEntidad luzEntidad = new QEntidad("Sol");
        luzEntidad.agregarComponente(sol);
        mundo.agregarEntidad(luzEntidad);

        QMaterialBas mat1 = new QMaterialBas();
        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));
        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaNormal.jpg"))));

        QEntidad cuboEntidad = new QEntidad("cubo");
        cuboEntidad.mover(0, 5, 0);
        cuboEntidad.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), mat1));
        mundo.agregarEntidad(cuboEntidad);

        QMaterialBas mat2 = new QMaterialBas();
        mat2.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa2", new File(QGlobal.RECURSOS + "texturas/fuego/fuego4.png"))));
        mat2.setTransparencia(true);
        mat2.setColorTransparente(QColor.BLACK);
        QEntidad cuboEntidad2 = new QEntidad("cubo 2");
        cuboEntidad2.mover(0, 5, -2);
//        cuboEntidad2.agregarComponente(QMaterialUtil.aplicarTexturaCubo(new QCaja(8), new File("res/texturas/transparent.png"), null, 1));
        cuboEntidad2.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(4), mat2));

        mundo.agregarEntidad(cuboEntidad2);

        QGeometria esfera = new QEsfera(mundo.UM.convertirPixel(25, QUnidadMedida.CENTIMETRO));
        QEntidad esferaEntidad = new QEntidad("esfera");
        esferaEntidad.mover(-2, 2, 2);
        esferaEntidad.agregarComponente(esfera);
        mundo.agregarEntidad(esferaEntidad);

//        QEntidad plano = new QEntidad("plano");
//        plano.agregarComponente(new QPlano(50, 50));
//        mundo.agregarEntidad(plano);
        QEntidad malla = new QEntidad("malla");
        malla.agregarComponente(new QMalla(true, 5, 5, 5, 5));
        mundo.agregarEntidad(malla);

        QGeometria pinoG = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VEGETACION/EXTERIOR/baja_calidad/pino/lowpolytree.obj")).get(0));

        QEntidad pino1 = new QEntidad();
        pino1.agregarComponente(pinoG.clone());
        pino1.mover(3, 0, 0);
        pino1.getTransformacion().setEscala(new QVector3(2, 2, 2));
        mundo.agregarEntidad(pino1);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
