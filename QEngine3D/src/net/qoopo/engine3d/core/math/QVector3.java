package net.qoopo.engine3d.core.math;

import java.io.Serializable;

import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.util.QGlobal;

public class QVector3 implements Serializable {

    public static final QVector3 zero = new QVector3(0, 0, 0);
    public static final QVector3 gravedad = new QVector3(0, -9.81f, 0);

    public static final QVector3 unitario_x = new QVector3(1, 0, 0);
    public static final QVector3 unitario_y = new QVector3(0, 1, 0);
    public static final QVector3 unitario_z = new QVector3(0, 0, 1);
    public static final QVector3 unitario_xyz = new QVector3(1, 1, 1);

    public float x, y, z;
    private float tempX;
    private float tempY;
    private float tempZ;
    private float sinAngle;
    private float cosAngle;

    public QVector3() {
        x = 0;
        y = 0;
        z = 0;
    }

    public QVector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public QVector3(QVertice v1) {
        this(v1.ubicacion.x, v1.ubicacion.y, v1.ubicacion.z);
    }

    public QVector3(QVertice v1, QVertice v2) {
        this(v2.ubicacion.getVector3(), v1.ubicacion.getVector3());
    }

    public QVector3(QVector3 v1, QVector3 v2) {
        this(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
    }

    public void setXYZ(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(QVector3 vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
    }

    public void setXYZ(QVertice v1, QVertice v2) {
        setXYZ(v2.ubicacion.x - v1.ubicacion.x, v2.ubicacion.y - v1.ubicacion.y, v2.ubicacion.z - v1.ubicacion.z);
    }

    public QVector3 invert() {
        x *= -1;
        y *= -1;
        z *= -1;
        return this;
    }

    /**
     * Meotod optimizado para calcular el vector normal usando el metodo de
     * calculo rapido de la inversa de la raiz cuadrada
     *
     * @return
     */
    public QVector3 normalize() {
        if (QGlobal.OPT_USAR_FAST_INVSQRT) {
            float inversaRaizCuadrada = QMath.fastInvSqrt(x * x + y * y + z * z);
            x *= inversaRaizCuadrada;
            y *= inversaRaizCuadrada;
            z *= inversaRaizCuadrada;
        } else {
            float length = length();
            x /= length;
            y /= length;
            z /= length;
            return this;
        }
        return this;
    }

    public QVector3 add(float value) {
        x += value;
        y += value;
        z += value;
        return this;
    }

    public QVector3 subtract(float value) {
        x -= value;
        y -= value;
        z -= value;
        return this;
    }

    public QVector3 add(QVector3... others) {
        for (QVector3 other : others) {
            x += other.x;
            y += other.y;
            z += other.z;
        }
        return this;
    }

    public QVector3 subtract(QVector3... others) {
        for (QVector3 other : others) {
            x -= other.x;
            y -= other.y;
            z -= other.z;
        }
        return this;
    }

    public QVector3 add(float delta, QVector3... others) {
        for (QVector3 other : others) {
            x += other.x * delta;
            y += other.y * delta;
            z += other.z * delta;
        }
        return this;
    }

    public void flip() {
        x = -x;
        y = -y;
        z = -z;
    }

    public void copyXYZ(QVector3 vector) {
        setXYZ(vector.x, vector.y, vector.z);
    }

    public QVector3 multiply(float alpha) {
        x *= alpha;
        y *= alpha;
        z *= alpha;
        return this;
    }

    public QVector3 crossProduct(QVector3 other) {
        tempX = this.y * other.z - this.z * other.y;
        tempY = this.z * other.x - this.x * other.z;
        z = this.x * other.y - this.y * other.x;
        x = tempX;
        y = tempY;
        return this;
    }

    public float length() {
        if (QGlobal.OPT_USAR_FAST_INVSQRT) {
            return 1.0f / (float) QMath.fastInvSqrt(x * x + y * y + z * z);
        } else {
            return (float) QMath.sqrt(x * x + y * y + z * z);
        }
    }

    public float dotProduct(QVector3 other) {
        return (this.x * other.x + this.y * other.y + this.z * other.z);
    }

    public QVector3 rotateX(float angle) {
        cosAngle = (float) QMath.cos(angle);
        sinAngle = (float) QMath.sin(angle);
        tempY = y * cosAngle - z * sinAngle;
        z = y * sinAngle + z * cosAngle;
        y = tempY;
        return this;
    }

    public QVector3 rotateY(float angle) {
        cosAngle = (float) QMath.cos(angle);
        sinAngle = (float) QMath.sin(angle);
        tempZ = z * cosAngle - x * sinAngle;
        x = z * sinAngle + x * cosAngle;
        z = tempZ;
        return this;
    }

    public QVector3 rotateZ(float angle) {
        cosAngle = (float) QMath.cos(angle);
        sinAngle = (float) QMath.sin(angle);
        tempX = x * cosAngle - y * sinAngle;
        y = x * sinAngle + y * cosAngle;
        x = tempX;
        return this;
    }

    public QVector3 clone() {
        return new QVector3(x, y, z);
    }

    public static QVector3 addNewVector(QVector3 v1, QVector3... others) {
        QVector3 result = v1.clone();
        for (QVector3 other : others) {
            result.x += other.x;
            result.y += other.y;
            result.z += other.z;
        }
        return result;
    }

    public static float dotProduct(QVector3 v1, QVector3 v2) {
        return (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);
    }

    public static QVector3 crossProduct(QVector3 v1, QVector3 v2) {
        return new QVector3(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x);
    }

    public static QVector3 multiply(float alpha, QVector3 vector) {
        return new QVector3(alpha * vector.x, alpha * vector.y, alpha * vector.z);
    }

    public String toString() {
        return x + ", " + y + ", " + z;
    }

    public float anguloX(QVector3 otro) {
        float angulo = 0;
        if (otro.y != 0 || otro.z != 0) {
            float cosenoAng = (float) ((y * otro.y + z * otro.z)
                    / ((Math.sqrt(y * y + z * z)) * (Math.sqrt(otro.y * otro.y + otro.z * otro.z))));
            angulo = (float) Math.acos(cosenoAng);
        } else {
            float dif = otro.z - z;
            if (dif > 0) {
                return 0;
            } else {
                return (float) Math.PI;//180
            }

        }
        if (Float.isNaN(angulo)) {
            angulo = 0;
        }
        return angulo;
    }

    public float anguloY(QVector3 otro) {
        float angulo = 0;
        if (otro.x != 0 || otro.z != 0) {
            float cosenoAng = (float) ((x * otro.x + z * otro.z)
                    / ((Math.sqrt(x * x + z * z)) * (Math.sqrt(otro.x * otro.x + otro.z * otro.z))));
            angulo = (float) Math.acos(cosenoAng);
        } else {
            float dif = otro.x - x;
            if (dif > 0) {
                return 0;
            } else {
                return (float) Math.PI;//180
            }

        }
        if (Float.isNaN(angulo)) {
            angulo = 0;
        }
        return angulo;
    }

    public float anguloZ(QVector3 otro) {
        float angulo = 0;
        if (otro.x != 0 || otro.y != 0) {
            float cosenoAng = (float) ((y * otro.y + x * otro.x) / ((Math.sqrt(y * y + x * x)) * (Math.sqrt(otro.y * otro.y + otro.x * otro.x))));
            angulo = (float) Math.acos(cosenoAng);
        } else {
            float dif = otro.x - x;
            if (dif > 0) {
                return 0;
            } else {
                return (float) Math.PI;//180
            }
        }
        if (Float.isNaN(angulo)) {
            angulo = 0;
        }
        return angulo;
    }

    public float angulo(QVector3 otro) {
        float cosenoAng = (float) ((x * otro.x + y * otro.y + z * otro.z) / ((Math.sqrt(x * x + y * y + z * z)) * (Math.sqrt(otro.x * otro.x + otro.y * otro.y + otro.z * otro.z))));
        float angulo = (float) Math.acos(cosenoAng);
        if (Float.isNaN(angulo)) {
            angulo = 0;
        }
        return angulo;
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
        }
        throw new IllegalArgumentException("index must be either 0, 1 or 2");
    }

    public QVertice toVertice() {
        return new QVertice(x, y, z, 1);
    }

    public QVector2 xy() {
        return new QVector2(x, y);
    }

    public QVector2 yx() {
        return new QVector2(y, x);
    }

    public QVector2 getXZ() {
        return new QVector2(x, z);
    }

    public QVector2 getYZ() {
        return new QVector2(y, z);
    }

}
