package net.qoopo.engine3d.componentes.geometria;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.primitivas.QLinea;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QNicoEsfera;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;

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

    public QPoligono agregarPoligono(QMaterial material, boolean smooth, int... vertices) throws Exception {
        validarVertices(vertices);
        QPoligono nuevo = new QPoligono(this, vertices);
        nuevo.setSmooth(smooth);
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

    protected void eliminarDatos() {
        this.destroy();
        this.vertices = new QVertice[0];
        this.primitivas = new QPrimitiva[0];
    }

    public QGeometria suavizar() {
        QMaterialUtil.suavizar(this, true);
        return this;
    }

    public QGeometria desSuavizar() {
        QMaterialUtil.suavizar(this, false);
        return this;
    }

    /**
     * Infla los vertices de forma esferica
     *
     * @param radio
     * @return
     */
    public QGeometria inflar(float radio) {
        for (QVertice ve : this.vertices) {
            double escala = radio / (Math.sqrt(ve.ubicacion.x * ve.ubicacion.x + ve.ubicacion.y * ve.ubicacion.y + ve.ubicacion.z * ve.ubicacion.z));
            ve.ubicacion.set(ve.ubicacion.getVector3().multiply((float) escala), 1);
        }
        return this;
    }

    /**
     * Realiza una division de la superficie.La superficie debe estar formada
     * por triangulos
     *
     * @param veces Veces a dividir (debe ser mayor a 1)
     * @return
     */
    public QGeometria dividir(int veces) {
        for (int i = 1; i <= veces - 1; i++) {
            this.dividir();
        }
        return this;
    }

    /**
     * Realiza una division de la superficie.La superficie debe estar formada
     * por triangulos
     *
     * @return
     */
    public QGeometria dividir() {
        QVertice[] v = Arrays.copyOf(vertices, vertices.length);
        QPrimitiva[] p = Arrays.copyOf(primitivas, primitivas.length);
        eliminarDatos();
        int c = 0;
        try {
            for (QPrimitiva t : p) {
                switch (t.listaVertices.length) {
                    case 3:
                        //primero agrego los vertices originales del triangulo
                        agregarVertice(v[t.listaVertices[0]]);//0
                        agregarVertice(v[t.listaVertices[1]]);//1
                        agregarVertice(v[t.listaVertices[2]]);//2
                        // luego agrego los nuevos vertices
                        agregarVertice(QVertice.promediar(v[t.listaVertices[0]], v[t.listaVertices[1]])); //3
                        agregarVertice(QVertice.promediar(v[t.listaVertices[1]], v[t.listaVertices[2]])); //4
                        agregarVertice(QVertice.promediar(v[t.listaVertices[2]], v[t.listaVertices[0]])); //5
                        // ahora agrego las caras
                        agregarPoligono(t.material, c + 0, c + 3, c + 5);
                        agregarPoligono(t.material, c + 1, c + 4, c + 3);
                        agregarPoligono(t.material, c + 2, c + 5, c + 4);
                        agregarPoligono(t.material, c + 3, c + 4, c + 5);
                        //recorro los vertices agregados
                        c += 6;
                        break;
                    case 4:
                        //primero agrego los vertices originales del triangulo
                        agregarVertice(v[t.listaVertices[0]]);//0
                        agregarVertice(v[t.listaVertices[1]]);//1
                        agregarVertice(v[t.listaVertices[2]]);//2
                        agregarVertice(v[t.listaVertices[3]]);//3

                        //vertice en el centro
                        agregarVertice(QVertice.promediar(v[t.listaVertices[0]], v[t.listaVertices[1]], v[t.listaVertices[2]], v[t.listaVertices[3]]));//3

                        // luego agrego los nuevos vertices en los lados
                        agregarVertice(QVertice.promediar(v[t.listaVertices[0]], v[t.listaVertices[1]])); //4
                        agregarVertice(QVertice.promediar(v[t.listaVertices[1]], v[t.listaVertices[2]])); //5
                        agregarVertice(QVertice.promediar(v[t.listaVertices[2]], v[t.listaVertices[3]])); //6
                        agregarVertice(QVertice.promediar(v[t.listaVertices[3]], v[t.listaVertices[0]])); //7
                        // ahora agrego las caras
                        agregarPoligono(t.material, c + 0, c + 5, c + 4, c + 8);
                        agregarPoligono(t.material, c + 5, c + 1, c + 6, c + 4);
                        agregarPoligono(t.material, c + 4, c + 6, c + 2, c + 7);
                        agregarPoligono(t.material, c + 8, c + 4, c + 7, c + 3);
                        //recorro los vertices agregados
                        c += 9;
                        break;
                    default:
                        //agrega los mismos vertices sin variar nada
                        int[] antVert = Arrays.copyOf(t.listaVertices, t.listaVertices.length);
                        int ii = 0;
                        for (int i : t.listaVertices) {
                            agregarVertice(v[i]);
                            antVert[ii] = c;
                            ii++;
                            c++;
                        }
                        agregarPoligono(t.material, antVert);
                        break;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(QNicoEsfera.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

    /**
     * Realiza una division de la superficie.La superficie debe estar formada
     * por triangulos
     *
     * @param veces Veces a dividir (debe ser mayor a 1)
     * @return
     */
    public QGeometria dividirCatmullClark(int veces) {
        for (int i = 1; i <= veces - 1; i++) {
            this.dividirCatmullClark();
        }
        return this;
    }

    /**
     * Realiza una division de la superficie.La superficie debe estar formada
     * por triangulos
     *
     *
     * http://graphics.stanford.edu/courses/cs468-10-fall/LectureSlides/10_Subdivision.pdf
     *
     * @return
     */
    public QGeometria dividirCatmullClark() {
        QVertice[] v = Arrays.copyOf(vertices, vertices.length);
        QPrimitiva[] p = Arrays.copyOf(primitivas, primitivas.length);

        eliminarDatos();
        int c = 0;
        try {
//            float f1 = (float) (3.0f / 8.0f + 1.0f / 4.0f * Math.cos(QMath.TWO_PI));
//            float factor = (3.0f / 8.0f) + f1 * f1;
            float factor = 1.0f;
            for (QPrimitiva t : p) {
                switch (t.listaVertices.length) {
                    case 3:
                        //primero agrego los vertices originales del triangulo
                        agregarVertice(v[t.listaVertices[0]].multiply(factor));//0
                        agregarVertice(v[t.listaVertices[1]].multiply(factor));//1
                        agregarVertice(v[t.listaVertices[2]].multiply(factor));//2
                        // luego agrego los nuevos vertices
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[0]], v[t.listaVertices[1]])); //3
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[1]], v[t.listaVertices[2]])); //4
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[2]], v[t.listaVertices[0]])); //5
                        // ahora agrego las caras
                        agregarPoligono(t.material, c + 0, c + 3, c + 5);
                        agregarPoligono(t.material, c + 1, c + 4, c + 3);
                        agregarPoligono(t.material, c + 2, c + 5, c + 4);
                        agregarPoligono(t.material, c + 3, c + 4, c + 5);
                        //recorro los vertices agregados
                        c += 6;
                        break;
                    case 4:
                        //primero agrego los vertices originales del triangulo
                        agregarVertice(v[t.listaVertices[0]]);//0
                        agregarVertice(v[t.listaVertices[1]]);//1
                        agregarVertice(v[t.listaVertices[2]]);//2
                        agregarVertice(v[t.listaVertices[3]]);//3

                        //vertice en el centro
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[0]], v[t.listaVertices[1]], v[t.listaVertices[2]], v[t.listaVertices[3]]));//3

                        // luego agrego los nuevos vertices en los lados
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[0]], v[t.listaVertices[1]])); //4
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[1]], v[t.listaVertices[2]])); //5
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[2]], v[t.listaVertices[3]])); //6
                        agregarVertice(QVertice.promediarCatmullClark(v[t.listaVertices[3]], v[t.listaVertices[0]])); //7
                        // ahora agrego las caras
                        agregarPoligono(t.material, c + 0, c + 5, c + 4, c + 8);
                        agregarPoligono(t.material, c + 5, c + 1, c + 6, c + 4);
                        agregarPoligono(t.material, c + 4, c + 6, c + 2, c + 7);
                        agregarPoligono(t.material, c + 8, c + 4, c + 7, c + 3);
                        //recorro los vertices agregados
                        c += 9;
                        break;
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(QNicoEsfera.class.getName()).log(Level.SEVERE, null, ex);
        }
        return this;
    }

}
