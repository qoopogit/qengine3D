/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.QNodoEnlace;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.salida.QPBRMaterial;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBRColorIluminacion;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBRColorMix;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado.QPBRColorTextura;

/**
 *
 * @author alberto
 */
public class PBRSimple2 extends GeneraEjemplo {

    public PBRSimple2() {

    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        // con textura (BASICO)
        QMaterialBas mat1 = new QMaterialBas();
        mat1.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));
        mat1.setMapaNormal(new QProcesadorSimple(QGestorRecursos.cargarTextura("normal", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaNormal.jpg"))));

        QEntidad cubo1 = new QEntidad("text BAS");
        cubo1.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), mat1));
        cubo1.mover(-5, 5, 5);

        mundo.agregarEntidad(cubo1);
        // con textura (PBR)
        QMaterialNodo matSoloTextura = new QMaterialNodo();

        QPBRColorTextura text1 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa2", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));

        QPBRMaterial nodosalida1 = new QPBRMaterial();
        QNodoEnlace enlace = new QNodoEnlace(text1.getSaColor(), nodosalida1.getEnColor());

        matSoloTextura.setNodo(nodosalida1);

        QEntidad cubo2 = new QEntidad("SoloText");
        cubo2.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), matSoloTextura));
        cubo2.mover(5, 5, 5);

        mundo.agregarEntidad(cubo2);
        //--------------------------------------------------------------------------------------------------
        // con textura e iluminacion (PBR)
        QMaterialNodo matPbrTextIlum = new QMaterialNodo();

        QPBRColorTextura nodoTextura = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa3", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));
        QPBRColorIluminacion nodoDifuso = new QPBRColorIluminacion();
        // al instanciar el enlace, este se agrega a los perifericos
        QNodoEnlace enCubo3_1 = new QNodoEnlace(nodoTextura.getSaColor(), nodoDifuso.getEnColor());

        QPBRMaterial nodosalida2 = new QPBRMaterial();
        QNodoEnlace enlace2 = new QNodoEnlace(nodoDifuso.getSaColor(), nodosalida2.getEnColor());

        matPbrTextIlum.setNodo(nodosalida2);

        QEntidad cubo3 = new QEntidad("pbr.textura.iluminacion");
        cubo3.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), matPbrTextIlum));
        cubo3.mover(5, 5, -5);

        mundo.agregarEntidad(cubo3);
        //--------------------------------------------------------------------------------------------------
        // con textura e iluminacion (PBR), tambien mezcla la textura con un color
        QMaterialNodo materialC4 = new QMaterialNodo();

        QPBRColorTextura nodoTexturaC4 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa4", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));
        QPBRColorIluminacion nodoDifusoC4 = new QPBRColorIluminacion();
        QPBRColorIluminacion nodDifusoAzul = new QPBRColorIluminacion(QColor.BLUE);
        QPBRColorMix nodMix = new QPBRColorMix();
        // enlace que une la salida de la textura con con DifuoC4
        QNodoEnlace enlaceC4_1 = new QNodoEnlace(nodoTexturaC4.getSaColor(), nodoDifusoC4.getEnColor());
        //enlace que une la salida de DifusoC4 con la entrada1 de mix
        QNodoEnlace enlaceC4_2 = new QNodoEnlace(nodoDifusoC4.getSaColor(), nodMix.getEnColor1());
        //enlace que une la salida de nodDifusoAzul con la entrada1 de mix
        QNodoEnlace enlaceC4_3 = new QNodoEnlace(nodDifusoAzul.getSaColor(), nodMix.getEnColor2());

        QPBRColorIluminacion ilum1 = new QPBRColorIluminacion(QColor.RED);
        QPBRMaterial nodosalida3 = new QPBRMaterial();
        QNodoEnlace enlace3 = new QNodoEnlace(nodMix.getSaColor(), nodosalida3.getEnColor());

        materialC4.setNodo(nodosalida3);

        QEntidad cubo4 = new QEntidad("pbr.text.ilum.mezcla");
        cubo4.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), materialC4));
        cubo4.mover(-5, 5, -5);

        mundo.agregarEntidad(cubo4);
        //--------------------------------------------------------------------------------------------------
        // con textura e iluminacion (PBR),mexcla 2 texturas
        QMaterialNodo materialC5 = new QMaterialNodo();

        QPBRColorTextura nodoTexturaC5_1 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa5", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));
        QPBRColorTextura nodoTexturaC5_2 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa6", new File(QGlobal.RECURSOS + "texturas/textura3.jpg"))));
        QPBRColorIluminacion nodoDifusoC5_1 = new QPBRColorIluminacion();
        QPBRColorIluminacion nodoDifusoC5_2 = new QPBRColorIluminacion();

        QPBRColorMix nodMixC5 = new QPBRColorMix();
        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlaceC5_1 = new QNodoEnlace(nodoTexturaC5_1.getSaColor(), nodoDifusoC5_1.getEnColor());
        //enlace que une la salida de DifusoC4 con la entrada1 de mix
        QNodoEnlace enlaceC5_2 = new QNodoEnlace(nodoTexturaC5_2.getSaColor(), nodoDifusoC5_2.getEnColor());
        //enlace que une la salida del primer difuso con la entrada1 de mix
        QNodoEnlace enlaceC5_3 = new QNodoEnlace(nodoDifusoC5_1.getSaColor(), nodMixC5.getEnColor1());
        //enlace que une la salida del segundo difuso con la entrada1 de mix
        QNodoEnlace enlaceC5_4 = new QNodoEnlace(nodoDifusoC5_2.getSaColor(), nodMixC5.getEnColor2());

        QPBRMaterial nodosalida5 = new QPBRMaterial();
        QNodoEnlace enlace5 = new QNodoEnlace(nodMixC5.getSaColor(), nodosalida5.getEnColor());

        materialC5.setNodo(nodosalida5);

        QEntidad cubo5 = new QEntidad("pbr.mexcla.texturas");
        cubo5.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), materialC5));
        cubo5.mover(-5, -5, -5);

        mundo.agregarEntidad(cubo5);

        //--------------------------------------------------------------------------------------------------
        // con textura e iluminacion (PBR) con mapa de normales
        QMaterialNodo materialC6 = new QMaterialNodo();

        QPBRColorTextura nodoTexturaC6_1 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa7", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaColor.jpg"))));
        QPBRColorTextura nodoTexturaC6_2 = new QPBRColorTextura(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa8", new File(QGlobal.RECURSOS + "texturas/testNormal/cajaNormal.jpg"))));
        QPBRColorIluminacion nodoDifusoC6_1 = new QPBRColorIluminacion();

        // enlace que une la salida de la textura con con difuso
        QNodoEnlace enlaceC6_1 = new QNodoEnlace(nodoTexturaC6_1.getSaColor(), nodoDifusoC6_1.getEnColor());
        //enlace que une la salida de DifusoC4 con la entrada1 de mix
        QNodoEnlace enlaceC6_2 = new QNodoEnlace(nodoTexturaC6_2.getSaColor(), nodoDifusoC6_1.getEnNormal());
        //enlace que une la salida del primer difuso con la entrada1 de mix

        QPBRMaterial nodosalida6 = new QPBRMaterial();
        QNodoEnlace enlace6 = new QNodoEnlace(nodoDifusoC6_1.getSaColor(), nodosalida6.getEnColor());

        materialC6.setNodo(nodosalida6);

        QEntidad cubo6 = new QEntidad("pbr.text.normales");
        cubo6.agregarComponente(QMaterialUtil.aplicarMaterial(new QCaja(2), materialC6));
        cubo6.mover(0, 0, 0);

        mundo.agregarEntidad(cubo6);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
