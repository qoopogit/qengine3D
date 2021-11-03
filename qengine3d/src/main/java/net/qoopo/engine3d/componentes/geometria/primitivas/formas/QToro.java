/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.generador.QGenerador;

/**
 *
 * @author alberto
 */
public class QToro
        extends QForma //        extends QMalla 
{

    private float radio1;
    private float radio2;
    private int secciones = 36;
    private int secciones2 = 18;

    public QToro() {
//        super(QMalla.EJE_Y, false, 2 * QMath.PI, 2 * QMath.PI, 36,18);
        nombre = "Toro";
        radio1 = 1;
        radio2 = 0.5f;
        secciones = 36;
        secciones2 = secciones;
        material = new QMaterialBas("Toro");

        construir();
    }

    public QToro(float radio1, float radio2) {
//        super(QMalla.EJE_Y, false,  2 * QMath.PI, 2 * QMath.PI, 36,18);
        nombre = "Toro";
        this.radio1 = radio1;
        this.radio2 = radio2;
        secciones = 36;
        secciones2 = secciones;
        material = new QMaterialBas("Toro");
        construir();
    }

    public QToro(float radio1, float radio2, int secciones) {
//        super(QMalla.EJE_Y, false, 2 * QMath.PI, 2 * QMath.PI, secciones, secciones);
        nombre = "Toro";
        this.radio1 = radio1;
        this.radio2 = radio2;
        this.secciones = secciones;
        secciones2 = secciones;
        material = new QMaterialBas("Toro");
        construir();
    }

    public QToro(float radio1, float radio2, int secciones, int secciones2) {
//        super(QMalla.EJE_Y, false,  2 * QMath.PI, 2 * QMath.PI,secciones, secciones2);
        nombre = "Toro";
        this.radio1 = radio1;
        this.radio2 = radio2;
        this.secciones = secciones;
        this.secciones2 = secciones2;
        material = new QMaterialBas("Toro");
        construir();
    }

    public void construir() {
        eliminarDatos();

        //primero el circulo
        QVertice inicial = this.agregarVertice(0, radio2, 0); //primer vertice
        QVector3 vector = new QVector3(inicial.ubicacion.x, inicial.ubicacion.y, inicial.ubicacion.z);
        float angulo = 360.0f / secciones2;
        QVector3 tmp = vector;
        //se crean las secciones menos la ultima
        for (int i = 1; i < secciones2; i++) {
            tmp = tmp.rotateZ((float) Math.toRadians(angulo));
            this.agregarVertice(tmp.x, tmp.y, tmp.z, 0, 1.0f - (1.0f / secciones2 * (i - 1)));
        }

        //para la ultima seccion se debe unir con los primeros puntos  
//        this.agregarVertice(inicial.x, inicial.y, inicial.z);
        //luego movemos los puntos al radio 1        
        for (QVertice vertice : this.vertices) {
            vertice.ubicacion.x -= radio1;
        }

        QGenerador.generarRevolucion(this, secciones, true, true, false, false);
        QUtilNormales.calcularNormales(this);
        //el objeto es suavizado
        QMaterialUtil.suavizar(this, true);
        QMaterialUtil.aplicarMaterial(this, material);

    }
//    @Override
//    public void construir() {
//        eliminarDatos();
//
////        QMaterialBas material = new QMaterialBas("Toro_material   ");
////        int numVertices = (secciones + 1) * (secciones + 1);
////        int numIndices = 2 * secciones * secciones*3;
////        float[] vertices = new float[numVertices * 3];
////        float[] normals = new float[numVertices * 3];
////        int[] indices = new int[numIndices];
//        for (int j = 0; j <= secciones; ++j) {
//            float largeRadiusAngle = (float) (2.0f * Math.PI * j / secciones);
//
//            for (int i = 0; i <= secciones; ++i) {
//                float smallRadiusAngle = (float) (2.0f * Math.PI * i / secciones);
//                float xNorm = (radio2 * (float) Math.sin(smallRadiusAngle)) * (float) Math.sin(largeRadiusAngle);
//                float x = (radio1 + radio2 * (float) Math.sin(smallRadiusAngle)) * (float) Math.sin(largeRadiusAngle);
//                float yNorm = (radio2 * (float) Math.sin(smallRadiusAngle)) * (float) Math.cos(largeRadiusAngle);
//                float y = (radio1 + radio2 * (float) Math.sin(smallRadiusAngle)) * (float) Math.cos(largeRadiusAngle);
//                float zNorm = radio2 * (float) Math.cos(smallRadiusAngle);
//                float z = zNorm;
////				normals[vertIndex] = xNorm * normLen;
////				vertices[vertIndex++] = x;
////				normals[vertIndex] = yNorm * normLen;
////				vertices[vertIndex++] = y;
////				normals[vertIndex] = zNorm * normLen;
////				vertices[vertIndex++] = z;
//                this.agregarVertice(x, y, z);
//
//                if (i > 0 && j > 0) {
//                    int a = (secciones + 1) * j + i;
//                    int b = (secciones + 1) * j + i - 1;
//                    int c = (secciones + 1) * (j - 1) + i - 1;
//                    int d = (secciones + 1) * (j - 1) + i;
//
////                    indices[index++] = a;
////                    indices[index++] = c;
////                    indices[index++] = b;
//                    this.agregarPoligono(a, c, b);
////                    indices[index++] = a;
////                    indices[index++] = d;
////                    indices[index++] = c;
//                    this.agregarPoligono(a, d, c);
//                }
//            }
//        }
//
//        QUtilNormales.calcularNormales(this);
//        //el objeto es suavizado
//
//        QMaterialUtil.suavizar(this, true);
//        QMaterialUtil.aplicarMaterial(this, material);
//
//    }
//   
//    @Override
//    public void construir() {
//        eliminarDatos();
//
//        super.setAncho(secciones);
//        super.setLargo(secciones2);
//        super.construir();
//
//        //transforms the grid domain into a tube          
//        for (QVertice vertice : vertices) {
//            calculateTube(vertice, radio2);
//        }
////        for (int i = 0; i < transformables.size(); i++) {
////            Triangle tri = (Triangle) transformables.get(i);
////            calculateTube(tri.v1, radio2);
////            calculateTube(tri.v2, radio2);
////            calculateTube(tri.v3, radio2);
////        }
//
//        //translates the tube domain by r1
////        this.translate(new Vertex(radio1, 0, 0));
//        for (QVertice vertice : vertices) {
//            vertice.ubicacion.x += radio1;
//        }
//
//        //transforms the tube into a torus
////        for (int j = 0; j < transformables.size(); j++) {
////            Triangle tri = (Triangle) transformables.get(j);
////            calculateTorus(tri.v1);
////            calculateTorus(tri.v2);
////            calculateTorus(tri.v3);
////        }
//        for (QVertice vertice : vertices) {
//            calculateTorus(vertice);
//        }
//
//        //translates the tube domain by r1
////        this.translate(new Vertex(radio1, 0, 0));
//        for (QVertice vertice : vertices) {
//            vertice.ubicacion.x -= radio1;
//        }
//
//        QUtilNormales.calcularNormales(this);
//        //el objeto es suavizado
//
//        QMaterialUtil.suavizar(this, true);
//        QMaterialUtil.aplicarMaterial(this, material);
//
//    }

//    /**
//     * Calculates the firts phase of torus creation, that of a tube It
//     * transforms only the x and z coordinates with the circle equation creating
//     * a tube in the y direction
//     *
//     * @param v vertex to be transformed
//     * @param r2 secondary radius
//     */
//    private void calculateTube(QVertice v, float r2) {
//        v.ubicacion.z = r2 * QMath.sin(v.ubicacion.x);
//        v.ubicacion.x = r2 * QMath.cos(v.ubicacion.x);
//    }
//
//    /**
//     * transforms the tube vertexes into a torus by applying this time the
//     * circle equation only to the x and y coordinates
//     *
//     * @param v vertex to be transformed
//     */
//    private void calculateTorus(QVertice v) {
//        float x;
//        x = v.ubicacion.x;
//        v.ubicacion.x = x * QMath.cos(v.ubicacion.y);
//        v.ubicacion.y = x * QMath.sin(v.ubicacion.y);
//    }
    public float getRadio1() {
        return radio1;
    }

    public void setRadio1(float radio1) {
        this.radio1 = radio1;
    }

    public float getRadio2() {
        return radio2;
    }

    public void setRadio2(float radio2) {
        this.radio2 = radio2;
    }

    public int getSecciones() {
        return secciones;
    }

    public void setSecciones(int secciones) {
        this.secciones = secciones;
    }

}
