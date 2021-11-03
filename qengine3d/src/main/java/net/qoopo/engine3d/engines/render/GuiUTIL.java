/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextField;

/**
 *
 * @author alberto
 */
public class GuiUTIL {

    public static void centrarVentana(JFrame ventana) {
//        ventana.setSize(500, 350);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    public static JLabel crearJLabel(String texto) {
        return crearJLabel(texto, null, null);
    }

    public static JLabel crearJLabel(String texto, String toolTipText) {
        return crearJLabel(texto, toolTipText, null);
    }

    public static JLabel crearJLabel(String texto, Icon icono) {
        return crearJLabel(texto, null, icono);
    }

    public static JLabel crearJLabel(String texto, String tooltipText, Icon icono) {
        JLabel r = new JLabel(texto);
        r.setToolTipText(tooltipText);
        r.setIcon(icono);
        return r;
    }

    public static JTextField crearJTextField(String texto, String tooltipText) {
        JTextField r = new JTextField(texto);
        r.setToolTipText(tooltipText);
        return r;
    }

    public static JButton crearJButton(String texto, String tooltipText, Icon icono, boolean pintarBorde) {
        JButton r = new JButton();
        r.setText(texto);
        r.setToolTipText(tooltipText);
        r.setIcon(icono);
        r.setBorderPainted(pintarBorde);
        return r;
    }

    public static JMenuItem crearMenuItem(String texto, String tooltipText, Icon icono, boolean pintarBorde) {
        JMenuItem r = new JMenuItem();
        r.setText(texto);
        r.setToolTipText(tooltipText);
        r.setIcon(icono);
        r.setBorderPainted(pintarBorde);
        return r;
    }

    public static JMenu crearMenu(String texto, String tooltipText, Icon icono, boolean pintarBorde) {
        JMenu r = new JMenu();
        r.setText(texto);
        r.setToolTipText(tooltipText);
        r.setIcon(icono);
        r.setBorderPainted(pintarBorde);
        return r;
    }

    public static void centrarVentana(JFrame ventana, int ancho, int alto) {
        ventana.setSize(ancho, alto);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

}
