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
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QShaderNodo;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerProcesadorTextura;

/**
 *
 * @author alberto
 */
public class QNodoColorReflexion extends QShaderNodo {

    private QPerColor enNormal;
    private QPerColor saColor;
    private QPerProcesadorTextura enTextura;

    private QColor color;

    private int tipoMapaEntorno;

    public QNodoColorReflexion() {
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

    public QNodoColorReflexion(QProcesadorTextura textura) {
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
            calcularReflexion(render, pixel);

            saColor.setColor(color);
        }

    }

    private void calcularReflexion(QMotorRender render, QPixel pixel) {
        // Reflexion del entorno 
        if (render.opciones.isMaterial()) {
            TempVars tm = TempVars.get();
            try {
                //*********************************************************************************************
                //******                    VECTOR VISION 
                //*********************************************************************************************
                // el pixel ya esta calculado en el espacio de la camara (primero se transformo los vertices, luego se interpolo),
                //la cámara esta en la posición (0,0,0) , por lo tanto es el vector visión
                tm.vector3f1.set(pixel.ubicacion.getVector3());
                //calculo vector vision método 2, segun video https://www.youtube.com/watch?v=xutvBtrG23A minuto 9:14
//                tm.vector3f1.setXYZ(currentPixel.x, currentPixel.y, currentPixel.z);
//                //quito la transformación Vista*Modelo (aunq mantiene la transformación de proyección)
//                tm.vector3f1.set(QTransformar.transformarVectorInversa(tm.vector3f1, currentPixel.entidad, render.getCamara()));
//                tm.vector3f1.set(QTransformar.transformarVectorModelo(tm.vector3f1, currentPixel.entidad));//aplica transformación de la entidad solamente
//                //luego resto de la posicion de la camara
//                tm.vector3f1.add(render.getCamara().getMatrizTransformacionTMP().toTranslationVector().multiply(-1.0f));
//                //
//                tm.vector3f1.normalize();
                //normal
                tm.vector3f2.set(pixel.normal);
                tm.vector3f2.normalize();
                //QMapaCubo mapaCubo = QUtilComponentes.getMapaCubo(currentPixel.entidad);
                //************************************************************
                //******                    REFLEXION
                //************************************************************

                tm.vector3f3.set(QMath.reflejarVector(tm.vector3f1, tm.vector3f2));
                //como estamos en el espacio de la cámara quitamos esa transformación del vector reflejo
                //tm.vector3f3.set(QTransformar.transformarVectorNormal(tm.vector3f3, currentPixel.entidad, render.getCamara()));
                tm.vector3f3.set(QTransformar.transformarVectorNormalInversa(tm.vector3f3, pixel.entidad, render.getCamara()));// funciona siempre y cuando el objeto no este rotado
                //convertirmos del espacio de la cámara al espacio del mundo 
//                tm.vector3f3.set(QTransformar.transformarVectorNormal(tm.vector3f3, render.getCamara().getMatrizTransformacion(QGlobal.tiempo).invert())); //funciona cuando se rota el objeto pero hay un error con lados invertidos
                color = QTexturaUtil.getColorMapaEntorno(tm.vector3f3, enTextura.getProcesadorTextura(), tipoMapaEntorno);

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

}
