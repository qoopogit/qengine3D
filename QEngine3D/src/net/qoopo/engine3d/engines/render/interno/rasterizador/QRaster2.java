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
import net.qoopo.engine3d.engines.render.interno.transformacion.QTransformar;
import net.qoopo.engine3d.componentes.transformacion.QVerticesBuffer;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector2;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.math.QVector4;
import net.qoopo.engine3d.core.util.TempVars;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.QOpcionesRenderer;

/**
 * Realiza la rasterización de los polígonos version 2. Aun tiene fallas en el
 * mapeo de texturas por la perspectiva
 *
 * @author alberto
 */
public class QRaster2 extends AbstractRaster {

    protected QMotorRender render;

    protected QVector3 toCenter = new QVector3();
    protected ArrayList<QVertice> listaVerticesTmp = new ArrayList<>();
    protected QVertice[] vt = new QVertice[4];//pueden ser 4 en caso de agregar un nuevo vertice para separar el trangulo en 2, el superior e inferior
    protected QVertice verticeTmp = new QVertice();
    protected float alfa;//factor de interpolación

    protected QVector2[] puntoXY = new QVector2[4]; // puntos proyectados, pueden ser 4 en casi de agregar un uevo vertice para separar el trangulo en 2, el superior e inferior
    protected int order[] = new int[4];
    protected int temp;

    protected float zDesde;
    protected float zHasta;
    protected float xDesde;
    protected float xHasta;
    protected float zActual;

    float tmpU, tmpV;
    float uDesde, vDesde;
    float uHasta, vHasta;

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

    public QRaster2(QMotorRender render) {
        this.render = render;
        for (int i = 0; i < 4; i++) {
            puntoXY[i] = new QVector2();
        }
    }

    /**
     *
     * @param primitiva
     * @param p1
     * @param p2
     */
    @Override
    public void dibujarLinea(QPrimitiva primitiva, QVector4 p1, QVector4 p2) {
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
    public void procesarPoligono(QVerticesBuffer bufferVertices, QPrimitiva primitiva, boolean wire, boolean siempreTop) {
        if (primitiva instanceof QPoligono) {
            if (wire) {
                procesarPoligonoWIRE(bufferVertices, (QPoligono) primitiva);
            } else {
                procesarPoligono(bufferVertices, (QPoligono) primitiva, siempreTop);
            }
        } else if (primitiva instanceof QLinea) {
            QVertice p1 = bufferVertices.getVerticesTransformados()[primitiva.listaVertices[0]];
            QVertice p2 = bufferVertices.getVerticesTransformados()[primitiva.listaVertices[1]];
            dibujarLinea(primitiva, p1.ubicacion, p2.ubicacion);
        }
    }

    /**
     * Realiza un proceso de un polígono donde solo dibuja las aristas
     *
     * @param bufferVertices
     * @param poligono
     *
     */
    protected void procesarPoligonoWIRE(QVerticesBuffer bufferVertices, QPoligono poligono) {
        if (poligono.listaVertices.length >= 3) {
            toCenter.set(poligono.centerCopy.ubicacion.getVector3());

            //validación caras traseras
            //si el objeto es tipo wire se dibuja igual sus caras traseras
            // si el objeto tiene transparencia (con material básico) igual dibuja sus caras traseras
            if ((!(poligono.material instanceof QMaterialBas) || ((poligono.material instanceof QMaterialBas) && !((QMaterialBas) poligono.material).isTransparencia()))
                    && poligono.geometria.tipo != QGeometria.GEOMETRY_TYPE_WIRE
                    && !render.opciones.verCarasTraseras && toCenter.dotProduct(poligono.normalCopy) > 0) {
                render.poligonosDibujadosTemp--;
                return; // salta el dibujo de caras traseras
            }

            // Clipping and eliminating faces
            listaVerticesTmp.clear();
            //recorre los vertices y verifica si estan dentro del campo de vision, si no es asi, encuentra el vertice que corresponderia dentro del campo de vision
            for (int i = 0; i < poligono.listaVertices.length; i++) {
                vt[0] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[i]];
                vt[1] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[(i + 1) % poligono.listaVertices.length]];

                //si los 2 vertices a procesar estan en el campo de vision
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
                        && !render.opciones.verCarasTraseras && toCenter.dotProduct(poligono.normalCopy) > 0) {
                    render.poligonosDibujadosTemp--;
                    return; // salta el dibujo de caras traseras
                }

                // Clipping and eliminating faces
                listaVerticesTmp.clear();
                //recorre los vertices y verifica si estan dentro del campo de vision, si no es asi, encuentra el vertice que corresponderia dentro del campo de vision
                for (int i = 0; i < poligono.listaVertices.length; i++) {
                    vt[0] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[i]];
                    vt[1] = bufferVertices.getVerticesTransformados()[poligono.listaVertices[(i + 1) % poligono.listaVertices.length]];

                    //si los 2 vertices a procesar estan en el campo de vision
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
                    if ((poligono.material instanceof QMaterialBas && ((QMaterialBas) poligono.material).getMapaNormal() != null) //                            || (poligono.material instanceof QMaterialBas && ((QMaterialBas) poligono.material).getMapaDesplazamiento() != null)
                            ) {
                        calcularArriba(up, vt[0], vt[1], vt[2]);
                        calcularDerecha(right, vt[0], vt[1], vt[2]);
                        up.normalize();
                        right.normalize();
                    }

                    // If poligono is closer than clipping distance
                    order[0] = 0;
                    order[1] = 1;
                    order[2] = 2;
                    order[3] = 3;//este no se mueve, se usa en caso de dividir del triangulo, el nuevo punto

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

                    if (puntoXY[order[1]].y == puntoXY[order[2]].y) {
                        //    this->scanBottomFlatTriangle(v1, v2, v3);
                        trianguloHaciaArriba(poligono, order[0], order[1], order[2]);
                    } else if (puntoXY[order[0]].y == puntoXY[order[1]].y) {
                        //    this->scanTopFlatTriangle(v1, v2, v3);
                        trianguloHaciaAbajo(poligono, order[0], order[1], order[2]);
                    } else {
                        // En este caso vamos a dividir el triángulo en dos
                        // para tener uno que cumpla 'bottomFlat' y el otro 'TopFlat' y necesitamos un punto extra para separar ambos
                        TempVars tvars = TempVars.get();

                        try {

                            //el nuevo vertice a crear
                            vt[3] = tvars.vertice1;

                            puntoXY[3].setXY((puntoXY[order[0]].x + ((puntoXY[order[1]].y - puntoXY[order[0]].y) / (puntoXY[order[2]].y - puntoXY[order[0]].y)) * (puntoXY[order[2]].x - puntoXY[order[0]].x)), puntoXY[order[1]].y);

                            QVector3 bar = tvars.vector3f1;
                            // Hallamos las coordenadas baricéntricas del punto v4 respecto al triángulo pa, pb, pc
                            QMath.getBarycentricCoordinates(bar, puntoXY[3].x, puntoXY[3].y, puntoXY[order[0]], puntoXY[order[1]], puntoXY[order[2]]);

                            //correccion de proyectar
//                            float zUV = 1.0f / (bar.x * 1.0f / vt[order[0]].ubicacion.z + bar.y * 1.0f / vt[order[1]].ubicacion.z + bar.z * 1.0f / vt[order[2]].ubicacion.z);
                            float zUV = 1.0f / (bar.x / vt[order[0]].ubicacion.z + bar.y / vt[order[1]].ubicacion.z + bar.z / vt[order[2]].ubicacion.z);

                            //calculamos el vertice nuevo
                            vt[3].ubicacion.x = bar.x * vt[order[0]].ubicacion.x + bar.y * vt[order[1]].ubicacion.x + bar.z * vt[order[2]].ubicacion.x;
                            vt[3].ubicacion.y = bar.x * vt[order[0]].ubicacion.y + bar.y * vt[order[1]].ubicacion.y + bar.z * vt[order[2]].ubicacion.y;
//                            vt[3].ubicacion.z = bar.x * vt[order[0]].ubicacion.z + bar.y * vt[order[1]].ubicacion.z + bar.z * vt[order[2]].ubicacion.z;
//                            vt[3].ubicacion.z = zUV * (bar.x * (vt[order[0]].ubicacion.z / vt[order[0]].ubicacion.z) + bar.y * (vt[order[1]].ubicacion.z / vt[order[1]].ubicacion.z) + bar.z * (vt[order[2]].ubicacion.z / vt[order[2]].ubicacion.z));
                            //correccion de proyectar
                            vt[3].ubicacion.z = zUV * (bar.x + bar.y + bar.z);

                            //coordenadas de textura
//                            vt[3].u = bar.x * vt[order[0]].u + bar.y * vt[order[1]].u + bar.z * vt[order[2]].u;
//                            vt[3].v = bar.x * vt[order[0]].v + bar.y * vt[order[1]].v + bar.z * vt[order[2]].v;
                            //correccion de proyectar
                            vt[3].u = zUV * (bar.x * (vt[order[0]].u / vt[order[0]].ubicacion.z) + bar.y * (vt[order[1]].u / vt[order[1]].ubicacion.z) + bar.z * (vt[order[2]].u / vt[order[2]].ubicacion.z));
                            vt[3].v = zUV * (bar.x * (vt[order[0]].v / vt[order[0]].ubicacion.z) + bar.y * (vt[order[1]].v / vt[order[1]].ubicacion.z) + bar.z * (vt[order[2]].v / vt[order[2]].ubicacion.z));

                            //la normal
                            vt[3].normal.x = bar.x * vt[order[0]].normal.x + bar.y * vt[order[1]].normal.x + bar.z * vt[order[2]].normal.x;
                            vt[3].normal.y = bar.x * vt[order[0]].normal.y + bar.y * vt[order[1]].normal.y + bar.z * vt[order[2]].normal.y;
                            vt[3].normal.z = bar.x * vt[order[0]].normal.z + bar.y * vt[order[1]].normal.z + bar.z * vt[order[2]].normal.z;

//                            vt[3] = V4;
                            trianguloHaciaArriba(poligono, order[0], order[1], 3);
                            //    this->scanBottomFlatTriangle(v1, v2, v4);
                            trianguloHaciaAbajo(poligono, order[1], 3, order[2]);
                            //    this->scanTopFlatTriangle(v4, v2, v3);
                        } catch (Exception e) {

                        } finally {
                            tvars.release();
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Realiza un recorrido por cada linea del triangulo dibujar
     *
     * @param poligono
     * @param startX
     * @param endX
     * @param y
     * @param i1
     * @param i2
     * @param i3
     */
    private void scanLine(QPoligono poligono, float startX, float endX, int y, int i1, int i2, int i3) {
        if (startX == endX) {
            return;
        }

        QVector2 pa = puntoXY[i1];
        QVector2 pb = puntoXY[i2];
        QVector2 pc = puntoXY[i3];

        QVertice VA = vt[i1];
        QVertice VB = vt[i2];
        QVertice VC = vt[i3];

        // left to right
        if (startX > endX) {
            int tmp = (int) startX;
            startX = endX;
            endX = tmp;
        }
        TempVars t = TempVars.get();
        try {
            for (int x = (int) startX; x < endX; x++) {
                if (x >= 0 && y >= 0 && render.getFrameBuffer().getAncho() > x && render.getFrameBuffer().getAlto() > y) {
//             const Point2D pointFinal(x, y);
//
//        if (Tools::isPixelInWindow(pointFinal.x, pointFinal.y)) {
//            // Hallamos las coordenadas baricéntricas del punto v4 respecto al triángulo pa, pb, pc
//            Maths::getBarycentricCoordinates(alpha, theta, gamma, x, y, pa, pb, pc);
//
//            float z = alpha * A.z + theta * B.z + gamma * C.z; // Homogeneous clipspace
//
//            float buffer_shadowmap_z = lp->getShadowMappingBuffer(pointFinal.x, pointFinal.y);
//            if (buffer_shadowmap_z != NULL) {
//                if ( z < buffer_shadowmap_z ) {
//                    lp->setShadowMappingBuffer(pointFinal.x, pointFinal.y, z + offset_self_shadow);
//                }
//            }  else {
//                lp->setShadowMappingBuffer(pointFinal.x, pointFinal.y, z + offset_self_shadow);
//            }
//        }
                    QVector3 bar = t.vector3f1;
                    // Hallamos las coordenadas baricéntricas del punto v4 respecto al triángulo pa, pb, pc
                    QMath.getBarycentricCoordinates(bar, x, y, pa, pb, pc);

                    //                    // correccion de proyectar  
                    float zUV = 1.0f / (bar.x / VA.ubicacion.z + bar.y / VB.ubicacion.z + bar.z / VC.ubicacion.z);
//                    float zUV = 1.0f / (bar.x * 1.0f / VA.ubicacion.z + bar.y * 1.0f / VB.ubicacion.z + bar.z * 1.0f / VC.ubicacion.z);

                    //z coordenadas homogeneas
//                    float z = bar.x * VA.ubicacion.z + bar.y * VB.ubicacion.z + bar.z * VC.ubicacion.z; // Homogeneous clipspace
                    ///--------------------------------- VERTICE INTERPOLADO-------------------------
                    verticeActual.ubicacion.x = bar.x * VA.ubicacion.x + bar.y * VB.ubicacion.x + bar.z * VC.ubicacion.x;
                    verticeActual.ubicacion.y = bar.x * VA.ubicacion.y + bar.y * VB.ubicacion.y + bar.z * VC.ubicacion.y;
//                    verticeActual.ubicacion.z = bar.x * VA.ubicacion.z + bar.y * VB.ubicacion.z + bar.z * VC.ubicacion.z;
//                    verticeActual.ubicacion.z = zUV * (bar.x * (VA.ubicacion.z / VA.ubicacion.z) + bar.y * (VB.ubicacion.z / VB.ubicacion.z) + bar.z * (VC.ubicacion.z / VC.ubicacion.z));
                    //correccion de proyectar
                    verticeActual.ubicacion.z = zUV * (bar.x + bar.y + bar.z);

                    //--------------------------------- COORDENADAS TEXTURAS-------------------------
//                    verticeActual.u = bar.x * VA.u + bar.y * VB.u + bar.z * VC.u;
//                    verticeActual.v = bar.x * VA.v + bar.y * VB.v + bar.z * VC.v;
                    //correccion de proyectar
                    verticeActual.u = zUV * (bar.x * (VA.u / VA.ubicacion.z) + bar.y * (VB.u / VB.ubicacion.z) + bar.z * (VC.u / VC.ubicacion.z));
                    verticeActual.v = zUV * (bar.x * (VA.v / VA.ubicacion.z) + bar.y * (VB.v / VB.ubicacion.z) + bar.z * (VC.v / VC.ubicacion.z));
//
//                    float zUV = 1.0f / (bar.x * VA.ubicacion.z + bar.y * VB.ubicacion.z + bar.z * VC.ubicacion.z);
//                    verticeActual.u/=  verticeActual.ubicacion.z;
//                    verticeActual.v/=  verticeActual.ubicacion.z;
//                    verticeActual.u *= zUV;
//                    verticeActual.v *= zUV;

//---------------------------------  NORMAL-------------------------
                    verticeActual.normal.x = bar.x * VA.normal.x + bar.y * VB.normal.x + bar.z * VC.normal.x;
                    verticeActual.normal.y = bar.x * VA.normal.y + bar.y * VB.normal.y + bar.z * VC.normal.y;
                    verticeActual.normal.z = bar.x * VA.normal.z + bar.y * VB.normal.z + bar.z * VC.normal.z;
//                    verticeActual.normal.normalize();

                    prepararPixel(poligono, x, y, false);
                }
            }
        } catch (Exception e) {

        } finally {
            t.release();
        }
    }

    /**
     *
     * @param poligono
     * @param i1
     * @param i2
     * @param i3
     */
    private void trianguloHaciaArriba(QPoligono poligono, int i1, int i2, int i3) {
        QVector2 pa = puntoXY[i1];
        QVector2 pb = puntoXY[i2];
        QVector2 pc = puntoXY[i3];

        float invslope1 = (pb.x - pa.x) / (pb.y - pa.y);
        float invslope2 = (pc.x - pa.x) / (pc.y - pa.y);

        float curx1 = pa.x;
        float curx2 = pa.x;

        for (int scanlineY = (int) pa.y; scanlineY <= pb.y; scanlineY++) {
            scanLine(poligono, curx1, curx2, scanlineY, i1, i2, i3);
            curx1 += invslope1;
            curx2 += invslope2;
        }

    }

    /**
     *
     * @param poligono
     * @param i1
     * @param i2
     * @param i3
     */
    private void trianguloHaciaAbajo(QPoligono poligono, int i1, int i2, int i3) {
        QVector2 pa = puntoXY[i1];
        QVector2 pb = puntoXY[i2];
        QVector2 pc = puntoXY[i3];

        float invslope1 = (pc.x - pa.x) / (pc.y - pa.y);
        float invslope2 = (pc.x - pb.x) / (pc.y - pb.y);

        float curx1 = pc.x;
        float curx2 = pc.x;

        for (int scanlineY = (int) pc.y; scanlineY > pa.y; scanlineY--) {
            scanLine(poligono, curx1, curx2, scanlineY, i1, i2, i3);
            curx1 -= invslope1;
            curx2 -= invslope2;
        }
    }

    /**
     * Prepara el pixel - Realiza la validacion de profundidad y setea los
     * valores a usar en el shader como material, entidad, etc.
     *
     * @param poligono
     * @param x
     * @param y
     * @param siempreArriba
     */
    protected void prepararPixel(QPoligono poligono, int x, int y, boolean siempreArriba) {
        if (siempreArriba || -verticeActual.ubicacion.z < render.getFrameBuffer().getZBuffer(y, x)) {

            //flat shadding (toma la normal del plano)
            if (poligono.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE || !(poligono.smooth && (render.opciones.tipoVista >= QOpcionesRenderer.VISTA_PHONG) || render.opciones.forzarSuavizado)) {
                verticeActual.normal.copyXYZ(poligono.normalCopy);
            }

            if (render.opciones.material) {
                //Mapa de normales
                if (poligono.material != null && (poligono.material instanceof QMaterialBas && ((QMaterialBas) poligono.material).getMapaNormal() != null) //si tiene material basico y tiene mapa normal
                        && render.opciones.normalMapping) {
                    QMaterialBas material = (QMaterialBas) poligono.material;

                    currentUp.copyXYZ(up);
                    currentRight.copyXYZ(right);
                    //usando el metodo arriba e izquierda
                    currentUp.multiply((material.getMapaNormal().getNormalY(verticeActual.u, verticeActual.v) * 2 - 1) * material.getFactorNormal());
                    currentRight.multiply((material.getMapaNormal().getNormalX(verticeActual.u, verticeActual.v) * 2 - 1) * material.getFactorNormal());
                    // continua ejecucion normal
                    verticeActual.normal.multiply(material.getMapaNormal().getNormalZ(verticeActual.u, verticeActual.v) * 2 - 1);
                    verticeActual.normal.add(currentUp, currentRight);
                    verticeActual.normal.normalize();
                }

                //si tiene material pbr y tiene mapa normal
                if (poligono.material != null && (poligono.material instanceof QMaterialNodo) && render.opciones.normalMapping) {
                    verticeActual.arriba.copyXYZ(up);
                    verticeActual.derecha.copyXYZ(right);
                }
            }

            //panelclip
            try {
                if (render.getPanelClip() != null) {
//                        if (!render.getPanelClip().esVisible(QTransformar.transformarVectorInversa(new QVector3(verticeActual.x, verticeActual.y, verticeActual.z), poligono.geometria.entidad, render.getCamara()))) {
                    if (!render.getPanelClip().esVisible(QTransformar.transformarVectorInversa(verticeActual.ubicacion.getVector3(), poligono.geometria.entidad, render.getCamara()))) {
                        return;
                    }
                }
            } catch (Exception e) {
            }

            if (render.getFrameBuffer().getPixel(y, x) != null) {
                render.getFrameBuffer().getPixel(y, x).setDibujar(true);
                render.getFrameBuffer().getPixel(y, x).ubicacion.set(verticeActual.ubicacion);
                render.getFrameBuffer().getPixel(y, x).normal.copyXYZ(verticeActual.normal);
                render.getFrameBuffer().getPixel(y, x).material = poligono.material;
                render.getFrameBuffer().getPixel(y, x).poligono = poligono;
                render.getFrameBuffer().getPixel(y, x).u = verticeActual.u;
                render.getFrameBuffer().getPixel(y, x).v = verticeActual.v;
                render.getFrameBuffer().getPixel(y, x).entidad = poligono.geometria.entidad;
                render.getFrameBuffer().getPixel(y, x).arriba.copyXYZ(verticeActual.arriba);
                render.getFrameBuffer().getPixel(y, x).derecha.copyXYZ(verticeActual.derecha);
                render.dibujarPixel(x, y);
            }

            //actualiza el zBuffer
            render.getFrameBuffer().setZBuffer(y, x, -verticeActual.ubicacion.z);
        }
    }

    /**
     * Prepara el pixel - Realiza la validacion de profundidad y setea los
     * valores a usar en el shader como material, entidad, etc.
     *
     * @param primitiva
     * @param x
     * @param y
     *
     * @param siempreArriba
     */
    protected void prepararPixelLinea(QPrimitiva primitiva, int x, int y, boolean siempreArriba) {
        if (x > 0 && x < render.getFrameBuffer().getAncho() && y > 0 && y < render.getFrameBuffer().getAlto()) {
            zActual = interpolateZbyX(xDesde, zDesde, xHasta, zHasta, x, (int) render.getFrameBuffer().getAncho(), render.getCamara().camaraAncho);

            if (!testDifference(zDesde, zHasta)) {
                alfa = (zActual - zDesde) / (zHasta - zDesde);
            } else {
                alfa = xDesdePantalla == xHastaPantalla ? 0 : (float) (x - xDesdePantalla) / (float) (xHastaPantalla - xDesdePantalla);
            }
            // siempre y cuando sea menor que el zbuffer se debe dibujar. quiere decir que esta delante
            if (siempreArriba || (-zActual > 0 && -zActual < render.getFrameBuffer().getZBuffer(y, x))) {
                QMath.linear(verticeActual, alfa, verticeDesde, verticeHasta);
                // si no es suavizado se copia la normal de la cara para dibujar con Flat Shadded
                // igualmente si es tipo wire toma la normal de la cara porq no hay normal interpolada

                if (primitiva instanceof QPoligono) {
                    if (primitiva.geometria.tipo == QGeometria.GEOMETRY_TYPE_WIRE
                            || !(((QPoligono) primitiva).smooth && (render.opciones.tipoVista >= QOpcionesRenderer.VISTA_PHONG) || render.opciones.forzarSuavizado)) {
                        verticeActual.normal.copyXYZ(((QPoligono) primitiva).normalCopy);
                    }
                }

                if (render.opciones.material) {

                    //modifico la normal de acuerdo a la rugosidad del material
//                    if (poligono.material != null
//                            && (poligono.material instanceof QMaterialBas) //si tiene material basico 
//                            ) {
//
//                        QMaterialBas material = (QMaterialBas) poligono.material;
//                        verticeActual.normal.add(material.getRugosidad() * (float) Math.random(), QVector3.unitario_xyz);
//                        verticeActual.normal.normalize();
//                    }
                    //mapa de desplazamiento
                    if (primitiva.material != null
                            && (primitiva.material instanceof QMaterialBas && ((QMaterialBas) primitiva.material).getMapaDesplazamiento() != null) //si tiene material basico y tiene mapa por desplazamiento
                            ) {
                        QMaterialBas material = (QMaterialBas) primitiva.material;
                        QColor colorDesplazamiento = material.getMapaDesplazamiento().get_QARGB(verticeActual.u, verticeActual.v);
//                        QColor colorDesplazamiento = material.getMapaDesplazamiento().get_QARGB(uvActual.u, uvActual.v);
//                        float fac = (colorDesplazamiento.r + colorDesplazamiento.g + colorDesplazamiento.b) / 3.0f * material.getFactorNormal();
                        float fac = (colorDesplazamiento.r + colorDesplazamiento.g + colorDesplazamiento.b) / 3.0f;
                        QVector3 tmp = new QVector3(verticeActual);
                        tmp.add(verticeActual.normal.clone().multiply(fac * 2 - 1));
//                        tmp.add(verticeActual.normal.clone().multiply(fac ));
                        verticeActual.setXYZ(tmp.x, tmp.y, tmp.z);
                    }

                    //Mapa de normales
                    if (primitiva.material != null
                            && (primitiva.material instanceof QMaterialBas && ((QMaterialBas) primitiva.material).getMapaNormal() != null) //si tiene material basico y tiene mapa normal
                            && render.opciones.normalMapping) {
                        QMaterialBas material = (QMaterialBas) primitiva.material;

                        currentUp.copyXYZ(up);
                        currentRight.copyXYZ(right);
                        //anteriormente
//                            currentUp.multiply((((rgb >> 8) & 0xFF) / 255.0f - .5f) * poligono.material.getFactorNormal());
//                            currentRight.multiply((((rgb >> 16) & 0xFF) / 255.0f - .5f) * poligono.material.getFactorNormal());
                        //usando el metodo arriba e izquierda
                        currentUp.multiply((material.getMapaNormal().getNormalY(verticeActual.u, verticeActual.v) * 2 - 1) * material.getFactorNormal());
                        currentRight.multiply((material.getMapaNormal().getNormalX(verticeActual.u, verticeActual.v) * 2 - 1) * material.getFactorNormal());
                        // continua ejecucion normal
                        verticeActual.normal.multiply(material.getMapaNormal().getNormalZ(verticeActual.u, verticeActual.v) * 2 - 1);
                        verticeActual.normal.add(currentUp, currentRight);
                        verticeActual.normal.normalize();
                    }
                    //si tiene material pbr y tiene mapa normal
                    if (primitiva.material != null && (primitiva.material instanceof QMaterialNodo) && render.opciones.normalMapping) {
                        verticeActual.arriba.copyXYZ(up);
                        verticeActual.derecha.copyXYZ(right);
                    }
                }

                //panelclip
                try {
                    if (render.getPanelClip() != null) {
//                        if (!render.getPanelClip().esVisible(QTransformar.transformarVectorInversa(new QVector3(verticeActual.x, verticeActual.y, verticeActual.z), poligono.geometria.entidad, render.getCamara()))) {
                        if (!render.getPanelClip().esVisible(QTransformar.transformarVectorInversa(verticeActual.ubicacion.getVector3(), primitiva.geometria.entidad, render.getCamara()))) {
                            return;
                        }
                    }
                } catch (Exception e) {
                }

                //actualiza le buffer de QPixeles
                if (render.getFrameBuffer().getPixel(y, x) != null) {
                    render.getFrameBuffer().getPixel(y, x).setDibujar(true);
                    render.getFrameBuffer().getPixel(y, x).ubicacion.set(verticeActual.ubicacion);
                    render.getFrameBuffer().getPixel(y, x).normal.copyXYZ(verticeActual.normal);
                    render.getFrameBuffer().getPixel(y, x).material = primitiva.material;
                    render.getFrameBuffer().getPixel(y, x).poligono = primitiva;
                    render.getFrameBuffer().getPixel(y, x).u = verticeActual.u;
                    render.getFrameBuffer().getPixel(y, x).v = verticeActual.v;
                    render.getFrameBuffer().getPixel(y, x).entidad = primitiva.geometria.entidad;
                    render.getFrameBuffer().getPixel(y, x).arriba.copyXYZ(verticeActual.arriba);
                    render.getFrameBuffer().getPixel(y, x).derecha.copyXYZ(verticeActual.derecha);
                    render.dibujarPixel(x, y);
                }
                //actualiza el zBuffer
                render.getFrameBuffer().setZBuffer(y, x, -zActual);
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

        prepararPixelLinea(primitiva, x, y, false);

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
                prepararPixelLinea(primitiva, x, y, false);
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
                prepararPixelLinea(primitiva, x, y, false);
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
