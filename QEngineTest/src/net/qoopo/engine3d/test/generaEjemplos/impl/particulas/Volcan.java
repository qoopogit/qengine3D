/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.particulas;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.modificadores.particulas.fuego.QEmisorVolcan;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 *
 * @author alberto
 */
public class Volcan extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QEntidad volcan = new QEntidad("fogata");
        AABB ambitoVolcan = new AABB(new QVertice(-9, 0, -1), new QVertice(-8, 2, 1));//
        QEmisorVolcan emisorVolcan = new QEmisorVolcan(ambitoVolcan, 300, 5000, 25, mundo.gravedad.clone().multiply(-1));
        volcan.agregarComponente(emisorVolcan);
        volcan.getTransformacion().trasladar(0, 0.5f, 0);

//        fogata.agregarComponente(new QLuz(QLuz.TYPE_POINT, 0.8f, 255, 255, 0, true));
//        fogata.agregarComponente(new QLuz(QLuz.TYPE_POINT, 0.8f, 255, 255, 255, true));
//        fogata.agregarComponente(new QLuz(QLuz.TYPE_POINT, 0.8f, 255, 0, 0, true));
        mundo.agregarEntidad(volcan);

        QEntidad plano = new QEntidad("plano");
        plano.agregarComponente(new QPlano(10, 10));

        plano.rotar((float) Math.toRadians(90), 0, 0);
        mundo.agregarEntidad(plano);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
