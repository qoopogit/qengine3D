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
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.audio.openal.QBufferSonido;
import net.qoopo.engine3d.componentes.audio.openal.QEmisorAudio;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaIndexada;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCaja;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.modificadores.procesadores.agua.QProcesadorAguaSimple;
import net.qoopo.engine3d.componentes.terreno.QTerreno;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.cielo.QCielo;
import net.qoopo.engine3d.core.cielo.QEsferaCielo;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.escena.QEscenario;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.test.juegotest.generadores.GenFuego;
import net.qoopo.engine3d.test.juegotest.generadores.GeneradorCasas;
import net.qoopo.engine3d.test.juegotest.generadores.GeneradorLamparas;

/**
 *
 * @author alberto
 */
public class NivelTest extends QEscenario {

    private static boolean pinos = true;
    private static boolean qubbie = true;
    private static boolean arbolesMuertos = true;
    private static boolean arboles1 = true;
    private static boolean arboles2 = true;
    private static boolean arboles3 = true;
    private static boolean rocas = true;
    private static boolean lamparas = true;
    private static boolean casas_1p = true;
    private static boolean casas_2p = true;
    private static boolean fogatas = false;

    private int anchoReflejo = 800;
    private int altoReflejo = 600;

    public void cargar(QEscena universo) {
        QLogger.info("Cargando nivel...");
        cargarTexturas();
        crearTerreno(universo);
//        crearLago1(universo);
//        crearLago2(universo);
//        crearObjetosAleatorios(terreno, universo);
//        crearCielo(universo);
        QLogger.info("Nivel cargado");
    }

    private void crearObjetosAleatorios(QTerreno terreno, QEscena universo) {

        GeneradorCasas generador = new GeneradorCasas();

        QLogger.info("  Cargando geometrías..");
        QGeometria pinoG = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VEGETACION/EXTERIOR/baja_calidad/pino/lowpolytree.obj")).get(0));
        QGeometria arbolG = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VEGETACION/EXTERIOR/alta_calidad/Tree/Tree.obj")).get(0));
        QGeometria arbol2G = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VEGETACION/EXTERIOR/baja_calidad/tree/tree.obj")).get(0));
        QGeometria arbolMuerto = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VEGETACION/EXTERIOR/baja_calidad/dead_tree/DeadTree.obj")).get(0));
        QGeometria roca1 = QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/TERRENO/baja_calidad/Rock1/Rock1.obj")).get(1));
//        List<QGeometria> arbol3 = CargaEstatica.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VEGETACION/EXTERIOR/baja_calidad/tree2/part.obj"));

        Random rnd = new Random();

        QLogger.info("  Cargando audios..");
        QBufferSonido bufFuego = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/fire.ogg", "au_beep");
        QBufferSonido bufBeep = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/beep.ogg", "au_beep");
        QBufferSonido bufAves = QGestorRecursos.cargarAudio(QGlobal.RECURSOS + "audio/ogg/aves.ogg", "au_aves");

        float x = 0, z = 0, y = 0;
        for (int i = 1; i <= 400; i++) {
            //FOGATA
            if (fogatas) {
                if (i % 200 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);
                    QEntidad fogata = GenFuego.crearFogata1();
                    fogata.mover(x, y, z);
                    try {
                        QEmisorAudio emisorAudio = new QEmisorAudio(true, false);
                        emisorAudio.setPosition(new QVector3(x, y, z));
                        emisorAudio.setBuffer(bufFuego.getBufferId());
                        emisorAudio.setReproducirAlInicio(true);
                        emisorAudio.setGain(0.1f);
                        fogata.agregarComponente(emisorAudio);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    universo.agregarEntidad(fogata);
                }
            }
//      CASA 1 piso
            if (casas_1p) {
                if (i % 20 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);
                    QEntidad casa = generador.casa1();
                    casa.mover(x, y + 1.25f, z);
                    universo.agregarEntidad(casa);
                }
            }
            if (casas_2p) //casa 2 pisos
            {
                if (i % 30 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);
                    QEntidad casa = generador.casa2Pisos();
                    casa.mover(x, y + 1.25f, z);
                    universo.agregarEntidad(casa);
                }
            }

//Pino
            if (pinos) {
                if (i % 20 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);

                    QEntidad pino = new QEntidad();
                    pino.agregarComponente(pinoG);
                    pino.mover(x, y + 2, z);

                    QColisionMallaConvexa colision = new QColisionMallaConvexa(pinoG);
                    pino.agregarComponente(colision);
                    QObjetoRigido pinoRigido = new QObjetoRigido(QObjetoDinamico.ESTATICO);
                    pinoRigido.setFormaColision(colision);
                    pino.agregarComponente(pinoRigido);

                    try {
//                        manager.addSoundBuffer(buffBeep);
                        QEmisorAudio sourceBeep = new QEmisorAudio(true, false);
                        sourceBeep.setPosition(new QVector3(x, y + 2, z));
                        sourceBeep.setBuffer(bufAves.getBufferId());
                        sourceBeep.setReproducirAlInicio(true);
                        sourceBeep.setGain(0.02f);
//                        sourceBeep.play();
                        pino.agregarComponente(sourceBeep);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    universo.agregarEntidad(pino);
                }
            }
////ARBOL
            if (arboles1) {
                if (i % 30 == 0) {

                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);

                    QEntidad arbol1 = new QEntidad();
                    arbol1.agregarComponente(arbolG);

                    QColisionMallaConvexa colision = new QColisionMallaConvexa(arbolG);
                    arbol1.agregarComponente(colision);

                    arbol1.mover(x, y + 2, z);
                    arbol1.escalar(2f, 2f, 2f);
                    QObjetoRigido rigi = new QObjetoRigido(QObjetoDinamico.ESTATICO);
                    rigi.setFormaColision(colision);
                    arbol1.agregarComponente(rigi);

                    universo.agregarEntidad(arbol1);
                }
            }

            //ARBOL 2
            if (arboles2) {
                if (i % 20 == 0) {

                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);

                    QEntidad arbol1 = new QEntidad();
                    arbol1.agregarComponente(arbol2G);
                    arbol1.mover(x, y, z);
                    arbol1.escalar(0.01f, 0.01f, 0.01f);
                    universo.agregarEntidad(arbol1);
                }
            }

            //ARBOL muerto
            if (arbolesMuertos) {
                if (i % 20 == 0) {

                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);

                    QEntidad arbol1 = new QEntidad("arbol_muerto");
                    arbol1.agregarComponente(arbolMuerto);
                    arbol1.mover(x, y, z);
                    arbol1.escalar(0.3f, 0.3f, 0.3f);
                    universo.agregarEntidad(arbol1);
                }
            }

            //ARBOL 3
//            if (arboles3) {
//                if (i % 20 == 0) {
//                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
//                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
//                    y = terreno.getAltura(x, z);
//
//                    QEntidad arbol1 = new QEntidad("arbol_3");
//                    arbol1.agregarComponente(QMaterialUtil.aplicarColor(arbol3.get(0), 1, QColor.GREEN, QColor.WHITE, 0, 64));
//                    arbol1.agregarComponente(QMaterialUtil.aplicarColor(arbol3.get(1), 1, QColor.BROWN, QColor.WHITE, 0, 64));
//                    arbol1.mover(x, y, z);
//                    arbol1.escalar(0.4f, 0.4f, 0.4f);
//                    universo.agregarEntidad(arbol1);
//                }
//            }
            //Roca 1
            if (rocas) {
                if (i % 10 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);

                    QEntidad roca = new QEntidad("roca");
                    roca.agregarComponente(roca1);
                    roca.mover(x, y, z);

                    roca.escalar(1, 1, 1);
                    QColisionMallaConvexa colision = new QColisionMallaConvexa(roca1);
                    roca.agregarComponente(colision);
                    QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.ESTATICO);
                    rigido.setFormaColision(colision);
                    roca.agregarComponente(rigido);
                    universo.agregarEntidad(roca);
                }
            }

            //lampara
            if (lamparas) {
                if (i % 10 == 0) {
                    x = rnd.nextFloat() * terreno.getAncho() - terreno.getAncho() - terreno.getInicioX();
                    z = rnd.nextFloat() * terreno.getLargo() - terreno.getLargo() - terreno.getInicioZ();
                    y = terreno.getAltura(x, z);

                    QEntidad lampara = GeneradorLamparas.crearLamparaPiso();
                    lampara.mover(x, y, z);

                    QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.ESTATICO);
                    rigido.setFormaColision(new QColisionCaja(0.25f, 2.25f, 0.25f));
                    lampara.agregarComponente(rigido);

//                    try {
//                        QEmisorAudio emisorAudio = new QEmisorAudio(true, false);
//                        emisorAudio.setPosition(new QVector3(x, y, z));
//                        emisorAudio.setBuffer(bufFuego.getBufferId());
//                        emisorAudio.setGain(0.08f);
//                        emisorAudio.setReproducirAlInicio(true);
//                        lampara.agregarComponente(emisorAudio);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    universo.agregarEntidad(lampara);
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
            // junto varios mapas de altura para generar un solo terreno
            BufferedImage img1 = ImageIO.read(new File(QGlobal.RECURSOS + "mapas_altura/map1.png"));
            BufferedImage img2 = ImageIO.read(new File(QGlobal.RECURSOS + "mapas_altura/map10.png"));
            BufferedImage img3 = ImageIO.read(new File(QGlobal.RECURSOS + "mapas_altura/map11.png"));
//            BufferedImage imgQuito = ImageIO.read(new File("res/mapas_altura/Quito terrain/Quito Height Map (ASTER 30m).png"));
            BufferedImage biFinal = new BufferedImage(img1.getWidth() + img2.getWidth(), img1.getHeight() + img2.getHeight(), img1.getType());
            biFinal.getGraphics().drawImage(img1, 0, 0, null);
            biFinal.getGraphics().drawImage(img2, img1.getWidth(), 0, null);
            biFinal.getGraphics().drawImage(img3, img1.getWidth(), img2.getHeight(), null);
            textura.setMuestrasU(20);
            textura.setMuestrasV(20);
            terreno.generar(biFinal, 1, -5f, 20f, textura, 2);
//            terreno.generar(img1, 1, -5f, 20f, textura, 20, 2);
//            terreno.generar(imgQuito, 1, -5f, 20f, textura, 20, 2);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

//        QColisionTerreno colision = new QColisionTerreno(terreno);
//        entidadTerreno.agregarComponente(colision);
        QColisionMallaIndexada colision = new QColisionMallaIndexada(QUtilComponentes.getGeometria(entidadTerreno));
        QObjetoRigido terrenoRigidez = new QObjetoRigido(QObjetoDinamico.ESTATICO);
        terrenoRigidez.setFormaColision(colision);
        entidadTerreno.agregarComponente(terrenoRigidez);
        universo.agregarEntidad(entidadTerreno);
        QLogger.info("Terreno cargado...");
    }

    private void crearLago1(QEscena universo) {
        //CREACION DEL LAGO
        //Lago
        QMaterialBas material = new QMaterialBas("Lago");
        material.setTransAlfa(0.4f);//40% ( transparencia del 60%)
        material.setColorBase(new QColor(1, 0.f, 0.f, 0.7f));
        material.setSpecularExponent(64);

//        QTextura mapaNormal = null;
//
//        try {
//            mapaNormal = QGestorRecursos.getTextura("lagoNormal");
//            material.setMapaNormal(new QProcesadorSimple(mapaNormal));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        QEntidad agua = new QEntidad("Agua");

        //puedo agregar la razon que sea necesaria no afectara a  la textura de reflexixon porq esta calcula las coordenadas UV en tiempo de renderizado
        agua.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlano(150, 150), material));
        QProcesadorAguaSimple procesador = new QProcesadorAguaSimple(universo, anchoReflejo, altoReflejo);
        agua.agregarComponente(procesador);
        material.setMapaNormal(new QProcesadorSimple(procesador.getTextNormal()));
        material.setMapaColor(procesador.getTextSalida());
        agua.mover(120, 0.1f, -120);
        agua.rotar((float) Math.toRadians(90), 0, 0);
        universo.agregarEntidad(agua);
    }

    private void crearLago2(QEscena universo) {
        //CREACION DEL LAGO
        //Lago
        QMaterialBas material = new QMaterialBas("Lago");

        material.setTransAlfa(0.7f);//70% ( transparencia del 60%)
        material.setColorBase(new QColor(1, 0.f, 0.f, 0.7f));
        material.setSpecularExponent(64);

//        QTextura mapaNormal = null;
//
//        try {
//            mapaNormal = QGestorRecursos.getTextura("lagoNormal");
//            material.setMapaNormal(new QProcesadorSimple(mapaNormal));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        QEntidad agua = new QEntidad("Agua");

        //puedo agregar la razon que sea necesaria no afectara a  la textura de reflexixon porq esta calcula las coordenadas UV en tiempo de renderizado
        agua.agregarComponente(QMaterialUtil.aplicarMaterial(new QPlano(150, 150), material));
        QProcesadorAguaSimple procesador = new QProcesadorAguaSimple(universo, anchoReflejo, altoReflejo);
        agua.agregarComponente(procesador);
        material.setMapaNormal(new QProcesadorSimple(procesador.getTextNormal()));
        material.setMapaColor(procesador.getTextSalida());
        agua.mover(-90, -1, 120);
        agua.escalar(2, 1.3f, 1);

        agua.rotar((float) Math.toRadians(90), 0, 0);

        universo.agregarEntidad(agua);
    }

    private void cargarTexturas() {
        QLogger.info("Cargando texturas...");
        QGestorRecursos.cargarTextura("terreno", QGlobal.RECURSOS + "texturas/terreno/text4.jpg");
        QGestorRecursos.cargarTextura("lagoNormal", QGlobal.RECURSOS + "texturas/agua/matchingNormalMap.png");
        QLogger.info("Texturas cargadas...");
    }

    private void crearCielo(QEscena universo) {
        //cielo
//        QEntidad cielo = new QEntidad("Cielo");
//        QGeometria cieloG = new QEsfera(universo.UM.convertirPixel(500, QUnidadMedida.METRO));
//        QMaterialUtil.aplicarTexturaEsfera(cieloG, new File("res/texturas/cielo/esfericos/cielo3.jpg"));
//        QNormales.invertirNormales(cieloG);
//        cielo.agregarComponente(cieloG);
//
//        universo.agregarEntidad(cielo);
//
//        //luz ambiente
//        universo.luzAmbiente = 0.5f;
//        QLuz luz = new QLuzDireccional(1.5f, QColor.WHITE, true, 1000, new QVector3(0.5f, -1, 0));
//
//        QEntidad sol = new QEntidad("Sol");
//        sol.agregarComponente(luz);
//        universo.agregarEntidad(sol);

        //sol
        QEntidad sol = new QEntidad("Sol");
        QLuzDireccional solLuz = new QLuzDireccional(1.1f, QColor.WHITE, 1, new QVector3(0, 0, 0), true, true);
        sol.agregarComponente(solLuz);
        universo.agregarEntidad(sol);

        QTextura cieloDia = QGestorRecursos.cargarTextura("dia", QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo_dia.jpg");
        QTextura cieloNoche = QGestorRecursos.cargarTextura("noche", QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo_noche.png");
//        QTextura cieloNoche = QGestorRecursos.cargarTextura("noche", "res/texturas/cielo/esfericos/cielo_noche_2.jpg");

        universo.setColorAmbiente(new QColor(0.5f, 0.5f, 0.5f));
        QCielo cielo = new QEsferaCielo(cieloDia, cieloNoche, universo.UM.convertirPixel(500, QUnidadMedida.METRO));
        universo.agregarEntidad(cielo.getEntidad());
    }

    public QTerreno getTerreno() {
        return terreno;
    }

    public void setTerreno(QTerreno terreno) {
        this.terreno = terreno;
    }

}
