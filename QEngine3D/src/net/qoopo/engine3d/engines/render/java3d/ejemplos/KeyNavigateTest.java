/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GraphicsConfigTemplate;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.media.j3d.Alpha;
import javax.media.j3d.Appearance;
import javax.media.j3d.AudioDevice;
import javax.media.j3d.Background;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.GraphicsConfigTemplate3D;
import javax.media.j3d.Group;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Locale;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.Node;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointSound;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Sound;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnCollisionEntry;
import javax.media.j3d.WakeupOnCollisionExit;
import javax.media.j3d.WakeupOnElapsedTime;
import javax.media.j3d.WakeupOr;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point2d;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.applet.MainFrame;
import com.sun.j3d.utils.behaviors.interpolators.RotPosScaleTCBSplinePathInterpolator;
import com.sun.j3d.utils.behaviors.interpolators.TCBKeyFrame;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;
import net.qoopo.engine3d.core.util.QGlobal;

/**
 * Simple DOOM style navigation of a 3D scene using Java 3D. The scene
 * description is loaded from a GIF file where different colors denote objects
 * in the 3D scene. The example includes: simple (manual) collision detection,
 * texture animation, keyboard navigation.
 */
public class KeyNavigateTest extends Java3dApplet implements CollisionDetector {

    private static int m_kWidth = 300;

    private static int m_kHeight = 300;

//    private static final String m_szMapName = QGlobal.RECURSOS + "otros/doom_niveles/small_map.gif";
//    private static final String m_szMapName = QGlobal.RECURSOS + "otros/doom_niveles/large_map.gif";
    private static final String m_szMapName = QGlobal.RECURSOS + "otros/doom_niveles/huge_map.gif";

    private float FLOOR_WIDTH = 256;

    private float FLOOR_LENGTH = 256;

    private final int m_ColorWall = new Color(0, 0, 0).getRGB();

    private final int m_ColorGuard = new Color(255, 0, 0).getRGB();

    private final int m_ColorLight = new Color(255, 255, 0).getRGB();

    private final int m_ColorBookcase = new Color(0, 255, 0).getRGB();

    private final int m_ColorWater = new Color(0, 0, 255).getRGB();

    private Vector3d m_MapSquareSize = null;

    private Appearance m_WallAppearance = null;

    private Appearance m_GuardAppearance = null;

    private Appearance m_BookcaseAppearance = null;

    private Appearance m_WaterAppearance = null;

    private BufferedImage m_MapImage = null;

    private final double m_kFloorLevel = -20;

    private final double m_kCeilingHeight = 50;

    private final double m_kCeilingLevel = m_kFloorLevel + m_kCeilingHeight;

    private Vector3d m_Translation = new Vector3d();

    public KeyNavigateTest() {
        initJava3d();
    }

    protected double getScale() {
        return 0.05;
    }

    protected int getCanvas3dWidth(Canvas3D c3d) {
        return m_kWidth - 10;
    }

    protected int getCanvas3dHeight(Canvas3D c3d) {
        return m_kHeight - 10;
    }

    // edit the positions of the clipping
    // planes so we don't clip on the front
    // plane prematurely
    protected double getBackClipDistance() {
        return 20.0;
    }

    protected double getFrontClipDistance() {
        return 0.05;
    }

    // we create 2 TransformGroups above the ViewPlatform:
    // the first merely applies a scale, while the second is
    // manipulated using the KeyBehavior
    public TransformGroup[] getViewTransformGroupArray() {
        TransformGroup[] tgArray = new TransformGroup[2];
        tgArray[0] = new TransformGroup();
        tgArray[1] = new TransformGroup();

        Transform3D t3d = new Transform3D();
        t3d.setScale(getScale());
        t3d.invert();
        tgArray[0].setTransform(t3d);

        // ensure the Behavior can access the TG
        tgArray[1].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        // create the KeyBehavior and attach
        KeyCollisionBehavior keyBehavior = new KeyCollisionBehavior(tgArray[1], this);
        keyBehavior.setSchedulingBounds(getApplicationBounds());
        keyBehavior.setMovementRate(0.7);
        tgArray[1].addChild(keyBehavior);

        return tgArray;
    }

    protected BranchGroup createSceneBranchGroup() {
        BranchGroup objRoot = super.createSceneBranchGroup();

        TransformGroup objTrans = new TransformGroup();
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        createMap(objTrans);
        createFloor(objTrans);
        createCeiling(objTrans);

        objRoot.addChild(objTrans);

        return objRoot;
    }

    public Group createFloor(Group g) {
        System.out.println("Creating floor");

        Group floorGroup = new Group();
        Land floorTile = null;

        // use a shared Appearance so we only store 1 copy of the texture
        Appearance app = new Appearance();
        g.addChild(floorGroup);

        final double kNumTiles = 6;

        for (double x = -FLOOR_WIDTH + FLOOR_WIDTH / (2 * kNumTiles); x < FLOOR_WIDTH; x = x
                + FLOOR_WIDTH / kNumTiles) {
            for (double z = -FLOOR_LENGTH + FLOOR_LENGTH / (2 * kNumTiles); z < FLOOR_LENGTH; z = z
                    + FLOOR_LENGTH / kNumTiles) {
                floorTile = new Land(this, g, ComplexObject.GEOMETRY | ComplexObject.TEXTURE);
                floorTile.createObject(app, new Vector3d(x, m_kFloorLevel, z),
                        new Vector3d(FLOOR_WIDTH / (2 * kNumTiles), 1, FLOOR_LENGTH / (2 * kNumTiles)),
                        //                        "floor.gif",
                        QGlobal.RECURSOS + "otros/doom_niveles/floor.gif",
                        null, null);
            }
        }

        return floorGroup;
    }

    public Group createCeiling(Group g) {
        System.out.println("Creating ceiling");

        Group ceilingGroup = new Group();
        Land ceilingTile = null;

        // because we are technically viewing the ceiling from below
        // we want to switch the normals using a PolygonAttributes.
        Appearance app = new Appearance();
        app.setPolygonAttributes(new PolygonAttributes(
                PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0,
                false));

        g.addChild(ceilingGroup);

        final double kNumTiles = 6;

        for (double x = -FLOOR_WIDTH + FLOOR_WIDTH / (2 * kNumTiles); x < FLOOR_WIDTH; x = x
                + FLOOR_WIDTH / kNumTiles) {
            for (double z = -FLOOR_LENGTH + FLOOR_LENGTH / (2 * kNumTiles); z < FLOOR_LENGTH; z = z
                    + FLOOR_LENGTH / kNumTiles) {
                ceilingTile = new Land(this, g, ComplexObject.GEOMETRY
                        | ComplexObject.TEXTURE);
                ceilingTile.createObject(app, new Vector3d(x, m_kCeilingLevel,
                        z), new Vector3d(FLOOR_WIDTH / (2 * kNumTiles), 1,
                        FLOOR_LENGTH / (2 * kNumTiles)),
                        //                        "ceiling.gif", 
                        QGlobal.RECURSOS + "otros/doom_niveles/ceiling.gif",
                        null,
                        null);
            }
        }

        return ceilingGroup;
    }

    Point3d convertToWorldCoordinate(int nPixelX, int nPixelY) {
        Point3d point3d = new Point3d();
        Vector3d squareSize = getMapSquareSize();

        // range from 0 to 1
        point3d.x = nPixelX * squareSize.x;
        point3d.x -= FLOOR_WIDTH;

        point3d.z = nPixelY * squareSize.z;
        point3d.z -= FLOOR_LENGTH;

        point3d.y = 0;

        return point3d;
    }

    Point3d convertToWorldCoordinatesPixelCenter(int nPixelX, int nPixelY) {
        Point3d point3d = convertToWorldCoordinate(nPixelX, nPixelY);
        Vector3d squareSize = getMapSquareSize();

        point3d.x += squareSize.x / 2;
        point3d.z += squareSize.z / 2;

        return point3d;
    }

    Point2d convertToMapCoordinate(Vector3d worldCoord) {
        Point2d point2d = new Point2d();

        Vector3d squareSize = getMapSquareSize();

        point2d.x = (worldCoord.x + FLOOR_WIDTH) / squareSize.x;
        point2d.y = (worldCoord.z + FLOOR_LENGTH) / squareSize.z;

        return point2d;
    }

    public boolean isCollision(Transform3D t3d, boolean bViewSide) {
        // get the translation
        t3d.get(m_Translation);

        // we need to scale up by the scale that was
        // applied to the root TG on the view side of the scenegraph
        if (bViewSide != false) {
            m_Translation.scale(1.0 / getScale());
        }

        Vector3d mapSquareSize = getMapSquareSize();

        // first check that we are still inside the "world"
        if (m_Translation.x < -FLOOR_WIDTH + mapSquareSize.x
                || m_Translation.x > FLOOR_WIDTH - mapSquareSize.x
                || m_Translation.y < -FLOOR_LENGTH + mapSquareSize.y
                || m_Translation.y > FLOOR_LENGTH - mapSquareSize.y) {
            return true;
        }

        if (bViewSide != false) // then do a pixel based look up using the map
        {
            return isCollision(m_Translation);
        }

        return false;
    }

    // returns true if the given x,z location in the world corresponds to a wall
    // section
    protected boolean isCollision(Vector3d worldCoord) {
        Point2d point = convertToMapCoordinate(worldCoord);
        int nImageWidth = m_MapImage.getWidth();
        int nImageHeight = m_MapImage.getHeight();

        // outside of image
        if (point.x < 0 || point.x >= nImageWidth || point.y < 0
                || point.y >= nImageHeight) {
            return true;
        }

        int color = m_MapImage.getRGB((int) point.x, (int) point.y);

        // we can't walk through walls or bookcases
        return (color == m_ColorWall || color == m_ColorBookcase);
    }

    public Group createMap(Group g) {
        System.out.println("Creating map items");

        Group mapGroup = new Group();
        g.addChild(mapGroup);

        Texture tex = new TextureLoader(m_szMapName, this).getTexture();
        m_MapImage = ((ImageComponent2D) tex.getImage(0)).getImage();

        float imageWidth = m_MapImage.getWidth();
        float imageHeight = m_MapImage.getHeight();

        FLOOR_WIDTH = imageWidth * 8;
        FLOOR_LENGTH = imageHeight * 8;

        for (int nPixelX = 1; nPixelX < imageWidth - 1; nPixelX++) {
            for (int nPixelY = 1; nPixelY < imageWidth - 1; nPixelY++) {
                createMapItem(mapGroup, nPixelX, nPixelY);
            }

            float percentDone = 100 * (float) nPixelX
                    / (float) (imageWidth - 2);
            System.out.println("   " + (int) (percentDone) + "%");
        }

        createExternalWall(mapGroup);

        return mapGroup;
    }

    void createMapItem(Group mapGroup, int nPixelX, int nPixelY) {
        int color = m_MapImage.getRGB((int) nPixelX, (int) nPixelY);

        if (color == m_ColorWall) {
            createWall(mapGroup, nPixelX, nPixelY);
        } else if (color == m_ColorGuard) {
            createGuard(mapGroup, nPixelX, nPixelY);
        } else if (color == m_ColorLight) {
            createLight(mapGroup, nPixelX, nPixelY);
        } else if (color == m_ColorBookcase) {
            createBookcase(mapGroup, nPixelX, nPixelY);
        } else if (color == m_ColorWater) {
            createWater(mapGroup, nPixelX, nPixelY);
        }
    }

    Vector3d getMapSquareSize() {
        if (m_MapSquareSize == null) {
            double imageWidth = m_MapImage.getWidth();
            double imageHeight = m_MapImage.getHeight();
            m_MapSquareSize = new Vector3d(2 * FLOOR_WIDTH / imageWidth, 0, 2
                    * FLOOR_LENGTH / imageHeight);
        }

        return m_MapSquareSize;
    }

    void createWall(Group mapGroup, int nPixelX, int nPixelY) {
        Point3d point = convertToWorldCoordinatesPixelCenter(nPixelX, nPixelY);

        if (m_WallAppearance == null) {
            m_WallAppearance = new Appearance();
        }

        Vector3d squareSize = getMapSquareSize();

        Cuboid wall = new Cuboid(this, mapGroup, ComplexObject.GEOMETRY
                | ComplexObject.TEXTURE);
        wall
                .createObject(m_WallAppearance, new Vector3d(point.x,
                        m_kFloorLevel, point.z), new Vector3d(squareSize.x / 2,
                        m_kCeilingHeight / 2, squareSize.z / 2),
                        //                        "wall.gif",
                        QGlobal.RECURSOS + "otros/doom_niveles/wall.gif",
                        null, null);
    }

    void createExternalWall(Group mapGroup) {
        Vector3d squareSize = getMapSquareSize();

        Appearance app = new Appearance();
        app.setColoringAttributes(new ColoringAttributes(new Color3f(
                132f / 255f, 0, 66f / 255f), ColoringAttributes.FASTEST));

        int imageWidth = m_MapImage.getWidth();
        int imageHeight = m_MapImage.getHeight();

        Point3d topLeft = convertToWorldCoordinatesPixelCenter(0, 0);
        Point3d bottomRight = convertToWorldCoordinatesPixelCenter(
                imageWidth - 1, imageHeight - 1);

        // top
        Cuboid wall = new Cuboid(this, mapGroup, ComplexObject.GEOMETRY);
        wall.createObject(app, new Vector3d(0, m_kFloorLevel, topLeft.z),
                new Vector3d(squareSize.x * imageWidth / 2,
                        m_kCeilingHeight / 2, squareSize.z / 2), null, null,
                null);

        // bottom
        wall = new Cuboid(this, mapGroup, ComplexObject.GEOMETRY);
        wall.createObject(app, new Vector3d(0, m_kFloorLevel, bottomRight.z),
                new Vector3d(squareSize.x * imageWidth / 2,
                        m_kCeilingHeight / 2, squareSize.z / 2), null, null,
                null);

        // left
        wall = new Cuboid(this, mapGroup, ComplexObject.GEOMETRY);
        wall.createObject(app, new Vector3d(topLeft.x, m_kFloorLevel, 0),
                new Vector3d(squareSize.x / 2, m_kCeilingHeight / 2,
                        squareSize.z * imageHeight / 2), null, null, null);

        // right
        wall = new Cuboid(this, mapGroup, ComplexObject.GEOMETRY);
        wall.createObject(app, new Vector3d(bottomRight.x, m_kFloorLevel, 0),
                new Vector3d(squareSize.x / 2, m_kCeilingHeight / 2,
                        squareSize.z * imageHeight / 2), null, null, null);
    }

    void createGuard(Group mapGroup, int nPixelX, int nPixelY) {
        Point3d point = convertToWorldCoordinatesPixelCenter(nPixelX, nPixelY);

        if (m_GuardAppearance == null) {
            m_GuardAppearance = new Appearance();
        }

        Vector3d squareSize = getMapSquareSize();

        Guard guard = new Guard(this, mapGroup, ComplexObject.GEOMETRY, this);
        guard.createObject(m_GuardAppearance, new Vector3d(point.x,
                (m_kFloorLevel + m_kCeilingLevel) / 2, point.z), new Vector3d(
                1, 1, 1), null, null, null);
    }

    void createLight(Group mapGroup, int nPixelX, int nPixelY) {
        Point3d point = convertToWorldCoordinatesPixelCenter(nPixelX, nPixelY);

        // we do not share an Appearance for lights
        // or they all animate in synch...
        final double lightHeight = m_kCeilingHeight / 1.5;

        Light light = new Light(this, mapGroup, ComplexObject.GEOMETRY
                | ComplexObject.TEXTURE);
        light.createObject(new Appearance(), new Vector3d(point.x,
                m_kFloorLevel + lightHeight / 2, point.z), new Vector3d(5,
                lightHeight, 5),
                //"light.gif",
                QGlobal.RECURSOS + "otros/doom_niveles/light.gif",
                null, null);
    }

    void createWater(Group mapGroup, int nPixelX, int nPixelY) {
        Point3d point = convertToWorldCoordinatesPixelCenter(nPixelX, nPixelY);

        if (m_WaterAppearance == null) {
            m_WaterAppearance = new Appearance();
            m_WaterAppearance.setPolygonAttributes(new PolygonAttributes(
                    PolygonAttributes.POLYGON_FILL,
                    PolygonAttributes.CULL_NONE, 0, false));
            m_WaterAppearance
                    .setTransparencyAttributes(new TransparencyAttributes(
                            TransparencyAttributes.BLENDED, 1.0f));
            m_WaterAppearance.setTextureAttributes(new TextureAttributes(
                    TextureAttributes.REPLACE, new Transform3D(), new Color4f(
                            0, 0, 0, 1), TextureAttributes.FASTEST));

        }

        Land water = new Land(this, mapGroup, ComplexObject.GEOMETRY
                | ComplexObject.TEXTURE);
        water.createObject(m_WaterAppearance, new Vector3d(point.x,
                m_kFloorLevel + 0.1, point.z), new Vector3d(40, 1, 40),
                //                "water.gif"
                QGlobal.RECURSOS + "otros/doom_niveles/water.gif",
                null, null);
    }

    void createBookcase(Group mapGroup, int nPixelX, int nPixelY) {
        Point3d point = convertToWorldCoordinatesPixelCenter(nPixelX, nPixelY);

        if (m_BookcaseAppearance == null) {
            m_BookcaseAppearance = new Appearance();
        }

        Vector3d squareSize = getMapSquareSize();

        Cuboid bookcase = new Cuboid(this, mapGroup, ComplexObject.GEOMETRY
                | ComplexObject.TEXTURE);
        bookcase.createObject(m_BookcaseAppearance, new Vector3d(point.x,
                m_kFloorLevel, point.z), new Vector3d(squareSize.x / 2,
                m_kCeilingHeight / 2.7, squareSize.z / 2),
                //                "bookcase.gif",
                QGlobal.RECURSOS + "otros/doom_niveles/bookcase.gif",
                null, null);

    }

    public static void main(String[] args) {
        KeyNavigateTest keyTest = new KeyNavigateTest();
        keyTest.saveCommandLineArguments(args);

        new MainFrame(keyTest, m_kWidth, m_kHeight);
    }
}

/**
 * *****************************************************************************
 * Copyright (C) 2001 Daniel Selman
 *
 * First distributed with the book "Java 3D Programming" by Daniel Selman and
 * published by Manning Publications. http://manning.com/selman
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, version 2.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * The license can be found on the WWW at: http://www.fsf.org/copyleft/gpl.html
 *
 * Or by writing to: Free Software Foundation, Inc., 59 Temple Place - Suite
 * 330, Boston, MA 02111-1307, USA.
 *
 * Authors can be contacted at: Daniel Selman:
 * <span id="cloak34379"><a href="mailto:daniel@selman.org">daniel@selman.org</a></span><script type="text/javascript">
 * //<!-- document.getElementById('cloak34379').innerHTML = ''; var prefix =
 * 'ma' + 'il' + 'to'; var path = 'hr' + 'ef' + '='; var addy34379 = 'daniel' +
 * '@'; addy34379 = addy34379 + 'selman' + '.' + 'org';
 * document.getElementById('cloak34379').innerHTML +=
 * '<a ' + path + '\'' + prefix + ':' + addy34379 + '\'>' +addy34379+'
 * <\/a>'; //-->
 * </script>
 *
 * If you make changes you think others would like, please contact one of the
 * authors or someone at the www.j3d.org web site.
 * ****************************************************************************
 */
//*****************************************************************************
/**
 * Java3dApplet
 *
 * Base class for defining a Java 3D applet. Contains some useful methods for
 * defining views and scenegraphs etc.
 *
 * @author Daniel Selman
 * @version 1.0
 */
//*****************************************************************************
abstract class Java3dApplet extends Applet {

    public static int m_kWidth = 300;

    public static int m_kHeight = 300;

    protected String[] m_szCommandLineArray = null;

    protected VirtualUniverse m_Universe = null;

    protected BranchGroup m_SceneBranchGroup = null;

    protected Bounds m_ApplicationBounds = null;

    //  protected com.tornadolabs.j3dtree.Java3dTree m_Java3dTree = null;
    public Java3dApplet() {
    }

    public boolean isApplet() {
        try {
            System.getProperty("user.dir");
            System.out.println("Running as Application.");
            return false;
        } catch (Exception e) {
        }

        System.out.println("Running as Applet.");
        return true;
    }

    public URL getWorkingDirectory() throws java.net.MalformedURLException {
        URL url = null;

        try {
            File file = new File(System.getProperty("user.dir"));
            System.out.println("Running as Application:");
            System.out.println("   " + file.toURL());
            return file.toURL();
        } catch (Exception e) {
        }

        System.out.println("Running as Applet:");
        System.out.println("   " + getCodeBase());

        return getCodeBase();
    }

    public VirtualUniverse getVirtualUniverse() {
        return m_Universe;
    }

    //public com.tornadolabs.j3dtree.Java3dTree getJ3dTree() {
    //return m_Java3dTree;
    //  }
    public Locale getFirstLocale() {
        java.util.Enumeration e = m_Universe.getAllLocales();

        if (e.hasMoreElements() != false) {
            return (Locale) e.nextElement();
        }

        return null;
    }

    protected Bounds getApplicationBounds() {
        if (m_ApplicationBounds == null) {
            m_ApplicationBounds = createApplicationBounds();
        }

        return m_ApplicationBounds;
    }

    protected Bounds createApplicationBounds() {
        m_ApplicationBounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0),
                100.0);
        return m_ApplicationBounds;
    }

    protected Background createBackground() {
        Background back = new Background(new Color3f(0.9f, 0.9f, 0.9f));
        back.setApplicationBounds(createApplicationBounds());
        return back;
    }

    public void initJava3d() {
        //  m_Java3dTree = new com.tornadolabs.j3dtree.Java3dTree();
        m_Universe = createVirtualUniverse();

        Locale locale = createLocale(m_Universe);

        BranchGroup sceneBranchGroup = createSceneBranchGroup();

        ViewPlatform vp = createViewPlatform();
        BranchGroup viewBranchGroup = createViewBranchGroup(
                getViewTransformGroupArray(), vp);

        createView(vp);

        Background background = createBackground();

        if (background != null) {
            sceneBranchGroup.addChild(background);
        }

        //    m_Java3dTree.recursiveApplyCapability(sceneBranchGroup);
        //  m_Java3dTree.recursiveApplyCapability(viewBranchGroup);
        locale.addBranchGraph(sceneBranchGroup);
        addViewBranchGroup(locale, viewBranchGroup);

        onDoneInit();
    }

    protected void onDoneInit() {
        //  m_Java3dTree.updateNodes(m_Universe);
    }

    protected double getScale() {
        return 1.0;
    }

    public TransformGroup[] getViewTransformGroupArray() {
        TransformGroup[] tgArray = new TransformGroup[1];
        tgArray[0] = new TransformGroup();

        // move the camera BACK a little...
        // note that we have to invert the matrix as
        // we are moving the viewer
        Transform3D t3d = new Transform3D();
        t3d.setScale(getScale());
        t3d.setTranslation(new Vector3d(0.0, 0.0, -20.0));
        t3d.invert();
        tgArray[0].setTransform(t3d);

        return tgArray;
    }

    protected void addViewBranchGroup(Locale locale, BranchGroup bg) {
        locale.addBranchGraph(bg);
    }

    protected Locale createLocale(VirtualUniverse u) {
        return new Locale(u);
    }

    protected BranchGroup createSceneBranchGroup() {
        m_SceneBranchGroup = new BranchGroup();
        return m_SceneBranchGroup;
    }

    protected View createView(ViewPlatform vp) {
        View view = new View();

        PhysicalBody pb = createPhysicalBody();
        PhysicalEnvironment pe = createPhysicalEnvironment();

        AudioDevice audioDevice = createAudioDevice(pe);

        if (audioDevice != null) {
            pe.setAudioDevice(audioDevice);
            audioDevice.initialize();
        }

        view.setPhysicalEnvironment(pe);
        view.setPhysicalBody(pb);

        if (vp != null) {
            view.attachViewPlatform(vp);
        }

        view.setBackClipDistance(getBackClipDistance());
        view.setFrontClipDistance(getFrontClipDistance());

        Canvas3D c3d = createCanvas3D();
        view.addCanvas3D(c3d);
        addCanvas3D(c3d);

        return view;
    }

    protected PhysicalBody createPhysicalBody() {
        return new PhysicalBody();
    }

    protected AudioDevice createAudioDevice(PhysicalEnvironment pe) {
        JavaSoundMixer javaSoundMixer = new JavaSoundMixer(pe);

        if (javaSoundMixer == null) {
            System.out.println("create of audiodevice failed");
        }

        return javaSoundMixer;
    }

    protected PhysicalEnvironment createPhysicalEnvironment() {
        return new PhysicalEnvironment();
    }

    protected float getViewPlatformActivationRadius() {
        return 100;
    }

    protected ViewPlatform createViewPlatform() {
        ViewPlatform vp = new ViewPlatform();
        vp.setViewAttachPolicy(View.RELATIVE_TO_FIELD_OF_VIEW);
        vp.setActivationRadius(getViewPlatformActivationRadius());

        return vp;
    }

    protected Canvas3D createCanvas3D() {
        GraphicsConfigTemplate3D gc3D = new GraphicsConfigTemplate3D();
        gc3D.setSceneAntialiasing(GraphicsConfigTemplate.PREFERRED);
        GraphicsDevice gd[] = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getScreenDevices();

        Canvas3D c3d = new Canvas3D(gd[0].getBestConfiguration(gc3D));
        c3d.setSize(getCanvas3dWidth(c3d), getCanvas3dHeight(c3d));

        return c3d;
    }

    protected int getCanvas3dWidth(Canvas3D c3d) {
        return m_kWidth;
    }

    protected int getCanvas3dHeight(Canvas3D c3d) {
        return m_kHeight;
    }

    protected double getBackClipDistance() {
        return 100.0;
    }

    protected double getFrontClipDistance() {
        return 1.0;
    }

    protected BranchGroup createViewBranchGroup(TransformGroup[] tgArray,
            ViewPlatform vp) {
        BranchGroup vpBranchGroup = new BranchGroup();

        if (tgArray != null && tgArray.length > 0) {
            Group parentGroup = vpBranchGroup;
            TransformGroup curTg = null;

            for (int n = 0; n < tgArray.length; n++) {
                curTg = tgArray[n];
                parentGroup.addChild(curTg);
                parentGroup = curTg;
            }

            tgArray[tgArray.length - 1].addChild(vp);
        } else {
            vpBranchGroup.addChild(vp);
        }

        return vpBranchGroup;
    }

    protected void addCanvas3D(Canvas3D c3d) {
        setLayout(new BorderLayout());
        add(c3d, BorderLayout.CENTER);
        doLayout();
    }

    protected VirtualUniverse createVirtualUniverse() {
        return new VirtualUniverse();
    }

    protected void saveCommandLineArguments(String[] szArgs) {
        m_szCommandLineArray = szArgs;
    }

    protected String[] getCommandLineArguments() {
        return m_szCommandLineArray;
    }
}

class KeyCollisionBehavior extends KeyBehavior {

    private CollisionChecker m_CollisionChecker = null;

    public KeyCollisionBehavior(TransformGroup tg,
            CollisionDetector collisionDetector) {
        super(tg);

        m_CollisionChecker = new CollisionChecker(tg, collisionDetector, true);
    }

    protected void updateTransform() {
        if (m_CollisionChecker.isCollision(transform3D) == false) {
            transformGroup.setTransform(transform3D);
        }
    }

    // dissallow rotation up or down
    protected void altMove(int keycode) {
    }

    // dissallow moving up or down
    protected void controlMove(int keycode) {
    }
}

class Guard extends ComplexObject {

    private CollisionDetector m_CollisionDetector = null;

    public Guard(Component comp, Group g, int nFlags, CollisionDetector detector) {
        super(comp, g, nFlags);

        m_CollisionDetector = detector;
    }

    protected Group createGeometryGroup(Appearance app, Vector3d position,
            Vector3d scale, String szTextureFile, String szSoundFile) {
        TransformGroup tg = new TransformGroup();
        tg.addChild(new Cone(5, 30));

        attachBehavior(new RandomWalkBehavior(getBehaviorTransformGroup(),
                m_CollisionDetector));

        return tg;
    }
}

interface CollisionDetector {

    public boolean isCollision(Transform3D t3d, boolean bViewSide);
}

class CollisionChecker {

    CollisionDetector m_Detector = null;

    Transform3D m_ToWorld = null;

    Transform3D m_Transform3D = null;

    Node m_Node = null;

    boolean m_bViewSide = false;

    public CollisionChecker(Node node, CollisionDetector detector,
            boolean bViewSide) {
        m_Detector = detector;
        m_Node = node;
        m_bViewSide = bViewSide;
    }

    public boolean isCollision(Transform3D t3d) {
        return m_Detector.isCollision(t3d, m_bViewSide);
    }
}

class Light extends ComplexObject {

    private TextureAttributes m_TextureAttributes = null;

    public Light(Component comp, Group g, int nFlags) {
        super(comp, g, nFlags);
    }

    protected Group createGeometryGroup(Appearance app, Vector3d position,
            Vector3d scale, String szTextureFile, String szSoundFile) {
        Group g = new Group();

        app.setPolygonAttributes(new PolygonAttributes(
                PolygonAttributes.POLYGON_FILL, PolygonAttributes.CULL_NONE, 0,
                false));
        app.setTransparencyAttributes(new TransparencyAttributes(
                TransparencyAttributes.BLENDED, 1.0f));

        m_TextureAttributes = new TextureAttributes(TextureAttributes.REPLACE,
                new Transform3D(), new Color4f(0, 0, 0, 1),
                TextureAttributes.FASTEST);
        app.setTextureAttributes(m_TextureAttributes);

        if ((m_nFlags & ComplexObject.TEXTURE) == ComplexObject.TEXTURE) {
            setTexture(app, szTextureFile);
        }

        Cone cone = new Cone(1, 1, Primitive.GENERATE_TEXTURE_COORDS, app);

        g.addChild(cone);

        attachBehavior(new TextureAnimationBehavior(m_TextureAttributes));

        return g;
    }
}

class TextureAnimationBehavior extends Behavior {
    // the wake up condition for the behavior

    protected WakeupCondition m_WakeupCondition = null;

    protected Transform3D m_Transform3D = null;

    protected TextureAttributes m_TextureAttributes = null;

    protected double rotY = 0;

    public TextureAnimationBehavior(TextureAttributes texAttribs) {
        m_TextureAttributes = texAttribs;
        m_Transform3D = new Transform3D();
        m_TextureAttributes
                .setCapability(TextureAttributes.ALLOW_TRANSFORM_WRITE);

        // create the WakeupCriterion for the behavior
        WakeupCriterion criterionArray[] = new WakeupCriterion[1];
        criterionArray[0] = new WakeupOnElapsedTime(300);

        // save the WakeupCriterion for the behavior
        m_WakeupCondition = new WakeupOr(criterionArray);
    }

    public void initialize() {
        // apply the initial WakeupCriterion
        wakeupOn(m_WakeupCondition);
    }

    public void processStimulus(java.util.Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion wakeUp = (WakeupCriterion) criteria.nextElement();

            if (wakeUp instanceof WakeupOnElapsedTime) {
                rotY += Utils.getRandomNumber(0.01, 0.01);
                m_Transform3D.rotY(rotY);
                m_TextureAttributes.setTextureTransform(m_Transform3D);
            }
        }

        // assign the next WakeUpCondition, so we are notified again
        wakeupOn(m_WakeupCondition);
    }
}

// this class implements a simple behavior that
// calculates and prints the size of an object
// based on the vertices in its GeometryArray
class RandomWalkBehavior extends Behavior {
    // the wake up condition for the behavior

    protected WakeupCondition m_WakeupCondition = null;

    protected TransformGroup m_TransformGroup = null;

    protected Transform3D m_Transform3D = null;

    protected Vector3d TargetVector3d = null;

    protected Vector3d CurrentVector3d = null;

    private final double m_MovementX = 2;

    private final double m_MovementY = 0;

    private final double m_MovementZ = 2;

    private int m_nFrameCount = 0;

    private CollisionChecker m_CollisionChecker = null;

    public RandomWalkBehavior(TransformGroup tg, CollisionDetector detector) {
        m_TransformGroup = tg;

        m_CollisionChecker = new CollisionChecker(tg, detector, false);

        m_Transform3D = new Transform3D();

        TargetVector3d = new Vector3d();
        CurrentVector3d = new Vector3d();

        // create the WakeupCriterion for the behavior
        WakeupCriterion criterionArray[] = new WakeupCriterion[1];
        criterionArray[0] = new WakeupOnElapsedTime(100);

        // save the WakeupCriterion for the behavior
        m_WakeupCondition = new WakeupOr(criterionArray);
    }

    public void initialize() {
        // apply the initial WakeupCriterion
        wakeupOn(m_WakeupCondition);
    }

    public void processStimulus(java.util.Enumeration criteria) {
        while (criteria.hasMoreElements()) {
            WakeupCriterion wakeUp = (WakeupCriterion) criteria.nextElement();

            if (wakeUp instanceof WakeupOnElapsedTime) {
                if (m_nFrameCount % 100 == 0) {
                    // generate a random direction for movement
                    TargetVector3d.x = m_MovementX
                            * Utils.getRandomNumber(0, 1);
                    TargetVector3d.y = m_MovementY
                            * Utils.getRandomNumber(0, 1);
                    TargetVector3d.z = m_MovementZ
                            * Utils.getRandomNumber(0, 1);
                }

                CurrentVector3d.x += TargetVector3d.x
                        * Utils.getRandomNumber(1, 0.1);
                CurrentVector3d.y += TargetVector3d.y
                        * Utils.getRandomNumber(1, 0.1);
                CurrentVector3d.z += TargetVector3d.z
                        * Utils.getRandomNumber(1, 0.1);

                m_Transform3D.setTranslation(CurrentVector3d);

                if (m_CollisionChecker.isCollision(m_Transform3D) == false) {
                    m_TransformGroup.setTransform(m_Transform3D);
                }

                m_nFrameCount++;
            }
        }

        // assign the next WakeUpCondition, so we are notified again
        wakeupOn(m_WakeupCondition);
    }
}

abstract class ComplexObject extends BranchGroup {

    protected Group m_ParentGroup = null;

    protected int m_nFlags = 0;

    protected BackgroundSound m_CollideSound = null;

    protected Component m_Component = null;

    protected TransformGroup m_TransformGroup = null;

    protected TransformGroup m_BehaviorTransformGroup = null;

    public static final int SOUND = 0x001;

    public static final int GEOMETRY = 0x002;

    public static final int TEXTURE = 0x004;

    public static final int COLLISION = 0x008;

    public static final int COLLISION_SOUND = 0x010;

    public ComplexObject(Component comp, Group group, int nFlags) {
        m_ParentGroup = group;
        m_nFlags = nFlags;
        m_Component = comp;
    }

    public Bounds getGeometryBounds() {
        return new BoundingSphere(new Point3d(0, 0, 0), 100);
    }

    private MediaContainer loadSoundFile(String szFile) {
        try {
            File file = new File(System.getProperty("user.dir"));
            URL url = file.toURL();

            URL soundUrl = new URL(url, szFile);
            return new MediaContainer(soundUrl);
        } catch (Exception e) {
            System.err.println("Error could not load sound file: " + e);
            System.exit(-1);
        }

        return null;
    }

    protected void setTexture(Appearance app, String szFile) {
        Texture tex = new TextureLoader(szFile, m_Component).getTexture();
        app.setTexture(tex);
    }

    abstract protected Group createGeometryGroup(Appearance app,
            Vector3d position, Vector3d scale, String szTextureFile,
            String szSoundFile);

    protected Group loadGeometryGroup(String szModel, Appearance app)
            throws java.io.FileNotFoundException {
        // load the object file
        Scene scene = null;
        Shape3D shape = null;

        // read in the geometry information from the data file
        ObjectFile objFileloader = new ObjectFile(ObjectFile.RESIZE);

        scene = objFileloader.load(szModel);

        // retrieve the Shape3D object from the scene
        BranchGroup branchGroup = scene.getSceneGroup();
        shape = (Shape3D) branchGroup.getChild(0);
        shape.setAppearance(app);

        return branchGroup;
    }

    protected int getSoundLoop(boolean bCollide) {
        return 1;
    }

    protected float getSoundPriority(boolean bCollide) {
        return 1.0f;
    }

    protected float getSoundInitialGain(boolean bCollide) {
        return 1.0f;
    }

    protected boolean getSoundInitialEnable(boolean bCollide) {
        return true;
    }

    protected boolean getSoundContinuousEnable(boolean bCollide) {
        return false;
    }

    protected Bounds getSoundSchedulingBounds(boolean bCollide) {
        return new BoundingSphere(new Point3d(0, 0, 0), 1.0);
    }

    protected boolean getSoundReleaseEnable(boolean bCollide) {
        return true;
    }

    protected Point2f[] getSoundDistanceGain(boolean bCollide) {
        return null;
    }

    protected void setSoundAttributes(Sound sound, boolean bCollide) {
        sound.setCapability(Sound.ALLOW_ENABLE_WRITE);
        sound.setCapability(Sound.ALLOW_ENABLE_READ);

        sound.setSchedulingBounds(getSoundSchedulingBounds(bCollide));
        sound.setEnable(getSoundInitialEnable(bCollide));
        sound.setLoop(getSoundLoop(bCollide));
        sound.setPriority(getSoundPriority(bCollide));
        sound.setInitialGain(getSoundInitialGain(bCollide));

        sound.setContinuousEnable(getSoundContinuousEnable(bCollide));
        sound.setReleaseEnable(bCollide);

        if (sound instanceof PointSound) {
            PointSound pointSound = (PointSound) sound;
            pointSound.setInitialGain(getSoundInitialGain(bCollide));

            Point2f[] gainArray = getSoundDistanceGain(bCollide);

            if (gainArray != null) {
                pointSound.setDistanceGain(gainArray);
            }
        }
    }

    public Group createObject(Appearance app, Vector3d position,
            Vector3d scale, String szTextureFile, String szSoundFile,
            String szCollisionSound) {
        m_TransformGroup = new TransformGroup();
        Transform3D t3d = new Transform3D();

        t3d.setScale(scale);
        t3d.setTranslation(position);

        m_TransformGroup.setTransform(t3d);

        m_BehaviorTransformGroup = new TransformGroup();

        if ((m_nFlags & GEOMETRY) == GEOMETRY) {
            m_BehaviorTransformGroup.addChild(createGeometryGroup(app, position, scale, szTextureFile, szSoundFile));
        }

        if ((m_nFlags & SOUND) == SOUND) {
            MediaContainer media = loadSoundFile(szSoundFile);
            PointSound pointSound = new PointSound(media, getSoundInitialGain(false), 0, 0, 0);
            setSoundAttributes(pointSound, false);
            m_BehaviorTransformGroup.addChild(pointSound);
        }

        if ((m_nFlags & COLLISION) == COLLISION) {
            m_BehaviorTransformGroup.setCapability(Node.ENABLE_COLLISION_REPORTING);
            m_BehaviorTransformGroup.setCollidable(true);
            m_BehaviorTransformGroup.setCollisionBounds(getGeometryBounds());

            if ((m_nFlags & COLLISION_SOUND) == COLLISION_SOUND) {
                MediaContainer collideMedia = loadSoundFile(szCollisionSound);

                m_CollideSound = new BackgroundSound(collideMedia, 1);
                setSoundAttributes(m_CollideSound, true);
                m_TransformGroup.addChild(m_CollideSound);
            }

            CollisionBehavior collision = new CollisionBehavior(m_BehaviorTransformGroup, this);
            collision.setSchedulingBounds(getGeometryBounds());

            m_BehaviorTransformGroup.addChild(collision);
        }

        m_TransformGroup.addChild(m_BehaviorTransformGroup);
        m_ParentGroup.addChild(m_TransformGroup);

        return m_BehaviorTransformGroup;
    }

    public void onCollide(boolean bCollide) {
        System.out.println("Collide: " + bCollide);

        if (m_CollideSound != null && bCollide == true) {
            m_CollideSound.setEnable(true);
        }
    }

    public void attachBehavior(Behavior beh) {
        m_BehaviorTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        beh.setSchedulingBounds(getGeometryBounds());
        m_BehaviorTransformGroup.addChild(beh);
    }

    public TransformGroup getBehaviorTransformGroup() {
        return m_BehaviorTransformGroup;
    }

    public void attachSplinePathInterpolator(Alpha alpha, Transform3D axis,
            URL urlKeyframes) {
        // read a spline path definition file and
        // add a Spline Path Interpolator to the TransformGroup for the object.

        m_BehaviorTransformGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

        RotPosScaleTCBSplinePathInterpolator splineInterpolator = Utils
                .createSplinePathInterpolator(alpha, m_BehaviorTransformGroup,
                        axis, urlKeyframes);

        if (splineInterpolator != null) {
            splineInterpolator.setSchedulingBounds(getGeometryBounds());
            m_BehaviorTransformGroup.addChild(splineInterpolator);
        } else {
            System.out.println("attachSplinePathInterpolator failed for: "
                    + urlKeyframes);
        }
    }
}

class KeyBehavior extends Behavior {

    protected static final double FAST_SPEED = 20.0;

    protected static final double NORMAL_SPEED = 1.0;

    protected static final double SLOW_SPEED = 0.5;

    protected TransformGroup transformGroup;

    protected Transform3D transform3D;

    protected WakeupCondition keyCriterion;

    private final static double TWO_PI = (2.0 * Math.PI);

    private double rotateXAmount = Math.PI / 16.0;

    private double rotateYAmount = Math.PI / 16.0;

    private double rotateZAmount = Math.PI / 16.0;

    private double moveRate = 5;

    private double speed = NORMAL_SPEED;

    private final double kMoveForwardScale = 1.1;

    private final double kMoveBackwardScale = 0.9;

    private int forwardKey = KeyEvent.VK_UP;

    private int backKey = KeyEvent.VK_DOWN;

    private int leftKey = KeyEvent.VK_LEFT;

    private int rightKey = KeyEvent.VK_RIGHT;

    public KeyBehavior(TransformGroup tg) {
        super();

        transformGroup = tg;
        transform3D = new Transform3D();
    }

    public void initialize() {
        WakeupCriterion[] keyEvents = new WakeupCriterion[2];
        keyEvents[0] = new WakeupOnAWTEvent(KeyEvent.KEY_PRESSED);
        keyEvents[1] = new WakeupOnAWTEvent(KeyEvent.KEY_RELEASED);
        keyCriterion = new WakeupOr(keyEvents);

        wakeupOn(keyCriterion);
    }

    public void processStimulus(Enumeration criteria) {
        WakeupCriterion wakeup;
        AWTEvent[] event;

        while (criteria.hasMoreElements()) {
            wakeup = (WakeupCriterion) criteria.nextElement();

            if (!(wakeup instanceof WakeupOnAWTEvent)) {
                continue;
            }

            event = ((WakeupOnAWTEvent) wakeup).getAWTEvent();

            for (int i = 0; i < event.length; i++) {
                if (event[i].getID() == KeyEvent.KEY_PRESSED) {
                    processKeyEvent((KeyEvent) event[i]);
                }
            }
        }

        wakeupOn(keyCriterion);
    }

    protected void processKeyEvent(KeyEvent event) {
        int keycode = event.getKeyCode();

        if (event.isShiftDown()) {
            speed = FAST_SPEED;
        } else {
            speed = NORMAL_SPEED;
        }

        if (event.isAltDown()) {
            altMove(keycode);
        } else if (event.isControlDown()) {
            controlMove(keycode);
        } else {
            standardMove(keycode);
        }
    }

    //moves forward backward or rotates left right
    private void standardMove(int keycode) {
        if (keycode == forwardKey) {
            moveForward();
        } else if (keycode == backKey) {
            moveBackward();
        } else if (keycode == leftKey) {
            rotLeft();
        } else if (keycode == rightKey) {
            rotRight();
        }
    }

    //moves left right, rotate up down
    protected void altMove(int keycode) {
        if (keycode == forwardKey) {
            rotUp();
        } else if (keycode == backKey) {
            rotDown();
        } else if (keycode == leftKey) {
            rotLeft();
        } else if (keycode == rightKey) {
            rotRight();
        } else if (keycode == leftKey) {
            moveLeft();
        } else if (keycode == rightKey) {
            moveRight();
        }
    }

    //move up down, rot left right
    protected void controlMove(int keycode) {
        if (keycode == forwardKey) {
            moveUp();
        } else if (keycode == backKey) {
            moveDown();
        } else if (keycode == leftKey) {
            rollLeft();
        } else if (keycode == rightKey) {
            rollRight();
        }
    }

    private void moveForward() {
        doMove(new Vector3d(0.0, 0.0, kMoveForwardScale * speed));
    }

    private void moveBackward() {
        doMove(new Vector3d(0.0, 0.0, -kMoveBackwardScale * speed));
    }

    private void moveLeft() {
        doMove(new Vector3d(-getMovementRate(), 0.0, 0.0));
    }

    private void moveRight() {
        doMove(new Vector3d(getMovementRate(), 0.0, 0.0));
    }

    private void moveUp() {
        doMove(new Vector3d(0.0, getMovementRate(), 0.0));
    }

    private void moveDown() {
        doMove(new Vector3d(0.0, -getMovementRate(), 0.0));
    }

    protected void rotRight() {
        doRotateY(getRotateRightAmount());
    }

    protected void rotUp() {
        doRotateX(getRotateUpAmount());
    }

    protected void rotLeft() {
        doRotateY(getRotateLeftAmount());
    }

    protected void rotDown() {
        doRotateX(getRotateDownAmount());
    }

    protected void rollLeft() {
        doRotateZ(getRollLeftAmount());
    }

    protected void rollRight() {
        doRotateZ(getRollRightAmount());
    }

    protected void updateTransform() {
        transformGroup.setTransform(transform3D);
    }

    protected void doRotateY(double radians) {
        transformGroup.getTransform(transform3D);
        Transform3D toMove = new Transform3D();
        toMove.rotY(radians);
        transform3D.mul(toMove);
        updateTransform();
    }

    protected void doRotateX(double radians) {
        transformGroup.getTransform(transform3D);
        Transform3D toMove = new Transform3D();
        toMove.rotX(radians);
        transform3D.mul(toMove);
        updateTransform();
    }

    protected void doRotateZ(double radians) {
        transformGroup.getTransform(transform3D);
        Transform3D toMove = new Transform3D();
        toMove.rotZ(radians);
        transform3D.mul(toMove);
        updateTransform();
    }

    protected void doMove(Vector3d theMove) {
        transformGroup.getTransform(transform3D);
        Transform3D toMove = new Transform3D();
        toMove.setTranslation(theMove);
        transform3D.mul(toMove);
        updateTransform();
    }

    protected double getMovementRate() {
        return moveRate * speed;
    }

    protected double getRollLeftAmount() {
        return rotateZAmount * speed;
    }

    protected double getRollRightAmount() {
        return -rotateZAmount * speed;
    }

    protected double getRotateUpAmount() {
        return rotateYAmount * speed;
    }

    protected double getRotateDownAmount() {
        return -rotateYAmount * speed;
    }

    protected double getRotateLeftAmount() {
        return rotateYAmount * speed;
    }

    protected double getRotateRightAmount() {
        return -rotateYAmount * speed;
    }

    public void setRotateXAmount(double radians) {
        rotateXAmount = radians;
    }

    public void setRotateYAmount(double radians) {
        rotateYAmount = radians;
    }

    public void setRotateZAmount(double radians) {
        rotateZAmount = radians;
    }

    public void setMovementRate(double meters) {
        moveRate = meters; // Travel rate in meters/frame
    }

    public void setForwardKey(int key) {
        forwardKey = key;
    }

    public void setBackKey(int key) {
        backKey = key;
    }

    public void setLeftKey(int key) {
        leftKey = key;
    }
}

//creates a 2x2x2 cuboid with its base at y=0
class Cuboid extends ComplexObject {

    public Cuboid(Component comp, Group g, int nFlags) {
        super(comp, g, nFlags);
    }

    protected Group createGeometryGroup(Appearance app, Vector3d position,
            Vector3d scale, String szTextureFile, String szSoundFile) {
        int nFlags = GeometryArray.COORDINATES | GeometryArray.NORMALS;

        if ((m_nFlags & TEXTURE) == TEXTURE) {
            nFlags |= GeometryArray.TEXTURE_COORDINATE_2;
        }

        QuadArray quadArray = new QuadArray(24, nFlags);

        quadArray.setCoordinates(0, verts, 0, 24);

        for (int n = 0; n < 24; n++) {
            quadArray.setNormal(n, normals[n / 4]);
        }

        if ((m_nFlags & TEXTURE) == TEXTURE) {
            quadArray.setTextureCoordinates(0, 0, tcoords, 0, 24);
            setTexture(app, szTextureFile);
        }

        Shape3D shape = new Shape3D(quadArray, app);

        BranchGroup bg = new BranchGroup();
        bg.addChild(shape);
        return bg;
    }

    private static final float[] verts = {
        // front face
        1.0f, 0.0f, 1.0f, 1.0f, 2.0f, 1.0f, -1.0f, 2.0f, 1.0f, -1.0f, 0.0f,
        1.0f,
        // back face
        -1.0f, 0.0f, -1.0f, -1.0f, 2.0f, -1.0f, 1.0f, 2.0f, -1.0f, 1.0f,
        0.0f, -1.0f,
        // right face
        1.0f, 0.0f, -1.0f, 1.0f, 2.0f, -1.0f, 1.0f, 2.0f, 1.0f, 1.0f, 0.0f,
        1.0f,
        // left face
        -1.0f, 0.0f, 1.0f, -1.0f, 2.0f, 1.0f, -1.0f, 2.0f, -1.0f, -1.0f,
        0.0f, -1.0f,
        // top face
        1.0f, 2.0f, 1.0f, 1.0f, 2.0f, -1.0f, -1.0f, 2.0f, -1.0f, -1.0f,
        2.0f, 1.0f,
        // bottom face
        -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f, 1.0f,
        0.0f, 1.0f,};

    private static final float[] tcoords = {
        // front
        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        // back
        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        //right
        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        // left
        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        // top
        1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
        // bottom
        0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f};

    private static final Vector3f[] normals = {new Vector3f(0.0f, 0.0f, 1.0f), // front
        // face
        new Vector3f(0.0f, 0.0f, -1.0f), // back face
        new Vector3f(1.0f, 0.0f, 0.0f), // right face
        new Vector3f(-1.0f, 0.0f, 0.0f), // left face
        new Vector3f(0.0f, 1.0f, 0.0f), // top face
        new Vector3f(0.0f, -1.0f, 0.0f), // bottom face
};
}

//*****************************************************************************
/**
 * Utils
 *
 * @author Daniel Selman
 * @version 1.0
 */
//*****************************************************************************
class Utils {
    // convert an angular rotation about an axis to a Quaternion

    static Quat4f createQuaternionFromAxisAndAngle(Vector3d axis, double angle) {
        double sin_a = Math.sin(angle / 2);
        double cos_a = Math.cos(angle / 2);

        // use a vector so we can call normalize
        Vector4f q = new Vector4f();

        q.x = (float) (axis.x * sin_a);
        q.y = (float) (axis.y * sin_a);
        q.z = (float) (axis.z * sin_a);
        q.w = (float) cos_a;

        // It is necessary to normalise the quaternion
        // in case any values are very close to zero.
        q.normalize();

        // convert to a Quat4f and return
        return new Quat4f(q);
    }

    // convert three rotations about the Euler axes to a Quaternion
    static Quat4f createQuaternionFromEuler(double angleX, double angleY,
            double angleZ) {
        // simply call createQuaternionFromAxisAndAngle
        // for each axis and multiply the results
        Quat4f qx = createQuaternionFromAxisAndAngle(new Vector3d(1, 0, 0), angleX);
        Quat4f qy = createQuaternionFromAxisAndAngle(new Vector3d(0, 1, 0),
                angleY);
        Quat4f qz = createQuaternionFromAxisAndAngle(new Vector3d(0, 0, 1),
                angleZ);

        // qx = qx * qy
        qx.mul(qy);

        // qx = qx * qz
        qx.mul(qz);

        return qx;
    }

    static public double getRandomNumber(double basis, double random) {
        return basis + ((float) Math.random() * random * 2f) - (random);
    }

    static public double getRandomNumber(double basis, double random,
            double scale) {
        double value = basis + ((float) Math.random() * random * 2f) - (random);
        return value * scale;
    }

    static public StringBuffer readFile(URL urlFile) {
        /*   allocate a temporary buffer to store the input file*/
        StringBuffer szBufferData = new StringBuffer();
        Vector keyFramesVector = new Vector();

        try {
            InputStream inputStream = urlFile.openStream();

            int nChar = 0;

            // read the entire file into the StringBuffer
            while (true) {
                nChar = inputStream.read();

                /* if we have not hit the end of file
         add the character to the StringBuffer
                 */
                if (nChar != -1) {
                    szBufferData.append((char) nChar);
                } else // EOF
                {
                    break;
                }
            }

            inputStream.close();
        } catch (Exception e) {
            System.err.println(e.toString());
            return null;
        }

        return szBufferData;
    }

    static public RotPosScaleTCBSplinePathInterpolator createSplinePathInterpolator(
            Alpha alpha, TransformGroup tg, Transform3D axis, URL urlKeyframes) {
        TCBKeyFrame[] keyFrames = readKeyFrames(urlKeyframes);

        if (keyFrames != null) {
            return new RotPosScaleTCBSplinePathInterpolator(alpha, tg, axis,
                    keyFrames);
        }

        return null;
    }

    static public TCBKeyFrame[] readKeyFrames(URL urlKeyframes) {
        StringBuffer szBufferData = readFile(urlKeyframes);

        if (szBufferData == null) {
            return null;
        }

        Vector keyFramesVector = new Vector();

        // create a tokenizer to tokenize the input file at whitespace
        java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(
                szBufferData.toString());

        // each keyframe is defined as follows
        // - knot (0 >= k <= 1)
        // - position (x,y,z)
        // - rotation (rx,ry,rz)
        // - scale (x,y,z)
        // - tension (-1 >= t <= 1)
        // - continuity (-1 >= c <= 1)
        // - bias (-1 >= b <= 1)
        // - linear (int - 0 or 1)
        while (true) {
            try {
                float knot = Float.parseFloat(tokenizer.nextToken());

                float posX = Float.parseFloat(tokenizer.nextToken());
                float posY = Float.parseFloat(tokenizer.nextToken());
                float posZ = Float.parseFloat(tokenizer.nextToken());

                float rotX = Float.parseFloat(tokenizer.nextToken());
                float rotY = Float.parseFloat(tokenizer.nextToken());
                float rotZ = Float.parseFloat(tokenizer.nextToken());

                float scaleX = Float.parseFloat(tokenizer.nextToken());
                float scaleY = Float.parseFloat(tokenizer.nextToken());
                float scaleZ = Float.parseFloat(tokenizer.nextToken());

                float tension = Float.parseFloat(tokenizer.nextToken());
                float continuity = Float.parseFloat(tokenizer.nextToken());
                float bias = Float.parseFloat(tokenizer.nextToken());

                int linear = Integer.parseInt(tokenizer.nextToken());

                TCBKeyFrame keyframe = new TCBKeyFrame(knot, linear,
                        new Point3f(posX, posY, posZ),
                        createQuaternionFromEuler(rotX, rotY, rotZ),
                        new Point3f(scaleX, scaleY, scaleZ), tension,
                        continuity, bias);

                keyFramesVector.add(keyframe);
            } catch (Exception e) {
                break;
            }
        }

        // create the return structure and populate
        TCBKeyFrame[] keysReturn = new TCBKeyFrame[keyFramesVector.size()];

        for (int n = 0; n < keysReturn.length; n++) {
            keysReturn[n] = (TCBKeyFrame) keyFramesVector.get(n);
        }

        // return the array
        return keysReturn;
    }
}

class Land extends ComplexObject {

    public static final float WIDTH = 1.0f;

    public static final float LENGTH = 1.0f;

    public static final float HEIGHT = 0.0f;

    public Land(Component comp, Group g, int nFlags) {
        super(comp, g, nFlags);
    }

    protected Group createGeometryGroup(Appearance app, Vector3d position,
            Vector3d scale, String szTextureFile, String szSoundFile) {
        int nFlags = GeometryArray.COORDINATES | GeometryArray.NORMALS;

        if ((m_nFlags & TEXTURE) == TEXTURE) {
            nFlags |= GeometryArray.TEXTURE_COORDINATE_2;
        }

        QuadArray quadArray = new QuadArray(4, nFlags);

        float[] coordArray = {-WIDTH, HEIGHT, LENGTH, WIDTH, HEIGHT, LENGTH,
            WIDTH, HEIGHT, -LENGTH, -WIDTH, HEIGHT, -LENGTH};

        quadArray.setCoordinates(0, coordArray, 0, coordArray.length / 3);

        for (int n = 0; n < coordArray.length / 3; n++) {
            quadArray.setNormal(n, new Vector3f(0, 1, 0));
        }

        if ((m_nFlags & TEXTURE) == TEXTURE) {
            float[] texArray = {0, 0, 1, 0, 1, 1, 0, 1};

            quadArray.setTextureCoordinates(0, 0, texArray, 0,
                    coordArray.length / 3);
            setTexture(app, szTextureFile);
        }

        BranchGroup bg = new BranchGroup();
        Shape3D shape = new Shape3D(quadArray, app);
        bg.addChild(shape);

        return bg;
    }
}

/**
 * This class is a simple behavior that invokes the KeyNavigator to modify the
 * view platform transform.
 */
class CollisionBehavior extends Behavior {

    private WakeupOnCollisionEntry wakeupOne = null;

    private WakeupOnCollisionExit wakeupTwo = null;

    private WakeupCriterion[] wakeupArray = new WakeupCriterion[2];

    private WakeupCondition wakeupCondition = null;

    private ComplexObject m_Owner = null;

    public CollisionBehavior(Node node, ComplexObject owner) {
        wakeupOne = new WakeupOnCollisionEntry(node,
                WakeupOnCollisionEntry.USE_GEOMETRY);
        wakeupTwo = new WakeupOnCollisionExit(node,
                WakeupOnCollisionExit.USE_GEOMETRY);

        wakeupArray[0] = wakeupOne;
        wakeupArray[1] = wakeupTwo;

        wakeupCondition = new WakeupOr(wakeupArray);

        m_Owner = owner;

    }

    /**
     * Override Behavior's initialize method to setup wakeup criteria.
     */
    public void initialize() {
        // Establish initial wakeup criteria
        wakeupOn(wakeupCondition);
    }

    /**
     * Override Behavior's stimulus method to handle the event.
     */
    public void processStimulus(Enumeration criteria) {
        WakeupCriterion genericEvt;

        while (criteria.hasMoreElements()) {
            genericEvt = (WakeupCriterion) criteria.nextElement();

            if (genericEvt instanceof WakeupOnCollisionEntry) {
                m_Owner.onCollide(true);
            } else if (genericEvt instanceof WakeupOnCollisionExit) {
                m_Owner.onCollide(false);
            }
        }

        // Set wakeup criteria for next time
        wakeupOn(wakeupCondition);
    }
}
