package net.qoopo.engine3d.componentes.geometria.primitivas;

import java.io.Serializable;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;

/**
 * Representa un vértice de los modelos 3D. Un vértice contiene coordenadas en
 * el espacio 3D, La normal de vértice, si el modelo tiene un esqueleto, el
 * vértice tiene una lista de los huesos y sus pesos
 *
 * @author alberto
 */
public class QVertice implements Serializable {

//    public float x, y, z;
    public QVector4 ubicacion = new QVector4();

    public QVector3 normal = new QVector3();
    //para pasarle a los pixel 
    public QVector3 arriba = new QVector3();
    public QVector3 derecha = new QVector3();
    public boolean normalInversa = false;

    public float u, v;
    /**
     * lista de huesos que afectan a este vértice
     */
    private QHueso listaHuesos[] = new QHueso[0];
    /**
     * lista de los pesos de cada hueso
     */
    private float listaHuesosPesos[] = new float[0];
    /**
     * lista de los ids de los huesos, se usa en el procesa de carga cuando aun
     * no hay un esqueleto pero si se tiene el id del hueso (ASSIMP)
     */
    private int listaHuesosIds[] = new int[0];
//    /**
//     * lista de las posiciones de los pesos (modelos md5)
//     */
//    private QVector3 listaPosicionPesos[] = new QVector3[0];

    public static final QVertice ZERO = new QVertice(0, 0, 0, 1);

    public QVertice() {
        ubicacion.x = 0;
        ubicacion.y = 0;
        ubicacion.z = 0;
        ubicacion.w = 1; //un vertice es de posicion siempre
//        ubicacion = new QVector3();
    }

    public QVertice(float x, float y, float z, float w) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = w;

//        ubicacion = new QVector3(x, y, z);
    }

    public QVertice(float x, float y, float z, float w, float u, float v) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = w;
        this.u = u;
        this.v = v;
//        ubicacion = new QVector3(x, y, z);
    }

    public QVertice(float x, float y, float z) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = 1;
//        ubicacion.w = w;        
//        ubicacion = new QVector3(x, y, z);
    }

    public QVertice(float x, float y, float z, float u, float v) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = 1;
//        ubicacion.w = w;    
        this.u = u;
        this.v = v;
//        ubicacion = new QVector3(x, y, z);
    }

    public void setXYZ(float x, float y, float z) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
//        ubicacion.set(x, y, z);
    }

//    /**
//     * Transforma el punto de acuerdo a un objeto de transformacion
//     * @param trans 
//     */
//    public void transformar(QTransformacion trans)
//    {
//        
//    }
    public void copyAttribute(QVertice other) {
        ubicacion.x = other.ubicacion.x;
        ubicacion.y = other.ubicacion.y;
        ubicacion.z = other.ubicacion.z;
        ubicacion.w = other.ubicacion.w;
//        ubicacion = other.ubicacion.clone();
        this.normal.set(other.normal);
    }

    public QVertice clone() {
        QVertice result = new QVertice(ubicacion.x, ubicacion.y, ubicacion.z, ubicacion.w);
        result.normal = normal.clone();
        return result;
    }

    @Override
    public String toString() {
        return "(" + ubicacion.x + ", " + ubicacion.y + ", " + ubicacion.z + ")";
//        return ubicacion.toString();
    }

    public QHueso[] getListaHuesos() {
        return listaHuesos;
    }

    public void setListaHuesos(QHueso[] listaHuesos) {
        this.listaHuesos = listaHuesos;
    }

    public float[] getListaHuesosPesos() {
        return listaHuesosPesos;
    }

    public void setListaHuesosPesos(float[] listaHuesosPesos) {
        this.listaHuesosPesos = listaHuesosPesos;
    }

//    public QVector3[] getListaPosicionPesos() {
//        return listaPosicionPesos;
//    }
//
//    public void setListaPosicionPesos(QVector3[] listaPosicionPesos) {
//        this.listaPosicionPesos = listaPosicionPesos;
//    }

    public int[] getListaHuesosIds() {
        return listaHuesosIds;
    }

    public void setListaHuesosIds(int[] listaHuesosIds) {
        this.listaHuesosIds = listaHuesosIds;
    }

}
