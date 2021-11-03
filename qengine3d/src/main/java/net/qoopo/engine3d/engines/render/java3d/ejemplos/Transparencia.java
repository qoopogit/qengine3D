/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos;

/**
 *
 * @author alberto
 */
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

public class Transparencia extends Applet {

    public Transparencia() throws Exception {
        setLayout(new BorderLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas3D = new Canvas3D(config);

        add("Center", canvas3D);

        BranchGroup scene = createSceneGraph();
        scene.compile();

        SimpleUniverse univ = new SimpleUniverse(canvas3D);

        univ.getViewingPlatform().setNominalViewingTransform();

        univ.addBranchGraph(scene);
    }

    public BranchGroup createSceneGraph() throws Exception {
        BranchGroup root = new BranchGroup();

        // A white background.
        Background bgd = new Background(1.0f, 1.0f, 1.0f);
        root.addChild(bgd);

        // This will spin the quad around
        TransformGroup spin = new TransformGroup();
        spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        root.addChild(spin);

        // define the quad:
        Point3d p1 = new Point3d(-0.5, -0.5, 0);
        Point3d p2 = new Point3d(0.5, -0.5, 0);
        Point3d p3 = new Point3d(0.5, 0.5, 0);
        Point3d p4 = new Point3d(-0.5, 0.5, 0);

        // colors
//        Color4b c = new Color4b((byte) 255, (byte) 0, (byte) 0, (byte) 255);
//        //Color3b c = new Color3b((byte)255, (byte)0, (byte)0);
//
        Color color = new Color(255, 0, 0, 50);
        Color4f c = new Color4f(color);

        QuadArray quads = new QuadArray(4, GeometryArray.COORDINATES | GeometryArray.COLOR_4);
        // GeometryArray.COORDINATES | GeometryArray.COLOR_3);

        quads.setCoordinate(0, p1);
        quads.setCoordinate(1, p2);
        quads.setCoordinate(2, p3);
        quads.setCoordinate(3, p4);
        quads.setColor(0, c);
        quads.setColor(1, c);
        quads.setColor(2, c);
        quads.setColor(3, c);

        // Not sure if I need this. Doesn't seem to make a difference.
        Appearance appearance = new Appearance();
        TransparencyAttributes trans = new TransparencyAttributes();
        trans.setTransparencyMode(TransparencyAttributes.BLENDED);
        appearance.setTransparencyAttributes(trans);

        // Create the shape...
        Shape3D shape = new Shape3D();
        shape.setGeometry(quads);
        shape.setAppearance(appearance);

        spin.addChild(shape);

        Alpha rotationAlpha = new Alpha(-1, 4000);
        RotationInterpolator rotator = new RotationInterpolator(rotationAlpha, spin);
        BoundingSphere bounds = new BoundingSphere();
        rotator.setSchedulingBounds(bounds);
        spin.addChild(rotator);

        return root;
    }

    public static void main(String[] args) throws Exception {
        Frame frame = new MainFrame(new Transparencia(), 256, 256);
    }
}
