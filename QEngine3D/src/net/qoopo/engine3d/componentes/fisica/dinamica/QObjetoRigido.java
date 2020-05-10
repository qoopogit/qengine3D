/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.dinamica;

import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QObjetoRigido extends QObjetoDinamico {

    public QVector3 velocidadLinear = QVector3.zero.clone();
    public QVector3 velocidadAngular = QVector3.zero.clone();
    public QVector3 fuerzaInercial = QVector3.zero.clone();

    //cada objeto tiene una afectacion diferente de la gravedad
    // este vector peso sera el resultado de multiplicar la gravedad por la masa    
    public QVector3 peso = QVector3.zero.clone();

    //la fuerza que se aplica en cada pasada a la velocidad
    public QVector3 fuerzaTotal = QVector3.zero.clone();
    public QVector3 fuerzaTorque = QVector3.zero.clone();

    private float masa = 1.0f;
    private float friccion = 0.5f;
    private float restitucion = 0.f; //elasticidad
    private float amortiguacion_traslacion = 0.04f; //linear
    private float amortiguacion_rotacion = 0.1f; //angular

    private boolean usado;

    public QObjetoRigido(int tipo_dinamico) {
        super(tipo_dinamico);
        if (tipo_dinamico == ESTATICO) {
            masa = 0;
        }
    }

    public QObjetoRigido(int tipo_dinamico, float masa) {
        super(tipo_dinamico);
        this.masa = masa;
        if (tipo_dinamico == ESTATICO) {
            masa = 0;
        }
    }

    /**
     * Agrega un impulso
     *
     * @param nuevaFuerza
     */
    public void agregarFuerzas(QVector3... nuevaFuerza) {
        fuerzaTotal.add(nuevaFuerza);
    }

    public void agregarFuerzasTorque(QVector3... nuevaFuerza) {
        fuerzaTorque.add(nuevaFuerza);
    }

//    public void agregarFuerzas(float deltaTime, QVector3... nuevaFuerza) {
//        fuerzaTotal.add(deltaTime, nuevaFuerza);
//    }
    public void aplicarGravedad() {
        if (tipoDinamico == QObjetoDinamico.DINAMICO) {
            agregarFuerzas(peso);
        }
    }

    public void actualizarVelocidad(float deltaTime) {
        if (tipoDinamico == QObjetoDinamico.DINAMICO) {
            velocidadLinear.add(fuerzaTotal.multiply(deltaTime));
        }
    }

    public void actualizarMovimiento(float deltaTime) {
        if (tipoDinamico == QObjetoDinamico.DINAMICO || tipoDinamico == QObjetoDinamico.CINEMATICO) {
            actualizarVelocidad(deltaTime);
//            transformacion.getTraslacion().add(velocidadLinear);
            entidad.mover(entidad.getTransformacion().getTraslacion().clone().add(velocidadLinear));
            //luego llamo al metodo para indicar que su movimeinto fue actualizado 
            entidad.comprobarMovimiento();
        }
    }

    public void limpiarFuezas() {
        fuerzaTotal.setXYZ(0, 0, 0);
        fuerzaTorque.setXYZ(0, 0, 0);
    }

    public void addForceAtPosition(QVector3 force, QVector3 position) {
        agregarFuerzasTorque(position.clone().crossProduct(force));
    }

    public void detener() {
        limpiarFuezas();
        velocidadLinear.setXYZ(0, 0, 0);
    }

    public void calcularPeso(QVector3 gravedad) {
        if (masa != 0f) {
            this.peso = gravedad.clone().multiply(masa);
        }
    }

    public QVector3 getPeso() {
        return peso.clone();
    }

    public float getMasa() {
        return masa;
    }

    public void setMasa(float masa, QVector3 inercia) {
        if (masa == 0f) {
//            this.tipoDinamico = ESTATICO;
        } else {
//            this.tipoDinamico = DINAMICO;
//            this.masa = 1 / masa;
            this.masa = masa;
        }
        this.fuerzaInercial.setXYZ(
                inercia.x != 0f ? 1f / inercia.x : 0f,
                inercia.y != 0f ? 1f / inercia.y : 0f,
                inercia.z != 0f ? 1f / inercia.z : 0f);

    }

    public QVector3 getFuerzaInercial() {
        return fuerzaInercial;
    }

    public void setFuerzaInercial(QVector3 fuerzaInercial) {
        this.fuerzaInercial = fuerzaInercial;
    }

    public float getFriccion() {
        return friccion;
    }

    public void setFriccion(float friccion) {
        this.friccion = friccion;
    }

    public float getRestitucion() {
        return restitucion;
    }

    public void setRestitucion(float restitucion) {
        this.restitucion = restitucion;
    }

    public float getAmortiguacion_traslacion() {
        return amortiguacion_traslacion;
    }

    public void setAmortiguacion_traslacion(float amortiguacion_traslacion) {
        this.amortiguacion_traslacion = amortiguacion_traslacion;
    }

    public float getAmortiguacion_rotacion() {
        return amortiguacion_rotacion;
    }

    public void setAmortiguacion_rotacion(float amortiguacion_rotacion) {
        this.amortiguacion_rotacion = amortiguacion_rotacion;
    }

    public boolean isUsado() {
        return usado;
    }

    public void setUsado(boolean usado) {
        this.usado = usado;
    }

}
