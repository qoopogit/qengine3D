/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.postproceso.procesos.test;

import java.awt.image.BufferedImage;

/**
 *
 * @author alberto
 */
public interface Filtro {

    public abstract BufferedImage filtrar(BufferedImage bi);
}
