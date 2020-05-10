/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura.procesador;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import net.qoopo.engine3d.core.textura.QTextura;
import net.qoopo.engine3d.core.math.QColor;

/**
 * Este procesador es un proxy simple sin aplicar ningun proceso
 *
 * @author alberto
 */
public class QProcesadorAtlas extends QProcesadorTextura {

    private QTextura texturaAtlas;
    private QTextura textura;

    private int filas = 1;
    private int columnas = 1;

    //la fila y columna de la textura que se ira a tomar
    private int fila = 1;
    private int col = 1;

    public QProcesadorAtlas(QTextura textura, int filas, int columnas) {
        this.texturaAtlas = textura;
        textura = new QTextura();
        this.filas = filas;
        this.columnas = columnas;
    }

    public int getFilas() {
        return filas;
    }

    public void setFilas(int filas) {
        this.filas = filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public void setColumnas(int columnas) {
        this.columnas = columnas;
    }

    public int getFila() {
        return fila;
    }

    public void setFila(int fila) {
        this.fila = fila;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    @Override
    public void procesar() {
        Dimension size = new Dimension(texturaAtlas.getAncho(), texturaAtlas.getAlto());
        int anchoCelda = size.width / columnas;
        int altoCelda = size.height / filas;
        int x = col * anchoCelda;
        int y = fila * altoCelda;
        textura.cargarTextura(texturaAtlas.getImagen(size).getSubimage(x, y, anchoCelda, altoCelda));

    }

    public QTextura getTextura() {
        return textura;
    }

    public void setTextura(QTextura textura) {
        this.textura = textura;
    }

    @Override
    public int get_ARGB(float x, float y) {
        return textura.getARGB(x, y);
    }

    @Override
    public QColor get_QARGB(float x, float y) {
        return textura.getQColor(x, y);
    }

    @Override
    public float getNormalX(float x, float y) {
        return textura.getNormalX(x, y);
    }

    @Override
    public float getNormalY(float x, float y) {
        return textura.getNormalY(x, y);
    }

    @Override
    public float getNormalZ(float x, float y) {
        return textura.getNormalZ(x, y);
    }

    @Override
    public BufferedImage getTexture(Dimension size) {
        return textura.getImagen(size);
    }

    @Override
    public BufferedImage getTexture() {
        return textura.getImagen();
    }

    @Override
    public void destruir() {
        if (texturaAtlas != null) {
            texturaAtlas.destruir();
            texturaAtlas = null;
        }
    }

    @Override
    public void setMuestrasU(float muestras) {
    }

    @Override
    public void setMuestrasV(float muestras) {
    }

}
