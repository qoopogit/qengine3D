/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.animaciones.esqueletica;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.carga.impl.md5.MD5Loader;
import net.qoopo.engine3d.core.util.SerializarUtil;

/**
 * Usada para ejecutar la actualizacion de los objetos de doom al formato
 * qengine
 *
 * @author alberto
 */
public class ActualizarMonstruosDoom {

    private static void serializar(File archivo, String salida) {
        try {
            QEntidad entidad = MD5Loader.cargar(archivo.getAbsolutePath());
            entidad.rotar(Math.toRadians(-90), 0, 0);
            SerializarUtil.agregarObjeto(salida, entidad, false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/hellknight/monster.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/hellknight.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/bob/boblamp.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/boblamp.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/cyberdemon/cyberdemon.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/cyberdemon.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/revenant/revenant.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/revenant.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/zfat/zfat.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/zfat.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/player/player.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/player.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/imp/imp.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/imp.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/pinky/pinky.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/pinky.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/trite/trite.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/trite.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/DOOM_MONSTERS/maggot3/maggot3.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/doom/maggot3.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/obihb_qshambler/models/obihb/qshambler/md5/qshambler.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/quake/qshambler.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/z_obihb_qdemon/models/obihb/qdemon/md5/qdemon.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/quake/qdemon.qengine");
        serializar(new File(QGlobal.RECURSOS + "objetos/formato_md5/QUAKE/obihb_qwizard/models/obihb/qwizard/md5/qwizard.md5mesh"), QGlobal.RECURSOS + "objetos/formato_qengine/quake/qwizard.qengine");

    }

}
