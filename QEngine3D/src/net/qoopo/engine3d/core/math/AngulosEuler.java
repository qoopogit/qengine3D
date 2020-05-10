/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.math;

import java.io.Serializable;

/**
 *
 * @author alberto
 */
public class AngulosEuler implements Serializable {

    private float anguloX = 0;
    private float anguloY = 0;
    private float anguloZ = 0;

    public AngulosEuler() {
        this(0, 0, 0);
    }

    public AngulosEuler(float x, float y, float z) {
        this.anguloX = x;
        this.anguloY = y;
        this.anguloY = z;
    }

    public void set(float x, float y, float z) {
        this.anguloX = x;
        this.anguloY = y;
        this.anguloY = z;
    }

    public float getAnguloX() {
        return anguloX;
    }

    public void setAnguloX(float anguloX) {
        this.anguloX = anguloX;
    }

    public float getAnguloY() {
        return anguloY;
    }

    public void setAnguloY(float anguloY) {
        this.anguloY = anguloY;
    }

    public float getAnguloZ() {
        return anguloZ;
    }

    public void setAnguloZ(float anguloZ) {
        this.anguloZ = anguloZ;
    }

    @Override
    public AngulosEuler clone() {
        return new AngulosEuler(anguloX, anguloY, anguloZ);
    }

    @Override
    public String toString() {
        return "AE {" + "x=" + anguloX + ", y=" + anguloY + ", z=" + anguloZ + '}';
    }

}
