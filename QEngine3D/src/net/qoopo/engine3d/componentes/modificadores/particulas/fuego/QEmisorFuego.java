/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.particulas.fuego;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.componentes.modificadores.particulas.QEmisorParticulas;
import net.qoopo.engine3d.componentes.modificadores.particulas.QParticula;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCaja;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlanoBillboard;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorAtlasSecuencial;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class QEmisorFuego extends QEmisorParticulas {

//    private QMaterialBas material = null;
    int maximoLuces = 3;
    private QTextura textura;
    float intencidadLuz = 0.12f;
    int actualLuz = 0;
    private final List<QLuz> luces = new ArrayList<>();
//    private QGeometria geometria = new QPlanoBillboard(0.05f, 0.05f);
    private final QGeometria geometria = new QPlanoBillboard(0.15f, 0.15f);
    private final Random rnd = new Random();
    private boolean agregarLuces = false;
//mantengo una lista de luces fijas y las voy a asignando a las nuevas particulas generadas
    //solo voy a crear las luces que corresponden al numero de particulas nuevas

    private void cargarTextura() {
        try {
            //new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS+ "texturas/fuego/fuego_esfera1.png")), QTextura.TIPO_MAPA_DIFUSA)
//                    new QTextura(ImageIO.read(new File(QGlobal.RECURSOS+ "texturas/fuego/texture_atlas.png")), QTextura.TIPO_MAPA_DIFUSA),
//            textura = new QTextura(ImageIO.read(new File(QGlobal.RECURSOS+ "texturas/fuego/fire-texture-atlas.png")), QTextura.TIPO_MAPA_DIFUSA);
            textura = QGestorRecursos.cargarTextura("texFuego", QGlobal.RECURSOS + "texturas/fuego/fire-texture-atlas_2.png");
//            textura = new QTextura(ImageIO.read(new File(QGlobal.RECURSOS+ "texturas/fuego/texture_atlas.png")), QTextura.TIPO_MAPA_DIFUSA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private QMaterialBas crearMaterial() {
        QMaterialBas material = null;
        try {

            material = new QMaterialBas();
            QProcesadorAtlasSecuencial proc = new QProcesadorAtlasSecuencial(
                    textura,
                    //                    4, 4, 50);
                    8, 8, 10);
            material.setMapaDifusa(proc);
            material.setColorTransparente(QColor.BLACK);
            material.setTransparencia(true);
//            material.setTransAlfa(0.90f);// el objeto tiene una transparencia 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return material;
    }

    public QEmisorFuego(AABB ambito, float tiempoVida, int maximoParticulas, int velocidadEmision, QVector3 direccion, boolean agregarLuces) {
        super(ambito, tiempoVida, maximoParticulas, velocidadEmision, direccion);
        this.agregarLuces = agregarLuces;
//        crearMaterial();
        cargarTextura();
    }

    private void agregarParticulas() {

        particulasNuevas.clear();

//        QLuz luz = null;
        if (actuales < maximoParticulas) {

            for (int i = 1; i < velocidadEmision; i++) {
                actuales++;

                QParticula nueva = new QParticula();

                QEntidad particula = new QEntidad("flama");
                particula.setBillboard(true);
                QMaterialUtil.aplicarMaterial(geometria, crearMaterial());
                particula.agregarComponente(geometria);

                QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.DINAMICO);
                rigido.setFormaColision(new QColisionCaja(0.15f, 0.15f, 0.01f));
                rigido.setMasa(-0.001f, QVector3.zero.clone());
                particula.agregarComponente(rigido);

                nueva.setTiempoVida(2 * tiempoVida / 3 + (rnd.nextFloat() * tiempoVida / 3));
                nueva.iniciarVida();

                //ubicacion inicial de la particula
                particula.mover(
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().x + rnd.nextFloat() * (ambito.aabMaximo.ubicacion.x - ambito.aabMinimo.ubicacion.x) + ambito.aabMinimo.ubicacion.x,
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().y + ambito.aabMinimo.ubicacion.y,
                        entidad.getMatrizTransformacion(System.currentTimeMillis()).toTranslationVector().z + rnd.nextFloat() * (ambito.aabMaximo.ubicacion.z - ambito.aabMinimo.ubicacion.z) + ambito.aabMinimo.ubicacion.z
                );
//                particula.rotar(0, (float) (rnd.nextFloat() * Math.toRadians(180)), 0);

                if (agregarLuces) {
                    if (maximoLuces > 0) {
                        if (luces.size() < maximoLuces) {
                            QLuz luz = new QLuzPuntual(intencidadLuz, QColor.YELLOW, 20, false, false);
                            particula.agregarComponente(luz);
                            luces.add(luz);
                        } else {
                            particula.agregarComponente(luces.get(actualLuz));
                            actualLuz++;
                            if (actualLuz >= maximoLuces) {
                                actualLuz = 0;
                            }
                        }
                    }
                }
                nueva.objeto = particula;
//                this.entidad.agregarHijo(particula);
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
            if ((System.currentTimeMillis() - particula.getTiempoCreacion()) > particula.getTiempoVida()
                    || (particula.objeto.getTransformacion().getTraslacion().y > ambito.aabMaximo.ubicacion.y)) {
                particulasEliminadas.add(particula);
                actuales--;
                particula.objeto.setRenderizar(false);
                particula.objeto.setEliminar(true);
                //recorro los hijos para apagar las luces de los eliminados
//                for (QEntidad hijo : copo.objeto.hijos) {
//                    if (hijo instanceof QLuz) {
//                        ((QLuz) hijo).setEnable(false);
//                    }
//                }
            }
        }
        if (actuales < 0) {
            actuales = 0;
        }
        this.particulas.removeAll(particulasEliminadas);
    }

    private void modificarParticulas() {
        //la modificacion de las particulas de la nieva se basa en un zigzagueo hacia abajo
        float miniMov = -0.009f;
        float maxMov = 0.009f;
        float dx = 1;
        float dz = 1;
        for (QParticula particula : this.particulas) {
            //se agrega un impulso
            for (QComponente componente : particula.objeto.getComponentes()) {
                if (componente instanceof QObjetoRigido) {
                    //el deltax y delta z es para acercarlo al centro, multiplico por -1 para ir en el sentido contrario
                    dx = particula.objeto.getTransformacion().getTraslacion().x / Math.abs(particula.objeto.getTransformacion().getTraslacion().x) * -1;
                    dz = particula.objeto.getTransformacion().getTraslacion().z / Math.abs(particula.objeto.getTransformacion().getTraslacion().z) * -1;

                    ((QObjetoRigido) componente).agregarFuerzas(new QVector3(
                            dx * rnd.nextFloat() * (maxMov - miniMov) + miniMov,
                            0,
                            dz * rnd.nextFloat() * (maxMov - miniMov) + miniMov));
                }
            }
            //modifico su tamaño para que vaya disminuyendo con el tiempo
            float d = 1.0f - (System.currentTimeMillis() - particula.getTiempoCreacion()) / particula.getTiempoVida();
            particula.objeto.escalar(d);
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
        super.destruir();
        luces.clear();
    }
}
