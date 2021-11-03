/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QUtilNormales {

    /**
     * *
     * Calcula normales para un objeto generado a mano
     *
     * @param objeto
     * @return
     */
    public static QGeometria calcularNormales(QGeometria objeto) {
        return calcularNormales(objeto, true);
    }

    /**
     * Calcula las normales de un objeto generado que no tiene normales
     *
     * @param objeto
     * @param forzar Si ya tiene normales no las vuelve a calcular
     * @return
     */
    public static QGeometria calcularNormales(QGeometria objeto, boolean forzar) {
        try {
            //se calcula las normales para los vértices, estos son usados para el suavizado
            for (QPrimitiva face : objeto.primitivas) {
                if (face instanceof QPoligono) {
                    QPoligono poligono = (QPoligono) face;
                    if (poligono.listaVertices.length >= 3) {
                        if (poligono.getNormal().equals(QVector3.zero) || forzar) {
                            poligono.calculaNormalYCentro();//calcula la normal de la cara
                        }
                        //le da a los vertices la normal de la cara
                        for (int i : poligono.listaVertices) {
                            objeto.vertices[i].normal.add(poligono.getNormal());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //normaliza 
        try {
            for (QVertice vertice : objeto.vertices) {
                vertice.normal.normalize();
                vertice.normalInversa = false;
            }
        } catch (Exception e) {
        }

        return objeto;
    }

    /**
     * Invierte las normales de una geometria
     *
     * @param objeto
     * @return
     */
    public static QGeometria invertirNormales(QGeometria objeto) {
        for (QPrimitiva face : objeto.primitivas) {
            if (face instanceof QPoligono) {
                ((QPoligono) face).getNormal().flip();
//            face.normalInversa = true;
                ((QPoligono) face).setNormalInversa(!((QPoligono) face).isNormalInversa());
            }
        }

        for (QVertice vertexList : objeto.vertices) {
            vertexList.normal.flip();
//            vertices.normalInversa = true;
            vertexList.normalInversa = !vertexList.normalInversa;
        }

        return objeto;
    }

}
