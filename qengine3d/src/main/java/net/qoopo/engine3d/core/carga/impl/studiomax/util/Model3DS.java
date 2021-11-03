package net.qoopo.engine3d.core.carga.impl.studiomax.util;

import java.util.LinkedList;

/**
 * Representation of the 3d model loaded from the 3ds file
 *
 * @author Kjetil �ster�s
 */
public class Model3DS {

    public LinkedList<ModelObject> objects = new LinkedList<ModelObject>();

    public ModelObject newModelObject(String name) {
        ModelObject object = new ModelObject(name);
        objects.push(object);
        return object;
    }
}
