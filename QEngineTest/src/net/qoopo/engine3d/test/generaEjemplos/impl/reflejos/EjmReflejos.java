/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.reflejos;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindro;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCono;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
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

//        QEntidad sol = new QEntidad("Sol");
//        sol.agregarComponente(new QLuzDireccional(.5f, QColor.WHITE, true, 10));
//        mundo.agregarEntidad(sol);
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

        //agrego un piso
        QEntidad plano = new QEntidad("plano");
        plano.agregarComponente(new QPlano(20, 20));
        mundo.agregarEntidad(plano);

        //a este cubo le agrego como el material de entorno la textura refleja, que es donde quiero llegar, tendra como mapa de reflexion
        // el generado por el otro cubo que esta en una ubicacion diferente, por lo tanto su reflexion sera de otra entidad
//        QEntidad cubo3 = new QEntidad("cubo3");
//        QGeometria caja3 = new QCaja(2);
//        QMaterialBas mat3 = new QMaterialBas("Reflexion");
//        mat3.setColorDifusa(QColor.BLUE);
//        mat3.setFactorReflexion(0.5f);
//        mat3.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaGlobal()));
//        cubo3.agregarComponente(QMaterialUtil.aplicarMaterial(caja3, mat3));
//        cubo3.mover(0, 4, 0);
//        mundo.agregarEntidad(cubo3);
        //VERDADERA REFLEXION
        //a cada entidad le agrego su generador de mapa de reflexion con un mapa cubico
        QEntidad cubo4 = new QEntidad("cubo3");
        QMapaCubo mapa = new QMapaCubo(200);

        QGeometria caja4 = new QCaja(1);
        QMaterialBas mat4 = new QMaterialBas("Reflexion real");
        mat4.setColorDifusa(QColor.YELLOW);
        mat4.setFactorEntorno(1);
        mat4.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
        mat4.setTipoMapaEntorno(1);
        cubo4.agregarComponente(QMaterialUtil.aplicarMaterial(caja4, mat4));
        cubo4.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_HDRI, 1, 0);
//        cubo4.mover(0, 0.5f, -1);        
        mundo.agregarEntidad(cubo4);
//
//        QEntidad esfera1 = new QEntidad("esfera1");
//        QMapaCubo mapaEsfera = new QMapaCubo(150, 150);
//        QGeometria esferaGeo = new QEsfera(1);
//        QMaterialBas matEsfera = new QMaterialBas("Reflexion real 2");
//        matEsfera.setColorDifusa(QColor.YELLOW);
//        matEsfera.setFactorReflexion(0.5f);
//        matEsfera.setMapaEntorno(new QProcesadorSimple(mapaEsfera.getTexturaGlobal()));
//        esfera1.agregarComponente(QMaterialUtil.aplicarMaterial(esferaGeo, matEsfera));
//        esfera1.agregarComponente(mapaEsfera);
//        esfera1.mover(-1, 0.5f, 1);
//        mundo.agregarEntidad(esfera1);
//        
//        
//        QEntidad tetera = new QEntidad("tetera");
//        QMapaCubo mapatetera = new QMapaCubo(150, 150);
//        QGeometria teteraGeom = CargaEstatica.cargarWaveObject(new File(Global.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot_uv.obj")).get(0);
//        QMaterialBas matTetera = new QMaterialBas("tetera");
//        matTetera.setColorDifusa(QColor.YELLOW);
//        matTetera.setFactorReflexion(0.5f);
//        matTetera.setMapaEntorno(new QProcesadorSimple(mapatetera.getTexturaGlobal()));
//        tetera.agregarComponente(QMaterialUtil.aplicarMaterial(teteraGeom, matTetera));
//        tetera.agregarComponente(mapatetera);
//        tetera.mover(1, 0.5f, 1);
//        mundo.agregarEntidad(tetera);

        //tetera.agregarComponente();
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
