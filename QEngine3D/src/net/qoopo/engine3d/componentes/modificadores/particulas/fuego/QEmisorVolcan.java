/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.particulas.fuego;

import java.util.Random;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.componentes.modificadores.particulas.QEmisorParticulas;
import net.qoopo.engine3d.componentes.modificadores.particulas.QParticula;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;

/**
 *
 * @author alberto
 */
public class QEmisorVolcan extends QEmisorParticulas {

    private QMaterialBas material = null;

    private void cargarMaterial() {
        material = null;
        try {
            material = new QMaterialBas(QGestorRecursos.cargarTextura("fuego", "res/fuego/fuego1.png"), 64);
            material.setTransAlfa(0.90f);// el objeto tiene una transparencia
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QEmisorVolcan(AABB ambito, float tiempoVida, int maximoParticulas, int velocidadEmision, QVector3 direccion) {
        super(ambito, tiempoVida, maximoParticulas, velocidadEmision, direccion);
        cargarMaterial();
    }

    private void agregarParticulas() {

        particulasNuevas.clear();

        if (actuales < maximoParticulas) {

            Random rnd = new Random();
            for (int i = 1; i < velocidadEmision; i++) {
                actuales++;
//            QParticula nueva = new QParticula("copo");
                QParticula nueva = new QParticula();

                QEntidad nuevaEntidad = new QEntidad("flama");

                QGeometria geometria = new QPlano(0.5f, 0.5f);
                QMaterialUtil.aplicarMaterial(geometria, material);
                nuevaEntidad.agregarComponente(geometria);

                QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.DINAMICO);

                rigido.velocidadLinear = direccion.clone();
                rigido.setMasa(0.005f, QVector3.zero.clone());
                nuevaEntidad.agregarComponente(rigido);

                nueva.setTiempoVida(tiempoVida);
                nueva.iniciarVida();

                //ubicacion inicial de la particula
                nuevaEntidad.getTransformacion().trasladar(
                        rnd.nextFloat() * (ambito.aabMaximo.ubicacion.x - ambito.aabMinimo.ubicacion.x) + ambito.aabMinimo.ubicacion.x,
                        ambito.aabMaximo.ubicacion.y,
                        rnd.nextFloat() * (ambito.aabMaximo.ubicacion.z - ambito.aabMinimo.ubicacion.z) + ambito.aabMinimo.ubicacion.z
                );

                nueva.objeto = nuevaEntidad;

                this.particulasNuevas.add(nueva);
//            nueva
            }
            this.particulas.addAll(particulasNuevas);
        }
    }

    private void eliminarParticulas() {
        particulasEliminadas.clear();
        for (QParticula particula : this.particulas) {

            //si ya paso su tiempo de vida o ya esta fuera del ambito en la altura
            if ((System.currentTimeMillis() - particula.getTiempoCreacion()) / 1000 > particula.getTiempoVida()
                    || (particula.objeto.getTransformacion().getTraslacion().y < ambito.aabMinimo.ubicacion.y)) {
//                particulasEliminadas.add(copo);
                actuales--;
                particula.objeto.setRenderizar(false);
                particula.objeto.setEliminar(true);
            }
        }
        if (actuales < 0) {
            actuales = 0;
        }
        this.particulas.removeAll(particulasEliminadas);
    }

    private void modificarParticulas() {
        //la modificacion de las particulas de la nieva se basa en un zigzagueo hacia abajo
        Random rnd = new Random();
        float miniMov = -0.005f;
        float maxMov = 0.005f;
        for (QParticula copo : this.particulas) {
            //se agrega un impulso
            for (QComponente componente : copo.objeto.getComponentes()) {
                if (componente instanceof QObjetoRigido) {
                    ((QObjetoRigido) componente).agregarFuerzas(new QVector3(
                            rnd.nextFloat() * (maxMov - miniMov) + miniMov,
                            0,
                            rnd.nextFloat() * (maxMov - miniMov) + miniMov));
                }
            }
        }
    }

    @Override
    public void emitir(long deltaTime) {

        //verificar si hay que agregar nuevos
        agregarParticulas();

        //verifica el tiempo de vida de cada particula para eliminarla si ya expiro
        eliminarParticulas();

        //modifica las particulas actuales
        modificarParticulas();
//        System.out.println("particulas actuales =" + actuales);
    }

    @Override
    public void destruir() {
        material.destruir();
        material = null;
    }
}
