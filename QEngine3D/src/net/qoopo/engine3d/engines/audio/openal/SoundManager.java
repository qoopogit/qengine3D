package net.qoopo.engine3d.engines.audio.openal;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.audio.openal.QBufferSonido;
import net.qoopo.engine3d.componentes.audio.openal.QEmisorAudio;
import net.qoopo.engine3d.componentes.audio.openal.QSoundListener;
import org.joml.Matrix4f;
import org.lwjgl.openal.AL;
import static org.lwjgl.openal.AL10.alDistanceModel;
//import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import static org.lwjgl.openal.ALC10.*;
import org.lwjgl.openal.ALCCapabilities;
import static org.lwjgl.system.MemoryUtil.NULL;

public class SoundManager {

    private long device;
    private long context;
    private QSoundListener listener;
    private final List<QBufferSonido> soundBufferList;
    private final Map<String, QEmisorAudio> soundSourceMap;
    private final Matrix4f cameraMatrix;

    public SoundManager() {
        soundBufferList = new ArrayList<>();
        soundSourceMap = new HashMap<>();
        cameraMatrix = new Matrix4f();
    }

    public void init() throws Exception {
        this.device = alcOpenDevice((ByteBuffer) null);
        if (device == NULL) {
            throw new IllegalStateException("Failed to open the default OpenAL device.");
        }
        ALCCapabilities deviceCaps = ALC.createCapabilities(device);
        this.context = alcCreateContext(device, (IntBuffer) null);
        if (context == NULL) {
            throw new IllegalStateException("Failed to create OpenAL context.");
        }
        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCaps);
    }

    public void addSoundSource(String name, QEmisorAudio soundSource) {
        this.soundSourceMap.put(name, soundSource);
    }

    public boolean contieneSource(String name) {
        return this.soundSourceMap.containsKey(name);
    }

    public QEmisorAudio getSoundSource(String name) {
        return this.soundSourceMap.get(name);
    }

    public void playSoundSource(String name) {
        QEmisorAudio soundSource = this.soundSourceMap.get(name);
        if (soundSource != null && !soundSource.isPlaying()) {
            soundSource.play();
        }
    }

    public void removeSoundSource(String name) {
        this.soundSourceMap.remove(name);
    }

    public void addSoundBuffer(QBufferSonido soundBuffer) {
        this.soundBufferList.add(soundBuffer);
    }

    public QSoundListener getListener() {
        return this.listener;
    }

    public void setListener(QSoundListener listener) {
        this.listener = listener;
    }

    public void updateListenerPosition(QEntidad entidad) {
        // Update camera matrix with camera data
//        Transformation.updateGenericViewMatrix(camera.getPosition(), camera.getRotation(), cameraMatrix);        
        listener.setPosition(entidad.getTransformacion().getTraslacion());
        listener.setOrientation(entidad.getDireccion(), entidad.getArriba());
    }

    public void setAttenuationModel(int model) {
        alDistanceModel(model);
    }

    public void cleanup() {
        for (QEmisorAudio soundSource : soundSourceMap.values()) {
            soundSource.cleanup();
        }
        soundSourceMap.clear();
        for (QBufferSonido soundBuffer : soundBufferList) {
            soundBuffer.cleanup();
        }
        soundBufferList.clear();
        if (context != NULL) {
            alcDestroyContext(context);
        }
        if (device != NULL) {
            alcCloseDevice(device);
        }
    }
}
