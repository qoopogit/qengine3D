package net.qoopo.engine3d.componentes.geometria.primitivas;

import java.io.Serializable;
import java.util.Arrays;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.material.QMaterial;

public class QPrimitiva implements Serializable {

    public QGeometria geometria;
// la lista de vertices deben ser en el orden contrario a las agujas del reloj
    public int[] listaVertices = new int[0]; //lista de indices vertices

    public QMaterial material = new QMaterial();

    public QPrimitiva() {
    }

    public QPrimitiva(QGeometria parent) {
        this.geometria = parent;
    }

    public QPrimitiva(QGeometria parent, int... vertices) {
        this(parent);
        setVertices(vertices);
    }

    public void addVertex(int vertex) {
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = vertex;
    }

    public void setVertices(int[] vertices) {
        listaVertices = Arrays.copyOf(vertices, vertices.length);
    }

    @Override
    public String toString() {
        return "QPrimititva{" + "listaVertices=" + Arrays.toString(listaVertices) + '}';
    }

}
