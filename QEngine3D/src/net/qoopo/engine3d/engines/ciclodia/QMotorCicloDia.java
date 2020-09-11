/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.ciclodia;

import net.qoopo.engine3d.QMotor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.cielo.QCielo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 * Controla el ciclo de día y noche en el mundo
 *
 * @author alberto
 */
public class QMotorCicloDia extends QMotor {

    protected QMotorRender render;
    /**
     * Cielo que se modificara para rotar las nubes y cambiar el color de
     * acuerdo a la hora. Y mezclar las texturas para el cambio
     */
    private QCielo cielo;

    protected boolean ejecutando = false;

    /**
     * Intervalo de dia entero en segundos
     */
    private float horaDelDia = 0;
    private boolean dia = true;
    private final float maximo = 0.5f;//iluminacion maxima al medio dia
    private final float minimo = 0.15f;
    private final QLuzDireccional sol;
    private final Thread hiloActualizacion;
    private final float angulo = 360 / 24;
    private final QVector3 direccionSolOriginal = new QVector3(0, 1, 0);

    public QMotorCicloDia(QCielo cielo, QMotorRender render, long duracionDiaEnSegundos, QLuzDireccional sol, float horaInicial) {
        this.render = render;
        this.cielo = cielo;
        this.sol = sol;
        sol.getDirection().set(direccionSolOriginal);
        this.horaDelDia = horaInicial;
        this.render.horaDelDia = horaDelDia;
        hiloActualizacion = new Thread(new Runnable() {
            @Override
            public void run() {
                //sera cada media hora segun intervalo
//                long t = 1000 * duracionHoraEnSegundos  / 2;
                long t = 1000 * duracionDiaEnSegundos / 24 / 2;
                QLogger.info("MDN--> El intervalo de ejecución=" + t);
                while (ejecutando) {
                    update();
                    try {
                        Thread.sleep(t);//disminuye uso de cpu, 
                    } catch (Exception e) {

                    }
                }
            }
        });
    }

    @Override
    public void iniciar() {
        ejecutando = true;
        hiloActualizacion.start();
    }

    @Override
    public void detener() {
        ejecutando = false;
        try {
            Thread.sleep(300);
        } catch (Exception e) {

        }
    }

    /**
     * Calcula el valor de la ilumincaicon amiental y la dirección del sol
     */
    private void calcularIluminacionAmbiental() {
        try {
            dia = horaDelDia >= 6 && horaDelDia <= 18f;

            if (horaDelDia > 24) {
                horaDelDia = 0;
            }

            render.horaDelDia = horaDelDia;
            //calculo la iluminacion
            if (dia) {
                //el dia tiene 12 horas
                // 2 periodos de 6 horas donde se va incrementando hasta el maximo y luego se va disminuyendo
                sol.getDirection().set(direccionSolOriginal);
                sol.getDirection().rotateZ(-(float) Math.toRadians(horaDelDia * angulo));
                sol.setEnable(true);
                if (horaDelDia < 12) {
                    //antes del medio dia
                    float v = maximo * (horaDelDia - 6) / 12 + minimo;
                    render.getEscena().setColorAmbiente(new QColor(v, v, v));
                } else {
                    float v = maximo - maximo * (horaDelDia - 12) / 12;
                    render.getEscena().setColorAmbiente(new QColor(v, v, v));
                }

//            System.out.println("luz ambiente=" + render.ambient);
            } else {
                //si es de noche seteo una iluminacin global minima
                render.getEscena().setColorAmbiente(new QColor(minimo, minimo, minimo));
                sol.setEnable(false);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private void actualizarCielo() {
//    (float) Math.toRadians(horaDelDia * angulo)
        // rota el cielo de acuerod a la hora, dando la sencacion que el planeta rota alrededor de su propio eje
        if (cielo != null) {
            cielo.getEntidad().rotar(0, Math.toRadians(horaDelDia * angulo), 0);
            //actualiza la textura del cielo
            actualizarTexturaCielo();
        }
    }

    private void actualizarTexturaCielo() {
        try {
            //valido rangos de transicion
            //madrugada
            if (cielo != null) {
                if (horaDelDia >= 5 && horaDelDia <= 7) {
                    // el tiempo de transicion es de 2 horas, por eso divido para 2, resto 5 por ser la hora inicial de la transicion
                    cielo.setRazon(1.0f - (horaDelDia - 5) / 2);
                } else if (horaDelDia >= 18 && horaDelDia <= 20) {
                    // el tiempo de transicion es de 2 horas, por eso divido para 2, resto 18 por ser la hora incial de la transicion, y resto de 1 porq quiero que sea descendente
                    cielo.setRazon((horaDelDia - 18) / 2);
                } else if (dia) {
                    //dia
                    cielo.setRazon(0);
                } else { //noche
                    cielo.setRazon(1);
                }

            }
        } catch (Exception e) {
        }
    }

    @Override
    public long update() {
        horaDelDia += 0.5f;
        calcularIluminacionAmbiental();
        actualizarCielo();
        this.render.horaDelDia = horaDelDia;

        QLogger.info("MDN-->" + DF.format(getFPS()) + " FPS");
        tiempoPrevio = System.currentTimeMillis();
        return tiempoPrevio;
    }
}
