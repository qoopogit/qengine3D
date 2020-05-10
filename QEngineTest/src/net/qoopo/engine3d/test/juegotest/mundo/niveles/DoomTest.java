/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest.mundo.niveles;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.test.juegotest.monstruos.QDoomMonster;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.fisica.colisiones.QComponenteColision;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.terreno.QTerreno;
import net.qoopo.engine3d.core.escena.QEscenario;

/**
 *
 * @author alberto
 */
public class DoomTest extends QEscenario {

    private static boolean monstruoDoom1 = true;
    private static boolean monstruoQuake1 = true;
    private static boolean monstruoQuake2 = true;

    public void cargar(QEscena universo) {
        QLogger.info("Cargando nivel...");
        cargarTexturas();
//        universo.luzAmbiente = 0.5f;
        crearTerreno(universo);
//        crearLago1(universo);
//        crearLago2(universo);
        crearObjetosAleatorios(terreno, universo);
        QLogger.info("Configurando neblina");
//        QEscena.INSTANCIA.neblina = new QNeblina(true, QColor.GRAY, 0.015f);// como 100 metros
//        QEscena.INSTANCIA.neblina = new QNeblina(true, QColor.GRAY, 0.01f);// como 2 metros

//        crearCielo(universo);
        QLogger.info("Nivel cargado");
    }

    private void crearObjetosAleatorios(QTerreno terreno, QEscena universo) {

        Random rnd = new Random();

        float x = 0, z = 0, y = 0;
        for (int i = 1; i <= 25; i++) {

            if (monstruoDoom1) {
                if (i % 10 == 0) {

                }
            }
            if (monstruoQuake1) {
                if (i % 5 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);
                    QEntidad doomMonster = QDoomMonster.quakeMonster();
                    doomMonster.mover(x, y, z);
//                    doomMonster.mover(-100, y + 0.8f, 10);
                    doomMonster.getTransformacion().setEscala(new QVector3(0.04f, 0.04f, 0.04f));
                    universo.agregarEntidad(doomMonster);
                }

            }

            if (monstruoQuake2) {
                if (i % 5 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);
                    QEntidad doomMonster = QDoomMonster.quakeMonster2();
                    doomMonster.mover(x, y, z);
//                    doomMonster.mover(-100, y + 0.8f, 10);
                    doomMonster.getTransformacion().setEscala(new QVector3(0.04f, 0.04f, 0.04f));
                    universo.agregarEntidad(doomMonster);
                }

            }

        }
    }

    private void crearTerreno(QEscena universo) {
        //el terreno generado con mapas de altura
        QLogger.info("Creando terreno...");

        QEntidad entidadTerreno = new QEntidad("Terreno");

        terreno = new QTerreno();
        entidadTerreno.agregarComponente(terreno);
        QTextura textura = null;
        try {
            textura = QGestorRecursos.getTextura("terreno");
        } catch (Exception ex) {

        }

        try {

            BufferedImage img1 = ImageIO.read(new File(QGlobal.RECURSOS + "mapas_altura/map1.png"));
            textura.setMuestrasU(20);
            textura.setMuestrasV(20);
            terreno.generar(img1, 1, 0f, 0f, textura, 2);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        QObjetoRigido terrenoRigidez = new QObjetoRigido(QObjetoDinamico.ESTATICO);
        terrenoRigidez.setMasa(200, QVector3.zero.clone());
        terrenoRigidez.tipoContenedorColision = QComponenteColision.TIPO_CONTENEDOR_AABB;
        entidadTerreno.agregarComponente(terrenoRigidez);
        universo.agregarEntidad(entidadTerreno);
        QLogger.info("Terreno cargado...");

    }

    private void cargarTexturas() {
        QLogger.info("Cargando texturas...");
        QGestorRecursos.cargarTextura("terreno", QGlobal.RECURSOS + "texturas/terreno/text4.jpg");
        QGestorRecursos.cargarTextura("lagoNormal", QGlobal.RECURSOS + "texturas/agua/matchingNormalMap.png");
        QLogger.info("Texturas cargadas...");
    }

    public QTerreno getTerreno() {
        return terreno;
    }

    public void setTerreno(QTerreno terreno) {
        this.terreno = terreno;
    }

}
