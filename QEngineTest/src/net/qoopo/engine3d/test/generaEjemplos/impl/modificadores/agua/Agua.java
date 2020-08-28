/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.modificadores.agua;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.modificadores.procesadores.agua.QProcesadorAguaSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class Agua extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;

            int anchoReflejo = 800;
            int altoReflejo = 600;
            //luz ambiente
//            mundo.setLuzAmbiente(0.5f);

            QEntidad sol = new QEntidad("Sol");
            sol.agregarComponente(new QLuzDireccional(1.5f, QColor.WHITE, true, 1000, new QVector3(0, -1, 0)));
            mundo.agregarEntidad(sol);

//        //cielo
            QEntidad cielo = new QEntidad("Cielo");
            QGeometria cieloG = new QEsfera(mundo.UM.convertirPixel(500, QUnidadMedida.METRO));
            QMaterialBas mat = new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo3.jpg"))));
            mat.setFactorEmision(1);
            QMaterialUtil.aplicarMaterial(cieloG, mat);
            QUtilNormales.invertirNormales(cieloG);
            cielo.agregarComponente(cieloG);

            mundo.agregarEntidad(cielo);

            QMaterialBas material = new QMaterialBas("Lago");
//            material.setTransparencia(true);
//            material.setTransAlfa(0.4f);//40% ( transparencia del 60%)
            material.setColorDifusa(new QColor(1, 0, 0, 0.7f));
            material.setSpecularExponent(64);
            material.setDifusaProyectada(true); //el mapa de reflexion es proyectado

            QEntidad agua = new QEntidad("Agua");
//puedo agregar la razon que sea necesaria no afectara a  la textura de reflexixon porq esta calcula las coordenadas UV en tiempo de renderizado
            agua.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlano(150, 150), material));

            QProcesadorAguaSimple procesador = new QProcesadorAguaSimple(mundo, anchoReflejo, altoReflejo);
            agua.agregarComponente(procesador);
            material.setMapaNormal(new QProcesadorSimple(procesador.getTextNormal()));
            material.setMapaDifusa(procesador.getTextSalida());
            agua.mover(0, 0.1f, 6);
            mundo.agregarEntidad(agua);
        } catch (IOException ex) {
            Logger.getLogger(Agua.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
