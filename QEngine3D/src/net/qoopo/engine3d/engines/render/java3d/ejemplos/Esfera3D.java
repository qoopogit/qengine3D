/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos;

import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Node;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public final class Esfera3D extends Applet {

    public Esfera3D() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);
        BranchGroup escena = crearEscenaGrafico();
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);
        simpleU.getViewingPlatform().setNominalViewingTransform();
        simpleU.addBranchGraph(escena);
    }

    public BranchGroup crearEscenaGrafico() {
        BranchGroup objPrincipal = new BranchGroup();
        objPrincipal.addChild(CrearEsfera());
        objPrincipal.addChild(CrearIluminacion());
        return objPrincipal;
    }

    private Node CrearEsfera() {
        Sphere esfera = new Sphere(0.3f); //Tamaño esfera (0.3)
        return esfera;
    }

    private Node CrearIluminacion() {
        Color3f colorLuz = new Color3f(1.0f, 0.5f, 6.1f);
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 20.0), 100.0);
        Vector3f direccionLuz = new Vector3f(4.0f, -7.0f, -12.0f);
        DirectionalLight luz1 = new DirectionalLight(colorLuz, direccionLuz);
        luz1.setInfluencingBounds(bounds);
        return luz1;
    }

    public static void main(String[] args) {
        Frame frame = new MainFrame(new Esfera3D(), 256, 256); //Tamaño ventana de 256x256 pixeles.
    }
}
