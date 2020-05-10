/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.procesadores.agua;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.modificadores.procesadores.QProcesador;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorMixAgua;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QClipPane;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.QRender;

/**
 * Procesador para simular agua modificando la textura de un Plano.
 *
 *
 * @author alberto
 */
public class QProcesadorAguaSimple2 extends QProcesador {

    transient private QMotorRender render;

    private QProcesadorMixAgua textSalida = null;//la mezcla de la textura de reflexion y la textur de refracción

    private QTextura textReflexion;
    private QTextura textRefraccion;
    private QTextura textNormal;
    private QTextura dudvMaps;

    //sera usada para simular movimiento de agua con el tiempo agregando un offset a las texturas
    private long time;
    private long tiempoPasado;
    private float factorX;
    private float factorY;
    private float velocidadAgua = 0.03f;
    private float fuerzaOla = 0.02f;
//    private float fuerzaOla = 1f;

    private final QVector3 arriba = QVector3.unitario_y.clone();
    private final QVector3 abajo = QVector3.unitario_y.clone().multiply(-1.0f);

    public QProcesadorAguaSimple2() {

    }

    public QProcesadorAguaSimple2(QTextura mapaNormales, QEscena universo, int ancho, int alto) {
        construir(mapaNormales, universo, ancho, alto);
    }

    public void construir(QTextura mapaNormales, QEscena universo, int ancho, int alto) {
        this.textReflexion = new QTextura();
        textReflexion.setSignoY(-1);
        this.textRefraccion = new QTextura();
        this.textNormal = mapaNormales;

        //material.getMapaDifusa().setModo(QProcesadorTextura.MODO_COMBINAR);//para que combine con el color azul del material
        try {
            dudvMaps = QGestorRecursos.cargarTextura("texnormal", QGlobal.RECURSOS + "texturas/agua/waterDUDV.png");
            dudvMaps.setMuestrasU(6);
            dudvMaps.setMuestrasV(6);
        } catch (Exception e) {
        }

        try {
            render = new QRender(universo, "Reflexion", null, ancho, ancho);
            render.setEfectosPostProceso(null);

            render.opciones.forzarResolucion = true;
            render.opciones.normalMapping = false;
            render.opciones.sombras = false;
            render.opciones.verCarasTraseras = false;
            render.setMostrarEstadisticas(false);
            render.setCamara(new QCamara("CamAgua"));
            render.resize();
            render.setRenderReal(false);

            render.opciones.verCarasTraseras = true;
            time = System.currentTimeMillis();
//            //a la textura difusa que sera el reflejo le invierto las coordenadas Y
//            textSalida.setSignoY(-1);
            render.setPanelClip(new QClipPane(arriba, 0));
            textSalida = new QProcesadorMixAgua(textReflexion, textRefraccion, dudvMaps);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preProceso() {
    }

    @Override
    public void procesar(QCamara camara, QEscena universo) {

        entidad.setRenderizar(false);
        //actualizo la resolución de acuerdo a la cámara
        if (render.opciones.ancho != render.getFrameBuffer().getAncho() && render.opciones.alto != render.getFrameBuffer().getAlto()) {
            render.opciones.ancho = render.getFrameBuffer().getAncho();
            render.opciones.alto = render.getFrameBuffer().getAlto();
            render.resize();
        }

        render.setEscena(universo);
        render.getCamara().setNombre(entidad.getNombre());
        render.setNombre(entidad.getNombre());
        try {
            QCamara tmpCam = render.getCamara();
            // REFRACCION -------------------------------------------------------------------
//            render.getCamara().transformacion = camara.transformacion.clone();// la misma posicion de la cámara actual
            render.setCamara(camara);
            render.setTexturaSalida(textRefraccion);
//            render.setPanelClip(null);
            render.getPanelClip().setNormal(abajo);
            render.getPanelClip().setDistancia(entidad.getTransformacion().getTraslacion().y);
            render.update();

            // REFLEXION -------------------------------------------------------------------
            //movemos la cámara debajo de la superficie del agua a una distancia 2 veces de la actual altura
            render.setCamara(tmpCam);
            render.setTexturaSalida(textReflexion);
            render.getPanelClip().setNormal(arriba);
            render.getPanelClip().setDistancia(entidad.getTransformacion().getTraslacion().y);
//            render.setPanelClip(new QClipPane(arriba, entidad.transformacion.getTraslacion().y));

            float distancia = 2 * (camara.getTransformacion().getTraslacion().y - entidad.getTransformacion().getTraslacion().y);
            QVector3 nuevaPos = camara.getTransformacion().getTraslacion().clone();
            nuevaPos.y -= distancia;
//            nuevaPos.y = -distancia;
            //este metodo tiene u problema la camara del render esta mirando al centro de la forma y la camara no necesariamente
            //render.getCamara().lookAtPosicionObjetivo(nuevaPos, entidad.transformacion.getTraslacion().clone(), camara.getArriba().clone());
            //invertir angulo Pitch
            render.getCamara().setTransformacion(camara.getTransformacion().clone());
            render.getCamara().getTransformacion().getRotacion().actualizarAngulos();
            render.getCamara().getTransformacion().getRotacion().getAngulos().setAnguloX(render.getCamara().getTransformacion().getRotacion().getAngulos().getAnguloX() * -1);
            render.getCamara().getTransformacion().getRotacion().actualizarCuaternion();
            render.getCamara().getTransformacion().trasladar(nuevaPos);
            render.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //MOVIMIENTO de las normales
        tiempoPasado = System.currentTimeMillis() - time;
        factorX = tiempoPasado * velocidadAgua;
        factorY = tiempoPasado * velocidadAgua;
        factorX %= 1;
        factorY %= 1;
        if (textNormal != null) {
            textNormal.setOffsetX(factorX);
            textNormal.setOffsetY(factorY);
        }
        // modifica la textura de refracción para simular el efecto de refracción
//        textRefraccion.setOffsetX(factorX);
//        textRefraccion.setOffsetY(factorY);

        time = System.currentTimeMillis();

        //actualiza la textura de la salida con la información de la reflexión
        textSalida.setFuerzaRuido(fuerzaOla);
        textSalida.setFactorTiempo(factorX);
        textSalida.setRazon(0.5f); //por iguales
//        textSalida.setRazon(0f); //todo reflexion
//        textSalida.setRazon(1f);//todo refraccion
//        textSalida.setRazon(0.9f);
        //efecto fresnel

//        QVector3 vision = entidad.transformacion.getTraslacion().clone().add(camara.transformacion.getTraslacion().clone().multiply(-1f));
//        float punto = arriba.dotProduct(vision);
//        textSalida.setRazon(punto); //por iguales
//         textSalida.actualizarImagenes();
//        textSalida.procesar();
//        textSalida.cargarTextura(procesador.getTexture());
        entidad.setRenderizar(true);
    }

    @Override
    public void postProceso() {
    }

    @Override
    public void destruir() {
        if (textReflexion != null) {
            textReflexion.destruir();
            textReflexion = null;
        }
        if (textNormal != null) {
            textNormal.destruir();
            textNormal = null;
        }
    }

    public QTextura getTextReflexion() {
        return textReflexion;
    }

    public void setTextReflexion(QTextura textReflexion) {
        this.textReflexion = textReflexion;
    }

    public QTextura getTextRefraccion() {
        return textRefraccion;
    }

    public void setTextRefraccion(QTextura textRefraccion) {
        this.textRefraccion = textRefraccion;
    }

    public QTextura getTextNormal() {
        return textNormal;
    }

    public void setTextNormal(QTextura textNormal) {
        this.textNormal = textNormal;
    }

    public QProcesadorMixAgua getTextSalida() {
        return textSalida;
    }

    public void setTextSalida(QProcesadorMixAgua textSalida) {
        this.textSalida = textSalida;
    }

}
