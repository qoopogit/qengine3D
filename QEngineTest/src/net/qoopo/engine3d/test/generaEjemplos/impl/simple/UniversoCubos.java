/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.io.File;
import java.util.Random;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 *
 * @author alberto
 */
public class UniversoCubos extends GeneraEjemplo {

    private QMaterialBas material = null;

    private void cargarMaterial() {
        material = null;
        try {
//            int colorTransparencia = -1;
//            material = new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/caja.jpg"))), 64);
            material = new QMaterialBas();
            material.setMapaDifusa(new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "texturas/Skybox_example.png"))));
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
        float tamUniverso = 100;

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

        for (int i = 0; i < 500; i++) {
            QEntidad cubo = new QEntidad("Cubo [" + i + "]");
            cubo.mover(rnd.nextFloat() * tamUniverso * 2 - tamUniverso, rnd.nextFloat() * tamUniverso * 2 - tamUniverso, rnd.nextFloat() * tamUniverso * 2 - tamUniverso);
//            actual = colores[rnd.nextInt(colores.length)];
//            ((QMaterialBas) geometria.listaPrimitivas[0].material).setColorDifusa(new QColor(actual));
            cubo.agregarComponente(geometria);
            mundo.agregarEntidad(cubo);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
