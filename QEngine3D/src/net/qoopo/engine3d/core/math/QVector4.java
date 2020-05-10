package net.qoopo.engine3d.core.math;

import java.io.Serializable;
import net.qoopo.engine3d.core.util.QGlobal;

public class QVector4 implements Serializable {

    public static QVector4 zero = new QVector4(0, 0, 0, 0);
//    public static QVector3 gravedad=new QVector3(0, -10, 0);

    public static QVector4 unitario_x = new QVector4(1, 0, 0, 0);
    public static QVector4 unitario_y = new QVector4(0, 1, 0, 0);
    public static QVector4 unitario_z = new QVector4(0, 0, 1, 0);
    public static QVector4 unitario_w = new QVector4(0, 0, 0, 1);
    public static QVector4 unitario_xyzw = new QVector4(1, 1, 1, 1);

    public float x, y, z, w;

    public QVector4() {
        x = 0;
        y = 0;
        z = 0;
        w = 0;
    }

    public QVector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    public QVector4(QVector3 vector, float w) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
        this.w = w;
    }

    public void setXYZW(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public void set(QVector4 vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
        this.w = vector.w;
    }

    public void set(QVector3 vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
        this.w = 1;
    }
    public void set(QVector3 vector,float w) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
        this.w = w;
    }

//    public void normalize() {
//        float length = length();
//        x /= length;
//        y /= length;
//        z /= length;
//        w /= length;
//    }
    public void normalize() {
        if (QGlobal.OPT_USAR_FAST_INVSQRT) {
            float inversaRaizCuadrada = QMath.fastInvSqrt(x * x + y * y + z * z + w * w);
            x *= inversaRaizCuadrada;
            y *= inversaRaizCuadrada;
            z *= inversaRaizCuadrada;
            w *= inversaRaizCuadrada;
        } else {
            float length = length();
            x /= length;
            y /= length;
            z /= length;
            w /= length;
        }

    }

    public QVector4 add(QVector4... others) {
        for (QVector4 other : others) {
            x += other.x;
            y += other.y;
            z += other.z;
        }
        return this;
    }

    public void flip() {
        x = -x;
        y = -y;
        z = -z;
    }

    public void copyXYZ(QVector4 vector) {
        setXYZW(vector.x, vector.y, vector.z, vector.w);
    }

    public QVector4 multiply(float alpha) {
        x *= alpha;
        y *= alpha;
        z *= alpha;
        return this;
    }

    public float length() {
        if (QGlobal.OPT_USAR_FAST_INVSQRT) {
            return 1.0f / (float) QMath.fastInvSqrt(x * x + y * y + z * z + w * w);
        } else {
            return (float) Math.sqrt(x * x + y * y + z * z + w * w);
        }
    }

    public float dotProduct(QVector4 other) {
        return (this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w);
    }

    @Override
    public QVector4 clone() {
        return new QVector4(x, y, z, w);
    }

    public static QVector4 addNewVector(QVector4 v1, QVector4... others) {
        QVector4 result = v1.clone();
        for (QVector4 other : others) {
            result.x += other.x;
            result.y += other.y;
            result.z += other.z;
            result.w += other.w;
        }
        return result;
    }

    public static float dotProduct(QVector4 v1, QVector4 v2) {
        return (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);
    }

    public static QVector4 crossProduct(QVector4 v1, QVector4 v2) {
        return new QVector4(
                v1.y * v2.z - v1.z * v2.y,
                v1.z * v2.x - v1.x * v2.z,
                v1.x * v2.y - v1.y * v2.x, v1.w * v2.w);
    }

    public static QVector4 multiply(float alpha, QVector4 vector) {
        return new QVector4(alpha * vector.x,
                alpha * vector.y, alpha * vector.z, alpha * vector.w);
    }

    public String toString() {
        return x + ", " + y + ", " + z;
    }

    public float get(int index) {
        switch (index) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            case 3:
                return w;
        }
        throw new IllegalArgumentException("index must be either 0, 1 or 2");
    }

    public QVector3 getVector3() {
        return new QVector3(x, y, z);
    }
}
