/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util.generador;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;

/**
 * Permite generar automaticamente gemotrias con fórmulas
 *
 * @author aigarcia
 */
public class QGenerador {

    /**
     * Genera objetos de revolución El objeto pasado como parametro solo
     * contiene vértices que corresponden a media silueta del objeto
     *
     * @param objeto Objeto con los vertices
     * @param lados
     * @return
     */
    public static QGeometria generarRevolucion(QGeometria objeto, int lados) {
        return generarRevolucion(objeto, lados, false, false, false, false);
    }

    /**
     * Genera objetos de revolución El objeto pasado como parametro solo
     * contiene vértices que corresponden a media silueta del objeto
     *
     * @param objeto Objeto con los vertices
     * @param lados
     * @param cerrarFigura
     * @param tipotoro
     * @param suavizar marca las caras como suaves
     * @param suavizarTopes, marca como suave las caras superiores e inferiores,
     * se puede usar para los cilindros y conos
     * @return
     */
    public static QGeometria generarRevolucion(QGeometria objeto, int lados, boolean cerrarFigura, boolean tipotoro, boolean suavizar, boolean suavizarTopes) {
        float angulo = (float) Math.toRadians((float) 360 / lados);
        int puntos_iniciales = objeto.listaVertices.length;

        //generamos los siguientes puntos, comienza en 1 porque ya existe un lado (con el que se manda a crear objetos de revolucion)
        for (int i = 1; i < lados; i++) {
            //recorremos los puntos iniciales solamente
            for (int j = 0; j < puntos_iniciales; j++) {
                QVector3 tmp = new QVector3(objeto.listaVertices[j].ubicacion.x, objeto.listaVertices[j].ubicacion.y, objeto.listaVertices[j].ubicacion.z);
                //se rota en el ejey de las Y cada punto 
                tmp.rotateY(angulo * i);//se rota solo en Eje de Y
                objeto.agregarVertice(tmp.x, tmp.y, tmp.z, (float) 1.0f / lados * i, objeto.listaVertices[j].v);//agrega el vertice rotado
            }
        }
        //ahora unimos las caras con los nuevos vertices

        //se recorre por cada lado
        int poligonos_x_lado = puntos_iniciales - 1;
        //los vertices 
        int v1 = 0, v2 = 0, v3 = 0, v4 = 0;
        int t = 0;
        int p1 = 0, p2 = 0;//puntos iniciales de cada lado para cerrar la figura
        for (int lado = 0; lado < lados; lado++) {
            //los poligonos por lado
            for (int j = 0; j < poligonos_x_lado; j++) {
                v1 = lado * puntos_iniciales + j;
//                if (!tipotoro) {
//                    if (j == 0) {
//                        v1 = 0;
//                    }
//                }
                v2 = v1 + 1;
                if (lado < lados - 1) {
                    t = v1 + puntos_iniciales;
                } else//si es el ultimo lado
                {
                    t = j;//aca une con los puntos originales para cerrar la figura
                }
                v3 = t + 1;
                v4 = t;

                // si es el primero plano o el ultimo (de cada lado)
                if (!tipotoro) {
                    if (j == 0 /*|| j == poligonos_x_lado - 1*/) {
                        try {
                            //agrega solo un triangulo
//                        objeto.agregarPoligono(v1, v2, v3);
                            objeto.agregarPoligono(0, v2, v3).smooth = suavizarTopes;
                        } catch (Exception ex) {
                            Logger.getLogger(QGenerador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (j == poligonos_x_lado - 1) {
                        try {
                            //                        objeto.agregarPoligono(v3, v4, v1);
                            objeto.agregarPoligono(j + 1, v4, v1).smooth = suavizarTopes;
                        } catch (Exception ex) {
                            Logger.getLogger(QGenerador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            //agrega 2 triangulos
                            objeto.agregarPoligono(v1, v2, v3).smooth = suavizar;
                            objeto.agregarPoligono(v3, v4, v1).smooth = suavizar;
                        } catch (Exception ex) {
                            Logger.getLogger(QGenerador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        //agrega 2 triangulos
                        objeto.agregarPoligono(v1, v2, v3).smooth = suavizar;
                        objeto.agregarPoligono(v3, v4, v1).smooth = suavizar;
                    } catch (Exception ex) {
                        Logger.getLogger(QGenerador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                //conserva puntos iniciales
                if (j == 0) {
                    p1 = v1;
                    p2 = v4;
                }
            }
            //agrega una cara mas que corresponde a cerrar el punto inicial con el final de los iniciales
            if (cerrarFigura) {
                try {
                    ///203
                    //352
                    //agrega 2 triangulos
                    objeto.agregarPoligono(p1, p2, v2);
//                objeto.agregarPoligono(p1, v3, v2);
                    objeto.agregarPoligono(p2, v3, v2);
                } catch (Exception ex) {
                    Logger.getLogger(QGenerador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        //ultimo lado
        return objeto;
    }

    public static QGeometria generarSombrero(float alto, float radio, int secciones) {
        QGeometria objeto = new QGeometria();
        QMaterialBas material = new QMaterialBas("Cilindro");
//primer paso generar vertices
        objeto.agregarVertice(0, alto / 2, 0);
        objeto.agregarVertice(radio, -alto / 4, 0);
        objeto.agregarVertice(radio * 2, -alto / 2, 0);
        objeto.agregarVertice(0, -alto / 2, 0);
        objeto = generarRevolucion(objeto, secciones);
        objeto = QUtilNormales.calcularNormales(objeto);
        objeto = QMaterialUtil.aplicarMaterial(objeto, material);
        return objeto;
    }

    public static QGeometria generarDisco(float radio, int secciones) {
        QGeometria objeto = new QGeometria();
        QMaterialBas material = new QMaterialBas("Cilindro");
//primer paso generar vertices
        objeto.agregarVertice(radio / 2, 0, 0);
        objeto = generarRevolucion(objeto, secciones);
        objeto = QUtilNormales.calcularNormales(objeto);
        objeto = QMaterialUtil.aplicarMaterial(objeto, material);
        return objeto;
    }

    public static QGeometria generarMediaEsfera(float radio) {
        int secciones = 36;
        return generarMediaEsfera(radio, secciones);
    }

    public static QGeometria generarMediaEsfera(float radio, int secciones) {
        QGeometria objeto = new QGeometria();
        QMaterialBas material = new QMaterialBas("Esfera");
        QVertice inicial = objeto.agregarVertice(0, radio, 0); //primer vertice
        QVector3 vector = new QVector3(inicial.ubicacion.x, inicial.ubicacion.y, inicial.ubicacion.z);
        float angulo = 360 / secciones;
//        generamos los vertices el contorno para luego generar el objeto por medio de revolucion
        for (int i = 1; i <= secciones / 4; i++) {//medio circulo
            vector = vector.rotateZ((float) Math.toRadians(angulo));
            objeto.agregarVertice(vector.x, vector.y, vector.z);
        }

        objeto = generarRevolucion(objeto, secciones, false, false, false, false);
        objeto = QUtilNormales.calcularNormales(objeto);
        //el objeto es suavizado
        for (QPrimitiva face : objeto.listaPrimitivas) {
            ((QPoligono) face).smooth = true;
        }
        objeto = QMaterialUtil.aplicarMaterial(objeto, material);
        return objeto;
    }

}
