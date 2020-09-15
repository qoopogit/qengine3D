/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

import java.util.Arrays;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;

/**
 *
 * @author alberto
 */
public class QMallaUtil {

    /**
     * Divide un objeto en mas puntos y planos
     *
     * @param objeto
     * @param veces
     * @return
     */
    public static QGeometria subdividir(QGeometria objeto, int veces) {
        try {
            for (int repeticion = 0; repeticion < veces; repeticion++) {

                QPrimitiva[] tmpCaras = Arrays.copyOf(objeto.primitivas, objeto.primitivas.length);
                //Procedimiento. A cada poligono se agrega un punto en el centro de todos los puntos del plano.
                // luego creamos triangulos que van desde 2 puntos consecutivos del anterior plano y lo unimos
                // al nuevo punto formando un triangulo.
                // Si antes era un triangulo se formaran 3 triangulos
                // si antes era un cuadrilatero se formaran 4 triangulos

                //para la texura toca calcular la nueva coordeada para el vertice del centro y 
                QVertice tmpNuevoVertice = new QVertice();
                QVertice tmpVerticeAgregado = new QVertice();//vertice agregado se usa para cambiar la normal
                QPoligono faceTmp;
//                QPoligono.UVCoordinate texTmp = new QPoligono.UVCoordinate();
//                float tmpU, tmpV;

                int contador = 0;
                int nPoligonos;
                int ultPos = 0;// la posicion del ultimo vertice agregado
                for (QPrimitiva primitiva : tmpCaras) {

                    if (primitiva instanceof QPoligono) {
                        QPoligono poligono = (QPoligono) primitiva;

                        //paso 1 obtener nuevo punto en el centro del plano
                        tmpNuevoVertice.setXYZ(0, 0, 0);

                        for (int vi = 0; vi < poligono.listaVertices.length; vi++) {
                            //coordenada vertice
                            tmpNuevoVertice.ubicacion.add(objeto.vertices[poligono.listaVertices[vi]].ubicacion);
//                            tmpNuevoVertice.ubicacion.x += objeto.vertices[poligono.vertices[vi]].ubicacion.x;
//                            tmpNuevoVertice.ubicacion.y += objeto.vertices[poligono.vertices[vi]].ubicacion.y;
//                            tmpNuevoVertice.ubicacion.z += objeto.vertices[poligono.vertices[vi]].ubicacion.z;
                            tmpNuevoVertice.u += objeto.vertices[poligono.listaVertices[vi]].u;
                            tmpNuevoVertice.v += objeto.vertices[poligono.listaVertices[vi]].v;

                        }
                        tmpNuevoVertice.ubicacion.multiply(1.0f / poligono.listaVertices.length);
//                        tmpNuevoVertice.ubicacion.x /= poligono.vertices.length;
//                        tmpNuevoVertice.ubicacion.y /= poligono.vertices.length;
//                        tmpNuevoVertice.ubicacion.z /= poligono.vertices.length;
                        tmpNuevoVertice.u /= poligono.listaVertices.length;
                        tmpNuevoVertice.v /= poligono.listaVertices.length;
                        //paso 1.1 calcula nuevo punto en el centro de la textura
//                         texTmp.u = 0;
//                        texTmp.v = 0;
//                        for (int vi = 0; vi < poligono.uv.length; vi++) {
//                            //coordenada textura
//                            texTmp.u += poligono.uv[vi].u;
//                            texTmp.v += poligono.uv[vi].v;
//                        }
//
//                        //textura
//                        texTmp.u /= poligono.uv.length;
//                        texTmp.v /= poligono.uv.length;

//                       
                        //agrego el nuevo vertice central
//                        tmpVerticeAgregado = objeto.agregarVertice(tmpNuevoVertice.ubicacion.x, tmpNuevoVertice.ubicacion.y, tmpNuevoVertice.ubicacion.z, tmpNuevoVertice.u, tmpNuevoVertice.v);
                        tmpVerticeAgregado = objeto.agregarVertice(tmpNuevoVertice.ubicacion, tmpNuevoVertice.u, tmpNuevoVertice.v);
                        tmpVerticeAgregado.normal = objeto.vertices[poligono.listaVertices[0]].normal.clone();

                        ultPos = objeto.vertices.length - 1;
                        // paso 2 agregar los nuevos poligonos
                        nPoligonos = poligono.listaVertices.length;
//                        QPoligono.UVCoordinate[] coordenadasCara = new QPoligono.UVCoordinate[3];
                        for (int j = 0; j < nPoligonos; j++) {
                            if (j < nPoligonos - 1) {
                                faceTmp = objeto.agregarPoligono(poligono.material, poligono.listaVertices[j], poligono.listaVertices[j + 1], ultPos);
//                                //textura
//                                coordenadasCara[0] = poligono.uv[j];
//                                coordenadasCara[1] = poligono.uv[j + 1];
//                                coordenadasCara[2] = new QPoligono.UVCoordinate(texTmp.u, texTmp.v);

                            } else {
                                faceTmp = objeto.agregarPoligono(poligono.material, poligono.listaVertices[j], poligono.listaVertices[0], ultPos);

//                                //textura
//                                coordenadasCara[0] = poligono.uv[j];
////                            coordenadasCara[1] = poligono.uv[j+1];
//                                coordenadasCara[1] = poligono.uv[0];
//                                coordenadasCara[2] = new QPoligono.UVCoordinate(texTmp.u, texTmp.v);
                            }
                            faceTmp.setSmooth(poligono.isSmooth());
//                            faceTmp.setUV(coordenadasCara);
                        }

                        contador++;
                    }
                }
//                //paso 3 elimino los poligonos que antes habian, desde 0 hasta contador
                QPrimitiva[] nuevasCaras = new QPrimitiva[objeto.primitivas.length - contador];
                for (int k = 0; k < nuevasCaras.length; k++) {
                    try {
                        nuevasCaras[k] = objeto.primitivas[k + contador];
                    } catch (Exception ee) {
                    }
                }
                objeto.primitivas = nuevasCaras;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        objeto = QUtilNormales.calcularNormales(objeto);

        return objeto;
    }

    /**
     * Simplifica un objeto en menos puntos y planos
     *
     * @param objeto
     * @param veces
     * @return
     */
//    public static QGeometria simplificar(QGeometria objeto, int veces) {
//        try {
//            for (int repeticion = 0; repeticion < veces; repeticion++) {
//
//                QPoligono[] tmpCaras = Arrays.copyOf(objeto.primitivas, objeto.primitivas.length);
//                //Procedimiento.Primero hacemos una pasada tomnaod 2 triangulos y convertimos en cuadrados
//                //lego realizamos otra pasada y conertimos los 2 cuadrados en  triangulos
//
//                int indice = 0;//indice del vertice
//                int contador = 0;
//                for (QVertice vertice : objeto.vertices) {
//                    //recorro las caras para buscar quien tiene a este vértice
//                    for (QPoligono cara : objeto.primitivas) {
//                          for(int j:cara.vertices)
//                          {
//                              if(j==indice)
//                              {
//                                  contador++;                                  
//                              }
//                          }
//                    }
//                    
//                    //luego pregunto si hay varias caras que tienen ese vertice 
//                    
//                    
//                    indice++;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        objeto = QUtilNormales.calcularNormales(objeto);
//
//        return objeto;
//    }
}
