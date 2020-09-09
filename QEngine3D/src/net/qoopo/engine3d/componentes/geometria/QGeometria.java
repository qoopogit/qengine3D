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
    public QVertice[] listaVertices = new QVertice[0];
    public QPrimitiva[] listaPrimitivas = new QPrimitiva[0];

    public QGeometria() {
        tipo = GEOMETRY_TYPE_MESH;
    }

    public QGeometria(int type) {
        this.tipo = type;
    }

    public void eliminarVertice(int indice) {
        System.arraycopy(listaVertices, indice + 1, listaVertices, indice, listaVertices.length - 1 - indice);
    }

    public void eliminarPoligono(int indice) {
        System.arraycopy(listaPrimitivas, indice + 1, listaPrimitivas, indice, listaPrimitivas.length - 1 - indice);
    }

    public void eliminarVertice(QVertice vertice) {

    }

    public QVertice agregarVertice() {
        QVertice nuevo = new QVertice();
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector3 posicion) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z);
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector4 posicion) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z, posicion.w);
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector3 posicion, float u, float v) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z, 1, u, v);
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(QVector4 posicion, float u, float v) {
        QVertice nuevo = new QVertice(posicion.x, posicion.y, posicion.z, posicion.w, u, v);
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(float x, float y, float z) {
        QVertice nuevo = new QVertice(x, y, z, 1);
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = nuevo;
        return nuevo;
    }

    public QVertice agregarVertice(float x, float y, float z, float u, float v) {
        QVertice nuevo = new QVertice(x, y, z, 1, u, v);
        listaVertices = Arrays.copyOf(listaVertices, listaVertices.length + 1);
        listaVertices[listaVertices.length - 1] = nuevo;
        return nuevo;
    }

    public QPoligono agregarPoligono() {
        QPoligono nuevo = new QPoligono(this);
        listaPrimitivas = Arrays.copyOf(listaPrimitivas, listaPrimitivas.length + 1);
        listaPrimitivas[listaPrimitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QLinea agregarLinea(int... vertices) throws Exception {
        validarVertices(vertices);
        QLinea nuevo = new QLinea(this, vertices);
        listaPrimitivas = Arrays.copyOf(listaPrimitivas, listaPrimitivas.length + 1);
        listaPrimitivas[listaPrimitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QLinea agregarLinea(QMaterial material, int... vertices) throws Exception {
        validarVertices(vertices);
        QLinea nuevo = new QLinea(this, vertices);
        nuevo.material = material;
        listaPrimitivas = Arrays.copyOf(listaPrimitivas, listaPrimitivas.length + 1);
        listaPrimitivas[listaPrimitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QPoligono agregarPoligono(int... vertices) throws Exception {
        validarVertices(vertices);
        QPoligono nuevo = new QPoligono(this, vertices);
        listaPrimitivas = Arrays.copyOf(listaPrimitivas, listaPrimitivas.length + 1);
        listaPrimitivas[listaPrimitivas.length - 1] = nuevo;
        return nuevo;
    }

    public QPoligono agregarPoligono(QMaterial material, int... vertices) throws Exception {
        validarVertices(vertices);
        QPoligono nuevo = new QPoligono(this, vertices);
        nuevo.material = material;
        listaPrimitivas = Arrays.copyOf(listaPrimitivas, listaPrimitivas.length + 1);
        listaPrimitivas[listaPrimitivas.length - 1] = nuevo;
        return nuevo;
    }

//    public QPoligono agregarPoligono(QPoligono.UVCoordinate[] newUV, int... vertices) {
//        QPoligono nuevo = new QPoligono(this, vertices);
//        nuevo.setUV(newUV);
//        listaPrimitivas = Arrays.copyOf(listaPrimitivas, listaPrimitivas.length + 1);
//        listaPrimitivas[listaPrimitivas.length - 1] = nuevo;
//
//        return nuevo;
//    }
//
//    public QPoligono agregarPoligono(QMaterial material, QPoligono.UVCoordinate[] newUV, int... vertices) {
//        QPoligono nuevo = new QPoligono(this, vertices);
//        nuevo.material = material;
//        nuevo.setUV(newUV);
//        listaPrimitivas = Arrays.copyOf(listaPrimitivas, listaPrimitivas.length + 1);
//        listaPrimitivas[listaPrimitivas.length - 1] = nuevo;
//
//        return nuevo;
//    }
    @Override
    public QGeometria clone() {
        QGeometria nuevo = new QGeometria(this.tipo);
//        if (this.entidad != null) {
//            nuevo.entidad = this.entidad.clone();
//        }
        for (QVertice current : listaVertices) {
            nuevo.agregarVertice(current.ubicacion.x, current.ubicacion.y, current.ubicacion.z).normal = current.normal.clone();
        }
        for (QPrimitiva face : listaPrimitivas) {
            if (face instanceof QPoligono) {
                QPoligono poligono = nuevo.agregarPoligono();
                poligono.setVertices(Arrays.copyOf(face.listaVertices, face.listaVertices.length));
                poligono.material = face.material;
                poligono.normal = ((QPoligono) face).normal;
                poligono.smooth = ((QPoligono) face).smooth;
//                poligono.uv = ((QPoligono) face).uv.clone();
            }
        }
        return nuevo;
    }

    public void destroy() {
        try {
            for (QPrimitiva face : listaPrimitivas) {
                face.geometria = null;
            }
        } catch (Exception e) {

        }
        listaPrimitivas = null;
    }

    @Override
    public void destruir() {
        listaVertices = null;
        listaPrimitivas = null;
    }

    private void validarVertices(int... vertices) throws Exception {
        for (int i : vertices) {
            if (i > this.listaVertices.length) {
                throw new Exception("Se esta agregando indices que no existen ");
            }
        }
    }

}
