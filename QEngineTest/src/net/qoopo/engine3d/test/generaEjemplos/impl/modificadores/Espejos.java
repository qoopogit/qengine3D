/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.modificadores;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.test.generaEjemplos.impl.animado.EsferaAnimada;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlanoBillboard;
import net.qoopo.engine3d.componentes.modificadores.procesadores.espejo.QReflexionPlanar;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class Espejos extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        int anchoEspejos = 200;
        int altoEspejos = 200;
        //agrego los espejos
        QEntidad espejo1 = new QEntidad("espejo1");
        QTextura texEspejo1 = new QTextura();
        espejo1.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBillboard(2, 2), new QMaterialBas(texEspejo1) ));
        espejo1.agregarComponente(new QReflexionPlanar(texEspejo1, mundo,
                        anchoEspejos,
                        altoEspejos));
        espejo1.mover(-mundo.UM.convertirPixel(2, QUnidadMedida.METRO), 1, -mundo.UM.convertirPixel(2, QUnidadMedida.METRO));

        espejo1.rotar(0, (float) Math.toRadians(45), 0);

        mundo.agregarEntidad(espejo1);

        QEntidad espejo2 = new QEntidad("espejo2");
        QTextura textEspejo2 = new QTextura();
        espejo2.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBillboard(2, 2), new QMaterialBas(textEspejo2)));
        espejo2.agregarComponente(new QReflexionPlanar(textEspejo2, mundo,
                        anchoEspejos,
                        altoEspejos));
        espejo2.mover(mundo.UM.convertirPixel(2, QUnidadMedida.METRO), 1, mundo.UM.convertirPixel(2, QUnidadMedida.METRO));

        espejo2.rotar(0, (float) Math.toRadians(135), 0);

        mundo.agregarEntidad(espejo2);

        QEntidad espejo3 = new QEntidad("espejo3");
        QTextura textEspejo3 = new QTextura();
        espejo3.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlanoBillboard(2, 2), new QMaterialBas(textEspejo3)));
        espejo3.agregarComponente(new QReflexionPlanar(textEspejo3, mundo,
                        anchoEspejos,
                        altoEspejos));
        espejo3.mover(0, 3, -3);

        espejo3.rotar((float) Math.toRadians(35), 0, 0);
        mundo.agregarEntidad(espejo3);

        QEntidad espejoCentral = new QEntidad("espejoEsfera");
        QTextura textEspejoEsfera = new QTextura();
        QMaterialBas mat = new QMaterialBas();
        mat.setMapaColor(new QProcesadorSimple(textEspejoEsfera));
        espejoCentral.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.5f), mat));
        espejoCentral.agregarComponente(new QReflexionPlanar(textEspejoEsfera, mundo,
                        anchoEspejos,
                        altoEspejos));
        espejoCentral.mover(0, 0.5f, 0);

        espejoCentral.rotar(0, (float) Math.toRadians(180), 0);

        mundo.agregarEntidad(espejoCentral);

        //lugo realizo agrego otromundo 
        //         QLuz luz = new QLuz(QLuz.TYPE_POINT, 8.5f, 255, 0, 128, 0, 2f, 0, true);
        GeneraEjemplo ejemplo;
//        ejemplo = new EjmTexturaCubo();
        ejemplo = new EsferaAnimada();
        ejemplo.iniciar(mundo);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
