/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.proxy;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.material.nodos.QMaterialNodo;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.QBasShader;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.nodos.QShaderNodos;

/**
 * Calcula el color e iluminación de cada pixel, dependiendo si es con material
 * básico o pbr redirecciona los shader correspondientes
 *
 * @author alberto
 */
public class QFullShader extends QShader {

    private QShader basico;
    private QShaderNodos nodos;

    public QFullShader(QMotorRender render) {
        super(render);
        basico = new QBasShader(render);
//        basico = new QPBRShader(render);
        nodos = new QShaderNodos(render);
    }

    @Override
    public QColor colorearPixel(QPixel pixel, int x, int y) {
        try {
            if (pixel.material instanceof QMaterialBas) {
                return basico.colorearPixel(pixel, x, y);
            } else if (pixel.material instanceof QMaterialNodo) {
                return nodos.colorearPixel(pixel, x, y);
            }
        } catch (Exception e) {

        }
        return null;
    }

}
