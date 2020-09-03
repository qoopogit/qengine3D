/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.textura;

import java.awt.image.BufferedImage;
import net.qoopo.engine3d.componentes.reflexiones.QMapaCubo;
import net.qoopo.engine3d.core.textura.procesador.QProcesadorTextura;
import net.qoopo.engine3d.core.math.QColor;
import net.qoopo.engine3d.core.math.QMath;
import net.qoopo.engine3d.core.math.QVector3;

/**
 * Util que permite
 *
 * @author alberto
 */
public class QTexturaUtil {

    public static QColor getColorMapaEntorno(QVector3 vector, QProcesadorTextura mapaEntorno, int tipoMapaEntorno) {
        switch (tipoMapaEntorno) {
            case QMapaCubo.FORMATO_MAPA_CUBO:
                return getColorTexturaCubeMap(vector, mapaEntorno);
            case QMapaCubo.FORMATO_MAPA_HDRI:
            default:
                return getColorTexturaHDRI(vector, mapaEntorno);
        }
    }

    /**
     * Obtiene el color correspondiente en un mapa HDRI
     *
     *
     * @param vector
     * @param mapaEntorno
     * @return
     */
    public static QColor getColorTexturaHDRI(QVector3 vector, QProcesadorTextura mapaEntorno) {
        try {
            BufferedImage img = mapaEntorno.getTexture();
            int hdriWidth = img.getWidth();
            int hdriHeight = img.getHeight();
            int newX = (int) (hdriWidth * ((Math.PI - Math.atan2(vector.x, vector.z)) / (2 * Math.PI)));
            int newY = (int) (hdriHeight * (Math.acos(vector.y / vector.length()) / Math.PI));
            newX = QMath.rotateNumber(newX, hdriWidth);
            newY = QMath.rotateNumber(newY, hdriHeight);
            return new QColor(img.getRGB(newX, newY));
        } catch (Exception e) {
            return QColor.BLACK;
        }
    }

    /**
     * Obtiene el color correspondiente de una textura en formato mapa cubico
     *
     * @param vector
     * @param mapaEntorno
     * @return
     */
    public static QColor getColorTexturaCubeMap(QVector3 vector, QProcesadorTextura mapaEntorno) {
        try {

            float absX = QMath.abs(vector.x);
            float absY = QMath.abs(vector.y);
            float absZ = QMath.abs(vector.z);

            boolean isXPositive = vector.x > 0;
            boolean isYPositive = vector.y > 0;
            boolean isZPositive = vector.z > 0;

            float maxAxis = 0, uc = 0, vc = 0;
            float u, v;

            float factorU = 1.0f / 4.0f;
            float factorV = 1.0f / 3.0f;
            float uOffset = 0;
            float vOffset = 0;

            // POSITIVE Y (Arriba)
            if (isYPositive && absY >= absX && absY >= absZ) {
                // u (0 to 1) goes from -x to +x
                // v (0 to 1) goes from +z to -z
                maxAxis = absY;
                uc = vector.x;
                vc = -vector.z;
                uOffset = factorU;
                vOffset = factorV * 2;
            }

            // NEGATIVE X (izquierda)
            if (!isXPositive && absX >= absY && absX >= absZ) {
                // u (0 to 1) goes from -z to +z
                // v (0 to 1) goes from -y to +y
                maxAxis = absX;
                uc = vector.z;
                vc = vector.y;
                uOffset = 0;
                vOffset = factorV;
            }

            // POSITIVE Z (adelante)
            if (isZPositive && absZ >= absX && absZ >= absY) {
                // u (0 to 1) goes from -x to +x
                // v (0 to 1) goes from -y to +y
                maxAxis = absZ;
                uc = vector.x;
                vc = vector.y;
                uOffset = factorU;
                vOffset = factorV;
            }
            // POSITIVE X (derecha)
            if (isXPositive && absX >= absY && absX >= absZ) {
                // u (0 to 1) goes from +z to -z
                // v (0 to 1) goes from -y to +y
                maxAxis = absX;
                uc = -vector.z;
                vc = vector.y;
                uOffset = 2 * factorU;
                vOffset = factorV;
            }

            // NEGATIVE Z (atras)
            if (!isZPositive && absZ >= absX && absZ >= absY) {
                // u (0 to 1) goes from +x to -x
                // v (0 to 1) goes from -y to +y
                maxAxis = absZ;
                uc = -vector.x;
                vc = vector.y;
                uOffset = 3 * factorU;
                vOffset = factorV;
            }

            // NEGATIVE Y (abajo)
            if (!isYPositive && absY >= absX && absY >= absZ) {
                // u (0 to 1) goes from -x to +x
                // v (0 to 1) goes from -z to +z
                maxAxis = absY;
                uc = vector.x;
                vc = vector.z;
                uOffset = factorU;
                vOffset = 0;
            }

            // Convert range from -1 to 1 to 0 to 1
            u = 0.5f * (uc / maxAxis + 1.0f);
            v = 0.5f * (vc / maxAxis + 1.0f);

            // convierte la coordenada de la subtextura a la textura global
            u = u * factorU + uOffset;
            v = v * factorV + vOffset;
            return mapaEntorno.get_QARGB(u, v);
        } catch (Exception e) {
            e.printStackTrace();
            return QColor.BLACK;
        }
    }

    /**
     * Obtiene el color correspondiente en un mapa cubico con el arreglo de
     * texturas
     *
     * @param vector
     * @param mapaEntorno
     * @return
     */
    public static QColor getColorTexturaCubeMap(QVector3 vector, QTextura[] mapaEntorno) {
        try {
            float absX = QMath.abs(vector.x);
            float absY = QMath.abs(vector.y);
            float absZ = QMath.abs(vector.z);

            boolean isXPositive = vector.x > 0;
            boolean isYPositive = vector.y > 0;
            boolean isZPositive = vector.z > 0;

            float maxAxis = 0, uc = 0, vc = 0;
            int index = 0;
            float u, v;

            // POSITIVE X (derecha)
            if (isXPositive && absX >= absY && absX >= absZ) {
                // u (0 to 1) goes from +z to -z
                // v (0 to 1) goes from -y to +y
                maxAxis = absX;
                uc = -vector.z;
                vc = vector.y;
                index = 5;
            }
            // NEGATIVE X (izquierda)
            if (!isXPositive && absX >= absY && absX >= absZ) {
                // u (0 to 1) goes from -z to +z
                // v (0 to 1) goes from -y to +y
                maxAxis = absX;
                uc = vector.z;
                vc = vector.y;
                index = 4;
            }
            // POSITIVE Y (Arriba)
            if (isYPositive && absY >= absX && absY >= absZ) {
                // u (0 to 1) goes from -x to +x
                // v (0 to 1) goes from +z to -z
                maxAxis = absY;
                uc = vector.x;
                vc = -vector.z;
                index = 0;
            }
            // NEGATIVE Y (abajo)
            if (!isYPositive && absY >= absX && absY >= absZ) {
                // u (0 to 1) goes from -x to +x
                // v (0 to 1) goes from -z to +z
                maxAxis = absY;
                uc = vector.x;
                vc = vector.z;
                index = 1;
            }
            // POSITIVE Z (adelante)
            if (isZPositive && absZ >= absX && absZ >= absY) {
                // u (0 to 1) goes from -x to +x
                // v (0 to 1) goes from -y to +y
                maxAxis = absZ;
                uc = vector.x;
                vc = vector.y;
                index = 2;
            }
            // NEGATIVE Z (atras)
            if (!isZPositive && absZ >= absX && absZ >= absY) {
                // u (0 to 1) goes from +x to -x
                // v (0 to 1) goes from -y to +y
                maxAxis = absZ;
                uc = -vector.x;
                vc = vector.y;
                index = 3;
            }

            // Convert range from -1 to 1 to 0 to 1
            u = 0.5f * (uc / maxAxis + 1.0f);
            v = 0.5f * (vc / maxAxis + 1.0f);

            return mapaEntorno[index].getQColor(u, v);
        } catch (Exception e) {
            return QColor.BLACK;
        }
    }

    /*
    
    
    
    A cube texture indexes six texture maps from 0 to 5 in order Positive X, Negative X, Positive Y, Negative Y, Positive Z,
    Negative Z.[4][5] The images are stored with the origin at the lower left of the image.
    The Positive X and Y faces must reverse the Z coordinate and the Negative Z face must negate the X coordinate.
    If given the face, and texture coordinates {\displaystyle (u,v)} (u,v), 
    the non-normalized vector {\displaystyle (x,y,z)} (x,y,z) can be computed by the function:
    
    
    void convert_cube_uv_to_xyz(int index, float u, float v, float *x, float *y, float *z)
{
  // convert range 0 to 1 to -1 to 1
  float uc = 2.0f * u - 1.0f;
  float vc = 2.0f * v - 1.0f;
  switch (index)
  {
    case 0: *x =  1.0f; *y =    vc; *z =   -uc; break;	// POSITIVE X
    case 1: *x = -1.0f; *y =    vc; *z =    uc; break;	// NEGATIVE X
    case 2: *x =    uc; *y =  1.0f; *z =   -vc; break;	// POSITIVE Y
    case 3: *x =    uc; *y = -1.0f; *z =    vc; break;	// NEGATIVE Y
    case 4: *x =    uc; *y =    vc; *z =  1.0f; break;	// POSITIVE Z
    case 5: *x =   -uc; *y =    vc; *z = -1.0f; break;	// NEGATIVE Z
  }
}
    
    
    
    void convert_xyz_to_cube_uv(float x, float y, float z, int *index, float *u, float *v)
{
  float absX = fabs(x);
  float absY = fabs(y);
  float absZ = fabs(z);
  
  int isXPositive = x > 0 ? 1 : 0;
  int isYPositive = y > 0 ? 1 : 0;
  int isZPositive = z > 0 ? 1 : 0;
  
  float maxAxis, uc, vc;
  
  // POSITIVE X
  if (isXPositive && absX >= absY && absX >= absZ) {
    // u (0 to 1) goes from +z to -z
    // v (0 to 1) goes from -y to +y
    maxAxis = absX;
    uc = -z;
    vc = y;
    *index = 0;
  }
  // NEGATIVE X
  if (!isXPositive && absX >= absY && absX >= absZ) {
    // u (0 to 1) goes from -z to +z
    // v (0 to 1) goes from -y to +y
    maxAxis = absX;
    uc = z;
    vc = y;
    *index = 1;
  }
  // POSITIVE Y
  if (isYPositive && absY >= absX && absY >= absZ) {
    // u (0 to 1) goes from -x to +x
    // v (0 to 1) goes from +z to -z
    maxAxis = absY;
    uc = x;
    vc = -z;
    *index = 2;
  }
  // NEGATIVE Y
  if (!isYPositive && absY >= absX && absY >= absZ) {
    // u (0 to 1) goes from -x to +x
    // v (0 to 1) goes from -z to +z
    maxAxis = absY;
    uc = x;
    vc = z;
    *index = 3;
  }
  // POSITIVE Z
  if (isZPositive && absZ >= absX && absZ >= absY) {
    // u (0 to 1) goes from -x to +x
    // v (0 to 1) goes from -y to +y
    maxAxis = absZ;
    uc = x;
    vc = y;
    *index = 4;
  }
  // NEGATIVE Z
  if (!isZPositive && absZ >= absX && absZ >= absY) {
    // u (0 to 1) goes from +x to -x
    // v (0 to 1) goes from -y to +y
    maxAxis = absZ;
    uc = -x;
    vc = y;
    *index = 5;
  }

  // Convert range from -1 to 1 to 0 to 1
  *u = 0.5f * (uc / maxAxis + 1.0f);
  *v = 0.5f * (vc / maxAxis + 1.0f);
}
     */
}
