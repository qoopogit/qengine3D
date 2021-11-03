/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura.mapeo;

/**
 *
 * @author alberto
 */
public class QMapeoTexturaEsfera {

//    public static QGeometria generarCoordenadasTextura(QGeometria esfera, int seccionesEsfera) {
//
//        //coordenadas UV
//        ArrayList<QVector2> uvList = new ArrayList<>();
//
//        int seccionesAncho = seccionesEsfera;
//        int seccionesAlto = seccionesEsfera / 2;
//
//        float anchoSeccion = 1f / (float) seccionesAncho;
//        float altoSeccion = 1f / (float) seccionesAlto;
//        int c = 0;
//        for (int i = 0; i <= seccionesAncho; i++) {
//            for (int j = seccionesAlto; j >= 0; j--) {
//                uvList.add(new QVector2(i * anchoSeccion, j * altoSeccion));
////                esfera.listaVertices[c].u = i * anchoSeccion;
////                esfera.listaVertices[c].v = i * anchoSeccion;
//                c++;
//            }
//        }
////        System.out.println("vertices uv =" + uvList.size());
////        System.out.println("vertices esfera =" + esfera.listaVertices.length);
////        QVector2[] coordenadasCara;
//
//        int caraActual = 0;
//        int t = 0;
//        int indiceVertice = 0;
//        for (QPrimitiva face : esfera.listaPrimitivas) {
//            QPoligono poligono = (QPoligono) face;
////            coordenadasCara = new QVector2[poligono.listaVertices.length];
//            for (int i = 0; i < poligono.listaVertices.length; i++) {
//                indiceVertice = poligono.listaVertices[i];
//                //falta validar si son los vertices de los polos para cambiarlo por las coordeandas de textura correctas
//
////                //si es del polo superior
////                if (indiceVertice == 0 && caraActual != 0) {
////                    int car = (caraActual / 2) / seccionesAlto;
////                    indiceVertice = car * seccionesAlto * 2 + 1;
////                    if (uvList.size() < indiceVertice) {
////                        System.out.println("NO HAY INDICE !! " + indiceVertice);
////                        System.out.println("----------------------------------");
////                        System.out.println("nuevo indice " + indiceVertice);
////                        System.out.println("cara actual=" + caraActual);
////                        System.out.println("cara =" + car);
////                        System.out.println("secciones alto=" + seccionesAlto);
////                    }
////                }
//                //aca multiplico por 2 porq cada seccion consta de 2 caras
//                if (esfera.listaPrimitivas.length - caraActual > seccionesAlto * 2) {
//                    //si son todas las caras excepto las de la ultima columna
////                    coordenadasCara[i] = uvList.get(indiceVertice);
//                    esfera.listaVertices[indiceVertice].u = uvList.get(indiceVertice).x;
//                    esfera.listaVertices[indiceVertice].v = uvList.get(indiceVertice).y;
//                } else {
//                    //aca pregunto si no son los primero vertices me tocaria cambiar con los ultimos vertices de la textura
//                    //esto es porque en el generador de la esfera nosotros unimos el ultimo plano con los primeros puntos
//                    // para poder calcular bien las normales, pero en la textura si existen estos vertices mas
//                    if (poligono.listaVertices[i] > seccionesAlto) {
////                        coordenadasCara[i] = uvList.get(indiceVertice); //lo mismo que con las demas caras
//                        esfera.listaVertices[indiceVertice].u = uvList.get(indiceVertice).x;
//                        esfera.listaVertices[indiceVertice].v = uvList.get(indiceVertice).y;
//                    } else {
//                        //son los vertices iniciales, no los debemos tomar en cuenta para el mapeado UV
//                        // sino que debemos tomar los vertices nuevos de la textura
//
//                        indiceVertice = poligono.listaVertices[i];//vuelvo a tomar lo normal
//                        t = seccionesAncho * (seccionesAlto + 1);
////                        coordenadasCara[i] = uvList.get(indiceVertice + t);
//                        esfera.listaVertices[indiceVertice].u = uvList.get(indiceVertice).x;
//                        esfera.listaVertices[indiceVertice].v = uvList.get(indiceVertice).y;
//                    }
//                }
//            }
////            poligono.setUV(coordenadasCara);
//            caraActual++;
//        }
//        //caras por lado
//        return esfera;
//    }

//    public static QGeometria generarCoordenadasTexturaMediaEsfera(QGeometria esfera, int seccionesEsfera) {
//        //coordenadas UV
//        ArrayList<QPoligono.UVCoordinate> uvList = new ArrayList<>();
//
//        int seccionesAncho = seccionesEsfera;
//        int seccionesAlto = seccionesEsfera / 2;
//
//        float anchoSeccion = 1f / (float) seccionesAncho;
//        float altoSeccion = 1f / (float) seccionesAlto;
//
//        for (int i = 0; i <= seccionesAncho; i++) {
//            //las longitudes verticales empiezan en el alto y terminan en 0
//            for (int j = seccionesAlto; j >= 0; j--) {
////                if (i < seccionesAncho - 1) {
//                uvList.add(new QPoligono.UVCoordinate(i * anchoSeccion, j * altoSeccion));//
////                } else {
////                    uvList.add(new QPoligono.UVCoordinate(0, j * altoSeccion));//
////                }
//            }
//        }
////        System.out.println("vertices uv =" + uvList.size());
////        System.out.println("vertices esfera =" + esfera.listaVertices.length);
//        QPoligono.UVCoordinate[] coordenadasCara;
//
//        int cara = 0;
//        int t = 0;
//
//        for (QPrimitiva face : esfera.listaPrimitivas) {
//            QPoligono poligono = (QPoligono) face;
//            coordenadasCara = new QPoligono.UVCoordinate[poligono.listaVertices.length];
//            for (int i = 0; i < poligono.listaVertices.length; i++) {
//                //aca multiplico por 2 porq cada seccion consta de 2 caras
//                if (esfera.listaPrimitivas.length - cara > seccionesAlto * 2) {
//                    //si son todas las caras excepto las de la ultima columna
//                    coordenadasCara[i] = uvList.get(poligono.listaVertices[i]);
//                } else {
//                    //aca pregunto si no son los primero vertices me tocaria cambiar con los ultimos vertices de la textura
//                    //esto es porque en el generador de la esfera nosotros unimos el ultimo plano con los primeros puntos
//                    // para poder calcular bien las normales, pero en la textura si existen estos vertices mas
//                    if (poligono.listaVertices[i] > seccionesAlto) {
//                        coordenadasCara[i] = uvList.get(poligono.listaVertices[i]); //lo mismo que con las demas caras
//                    } else {
//                        //son los vertices iniciales, no los debemos tomar en cuenta para el mapeado UV
//                        // sino que debemos tomar los vertices nuevos de la textura
//                        t = seccionesAncho * (seccionesAlto + 1);
//                        coordenadasCara[i] = uvList.get(poligono.listaVertices[i] + t);
//
//                    }
//                }
//            }
//            poligono.setUV(coordenadasCara);
//            cara++;
//        }
//
//        //caras por lado
//        return esfera;
//    }
}
