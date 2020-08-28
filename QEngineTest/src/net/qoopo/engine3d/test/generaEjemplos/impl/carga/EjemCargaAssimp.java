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

            List<File> archivos = new ArrayList<>();

            //------------------------ md5 --------------------------------
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/hellknight/monster.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/bob/boblamp.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/imp/imp.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/zfat/zfat.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/player/player.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/cyberdemon/cyberdemon.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/revenant/revenant.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/archvile/archvile.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/maggot3/maggot3.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/pinky/pinky.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/trite/trite.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/obihb_qshambler/models/obihb/qshambler/md5/qshambler.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/z_obihb_qdemon/models/obihb/qdemon/md5/qdemon.md5mesh"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/obihb_qwizard/models/obihb/qwizard/md5/qwizard.md5mesh"));
//            //---------------- collada ---------------------------
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/vaquero_tuto/model.dae"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/crisys/Samba Dancing.dae"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/swat/Taunt/Taund.dae"));
////            //colada Bot-Y
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/Bot-Y/Idle.dae"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_collada/animaciones_mixamo/Bot-Y/Boxing.dae"));
////            //------- fbx ------------------
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/Bot-Y/Idle.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/Bot-Y/Boxing.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/Bot-Y/Mutant Punch.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/Bot-Y/Silly Dancing.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/Bot-Y/Run.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/swat/Boxing.fbx"));
//            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_fbx/animaciones_mixamo/PandaMale/Taunt.fbx"));
//            //---------- blend ----------------------
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Only_Spider_with_Animations.blend"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Only_Spider_with_Animations.3ds"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Spider.fbx"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Spider_2.fbx"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Low-Poly Spider/Spider_3.fbx"));
////            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_blend/Black Dragon NEW/Dragon2.5_fbx.fbx"));
            int i = 0;
            for (int y = 0; y < 5; y++) {
                for (int x = 0; x < 5; x++) {
                    try {
                        if (i < archivos.size()) {
                            //cargardor ASIMP
                            QEntidad entidad2 = AssimpLoader.cargarAssimpItems(archivos.get(i).getAbsolutePath(), archivos.get(i).getParent()).get(0);
                            entidad2.mover(x * 10, 0, y * 10);
//                    entidad2.escalar(1f, 1f, 1f);
                            entidad2.escalar(0.05f, 0.05f, 0.05f);
//                    entidad2.escalar(0.05f, 0.05f, 0.05f);
//                    entidad2.rotar(Math.toRadians(-180), 0, 0);
                            mundo.agregarEntidad(entidad2);
                            i++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(EjemCargaAssimp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
