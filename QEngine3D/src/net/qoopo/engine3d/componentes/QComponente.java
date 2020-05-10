/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes;

import java.io.Serializable;

/**
 *
 * @author alberto
 */
public abstract class QComponente implements Serializable {

    public QEntidad entidad;

    public abstract void destruir();

}
