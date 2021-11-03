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
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;

/**
 *
 * @author alberto
 */
public class EjemploFisica2 extends FisicaDisparar {

    private float largoLadrillo = 0.48f;
    private float anchoLadrillo = 0.24f;
    private float altoLadrillo = 0.12f;

    private static QMaterialBas materialLadrillo;

    @Override
    public void iniciar(QEscena mundo) {
        super.iniciar(mundo);
        //el mundo por default esta con unidades de medida en metro
        //al non usar el conversor d eunidades de media, se toma como metros
        //Piso
        QEntidad piso = new QEntidad("piso");
        piso.getTransformacion().getTraslacion().y = 0f;
        piso.mover(0, 0, 0);

        piso.agregarComponente(new QCaja(0.1f, mundo.UM.convertirPixel(50), mundo.UM.convertirPixel(50)));

        QColisionCaja colision = new QColisionCaja(mundo.UM.convertirPixel(50), 0.1f, mundo.UM.convertirPixel(50));
        piso.agregarComponente(colision);

        QObjetoRigido pisoRigidez = new QObjetoRigido(QObjetoDinamico.ESTATICO);
        pisoRigidez.setMasa(0, QVector3.zero.clone());
        pisoRigidez.setFormaColision(colision);

        piso.agregarComponente(pisoRigidez);

        mundo.agregarEntidad(piso);

//        crearEsferas(mundo);
        construirMuro(mundo);
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
        QEntidad bloque = new QEntidad();
        bloque.mover(loc);
        QGeometria geometria = new QCaja(altoLadrillo, anchoLadrillo, largoLadrillo);
        QMaterialUtil.aplicarMaterial(geometria, materialLadrillo);
        QColisionCaja formaColision = new QColisionCaja(anchoLadrillo, altoLadrillo, largoLadrillo);
        QObjetoRigido bloquefisica = new QObjetoRigido(QObjetoDinamico.DINAMICO);
        bloquefisica.setMasa(4f, QVector3.zero.clone());
        bloquefisica.setFormaColision(formaColision);
        bloque.agregarComponente(bloquefisica);
        bloque.agregarComponente(formaColision);
        bloque.agregarComponente(geometria);
        mundo.agregarEntidad(bloque);
    }

}
