package net.qoopo.engine3d.core.carga.impl.assimp.imp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
//import org.lwjglb.engine.graph.Texture;

public class TextureCache {

    private static TextureCache INSTANCE;

    private Map<String, QTextura> texturesMap;

    private TextureCache() {
        texturesMap = new HashMap<>();
    }

    public static synchronized TextureCache getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TextureCache();
        }
        return INSTANCE;
    }

    public QTextura getTexture(String path) throws Exception {
        QTextura texture = texturesMap.get(path);
        if (texture == null) {
//            texture = new QTextura(path);
            texture = QGestorRecursos.cargarTextura(path, new File(path));
            if (texture != null) {
                texturesMap.put(path, texture);
            }
        }
        return texture;
    }
}
