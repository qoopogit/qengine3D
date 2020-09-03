/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.terreno;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPrimitiva;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorSimple;
import net.qoopo.engine3d.core.util.QLogger;
import net.qoopo.engine3d.core.util.QUtilNormales;

/**
 *
 * @author alberto
 */
public class QTerreno extends QComponente {

//    private static final int MAX_COLOR = 255;
    private static final int MAX_COLOR = 255*255*255;

    // arreglo  que tiene la altura, para averigurar la altura en tiempo de ejecucion
    private float[][] altura;

    private float ancho = 0;
    private float largo = 0;
    private float inicioX = 0;
    private float inicioZ = 0;

    private int planosAncho = 0;
    private int planosLargo = 0;
    private float maxY;
    private float minY;

    private QMaterialBas material = new QMaterialBas("terreno");

    public QTerreno() {
    }

    public float getAltura(float x, float z) {
        try {
            float terrenoX = x - this.inicioX;
            float terrenoZ = z - this.inicioZ;
//        if (terrenoX < 0 || terrenoX > ancho) {
//            return 0;
//        }
//        if (terrenoZ < 0 || terrenoZ > largo) {
//            return 0;
//        }

            float gridSquareSize = ancho / ((float) altura.length - 1);
            int gridX = (int) Math.floor(terrenoX / gridSquareSize);
            int gridZ = (int) Math.floor(terrenoZ / gridSquareSize);
            if (gridX < 0 || gridX > altura.length - 1) {
                return 0;
            }
            if (gridZ < 0 || gridZ > altura.length - 1) {
                return 0;
            }
            float xCoord = (terrenoX % gridSquareSize) / gridSquareSize;
            float zCoord = (terrenoZ % gridSquareSize) / gridSquareSize;
            if (xCoord <= (1 - zCoord)) {
                return barryCentric(new QVector3(0, altura[gridX][gridZ], 0), new QVector3(1, altura[gridX + 1][gridZ], 0), new QVector3(0, altura[gridX][gridZ + 1], 1), new QVector3(xCoord, zCoord, 0));
            } else {
                return barryCentric(new QVector3(1, altura[gridX + 1][gridZ], 0), new QVector3(1, altura[gridX + 1][gridZ + 1], 1), new QVector3(0, altura[gridX][gridZ + 1], 1), new QVector3(xCoord, zCoord, 0));
            }
        } catch (Exception e) {
            return Float.NEGATIVE_INFINITY;
        }

    }

    public static float barryCentric(QVector3 p1, QVector3 p2, QVector3 p3, QVector3 pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    public void generar(File archivo, float tamanioCelda, float minY, float maxY, QTextura textura, int offset) {
        try {
            BufferedImage imagen = ImageIO.read(archivo);
            generar(imagen, tamanioCelda, minY, maxY, textura, offset);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void generar(BufferedImage imagen, float tamanioCelda, float minY, float maxY, QTextura textura, int offset) {
        //Geometria
        if (offset <= 0) {
            offset = 1;
        }
        this.minY = minY;
        this.maxY = maxY;

        QGeometria geometria = new QGeometria();
        geometria.nombre = "Terreno";

        //se aplica la textura usando un procesador simple
        if (textura != null) {
            material.setMapaDifusa(new QProcesadorSimple(textura));
        } else {
            material.setMapaDifusa(null);
        }

        material.setColorDifusa(new QColor(1, 139f / 255f, 99f / 255f, 55f / 255f));
        material.setSpecularExponent(10000);
        try {

            ancho = (imagen.getWidth() - 1) * tamanioCelda;
            largo = (imagen.getHeight() - 1) * tamanioCelda;

            planosAncho = (int) ((imagen.getWidth() - 1)) / offset;
            planosLargo = (int) ((imagen.getHeight() - 1)) / offset;

            inicioX = -((float) imagen.getWidth() * tamanioCelda) / 2;
            inicioZ = -((float) imagen.getHeight() * tamanioCelda) / 2;

            QLogger.info("Iniciando mapa de altura " + planosAncho + "x" + planosLargo);
            altura = new float[planosAncho][planosLargo];

            float alturaVertice = 0;
            int j = 0, i = 0;

            for (int z = 0; z < imagen.getHeight() - 1; z += offset) {
                i = 0;
                for (int x = 0; x < imagen.getWidth() - 1; x += offset) {
                    if (x > imagen.getWidth()) {
                        x = imagen.getWidth();
                    }

                    if (z > imagen.getHeight()) {
                        z = imagen.getHeight();
                    }

                    alturaVertice = getAltura(imagen.getRGB(x, z), minY, maxY);
                    try {
                        altura[i][j] = alturaVertice;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    geometria.agregarVertice(inicioX + x * tamanioCelda,
                            alturaVertice,
                            inicioZ + z * tamanioCelda,
                            //Coordenada de texturas
                            (float) x / (float) imagen.getWidth(), (float) z / (float) imagen.getHeight());

                    i++;
                }
                j++;
            }

            QLogger.info("..coordenadas generadas=" + geometria.listaVertices.length);

            for (j = 0; j < planosLargo - 1; j++) {
                for (i = 0; i < planosAncho - 1; i++) {
                    geometria.agregarPoligono(material,
                            j * planosAncho + i,
                            j * planosAncho + planosAncho + i,
                            j * planosAncho + i + 1);

                    geometria.agregarPoligono(material,
                            j * planosAncho + i + 1,
                            j * planosAncho + planosAncho + i,
                            j * planosAncho + planosAncho + i + 1);
                }
            }
            QLogger.info("..Triángulos generados =" + geometria.listaPrimitivas.length);
            geometria = QUtilNormales.calcularNormales(geometria);
            //el objeto es suavizado

            for (QPrimitiva face : geometria.listaPrimitivas) {
                ((QPoligono) face).smooth = true;
            }

        } catch (Exception ex) {
            QLogger.info("error al generar");
            ex.printStackTrace();
        }
        QLogger.info("fin de generacion");
        entidad.agregarComponente(geometria);
    }

    /**
     * Devuelve una altura al momento de generar el terreno
     * @param rgb
     * @param minY
     * @param maxY
     * @return 
     */
    private static float getAltura(int rgb, float minY, float maxY) {
        int canalr = rgb;
//        int canalr = (rgb >> 16) & 0xFF;
//        if (canalr > 255) {
//            QLogger.info("ES MAYOR QUE 255");
//        }
        return minY + Math.abs(maxY - minY) * ((float) canalr / (float) MAX_COLOR);
    }

    public float[][] getAltura() {
        return altura;
    }

    public void setAltura(float[][] altura) {
        this.altura = altura;
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

    public float getLargo() {
        return largo;
    }

    public void setLargo(float largo) {
        this.largo = largo;
    }

    public float getInicioX() {
        return inicioX;
    }

    public void setInicioX(float inicioX) {
        this.inicioX = inicioX;
    }

    public float getInicioZ() {
        return inicioZ;
    }

    public void setInicioZ(float inicioZ) {
        this.inicioZ = inicioZ;
    }

    public int getPlanosAncho() {
        return planosAncho;
    }

    public void setPlanosAncho(int planosAncho) {
        this.planosAncho = planosAncho;
    }

    public int getPlanosLargo() {
        return planosLargo;
    }

    public void setPlanosLargo(int planosLargo) {
        this.planosLargo = planosLargo;
    }

    public float[][] getMapaAltura() {
        return altura;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    @Override
    public void destruir() {
    }

}
