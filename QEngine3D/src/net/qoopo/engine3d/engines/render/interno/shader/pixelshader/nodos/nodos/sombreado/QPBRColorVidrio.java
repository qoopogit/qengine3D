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
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.QNodoPBR;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerColor;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.nodos.core.perifericos.QPerProcesadorTextura;

/**
 *
 * @author alberto
 */
public class QPBRColorVidrio extends QNodoPBR {

    private QPerColor enNormal;
    private QPerColor saColor;
    private QPerProcesadorTextura enTextura;

    private QColor colorReflejo;
    private QColor colorRefraccion;

    private float factorFresnel = 0;

    private QColor color = QColor.BLACK.clone();

    private int tipoMapaEntorno;

    private float indiceRefraccion = 1.45f;//indice de refraccion, aire 1, agua 1.33, vidrio 1.52, diamante 2.42, 

    public QPBRColorVidrio() {
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

    public QPBRColorVidrio(QProcesadorTextura textura, float indiceRefraccion) {
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

        if (render.opciones.material //esta activada la opción de material
                ) {
            //toma el color de entrada 
            enTextura.procesarEnlaces(render, pixel);

            if (render.opciones.normalMapping && enNormal.tieneEnlaces()) {
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
            calcularEntorno(render, pixel);

            saColor.setColor(color);
        }

    }

    private void calcularEntorno(QMotorRender render, QPixel currentPixel) {
        // Reflexion y refraccion del entorno (en caso de materiales con refraccion (transparentes)
        if (render.opciones.material //esta activada la opción de material
                ) {
            TempVars tm = TempVars.get();
            try {
                //*********************************************************************************************
                //******                    VECTOR VISION 
                //*********************************************************************************************
                // el pixel ya esta calculado en el espacio de la camara (primero se transformo los vertices, luego se interpolo),
                //la cámara esta en la posición (0,0,0) , por lo tanto es el vector visión
                tm.vector3f1.set(currentPixel.ubicacion.getVector3());
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
                tm.vector3f2.set(currentPixel.normal);
                tm.vector3f2.normalize();
                //QMapaCubo mapaCubo = QUtilComponentes.getMapaCubo(currentPixel.entidad);
                //************************************************************
                //******                    REFLEXION
                //************************************************************

                tm.vector3f3.set(QMath.reflejarVector(tm.vector3f1, tm.vector3f2));
                //como estamos en el espacio de la cámara quitamos esa transformación del vector reflejo
                //tm.vector3f3.set(QTransformar.transformarVectorNormal(tm.vector3f3, currentPixel.entidad, render.getCamara()));
                    tm.vector3f3.set(QTransformar.transformarVectorNormalInversa(tm.vector3f3, currentPixel.entidad, render.getCamara()));// funciona siempre y cuando el objeto no este rotado
                //convertirmos del espacio de la cámara al espacio del mundo 
//                tm.vector3f3.set(QTransformar.transformarVectorNormal(tm.vector3f3, render.getCamara().getMatrizTransformacion(QGlobal.tiempo).invert())); //funciona cuando se rota el objeto pero hay un error con lados invertidos
                colorReflejo = QTexturaUtil.getColorMapaEntorno(tm.vector3f3, enTextura.getProcesadorTextura(), tipoMapaEntorno);

                //***********************************************************
                //******                    REFRACCION
                //***********************************************************
                tm.vector3f4.set(QMath.refractarVectorGL(tm.vector3f1, tm.vector3f2, 1.0f / indiceRefraccion)); //indice del aire sobre indice del material
//                    tm.vector3f4.set(QMath.refractarVector(tm.vector3f1, tm.vector3f2, 1.0f /indiceRefraccion)); //indice del aire sobre indice del material
//                tm.vector3f4.set(QMath.refractarVector3(tm.vector3f1, tm.vector3f2, 1.0f / indiceRefraccion)); //indice del aire sobre indice del material
                //como estamos en el espacio de la cámara quitamos esa transformación del vector refractado
                //tm.vector3f4.set(QTransformar.transformarVectorNormal(tm.vector3f4, currentPixel.entidad, render.getCamara()));
                    tm.vector3f4.set(QTransformar.transformarVectorNormalInversa(tm.vector3f4, currentPixel.entidad, render.getCamara()));// funciona siempre y cuando el objeto no este rotado
                //convertirmos del espacio de la cámara al espacio del mundo
//                tm.vector3f4.set(QTransformar.transformarVectorNormal(tm.vector3f4, render.getCamara().getMatrizTransformacion(QGlobal.tiempo).invert()));//funciona cuando se rota el objeto pero hay un error con lados invertidos
                colorRefraccion = QTexturaUtil.getColorMapaEntorno(tm.vector3f4, enTextura.getProcesadorTextura(), tipoMapaEntorno);

                //APLICACION DEL COLOR DEL ENTORNO              
                //mezclo el color de reflexion con el de refraccion
                if (colorReflejo != null && colorRefraccion != null) {
                    if (QGlobal.REFLEJOS_CALCULAR_FRESNEL) {
                        factorFresnel = QMath.factorFresnel(tm.vector3f1, tm.vector3f2, 0);
                    } else {
                        factorFresnel = 0.5f;//mezcla equilibrada entre reflejo y refraccion
                    }
//                    colorEntorno = QMath.mix(colorRefraccion, colorReflejo, factorFresnel);
                    color.r = QMath.mix(colorRefraccion.r, colorReflejo.r, factorFresnel);
                    color.g = QMath.mix(colorRefraccion.g, colorReflejo.g, factorFresnel);
                    color.b = QMath.mix(colorRefraccion.b, colorReflejo.b, factorFresnel);
                } else if (colorReflejo != null) {
                    color = colorReflejo.clone();
                } else if (colorRefraccion != null) {
                    color = colorRefraccion.clone();
                }

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
