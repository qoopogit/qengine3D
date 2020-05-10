/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.util;

import net.qoopo.engine3d.core.math.QVector3;

/**
 * Clase usada para el manejo de las unidades de medida
 * @author alberto
 */
public class QUnidadMedida {

    private float unidad_x_pixel = 1;
    private int tipoUnidad = 1;
    public static final int MILIMETRO = 1;
    public static final int CENTIMETRO = 2;
    public static final int METRO = 3;
    public static final int KILOMETRO = 4;

    /**
     *
     * @param unidades_x_pixel
     * @param tipoUnidad
     */
    public void inicializar(float unidades_x_pixel, int tipoUnidad) {
        this.tipoUnidad = tipoUnidad;
        this.unidad_x_pixel = unidades_x_pixel;
    }

    /**
     * Convierte a pixeles las unidades
     *
     * @param unidades as unidades estan en el formato configurado
     * @return
     */
    public float convertirPixel(float unidades) {
        return unidades / unidad_x_pixel;
    }

    /**
     * Convierte a unidades los pixeles. La undiad devuelta esta en el tipo
     * actualmetne configurado
     *
     * @param valorPixeles
     * @return
     */
    public float convertirUnidades(float valorPixeles) {
        return valorPixeles * unidad_x_pixel;
    }

    /**
     * Convierte las unidades a pixeles. Se calcula primero la referencia a
     * unidades actualmente configuradas de acuerdo al tipo del parametro
     *
     * @param unidades
     * @param tipo
     * @return
     */
    public float convertirPixel(float unidades, int tipo) {

        //de acuerdo a la unidad actual
        switch (tipoUnidad) {

            case MILIMETRO:
                switch (tipo) {

                    case CENTIMETRO:
                        unidades = unidades * 10;
                        break;
                    case METRO:
                        unidades = unidades * 1000;
                        break;
                    case KILOMETRO:
                        unidades = unidades * 1000 * 1000;
                        break;
                }

                break;
            case CENTIMETRO:
                switch (tipo) {
                    case MILIMETRO:
                        unidades = unidades / 10;
                        break;
                    case METRO:
                        unidades = unidades * 100;
                        break;
                    case KILOMETRO:
                        unidades = unidades * 1000 * 10;
                        break;
                }
                break;
            case METRO:
                switch (tipo) {

                    case MILIMETRO:
                        unidades = unidades / 1000;
                        break;
                    case CENTIMETRO:
                        unidades = unidades / 100;
                        break;
                    case KILOMETRO:
                        unidades = unidades * 1000;
                        break;
                }
                break;
            case KILOMETRO:
                switch (tipo) {
                    case MILIMETRO:
                        unidades = unidades / (1000 * 1000);
                        break;
                    case CENTIMETRO:
                        unidades = unidades / (1000 * 100);
                        break;
                    case METRO:
                        unidades = unidades / 1000;
                        break;
                }
                break;
        }

        return convertirUnidades(unidades);
    }

    public float convertirUnidades(float valorPixeles, int tipo) {

        //de acuerdo a la unidad actual
        switch (tipoUnidad) {

            case MILIMETRO:
                switch (tipo) {

                    case CENTIMETRO:
                        valorPixeles = valorPixeles * 10;
                        break;
                    case METRO:
                        valorPixeles = valorPixeles * 1000;
                        break;
                    case KILOMETRO:
                        valorPixeles = valorPixeles * 1000 * 1000;
                        break;
                }

                break;
            case CENTIMETRO:
                switch (tipo) {

                    case MILIMETRO:
                        valorPixeles = valorPixeles / 10;
                        break;
                    case METRO:
                        valorPixeles = valorPixeles * 100;
                        break;
                    case KILOMETRO:
                        valorPixeles = valorPixeles * 1000 * 10;
                        break;
                }
                break;
            case METRO:
                switch (tipo) {

                    case MILIMETRO:
                        valorPixeles = valorPixeles / 1000;
                        break;
                    case CENTIMETRO:
                        valorPixeles = valorPixeles / 100;
                        break;
                    case KILOMETRO:
                        valorPixeles = valorPixeles * 1000;
                        break;
                }
                break;
            case KILOMETRO:
                switch (tipo) {

                    case MILIMETRO:
                        valorPixeles = valorPixeles / (1000 * 1000);
                        break;
                    case CENTIMETRO:
                        valorPixeles = valorPixeles / (1000 * 100);
                        break;
                    case METRO:
                        valorPixeles = valorPixeles / 1000;
                        break;

                }
                break;
        }

        return convertirUnidades(valorPixeles);
    }

    /**
     * Adecua un vector de velocidad m/s de acuerdo a los procesos por segundo
     * que hara el motor de física
     *
     * @param velocidad Un vector de velocidad en m/s
     * @return
     */
    public static QVector3 velocidad(QVector3 velocidad) {
//        return velocidad.multiply(1f / QGlobal.MOTOR_FISICA_FPS);
        return velocidad.clone();
    }

//    private float convertirMilimetros(float valor)
//    {
//        
//    }
}
