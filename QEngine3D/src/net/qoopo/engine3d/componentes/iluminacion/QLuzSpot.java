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

    public QLuzSpot(float intensidad, QColor color,  float radio, QVector3 direction, float angulo, boolean proyectarSombras, boolean sombraDinamica) {
        super(intensidad, color,  radio, proyectarSombras, sombraDinamica);
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
        QLuzSpot newLight = new QLuzSpot(energia, color, radio, direction.clone(), angulo, proyectarSombras, sombraDinamica);
        newLight.entidad = this.entidad.clone();
        newLight.setEnable(this.enable);
        newLight.setResolucionMapaSombra(resolucionMapaSombra);
        return newLight;
    }

    @Override
    public void destruir() {
        super.destruir();
        direction = null;
    }

}
