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

    public boolean material;
    public boolean sombras;
    public int tipoVista = VISTA_PHONG;

    public boolean dibujarLuces = true;
    public boolean verCarasTraseras = false;// inverso de BackFaceCulling

    public boolean forzarSuavizado = false;
    public boolean forzarResolucion = false;
    public int ancho = -1;
    public int alto = -1;
    public boolean showNormal = false;
    
    public boolean zSort = true;
    public boolean normalMapping = true;

    public QOpcionesRenderer() {
        tipoVista = VISTA_PHONG;
        material = true;
        sombras = false;
    }

    public QOpcionesRenderer(boolean material, boolean sombras) {
        this.material = material;
        this.sombras = sombras;
    }

}
