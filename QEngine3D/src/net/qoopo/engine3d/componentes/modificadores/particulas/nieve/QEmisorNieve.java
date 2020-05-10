/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.particulas.nieve;

import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.componentes.modificadores.particulas.QEmisorParticulas;
import net.qoopo.engine3d.componentes.modificadores.particulas.QParticula;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlanoBillboard;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.textura.QTextura;

/**
 *
 * @author alberto
 */
public class QEmisorNieve extends QEmisorParticulas {

    private QMaterialBas material = null;

    private void cargarMaterial() {
        material = null;
        try {
            material = new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/nieve/copo0.png"))), 64);
//            material.setTransAlfa(0.90f);// el objeto tiene una transparencia 
            material.setColorTransparente(QColor.BLACK);
            material.setTransparencia(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public QEmisorNieve(AABB ambito, float tiempoVida, int maximoParticulas, int velocidadEmision, QVector3 direccion) {
        super(ambito, tiempoVida, maximoParticulas, velocidadEmision, direccion);
        cargarMaterial();
    }

    private void agregarParticulas() {

        particulasNuevas.clear();

        if (actuales < maximoParticulas) {

            Random rnd = new Random();
            for (int i = 1; i < velocidadEmision; i++) {
                actuales++;
                QParticula nueva = new QParticula();
                QEntidad entParticula = new QEntidad("copo");
                entParticula.setBillboard(true);
                QGeometria geometria = new QPlanoBillboard(0.05f, 0.05f);
                QMaterialUtil.aplicarMaterial(geometria, material);
                entParticula.agregarComponente(geometria);

                QFormaColision colision = new QColisionMallaConvexa(geometria);
                entParticula.agregarComponente(colision);

                QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.DINAMICO);
                rigido.setMasa(0.00005f, QVector3.zero.clone());
//                rigido.setMasa(0.0005f, QVector3.zero.clone());
                rigido.setFormaColision(colision);
                entParticula.agregarComponente(rigido);

                nueva.setTiempoVida(tiempoVida);
                nueva.iniciarVida();

                //ubicacion inicial de la particula
                entParticula.mover(
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().x + rnd.nextFloat() * (ambito.aabMaximo.ubicacion.x - ambito.aabMinimo.ubicacion.x) + ambito.aabMinimo.ubicacion.x,
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().y + ambito.aabMaximo.ubicacion.y,
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().z + rnd.nextFloat() * (ambito.aabMaximo.ubicacion.z - ambito.aabMinimo.ubicacion.z) + ambito.aabMinimo.ubicacion.z
                );
                entParticula.rotar(0, (float) (rnd.nextFloat() * Math.toRadians(180)), 0);
                nueva.objeto = entParticula;
//                nueva.objeto.setPadre(this.entidad);
//                this.entidad.agregarHijo(nueva.objeto);
                this.particulasNuevas.add(nueva);
//            nueva
            }
            this.particulas.addAll(particulasNuevas);
        }
    }

    private void eliminarParticulas() {
        particulasEliminadas.clear();
        for (QParticula particula : this.particulas) {
//            copo.setTiempoVida(copo.getTiempoVida() - 0.1f);
            //si ya paso su tiempo de vida o ya esta fuera del ambito en la altura
            if ((System.currentTimeMillis() - particula.getTiempoCreacion()) / 1000 > particula.getTiempoVida()
                    || (particula.objeto.getTransformacion().getTraslacion().y < ambito.aabMinimo.ubicacion.y)) {
                particulasEliminadas.add(particula);
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

    private void modificarParticulas(long deltaTime) {
        //la modificacion de las particulas de la nieva se basa en un zigzagueo hacia abajo
        Random rnd = new Random();
        float miniMov = -0.00009f;
        float maxMov = 0.00009f;

        for (QParticula copo : this.particulas) {
            QObjetoRigido rigido = QUtilComponentes.getFisicoRigido(copo.objeto);
            rigido.agregarFuerzas(new QVector3(
                    rnd.nextFloat() * (maxMov - miniMov) + miniMov,
                    rnd.nextFloat() * (maxMov - miniMov) + miniMov,
                    rnd.nextFloat() * (maxMov - miniMov) + miniMov));
        }
    }

    @Override
    public void emitir(long deltaTime) {

        //verificar si hay que agregar nuevos
        agregarParticulas();

        //verifica el tiempo de vida de cada particula para eliminarla si ya expiro
        eliminarParticulas();

        //modifica las particulas actuales
        modificarParticulas(deltaTime);
    }

    @Override
    public void destruir() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
