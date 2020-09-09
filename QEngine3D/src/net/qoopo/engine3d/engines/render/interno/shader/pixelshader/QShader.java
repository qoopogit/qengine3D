/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.core.escena.QNeblina;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector3;
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
//    protected float r, g, b;
    protected QColor color = new QColor();//color default, blanco

//    protected float iluminacionDifusa;
//    protected float iluminacionEspecular;
    protected float distanciaLuz;
    protected QVector3 tmpPixelPos = new QVector3();
    protected QVector3 vectorLuz = new QVector3();
    protected QVector3 tempVector = new QVector3();

    protected static final float exponenteGamma = 1.0f / 2.2f;
    protected static final float exposicion = 1.0f;

    public QShader(QMotorRender render) {
        this.render = render;
    }

    public QMotorRender getRender() {
        return render;
    }

    public void setRender(QMotorRender render) {
        this.render = render;
    }

    /**
     * Realiza la correccion de Gamma
     *
     * https://learnopengl.com/Advanced-Lighting/Gamma-Correction
     *
     * @param color
     * @return
     */
    public static QColor corregirGamma(QColor color) {
        QVector3 tmpColor = color.rgb();
        //HDR tonemapping 
        tmpColor.divide(tmpColor.clone().add(QVector3.unitario_xyz));
        // Con ajuste de exposicion 
//        tmpColor = QVector3.unitario_xyz.clone().subtract(new QVector3((float) Math.exp(-tmpColor.x * exposicion), (float) Math.exp(-tmpColor.y * exposicion), (float) Math.exp(-tmpColor.z * exposicion)));
        //correccion de gamma        
        tmpColor.set(QMath.pow(tmpColor.x, exponenteGamma), QMath.pow(tmpColor.y, exponenteGamma), QMath.pow(tmpColor.z, exponenteGamma));
        //cambia el color
        color.set(color.a, tmpColor.x, tmpColor.y, tmpColor.z);
        return color;
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

}
