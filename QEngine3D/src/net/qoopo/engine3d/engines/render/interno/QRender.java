package net.qoopo.engine3d.engines.render.interno;

import java.awt.Color;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy.QPBRShaderProxy;
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

    public QRender(QEscena escena, Superficie superficie, int ancho, int alto) {
        super(escena, superficie, ancho, alto);
        defaultShader = new QFullShader(this);
        raster = new QRaster1(this);
    }

    public QRender(QEscena escena, String nombre, Superficie superficie, int ancho, int alto) {
        super(escena, nombre, superficie, ancho, alto);
        defaultShader = new QFullShader(this);
        raster = new QRaster1(this);
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
                    setColorFondo(escena.getColorAmbiente());//pintamos el fondo con el color ambiente
                    limpiar();
                    QLogger.info("P1. Limpiar pantalla=" + getSubDelta());
                    actualizarLucesSombras();
                    QLogger.info("P2. Actualizar luces y sombras=" + getSubDelta());
                    QLogger.info("P5. ----Renderizado-----");
                    render();
                    if (renderReal) {
                        mostrarEstadisticas(frameBuffer.getRendered().getGraphics());
                        QLogger.info("P8. Estadísticas=" + getSubDelta());
                    }

                    // Dibuja sobre la superficie
                    if (renderReal
                            && (this.getSuperficie() != null
                            && this.getSuperficie().getComponente() != null
                            && this.getSuperficie().getComponente().getGraphics() != null)) {
                        this.getSuperficie().getComponente().setImagen(frameBuffer.getRendered());
                        this.getSuperficie().getComponente().repaint();
                        QLogger.info("P10. Tiempo dibujado sobre superficie=" + getSubDelta());
                    }
                    QLogger.info("MR-->" + DF.format(getFPS()) + " FPS");
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
        return tiempoPrevio;
    }

    /**
     * Realiza la limpieza de pantalla
     */
    protected void limpiar() {
        frameBuffer.limpiarZBuffer(); //limpia el buffer de profundidad
        frameBuffer.llenarColor(colorFondo);//llena el buffer de color con el color indicado, dura 1ms
    }

    /**
     * Renderiza artefactos usados por el editor como, seleccion de objeto,
     * gizmos y demas
     */
    private void renderArtefactosEditor() {
        //-------------------- RENDERIZA OBJETOS FUERA DE LA ESCENA PROPIOS DEL EDITOR

        // ------------------------------------ GRID  ------------------------------------
        if (opciones.isDibujarGrid()) {
            TempVars t = TempVars.get();
            try {
                int secciones = 50;
                float espacio = 1.0f;
                float maxCoordenada = espacio * secciones / 2.0f;
                // primero en el eje de  X
                for (int i = 0; i <= secciones; i++) {
                    t.vector4f1.setXYZW((-secciones / 2 + i) * espacio, 0, maxCoordenada, 1);
                    t.vector4f2.setXYZW((-secciones / 2 + i) * espacio, 0, -maxCoordenada, 1);
                    QVector4 ps1 = QTransformar.transformarVector(t.vector4f1, null, camara);
                    QVector4 ps2 = QTransformar.transformarVector(t.vector4f2, null, camara);
                    raster.raster(polGrid, ps1, ps2);
                }
                // el otro eje Z
                for (int i = 0; i <= secciones; i++) {
                    t.vector4f1.setXYZW(maxCoordenada, 0, (-secciones / 2 + i) * espacio, 1);
                    t.vector4f2.setXYZW(-maxCoordenada, 0, (-secciones / 2 + i) * espacio, 1);
                    QVector4 ps1 = QTransformar.transformarVector(t.vector4f1, null, camara);
                    QVector4 ps2 = QTransformar.transformarVector(t.vector4f2, null, camara);
                    raster.raster(polGrid, ps1, ps2);
                }
                // el eje de  X
                {
                    t.vector4f1.setXYZW(maxCoordenada, 0, 0.001f, 1);
                    t.vector4f2.setXYZW(-maxCoordenada, 0, 0.001f, 1);
                    QVector4 ps1 = QTransformar.transformarVector(t.vector4f1, null, camara);
                    QVector4 ps2 = QTransformar.transformarVector(t.vector4f2, null, camara);
                    raster.raster(polEjeX, ps1, ps2);
                }
                // el eje de  Z
                {
                    t.vector4f1.setXYZW(0, 0.001f, maxCoordenada, 1);
                    t.vector4f2.setXYZW(0, 0.001f, -maxCoordenada, 1);
                    QVector4 ps1 = QTransformar.transformarVector(t.vector4f1, null, camara);
                    QVector4 ps2 = QTransformar.transformarVector(t.vector4f2, null, camara);
                    raster.raster(polEjeZ, ps1, ps2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                t.release();
            }
        }

        // ------------------------------------ CAJA DE SELECCION  ------------------------------------
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

                float gizmoSize = 0.06f;
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
        if (opciones.isDibujarGrid()) {
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
    public void render() throws Exception {
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
                if (poligono.geometria.entidad instanceof QCamara && !opciones.isDibujarLuces()) {
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
                    raster.raster(t.bufferVertices1, poligono, opciones.getTipoVista() == QOpcionesRenderer.VISTA_WIRE || poligono.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE, false);
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
                if (opciones.iszSort()) {
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
                    raster.raster(t.bufferVertices1, poligono, opciones.getTipoVista() == QOpcionesRenderer.VISTA_WIRE || poligono.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE, false);
                }
                QLogger.info("  Render-- Rasterización poligonos transparentes (" + listaCarasTransparente.size() + ") =" + getSubDelta());
            }
        } catch (Exception e) {

        } finally {
            t.release();
        }
        if (renderReal && opciones.isRenderArtefactos()) {
            renderArtefactosEditor();
            QLogger.info("P5.1. Renderizado artefactos  =" + getSubDelta());
        }
        efectosPostProcesamiento();
        QLogger.info("P6. Postprocesamiento=" + getSubDelta());
        if (renderReal && opciones.isRenderArtefactos()) {
            dibujarLuces(frameBuffer.getRendered().getGraphics());
            QLogger.info("P7. Dibujo de luces=" + getSubDelta());
        }
    }

    /**
     * Ejecuta los efectosPotProcesamiento
     */
    private void efectosPostProcesamiento() {
        if (efectosPostProceso != null) {
//            try {
//                ImageIO.write(frameBuffer.getRendered(), "png", new File("/home/alberto/testSalidaAntes_" + sf.format(new Date()) + ".png"));
//            } catch (IOException ex) {
//
//            }
            frameBuffer.setBufferColor(efectosPostProceso.ejecutar(frameBuffer.getBufferColor()));
//            frameBuffer.actualizarTextura();
//            try {
//                ImageIO.write(frameBuffer.getRendered(), "png", new File("/home/alberto/testSalidaDespues_" + sf.format(new Date()) + ".png"));
//            } catch (IOException ex) {
//
//            }
        }
    }

    /**
     * Realiza el dibujo de las luces para la vista de trabajo.
     *
     * @param g
     */
    private void dibujarLuces(Graphics g) {
        if (opciones.isDibujarLuces()) {
            //dibuja las luces
            TempVars t = TempVars.get();

            QVector2 puntoLuz = t.vector2f1;
            for (QEntidad entidad : escena.getListaEntidades()) {
                if (entidad.isRenderizar()) {
                    for (QComponente componente : entidad.getComponentes()) {
                        if (componente instanceof QLuz) {
                            QLuz luz = (QLuz) componente;
                            QMatriz4 matVistaModelo = camara.getMatrizTransformacion(QGlobal.tiempo).invert().mult(entidad.getMatrizTransformacion(QGlobal.tiempo));
                            t.vertice1.set(QVertexShader.transformarVertice(QVertice.ZERO, matVistaModelo));
                            if (t.vertice1.ubicacion.z > 0) {
                                continue;
                            }
                            camara.getCoordenadasPantalla(puntoLuz, t.vertice1.ubicacion, frameBuffer.getAncho(), frameBuffer.getAlto());
                            g.setColor(luz.color.getColor());
                            if (entidadActiva == entidad) {
                                g.setColor(new Color(255, 128, 0));
                            }
                            g.drawOval((int) puntoLuz.x - lightOnScreenSize / 2, (int) puntoLuz.y - lightOnScreenSize / 2, lightOnScreenSize, lightOnScreenSize);
                            //dibuja la dirección de la luz direccional y cónica
                            if (luz instanceof QLuzDireccional) {
                                t.vector3f1.set(((QLuzDireccional) luz).getDirection());
                                t.vector3f1.set(QTransformar.transformarVector(t.vector3f1, entidad, camara));
                                QVector2 secondPoint = new QVector2();
                                camara.getCoordenadasPantalla(secondPoint, new QVector4(t.vector3f1, 0), frameBuffer.getAncho(), frameBuffer.getAlto());
                                g.drawLine((int) puntoLuz.x, (int) puntoLuz.y, (int) secondPoint.x, (int) secondPoint.y);
                            } else if (luz instanceof QLuzSpot) {
                                t.vector3f1.set(((QLuzSpot) luz).getDirection());
                                t.vector3f1.set(QTransformar.transformarVector(t.vector3f1, entidad, camara));
                                QVector2 secondPoint = new QVector2();
                                camara.getCoordenadasPantalla(secondPoint, new QVector4(t.vector3f1, 0), frameBuffer.getAncho(), frameBuffer.getAlto());
                                g.drawLine((int) puntoLuz.x, (int) puntoLuz.y, (int) secondPoint.x, (int) secondPoint.y);
                                // otro circulo alrededo el segundo punto
                                g.drawOval((int) secondPoint.x - lightOnScreenSize, (int) secondPoint.y - lightOnScreenSize / 2, lightOnScreenSize * 2, lightOnScreenSize);
                            }
                            //dibuja el radio de la luz
                            t.vector3f1.set(t.vertice1.ubicacion.getVector3());
                            t.vector3f1.add(new QVector3(0, luz.radio, 0));//agrego un vector hacia arriba con tamanio del radio
                            QVector2 secondPoint = new QVector2();
                            camara.getCoordenadasPantalla(secondPoint, new QVector4(t.vector3f1, 1), frameBuffer.getAncho(), frameBuffer.getAlto());
//                            g.setColor(Color.DARK_GRAY);
                            g.setColor(luz.color.getColor());
                            float difx = Math.abs(secondPoint.x - puntoLuz.x);
                            float dify = Math.abs(secondPoint.y - puntoLuz.y);
                            int largo = (int) Math.sqrt(difx * difx + dify * dify);
                            g.drawOval((int) puntoLuz.x - largo, (int) puntoLuz.y - largo, largo * 2, largo * 2);
                        }
                    }
                }
            }
            t.release();

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
                            QLuz luz = ((QLuz) componente);
                            if (!luces.contains(luz)) {
                                luces.add(luz);
                            }

                            //la direccion de la luz en el espacio de la camara (Se usa para el calculo de la iluminacion)
                            QVector3 direccionLuzEspacioCamara = QVector3.zero;
                            //la direccion de la luz en el espacio mundial(no se aplica la transformacion de la camara) (Se usa en el calculo de la sombra)
                            QVector3 direccionLuzMapaSombra = QVector3.zero;

                            if (luz instanceof QLuzDireccional) {
                                direccionLuzEspacioCamara = ((QLuzDireccional) componente).getDirection();
                                direccionLuzMapaSombra = ((QLuzDireccional) componente).getDirection();
                            } else if (luz instanceof QLuzSpot) {
                                direccionLuzEspacioCamara = ((QLuzSpot) componente).getDirection();
                                direccionLuzMapaSombra = ((QLuzSpot) componente).getDirection();
                            }

                            // Al igual que con el buffer de vertices se aplica la transformacion, a esta copia de la luz
                            // se le aplica la transformacion para luego calcular la ilumnicacion.
                            // La ilumnicacion se calcula usando lso vertices ya transformados
//                            luz.entidad.getTransformacion().getTraslacion().set(QTransformar.transformarVector(QVector3.zero, entidad, camara));
//                            actualiza la dirección de la luz
                            direccionLuzEspacioCamara = QTransformar.transformarVectorNormal(direccionLuzEspacioCamara, entidad, camara);
                            direccionLuzMapaSombra = QTransformar.transformarVectorNormal(direccionLuzMapaSombra, entidad.getMatrizTransformacion(QGlobal.tiempo));

                            if (luz instanceof QLuzDireccional) {
                                ((QLuzDireccional) luz).setDirectionTransformada(direccionLuzEspacioCamara);
                            } else if (luz instanceof QLuzSpot) {
                                ((QLuzSpot) luz).setDirectionTransformada(direccionLuzEspacioCamara);
                            }

                            //Actualiza procesadores de sombras
                            if (opciones.isSombras() && opciones.isMaterial() && luz.isProyectarSombras()) {
                                //si no existe crea uno nuevo
                                QProcesadorSombra proc = null;
                                if (luz.getSombras() == null) {
                                    if (luz instanceof QLuzDireccional) {
                                        if (QGlobal.SOMBRAS_DIRECCIONALES_CASCADA) {
                                            proc = new QSombraDireccionalCascada(QGlobal.SOMBRAS_CASCADAS_TAMANIO, escena, (QLuzDireccional) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                        } else {
                                            proc = new QSombraDireccional(escena, (QLuzDireccional) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                        }
                                        QLogger.info("Creado pocesador de sombra Direccional con clave " + entidad.getNombre());
                                    } else if (luz instanceof QLuzPuntual) {
                                        proc = new QSombraOmnidireccional(escena, (QLuzPuntual) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                        QLogger.info("Creado pocesador de sombra Omnidireccional con clave " + entidad.getNombre());
                                    } else if (luz instanceof QLuzSpot) {
                                        proc = new QSombraCono(escena, (QLuzSpot) componente, camara, luz.getResolucionMapaSombra(), luz.getResolucionMapaSombra());
                                        QLogger.info("Creado pocesador de sombra Cónica con clave " + entidad.getNombre());
                                    }
                                    if (proc != null) {
                                        proc.setDinamico(luz.isSombraDinamica());
                                        luz.setSombras(proc);
                                    }
                                } else {
                                    // si ya existe un procesador de sombras
                                    // si el direccional actualizo la direccion de la luz del procesador de sombra de acuerdo a la entidad
                                    if (luz instanceof QLuzDireccional) {
                                        proc = (QSombraDireccional) luz.getSombras();
                                        ((QSombraDireccional) proc).actualizarDireccion(direccionLuzMapaSombra);
                                        if (forzarActualizacionMapaSombras) {
                                            proc.setActualizar(true);
                                        }
                                        proc.generarSombras();
                                    } else if (luz instanceof QLuzSpot) {
                                        proc = (QSombraCono) luz.getSombras();
                                        ((QSombraCono) proc).actualizarDireccion(direccionLuzMapaSombra);
                                    }
                                }
                                if (proc != null) {
                                    if (forzarActualizacionMapaSombras) {
                                        proc.setActualizar(true);
                                    }
                                    proc.generarSombras();
                                }
                            } else {
                                luz.setSombras(null);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            forzarActualizacionMapaSombras = false;
        }
    }

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
     * QTexturaShader, 4 QIluminadoShader, 5 QShadowShader, 6 QFullShader, 7
     * QPBRShader
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
            case 7:
                defaultShader = new QPBRShaderProxy(this);
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
