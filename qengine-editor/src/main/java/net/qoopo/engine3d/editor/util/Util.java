/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.editor.util;

import java.awt.Image;
import javax.swing.ImageIcon;
import net.qoopo.engine3d.editor.Principal;

/**
 *
 * @author alberto
 */
public class Util {
    
      public static ImageIcon cargarIcono12(String icono) {
        return cargarIcono(icono, 12, 12);
    }

    public static ImageIcon cargarIcono14(String icono) {
        return cargarIcono(icono, 14, 14);
    }

    public static ImageIcon cargarIcono16(String icono) {
        return cargarIcono(icono, 16, 16);
    }

    public static ImageIcon cargarIcono20(String icono) {
        return cargarIcono(icono, 20, 20);
    }

    public static ImageIcon cargarIcono24(String icono) {
        return cargarIcono(icono, 24, 24);
    }

    public static ImageIcon cargarIcono32(String icono) {
        return cargarIcono(icono, 32, 32);
    }

    public static ImageIcon cargarIcono(String icono, int ancho, int alto) {
        try {
            return new ImageIcon(new ImageIcon(Principal.class.getResource(icono)).getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ImageIcon cargarIcono(String icono) {
        return new ImageIcon(Principal.class.getResource(icono));
    }
}
