/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.animado;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EsferaAnimada extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        //         QLuz luz = new QLuzPuntual( 8.5f, 255, 0, 128, 0, 2f, 0, true);
        QEntidad faro1 = new QEntidad("faro1");
        faro1.mover(0, 2f, 0);
        faro1.agregarComponente(new QLuzPuntual(1.5f, QColor.RED, 10, false, false));
        mundo.agregarEntidad(faro1);

        QEntidad faro2 = new QEntidad("faro2");
        faro2.mover(3f, 2f, 0);
        faro2.agregarComponente(new QLuzPuntual(1.5f, QColor.GREEN, 10, false, false));
        mundo.agregarEntidad(faro2);

        QEntidad faro3 = new QEntidad("faro2");
        faro3.mover(-2f, 2f, 2f);
        faro3.agregarComponente(new QLuzPuntual(1.5f, QColor.BLUE, 10, false, false));
        mundo.agregarEntidad(faro3);

        QEntidad piso = new QEntidad("Piso");

        QGeometria pisoGeometria = new QCaja(mundo.UM.convertirPixel(0.1f),
                mundo.UM.convertirPixel(6, QUnidadMedida.METRO),
                mundo.UM.convertirPixel(6, QUnidadMedida.METRO), 10.0f);

        QMaterialBas mat;
        try {
            mat = new QMaterialBas(new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/piso/baldosas/baldosa8.jpg"))));
            mat.getMapaDifusa().setMuestrasU(10);
            mat.getMapaDifusa().setMuestrasV(10);
            QMaterialUtil.aplicarMaterial(pisoGeometria, mat);
        } catch (IOException ex) {
            Logger.getLogger(EsferaAnimada.class.getName()).log(Level.SEVERE, null, ex);
        }

        piso.agregarComponente(pisoGeometria);
        mundo.agregarEntidad(piso);

        QEntidad esfera = new QEntidad("Esfera");

        QGeometria esferaGeometria = new QEsfera(0.5f);
        QMaterialBas transparente = new QMaterialBas("Transparencia");
        transparente.setTransAlfa(0.45f);
        transparente.setTransparencia(true);
        transparente.setColorDifusa(new QColor(1, 0.15f, 0.15f, 0.2f));

        transparente.setSpecularExponent(64);
        for (QPrimitiva cara : esferaGeometria.listaPrimitivas) {
            cara.material = transparente;
        }
        esfera.getTransformacion().trasladar(0, 0.5f, mundo.UM.convertirPixel(5, QUnidadMedida.METRO) / 2);
        esfera.agregarComponente(esferaGeometria);

        QEntidad sombra = new QEntidad("sombra");

        QGeometria sombraGeometria = new QEsfera(0.5f);
        QMaterialBas transparenteSombra = new QMaterialBas("Sombra");
        transparenteSombra.setTransAlfa(0.3f);
        transparenteSombra.setColorDifusa(new QColor(1, 0.15f, 0.15f, 0.2f));

        transparenteSombra.setSpecularExponent(64);
        for (QPrimitiva cara : sombraGeometria.listaPrimitivas) {
            cara.material = transparenteSombra;
        }
        sombra.mover(0,
                mundo.UM.convertirPixel(0.1f) / 2 + 0.01f,
                mundo.UM.convertirPixel(5, QUnidadMedida.METRO) / 2);
        sombra.escalar(1, 0f, 1);

        sombra.agregarComponente(sombraGeometria);
        mundo.agregarEntidad(sombra);

        QEntidad cubo = new QEntidad("cubo");
        cubo.agregarComponente(new QCaja(0.3f, 0.3f, 0.3f));
//        cubo.transformacion.trasladar(0, 0.5f, mundo.UM.convertirPixel(5, QUnidadMedida.METRO) / 2);

        cubo.agregarComponente(new QLuzPuntual(1.5f, QColor.WHITE, 10, false, false));

        esfera.agregarHijo(cubo);

        mundo.agregarEntidad(esfera);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
        }

        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {

                float difX = 0.04f;
                float difZ = 0.04f;

                float angx = (float) Math.toRadians(5) * -1;
//        float angy=(float) Math.toRadians(1);
                float angy = 0;
                float angz = (float) Math.toRadians(5) * 1;
//        float angz = 0;

                while (true) {

                    sombra.aumentarX(difX);
                    sombra.aumentarZ(difZ);

//                    cubo.aumentarX(difX);
//                    cubo.aumentarZ(difZ);
                    esfera.aumentarX(difX);
                    esfera.aumentarZ(difZ);

                    esfera.getTransformacion().getRotacion().aumentarRotacion(angx, angy, angz);
//                    cubo.transformacion.getRotacion().aumentarRotacion(angx, angy, angz);

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                    }

                    if (esfera.getTransformacion().getTraslacion().x > mundo.UM.convertirPixel(5, QUnidadMedida.METRO) / 2
                            || esfera.getTransformacion().getTraslacion().x < -mundo.UM.convertirPixel(5, QUnidadMedida.METRO) / 2) {
                        difX *= -1;
                        angz *= -1;
                    }

                    if (esfera.getTransformacion().getTraslacion().z > mundo.UM.convertirPixel(5, QUnidadMedida.METRO) / 2
                            || esfera.getTransformacion().getTraslacion().z < -mundo.UM.convertirPixel(5, QUnidadMedida.METRO) / 2) {
                        difZ *= -1;
                        angx *= -1;
                    }
                }
            }
        });
        hilo.start();

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
