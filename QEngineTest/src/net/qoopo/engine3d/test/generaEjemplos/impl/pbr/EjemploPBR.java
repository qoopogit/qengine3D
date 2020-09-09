/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemploPBR extends GeneraEjemplo {

    public EjemploPBR() {

    }

    public void iniciar(QEscena escena) {
        this.mundo = escena;

        int nrRows = 7;
        int nrColumns = 7;
        float spacing = 2.5f;

//        QGeometria malla = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot.obj")).get(0));
        for (int row = 0; row <=nrRows; ++row) {
            for (int col = 0; col <=nrColumns; ++col) {
                QMaterialBas material = new QMaterialBas("PBR");
                material.setColorBase(QColor.RED);
                material.setRugosidad(QMath.clamp((float)col / (float) nrColumns, 0.05f, 1.0f));
                material.setMetalico((float) row / (float) nrRows);
                QEntidad objeto = new QEntidad("PBR");
                objeto.mover((col - (nrColumns / 2)) * spacing, (row - (nrRows / 2)) * spacing, 0);
                objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(1.0f), material));
//                objeto.agregarComponente(QMaterialUtil.aplicarMaterial(malla.clone(), material));
//                objeto.escalar(0.8f);
                escena.agregarEntidad(objeto);
            }
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
