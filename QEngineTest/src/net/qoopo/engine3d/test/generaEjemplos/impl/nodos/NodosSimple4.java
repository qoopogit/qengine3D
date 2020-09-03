/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.nodos;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
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
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QNodoMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorIluminacion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorRefraccion;

/**
 *
 * @author alberto
 */
public class NodosSimple4 extends GeneraEjemplo {
    
    public NodosSimple4() {
        
    }
    
    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        
        //agrego una esfera para cargar un mapa como entorno
        QEntidad entorno = new QEntidad("Entorno");
        QMaterialBas matEntorno = new QMaterialBas("Entorno");
        matEntorno.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/entorno/hdri/exteriores/from_cubemap.jpg"))));

        entorno.agregarComponente(QMaterialUtil.aplicarMaterial(QUtilNormales.invertirNormales(new QEsfera(50)), matEntorno));

        mundo.agregarEntidad(entorno);
        //agrego un piso
//        QEntidad plano = new QEntidad("plano");
//        plano.agregarComponente(new QPlano(20, 20));
//        mundo.agregarEntidad(plano);
        //Reflexion estandar
        //a cada entidad le agrego su generador de mapa de reflexion con un mapa cubico
        QEntidad cubo4 = new QEntidad("esferaR1");
        QMapaCubo mapa = new QMapaCubo(400);
        
        QGeometria esfera1 = new QEsfera(1);
        QMaterialBas mat4 = new QMaterialBas("Reflexion real");
        mat4.setColorDifusa(QColor.YELLOW);
        mat4.setMetalico(1);
        mat4.setIndiceRefraccion(1.45f);
        mat4.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
        mat4.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        cubo4.agregarComponente(QMaterialUtil.aplicarMaterial(esfera1, mat4));
        cubo4.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 1.45f);
        cubo4.mover(0, 0.5f, 0);
        mundo.agregarEntidad(cubo4);
//---------------------------------------------------------------------------------------
        // reflejos con nodo
        QEntidad cubo5 = new QEntidad("Reflejo Nodo");
        QMapaCubo mapa2 = new QMapaCubo(400);
        
        QGeometria esfera2 = new QEsfera(1);
        QMaterialNodo mat5 = new QMaterialNodo("Reflexion real Nodo");
        
        QNodoColorRefraccion nodoRefraccion = new QNodoColorRefraccion(new QProcesadorSimple(mapa2.getTexturaSalida()),1.45f);
        nodoRefraccion.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        QNodoColorIluminacion nodoDifuso = new QNodoColorIluminacion();

        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlace = new QNodoEnlace(nodoRefraccion.getSaColor(), nodoDifuso.getEnColor());
        
        QNodoMaterial nodosalida = new QNodoMaterial();
        QNodoEnlace enlace2 = new QNodoEnlace(nodoDifuso.getSaColor(), nodosalida.getEnColor());
        mat5.setNodo(nodosalida);
//        mat5.setNodo(nodoRefraccion);

        cubo5.agregarComponente(QMaterialUtil.aplicarMaterial(esfera2, mat5));
        cubo5.agregarComponente(mapa2);
        mapa2.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1, 0);
        cubo5.mover(2, 0.5f, 0);
        mundo.agregarEntidad(cubo5);
        
    }
    
    @Override
    public void accion(int numAccion, QMotorRender render) {
    }
    
}
