/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

/**
 *
 * @author alberto
 */
public class QMallaUtil {

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
