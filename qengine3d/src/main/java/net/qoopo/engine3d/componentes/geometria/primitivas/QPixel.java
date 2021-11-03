package net.qoopo.engine3d.componentes.geometria.primitivas;

import java.io.Serializable;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.material.QMaterial;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;

/**
 * Pixel que pertenece al buffer de pixel
 *
 * @author alberto
 */
public class QPixel implements Serializable {

    private boolean dibujar = false;
    public float u, v;
    public QVector4 ubicacion = new QVector4();
    public QVector3 normal = new QVector3();
    public QVector3 arriba = new QVector3();//usada para el mapa normal con los somreadores nodo y no en el raster
    public QVector3 derecha = new QVector3();//usada para el mapa normal con los somreadores nodo y no en el raster
    public QMaterial material;
    public QPrimitiva primitiva;
    public QEntidad entidad;

    public QPixel() {
    }

    public boolean isDibujar() {
        return dibujar;
    }

    public void setDibujar(boolean dibujar) {
        this.dibujar = dibujar;
    }

    public float getX() {
        return ubicacion.x;
    }

    public void setX(float x) {
        this.ubicacion.x = x;
    }

    public float getY() {
        return ubicacion.y;
    }

    public void setY(float y) {
        this.ubicacion.y = y;
    }

    public float getZ() {
        return ubicacion.z;
    }

    public void setZ(float z) {
        this.ubicacion.z = z;
    }

    public float getU() {
        return u;
    }

    public void setU(float u) {
        this.u = u;
    }

    public float getV() {
        return v;
    }

    public void setV(float v) {
        this.v = v;
    }

    public QVector3 getNormal() {
        return normal;
    }

    public void setNormal(QVector3 normal) {
        this.normal = normal;
    }

    public QVector3 getArriba() {
        return arriba;
    }

    public void setArriba(QVector3 arriba) {
        this.arriba = arriba;
    }

    public QVector3 getDerecha() {
        return derecha;
    }

    public void setDerecha(QVector3 derecha) {
        this.derecha = derecha;
    }

    public QMaterial getMaterial() {
        return material;
    }

    public void setMaterial(QMaterial material) {
        this.material = material;
    }

    public QPrimitiva getPrimitiva() {
        return primitiva;
    }

    public void setPoligono(QPoligono poligono) {
        this.primitiva = poligono;
    }

    public QEntidad getEntidad() {
        return entidad;
    }

    public void setEntidad(QEntidad entidad) {
        this.entidad = entidad;
    }

}
