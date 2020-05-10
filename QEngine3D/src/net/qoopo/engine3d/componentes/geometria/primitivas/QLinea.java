package net.qoopo.engine3d.componentes.geometria.primitivas;

import java.io.Serializable;
import java.util.Arrays;
import net.qoopo.engine3d.componentes.geometria.QGeometria;

public class QLinea extends QPrimitiva implements Comparable<QLinea>, Serializable {

    public QLinea(QGeometria parent) {
        super(parent);
    }

    public QLinea(QGeometria parent, int... vertices) {
        this(parent);
        setVertices(vertices);
    }

    @Override
    public String toString() {
        return "QLinea{" + "listaVertices=" + Arrays.toString(listaVertices) + '}';
    }

    @Override
    public int compareTo(QLinea other) {
//        return Float.valueOf(vFin.z).compareTo(other.vFin.z);
        return Float.valueOf(geometria.listaVertices[listaVertices[0]].ubicacion.z).compareTo(other.geometria.listaVertices[listaVertices[0]].ubicacion.z);
//        return Float.valueOf(other.vFin.z).compareTo(vFin.z);
    }

}
