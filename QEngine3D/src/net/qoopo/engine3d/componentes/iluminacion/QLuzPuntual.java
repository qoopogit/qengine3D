/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.iluminacion;

import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class QLuzPuntual extends QLuz {

    public QLuzPuntual() {
        this.resolucionMapaSombra = QGlobal.SOMBRAS_OMNI_DIRECCIONAL_MAPA_ANCHO;
    }

    public QLuzPuntual(float energy, QColor color, boolean getNewId, float radio) {
        super(energy, color, getNewId, radio);
        this.resolucionMapaSombra = QGlobal.SOMBRAS_OMNI_DIRECCIONAL_MAPA_ANCHO;
    }

    @Override
    public QLuz clone() {
        QLuz newLight = new QLuzPuntual(energia, color, false, radio);
        newLight.entidad = this.entidad.clone();
        newLight.setEnable(this.enable);
        newLight.setProyectarSombras(this.proyectarSombras);
        newLight.setResolucionMapaSombra(resolucionMapaSombra);
        newLight.setSombraDinamica(sombraDinamica);
        return newLight;
    }

}
