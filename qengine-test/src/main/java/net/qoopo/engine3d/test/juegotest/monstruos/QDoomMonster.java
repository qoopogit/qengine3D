/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest.monstruos;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.animacion.QComponenteAnimacion;
import net.qoopo.engine3d.componentes.audio.openal.QBufferSonido;
import net.qoopo.engine3d.componentes.audio.openal.QEmisorAudio;
import net.qoopo.engine3d.componentes.inteligencia.acciones.QAccion;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.util.SerializarUtil;

/**
 *
 * @author alberto
 */
public class QDoomMonster {

    /**
     * Carga un modelo con animaciones y les agrega sonidos y comportamiento
     *
     * @return
     */
    public static QEntidad quakeMonster() {

        QEntidad monstruo = (QEntidad) SerializarUtil.leerObjeto(QGlobal.RECURSOS + "objetos/formato_qengine/quake/qdemon.qengine", 0, true);

        //Carga de audios
        QBufferSonido qdemon_atacar = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/quake/obihb_qdemon_growl.ogg", "qdemon_atacar");
        QBufferSonido qdemon_walk = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/quake/obihb_qwizard_walk.ogg", "qdemon_walk");
        QBufferSonido bufAves = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/aves.ogg", "au_aves");

        QEmisorAudio emisorAudio = new QEmisorAudio(false, false);
        emisorAudio.setPosition(monstruo.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector());
        emisorAudio.setBuffer(bufAves.getBufferId());
        emisorAudio.setReproducirAlInicio(false);
        emisorAudio.setGain(1.0f);
        monstruo.agregarComponente(emisorAudio);

        //animacion idle
        monstruo.agregarComponente(QUtilComponentes.getAlmacenAnimaciones(monstruo).getAnimacion("idle1"));

        QAccion accionIdle = new QAccion("Idle") {
            @Override
            public void ejecutar(Object... parametros) {

                //configura la animacion de caminar
                QUtilComponentes.eliminarComponenteAnimacion(monstruo);
                QComponenteAnimacion anim = QUtilComponentes.getAlmacenAnimaciones(monstruo).getAnimacion("idle1");
                monstruo.agregarComponente(anim);

                try {
                    emisorAudio.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    emisorAudio.setBuffer(qdemon_walk.getBufferId());
                    emisorAudio.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        QAccion caminar = new QAccion("caminar") {
            @Override
            public void ejecutar(Object... parametros) {

                //configura la animacion de caminar
                QUtilComponentes.eliminarComponenteAnimacion(monstruo);
                QComponenteAnimacion anim = QUtilComponentes.getAlmacenAnimaciones(monstruo).getAnimacion("walk1");
                //le quito el loop
                anim.reiniciar();
                anim.setLoop(false);
                monstruo.agregarComponente(anim);

                try {
                    emisorAudio.stop();
                } catch (Exception e) {
                }
                emisorAudio.setBuffer(qdemon_walk.getBufferId());
                emisorAudio.play();

                try {
                    //espero unos 15 segundos para empezar
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(QDoomMonster.class.getName()).log(Level.SEVERE, null, ex);
                }
                accionIdle.ejecutar();
            }
        };

        QAccion atacar = new QAccion("atacar") {
            @Override
            public void ejecutar(Object... parametros) {
                //configura la animacion de caminar
                QUtilComponentes.eliminarComponenteAnimacion(monstruo);

                QComponenteAnimacion anim = QUtilComponentes.getAlmacenAnimaciones(monstruo).getAnimacion("melee1");
                //le quito el loop
                anim.reiniciar();
                anim.setLoop(false);
                monstruo.agregarComponente(anim);
                try {
                    emisorAudio.stop();
                } catch (Exception e) {
                }
                emisorAudio.setBuffer(qdemon_atacar.getBufferId());
                emisorAudio.play();

                try {

                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(QDoomMonster.class.getName()).log(Level.SEVERE, null, ex);
                }

                accionIdle.ejecutar();
            }
        };

        // este hilo va a iterar sobre las acciones, deberia existir una clase de comportamientos que defina la ejecucion o no de las acciones
        Thread hiloTest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //espero unos 15 segundos para empezar
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(QDoomMonster.class.getName()).log(Level.SEVERE, null, ex);
                }
                long tInicio = System.currentTimeMillis();

                //or 2minutos
//                while (System.currentTimeMillis() - tInicio < 60000 * 2) {
                while (true) {
                    try {
                        caminar.ejecutar();
                        Thread.sleep(10000);// espero 10 segundos antes de cambiar de accion
                        atacar.ejecutar();
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(QDoomMonster.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        hiloTest.start();

        return monstruo;
    }

    public static QEntidad quakeMonster2() {

        QEntidad monstruo = (QEntidad) SerializarUtil.leerObjeto(QGlobal.RECURSOS + "objetos/formato_qengine/quake/qshambler.qengine", 0, true);

        //Carga de audios
        QBufferSonido bucAtacar = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/quake/obihb_qdemon_growl.ogg", "qdemon_atacar");

        QBufferSonido bufAves = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/aves.ogg", "au_aves");

        QEmisorAudio emisorAudio = new QEmisorAudio(false, false);
        emisorAudio.setPosition(monstruo.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector());
        emisorAudio.setBuffer(bufAves.getBufferId());
        emisorAudio.setReproducirAlInicio(false);
        emisorAudio.setGain(1.0f);
        monstruo.agregarComponente(emisorAudio);

        //animacion idle
        monstruo.agregarComponente(QUtilComponentes.getAlmacenAnimaciones(monstruo).getAnimacion("idle02"));

        QAccion accionIdle = new QAccion("caminar") {
            @Override
            public void ejecutar(Object... parametros) {

                //configura la animacion de caminar
                QUtilComponentes.eliminarComponenteAnimacion(monstruo);
                QComponenteAnimacion anim = QUtilComponentes.getAlmacenAnimaciones(monstruo).getAnimacion("idle02");
                monstruo.agregarComponente(anim);

                try {
                    emisorAudio.stop();
                } catch (Exception e) {
                }
//                emisorAudio.setBuffer(qdemon_walk.getBufferId());
//                emisorAudio.play();
            }
        };

        QAccion atacar = new QAccion("atacar") {
            @Override
            public void ejecutar(Object... parametros) {
                //configura la animacion de caminar
                QUtilComponentes.eliminarComponenteAnimacion(monstruo);

                QComponenteAnimacion anim = QUtilComponentes.getAlmacenAnimaciones(monstruo).getAnimacion("attack02");
                //le quito el loop
                anim.reiniciar();
                anim.setLoop(false);
                monstruo.agregarComponente(anim);
                try {
                    emisorAudio.stop();
                } catch (Exception e) {
                }
                emisorAudio.setBuffer(bucAtacar.getBufferId());
                emisorAudio.play();

                try {

                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(QDoomMonster.class.getName()).log(Level.SEVERE, null, ex);
                }

                accionIdle.ejecutar();
            }
        };

        // este hilo va a iterar sobre las acciones, deberia existir una clase de comportamientos que defina la ejecucion o no de las acciones
        Thread hiloTest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //espero unos 15 segundos para empezar
                    Thread.sleep(15000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(QDoomMonster.class.getName()).log(Level.SEVERE, null, ex);
                }
                long tInicio = System.currentTimeMillis();

                //or 2minutos
//                while (System.currentTimeMillis() - tInicio < 60000 * 2) {
                while (true) {
                    try {
                        atacar.ejecutar();
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(QDoomMonster.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        hiloTest.start();

        return monstruo;
    }
}
