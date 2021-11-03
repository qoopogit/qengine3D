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
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemploPBR_CerberusGun extends GeneraEjemplo {

    public EjemploPBR_CerberusGun() {

    }

    public void iniciar(QEscena escena) {
        this.mundo = escena;
        try {
            QMapaCubo mapa = new QMapaCubo(QGlobal.MAPA_CUPO_RESOLUCION);

//            QEntidad objeto = new QEntidad("PBR");
//                    QEntidad objeto = AssimpLoader.cargarAssimpItems(new File(QGlobal.RECURSOS + "objetos/formato_fbx/Cerberus_by_Andrew_Maximov/Cerberus_LP.FBX")).get(0);
            QEntidad objeto = CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VARIOS/Cerberus_Gun/cerberus_gun.obj")).get(0);
            objeto.escalar(10);
            objeto.agregarComponente(mapa);
            objeto.rotar(0, Math.toRadians(180), 0);
//            objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(1.0f), material));
            mapa.aplicar(QMapaCubo.FORMATO_MAPA_CUBO, 1.0f, 0);
            escena.agregarEntidad(objeto);
        } catch (Exception ex) {
            Logger.getLogger(EjemploPBR_CerberusGun.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
