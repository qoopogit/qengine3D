/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.simple;

import java.io.File;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.componentes.terreno.QTerreno;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjmTerreno extends GeneraEjemplo {

    public void iniciar(QEscena mundo) {
        this.mundo = mundo;
//         mundo.agregarLuz(new QLight(0, 2, 128, 255, 0, 1.5f, -.8f, 1.5f, true));
//        mundo.agregarLuz(new QLight(0, 2, 255, 0, 128, -1.5f, -.8f, 1.5f, true));
//        QLight sun = new QLight(1, .5f, 128, 128, 255, 0, 0, 0, true, "Sol");
//        mundo.agregarLuz(sun);

        QEntidad entidad = new QEntidad("Terreno");

        QTerreno terreno = new QTerreno();
        entidad.agregarComponente(terreno);
        QTextura textura = null;

//            textura = new QTextura(ImageIO.read(new File("res/texturas/terreno/text4.jpg")), QTextura.TIPO_MAPA_DIFUSA);
        textura = QGestorRecursos.cargarTextura("terreno", QGlobal.RECURSOS + "texturas/terreno/text4.jpg");
        textura.setMuestrasU(3);
        textura.setMuestrasV(3);
        terreno.generar(new File(QGlobal.RECURSOS + "mapas_altura/map10.png"), 1, -10, 20f, textura, 1);

//        terrenoEntidad.agregarComponente(QTerrenoGen.generaTerrenoMapaAltura(new File("res/mapas_altura/map11.png"), 1, -10, 20f, new File("res/texturas/terreno/text4.jpg"), 3, 4));
//        terrenoEntidad.agregarComponente(QTerrenoGen.generaTerrenoMapaAltura(new File("res/mapas_altura/Quito terrain/Quito Height Map (ASTER 30m).png"), 1, -10, 20f, new File("res/texturas/terreno/text4.jpg"), 4, 23));
//        terrenoEntidad.agregarComponente(QTerrenoGen.generaTerrenoMapaAltura(new File("res/mapas_altura/heightmap1.png"), 1, -10, 20f, new File("res/texturas/terreno/text4.jpg"), 3, 1));
//procedural
//        terrenoEntidad.agregarComponente(QTerrenoGen.generaTerreno(128, 128, 1f));
//        terrenoEntidad.agregarComponente(QGenerador.generaTerreno(64, 64, 10f));
//        terrenoEntidad.agregarComponente(QGenerador.generaTerreno(256, 256, 10f));
//        terrenoEntidad.agregarComponente(QGenerador.generaTerreno(256, 256, 1f));
        mundo.agregarEntidad(entidad);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {
    }

}
