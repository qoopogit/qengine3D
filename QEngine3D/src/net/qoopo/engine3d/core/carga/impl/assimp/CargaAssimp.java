/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga.impl.assimp;

import java.io.File;
import net.qoopo.engine3d.core.carga.CargaObjeto;
import net.qoopo.engine3d.core.carga.impl.assimp.imp.AssimpLoader;

/**
 *
 * @author alberto
 */
public class CargaAssimp extends CargaObjeto {

    public CargaAssimp() {
    }

    @Override
    public void cargar(File archivo) {
        this.archivo = archivo;
        start();
    }

    @Override
    public void run() {
        try {

//            QEntidad entidad = new QEntidad();
//            QGeometria[] tmpLst = StaticMeshesLoader.load(archivo.getAbsolutePath(), archivo.getParent());
//            for (QGeometria malla : tmpLst) {
//                entidad.agregarComponente(malla);
//            }
//            this.lista.add(entidad);
            this.lista.addAll(AssimpLoader.cargarAssimpItems(archivo));

            if (accionFinal != null) {
                accionFinal.ejecutar();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
