/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.math.tablas;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alberto
 */
public class QTablas {

    public static int calculadas = 0;
    public static int tablas = 0;

    public static Map<Float, Float> coseno = new HashMap<>();
    public static Map<Float, Float> seno = new HashMap<>();

    public static void iniciarCosenos() {
        coseno.clear();
    }

    public static void iniciarSenos() {
        seno.clear();
    }

    public static Float coseno(Float angulo) {
        Float cos = coseno.get(angulo);
        if (cos == null) {
            calculadas++;
            cos = (float) Math.cos(angulo);
            coseno.put(angulo, cos);
        } else {
            tablas++;
        }
        return cos;
    }

    public static Float seno(Float angulo) {
        Float sen = seno.get(angulo);
        if (sen == null) {
            calculadas++;
            sen = (float) Math.sin(angulo);
            seno.put(angulo, sen);
        } else {
            tablas++;
        }
        return sen;
    }

}
