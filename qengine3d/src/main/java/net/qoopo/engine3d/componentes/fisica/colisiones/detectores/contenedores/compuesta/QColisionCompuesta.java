/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.colisiones.detectores.contenedores.compuesta;

import java.util.ArrayList;
import java.util.List;
import net.qoopo.engine3d.componentes.fisica.colisiones.detectores.QFormaColision;
import net.qoopo.engine3d.componentes.geometria.QGeometria;
import net.qoopo.engine3d.core.math.QVector3;

/**
 * Define una esfera contenedora del objeto
 *
 * @author alberto
 */
public class QColisionCompuesta extends QFormaColision {

    private List<QColisionCompuestaHija> hijos = new ArrayList<QColisionCompuestaHija>();

    public QColisionCompuesta() {
    }

    public void agregarHijo(QColisionCompuestaHija hijo) {
        hijos.add(hijo);
    }

    public void agregarHijo(QFormaColision forma, QVector3 ubicacion) {
        hijos.add(new QColisionCompuestaHija(forma, ubicacion));
    }

    @Override
    public boolean verificarColision(QFormaColision otro) {

        boolean b = false;

//validar contra otro estera
        if (otro instanceof QColisionCompuesta) {
        }

        return b;

    }

    @Override
    public void destruir() {
    }

    public List<QColisionCompuestaHija> getHijos() {
        return hijos;
    }

    public void setHijos(List<QColisionCompuestaHija> hijos) {
        this.hijos = hijos;
    }

}
