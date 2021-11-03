/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.carga.impl.studiomax;

import net.qoopo.engine3d.core.carga.impl.studiomax.util.ModelObject;
import net.qoopo.engine3d.core.carga.impl.studiomax.util.ModelLoader;
import net.qoopo.engine3d.core.carga.impl.studiomax.util.Model3DS;
import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.carga.CargaObjeto;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;

/**
 * Carga una malla simple de unarchivo de 3DStudioMax
 *
 * @author alberto
 */
public class Carga3DMax extends CargaObjeto {

    public Carga3DMax() {
    }

    @Override
    public void cargar(File archivo) {
        this.archivo = archivo;
        start();
    }

    @Override
    public void run() {
        try {
            Model3DS modelo = ModelLoader.load3dModel(archivo);
            QMaterialBas material = new QMaterialBas("default");
//            try{
//                 material = new QMaterialBas(ImgReader.leerImagen(new File(archivo.getParentFile(),archivo.getName() + ".png")), 64, 64);
//                 material.nombre="Default";
//            }catch(Exception e)
//            {
//            }
            QGeometria objetoActual = null;

            for (ModelObject modeloOb : modelo.objects) {
                objetoActual = new QGeometria();
                objetoActual.nombre = modeloOb.getName();
                //vertices
                int vertices = modeloOb.vertices.length;
                for (int i = 0; i < vertices; i += 3) {
//                    objetoActual.agregarVertice(objeto.vertices[i], objeto.vertices[i + 1], objeto.vertices[i + 2]);
                    objetoActual.agregarVertice(modeloOb.vertices[i], modeloOb.vertices[i + 2], modeloOb.vertices[i + 1]);
                }
                //caras
                int caras = modeloOb.polygons.length;
                for (int i = 0; i < caras; i += 3) {
                    if (modeloOb.polygons[i] < vertices && modeloOb.polygons[i + 1] < vertices && modeloOb.polygons[i + 2] < vertices) {
//                        objetoActual.agregarPoligono(material, objeto.polygons[i], objeto.polygons[i + 1], objeto.polygons[i + 2]);
                        QPoligono face = objetoActual.agregarPoligono(material, modeloOb.polygons[i], modeloOb.polygons[i + 2], modeloOb.polygons[i + 1]);

                    } else {
                        System.out.println("esta llamando a un vertice que no existe");
                    }
                }
//                objeto.textureCoordinates

//                System.out.println("vertices=" + vertices);
//                System.out.println("caras=" + caras);
//                System.out.println("objeto actual ");
//                System.out.println("  vertices=" + objetoActual.vertices.length);
//                System.out.println("  caras = " + objetoActual.primitivas.length);
                objetoActual = QUtilNormales.calcularNormales(objetoActual);

                QEntidad ent = new QEntidad(objetoActual.nombre);
                ent.agregarComponente(objetoActual);
                lista.add(ent);
            }

            if (accionFinal != null) {
                accionFinal.ejecutar();
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

}
