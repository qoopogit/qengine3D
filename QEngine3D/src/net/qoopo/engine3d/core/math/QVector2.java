package net.qoopo.engine3d.core.math;

import java.io.Serializable;

import net.qoopo.engine3d.core.util.QGlobal;

public class QVector2 implements Serializable {

    public float x, y;

    public QVector2() {
        x = 0;
        y = 0;
    }

    public QVector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setXY(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(QVector2 vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    /**
     * Meotod optimizado para calcular el vector normal usando el metodo de
     * calculo rapido de la inversa de la raiz cuadrada
     *
     * @return
     */
    public QVector2 normalize() {
        if (QGlobal.OPT_USAR_FAST_INVSQRT) {
            float inversaRaizCuadrada = QMath.fastInvSqrt(x * x + y * y);
            x *= inversaRaizCuadrada;
            y *= inversaRaizCuadrada;

        } else {
            float length = length();
            x /= length;
            y /= length;
            return this;
        }
        return this;
    }

    public QVector2 invert() {
        x *= -1;
        y *= -1;
        return this;
    }

    public QVector2 add(float value) {
        x += value;
        y += value;
        return this;
    }

    public QVector2 subtract(float value) {
        x -= value;
        y -= value;
        return this;
    }

    public QVector2 add(QVector2... others) {
        for (QVector2 other : others) {
            x += other.x;
            y += other.y;
        }
        return this;
    }

    public QVector2 subtract(QVector2... others) {
        for (QVector2 other : others) {
            x -= other.x;
            y -= other.y;
        }
        return this;
    }

    public QVector2 add(float delta, QVector2... others) {
        for (QVector2 other : others) {
            x += other.x * delta;
            y += other.y * delta;
        }
        return this;
    }

    public void flip() {
        x = -x;
        y = -y;
    }

    public void copyXY(QVector2 vector) {
        setXY(vector.x, vector.y);
    }

    public QVector2 multiply(float alpha) {
        x *= alpha;
        y *= alpha;
        return this;
    }

//    public QVector2 crossProduct(QVector2 other) {
//        tempX = this.y * other.z - this.z * other.y;
//        tempY = this.z * other.x - this.x * other.z;
//        z = this.x * other.y - this.y * other.x;
//        x = tempX;
//        y = tempY;
//        return this;
//    }
    public float length() {
        if (QGlobal.OPT_USAR_FAST_INVSQRT) {
            return 1.0f / (float) QMath.fastInvSqrt(x * x + y * y);
        } else {
            return (float) QMath.sqrt(x * x + y * y);
        }
    }

    public float dotProduct(QVector2 other) {
        return (this.x * other.x + this.y * other.y);
    }

//    public QVector2 rotateX(float angle) {
//        cosAngle = (float) QMath.cos(angle);
//        sinAngle = (float) QMath.sin(angle);
//        tempY = y * cosAngle - z * sinAngle;
//        z = y * sinAngle + z * cosAngle;
//        y = tempY;
//        return this;
//    }
//
//    public QVector2 rotateY(float angle) {
//        cosAngle = (float) QMath.cos(angle);
//        sinAngle = (float) QMath.sin(angle);
//        tempZ = z * cosAngle - x * sinAngle;
//        x = z * sinAngle + x * cosAngle;
//        z = tempZ;
//        return this;
//    }
//
//    public QVector2 rotateZ(float angle) {
//        cosAngle = (float) QMath.cos(angle);
//        sinAngle = (float) QMath.sin(angle);
//        tempX = x * cosAngle - y * sinAngle;
//        y = x * sinAngle + y * cosAngle;
//        x = tempX;
//        return this;
//    }
    public QVector2 clone() {
        return new QVector2(x, y);
    }

    public static QVector2 addNewVector(QVector2 v1, QVector2... others) {
        QVector2 result = v1.clone();
        for (QVector2 other : others) {
            result.x += other.x;
            result.y += other.y;
        }
        return result;
    }

    public static float dotProduct(QVector2 v1, QVector2 v2) {
        return (v1.x * v2.x + v1.y * v2.y);
    }
//
//    public static QVector2 crossProduct(QVector2 v1, QVector2 v2) {
//        return new QVector2(
//                v1.y * v2.z - v1.z * v2.y,
//                v1.z * v2.x - v1.x * v2.z,
//                v1.x * v2.y - v1.y * v2.x);
//    }

    public static QVector2 multiply(float alpha, QVector2 vector) {
        return new QVector2(alpha * vector.x, alpha * vector.y);
    }

    public String toString() {
        return x + ", " + y;
    }

//    public float anguloX(QVector2 otro) {
//        float angulo = 0;
//        if (otro.y != 0 || otro.z != 0) {
//            float cosenoAng = (float) ((y * otro.y + z * otro.z)
//                    / ((Math.sqrt(y * y + z * z)) * (Math.sqrt(otro.y * otro.y + otro.z * otro.z))));
//            angulo = (float) Math.acos(cosenoAng);
//        } else {
//            float dif = otro.z - z;
//            if (dif > 0) {
//                return 0;
//            } else {
//                return (float) Math.PI;//180
//            }
//
//        }
//        if (Float.isNaN(angulo)) {
//            angulo = 0;
//        }
//        return angulo;
//    }
//
//    public float anguloY(QVector2 otro) {
//        float angulo = 0;
//        if (otro.x != 0 || otro.z != 0) {
//            float cosenoAng = (float) ((x * otro.x + z * otro.z)
//                    / ((Math.sqrt(x * x + z * z)) * (Math.sqrt(otro.x * otro.x + otro.z * otro.z))));
//            angulo = (float) Math.acos(cosenoAng);
//        } else {
//            float dif = otro.x - x;
//            if (dif > 0) {
//                return 0;
//            } else {
//                return (float) Math.PI;//180
//            }
//
//        }
//        if (Float.isNaN(angulo)) {
//            angulo = 0;
//        }
//        return angulo;
//    }
//
//    public float anguloZ(QVector2 otro) {
//        float angulo = 0;
//        if (otro.x != 0 || otro.y != 0) {
//            float cosenoAng = (float) ((y * otro.y + x * otro.x) / ((Math.sqrt(y * y + x * x)) * (Math.sqrt(otro.y * otro.y + otro.x * otro.x))));
//            angulo = (float) Math.acos(cosenoAng);
//        } else {
//            float dif = otro.x - x;
//            if (dif > 0) {
//                return 0;
//            } else {
//                return (float) Math.PI;//180
//            }
//        }
//        if (Float.isNaN(angulo)) {
//            angulo = 0;
//        }
//        return angulo;
//    }
//    public float angulo(QVector2 otro) {
//        float cosenoAng = (float) ((x * otro.x + y * otro.y + z * otro.z) / ((Math.sqrt(x * x + y * y + z * z)) * (Math.sqrt(otro.x * otro.x + otro.y * otro.y + otro.z * otro.z))));
//        float angulo = (float) Math.acos(cosenoAng);
//        if (Float.isNaN(angulo)) {
//            angulo = 0;
//        }
//        return angulo;
//    }
    public float get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;

        }
        throw new IllegalArgumentException("index must be either 0 or 1 ");
    }

//    public QVertice toVertice() {
//        return new QVertice(x, y, 0);
//    }
}
