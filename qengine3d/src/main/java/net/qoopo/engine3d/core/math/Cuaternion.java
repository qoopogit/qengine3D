package net.qoopo.engine3d.core.math;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.logging.Logger;

/**
 * <code>Cuaternion</code> defines a single example of a more general class of
 * hypercomplex numbers. Quaternions extends a rotation in three dimensions to a
 * rotation in four dimensions. This avoids "gimbal lock" and allows for smooth
 * continuous rotation.
 *
 * <code>Cuaternion</code> is defined by four floating point numbers: {x y z w}.
 *
 * @author Mark Powell
 * @author Joshua Slack
 */
public final class Cuaternion implements Cloneable, java.io.Serializable {

    
    public static final int IZQUIERDA=0;
    public static final int ARRIBA=1;
    public static final int DIRECCION=2;
    
    static final long serialVersionUID = 1;

    private static final Logger logger = Logger.getLogger(Cuaternion.class.getName());
    /**
     * Represents the identity quaternion rotation (0, 0, 0, 1).
     */
    public static final Cuaternion IDENTITY = new Cuaternion();
    public static final Cuaternion DIRECTION_Z = new Cuaternion();
    public static final Cuaternion ZERO = new Cuaternion(0, 0, 0, 0);

    static {
        DIRECTION_Z.fromAxes(QVector3.unitario_x, QVector3.unitario_y, QVector3.unitario_z);
    }
    public float x, y, z, w;

    /**
     * Constructor instantiates a new <code>Quaternion</code> object
     * initializing all values to zero, except w which is initialized to 1.
     *
     */
    public Cuaternion() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from the
     * given list of parameters.
     *
     * @param x the x value of the quaternion.
     * @param y the y value of the quaternion.
     * @param z the z value of the quaternion.
     * @param w the w value of the quaternion.
     */
    public Cuaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public float getW() {
        return w;
    }

    /**
     * sets the data in a <code>Cuaternion</code> object from the given list of
     * parameters.
     *
     * @param x the x value of the quaternion.
     * @param y the y value of the quaternion.
     * @param z the z value of the quaternion.
     * @param w the w value of the quaternion.
     * @return this
     */
    public Cuaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Sets the data in this <code>Cuaternion</code> object to be equal to the
     * passed <code>Cuaternion</code> object. The values are copied producing a
     * new object.
     *
     * @param q The Cuaternion to copy values from.
     * @return this
     */
    public Cuaternion set(Cuaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
        return this;
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from a
     * collection of rotation angles.
     *
     * @param angles the angles of rotation (x, y, z) that will define the
     * <code>Quaternion</code>.
     */
    public Cuaternion(float[] angles) {
        fromAngles(angles);
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from an
     * interpolation between two other quaternions.
     *
     * @param q1 the first quaternion.
     * @param q2 the second quaternion.
     * @param interp the amount to interpolate between the two quaternions.
     */
    public Cuaternion(Cuaternion q1, Cuaternion q2, float interp) {
        slerp(q1, q2, interp);
    }

    /**
     * Constructor instantiates a new <code>Quaternion</code> object from an
     * existing quaternion, creating a copy.
     *
     * @param q the quaternion to copy.
     */
    public Cuaternion(Cuaternion q) {
        this.x = q.x;
        this.y = q.y;
        this.z = q.z;
        this.w = q.w;
    }

    /**
     * Sets this Cuaternion to {0, 0, 0, 1}. Same as calling set(0,0,0,1).
     */
    public void loadIdentity() {
        x = y = z = 0;
        w = 1;
    }

    /**
     * @return true if this Cuaternion is {0,0,0,1}
     */
    public boolean isIdentity() {
        if (x == 0 && y == 0 && z == 0 && w == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <code>fromAngles</code> builds a quaternion from the Euler rotation
     * angles (y,r,p).
     *
     * @param angles the Euler angles of rotation (in radians).
     */
    public Cuaternion fromAngles(float[] angles) {
        if (angles.length != 3) {
            throw new IllegalArgumentException(
                    "Angles array must have three elements");
        }

        return fromAngles(angles[0], angles[1], angles[2]);
    }

    /**
     * <code>fromAngles</code> builds a Cuaternion from the Euler rotation
     * angles (x,y,z) aka (pitch, yaw, rall)). Note that we are applying in
     * order: (y, z, x) aka (yaw, roll, pitch) but we've ordered them in x, y,
     * and z for convenience.
     *
     * @see
     * <a href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToQuaternion/index.htm">http://www.euclideanspace.com/maths/geometry/rotations/conversions/eulerToQuaternion/index.htm</a>
     *
     * @param xAngle the Euler pitch of rotation (in radians). (aka Attitude,
     * often rot around x)
     * @param yAngle the Euler yaw of rotation (in radians). (aka Heading, often
     * rot around y)
     * @param zAngle the Euler roll of rotation (in radians). (aka Bank, often
     * rot around z)
     */
    public Cuaternion fromAngles(float xAngle, float yAngle, float zAngle) {
        float angle;
        float sinY, sinZ, sinX, cosY, cosZ, cosX;
        angle = zAngle * 0.5f;
        sinZ = QMath.sin(angle);
        cosZ = QMath.cos(angle);
        angle = yAngle * 0.5f;
        sinY = QMath.sin(angle);
        cosY = QMath.cos(angle);
        angle = xAngle * 0.5f;
        sinX = QMath.sin(angle);
        cosX = QMath.cos(angle);

        // variables used to reduce multiplication calls.
        float cosYXcosZ = cosY * cosZ;
        float sinYXsinZ = sinY * sinZ;
        float cosYXsinZ = cosY * sinZ;
        float sinYXcosZ = sinY * cosZ;

        w = (cosYXcosZ * cosX - sinYXsinZ * sinX);
        x = (cosYXcosZ * sinX + sinYXsinZ * cosX);
        y = (sinYXcosZ * cosX + cosYXsinZ * sinX);
        z = (cosYXsinZ * cosX - sinYXcosZ * sinX);

        normalizeLocal();
        return this;
    }

    /**
     * <code>toAngles</code> returns this quaternion converted to Euler rotation
     * angles (yaw,roll,pitch).<br/>
     * Note that the result is not always 100% accurate due to the implications
     * of euler angles.
     *
     * @see
     * <a href="http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/index.htm">http://www.euclideanspace.com/maths/geometry/rotations/conversions/quaternionToEuler/index.htm</a>
     *
     * @param angles the float[] in which the angles should be stored, or null
     * if you want a new float[] to be created
     * @return the float[] in which the angles are stored.
     */
    public float[] toAngles(float[] angles) {
        if (angles == null) {
            angles = new float[3];
        } else if (angles.length != 3) {
            throw new IllegalArgumentException("Angles array must have three elements");
        }

        float sqw = w * w;
        float sqx = x * x;
        float sqy = y * y;
        float sqz = z * z;
        float unit = sqx + sqy + sqz + sqw; // if normalized is one, otherwise
        // is correction factor
        float test = x * y + z * w;
        if (test > 0.499 * unit) { // singularity at north pole
            angles[1] = 2 * QMath.atan2(x, w);
            angles[2] = QMath.HALF_PI;
            angles[0] = 0;
        } else if (test < -0.499 * unit) { // singularity at south pole
            angles[1] = -2 * QMath.atan2(x, w);
            angles[2] = -QMath.HALF_PI;
            angles[0] = 0;
        } else {
            angles[1] = QMath.atan2(2 * y * w - 2 * x * z, sqx - sqy - sqz + sqw); // roll or heading 
            angles[2] = QMath.asin(2 * test / unit); // pitch or attitude
            angles[0] = QMath.atan2(2 * x * w - 2 * y * z, -sqx + sqy - sqz + sqw); // yaw or bank
        }
        return angles;
    }

    /**
     *
     * <code>fromRotationMatrix</code> generates a quaternion from a supplied
     * matrix. This matrix is assumed to be a rotational matrix.
     *
     * @param matrix the matrix that defines the rotation.
     */
    public Cuaternion fromRotationMatrix(QMatriz3 matrix) {
        return fromRotationMatrix(matrix.m00, matrix.m01, matrix.m02, matrix.m10,
                matrix.m11, matrix.m12, matrix.m20, matrix.m21, matrix.m22);
    }

    public Cuaternion fromRotationMatrix(float m00, float m01, float m02,
            float m10, float m11, float m12, float m20, float m21, float m22) {
        // first normalize the forward (F), up (U) and side (S) vectors of the rotation matrix
        // so that the scale does not affect the rotation
        float lengthSquared = m00 * m00 + m10 * m10 + m20 * m20;
        if (lengthSquared != 1f && lengthSquared != 0f) {
            lengthSquared = 1.0f / QMath.sqrt(lengthSquared);
            m00 *= lengthSquared;
            m10 *= lengthSquared;
            m20 *= lengthSquared;
        }
        lengthSquared = m01 * m01 + m11 * m11 + m21 * m21;
        if (lengthSquared != 1f && lengthSquared != 0f) {
            lengthSquared = 1.0f / QMath.sqrt(lengthSquared);
            m01 *= lengthSquared;
            m11 *= lengthSquared;
            m21 *= lengthSquared;
        }
        lengthSquared = m02 * m02 + m12 * m12 + m22 * m22;
        if (lengthSquared != 1f && lengthSquared != 0f) {
            lengthSquared = 1.0f / QMath.sqrt(lengthSquared);
            m02 *= lengthSquared;
            m12 *= lengthSquared;
            m22 *= lengthSquared;
        }

        // Use the Graphics Gems code, from 
        // ftp://ftp.cis.upenn.edu/pub/graphics/shoemake/quatut.ps.Z
        // *NOT* the "Matrix and Quaternions FAQ", which has errors!
        // the trace is the sum of the diagonal elements; see
        // http://mathworld.wolfram.com/MatrixTrace.html
        float t = m00 + m11 + m22;

        // we protect the division by s by ensuring that s>=1
        if (t >= 0) { // |w| >= .5
            float s = QMath.sqrt(t + 1); // |s|>=1 ...
            w = 0.5f * s;
            s = 0.5f / s;                 // so this division isn't bad
            x = (m21 - m12) * s;
            y = (m02 - m20) * s;
            z = (m10 - m01) * s;
        } else if ((m00 > m11) && (m00 > m22)) {
            float s = QMath.sqrt(1.0f + m00 - m11 - m22); // |s|>=1
            x = s * 0.5f; // |x| >= .5
            s = 0.5f / s;
            y = (m10 + m01) * s;
            z = (m02 + m20) * s;
            w = (m21 - m12) * s;
        } else if (m11 > m22) {
            float s = QMath.sqrt(1.0f + m11 - m00 - m22); // |s|>=1
            y = s * 0.5f; // |y| >= .5
            s = 0.5f / s;
            x = (m10 + m01) * s;
            z = (m21 + m12) * s;
            w = (m02 - m20) * s;
        } else {
            float s = QMath.sqrt(1.0f + m22 - m00 - m11); // |s|>=1
            z = s * 0.5f; // |z| >= .5
            s = 0.5f / s;
            x = (m02 + m20) * s;
            y = (m21 + m12) * s;
            w = (m10 - m01) * s;
        }

        return this;
    }

    /**
     * <code>toRotationMatrix</code> converts this quaternion to a rotational
     * matrix. Note: the result is created from a normalized version of this
     * quat.
     *
     * @return the rotation matrix representation of this quaternion.
     */
    public QMatriz3 toRotationMatrix() {
        QMatriz3 matrix = new QMatriz3();
        return toRotationMatrix(matrix);
    }

    /**
     * <code>toRotationMatrix</code> converts this quaternion to a rotational
     * matrix. The result is stored in result.
     *
     * @param result The QMatriz3 to store the result in.
     * @return the rotation matrix representation of this quaternion.
     */
    public QMatriz3 toRotationMatrix(QMatriz3 result) {

        float norm = norm();
        // we explicitly test norm against one here, saving a division
        // at the cost of a test and branch.  Is it worth it?
        float s = (norm == 1f) ? 2f : (norm > 0f) ? 2f / norm : 0;

        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        float xs = x * s;
        float ys = y * s;
        float zs = z * s;
        float xx = x * xs;
        float xy = x * ys;
        float xz = x * zs;
        float xw = w * xs;
        float yy = y * ys;
        float yz = y * zs;
        float yw = w * ys;
        float zz = z * zs;
        float zw = w * zs;

        // using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
        result.m00 = 1 - (yy + zz);
        result.m01 = (xy - zw);
        result.m02 = (xz + yw);
        result.m10 = (xy + zw);
        result.m11 = 1 - (xx + zz);
        result.m12 = (yz - xw);
        result.m20 = (xz - yw);
        result.m21 = (yz + xw);
        result.m22 = 1 - (xx + yy);

        return result;
    }

    /**
     * <code>toRotationMatrix</code> converts this quaternion to a rotational
     * matrix. The result is stored in result. 4th row and 4th column values are
     * untouched. Note: the result is created from a normalized version of this
     * quat.
     *
     * @param result The QMatriz4 to store the result in.
     * @return the rotation matrix representation of this quaternion.
     */
    public QMatriz4 toRotationMatrix(QMatriz4 result) {

        QVector3 originalScale = new QVector3();

        result.toScaleVector(originalScale);
        result.setScale(1, 1, 1);
        float norm = norm();
        // we explicitly test norm against one here, saving a division
        // at the cost of a test and branch.  Is it worth it?
        float s = (norm == 1f) ? 2f : (norm > 0f) ? 2f / norm : 0;

        // compute xs/ys/zs first to save 6 multiplications, since xs/ys/zs
        // will be used 2-4 times each.
        float xs = x * s;
        float ys = y * s;
        float zs = z * s;
        float xx = x * xs;
        float xy = x * ys;
        float xz = x * zs;
        float xw = w * xs;
        float yy = y * ys;
        float yz = y * zs;
        float yw = w * ys;
        float zz = z * zs;
        float zw = w * zs;

        // using s=2/norm (instead of 1/norm) saves 9 multiplications by 2 here
        result.m00 = 1 - (yy + zz);
        result.m01 = (xy - zw);
        result.m02 = (xz + yw);
        result.m10 = (xy + zw);
        result.m11 = 1 - (xx + zz);
        result.m12 = (yz - xw);
        result.m20 = (xz - yw);
        result.m21 = (yz + xw);
        result.m22 = 1 - (xx + yy);

        result.setScale(originalScale);

        return result;
    }

    /**
     * <code>getRotationColumn</code> returns one of three columns specified by
     * the parameter. This column is returned as a <code>QVector3</code> object.
     *
     * @param i the column to retrieve. Must be between 0 and 2.
     * @return the column specified by the index.
     */
    public QVector3 getRotationColumn(int i) {
        return getRotationColumn(i, null);
    }

    /**
     * <code>getRotationColumn</code> returns one of three columns specified by
     * the parameter. This column is returned as a <code>QVector3</code> object.
     * The value is retrieved as if this quaternion was first normalized.
     *
     * @param i the column to retrieve. Must be between 0 and 2.
     * @param store the vector object to store the result in. if null, a new one
     * is created.
     * @return the column specified by the index.
     */
    public QVector3 getRotationColumn(int i, QVector3 store) {
        if (store == null) {
            store = new QVector3();
        }

        float norm = norm();
        if (norm != 1.0f) {
            norm = QMath.invSqrt(norm);
        }

        float xx = x * x * norm;
        float xy = x * y * norm;
        float xz = x * z * norm;
        float xw = x * w * norm;
        float yy = y * y * norm;
        float yz = y * z * norm;
        float yw = y * w * norm;
        float zz = z * z * norm;
        float zw = z * w * norm;

        switch (i) {
            case 0:
                store.x = 1 - 2 * (yy + zz);
                store.y = 2 * (xy + zw);
                store.z = 2 * (xz - yw);
                break;
            case 1:
                store.x = 2 * (xy - zw);
                store.y = 1 - 2 * (xx + zz);
                store.z = 2 * (yz + xw);
                break;
            case 2:
                store.x = 2 * (xz + yw);
                store.y = 2 * (yz - xw);
                store.z = 1 - 2 * (xx + yy);
                break;
            default:
                logger.warning("Invalid column index.");
                throw new IllegalArgumentException("Invalid column index. " + i);
        }

        return store;
    }

    /**
     * <code>fromAngleAxis</code> sets this quaternion to the values specified
     * by an angle and an axis of rotation. This method creates an object, so
     * use fromAngleNormalAxis if your axis is already normalized.
     *
     * @param angle the angle to rotate (in radians).
     * @param axis the axis of rotation.
     * @return this quaternion
     */
    public Cuaternion fromAngleAxis(float angle, QVector3 axis) {
        QVector3 normAxis = axis.clone();
        normAxis.normalize();
        fromAngleNormalAxis(angle, normAxis);
        return this;
    }

    /**
     * <code>fromAngleNormalAxis</code> sets this quaternion to the values
     * specified by an angle and a normalized axis of rotation.
     *
     * @param angle the angle to rotate (in radians).
     * @param axis the axis of rotation (already normalized).
     */
    public Cuaternion fromAngleNormalAxis(float angle, QVector3 axis) {
        if (axis.x == 0 && axis.y == 0 && axis.z == 0) {
            loadIdentity();
        } else {
            float halfAngle = 0.5f * angle;
            float sin = QMath.sin(halfAngle);
            w = QMath.cos(halfAngle);
            x = sin * axis.x;
            y = sin * axis.y;
            z = sin * axis.z;
        }
        return this;
    }

    /**
     * <code>toAngleAxis</code> sets a given angle and axis to that represented
     * by the current quaternion. The values are stored as follows: The axis is
     * provided as a parameter and built by the method, the angle is returned as
     * a float.
     *
     * @param axisStore the object we'll store the computed axis in.
     * @return the angle of rotation in radians.
     */
    public float toAngleAxis(QVector3 axisStore) {
        float sqrLength = x * x + y * y + z * z;
        float angle;
        if (sqrLength == 0.0f) {
            angle = 0.0f;
            if (axisStore != null) {
                axisStore.x = 1.0f;
                axisStore.y = 0.0f;
                axisStore.z = 0.0f;
            }
        } else {
            angle = (2.0f * QMath.acos(w));
            if (axisStore != null) {
                float invLength = (1.0f / QMath.sqrt(sqrLength));
                axisStore.x = x * invLength;
                axisStore.y = y * invLength;
                axisStore.z = z * invLength;
            }
        }

        return angle;
    }

    /**
     * <code>slerp</code> sets this quaternion's value as an interpolation
     * between two other quaternions.
     *
     * @param q1 the first quaternion.
     * @param q2 the second quaternion.
     * @param t the amount to interpolate between the two quaternions.
     */
    public Cuaternion slerp(Cuaternion q1, Cuaternion q2, float t) {
        // Create a local quaternion to store the interpolated quaternion
        if (q1.x == q2.x && q1.y == q2.y && q1.z == q2.z && q1.w == q2.w) {
            this.set(q1);
            return this;
        }

        float result = (q1.x * q2.x) + (q1.y * q2.y) + (q1.z * q2.z)
                + (q1.w * q2.w);

        if (result < 0.0f) {
            // Negate the second quaternion and the result of the dot product
            q2.x = -q2.x;
            q2.y = -q2.y;
            q2.z = -q2.z;
            q2.w = -q2.w;
            result = -result;
        }

        // Set the first and second scale for the interpolation
        float scale0 = 1 - t;
        float scale1 = t;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - result) > 0.1f) {// Get the angle between the 2 quaternions,
            // and then store the sin() of that angle
            float theta = QMath.acos(result);
            float invSinTheta = 1f / QMath.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = QMath.sin((1 - t) * theta) * invSinTheta;
            scale1 = QMath.sin((t * theta)) * invSinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special
        // form of linear interpolation for quaternions.
        this.x = (scale0 * q1.x) + (scale1 * q2.x);
        this.y = (scale0 * q1.y) + (scale1 * q2.y);
        this.z = (scale0 * q1.z) + (scale1 * q2.z);
        this.w = (scale0 * q1.w) + (scale1 * q2.w);

        // Return the interpolated quaternion
        return this;
    }

    /**
     * Sets the values of this quaternion to the slerp from itself to q2 by
     * changeAmnt
     *
     * @param q2 Final interpolation value
     * @param changeAmnt The amount diffrence
     */
    public void slerp(Cuaternion q2, float changeAmnt) {
        if (this.x == q2.x && this.y == q2.y && this.z == q2.z
                && this.w == q2.w) {
            return;
        }

        float result = (this.x * q2.x) + (this.y * q2.y) + (this.z * q2.z)
                + (this.w * q2.w);

        if (result < 0.0f) {
            // Negate the second quaternion and the result of the dot product
            q2.x = -q2.x;
            q2.y = -q2.y;
            q2.z = -q2.z;
            q2.w = -q2.w;
            result = -result;
        }

        // Set the first and second scale for the interpolation
        float scale0 = 1 - changeAmnt;
        float scale1 = changeAmnt;

        // Check if the angle between the 2 quaternions was big enough to
        // warrant such calculations
        if ((1 - result) > 0.1f) {
            // Get the angle between the 2 quaternions, and then store the sin()
            // of that angle
            float theta = QMath.acos(result);
            float invSinTheta = 1f / QMath.sin(theta);

            // Calculate the scale for q1 and q2, according to the angle and
            // it's sine value
            scale0 = QMath.sin((1 - changeAmnt) * theta) * invSinTheta;
            scale1 = QMath.sin((changeAmnt * theta)) * invSinTheta;
        }

        // Calculate the x, y, z and w values for the quaternion by using a
        // special
        // form of linear interpolation for quaternions.
        this.x = (scale0 * this.x) + (scale1 * q2.x);
        this.y = (scale0 * this.y) + (scale1 * q2.y);
        this.z = (scale0 * this.z) + (scale1 * q2.z);
        this.w = (scale0 * this.w) + (scale1 * q2.w);
    }

    /**
     * Sets the values of this quaternion to the nlerp from itself to q2 by
     * blend.
     *
     * @param q2
     * @param blend
     */
    public void nlerp(Cuaternion q2, float blend) {
        float dot = dot(q2);
        float blendI = 1.0f - blend;
        if (dot < 0.0f) {
            x = blendI * x - blend * q2.x;
            y = blendI * y - blend * q2.y;
            z = blendI * z - blend * q2.z;
            w = blendI * w - blend * q2.w;
        } else {
            x = blendI * x + blend * q2.x;
            y = blendI * y + blend * q2.y;
            z = blendI * z + blend * q2.z;
            w = blendI * w + blend * q2.w;
        }
        normalizeLocal();
    }

    /**
     * <code>add</code> adds the values of this quaternion to those of the
     * parameter quaternion. The result is returned as a new quaternion.
     *
     * @param q the quaternion to add to this.
     * @return the new quaternion.
     */
    public Cuaternion add(Cuaternion q) {
        return new Cuaternion(x + q.x, y + q.y, z + q.z, w + q.w);
    }

    /**
     * <code>add</code> adds the values of this quaternion to those of the
     * parameter quaternion. The result is stored in this Cuaternion.
     *
     * @param q the quaternion to add to this.
     * @return This Cuaternion after addition.
     */
    public Cuaternion addLocal(Cuaternion q) {
        this.x += q.x;
        this.y += q.y;
        this.z += q.z;
        this.w += q.w;
        return this;
    }

    /**
     * <code>subtract</code> subtracts the values of the parameter quaternion
     * from those of this quaternion. The result is returned as a new
     * quaternion.
     *
     * @param q the quaternion to subtract from this.
     * @return the new quaternion.
     */
    public Cuaternion subtract(Cuaternion q) {
        return new Cuaternion(x - q.x, y - q.y, z - q.z, w - q.w);
    }

    /**
     * <code>subtract</code> subtracts the values of the parameter quaternion
     * from those of this quaternion. The result is stored in this Cuaternion.
     *
     * @param q the quaternion to subtract from this.
     * @return This Cuaternion after subtraction.
     */
    public Cuaternion subtractLocal(Cuaternion q) {
        this.x -= q.x;
        this.y -= q.y;
        this.z -= q.z;
        this.w -= q.w;
        return this;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter quaternion.
     * The result is returned as a new quaternion. It should be noted that
     * quaternion multiplication is not commutative so q * p != p * q.
     *
     * @param q the quaternion to multiply this quaternion by.
     * @return the new quaternion.
     */
    public Cuaternion mult(Cuaternion q) {
        return mult(q, null);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter quaternion.
     * The result is returned as a new quaternion. It should be noted that
     * quaternion multiplication is not commutative so q * p != p * q.
     *
     * It IS safe for q and res to be the same object. It IS NOT safe for this
     * and res to be the same object.
     *
     * @param q the quaternion to multiply this quaternion by.
     * @param res the quaternion to store the result in.
     * @return the new quaternion.
     */
    public Cuaternion mult(Cuaternion q, Cuaternion res) {
        if (res == null) {
            res = new Cuaternion();
        }
        float qw = q.w, qx = q.x, qy = q.y, qz = q.z;
        res.x = x * qw + y * qz - z * qy + w * qx;
        res.y = -x * qz + y * qw + z * qx + w * qy;
        res.z = x * qy - y * qx + z * qw + w * qz;
        res.w = -x * qx - y * qy - z * qz + w * qw;
        return res;
    }

    /**
     * <code>apply</code> multiplies this quaternion by a parameter matrix
     * internally.
     *
     * @param matrix the matrix to apply to this quaternion.
     */
    public void apply(QMatriz3 matrix) {
        float oldX = x, oldY = y, oldZ = z, oldW = w;
        fromRotationMatrix(matrix);
        float tempX = x, tempY = y, tempZ = z, tempW = w;

        x = oldX * tempW + oldY * tempZ - oldZ * tempY + oldW * tempX;
        y = -oldX * tempZ + oldY * tempW + oldZ * tempX + oldW * tempY;
        z = oldX * tempY - oldY * tempX + oldZ * tempW + oldW * tempZ;
        w = -oldX * tempX - oldY * tempY - oldZ * tempZ + oldW * tempW;
    }

    /**
     *
     * <code>fromAxes</code> creates a <code>Cuaternion</code> that represents
     * the coordinate system defined by three axes. These axes are assumed to be
     * orthogonal and no error checking is applied. Thus, the user must insure
     * that the three axes being provided indeed represents a proper right
     * handed coordinate system.
     *
     * @param axis the array containing the three vectors representing the
     * coordinate system.
     */
    public Cuaternion fromAxes(QVector3[] axis) {
        if (axis.length != 3) {
            throw new IllegalArgumentException(
                    "Axis array must have three elements");
        }
        return fromAxes(axis[0], axis[1], axis[2]);
    }

    /**
     *
     * <code>fromAxes</code> creates a <code>Cuaternion</code> that represents
     * the coordinate system defined by three axes. These axes are assumed to be
     * orthogonal and no error checking is applied. Thus, the user must insure
     * that the three axes being provided indeed represents a proper right
     * handed coordinate system.
     *
     * @param xAxis vector representing the x-axis of the coordinate system.
     * @param yAxis vector representing the y-axis of the coordinate system.
     * @param zAxis vector representing the z-axis of the coordinate system.
     */
    public Cuaternion fromAxes(QVector3 xAxis, QVector3 yAxis, QVector3 zAxis) {
        return fromRotationMatrix(xAxis.x, yAxis.x, zAxis.x, xAxis.y, yAxis.y,
                zAxis.y, xAxis.z, yAxis.z, zAxis.z);
    }

    /**
     *
     * <code>toAxes</code> takes in an array of three vectors. Each vector
     * corresponds to an axis of the coordinate system defined by the quaternion
     * rotation.
     *
     * @param axis the array of vectors to be filled.
     */
    public void toAxes(QVector3 axis[]) {
        QMatriz3 tempMat = toRotationMatrix();
        axis[0] = tempMat.getColumn(0, axis[0]);
        axis[1] = tempMat.getColumn(1, axis[1]);
        axis[2] = tempMat.getColumn(2, axis[2]);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter vector. The
     * result is returned as a new vector.
     *
     * @param v the vector to multiply this quaternion by.
     * @return the new vector.
     */
    public QVector3 mult(QVector3 v) {
        return mult(v, null);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter vector. The
     * result is stored in the supplied vector
     *
     * @param v the vector to multiply this quaternion by.
     * @return v
     */
    public QVector3 multLocal(QVector3 v) {
        float tempX, tempY;
        tempX = w * w * v.x + 2 * y * w * v.z - 2 * z * w * v.y + x * x * v.x
                + 2 * y * x * v.y + 2 * z * x * v.z - z * z * v.x - y * y * v.x;
        tempY = 2 * x * y * v.x + y * y * v.y + 2 * z * y * v.z + 2 * w * z
                * v.x - z * z * v.y + w * w * v.y - 2 * x * w * v.z - x * x
                * v.y;
        v.z = 2 * x * z * v.x + 2 * y * z * v.y + z * z * v.z - 2 * w * y * v.x
                - y * y * v.z + 2 * w * x * v.y - x * x * v.z + w * w * v.z;
        v.x = tempX;
        v.y = tempY;
        return v;
    }

    /**
     * Multiplies this Cuaternion by the supplied quaternion. The result is
     * stored in this Cuaternion, which is also returned for chaining. Similar
     * to this *= q.
     *
     * @param q The Cuaternion to multiply this one by.
     * @return This Cuaternion, after multiplication.
     */
    public Cuaternion multLocal(Cuaternion q) {
        float x1 = x * q.w + y * q.z - z * q.y + w * q.x;
        float y1 = -x * q.z + y * q.w + z * q.x + w * q.y;
        float z1 = x * q.y - y * q.x + z * q.w + w * q.z;
        w = -x * q.x - y * q.y - z * q.z + w * q.w;
        x = x1;
        y = y1;
        z = z1;
        return this;
    }

    /**
     * Multiplies this Cuaternion by the supplied quaternion. The result is
     * stored in this Cuaternion, which is also returned for chaining. Similar
     * to this *= q.
     *
     * @param qx - quat x value
     * @param qy - quat y value
     * @param qz - quat z value
     * @param qw - quat w value
     *
     * @return This Cuaternion, after multiplication.
     */
    public Cuaternion multLocal(float qx, float qy, float qz, float qw) {
        float x1 = x * qw + y * qz - z * qy + w * qx;
        float y1 = -x * qz + y * qw + z * qx + w * qy;
        float z1 = x * qy - y * qx + z * qw + w * qz;
        w = -x * qx - y * qy - z * qz + w * qw;
        x = x1;
        y = y1;
        z = z1;
        return this;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter vector. The
     * result is returned as a new vector.
     *
     * @param v the vector to multiply this quaternion by.
     * @param store the vector to store the result in. It IS safe for v and
     * store to be the same object.
     * @return the result vector.
     */
    public QVector3 mult(QVector3 v, QVector3 store) {
        if (store == null) {
            store = new QVector3();
        }
        if (v.x == 0 && v.y == 0 && v.z == 0) {
            store.set(0, 0, 0);
        } else {
            float vx = v.x, vy = v.y, vz = v.z;
            store.x = w * w * vx + 2 * y * w * vz - 2 * z * w * vy + x * x
                    * vx + 2 * y * x * vy + 2 * z * x * vz - z * z * vx - y
                    * y * vx;
            store.y = 2 * x * y * vx + y * y * vy + 2 * z * y * vz + 2 * w
                    * z * vx - z * z * vy + w * w * vy - 2 * x * w * vz - x
                    * x * vy;
            store.z = 2 * x * z * vx + 2 * y * z * vy + z * z * vz - 2 * w
                    * y * vx - y * y * vz + 2 * w * x * vy - x * x * vz + w
                    * w * vz;
        }
        return store;
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter scalar. The
     * result is returned as a new quaternion.
     *
     * @param scalar the quaternion to multiply this quaternion by.
     * @return the new quaternion.
     */
    public Cuaternion mult(float scalar) {
        return new Cuaternion(scalar * x, scalar * y, scalar * z, scalar * w);
    }

    /**
     * <code>mult</code> multiplies this quaternion by a parameter scalar. The
     * result is stored locally.
     *
     * @param scalar the quaternion to multiply this quaternion by.
     * @return this.
     */
    public Cuaternion multLocal(float scalar) {
        w *= scalar;
        x *= scalar;
        y *= scalar;
        z *= scalar;
        return this;
    }

    /**
     * <code>dot</code> calculates and returns the dot product of this
     * quaternion with that of the parameter quaternion.
     *
     * @param q the quaternion to calculate the dot product of.
     * @return the dot product of this and the parameter quaternion.
     */
    public float dot(Cuaternion q) {
        return w * q.w + x * q.x + y * q.y + z * q.z;
    }

    /**
     * <code>norm</code> returns the norm of this quaternion. This is the dot
     * product of this quaternion with itself.
     *
     * @return the norm of the quaternion.
     */
    public float norm() {
        return w * w + x * x + y * y + z * z;
    }

//    /**
//     * <code>normalize</code> normalizes the current <code>Cuaternion</code>
//     * @deprecated The naming of this method doesn't follow convention.
//     * Please use {@link Cuaternion#normalizeLocal() } instead.
//     */
//    @Deprecated
//    public void normalize() {
//        float n = QMath.invSqrt(norm());
//        x *= n;
//        y *= n;
//        z *= n;
//        w *= n;
//    }
    /**
     * <code>normalize</code> normalizes the current <code>Cuaternion</code>.
     * The result is stored internally.
     */
    public Cuaternion normalizeLocal() {
        float n = QMath.invSqrt(norm());
        x *= n;
        y *= n;
        z *= n;
        w *= n;
        return this;
    }

    /**
     * <code>inverse</code> returns the inverse of this quaternion as a new
     * quaternion. If this quaternion does not have an inverse (if its normal is
     * 0 or less), then null is returned.
     *
     * @return the inverse of this quaternion or null if the inverse does not
     * exist.
     */
    public Cuaternion inverse() {
        float norm = norm();
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            return new Cuaternion(-x * invNorm, -y * invNorm, -z * invNorm, w
                    * invNorm);
        }
        // return an invalid result to flag the error
        return null;
    }

    /**
     * <code>inverse</code> calculates the inverse of this quaternion and
     * returns this quaternion after it is calculated. If this quaternion does
     * not have an inverse (if it's normal is 0 or less), nothing happens
     *
     * @return the inverse of this quaternion
     */
    public Cuaternion inverseLocal() {
        float norm = norm();
        if (norm > 0.0) {
            float invNorm = 1.0f / norm;
            x *= -invNorm;
            y *= -invNorm;
            z *= -invNorm;
            w *= invNorm;
        }
        return this;
    }

    /**
     * <code>negate</code> inverts the values of the quaternion.
     *
     */
    public void negate() {
        x *= -1;
        y *= -1;
        z *= -1;
        w *= -1;
    }

    /**
     *
     * <code>toString</code> creates the string representation of this
     * <code>Cuaternion</code>. The values of the quaternion are displaced (x,
     * y, z, w), in the following manner: <br>
     * (x, y, z, w)
     *
     * @return the string representation of this object.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ", " + w + ")";
    }

    /**
     * <code>equals</code> determines if two quaternions are logically equal,
     * that is, if the values of (x, y, z, w) are the same for both quaternions.
     *
     * @param o the object to compare for equality
     * @return true if they are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cuaternion)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        Cuaternion comp = (Cuaternion) o;
        if (Float.compare(x, comp.x) != 0) {
            return false;
        }
        if (Float.compare(y, comp.y) != 0) {
            return false;
        }
        if (Float.compare(z, comp.z) != 0) {
            return false;
        }
        if (Float.compare(w, comp.w) != 0) {
            return false;
        }
        return true;
    }

    /**
     *
     * <code>hashCode</code> returns the hash code value as an integer and is
     * supported for the benefit of hashing based collection classes such as
     * Hashtable, HashMap, HashSet etc.
     *
     * @return the hashcode for this instance of Cuaternion.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 37;
        hash = 37 * hash + Float.floatToIntBits(x);
        hash = 37 * hash + Float.floatToIntBits(y);
        hash = 37 * hash + Float.floatToIntBits(z);
        hash = 37 * hash + Float.floatToIntBits(w);
        return hash;

    }

    /**
     * <code>readExternal</code> builds a quaternion from an
     * <code>ObjectInput</code> object. <br>
     * NOTE: Used with serialization. Not to be called manually.
     *
     * @param in the ObjectInput value to read from.
     * @throws IOException if the ObjectInput value has problems reading a
     * float.
     * @see java.io.Externalizable
     */
    public void readExternal(ObjectInput in) throws IOException {
        x = in.readFloat();
        y = in.readFloat();
        z = in.readFloat();
        w = in.readFloat();
    }

    /**
     * <code>writeExternal</code> writes this quaternion out to a
     * <code>ObjectOutput</code> object. NOTE: Used with serialization. Not to
     * be called manually.
     *
     * @param out the object to write to.
     * @throws IOException if writing to the ObjectOutput fails.
     * @see java.io.Externalizable
     */
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeFloat(x);
        out.writeFloat(y);
        out.writeFloat(z);
        out.writeFloat(w);
    }

    /**
     * <code>lookAt</code> is a convienence method for auto-setting the
     * quaternion based on a direction and an up vector. It computes the
     * rotation to transform the z-axis to point into 'direction' and the y-axis
     * to 'up'.
     *
     * @param direction where to look at in terms of local coordinates
     * @param up a vector indicating the local up direction. (typically {0, 1,
     * 0} in jME.)
     */
    public void lookAt(QVector3 direction, QVector3 up) {
        QVector3 ejeDerecha = up.clone().cross(direction);
        ejeDerecha.normalize();
        QVector3 ejeArriba = direction.clone().cross(ejeDerecha);
        QVector3 ejeAdelante = direction.clone();
        ejeAdelante.normalize();
        fromAxes(ejeDerecha, ejeArriba, ejeAdelante);
    }

    /**
     * @return A new quaternion that describes a rotation that would point you
     * in the exact opposite direction of this Cuaternion.
     */
    public Cuaternion opposite() {
        return opposite(null);
    }

    /**
     * FIXME: This seems to have singularity type issues with angle == 0,
     * possibly others such as PI.
     *
     * @param store A Cuaternion to store our result in. If null, a new one is
     * created.
     * @return The store quaternion (or a new Quaterion, if store is null) that
     * describes a rotation that would point you in the exact opposite direction
     * of this Cuaternion.
     */
    public Cuaternion opposite(Cuaternion store) {
        if (store == null) {
            store = new Cuaternion();
        }

        QVector3 axis = new QVector3();
        float angle = toAngleAxis(axis);

        store.fromAngleAxis(QMath.PI + angle, axis);
        return store;
    }

    /**
     * @return This Cuaternion, altered to describe a rotation that would point
     * you in the exact opposite direction of where it is pointing currently.
     */
    public Cuaternion oppositeLocal() {
        return opposite(this);
    }

    @Override
    public Cuaternion clone() {
        return new Cuaternion(x, y, z, w);
//        try {
//            return (Cuaternion) super.clone();
//   
//        } catch (CloneNotSupportedException e) {
//            throw new AssertionError(); // can not happen
//        }
    }

    /**
     * Interpolates between two quaternion rotations and returns the resulting
     * quaternion rotation. The interpolation method here is "nlerp", or
     * "normalized-lerp". Another mnethod that could be used is "slerp", and you
     * can see a comparison of the methods here:
     * https://keithmaggio.wordpress.com/2011/02/15/math-magician-lerp-slerp-and-nlerp/
     *
     * and here:
     * http://number-none.com/product/Understanding%20Slerp,%20Then%20Not%20Using%20It/
     *
     * @param a
     * @param b
     * @param blend - a value between 0 and 1 indicating how far to interpolate
     * between the two quaternions.
     * @return The resulting interpolated rotation in quaternion format.
     */
    public static Cuaternion interpolate(Cuaternion a, Cuaternion b, float blend) {
        Cuaternion result = new Cuaternion(0, 0, 0, 1);
        float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
        float blendI = 1f - blend;
        if (dot < 0) {
            result.w = blendI * a.w + blend * -b.w;
            result.x = blendI * a.x + blend * -b.x;
            result.y = blendI * a.y + blend * -b.y;
            result.z = blendI * a.z + blend * -b.z;
        } else {
            result.w = blendI * a.w + blend * b.w;
            result.x = blendI * a.x + blend * b.x;
            result.y = blendI * a.y + blend * b.y;
            result.z = blendI * a.z + blend * b.z;
        }
        result.normalizeLocal();
        return result;
    }
}
