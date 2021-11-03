/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.vehiculo;

import java.io.Serializable;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QRueda implements Serializable {

    /*    
    private static final float suspensionRestLength = 0.6f;
     private static float wheelFriction = 1000;//1e30f;
    private static float suspensionStiffness = 20.f;
    private static float dampingRelaxation = 2.3f;
    private static float dampingCompression = 4.4f;
    private static float rollInfluence = 0.1f;//1.0f;
     */
    private float radio;
    private boolean frontal;
    private float ancho;
    private float suspensionRestLength = 0.6f;
    private float suspensionStiffness = 20.0f;
    private float dampingRelaxation = 2.3f;
    private float dampingCompression = 4.4f;
    private float friccion = 1000.0f;
    private float influenciaRodamiento = 0.1f;
    private QVector3 ubicacion = new QVector3();

    private QEntidad entidadRueda;

    public QRueda() {
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public boolean isFrontal() {
        return frontal;
    }

    public void setFrontal(boolean frontal) {
        this.frontal = frontal;
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

    public float getSuspensionRestLength() {
        return suspensionRestLength;
    }

    public void setSuspensionRestLength(float suspensionRestLength) {
        this.suspensionRestLength = suspensionRestLength;
    }

    public float getSuspensionStiffness() {
        return suspensionStiffness;
    }

    public void setSuspensionStiffness(float suspensionStiffness) {
        this.suspensionStiffness = suspensionStiffness;
    }

    public float getDampingRelaxation() {
        return dampingRelaxation;
    }

    public void setDampingRelaxation(float dampingRelaxation) {
        this.dampingRelaxation = dampingRelaxation;
    }

    public float getDampingCompression() {
        return dampingCompression;
    }

    public void setDampingCompression(float dampingCompression) {
        this.dampingCompression = dampingCompression;
    }

    public float getFriccion() {
        return friccion;
    }

    public void setFriccion(float friccion) {
        this.friccion = friccion;
    }

    public float getInfluenciaRodamiento() {
        return influenciaRodamiento;
    }

    public void setInfluenciaRodamiento(float influenciaRodamiento) {
        this.influenciaRodamiento = influenciaRodamiento;
    }

    public QEntidad getEntidadRueda() {
        return entidadRueda;
    }

    public void setEntidadRueda(QEntidad entidadRueda) {
        this.entidadRueda = entidadRueda;
    }

    public QVector3 getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(QVector3 ubicacion) {
        this.ubicacion = ubicacion;
    }

}
