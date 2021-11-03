/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos.brazo;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.geometry.Box;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Brazo extends Applet {

    private SimpleUniverse u = null;

    public BranchGroup createSceneGraph(SimpleUniverse su) {
        BranchGroup bg = new BranchGroup();  //la escena completa
        Transform3D sc = new Transform3D();  //scale
        Transform3D tr = new Transform3D();  //translate
        Transform3D ro = new Transform3D();  //rotate

//la apariencia del mekano
        Appearance pintura1 = new Appearance();
        Appearance pintura2 = new Appearance();
        Appearance pintura3 = new Appearance();
        Appearance pintura4 = new Appearance();

        pintura1.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        pintura2.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        pintura3.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        pintura4.setCapability(Appearance.ALLOW_MATERIAL_WRITE);

//Coloring and Shading
        ColoringAttributes cat = new ColoringAttributes();
        cat.setShadeModel(ColoringAttributes.SHADE_GOURAUD);

//material para luz
        Material m = new Material();
        m.setShininess(128.0f);
        m.setDiffuseColor(0.5f, 0.5f, 0.5f);
        m.setSpecularColor(0.8f, 0.8f, 0.8f);
        Material m2 = new Material();
        m2.setShininess(128.0f);
        m2.setDiffuseColor(0.8f, 0.2f, 0.2f);
        m2.setSpecularColor(0.8f, 0.8f, 0.8f);

        pintura1.setColoringAttributes(cat);
        pintura1.setMaterial(m2);
        pintura2.setColoringAttributes(cat);
        pintura2.setMaterial(m);
        pintura3.setColoringAttributes(cat);
        pintura3.setMaterial(m);
        pintura4.setColoringAttributes(cat);
        pintura4.setMaterial(m);

//las transformaciones al brazo
        //piso
        TransformGroup pisopos = new TransformGroup();
        //hombro
        TransformGroup hombrorot = new TransformGroup();
        tr.set(new Vector3f(0.0f, 0.0f, 0.8f));
        TransformGroup hombropos = new TransformGroup(tr);
        tr.set(new Vector3f(0.0f, 0.0f, 0.35f));
        TransformGroup hombropos1 = new TransformGroup(tr);
        //codo
        tr.set(new Vector3f(0.0f, 0.0f, 0.65f));
        TransformGroup codopos = new TransformGroup(tr);
        tr.set(new Vector3f(0.0f, 0.0f, 0.25f));
        TransformGroup codopos1 = new TransformGroup(tr);
        TransformGroup codorot = new TransformGroup();
        //muneca
        tr.set(new Vector3f(0.0f, 0.0f, 0.5f));
        TransformGroup munecarot = new TransformGroup();
        TransformGroup munecapos = new TransformGroup(tr);

        pisopos.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        hombrorot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        codorot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        munecarot.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

//la geometria
        Box pieza2 = new Box(0.12f, 0.12f, 0.35f, Box.GENERATE_NORMALS, pintura2);
        Box pieza3 = new Box(0.1f, 0.1f, 0.25f, Box.GENERATE_NORMALS, pintura3);
        Box pieza4 = new Box(0.12f, 0.12f, 0.02f, Box.GENERATE_NORMALS, pintura4);
        Pieza1 p1 = new Pieza1(pintura1);

//armar el grafo    
        pisopos.addChild(p1.getBG());
        pisopos.addChild(hombropos);
        hombropos.addChild(hombrorot);
        hombrorot.addChild(hombropos1);
        hombropos1.addChild(pieza2);
        hombrorot.addChild(codopos);
        codopos.addChild(codorot);
        codorot.addChild(codopos1);
        codopos1.addChild(pieza3);
        codorot.addChild(munecapos);
        munecapos.addChild(munecarot);
        munecarot.addChild(pieza4);

//conectar a los behaviors
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Movimiento mov = new Movimiento(pisopos, hombrorot, codorot, munecarot,
                pintura1, pintura2, pintura3, pintura4, m, m2);
        mov.setSchedulingBounds(bounds);
        hombrorot.addChild(mov);

//codigo para jugar con la pantalla
        Transform3D t3d = new Transform3D();
        t3d.lookAt(new Point3d(0, -3, 2), new Point3d(0, 2, 0), new Vector3d(0, 1, 0));
        TransformGroup tg5 = new TransformGroup();
        tg5.setTransform(t3d);
        tg5.addChild(pisopos);

        //crear las luces
        Vector3f direction = new Vector3f(4.0f, 6.0f, 4.0f);
        direction.normalize();
        DirectionalLight dl1 = new DirectionalLight(new Color3f(1.0f, 1.0f, 1.0f), direction);
        dl1.setInfluencingBounds(bounds);
        AmbientLight al2 = new AmbientLight(new Color3f(0.3f, 0.3f, 0.3f));
        al2.setInfluencingBounds(bounds);
        tg5.addChild(dl1);
        tg5.addChild(al2);

        bg.addChild(tg5);
        bg.compile();
        return bg;
    }

    public Brazo() {
    }

    public void init() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D c = new Canvas3D(config);
        add("Center", c);
        // Create a simple scene and attach it to the virtual universe
        u = new SimpleUniverse(c);
        BranchGroup scene = createSceneGraph(u);
        u.addBranchGraph(scene);
    }

    public void destroy() {
        u.removeAllLocales();
    }

    public static void main(String[] args) {
        new MainFrame(new Brazo(), 600, 600);
    }
}
