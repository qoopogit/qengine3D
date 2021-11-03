/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.nodos;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QTeapot;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoEnlace;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QNodoMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorIluminacion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorMix;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorReflexion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorRefraccion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorVidrio;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 * @author alberto
 */
public class NodosSimple5 extends GeneraEjemplo {

    public NodosSimple5() {

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

//        //agrego una esfera para cargar un mapa como entorno
//        QEntidad entorno = new QEntidad("Entorno");
//        QMaterialBas matEntorno = new QMaterialBas("Entorno");
//        matEntorno.setMapaColor(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/exteriores/from_cubemap.jpg"))));
//        entorno.agregarComponente(QMaterialUtil.aplicarMaterial(QUtilNormales.invertirNormales(new QEsfera(50)), matEntorno));
//        mundo.agregarEntidad(entorno);
        //Reflexion estandar
        //a cada entidad le agrego su generador de mapa de reflexion con un mapa cubico
        QEntidad cubo4 = new QEntidad("vidrioBAS");
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        QMaterialBas mat4 = new QMaterialBas("Reflexion real");
        mat4.setColorBase(QColor.YELLOW);
        mat4.setMetalico(1);
        mat4.setIndiceRefraccion(1.45f);
        mat4.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaEntorno()));
        mat4.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        cubo4.agregarComponente(QMaterialUtil.aplicarMaterial(new QTeapot(), mat4));
        cubo4.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 1.45f);
        cubo4.mover(-6, 0.5f, 0);
        mundo.agregarEntidad(cubo4);
//---------------------------------------------------------------------------------------
        // vidrio con pbr
        QEntidad cubo5 = new QEntidad("Vidrio Nodo");
        QMapaCubo mapa2 = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        QMaterialNodo mat5 = new QMaterialNodo("Vidrio real Nodo");
        QNodoColorVidrio nodoVidrio = new QNodoColorVidrio(new QProcesadorSimple(mapa2.getTexturaEntorno()), 1.45f);
        nodoVidrio.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        QNodoColorIluminacion nodoDifuso = new QNodoColorIluminacion();
        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlace = new QNodoEnlace(nodoVidrio.getSaColor(), nodoDifuso.getEnColor());
        QNodoMaterial nodosalida = new QNodoMaterial();
        QNodoEnlace enlace2 = new QNodoEnlace(nodoDifuso.getSaColor(), nodosalida.getEnColor());
        mat5.setNodo(nodosalida);
        cubo5.agregarComponente(QMaterialUtil.aplicarMaterial(new QTeapot(), mat5));
        cubo5.agregarComponente(mapa2);
        mapa2.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 0);
        cubo5.mover(0, 0.5f, 0);
        mundo.agregarEntidad(cubo5);
//---------------------------------------------------------------------------------------
        // vidrio con pbr con mix
        QEntidad cubo6 = new QEntidad("Mix");
        QMapaCubo mapa3 = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        QMaterialNodo mat6 = new QMaterialNodo("Vidrio real Nodo");
        QNodoColorReflexion nodoReflejo = new QNodoColorReflexion(new QProcesadorSimple(mapa3.getTexturaEntorno()));
        nodoReflejo.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        QNodoColorRefraccion nodoRefraccion = new QNodoColorRefraccion(new QProcesadorSimple(mapa3.getTexturaEntorno()), 1.45f);
        nodoRefraccion.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        QNodoColorMix nodoMix = new QNodoColorMix(0.5f);
        QNodoColorIluminacion nodoDifuso6_1 = new QNodoColorIluminacion();

        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlace6_1 = new QNodoEnlace(nodoReflejo.getSaColor(), nodoMix.getEnColor1());
        QNodoEnlace enlace6_2 = new QNodoEnlace(nodoRefraccion.getSaColor(), nodoMix.getEnColor2());
        QNodoEnlace enlace6_3 = new QNodoEnlace(nodoMix.getSaColor(), nodoDifuso6_1.getEnColor());
        QNodoMaterial nodosalida2 = new QNodoMaterial();
        QNodoEnlace enlace3 = new QNodoEnlace(nodoMix.getSaColor(), nodosalida2.getEnColor());
        mat6.setNodo(nodosalida2);
        cubo6.agregarComponente(QMaterialUtil.aplicarMaterial(new QTeapot(), mat6));
        cubo6.agregarComponente(mapa3);
        mapa3.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 0);
        cubo6.mover(6, 0.5f, 0);
        mundo.agregarEntidad(cubo6);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
