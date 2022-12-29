/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.test.juegotest;

import net.qoopo.engine3d.QEngine3D;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.audio.openal.QSoundListener;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.mallas.QColisionMallaConvexa;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoDinamico;
import net.qoopo.engine3d.componentes.fisica.dinamica.QObjetoRigido;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QRueda;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QVehiculo;
import net.qoopo.engine3d.componentes.fisica.vehiculo.QVehiculoControl;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCaja;
import net.qoopo.engine3d.componentes.geometria.primitivas.formas.QCilindroX;
import net.qoopo.engine3d.componentes.geometria.util.QUnidadMedida;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.core.cielo.QCielo;
import net.qoopo.engine3d.core.cielo.QEsferaCielo;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.escena.QEscenario;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.test.juegotest.mundo.niveles.NivelTest2;

/**
 *
 * @author alberto
 */
public class JuegoEjemplo {

    public static void main(String[] args) {
        QEngine3D motor = new QEngine3D("QMotor 3D Juego Test");

        QCamara cam = new QCamara();
        cam.frustrumLejos = 1000;
        cam.updateCamera();
        motor.getEscena().agregarEntidad(cam);
        motor.setearSalirESC();

//estas  lineas es por el java3D que debo cargar el escenario antes del renderizador
//<Java3D>
//        motor.iniciarAudio();
//        motor.setIniciarAudio(false);
//        cargarNivelPersonaje(motor, cam);
//        QEntidad sol = new QEntidad("Sol");
//        sol.agregarComponente(new QLuzDireccional(.5f, QColor.YELLOW, true, 1000000));
//        motor.getUniverso().agregarEntidad(sol);
//</Java3D>
//        motor.configurarRendererFullScreen(cam);
//        motor.configurarRenderer(800, 600, cam, true);
//        motor.configurarRenderer(800, 600, cam, false);
//        motor.setIniciarFisica(false);
        motor.configurarRenderer(800, 600, cam);
        motor.getRenderer().setCargando(true);
        configurarCielo(motor);
        motor.iniciar();

        cargarNivelPersonaje(motor, cam);

        motor.getRenderer().setCargando(false);
        System.out.println("Listo");
    }

    private static QEntidad crearVehiculo(QEscena mundo) {

        float alturaLlantaConexion = 0.25f;
        float px = 0.5f;
        float pz = 0.8f;
        QMaterialBas material = new QMaterialBas("Vehículo");
        material.setColorBase(QColor.BLUE);

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
        materialRueda.setColorBase(QColor.DARK_GRAY);

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

        //agrego los componentes
        carro.agregarComponente(new QVehiculoControl(vehiculo));

        carro.mover(0, 25, 0);
//        carro.agregarHijo(rueda1E);
//        carro.agregarHijo(rueda2E);
//        carro.agregarHijo(rueda3E);
//        carro.agregarHijo(rueda4E);
        mundo.agregarEntidad(carro);
        return carro;
    }

    private static void configurarCielo(QEngine3D motor) {
        //sol
        QEntidad sol = new QEntidad("Sol");
        QLuzDireccional solLuz = new QLuzDireccional(1.1f, QColor.WHITE, 1, new QVector3(0, 0, 0), true, true);
        solLuz.setResolucionMapaSombra(1024);
        sol.agregarComponente(solLuz);
        motor.getEscena().agregarEntidad(sol);

//        QTextura cieloDia = QGestorRecursos.cargarTextura("dia", QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo_dia.jpg");
        QTextura cieloDia = QGestorRecursos.cargarTextura("dia", QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo4.jpg");
//        QTextura cieloNoche = QGestorRecursos.cargarTextura("noche", "res/texturas/cielo/esfericos/cielo_noche.png");
        QTextura cieloNoche = QGestorRecursos.cargarTextura("noche", QGlobal.RECURSOS + "texturas/cielo/esfericos/cielo_noche_2.jpg");
        QCielo cielo = new QEsferaCielo(cieloDia, cieloNoche, motor.getEscena().UM.convertirPixel(500, QUnidadMedida.METRO));
        motor.getEscena().agregarEntidad(cielo.getEntidad());

//        motor.configurarDiaNoche(cielo, 60 * 2, solLuz, 9); //el dia dura 2 minutos
//        motor.configurarDiaNoche(cielo, 60, solLuz, 9); 
        //50 minutos
        motor.configurarDiaNoche(cielo, 60 * 50, solLuz, 9);//cada hora es igual a 50 minutos
    }

    private static void cargarNivelPersonaje(QEngine3D motor, QCamara cam) {
//        QEscenario nivel = new NivelTest();
        QEscenario nivel = new NivelTest2();
//        QEscenario nivel = new DoomTest();
        nivel.cargar(motor.getEscena());
//
//        QEntidad sol = new QEntidad("Sol");
//        QLuzDireccional solLuz = new QLuzDireccional(1.5f, QColor.WHITE, true, 1, new QVector3(1f, -1, 0));
//        solLuz.setProyectarSombras(true);
////        solLuz.setRadioSombra(50);
//        solLuz.setSombraDinamica(true);
//        solLuz.setResolucionMapaSombra(1024);
//        sol.agregarComponente(solLuz);
//        motor.getEscena().agregarEntidad(sol);

        try {
//            QEntidad personaje = MD5Loader.cargar(new File(QGlobal.RECURSOS + "objetos/formato_md5/bob/boblamp.md5mesh").getAbsolutePath());
//            personaje.mover(-90, 200, 9);
//            personaje.escalar(0.05f, 0.05f, 0.05f);
//            personaje.rotar(Math.toRadians(-90), 0, 0);
//-------------------------------------------
//            Bob personaje = new Bob();
//            personaje.mover(-90, 200, 9);
//            personaje.setTerreno(nivel.getTerreno());

            QEntidad personaje = crearVehiculo(motor.getEscena());
            personaje.mover(-90, 15, 9);
            //pongo las coordenadas para q este detras del jugador
            cam.lookAtTarget(
                    new QVector3(0, 3f, -8),
                    new QVector3(0, 1f, 0),
                    QVector3.unitario_y.clone());

//        cam.lookAtTarget(
//                new QVector3(0, 2.5f, 6), //detras y arriba de bob                                
//                new QVector3(0, -2.5f, -6),
//                QVector3.unitario_y.clone());
//pongo las coordenadas para q este en la cabeza del jugador a manera ojos
//        cam.lookAtTarget(
//                new QVector3(0, 1.2f, -0.1f), //en la cabeza, en el lugar de los ojos
//                new QVector3(0, 1.0f, -0.5f),//mire al frente, un poco inclinado
//                QVector3.unitario_y.clone());
            //al agregar la camara como hija del personaje, siempre va a seguir al personaje
            personaje.agregarHijo(cam);
//        personaje.agregarLinterna();
//        personaje.agregarPistola();
//        personaje.posicionPistola();
            //configuro a personaje como listener de audio
            personaje.agregarComponente(new QSoundListener(personaje.getTransformacion().getTraslacion()));
            motor.getEscena().agregarEntidad(personaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
