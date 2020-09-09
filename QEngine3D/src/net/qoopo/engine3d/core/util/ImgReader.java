/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author alberto
 */
public class ImgReader {

    public static BufferedImage leerImagen(File archivo) throws IOException {
        if (archivo.getAbsolutePath().toLowerCase().endsWith(".tga")) {
            return TargaReader.getImage(archivo.getAbsolutePath());
        } else {
            return ImageIO.read(archivo);
        }
    }

    public static BufferedImage leerImagen(InputStream is) throws IOException {
//        if (archivo.getAbsolutePath().toLowerCase().endsWith(".tga")) {
//            return TargaReader.getImage(archivo.getAbsolutePath());
//        } else {
        return ImageIO.read(is);
//        }
    }
}
