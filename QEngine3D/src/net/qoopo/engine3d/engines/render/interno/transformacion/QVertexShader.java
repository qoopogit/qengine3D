/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.transformacion;

import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;

/**
 * Vertex Shader. Realiza los calculos de transformacion de cada vertice
 *
 * @author alberto
 */
public class QVertexShader {

    /**
     * Transforma un vertice y devuelve un nuevo vertice con la transformación
     * aplicada
     *
     * //Viene a ser el Vertex Shader del OpenGL
     *
     * @param vertice
     * @param matrizVistaModelo Matriz Vista Modelo
     * @return
     */
    public static QVertice transformarVertice(QVertice vertice, QMatriz4 matrizVistaModelo) {
        QVertice nuevo = new QVertice();
        nuevo.u = vertice.u;
        nuevo.v = vertice.v;
        TempVars tmp = TempVars.get();

        //tmp.vector4f1-- posicion
        //tmp.vector4f2 --normal
        //tmp.vector4f3 --normal (entrada para multiplicar)
        try {

            //Pasos
            // 1.En caso de existir un esqueleto,  Modificar la posición del vértice de acuerdo a las matrices de transformación de los huesos
            // 2. Modificar el vertice con la matriz de tranformación enviada modifico de acuerdo a las matrices de transformacion de los huesos
            //********************************* PASO 1 ********************************************
            //(!QGlobal.RENDER_ANIMATION_POSE || (QGlobal.RENDER_ANIMATION_POSE && (QMotor3D.INSTANCIA != null && QMotor3D.INSTANCIA.getMotorAnimacion() != null && QMotor3D.INSTANCIA.getMotorAnimacion().isEjecutando()))) && 
            if (vertice.getListaHuesos() != null && vertice.getListaHuesos().length > 0) {

                QHueso hueso;
                QMatriz4 matrizTransformacionHueso;
                tmp.vector4f1.setXYZW(0, 0, 0, 1);//posicion final
                tmp.vector4f2.setXYZW(0, 0, 0, 0);//normal final

                float peso = 0;

                //recorre los huesos que afectan el vertice
                for (int i = 0; i < vertice.getListaHuesos().length; i++) {
                    peso = vertice.getListaHuesosPesos()[i];
                    hueso = vertice.getListaHuesos()[i];
                    matrizTransformacionHueso = hueso.getMatrizTransformacionHueso(QGlobal.tiempo);
                    tmp.vector4f1.add(matrizTransformacionHueso.mult(vertice.ubicacion).multiply(peso));
                    //la normal
                    tmp.vector4f3.set(vertice.normal, 0);
                    tmp.vector4f2.add(matrizTransformacionHueso.mult(tmp.vector4f3).multiply(peso));
                }

            } else {
                // si no hay esqueleto o no esta activo el motor de animaci[on, usa la coordenada del vertice
                tmp.vector4f1.set(vertice.ubicacion);
                tmp.vector4f2.set(vertice.normal, 0);
            }

            //********************************* PASO 2 ********************************************
            //multiplica la matriz vista modelo a la coordenada
            tmp.vector4f1.set(matrizVistaModelo.mult(tmp.vector4f1));
            //multipla la matriz vista modelo por la normal
            tmp.vector4f2.set((matrizVistaModelo.mult(tmp.vector4f2)));

//            //proyecta
//            if (PROYECTAR) {
//                tmp.vector3f2.set(camara.proyectarNormalizar(tmp.vector3f2));
//            }
            nuevo.ubicacion.set(tmp.vector4f1);
            nuevo.normal = tmp.vector4f2.getVector3();
            nuevo.normal.normalize();
        } finally {
            tmp.release();
        }
        return nuevo;
    }

//    /**
//     * Transforma una entidad vertice que tiene una posicion y un vector normal
//     *
//     * Usada por el render para dibujar las luces
//     *
//     * @param vertice
//     * @param entidad
//     * @param camara
//     * @return
//     */
//    public static QVertice transformarVertice(QVertice vertice, QEntidad entidad, QCamara camara) {
//        QMatriz4 matVistaModelo = camara.getMatrizTransformacion(QGlobal.tiempo).invert().mult(entidad.getMatrizTransformacion(QGlobal.tiempo));
//        return QVertexShader.transformarVertice(vertice, matVistaModelo);
//    }
}
