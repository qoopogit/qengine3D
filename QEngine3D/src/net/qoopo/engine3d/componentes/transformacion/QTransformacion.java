/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.transformacion;

import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QRotacion;

/**
 *
 * @author alberto
 */
public class QTransformacion extends QComponente {

    private final QMatriz4 matriz = new QMatriz4();
    private QVector3 traslacion = QVector3.zero.clone();
    private QVector3 escala = QVector3.unitario_xyz.clone();
    private QRotacion rotacion = new QRotacion(QRotacion.ANGULOS_EULER);

    public QTransformacion() {
    }

    public QTransformacion(int tipoRotacion) {
        rotacion = new QRotacion(tipoRotacion);
    }

    public QVector3 getTraslacion() {
        return traslacion;
    }

    public void setTraslacion(QVector3 traslacion) {
        this.traslacion.set(traslacion);
    }

    public QVector3 getEscala() {
        return escala;
    }

    public void setEscala(QVector3 escala) {
        this.escala.set(escala);
    }

    public QRotacion getRotacion() {
        return rotacion;
    }

    public void setRotacion(QRotacion rotacion) {
        this.rotacion = rotacion;
    }

    public void desdeMatrix(QMatriz4 matriz) {
        traslacion.set(matriz.toTranslationVector());
        escala.set(matriz.toScaleVector());
        rotacion.setCuaternion(matriz.toRotationQuat());
        rotacion.actualizarAngulos();
    }

    public QMatriz4 toTransformMatriz() {
        matriz.loadIdentity();
        matriz.setTranslation(traslacion);
        matriz.setRotationCuaternion(rotacion.getCuaternion());
        matriz.setScale(escala);
        return matriz;
    }

    @Override
    public QTransformacion clone() {
        QTransformacion nuevo = new QTransformacion();
        try {
            nuevo.setEscala(escala.clone());
            nuevo.setTraslacion(traslacion.clone());
            nuevo.setRotacion(rotacion.clone());
//        nuevo.entidad= entidad.clone();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nuevo;
    }

    public void trasladar(float x, float y, float z) {
        traslacion.setXYZ(x, y, z);
    }

    public void trasladar(QVector3 nuevaPosicion) {
        traslacion.set(nuevaPosicion);
    }
    
    public void escalar(float x, float y, float z) {
        escala.setXYZ(x, y, z);
    }

    public void escalar(QVector3 valor) {
        escala.set(valor);
    }

    @Override
    public void destruir() {
        traslacion = null;
        escala = null;
        rotacion = null;
    }

    @Override
    public String toString() {
        return "QTRans {" + "t=" + traslacion + ", e=" + escala + ", r=" + rotacion.toString() + '}';
    }

    /**
     * Realiza la interporlacion entre dos trasnforamcion
     *
     * @param origen
     * @param destino
     * @param progresion
     * @return
     */
    public static QTransformacion interpolar(QTransformacion origen, QTransformacion destino, float progresion) {
        QTransformacion nueva = new QTransformacion(QRotacion.CUATERNION);
        QMath.linear(nueva.traslacion, progresion, origen.traslacion, destino.traslacion);
        QMath.linear(nueva.escala, progresion, origen.escala, destino.escala);
        //interpolacion espefira (SLERP)
//        nueva.getRotacion().setCuaternion(new Cuaternion(origen.getRotacion().getCuaternion(), destino.getRotacion().getCuaternion(), progresion));
        //interpolacion normalizada (NLERP) , m[as rapida
        nueva.getRotacion().getCuaternion().set(origen.getRotacion().getCuaternion());
        nueva.getRotacion().getCuaternion().nlerp(destino.getRotacion().getCuaternion(), progresion);
        return nueva;
    }
}
