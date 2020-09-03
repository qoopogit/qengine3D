/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QShaderNodo;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;

/**
 *
 * @author alberto
 */
public class QNodoColorIluminacion extends QShaderNodo {

    private QPerColor enColor;
    private QPerColor enNormal;
    private QPerColor saColor;
    //------------------ variables internas

    private float distanciaLuz;
    private final QVector3 tmpPixelPos = new QVector3();
    private final QVector3 vectorLuz = new QVector3();
    private final QVector3 tempVector = new QVector3();

    private final QIluminacion iluminacion = new QIluminacion();
    private final QColor colorEspecular = QColor.WHITE.clone();

    private QColor color;

    public QNodoColorIluminacion() {
        enColor = new QPerColor(QColor.WHITE);
        enColor.setNodo(this);
        enNormal = new QPerColor(QColor.BLACK);
        enNormal.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        saColor.setNodo(this);
        entradas = new ArrayList<>();

        entradas.add(enColor);
        entradas.add(enNormal);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    public QNodoColorIluminacion(QColor color) {
        enColor = new QPerColor(color);
        enNormal = new QPerColor(QColor.BLACK);
        saColor = new QPerColor(QColor.WHITE);
        enColor.setNodo(this);
        enNormal.setNodo(this);
        saColor.setNodo(this);

        entradas = new ArrayList<>();
        entradas.add(enColor);
        entradas.add(enNormal);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    @Override
    public void procesar(QMotorRender render, QPixel pixel) {
        //toma el color de entrada        

        if (render.opciones.isMaterial()) {
            enColor.procesarEnlaces(render, pixel);
            color = enColor.getColor().clone();

            if (render.opciones.isNormalMapping() && enNormal.tieneEnlaces()) {
                enNormal.procesarEnlaces(render, pixel);
                //cambio la direccion de la normal del pixel de acuerdo a la normal de entrada (mapa de normales)
                QColor tmp = enNormal.getColor();
                pixel.arriba.multiply((tmp.g * 2 - 1));
                pixel.derecha.multiply((tmp.r * 2 - 1));
                pixel.normal.multiply(tmp.b * 2 - 1);
                pixel.normal.add(pixel.arriba, pixel.derecha);
                pixel.normal.normalize();
            }

            //paso 1 calcular iluminación phong
            calcularIluminacion(render, iluminacion, pixel);
//
            
            color.scale(iluminacion.getColorAmbiente());
            
            color.addLocal(iluminacion.getColorLuz());
            saColor.setColor(color);
        }

    }

    private void calcularIluminacion(QMotorRender render, QIluminacion iluminacion, QPixel pixel) {
        pixel.normal.normalize();

        //inicia con la luz ambiente ambiente
        iluminacion.setColorAmbiente(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));

        TempVars tv = TempVars.get();
        try {
            iluminacion.setColorLuz(QColor.BLACK.clone());
            float factorSombra = 1;//1= no sombra
            float reflectancia = 1.0f;

            // solo si hay luces y si las opciones de la vista tiene activado el material
            if (render.opciones.isMaterial() && !render.getLuces().isEmpty()) {
                for (QLuz luz : render.getLuces()) {
                    //si esta encendida
                    if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
                        factorSombra = 1;
                        QProcesadorSombra proc = luz.getSombras();
                        if (proc != null && render.opciones.isSombras()) {
                            tv.vector3f1.set(pixel.ubicacion.getVector3());
                            factorSombra = proc.factorSombra(QTransformar.transformarVectorInversa(tv.vector3f1, pixel.entidad, render.getCamara()), pixel.entidad);
                        }

                        if (luz instanceof QLuzPuntual || luz instanceof QLuzSpot) {
                            vectorLuz.setXYZ(pixel.ubicacion.x - luz.entidad.getTransformacion().getTraslacion().x, pixel.ubicacion.y - luz.entidad.getTransformacion().getTraslacion().y, pixel.ubicacion.z - luz.entidad.getTransformacion().getTraslacion().z);
                            //solo toma en cuenta  a los puntos  q estan en el area de afectacion
                            if (vectorLuz.length() > luz.radio) {
                                continue;
                            }

                            //si es Spot valido que este dentro del cono
                            if (luz instanceof QLuzSpot) {
                                QVector3 coneDirection = ((QLuzSpot) luz).getDirection().clone().normalize();
                                tv.vector3f2.set(vectorLuz);
                                if (coneDirection.angulo(tv.vector3f2.normalize()) > ((QLuzSpot) luz).getAngulo()) {
                                    continue;
                                }
                            }
                            distanciaLuz = vectorLuz.x * vectorLuz.x + vectorLuz.y * vectorLuz.y + vectorLuz.z * vectorLuz.z;
                            QColor colorLuz = QMath.calcularColorLuz(color.clone(), colorEspecular.clone(), luz.color, luz.energia * factorSombra , pixel.ubicacion.getVector3(), vectorLuz.normalize().invert(), pixel.normal, 50, reflectancia);
                            colorLuz.scaleLocal(1.0f / distanciaLuz);
                            iluminacion.getColorLuz().addLocal(colorLuz);
                        } else if (luz instanceof QLuzDireccional) {
                            vectorLuz.copyXYZ(((QLuzDireccional) luz).getDirection());
                            iluminacion.getColorLuz().addLocal(QMath.calcularColorLuz(color, colorEspecular, luz.color, luz.energia * factorSombra , pixel.ubicacion.getVector3(), vectorLuz.normalize().invert(), pixel.normal, 50, reflectancia));
                        }
                    }
                }
            } else {
                //iluminacion default cuando no hay luces se asume una luz central
                tmpPixelPos.set(pixel.ubicacion.getVector3());
                tmpPixelPos.normalize();
                iluminacion.getColorAmbiente().add(-tmpPixelPos.dotProduct(pixel.normal));
            }
        } finally {
            tv.release();
        }
    }

    public QPerColor getEnColor() {
        return enColor;
    }

    public void setEnColor(QPerColor enColor) {
        this.enColor = enColor;
    }

    public QPerColor getSaColor() {
        return saColor;
    }

    public void setSaColor(QPerColor saColor) {
        this.saColor = saColor;
    }

    public QPerColor getEnNormal() {
        return enNormal;
    }

    public void setEnNormal(QPerColor enNormal) {
        this.enNormal = enNormal;
    }

}
