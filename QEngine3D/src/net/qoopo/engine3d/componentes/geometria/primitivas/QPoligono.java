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

    private QVector3 normal = new QVector3();
    //la normal transformada
    private QVector3 normalCopy = new QVector3();
    private boolean smooth = false;
    private QVertice center = new QVertice();
    //cel centor transformado
    private QVertice centerCopy = new QVertice();
    private boolean normalInversa = false;

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
        float distancia = punto.dot(this.normal);
        if (distancia >= -tolerancia && distancia <= tolerancia) {
            //falta verificar los limites con los puntos hasta ahorita solo sabemos si esta en el plano
            return true;
        }
        return false;
    }

    public void calculaNormalYCentro() {
        calculaNormalYCentro(geometria.vertices);
        normalInversa = false;
    }

    public void calculaNormalYCentro(QVertice[] vertices) {
        if (vertices.length >= 3) {
            try {
//                normal = new QVector3(vertices[vertices[0]], vertices[vertices[1]]);
                normal.set(vertices[listaVertices[0]], vertices[listaVertices[1]]);
                normal.crossProduct(new QVector3(vertices[listaVertices[0]], vertices[listaVertices[2]]));
                normal.normalize();
                int count = 0;
                center.ubicacion.set(0, 0, 0, 1);
                for (int i : listaVertices) {
                    center.ubicacion.add(vertices[i].ubicacion);
                    count++;
                }
                center.ubicacion.multiply(1.0f / count);
                centerCopy.copyAttribute(center);
                normalCopy.set(normal);
            } catch (Exception e) {

            }
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
//        center.ubicacion.set(0, 0, 0, 0);
////        for (int i : vertices) {
////            center.x += geometria.vertices[i].x;
////            center.y += geometria.vertices[i].y;
////            center.z += geometria.vertices[i].z;
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

    public QVector3 getNormal() {
        return normal;
    }

    public void setNormal(QVector3 normal) {
        this.normal = normal;
    }

    public QVector3 getNormalCopy() {
        return normalCopy;
    }

    public void setNormalCopy(QVector3 normalCopy) {
        this.normalCopy = normalCopy;
    }

    public boolean isSmooth() {
        return smooth;
    }

    public void setSmooth(boolean smooth) {
        this.smooth = smooth;
    }

    public QVertice getCenter() {
        return center;
    }

    public void setCenter(QVertice center) {
        this.center = center;
    }

    public QVertice getCenterCopy() {
        return centerCopy;
    }

    public void setCenterCopy(QVertice centerCopy) {
        this.centerCopy = centerCopy;
    }

    public boolean isNormalInversa() {
        return normalInversa;
    }

    public void setNormalInversa(boolean normalInversa) {
        this.normalInversa = normalInversa;
    }

}
