/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 *
 * @author alberto
 */
public class EjemploLuces extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;
        float d = 7;
        int n = 1;
        d = d * 2 / n;
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                QEntidad objeto = new QEntidad("Luz");
                objeto.agregarComponente(new QLuzPuntual(3.0f, QColor.WHITE, 100, false, false));
//                objeto.mover(i * d - ((n - 1) * d / 2), j * d - ((n - 1) * d / 2), 10);
                objeto.mover(i * d - (n * d / 2), j * d - (n * d / 2), 10);
                mundo.agregarEntidad(objeto);
            }
        }
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
