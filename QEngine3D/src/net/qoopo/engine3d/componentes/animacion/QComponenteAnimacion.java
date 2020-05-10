/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.animacion;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.engines.animacion.QAnimacionFrame;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.animacion.QParAnimacion;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;

/**
 * Componente de Animación Controla la animacion de cada entidad. Contiene un
 * conjunto de frames llamadas KeyFrames, los cuales marcan las trasnformaciones
 * en una determinada marca de tiempo
 *
 * @author alberto
 */
public class QComponenteAnimacion extends QComponente {

    private String nombre = "Animación";

    /**
     * Duracion de la animacion
     */
    private float duracion;
    /**
     * El tiempo actual de la animacion
     */
    private float tiempo = 0;

    /**
     * Bandera que indica que si la animacion debe repetirse
     */
    private boolean loop = true;

    /**
     * Lista de los frames/keyframes de la animacion
     */
    private List<QAnimacionFrame> listaFrames = new ArrayList<>();

    /**
     * El frame actual con las transformaciones a aplicar
     */
    private QAnimacionFrame frameActual = new QAnimacionFrame();

    public QComponenteAnimacion() {
    }

    public void reiniciar() {
        tiempo = 0;
    }

    /**
     * Crea una animacion que contiene las contrasformaciones originales de los
     * huesos, para mostrar la posicion POSE
     *
     * @param esqueleto
     * @return
     */
    public static QComponenteAnimacion crearAnimacionPose(QEsqueleto esqueleto) {
        QComponenteAnimacion animacion = new QComponenteAnimacion();
        animacion.setNombre("Pose");
        animacion.setLoop(true);
        QAnimacionFrame qFrame = new QAnimacionFrame(0.00f);
        for (QHueso hueso : esqueleto.getHuesos()) {
            QMatriz4 mat4 = hueso.getTransformacion().toTransformMatriz();
            QTransformacion transformacion = new QTransformacion();
            transformacion.desdeMatrix(mat4);
            qFrame.agregarPar(new QParAnimacion(hueso, transformacion));
        }
        animacion.agregarAnimacionFrame(qFrame);
        return animacion;
    }

    public QComponenteAnimacion(float duracion) {
        this.duracion = duracion;
    }

    /**
     * Agrea un frame a la lista de frames
     *
     * @param animacion
     */
    public void agregarAnimacionFrame(QAnimacionFrame animacion) {
        listaFrames.add(animacion);
    }

    public void eliminarAnimacionFram(QAnimacionFrame animacion) {
        listaFrames.remove(animacion);
    }

    public List<QAnimacionFrame> getListaFrames() {
        return listaFrames;
    }

    public void setListaFrames(List<QAnimacionFrame> listaFrames) {
        this.listaFrames = listaFrames;
    }

    /**
     * Realiza los calculos para cambiar de frame
     *
     * @param marcaTiempo
     */
    public void updateAnim(float marcaTiempo) {
//        float tiempoPrevio = tiempo;
        this.tiempo = marcaTiempo;

        //calcula el tiempo transcurrido
        if (tiempo > duracion) {
            if (loop) {
                tiempo %= duracion;
            } else {
//                tiempo = tiempoPrevio;
                tiempo = listaFrames.get(listaFrames.size() - 1).getMarcaTiempo();
            }
        }

        calcularFrameActual();
        procesarFrame(getFrameActual());

//        //si no es infinito vamos al siguiente frame
//        // tambien se verifica si se debe ya pasar al siguiente frame, es decir si el tiempo de vida de este frame ya expiro
//        switch (tipo) {
//            case DURACION_FRAMES:
//                if (getFrameActual() != null
//                        && !getFrameActual().isInfinito()
//                        && getFrameActual().pasarSiguienteFrame()) {
//                    if (frameActualPosicion < listaFrames.size() - 1) {
//                        frameActualPosicion++;
//                    } else {
//                        //ahora pregunto si se debe repetir la animacion o si ya es lo ultimo
////                        if (getFrameActual().isRegresarAlInicio()) {
//                        if (loop) {
//                            frameActualPosicion = 0;
//                        } else {
//                            frameActualPosicion = -1;//ya no se debe dar mas frames
//                        }
//                    }
//                }
//                break;
//            case DURACION_TIEMPO:
//                calcularFrameActual();
//                break;
//        }
    }

    /**
     *
     *
     * @param deltaSegundos
     */
    public void incrementar(float deltaSegundos) {
        tiempo += deltaSegundos;
        updateAnim(tiempo);
    }

    /**
     * Realiza la aplicacion de la transformacion que corresponde al frame
     * actual de la animacion
     *
     * @param frame
     */
    public void procesarFrame(QAnimacionFrame frame) {
        if (frame == null) {
            return;
        }

        //ahora recorremos los pares para cambiar la posicion y rotacion de cada uno
        for (QParAnimacion par : frame.getParesModificarAnimacion()) {
            par.getEntidad().setTransformacion(par.getTransformacion());
        }
    }

    /**
     * Calcula el frame actual interpolando las posiciones de los keyFrames
     */
    public void calcularFrameActual() {
        QAnimacionFrame[] previoYsiguiente = obtenerPrevioySiguiente();
        if (QGlobal.ANIMACION_INTERPOLAR) {
            float progresion = calcularProgresion(previoYsiguiente[0], previoYsiguiente[1]);
            frameActual = interpolar(previoYsiguiente[0], previoYsiguiente[1], progresion);
        } else {
            frameActual = previoYsiguiente[0];
        }

//        switch (QGlobal.TEST_INTER) {
//            case 1:
//                frameActual = previoYsiguiente[0];
//                break;
//            case 2:
//                float progresion = calcularProgresion(previoYsiguiente[0], previoYsiguiente[1]);
//                frameActual = interpolar(previoYsiguiente[0], previoYsiguiente[1], progresion);
//                break;
//            case 3:
//                frameActual = previoYsiguiente[1];
//                break;
//        }
    }

    /**
     * Calcula un Frame interpolando dos frames
     *
     * @param previo
     * @param siguiente
     * @param progresion
     * @return
     */
    private QAnimacionFrame interpolar(QAnimacionFrame previo, QAnimacionFrame siguiente, float progresion) {
        QAnimacionFrame nuevo = new QAnimacionFrame(tiempo);
        for (int i = 0; i < previo.getParesModificarAnimacion().size(); i++) {
            if (siguiente.getParesModificarAnimacion().size() > i) {
                // si tiene el mismo tama;o (deberia ser asi)
                QEntidad hueso = previo.getParesModificarAnimacion().get(i).getEntidad();
                QTransformacion origen = previo.getParesModificarAnimacion().get(i).getTransformacion();
                QTransformacion destino = siguiente.getParesModificarAnimacion().get(i).getTransformacion();
                if (siguiente.getParesModificarAnimacion().get(i).getEntidad().getNombre().equals(hueso.getNombre())) {
                    nuevo.agregarPar(new QParAnimacion(hueso, QTransformacion.interpolar(origen, destino, progresion)));
                } else {
                    System.out.println("ERROR AL INTERPOLAR, LAS ENTIDADES NO ESTAN EN LAS MISMAS POSICIONES EN LOS DOS KEYFRAMES");
                }
            } else {
                //  si tienen diferetes tamanios (no deberia ser asi, a menos que el sigiente frame no se mueva el hueso y no se agrego a la animacion)
                System.out.println("ERROR LA INTERPOLAR, LOS KEYFRAMES NO TIENEN LOS MISMOS PARES ");
            }
        }
        return nuevo;
    }

    /**
     * Calcula la progresion que se usara para la interpolacion de las
     * transformaciones
     *
     * @param previo
     * @param siguiente
     * @return
     */
    private float calcularProgresion(QAnimacionFrame previo, QAnimacionFrame siguiente) {
        float totalTime = Math.abs(siguiente.getMarcaTiempo() - previo.getMarcaTiempo());
        float currentTime = tiempo - previo.getMarcaTiempo();
        return currentTime / totalTime;
    }

    /**
     * Obtiene los frames previo y siguiente de la animacion de acuerdo al
     * tiempo actual. Con estos 2 keyframes se interpolara sus transformaciones
     * para obtener una animacion mas fluida
     *
     * @return
     */
    private QAnimacionFrame[] obtenerPrevioySiguiente() {
        QAnimacionFrame previousFrame = listaFrames.get(0);
        QAnimacionFrame nextFrame = listaFrames.get(0);
        for (int i = 1; i < listaFrames.size(); i++) {
            nextFrame = listaFrames.get(i);
            if (nextFrame.getMarcaTiempo() >= tiempo) {
                break;
            }
            previousFrame = listaFrames.get(i);
        }
        return new QAnimacionFrame[]{previousFrame, nextFrame};
    }

    public QAnimacionFrame getFrame(int frame) {
        return listaFrames.get(frame);
    }

    /**
     * Devuelve el frame actual con las transformaciones a ser aplicadas
     *
     * @return
     */
    public QAnimacionFrame getFrameActual() {
        return frameActual;

//        switch (tipo) {
//            case DURACION_FRAMES:
//                if (frameActualPosicion == -1) {
//                    return null;
//                }
//                if (frameActualPosicion < listaFrames.size()) {
//                    return listaFrames.get(frameActualPosicion);
//                } else {
//                    System.out.println("se intenta dar una posicion q no hay en la lista se va devolver el primer frame");
//                    frameActualPosicion = 0;
//                    return listaFrames.get(0);
//                }
//
//            case DURACION_TIEMPO:
//                return frameActual;
//
//        }
//        return null;
    }

//    public int getTipo() {
//        return tipo;
//    }
//
//    public void setTipo(int tipo) {
//        this.tipo = tipo;
//    }
    public float getDuracion() {
        return duracion;
    }

    public void setDuracion(float duracion) {
        this.duracion = duracion;
    }

    public float getTiempo() {
        return tiempo;
    }

    public void setTiempo(float tiempo) {
        this.tiempo = tiempo;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public void destruir() {
        for (QAnimacionFrame frame : listaFrames) {
            frame.destruir();
        }
        listaFrames.clear();
        listaFrames = null;
    }

//    /**
//     * La duración de los frames depende de las pasadas del motor de animacion
//     * (fps) La el tiempo de duración de los frames depende de la velocidad del
//     * motor de animaciones y si esta varia la velocidad de la animacion no es
//     * constante
//     */
//    public static final int DURACION_FRAMES = 1;
//    /**
//     * La duración de los frames depende del tiempo transcurrido Cada frame debe
//     * tener una marca de tiempo en lugar de duracion de frames
//     */
//    public static final int DURACION_TIEMPO = 2;
//    private int tipo = 1;
//    private int frameActualPosicion = 0;
    // variables para el control de la animación con tiempo    
}
