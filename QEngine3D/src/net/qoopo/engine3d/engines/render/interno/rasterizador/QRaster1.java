/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.rasterizador;

import java.util.ArrayList;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QLinea;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.transformacion.QVerticesBuffer;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector2;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.engines.render.QOpcionesRenderer;
import net.qoopo.engine3d.engines.render.interno.QRender;
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;

/**
 * Realiza la rasterización de los polígonos
 *
 * @author alberto
 */
public class QRaster1 extends AbstractRaster {

    protected QRender render;

    protected QVector3 toCenter = new QVector3();
    protected ArrayList<QVertice> listaVerticesTmp = new ArrayList<>();
    protected QVertice[] vt = new QVertice[3]; //vertices transformados
    protected QVertice verticeTmp = new QVertice();
    protected float alfa;//factor de interpolación

    protected QVector2[] puntoXY = new QVector2[3]; // puntos proyectados
    protected int order[] = new int[3];
    protected int temp;

    protected float zDesde;
    protected float zHasta;
    protected float xDesde;
    protected float xHasta;
    protected float zActual;

    protected float tmpU, tmpV;
    protected float uDesde, vDesde;
    protected float uHasta, vHasta;

    protected int xDesdePantalla;
    protected int xHastaPantalla;
    protected int increment;

    protected final float INTERPOLATION_CLAMP = .0001f;
    protected QVertice verticeDesde = new QVertice();
    protected QVertice verticeHasta = new QVertice();
    protected QVertice verticeActual = new QVertice();
    protected QVector3 up = new QVector3();
    protected QVector3 right = new QVector3();
    protected QVector3 currentUp = new QVector3();
    protected QVector3 currentRight = new QVector3();
    protected float tempFloat, vYLength, vXLength, coefficient1, coefficient2;

    public QRaster1(QRender render) {
        this.render = render;
        for (int i = 0; i < 3; i++) {
            puntoXY[i] = new QVector2();
        }
    }

    /**
     *
     * @param primitiva
     * @param p1
     * @param p2
     *
     */
    @Override
    public void raster(QPrimitiva primitiva, QVector4 p1, QVector4 p2) {
        vt[0] = new QVertice(p1.x, p1.y, p1.z, p1.w);
        vt[1] = new QVertice(p2.x, p2.y, p2.z, p1.w);
        vt[2] = new QVertice(p2.x, p2.y, p2.z, p1.w);
        render.getCamara().getCoordenadasPantalla(puntoXY[0], p1, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());
        render.getCamara().getCoordenadasPantalla(puntoXY[1], p2, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());
        lineaBresenham(primitiva, (int) puntoXY[0].x, (int) puntoXY[0].y, (int) puntoXY[1].x, (int) puntoXY[1].y);
    }

    /**
     * Realiza la rasterización de un polígono
     *
     * @param bufferVertices
     * @param primitiva
     * @param wire
     * @param siempreTop
     */
    @Override
    public void raster(QVerticesBuffer bufferVertices, QPrimitiva primitiva, boolean wire, boolean siempreTop) {
        if (primitiva instanceof QPoligono) {
            if (wire) {
                procesarPoligonoWIRE(bufferVertices, (QPoligono) primitiva);
            } else {
                procesarPoligono(bufferVertices, (QPoligono) primitiva, siempreTop);
            }
        } else if (primitiva instanceof QLinea) {
            QVertice p1 = bufferVertices.getVerticesTransformados()[primitiva.listaVertices[0]];
            QVertice p2 = bufferVertices.getVerticesTransformados()[primitiva.listaVertices[1]];
            raster(primitiva, p1.ubicacion, p2.ubicacion);
        }
    }

    /**
     * Realiza un proceso de un polígono donde solo dibuja las aristas
     *
     * @param bufferVertices
     * @param poligono
     */
    protected void procesarPoligonoWIRE(QVerticesBuffer bufferVertices, QPoligono poligono) {
        if (poligono.listaVertices.length >= 3) {
            toCenter.set(poligono.centerCopy.ubicacion.getVector3());

            //validación caras traseras
            //si el objeto es tipo wire se dibuja igual sus caras traseras
            // si el objeto tiene transparencia (con material básico) igual dibuja sus caras traseras
            if ((!(poligono.material instanceof QMaterialBas) || ((poligono.material instanceof QMaterialBas) && !((QMaterialBas) poligono.material).isTransparencia()))
                    && poligono.geometria.tipo != QGeometria.GEOMETRY_TYPE_WIRE
                    && !render.opciones.isVerCarasTraseras() && toCenter.dotProduct(poligono.normalCopy) > 0) {
                render.poligonosDibujadosTemp--;
                return; // salta el dibujo de caras traseras
            }

            // Clipping and eliminating faces
            listaVerticesTmp.clear();
//            tempUVList.clear();
            //recorre los vertices y verifica si estan dentro del campo de vision, si no es asi, encuentra el vertice que corresponderia dentro del campo de vision
            for (int i = 0; i < poligono.listaVertices.length; i++) {
                vt[0] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[i]];
                vt[1] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[(i + 1) % poligono.listaVertices.length]];

                //si los 2 vertices a procesar estan en el campo de vision
                if (render.getCamara().estaEnCampoVision(vt[0]) && render.getCamara().estaEnCampoVision(vt[1])) {
                    listaVerticesTmp.add(vt[0]);
                    //si el primer vertice esta en el campo de vision y el segundo no, calculamos el vertice que corresponde
                    // al q no esta en el campo de vision interpolando
                } else if (render.getCamara().estaEnCampoVision(vt[0]) && !render.getCamara().estaEnCampoVision(vt[1])) {
                    listaVerticesTmp.add(vt[0]);
                    if (-vt[0].ubicacion.z == render.getCamara().frustrumCerca) {
                        continue;
                    }
                    alfa = (-render.getCamara().frustrumCerca - vt[0].ubicacion.z) / (vt[1].ubicacion.z - vt[0].ubicacion.z);
                    verticeTmp = new QVertice();
                    QMath.linear(verticeTmp, alfa, vt[0], vt[1]);
                    listaVerticesTmp.add(verticeTmp);
                    //si el primer vertice esta en el campo de vision y el segundo no, calculamos el vertice que corresponde
                    // al q no esta en el campo de vision interpolando
                } else if (!render.getCamara().estaEnCampoVision(vt[0]) && render.getCamara().estaEnCampoVision(vt[1])) {
                    if (-vt[1].ubicacion.z == render.getCamara().frustrumCerca && (i + 1) % poligono.listaVertices.length != 0) {
                        listaVerticesTmp.add(vt[1]);
                        continue;
                    }
                    alfa = (-render.getCamara().frustrumCerca - vt[0].ubicacion.z) / (vt[1].ubicacion.z - vt[0].ubicacion.z);
                    verticeTmp = new QVertice();
                    QMath.linear(verticeTmp, alfa, vt[0], vt[1]);
                    listaVerticesTmp.add(verticeTmp);
                }
            }

            // Rasterizacion (dibujo de los puntos del plano) 
            for (int i = 1; i < listaVerticesTmp.size() - 1; i++) {
                vt[0] = listaVerticesTmp.get(0);
                vt[1] = listaVerticesTmp.get(i);
                vt[2] = listaVerticesTmp.get(i + 1);

                if (!render.getCamara().estaEnCampoVision(vt[0]) && !render.getCamara().estaEnCampoVision(vt[1]) && !render.getCamara().estaEnCampoVision(vt[2])) {
                    continue;
                }
                render.getCamara().getCoordenadasPantalla(puntoXY[0], vt[0].ubicacion, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());
                render.getCamara().getCoordenadasPantalla(puntoXY[1], vt[1].ubicacion, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());
                render.getCamara().getCoordenadasPantalla(puntoXY[2], vt[2].ubicacion, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());

                if ((puntoXY[0].x < 0 && puntoXY[1].x < 0 && puntoXY[2].x < 0)
                        || (puntoXY[0].y < 0 && puntoXY[1].y < 0 && puntoXY[2].y < 0)
                        || (puntoXY[0].x > render.getFrameBuffer().getAncho() && puntoXY[1].x > render.getFrameBuffer().getAncho()
                        && puntoXY[2].x > render.getFrameBuffer().getAncho())
                        || (puntoXY[0].y > render.getFrameBuffer().getAlto() && puntoXY[1].y > render.getFrameBuffer().getAlto()
                        && puntoXY[2].y > render.getFrameBuffer().getAlto())) {
                    render.poligonosDibujados--;
                    continue;
                }

                //tres lineas
                lineaBresenham(poligono, (int) puntoXY[0].x, (int) puntoXY[0].y, (int) puntoXY[1].x, (int) puntoXY[1].y);
                lineaBresenham(poligono, (int) puntoXY[0].x, (int) puntoXY[0].y, (int) puntoXY[2].x, (int) puntoXY[2].y);
                lineaBresenham(poligono, (int) puntoXY[1].x, (int) puntoXY[1].y, (int) puntoXY[2].x, (int) puntoXY[2].y);
            }
        }
    }

    /**
     * Realiza el proceso de rasterización de un polígono sólido
     *
     * @param poligono
     * @param siempreTop
     * @param dibujar . SI es true, llama al método procesarPixel del shader
     */
    private void procesarPoligono(QVerticesBuffer bufferVertices, QPoligono poligono, boolean siempreTop) {
        try {
            if (poligono.listaVertices.length >= 3) {
                toCenter.set(poligono.centerCopy.ubicacion.getVector3());
                //validación caras traseras
                //si el objeto es tipo wire se dibuja igual sus caras traseras
                // si el objeto tiene transparencia (con material básico) igual dibuja sus caras traseras
                if ((!(poligono.material instanceof QMaterialBas) || ((poligono.material instanceof QMaterialBas) && !((QMaterialBas) poligono.material).isTransparencia()))
                        && !render.opciones.isVerCarasTraseras() && toCenter.dotProduct(poligono.normalCopy) > 0) {
                    render.poligonosDibujadosTemp--;
                    return; // salta el dibujo de caras traseras
                }

                // Clipping and eliminating faces
                listaVerticesTmp.clear();

                //recorre los vertices y verifica si estan dentro del campo de vision, si no es asi, encuentra el vertice que corresponderia dentro del campo de vision
                for (int i = 0; i < poligono.listaVertices.length; i++) {
                    vt[0] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[i]];
                    vt[1] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[(i + 1) % poligono.listaVertices.length]];

//                    //si los 2 vertices a procesar estan en el campo de vision
                    if (render.getCamara().estaEnCampoVision(vt[0]) && render.getCamara().estaEnCampoVision(vt[1])) {
                        listaVerticesTmp.add(vt[0]);
                    } else {
                        //cuando uno de los dos no esta en el campo de vision
                        if (render.getCamara().estaEnCampoVision(vt[0]) && !render.getCamara().estaEnCampoVision(vt[1])) {
                            listaVerticesTmp.add(vt[0]);
                            if (-vt[0].ubicacion.z == render.getCamara().frustrumCerca) {
                                continue;
                            }
//                            alfa = (-render.getCamara().frustrumCerca - vt[0].ubicacion.z) / (vt[1].ubicacion.z - vt[0].ubicacion.z);
                        } else if (!render.getCamara().estaEnCampoVision(vt[0]) && render.getCamara().estaEnCampoVision(vt[1])) {
                            if (-vt[1].ubicacion.z == render.getCamara().frustrumCerca && (i + 1) % poligono.listaVertices.length != 0) {
                                listaVerticesTmp.add(vt[1]);
                                continue;
                            }
//                            alfa = (-render.getCamara().frustrumCerca - vt[0].ubicacion.z) / (vt[1].ubicacion.z - vt[0].ubicacion.z);
                        } else {
                            continue;
                        }
                        alfa = render.getCamara().obtenerClipedVerticeAlfa(vt[0].ubicacion.getVector3(), vt[1].ubicacion.getVector3());
                        verticeTmp = new QVertice();
                        QMath.linear(verticeTmp, alfa, vt[0], vt[1]);
                        listaVerticesTmp.add(verticeTmp);
                    }
////si los 2 vertices a procesar estan en el campo de vision
//                    if (render.getCamara().estaEnCampoVision(vt[0]) && render.getCamara().estaEnCampoVision(vt[1])) {
//                        listaVerticesTmp.add(vt[0]);
//                        //si el primer vertice esta en el campo de vision y el segundo no, calculamos el vertice que corresponde
//                        // al q no esta en el campo de vision interpolando
//                    } else if (render.getCamara().estaEnCampoVision(vt[0]) && !render.getCamara().estaEnCampoVision(vt[1])) {
//                        listaVerticesTmp.add(vt[0]);
//                        if (-vt[0].ubicacion.z == render.getCamara().frustrumCerca) {
//                            continue;
//                        }
//                        alfa = (-render.getCamara().frustrumCerca - vt[0].ubicacion.z) / (vt[1].ubicacion.z - vt[0].ubicacion.z);
//                        verticeTmp = new QVertice();
//                        QMath.linear(verticeTmp, alfa, vt[0], vt[1]);
//                        listaVerticesTmp.add(verticeTmp);
//                        //si el primer vertice esta en el campo de vision y el segundo no, calculamos el vertice que corresponde
//                        // al q no esta en el campo de vision interpolando
//                    } else if (!render.getCamara().estaEnCampoVision(vt[0]) && render.getCamara().estaEnCampoVision(vt[1])) {
//                        if (-vt[1].ubicacion.z == render.getCamara().frustrumCerca && (i + 1) % primitiva.listaVertices.length != 0) {
//                            listaVerticesTmp.add(vt[1]);
//                            continue;
//                        }
//                        alfa = (-render.getCamara().frustrumCerca - vt[0].ubicacion.z) / (vt[1].ubicacion.z - vt[0].ubicacion.z);
//                        verticeTmp = new QVertice();
//                        QMath.linear(verticeTmp, alfa, vt[0], vt[1]);
//                        listaVerticesTmp.add(verticeTmp);
//                    }
                }

                // Rasterizacion (dibujo de los puntos del plano)
                //Separo en triangulos sin importar cuantos puntos tenga
                for (int i = 1; i < listaVerticesTmp.size() - 1; i++) {
                    vt[0] = listaVerticesTmp.get(0);
                    vt[1] = listaVerticesTmp.get(i);
                    vt[2] = listaVerticesTmp.get(i + 1);
                    // si el triangulo no esta en el campo de vision, pasamos y no dibujamos
                    if (!render.getCamara().estaEnCampoVision(vt[0]) && !render.getCamara().estaEnCampoVision(vt[1]) && !render.getCamara().estaEnCampoVision(vt[2])) {
                        continue;
                    }
                    //obtenemos los puntos proyectados en la pantalla
                    render.getCamara().getCoordenadasPantalla(puntoXY[0], vt[0].ubicacion, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());
                    render.getCamara().getCoordenadasPantalla(puntoXY[1], vt[1].ubicacion, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());
                    render.getCamara().getCoordenadasPantalla(puntoXY[2], vt[2].ubicacion, render.getFrameBuffer().getAncho(), render.getFrameBuffer().getAlto());

                    //valido si el punto proyectado esta dentro del rango de vision de la Camara
                    if ((puntoXY[0].x < 0 && puntoXY[1].x < 0 && puntoXY[2].x < 0)
                            || (puntoXY[0].y < 0 && puntoXY[1].y < 0 && puntoXY[2].y < 0)
                            || (puntoXY[0].x > render.getFrameBuffer().getAncho() && puntoXY[1].x > render.getFrameBuffer().getAncho() && puntoXY[2].x > render.getFrameBuffer().getAncho())
                            || (puntoXY[0].y > render.getFrameBuffer().getAlto() && puntoXY[1].y > render.getFrameBuffer().getAlto() && puntoXY[2].y > render.getFrameBuffer().getAlto())) {
                        render.poligonosDibujados--;
                        continue;
                    }

                    //mapeo normal para materiales básicos
                    if ((poligono.material instanceof QMaterialBas && ((QMaterialBas) poligono.material).getMapaNormal() != null) //                            || (primitiva.material instanceof QMaterialBas && ((QMaterialBas) primitiva.material).getMapaDesplazamiento() != null)
                            ) {
                        calcularArriba(up, vt[0], vt[1], vt[2]);
                        calcularDerecha(right, vt[0], vt[1], vt[2]);
                        up.normalize();
                        right.normalize();
                    }

                    // If primitiva is closer than clipping distance
                    order[0] = 0;
                    order[1] = 1;
                    order[2] = 2;

                    // Sort the points by ejeY coordinate. (bubble sort...derps)
                    if (puntoXY[order[0]].y > puntoXY[order[1]].y) {
                        temp = order[0];
                        order[0] = order[1];
                        order[1] = temp;
                    }
                    if (puntoXY[order[1]].y > puntoXY[order[2]].y) {
                        temp = order[1];
                        order[1] = order[2];
                        order[2] = temp;
                    }
                    if (puntoXY[order[0]].y > puntoXY[order[1]].y) {
                        temp = order[0];
                        order[0] = order[1];
                        order[1] = temp;
                    }

                    //proceso de dibujo, se deberia separar en una clase de escaneo
                    for (int y = (int) puntoXY[order[0]].y; y <= puntoXY[order[2]].y; y++) {
                        if (y > render.getFrameBuffer().getAlto()) {
                            break;
                        }
                        if (y < 0) {
                            if (puntoXY[order[2]].y > 0) {
                                y = 0;
                            }
                        }

                        zHasta = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[2]].ubicacion.y, vt[order[2]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
                        xHastaPantalla = (int) QMath.linear(puntoXY[order[0]].y, puntoXY[order[2]].y, y, puntoXY[order[0]].x, puntoXY[order[2]].x);

                        if (!testDifference(vt[order[0]].ubicacion.z, vt[order[2]].ubicacion.z)) {
                            alfa = (zHasta - vt[order[0]].ubicacion.z) / (vt[order[2]].ubicacion.z - vt[order[0]].ubicacion.z);
                        } else {
                            alfa = puntoXY[order[0]].y == puntoXY[order[2]].y ? 1 : (float) (y - puntoXY[order[0]].y) / (float) (puntoXY[order[2]].y - puntoXY[order[0]].y);
                        }
                        xHasta = QMath.linear(alfa, vt[order[0]].ubicacion.x, vt[order[2]].ubicacion.x);
                        QMath.linear(verticeHasta, alfa, vt[order[0]], vt[order[2]]);
                        if (y <= puntoXY[order[1]].y) {
                            // Primera mitad
                            if (vt[order[0]].ubicacion.y != vt[order[1]].ubicacion.y) {
                                zDesde = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[1]].ubicacion.y, vt[order[1]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
                            } else {
                                zDesde = vt[order[1]].ubicacion.z;
                            }
                            if (!testDifference(vt[order[0]].ubicacion.z, vt[order[1]].ubicacion.z)) {
                                alfa = (zDesde - vt[order[0]].ubicacion.z) / (vt[order[1]].ubicacion.z - vt[order[0]].ubicacion.z);
                            } else {
                                alfa = puntoXY[order[0]].y == puntoXY[order[1]].y ? 0 : (float) (y - puntoXY[order[0]].y) / (float) (puntoXY[order[1]].y - puntoXY[order[0]].y);
                            }
                            xDesde = QMath.linear(alfa, vt[order[0]].ubicacion.x, vt[order[1]].ubicacion.x);
                            uDesde = QMath.linear(alfa, vt[order[0]].u, vt[order[1]].u);
                            vDesde = QMath.linear(alfa, vt[order[0]].v, vt[order[1]].v);
                            xDesdePantalla = (int) QMath.linear(puntoXY[order[0]].y, puntoXY[order[1]].y, y, puntoXY[order[0]].x, puntoXY[order[1]].x);
                            QMath.linear(verticeDesde, alfa, vt[order[0]], vt[order[1]]);
                        } else {
                            // Segunda mitad
                            if (vt[order[1]].ubicacion.y != vt[order[2]].ubicacion.y) {
                                zDesde = interpolateZbyY(vt[order[1]].ubicacion.y, vt[order[1]].ubicacion.z, vt[order[2]].ubicacion.y, vt[order[2]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
                            } else {
                                zDesde = vt[order[1]].ubicacion.z;
                            }
                            if (!testDifference(vt[order[1]].ubicacion.z, vt[order[2]].ubicacion.z)) {
                                alfa = (zDesde - vt[order[1]].ubicacion.z) / (vt[order[2]].ubicacion.z - vt[order[1]].ubicacion.z);
                            } else {
                                alfa = puntoXY[order[1]].y == puntoXY[order[2]].y ? 0 : (float) (y - puntoXY[order[1]].y) / (float) (puntoXY[order[2]].y - puntoXY[order[1]].y);
                            }
                            xDesde = QMath.linear(alfa, vt[order[1]].ubicacion.x, vt[order[2]].ubicacion.x);
                            uDesde = QMath.linear(alfa, vt[order[1]].u, vt[order[2]].u);
                            vDesde = QMath.linear(alfa, vt[order[1]].v, vt[order[2]].v);
                            xDesdePantalla = (int) QMath.linear(puntoXY[order[1]].y, puntoXY[order[2]].y, y, puntoXY[order[1]].x, puntoXY[order[2]].x);
                            QMath.linear(verticeDesde, alfa, vt[order[1]], vt[order[2]]);
                        }
                        increment = xDesdePantalla > xHastaPantalla ? -1 : 1;

                        if (xDesdePantalla != xHastaPantalla) {
                            for (int x = xDesdePantalla; x != xHastaPantalla; x += increment) {
                                if (x >= render.getFrameBuffer().getAncho() && increment > 0) {
                                    break;
                                } else if (x < 0 && increment < 0) {
                                    break;
                                } else if (x >= render.getFrameBuffer().getAncho() && increment < 0) {
                                    if (xHastaPantalla < render.getFrameBuffer().getAncho()) {
                                        x = render.getFrameBuffer().getAncho();
                                    } else {
                                        break;
                                    }
                                } else if (x < 0 && increment > 0) {
                                    if (xHastaPantalla >= 0) {
                                        x = -1;
                                    } else {
                                        break;
                                    }
                                }
                                prepararPixel(poligono, x, y, siempreTop);//Pixeles del interior del primitiva                          
                            }
                        }
                        prepararPixel(poligono, xHastaPantalla, y, siempreTop); //<ag> pixeles del exterior del primitiva
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Prepara el pixel - Realiza la validacion de profundidad y setea los
     * valores a usar en el shader como material, entidad, etc.
     *
     * @param primitiva
     * @param x
     * @param y
     * @param siempreArriba
     */
    protected void prepararPixel(QPrimitiva primitiva, int x, int y, boolean siempreArriba) {
        if (x > 0 && x < render.getFrameBuffer().getAncho() && y > 0 && y < render.getFrameBuffer().getAlto()) {
            zActual = interpolateZbyX(xDesde, zDesde, xHasta, zHasta, x, (int) render.getFrameBuffer().getAncho(), render.getCamara().camaraAncho);
            if (!testDifference(zDesde, zHasta)) {
                alfa = (zActual - zDesde) / (zHasta - zDesde);
            } else {
                alfa = xDesdePantalla == xHastaPantalla ? 0 : (float) (x - xDesdePantalla) / (float) (xHastaPantalla - xDesdePantalla);
            }

            // siempre y cuando sea menor que el zbuffer se debe dibujar. quiere decir que esta delante
            if (siempreArriba || (-zActual > 0 && -zActual < render.getFrameBuffer().getZBuffer(x, y))) {
                QMath.linear(verticeActual, alfa, verticeDesde, verticeHasta);
                // si no es suavizado se copia la normal de la cara para dibujar con Flat Shadded
                // igualmente si es tipo wire toma la normal de la cara porq no hay normal interpolada

                if (primitiva instanceof QPoligono) {
                    if (primitiva.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE
                            || !(((QPoligono) primitiva).smooth && (render.opciones.getTipoVista() >= QOpcionesRenderer.VISTA_PHONG) || render.opciones.isForzarSuavizado())) {
                        verticeActual.normal.set(((QPoligono) primitiva).normalCopy);
                    }
                }

                if (render.opciones.isMaterial()) {
                    //modifico la normal de acuerdo a la rugosidad del material
//                    if (primitiva.material != null
//                            && (primitiva.material instanceof QMaterialBas) //si tiene material basico 
//                            ) {
//
//                        QMaterialBas material = (QMaterialBas) primitiva.material;
//                        verticeActual.normal.add(material.getRugosidad() * (float) Math.random(), QVector3.unitario_xyz);
//                        verticeActual.normal.normalize();
//                    }
                    //mapa de desplazamiento
                    if (primitiva.material != null && (primitiva.material instanceof QMaterialBas && ((QMaterialBas) primitiva.material).getMapaDesplazamiento() != null) //si tiene material basico y tiene mapa por desplazamiento
                            ) {
                        QMaterialBas material = (QMaterialBas) primitiva.material;
                        QColor colorDesplazamiento = material.getMapaDesplazamiento().get_QARGB(verticeActual.u, verticeActual.v);
                        float fac = (colorDesplazamiento.r + colorDesplazamiento.g + colorDesplazamiento.b) / 3.0f;
                        QVector3 tmp = new QVector3(verticeActual);
                        tmp.add(verticeActual.normal.clone().multiply(fac * 2 - 1));
                        verticeActual.setXYZ(tmp.x, tmp.y, tmp.z);
                    }

                    //Mapa de normales
                    if (render.opciones.isNormalMapping() && primitiva.material != null && (primitiva.material instanceof QMaterialBas && ((QMaterialBas) primitiva.material).getMapaNormal() != null) //si tiene material basico y tiene mapa normal
                            ) {
                        QMaterialBas material = (QMaterialBas) primitiva.material;

                        currentUp.set(up);
                        currentRight.set(right);
                        //usando el metodo arriba e izquierda
                        currentUp.multiply((material.getMapaNormal().getNormalY(verticeActual.u, verticeActual.v) * 2 - 1) * material.getFactorNormal());
                        currentRight.multiply((material.getMapaNormal().getNormalX(verticeActual.u, verticeActual.v) * 2 - 1) * material.getFactorNormal());
                        // continua ejecucion normal
                        verticeActual.normal.multiply(material.getMapaNormal().getNormalZ(verticeActual.u, verticeActual.v) * 2 - 1);
                        verticeActual.normal.add(currentUp, currentRight);
                        verticeActual.normal.normalize();
                    }
                    //si tiene material pbr y tiene mapa normal
                    if (primitiva.material != null && (primitiva.material instanceof QMaterialNodo) && render.opciones.isNormalMapping()) {
                        verticeActual.arriba.set(up);
                        verticeActual.derecha.set(right);
                    }
                }

                //panelclip
                try {
                    if (render.getPanelClip() != null) {
                        if (!render.getPanelClip().esVisible(QTransformar.transformarVector(QTransformar.transformarVectorInversa(verticeActual.ubicacion, primitiva.geometria.entidad, render.getCamara()), primitiva.geometria.entidad))) {
                            return;
                        }
                    }
                } catch (Exception e) {
                }

                //actualiza le buffer 
                if (render.getFrameBuffer().getPixel(x, y) != null) {
                    render.getFrameBuffer().getPixel(x, y).setDibujar(true);
                    render.getFrameBuffer().getPixel(x, y).ubicacion.set(verticeActual.ubicacion);
                    render.getFrameBuffer().getPixel(x, y).normal.set(verticeActual.normal);
                    render.getFrameBuffer().getPixel(x, y).material = primitiva.material;
                    render.getFrameBuffer().getPixel(x, y).primitiva = primitiva;
                    render.getFrameBuffer().getPixel(x, y).u = verticeActual.u;
                    render.getFrameBuffer().getPixel(x, y).v = verticeActual.v;
                    render.getFrameBuffer().getPixel(x, y).entidad = primitiva.geometria.entidad;
                    render.getFrameBuffer().getPixel(x, y).arriba.set(verticeActual.arriba);
                    render.getFrameBuffer().getPixel(x, y).derecha.set(verticeActual.derecha);
                    render.getFrameBuffer().setQColor(x, y, render.getShader().colorearPixel(render.getFrameBuffer().getPixel(x, y), x, y));
                }
                //actualiza el zBuffer
                render.getFrameBuffer().setZBuffer(x, y, -zActual);
            }
        }
    }

    protected void calcularArriba(QVector3 upVector, QVertice v1, QVertice v2, QVertice v3) {
        if (v1.u == v2.u) {
            coefficient1 = 0;
            coefficient2 = 1.0f / (v1.v - v2.v);
        } else if (v1.u == v3.u) {
            coefficient1 = 1.0f / (v1.v - v3.v);
            coefficient2 = 0;
        } else {
            coefficient1 = 1.0f / ((v1.v - v3.v) - (v1.u - v3.u) * (v1.v - v2.v) / (v1.u - v2.u));
            coefficient2 = (-coefficient1 * (v1.u - v3.u)) / (v1.u - v2.u);
        }
        upVector.x = coefficient1 * (v1.ubicacion.x - v3.ubicacion.x) + coefficient2 * (v1.ubicacion.x - v2.ubicacion.x);
        upVector.y = coefficient1 * (v1.ubicacion.y - v3.ubicacion.y) + coefficient2 * (v1.ubicacion.y - v2.ubicacion.y);
        upVector.z = coefficient1 * (v1.ubicacion.z - v3.ubicacion.z) + coefficient2 * (v1.ubicacion.z - v2.ubicacion.z);
    }

    protected void calcularDerecha(QVector3 rightVector, QVertice v1, QVertice v2, QVertice v3) {
        if (v1.v == v2.v) {
            coefficient1 = 0;
            coefficient2 = 1.0f / (v1.u - v2.u);
        } else if (v1.v == v3.v) {
            coefficient1 = 1.0f / (v1.u - v3.u);
            coefficient2 = 0;
        } else {
            coefficient1 = 1.0f / ((v1.u - v3.u) - (v1.v - v3.v) * (v1.u - v2.u) / (v1.v - v2.v));
            coefficient2 = (-coefficient1 * (v1.v - v3.v)) / (v1.v - v2.v);
        }
        rightVector.x = coefficient1 * (v1.ubicacion.x - v3.ubicacion.x) + coefficient2 * (v1.ubicacion.x - v2.ubicacion.x);
        rightVector.y = coefficient1 * (v1.ubicacion.y - v3.ubicacion.y) + coefficient2 * (v1.ubicacion.y - v2.ubicacion.y);
        rightVector.z = coefficient1 * (v1.ubicacion.z - v3.ubicacion.z) + coefficient2 * (v1.ubicacion.z - v2.ubicacion.z);
    }

    /**
     * Algoritmo para pintar una linea
     *
     * @param primitiva
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     */
    public void lineaBresenham(QPrimitiva primitiva, int x0, int y0, int x1, int y1) {
        int x, y, dx, dy, p, incE, incNE, stepx, stepy;
        dx = (x1 - x0);
        dy = (y1 - y0);
        /* determinar que punto usar para empezar, cual para terminar */
        if (dy < 0) {
            dy = -dy;
            stepy = -1;
        } else {
            stepy = 1;
        }
        if (dx < 0) {
            dx = -dx;
            stepx = -1;
        } else {
            stepx = 1;
        }
        x = x0;
        y = y0;

        zHasta = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[2]].ubicacion.y, vt[order[2]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
        xHasta = QMath.linear(dx, vt[order[0]].ubicacion.x, vt[order[2]].ubicacion.x);
        zDesde = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[1]].ubicacion.y, vt[order[1]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);

        prepararPixel(primitiva, x, y, false);

        /* se cicla hasta llegar al extremo de la línea */
        if (dx > dy) {
            p = 2 * dy - dx;
            incE = 2 * dy;
            incNE = 2 * (dy - dx);
            while (x != x1) {
                x = x + stepx;
                if (p < 0) {
                    p = p + incE;
                } else {
                    y = y + stepy;
                    p = p + incNE;
                }

                zHasta = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[2]].ubicacion.y, vt[order[2]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
                xHasta = QMath.linear(dx, vt[order[0]].ubicacion.x, vt[order[2]].ubicacion.x);
                zDesde = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[1]].ubicacion.y, vt[order[1]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
                prepararPixel(primitiva, x, y, false);
            }
        } else {
            p = 2 * dx - dy;
            incE = 2 * dx;
            incNE = 2 * (dx - dy);
            while (y != y1) {
                y = y + stepy;
                if (p < 0) {
                    p = p + incE;
                } else {
                    x = x + stepx;
                    p = p + incNE;
                }

                zHasta = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[2]].ubicacion.y, vt[order[2]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
                xHasta = QMath.linear(dx, vt[order[0]].ubicacion.x, vt[order[2]].ubicacion.x);
                zDesde = interpolateZbyY(vt[order[0]].ubicacion.y, vt[order[0]].ubicacion.z, vt[order[1]].ubicacion.y, vt[order[1]].ubicacion.z, y, (int) render.getFrameBuffer().getAlto(), render.getCamara().camaraAlto);
                prepararPixel(primitiva, x, y, false);
            }
        }
    }

    protected float interpolateZbyX(float x1, float z1, float x2, float z2, int xS, int wS, float w) {
        if (testDifference(z1, z2)) {
            return z1;
        }
        tempFloat = (x1 - z1 * (x2 - x1) / (z2 - z1)) / (-(x2 - x1) / (z2 - z1) + (.5f - (float) xS / (float) wS) * w);
        if ((tempFloat - z1) / (z2 - z1) > 1) {
            return z2;
        }
        if ((tempFloat - z1) / (z2 - z1) < 0) {
            return z1;
        }
        return tempFloat;
    }

    protected float interpolateZbyY(float y1, float z1, float y2, float z2, int yS, int hS, float h) {
        if (testDifference(z1, z2)) {
            return z1;
        }
        tempFloat = (z1 * (y2 - y1) / (z2 - z1) - y1) / ((y2 - y1) / (z2 - z1) + ((.5f - (float) yS / (float) hS) * h));
        if ((tempFloat - z1) / (z2 - z1) > 1) {
            return z2;
        }
        if ((tempFloat - z1) / (z2 - z1) < 0) {
            return z1;
        }
        return tempFloat;
    }

    protected boolean testDifference(float n1, float n2) {
        return n1 - n2 > -INTERPOLATION_CLAMP && n1 - n2 < INTERPOLATION_CLAMP;
    }

}
