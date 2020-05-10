/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.carga.impl.assimp.imp.AssimpLoader;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemploSponza extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;

            for (QEntidad entidad : AssimpLoader.cargarAssimpItems(new File(QGlobal.RECURSOS + "objetos/formato_obj/ESCENARIOS/sponza/sponza.obj"))) {
                entidad.escalar(0.1f, 0.1f, 0.1f);
                this.mundo.agregarEntidad(entidad);
            }

        } catch (Exception ex) {
            Logger.getLogger(EjemploSponza.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
