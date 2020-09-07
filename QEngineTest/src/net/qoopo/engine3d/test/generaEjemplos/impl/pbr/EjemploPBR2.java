/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.pbr;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.carga.impl.assimp.imp.AssimpLoader;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
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

        int nrRows = 10;
        int nrColumns = 10;
        float spacing = 2.5f;

        //la entidad reflexion se encargara de renderizar el mapa de reflejos
        QEntidad reflexion = new QEntidad();
        QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);
//                material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
//                material.setTipoMapaEntorno(QMapaCubo.FORMATO_MAPA_CUBO);
        reflexion.agregarComponente(mapa);
        mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1.0f, 0);
        escena.agregarEntidad(reflexion);

        for (int row = 0; row < nrRows; ++row) {

            for (int col = 0; col < nrColumns; ++col) {
                try {
                    QMaterialBas material = new QMaterialBas("PBR");
                    material.setColorDifusa(QColor.RED);
                    material.setRugosidad(QMath.clamp((float) col / (float) nrColumns, 0.05f, 1.0f));
                    material.setMetalico((float) row / (float) nrRows);
                    material.setMapaEntorno(new QProcesadorSimple(mapa.getTexturaSalida()));
                    material.setMapaIrradiacion(new QProcesadorSimple(mapa.getTexturaIrradiacion()));
//                QEntidad objeto = new QEntidad("PBR");

                    QEntidad objeto = AssimpLoader.cargarAssimpItems(new File(QGlobal.RECURSOS + "objetos/formato_fbx/Cerberus_by_Andrew_Maximov/Cerberus_LP.FBX")).get(0);
//                    QEntidad objeto = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VARIOS/Cerberus_Gun/cerberus_gun.obj")).get(0);

                    objeto.mover(
                            (col - (nrColumns / 2)) * spacing,
                            (row - (nrRows / 2)) * spacing,
                            0);
                    objeto.rotar(0, Math.toRadians(180), spacing);
                    objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(1.0f), material));

                    escena.agregarEntidad(objeto);
                } catch (Exception ex) {
                    Logger.getLogger(EjemploPBR2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
