/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindro;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCono;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QToro;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class Ejemplo2 extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        try {
            this.mundo = mundo;

            QEntidad cube = new QEntidad("Doodster");

            QGeometria cubGeometria = new QGeometria();
            cubGeometria.agregarVertice(1, 1, 1);
            cubGeometria.agregarVertice(1, 1, -1);
            cubGeometria.agregarVertice(-1, 1, -1);
            cubGeometria.agregarVertice(-1, 1, 1);
            cubGeometria.agregarVertice(1, -1, 1);
            cubGeometria.agregarVertice(1, -1, -1);
            cubGeometria.agregarVertice(-1, -1, -1);
            cubGeometria.agregarVertice(-1, -1, 1);
            cubGeometria.agregarPoligono(0, 1, 2, 3);
            cubGeometria.agregarPoligono(0, 4, 5, 1);
            cubGeometria.agregarPoligono(3, 2, 6, 7);
            cubGeometria.agregarPoligono(0, 3, 7, 4);
            cubGeometria.agregarPoligono(1, 5, 6, 2);
            cubGeometria.agregarPoligono(4, 7, 6, 5);
            cubGeometria = QUtilNormales.calcularNormales(cubGeometria);

            cube.agregarComponente(cubGeometria);

            mundo.agregarEntidad(cube);

            QEntidad clone = new QEntidad("Doodster 2");
            clone.mover(0.2f, 0.2f, 0.2f);
            clone.agregarComponente(cubGeometria.clone());
            mundo.agregarEntidad(clone);

            QEntidad luzSpot = new QEntidad("luz spot");
            luzSpot.agregarComponente(new QLuzSpot(2f, QColor.YELLOW, 30, new QVector3(-1, 0, 0), (float) Math.toRadians(60), true, false));
            luzSpot.mover(8, 0, 0);
            mundo.agregarEntidad(luzSpot);

            QEntidad luz1 = new QEntidad("luz1");
            luz1.agregarComponente(new QLuzPuntual(2, new QColor(0.5f, 1, 0), 10, true, false));
            luz1.mover(1.5f, -.8f, 1.5f);
            mundo.agregarEntidad(luz1);

            QEntidad luz2 = new QEntidad("luz1");
            luz2.agregarComponente(new QLuzPuntual(2, new QColor(1, 0, 0.5f), 10, true, false));
            luz2.mover(-1.5f, -.8f, 1.5f);
            mundo.agregarEntidad(luz2);

            QEntidad sol = new QEntidad("Sol");
            sol.agregarComponente(new QLuzDireccional(.5f, new QColor(0.5f, 0.5f, 1), 10, true, false));
            mundo.agregarEntidad(sol);
            QEntidad cuboBeto = new QEntidad("cubo");
            cuboBeto.agregarComponente(new QCaja(2));
            cuboBeto.getTransformacion().getTraslacion().x += 2;
            cuboBeto.getTransformacion().getTraslacion().y += 2;
            mundo.agregarEntidad(cuboBeto);

            QEntidad cilindroBeto = new QEntidad("cilindro");
            cilindroBeto.agregarComponente(new QCilindro(2, 1));
            cilindroBeto.getTransformacion().getTraslacion().x += 5;
            cilindroBeto.getTransformacion().getTraslacion().z += 5;
            mundo.agregarEntidad(cilindroBeto);

            QEntidad esfera = new QEntidad("esfera");
            esfera.agregarComponente(new QEsfera(2));
            esfera.getTransformacion().getTraslacion().x += 5;
            esfera.getTransformacion().getTraslacion().z -= 5;
            mundo.agregarEntidad(esfera);
//
            QEntidad toro = new QEntidad("toro");
            toro.agregarComponente(new QToro(4, 2));
            toro.getTransformacion().getTraslacion().x -= 5;
            toro.getTransformacion().getTraslacion().z -= 5;
            mundo.agregarEntidad(toro);

            QEntidad cono = new QEntidad("cono");
            cono.agregarComponente(new QCono(4, 2));
            cono.getTransformacion().getTraslacion().x -= 5;

            cono.getTransformacion().getTraslacion().z += 5;
            mundo.agregarEntidad(cono);
        } catch (Exception ex) {
            Logger.getLogger(Ejemplo2.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
