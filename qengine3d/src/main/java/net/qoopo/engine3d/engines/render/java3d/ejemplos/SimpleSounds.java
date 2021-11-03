/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.java3d.ejemplos;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Appearance;
import javax.media.j3d.BackgroundSound;
import javax.media.j3d.BoundingLeaf;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Locale;
import javax.media.j3d.Material;
import javax.media.j3d.MediaContainer;
import javax.media.j3d.PhysicalBody;
import javax.media.j3d.PhysicalEnvironment;
import javax.media.j3d.PointSound;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.View;
import javax.media.j3d.ViewPlatform;
import javax.media.j3d.VirtualUniverse;
import javax.vecmath.Color3f;
import javax.vecmath.Point2f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
//import com.sun.j3d.audioengines.javasound.JavaSoundMixer;
import com.sun.j3d.utils.behaviors.keyboard.KeyNavigatorBehavior;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.SimpleUniverse;
import net.qoopo.engine3d.core.util.QGlobal;

/**
 * This application demonstrates the use of 3D sound. It loads three sounds:
 * loop3.wav is an ambient background sound and loop1.wav and loop2.wav are
 * point sounds. The two point sounds can be switched on and off use AWT
 * buttons. The user can navigate around the scene using the keyboard.
 *
 * @author I.J.Palmer
 * @version 1.0
 */
public class SimpleSounds extends Frame implements ActionListener {

    protected Canvas3D myCanvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());

    /**
     * The exit button to quit the application
     */
    protected Button exitButton = new Button("Exit");

    /**
     * The button to switch on and off the first sound
     */
    protected Button sound1Button = new Button("Sound 1");

    /**
     * The button to switch on and off the second sound
     */
    protected Button sound2Button = new Button("Sound 2");

    protected BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0,
            0.0), 10000.0);

    //Create the two point sounds
    PointSound sound1 = new PointSound();

    PointSound sound2 = new PointSound();

    /**
     * Build the view branch of the scene graph. In this case a key navigation
     * utility object is created and associated with the view transform so that
     * the view can be changed via the keyboard.
     *
     * @return BranchGroup that is the root of the view branch
     */
    protected BranchGroup buildViewBranch(Canvas3D c) {
        BranchGroup viewBranch = new BranchGroup();
        Transform3D viewXfm = new Transform3D();
        viewXfm.set(new Vector3f(0.0f, 0.0f, 30.0f));
        TransformGroup viewXfmGroup = new TransformGroup(viewXfm);
        viewXfmGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        viewXfmGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        ViewPlatform myViewPlatform = new ViewPlatform();
        BoundingSphere movingBounds = new BoundingSphere(new Point3d(0.0, 0.0,
                0.0), 100.0);
        BoundingLeaf boundLeaf = new BoundingLeaf(movingBounds);
        PhysicalBody myBody = new PhysicalBody();
        PhysicalEnvironment myEnvironment = new PhysicalEnvironment();
        viewXfmGroup.addChild(myViewPlatform);
        viewBranch.addChild(viewXfmGroup);
        View myView = new View();
        myView.addCanvas3D(c);
        myView.attachViewPlatform(myViewPlatform);
        myView.setPhysicalBody(myBody);
        myView.setPhysicalEnvironment(myEnvironment);
        KeyNavigatorBehavior keyNav = new KeyNavigatorBehavior(viewXfmGroup);
        keyNav.setSchedulingBounds(movingBounds);
        viewBranch.addChild(keyNav);
        //Create a sounds mixer to use our sounds with
        //and initialise it
        
//        JavaSoundMixer myMixer = new JavaSoundMixer(myEnvironment);
//        myMixer.initialize();
        return viewBranch;
    }

    /**
     * Add some lights to the scene graph
     *
     * @param b BranchGroup that the lights are added to
     */
    protected void addLights(BranchGroup b) {
        Color3f ambLightColour = new Color3f(0.5f, 0.5f, 0.5f);
        AmbientLight ambLight = new AmbientLight(ambLightColour);
        ambLight.setInfluencingBounds(bounds);
        Color3f dirLightColour = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f dirLightDir = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight dirLight = new DirectionalLight(dirLightColour,
                dirLightDir);
        dirLight.setInfluencingBounds(bounds);
        b.addChild(ambLight);
        b.addChild(dirLight);
    }

    /**
     * This adds a continuous background sound to the branch group.
     *
     * @param b BranchGroup to add the sound to.
     * @param soundFile String that is the name of the sound file.
     */
    protected void addBackgroundSound(BranchGroup b, String soundFile) {
        //Create a media container to load the file
        MediaContainer droneContainer = new MediaContainer(soundFile);
        //Create the background sound from the media container
        BackgroundSound drone = new BackgroundSound(droneContainer, 1.0f);
        //Activate the sound
        drone.setSchedulingBounds(bounds);
        drone.setEnable(true);
        //Set the sound to loop forever
        drone.setLoop(BackgroundSound.INFINITE_LOOPS);
        //Add it to the group
        b.addChild(drone);
    }

    /**
     * Add a sound to the transform group. This takes a point sound object and
     * loads into it a sounds from a given file. The edge of the sound's extent
     * is also defined in a parameter.
     *
     * @param tg TransformGroup that the sound is to be added to
     * @param sound PointSound to be used
     * @param soundFile String that is the name of the sound file to be loaded
     * @param edge float that represents the sound's maximum extent
     */
    protected void addObjectSound(TransformGroup tg, PointSound sound,
            String soundFile, float edge) {
        //First we get the current transform so that we can
        //position the sound in the same place
        Transform3D objXfm = new Transform3D();
        Vector3f objPosition = new Vector3f();
        tg.getTransform(objXfm);
        objXfm.get(objPosition);
        //Create the media container to load the sound
        MediaContainer soundContainer = new MediaContainer(soundFile);
        //Use the loaded data in the sound
        sound.setSoundData(soundContainer);
        sound.setInitialGain(1.0f);
        //Set the position to that of the given transform
        sound.setPosition(new Point3f(objPosition));
        //Allow use to switch the sound on and off
        sound.setCapability(PointSound.ALLOW_ENABLE_READ);
        sound.setCapability(PointSound.ALLOW_ENABLE_WRITE);
        sound.setSchedulingBounds(bounds);
        //Set it off to start with
        sound.setEnable(false);
        //Set it to loop forever
        sound.setLoop(BackgroundSound.INFINITE_LOOPS);
        //Use the edge value to set to extent of the sound
        Point2f[] attenuation = {new Point2f(0.0f, 1.0f),
            new Point2f(edge, 0.1f)};
        sound.setDistanceGain(attenuation);
        //Add the sound to the transform group
        tg.addChild(sound);
    }

    protected BranchGroup buildContentBranch() {
        //Create the appearance
        Appearance app = new Appearance();
        Color3f ambientColour = new Color3f(1.0f, 0.0f, 0.0f);
        Color3f emissiveColour = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f specularColour = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f diffuseColour = new Color3f(1.0f, 0.0f, 0.0f);
        float shininess = 20.0f;
        app.setMaterial(new Material(ambientColour, emissiveColour,
                diffuseColour, specularColour, shininess));
        //Make the cube
        Box myCube = new Box(1.0f, 1.0f, 1.0f, app);

        TransformGroup cubeGroup = new TransformGroup();

        BranchGroup contentBranch = new BranchGroup();
        addLights(contentBranch);
        addObjectSound(cubeGroup, sound1, "file:" + QGlobal.RECURSOS + "audio/wav/birds1.wav", 10.0f);
        addObjectSound(cubeGroup, sound2, "file:" + QGlobal.RECURSOS + "audio/wav/vaca.wav", 20.0f);
        addBackgroundSound(contentBranch, "file:" + QGlobal.RECURSOS + "audio/wav/goteo.wav");

        cubeGroup.addChild(myCube);
        contentBranch.addChild(cubeGroup);
        return contentBranch;

    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitButton) {
            dispose();
            System.exit(0);
        } else if (e.getSource() == sound1Button) {
            if (sound1.getEnable()) {
                sound1.setEnable(false);
            } else {
                sound1.setEnable(true);
            }
        } else if (e.getSource() == sound2Button) {
            sound2.setEnable(!sound2.getEnable());
        }
    }

    public SimpleSounds() {
        VirtualUniverse myUniverse = new VirtualUniverse();
        Locale myLocale = new Locale(myUniverse);
        //              buildUniverse(myCanvas3D);
        myLocale.addBranchGraph(buildContentBranch());
        myLocale.addBranchGraph(buildViewBranch(myCanvas3D));
        setTitle("SimpleSounds");
        setSize(400, 400);
        setLayout(new BorderLayout());
        Panel bottom = new Panel();
        bottom.add(sound1Button);
        bottom.add(sound2Button);
        bottom.add(exitButton);
        add(BorderLayout.CENTER, myCanvas3D);
        add(BorderLayout.SOUTH, bottom);
        exitButton.addActionListener(this);
        sound1Button.addActionListener(this);
        sound2Button.addActionListener(this);
        setVisible(true);
    }

    public static void main(String[] args) {
        SimpleSounds sw = new SimpleSounds();
    }
}
