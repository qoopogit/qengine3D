/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga.impl.qengine;

import java.io.File;
import javax.swing.JProgressBar;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.carga.CargaObjeto;
import net.qoopo.engine3d.core.util.SerializarUtil;

/**
 *
 * @author alberto
 */
public class CargaQENGINE2 extends CargaObjeto {

    public CargaQENGINE2() {
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
            progreso.setValue(0);
//            QEntidad entidad = (QEntidad) SerializarUtil.leerObjeto(archivo.getAbsolutePath());

            int tamanio = SerializarUtil.leerObjetos(archivo.getAbsolutePath()).size();
            for (int i = 0; i < tamanio; i++) {
                lista.add((QEntidad) SerializarUtil.leerObjeto(archivo.getAbsolutePath(), i, true));
                progreso.setValue((int) (100 * (float) i / (float) tamanio));
            }

            progreso.setValue(100);

            if (accionFinal != null) {
                accionFinal.ejecutar();
            }
            progreso.setValue(0);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
