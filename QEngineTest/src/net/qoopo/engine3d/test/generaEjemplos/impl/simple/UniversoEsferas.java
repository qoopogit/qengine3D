/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.util.Random;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 *
 * @author alberto
 */
public class UniversoEsferas extends GeneraEjemplo {

    private QMaterialBas material = null;

    private void cargarMaterial() {
        material = null;
        try {
//            int colorTransparencia = -1;
            material = new QMaterialBas(QGestorRecursos.cargarTextura("texEsfera", QGlobal.RECURSOS + "texturas/textura3.jpg"), 64);
//            material.texturaColorTransparente = colorTransparencia;
//            if (colorTransparencia != -1) {
//                material.transAlfa = 0.99f;// el objeto tiene una trasnparencia 
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

        QEntidad luzEntidad = new QEntidad("luz");

        //luces
        QLuz luz = new QLuzDireccional(1.5f, QColor.WHITE, true, 1, new QVector3(-1, -1, 1));

        luzEntidad.agregarComponente(luz);
        mundo.agregarEntidad(luzEntidad);

//        QLuz luz2 = new QLuz(QLuz.LUZ_PUNTUAL, 1.5f, 0, 255, 0, 3f, 2f, 0, true);
//        mundo.agregarLuz(luz2);
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
        QGeometria geometria = new QEsfera(1);
        QMaterialUtil.aplicarMaterial(geometria, material);
        for (int i = 0; i < 100; i++) {
            QEntidad esfera = new QEntidad("Esfera [" + i + "]");
            esfera.mover(rnd.nextFloat() * tamUniverso * 2 - tamUniverso, rnd.nextFloat() * tamUniverso * 2 - tamUniverso, rnd.nextFloat() * tamUniverso * 2 - tamUniverso);
//            actual = colores[rnd.nextInt(colores.length)];
//            ((QMaterialBas) geometria.listaPrimitivas[0].material).setColorDifusa(new QColor(actual));
            esfera.agregarComponente(geometria);
            mundo.agregarEntidad(esfera);
        }
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
