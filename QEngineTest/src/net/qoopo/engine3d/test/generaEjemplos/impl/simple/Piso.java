/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class Piso extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;
            QEntidad piso = new QEntidad("Piso");
            QGeometria pisoGeometria = new QPlano(50, 50);
            QMaterialBas mat;
            try {
                mat = new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/basicas/piso/baldosas/baldosa8.jpg"))));
                mat.getMapaColor().setMuestrasU(10);
                mat.getMapaColor().setMuestrasV(10);
                QMaterialUtil.aplicarMaterial(pisoGeometria, mat);
            } catch (IOException ex) {
                Logger.getLogger(Piso.class.getName()).log(Level.SEVERE, null, ex);
            }

            piso.agregarComponente(pisoGeometria);
            mundo.agregarEntidad(piso);
        } catch (Exception ex) {
            Logger.getLogger(Piso.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
