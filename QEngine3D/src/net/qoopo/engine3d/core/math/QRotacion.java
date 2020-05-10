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
public class QRotacion implements Serializable {

    public static final int ANGULOS_EULER = 1;
    public static final int CUATERNION = 2;
    private int tipo = ANGULOS_EULER;
    private Cuaternion cuaternion = new Cuaternion();
    private AngulosEuler angulos = new AngulosEuler();

    public QRotacion() {
        tipo = ANGULOS_EULER;
        actualizarCuaternion();
    }

    public void inicializar() {
        angulos.setAnguloX(0);
        angulos.setAnguloY(0);
        angulos.setAnguloZ(0);
        actualizarCuaternion();
    }

    public QRotacion(int tipo) {
        this.tipo = tipo;
        actualizarCuaternion();
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public Cuaternion getCuaternion() {
        if (tipo == ANGULOS_EULER) {
            actualizarCuaternion();
        }
        return cuaternion;
    }

    public void setCuaternion(Cuaternion cuaternion) {
        this.cuaternion = cuaternion;
    }

    public AngulosEuler getAngulos() {
        if (tipo == CUATERNION) {
            actualizarAngulos();
        }
        return angulos;
    }

    public void actualizarAngulos() {
        float[] ang = cuaternion.toAngles(null);
        angulos.setAnguloX(ang[0]);
        angulos.setAnguloY(ang[1]);
        angulos.setAnguloZ(ang[2]);
    }

    public void actualizarCuaternion() {
        cuaternion.fromAngles(angulos.getAnguloX(), angulos.getAnguloY(), angulos.getAnguloZ());
    }

    public void setAngulos(AngulosEuler angulos) {
        this.angulos = angulos;
    }

    public void rotarX(float angulo) {
        angulos.setAnguloX(angulo);
        actualizarCuaternion();
    }

    public void rotarY(float angulo) {
        angulos.setAnguloY(angulo);
        actualizarCuaternion();
    }

    public void rotarZ(float angulo) {
        angulos.setAnguloZ(angulo);
        actualizarCuaternion();
    }

    public void aumentarRotacion(float x, float y, float z) {
        aumentarRotX(x);
        aumentarRotY(y);
        aumentarRotZ(z);
    }

    public void aumentarRotX(float angulo) {
        rotarX(angulos.getAnguloX() + angulo);
    }

    public void aumentarRotY(float angulo) {
        rotarY(angulos.getAnguloY() + angulo);
    }

    public void aumentarRotZ(float angulo) {
        rotarZ(angulos.getAnguloZ() + angulo);
    }

    @Override
    public QRotacion clone() {
        QRotacion nuevo = new QRotacion(tipo);
        nuevo.setAngulos(angulos.clone());
        nuevo.setCuaternion(cuaternion.clone());
        return nuevo;
    }

    @Override
    public String toString() {
        return "QRot {" + "t=" + tipo + ", c=" + cuaternion + ", a=" + angulos + '}';
    }

}
