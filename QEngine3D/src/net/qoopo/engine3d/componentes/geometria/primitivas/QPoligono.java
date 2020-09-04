package net.qoopo.engine3d.componentes.geometria.primitivas;

import java.io.Serializable;
import net.qoopo.engine3d.core.math.QVector3;
import java.util.Arrays;
import net.qoopo.engine3d.componentes.geometria.QGeometria;

/**
 * Un poligono se dibuja agregando vertices en el orden contrario a las agujas
 * del reloj
 *
 * @author alberto
 */
public class QPoligono extends QPrimitiva implements Comparable<QPoligono>, Serializable {

    public QVector3 normal = new QVector3();
    //la normal transformada
    public QVector3 normalCopy = new QVector3();
    public boolean smooth = false;
    public QVertice center = new QVertice();
    //cel centor transformado
    public QVertice centerCopy = new QVertice();
    public boolean normalInversa = false;

    public QPoligono() {
    }

    public QPoligono(QGeometria parent) {
        super(parent);
    }

    public QPoligono(QGeometria parent, int... vertices) {
//        this(parent);
        super(parent, vertices);
    }

    public boolean verificarPuntoEnPlano(QVector3 punto) {
        return verificarPuntoEnPlano(punto, 0);
    }

    public boolean verificarPuntoEnPlano(QVector3 punto, float tolerancia) {
        //primero calculamos la distancia al plano
        float distancia = punto.dotProduct(this.normal);
        if (distancia >= -tolerancia && distancia <= tolerancia) {
            //falta verificar los limites con los puntos hasta ahorita solo sabemos si esta en el plano
            return true;
        }
        return false;
    }

    public void calculaNormalYCentro() {
        calculaNormalYCentro(geometria.listaVertices);
        normalInversa = false;
    }

    public void calculaNormalYCentro(QVertice[] vertices) {
        if (vertices.length >= 3) {
            normal = new QVector3(vertices[listaVertices[0]], vertices[listaVertices[1]]);
            normal.crossProduct(new QVector3(vertices[listaVertices[0]], vertices[listaVertices[2]]));
            normal.normalize();
            int count = 0;
            center.ubicacion.setXYZW(0, 0, 0, 1);

            for (int i : listaVertices) {
                center.ubicacion.add(vertices[i].ubicacion);
                count++;
            }
            center.ubicacion.multiply(1.0f / count);

            centerCopy.copyAttribute(center);
            normalCopy.set(normal);
        }
    }

//    public void calculaNormalYCentro(QVertice vert1, QVertice vert2, QVertice vert3) {
//        normal = new QVector3(vert1, vert2);
//        normal.crossProduct(new QVector3(vert2, vert3));
//        normal.normalize();
//        int count = 0;
////        center.x = 0;
////        center.y = 0;
////        center.z = 0;
//        center.ubicacion.setXYZW(0, 0, 0, 0);
////        for (int i : listaVertices) {
////            center.x += geometria.listaVertices[i].x;
////            center.y += geometria.listaVertices[i].y;
////            center.z += geometria.listaVertices[i].z;
////            count++;
////        }
//
//        center.ubicacion.x = vert1.ubicacion.x + vert2.ubicacion.x + vert3.ubicacion.x;
//        center.ubicacion.y = vert1.ubicacion.y + vert2.ubicacion.y + vert3.ubicacion.y;
//        center.ubicacion.z = vert1.ubicacion.z + vert2.ubicacion.z + vert3.ubicacion.z;
////        count = 3;
////------------
//        center.ubicacion.x /= count;
//        center.ubicacion.y /= count;
//        center.ubicacion.z /= count;
//        centerCopy.copyAttribute(center);
//        normalCopy.copyXYZ(normal);
//    }
    @Override
    public String toString() {
        return "QPoligono{" + "listaVertices=" + Arrays.toString(listaVertices) + '}';
    }

    @Override
    public int compareTo(QPoligono other) {
        return Float.valueOf(centerCopy.ubicacion.z).compareTo(other.centerCopy.ubicacion.z);
//        return Float.valueOf(other.centerCopy.z).compareTo(centerCopy.z);
    }

}
