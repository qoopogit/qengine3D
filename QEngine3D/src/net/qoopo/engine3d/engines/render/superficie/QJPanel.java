/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.superficie;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author alberto
 */
//public class QJPanel extends JPanel{
public class QJPanel extends JComponent {

    private BufferedImage imagen;

    public BufferedImage getImagen() {
        return imagen;
    }

    public void setImagen(BufferedImage imagen) {
        this.imagen = imagen;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (imagen != null) {
            g.drawImage(imagen, 0, 0,
                    getWidth(),
                    getHeight(),
                    null);
        }
//        g.clearRect(0, 0, WIDTH, HEIGHT);
//        super.paint(g); //To change body of generated methods, choose Tools | Templates.
    }

}
