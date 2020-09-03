/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;
import net.qoopo.engine3d.componentes.modificadores.particulas.QEmisorParticulas;
import net.qoopo.engine3d.componentes.modificadores.particulas.QParticula;
import net.qoopo.engine3d.componentes.modificadores.procesadores.QProcesador;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.cielo.QCielo;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.input.QInputManager;
import net.qoopo.engine3d.core.recursos.QGestorRecursos;
import net.qoopo.engine3d.core.util.Accion;
import net.qoopo.engine3d.core.util.GC;
import net.qoopo.engine3d.core.util.ImgUtil;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.engines.animacion.QMotorAnimacion;
import net.qoopo.engine3d.engines.audio.QMotorAudio;
import net.qoopo.engine3d.engines.audio.openal.QOpenAL;
import net.qoopo.engine3d.engines.ciclodia.QMotorCicloDia;
import net.qoopo.engine3d.engines.fisica.QMotorFisica;
import net.qoopo.engine3d.engines.fisica.interno.QFisica;
import net.qoopo.engine3d.engines.fisica.jbullet.QJBullet;
import net.qoopo.engine3d.engines.inteligencia.QMotorInteligencia;
import net.qoopo.engine3d.engines.inteligencia.interno.QInteligencia;
import net.qoopo.engine3d.engines.render.GuiUTIL;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.QRender;
import net.qoopo.engine3d.engines.render.java3d.QRenderJava3D;
import net.qoopo.engine3d.engines.render.lwjgl.QOpenGL;
import net.qoopo.engine3d.engines.render.superficie.QJPanel;
import net.qoopo.engine3d.engines.render.superficie.Superficie;

/**
 * Motor 3D QEngine.
 *
 * Contiene y gestiona los demás motores
 *
 * @author alberto
 */
public class QMotor3D extends QMotor implements Runnable {

    /**
     *
     * Curso LWJGL (Ingles) (Java)
     * https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/
     *
     * Cursos engine 3d en espa;ol (C) https://brakeza.com/indice
     *
     * Curso completo OpenGL C (Ingles) http://ogldev.atspace.co.uk/index.html
     *
     * Transformaciones 3D http://www.songho.ca/opengl/gl_transform.html
     */
    public static QMotor3D INSTANCIA;
    public String titulo = "QMotor3D";

//    private boolean ejecutando = false;
    /**
     * Indica si se realiza alguna modificacion en el monitor para no realizar
     * actualizaciones hasta que la bandera este en false
     */
    private boolean modificando = false;

    // Escena a usar en todos los motores
    private QEscena escena;
    //motores
    private QMotorFisica motorFisica = null;//el motor de fisica
    private QMotorRender renderer = null; //motor de renderizado
    private List<QMotorRender> rendererList = null; //lista de renderizadores, en caso de un editor puede usar varios renderers
    private QMotorAnimacion motorAnimacion = null;//el motor que procesa las animaciones
    private QMotorCicloDia motorDiaNoche = null;//el motor que procesa el ciclo de dia y noche
    private QMotorAudio motorAudio = null;
    private QMotorInteligencia motorInteligencia = null;

    // banderas que indican que motor se va a iniciar cuando se llame al método iniciar del motor 3d
    private boolean iniciarFisica = true;
    private boolean iniciarAnimaciones = true;
    private boolean iniciarDiaNoche = true;
    private boolean iniciarAudio = true;
    private boolean iniciarInteligencia = true;

    //----------------------------------------
    //opciones para pruebas de rendimiento
    protected boolean forzarActualizacionMapaReflejos = false;

    private final Thread hiloPrincipal;

    private List<Accion> accionesEjecucion = new ArrayList<>();

    public QMotor3D() {
        ImgUtil.iniciar();
        escena = new QEscena();
        hiloPrincipal = new Thread(this, "QENGINE_PRINCIPAL");
        INSTANCIA = this;
    }

    public QMotor3D(String titulo) {
        ImgUtil.iniciar();
        escena = new QEscena();
        this.titulo = titulo;
        hiloPrincipal = new Thread(this, "QENGINE_PRINCIPAL");
        INSTANCIA = this;
    }

    public QMotor3D(QEscena escena) {
        ImgUtil.iniciar();
        this.escena = escena;
        hiloPrincipal = new Thread(this, "QENGINE_PRINCIPAL");
        INSTANCIA = this;
    }

    public QMotor3D(QEscena escena, String titulo) {
        ImgUtil.iniciar();
        this.escena = escena;
        this.titulo = titulo;
        hiloPrincipal = new Thread(this, "QENGINE_PRINCIPAL");
        INSTANCIA = this;
    }

    private void cargar() {
        try {
            ejecutando = true;
            GC.iniciar();//inicia el recolector de basura

            if (iniciarAudio) {
                iniciarAudio();
            }

            motorAnimacion = new QMotorAnimacion(getEscena());
            if (iniciarAnimaciones) {
                iniciarAnimaciones();
            }
            if (iniciarFisica) {
                iniciarFisica();
            }
            if (iniciarInteligencia) {
                iniciarInteligencia();
            }

            renderer.iniciar();
            if (rendererList != null && !rendererList.isEmpty()) {
                for (QMotorRender rend : rendererList) {
                    rend.iniciar();
                }
            }

            //el motor de dianoche siempre se ejecuta independientemente
            if (motorDiaNoche != null) {
                motorDiaNoche.iniciar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loop() {
        long t = 1000 / (int) QGlobal.MOTOR_RENDER_FPS;
        long t2;
        while (ejecutando) {
            try {
                if (!modificando) {
                    update();
                }
            } catch (Exception e) {

            } finally {
                try {
                    t2 = getDelta();
                    if (t2 < t) {
                        Thread.sleep(t - t2);//disminuye uso de cpu, 
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Ejecuta una actualización
     *
     * @return
     */
    @Override
    public long update() {
        try {
            //ejecuta los componentes que realizan modificadiones
            ejecutarComponentes();
            //ejecuta los motores
            if (motorAnimacion != null && motorAnimacion.isEjecutando()) {
                motorAnimacion.update();
            }
            if (motorAudio != null) {
                motorAudio.update();
            }
            if (motorFisica != null) {
                motorFisica.update();
            }

            //ejecuta acciones de usuario
            if (accionesEjecucion != null && !accionesEjecucion.isEmpty()) {
                for (Accion accion : accionesEjecucion) {
                    accion.ejecutar();
                }
            }

            if (rendererList != null && !rendererList.isEmpty()) {
                rendererList.forEach(rend -> {
                    rend.update();
                });
            } else {
                if (renderer != null) {
                    renderer.update();
                }
            }

            //elimina las entidades que estan marcadas para eliminarse (no vivas)
            escena.eliminarEntidadesNoVivas();
            QTime.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        QLogger.info("M3D-->" + DF.format(getFPS()) + " FPS");
        tiempoPrevio = System.currentTimeMillis();
        return tiempoPrevio;
    }

    @Override
    public void run() {
        cargar();
        loop();
    }

    public QEscena getEscena() {
        return escena;
    }

    public void setEscena(QEscena escena) {
        this.escena = escena;
    }

    public QMotorAnimacion getMotorAnimacion() {
        return motorAnimacion;
    }

    public void setMotorAnimacion(QMotorAnimacion motorAnimacion) {
        this.motorAnimacion = motorAnimacion;
    }

    public QMotorCicloDia getMotorDiaNoche() {
        return motorDiaNoche;
    }

    public void setMotorDiaNoche(QMotorCicloDia motorDiaNoche) {
        this.motorDiaNoche = motorDiaNoche;
    }

    public QMotorAudio getMotorAudio() {
        return motorAudio;
    }

    public void setMotorAudio(QMotorAudio motorAudio) {
        this.motorAudio = motorAudio;
    }

    public QMotorFisica getMotorFisica() {
        return motorFisica;
    }

    public void setMotorFisica(QMotorFisica motorFisica) {
        this.motorFisica = motorFisica;
    }

    public QMotorRender getRenderer() {
        return renderer;
    }

    public void setRenderer(QMotorRender renderer) {
        this.renderer = renderer;
    }

    /**
     * Inicia el motor de física
     */
    public void iniciarFisica() {
//        iniciarFisica(QMotorFisica.FISICA_INTERNO); //interno
        iniciarFisica(QMotorFisica.FISICA_JBULLET);//jbullet
    }

    /**
     * Inicia el motor de fisica
     *
     * @param simulador
     */
    public void iniciarFisica(int simulador) {
        switch (simulador) {
            case QMotorFisica.FISICA_INTERNO:
            default:
                //usa el motor interno
                motorFisica = new QFisica(getEscena());
                break;
            case QMotorFisica.FISICA_JBULLET:
                motorFisica = new QJBullet(getEscena());
                break;
        }
        motorFisica.iniciar();
    }

    public void detenerFisica() {
        if (motorFisica != null) {
            motorFisica.detener();
            motorFisica = null;
        }
    }

    /**
     * Inicial el motor de Audio OpenAL
     */
    public void iniciarAudio() {
        motorAudio = new QOpenAL(escena);
        motorAudio.iniciar();
    }

    public void detenerAudio() {
        if (motorAudio != null) {
            motorAudio.detener();
        }
    }

    /**
     * Inicia el motor de animaciones
     */
    public void iniciarAnimaciones() {
        motorAnimacion.iniciar();
    }

    public void detenerAnimaciones() {
        if (motorAnimacion != null) {
            motorAnimacion.detener();
//            motorAnimacion = null;
        }
    }

    /**
     * Inicia el motor de inteligencia
     */
    public void iniciarInteligencia() {
        motorInteligencia = new QInteligencia(getEscena());
        motorInteligencia.iniciar();
    }

    public void detenerInteligencia() {
        if (motorAnimacion != null) {
            motorAnimacion.detener();
        }
    }

    /**
     * Configura el motor del ciclo del dia
     *
     * @param cielo
     * @param intervalo
     * @param sol
     * @param horaInicial
     */
    public void configurarDiaNoche(QCielo cielo, long intervalo, QLuzDireccional sol, float horaInicial) {
        motorDiaNoche = new QMotorCicloDia(cielo, getRenderer(), intervalo, sol, horaInicial);
    }

    public void detenerDiaNoche() {
        if (motorDiaNoche != null) {
            motorDiaNoche.detener();
        }
    }

    /**
     * Configura un renderer em modo pantalla completa
     *
     * @param camara
     */
    public void configurarRendererFullScreen(QCamara camara) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        configurarRenderer(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight(), camara, true);
    }

    /**
     * Configura un renderer en modo pantalla completa
     *
     * @param camara
     * @param tipoRenderer
     */
    public void configurarRendererFullScreen(QCamara camara, int tipoRenderer) {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        configurarRenderer(device.getDisplayMode().getWidth(), device.getDisplayMode().getHeight(), tipoRenderer, camara, true);
    }

    /**
     * Configura un renderer en modo ventana
     *
     * @param ancho
     * @param alto
     * @param camara
     */
    public void configurarRenderer(int ancho, int alto, QCamara camara) {
        configurarRenderer(ancho, alto, camara, false);
    }

    /**
     * Configura un renderer interno
     *
     * @param ancho
     * @param alto
     * @param camara
     * @param pantallaCompleta
     */
    public void configurarRenderer(int ancho, int alto, QCamara camara, boolean pantallaCompleta) {
        configurarRenderer(ancho, alto, QMotorRender.RENDER_INTERNO, camara, pantallaCompleta);
    }

    /**
     * Inicia el motor 3D
     */
    @Override
    public void iniciar() {
        hiloPrincipal.start();
    }

    @Override
    public void detener() {
        getRenderer().setCargando(true);
        QGestorRecursos.liberarRecursos();
        detenerFisica();
        detenerAnimaciones();
        detenerAudio();
        detenerDiaNoche();
        GC.detener();
        ejecutando = false;
        try {
            Thread.sleep(300);
        } catch (InterruptedException ex) {

        }
//        System.exit(0);
    }

    /**
     * Configura un renderer
     *
     * @param ancho
     * @param alto
     * @param tipoRenderer
     * @param camara
     * @param pantallaCompleta
     */
    public void configurarRenderer(int ancho, int alto, int tipoRenderer, QCamara camara, boolean pantallaCompleta) {
        JFrame ventana = new JFrame(titulo);
        ventana.setSize(ancho, alto);
        ventana.setPreferredSize(new Dimension(ancho, alto));
        ventana.setResizable(true);
        ventana.setLayout(new BorderLayout());
        ventana.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ventana.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                detener();
                super.windowClosing(e); //To change body of generated methods, choose Tools | Templates.
            }
        });
        QJPanel panelDibujo = new QJPanel();
        ventana.add(panelDibujo, BorderLayout.CENTER);
        if (pantallaCompleta) {
            try {
                GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                ventana.setUndecorated(true);
                device.setFullScreenWindow(ventana);
            } catch (Exception e) {
            }
        } else {
            //centra la ventana
            GuiUTIL.centrarVentana(ventana);
        }

        switch (tipoRenderer) {
            default:
            case QMotorRender.RENDER_INTERNO:
                renderer = new QRender(escena, new Superficie(panelDibujo), ancho, alto);
                break;
            case QMotorRender.RENDER_JAVA3D:
                renderer = new QRenderJava3D(escena, new Superficie(panelDibujo), ancho, alto);
                break;
            case QMotorRender.RENDER_OPENGL:
                renderer = new QOpenGL(escena, new Superficie(panelDibujo), ancho, alto);
                break;
        }

        renderer.opciones.setForzarResolucion(true);
        renderer.setCamara(camara);
        renderer.resize();
        ventana.setVisible(true);
        ventana.pack();
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public boolean isIniciarFisica() {
        return iniciarFisica;
    }

    public void setIniciarFisica(boolean iniciarFisica) {
        this.iniciarFisica = iniciarFisica;
    }

    public boolean isIniciarAnimaciones() {
        return iniciarAnimaciones;
    }

    public void setIniciarAnimaciones(boolean iniciarAnimaciones) {
        this.iniciarAnimaciones = iniciarAnimaciones;
    }

    public boolean isIniciarDiaNoche() {
        return iniciarDiaNoche;
    }

    public void setIniciarDiaNoche(boolean iniciarDiaNoche) {
        this.iniciarDiaNoche = iniciarDiaNoche;
    }

    public boolean isIniciarAudio() {
        return iniciarAudio;
    }

    public void setIniciarAudio(boolean iniciarAudio) {
        this.iniciarAudio = iniciarAudio;
    }

    public boolean isIniciarInteligencia() {
        return iniciarInteligencia;
    }

    public void setIniciarInteligencia(boolean iniciarInteligencia) {
        this.iniciarInteligencia = iniciarInteligencia;
    }

    public QMotorInteligencia getMotorInteligencia() {
        return motorInteligencia;
    }

    public void setMotorInteligencia(QMotorInteligencia motorInteligencia) {
        this.motorInteligencia = motorInteligencia;
    }

    /**
     * Configura que el motor se detenga al presionar ESC
     */
    public void setearSalirESC() {
        QInputManager.agregarListenerTeclado(new QTecladoReceptor() {
            @Override
            public void keyPressed(KeyEvent evt) {
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        detener();
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {

            }
        });
    }

    /**
     * Ejecuta los componentes que modifican geometrias y texturas
     *
     */
    private void ejecutarComponentes() {
        try {
            QEmisorParticulas emisor; //emisor de particula
            for (QEntidad entidad : escena.getListaEntidades()) {
                for (QComponente componente : entidad.getComponentes()) {
                    if (componente instanceof QProcesador) {
                        ((QProcesador) componente).procesar(getRenderer(), escena);
                        //mapa de cubo
                    } else if (componente instanceof QMapaCubo) {
                        if (forzarActualizacionMapaReflejos) {
                            ((QMapaCubo) componente).setActualizar(true);
                        }
                        ((QMapaCubo) componente).actualizarMap(getRenderer());
                        //particulas
                    } else if (componente instanceof QEmisorParticulas) {
                        emisor = (QEmisorParticulas) componente;
                        emisor.emitir(QTime.delta);
                        for (QParticula particula : emisor.getParticulasNuevas()) {
                            escena.agregarEntidad(particula.objeto);
                        }
                    }

//                     for (QParticula particula : emisor.getParticulasEliminadas()) {
//                        escena.eliminarGeometria(particula.objeto);
//                        particula.objeto.renderizar=false;
//                        modificado = true;
//                    }
                    //agrego las particulas nueva y elimino las viejas
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        forzarActualizacionMapaReflejos = false;

    }

    public boolean isForzarActualizacionMapaReflejos() {
        return forzarActualizacionMapaReflejos;
    }

    public void setForzarActualizacionMapaReflejos(boolean forzarActualizacionMapaReflejos) {
        this.forzarActualizacionMapaReflejos = forzarActualizacionMapaReflejos;
    }

    public List<QMotorRender> getRendererList() {
        return rendererList;
    }

    public void setRendererList(List<QMotorRender> rendererList) {
        this.rendererList = rendererList;
    }

    public List<Accion> getAccionesEjecucion() {
        return accionesEjecucion;
    }

    public void setAccionesEjecucion(List<Accion> accionesEjecucion) {
        this.accionesEjecucion = accionesEjecucion;
    }

    public boolean isModificando() {
        return modificando;
    }

    public void setModificando(boolean modificando) {
        this.modificando = modificando;
    }

}
