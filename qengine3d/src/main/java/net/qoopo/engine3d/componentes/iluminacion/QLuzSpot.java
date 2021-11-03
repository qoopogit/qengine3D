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
    private QVector3 directionTransformada = new QVector3(0, -1, 0);
    private float anguloExterno = (float) Math.toRadians(75.0f);
    private float anguloInterno = (float) Math.toRadians(75.0f);

    public QLuzSpot() {
        this.resolucionMapaSombra = QGlobal.SOMBRAS_FOCOS_MAPA_ANCHO;
    }

    public QLuzSpot(QVector3 direction, float angulo, float anguloInterno) {
        this.direction = direction;
        this.anguloExterno = angulo;
        this.anguloInterno = angulo;
        this.resolucionMapaSombra = QGlobal.SOMBRAS_FOCOS_MAPA_ANCHO;
    }

    public QLuzSpot(float intensidad, QColor color, float radio, QVector3 direction, float angulo, float anguloInterno, boolean proyectarSombras, boolean sombraDinamica) {
        super(intensidad, color, radio, proyectarSombras, sombraDinamica);
        this.direction = direction;
        this.anguloExterno = angulo;
        this.anguloInterno = anguloInterno;
        this.resolucionMapaSombra = QGlobal.SOMBRAS_FOCOS_MAPA_ANCHO;
    }

    public QVector3 getDirection() {
        return direction;
    }

    public void setDirection(QVector3 direction) {
        this.direction = direction;
    }

    public void setDirection(float x, float y, float z) {
        this.direction.set(x, y, z);
    }

    public QVector3 getDirectionTransformada() {
        return directionTransformada;
    }

    public void setDirectionTransformada(QVector3 directionTransformada) {
        this.directionTransformada = directionTransformada;
    }

    public float getAnguloExterno() {
        return anguloExterno;
    }

    public void setAnguloExterno(float anguloExterno) {
        this.anguloExterno = anguloExterno;
    }

    public float getAnguloInterno() {
        return anguloInterno;
    }

    public void setAnguloInterno(float anguloInterno) {
        this.anguloInterno = anguloInterno;
    }

    @Override
    public QLuz clone() {
        QLuzSpot newLight = new QLuzSpot(energia, color, radio, direction.clone(), anguloExterno, anguloInterno, proyectarSombras, sombraDinamica);
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
