package net.qoopo.engine3d.engines.render.interno;

import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.QEntidad;
import net.qoopo.engine3d.componentes.QUtilComponentes;
import net.qoopo.engine3d.componentes.animacion.QEsqueleto;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.iluminacion.QLuz;
import net.qoopo.engine3d.componentes.iluminacion.QLuzDireccional;
import net.qoopo.engine3d.componentes.iluminacion.QLuzPuntual;
import net.qoopo.engine3d.componentes.iluminacion.QLuzSpot;
import net.qoopo.engine3d.componentes.shader.QShaderComponente;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QVector2;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.animacion.esqueleto.QHueso;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.QOpcionesRenderer;
import net.qoopo.engine3d.engines.render.interno.rasterizador.AbstractRaster;
import net.qoopo.engine3d.engines.render.interno.rasterizador.QRaster1;
import net.qoopo.engine3d.engines.render.interno.rasterizador.QRaster2;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QFlatShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QFullShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QIluminadoShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QPhongShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QShadowShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QSimpleShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QTexturaShader;
import net.qoopo.engine3d.engines.render.interno.sombras.QProcesadorSombra;
import net.qoopo.engine3d.engines.render.interno.sombras.procesadores.QSombraCono;
import net.qoopo.engine3d.engines.render.interno.sombras.procesadores.QSombraDireccional;
import net.qoopo.engine3d.engines.render.interno.sombras.procesadores.QSombraDireccionalCascada;
import net.qoopo.engine3d.engines.render.interno.sombras.procesadores.QSombraOmnidireccional;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.engines.render.interno.transformacion.QVertexShader;
import net.qoopo.engine3d.engines.render.superficie.Superficie;

/**
 * Renderizador interno. No utiliza aceleracion por Hardware
 *
 * @author alberto
 */
public class QRender extends QMotorRender {

    private static SimpleDateFormat sf = new SimpleDateFormat("yyMMdd");

    //El sombreador (el que calcula el color de cada pixel)
    protected QShader shader;
    //El sombreador default (el que calcula el color de cada pixel)
    protected QShader defaultShader;
    //El que crea los triangulos y llama al shader en cada pixel
    protected AbstractRaster raster;

    public QRender(QEscena universo, Superficie superficie, int ancho, int alto) {
        super(universo, superficie, ancho, alto);
        defaultShader = new QFullShader(this);
        raster = new QRaster1(this);
//        raster = new QRaster2(this);
    }

    public QRender(QEscena universo, String nombre, Superficie superficie, int ancho, int alto) {
        super(universo, nombre, superficie, ancho, alto);
        defaultShader = new QFullShader(this);
        raster = new QRaster1(this);
//        raster = new QRaster2(this);
    }

    @Override
    public long update() {
        //el tiempo usado para manejar la cache de las transformaciones
        QGlobal.tiempo = System.currentTimeMillis();
        try {
            if (!isCargando()) {
                if (frameBuffer != null) {
                    QLogger.info("");
                    QLogger.info("=======================  QRENDER =======================");
                    QLogger.info("");
                    getSubDelta();
                    limpiarPantalla();
                    QLogger.info("P1. Limpiar pantalla=" + getSubDelta());
                    actualizarLucesSombras();
                    QLogger.info("P2. Actualizar luces y sombras=" + getSubDelta());
                    calcularSombras();
                    QLogger.info("P3. Tiempo calcular sombras=" + getSubDelta());
                    QLogger.info("P5. ----Renderizado-----");
                    render();
                    if (renderReal && renderArtefactos) {
                        renderArtefactosEditor();
                        QLogger.info("P5.1. Renderizado artefactos  =" + getSubDelta());
                    }
                    efectosPostProcesamiento();
                    QLogger.info("P6. Postprocesamiento=" + getSubDelta());
                    if (renderReal && renderArtefactos) {
                        dibujarLuces(frameBuffer.getRendered().getGraphics());
                        QLogger.info("P7. Dibujo de luces=" + getSubDelta());
                    }

                    if (renderReal) {
                        mostrarEstadisticas(frameBuffer.getRendered().getGraphics());
                        QLogger.info("P8. Estadísticas=" + getSubDelta());
                    }

                    // Dibuja sobre la superficie
                    if (renderReal
                            && (this.getSuperficie() != null
                            && this.getSuperficie().getComponente() != null
                            && this.getSuperficie().getComponente().getGraphics() != null) //                            && this.getSuperficie().getComponente().isFocusOwner() // si tiene el foco
                            ) {
                        this.getSuperficie().getComponente().setImagen(frameBuffer.getRendered());
                        this.getSuperficie().getComponente().repaint();
                        QLogger.info("P10. Tiempo dibujado sobre superficie=" + getSubDelta());
                    }
                    QLogger.info("MR-->" + df.format(getFPS()) + " FPS");
                    QLogger.info("");
                    QLogger.info("");
                    QLogger.info("");
                }
            } else {
                mostrarSplash();
            }
        } catch (Exception e) {
            QLogger.info("Error en el render=" + nombre);
            e.printStackTrace();

        }
        poligonosDibujados = poligonosDibujadosTemp;
        tiempoPrevio = System.currentTimeMillis();

//        System.out.println("render: " + nombre + " renderizado t=" + tiempoPrevio);
        return tiempoPrevio;
    }

    /**
     * Realiza la limpieza de pantalla
     */
    private void limpiarPantalla() {
        frameBuffer.limpiarZBuffer(); //limpia el buffer de profundidad
        frameBuffer.llenarColor(colorFondo);//llena el buffer de color con el color indicado, dura 1ms
//        frameBuffer.limpiarPixelBuffer();//limpia del buffer de pixeles
    }

    /**
     * Renderiza artefactos usados por el editor como, seleccion de objeto,
     * gizmos y demas
     */
    private void renderArtefactosEditor() {
        //-------------------- RENDERIZA OBJETOS FUERDA DE LA ESCENA PROPIOS DEL EDITOR

        //  se dibuja la caja de seleccion
        if (!entidadesSeleccionadas.isEmpty()) {
            for (QEntidad entidadSeleccionado : entidadesSeleccionadas) {
                AABB tmp = null;
                for (QComponente comp : entidadSeleccionado.getComponentes()) {
                    if (comp instanceof QGeometria) {
                        if (tmp == null) {
                            tmp = new AABB(((QGeometria) comp).listaVertices[0].clone(), ((QGeometria) comp).listaVertices[0].clone());
                        }
                        for (QVertice vertice : ((QGeometria) comp).listaVertices) {
                            if (vertice.ubicacion.x < tmp.aabMinimo.ubicacion.x) {
                                tmp.aabMinimo.ubicacion.x = vertice.ubicacion.x;
                            }
                            if (vertice.ubicacion.y < tmp.aabMinimo.ubicacion.y) {
                                tmp.aabMinimo.ubicacion.y = vertice.ubicacion.y;
                            }
                            if (vertice.ubicacion.z < tmp.aabMinimo.ubicacion.z) {
                                tmp.aabMinimo.ubicacion.z = vertice.ubicacion.z;
                            }
                            if (vertice.ubicacion.x > tmp.aabMaximo.ubicacion.x) {
                                tmp.aabMaximo.ubicacion.x = vertice.ubicacion.x;
                            }
                            if (vertice.ubicacion.y > tmp.aabMaximo.ubicacion.y) {
                                tmp.aabMaximo.ubicacion.y = vertice.ubicacion.y;
                            }
                            if (vertice.ubicacion.z > tmp.aabMaximo.ubicacion.z) {
                                tmp.aabMaximo.ubicacion.z = vertice.ubicacion.z;
                            }
                        }
                    }
                }

                if (tmp != null) {
                    //dibujo las esquinas del objeto seleccionado
                    float dx = 0.2f * Math.abs(tmp.aabMinimo.ubicacion.x - tmp.aabMaximo.ubicacion.x);
                    float dy = 0.2f * Math.abs(tmp.aabMinimo.ubicacion.y - tmp.aabMaximo.ubicacion.y);
                    float dz = 0.2f * Math.abs(tmp.aabMinimo.ubicacion.z - tmp.aabMaximo.ubicacion.z);

                    TempVars t = TempVars.get();
                    try {

//                        ((QMaterialBas) polSeleccion.material).setColorDifusa(QColor.YELLOW);
                        //superiores
                        //1
                        t.vector4f1.setXYZW(tmp.aabMaximo.ubicacion.x, tmp.aabMaximo.ubicacion.y, tmp.aabMaximo.ubicacion.z, tmp.aabMaximo.ubicacion.w);

                        QVector4 ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z - dz, 1), entidadSeleccionado, camara);
                        QVector4 ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y - dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        QVector4 ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x - dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);

                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadSeleccionado, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);
                        //2
                        t.vector4f1.setXYZW(tmp.aabMinimo.ubicacion.x, tmp.aabMaximo.ubicacion.y, tmp.aabMaximo.ubicacion.z, tmp.aabMaximo.ubicacion.w);
                        ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z - dz, 1), entidadSeleccionado, camara);
                        ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y - dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x + dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadSeleccionado, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);
                        //3
                        t.vector4f1.setXYZW(tmp.aabMinimo.ubicacion.x, tmp.aabMaximo.ubicacion.y, tmp.aabMinimo.ubicacion.z, tmp.aabMaximo.ubicacion.w);
                        ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z + dz, 1), entidadSeleccionado, camara);
                        ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y - dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x + dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadSeleccionado, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);
                        //4
                        t.vector4f1.setXYZW(tmp.aabMaximo.ubicacion.x, tmp.aabMaximo.ubicacion.y, tmp.aabMinimo.ubicacion.z, tmp.aabMaximo.ubicacion.w);
                        ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z + dz, 1), entidadSeleccionado, camara);
                        ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y - dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x - dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadSeleccionado, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);

//                        ((QMaterialBas) polSeleccion.material).setColorDifusa(QColor.CYAN);
                        //inferiores
                        //1
                        t.vector4f1.setXYZW(tmp.aabMinimo.ubicacion.x, tmp.aabMinimo.ubicacion.y, tmp.aabMinimo.ubicacion.z, tmp.aabMinimo.ubicacion.w);
                        ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z + dz, 1), entidadSeleccionado, camara);
                        ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y + dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x + dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadSeleccionado, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);
                        //2
                        t.vector4f1.setXYZW(tmp.aabMaximo.ubicacion.x, tmp.aabMinimo.ubicacion.y, tmp.aabMinimo.ubicacion.z, tmp.aabMinimo.ubicacion.w);
                        ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z + dz, 1), entidadSeleccionado, camara);
                        ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y + dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x - dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadSeleccionado, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);
                        //3
                        t.vector4f1.setXYZW(tmp.aabMaximo.ubicacion.x, tmp.aabMinimo.ubicacion.y, tmp.aabMaximo.ubicacion.z, tmp.aabMinimo.ubicacion.w);
                        ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z - dz, 1), entidadSeleccionado, camara);
                        ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y + dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x - dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadSeleccionado, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);
                        //4
                        t.vector4f1.setXYZW(tmp.aabMinimo.ubicacion.x, tmp.aabMinimo.ubicacion.y, tmp.aabMaximo.ubicacion.z, tmp.aabMinimo.ubicacion.w);
                        ps2 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y, t.vector4f1.z - dz, 1), entidadSeleccionado, camara);
                        ps3 = QTransformar.transformarVector(new QVector4(t.vector4f1.x, t.vector4f1.y + dy, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        ps4 = QTransformar.transformarVector(new QVector4(t.vector4f1.x + dx, t.vector4f1.y, t.vector4f1.z, 1), entidadSeleccionado, camara);
                        t.vector4f1.set(QTransformar.transformarVector(t.vector4f1, entidadActiva, camara));
                        raster.raster(polSeleccion, t.vector4f1, ps2);
                        raster.raster(polSeleccion, t.vector4f1, ps3);
                        raster.raster(polSeleccion, t.vector4f1, ps4);
                    } finally {
                        t.release();
                    }
                }
            }
        }

        // ------------------------------------ ESQUELETOS  ------------------------------------
        //dibuja los esqueletos
        TempVars t = TempVars.get();
        try {
//            QMatriz4 matTransformacion;

            //---- LIMPIO EL ZBUFFER PARA SOBREESCRIBIR
            //limpio el zbuffer
//            frameBuffer.limpiarZBuffer();
            //los demas procesos de dibujo usan el bufferVertices, por lo tanto lo cambio temporalmente con el de los gizmos
//            bufferVerticesTMP = bufferVertices;
//            bufferVertices = bufferVertices2;
            //una entidad que contiene a los huesos para mostrarlos, aplicamos la misma transofmraicon de la entidad original
            QEntidad entidadTmp = null;
            for (QEntidad entidad : escena.getListaEntidades()) {
                for (QComponente componente : entidad.getComponentes()) {
                    if (componente instanceof QEsqueleto) {
                        QEsqueleto esqueleto = (QEsqueleto) componente;
                        if (esqueleto.isMostrar()) {
                            if (esqueleto.isSuperponer()) {
                                frameBuffer.limpiarZBuffer();
                            }
                            List<QEntidad> lista = new ArrayList<>();
                            //entidad falsa usada para corregir la transformacion de la entidad y mostrar los huesos acordes a esta transformacion
                            entidadTmp = new QEntidad();
//                            entidadTmp.transformacion = entidad.transformacion;
                            entidadTmp.getTransformacion().desdeMatrix(entidad.getMatrizTransformacion(QGlobal.tiempo));
//                            lista.add(entidadTmp);
                            for (QHueso hueso : esqueleto.getHuesos()) {
//                                lista.add(hueso);
                                // agrega al nodo invisible para usar la transformacion de la entidad y mostrar correctamente
                                // sin embargo da el erro que nousa la pose sin animacion
                                if (hueso.getPadre() == null) {
                                    QHueso hueson = hueso.clone();
                                    entidadTmp.agregarHijo(hueson);
                                    hueson.toLista(lista);
//                                    System.out.println("ARBOL DE HUESOS AL RENDERIZAR ");
//                                    QEntidad.imprimirArbolEntidad(hueson, "");
//                                    System.out.println("---------------------");
                                }
                            }
                            QTransformar.transformar(lista, camara, t.bufferVertices1);
                            for (QPrimitiva poligono : t.bufferVertices1.getPoligonosTransformados()) {
                                raster.raster(t.bufferVertices1, poligono, false, false);
                            }
                        }
                    }
                }
            }
        } finally {
            t.release();
        }

        // ------------------------------------ GIZMOS  ------------------------------------
        //seteo los gizmos
        if (entidadActiva != null && tipoGizmoActual != GIZMO_NINGUNO) {
            t = TempVars.get();
            try {
                //---- LIMPIO EL ZBUFFER PARA SOBREESCRIBIR
                //limpio el zbuffer
                frameBuffer.limpiarZBuffer();
                switch (tipoGizmoActual) {
                    case GIZMO_TRASLACION:
                    default:
                        gizActual = gizTraslacion;
                        break;
                    case GIZMO_ROTACION:
                        gizActual = gizRotacion;
                        break;
                    case GIZMO_ESCALA:
                        gizActual = gizEscala;
                        break;
                }
                gizActual.setEntidad(entidadActiva);
                gizActual.actualizarPosicionGizmo();

                //float gizmoSize = 0.08f;
                float gizmoSize = 0.04f;
                float scale = (float) (gizmoSize * (camara.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector().add(gizActual.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector().multiply(-1.0f)).length() / Math.tan(camara.getFOV() / 2.0f)));
                gizActual.escalar(scale, scale, scale);
                List<QEntidad> lista = new ArrayList<>();
                lista.add(gizActual);
                for (QEntidad hijo : gizActual.getHijos()) {
                    lista.add(hijo);
                }

                //los demas procesos de dibujo usan el bufferVertices, por lo tanto lo cambio temporalmente con el de los gizmos
                QTransformar.transformar(lista, camara, t.bufferVertices1);
                for (QPrimitiva poligono : t.bufferVertices1.getPoligonosTransformados()) {
                    raster.raster(t.bufferVertices1, poligono, false, false);
                }
            } finally {
                t.release();
            }
        } else {
            gizActual = null;
        }

        // ------------------------------------ EJES  ------------------------------------
        if (opciones.dibujarLuces) {
            frameBuffer.limpiarZBuffer();
            t = TempVars.get();
            try {
                List<QEntidad> lista = new ArrayList<>();
                lista.add(this.entidadOrigen);
                QTransformar.transformar(lista, camara, t.bufferVertices1);
                for (QPrimitiva poligono : t.bufferVertices1.getPoligonosTransformados()) {
                    raster.raster(t.bufferVertices1, poligono, false, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                t.release();
            }
        }
    }

    /**
     * Realiza el proceso de renderizado
     *
     * @throws Exception
     */
    private void render() throws Exception {
        QLogger.info("");
        QLogger.info("[Renderizador]");
        listaCarasTransparente.clear();
        poligonosDibujadosTemp = 0;
        TempVars t = TempVars.get();
        QShaderComponente qshader = null;
        try {
            //La Matriz de vista es la inversa de la matriz de la camara.
            // Esto es porque la camara siempre estara en el centro y movemos el mundo
            // en direccion contraria a la camara.
//            QMatriz4 matrizVista = camara.getMatrizTransformacion(QGlobal.tiempo).invert();
            // transformo todas las entidades para poder ordenar correctamente las caras transparentes de diferentes objetos
            QTransformar.transformar(escena.getListaEntidades(), camara, t.bufferVertices1);

            //--------------------------------------------------------------------------------------
            //                  CARAS SOLIDAS NO TRANSPARENTES
            //--------------------------------------------------------------------------------------
            setShader(defaultShader);

            for (QPrimitiva poligono : t.bufferVertices1.getPoligonosTransformados()) {
                // salta las camaras si no esta activo el dibujo de las camaras
                if (poligono.geometria.entidad instanceof QCamara && !opciones.dibujarLuces) {
                    continue;
                }
                //salta el dibujo de la camara que esta usando el render
                if (poligono.geometria.entidad instanceof QCamara && poligono.geometria.entidad.equals(this.camara)) {
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

                    // busca un shader personalizado   
                    qshader = QUtilComponentes.getShader(poligono.geometria.entidad);
                    if (qshader != null && qshader.getShader() != null) {
                        setShader(qshader.getShader());
                    } else {
                        setShader(defaultShader);
                    }
                    getShader().setRender(this);
                    poligonosDibujadosTemp++;
                    raster.raster(t.bufferVertices1, poligono, opciones.tipoVista == QOpcionesRenderer.VISTA_WIRE || poligono.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE, false);
                } else {
                    if (poligono instanceof QPoligono) {
                        listaCarasTransparente.add((QPoligono) poligono);
                    }
                }
            }
            QLogger.info("  Render-- Rasterización poligonos=" + getSubDelta());

            //--------------------------------------------------------------------------------------
            //                  TRANSPARENTES
            //--------------------------------------------------------------------------------------
            //ordeno las caras transparentes 
            if (!listaCarasTransparente.isEmpty()) {
                if (opciones.zSort) {
                    Collections.sort(listaCarasTransparente);
                }
                for (QPoligono poligono : listaCarasTransparente) {
                    // busca un shader personalizado   
                    qshader = QUtilComponentes.getShader(poligono.geometria.entidad);
                    if (qshader != null && qshader.getShader() != null) {
                        setShader(qshader.getShader());
                    } else {
                        setShader(defaultShader);
                    }
                    getShader().setRender(this);
                    poligonosDibujadosTemp++;
                    raster.raster(t.bufferVertices1, poligono, opciones.tipoVista == QOpcionesRenderer.VISTA_WIRE || poligono.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE, false);
                }
                QLogger.info("  Render-- Rasterización poligonos transparentes (" + listaCarasTransparente.size() + ") =" + getSubDelta());
            }
        } catch (Exception e) {

        } finally {
            t.release();
        }
    }

    /**
     * Ejecuta los efectosPotProcesamiento
     */
    private void efectosPostProcesamiento() {
        if (efectosPostProceso != null) {
            try {
                ImageIO.write(frameBuffer.getRendered(), "png", new File("/home/alberto/testSalidaAntes_" + sf.format(new Date()) + ".png"));
            } catch (IOException ex) {

            }
            frameBuffer = efectosPostProceso.ejecutar(frameBuffer);
            frameBuffer.actualizarTextura();
            try {
                ImageIO.write(frameBuffer.getRendered(), "png", new File("/home/alberto/testSalidaDespues_" + sf.format(new Date()) + ".png"));
            } catch (IOException ex) {

            }
//        } else {
//            frameBufferFinal = frameBuffer;
        }
    }

    /**
     * Realiza el dibujo de las luces para la vista de trabajo.
     *
     * @param g
     */
    private void dibujarLuces(Graphics g) {
        // Dibuja el centro del objeto seleccionado
//        if (entidadActiva != null
//                && (selectX1 < frameBuffer.getAncho() || selectX2 > 0 || selectY1 < frameBuffer.getAlto() || selectY2 > 0)) {
//            g.setColor(Color.orange);
////            g.drawRect(selectX1, selectY1, selectX2 - selectX1, selectY2 - selectY1);
//            g.fillOval((selectX1 + selectX2) / 2 - 5, (selectY1 + selectY2) / 2 - 5, 10, 10);
//        }

        if (opciones.dibujarLuces) {
            //dibuja las luces
            QVector2 puntoLuz = new QVector2();
            for (QEntidad entidad : escena.getListaEntidades()) {
                if (entidad.isRenderizar()) {
                    for (QComponente componente : entidad.getComponentes()) {
                        if (componente instanceof QLuz) {
                            QLuz luz = (QLuz) componente;
                            QMatriz4 matVistaModelo = camara.getMatrizTransformacion(QGlobal.tiempo).invert().mult(entidad.getMatrizTransformacion(QGlobal.tiempo));
                            verticeLuz = QVertexShader.transformarVertice(QVertice.ZERO, matVistaModelo);
                            if (verticeLuz.ubicacion.z > 0) {
                                continue;
                            }
                            camara.getCoordenadasPantalla(puntoLuz, verticeLuz.ubicacion, frameBuffer.getAncho(), frameBuffer.getAlto());
                            g.setColor(luz.color.getColor());
                            if (entidadActiva == entidad) {
                                g.setColor(new Color(255, 128, 0));
                            }
                            g.drawOval((int) puntoLuz.x - lightOnScreenSize / 2, (int) puntoLuz.y - lightOnScreenSize / 2, lightOnScreenSize, lightOnScreenSize);
                            //dibuja la dirección de la luz direccional y cónica
                            if (luz instanceof QLuzDireccional) {
                                tempVector.copyXYZ(((QLuzDireccional) luz).getDirection());
                                tempVector = QTransformar.transformarVector(tempVector, entidad, camara);
                                QVector2 secondPoint = new QVector2();
                                camara.getCoordenadasPantalla(secondPoint, new QVector4(tempVector, 0), frameBuffer.getAncho(), frameBuffer.getAlto());
                                g.drawLine((int) puntoLuz.x, (int) puntoLuz.y, (int) secondPoint.x, (int) secondPoint.y);
                            } else if (luz instanceof QLuzSpot) {
                                tempVector.copyXYZ(((QLuzSpot) luz).getDirection());
                                tempVector = QTransformar.transformarVector(tempVector, entidad, camara);
                                QVector2 secondPoint = new QVector2();
                                camara.getCoordenadasPantalla(secondPoint, new QVector4(tempVector, 0), frameBuffer.getAncho(), frameBuffer.getAlto());
                                g.drawLine((int) puntoLuz.x, (int) puntoLuz.y, (int) secondPoint.x, (int) secondPoint.y);
                                // otro circulo alrededo el segundo punto
                                g.drawOval((int) secondPoint.x - lightOnScreenSize, (int) secondPoint.y - lightOnScreenSize / 2, lightOnScreenSize * 2, lightOnScreenSize);
                            }
                            //dibuja el radio de la luz
                            tempVector.set(verticeLuz.ubicacion.getVector3());
                            tempVector.add(new QVector3(0, luz.radio, 0));//agrego un vector hacia arriba con tamanio del radio
                            QVector2 secondPoint = new QVector2();
                            camara.getCoordenadasPantalla(secondPoint, new QVector4(tempVector, 1), frameBuffer.getAncho(), frameBuffer.getAlto());
                            g.setColor(Color.DARK_GRAY);
                            float difx = Math.abs(secondPoint.x - puntoLuz.x);
                            float dify = Math.abs(secondPoint.y - puntoLuz.y);
                            int largo = (int) Math.sqrt(difx * difx + dify * dify);
                            g.drawOval((int) puntoLuz.x - largo, (int) puntoLuz.y - largo, largo * 2, largo * 2);
                        }
                    }
                }
            }

            //--------------------------------------------------------------------------------------------
            //              DIBUJA LOS EJES
            //--------------------------------------------------------------------------------------------
//            g.setFont(new Font("Arial", Font.PLAIN, 10));
//            //obtiene el punto central
//            tempVector.setXYZ(0, 0, 0);
//            tempVector.normalize();
//            tempVector = camara.getMatrizTransformacion(QGlobal.tiempo).invert().mult(tempVector);
//            QVector2 puntoCentral = new QVector2();
//            QVector2 puntoX = new QVector2();
//            QVector2 puntoY = new QVector2();
//            QVector2 puntoZ = new QVector2();
//            camara.getCoordenadasPantalla(puntoCentral, new QVector4(tempVector, 1));
//            //eje X -------------------------------------
//            tempVector.setXYZ(1, 0, 0);
//            tempVector.normalize();
//            // la trasnformacioń es solo de la camara
//            tempVector = camara.getMatrizTransformacion(QGlobal.tiempo).invert().mult(tempVector);
//            camara.getCoordenadasPantalla(puntoX, new QVector4(tempVector, 1));
//            g.setColor(Color.red);
//            g.drawLine((int) puntoCentral.x, (int) puntoCentral.y, (int) puntoX.x, (int) puntoX.y);
//            g.drawString("X", (int) puntoX.x + 5, (int) puntoX.y + 5);
//            //eje Y -----------------------------------------
//            tempVector.setXYZ(0, 1, 0);
//            tempVector.normalize();
//            // la trasnformacioń es solo de la camara
//            tempVector = camara.getMatrizTransformacion(QGlobal.tiempo).invert().mult(tempVector);
//            camara.getCoordenadasPantalla(puntoY, new QVector4(tempVector, 1));
//            g.setColor(Color.yellow);
//            g.drawLine((int) puntoCentral.x, (int) puntoCentral.y, (int) puntoY.x, (int) puntoY.y);
//            g.drawString("Y", (int) puntoY.x + 5, (int) puntoY.y + 5);
//            //Eje Z ------------------------------------------------------------------
//            tempVector.setXYZ(0, 0, 1);
//            tempVector.normalize();
//            // la trasnformacioń es solo de la camara
//            tempVector = camara.getMatrizTransformacion(QGlobal.tiempo).invert().mult(tempVector);
//            camara.getCoordenadasPantalla(puntoZ, new QVector4(tempVector, 1));
//            g.setColor(Color.blue);
//            g.drawLine((int) puntoCentral.x, (int) puntoCentral.y, (int) puntoZ.x, (int) puntoZ.y);
//            g.drawString("Z", (int) puntoZ.x + 5, (int) puntoZ.y + 5);
//            //-------------------------------------------
//            //dibuja los mismos ejes fijos en la esquina inferior izquierda
//            QVector2 puntoFijo = new QVector2(50, frameBuffer.getAlto() - 50);
//            g.setColor(Color.red);
//            g.drawLine((int) puntoFijo.x, (int) puntoFijo.y, (int) (puntoFijo.x - puntoCentral.x + puntoX.x), (int) (puntoFijo.y - puntoCentral.y + puntoX.y));
//            g.drawString("X", (int) (puntoFijo.x - puntoCentral.x + puntoX.x + 5), (int) (puntoFijo.y - puntoCentral.y + puntoX.y + 5));
//            g.setColor(Color.yellow);
//            g.drawLine((int) puntoFijo.x, (int) puntoFijo.y, (int) (puntoFijo.x - puntoCentral.x + puntoY.x), (int) (puntoFijo.y - puntoCentral.y + puntoY.y));
//            g.drawString("Y", (int) (puntoFijo.x - puntoCentral.x + puntoY.x + 5), (int) (puntoFijo.y - puntoCentral.y + puntoY.y + 5));
//            g.setColor(Color.blue);
//            g.drawLine((int) puntoFijo.x, (int) puntoFijo.y, (int) (puntoFijo.x - puntoCentral.x + puntoZ.x), (int) (puntoFijo.y - puntoCentral.y + puntoZ.y));
//            g.drawString("Z", (int) (puntoFijo.x - puntoCentral.x + puntoZ.x + 5), (int) (puntoFijo.y - puntoCentral.y + puntoZ.y + 5));
        }
        //dibuja las normales de las caras
//        if (opciones.showNormal) {
//            g.setColor(Color.CYAN);
//            for (QVertice vertex : bufferVertices.getVerticesTransformados()) {
//                if (vertex.ubicacion.z > 0) {
//                    continue;
//                }
//                QVector2 vertexPoint = new QVector2();
//                camara.getCoordenadasPantalla(vertexPoint, vertex);
//                QVertice tail = vertex.clone();
//                tail.ubicacion.x += vertex.normal.x / 3;
//                tail.ubicacion.y += vertex.normal.y / 3;
//                tail.ubicacion.z += vertex.normal.z / 3;
//                QVector2 tailPoint = new QVector2();
//                camara.getCoordenadasPantalla(tailPoint, tail);
//                g.drawLine((int) vertexPoint.x, (int) vertexPoint.y, (int) tailPoint.x, (int) tailPoint.y);
//            }
//        }
    }

    /**
     * Calcula las sombras, Renderiza desde el punto de vista de la luz
     */
    private void calcularSombras() {
        if (!luces.isEmpty() && (opciones.sombras && opciones.material)) {
            for (QLuz luz : luces) {
                if (luz != null && luz.entidad.isRenderizar() && luz.isEnable()) {
                    QProcesadorSombra proc = procesadorSombras.get(luz.entidad.getNombre());
                    if (proc != null) {
                        if (forzarActualizacionMapaSombras) {
                            proc.setActualizar(true);
                        }
                        proc.generarSombras();
                    }
                }
            }
        }
        forzarActualizacionMapaSombras = false;
    }

    /**
     * Actualiza la información de las luces y de las sombras, procesadores de
     * sombras
     */
    private void actualizarLucesSombras() {
        //cuenta las luces que si se pueden renderizar
        try {
            luces.clear();
            for (QEntidad entidad : escena.getListaEntidades()) {
                if (entidad.isRenderizar()) {
                    for (QComponente componente : entidad.getComponentes()) {
                        if (componente instanceof QLuz) {
                            QLuz luz = ((QLuz) componente).clone();

                            if (!luces.contains(luz)) {
                                luces.add(luz);
                            }

                            //la direccion de la luz en el sistmea de la camara (Se usa para el calculo de la iluminacion)
                            QVector3 direccionLuz = QVector3.zero;
                            //la direccion de la luz en el sistema de la entidad (no se aplica la transformacion de la camara) (Se usa en el calculo de la sombra)
                            QVector3 direccionLuzMapaSombra = QVector3.zero;

                            if (luz instanceof QLuzDireccional) {
                                direccionLuz = ((QLuzDireccional) componente).getDirection();
                                direccionLuzMapaSombra = ((QLuzDireccional) componente).getDirection();
                            } else if (luz instanceof QLuzSpot) {
                                direccionLuz = ((QLuzSpot) componente).getDirection();
                                direccionLuzMapaSombra = ((QLuzSpot) componente).getDirection();
                            }

                            // Al igual que con el buffer de vertices se aplica la transformacion, a esta copia de la luz
                            // se le aplica la transformacion para luego calcular la ilumnicacion.
                            // La ilumnicacion se calcula usando lso vertices ya transformados
                            luz.entidad.getTransformacion().getTraslacion().setXYZ(0, 0, 0);
                            luz.entidad.getTransformacion().setTraslacion(QTransformar.transformarVector(QVector3.zero, entidad, camara));
//                            actualiza la dirección de la luz

                            direccionLuz = QTransformar.transformarVectorNormal(direccionLuz, entidad, camara);
                            direccionLuzMapaSombra = QTransformar.transformarVectorNormal(direccionLuzMapaSombra, entidad.getMatrizTransformacion(QGlobal.tiempo));

                            if (luz instanceof QLuzDireccional) {
                                ((QLuzDireccional) luz).setDirection(direccionLuz);
                            } else if (luz instanceof QLuzSpot) {
                                ((QLuzSpot) luz).setDirection(direccionLuz);
                            }

                            //creo procesadores de sombras, en caso de no existir en el mapa 
                            if (opciones.sombras && opciones.material) {
                                if (!procesadorSombras.containsKey(entidad.getNombre())) {
                                    //si la luz debe proyectar sombra
                                    if (luz.isProyectarSombras()) {
                                        QProcesadorSombra nuevo = null;
                                        if (luz instanceof QLuzDireccional) {
                                            if (QGlobal.SOMBRAS_DIRECCIONALES_CASCADA) {
                                                nuevo = new QSombraDireccionalCascada(QGlobal.SOMBRAS_CASCADAS_TAMANIO, escena, (QLuzDireccional) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                            } else {
                                                nuevo = new QSombraDireccional(escena, (QLuzDireccional) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                            }
                                            QLogger.info("Creado pocesador de sombra Direccional con clave " + entidad.getNombre());
                                        } else if (luz instanceof QLuzPuntual) {
                                            nuevo = new QSombraOmnidireccional(escena, (QLuzPuntual) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                            QLogger.info("Creado pocesador de sombra Omnidireccional con clave " + entidad.getNombre());
                                        } else if (luz instanceof QLuzSpot) {
                                            nuevo = new QSombraCono(escena, (QLuzSpot) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                            QLogger.info("Creado pocesador de sombra Cónica con clave " + entidad.getNombre());
                                        }
                                        if (nuevo != null) {
                                            nuevo.setDinamico(luz.isSombraDinamica());
                                            procesadorSombras.put(entidad.getNombre(), nuevo);
                                        }
                                    }
                                } else {
                                    // si ya existe un procesador de sombras
                                    //en caso que tenga procesador sombra pero luego se desactivo la proyección, la elimino
                                    if (!luz.isProyectarSombras()) {
                                        procesadorSombras.remove(entidad.getNombre());
                                    } else {
//                                        si el direccional actualizo la direccion de la luz del procesador de sombra de acuerdo a la entidad
                                        if (luz instanceof QLuzDireccional) {
                                            QSombraDireccional proc = (QSombraDireccional) procesadorSombras.get(entidad.getNombre());
                                            proc.actualizarDireccion(direccionLuzMapaSombra);
                                        } else if (luz instanceof QLuzSpot) {
                                            QSombraCono proc = (QSombraCono) procesadorSombras.get(entidad.getNombre());
                                            proc.actualizarDireccion(direccionLuzMapaSombra);
                                        }
                                    }
                                }
                            } else {
                                //elimino los procesadores de sombras
                                if (!procesadorSombras.isEmpty()) {
                                    procesadorSombras.clear();
                                    System.gc();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

//    @Override
//    public void dibujarPixel(int x, int y) {
//        frameBuffer.setQColor(x, y, shader.colorearPixel(frameBuffer.getPixel(y, x), x, y));
//    }
    @Override
    public void iniciar() {

    }

    @Override
    public void detener() {

    }

    @Override
    public void cambiarRaster(int opcion) {
        switch (opcion) {
            case 1:
                raster = new QRaster1(this);
                break;
            case 2:
                raster = new QRaster2(this);
                break;
        }
    }

    /**
     * Cambia el shader
     *
     * @param opcion 0. QSimpleShader, 1 QFlatShader, 2 QPhongShader, 3
     * QTexturaShader, 4 QIluminadoShader, 5 QShadowShader, 6 QFullShader
     */
    @Override
    public void cambiarShader(int opcion) {
        super.cambiarShader(opcion); //To change body of generated methods, choose Tools | Templates.

        switch (opcion) {
            case 0:
                defaultShader = new QSimpleShader(this);
                break;
            case 1:
                defaultShader = new QFlatShader(this);
                break;
            case 2:
                defaultShader = new QPhongShader(this);
                break;
            case 3:
                defaultShader = new QTexturaShader(this);
                break;
            case 4:
                defaultShader = new QIluminadoShader(this);
                break;
            case 5:
                defaultShader = new QShadowShader(this);
                break;
            case 6:
                defaultShader = new QFullShader(this);
                break;
        }
    }

    public QShader getShader() {
        return shader;
    }

    public void setShader(QShader shader) {
        this.shader = shader;
    }

    public QShader getDefaultShader() {
        return defaultShader;
    }

    public void setDefaultShader(QShader defaultShader) {
        this.defaultShader = defaultShader;
    }

    public AbstractRaster getRaster() {
        return raster;
    }

    public void setRaster(AbstractRaster raster) {
        this.raster = raster;
    }

}
