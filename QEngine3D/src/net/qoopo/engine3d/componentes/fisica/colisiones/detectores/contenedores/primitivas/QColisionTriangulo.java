/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QTriangulo;
import net.qoopo.engine3d.core.math.QVector3;

/**
 * Define una esfera contenedora del objeto
 *
 * @author alberto
 */
public class QColisionTriangulo extends QFormaColision {

    private QVector3 p1;
    private QVector3 p2;
    private QVector3 p3;

    public QColisionTriangulo() {
    }

    public QColisionTriangulo(QTriangulo triangulo) {
        p1 = new QVector3(triangulo.listaVertices[0]);
        p2 = new QVector3(triangulo.listaVertices[1]);
        p3 = new QVector3(triangulo.listaVertices[2]);
    }

    public QColisionTriangulo(QVector3 p1, QVector3 p2, QVector3 p3) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
    }

    public QVector3 getP1() {
        return p1;
    }

    public void setP1(QVector3 p1) {
        this.p1 = p1;
    }

    public QVector3 getP2() {
        return p2;
    }

    public void setP2(QVector3 p2) {
        this.p2 = p2;
    }

    public QVector3 getP3() {
        return p3;
    }

    public void setP3(QVector3 p3) {
        this.p3 = p3;
    }

    @Override
    public void destruir() {

    }

    @Override
    public boolean verificarColision(QFormaColision otro) {
        return false;
    }
}
