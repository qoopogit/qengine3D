/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos;

import java.applet.Applet;
import java.awt.FlowLayout;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame;

import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.net.URL;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Material;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public class RedGreenGirl extends Applet {

    protected Canvas3D c1 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    // private Canvas3D c2 = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
    private static MainFrame mf;
    protected SimpleUniverse u = null;
    protected BranchGroup scene = null;
    protected String URLString = "http://www.java3d.org/renee.obj";
    protected float eyeOffset = 0.03F;
    protected static int size = 600;

    public void init() {
        setLayout(new FlowLayout());
        GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
        String v = getParameter("url");
        if (v != null) {
            URLString = v;
        }
        c1.setSize(size, size);
        add(c1);
        scene = createSceneGraph(0);
        u = new SimpleUniverse(c1);
        // This will move the ViewPlatform back a bit so the
        // objects in the scene can be viewed.
        //u.getViewingPlatform().setNominalViewingTransform();
        u.addBranchGraph(scene);

    }

    public BranchGroup createSceneGraph(int i) {
        System.out.println("Creating scene for: " + URLString);
        // Create the root of the branch graph
        BranchGroup objRoot = new BranchGroup();
        try {
            Transform3D myTransform3D = new Transform3D();
            myTransform3D.setTranslation(new Vector3f(+0.0f, -0.15f, -3.6f));
            TransformGroup objTrans = new TransformGroup(myTransform3D);
            objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            Transform3D t = new Transform3D();
            TransformGroup tg = new TransformGroup(t);
            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
            tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
            objTrans.addChild(tg);
            URL url = new URL(URLString);
            ObjectFile f = new ObjectFile();
            f.setFlags(ObjectFile.RESIZE | ObjectFile.TRIANGULATE | ObjectFile.STRIPIFY);
            System.out.println("About to load");

            Scene s = f.load(url);
            Transform3D myTrans = new Transform3D();
            myTrans.setTranslation(new Vector3f(eyeOffset, -eyeOffset, 0F));
            TransformGroup mytg = new TransformGroup(myTrans);
            //mytg.addChild(s.getSceneGroup());
            tg.addChild(mytg);
            Transform3D myTrans2 = new Transform3D();
            myTrans2.setTranslation(new Vector3f(-eyeOffset, +eyeOffset, 0F));
            TransformGroup mytg2 = new TransformGroup(myTrans2);
            //mytg2.addChild(s.getSceneGroup());
            Hashtable table = s.getNamedObjects();
            for (Enumeration e = table.keys(); e.hasMoreElements();) {
                Object key = e.nextElement();
                System.out.println(key);
                Object obj = table.get(key);
                System.out.println(obj.getClass().getName());
                Shape3D shape = (Shape3D) obj;
                //shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);
                Appearance ap = new Appearance();
                Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
                Color3f red = new Color3f(0.7f, .0f, .15f);
                Color3f green = new Color3f(0f, .7f, .15f);
                ap.setMaterial(new Material(green, black, green, black, 1.0f));
                Appearance ap2 = new Appearance();
                ap2.setMaterial(new Material(red, black, red, black, 1.0f));
                float transparencyValue = 0.5f;
                TransparencyAttributes t_attr
                        = new TransparencyAttributes(
                                TransparencyAttributes.BLENDED,
                                transparencyValue,
                                TransparencyAttributes.BLEND_SRC_ALPHA,
                                TransparencyAttributes.BLEND_ONE);
                ap2.setTransparencyAttributes(t_attr);
                ap2.setRenderingAttributes(new RenderingAttributes());
                ap.setTransparencyAttributes(t_attr);
                ap.setRenderingAttributes(new RenderingAttributes());
                // bg.addChild(ap);
                shape.setAppearance(ap);
                mytg2.addChild(new Shape3D(shape.getGeometry(), ap2));
                mytg.addChild(new Shape3D(shape.getGeometry(), ap));
            }
            tg.addChild(mytg2);
            System.out.println("Finished Loading");
            BoundingSphere bounds
                    = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
            Color3f light1Color = new Color3f(.9f, 0.9f, 0.9f);
            Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);
            DirectionalLight light1
                    = new DirectionalLight(light1Color, light1Direction);
            light1.setInfluencingBounds(bounds);
            objTrans.addChild(light1);
            // Set up the ambient light
            Color3f ambientColor = new Color3f(1.0f, .4f, 0.3f);
            AmbientLight ambientLightNode = new AmbientLight(ambientColor);
            ambientLightNode.setInfluencingBounds(bounds);
            objTrans.addChild(ambientLightNode);

            MouseRotate behavior = new MouseRotate();
            behavior.setTransformGroup(tg);
            objTrans.addChild(behavior);
            // Create the translate behavior node
            MouseTranslate behavior3 = new MouseTranslate();
            behavior3.setTransformGroup(tg);
            objTrans.addChild(behavior3);
            behavior3.setSchedulingBounds(bounds);

            KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(tg);
            keyNavBeh.setSchedulingBounds(new BoundingSphere(
                    new Point3d(), 1000.0));
            objTrans.addChild(keyNavBeh);

            behavior.setSchedulingBounds(bounds);
            objRoot.addChild(objTrans);
        } catch (Throwable t) {
            System.out.println("Error: " + t);
        }
        return objRoot;
    }

    public RedGreenGirl() {
    }

    public void destroy() {
        u.removeAllLocales();
    }

    public static void main(String[] args) {

        RedGreenGirl s = new RedGreenGirl();
        if (args.length > 0) {
            s.URLString = args[0];
        }
        mf = new MainFrame(s, size, size);
    }
}
