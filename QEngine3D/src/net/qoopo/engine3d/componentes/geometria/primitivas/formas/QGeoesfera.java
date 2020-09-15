/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QUtilNormales;

/**
 * Geoesfera o Icoesfera
 *
 * @author alberto
 */
public class QGeoesfera extends QForma {

    private float radio;
    private int subDivisiones = 3;

    private static final float H_ANGLE = QMath.PI / 180 * 72;    // 72 degree = 360 / 5
    private static final float V_ANGLE = QMath.atan(1.0f / 2);  // elevation = 26.565 degree

    public QGeoesfera() {
        nombre = "Esfera";
        material = new QMaterialBas("Esfera");
        radio = 1;
        construir();
    }

    public QGeoesfera(float radio) {
        nombre = "Esfera";
        material = new QMaterialBas("Esfera");
        this.radio = radio;
        construir();
    }

    public QGeoesfera(float radio, int divisiones) {
        nombre = "Esfera";
        material = new QMaterialBas("Esfera");
        this.radio = radio;
        this.subDivisiones = divisiones;
        construir();
    }

//    private boolean validaVertice(String nombre, int vertice) {
//        if (this.vertices.length < vertice) {
//            System.out.println(nombre + " fuera de rango " + vertice + " limite " + this.vertices.length);
//            return false;
//        }
//        return true;
//    }
    /**
     * Construye una esfera http://www.songho.ca/opengl/gl_sphere.html
     */
    @Override
    public void construir() {
        eliminarDatos();
        //paso 1.- generar el icosaedro origen
        crearIcosaedro();
        //paso 2 
        armarTriangulos();
        //paso 3. - realizar la division del icosaedro
//        dividirIcosaedro(subDivisiones);
        // ahora armamos las caras
//        System.out.println("Esfera generada { vertices: " + this.vertices.length + " , triangulos:" + this.primitivas.length + "}");
//        System.out.println("numVertices=" + numVertices);
        QUtilNormales.calcularNormales(this);
        //el objeto es suavizado
//        QMaterialUtil.suavizar(this, true);

//        for (QPrimitiva face : this.primitivas) {
//            ((QPoligono) face).smooth = true;
//        }
        QMaterialUtil.aplicarMaterial(this, material);
    }

    private void armarTriangulos() {

        // float S_STEP = 1 / 11.0f;         // horizontal texture step
        // float T_STEP = 1 / 3.0f;          // vertical texture step
        float S_STEP = 186 / 2048.0f;     // horizontal texture step
        float T_STEP = 322 / 1024.0f;     // vertical texture step

        QVector3 vertice = new QVector3();                             // vertex
        QVector3 normal = new QVector3();                             // normal
        float scale;                            // scale factor for normalization

        // smooth icosahedron has 14 non-shared (0 to 13) and
        // 8 shared vertices (14 to 21) (total 22 vertices)
        //  00  01  02  03  04          //
        //  /\  /\  /\  /\  /\          //
        // /  \/  \/  \/  \/  \         //
        //10--14--15--16--17--11        //
        // \  /\  /\  /\  /\  /\        //
        //  \/  \/  \/  \/  \/  \       //
        //  12--18--19--20--21--13      //
        //   \  /\  /\  /\  /\  /       //
        //    \/  \/  \/  \/  \/        //
        //    05  06  07  08  09        //
        // add 14 non-shared vertices first (index from 0 to 13)
//         addVertex(tmpVertices[0], tmpVertices[1], tmpVertices[2]);      // v0 (top)
//    addNormal(0, 0, 1);
//    addTexCoord(S_STEP, 0);
        agregarVertice(vertices[0].ubicacion, S_STEP, 0);

//    addVertex(tmpVertices[0], tmpVertices[1], tmpVertices[2]);      // v1
//    addNormal(0, 0, 1);
//    addTexCoord(S_STEP * 3, 0);
        agregarVertice(vertices[0].ubicacion, S_STEP * 3, 0);

//    addVertex(tmpVertices[0], tmpVertices[1], tmpVertices[2]);      // v2
//    addNormal(0, 0, 1);
//    addTexCoord(S_STEP * 5, 0);
        agregarVertice(vertices[0].ubicacion, S_STEP * 5, 0);

//    addVertex(tmpVertices[0], tmpVertices[1], tmpVertices[2]);      // v3
//    addNormal(0, 0, 1);
//    addTexCoord(S_STEP * 7, 0);
        agregarVertice(vertices[0].ubicacion, S_STEP * 7, 0);

//    addVertex(tmpVertices[0], tmpVertices[1], tmpVertices[2]);      // v4
//    addNormal(0, 0, 1);
//    addTexCoord(S_STEP * 9, 0);
        agregarVertice(vertices[0].ubicacion, S_STEP * 9, 0);

//    addVertex(tmpVertices[33], tmpVertices[34], tmpVertices[35]);   // v5 (bottom)
//    addNormal(0, 0, -1);
//    addTexCoord(S_STEP * 2, T_STEP * 3);
        agregarVertice(vertices[11].ubicacion, S_STEP * 2, T_STEP * 3);

//        addVertex(tmpVertices[33], tmpVertices[34], tmpVertices[35]);   // v6
//        addNormal(0, 0, -1);
//        addTexCoord(S_STEP * 4, T_STEP * 3);
        agregarVertice(vertices[11].ubicacion, S_STEP * 4, T_STEP * 3);

//        addVertex(tmpVertices[33], tmpVertices[34], tmpVertices[35]);   // v7
//        addNormal(0, 0, -1);
//        addTexCoord(S_STEP * 6, T_STEP * 3);
        agregarVertice(vertices[11].ubicacion, S_STEP * 6, T_STEP * 3);

//        addVertex(tmpVertices[33], tmpVertices[34], tmpVertices[35]);   // v8
//        addNormal(0, 0, -1);
//        addTexCoord(S_STEP * 8, T_STEP * 3);
        agregarVertice(vertices[11].ubicacion, S_STEP * 8, T_STEP * 3);

//        addVertex(tmpVertices[33], tmpVertices[34], tmpVertices[35]);   // v9
//        addNormal(0, 0, -1);
//        addTexCoord(S_STEP * 10, T_STEP * 3);
        agregarVertice(vertices[11].ubicacion, S_STEP * 10, T_STEP * 3);
    }

    /**
     *
     */
    private void crearIcosaedro() {

        try {
            int i1, i2;                             // indices
            float x, y, z, x2, z2, radio_Cos;                            // coords
            float hAngle1 = -QMath.PI / 2 - H_ANGLE / 2;  // start from -126 deg at 1st row
            float hAngle2 = -QMath.PI / 2;                // start from -90 deg at 2nd row
            // the first top vertex at (0, 0, r)
//        vertices[0] = 0;
//        vertices[1] = 0;
//        vertices[2] = radius;
            agregarVertice(0, 0, radio);
//inicializamos con 12 vertices (menos el primero ya agregado), luego calculamos sus posiciones
            for (int i = 1; i <= 11; i++) {
                agregarVertice(0, 0, 0);
            }
            y = radio * QMath.sin(V_ANGLE);            // elevaton
// compute 10 vertices at 1st and 2nd rows
            for (int i = 1; i <= 5; ++i) {
                i1 = i;         // index for 1st row
                i2 = (i + 5);   // index for 2nd row

                radio_Cos = radio * QMath.cos(V_ANGLE);            // length on xy plane

                x = radio_Cos * QMath.sin(hAngle1);             // r * cos(u) * sin(v)
                z = radio_Cos * QMath.cos(hAngle1);             // r * cos(u) * cos(v)

                x2 = radio_Cos * QMath.sin(hAngle2);             // r * cos(u) * sin(v)
                z2 = radio_Cos * QMath.cos(hAngle2);             // r * cos(u) * cos(v)

//            vertices[i1] = xy * cosf(hAngle1);      // x
//            vertices[i2] = xy * cosf(hAngle2);
//            vertices[i1 + 1] = xy * sinf(hAngle1);  // y
//            vertices[i2 + 1] = xy * sinf(hAngle2);
//            vertices[i1 + 2] = z;                   // z
//            vertices[i2 + 2] = -z;
                vertices[i1].ubicacion.set(radio_Cos * QMath.cos(hAngle1), radio_Cos * QMath.sin(hAngle1), y, 1);
                vertices[i2].ubicacion.set(radio_Cos * QMath.cos(hAngle2), radio_Cos * QMath.sin(hAngle2), -y, 1);

//            vertices[i1].ubicacion.set(x, y, z, 1);
//            vertices[i2].ubicacion.set(x2, -y, z2, 1);
// next horizontal angles
                hAngle1 += H_ANGLE;
                hAngle2 += H_ANGLE;
            }   // the last bottom vertex at (0, 0, -r)
//        i1 = 11 * 3;
//        vertices[i1] = 0;
//        vertices[i1 + 1] = 0;
//        vertices[i1 + 2] = -radius;
            vertices[11].ubicacion.set(0, 0, -radio, 1);
// las caras superiores
            agregarPoligono(0, 1, 2);
            agregarPoligono(0, 2, 3);
            agregarPoligono(0, 3, 4);
            agregarPoligono(0, 4, 5);
            agregarPoligono(0, 5, 1);
//las caras del centro
            agregarPoligono(1, 6, 2);
            agregarPoligono(2, 6, 7);
//
            agregarPoligono(2, 7, 3);
            agregarPoligono(3, 7, 8);
//
            agregarPoligono(3, 8, 4);
            agregarPoligono(4, 8, 9);
//
            agregarPoligono(4, 9, 5);
            agregarPoligono(5, 9, 10);
//
            agregarPoligono(5, 10, 1);
            agregarPoligono(1, 10, 6);
//
// las caras inferiores
            agregarPoligono(11, 7, 6);
            agregarPoligono(11, 8, 7);
            agregarPoligono(11, 9, 8);
            agregarPoligono(11, 10, 9);
            agregarPoligono(11, 6, 10);
        } catch (Exception ex) {
            Logger.getLogger(QGeoesfera.class.getName()).log(Level.SEVERE, null, ex);
        }

    }


    /**
     * Divide el icosaedro original
     *
     * @param divisiones
     */
//    private void dividirIcosaedro(int subdivision) {
////        std::vector < float > tmpVertices;
////        std::vector < float > tmpIndices;
////        const float *v1, *v2, *v3;          // ptr to original vertices of a triangle
//        QVertice nuevo;
//        QVector3 v1,v2,v3;
//        QVector3 newV1=new QVector3(), newV2=new QVector3(), newV3=new QVector3(); // new vertex positions
//        int index;
//
//        QPrimitiva[] tmpIndices;
//        QVertice[] tmpVertices;
//
//// iterate all subdivision levels
//        for (int i = 1; i <= subdivision; ++i) {
//            // copy prev vertex/index arrays and clear
////            tmpVertices = vertices;
////            tmpIndices = indices;
////            vertices.clear();
////            indices.clear();
//
//            tmpIndices = this.primitivas;
//            index = 0;
//
//            // perform subdivision for each triangle
//            for (int j = 0; j < tmpIndices.length; j++ /* j += 3*/) {
//                // get 3 vertices of a triangle
//                v1 =  & tmpVertices[tmpIndices[j] * 3];
//                v2 =  & tmpVertices[tmpIndices[j + 1] * 3];
//                v3 =  & tmpVertices[tmpIndices[j + 2] * 3];
//
//                // compute 3 new vertices by spliting half on each edge
//                //         v1       
//                //        / \       
//                // newV1 *---* newV3
//                //      / \ / \     
//                //    v2---*---v3   
//                //       newV2      
//                computeHalfVertex(v1, v2, newV1);
//                computeHalfVertex(v2, v3, newV2);
//                computeHalfVertex(v1, v3, newV3);
//
//                // add 4 new triangles to vertex array
//                addVertices(v1, newV1, newV3);
//                addVertices(newV1, v2, newV2);
//                addVertices(newV1, newV2, newV3);
//                addVertices(newV3, newV2, v3);
//
//                // add indices of 4 new triangles
//                addIndices(index, index + 1, index + 2);
//                addIndices(index + 3, index + 4, index + 5);
//                addIndices(index + 6, index + 7, index + 8);
//                addIndices(index + 9, index + 10, index + 11);
//                index += 12;    // next index
//            }
//        }
//    }
    ///////////////////////////////////////////////////////////////////////////////
// find middle point of 2 vertices
// NOTE: new vertex must be resized, so the length is equal to the radius
///////////////////////////////////////////////////////////////////////////////
    private void computeHalfVertex(QVector3 v1, QVector3 v2, QVector3 newV) {
//    newV[0] = v1[0] + v2[0];    // x
//    newV[1] = v1[1] + v2[1];    // y
//    newV[2] = v1[2] + v2[2];    // z
        newV.x = v1.x + v2.x;
        newV.y = v1.y + v2.y;
        newV.z = v1.z + v2.z;

        float scale = radio / QMath.sqrt(newV.x * newV.x + newV.y * newV.y + newV.z * newV.z);
        newV.multiply(scale);
//    newV[0] *= scale;
//    newV[1] *= scale;
//    newV[2] *= scale;
    }

    public float getRadio() {
        return radio;
    }

    public void setRadio(float radio) {
        this.radio = radio;
    }

    public int getSubDivisiones() {
        return subDivisiones;
    }

    public void setSubDivisiones(int subDivisiones) {
        this.subDivisiones = subDivisiones;
    }

}
