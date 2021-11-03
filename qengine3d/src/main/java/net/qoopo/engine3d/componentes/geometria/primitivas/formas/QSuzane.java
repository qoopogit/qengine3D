/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.input.control.gizmo.transformacion.traslacion.QGizmoTraslacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QUtilNormales;

/**
 *
 * @author alberto
 */
public class QSuzane extends QForma {

    private static final QEntidad ent = CargaWaveObject.cargarWaveObject(QGizmoTraslacion.class.getResourceAsStream("/res/modelos/suzane.obj")).get(0);

    public QSuzane() {
        material = new QMaterialBas("QSusane");
        nombre = "QSusane";
        construir();
    }

    @Override
    public void construir() {
        eliminarDatos();
        try {
            QGeometria teapot = QUtilComponentes.getGeometria(ent);
            for (QVertice vertice : teapot.vertices) {
                this.agregarVertice(vertice);
            }
            for (QPrimitiva primitiva : teapot.primitivas) {
                this.agregarPoligono(primitiva.listaVertices);
            }
        } catch (Exception ex) {
            Logger.getLogger(QSuzane.class.getName()).log(Level.SEVERE, null, ex);
        }
        QUtilNormales.calcularNormales(this);
        QMaterialUtil.suavizar(this, true);
        QMaterialUtil.aplicarMaterial(this, material);
    }

}
