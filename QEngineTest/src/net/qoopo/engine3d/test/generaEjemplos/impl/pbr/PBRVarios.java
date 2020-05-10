/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoEnlace;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QPBRMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBRColorIluminacion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBRColorReflexion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBRColorTextura;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBREmision;

/**
 *
 * @author alberto
 */
public class PBRVarios extends GeneraEjemplo {

    public PBRVarios() {

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        //agrego una esfera para cargar un mapa como entorno
        QEntidad entorno = new QEntidad("Entorno");
        QMaterialBas matEntorno = new QMaterialBas("Entorno");
//        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/from_cubemap.jpg"))));
        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/exteriores/Tropical_Beach/Tropical_Beach_8k.jpg"))));
        entorno.agregarComponente(QMaterialUtil.aplicarMaterial(QUtilNormales.invertirNormales(new QEsfera(50)), matEntorno));
        mundo.agregarEntidad(entorno);

        // materiales con difuso
        QMaterialNodo material = new QMaterialNodo("PBR_Esfera");

        QPBRColorIluminacion ilum1 = new QPBRColorIluminacion(QColor.RED);
        QPBRMaterial nodosalida = new QPBRMaterial();
        QNodoEnlace enlace = new QNodoEnlace(ilum1.getSaColor(), nodosalida.getEnColor());

        material.setNodo(nodosalida);

        QEntidad esfera = new QEntidad("esfera");
        esfera.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2), material));
        esfera.mover(-5, 5, 0);
        mundo.agregarEntidad(esfera);

        // con textura e iluminacion (PBR) con mapa de normales
        QMaterialNodo materialC6 = new QMaterialNodo();

        QPBRColorTextura nodoTexturaC6_1 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/poliigon/bricks/RockGrey016/1K/RockGrey016_COL_VAR1_1K.jpg"))));
        QPBRColorTextura nodoTexturaC6_2 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/poliigon/bricks/RockGrey016/1K/RockGrey016_NRM_1K.jpg"))));
        QPBRColorIluminacion nodoDifusoC6_1 = new QPBRColorIluminacion();

        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlaceC6_1 = new QNodoEnlace(nodoTexturaC6_1.getSaColor(), nodoDifusoC6_1.getEnColor());
        //enlace que une la salida de DifusoC4 con la entrada1 de mix
        QNodoEnlace enlaceC6_2 = new QNodoEnlace(nodoTexturaC6_2.getSaColor(), nodoDifusoC6_1.getEnNormal());
        //enlace que une la salida del primer difuso con la entrada1 de mix

        QPBRMaterial nodosalida2 = new QPBRMaterial();
        QNodoEnlace enlace2_c6 = new QNodoEnlace(nodoDifusoC6_1.getSaColor(), nodosalida2.getEnColor());

        materialC6.setNodo(nodosalida2);
        QEntidad cubo6 = new QEntidad("Caja");
        cubo6.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), materialC6));
        cubo6.mover(0, 0, 0);

        mundo.agregarEntidad(cubo6);

        // reflejos
        // reflejos con pbr
        QEntidad tetera = new QEntidad("Tetera");
        QMapaCubo mapa2 = new QMapaCubo(200);

//        QGeometria esfera2 = new QEsfera(1);
        QGeometria teteraGeometria = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot_uv.obj")).get(0));

        QMaterialNodo mat5 = new QMaterialNodo("Reflexion");

        QPBRColorReflexion nodoreflexion = new QPBRColorReflexion(new QProcesadorSimple(mapa2.getTexturaSalida()));
        nodoreflexion.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        QPBRColorIluminacion nodoDifuso = new QPBRColorIluminacion();

        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlace3 = new QNodoEnlace(nodoreflexion.getSaColor(), nodoDifuso.getEnColor());

        QPBRMaterial nodosalida3 = new QPBRMaterial();
        QNodoEnlace enlace4 = new QNodoEnlace(nodoDifuso.getSaColor(), nodosalida3.getEnColor());

        mat5.setNodo(nodosalida3);
//        mat5.setNodo(nodoreflexion);

        tetera.agregarComponente(QMaterialUtil.aplicarMaterial(teteraGeometria, mat5));
        tetera.agregarComponente(mapa2);
        mapa2.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 0);
        tetera.mover(2, 0.5f, 0);
        mundo.agregarEntidad(tetera);

        // emisivo
        QEntidad entEmisivo = new QEntidad("emision");
        
        QMaterialNodo matEmisivo = new QMaterialNodo("Emisión");
        QPBREmision nodoEmision = new QPBREmision(QColor.YELLOW, 1.0f);

        QPBRMaterial nodosalida5 = new QPBRMaterial();
        QNodoEnlace enlace5 = new QNodoEnlace(nodoEmision.getSaColor(), nodosalida5.getEnColor());

        matEmisivo.setNodo(nodosalida5);
        entEmisivo.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.25f), matEmisivo));
        entEmisivo.mover(4, 4, 4);
        mundo.agregarEntidad(entEmisivo);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
