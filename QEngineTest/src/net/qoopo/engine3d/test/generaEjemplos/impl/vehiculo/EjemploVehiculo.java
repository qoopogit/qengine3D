/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.generaEjemplos.impl.vehiculo;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaIndexada;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QRueda;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QVehiculo;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QVehiculoControl;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindroX;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QMalla;
import net.qoopo.engine3d.componentes.terreno.QTerreno;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.test.generaEjemplos.GeneraEjemplo;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.engines.render.QMotorRender;

/**
 *
 * @author alberto
 */
public class EjemploVehiculo extends GeneraEjemplo {

    @Override
    public void iniciar(QEscena mundo) {
        this.mundo = mundo;
//        crearPiso(mundo);
        crearTerreno(mundo);
        crearVehiculo(mundo);
    }

    private void crearPiso(QEscena mundo) {
        // piso
        QEntidad piso = new QEntidad("Piso", false);
        QMalla geom = new QMalla(20, 20, 20,20);
        QColisionMallaConvexa colision = new QColisionMallaConvexa(geom);
        QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.ESTATICO, 0);
        rigido.setFormaColision(colision);

        piso.agregarComponente(geom);
        piso.agregarComponente(colision);
        piso.agregarComponente(rigido);
        piso.escalar(10, 10, 10);
        mundo.agregarEntidad(piso);
    }

    private void crearTerreno(QEscena universo) {
        //el terreno generado con mapas de altura
        QLogger.info("Creando terreno...");
        QEntidad entidadTerreno = new QEntidad("Terreno");
        QTerreno terreno = new QTerreno();
        entidadTerreno.agregarComponente(terreno);
        QTextura textura = null;
        try {
            textura = new QTextura(ImageIO.read(new File(QGlobal.RECURSOS + "texturas/terreno/text4.jpg")));
        } catch (Exception ex) {

        }

        try {
            textura.setMuestrasU(20);
            textura.setMuestrasV(20);
            BufferedImage img1 = ImageIO.read(new File(QGlobal.RECURSOS + "mapas_altura/map10.png"));
//            BufferedImage img1 = ImageIO.read(new File(QGlobal.RECURSOS + "mapas_altura/map11.png"));
            terreno.generar(img1, 1, 0f, 20f, textura, 5);
        } catch (Exception ex) {

        }

        QFormaColision colision = new QColisionMallaIndexada(QUtilComponentes.getGeometria(entidadTerreno));
        entidadTerreno.agregarComponente(colision);

        QObjetoRigido terrenoRigidez = new QObjetoRigido(QObjetoDinamico.ESTATICO, 0);
        terrenoRigidez.setFormaColision(colision);
        entidadTerreno.agregarComponente(terrenoRigidez);
        universo.agregarEntidad(entidadTerreno);
        QLogger.info("Terreno cargado...");

    }

    private void crearVehiculo(QEscena mundo) {

        float alturaLlantaConexion = 0.25f;
        float px = 0.5f;
        float pz = 0.8f;
        QMaterialBas material = new QMaterialBas("Vehículo");
        material.setColorDifusa(QColor.BLUE);

        QEntidad carro = new QEntidad();
        QCaja geom = new QCaja(0.5f, 1, 2);
        QMaterialUtil.aplicarMaterial(geom, material);
        carro.agregarComponente(geom);

        QFormaColision colision = new QColisionMallaConvexa(geom);
        carro.agregarComponente(colision);
//        QFormaColision colision = new QColisionCaja(2,1f,3);
//        QColisionCompuesta colisionCom = new QColisionCompuesta();
//        colisionCom.agregarHijo(colision, new QVector3(0, 1f, 0));
//        carro.agregarComponente(colisionCom);

        QObjetoRigido rigido = new QObjetoRigido(QObjetoDinamico.DINAMICO, 800);
//        rigido.setFormaColision(colisionCom);
        rigido.setFormaColision(colision);
        carro.agregarComponente(rigido);

        QVehiculo vehiculo = new QVehiculo(rigido);
        carro.agregarComponente(vehiculo);

        //ruedas
        QMaterialBas materialRueda = new QMaterialBas("Rueda");
        materialRueda.setColorDifusa(QColor.DARK_GRAY);

        QCilindroX forma = new QCilindroX(0.1f, 0.25f);
        QMaterialUtil.aplicarMaterial(forma, materialRueda);
        //--

        QEntidad rueda1E = new QEntidad("rueda1", false);// la entidad que se actualizara su transformacion con el vehiculo        
        rueda1E.agregarComponente(forma.clone());
        rueda1E.mover(-px, alturaLlantaConexion, pz);
        mundo.agregarEntidad(rueda1E);

        QEntidad rueda2E = new QEntidad("rueda2", false);// la entidad que se actualizara su transformacion con el vehiculo        
        rueda2E.agregarComponente(forma.clone());
        rueda2E.mover(px, alturaLlantaConexion, pz);
        mundo.agregarEntidad(rueda2E);

        QEntidad rueda3E = new QEntidad("rueda3", false);// la entidad que se actualizara su transformacion con el vehiculo        
        rueda3E.agregarComponente(forma.clone());
        rueda3E.mover(-px, alturaLlantaConexion, -pz);
        mundo.agregarEntidad(rueda3E);
        QEntidad rueda4E = new QEntidad("rueda4", false);// la entidad que se actualizara su transformacion con el vehiculo        
        rueda4E.agregarComponente(forma.clone());
        rueda4E.mover(px, alturaLlantaConexion, -pz);
        mundo.agregarEntidad(rueda4E);
        //----------------
        //1
        QRueda rueda1 = new QRueda();
        rueda1.setEntidadRueda(rueda1E);
        rueda1.setFriccion(1000f);
        rueda1.setFrontal(true);
        rueda1.setAncho(0.1f);
        rueda1.setRadio(0.25f);
        rueda1.setUbicacion(new QVector3(-px, alturaLlantaConexion, pz));
        vehiculo.agregarRueda(rueda1);
        //2
        QRueda rueda2 = new QRueda();
        rueda2.setEntidadRueda(rueda2E);
        rueda2.setFriccion(1000f);
        rueda2.setFrontal(true);
        rueda2.setAncho(0.1f);
        rueda2.setRadio(0.25f);
        rueda2.setUbicacion(new QVector3(px, alturaLlantaConexion, pz));
        vehiculo.agregarRueda(rueda2);
        //3

        QRueda rueda3 = new QRueda();
        rueda3.setEntidadRueda(rueda3E);
        rueda3.setFriccion(1000f);
        rueda3.setFrontal(false);
        rueda3.setAncho(0.1f);
        rueda3.setRadio(0.25f);
        rueda3.setUbicacion(new QVector3(-px, alturaLlantaConexion, -pz));
        vehiculo.agregarRueda(rueda3);
        //4

        QRueda rueda4 = new QRueda();
        rueda4.setEntidadRueda(rueda4E);
        rueda4.setFriccion(1000f);
        rueda4.setFrontal(false);
        rueda4.setAncho(0.1f);
        rueda4.setRadio(0.25f);
        rueda4.setUbicacion(new QVector3(pz, alturaLlantaConexion, -pz));
        vehiculo.agregarRueda(rueda4);
//control del vehiculo

        QVehiculoControl control = new QVehiculoControl(vehiculo);

        //agrego los componentes
        carro.agregarComponente(control);

        carro.mover(0, 25, 0);
//        carro.agregarHijo(rueda1E);
//        carro.agregarHijo(rueda2E);
//        carro.agregarHijo(rueda3E);
//        carro.agregarHijo(rueda4E);
        mundo.agregarEntidad(carro);
    }

    @Override
    public void accion(int numAccion, QMotorRender render) {

    }

}
