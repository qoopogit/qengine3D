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

        int n = 5;
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                QMaterialBas material = new QMaterialBas("PBR");
                material.setColorDifusa(QColor.RED);
                material.setRugosidad(i * (1.0f / n));
                material.setMetalico(j * (1.0f / n));
                QEntidad objeto = new QEntidad("PBR");
                objeto.mover(i * 10 - (n * 10 / 2), j * 10 - (n * 10 / 2), 0);
                objeto.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(3.0f), material));
                escena.agregarEntidad(objeto);
            }
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
