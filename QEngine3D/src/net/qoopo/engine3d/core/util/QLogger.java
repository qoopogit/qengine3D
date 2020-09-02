/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

/**
 *
 * @author alberto
 */
public class QLogger {

    public static boolean activo = true;

    public static void info(String msg) {
        if (activo) {
            System.out.println(msg);
        }
    }
}
