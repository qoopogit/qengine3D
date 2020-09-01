/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemplRotarItems extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;

            //uso esta lista en lugar del mundo par arotar.. para rotar solo aquellas entidades creadas antes de llamar a esta clase.. y asi evitar rotar las posteriores como terrenos o pisos que no desean ser rotados
            List<QEntidad> lista = new ArrayList<>();

            for (QEntidad entidad : mundo.getListaEntidades()) {
                lista.add(entidad);
            }

            Thread hilo = new Thread(new Runnable() {
                @Override
                public void run() {

                    float angulo = (float) Math.toRadians(10);
//        float angz = 0;

                    while (true) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                        }
                        for (QEntidad entidad : lista) {
                            entidad.rotar(angulo, angulo, angulo);
                        }
                    }
                }
            });
            hilo.start();

        } catch (Exception ex) {
            Logger.getLogger(EjemplRotarItems.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
