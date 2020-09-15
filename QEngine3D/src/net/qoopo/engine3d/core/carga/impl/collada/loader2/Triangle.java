package net.qoopo.engine3d.core.carga.impl.collada.loader2;

import net.qoopo.engine3d.core.math.QVector4;

/**
 * Triangle class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Triangle {

    private final QVector4[] points = new QVector4[3];

    public Triangle(QVector4 a, QVector4 b, QVector4 c) {
        points[0] = a;
        points[1] = b;
        points[2] = c;
    }

    public QVector4[] getPoints() {
        return points;
    }
    
}
