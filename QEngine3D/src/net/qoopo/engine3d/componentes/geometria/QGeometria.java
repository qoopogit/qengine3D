package net.qoopo.engine3d.componentes.geometria;

import java.util.Arrays;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.primitivas.QLinea;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;

public class QGeometria extends QComponente {

    public static final int GEOMETRY_TYPE_MESH = 0;//malla
    public static final int GEOMETRY_TYPE_MATH = 1;// funcion matematica
    public static final int GEOMETRY_TYPE_PATH = 2;// ruta
    public static final int GEOMETRY_TYPE_WIRE = 3; //alambre (formado por triangulos)
    public static final int GEOMETRY_TYPE_SEGMENT = 4; // alambre por lineas

    public int tipo = GEOMETRY_TYPE_MESH;
    public String nombre = "";//usada para identificar los objetos cargados , luego la eliminamos
    public QVertice[] vertices = new QVertice[0];
    public QPrimitiva[] primitivas = new QPrimitiva[0];

    public QGeometria() {
        tipo = GEOMETRY_TYPE_MESH;
    }

    public QGeometria(int type) {
        this.tipo = type;
    }

    public void eliminarVertice(int indice) {
        System.arraycopy(vertices, indice + 1, vertices, indice, vertices.length - 1 - indice);
    }

    public void eliminarPoligono(int indice) {
        System.arraycopy(primitivas, indice + 1, primitivas, indice, primitivas.length - 1 - indice);
    }

    public void eliminarVertice(QVertice vertice) {

    }

    public QVertice agregarVertice() {
        QVertice nuevo = new QVertice();
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVertice vertice) {
        QVertice nuevo = vertice.clone();
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector3 posicion) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z);
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector4 posicion) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z, posicion.w);
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector3 posicion, float u, float v) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z, 1, u, v);
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector4 posicion, float u, float v) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z, posicion.w, u, v);
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(float x, float y, float z) {
        QVertice nuevo = new QVertice(x, y, z, 1);
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(float x, float y, float z, float u, float v) {
        QVertice nuevo = new QVertice(x, y, z, 1, u, v);
        vertices = Arrays.copyOf(vertices, vertices.length + 1);
        vertices[vertices.length - 1] = nuevo;
        return nuevo;
    }

    public QPoligono agregarPoligono() {
        QPoligono nuevo = new QPoligono(this);
        primitivas = Arrays.copyOf(primitivas, primitivas.length + 1);
        primitivas[primitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QLinea agregarLinea(int... vertices) throws Exception {
        validarVertices(vertices);
        QLinea nuevo = new QLinea(this, vertices);
        primitivas = Arrays.copyOf(primitivas, primitivas.length + 1);
        primitivas[primitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QLinea agregarLinea(QMaterial material, int... vertices) throws Exception {
        validarVertices(vertices);
        QLinea nuevo = new QLinea(this, vertices);
        nuevo.material = material;
        primitivas = Arrays.copyOf(primitivas, primitivas.length + 1);
        primitivas[primitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QPoligono agregarPoligono(int... vertices) throws Exception {
        validarVertices(vertices);
        QPoligono nuevo = new QPoligono(this, vertices);
        primitivas = Arrays.copyOf(primitivas, primitivas.length + 1);
        primitivas[primitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QPoligono agregarPoligono(QMaterial material, int... vertices) throws Exception {
        validarVertices(vertices);
        QPoligono nuevo = new QPoligono(this, vertices);
        nuevo.material = material;
        primitivas = Arrays.copyOf(primitivas, primitivas.length + 1);
        primitivas[primitivas.length - 1] = nuevo;
        return nuevo;
    }

    @Override
    public QGeometria clone() {
        QGeometria nuevo = new QGeometria(this.tipo);
        for (QVertice current : vertices) {
            nuevo.agregarVertice(current.ubicacion.x, current.ubicacion.y, current.ubicacion.z).normal = current.normal.clone();
        }
        for (QPrimitiva face : primitivas) {
            if (face instanceof QPoligono) {
                QPoligono poligono = nuevo.agregarPoligono();
                poligono.setVertices(Arrays.copyOf(face.listaVertices, face.listaVertices.length));
                poligono.material = face.material;
                poligono.setNormal(((QPoligono) face).getNormal());
                poligono.setSmooth(((QPoligono) face).isSmooth());
            }
        }
        return nuevo;
    }

    public void destroy() {
        try {
            for (QPrimitiva face : primitivas) {
                face.geometria = null;
            }
        } catch (Exception e) {

        }
        primitivas = null;
    }

    @Override
    public void destruir() {
        vertices = null;
        primitivas = null;
    }

    private void validarVertices(int... vertices) throws Exception {
        for (int i : vertices) {
            if (i > this.vertices.length) {
                throw new Exception("Se esta agregando indices que no existen ");
            }
        }
    }

}
