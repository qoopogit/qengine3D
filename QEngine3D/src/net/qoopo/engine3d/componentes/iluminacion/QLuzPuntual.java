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

    public QLuzPuntual(float intensidad, QColor color, float radio, boolean proyectarSombras, boolean sombraDinamica) {
        super(intensidad, color, radio, proyectarSombras, sombraDinamica);
        this.resolucionMapaSombra = QGlobal.SOMBRAS_OMNI_DIRECCIONAL_MAPA_ANCHO;
    }

    @Override
    public QLuz clone() {
        QLuz newLight = new QLuzPuntual(energia, color, radio, proyectarSombras, sombraDinamica);
        newLight.entidad = this.entidad.clone();
        newLight.setEnable(this.enable);
        newLight.setResolucionMapaSombra(resolucionMapaSombra);       
        return newLight;
    }

}
