/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.componentes.shader;

import net.qoopo.engine3d.componentes.QComponente;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Componente que tiene un shader personalizado, si una entidad tiene este
 * shaderm se usara en lugar del default
 *
 * @author alberto
 */
public class QShaderComponente extends QComponente {

    private QShader shader;

    public QShaderComponente() {
    }

    public QShaderComponente(QShader shader) {
        this.shader = shader;
    }

    public QShader getShader() {
        return shader;
    }

    public void setShader(QShader shader) {
        this.shader = shader;
    }

    @Override
    public void destruir() {
        shader = null;
    }

}
