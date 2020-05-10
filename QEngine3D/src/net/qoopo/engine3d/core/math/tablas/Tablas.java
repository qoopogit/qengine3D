/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.math.tablas;

import java.util.Random;

/**
 *
 * @author alberto
 */
public class Tablas {

    static final int precision = 25; // gradations per degree, adjust to suit
    static final int modulus = 360 * precision;
    static final float[] sin = new float[modulus]; // lookup table

    static {
        // a static initializer fills the table
        // in this implementation, units are in degrees
        for (int i = 0; i < sin.length; i++) {
            sin[i] = (float) Math.sin((i * Math.PI) / (precision * 180));
        }
    }
// Private function for table lookup

    private static float sinLookup(int a) {
        return a >= 0 ? sin[a % (modulus)] : -sin[-a % (modulus)];
    }

// These are your working functions:
    public static float sin(float a) {
        return sinLookup((int) (a * precision + 0.5f));
    }

    public static float cos(float a) {
        return sinLookup((int) ((a + 90f) * precision + 0.5f));
    }

    //-----------------------------------------------------------
    public static void main(String[] args) {
        int reps = 1 << 23;
        int sets = 10;

        System.out.println("Reps=" + reps);
//        Q.pl();
        System.out.println("  Trial  sinTab  cosTab  sinLib");
        for (int i = 0; i < sets; i++) {
            System.out.println("");
            System.out.println(String.format("%7d\t%7.2f\t%7.2f\t%7.2f\n", i, testSinTab(reps), testCosTab(reps), testSinLib(reps)));
        }
    }

    private static float[] sample(int n) {
        Random rand = new Random();
        float[] values = new float[n];
        for (int i = 0; i < n; i++) {
            values[i] = 400 * (rand.nextFloat() * 2 - 1);
        }
        return values;
    }

    private static float testSinTab(int n) {
        float[] sample = sample(n);
        long time = -System.nanoTime();
        for (int i = 0; i < n; i++) {
            sample[i] = sin(sample[i]);
        }
        time += System.nanoTime();
        return (time / 1e6f);
    }

    private static float testCosTab(int n) {
        float[] sample = sample(n);
        long time = -System.nanoTime();
        for (int i = 0; i < n; i++) {
            sample[i] = cos(sample[i]);
        }
        time += System.nanoTime();
        return time / 1e6f;
    }

    private static float testSinLib(int n) {
        float[] sample = sample(n);
        long time = -System.nanoTime();
        for (int i = 0; i < n; i++) {
            sample[i] = (float) Math.sin(sample[i]);
        }
        time += System.nanoTime();
        return time / 1e6f;
    }
}
