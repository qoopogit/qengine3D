/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.particulas;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlanoBillboard;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.juegotest.generadores.GenFuego;

/**
 *
 *
 * @author alberto
 */
public class Fuego extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

//        QEntidad fogata = GenFuego.crearFogataConLuces();
        QEntidad fogata = GenFuego.crearFogata1();
        mundo.agregarEntidad(fogata);

        QEntidad plano = new QEntidad("plano");
        plano.agregarComponente(new QPlano(10, 10));
        mundo.agregarEntidad(plano);
//        QEntidad plano2 = new QEntidad("plano2");
//        plano2.agregarComponente(new QPlanoBillboard(10, 10));
//        mundo.agregarEntidad(plano2);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
