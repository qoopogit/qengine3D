/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.primitivas;

import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;

/**
 * Define una AABB (Axis aligned bounding boxes) Es una caja virtual que
 * envuelve un objeto y es utilizada para el detector de colisiones
 *
 * @author alberto
 */
public class AABB extends QFormaColision {

    public QVertice aabMinimo;
    public QVertice aabMaximo;

    public AABB() {
//        aabMinimo= new QVertice();
//        aabMaximo= new QVertice();
    }

    public AABB(QVertice aabMinimo, QVertice aabMaximo) {
        this.aabMinimo = aabMinimo;
        this.aabMaximo = aabMaximo;
    }

    @Override
    public boolean verificarColision(QFormaColision otro) {

        boolean b = false;

//        System.out.println("Preguntando verificarColision AABB_1");
//        System.out.println("     minimo " + aabMinimo.toString());
//        System.out.println("     maximo " + aabMaximo.toString());
//
//        System.out.println("  AABB_2");
//        System.out.println("     minimo " + otro.aabMinimo.toString());
//        System.out.println("     maximo " + otro.aabMaximo.toString());
//        System.out.println("");
//        System.out.println("");
//validar contra otro AABB
        if (otro instanceof AABB) {
            b = this.aabMaximo.ubicacion.x > ((AABB) otro).aabMinimo.ubicacion.x
                    && this.aabMinimo.ubicacion.x < ((AABB) otro).aabMaximo.ubicacion.x
                    && this.aabMaximo.ubicacion.y > ((AABB) otro).aabMinimo.ubicacion.y
                    && this.aabMinimo.ubicacion.y < ((AABB) otro).aabMaximo.ubicacion.y
                    && this.aabMaximo.ubicacion.z > ((AABB) otro).aabMinimo.ubicacion.z
                    && this.aabMinimo.ubicacion.z < ((AABB) otro).aabMaximo.ubicacion.z;
        }

        return b;

    }

    @Override
    public void destruir() {

    }
}
