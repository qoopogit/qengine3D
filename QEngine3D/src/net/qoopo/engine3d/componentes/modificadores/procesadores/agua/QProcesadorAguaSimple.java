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
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorMixAgua;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QClipPane;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.QRender;

/**
 * Procesador para simular agua modificando la textura de un Plano.
 *
 *
 * @author alberto
 */
public class QProcesadorAguaSimple extends QProcesador {

    private final static float VELOCIDAD_AGUA = 0.05f;
    private final static float FUERZA_OLA = 0.005f;
    private final static int MUESTRAS_TEXTURAS = 2;
    transient private QMotorRender render;
    private QProcesadorMixAgua textSalida = null;//la mezcla de la textura de reflexion y la textur de refracción
    private QFrameBuffer frameReflexion;
    private QFrameBuffer frameRefraccion;
    private QTextura textReflexion;
    private QTextura textRefraccion;
    private QTextura textNormal = null;
    private QTextura dudvMaps = null;
    //sera usada para simular movimiento de agua con el tiempo agregando un offset a las texturas
    private long time;
    private long tiempoPasado;
    private float factorX;
    private float factorY;
    private final QVector3 arriba = QVector3.unitario_y.clone();
    private final QVector3 abajo = QVector3.unitario_y.clone().multiply(-1.0f);

    public QProcesadorAguaSimple() {

    }

    public QProcesadorAguaSimple(QEscena universo, int ancho, int alto) {
        construir(universo, ancho, alto);
    }

    public void construir(QEscena universo, int ancho, int alto) {
        this.textReflexion = new QTextura();
        this.textReflexion.setSignoY(-1);
        this.textRefraccion = new QTextura();

        try {
            textNormal = QGestorRecursos.cargarTextura("textNormal", QGlobal.RECURSOS + "texturas/agua/matchingNormalMap.png");
            textNormal.setMuestrasU(MUESTRAS_TEXTURAS);
            textNormal.setMuestrasV(MUESTRAS_TEXTURAS);
        } catch (Exception e) {
        }
        try {
            dudvMaps = QGestorRecursos.cargarTextura("dudvMaps", QGlobal.RECURSOS + "texturas/agua/waterDUDV.png");
            dudvMaps.setMuestrasU(MUESTRAS_TEXTURAS);
            dudvMaps.setMuestrasV(MUESTRAS_TEXTURAS);
        } catch (Exception e) {
        }
        try {
            frameReflexion = new QFrameBuffer(ancho, alto, textReflexion);
            frameRefraccion = new QFrameBuffer(ancho, alto, textRefraccion);
            render = new QRender(universo, "Agua", null, ancho, ancho);
            render.setEfectosPostProceso(null);
            render.renderReal = false;
            render.opciones.setForzarResolucion(true);
            render.opciones.setNormalMapping(false);
            render.opciones.setSombras(false);
            render.opciones.setVerCarasTraseras(false);
            render.setMostrarEstadisticas(false);
            render.setCamara(new QCamara("CamAgua"));
            render.resize();
            render.setRenderReal(false);
            render.opciones.setVerCarasTraseras(true);
            time = System.currentTimeMillis();
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
    public void procesar(QMotorRender mainRender, QEscena universo) {
        // evita la ejecucion si no esta activo los materiales

        if (!mainRender.opciones.isMaterial()) {
            return;
        }
        if (mainRender.getFrameBuffer() == null) {
            return;
        }

        entidad.setRenderizar(false);//evita renderizar el agua
        //actualizo la resolución de acuerdo a la cámara
        if (render.opciones.getAncho() != mainRender.getFrameBuffer().getAncho() && render.opciones.getAlto() != mainRender.getFrameBuffer().getAlto()) {
            render.opciones.setAncho(mainRender.getFrameBuffer().getAncho());
            render.opciones.setAlto(mainRender.getFrameBuffer().getAlto());
            frameReflexion = new QFrameBuffer(render.opciones.getAncho(), render.opciones.getAlto(), textReflexion);
            frameRefraccion = new QFrameBuffer(render.opciones.getAncho(), render.opciones.getAlto(), textRefraccion);
            render.resize();
        }

        render.setEscena(universo);
        render.setNombre(entidad.getNombre());
        render.getCamara().setNombre(entidad.getNombre());
        try {

            // REFRACCION -------------------------------------------------------------------
            render.getCamara().setTransformacion(mainRender.getCamara().getTransformacion().clone());// la misma posicion de la cámara actual
            render.getCamara().getTransformacion().getRotacion().actualizarAngulos();
            render.setFrameBuffer(frameRefraccion);
            render.getPanelClip().setNormal(abajo);
            render.getPanelClip().setDistancia(entidad.getTransformacion().getTraslacion().y - 1.0f);
            render.update();
            render.getFrameBuffer().actualizarTextura();
            // REFLEXION -------------------------------------------------------------------
            //movemos la cámara debajo de la superficie del agua a una distancia 2 veces de la actual altura
            render.setFrameBuffer(frameReflexion);
            render.getPanelClip().setNormal(arriba);
            render.getPanelClip().setDistancia(entidad.getTransformacion().getTraslacion().y + 0.5f);

            float distancia = 2 * (mainRender.getCamara().getTransformacion().getTraslacion().y - entidad.getTransformacion().getTraslacion().y);
            QVector3 nuevaPos = mainRender.getCamara().getTransformacion().getTraslacion().clone();
            nuevaPos.y -= distancia;

            //invertir angulo Pitch
            render.getCamara().setTransformacion(mainRender.getCamara().getTransformacion().clone());
            render.getCamara().getTransformacion().getRotacion().actualizarAngulos();
            render.getCamara().getTransformacion().getRotacion().getAngulos().setAnguloX(render.getCamara().getTransformacion().getRotacion().getAngulos().getAnguloX() * -1);
            render.getCamara().getTransformacion().getRotacion().actualizarCuaternion();
            render.getCamara().getTransformacion().trasladar(nuevaPos);
            render.update();
            render.getFrameBuffer().actualizarTextura();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //MOVIMIENTO de las normales
        tiempoPasado = System.currentTimeMillis() - time;
        factorX = tiempoPasado * VELOCIDAD_AGUA;
        factorY = tiempoPasado * VELOCIDAD_AGUA;
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
        textSalida.setFuerzaOla(FUERZA_OLA);
        textSalida.setFactorTiempo(factorX);
//        textSalida.setRazon(0.5f); //por iguales
//        textSalida.setRazon(0f); //todo reflexion
//        textSalida.setRazon(1f);//todo refraccion
//        textSalida.setRazon(0.9f);
        //efecto fresnel

        //este calculo se deberia hacer para cada pixel de la superficie, pues el angulo es diferente para cada punto de la superficie del agua
        QVector3 vision = mainRender.getCamara().getTransformacion().getTraslacion().clone().subtract(entidad.getTransformacion().getTraslacion().clone());
        //el factor fresnel representa que tanta refraccion se aplica
        float factorFresnel = arriba.dot(vision.normalize());
        factorFresnel = QMath.pow(factorFresnel, 0.5f);//para que sea menos reflectante (si se una un numero positivo en el exponente seria mas reflectivo)
        textSalida.setRazon(factorFresnel);
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
