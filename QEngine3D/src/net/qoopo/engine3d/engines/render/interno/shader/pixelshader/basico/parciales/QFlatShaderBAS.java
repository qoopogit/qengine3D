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
    public QColor colorearPixel(QPixel currentPixel, int x, int y) {
        if (currentPixel == null) {
            return null;
        }
        if (!currentPixel.isDibujar()) {
            return null;
        }

        //(flat shadding)
        //le dice que use la normal de la cara y no la normal interpolada anteriormente
        if (currentPixel.poligono instanceof QPoligono) {
            currentPixel.normal.copyXYZ(((QPoligono) currentPixel.poligono).normal);
        }

        //No procesa textura , usa el color del material
        r = ((QMaterialBas) currentPixel.material).getColorDifusa().r;
        g = ((QMaterialBas) currentPixel.material).getColorDifusa().g;
        b = ((QMaterialBas) currentPixel.material).getColorDifusa().b;

//        calcularIluminacion(iluminacion, currentPixel.x, currentPixel.y, currentPixel.z, currentPixel.normal, ((QMaterialBas) currentPixel.material), currentPixel.entidad);
        calcularIluminacion(iluminacion, currentPixel);

        // Set diffuse illumination
        r = r * iluminacion.dR;
        g = g * iluminacion.dG;
        b = b * iluminacion.dB;

        // Agrega Luz especular.
        r += iluminacion.sR;
        g += iluminacion.sG;
        b += iluminacion.sB;

        // Clamp rgb to 1.
        if (r > 1) {
            r = 1;
        }
        if (g > 1) {
            g = 1;
        }
        if (b > 1) {
            b = 1;
        }
        //actualiza los bytes renderizados de acuerdo transAlfa los datos modificados del pixel
//        render.getFrameBuffer().setRGB(x, y, r, g, b);
        return new QColor(r, g, b);
    }

//    @Override
    protected void calcularIluminacion(QIluminacion illumination, QPixel currentPixel) {
        currentPixel.normal.normalize();
        iluminacionDifusa = 0;
        iluminacionEspecular = 0;

        //toma en cuenta la luz ambiente
        illumination.dR = render.getEscena().getLuzAmbiente();
        illumination.dG = render.getEscena().getLuzAmbiente();
        illumination.dB = render.getEscena().getLuzAmbiente();

        illumination.sR = 0;
        illumination.sG = 0;
        illumination.sB = 0;
        tmpPixelPos.set(currentPixel.ubicacion.getVector3());
        tmpPixelPos.normalize();

        //Iluminacion default no toma en cuenta las luces del entorno
        illumination.dR += -tmpPixelPos.dotProduct(currentPixel.normal);
        illumination.dG += -tmpPixelPos.dotProduct(currentPixel.normal);
        illumination.dB += -tmpPixelPos.dotProduct(currentPixel.normal);

        if (illumination.dR < 0) {
            illumination.dR = 0;
        }
        if (illumination.dG < 0) {
            illumination.dG = 0;
        }
        if (illumination.dB < 0) {
            illumination.dB = 0;
        }

        if (illumination.sR < 0) {
            illumination.sR = 0;
        }
        if (illumination.sG < 0) {
            illumination.sG = 0;
        }
        if (illumination.sB < 0) {
            illumination.sB = 0;
        }
    }

}
