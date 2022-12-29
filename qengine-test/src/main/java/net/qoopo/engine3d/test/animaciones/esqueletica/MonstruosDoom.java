/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.animaciones.esqueletica;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.QEngine3D;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.util.SerializarUtil;

/**
 *
 * @author alberto
 */
public class MonstruosDoom {

    public static void main(String[] args) {
        QEngine3D motor = new QEngine3D();

        QCamara cam = new QCamara();
        cam.frustrumLejos = 1000;
        cam.lookAtTarget(new QVector3(250, 250, 250), QVector3.zero, QVector3.unitario_y.clone());
        motor.getEscena().agregarEntidad(cam);
        motor.configurarRenderer(800, 600, cam);
        motor.getRenderer().opciones.setDibujarLuces(false);
        motor.getRenderer().setCargando(true);
        motor.setIniciarAudio(false);
        motor.setIniciarFisica(false);
        motor.setIniciarDiaNoche(false);
        motor.setearSalirESC();
        motor.iniciar();

        // carga las animaciones
        QEntidad doomMonster = (QEntidad) SerializarUtil.leerObjeto(QGlobal.RECURSOS + "objetos/formato_qengine/doom/hellknight.qengine", 0, true);
        doomMonster.mover(-100, 0, 0);
        motor.getEscena().agregarEntidad(doomMonster);

        QEntidad quake1 = (QEntidad) SerializarUtil.leerObjeto(QGlobal.RECURSOS + "objetos/formato_qengine/quake/qdemon.qengine", 0, true);
        quake1.mover(100, 0, 0);
        quake1.agregarComponente(QUtilComponentes.getAlmacenAnimaciones(quake1).getAnimacion("idle1"));
        motor.getEscena().agregarEntidad(quake1);

        QEntidad quake2 = (QEntidad) SerializarUtil.leerObjeto(QGlobal.RECURSOS + "objetos/formato_qengine/quake/qshambler.qengine", 0, true);
        quake2.mover(0, 0, -100);
        quake2.agregarComponente(QUtilComponentes.getAlmacenAnimaciones(quake2).getAnimacion("idle02"));
        motor.getEscena().agregarEntidad(quake2);

        QEntidad quake3 = (QEntidad) SerializarUtil.leerObjeto(QGlobal.RECURSOS + "objetos/formato_qengine/quake/qwizard.qengine", 0, true);
        quake3.mover(0, 40, 100);
        quake3.agregarComponente(QUtilComponentes.getAlmacenAnimaciones(quake3).getAnimacion("idle"));
        motor.getEscena().agregarEntidad(quake3);

        QLuz sol = new QLuzDireccional(1f, QColor.WHITE, 200, new QVector3(-0.5f, -1f, 0.5f), true, true);
        sol.setProyectarSombras(true);
        QEntidad luzEntidad = new QEntidad("Sol");
        luzEntidad.agregarComponente(sol);
        motor.getEscena().agregarEntidad(luzEntidad);

        QEntidad piso = new QEntidad("piso");
//        piso.rotar(Math.toRadians(5), Math.toRadians(5), 0);
        piso.agregarComponente(new QCaja(0.f, 500, 500));
        motor.getEscena().agregarEntidad(piso);

        motor.getRenderer().setCargando(false);

    }
}
