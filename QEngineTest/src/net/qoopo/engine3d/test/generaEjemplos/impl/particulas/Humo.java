/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.particulas;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.modificadores.particulas.humo.QEmisorHumo;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 *
 * @author alberto
 */
public class Humo extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QEntidad emisorHumo = new QEntidad("Emisor");

        AABB ambitoHumo = new AABB(new QVertice(-0.15f, 0, -0.15f, 1), new QVertice(.15f, 3.5f, .15f, 1));//
        QEmisorHumo emisor = new QEmisorHumo(ambitoHumo, 5000, 400, 4);
        emisorHumo.agregarComponente(emisor);
        emisorHumo.mover(0, 1.5f, 0);

        mundo.agregarEntidad(emisorHumo);

        QEntidad plano = new QEntidad("plano");
        plano.agregarComponente(new QPlano(10, 10));
        mundo.agregarEntidad(plano);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
