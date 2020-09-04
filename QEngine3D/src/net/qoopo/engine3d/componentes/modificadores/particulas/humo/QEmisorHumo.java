/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.particulas.humo;

import java.util.Random;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.componentes.modificadores.particulas.QEmisorParticulas;
import net.qoopo.engine3d.componentes.modificadores.particulas.QParticula;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlanoBillboard;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorAtlasSecuencial;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class QEmisorHumo extends QEmisorParticulas {

//    private QMaterial material = null;
    int maximoLuces = 3;
    private QTextura textura;
    float intencidadLuz = 0.12f;
    int actualLuz = 0;
    private QGeometria geometria = new QPlanoBillboard(0.25f, 0.25f);
    private Random rnd = new Random();

//mantengo una lista de luces fijas y las voy a asignando a las nuevas particulas generadas
    //solo voy a crear las luces que corresponden al numero de particulas nuevas
    private void cargarTextura() {
        try {
//            textura = new QTextura(ImageIO.read(new File("res/texturas/humo/smoke_atlas_2.jpeg")), QTextura.TIPO_MAPA_DIFUSA);
//            textura = new QTextura(ImageIO.read(new File("res/texturas/humo/smoke_atlas_1.png")), QTextura.TIPO_MAPA_DIFUSA);
            textura = QGestorRecursos.cargarTextura("humo", QGlobal.RECURSOS + "texturas/humo/smoke_atlas_1.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QMaterial crearMaterial() {
        QMaterialBas material = null;
        try {

            material = new QMaterialBas();
            QProcesadorAtlasSecuencial proc = new QProcesadorAtlasSecuencial(textura, 4, 4, 100);
            material.setMapaDifusa(proc);
            material.setColorTransparente(QColor.BLACK);
            material.setTransparencia(true);
            material.setTransAlfa(0.90f);// el objeto tiene una transparencia
        } catch (Exception e) {
            e.printStackTrace();
        }
        return material;
    }

    public QEmisorHumo(AABB ambito, float tiempoVida, int maximoParticulas, int velocidadEmision) {
        super(ambito, tiempoVida, maximoParticulas, velocidadEmision, null);
        cargarTextura();
    }

    private void agregarParticulas() {

        particulasNuevas.clear();

//        QLuz luz = null;
        if (actuales < maximoParticulas) {

            for (int i = 1; i < velocidadEmision; i++) {
                actuales++;

                QParticula nueva = new QParticula();

                QEntidad particula = new QEntidad("humo");
                particula.setBillboard(true);

                QMaterialUtil.aplicarMaterial(geometria, crearMaterial());
                particula.agregarComponente(geometria);

//                QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.DINAMICO);
//                rigido.setFormaColision(new QColisionCaja(0.15f, 0.15f, 0.01f));
//
//                // para que funcione con el motor jbullet
////                rigido.setMasa(0.00000000000000000001f, QVector3.zero.clone());
//                rigido.setMasa(0.00000000000000000001f, QVector3.unitario_y.clone());
////                rigido.agregarFuerzas(new QVector3(rnd.nextFloat() * 0.5f - 0.5f, rnd.nextFloat() * 0.5f - 0.5f, rnd.nextFloat() * 0.5f - 0.5f));
//                particula.agregarComponente(rigido);
                nueva.setTiempoVida(2 * tiempoVida / 3 + (rnd.nextFloat() * tiempoVida / 3));
                nueva.iniciarVida();

                //ubicacion inicial de la particula
                particula.mover(
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().x + rnd.nextFloat() * (ambito.aabMaximo.ubicacion.x - ambito.aabMinimo.ubicacion.x) + ambito.aabMinimo.ubicacion.x,
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().y + ambito.aabMinimo.ubicacion.y,
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().z + rnd.nextFloat() * (ambito.aabMaximo.ubicacion.z - ambito.aabMinimo.ubicacion.z) + ambito.aabMinimo.ubicacion.z
                );
//                particula.rotar(0, (float) (rnd.nextFloat() * Math.toRadians(180)), 0);

                nueva.objeto = particula;
                this.particulasNuevas.add(nueva);
            }
            this.particulas.addAll(particulasNuevas);
        }
    }

    private void eliminarParticulas() {
        particulasEliminadas.clear();
        for (QParticula particula : this.particulas) {

            //si ya paso su tiempo de vida o ya esta fuera del ambito en la altura
            if ((System.currentTimeMillis() - particula.getTiempoCreacion()) > particula.getTiempoVida()
                    || (particula.objeto.getTransformacion().getTraslacion().y > ambito.aabMaximo.ubicacion.y)) {
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
        float miniMov = -0.0000009f;
        float maxMov = 0.0000009f;
        QVector3 velocidad = new QVector3(0, 0.005f, 0);
        for (QParticula particula : this.particulas) {

            float delta = deltaTime / 1000.0f;
            float dx = velocidad.x * delta;
            float dy = velocidad.y * delta;
            float dz = velocidad.z * delta;
            QVector3 pos = particula.objeto.getTransformacion().getTraslacion();

            particula.objeto.mover(pos.x + dx, pos.y + dy, pos.z + dz);

//            particula.objeto.
//            QObjetoRigido rigido = QUtilComponentes.getFisicoRigido(particula.objeto);
//            rigido.agregarFuerzas(new QVector3(
//                    rnd.nextFloat() * (maxMov - miniMov) + miniMov,
//                    0.00009f, //siempre hacia arriba
//                    rnd.nextFloat() * (maxMov - miniMov) + miniMov
//            ));
            //modifico su tamaño para que vaya disminuyendo con el tiempo
            float d = 1.0f - (System.currentTimeMillis() - particula.getTiempoCreacion()) / particula.getTiempoVida();
            particula.objeto.escalar(d);

            //modifico transparencia
            for (QComponente componente : particula.objeto.getComponentes()) {
                if (componente instanceof QGeometria) {
                    ((QMaterialBas) ((QGeometria) componente).listaPrimitivas[0].material).setTransAlfa(d - 0.5f);//para que nunca sea 100 visible, se agrega mas transparencia al ser humo
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
        modificarParticulas(deltaTime);
//        System.out.println("particulas actuales =" + actuales);
    }

    @Override
    public void destruir() {
        textura = null;
    }
}
