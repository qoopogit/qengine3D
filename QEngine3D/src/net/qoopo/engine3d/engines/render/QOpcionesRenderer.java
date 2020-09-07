/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render;

/**
 *
 * @author alberto
 */
public class QOpcionesRenderer {

    public static final int VISTA_WIRE = 0;
    public static final int VISTA_FLAT = 1;
    public static final int VISTA_PHONG = 2;

    private boolean material;
    private boolean sombras;
    private int tipoVista = VISTA_PHONG;
    private boolean dibujarLuces = false;
    private boolean verCarasTraseras = false;// inverso de BackFaceCulling
    private boolean forzarSuavizado = false;
    private boolean forzarResolucion = false;
    private int ancho = -1;
    private int alto = -1;
    private boolean zSort = true;
    private boolean normalMapping = true;

    public QOpcionesRenderer() {
        tipoVista = VISTA_PHONG;
        material = true;
        sombras = false;
    }

    public QOpcionesRenderer(boolean material, boolean sombras) {
        this.material = material;
        this.sombras = sombras;
    }

    public boolean isMaterial() {
        return material;
    }

    public void setMaterial(boolean material) {
        this.material = material;
    }

    public boolean isSombras() {
        return sombras;
    }

    public void setSombras(boolean sombras) {
        this.sombras = sombras;
    }

    public int getTipoVista() {
        return tipoVista;
    }

    public void setTipoVista(int tipoVista) {
        this.tipoVista = tipoVista;
    }

    public boolean isDibujarLuces() {
        return dibujarLuces;
    }

    public void setDibujarLuces(boolean dibujarLuces) {
        this.dibujarLuces = dibujarLuces;
    }

    public boolean isVerCarasTraseras() {
        return verCarasTraseras;
    }

    public void setVerCarasTraseras(boolean verCarasTraseras) {
        this.verCarasTraseras = verCarasTraseras;
    }

    public boolean isForzarSuavizado() {
        return forzarSuavizado;
    }

    public void setForzarSuavizado(boolean forzarSuavizado) {
        this.forzarSuavizado = forzarSuavizado;
    }

    public boolean isForzarResolucion() {
        return forzarResolucion;
    }

    public void setForzarResolucion(boolean forzarResolucion) {
        this.forzarResolucion = forzarResolucion;
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public boolean iszSort() {
        return zSort;
    }

    public void setzSort(boolean zSort) {
        this.zSort = zSort;
    }

    public boolean isNormalMapping() {
        return normalMapping;
    }

    public void setNormalMapping(boolean normalMapping) {
        this.normalMapping = normalMapping;
    }

}
