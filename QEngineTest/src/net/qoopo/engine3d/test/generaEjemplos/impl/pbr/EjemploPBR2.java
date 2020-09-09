/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemploPBR2 extends GeneraEjemplo {

    public EjemploPBR2() {

    }

    public void iniciar(QEscena escena) {
        this.mundo = escena;

        int nrRows = 7;
        int nrColumns = 7;
        float spacing = 2.5f;

        //la entidad reflexion se encargara de renderizar el mapa de reflejos
        QEntidad reflexion = new QEntidad();
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
        reflexion.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1.0f, 0);
        escena.agregarEntidad(reflexion);

//        QGeometria malla = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/PRIMITIVAS/teapot.obj")).get(0));
        for (int row = 0; row < nrRows; ++row) {
            for (int col = 0; col < nrColumns; ++col) {
                QMaterialBas material = new QMaterialBas("PBR");
                material.setColorBase(QColor.RED);
                material.setRugosidad(QMath.clamp((float) col / (float) nrColumns, 0.05f, 1.0f));
                material.setMetalico((float) row / (float) nrRows);
                material.setMapaEntorno(mapa.getProcEntorno());
                material.setMapaIrradiacion(mapa.getProcIrradiacion());
                material.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
//                material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
//                material.setMapaIrradiacion(new QProcesadorSimple(mapa.getTexturaIrradiacion()));
                QEntidad objeto = new QEntidad("PBR");
                objeto.mover((col - (nrColumns / 2)) * spacing, (row - (nrRows / 2)) * spacing, 0);
//                objeto.rotar(0, Math.toRadians(180), 0);

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
