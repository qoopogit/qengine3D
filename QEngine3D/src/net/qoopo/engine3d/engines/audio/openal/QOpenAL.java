/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.audio.openal;

//import org.lwjgl.openal.AL;
import net.qoopo.engine3d.componentes.audio.openal.QSoundListener;
import net.qoopo.engine3d.componentes.audio.openal.QEmisorAudio;
import net.qoopo.engine3d.engines.audio.QMotorAudio;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import org.lwjgl.openal.AL11;

/**
 * Motor de audio usando OpenAL
 * @author alberto
 */
public class QOpenAL extends QMotorAudio {

    private SoundManager manager;

    public QOpenAL(QEscena escena) {
        super(escena);
        manager = new SoundManager();
    }

    @Override
    public void detener() {
        super.detener(); //To change body of generated methods, choose Tools | Templates.
        manager.cleanup();
    }

    @Override
    public void iniciar() {
        try {
            manager.init();
            manager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //        cargarSonidos();
    }

    @Override
    public long update() {
        try {
            long t = System.currentTimeMillis();
            //verifico si exiten nuevos componentes que deba agregar/eliminar/modificar
            for (QEntidad entidad : escena.getListaEntidades()) {
                if (entidad.isRenderizar()) {
                    for (QComponente componente : entidad.getComponentes()) {
                        if (componente instanceof QEmisorAudio) {
                            if (!manager.contieneSource(componente.entidad.getNombre())) {
                                manager.addSoundSource(componente.entidad.getNombre(), (QEmisorAudio) componente);
                                if (((QEmisorAudio) componente).isReproducirAlInicio()) {
                                    ((QEmisorAudio) componente).play();
                                }
                            }
                            //se actualiza la coordenada de sonido
                            ((QEmisorAudio) componente).setPosition(entidad.getMatrizTransformacion(t).toTranslationVector());
                        }

                        //se comporta como componente aunq solo debe haber un listener en todo el escena
                        if (componente instanceof QSoundListener) {
                            if (manager.getListener() == null) {
                                manager.setListener((QSoundListener) componente);
                            } else {// actualiza la ubicacion del listener
                                manager.getListener().setPosition(entidad.getTransformacion().getTraslacion());
                                manager.getListener().setOrientation(entidad.getDireccion(), entidad.getArriba());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        tiempoPrevio = System.currentTimeMillis();
        return tiempoPrevio;
    }

//    private void cargarSonidos() {
//        try {
//            QBufferSonido buffBack = new QBufferSonido(Global.RECURSOS + "audio/background.ogg");
//            manager.addSoundBuffer(buffBack);
//            QEmisorAudio sourceBack = new QEmisorAudio(true, true);
//            sourceBack.setBuffer(buffBack.getBufferId());
//            manager.addSoundSource("background", sourceBack);
//
//            QBufferSonido buffBeep = new QBufferSonido(Global.RECURSOS + "audio/beep.ogg");
//            manager.addSoundBuffer(buffBeep);
//            QEmisorAudio sourceBeep = new QEmisorAudio(false, true);
//            sourceBeep.setBuffer(buffBeep.getBufferId());
//            manager.addSoundSource("beep", sourceBeep);
//
//            QBufferSonido buffFire = new QBufferSonido(Global.RECURSOS + "/audio/fire.ogg");
//            manager.addSoundBuffer(buffFire);
//            QEmisorAudio sourceFire = new QEmisorAudio(true, false);
////            Vector3f pos = particleEmitter.getBaseParticle().getPosition();
//            QVector3 pos = new QVector3(-5, 0, 2);
//            sourceFire.setPosition(pos);
//            sourceFire.setBuffer(buffFire.getBufferId());
//            manager.addSoundSource("fuego", sourceFire);
//            sourceFire.play();
//
////            manager.setListener(new QSoundListener(new QVector3(0, 0, 0)));
////            sourceBack.play();
//        } catch (Exception ex) {
//            Logger.getLogger(QOpenAL.class.getName()).log(Level.SEVERE, null, ex);
//            ex.printStackTrace();
//        }
//    }
}
