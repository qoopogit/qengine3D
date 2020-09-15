/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QDefinirCentro {

    /**
     * Traslada la ubicación de la entidad al centro del origen y sus vertices
     * acorde al traslado
     *
     * @param entidad
     */
    public static void definirCentroGeometriaAlOrigen(QEntidad entidad) {

    }

    /**
     * Traslada la ubicación de la entidad al centro de la geometria
     *
     * @param entidad
     */
    public static void definirCentroOrigenAGeometria(QEntidad entidad) {
        //paso 1 . Obtener el centro de todos los vertices pues sera la nueva ubicación de la trasnformación
        List<QVertice> vertices = new ArrayList<>();
        for (QComponente com : entidad.getComponentes()) {
            if (com instanceof QGeometria) {
                for (QVertice ver : ((QGeometria) com).vertices) {
                    vertices.add(ver);
                }
            }
        }

        //recorrer los vertice sy sacar punto medio
        QVector3 centro = new QVector3();
        int c = 0;
        for (QVertice vertice : vertices) {
//            centro.add(new QVector3(vertice.x, vertice.y, vertice.z));
            centro.add(vertice.ubicacion.getVector3());
            c++;
        }
        centro.multiply(1.0f / c);//divido para obtener promedio
        //Paso 2. A todos los vertices restar el centro 
        for (QVertice vertice : vertices) {
            vertice.ubicacion.x -= centro.x;
            vertice.ubicacion.y -= centro.y;
            vertice.ubicacion.z -= centro.z;
        }

        //Paso 3 . a LA trasnformación dar la ubicación del centro
        entidad.getTransformacion().getTraslacion().set(centro);
    }
}
