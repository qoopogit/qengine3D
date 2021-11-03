/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.particulas;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;

/**
 *
 * @author alberto
 */
public abstract class QEmisorParticulas extends QComponente {

    //define el area sobre el que se va a emitir las particulas
    protected AABB ambito;

    protected List<QParticula> particulas = new ArrayList<>();
    protected List<QParticula> particulasEliminadas = new ArrayList<>();
    protected List<QParticula> particulasNuevas = new ArrayList<>();

    protected long actuales = 0;

    /**
     * Tiempo de vida de las particulas en segundos
     */
    protected float tiempoVida;

    /**
     * Máximo número de particulas a emitir al mismo tiempo
     */
    protected int maximoParticulas;
    /**
     * Número de particulas a emitir cada segundo
     */
    protected int velocidadEmision;

    /**
     * Direccion de emision de las particulas
     */
    protected QVector3 direccion;

    /**
     * Emite las particulas. Se llama en cada pasada de renderizado
     */
    public abstract void emitir(long deltaTime);

    public QEmisorParticulas(AABB ambito, float tiempoVida, int maximoParticulas, int velocidadEmision, QVector3 direccion) {
        this.ambito = ambito;
        this.tiempoVida = tiempoVida;
        this.maximoParticulas = maximoParticulas;
        this.velocidadEmision = velocidadEmision;
        this.direccion = direccion;
    }

    public List<QParticula> getParticulas() {
        return particulas;
    }

    public void setParticulas(List<QParticula> particulas) {
        this.particulas = particulas;
    }

    public List<QParticula> getParticulasEliminadas() {
        return particulasEliminadas;
    }

    public void setParticulasEliminadas(List<QParticula> particulasEliminadas) {
        this.particulasEliminadas = particulasEliminadas;
    }

    public List<QParticula> getParticulasNuevas() {
        return particulasNuevas;
    }

    public void setParticulasNuevas(List<QParticula> particulasNuevas) {
        this.particulasNuevas = particulasNuevas;
    }

    @Override
    public void destruir() {
        ambito = null;
        particulas.clear();
        particulas = null;
        particulasEliminadas.clear();
        particulasEliminadas = null;
        particulasNuevas.clear();
        particulasNuevas = null;
    }

}
