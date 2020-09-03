/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.sombreado;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.textura.QTexturaUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QShaderNodo;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerProcesadorTextura;

/**
 *
 * @author alberto
 */
public class QNodoColorRefraccion extends QShaderNodo {

    private QPerColor enNormal;
    private QPerColor saColor;
    private QPerProcesadorTextura enTextura;
    private QColor color;
    private int tipoMapaEntorno;
    private float indiceRefraccion = 1.45f;//indice de refraccion, aire 1, agua 1.33, vidrio 1.52, diamante 2.42, 

    public QNodoColorRefraccion() {
        enNormal = new QPerColor(QColor.BLACK);
        enNormal.setNodo(this);
        enTextura = new QPerProcesadorTextura();
        enTextura.setNodo(this);
        saColor = new QPerColor(QColor.WHITE);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enTextura);
        entradas.add(enNormal);
        salidas = new ArrayList<>();
        salidas.add(saColor);
    }

    public QNodoColorRefraccion(QProcesadorTextura textura, float indiceRefraccion) {
        enTextura = new QPerProcesadorTextura(textura);
        enNormal = new QPerColor(QColor.BLACK);
        saColor = new QPerColor(QColor.WHITE);
        enTextura.setNodo(this);
        enNormal.setNodo(this);
        saColor.setNodo(this);
        entradas = new ArrayList<>();
        entradas.add(enTextura);
        entradas.add(enNormal);
        salidas = new ArrayList<>();
        salidas.add(saColor);
        this.indiceRefraccion = indiceRefraccion;
    }

    @Override
    public void procesar(QMotorRender render, QPixel pixel) {
        //toma el color de entrada        
        if (render.opciones.isMaterial()) {
            enTextura.procesarEnlaces(render, pixel);

            if (render.opciones.isNormalMapping() && enNormal.tieneEnlaces()) {
                enNormal.procesarEnlaces(render, pixel);
                //cambio la direccion de la normal del pixel de acuerdo a la normal de entrada (mapa de normales)
                QColor tmp = enNormal.getColor().clone();
////            pixel.normal=new QVector3(tmp.r * 2 - 1, tmp.g * 2 - 1, tmp.b * 2 - 1);
//            pixel.normal.add(new QVector3(tmp.r * 2 - 1, tmp.g * 2 - 1, tmp.b * 2 - 1));
//            pixel.normal.normalize();

                pixel.arriba.multiply((tmp.g * 2 - 1));
                pixel.derecha.multiply((tmp.r * 2 - 1));

                pixel.normal.multiply(tmp.b * 2 - 1);
                pixel.normal.add(pixel.arriba, pixel.derecha);
                pixel.normal.normalize();

            }
            calcularRefraccion(render, pixel);

            saColor.setColor(color);
        }

    }

    private void calcularRefraccion(QMotorRender render, QPixel pixel) {
        // Refraccion del entorno (en caso de materiales con refraccion (transparentes)
        if (render.opciones.isMaterial()) {
            TempVars tm = TempVars.get();
            try {
                //la normal del pixel
                tm.vector3f2.set(pixel.normal);
                tm.vector3f2.normalize();
                //*********************************************************************************************
                //******                    VECTOR VISION 
                //*********************************************************************************************
                //para obtener el vector vision quitamos la trasnformacion de la ubicacion y volvemos a calcularla en las coordenadas del mundo
//                tm.vector3f1.set(currentPixel.ubicacion.getVector3());                
                tm.vector3f1.set(QTransformar.transformarVector(QTransformar.transformarVectorInversa(pixel.ubicacion, pixel.entidad, render.getCamara()), pixel.entidad).getVector3());
                //ahora restamos la posicion de la camara a la posicion del mundo
                tm.vector3f1.subtract(render.getCamara().getMatrizTransformacion(QGlobal.tiempo).toTranslationVector());
                tm.vector3f1.normalize();

                //***********************************************************
                //******                    REFRACCION
                //***********************************************************
                tm.vector3f4.set(QMath.refractarVectorGL(tm.vector3f1, tm.vector3f2, indiceRefraccion > 0 ? 1.0f / indiceRefraccion : 0.0f)); //indice del aire sobre indice del material
                color = QTexturaUtil.getColorMapaEntorno(tm.vector3f4, enTextura.getProcesadorTextura(), tipoMapaEntorno);
            } catch (Exception e) {
//                System.out.println("error reflexion " + e.getMessage());
            } finally {
                tm.release();
            }
        }
    }

    public QPerColor getEnNormal() {
        return enNormal;
    }

    public void setEnNormal(QPerColor enNormal) {
        this.enNormal = enNormal;
    }

    public QPerColor getSaColor() {
        return saColor;
    }

    public void setSaColor(QPerColor saColor) {
        this.saColor = saColor;
    }

    public QPerProcesadorTextura getEnTextura() {
        return enTextura;
    }

    public void setEnTextura(QPerProcesadorTextura enTextura) {
        this.enTextura = enTextura;
    }

    public int getTipoMapaEntorno() {
        return tipoMapaEntorno;
    }

    public void setTipoMapaEntorno(int tipoMapaEntorno) {
        this.tipoMapaEntorno = tipoMapaEntorno;
    }

    public float getIndiceRefraccion() {
        return indiceRefraccion;
    }

    public void setIndiceRefraccion(float indiceRefraccion) {
        this.indiceRefraccion = indiceRefraccion;
    }

}
