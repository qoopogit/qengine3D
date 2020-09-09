/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.textura;

import java.io.File;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlanoBillboard;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorAtlasSecuencial;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjmTexturaTransparente extends GeneraEjemplo {

    private QMaterialBas crearMaterial() {
        QMaterialBas material = null;
        try {
            QColor colorTransparencia = QColor.BLACK;
            material = new QMaterialBas();
            QProcesadorAtlasSecuencial proc = new QProcesadorAtlasSecuencial(
                    //                    new QTextura(ImageIO.read(new File(QGlobal.RECURSOS+"texturas/humo/smoke_atlas_1.png"))),
                    new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/fuego/fire-texture-atlas_2.png"))),
                    8, 4, 10);
            material.setMapaColor(proc);
            material.setColorTransparente(colorTransparencia);

            material.setTransparencia(true);
            material.setTransAlfa(0.90f);// el objeto tiene una trasnparencia 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return material;
    }

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QMaterialBas mat = crearMaterial();

        QEntidad plano1 = new QEntidad("Plano");
        plano1.setBillboard(true);
        plano1.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBillboard(1f, 1f), mat));
        plano1.mover(0, 0.5f, 0);
        mundo.agregarEntidad(plano1);

        QEntidad plano2 = new QEntidad("Plano");
        plano2.setBillboard(true);
        plano2.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBillboard(1f, 1f), mat));
        plano2.mover(0, 0.5f, 0.25f);
        mundo.agregarEntidad(plano2);

        QEntidad plano3 = new QEntidad("Plano");
        plano3.setBillboard(true);
        plano3.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBillboard(1f, 1f), mat));
        plano3.mover(0, 0.5f, 0.5f);
        mundo.agregarEntidad(plano3);

//        QEntidad plano4 = new QEntidad("Plano");
//        plano4.agregarComponente(QMaterialUtil.aplicarTexturaPlano(new QPlano(1f, 1f),mat, 1));
//        plano4.mover(0.25f, 0.5f, 0.3f);
//        mundo.agregarEntidad(plano4);
//
//        QEntidad plano5 = new QEntidad("Plano");
//        plano5.agregarComponente(QMaterialUtil.aplicarTexturaPlano(new QPlano(1f, 1f),mat, 1));
//        plano5.mover(-0.25f, 0.5f, 0.22f);
//        mundo.agregarEntidad(plano5);
        QEntidad plano = new QEntidad("Piso");
        plano.agregarComponente(QMaterialUtil.aplicarColor(new QPlano(10, 10), 1, 1, 1, 0, 0, 1, 1, 61));

        mundo.agregarEntidad(plano);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
