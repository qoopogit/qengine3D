/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader;

import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.core.escena.QNeblina;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 * Las implementaciones de esta clase permiten el cálculo de color y luz de cada
 * pixel
 *
 * @author alberto
 */
/*
 * Implementaciones: 
 * 0. QSimpleShader. Sombreado simple donde no calcula iluminacion. Solo usa los colores sin textura
 * 1. QFlatShader. Sombreado Flat con caras con aristas visibles.
 * 2. QPhongShader. Sombreado de Phong con caras suaves sin aristas visibles.
 * 3. QTexturaShader. Permite el sombreado de Phong con texturas
 * 4. QIluminadoShader. Permite sl sombreado de Phong con texturas y cáculo de iluminción con las luces de la escena.
 * 5. QShadowShader. Permite los mismo que las atenriores más el cálculo de sombras.
 * 6. QFullShader. Permite todo lo anterior.
 */
public abstract class QShader {

    protected QMotorRender render;
    protected QIluminacion iluminacion = new QIluminacion();

    //usada en los anteriores, esta pendiente quitar
    protected float r, g, b;

    protected QColor color = new QColor();//color default, blanco

    protected float iluminacionDifusa;
    protected float iluminacionEspecular;
    protected float distanciaLuz;
    protected QVector3 tmpPixelPos = new QVector3();
    protected QVector3 vectorLuz = new QVector3();
    protected QVector3 tempVector = new QVector3();

    public QShader(QMotorRender render) {
        this.render = render;
    }

    /**
     *
     * @param pixel
     * @param x
     * @param y
     * @return
     */
    public abstract QColor colorearPixel(QPixel pixel, int x, int y);

    public static QColor calcularNeblina(QColor color, QPixel pixel, QNeblina neblina) {

        if (!neblina.isActive()) {
            return color;
        }
//        float distance = length(pos);
        float distance = pixel.ubicacion.z;
        float fogFactor = (float) (1.0 / Math.exp((distance * neblina.getDensity()) * (distance * neblina.getDensity())));
        fogFactor = QMath.clamp(fogFactor, 0.0f, 1.0f);

        return new QColor(
                QMath.mix(neblina.getColour().r, color.r, fogFactor) / 2,
                QMath.mix(neblina.getColour().g, color.g, fogFactor) / 2,
                QMath.mix(neblina.getColour().b, color.b, fogFactor) / 2
        );
    }

//
//    /**
//     * Verifica si un punto pertenece al vector de la luz Este metodo de
//     * calcular sombrasRayos es realista pero muy muy lento
//     *
//     * @param vectorLuz
//     * @param x coordenada de la luz
//     * @param y coordenada de la luz
//     * @param z coordenada de la luz
//     * @param x1 coordenada del punto
//     * @param y1 coordenada del punto
//     * @param z1 coordenada del punto
//     * @return
//     */
//    private boolean verificarSombra(QVector3 vectorLuz, float x, float y, float z, float x1, float y1, float z1) {
//        //solo procesamos si es que esta habilitada la opcion de sombrasRayos
//
//        if (render.sombrasRayos && render.opciones.sombras) {
////            long tInicio = System.currentTimeMillis();
////            System.out.println("inicio calculo pixel " + tInicio);
//            //debemos recorrer todos los puntos (al menos lo svertices ejeY ver si no hay alguno que este cruzado por el vector
//            QPixel actual;
//            float componenx = 0;
//            float componeny = 0;
//            float componenz = 0;
//            for (int cy = 0; cy < render.camara.panelHeight; cy++) {
//                for (int cx = 0; cx < render.camara.panelWidth; cx++) {
////                    actual = pixel[cy][cx];
//                    actual = render.getFrameBuffer().getPixel(cy, cx);
//                    if (actual != null && actual.dibujar) {
//                        //si no es el mismo pixel porq el que quiero saber si esta con alguna sombra
//                        if (actual.x != x1 && actual.y != y1 && actual.z != z1) {
//                            componenx = (actual.x - x) / vectorLuz.x;
//                            componeny = (actual.y - y) / vectorLuz.y;
//                            componenz = (actual.z - z) / vectorLuz.z;
//                            if (componenx == componeny && componenx == componenz) {
//                                //hasta ahorita sabemos que pertenece a la misma recta falta determinar si esta en el modulo de esa recta
//                                System.out.println("si hay sombreados");
//                                return true;
//                            }
//                        }
//                    }
//                }
//            }
//
////            long tFin = System.currentTimeMillis();
////            System.out.println("fin calculo pixel " + (tInicio - tInicio) + "ms");
//        }
//        return false;
//    }
}
