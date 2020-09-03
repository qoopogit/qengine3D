/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.nodos;

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
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoEnlace;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QNodoMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorIluminacion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorReflexion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorTextura;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoEmision;

/**
 *
 * @author alberto
 */
public class NodosVarios extends GeneraEjemplo {

    public NodosVarios() {

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        // materiales con difuso
        QMaterialNodo material = new QMaterialNodo("Nodo_Esfera");

        QNodoColorIluminacion ilum1 = new QNodoColorIluminacion(QColor.RED);
        QNodoMaterial nodosalida = new QNodoMaterial();
        QNodoEnlace enlace = new QNodoEnlace(ilum1.getSaColor(), nodosalida.getEnColor());

        material.setNodo(nodosalida);

        QEntidad esfera = new QEntidad("esfera");
        esfera.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(2), material));
        esfera.mover(-5, 5, 0);
        mundo.agregarEntidad(esfera);

        // con textura e iluminacion (Nodo) con mapa de normales
        QMaterialNodo materialC6 = new QMaterialNodo();

        QNodoColorTextura nodoTexturaC6_1 = new QNodoColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/poliigon/bricks/RockGrey016/1K/RockGrey016_COL_VAR1_1K.jpg"))));
        QNodoColorTextura nodoTexturaC6_2 = new QNodoColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/poliigon/bricks/RockGrey016/1K/RockGrey016_NRM_1K.jpg"))));
        QNodoColorIluminacion nodoDifusoC6_1 = new QNodoColorIluminacion();

        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlaceC6_1 = new QNodoEnlace(nodoTexturaC6_1.getSaColor(), nodoDifusoC6_1.getEnColor());
        //enlace que une la salida de DifusoC4 con la entrada1 de mix
        QNodoEnlace enlaceC6_2 = new QNodoEnlace(nodoTexturaC6_2.getSaColor(), nodoDifusoC6_1.getEnNormal());
        //enlace que une la salida del primer difuso con la entrada1 de mix

        QNodoMaterial nodosalida2 = new QNodoMaterial();
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
        QGeometria teteraGeometria = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot.obj")).get(0));

        QMaterialNodo mat5 = new QMaterialNodo("Reflexion");

        QNodoColorReflexion nodoreflexion = new QNodoColorReflexion(new QProcesadorSimple(mapa2.getTexturaSalida()));
        nodoreflexion.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        QNodoColorIluminacion nodoDifuso = new QNodoColorIluminacion();

        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlace3 = new QNodoEnlace(nodoreflexion.getSaColor(), nodoDifuso.getEnColor());

        QNodoMaterial nodosalida3 = new QNodoMaterial();
        QNodoEnlace enlace4 = new QNodoEnlace(nodoDifuso.getSaColor(), nodosalida3.getEnColor());

        mat5.setNodo(nodosalida3);
//        mat5.setNodo(nodoreflexion);

        tetera.agregarComponente(QMaterialUtil.aplicarMaterial(teteraGeometria, mat5));
        tetera.agregarComponente(mapa2);
        tetera.mover(2, 0.5f, 0);
        mapa2.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 0);

        mundo.agregarEntidad(tetera);

        // emisivo
        QEntidad entEmisivo = new QEntidad("emision");

        QMaterialNodo matEmisivo = new QMaterialNodo("Emisión");
        QNodoEmision nodoEmision = new QNodoEmision(QColor.YELLOW, 1.0f);

        QNodoMaterial nodosalida5 = new QNodoMaterial();
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
