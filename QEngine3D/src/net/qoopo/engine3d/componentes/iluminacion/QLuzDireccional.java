/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.iluminacion;

import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QLuzDireccional extends QLuz {

    private QVector3 direction = new QVector3(0, -1, 0);

    public QLuzDireccional(QVector3 direccion) {
        this.direction = direccion;
        this.resolucionMapaSombra = QGlobal.SOMBRAS_DIRECCIONAL_MAPA_ANCHO;
    }

    public QLuzDireccional(float energy, QColor color, boolean getNewId, float radio) {
        super(energy, color, getNewId, radio);
        this.resolucionMapaSombra = QGlobal.SOMBRAS_DIRECCIONAL_MAPA_ANCHO;
    }

    public QLuzDireccional(float energy, QColor color, boolean getNewId, float radio, QVector3 direccion) {
        super(energy, color, getNewId, radio);
        this.direction = direccion;
        this.resolucionMapaSombra = QGlobal.SOMBRAS_DIRECCIONAL_MAPA_ANCHO;
    }

    public QVector3 getDirection() {
        return direction;
    }

    public void setDirection(QVector3 direction) {
        this.direction = direction;
    }
    public void setDirection(float x, float y , float z) {
        this.direction.setXYZ(x, y, z);
    }

    @Override
    public QLuz clone() {
        QLuz newLight = new QLuzDireccional(energia, color, false, radio, direction.clone());
        newLight.entidad = this.entidad.clone();
        newLight.setEnable(this.enable);
        newLight.setProyectarSombras(this.proyectarSombras);
        newLight.setResolucionMapaSombra(resolucionMapaSombra);
        newLight.setSombraDinamica(sombraDinamica);
        return newLight;
    }

    @Override
    public void destruir() {
        super.destruir();
        direction = null;
    }

}
