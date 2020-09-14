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
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.carga.impl.md5.MD5Loader;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemCargaMD5 extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;

            //Cargador propio
            List<File> archivos = new ArrayList<>();
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/hellknight/monster.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/bob/boblamp.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/imp/imp.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/zfat/zfat.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/player/player.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/cyberdemon/cyberdemon.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/revenant/revenant.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/archvile/archvile.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/maggot3/maggot3.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/pinky/pinky.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/trite/trite.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/obihb_qshambler/models/obihb/qshambler/md5/qshambler.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/z_obihb_qdemon/models/obihb/qdemon/md5/qdemon.md5mesh"));
            archivos.add(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/obihb_qwizard/models/obihb/qwizard/md5/qwizard.md5mesh"));

            for (int i = 0; i < archivos.size(); i++) {
                QEntidad entidad = MD5Loader.cargar(archivos.get(i).getAbsolutePath());
                entidad.mover(i * 10, 0, 2);
                entidad.escalar(0.05f, 0.05f, 0.05f);
                entidad.rotar(Math.toRadians(-90), 0, 0);
                mundo.agregarEntidad(entidad);
            }

        } catch (Exception ex) {
            Logger.getLogger(EjemCargaMD5.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
