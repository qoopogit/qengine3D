/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.fisica.dinamica;

import net.qoopo.engine3d.core.math.QVector3;

/**
 *
 * @author alberto
 */
public class QObjetoSuave extends QObjetoDinamico {

    public QVector3 velocidadLinear = QVector3.zero;
    public QVector3 velocidadAngular = QVector3.zero;
    public QVector3 fuerzaInercial = QVector3.zero;
    public QVector3 fuerzaTotal = QVector3.zero;
    public QVector3 fueraTorque = QVector3.zero;

    public float masa;

    public QObjetoSuave(int tipo_dinamico) {
        super(tipo_dinamico);
    }

}
