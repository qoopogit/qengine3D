/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.animacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alberto
 */
public class QAnimacionFrame implements Serializable {

    /**
     * Marca de tiempo en segundos
     */
    private float marcaTiempo;
    /**
     * QParAnimacion de paresModificarAnimacion a ser modificados en esta
     * animación
     */
    private List<QParAnimacion> paresModificarAnimacion = new ArrayList<>();

    public QAnimacionFrame() {

    }

//    public QAnimacionFrame(List<QParAnimacion> objetos) {
//        this.paresModificarAnimacion = objetos;
//    }
    public QAnimacionFrame(float marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }

    public QAnimacionFrame(float marcaTiempo, List<QParAnimacion> objetos) {
        this.marcaTiempo = marcaTiempo;
        this.paresModificarAnimacion = objetos;
    }

    public List<QParAnimacion> getParesModificarAnimacion() {
        return paresModificarAnimacion;
    }

    public void setParesModificarAnimacion(List<QParAnimacion> paresModificarAnimacion) {
        this.paresModificarAnimacion = paresModificarAnimacion;
    }

    public void agregarPar(QParAnimacion par) {
        paresModificarAnimacion.add(par);
    }

    public void eliminarPar(QParAnimacion par) {
        paresModificarAnimacion.remove(par);
    }

//    /**
//     * Valida si se debe pasar al siguiente frame para el tipo de animacion en
//     * frames
//     *
//     * @return
//     */
//    public boolean pasarSiguienteFrame() {
//        aplicarTransformacion = false;
//        contadorTiempoVida--;
//        if (contadorTiempoVida <= 0) {
//            contadorTiempoVida = frames;
//            aplicarTransformacion = true;
//            return true;
//        } else {
//            return false;
//        }
//    }
    /**
     * Valida si se debe pasar al siguiente frame de acuerdo al marcaTiempo de
     * la animacion
     *
     * @param tiempo
     * @return
     */
    public boolean pasarSiguienteFrame(float tiempo) {
        return tiempo >= this.marcaTiempo;
    }

    public float getMarcaTiempo() {
        return marcaTiempo;
    }

    public void setMarcaTiempo(float marcaTiempo) {
        this.marcaTiempo = marcaTiempo;
    }

    public void destruir() {
        paresModificarAnimacion.clear();
        paresModificarAnimacion = null;
    }

//    /**
//     * Es una animacion infinita.El motor de animaciones no lo va a detener
//     * nunca Se usa por ejemplo para que un objeto rote todo el marcaTiempo
//     */
//    public static final int TIPO_INFINITA = 0;
//    /**
//     * Es una animacion finita solo se ejecuta y se pasa a la siguiente
//     */
//    public static final int TIPO_FINITA = 1;
//    
//    /**
//     * La transformacion que se configura en este frame, se setea tal como esta
//     * a la entidad objetivo
//     */
//    public static final int TIPO_TRANSFORMACION_UNICA = 1;
//    /**
//     * La transformación que se configura en este frame se adiciona a la actual
//     * transformacion configurada en la entidad objetivo
//     */
//    public static final int TIPO_TRANSFORMACION_ADICIONAR = 2;
//
////    private int tipo = TIPO_FINITA;
//
////    private boolean regresarAlInicio = false;
//    private int tipoTransformacion = TIPO_TRANSFORMACION_UNICA;
    //Esta variable indica si se debe aplicar o no la transformacion.
    //en caso que exista un frame de adicion, pero que debe durar 20 frames, solo se aplicaria la transformacion en el primer frame
    //y no en los siguientes 19
    //esto se controla con esta variable
//    private boolean aplicarTransformacion = true;    
    //indica cuantos frames (pasadas del motor de animaciones) se debe mantener este frame
    // para el tipo de animacion de frames
//    private int frames = 1;
//    private int contadorTiempoVida;    
}
