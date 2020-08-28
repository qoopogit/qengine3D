/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest.generadores;

import java.io.File;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.math.QColor;

/**
 *
 * @author alberto
 */
public class GenMonitores {

    public static QEntidad crearMonitorTipo1(QTextura textura) {
        QEntidad monitor = new QEntidad("Monitor");
        QTextura textMonitor = null;
        QMaterialBas materialCarcasa = new QMaterialBas();
        materialCarcasa.setColorDifusa(new QColor(1, 0.5f, 0.5f, 0.5f));
        try {
            textMonitor = new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/computadores/Monitor-300x191.gif")));
            materialCarcasa.setMapaDifusa(new QProcesadorSimple(textMonitor));
        } catch (Exception e) {
            e.printStackTrace();
        }
        monitor.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlano(3 * 600 / 800 + 0.2f, 3 + 0.1f), materialCarcasa));

        QEntidad pantalla = new QEntidad("pantalla");
        QMaterialBas pantallMat = new QMaterialBas();
        pantallMat.setMapaDifusa(new QProcesadorSimple(textura));
        pantallMat.setColorDifusa(new QColor(1, 0.2f, 0.2f, 0.2f));
        pantalla.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlano(3 * 600 / 800, 3), pantallMat));
        pantalla.mover(0, 0.02f, 0);
        monitor.agregarHijo(pantalla);

        return monitor;
    }
}
