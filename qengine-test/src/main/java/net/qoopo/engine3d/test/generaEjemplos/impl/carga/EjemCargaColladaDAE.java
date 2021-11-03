/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.carga;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.carga.impl.assimp.imp.AssimpLoader;
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
                    for (QPrimitiva face : ((QGeometria) comp).primitivas) {
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
            
            
            
             List<File> archivos = new ArrayList<>();

//            //---------------- collada ---------------------------
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/vaquero_tuto/model.dae"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/crisys/Samba Dancing.dae"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/swat/Taunt/Taund.dae"));
////            //colada Bot-Y
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/Bot-Y/Idle.dae"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/Bot-Y/Boxing.dae"));

            int i = 0;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    try {
                        if (i < archivos.size()) {
                            //cargardor ASIMP
                            QEntidad objeto = CargaColladaThinkMatrix.cargarCollada(archivos.get(i));
                            objeto.rotar(Math.toRadians(-90), 0, 0);
                            objeto.mover(x * 10, 0, y * 10);
                            objeto.escalar(0.05f, 0.05f, 0.05f);
                            mundo.agregarEntidad(objeto);
                            i++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(EjemCargaColladaDAE.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
