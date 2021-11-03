/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos.brazo;

import javax.media.j3d.*;
import javax.vecmath.*;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Cylinder;

public class Pieza1 {

    BranchGroup bg;

    public Pieza1(Appearance pintura) {
        bg = new BranchGroup();
        Transform3D ro = new Transform3D();
        Transform3D tr = new Transform3D();
        TransformGroup tg1 = new TransformGroup();
        TransformGroup tg2 = new TransformGroup();
        TransformGroup tg3 = new TransformGroup();
        TransformGroup tg4 = new TransformGroup();
        TransformGroup tg5 = new TransformGroup();

        Cylinder rueda1 = new Cylinder(0.05f, 0.5f, Cylinder.GENERATE_NORMALS, pintura);
        Cylinder rueda2 = new Cylinder(0.05f, 0.5f, Cylinder.GENERATE_NORMALS, pintura);
        Box piso = new Box(0.4f, 0.25f, 0.05f, Box.GENERATE_NORMALS, pintura);
        Box pie = new Box(0.15f, 0.15f, 0.4f, Box.GENERATE_NORMALS, pintura);

        tr.set(new Vector3f(-0.25f, 0.0f, 0.0f));
        tg1.setTransform(tr);
        tg1.addChild(rueda1);
        tr.set(new Vector3f(0.25f, 0.0f, 0.0f));
        tg2.setTransform(tr);
        tg2.addChild(rueda2);
//    ro.rotX(Math.PI/2.0);
        tr.set(new Vector3f(0.0f, 0.0f, -0.05f));
//    tr.mul(ro);
        tg3.setTransform(tr);
        tg3.addChild(tg1);
        tg3.addChild(tg2);
        tr.set(new Vector3f(0.0f, 0.0f, 0.05f));
        tg4.setTransform(tr);
        tg4.addChild(piso);
        tr.set(new Vector3f(0.0f, 0.0f, 0.45f));
        tg5.setTransform(tr);
        tg5.addChild(pie);
        bg.addChild(tg5);
        bg.addChild(tg4);
        bg.addChild(tg3);
        bg.compile();
    }

    public BranchGroup getBG() {
        return bg;
    }
}
