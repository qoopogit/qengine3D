/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 *
 * @author alberto
 */
public class EjemploSol extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QEntidad sol = new QEntidad("Sol");
        sol.rotar(Math.toRadians(45), 0, Math.toRadians(-45));
        sol.agregarComponente(new QLuzDireccional(1.5f, QColor.WHITE, 50, new QVector3(0, -1f, 0), true, true));
        mundo.agregarEntidad(sol);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
