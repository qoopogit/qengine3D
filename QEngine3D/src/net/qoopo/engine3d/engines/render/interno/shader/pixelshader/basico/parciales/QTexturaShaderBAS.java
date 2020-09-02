/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.engines.render.interno.shader.pixelshader.basico.parciales;

import net.qoopo.engine3d.componentes.geometria.primitivas.QPixel;
import net.qoopo.engine3d.componentes.iluminacion.QIluminacion;
import net.qoopo.engine3d.core.material.basico.QMaterialBas;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.engines.render.QMotorRender;
import net.qoopo.engine3d.engines.render.interno.shader.pixelshader.QShader;

/**
 * Este shader toma en cuenta la textura con sombreado phong. Sin iluminacion
 * del entorno
 *
 * @author alberto
 */
public class QTexturaShaderBAS extends QShader {

    private QColor colorDifuso;
    private float transparencia;

    public QTexturaShaderBAS(QMotorRender render) {
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

        boolean pixelTransparente = false;
        boolean pixelTransparente2 = false;

//TOMA EL VALOR DE LA TRANSPARENCIA        
        if (((QMaterialBas) pixel.material).isTransparencia()) {
            //si tiene un mapa de transparencia
            if (((QMaterialBas) pixel.material).getMapaTransparencia() != null) {
                // es una imagen en blanco y negro, toma cualquier canal de color
                transparencia = ((QMaterialBas) pixel.material).getMapaTransparencia().get_QARGB(pixel.u, pixel.v).r;
            } else {
                //toma el valor de transparencia del material
                transparencia = ((QMaterialBas) pixel.material).getTransAlfa();
            }
        } else {
            transparencia = 1;
        }

        if (((QMaterialBas) pixel.material).getMapaDifusa() == null || !render.opciones.isMaterial()) {
            // si no hay textura usa el color del material
            color.set(((QMaterialBas) pixel.material).getColorDifusa());
        } else {

            //si la textura no es proyectada (lo hace otro renderer) toma las coordenadas ya calculadas
            if (!((QMaterialBas) pixel.material).getMapaDifusa().isProyectada()) {
                colorDifuso = ((QMaterialBas) pixel.material).getMapaDifusa().get_QARGB(pixel.u, pixel.v);
            } else {
                colorDifuso = ((QMaterialBas) pixel.material).getMapaDifusa().get_QARGB((float) x / (float) render.getFrameBuffer().getAncho(), -(float) y / (float) render.getFrameBuffer().getAlto());
            }

            switch (((QMaterialBas) pixel.material).getMapaDifusa().getModo()) {
                case QProcesadorTextura.MODO_COMBINAR:
                    color.r = (colorDifuso.r + ((QMaterialBas) pixel.material).getColorDifusa().r) / 2;
                    color.g = (colorDifuso.g + ((QMaterialBas) pixel.material).getColorDifusa().g) / 2;
                    color.b = (colorDifuso.b + ((QMaterialBas) pixel.material).getColorDifusa().b) / 2;
                    break;
                case QProcesadorTextura.MODO_REMPLAZAR:
                default:
                    color.set(colorDifuso);
                    break;
            }
            pixelTransparente2 = ((QMaterialBas) pixel.material).isTransparencia() && ((QMaterialBas) pixel.material).getColorTransparente() != null && colorDifuso.toRGB() == ((QMaterialBas) pixel.material).getColorTransparente().toRGB();//sin alfa
            //solo activa la transparencia si tiene el canal alfa y el color es negro (el negro es el color transparente)
            pixelTransparente = colorDifuso.a < 1 || pixelTransparente2;//transparencia imagenes png
            if (pixelTransparente) {
                return null;
            }
        }

        calcularIluminacion(iluminacion, pixel);

        // Iluminacion difusa
        color.scale(iluminacion.getColorDifuso());

        //***********************************************************
        //******                    TRANSPARENCIA
        //***********************************************************
        if (((QMaterialBas) pixel.material).isTransparencia() && transparencia < 1) {
            QColor tmp = render.getFrameBuffer().getColor(x, y);//el color actual en el buffer para mezclarlo
            color.r = (1 - transparencia) * tmp.r + transparencia * color.r;
            color.g = (1 - transparencia) * tmp.g + transparencia * color.g;
            color.b = (1 - transparencia) * tmp.b + transparencia * color.b;
            tmp = null;
        }

        // Agrega Luz especular.
        color.addLocal(iluminacion.getColorEspecular());

        return color;
    }

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
