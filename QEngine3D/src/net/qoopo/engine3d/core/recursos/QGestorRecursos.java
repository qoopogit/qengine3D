/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.recursos;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import net.qoopo.engine3d.componentes.audio.openal.QBufferSonido;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.util.ImgReader;
import net.qoopo.engine3d.core.util.QLogger;

/**
 * Gestiona los recursos utilizados por el engine
 *
 * @author alberto
 */
public class QGestorRecursos implements Serializable {

    public static Map<String, Object> mapa = new HashMap<>();

    public static void liberarRecursos() {
        QLogger.info("Liberando recursos");
        try {
            for (Object objeto : mapa.values()) {
                if (objeto instanceof QBufferSonido) {
                    QLogger.info("  Liberando recurso de audio..");
                    ((QBufferSonido) objeto).cleanup();
                } else if (objeto instanceof QTextura) {
                    QLogger.info("  Liberando recurso de textura..");
                    ((QTextura) objeto).destruir();
                }
            }
            mapa.clear();
            QLogger.info("Recursos liberados");
        } catch (Exception e) {

        }
    }

    public static void agregarRecurso(String clave, Object valor) {
        mapa.put(clave, valor);
    }

    public static Object getRecurso(String clave) {
        return mapa.get(clave);
    }

    public static QTextura cargarTextura(String clave, String file) {
        return cargarTextura(clave, new File(file));
    }

    public static QTextura getTextura(String clave) {
        return (QTextura) getRecurso(clave);
    }

    public static QBufferSonido getAudio(String clave) {
        return (QBufferSonido) getRecurso(clave);
    }

    public static QBufferSonido cargarAudio(String archivo, String clave) {
        QBufferSonido bufferAudio = null;
        try {
            QLogger.info("  Cargando audio " + archivo);
            bufferAudio = new QBufferSonido(archivo);
            agregarRecurso(clave, bufferAudio);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bufferAudio;
    }

    public static QTextura cargarTextura(String clave, File file) {
        try {
            QTextura text = new QTextura();
            QLogger.info("  Cargando textura " + file.getName());
//            System.out.println("  Cargando textura " + file.getName());
            text.cargarTextura(ImgReader.leerImagen(file));

            agregarRecurso(clave, text);
            return text;
        } catch (IOException ex) {
            System.out.println("Error al cargar la textura " + file.getAbsolutePath());
            ex.printStackTrace();
        }
        return null;
    }

}
