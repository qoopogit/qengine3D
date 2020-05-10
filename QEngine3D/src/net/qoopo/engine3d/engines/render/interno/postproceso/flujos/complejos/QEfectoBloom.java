/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.flujos.complejos;

import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.blur.QProcesadorBlur;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorBrillo;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorCombina;
import net.qoopo.engine3d.engines.render.interno.postproceso.procesos.color.QProcesadorContraste;

/**
 * Realiza le efecto de postprocesamiento de Bloom
 *
 * @author alberto
 */
public class QEfectoBloom extends QRenderEfectos {

    private QProcesadorBlur blur = null;
    private QProcesadorBrillo brillo = null;
    private QProcesadorCombina combina = null;
    private QProcesadorContraste contraste = null;

    public QEfectoBloom() {
    }

    @Override
    public QFrameBuffer ejecutar(QFrameBuffer buffer) {//
//        int ancho = buffer.getAncho();
//        int alto = buffer.getAlto();
        try {
//            buffer = QFrameBuffer.copiar(buffer, ancho / 4, alto / 4);
            if (brillo == null || (brillo.getBufferSalida().getAncho() != buffer.getAncho() && brillo.getBufferSalida().getAlto() != buffer.getAlto())) {
                brillo = new QProcesadorBrillo(buffer.getAncho(), buffer.getAlto());
            }
            if (blur == null) {
                blur = new QProcesadorBlur(1.0f, 1);
            }
            if (combina == null || (combina.getBufferSalida().getAncho() != buffer.getAncho() && combina.getBufferSalida().getAlto() != buffer.getAlto())) {
                combina = new QProcesadorCombina(buffer.getAncho(), buffer.getAlto());
            }
            if (contraste == null || (contraste.getBufferSalida().getAncho() != buffer.getAncho() && contraste.getBufferSalida().getAlto() != buffer.getAlto())) {
                contraste = new QProcesadorContraste(buffer.getAncho(), buffer.getAlto(), 0.2f);
            }

            brillo.procesar(buffer);
            blur.procesar(brillo.getBufferSalida());
            combina.procesar(buffer, blur.getBufferSalida());
//escalo para acelerar 
//            blur.procesar(QFrameBuffer.copiar(brillo.getBufferSalida(), ancho / 4, alto / 4));
//            combina.procesar(buffer, QFrameBuffer.copiar(blur.getBufferSalida(), ancho, alto));
//--------------
            contraste.procesar(combina.getBufferSalida());
            return contraste.getBufferSalida();
//            return QFrameBuffer.copiar(contraste.getBufferSalida(), ancho, alto);
        } catch (Exception e) {
            return buffer;
        }
    }

}
