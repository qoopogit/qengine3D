/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.AABB;
import java.util.List;
import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.componentes.geometria.QGeometria;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas.QColisionEsfera;
import net.qoopo.engine3d.core.math.QVector3;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.componentes.transformacion.QTransformacion;

/**
 *
 * @author alberto
 */
public class QComponenteColision extends QComponente {

    public static final int TIPO_CONTENEDOR_ESFERA = 1;
    public static final int TIPO_CONTENEDOR_AABB = 2;
    public static final int TIPO_COMPLEJO_TRIANGULO = 3;

    public int tipoContenedorColision = TIPO_CONTENEDOR_ESFERA;

    public boolean tieneColision;//indica si entro en verificarColision

    //Se divide al objeto en partes mas pequeñas y se calcula un formaColision para esa parte y asi se tiene
    //varios contenedores mas acordes a la silueta del objeto
    public List<QFormaColision> listaContenedores;
    public QFormaColision formaColision = null; //Caja invisible que envuelve al objeto. Se usa para detectar verificarColision contra la caja que envuelve a otro objeto

    public QComponenteColision() {

    }

    public void setFormaColision(QFormaColision contenedor) {
        this.formaColision = contenedor;
    }

    public boolean verificarColision(QComponenteColision otro) {
        //PRIMER PASO VERIFICAR COLISION CON LOS CONTENEDORES
        // AYUDA A DESCARTAR RAPIDAMENTE OBJETOS NO CERCANOS
        if (formaColision != null && otro != null) {
            tieneColision = this.formaColision.verificarColision(otro.formaColision);
        }

        return tieneColision;
    }

    public void crearContenedorColision(QGeometria geometria, QTransformacion transformacion) {
        switch (tipoContenedorColision) {
            case TIPO_CONTENEDOR_ESFERA:
                crearEsfera(geometria, transformacion);
                break;
            case TIPO_CONTENEDOR_AABB:
                crearAABB(geometria, transformacion);
                break;
        }
    }

    /**
     * Crea una figura contenedora tipo AABB
     *
     * @param geometria
     * @param transformacion
     */
    public void crearAABB(QGeometria geometria, QTransformacion transformacion) {
        this.formaColision = new AABB(geometria.listaVertices[0].clone(), geometria.listaVertices[0].clone());

        AABB tmp = (AABB) formaColision;
//        this.formaColision = new AABB(this.listaVertices[0], this.listaVertices[0]);

        //el siguiente metodo funciona para todas las formas
        //pero seria mejro que existan clases que sobrecarguen este metodo de acuerdo a una geometria predefinida
        for (QVertice vertice : geometria.listaVertices) {
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

        //actualizo la coordenads con la posicion del objeto en el espacio tambien
        tmp.aabMinimo.ubicacion.x += transformacion.getTraslacion().x;
        tmp.aabMinimo.ubicacion.y += transformacion.getTraslacion().y;
        tmp.aabMinimo.ubicacion.z += transformacion.getTraslacion().z;

        tmp.aabMaximo.ubicacion.x += transformacion.getTraslacion().x;
        tmp.aabMaximo.ubicacion.y += transformacion.getTraslacion().y;
        tmp.aabMaximo.ubicacion.z += transformacion.getTraslacion().z;

//        System.out.println("AAAB Calculado  " + ((AABB) formaColision).aabMinimo + " -  " + ((AABB) formaColision).aabMaximo);
    }

    public void crearEsfera(QGeometria geometria, QTransformacion transformacion) {
        QVertice centro = null;
        float radio = 0;
        //primero calculamos el rectangulo AABB para obtener el centro y radio
        crearAABB(geometria, transformacion);
        QVector3 tmp = new QVector3(((AABB) formaColision).aabMinimo, ((AABB) formaColision).aabMaximo);

        radio = tmp.length() * 0.5f;// el radio es igual a la mitad de la diagonal de cubo

        tmp.multiply(0.5f);
        centro = new QVertice(tmp.x, tmp.y, tmp.z, 1);

//        this.formaColision = new QEsferaContenedor(centro, radio);
        this.formaColision = new QColisionEsfera(radio);
    }

    @Override
    public void destruir() {
        if (listaContenedores != null) {
            listaContenedores.clear();
            listaContenedores = null;
        }
    }

}
