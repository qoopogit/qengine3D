/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.fisica;

import java.io.File;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionCaja;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.carga.CargaObjeto;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.Accion;

/**
 *
 * @author alberto
 */
public class EjemploFisica1 extends FisicaDisparar {

    private float largoLadrillo = 0.48f;
    private float anchoLadrillo = 0.24f;
    private float altoLadrillo = 0.12f;

    private static QMaterialBas materialLadrillo;
    private static QMaterialBas materialBalas;
    private static QMaterialBas materialBombas;

    @Override
    public void iniciar(QEscena mundo) {
        super.iniciar(mundo);

        materialBalas = new QMaterialBas("bala");
        materialBalas.setColorBase(QColor.BLUE);
        materialBombas = new QMaterialBas("bomba");
        materialBombas.setColorBase(QColor.YELLOW);

//        //el mundo por default esta con unidades de medida en metro
//        //al non usar el conversor d eunidades de media, se toma como metros
//        //Piso
//        QEntidad piso = new QEntidad("piso");
////        piso.transformacion.getTraslacion().y = 0f;
//        piso.mover(0, 0, 0);
//        piso.escalar(10f, 10f, 10f);
//
//        piso.agregarComponente(new QCaja(0.1f, mundo.UM.convertirPixel(50), mundo.UM.convertirPixel(50)));
//
//        QColisionCaja colision = new QColisionCaja(mundo.UM.convertirPixel(50), 0.1f, mundo.UM.convertirPixel(50));
//        piso.agregarComponente(colision);
//
//        QObjetoRigido pisoRigidez = new QObjetoRigido(QObjetoDinamico.ESTATICO);
//        pisoRigidez.setMasa(0, QVector3.zero.clone());
//        pisoRigidez.setFormaColision(colision);
//
//        piso.agregarComponente(pisoRigidez);
//        mundo.agregarEntidad(piso);
        CargaObjeto carga = new CargaWaveObject();

        Accion accionFinal = new Accion() {
            @Override
            public void ejecutar(Object... parametros) {
                //agregar los objetos al renderer
                for (QEntidad objeto : carga.getLista()) {
                    mundo.agregarEntidad(objeto);
                }

            }
        };

        carga.setAccionFinal(accionFinal);
//            carga.setProgreso(barraProgreso);
        carga.cargar(new File(QGlobal.RECURSOS + "objetos/formato_obj/ESCENARIOS/escenarioQEngine.obj"));

        crearEsferas(mundo);
        construirMuro(mundo);
    }

    private void crearEsferas(QEscena mundo) {
        //Balon 1
        QEntidad balon = new QEntidad("Esfera1");//QEntidad("Esfera 0.05");
        balon.agregarComponente(new QEsfera(4));
        QColisionEsfera colision = new QColisionEsfera(4);
        balon.agregarComponente(colision);
        QObjetoRigido fisicaBalon = new QObjetoRigido(QObjetoDinamico.DINAMICO);
        fisicaBalon.setMasa(16f, QVector3.zero.clone());
        fisicaBalon.setFormaColision(colision);
        balon.agregarComponente(fisicaBalon);
        balon.mover(mundo.UM.convertirPixel(0), mundo.UM.convertirPixel(15f), mundo.UM.convertirPixel(-5));

        mundo.agregarEntidad(balon);

        //Balon 2
        QEntidad balon2 = new QEntidad("Esfera2");//QEntidad("Esfera 0.08");
        QColisionEsfera colision2 = new QColisionEsfera(1);
        balon2.agregarComponente(colision2);
        balon2.agregarComponente(new QEsfera(1));
        QObjetoRigido balon2Fisica = new QObjetoRigido(QObjetoDinamico.DINAMICO);
        balon2Fisica.setFormaColision(colision2);
        balon2Fisica.setMasa(1f, QVector3.zero.clone());
        balon2.agregarComponente(balon2Fisica);
        balon2.mover(mundo.UM.convertirPixel(3), mundo.UM.convertirPixel(8f), mundo.UM.convertirPixel(-5));
        mundo.agregarEntidad(balon2);

        //Balon 3
        QEntidad balon3 = new QEntidad("Esfera2");//QEntidad("Esfera 0.01");

        balon3.agregarComponente(new QEsfera(2));
        QColisionEsfera colision3 = new QColisionEsfera(2);
        balon3.agregarComponente(colision3);
        QObjetoRigido balon3Fisica = new QObjetoRigido(QObjetoDinamico.DINAMICO);
        balon3Fisica.setFormaColision(colision3);
        balon3Fisica.setMasa(2f, QVector3.zero.clone());
        balon3.agregarComponente(balon3Fisica);
        balon3.mover(mundo.UM.convertirPixel(1.5f), mundo.UM.convertirPixel(12f), mundo.UM.convertirPixel(0));
        mundo.agregarEntidad(balon3);
    }

    private void construirMuro(QEscena mundo) {
        largoLadrillo = mundo.UM.convertirPixel(50, QUnidadMedida.CENTIMETRO);
        anchoLadrillo = mundo.UM.convertirPixel(50, QUnidadMedida.CENTIMETRO);
        altoLadrillo = mundo.UM.convertirPixel(50, QUnidadMedida.CENTIMETRO);

        materialLadrillo = new QMaterialBas();

        materialLadrillo = null;
        try {
            materialLadrillo = new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/muro/ladrillo_1.jpg"))), 64);
//            materialLadrillo.alpha = 1;
        } catch (Exception e) {
            e.printStackTrace();
        }

        float inicio = anchoLadrillo / 4;
        float alto = -altoLadrillo / 2;
        for (int j = 0; j < 15; j++) {
            for (int i = 0; i < 16; i++) {
                QVector3 vt = new QVector3(i * anchoLadrillo + inicio - 7, altoLadrillo + alto, 0);
                hacerLadrillo(vt, mundo);
            }
            inicio = -inicio;
            alto += altoLadrillo;
        }
    }

    private void hacerLadrillo(QVector3 loc, QEscena mundo) {
        QEntidad bloque = new QEntidad("Bloque");
        bloque.mover(loc);
        QCaja geometria = new QCaja(altoLadrillo, anchoLadrillo, largoLadrillo);
        geometria.setMaterial(materialLadrillo);
        QMaterialUtil.aplicarMaterial(geometria, materialLadrillo);
        QColisionCaja formaColision = new QColisionCaja(anchoLadrillo, altoLadrillo, largoLadrillo);
        bloque.agregarComponente(formaColision);
        QObjetoRigido bloquefisica = new QObjetoRigido(QObjetoDinamico.DINAMICO);
        bloquefisica.setMasa(1f, QVector3.zero.clone());
        bloquefisica.setFormaColision(formaColision);
        bloque.agregarComponente(bloquefisica);
        bloque.agregarComponente(geometria);
        mundo.agregarEntidad(bloque);
    }

}
