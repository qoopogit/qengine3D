/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QGlobal {

    public static long tiempo = 0;

    public static final String RECURSOS = "/home/alberto/DATOS/Recursos/3D/";

    public static final float MOTOR_RENDER_FPS = 60f;

    //esta gravedad representa a la gravedad en el motor, se calcula en funcion d elos frames por segundo
    public static QVector3 gravedad;

    // Permite usar la aproximacion de la inversa de la raiz cuadrada de un número mejorando la velocidad de calculo, aunq no es 100% exacto
    public static boolean OPT_USAR_FAST_INVSQRT = true;

    //--------------------------------------------------------------------------------------------------------
    //------                            SOMBRAS
    //--------------------------------------------------------------------------------------------------------
    public static boolean SOMBRAS_DEBUG_PINTAR = false;
    public static boolean SOMBRAS_SUAVES = true;

    public static boolean SOMBRAS_DIRECCIONALES_CASCADA = true;
    public static int SOMBRAS_CASCADAS_TAMANIO = 3;
    public static float lambda = 0.95f;

    //--Resoluciones de los mapas de sombras
    public static int SOMBRAS_DIRECCIONAL_MAPA_ANCHO = 1024;
    public static int SOMBRAS_FOCOS_MAPA_ANCHO = 512;
    public static int SOMBRAS_OMNI_DIRECCIONAL_MAPA_ANCHO = 256;
    //-------- Otras opciones   

    //OPCIONES RENDERIZADOR REFLEJOS Y REFRACCION
    public static boolean REFLEJOS_CALCULAR_FRESNEL = true;
    public static int MAPA_CUPO_RESOLUCION = 512;
    public static boolean ANIMACION_INTERPOLAR = true;

    public static int TEST_INTER = 0;

    /**
     * Si esta variable es verdadera, el renderizador mostrara la posicion POSE
     * cuando el animador este detenido
     */
    public static boolean RENDER_ANIMATION_POSE = false;
}
