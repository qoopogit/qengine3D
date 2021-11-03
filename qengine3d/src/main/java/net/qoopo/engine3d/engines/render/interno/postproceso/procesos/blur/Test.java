/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.core.util.ImgReader;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.test.FiltroBalanceBlanco;

/**
 *
 * @author alberto
 */
public class Test {

    public static void main(String[] args) {
        try {
//            BufferedImage img1= ImgReader.leerImagen(new File("/home/alberto/test.png"));
            BufferedImage img1 = ImgReader.leerImagen(new File("/home/alberto/testFiltro.png"));
//            BufferedImage img2= QProcesadorBlur.transposedHBlur(img1);

            int n = 8;

            for (int i = 1; i <= n; i++) {
                img1 = QProcesadorBlur.transposedHBlur(QProcesadorBlur.transposedHBlur(img1));
//                img1 = QProcesadorBlur.transposedHBlur(img1);
            }

            ImageIO.write(img1, "png", new File("/home/alberto/testFiltro1.png"));
            BufferedImage img3 = new FiltroBalanceBlanco().filtrar(img1);
            ImageIO.write(img3, "png", new File("/home/alberto/testFiltro2.png"));
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
