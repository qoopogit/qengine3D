/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.textura;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QEsfera;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;

/**
 *
 * @author alberto
 */
public class EjmTexturaSistemaSolar extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;

        QEntidad sol = new QEntidad("Sol");
        QGeometria geomeSol = QMaterialUtil.aplicarMaterial(new QEsfera(2, 36), new QMaterialBas(QGestorRecursos.cargarTextura("sol", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/sol.jpg"))));
       ((QMaterialBas) geomeSol.primitivas[0].material).setFactorEmision(1.0f);
        sol.agregarComponente(geomeSol);
        sol.mover(QVector3.zero);
        mundo.agregarEntidad(sol);

        QEntidad mercurio = new QEntidad("Mercurio");
        mercurio.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.1f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("mercurio", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/mercurio_color.jpg")))));
        mercurio.mover(new QVector3(4, 0, 0));
        mundo.agregarEntidad(mercurio);

        QEntidad venus = new QEntidad("Venus");
        venus.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.15f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/venus_color.jpg")))));
        venus.mover(new QVector3(5, 0, 0));
        mundo.agregarEntidad(venus);

        QEntidad tierra = new QEntidad("Tierra");
        tierra.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.2f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/tierra/text4/tierra.jpg")))));
        tierra.mover(new QVector3(6, 0, 0));
        mundo.agregarEntidad(tierra);

        QEntidad luna = new QEntidad("Luna");
        luna.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.02f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/luna/text1/moonmap1k.jpg")))));
        luna.mover(new QVector3(6, 0, 0.35f));
        mundo.agregarEntidad(luna);

        QEntidad marte = new QEntidad("Marte");
        marte.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.18f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/marte/text2/marte_color.jpg")))));
        marte.mover(new QVector3(7, 0, 0));
        mundo.agregarEntidad(marte);

        QEntidad jupiter = new QEntidad("Júpiter");
        jupiter.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.5f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/jupiter_color.jpg")))));
        jupiter.mover(new QVector3(8.5f, 0, 0));
        mundo.agregarEntidad(jupiter);

        QEntidad saturno = new QEntidad("Saturno");
        saturno.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.4f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/saturno_color.jpg")))));
        saturno.mover(new QVector3(10.5f, 0, 0));
        mundo.agregarEntidad(saturno);

        QEntidad urano = new QEntidad("Urano");
        urano.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.3f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/urano_color.jpg")))));
        urano.mover(new QVector3(11.5f, 0, 0));
        mundo.agregarEntidad(urano);

        QEntidad neptuno = new QEntidad("Neptuno");
        neptuno.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.25f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/neptuno_color.jpg")))));
        neptuno.mover(new QVector3(12.5f, 0, 0));
        mundo.agregarEntidad(neptuno);

        QEntidad pluton = new QEntidad("Plutón");
        pluton.agregarComponente(QMaterialUtil.aplicarMaterial(new QEsfera(0.05f, 36), new QMaterialBas(QGestorRecursos.cargarTextura("venus", new File(QGlobal.RECURSOS + "texturas/basicas/planetas/pluton_color.jpg")))));
        pluton.mover(new QVector3(13.5f, 0, 0));
        mundo.agregarEntidad(pluton);

    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
