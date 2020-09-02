/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Shader con sombrado de phong. sin Textura
 *
 * @author alberto
 */
public class QPhongShaderBAS extends QShader {

    public QPhongShaderBAS(QMotorRender render) {
        super(render);
    }

    @Override
    public QColor colorearPixel(QPixel pixel, int x, int y) {
        if (pixel == null) {
            return null;
        }
        if (!pixel.isDibujar()) {
            return null;
        }

        //No procesa textura , usa el color del material
        color.set(((QMaterialBas) pixel.material).getColorDifusa());
        calcularIluminacion(iluminacion, pixel);
        // Iluminacion difusa
        color.scale(iluminacion.getColorDifuso());
        // Agrega Luz especular.
        color.addLocal(iluminacion.getColorEspecular());
        return color;
    }

//    @Override
    protected void calcularIluminacion(QIluminacion illumination, QPixel pixel) {
        pixel.normal.normalize();
        iluminacionDifusa = 0;
        iluminacionEspecular = 0;
        //toma en cuenta la luz ambiente
        iluminacion.setColorDifuso(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));
        iluminacion.setColorEspecular(QColor.BLACK.clone());
        tmpPixelPos.set(pixel.ubicacion.getVector3());
        tmpPixelPos.normalize();
        //Iluminacion default no toma en cuenta las luces del entorno      
        iluminacion.getColorDifuso().add(-tmpPixelPos.dotProduct(pixel.normal));
    }

}
