/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.geometria.primitivas.QPoligono;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Shader plano sin textura
 *
 * @author alberto
 */
public class QFlatShaderBAS extends QShader {

    public QFlatShaderBAS(QMotorRender render) {
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

        //(flat shadding)
        //le dice que use la normal de la cara y no la normal interpolada anteriormente
        if (pixel.primitiva instanceof QPoligono) {
            pixel.normal.set(((QPoligono) pixel.primitiva).normal);
        }

        //No procesa textura , usa el color del material
        color.set(((QMaterialBas) pixel.material).getColorDifusa());
        calcularIluminacion(iluminacion, pixel);
        // Iluminacion ambiente
        color.scale(iluminacion.getColorAmbiente());
        // Agrega color de la luz.
        color.addLocal(iluminacion.getColorLuz());
        return color;
    }

//    @Override
    protected void calcularIluminacion(QIluminacion illumination, QPixel pixel) {
        pixel.normal.normalize();
        
        //toma en cuenta la luz ambiente
        iluminacion.setColorAmbiente(new QColor(render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente(), render.getEscena().getLuzAmbiente()));
        iluminacion.setColorLuz(QColor.BLACK.clone());
        tmpPixelPos.set(pixel.ubicacion.getVector3());
        tmpPixelPos.normalize();
        //Iluminacion default no toma en cuenta las luces del entorno      
        iluminacion.getColorAmbiente().add(-tmpPixelPos.dotProduct(pixel.normal));

    }

}
