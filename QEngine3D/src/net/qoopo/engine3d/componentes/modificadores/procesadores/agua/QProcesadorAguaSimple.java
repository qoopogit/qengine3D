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
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.engines.render.QClipPane;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.QRender;

/**
 * Procesador para simular agua modificando la textura de un Plano. No usa
 * refracción en su lugar usa la trasnparencia del material configurado
 * previamente
 *
 * @author alberto
 */
public class QProcesadorAguaSimple extends QProcesador {

    transient private QMotorRender render;

    private QProcesadorSimple textSalida;//en este caso será la misma de la reflexión porq no hay mezcla con otras texturas
    private QTextura textReflexion;
    private QTextura textNormal;
    //sera usada para simular movimiento de agua con el tiempo agregando un offset a las texturas
    private long time;
    private long tiempoPasado;
    private float factorX;
    private float factorY;
    private float velocidadAgua = 0.03f;

    public QProcesadorAguaSimple() {

    }

    public QProcesadorAguaSimple(QTextura mapaNormales, QEscena universo, int ancho, int alto) {
        construir(mapaNormales, universo, ancho, alto);
    }

    public void construir(QTextura mapaNormales, QEscena universo, int ancho, int alto) {

        this.textSalida = new QProcesadorSimple(new QTextura());
        this.textReflexion = new QTextura();
        this.textNormal = mapaNormales;
        try {
            render = new QRender(universo, "Reflexion", null, ancho, ancho);
            render.setEfectosPostProceso(null);
            render.setTexturaSalida(textReflexion);

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
            //a la textura difusa que sera el reflejo el invierto las coordenadas Y
//            textReflexion.setSignoY(-1);
            textSalida.getTextura().setSignoY(-1);
            render.setPanelClip(new QClipPane(QVector3.unitario_y.clone(), 0));//la normal es hacia arriba
//            render.setPanelClip(new QClipPane(QVector3.unitario_y.clone().multiply(-1.0f), 0));//la normal es hacia arriba
//            render.getPanelClip().setNormal(QVector3.unitario_y);//la normal es hacia arriba
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

        // REFLEXION -------------------------------------------------------------------
        //El proceso de reflexion es parecido al de los espejos
        // el proceo consiste en el siguiente
        //debo colocar la camara en la posicion contraria a la que viene y en direccion contraria
        //la posicion sera igual a restar un vector a la posicion
        //este vector es el resultado de restar la camara menos la posicion de este espejo, el centro dle espejo
        try {
            float distancia = 2 * (camara.getTransformacion().getTraslacion().y - entidad.getTransformacion().getTraslacion().y);
            QVector3 nuevaPos = camara.getTransformacion().getTraslacion().clone();
            nuevaPos.y -= distancia;
//            nuevaPos.y = -distancia;
            //este metodo tiene u problema la camara del render esta mirando al centro de la forma y la camara no necesariamente
            //render.getCamara().lookAtPosicionObjetivo(nuevaPos, entidad.transformacion.getTraslacion().clone(), camara.getArriba().clone());
            //invetir angulo Pitch
            render.getCamara().setTransformacion(camara.getTransformacion().clone());
            render.getCamara().getTransformacion().getRotacion().actualizarAngulos();
            render.getCamara().getTransformacion().getRotacion().getAngulos().setAnguloX(render.getCamara().getTransformacion().getRotacion().getAngulos().getAnguloX() * -1);
            render.getCamara().getTransformacion().getRotacion().actualizarCuaternion();
            render.getCamara().getTransformacion().trasladar(nuevaPos);
            render.getPanelClip().setDistancia(entidad.getTransformacion().getTraslacion().y);
//            render.getPanelClip().setDistancia(entidad.transformacion.getTraslacion().y + 0.02f);
//            render.getPanelClip().setDistancia(entidad.transformacion.getTraslacion().y - 0.2f);

//            render.getPanelClip().setNormal(entidad.getDireccion().clone());
//            render.getPanelClip().setNormal(entidad.getArriba().clone());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // REFRACCION -------------------------------------------------------------------
        //MOVIMIENTO 
        tiempoPasado = System.currentTimeMillis() - time;
        factorX = tiempoPasado * velocidadAgua;
        factorY = tiempoPasado * velocidadAgua;
        factorX %= 1;
        factorY %= 1;
        textNormal.setOffsetX(factorX);
        textNormal.setOffsetY(factorY);
//        this.textDifusa.setOffsetX(factorX);

        time = System.currentTimeMillis();

        render.update();
        //actualiza la textura de la salida con la información de la reflexión
        textSalida.getTextura().cargarTextura(textReflexion.getImagen());
//        textSalida = textReflexion;
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

    public QTextura getTextNormal() {
        return textNormal;
    }

    public void setTextNormal(QTextura textNormal) {
        this.textNormal = textNormal;
    }

    public QProcesadorSimple getTextSalida() {
        return textSalida;
    }

    public void setTextSalida(QProcesadorSimple textSalida) {
        this.textSalida = textSalida;
    }

}
