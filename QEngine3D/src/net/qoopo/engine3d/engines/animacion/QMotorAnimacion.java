/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.animacion;

import net.qoopo.engine3d.QMotor;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.animacion.QComponenteAnimacion;

import net.qoopo.engine3d.core.escena.QEscena;

/**
 * Este motor se encarga de procesar los frames de animacion
 *
 * @author alberto
 */
public class QMotorAnimacion extends QMotor {

    protected QEscena universo;
    private float tiempoInicio = 0;
    private float tiempo;
    private float tiempoFin = 10;
    private float velocidad = 1.0f;
    private float direccion = 1.0f;

    public QMotorAnimacion(QEscena universo) {
        this.universo = universo;
    }

    @Override
    public void iniciar() {
        ejecutando = true;
        tiempoPrevio = System.currentTimeMillis();
    }

    @Override
    public void detener() {
        ejecutando = false;
        tiempo = 0;
        actualizarPoses(0);
        try {
            Thread.sleep(300);
        } catch (Exception e) {

        }
    }

    public void velocidad1x() {
        velocidad = 1.0f;
    }

    public void velocidad2x() {
        velocidad = 2.0f;
    }

    public void velocidad4x() {
        velocidad = 4.0f;
    }

    public void velocidad05x() {
        velocidad = 0.5f;
    }

    public void velocidad04x() {
        velocidad = 0.4f;
    }

    /**
     *
     * @param marcaTiempo
     */
    public void actualizarPoses(float marcaTiempo) {
        try {
            QComponenteAnimacion actual;
            for (QEntidad entidad : QEscena.INSTANCIA.getListaEntidades()) {
                if (entidad.isRenderizar()) {
                    for (QComponente componente : entidad.getComponentes()) {
                        if (componente instanceof QComponenteAnimacion) {
                            actual = (QComponenteAnimacion) componente;
                            actual.updateAnim(marcaTiempo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public long update() {
        tiempo += (getDelta() * velocidad / (1000.0f)) * direccion;
        if (tiempo > tiempoFin) {
            tiempo %= tiempoFin + tiempoInicio;
        }
        if (tiempo < 0.00) {
            tiempo = tiempoFin - tiempo;
        }
        actualizarPoses(tiempo);
        tiempoPrevio = System.currentTimeMillis();
        return tiempoPrevio;
    }

    public QEscena getUniverso() {
        return universo;
    }

    public void setUniverso(QEscena universo) {
        this.universo = universo;
    }

    public float getTiempoInicio() {
        return tiempoInicio;
    }

    public void setTiempoInicio(float tiempoInicio) {
        this.tiempoInicio = tiempoInicio;
    }

    public float getTiempo() {
        return tiempo;
    }

    public void setTiempo(float tiempo) {
        this.tiempo = tiempo;
    }

    public float getTiempoFin() {
        return tiempoFin;
    }

    public void setTiempoFin(float tiempoFin) {
        this.tiempoFin = tiempoFin;
    }

    public float getVelocidad() {
        return velocidad;
    }

    public void setVelocidad(float velocidad) {
        this.velocidad = velocidad;
    }

    public float getDireccion() {
        return direccion;
    }

    public void setDireccion(float direccion) {
        this.direccion = direccion;
    }

    public void invertir() {
        direccion *= -1.0f;
    }

}
