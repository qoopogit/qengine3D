/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;

/**
 * Esta clase define un plano para realizar clipping de escenas y de esta manera
 * no dibujar lo que este fuera de este plano
 *
 * @author alberto
 */
public class QClipPane {

    /**
     * La normal de plano Los puntos que esten de este lado seran los visibles
     */
    private QVector3 normal;
    /**
     * La distancia que existe desde el origen al plano
     */
    private float distancia;

    private QVector3 pos1;
    private QVector3 pos2;
    private QVector3 pos3;

    /**
     * Construye un plano a partir de una direccion y la distancia del plano al
     * origen
     *
     * @param direccion
     * @param distancia
     */
    public QClipPane(QVector3 direccion, float distancia) {
        this.normal = direccion;
        normal.normalize();
        this.distancia = distancia;
    }

    /**
     * Construye un plano a partir de 3 vectores de posicion
     *
     * @param pos1
     * @param pos2
     * @param pos3
     */
    public QClipPane(QVector3 pos1, QVector3 pos2, QVector3 pos3) {
        normal = new QVector3(pos1, pos2);
        normal.crossProduct(new QVector3(pos2, pos3));
        normal.normalize();
//        QVector3 center = new QVector3();
//        center.add(pos1, pos2, pos3);
//        center.multiply(1.0f / 3.0f);
//        this.distancia = center.length();
//segun https://www.cubic.org/docs/3dclip.htm
        this.distancia = normal.dot(pos1);

        this.pos1 = pos1;
        this.pos2 = pos2;
        this.pos3 = pos3;
//        System.out.println("Plano construido");
//        System.out.println("    Normal[" + normal.toString() + "]");
//        System.out.println("    distancia[" + distancia + "]");
    }

    public QVector3 getNormal() {
        return normal;
    }

    public void setNormal(QVector3 normal) {
        this.normal = normal;
    }

    public float getDistancia() {
        return distancia;
    }

    public void setDistancia(float distancia) {
        this.distancia = distancia;
    }

    public boolean esVisible(QVector3 posicion) {
        return (posicion.dot(normal) - distancia) > 0;
//        return (posicion.dot(normal) - distancia) < 0;
    }
    public boolean esVisible(QVector4 posicion) {
        return (posicion.getVector3().dot(normal) - distancia) > 0;
//        return (posicion.dot(normal) - distancia) < 0;
    }

    public float distancia(QVector3 posicion) {
        return (posicion.dot(normal) - distancia);
    }

//    public float distancia(QVector3 posicion) {
//        float D = -((normal.x * pos1.x) + (normal.y * pos1.y) + (normal.z * pos1.z));
//        float dist = ((normal.x * posicion.x) + (normal.y * posicion.y) + (normal.z * posicion.z) + D);
//        return dist;
//    }
}
