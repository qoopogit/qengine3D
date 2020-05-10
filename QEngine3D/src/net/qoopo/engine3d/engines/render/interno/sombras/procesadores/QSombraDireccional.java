/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.sombras.procesadores;

import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.engines.render.interno.sombras.renders.QRenderSombras;

/**
 * Procesador de sombras para luces Direccionales
 *
 * @author alberto
 */
public class QSombraDireccional extends QProcesadorSombra {

    //un solo render de sombras pues solo hay una direccion
    private QRenderSombras renderSombra;

    public QSombraDireccional(QEscena mundo, QLuzDireccional luz, QCamara camaraRender, int ancho, int alto) {
        super(mundo, luz, ancho, alto);
        try {
            renderSombra = new QRenderSombras(QRenderSombras.DIRECIONALES, mundo, luz, camaraRender, ancho, alto);
            renderSombra.resize();
            renderSombra.limpiar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public float factorSombra(QVector3 vector, QEntidad entidad) {
        return renderSombra.factorSombra(vector, entidad);
    }

    @Override
    public void generarSombras() {
        if (renderSombra != null && (actualizar || dinamico)) {
            renderSombra.update();
        }
        actualizar = false;
    }

    public void actualizarDireccion(QVector3 direccion) {
        if (renderSombra != null) {
            renderSombra.setDireccion(direccion);
        }
    }

    @Override
    public void destruir() {
        renderSombra = null;
    }

}
