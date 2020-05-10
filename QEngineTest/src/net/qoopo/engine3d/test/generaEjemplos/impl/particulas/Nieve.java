/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.particulas;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.modificadores.particulas.nieve.QEmisorNieve;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 *
 * @author alberto
 */
public class Nieve extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;
//        mundo.agregarLuz(new QLuz(0, 2, 128, 255, 0, 1.5f, -.8f, 1.5f, true));
//        mundo.agregarLuz(new QLuz(QLuz.TYPE_POINT, 2.5f, 255, 255, 255, 0, 0.5f, 5f, true));

//** Crea un emisor de nieve, se define el ambito
        QEntidad emisor = new QEntidad("Emisor");

        AABB ambito = new AABB(new QVertice(-10, 0, -10), new QVertice(10, 10, 10));//
        QEmisorNieve emisorNieve = new QEmisorNieve(ambito, 300, 1000, 5, QVector3.unitario_y.clone().multiply(-1));
        emisor.agregarComponente(emisorNieve);
        mundo.agregarEntidad(emisor);

        QEntidad plano = new QEntidad("plano");
        plano.agregarComponente(new QPlano(10, 10));
        mundo.agregarEntidad(plano);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
