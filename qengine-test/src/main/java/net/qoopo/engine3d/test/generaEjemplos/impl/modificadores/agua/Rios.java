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
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.modificadores.procesadores.agua.QProcesadorAguaSimple;
import net.qoopo.engine3d.componentes.terreno.QTerreno;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.test.juegotest.generadores.GenMonitores;
import net.qoopo.engine3d.test.juegotest.mundo.niveles.NivelTest;

/**
 *
 * @author alberto
 */
public class Rios extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;

            int anchoReflejo = 800;
            int altoReflejo = 600;

            QEntidad sol = new QEntidad("Sol");
            sol.agregarComponente(new QLuzDireccional(1.5f, QColor.WHITE, 1000, new QVector3(0, -1, 0), true, true));
            mundo.agregarEntidad(sol);
//        //cielo
            QEntidad cielo = new QEntidad("Cielo");
            QGeometria cieloG = new QEsfera(mundo.UM.convertirPixel(500, QUnidadMedida.METRO));
            QMaterialBas mat = new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo.jpg"))));
            QMaterialUtil.aplicarMaterial(cieloG, mat);
            QUtilNormales.invertirNormales(cieloG);
            cielo.agregarComponente(cieloG);

            mundo.agregarEntidad(cielo);

//        //un planeta tierra
//        QEntidad planeta = new QEntidad("planeta");
//        QGeometria planteG = new QEsfera(mundo.UM.convertirPixel(5, QUnidadMedida.KILOMETRO));
//        QMaterialUtil.aplicarTexturaEsfera(planteG, new File(QGlobal.RECURSOS+"texturas/planetas/tierra/text4/tierra.jpg"));
////        QUtilNormales.invertirNormales(cieloG);
//        planeta.agregarComponente(planteG);
//        planeta.mover(-10000, 0, 10000);
//        mundo.agregarEntidad(planeta);
//CREACION DEL LAGO
            System.out.println("Cargando Lago");
//Lago
            QMaterialBas material = new QMaterialBas("Lago");
//            material.setTransparencia(true);
//            material.setTransAlfa(0.4f);//40% ( transparencia del 60%)
            material.setColorBase(new QColor(1, 0, 0, 0.7f));

            QEntidad agua = new QEntidad("Agua");
            agua.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlano(350, 350), material));
            QProcesadorAguaSimple procesador = new QProcesadorAguaSimple(mundo, anchoReflejo, altoReflejo);
            agua.agregarComponente(procesador);
            material.setMapaColor(procesador.getTextSalida());
            material.setMapaNormal(new QProcesadorSimple(procesador.getTextNormal()));
            agua.mover(0, 0.1f, 6);

            mundo.agregarEntidad(agua);

//un monitor para ver el mapa de reflexion
//texEspejo1
            QEntidad monitor = GenMonitores.crearMonitorTipo1(procesador.getTextReflexion());
            monitor.mover(-48, 48, -45);
            monitor.rotar((float) Math.toRadians(-45), (float) Math.toRadians(45), 0);

            mundo.agregarEntidad(monitor);

//el terreno generado con mapas de altura
            System.out.println("cargando Terreno");
            QEntidad entidadTerreno = new QEntidad("Terreno");
            QTerreno terreno = new QTerreno();
            entidadTerreno.agregarComponente(terreno);
            QTextura textura = null;
            try {
                textura = new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/terreno/text4.jpg")));
                textura.setMuestrasU(3);
                textura.setMuestrasV(3);
            } catch (IOException ex) {
                Logger.getLogger(NivelTest.class.getName()).log(Level.SEVERE, null, ex);
            }
            terreno.generar(new File(QGlobal.RECURSOS + "mapas_altura/map5.png"), 1, -10f, 20f, textura, 1);
            mundo.agregarEntidad(entidadTerreno);

            System.out.println("cargando arboles");
//arboles
            QGeometria pinoG = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VEGETACION/EXTERIOR/baja_calidad/pino/lowpolytree.obj")).get(0));
            QEntidad pino1 = new QEntidad();
            pino1.agregarComponente(pinoG.clone());
            pino1.mover(78, 20, 0);
            pino1.escalar(4, 4, 4);
            mundo.agregarEntidad(pino1);

            QEntidad pino2 = new QEntidad();
            pino2.agregarComponente(pinoG.clone());
            pino2.mover(78, 20, 25);
            pino2.escalar(4, 4, 4);
            mundo.agregarEntidad(pino2);

            QEntidad pino3 = new QEntidad();
            pino3.agregarComponente(pinoG.clone());
            pino3.mover(90, 25, 15);
            pino3.escalar(4, 4, 4);
            mundo.agregarEntidad(pino3);

            QEntidad pino4 = new QEntidad();
            pino4.agregarComponente(pinoG.clone());
            pino4.mover(60, 25, 60);
            pino4.escalar(4, 4, 4);
            mundo.agregarEntidad(pino4);

            QEntidad pino5 = new QEntidad();
            pino5.agregarComponente(pinoG.clone());
            pino5.mover(45, 25, 68);
            pino5.escalar(4, 4, 4);
            mundo.agregarEntidad(pino5);
            System.out.println("Cargado");
        } catch (IOException ex) {
            Logger.getLogger(Rios.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
