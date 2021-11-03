/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.nodos;

import java.io.File;
import java.util.Random;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoEnlace;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QNodoMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorIluminacion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QNodoColorTextura;

/**
 *
 * @author alberto
 */
public class NodosUniversoCubos extends GeneraEjemplo {

    public NodosUniversoCubos() {

    }

    private QMaterialNodo material = null;

    private void cargarMaterial() {
        material = null;
        try {
//            int colorTransparencia = -1;
            material = new QMaterialNodo();

            QNodoColorTextura nodoTextura = new QNodoColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/Skybox_example.png"))));
            QNodoColorIluminacion nodoDifuso = new QNodoColorIluminacion();
            // al instanciar el enlace, este se agrega a los perifericos
            QNodoEnlace enlace = new QNodoEnlace(nodoTextura.getSaColor(), nodoDifuso.getEnColor());

            QNodoMaterial nodosalida = new QNodoMaterial();
            QNodoEnlace enlace2 = new QNodoEnlace(nodoDifuso.getSaColor(), nodosalida.getEnColor());

            material.setNodo(nodosalida);
//            material.texturaColorTransparente = colorTransparencia;
//            if (colorTransparencia != -1) {
//                material.transAlfa = 0.99f;// el objeto tiene una transparencia 
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;
        cargarMaterial();
        Random rnd = new Random();
        float tamUniverso = 200;

//        Color colores[] = {
//            Color.YELLOW,
//            Color.RED,
//            Color.GREEN,
//            Color.PINK,
//            Color.MAGENTA,
//            Color.ORANGE,
//            Color.BLUE,
//            Color.CYAN,
//            Color.LIGHT_GRAY,
//            //            Color.BLACK,
//            Color.WHITE,
//            Color.GRAY,
//            Color.DARK_GRAY
//        };
//        Color actual;

        QGeometria geometria = new QCaja(1);
        QMaterialUtil.aplicarMaterial(geometria, material);

        for (int i = 0; i < 3000; i++) {
            QEntidad cubo = new QEntidad("Cubo [" + i + "]");
            cubo.mover(rnd.nextFloat() * tamUniverso * 2 - tamUniverso, rnd.nextFloat() * tamUniverso * 2 - tamUniverso, rnd.nextFloat() * tamUniverso * 2 - tamUniverso);
            cubo.agregarComponente(geometria);
            mundo.agregarEntidad(cubo);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
