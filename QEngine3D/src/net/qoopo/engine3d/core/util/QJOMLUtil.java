/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.qoopo.engine3d.core.util;

//import javax.vecmath.Color3f;
//import javax.vecmath.Color3f;
//import javax.vecmath.Matrix3f;
//import javax.vecmath.Point3d;
//import javax.vecmath.Point3f;
//import javax.vecmath.Quat4f;
//import javax.vecmath.Vector3d;
//import javax.vecmath.Vector3f;
import net.qoopo.engine3d.core.math.QMatriz3;
import net.qoopo.engine3d.core.math.QMatriz4;
import net.qoopo.engine3d.core.math.QVector3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

//import javax.vecmath.Point3d;
//import javax.vecmath.Point3f;
//import javax.vecmath.Quat4f;
//import net.qoopo.engine3d.core.math.QMatriz3;
//import net.qoopo.engine3d.core.math.QVector3;
//import org.joml.Matrix3f;
//import org.joml.Vector3d;
//import org.joml.Vector3f;
/**
 *
 * @author alberto
 */
public class QJOMLUtil {

//    public static Color3f convertirColor3f(QColor color) {
//        return new Color3f(color.r, color.g, color.b);
//    }
    public static QVector3 convertirQVector3(Vector3f vector) {
        return new QVector3(vector.x, vector.y, vector.z);
    }

    public static Vector3f convertirVector3f(QVector3 vector) {
        return new Vector3f(vector.x, vector.y, vector.z);
    }

    public static Vector3d convertirVector3d(QVector3 vector) {
        return new Vector3d(vector.x, vector.y, vector.z);
    }

//    public static Point3f convertirPoint3f(QVector3 vector) {
//        return new Point3f(vector.x, vector.y, vector.z);
//    }
//    public static Point3d convertirPoint3d(QVector3 vector) {
//        return new Point3d(vector.x, vector.y, vector.z);
//    }
    public static Matrix3f convertirMatriz3f(QMatriz3 matriz) {
        Matrix3f mat = new Matrix3f(
                matriz.get(0, 0),
                matriz.get(0, 1),
                matriz.get(0, 2),
                matriz.get(1, 0),
                matriz.get(1, 1),
                matriz.get(1, 2),
                matriz.get(2, 0),
                matriz.get(2, 1),
                matriz.get(2, 2)
        );
        return mat;
    }

    public static QMatriz4 convertirQMatriz4(Matrix4f matriz) {
        QMatriz4 mat4 = new QMatriz4(
                matriz.m00(), matriz.m01(), matriz.m02(), matriz.m03(),
                matriz.m10(), matriz.m11(), matriz.m12(), matriz.m13(),
                matriz.m20(), matriz.m21(), matriz.m22(), matriz.m23(),
                matriz.m30(), matriz.m31(), matriz.m32(), matriz.m33());
        return mat4;
    }

}
