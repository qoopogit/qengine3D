/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura;

/**
 *
 * @author alberto
 */
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import net.qoopo.engine3d.core.util.ImgReader;
import net.qoopo.engine3d.core.util.QGlobal;

/**
 *
 * @web http://java-buddy.blogspot.com/
 */
public class TextImagenTransparente {

    static JFrameWin jFrameWindow;

    public static class MyComponent extends JComponent {

        @Override
        protected void paintComponent(Graphics g) {
            try {
                //prepare a original Image source
                Image image = ImgReader.leerImagen(new File(QGlobal.RECURSOS + "texturas/transparent.png"));

                //Get current GraphicsConfiguration
                GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

                //Create a Compatible BufferedImage
                BufferedImage bufferedImage = graphicsConfiguration.createCompatibleImage(image.getWidth(null), image.getHeight(null));

                //Copy from original Image to new Compatible BufferedImage
                Graphics tempGraphics = bufferedImage.getGraphics();
                tempGraphics.drawImage(image, 0, 0, null);
                tempGraphics.dispose();

                //Create a Compatible BufferedImage for translucent image
                BufferedImage bufferedImage_translucent = graphicsConfiguration.createCompatibleImage(image.getWidth(null), image.getHeight(null), Transparency.TRANSLUCENT);
                //Copy from original Image to new Compatible BufferedImage
                Graphics tempGraphics_translucent = bufferedImage_translucent.getGraphics();
                tempGraphics_translucent.drawImage(image, 0, 0, null);
                tempGraphics_translucent.dispose();

                g.drawImage(bufferedImage, 0, 0, null);
                g.drawImage(bufferedImage_translucent, 0, bufferedImage.getHeight(), null);

            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    public static class JFrameWin extends JFrame {

        public JFrameWin() {
            this.setTitle("java-buddy.blogspot.com");
            this.setSize(300, 300);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            MyComponent myComponent = new MyComponent();
            this.add(myComponent);
        }
    }

    public static void main(String[] args) {
        Runnable doSwingLater = new Runnable() {

            @Override
            public void run() {
                jFrameWindow = new JFrameWin();
                jFrameWindow.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(doSwingLater);

    }

}
