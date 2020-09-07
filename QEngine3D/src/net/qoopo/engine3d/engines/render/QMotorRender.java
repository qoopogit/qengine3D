package net.qoopo.engine3d.engines.render;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import net.qoopo.engine3d.QMotor;
import net.qoopo.engine3d.QTime;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.interaccion.QMouseReceptor;
import net.qoopo.engine3d.componentes.interaccion.QTecladoReceptor;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.escena.QOrigen;
import net.qoopo.engine3d.core.input.QDefaultListener;
import net.qoopo.engine3d.core.input.QInputManager;
import net.qoopo.engine3d.core.input.control.gizmo.QGizmo;
import net.qoopo.engine3d.core.input.control.gizmo.transformacion.escala.QGizmoEscala;
import net.qoopo.engine3d.core.input.control.gizmo.transformacion.rotacion.QGizmoRotacion;
import net.qoopo.engine3d.core.input.control.gizmo.transformacion.traslacion.QGizmoTraslacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector2;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.util.Accion;
import net.qoopo.engine3d.engines.render.buffer.QFrameBuffer;
import net.qoopo.engine3d.engines.render.interno.postproceso.flujos.QRenderEfectos;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.engines.render.superficie.Superficie;

public abstract class QMotorRender extends QMotor {

    protected static BufferedImage imageSplash;

//--------------------------------------------------------------------------------------------------------------------------------
//                      CONSTANTES    
//--------------------------------------------------------------------------------------------------------------------------------
    public static final int RENDER_INTERNO = 1;
    public static final int RENDER_JAVA3D = 2;
    public static final int RENDER_OPENGL = 3;

    private static final Color COLOR_FONDO_ESTADISTICAS = new Color(0, 0, 0.6f, 0.4f);
    protected static int lightOnScreenSize = 20;
    public static final int GIZMO_NINGUNO = 0;
    public static final int GIZMO_TRASLACION = 1;
    public static final int GIZMO_ROTACION = 2;
    public static final int GIZMO_ESCALA = 3;

    static {
        try {
//            imageSplash = ImageIO.read(new File("res/imagenes/loading.png"));
//            imageSplash = ImageIO.read(new File("res/imagenes/wolf/wolf_6.png"));
//            imageSplash = ImageIO.read(new File("res/imagenes/wolf/wolf_4.png"));
//            imageSplash = ImageIO.read(new File("res/imagenes/wolf/wolf_5.png"));
//            imageSplash = ImageIO.read(new File("res/imagenes/wolf/wolf_8.png"));
//            imageSplash = ImageIO.read(new File("res/imagenes/wolf/wolf_9.png"));
//            BufferedImage bi = ImageIO.read(new File("res/imagenes/wolf/wolf_2.png"));
//            int maxancho = 400;
//            imageSplash = new BufferedImage(maxancho, (maxancho * bi.getHeight()) / bi.getWidth(), bi.getType());
//            imageSplash.getGraphics().drawImage(bi, imageSplash.getWidth(), imageSplash.getHeight(), null);
            imageSplash = ImageIO.read(QMotorRender.class.getResourceAsStream("/res/imagenes/wolf/wolf_9.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
            imageSplash = null;
        }
    }

    protected String nombre = "Default";
    /**
     * Indica al renderer que se esta cargando, para lo cual muestra una imagen
     * en cada render en lugar de procesar el escena
     */
    private boolean cargando = false;
    protected QColor colorFondo = QColor.BLACK;

    /**
     * Esta variable indica si se puede interactuar con el renderer Hay casos
     * como los que permiten la visa previa de los materiales y a las entidades
     * qu eno se debe interactuar con el mouse
     */
    protected boolean interactuar = true;

//--------------------------------------------------------------------------------------------------------------------------------
//                      VARIABLES    
//--------------------------------------------------------------------------------------------------------------------------------
    /**
     * La escena que se está renderizando
     */
    protected QEscena escena;
    /**
     * La cámara actual para la toma de la escena
     */
    protected QCamara camara;
    /**
     * Las opciones con las que se renderiza
     */
    public QOpcionesRenderer opciones = new QOpcionesRenderer();
    /**
     * Contiene los vértices y polígonos resultado de la transformación
     */

    /**
     * Plano que permite hacer culling
     */
    protected QClipPane panelClip = null;

    /**
     * Superficie donde se va a dibujar
     */
    protected Superficie superficie;//superficie donde se va a dibujar al finalizar el proceso de renderizado
    /**
     * Buffer donde se dibuja, antes de efectos
     */
    protected QFrameBuffer frameBuffer;
    /**
     * Buffer resultado de los efecto post procesamiento
     */
//    protected QFrameBuffer frameBufferFinal;
    /**
     * La textura donde se va a renderizar la salida del frameBuffer
     */
    protected QTextura textura;

    /**
     * Esta variable indica si el render es real. En caso de no serlo se salta
     * unas fases no necesarias para los virtuales
     */
    public boolean renderReal = true;
//
//    /**
//     * Mapa de los procesadores de sombras creados
//     */
//    protected final Map<String, QProcesadorSombra> procesadorSombras = new HashMap<>();

    /**
     * Bandera que indica si se muestran las estadísticas de renderizado
     */
    protected boolean mostrarEstadisticas = true;

    /**
     * Esta variable indica si debe renderizar los artefactos del editor como
     * gizmos y lineas guias
     */
    public boolean renderArtefactos = false;

    /**
     * Estadísticas. Los polígonos dibujados
     */
    public int poligonosDibujados = 0;
    public int poligonosDibujadosTemp = 0;

    protected List<QLuz> luces = new ArrayList<>();
    protected QVector3 tempVector = new QVector3();
    protected QVertice verticeLuz = new QVertice();

    protected ArrayList<QPoligono> listaCarasTransparente = new ArrayList<>();
    protected boolean tomar;

    public int tempX;
    public int tempY;
    public int tempZ;

    public List<QEntidad> entidadesSeleccionadas = new ArrayList<>();
    public QEntidad entidadActiva = null;

    protected QRenderEfectos efectosPostProceso;

    // varables usados para detectar movimiento en la pantalla
    public int prevX = -1;
    public int prevY = -1;

    public boolean ctrl = false;
    public boolean shift = false;
    public boolean alt = false;
    public float horaDelDia = 0;

    private Robot robot;

    protected boolean forzarActualizacionMapaSombras = false;

    protected QGizmo gizTraslacion = new QGizmoTraslacion();
    protected QGizmo gizRotacion = new QGizmoRotacion();
    protected QGizmo gizEscala = new QGizmoEscala();
    protected QGizmo gizActual = null;

    protected int tipoGizmoActual = GIZMO_TRASLACION;

    protected Accion accionSeleccionar = null;//la accion que debe ejecutar cuando selecciona un objeto

    protected QPoligono polSeleccion = null;
    protected QPoligono polHueso = null;

    protected QOrigen entidadOrigen;

    public QMotorRender(QEscena escena, Superficie superficie, int ancho, int alto) {
        this(escena, "Nuevo Renderer", superficie, ancho, alto);
    }

    public QMotorRender(QEscena escena, String nombre, Superficie superficie, int ancho, int alto) {
        try {
            robot = new Robot();
        } catch (AWTException ex) {
        }

        this.escena = escena;
        this.nombre = nombre;
        this.superficie = superficie;
        this.opciones.setAncho(ancho);
        this.opciones.setAlto(alto);

        if (superficie != null) {
            this.superficie.getComponente().addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    resize();
                }
            });
        }
        prepararInputListener();

        // creo una entidad no existente en el escena con un material para poder tener un primitiva para el dibujo de la seleccion
        // se deberia revisar el raster por esas limitaciones
        polSeleccion = new QPoligono(new QGeometria());
        QMaterialBas matSeleccion = new QMaterialBas("matSeleccion");
        matSeleccion.setColorDifusa(QColor.YELLOW);
        matSeleccion.setFactorEmision(1.0f);
        polSeleccion.material = matSeleccion;
        polHueso = new QPoligono(new QGeometria());
        QMaterialBas matHueso = new QMaterialBas("matHueso");
        matHueso.setColorDifusa(QColor.GRAY);
        matHueso.setFactorEmision(0.15f);
        polHueso.material = matHueso;
        entidadOrigen = new QOrigen();
    }

    /**
     * Si al ventana cambia, actualiza el framebuffer (en caso de un usar
     * resolucion forzada)
     */
    public void resize() {
        int ancho = 0;
        int alto = 0;

        if (opciones.isForzarResolucion()) {
            ancho = opciones.getAncho();
            alto = opciones.getAlto();
        } else {
            if (this.getSuperficie() != null && this.getSuperficie().getComponente() != null) {
                ancho = this.getSuperficie().getComponente().getWidth();
                alto = this.getSuperficie().getComponente().getHeight();
            } else {
                System.out.println("Render:" + nombre + " no hay superficie. Seteando resolucion default 800x600");
                ancho = 800;
                alto = 600;
            }
        }

        //resolucion default
        if (ancho <= 0) {
            ancho = 800;
        }
        if (alto <= 0) {
            alto = 600;
        }

        if (camara != null) {
            camara.configurarRadioAspecto(ancho, alto);
        }

        frameBuffer = new QFrameBuffer(ancho, alto, textura);
    }

    /**
     * Permite la selección de un objeto con el mouse
     *
     * @param mouseLocation
     * @return
     */
    public QEntidad seleccionarEnPantalla(Point mouseLocation) {
        try {
            if (opciones.isForzarResolucion() && this.getSuperficie() != null) {
                mouseLocation.x = mouseLocation.x * opciones.getAncho() / this.getSuperficie().getComponente().getWidth();
                mouseLocation.y = mouseLocation.y * opciones.getAlto() / this.getSuperficie().getComponente().getHeight();
            }

            QVector2 ubicacionLuz = new QVector2();
            QVector3 tmp;
            // Selección de luces
            for (QEntidad entidad : escena.getListaEntidades()) {
                if (entidad.isRenderizar()) {
                    for (QComponente componente : entidad.getComponentes()) {
                        if (componente instanceof QLuz) {
                            tmp = QTransformar.transformarVector(QVector3.zero, entidad, camara);
                            camara.getCoordenadasPantalla(ubicacionLuz, new QVector4(tmp, 1), frameBuffer.getAncho(), frameBuffer.getAlto());
                            if ((ubicacionLuz.x - mouseLocation.x)
                                    * (ubicacionLuz.x - mouseLocation.x)
                                    + (ubicacionLuz.y - mouseLocation.y)
                                    * (ubicacionLuz.y - mouseLocation.y)
                                    <= lightOnScreenSize * lightOnScreenSize / 4) {
                                return entidad;
                            }
                        }
                    }
                }
            }

            //metodo donde ordeno las caras y veo si lo que selecciona esta dentro de las coordenadas de pantalla de la cara
//            Arrays.sort(carasAdibujarOrdenadas);
////            for (QPoligono primitiva : carasAdibujarOrdenadas) {
////            for (int ii=0;ii< carasAdibujarOrdenadas.length;ii++) {
//            for (int ii = carasAdibujarOrdenadas.length - 1; ii > 0; ii--) {
//
//                QPoligono primitiva = carasAdibujarOrdenadas[ii];
//                Point[] facePoints = new Point[primitiva.listaVertices.length];
//                int j = 0;
//                for (int i : primitiva.listaVertices) {
//                    facePoints[j] = new Point();
//                    camara.proyectar(facePoints[j++], bufferVerticesTransformados.getVertice(i));
//                }
//                if (pointBelongsToPlane(mouseLocation, facePoints)) {
//                    return primitiva.geometria.entidad;
//                }
//            }
            // metodo donde tomo las coordenadas de pantalla del cursor y veo en buffer el pixel    
            QPixel pixel = frameBuffer.getPixel(mouseLocation.x, mouseLocation.y);
            if (pixel != null) {
                return pixel.entidad;
            }

        } catch (Exception e) {
        }
        return null;
    }

    public Superficie getSuperficie() {
        return superficie;
    }

    public void setSuperficie(Superficie superficie) {
        this.superficie = superficie;
    }

    public float getCameraRotationZ() {
        return camara.getTransformacion().getRotacion().getAngulos().getAnguloZ();
    }

    public void setCameraRotationZ(float rotateZ) {
        this.camara.getTransformacion().getRotacion().rotarZ(rotateZ);
    }

    public QCamara getCamara() {
        return camara;
    }

    public void setCamara(QCamara camara) {
        this.camara = camara;
    }

    public QEscena getEscena() {
        return escena;
    }

    public void setEscena(QEscena escena) {
        this.escena = escena;
    }

//    public abstract void dibujarPixel(int x, int y);
    protected void prepararInputListener() {
        //creo los receptores  para agregar al inputManager
        QInputManager.agregarListenerMouse(new QMouseReceptor() {

            private QEntidad selectedObject;

            @Override
            public void mouseEntered(MouseEvent evt) {
                if (interactuar) {
                    try {
                        getSuperficie().getComponente().requestFocus();
                    } catch (Exception e) {
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                if (!interactuar) {
                    return;
                }
                if (SwingUtilities.isLeftMouseButton(evt)) {
                    selectedObject = seleccionarEnPantalla(new Point(evt.getX(), evt.getY()));
                    if (selectedObject instanceof QGizmo //|| selectedObject instanceof QGizmoParte
                            ) {
                        return;
                    }
                    entidadActiva = selectedObject;
                    if (entidadActiva == null) {
                        entidadesSeleccionadas.clear();
                        return;
                    }
                    if (!shift) {
                        entidadesSeleccionadas.clear();
                    }
                    entidadesSeleccionadas.add(entidadActiva);
                    if (accionSeleccionar != null) {
                        accionSeleccionar.ejecutar(entidadActiva);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                prevX = -1;
                prevY = -1;
                selectedObject = null;
            }

            @Override
            public void mouseDragged(MouseEvent evt) {
                if (!interactuar) {
                    return;
                }
                int deltaX = 0;
                int deltaY = 0;

                if (prevX >= 0) {
                    deltaX = evt.getXOnScreen() - prevX;
                }
                if (prevY >= 0) {
                    deltaY = evt.getYOnScreen() - prevY;
                }

                if (SwingUtilities.isLeftMouseButton(evt)) {
                    // activo los Gizmos
                    if (selectedObject != null) {
                        if (selectedObject instanceof QGizmo) {
                            ((QGizmo) selectedObject).mouseMove(deltaX, -deltaY);
//                        } else if (selectedObject instanceof QGizmoParte) {
//                            ((QGizmoParte) selectedObject).mouseMove(deltaX, -deltaY);
                        }
                    }
                }

                if (SwingUtilities.isMiddleMouseButton(evt)) {
                    if (!shift && !ctrl && !alt) {
                        //rota camara 
//                        camara.mouseRotar(deltaX, deltaY);
                        camara.aumentarRotY((float) Math.toRadians(-deltaX / 2));
                        camara.aumentarRotX((float) Math.toRadians(-deltaY / 2));

                    } else if (shift && !ctrl && !alt) {
                        //mueve la camara 
                        camara.moverDerechaIzquierda(-deltaX / 100.0f);
                        camara.aumentarY(deltaY / 100.0f);
//                        camara.mouseMoveCamara(deltaX, deltaY);
                    }
                }

                prevX = evt.getXOnScreen();
                prevY = evt.getYOnScreen();
                warpMouse(evt.getXOnScreen(), evt.getYOnScreen());
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent evt) {
                if (interactuar) {
                    if (evt.getWheelRotation() < 0) {
                        if (!shift) {
                            camara.moverAdelanteAtras(0.2f);
                        } else {
                            camara.moverAdelanteAtras(1f);
                        }
                    } else {
                        if (!shift) {
                            camara.moverAdelanteAtras(-0.2f);
                        } else {
                            camara.moverAdelanteAtras(-1f);
                        }
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent evt) {
                if (interactuar) {

                }
            }

            @Override
            public void destruir() {

            }
        });
        QInputManager.agregarListenerTeclado(new QTecladoReceptor() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (!interactuar || !renderReal) {
                    return;
                }
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_J:
//                    case KeyEvent.VK_NUMPAD5:
                        camara.setOrtogonal(!camara.isOrtogonal());
                        break;
                    case KeyEvent.VK_Y:
                        opciones.setTipoVista(QOpcionesRenderer.VISTA_WIRE);
                        break;
                    case KeyEvent.VK_U:
                        opciones.setTipoVista(QOpcionesRenderer.VISTA_FLAT);
                        break;
                    case KeyEvent.VK_I:
                        opciones.setTipoVista(QOpcionesRenderer.VISTA_PHONG);
                        break;
                    case KeyEvent.VK_T:
                        mostrarEstadisticas = !mostrarEstadisticas;
                        break;
                    case KeyEvent.VK_O:
                        opciones.setMaterial(!opciones.isMaterial());
                        break;
//                    case KeyEvent.VK_M:
//                        opciones.setShowNormal(!opciones.isShowNormal());
//                        break;
                    case KeyEvent.VK_B:
                        opciones.setVerCarasTraseras(!opciones.isVerCarasTraseras());
                        break;
                    case KeyEvent.VK_N:
                        opciones.setNormalMapping(!opciones.isNormalMapping());
                        break;
                    case KeyEvent.VK_L:
                        opciones.setDibujarLuces(!opciones.isDibujarLuces());
                        break;
                    case KeyEvent.VK_P:
                        opciones.setSombras(!opciones.isSombras());
                        break;
                    case KeyEvent.VK_1:
                        tipoGizmoActual = GIZMO_NINGUNO;
                        break;
                    case KeyEvent.VK_2:
                        tipoGizmoActual = GIZMO_TRASLACION;
                        break;
                    case KeyEvent.VK_3:
                        tipoGizmoActual = GIZMO_ROTACION;
                        break;
                    case KeyEvent.VK_4:
                        tipoGizmoActual = GIZMO_ESCALA;
                        break;
                    case KeyEvent.VK_Q:
                        if (!shift) {
                            camara.aumentarY(0.2f);
                        } else {
                            camara.aumentarY(0.8f);
                        }

                        break;
                    case KeyEvent.VK_E:
                        if (!shift) {
                            camara.aumentarY(-0.2f);
                        } else {
                            camara.aumentarY(-0.8f);
                        }
                        break;
                    case KeyEvent.VK_W:
                        //ir hacia adelante
                        if (!shift) {
                            camara.moverAdelanteAtras(0.2f);
                        } else {
                            camara.moverAdelanteAtras(0.8f);
                        }
                        break;
                    case KeyEvent.VK_S:
                        if (!shift) {
                            camara.moverAdelanteAtras(-0.2f);
                        } else {
                            camara.moverAdelanteAtras(-0.8f);
                        }
                        break;
                    case KeyEvent.VK_D:
                        //camara.aumentarZ(1);
                        if (!shift) {
                            camara.moverDerechaIzquierda(0.2f);
                        } else {
                            camara.moverDerechaIzquierda(0.8f);
                        }
                        break;
                    case KeyEvent.VK_A:
                        if (!shift) {
                            camara.moverDerechaIzquierda(-0.2f);
                        } else {
                            camara.moverDerechaIzquierda(-0.8f);
                        }
                        break;
                    case KeyEvent.VK_UP:
                        camara.aumentarRotX((float) Math.toRadians(5));
                        break;
                    case KeyEvent.VK_DOWN:
                        camara.aumentarRotX((float) Math.toRadians(-5));
                        break;
                    case KeyEvent.VK_RIGHT:
                        camara.aumentarRotY((float) Math.toRadians(-5));
                        break;
                    case KeyEvent.VK_LEFT:
                        camara.aumentarRotY((float) Math.toRadians(5));
                        break;
                    case KeyEvent.VK_CONTROL:
                        ctrl = true;
                        break;
                    case KeyEvent.VK_SHIFT:
                        shift = true;
                        break;
                    case KeyEvent.VK_ALT:
                        alt = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent evt) {
                if (!interactuar) {
                    return;
                }
                switch (evt.getKeyCode()) {
                    case KeyEvent.VK_CONTROL:
                        ctrl = false;
                        break;
                    case KeyEvent.VK_SHIFT:
                        shift = false;
                        break;
                    case KeyEvent.VK_ALT:
                        alt = false;
                        break;
                }
            }
        });
        if (superficie != null && superficie.getComponente() != null) {
            agregarListeners(superficie.getComponente());
//            System.out.println("a la superficie se agrego el listener");
        } else {
            System.out.println("no se agrego el listener");
        }
    }

    protected void agregarListeners(Component componente) {
        componente.addMouseMotionListener(QDefaultListener.INSTANCIA);
        superficie.getComponente().addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                rendererFocusLost(evt);
            }
        });
        componente.addMouseWheelListener(QDefaultListener.INSTANCIA);
        componente.addMouseListener(QDefaultListener.INSTANCIA);
        componente.addKeyListener(QDefaultListener.INSTANCIA);
    }

    protected void rendererFocusLost(java.awt.event.FocusEvent evt) {
        ctrl = false;
        shift = false;
        alt = false;
    }

    public void warpMouse(int x, int y) {
        if (robot != null) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            if (x >= screenSize.width - 1) {
                x = 1;
                prevX = 0;
            }
            if (y >= screenSize.height - 1) {
                y = 1;
                prevY = 0;
            }
            if (x <= 0) {
                x = screenSize.width - 2;
                prevX = screenSize.width - 1;
            }
            if (y <= 0) {
                y = screenSize.height - 2;
                prevY = screenSize.height - 1;
            }
            robot.mouseMove(x, y);
        }
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isMostrarEstadisticas() {
        return mostrarEstadisticas;
    }

    public void setMostrarEstadisticas(boolean renderEstadisticas) {
        this.mostrarEstadisticas = renderEstadisticas;
    }

    public QFrameBuffer getFrameBuffer() {
        return frameBuffer;
    }

    public void setFrameBuffer(QFrameBuffer frameBuffer) {
        this.frameBuffer = frameBuffer;
    }

    /**
     * Dibuja las estadísticas
     *
     * @param g
     */
    protected void mostrarEstadisticas(Graphics g) {
        if (mostrarEstadisticas && renderReal) {
            int ancho = 0;
            int alto = 0;

            if (opciones.isForzarResolucion()) {
                ancho = opciones.getAncho();
                alto = opciones.getAlto();
            } else {
                ancho = this.getSuperficie().getComponente().getWidth();
                alto = this.getSuperficie().getComponente().getHeight();
            }
            g.setColor(COLOR_FONDO_ESTADISTICAS);
            g.fillRect(0, 0, 170, 115);
            g.setColor(Color.orange);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString(nombre != null ? nombre : "", 10, 10);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            g.drawString(ancho + "x" + alto, 10, 20);
            g.drawString("FPS       :" + DF.format(getFPS()), 10, 30);
            g.drawString("Delta (ms):" + DF.format(getDelta()), 10, 40);
            g.drawString("FPS       :" + QTime.FPS, 10, 50);
            g.drawString("Delta (ms):" + QTime.delta / 10000000, 10, 60);
            g.drawString("Pol       :" + poligonosDibujados, 10, 70);
            g.drawString("T. Vista  : " + (opciones.getTipoVista() == QOpcionesRenderer.VISTA_FLAT ? "FLAT" : (opciones.getTipoVista() == QOpcionesRenderer.VISTA_PHONG ? "PHONG" : (opciones.getTipoVista() == QOpcionesRenderer.VISTA_WIRE ? "WIRE" : "N/A"))), 10, 80);
            g.drawString("Material  :" + (opciones.isMaterial() ? "ACTIVADO" : "DESACTIVADO"), 10, 90);
            g.drawString("Sombras   :" + (opciones.isSombras() ? "ACTIVADO" : "DESACTIVADO"), 10, 100);
            g.drawString("Hora del día :" + horaDelDia, 10, 110);
//            g.drawString("Cam (X;Y;Z): (" + DF.format(camara.transformacion.getTraslacion().x) + ";" + DF.format(camara.transformacion.getTraslacion().y) + ";" + DF.format(camara.transformacion.getTraslacion().z) + ")", 10, 80);
//            g.drawString("Ang (X;Y;Z): (" + DF.format(Math.toDegrees(camara.transformacion.getRotacion().getAngulos().getAnguloX())) + ";" + DF.format(Math.toDegrees(camara.transformacion.getRotacion().getAngulos().getAnguloY())) + ";" + DF.format(Math.toDegrees(camara.transformacion.getRotacion().getAngulos().getAnguloZ())) + ")", 10, 90);

        }
//        gf.drawImage(g., 0, 0, this.getSuperficie().getComponente().getWidth(), this.getSuperficie().getComponente().getHeight(), this.getSuperficie().getComponente());

    }

    /**
     * Muestra una imagen splash
     */
    protected void mostrarSplash() {
        if (renderReal && imageSplash != null) //dibujamos una imagen de carga
        {
            if (this.getSuperficie() != null
                    && this.getSuperficie().getComponente() != null
                    && this.getSuperficie().getComponente().getGraphics() != null) {
                this.getSuperficie().getComponente().getGraphics().setColor(Color.BLACK);
                this.getSuperficie().getComponente().getGraphics().fillRect(0, 0, this.getSuperficie().getComponente().getWidth(), this.getSuperficie().getComponente().getHeight());
                this.getSuperficie().getComponente().getGraphics().drawImage(imageSplash,
                        (this.getSuperficie().getComponente().getWidth() - imageSplash.getWidth()) / 2,
                        (this.getSuperficie().getComponente().getHeight() - imageSplash.getHeight()) / 2,
                        imageSplash.getWidth(),
                        imageSplash.getHeight(),
                        this.getSuperficie().getComponente());

            }
        }
    }

    public QTextura getTextura() {
        return textura;
    }

    public void setTextura(QTextura textura) {
        this.textura = textura;
        if (frameBuffer != null) {
            frameBuffer.setTextura(textura);
        }
    }

    public QClipPane getPanelClip() {
        return panelClip;
    }

    public void setPanelClip(QClipPane panelClip) {
        this.panelClip = panelClip;
    }

    public List<QLuz> getLuces() {
        return luces;
    }

    public void setLuces(List<QLuz> luces) {
        this.luces = luces;
    }

//    public Map<String, QProcesadorSombra> getProcesadorSombras() {
//        return procesadorSombras;
//    }
    public boolean isCargando() {
        return cargando;
    }

    public void setCargando(boolean cargando) {
        this.cargando = cargando;
    }

    public QRenderEfectos getEfectosPostProceso() {
        return efectosPostProceso;
    }

    public void setEfectosPostProceso(QRenderEfectos efectosPostProceso) {
        this.efectosPostProceso = efectosPostProceso;
    }

    public boolean isForzarActualizacionMapaSombras() {
        return forzarActualizacionMapaSombras;
    }

    public void setForzarActualizacionMapaSombras(boolean forzarActualizacionMapaSombras) {
        this.forzarActualizacionMapaSombras = forzarActualizacionMapaSombras;
    }

    public QColor getColorFondo() {
        return colorFondo;
    }

    public void setColorFondo(QColor colorFondo) {
        this.colorFondo = colorFondo;
    }

    public Accion getAccionSeleccionar() {
        return accionSeleccionar;
    }

    public void setAccionSeleccionar(Accion accionSeleccionar) {
        this.accionSeleccionar = accionSeleccionar;
    }

    public boolean isInteractuar() {
        return interactuar;
    }

    public void setInteractuar(boolean interactuar) {
        this.interactuar = interactuar;
    }

    public boolean isRenderReal() {
        return renderReal;
    }

    public void setRenderReal(boolean renderReal) {
        this.renderReal = renderReal;
    }

    public boolean isRenderArtefactos() {
        return renderArtefactos;
    }

    public void setRenderArtefactos(boolean renderArtefactos) {
        this.renderArtefactos = renderArtefactos;
    }

    /**
     * Cambia el raster
     *
     * @param opcion
     */
    public void cambiarRaster(int opcion) {

    }

    /**
     * Cambia el shader
     *
     * @param shader
     */
    public void cambiarShader(int opcion) {

    }
}
