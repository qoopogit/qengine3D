/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.reflexiones;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.escena.QCamara;
import net.qoopo.engine3d.core.escena.QEscena;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.util.QGlobal;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.QRender;

/**
 * Genera y gestiona un mapa cúbico * Permite la creación de un mapa cúbico
 * dinámico * Permite la generación de una imagen HDRI a partir de texturas para
 * cada lado
 *
 * @author alberto
 */
public class QMapaCubo extends QComponente {

    public static final int FORMATO_MAPA_CUBO = 1;
    public static final int FORMATO_MAPA_HDRI = 2;

    /**
     * Se realiza un Mapeo cubico, 6 texturas uno por cada lado del cubo
     */
    transient private QMotorRender render;
    private QVector3[] direcciones;
    private QTextura[] texturas;
    private QVector3[] direccionesArriba;

    private int tipoSalida = FORMATO_MAPA_CUBO;
//    private int tipoSalida = FORMATO_MAPA_HDRI;
    private QTextura texturaSalida;// esta textura es la union de las 6 texturas renderizadas en el formato de cubemap o HDRI

    public String[] nombres = {"Arriba", "Abajo", "Frente", "Atras", "Izquierda", "Derecha"};

    private int tamanio;
    private boolean actualizar = true;
    private Dimension dimensionLado;
    private boolean dinamico;

    // variables para el panel que lo contruye
    private float factorReflexion = 1.0f;
    private float indiceRefraccion = 1.52f;

    public QMapaCubo(int resolucion) {
        direcciones = new QVector3[6];

        //-------------------------------------
        direcciones[1] = new QVector3(0, 1, 0);  //arriba
        direcciones[0] = new QVector3(0, -1, 0); //abajo
        direcciones[3] = new QVector3(0, 0, 1);  //adelante
        direcciones[2] = new QVector3(0, 0, -1); //atras
        direcciones[5] = new QVector3(-1, 0, 0); //izquierda
        direcciones[4] = new QVector3(1, 0, 0);  //derecha
        //------------------------------------- 
        direccionesArriba = new QVector3[6];
        direccionesArriba[0] = new QVector3(0, 0, -1); //arriba
        direccionesArriba[1] = new QVector3(0, 0, 1); //abajo
        direccionesArriba[2] = new QVector3(0, 1, 0); //adelante
        direccionesArriba[3] = new QVector3(0, 1, 0); //atras
        direccionesArriba[4] = new QVector3(0, 1, 0); //izquierda
        direccionesArriba[5] = new QVector3(0, 1, 0); //derecha

        texturas = new QTextura[6];
        for (int i = 0; i < 6; i++) {
            texturas[i] = new QTextura();
            texturas[i].setSignoX(-1);//es reflejo
        }

        render = new QRender(QEscena.INSTANCIA, null, resolucion, resolucion);
        render.setEfectosPostProceso(null);
        render.setMostrarEstadisticas(false);
        render.setRenderReal(false);
        render.setCamara(new QCamara());
        render.getCamara().setFOV((float) Math.toRadians(90.0f));//angulo de visión de 90 grados        
//        render.cambiarShader(3);//el shader de textura, un shader simple                    
//        render.cambiarShader(4);//el shader de textura con iluminacion
//        render.cambiarShader(5);//el shader con sombras
//        render.cambiarShader(6);//el shader full
        texturaSalida = new QTextura();
        construir(resolucion);
    }

    /**
     * Construye un mapa cubico estatico
     *
     * @param tipo
     * @param positivoY
     * @param positivoX
     * @param positivoZ
     * @param negativoY
     * @param negativoX
     * @param negativoZ
     */
    public QMapaCubo(int tipo, QTextura positivoX, QTextura positivoY, QTextura positivoZ, QTextura negativoX, QTextura negativoY, QTextura negativoZ) {
        this.tamanio = positivoX.getAncho();
        direcciones = new QVector3[6];
        texturas = new QTextura[6];
        texturas[0] = positivoY;
        texturas[1] = negativoY;
        texturas[2] = positivoZ;
        texturas[3] = negativoZ;
        texturas[4] = negativoX;
        texturas[5] = positivoX;
        texturaSalida = new QTextura();
        dimensionLado = null;
        dinamico = false;
        tipoSalida = tipo;
        actualizarTextura();
        direccionesArriba = new QVector3[6];
    }

    public void construir(int tamanio) {
        this.tamanio = tamanio;
        render.opciones.setForzarResolucion(true);
        render.opciones.setAncho(tamanio);
        render.opciones.setAlto(tamanio);
        render.opciones.setNormalMapping(false);
        render.opciones.setVerCarasTraseras(false);
        render.opciones.setSombras(false);
        render.opciones.setDibujarLuces(false);
        render.opciones.setNormalMapping(false);
        render.resize();
        dimensionLado = new Dimension(tamanio, tamanio);
        dinamico = false;
        actualizar = true;// obliga a actualizar el mapa
    }

    public void aplicar(int tipo, float factorMetalico, float indiceRefraccion) {
        setTipoSalida(tipo);
        setFactorReflexion(factorMetalico);
        setIndiceRefraccion(indiceRefraccion);
        List<QMaterialBas> lst = new ArrayList<>();
        //ahora  recorro todos los materiales del objeto y le agrego la textura de reflexion
        for (QComponente componente : entidad.getComponentes()) {
            if (componente instanceof QGeometria) {
                for (QPrimitiva poligono : ((QGeometria) componente).listaPrimitivas) {
                    if (poligono.material instanceof QMaterialBas) {
                        if (!lst.contains((QMaterialBas) poligono.material)) {
                            lst.add((QMaterialBas) poligono.material);
                        }
                    }
                }
            }
        }

        QProcesadorTextura proc = new QProcesadorSimple(getTexturaSalida());
        for (QMaterialBas mat : lst) {
            mat.setMapaEntorno(proc);
            mat.setMetalico(factorMetalico);
            mat.setIndiceRefraccion(indiceRefraccion);
            mat.setReflexion(factorMetalico > 0.0f);
            mat.setRefraccion(indiceRefraccion > 0.0f);
            mat.setTipoMapaEntorno(getTipoSalida());//mapa cubico o HDRI
        }
        actualizar = true;// obliga a actualizar el mapa
    }

    /**
     * Actualiza los mapas de acuerdo a la posicion del cubo
     *
     * @param posicion
     */
    public void actualizarMapa(QVector3 posicion) {
        for (int i = 0; i < 6; i++) {
            render.getCamara().lookAt(posicion, direcciones[i], direccionesArriba[i]);
//            render.setTexturaSalida(texturas[i]);
            render.update();
            texturas[i].cargarTextura(render.getFrameBuffer().getRendered());

        }
        actualizarTextura();
    }

    /**
     * Actualiza el mapa. La actualizacion es controlada con las variables <<Actualizar>> o <<Dinamico>>
     *
     * @param mainRender
     */
    public void actualizarMap(QMotorRender mainRender) {
        if (!mainRender.opciones.isMaterial()) {
            return;
        }
        if (mainRender.getFrameBuffer() == null) {
            return;
        }
        if (dinamico || actualizar) {
            boolean dibujar = entidad.isRenderizar();
            try {
                //seteo para q no se dibuje a la entidad
                entidad.setRenderizar(false);
                actualizarMapa(entidad.getMatrizTransformacion(QGlobal.tiempo).toTranslationVector());
                actualizar = false;
            } finally {
                //seteo para q se dibuje la entidad en los demas renderes
                entidad.setRenderizar(dibujar);
            }
        }
    }

    /**
     * actualiza la textura de salida con loas 6 texturas renderizadas
     */
    private void actualizarTextura() {
        switch (tipoSalida) {
            case FORMATO_MAPA_CUBO:
            default:
                texturaSalida.cargarTextura(convertirMapaCubo());
                break;
            case FORMATO_MAPA_HDRI:
                texturaSalida.cargarTextura(convertirHDRI(convertirMapaCubo()));
                break;
        }
    }

    /**
     * Crea una imagen de salida en formato mapa cubico
     *
     * @return
     */
    private BufferedImage convertirMapaCubo() {
        BufferedImage img = new BufferedImage(tamanio * 4, tamanio * 3, BufferedImage.TYPE_INT_ARGB);
//        img.getGraphics().setColor(Color.WHITE);
//        img.getGraphics().fillRect(0, 0, img.getWidth(),img.getHeight());
        //formato primera imagen 
        //https://en.wikipedia.org/wiki/Cube_mapping
//        img.getGraphics().drawImage(texturas[0].getImagen(), ancho, 0, null);  //arriba
//        img.getGraphics().drawImage(texturas[3].getImagen(), 0, alto, null);//atras
//        img.getGraphics().drawImage(texturas[4].getImagen(), ancho, alto, null);//izquierda
//        img.getGraphics().drawImage(texturas[2].getImagen(), ancho * 2, alto, null);//frente
//        img.getGraphics().drawImage(texturas[5].getImagen(), ancho * 3, alto, null);//derecha
//        img.getGraphics().drawImage(texturas[1].getImagen(), ancho, alto * 2, null);//abajo
//       formato acorde par ala generacion de l hdri
//https://i.stack.imgur.com/Q1SO4.png
//https://stackoverflow.com/questions/34250742/converting-a-cubemap-into-equirectangular-panorama
        // segunda imagen
        //  //https://en.wikipedia.org/wiki/Cube_mapping
        // el orden de pintura es importante porq evita las lineas negras que tengo en los frames sobreescribiendolas, debo corregir esas lineas negras        
        img.getGraphics().drawImage(texturas[1].getImagen(dimensionLado), tamanio, tamanio * 2 - 4, null);//abajo
        img.getGraphics().drawImage(texturas[3].getImagen(dimensionLado), tamanio * 3 - 5, tamanio - 1, null);//atras 3
        img.getGraphics().drawImage(texturas[5].getImagen(dimensionLado), tamanio * 2 - 2, tamanio - 1, null);//derecha 5
        img.getGraphics().drawImage(texturas[2].getImagen(dimensionLado), tamanio, tamanio - 1, null);//frente
        img.getGraphics().drawImage(texturas[4].getImagen(dimensionLado), 0, tamanio - 2, null); //izquierda 4
        img.getGraphics().drawImage(texturas[0].getImagen(dimensionLado), tamanio, 0, null);  //arriba 0
        return img;
    }

    /**
     * Crea una imagen HDRI desde la imagen del mapa de cubo
     * https://stackoverflow.com/questions/34250742/converting-a-cubemap-into-equirectangular-panorama
     *
     * @param cubeMap
     * @return
     */
    private BufferedImage convertirHDRI(BufferedImage cubeMap) {
        BufferedImage salida = new BufferedImage(cubeMap.getWidth(), cubeMap.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        BufferedImage salida = new BufferedImage(tamanio * 4, tamanio * 3, BufferedImage.TYPE_INT_ARGB);
        float u, v; //Normalised texture coordinates, from 0 to 1, starting at lower left corner
        float phi, theta; //Polar coordinates
        int anchoCara, altoCara;

//        anchoCara = cubeMap.getWidth() / 4; //4 horizontal faces
//        altoCara = cubeMap.getHeight() / 3; //3 vertical faces
        anchoCara = tamanio; //4 horizontal faces
        altoCara = tamanio; //3 vertical faces

        for (int j = 0; j < salida.getHeight(); j++) {
            //Rows start from the bottom
            v = 1 - ((float) j / (float) salida.getHeight());
            theta = v * QMath.PI;

            for (int i = 0; i < salida.getWidth(); i++) {
                //Columns start from the left
                u = ((float) i / (float) salida.getWidth());
                phi = u * 2 * QMath.PI;

                float x, y, z; //Unit 

                x = QMath.sin(phi) * QMath.sin(theta) * -1;
                y = QMath.cos(theta);
                z = QMath.cos(phi) * QMath.sin(theta) * -1;

//                x = QMath.cos(phi) * QMath.cos(theta);
//                y = QMath.sin(phi);
//                z = QMath.cos(phi) * QMath.sin(theta);
                float xa, ya, za;
                float a;

                a = Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));

                //Vector Parallel to the unit vector that lies on one of the cube faces
                xa = x / a;
                ya = y / a;
                za = z / a;

                int color;
                int xPixel, yPixel;
                int xOffset, yOffset;

                if (xa == 1) {
                    //Right
                    xPixel = (int) ((((za + 1f) / 2f) - 1f) * anchoCara);
                    xOffset = 2 * anchoCara; //Offset
                    yPixel = (int) ((((ya + 1f) / 2f)) * altoCara);
                    yOffset = altoCara; //Offset
                } else if (xa == -1) {
                    //Left
                    xPixel = (int) ((((za + 1f) / 2f)) * anchoCara);
                    xOffset = 0;
                    yPixel = (int) ((((ya + 1f) / 2f)) * altoCara);
                    yOffset = altoCara;
                } else if (ya == 1) {
                    //Up
                    xPixel = (int) ((((xa + 1f) / 2f)) * anchoCara);
                    xOffset = anchoCara;
                    yPixel = (int) ((((za + 1f) / 2f) - 1f) * altoCara);
                    yOffset = 2 * altoCara;
                } else if (ya == -1) {
                    //Down
                    xPixel = (int) ((((xa + 1f) / 2f)) * anchoCara);
                    xOffset = anchoCara;
                    yPixel = (int) ((((za + 1f) / 2f)) * altoCara);
                    yOffset = 0;
                } else if (za == 1) {
                    //Front
                    xPixel = (int) ((((xa + 1f) / 2f)) * anchoCara);
                    xOffset = anchoCara;
                    yPixel = (int) ((((ya + 1f) / 2f)) * altoCara);
                    yOffset = altoCara;
                } else if (za == -1) {
                    //Back
                    xPixel = (int) ((((xa + 1f) / 2f) - 1f) * anchoCara);
                    xOffset = 3 * anchoCara;
                    yPixel = (int) ((((ya + 1f) / 2f)) * altoCara);
                    yOffset = altoCara;
                } else {
                    xPixel = 0;
                    yPixel = 0;
                    xOffset = 0;
                    yOffset = 0;
                }

                xPixel = Math.abs(xPixel);
                yPixel = Math.abs(yPixel);

                xPixel += xOffset;
                yPixel += yOffset;

                //desde 1 hasta el ancho -1
                xPixel = QMath.clamp(xPixel, 0, cubeMap.getWidth() - 1);
                yPixel = QMath.clamp(yPixel, 0, cubeMap.getHeight() - 1);

                try {
                    color = cubeMap.getRGB(xPixel, yPixel);
                    //salida.setRGB(i, j, color);
                    //invierto la coordenada X d
                    salida.setRGB(salida.getWidth() - i, j, color);
                } catch (Exception e) {

                }
            }
        }
        return salida;
    }

    public QTextura[] getTexturas() {
        return texturas;
    }

    public QTextura getTextura(int i) {
        return texturas[i];
    }

    public QTextura getTexturaSalida() {
        return texturaSalida;
    }

    public int getTipoSalida() {
        return tipoSalida;
    }

    public void setTipoSalida(int tipoSalida) {
        this.tipoSalida = tipoSalida;
    }

    @Override
    public void destruir() {
        texturaSalida = null;
        render = null;
        texturas = null;
    }

    public boolean isActualizar() {
        return actualizar;
    }

    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

    public float getFactorReflexion() {
        return factorReflexion;
    }

    public void setFactorReflexion(float factorReflexion) {
        this.factorReflexion = factorReflexion;
    }

    public float getIndiceRefraccion() {
        return indiceRefraccion;
    }

    public void setIndiceRefraccion(float indiceRefraccion) {
        this.indiceRefraccion = indiceRefraccion;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }

}
