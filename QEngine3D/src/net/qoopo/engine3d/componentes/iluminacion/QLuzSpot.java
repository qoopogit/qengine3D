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
public class QLuzSpot extends QLuz {

    private QVector3 direction = new QVector3(0, -1, 0);
    private float angulo = (float) Math.toRadians(60.0f);

    public QLuzSpot() {
        this.resolucionMapaSombra = QGlobal.SOMBRAS_FOCOS_MAPA_ANCHO;
    }

    public QLuzSpot(float energy, QColor color, boolean getNewId, float radio, QVector3 direction, float angulo) {
        super(energy, color, getNewId, radio);
        this.direction = direction;
        this.angulo = angulo;
        this.resolucionMapaSombra = QGlobal.SOMBRAS_FOCOS_MAPA_ANCHO;
    }

    public QVector3 getDirection() {
        return direction;
    }

    public void setDirection(QVector3 direction) {
        this.direction = direction;
    }
    public void setDirection(float x, float y, float z) {
        this.direction.setXYZ(x, y, z);
    }

    public float getAngulo() {
        return angulo;
    }

    public void setAngulo(float angulo) {
        this.angulo = angulo;
    }

    @Override
    public QLuz clone() {
        QLuzSpot newLight = new QLuzSpot(energia, color, false, radio, direction.clone(), angulo);
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
