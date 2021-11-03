/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.geometria.primitivas.formas;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.qoopo.engine3d.componentes.geometria.primitivas.QForma;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.util.QUtilNormales;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.mapeo.QMaterialUtil;

/**
 *
 * @author alberto
 */
public class QMalla extends QForma {

    public static final int EJE_Z = 1;
    public static final int EJE_Y = 2;
    private float ancho;
    private float largo;

    private float delta;
    private float delta2;

    private int eje = EJE_Z;

    public QMalla(float ancho, float largo, float seccionesAncho, float seccionesLargo) {
        nombre = "Malla";
        material = new QMaterialBas("Malla");
        this.ancho = ancho;
        this.largo = largo;
        this.delta = ancho / seccionesAncho;
        this.delta2 = largo / seccionesLargo;
        construir();
    }

    public QMalla(boolean wire, float ancho, float largo, float seccionesAncho, float seccionesLargo) {
        nombre = "Malla";
        this.ancho = ancho;
        material = new QMaterialBas("Malla");
        this.largo = largo;
        this.delta = ancho / seccionesAncho;
        this.delta2 = largo / seccionesLargo;
        if (wire) {
            tipo = QGeometria.GEOMETRY_TYPE_WIRE;
        }
        construir();
    }

    public QMalla(int eje, float ancho, float largo, float seccionesAncho, float seccionesLargo) {
        nombre = "Malla";
        material = new QMaterialBas("Malla");
        this.ancho = ancho;
        this.largo = largo;
        this.delta = ancho / seccionesAncho;
        this.delta2 = largo / seccionesLargo;
        this.eje = eje;
        construir();
    }

    public QMalla(int eje, boolean wire, float ancho, float largo, float seccionesAncho, float seccionesLargo) {
        nombre = "Malla";
        this.ancho = ancho;
        material = new QMaterialBas("Malla");
        this.largo = largo;
        this.delta = ancho / seccionesAncho;
        this.delta2 = largo / seccionesLargo;
        this.eje = eje;
        if (wire) {
            tipo = QGeometria.GEOMETRY_TYPE_WIRE;
        }
        construir();
    }

    @Override
    public void construir() {
        eliminarDatos();

        switch (eje) {
            case EJE_Z:

                for (float z = -largo / 2; z < largo / 2; z += delta2) {
                    for (float x = -ancho / 2; x < ancho / 2; x += delta) {
                        this.agregarVertice(x, 0, z,
                                //texturas uv
                                ((float) x + ancho / 2) / ancho, 1.0f - ((float) z + largo / 2) / largo
                        );

                    }
                }
                break;
            case EJE_Y:

                for (float z = -largo / 2; z < largo / 2; z += delta2) {
                    for (float x = -ancho / 2; x < ancho / 2; x += delta) {
                        this.agregarVertice(x, z, 0,
                                //texturas uv
                                ((float) x + ancho / 2) / ancho, ((float) z + largo / 2) / largo
                        );

                    }
                }
                break;
        }

        int planosAncho = (int) (ancho / delta);
        int planosLargo = (int) (largo / delta2);

        for (int j = 0; j < planosLargo - 1; j++) {
            for (int i = 0; i < planosAncho - 1; i++) {
                try {
                    this.agregarPoligono(material, j * planosAncho + i, j * planosAncho + planosAncho + i, j * planosAncho + i + 1);
                    this.agregarPoligono(material, j * planosAncho + i + 1, j * planosAncho + planosAncho + i, j * planosAncho + planosAncho + i + 1);
                } catch (Exception ex) {
                    Logger.getLogger(QMalla.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        QUtilNormales.calcularNormales(this);
        QMaterialUtil.suavizar(this, true);
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

    public float getTamanio() {
        return delta;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

}
