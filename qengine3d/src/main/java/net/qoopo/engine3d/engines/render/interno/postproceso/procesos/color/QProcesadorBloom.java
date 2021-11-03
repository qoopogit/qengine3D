/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color;

import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.QPostProceso;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur.QProcesadorBlur;

/**
 * Realiza una modificación del color final de la imagen realzando el contraste
 *
 * @author alberto
 */
public class QProcesadorBloom extends QPostProceso {

    private QProcesadorBlur blur = null;
    private QProcesadorBrillo brillo = null;
    private QProcesadorCombina combina = null;
    private QProcesadorContraste contraste = null;

    public QProcesadorBloom(int ancho, int alto) {
        this.bufferSalida = new QTextura(ancho, alto);
        this.blur = new QProcesadorBlur(ancho, alto, 10);
        this.brillo = new QProcesadorBrillo(ancho, alto, 0.85f);
        this.combina = new QProcesadorCombina(ancho, alto);
        this.contraste = new QProcesadorContraste(ancho, alto, 0.3f);
    }

    public QProcesadorBloom(int ancho, int alto, float brillo) {
        this.bufferSalida = new QTextura(ancho, alto);
        this.blur = new QProcesadorBlur(ancho, alto, 10);
        this.brillo = new QProcesadorBrillo(ancho, alto, brillo);
        this.combina = new QProcesadorCombina(ancho, alto);
        this.contraste = new QProcesadorContraste(ancho, alto, 0.3f);
    }

    @Override
    public void procesar(QTextura... buffer) {
        try {
            QTextura textura = buffer[0];
            brillo.procesar(textura);
            blur.procesar(brillo.getBufferSalida());
            combina.procesar(textura, blur.getBufferSalida());
            contraste.procesar(combina.getBufferSalida());
            bufferSalida.cargarTextura(contraste.getBufferSalida().getImagen());
//            bufferSalida.cargarTextura(combina.getBufferSalida().getImagen());
//            bufferSalida.cargarTextura(blur.getBufferSalida().getImagen());
        } catch (Exception e) {

        }
//        bufferSalida.actualizarTextura();
    }

}
