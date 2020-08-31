/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.sombras.renders;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QVector2;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.QOpcionesRenderer;
import net.qoopo.engine3d.engines.render.interno.rasterizador.AbstractRaster;
import net.qoopo.engine3d.engines.render.interno.rasterizador.QRaster2;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales.QSimpleShaderBAS;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;

/**
 * EL renderizador de sombras es una implementacion de un mapa de sombras
 *
 * @author alberto
 */
public class QRenderSombras extends QMotorRender {

    private JFrame ventana;// para mostrar el mapa de sombras
    private JPanel panelDibujo;

    //estos tipos los uso para saber que posicion usar para el calculo de la camara
    //si es direcccional uso la posicion de la camara del render
    public static int DIRECIONALES = 1;
    //si es omnidireccional uso la posicion de la luz, pues es para luces puntuales
    public static int NO_DIRECCIONALES = 2;

    protected int tipo = DIRECIONALES;
    protected QLuz luz;
    protected QCamara camaraRender;
    protected QVector3 direccion;

    //---------------------------------------
    protected QVector3 posicion;
    protected QVector3 normalDireccion = QVector3.zero.clone();
    protected QVector3 centro;
    protected QVector3 vArriba = QVector3.unitario_y.clone();

    protected boolean cascada = false;
    protected int cascada_tamanio = 1;
    protected int cascada_indice = 1;

    /**
     * Representa la distancia del centro de la seccion del frustrum cuando es
     * en tipo cascada
     */
    private float distanciaCascada = -1;

//    protected float radio;
    //este factor se calcula en cada renderizado dependiendo del rango de profundidades
//    private float factorAcne = 0.05f;
    private float factorAcne = 0.0f;

    //----------------------------------------------
    private QShader shader;
    private AbstractRaster raster;

    public QRenderSombras(int tipo, QEscena mundo, QLuz luz, int ancho, int alto) {
        super(mundo, "Sombra " + luz.entidad.getNombre(), null, ancho, alto);
        this.tipo = tipo;
        this.luz = luz;
        this.opciones.forzarResolucion = true;
    }

    public QRenderSombras(int tipo, QEscena mundo, QLuzDireccional luz, QCamara camaraRender, int ancho, int alto) {
        this(tipo, mundo, luz, ancho, alto);
        camara = new QCamara();
//        camara.frustrumLejos = 500.0f;
        camara.frustrumLejos = camaraRender.frustrumLejos;
        camara.setOrtogonal(true);
//        camara.setEscalaOrtogonal(50);
        camara.setEscalaOrtogonal(100);
        camara.configurarTamanioPantala(ancho, alto);
        this.camaraRender = camaraRender;
        setDireccion(luz.getDirection());
        raster = new QRaster2(this);
//        shader = new QFullShader(this);
        shader = new QSimpleShaderBAS(this);
    }

    public QRenderSombras(int tipo, QEscena mundo, QLuzSpot luz, QCamara camaraRender, int ancho, int alto) {
        this(tipo, mundo, luz, ancho, alto);
        camara = new QCamara();
        camara.frustrumLejos = luz.radio;
        camara.setOrtogonal(false);
        camara.setFOV(luz.getAngulo());
        camara.configurarTamanioPantala(ancho, alto);
        this.camaraRender = camaraRender;
        setDireccion(luz.getDirection());
        raster = new QRaster2(this);
//        shader = new QFullShader(this);
        shader = new QSimpleShaderBAS(this);
    }

    public QRenderSombras(int tipo, QEscena mundo, QLuzPuntual luz, QCamara camaraRender, int ancho, int alto, QVector3 direccion, QVector3 arriba) {
        this(tipo, mundo, luz, ancho, alto);
        camara = new QCamara();
        camara.frustrumLejos = luz.radio;
        camara.setOrtogonal(false);
        //como es un mapeo cubico el angulo sera de 90 grados
        camara.setFOV((float) Math.toRadians(90));//360grados de vision para 4
        camara.configurarTamanioPantala(ancho, alto);
        this.camaraRender = camaraRender;
        setDireccion(direccion);
        vArriba = arriba;
        raster = new QRaster2(this);
//        shader = new QFullShader(this);
        shader = new QSimpleShaderBAS(this);
    }

    public void limpiar() {
        try {
            frameBuffer.limpiarZBuffer();
            frameBuffer.llenarColor(colorFondo);
        } catch (Exception e) {
        }
    }

    public QVector3 getDireccion() {
        return direccion;
    }

    public void setDireccion(QVector3 direccion) {
        this.direccion = direccion;
    }

    public QLuz getLuz() {
        return luz;
    }

    public void setLuz(QLuz luz) {
        this.luz = luz;
    }

    /**
     * Actualiza el campo de vision de la camara del render de sombras
     */
    protected void actualizarCampoVision() {
        //voy a trabajar con una esfera donde posicionare la camara, esta en el lugar contrario a la direccion de la luz
        normalDireccion.set(direccion);
        //invierte la direcion para ir contrario a la luz
        normalDireccion.multiply(-1.0f);
        normalDireccion.normalize();
        float radio = 0.0f;
        if (tipo == QRenderSombras.DIRECIONALES) {
//          la camara se puede mover al ser adjunta a una entidad
            centro = camaraRender.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector();
            if (!cascada) {
                radio = Math.abs(camaraRender.frustrumLejos - camaraRender.frustrumCerca) / 2;
                // modifico el centro para que sea el centro de frustrum de visión de la caḿara
                //por lo tanto aumento un vector de dimensión igual a la distancia del frustrum/2 (centro)
                // con dirección de la vista de la ćamara
                float d = Math.abs(camaraRender.frustrumLejos - camaraRender.frustrumCerca) / 2;
//                float d = camaraRender.frustrumCerca;
                //es negativo (-d) porque la direccion apunta hacia atras de la camara (la camara apunta haca -z en lugar de +z)
                centro.add(camaraRender.getDireccion().clone().normalize().multiply(-d));
            } else {
                //direccional cascada
                if (distanciaCascada == -1) {
//                    float uniforme = Math.abs(camaraRender.frustrumLejos - camaraRender.frustrumCerca) / cascada_tamanio;
//                    float logaritmica = (float) Math.log((float) cascada_tamanio / (float) cascada_indice);
//                    //esta distancia es la que agregaré a la posición de la cámara
//                    distanciaCascada = cascada_indice * uniforme + (1 - logaritmica) * cascada_indice;
//                    distanciaCascada = cascada_indice * uniforme + (logaritmica) * cascada_indice;
////                radio = (camaraRender.frustrumLejos - camaraRender.frustrumCerca) / cascada_tamanio;
//-------------------------------------
//                    //metodo jmonkey//
                    float IDM = cascada_indice / (float) cascada_tamanio;
                    float log = camaraRender.frustrumCerca * QMath.pow((camaraRender.frustrumLejos / camaraRender.frustrumCerca), IDM);
                    float uniform = camaraRender.frustrumCerca + (camaraRender.frustrumLejos - camaraRender.frustrumCerca) * IDM;
                    distanciaCascada = log * QGlobal.lambda + uniform * (1.0f - QGlobal.lambda);
                    distanciaCascada = distanciaCascada / 2;
                    radio = distanciaCascada;
                }
                // modifico el centro para que sea el centro de la sección del frustrum de visión de la caḿara
                centro.add(camaraRender.getDireccion().clone().normalize().multiply(-distanciaCascada / 2));
            }
            //la posicion de la luz
            posicion = centro.clone().add(normalDireccion.multiply(radio));
            camara.lookAtPosicionObjetivo(posicion, centro, vArriba);
        } else {
            //LUCES NO DIRECCIONALES
//            posicion = luz.entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector();
            posicion = luz.entidad.getTransformacion().getTraslacion().clone();
            camara.lookAt(posicion, normalDireccion, vArriba);
            //la luz es el centro de la esfera
//            centro = luz.entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector();
//            radio = 0.0001f;
//            posicion = centro.add(normalDireccion.multiply(radio));
//            camara.lookAt(posicion, normalDireccion, vArriba);
        }
    }

    public boolean isCascada() {
        return cascada;
    }

    public void setCascada(boolean cascada) {
        this.cascada = cascada;
    }

    public int getCascada_tamanio() {
        return cascada_tamanio;
    }

    public void setCascada_tamanio(int cascada_tamanio) {
        this.cascada_tamanio = cascada_tamanio;
    }

    public int getCascada_indice() {
        return cascada_indice;
    }

    public void setCascada_indice(int cascada_indice) {
        this.cascada_indice = cascada_indice;
    }

    public float getDistanciaCascada() {
        return distanciaCascada;
    }

    public void setDistanciaCascada(float distanciaCascada) {
        this.distanciaCascada = distanciaCascada;
    }

    protected void pintarMapa() {
        try {
            if (ventana == null) {
                ventana = new JFrame(this.nombre);
                ventana.setSize(frameBuffer.getAncho(), frameBuffer.getAlto());
                ventana.setPreferredSize(new Dimension(frameBuffer.getAncho(), frameBuffer.getAlto()));
                ventana.setLayout(new BorderLayout());
                ventana.setResizable(false);
                panelDibujo = new JPanel();
                ventana.add(panelDibujo, BorderLayout.CENTER);
                ventana.setVisible(true);
                ventana.pack();
            }
            if (frameBuffer != null) {
//                ventana.setTitle(nombre + " " + System.currentTimeMillis() / 1000);
                frameBuffer.pintarMapaProfundidad();
                BufferedImage bi = frameBuffer.getRendered();
                panelDibujo.getGraphics().drawImage(bi, 0, 0, frameBuffer.getAncho(), frameBuffer.getAlto(), null);
//            } else {
//                ventana.setTitle(nombre + " " + System.currentTimeMillis() / 1000 + " no hay framebuffer");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Realiza el proceso de renderizado
     *
     * @throws Exception
     */
    private void render() {
        listaCarasTransparente.clear();
        poligonosDibujadosTemp = 0;
        TempVars t = TempVars.get();
        try {
            //La Matriz de vista es la inversa de la matriz de la camara.
            // Esto es porque la camara siempre estara en el centro y movemos el mundo
            // en direccion contraria a la camara.
            QMatriz4 matrizVista = camara.getMatrizTransformacion(QGlobal.tiempo).invert();
            for (QEntidad entidad : escena.getListaEntidades()) {
                QTransformar.transformar(entidad, matrizVista, t.bufferVertices1);

                //--------------------------------------------------------------------------------------
                //                  CARAS SOLIDAS NO TRANSPARENTES
                //--------------------------------------------------------------------------------------
                for (QPrimitiva poligono : t.bufferVertices1.getPoligonosTransformados()) {
                    // salta las camaras 
                    if (poligono.geometria.entidad instanceof QCamara) {
                        continue;
                    }

                    if (poligono.material == null
                            //q no tengra transparencia cuando tiene el tipo de material basico
                            || (poligono.material instanceof QMaterialBas && !((QMaterialBas) poligono.material).isTransparencia())) {
                        tomar = false;
                        //comprueba que todos los vertices estan en el campo de vision
                        for (int i : poligono.listaVertices) {
                            if (camara.estaEnCampoVision(t.bufferVertices1.getVertice(i))) {
                                tomar = true;
                                break;
                            }
                        }
                        if (!tomar) {
                            continue;
                        }
                        poligonosDibujadosTemp++;
                        raster.raster(t.bufferVertices1, poligono, opciones.tipoVista == QOpcionesRenderer.VISTA_WIRE || poligono.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE, false);
                    } else {
                        if (poligono instanceof QPoligono) {
                            listaCarasTransparente.add((QPoligono) poligono);
                        }
                    }
                }

                //--------------------------------------------------------------------------------------
                //                  TRANSPARENTES
                //--------------------------------------------------------------------------------------
                //ordeno las caras transparentes 
                if (!listaCarasTransparente.isEmpty()) {
                    if (opciones.zSort) {
                        Collections.sort(listaCarasTransparente);
                    }
                    for (QPoligono poligono : listaCarasTransparente) {
                        poligonosDibujadosTemp++;
                        raster.raster(t.bufferVertices1, poligono, opciones.tipoVista == QOpcionesRenderer.VISTA_WIRE || poligono.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE, false);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            t.release();
        }

    }

    @Override
    public long update() {
        long tInicio = System.nanoTime();
        try {
            if (frameBuffer != null && direccion != null) {
                limpiar();
                actualizarCampoVision();
                render();
            } else {
                System.out.println("no deberia entrar por aca");
            }
            poligonosDibujados = poligonosDibujadosTemp;
            //como no tengo normalizadas las coordenadas z necesito estos valores para que el factor acne seal el 3% de esta diferencia
            frameBuffer.calcularMaximosMinimosZBuffer();
            factorAcne = (frameBuffer.getMaximo() - frameBuffer.getMinimo()) * 0.1f;
            if (QGlobal.SOMBRAS_DEBUG_PINTAR) {
                pintarMapa();
            }
//        System.out.println("PS-->" + df.format(1000.0 / getDelta()) + " FPS");
        } catch (Exception e) {
            e.printStackTrace();
        }
        tiempoPrevio = System.currentTimeMillis();
        return System.nanoTime() - tInicio;
    }

    /**
     * Calcula cuanta sombra recibe un punto. 1. Nada de sombra, 0 Totalmente en
     * sombra.
     *
     * @param vector
     * @param entidad
     * @return
     */
    public float factorSombra(QVector3 vector, QEntidad entidad) {
        float factor = 0.0f;
        QVector2 punto = new QVector2();
        try {
            if (frameBuffer != null) {
                vector = QTransformar.transformarVector(vector, entidad, camara);
                camara.getCoordenadasPantalla(punto, new QVector4(vector, 1), getFrameBuffer().getAncho(), getFrameBuffer().getAlto());
                //si el punto no esta en mi campo de vision
                if ((punto.x < 0)
                        || (punto.y < 0)
                        || (punto.x > getFrameBuffer().getAncho())
                        || (punto.y > getFrameBuffer().getAlto())) {
                    factor = 0;
                } else {
                    //metodo donde sale la sombra pixelada
                    if (!QGlobal.SOMBRAS_SUAVES) {
                        factor = (-vector.z - factorAcne) > frameBuffer.getZBuffer((int) punto.y, (int) punto.x) ? 1 : 0;
                    } else {
                        //metodo con la sombra suave
                        for (int row = -1; row <= 1; ++row) {
                            for (int col = -1; col <= 1; ++col) {
                                try {
                                    factor += (-vector.z - factorAcne) > frameBuffer.getZBuffer((int) punto.y + row, (int) punto.x + col) ? 1 : 0;
                                } catch (Exception e) {
                                }
                            }
                        }
                        factor /= 9.0f;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1.0f - factor;
    }

    @Override
    public void dibujarPixel(int x, int y) {
//        frameBuffer.setQColor(x, y, shader.colorearPixel(frameBuffer.getPixel(y, x), x, y));
    }

    @Override
    public void iniciar() {

    }

    @Override
    public void detener() {

    }
}
