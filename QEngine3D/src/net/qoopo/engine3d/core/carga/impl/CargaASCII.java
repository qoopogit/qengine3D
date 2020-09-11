/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.carga.CargaObjeto;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;

/**
 *
 * @author alberto
 */
public class CargaASCII extends CargaObjeto {

    public CargaASCII() {
    }

    @Override
    public void cargar(File archivo) {
        this.archivo = archivo;
        start();
    }

    @Override
    public void run() {
        try {

            long progress = 0;
            QGeometria readingObject = null;
            int vertexIndexOffset = 0;
            BufferedReader reader = null;
            boolean smoothMode = false;
            boolean vertexNormalSpecified = false;
            long fileLength = archivo.length();
            try {
                reader = new BufferedReader(new FileReader(archivo));

                String line;
//                ArrayList<QPoligono.UVCoordinate> uvList = new ArrayList<>();

                QMaterialBas defaultMaterial = new QMaterialBas("Default");
                QMaterialBas currentMaterial = null;

                int cargando = 0;
                boolean vertices = false;
                boolean caras = false;

                while ((line = reader.readLine()) != null) {
                    progress += line.length() + 2;
                    progreso.setValue((int) (100 * progress / fileLength));

                    if (!line.contains(" ") || line.contains("Vertices") || line.contains("Faces")) {
                        //si no contiene espacios es el numero que identifica los vertices o caras
                        cargando++;
                        //si es vertices
                        if (cargando == 1) {
                            vertices = true;
                            caras = false;
                        } else {
                            //si es caras
                            vertices = false;
                            caras = true;
                        }
                    } else if (line.contains("Material") || line.contains("Smoothing")){
                        
                    } else {
                        //si es un vertice o una cara
                        if (vertices) {
                            if (readingObject == null) {
                                readingObject = new QGeometria();
                            }
                            String[] att = line.split(" ");
                            if (att.length == 3) {
                                readingObject.agregarVertice(Float.parseFloat(att[0]), Float.parseFloat(att[1]), Float.parseFloat(att[2]));
                            } else if (att.length == 5) {
                                readingObject.agregarVertice(Float.parseFloat(att[0]), Float.parseFloat(att[1]), Float.parseFloat(att[2]), Float.parseFloat(att[3]), Float.parseFloat(att[4]));
                            }

                        } else if (caras) {
                            if (readingObject == null) {
                                readingObject = new QGeometria();
                            }
                            String[] attr = line.split(" ");
                            int[] vertices_cara = new int[attr.length];
                            for (int i = 0; i < attr.length; i++) {
                                vertices_cara[i] = Integer.valueOf(attr[i]);
                            }
                            QPoligono face = readingObject.agregarPoligono(vertices_cara);
                            face.material = defaultMaterial;
                        }
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

            for (QPrimitiva face : readingObject.listaPrimitivas) {
                if (face.listaVertices.length >= 3) {

                    ((QPoligono) face).calculaNormalYCentro();
                    if (!vertexNormalSpecified || true) {
                        for (int i : face.listaVertices) {
                            face.geometria.listaVertices[i].normal.add(((QPoligono) face).getNormal());
                        }
                    }
                }
            }
            for (int i = 0; i < readingObject.listaVertices.length; i++) {
                readingObject.listaVertices[i].normal.normalize();
            }
            progreso.setValue(0);
//            lista.add(readingObject);

            QEntidad ent = new QEntidad(readingObject.nombre);
            ent.agregarComponente(readingObject);

            lista.add(ent);
//            refreshObjectList();
            if (accionFinal != null) {
                accionFinal.ejecutar();
            }
//            System.out.println(readingObject.nombre);
        } catch (Exception e) {

        }
    }

}
