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
    private QVector3 directionTransformada = new QVector3(0, -1, 0);

    public QLuzDireccional(QVector3 direccion) {
        this.direction = direccion;
        this.resolucionMapaSombra = QGlobal.SOMBRAS_DIRECCIONAL_MAPA_ANCHO;
    }

    public QLuzDireccional(float intensidad, QColor color, float radio, boolean proyectarSombras, boolean sombraDinamica) {
        super(intensidad, color, radio, proyectarSombras, sombraDinamica);
        this.resolucionMapaSombra = QGlobal.SOMBRAS_DIRECCIONAL_MAPA_ANCHO;
    }

    public QLuzDireccional(float energia, QColor color, float radio, QVector3 direccion, boolean proyectarSombras, boolean sombraDinamica) {
        super(energia, color, radio, proyectarSombras, sombraDinamica);
        this.direction = direccion;
        this.resolucionMapaSombra = QGlobal.SOMBRAS_DIRECCIONAL_MAPA_ANCHO;
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

    @Override
    public QLuz clone() {
        QLuz newLight = new QLuzDireccional(energia, color, radio, direction.clone(), proyectarSombras, sombraDinamica);
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
