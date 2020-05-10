/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.fisica.interno;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.fisica.colisiones.QParColision;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.engines.fisica.QMotorFisica;
import net.qoopo.engine3d.componentes.fisica.colisiones.procesador.QProcesadorColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.procesador.impl.QProcesadorImplF4;

/**
 * Motor de simulacion física. Se encarga de aplicar las fuerzas a los objetos
 * del universo para modificar sus propiedades Motor Interno
 *
 * @author alberto
 */
public class QFisica extends QMotorFisica {

    private final QProcesadorColision procesadorColision;
    private DecimalFormat df = new DecimalFormat("0.00");
    private final List<QParColision> listaParesColision = new ArrayList<>();

    public QFisica(QEscena universo) {
        super(universo);
//        procesadorColision = new Fuerzas1();
//        procesadorColision = new Fuerzas2();
//        procesadorColision = new QProcesadorImplF3();
        procesadorColision = new QProcesadorImplF4();
        tiempoPrevio = System.currentTimeMillis();
    }

     @Override
    public void iniciar() {

    }
    
    @Override
    public long update() {
        try {
            //------------------ DINAMICA
            //aplicar peso (agrega a la fuerza total la fuerza de la peso)
            aplicarGravedad();
            //predecir trans --no se que es estaba en el pipeline
            //--------------------------- DETECCION DE COLISIONES
            verificarColisiones();
            procesarColisiones();
            //------------------------------DINAMICA
            //definir restricciones
            //resolver restricciones (entrada: puntos de contacto, masa inercia, restricciones articuladas. salida ? )
            //integrar posicion
            //la velocidad es en segundos 
            actualizaMundo(getDelta() / 1000);
            limpiarFuerzas();

        } catch (Exception e) {
            System.out.println("MF. Error=" + e.getMessage());
        }
//        System.out.println("MF-->" + df.format(1000.0 / (float) deltaTime) + " FPS");
        tiempoPrevio = System.currentTimeMillis();
        return tiempoPrevio;
    }

    private void aplicarGravedad() {
        try {
            QObjetoRigido actual;
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    for (QComponente componente : objeto.getComponentes()) {
                        if (componente instanceof QObjetoRigido) {
                            actual = (QObjetoRigido) componente;
                            //metodo 1 (usando el peso  que es el resultado de la masa x la gravedad)
                            actual.calcularPeso(universo.gravedad);// como este no varia se deberia hacer al momento de la creacion del objeto
                            actual.aplicarGravedad();
                            //metodo 2 Caida libre. (es simplemente aplicar la fuerza de gravedad sin tomar en cuenta su peso)
//                actual.agregarFuerzas(universo.gravedad);
                            actual = null;
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private void limpiarFuerzas() {
        try {
            QObjetoRigido actual;
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    for (QComponente componente : objeto.getComponentes()) {
                        if (componente instanceof QObjetoRigido) {
                            actual = (QObjetoRigido) componente;
                            actual.limpiarFuezas();
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void actualizaMundo(float deltaTime) {
        try {
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    for (QComponente componente : objeto.getComponentes()) {
                        if (componente instanceof QObjetoRigido) {
                            ((QObjetoRigido) componente).actualizarMovimiento(deltaTime);
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    //calcular AABB's (entrada formas de colision, salida formas AABB)
    //detectar pares suprpuestos (etrada formas AABB, salida pares superpuestos)
    //detectar puntos de contacto (entrada pares superpuestos, salida puntos de contacto)
    //calculamos AABB's y marcamos los objetos sin colision, solo cuando no tienen una forma isntancianda incialmente
    private void actualizarABBS() {
        try {
            for (QEntidad objeto : universo.getListaEntidades()) {
                if (objeto.isRenderizar()) {
                    //primero busco el componente fisico
                    for (QComponente componenteFisico : objeto.getComponentes()) {
                        if (componenteFisico instanceof QObjetoRigido) {
                            ((QObjetoRigido) componenteFisico).tieneColision = false;
                            if (((QObjetoRigido) componenteFisico).formaColision == null) {
                                //ahora busco el componente geometria, es neesario para calcular los cuadros de colision en caso que no se haya definido una forma previamente
                                for (QComponente componente : objeto.getComponentes()) {
                                    if (componente instanceof QGeometria) {
                                        ((QObjetoRigido) componenteFisico).crearContenedorColision((QGeometria) componente, objeto.getTransformacion());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verificarColisiones() {
        try {
            actualizarABBS();
            //esto es para no volver a verificar el mismo par y q se anulen las fuerzas
            Map<String, Boolean> parColision = new HashMap<>();
            listaParesColision.clear();
            //detectamos colision con AABB y armamos pares
            QObjetoRigido componenteFisico1;
            QObjetoRigido componenteFisico2;
            List<QEntidad> listaEntidades = new ArrayList<>();
            listaEntidades.addAll(universo.getListaEntidades());
            for (QEntidad ob1 : listaEntidades) {
                if (ob1.isRenderizar()) {
                    for (QComponente componente1 : ob1.getComponentes()) {
                        if (componente1 instanceof QObjetoRigido) {
                            componenteFisico1 = (QObjetoRigido) componente1;
                            for (QEntidad objeto2 : listaEntidades) {
                                if (objeto2.isRenderizar()) {
                                    if (!ob1.equals(objeto2)) {
                                        for (QComponente componente2 : objeto2.getComponentes()) {
                                            if (componente2 instanceof QObjetoRigido) {
                                                componenteFisico2 = (QObjetoRigido) componente2;
                                                componenteFisico1.verificarColision(componenteFisico2);
                                                if (componenteFisico1.tieneColision) {
                                                    if (!parColision.containsKey(ob1.getNombre() + "x" + objeto2.getNombre())) {
                                                        listaParesColision.add(new QParColision(componenteFisico1, componenteFisico2));
                                                        //agrego al para para ya no procesar el mismo par de objetos
                                                        parColision.put(ob1.getNombre() + "x" + objeto2.getNombre(), true);
                                                        parColision.put(objeto2.getNombre() + "x" + ob1.getNombre(), true);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Procesa las colisiones y agrega las fuerzas a los objetos colisionados
     */
    private void procesarColisiones() {
        try {
            for (QParColision par : listaParesColision) {
                procesadorColision.procesarColision((QObjetoRigido) par.getOb1(), (QObjetoRigido) par.getOb2());
            }
        } catch (Exception e) {
        }
    }

}
