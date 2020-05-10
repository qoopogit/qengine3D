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
import net.qoopo.engine3d.core.carga.impl.assimp.imp.AssimpLoader;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemCargaAssimp extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;

            //Cargador propio
            List<File> archivos = new ArrayList<>();
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/hellknight/monster.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/bob/boblamp.md5mesh"));

            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/vaquero_tuto/model.dae"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/crisys/Samba Dancing.dae"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/swat/Taunt/Taund.dae"));
//            //colada Bot-Y
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/Bot-Y/Idle.dae"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/Bot-Y/Boxing.dae"));
//            //fbx Bot-Y
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/Bot-Y/Idle.fbx"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/Bot-Y/Boxing.fbx"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/swat/Boxing.fbx"));

//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Only_Spider_with_Animations.blend"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Only_Spider_with_Animations.3ds"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Spider.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Spider_2.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Spider_3.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Black Dragon NEW/Dragon2.5_fbx.fbx"));
            for (int i = 0; i < archivos.size(); i++) {

                try {
                    //cargardor ASIMP
                    QEntidad entidad2 = AssimpLoader.cargarAssimpItems(archivos.get(i).getAbsolutePath(), archivos.get(i).getParent()).get(0);
                    entidad2.mover(i * 10, 0, 6);
//                    entidad2.escalar(1f, 1f, 1f);
                    entidad2.escalar(0.1f, 0.1f, 0.1f);
//                    entidad2.escalar(0.05f, 0.05f, 0.05f);
//                    entidad2.rotar(Math.toRadians(-180), 0, 0);
                    mundo.agregarEntidad(entidad2);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

//            mundo.setLuzAmbiente(0.25f);
        } catch (Exception ex) {
            Logger.getLogger(EjemCargaAssimp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
