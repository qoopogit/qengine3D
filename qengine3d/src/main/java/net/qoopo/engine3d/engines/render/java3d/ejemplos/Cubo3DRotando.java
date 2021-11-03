/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos;

import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.SimpleUniverse;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

/**
 *
 * @author alberto
 */
public class Cubo3DRotando implements Runnable {

    SimpleUniverse universo = new SimpleUniverse();
    BranchGroup group = new BranchGroup();
    ColorCube cube = new ColorCube(0.3);
    TransformGroup GT = new TransformGroup();
    Transform3D transform = new Transform3D();

    Thread hilo = new Thread(this);
    double Y = 0;

    public Cubo3DRotando() {
        GT.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        hilo.start();
//        GT.setTransform(transform);

        GT.addChild(cube);
        group.addChild(GT);
        universo.getViewingPlatform().setNominalViewingTransform();
        universo.addBranchGraph(group);
    }

    @Override
    public void run() {
        Thread ct = Thread.currentThread();
        transform.rotX(Math.toRadians(45));

        while (ct == hilo) {
            try {
                Y += 0.1;
                transform.rotY(Y);
                GT.setTransform(transform);
                Thread.sleep(100);
            } catch (Exception e) {
            }
        }
    }

    public static void main(String args[]) {
        new Cubo3DRotando();
    }
}
