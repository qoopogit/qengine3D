/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.carga.CargaObjeto;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector2;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.ImgReader;

/**
 *
 * @author alberto
 */
public class CargaWaveObject extends CargaObjeto {

    public CargaWaveObject() {
    }

    @Override
    public void cargar(File archivo) {
        this.archivo = archivo;
        start();
    }

    public static List<QEntidad> cargarWaveObject(File archivo) {
        if (archivo.exists()) {
            try {
                return cargarWaveObject(null, new FileInputStream(archivo), archivo.getParent(), archivo.length());
            } catch (FileNotFoundException ex) {
                Logger.getLogger(CargaWaveObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    public static List<QEntidad> cargarWaveObject(InputStream stream) {
        return cargarWaveObject(null, stream, "", 0);
    }

    public static List<QEntidad> cargarWaveObject(JProgressBar progreso, InputStream stream, String directory, long size) {
        List<QEntidad> lista = new ArrayList<>();
        try {
            long progress = 0;
            QGeometria geometriaLeyendo = null;
            int vertexIndexOffset = 0;
            BufferedReader reader = null;
            boolean smoothMode = false;
            boolean vertexNormalSpecified = false;
            long fileLength = size;
            try {
//                reader = new BufferedReader(new FileReader(archivo));
                reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                List<QVector2> listaUV = new ArrayList<>();
                HashMap<String, QMaterialBas> materialMap = new HashMap<>();
                QMaterialBas defaultMaterial = new QMaterialBas("Default");
                QMaterialBas currentMaterial = null;
                while ((line = reader.readLine()) != null) {
                    progress += line.length() + 2;
                    if (progreso != null) {
                        progreso.setValue((int) (100 * progress / fileLength));
                    }

                    String[] tokens = line.split("\\s+");
                    switch (tokens[0]) {
                        case "mtllib":
                            //Lee el archivo del material
                            String materialFileName = line.substring("mtllib ".length());
//                            String materialFileName = tokens[1];
                            File materialFile = new File(directory, materialFileName);
                            try {
                                if (materialFile.exists()) {
                                    BufferedReader materialReader = new BufferedReader(new FileReader(materialFile));
                                    String materialLine = "";
                                    QMaterialBas readingMaterial = null;
                                    while ((materialLine = materialReader.readLine()) != null) {
                                        if (materialLine.startsWith("newmtl ")) {
                                            if (readingMaterial != null) {
                                                materialMap.put(readingMaterial.getNombre(), readingMaterial);
                                            }
                                            String materialName = materialLine.substring("newmtl ".length());
                                            readingMaterial = new QMaterialBas(materialName);
                                        } else if (materialLine.startsWith("Ka ")) {
//                                        String[] att = materialLine.split("\\s+");
//                                    readingMaterial.setColorAmbiente(new QColor(1, Float.parseFloat(att[1]), Float.parseFloat(att[2]), Float.parseFloat(att[3])));
                                        } else if (materialLine.startsWith("Kd ")) {
                                            String[] att = materialLine.split("\\s+");
                                            readingMaterial.setColorBase(new QColor(1, Float.parseFloat(att[1]), Float.parseFloat(att[2]), Float.parseFloat(att[3])));
                                        } else if (materialLine.startsWith("Ks ")) {
//                                        String[] att = materialLine.split("\\s+");
//                                        readingMaterial.setColorEspecular(new QColor(1, Float.parseFloat(att[1]), Float.parseFloat(att[2]), Float.parseFloat(att[3])));
                                        } else if (materialLine.startsWith("d ")) {
                                            String[] att = materialLine.split("\\s+");
                                            readingMaterial.setTransAlfa(Float.parseFloat(att[1]));
                                        } else if (materialLine.startsWith("Tr ")) {
                                            String[] att = materialLine.split("\\s+");
                                            readingMaterial.setTransAlfa(1 - Float.parseFloat(att[1]));
                                        } else if (materialLine.startsWith("Ns ")) {
                                            String[] att = materialLine.split("\\s+");
                                            readingMaterial.setSpecularExponent((int) Float.parseFloat(att[1]));
                                        } else if (materialLine.toLowerCase().startsWith("map_kd ")) {
                                            String texture = materialLine.substring("map_Kd ".length()).trim();
                                            if (!texture.isEmpty()) {
                                                texture = texture.replaceAll("\\\\", "/");
                                                try {
                                                    readingMaterial.setMapaColor(new QProcesadorSimple(new QTextura(ImgReader.leerImagen(new File(directory, texture)))));
                                                } catch (Exception e) {
                                                    System.out.println("Error al cargar " + directory + File.separator + texture);
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else if (materialLine.toLowerCase().startsWith("map_bump ")) {
                                            String texture = materialLine.substring("map_Bump ".length()).trim();
                                            if (!texture.isEmpty()) {
                                                if (texture.startsWith("-bm ")) {
                                                    texture = texture.substring("-bm ".length()).trim();
                                                    readingMaterial.setFactorNormal(Float.parseFloat(texture.substring(0, texture.indexOf(" "))));
                                                    texture = texture.substring(texture.indexOf(" ")).trim();
                                                }
                                                texture = texture.replaceAll("\\\\", "/");
                                                try {
                                                    readingMaterial.setMapaNormal(new QProcesadorSimple(new QTextura(ImgReader.leerImagen(new File(directory, texture)))));
                                                } catch (Exception e) {
                                                    System.out.println("Error al cargar " + directory + File.separator + texture);
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else if (materialLine.toLowerCase().startsWith("map_ns ")) {
                                            String texture = materialLine.substring("map_Ns ".length()).trim();
                                            if (!texture.isEmpty()) {
                                                texture = texture.replaceAll("\\\\", "/");
                                                try {
                                                    readingMaterial.setMapaRugosidad(new QProcesadorSimple(new QTextura(ImgReader.leerImagen(new File(directory, texture)))));
                                                } catch (Exception e) {
                                                    System.out.println("Error al cargar " + directory + File.separator + texture);
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else if (materialLine.toLowerCase().startsWith("refl ")) {
                                            String texture = materialLine.substring("refl ".length()).trim();
                                            if (!texture.isEmpty()) {
                                                texture = texture.replaceAll("\\\\", "/");
                                                try {
                                                    readingMaterial.setMapaMetalico(new QProcesadorSimple(new QTextura(ImgReader.leerImagen(new File(directory, texture)))));
                                                } catch (Exception e) {
                                                    System.out.println("Error al cargar " + directory + File.separator + texture);
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else if (materialLine.toLowerCase().startsWith("bump ")) {
                                            String texture = materialLine.substring("bump ".length()).trim();
                                            if (!texture.isEmpty()) {
                                                if (texture.startsWith("-bm ")) {
                                                    texture = texture.substring("-bm ".length()).trim();
                                                    readingMaterial.setFactorNormal(Float.parseFloat(texture.substring(0, texture.indexOf(" "))));
                                                    texture = texture.substring(texture.indexOf(" ")).trim();
                                                }
                                                texture = texture.replaceAll("\\\\", "/");
//                                        System.out.println(directory + File.separator + texture);
                                                try {
                                                    readingMaterial.setMapaNormal(new QProcesadorSimple(new QTextura(ImgReader.leerImagen(new File(directory, texture)))));
                                                } catch (Exception e) {
                                                    System.out.println("Error al cargar " + directory + File.separator + texture);
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                    if (readingMaterial != null) {
                                        materialMap.put(readingMaterial.getNombre(), readingMaterial);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "o":
                            if (geometriaLeyendo != null) {
                                for (QPrimitiva face : geometriaLeyendo.listaPrimitivas) {
                                    if (face.listaVertices.length >= 3) {
                                        ((QPoligono) face).calculaNormalYCentro();
                                        if (!vertexNormalSpecified || true) {
                                            for (int i : face.listaVertices) {
                                                face.geometria.listaVertices[i].normal.add(((QPoligono) face).normal);
                                            }
                                        }
                                    }
                                }
                                for (int i = 0; i < geometriaLeyendo.listaVertices.length; i++) {
                                    geometriaLeyendo.listaVertices[i].normal.normalize();
                                }
                                vertexIndexOffset += geometriaLeyendo.listaVertices.length;

                                QEntidad ent = new QEntidad(geometriaLeyendo.nombre);
                                ent.agregarComponente(geometriaLeyendo);
                                QFormaColision colision = new QColisionMallaConvexa(geometriaLeyendo);
                                ent.agregarComponente(colision);
                                QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.ESTATICO, 0);
                                rigido.setFormaColision(colision);
                                ent.agregarComponente(rigido);
                                lista.add(ent);
//                            QGestorRecursos.agregarRecurso("g" + geometriaLeyendo.nombre, geometriaLeyendo);
//                            QGestorRecursos.agregarRecurso("e" + ent.nombre, ent);
                            }
                            String name = tokens[1].trim();
                            //                        System.out.println(name);
                            if (name.isEmpty()) {
                                name = null;
                            }
                            geometriaLeyendo = new QGeometria();
                            geometriaLeyendo.nombre = name;
                            break;
                        case "v":
                            if (geometriaLeyendo == null) {
                                geometriaLeyendo = new QGeometria();
                            }
                            geometriaLeyendo.agregarVertice(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2]), Float.parseFloat(tokens[3]));
                            break;

                        case "vt":
                            listaUV.add(new QVector2(Float.parseFloat(tokens[1]), Float.parseFloat(tokens[2])));
                            break;
                        case "vn":
                            //                                String[] att = line.split("\\s+");
//                                readingObject.listaVertices[currentVertex].normal
//                                        .set(Float.parseFloat(att[1]),
//                                                Float.parseFloat(att[2]),
//                                                Float.parseFloat(att[3]));
//                                vertexNormalSpecified = true;
                            break;
                        case "s":
//                            smoothMode = line.trim().equals("s 1");
                            smoothMode = tokens[1].equals("1");
                            break;
                        case "usemtl":
//                            currentMaterial = materialMap.get(line.substring("usemtl ".length()));
                            currentMaterial = materialMap.get(tokens[1]);
                            break;
                        case "f":
                            if (geometriaLeyendo == null) {
                                geometriaLeyendo = new QGeometria();
                            }

                            int[] verticesCara = new int[tokens.length - 1];
                            //                        QPoligono.UVCoordinate[] newFaceTexture = null;
                            //lee las caras, este for toma los vertices que definen la cara
//                            System.out.println("leyendo cara ");
//                            System.out.println("  tokens=" + tokens.length);
//                            System.out.println("   " + Arrays.toString(tokens));
                            int c = 0;
                            for (String token : tokens) {
                                if (!token.equals("f")) {
                                    String[] partes = token.split("/");
                                    verticesCara[c] = Integer.parseInt(partes[0]) - 1 - vertexIndexOffset;
                                    //si tiene texturas
                                    if (partes.length > 1) {
                                        if (!partes[1].isEmpty()) {
                                            geometriaLeyendo.listaVertices[verticesCara[c]].u = listaUV.get(Integer.parseInt(partes[1]) - 1).x;
                                            geometriaLeyendo.listaVertices[verticesCara[c]].v = listaUV.get(Integer.parseInt(partes[1]) - 1).y;
                                        }
                                        //si tiene la normal
                                        if (partes.length > 2) {

                                        }
                                    }
                                    c++;
                                }
                            }
                            QPoligono nuevaCara = geometriaLeyendo.agregarPoligono(verticesCara);
                            nuevaCara.smooth = smoothMode;
                            if (currentMaterial != null) {
                                nuevaCara.material = currentMaterial;
                            } else {
                                nuevaCara.material = defaultMaterial;
                            }
                            break;

                        default:
                            break;
                    }
                }
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                    }
                }
            }
            if (geometriaLeyendo != null) {
                for (QPrimitiva face : geometriaLeyendo.listaPrimitivas) {
                    if (face.listaVertices.length >= 3) {
                        ((QPoligono) face).calculaNormalYCentro();
                        if (!vertexNormalSpecified || true) {
                            for (int i : face.listaVertices) {
                                face.geometria.listaVertices[i].normal.add(((QPoligono) face).normal);
                            }
                        }
                    }
                }
                for (int i = 0; i < geometriaLeyendo.listaVertices.length; i++) {
                    geometriaLeyendo.listaVertices[i].normal.normalize();
                }
                if (progreso != null) {
                    progreso.setValue(0);
                }

                QEntidad ent = new QEntidad(geometriaLeyendo.nombre);
                ent.agregarComponente(geometriaLeyendo);
                QFormaColision colision = new QColisionMallaConvexa(geometriaLeyendo);
                ent.agregarComponente(colision);
                QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.ESTATICO, 0);
                rigido.setFormaColision(colision);
                ent.agregarComponente(rigido);
                lista.add(ent);

            }
//            QGestorRecursos.agregarRecurso("g" + geometriaLeyendo.nombre, geometriaLeyendo);
//            QGestorRecursos.agregarRecurso("e" + ent.nombre, ent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void run() {
        try {
            lista = cargarWaveObject(progreso, new FileInputStream(archivo), archivo.getParent(), archivo.length());
            if (accionFinal != null) {
                accionFinal.ejecutar();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargaWaveObject.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
