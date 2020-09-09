/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.carga;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.carga.impl.collada.vThinMatrix.CargaColladaThinkMatrix;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemCargaColladaDAE extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;
            QProcesadorSimple proc = new QProcesadorSimple(QGestorRecursos.cargarTextura("difusa", new File(QGlobal.RECURSOS + "objetos/formato_collada/vaquero_tuto/diffuse.png")));
            File archivo = new File(QGlobal.RECURSOS + "objetos/formato_collada/vaquero_tuto/model.dae");
            QEntidad entidad = CargaColladaThinkMatrix.cargarCollada(archivo);
            //cambio la textura de las geometria cargadas
            for (QComponente comp : entidad.getComponentes()) {
                if (comp instanceof QGeometria) {
                    for (QPrimitiva face : ((QGeometria) comp).listaPrimitivas) {
                        if (face.material instanceof QMaterialBas) {
                            ((QMaterialBas) face.material).setMapaColor(proc);
                        }
                    }
                }
            }
            entidad.rotar(Math.toRadians(-90), 0, 0);
            entidad.mover(-5, 0, 0);            
//            entidad.mover(i * 6, 0, j * 6);
            mundo.agregarEntidad(entidad);

        } catch (Exception ex) {
            Logger.getLogger(EjemCargaColladaDAE.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
