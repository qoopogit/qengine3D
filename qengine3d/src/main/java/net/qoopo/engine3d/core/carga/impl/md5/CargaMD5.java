/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga.impl.md5;

import java.io.File;
import javax.swing.JProgressBar;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.carga.CargaObjeto;

/**
 *
 * @author alberto
 */
public class CargaMD5 extends CargaObjeto {

    public CargaMD5() {
    }

    @Override
    public void cargar(File archivo) {
        this.archivo = archivo;
        start();
    }

    @Override
    public void run() {
        try {

            if (progreso == null) {
                progreso = new JProgressBar();
            }

            //malla
            progreso.setValue(0);
            QEntidad entidad = MD5Loader.cargar(archivo.getAbsolutePath());
            entidad.rotar(Math.toRadians(-90), 0, 0);
            progreso.setValue(100);
            lista.add(entidad);

            if (accionFinal != null) {
                accionFinal.ejecutar();
            }
            progreso.setValue(0);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
