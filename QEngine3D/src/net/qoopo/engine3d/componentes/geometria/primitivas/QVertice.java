package net.qoopo.engine3d.componentes.geometria.primitivas;

import java.io.Serializable;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;

/**
 * Representa un vértice de los modelos 3D. Un vértice contiene coordenadas en
 * el espacio 3D, La normal de vértice, si el modelo tiene un esqueleto, el
 * vértice tiene una lista de los huesos y sus pesos
 *
 * @author alberto
 */
public class QVertice implements Serializable {

    public static final QVertice ZERO = new QVertice(0, 0, 0, 1);

    public QVector4 ubicacion = new QVector4();
    public QVector3 normal = new QVector3();
    public float u, v;

    public QVector3 arriba = new QVector3();
    public QVector3 derecha = new QVector3();
    public boolean normalInversa = false;

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

    public QVertice() {
        ubicacion.x = 0;
        ubicacion.y = 0;
        ubicacion.z = 0;
        ubicacion.w = 1; //un vertice es de posicion siempre
    }

    public QVertice(float x, float y, float z, float w) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = w;
    }

    public QVertice(float x, float y, float z, float w, float u, float v) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = w;
        this.u = u;
        this.v = v;
    }

    public QVertice(float x, float y, float z) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = 1;
    }

    public QVertice(float x, float y, float z, float u, float v) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = 1;
        this.u = u;
        this.v = v;
    }

    public void set(float x, float y, float z, float w) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
        ubicacion.w = w;
    }

    public void setXYZ(float x, float y, float z) {
        ubicacion.x = x;
        ubicacion.y = y;
        ubicacion.z = z;
    }

    public void set(QVertice vertice) {
        ubicacion.x = vertice.ubicacion.x;
        ubicacion.y = vertice.ubicacion.y;
        ubicacion.z = vertice.ubicacion.z;
        ubicacion.w = vertice.ubicacion.w;
        this.normal.set(vertice.normal);
    }

    public void copyAttribute(QVertice other) {
        ubicacion.x = other.ubicacion.x;
        ubicacion.y = other.ubicacion.y;
        ubicacion.z = other.ubicacion.z;
        ubicacion.w = other.ubicacion.w;
        this.normal.set(other.normal);
    }

    public QVertice clone() {
        QVertice result = new QVertice(ubicacion.x, ubicacion.y, ubicacion.z, ubicacion.w, u, v);
        result.normal.set(normal);
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

    public int[] getListaHuesosIds() {
        return listaHuesosIds;
    }

    public void setListaHuesosIds(int[] listaHuesosIds) {
        this.listaHuesosIds = listaHuesosIds;
    }

    public QVertice multiply(float valor) {
        this.ubicacion.multiply(valor);
        normal.multiply(valor);
        return this;
    }

    public QVertice add(float valor) {
        this.ubicacion.add(valor);
        normal.add(valor);
        return this;
    }

    public static QVertice promediar(QVertice... vert) {

        QVertice ve = new QVertice();
        for (QVertice v : vert) {
            ve.ubicacion.x += v.ubicacion.x;
            ve.ubicacion.y += v.ubicacion.y;
            ve.ubicacion.z += v.ubicacion.z;
            ve.u += v.u;
            ve.v += v.v;
            ve.normal.add(v.normal);
        }

        //promedia 
        ve.ubicacion.x /= vert.length;
        ve.ubicacion.y /= vert.length;
        ve.ubicacion.z /= vert.length;
        ve.u /= vert.length;
        ve.v /= vert.length;
        ve.normal.multiply(1.0f / vert.length);
        return ve;
    }

    public static QVertice promediarCatmullClark(QVertice... vert) {
        QVertice ve = new QVertice();
        for (QVertice v : vert) {
            ve.ubicacion.x += v.ubicacion.x;
            ve.ubicacion.y += v.ubicacion.y;
            ve.ubicacion.z += v.ubicacion.z;
            ve.u += v.u;
            ve.v += v.v;
            ve.normal.add(v.normal);
        }

        //promedia 
        ve.ubicacion.x /= vert.length;
        ve.ubicacion.y /= vert.length;
        ve.ubicacion.z /= vert.length;
        ve.u /= vert.length;
        ve.v /= vert.length;
        ve.normal.multiply(1.0f / vert.length);

        //modifica el nuevo vertice para que se eleve y suavice la supervicie
        double escala = 1.0f / (Math.sqrt(ve.ubicacion.x * ve.ubicacion.x + ve.ubicacion.y * ve.ubicacion.y + ve.ubicacion.z * ve.ubicacion.z));
        ve.ubicacion.set(ve.ubicacion.getVector3().multiply((float) escala), 1);
        return ve;
    }

}
