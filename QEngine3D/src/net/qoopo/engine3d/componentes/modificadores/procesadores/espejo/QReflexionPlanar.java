/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.modificadores.procesadores.espejo;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.modificadores.procesadores.QProcesador;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.QRender;

/**
 * Genera una reflexion planar.Útil en espejos, pisos etc.
 *
 * @author alberto
 */
public class QReflexionPlanar extends QProcesador {

//    private QTextura texturaEspejo;
    //es el renderizador que tomara la imagen para reflejar
    private QMotorRender render;

    private int metodo = 1;

    public QReflexionPlanar(QTextura textura, QEscena universo, int ancho, int alto) {
//        this.texturaEspejo = textura;
        try {

            render = new QRender(universo, "Reflexión", null, ancho, ancho);
            render.setTexturaSalida(textura);
            render.setEfectosPostProceso(null);
            render.setCamara(new QCamara("espejo"));
            render.opciones.forzarResolucion = true;
            render.opciones.normalMapping = false;
            render.opciones.sombras = false;
            render.opciones.verCarasTraseras = false;
            render.setMostrarEstadisticas(false);
            render.resize();
            render.setRenderReal(false);
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

        render.getCamara().setNombre(entidad.getNombre());
        render.setNombre(entidad.getNombre());

        // el proceo consiste en el siguiente
        //debo colocar la camara en la posicion contraria a la que viene y en direcicon contraria
        //la posicion sera igual a restar un vector a la posicion
        //este vector es el resultado de restar la camara menos la posicion de este espejo, el centro dle espejo
        if (metodo == 1) {
            //metoo uno con la camara fija en el espejo
            try {
                render.getCamara().lookAt(entidad.getTransformacion().getTraslacion(), entidad.getDireccion(), entidad.getArriba().clone());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (metodo == 2) {
            //metodo 2 
            //el vector del rayo de vision
            QVector3 vision = new QVector3();
            vision.setXYZ(entidad.getTransformacion().getTraslacion().x - camara.getTransformacion().getTraslacion().x,
                    entidad.getTransformacion().getTraslacion().y - camara.getTransformacion().getTraslacion().y,
                    entidad.getTransformacion().getTraslacion().z - camara.getTransformacion().getTraslacion().z);
            //entidad.transformacion.getTraslacion().clone().add(camara.transformacion.getTraslacion().clone().multiply(-1));
            //http://di002.edv.uniovi.es/~rr/Tema5.pdf
            //        QVector3 rayoReflejado = vision.add(
            //                entidad.getDireccion().clone().multiply(vision.clone().dotProduct(entidad.getDireccion().clone())).multiply(-2)
            //        );
            QVector3 rayoReflejado = entidad.getDireccion().clone().multiply(entidad.getDireccion().clone().dotProduct(vision)).multiply(2).add(vision.clone().multiply(-1));

            try {
                //hasta ahorita tengo el rayo reflejado, pero necesito que este en la posicion contraria
                QVector3 nuevaPos = entidad.getTransformacion().getTraslacion().clone().add(rayoReflejado.multiply(0.25f));
                //ya tenemos en la posicion correcta pero hay un problema, esta renderizando a su propia geometia y colapsa la vision (ya se corrigio con la bandera de renderizar)

                render.getCamara().lookAtPosicionObjetivo(nuevaPos, entidad.getTransformacion().getTraslacion().clone(), entidad.getArriba().clone());

//                render.setPanelClip(new ClipPane(entidad.getDireccion(), -entidad.transformacion.getTraslacion().length()));
                //plano de recorte para no ver el espejo tapando
//                render.getCamara().frustrumCerca = rayoReflejado.length();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        render.update();
        entidad.setRenderizar(true);
//        QVector nuevaPosicion = 
        //por ahora voy a poner una camara fija en el espejo y la voy a getCoordenadasPantalla en el espejo
    }

    @Override
    public void postProceso() {
    }

    @Override
    public void destruir() {
    }

}
