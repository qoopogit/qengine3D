/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.sombras;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QPlano;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.core.carga.impl.CargaWaveObject;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 *
 * @author alberto
 */
public class SombrasOmniDireccional2 extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        //luces
        QEntidad luzCentral = new QEntidad("Luz_central");
        luzCentral.mover(0, 2f, 0);
        QLuzPuntual luz = new QLuzPuntual(50f, QColor.YELLOW, true, Float.POSITIVE_INFINITY);
        luz.setProyectarSombras(true);
        luzCentral.agregarComponente(luz);
        mundo.agregarEntidad(luzCentral);

        QEntidad calabaza = new QEntidad("Jack");
        calabaza.agregarComponente(QUtilComponentes.getGeometria(CargaWaveObject.cargarWaveObject(new File(QGlobal.RECURSOS + "objetos/formato_obj/VARIOS/CabezaHallowenCalabaza/Cabeza2.obj")).get(0)));
        calabaza.escalar(2, 2, 2);
//        calabaza.mover(2f, -2f, -2f);
//        calabaza.rotar(0, Math.toRadians(180), 0);
        mundo.agregarEntidad(calabaza);

        QEntidad ojo1 = new QEntidad("esfera");
        ojo1.mover(-7, 15, -25);
        ojo1.agregarComponente(new QEsfera());
        mundo.agregarEntidad(ojo1);

        QEntidad ojo2 = new QEntidad("esfera");
        ojo2.mover(7, 15, -25);
        ojo2.agregarComponente(new QEsfera());
        mundo.agregarEntidad(ojo2);

        QEntidad piso = new QEntidad("piso");
//        piso.mover(0, -1, 0);
        piso.rotar(Math.toRadians(90), 0, 0);
//        piso.agregarComponente(new QCaja(0.1f, 10f, 10f));
        piso.agregarComponente(new QPlano(150, 150));
        mundo.agregarEntidad(piso);

        QEntidad pared1 = new QEntidad("Caja");
//        pared1.mover(0, -1, 0);
        pared1.agregarComponente(QUtilNormales.invertirNormales(new QCaja(100f, 100f, 100f)));
        mundo.agregarEntidad(pared1);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
