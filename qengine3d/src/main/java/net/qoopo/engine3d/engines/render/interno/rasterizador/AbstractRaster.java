/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.rasterizador;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.transformacion.QVerticesBuffer;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector4;

/**
 * Realiza la rasterización de los polígonos
 *
 * @author alberto
 */
public abstract class AbstractRaster {

    protected ArrayList<QVertice> verticesClipped = new ArrayList<>();

//    public abstract AbstractRaster(QMotorRender render);
    /**
     *
     * @param primitiva
     * @param p1
     * @param p2
     */
    public abstract void raster(QPrimitiva primitiva, QVector4 p1, QVector4 p2);

    /**
     * Realiza la rasterización de un polígono
     *
     * @param bufferVertices
     * @param primitiva
     * @param wire
     */
    public abstract void raster(QVerticesBuffer bufferVertices, QPrimitiva primitiva, boolean wire);

    /**
     * Realiza el clipping de los vertices
     *
     * @param camara
     * @param poligono
     * @param vertices
     */
    protected void clipping(QCamara camara, QPrimitiva poligono, QVertice[] vertices) {
        verticesClipped.clear();
        QVertice[] vt = new QVertice[3];
        float alfa;
        QVertice verticeTmp;
        //recorre los vertices y verifica si estan dentro del campo de vision, si no es asi, encuentra el vertice que corresponderia dentro del campo de vision
        for (int i = 0; i < poligono.listaVertices.length; i++) {
            vt[0] = vertices[poligono.listaVertices[i]];
            vt[1] = vertices[poligono.listaVertices[(i + 1) % poligono.listaVertices.length]];

//                    //si los 2 vertices a procesar estan en el campo de vision
            if (camara.estaEnCampoVision(vt[0]) && camara.estaEnCampoVision(vt[1])) {
                verticesClipped.add(vt[0]);
            } else {
                //cuando uno de los dos no esta en el campo de vision
                if (camara.estaEnCampoVision(vt[0]) && !camara.estaEnCampoVision(vt[1])) {
                    verticesClipped.add(vt[0]);
//                    if (-vt[0].ubicacion.z == camara.frustrumCerca) {
//                        continue;
//                    }
//                            alfa = (-camara.frustrumCerca - vt[0].ubicacion.z) / (vt[1].ubicacion.z - vt[0].ubicacion.z);
                } else if (!camara.estaEnCampoVision(vt[0]) && camara.estaEnCampoVision(vt[1])) {
                    if (-vt[1].ubicacion.z == camara.frustrumCerca && (i + 1) % poligono.listaVertices.length != 0) {
                        verticesClipped.add(vt[1]);
                        continue;
                    }
                } else {
                    continue;
                }
                alfa = camara.obtenerClipedVerticeAlfa(vt[0].ubicacion.getVector3(), vt[1].ubicacion.getVector3());
                verticeTmp = new QVertice();
                QMath.linear(verticeTmp, alfa, vt[0], vt[1]);
                verticesClipped.add(verticeTmp);
            }
        }
    }

}
