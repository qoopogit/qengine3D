package net.qoopo.engine3d.core.math;

import java.util.Random;
import net.qoopo.engine3d.componentes.geometria.primitivas.QVertice;
import net.qoopo.engine3d.core.math.tablas.LUT;
import net.qoopo.engine3d.core.util.TempVars;

final public class QMath {

    public static final boolean TABLAS_ACTIVO = true;
//    public static final boolean TABLAS_ACTIVO = false;

    /**
     * A "close to zero" double epsilon value for use
     */
    public static final double DBL_EPSILON = 2.220446049250313E-16d;
    /**
     * A "close to zero" float epsilon value for use
     */
    public static final float FLT_EPSILON = 1.1920928955078125E-7f;
    /**
     * A "close to zero" float epsilon value for use
     */
    public static final float ZERO_TOLERANCE = 0.0001f;
    public static final float ONE_THIRD = 1f / 3f;
    /**
     * The value PI as a float. (180 degrees)
     */
    public static final float PI = (float) Math.PI;
    /**
     * The value 2PI as a float. (360 degrees)
     */
    public static final float TWO_PI = 2.0f * PI;
    /**
     * The value PI/2 as a float. (90 degrees)
     */
    public static final float HALF_PI = 0.5f * PI;
    /**
     * The value PI/4 as a float. (45 degrees)
     */
    public static final float QUARTER_PI = 0.25f * PI;
    /**
     * The value 1/PI as a float.
     */
    public static final float INV_PI = 1.0f / PI;
    /**
     * The value 1/(2PI) as a float.
     */
    public static final float INV_TWO_PI = 1.0f / TWO_PI;
    /**
     * A value to multiply a degree value by, to convert it to radians.
     */
    public static final float DEG_TO_RAD = PI / 180.0f;
    /**
     * A value to multiply a radian value by, to convert it to degrees.
     */
    public static final float RAD_TO_DEG = 180.0f / PI;
    /**
     * A precreated random object for random numbers.
     */
    public static final Random rand = new Random(System.currentTimeMillis());

    /**
     * Returns true if the number is a power of 2 (2,4,8,16...)
     *
     * A good implementation found on the Java boards. note: a number is a power
     * of two if and only if it is the smallest number with that number of
     * significant bits. Therefore, if you subtract 1, you know that the new
     * number will have fewer bits, so ANDing the original number with anything
     * less than it will give 0.
     *
     * @param number The number to test.
     * @return True if it is a power of two.
     */
    public static boolean isPowerOfTwo(int number) {
        return (number > 0) && (number & (number - 1)) == 0;
    }

    /**
     * Get the next power of two of the given number.
     *
     * E.g. for an input 100, this returns 128. Returns 1 for all numbers <= 1.
     *
     * @param number The number to obtain the POT for.
     * @return The next power of two.
     */
    public static int nearestPowerOfTwo(int number) {
        number--;
        number |= number >> 1;
        number |= number >> 2;
        number |= number >> 4;
        number |= number >> 8;
        number |= number >> 16;
        number++;
        number += (number == 0) ? 1 : 0;
        return number;
    }

    /**
     * Linear interpolation from startValue to endValue by the given percent.
     * Basically: ((1 - percent) * startValue) + (percent * endValue)
     *
     * @param scale scale value to use. if 1, use endValue, if 0, use
     * startValue.
     * @param startValue Beginning value. 0% of f
     * @param endValue ending value. 100% of f
     * @return The interpolated value between startValue and endValue.
     */
    public static float interpolateLinear(float scale, float startValue, float endValue) {
        if (startValue == endValue) {
            return startValue;
        }
        if (scale <= 0f) {
            return startValue;
        }
        if (scale >= 1f) {
            return endValue;
        }
        return ((1f - scale) * startValue) + (scale * endValue);
    }

    public static float linear(float alpha, float start, float end) {
        if (alpha < 0) {
            return start;
        }
        if (alpha > 1) {
            return end;
        }
        return start + (end - start) * alpha;
    }

//    public static void linear(QIluminacion newIllumination, float alpha, QIluminacion start, QIluminacion end) {
//        newIllumination.dR = linear(alpha, start.dR, end.dR);
//        newIllumination.dG = linear(alpha, start.dG, end.dG);
//        newIllumination.dB = linear(alpha, start.dB, end.dB);
//        newIllumination.sR = linear(alpha, start.sR, end.sR);
//        newIllumination.sG = linear(alpha, start.sG, end.sG);
//        newIllumination.sB = linear(alpha, start.sB, end.sB);
//    }
    public static void linear(QVertice newVertex, float alpha, QVertice start, QVertice end) {
        newVertex.ubicacion.x = linear(alpha, start.ubicacion.x, end.ubicacion.x);
        newVertex.ubicacion.y = linear(alpha, start.ubicacion.y, end.ubicacion.y);
        newVertex.ubicacion.z = linear(alpha, start.ubicacion.z, end.ubicacion.z);
        newVertex.u = linear(alpha, start.u, end.u);
        newVertex.v = linear(alpha, start.v, end.v);
        newVertex.normal.x = linear(alpha, start.normal.x, end.normal.x);
        newVertex.normal.y = linear(alpha, start.normal.y, end.normal.y);
        newVertex.normal.z = linear(alpha, start.normal.z, end.normal.z);
    }

    public static void linear(QVector2 newVector, float alpha, QVector2 start, QVector2 end) {
        newVector.x = linear(alpha, start.x, end.x);
        newVector.y = linear(alpha, start.y, end.y);
    }

    public static void linear(QVector3 newVector, float alpha, QVector3 start, QVector3 end) {
        newVector.x = linear(alpha, start.x, end.x);
        newVector.y = linear(alpha, start.y, end.y);
        newVector.z = linear(alpha, start.z, end.z);
    }

    public static void linear(QVector4 newVector, float alpha, QVector4 start, QVector4 end) {
        newVector.x = linear(alpha, start.x, end.x);
        newVector.y = linear(alpha, start.y, end.y);
        newVector.z = linear(alpha, start.z, end.z);
        newVector.w = linear(alpha, start.w, end.w);
    }

//    public static void linear(QPoligono.UVCoordinate newUV, float alpha, QPoligono.UVCoordinate start, QPoligono.UVCoordinate end) {
//        newUV.u = linear(alpha, start.u, end.u);
//        newUV.v = linear(alpha, start.v, end.v);
//    }
    public static float linear(int startProgress, int endProgress, int progress, int start, int end) {
        return startProgress == endProgress
                ? start
                : start + (end - start) * (progress - startProgress)
                / (endProgress - startProgress);
    }

    public static float linear(float startProgress, float endProgress, float progress, float start, float end) {
        return startProgress == endProgress
                ? start
                : start + (end - start) * (progress - startProgress)
                / (endProgress - startProgress);
    }

    /**
     * Linear extrapolation from startValue to endValue by the given scale. if
     * scale is between 0 and 1 this method returns the same result as
     * interpolateLinear if the scale is over 1 the value is linearly
     * extrapolated. Note that the end value is the value for a scale of 1.
     *
     * @param scale the scale for extrapolation
     * @param startValue the starting value (scale = 0)
     * @param endValue the end value (scale = 1)
     * @return an extrapolation for the given parameters
     */
    public static float extrapolateLinear(float scale, float startValue, float endValue) {
//        if (scale <= 0f) {
//            return startValue;
//        }
        return ((1f - scale) * startValue) + (scale * endValue);
    }

    /**
     * Interpolate a spline between at least 4 control points following the
     * Catmull-Rom equation. here is the interpolation matrix m = [ 0.0 1.0 0.0
     * 0.0 ] [-T 0.0 T 0.0 ] [ 2T T-3 3-2T -T ] [-T 2-T T-2 T ] where T is the
     * curve tension the result is a value between p1 and p2, t=0 for p1, t=1
     * for p2
     *
     * @param u value from 0 to 1
     * @param T The tension of the curve
     * @param p0 control point 0
     * @param p1 control point 1
     * @param p2 control point 2
     * @param p3 control point 3
     * @return Catmull–Rom interpolation
     */
    public static float interpolateCatmullRom(float u, float T, float p0, float p1, float p2, float p3) {
        float c1, c2, c3, c4;
        c1 = p1;
        c2 = -1.0f * T * p0 + T * p2;
        c3 = 2 * T * p0 + (T - 3) * p1 + (3 - 2 * T) * p2 + -T * p3;
        c4 = -T * p0 + (2 - T) * p1 + (T - 2) * p2 + T * p3;

        return (float) (((c4 * u + c3) * u + c2) * u + c1);
    }

    /**
     * Interpolate a spline between at least 4 control points following the
     * Bezier equation. here is the interpolation matrix m = [ -1.0 3.0 -3.0 1.0
     * ] [ 3.0 -6.0 3.0 0.0 ] [ -3.0 3.0 0.0 0.0 ] [ 1.0 0.0 0.0 0.0 ] where T
     * is the curve tension the result is a value between p1 and p3, t=0 for p1,
     * t=1 for p3
     *
     * @param u value from 0 to 1
     * @param p0 control point 0
     * @param p1 control point 1
     * @param p2 control point 2
     * @param p3 control point 3
     * @return Bezier interpolation
     */
    public static float interpolateBezier(float u, float p0, float p1, float p2, float p3) {
        float oneMinusU = 1.0f - u;
        float oneMinusU2 = oneMinusU * oneMinusU;
        float u2 = u * u;
        return p0 * oneMinusU2 * oneMinusU
                + 3.0f * p1 * u * oneMinusU2
                + 3.0f * p2 * u2 * oneMinusU
                + p3 * u2 * u;
    }

    /**
     * Returns the arc cosine of a value.<br>
     * Special cases:
     * <ul><li>If fValue is smaller than -1, then the result is PI.
     * <li>If the argument is greater than 1, then the result is 0.</ul>
     *
     * @param fValue The value to arc cosine.
     * @return The angle, in radians.
     * @see java.lang.Math#acos(double)
     */
    public static float acos(float fValue) {
        if (-1.0f < fValue) {
            if (fValue < 1.0f) {
                return (float) Math.acos(fValue);
            }

            return 0.0f;
        }

        return PI;
    }

    /**
     * Returns the arc sine of a value.<br>
     * Special cases:
     * <ul><li>If fValue is smaller than -1, then the result is -HALF_PI.
     * <li>If the argument is greater than 1, then the result is HALF_PI.</ul>
     *
     * @param fValue The value to arc sine.
     * @return the angle in radians.
     * @see java.lang.Math#asin(double)
     */
    public static float asin(float fValue) {
        if (-1.0f < fValue) {
            if (fValue < 1.0f) {
                return (float) Math.asin(fValue);
            }

            return HALF_PI;
        }

        return -HALF_PI;
    }

    /**
     * Returns the arc tangent of an angle given in radians.<br>
     *
     * @param fValue The angle, in radians.
     * @return fValue's atan
     * @see java.lang.Math#atan(double)
     */
    public static float atan(float fValue) {
        return (float) Math.atan(fValue);
    }

    /**
     * A direct call to Math.atan2.
     *
     * @param fY
     * @param fX
     * @return Math.atan2(fY,fX)
     * @see java.lang.Math#atan2(double, double)
     */
    public static float atan2(float fY, float fX) {
        return (float) Math.atan2(fY, fX);
    }

    /**
     * Rounds a fValue up. A call to Math.ceil
     *
     * @param fValue The value.
     * @return The fValue rounded up
     * @see java.lang.Math#ceil(double)
     */
    public static float ceil(float fValue) {
        return (float) Math.ceil(fValue);
    }

    /**
     * Retorna el coseno de un angulo. Si esta ACTIVO el uso de tablas, llama a
     * las tablas de cosenos
     *
     * @see Math#cos(double)
     * @param v The angle to cosine.
     * @return the cosine of the angle.
     */
    public static float cos(float v) {
        if (TABLAS_ACTIVO) {
//            return QTablas.coseno(v);
//            LUT.initialize();
            return LUT.cosLut(v);
//            return Tablas.cos((float) Math.toDegrees(v));
        } else {
            return (float) Math.cos(v);
        }
    }

    /**
     * Returns the sine of an angle. Direct call to java.lang.Math
     *
     * @see Math#sin(double)
     * @param v The angle to sine.
     * @return the sine of the angle.
     */
    public static float sin(float v) {
        if (TABLAS_ACTIVO) {
//            return QTablas.seno(v);
//            LUT.initialize();
            return LUT.sinLut(v);
//            return Tablas.sin((float) Math.toDegrees(v));
        } else {
            return (float) Math.sin(v);
        }
    }

    /**
     * Returns E^fValue
     *
     * @param fValue Value to raise to a power.
     * @return The value E^fValue
     * @see java.lang.Math#exp(double)
     */
    public static float exp(float fValue) {
        return (float) Math.exp(fValue);
    }

    /**
     * Returns Absolute value of a float.
     *
     * @param fValue The value to abs.
     * @return The abs of the value.
     * @see java.lang.Math#abs(float)
     */
    public static float abs(float fValue) {
        if (fValue < 0) {
            return -fValue;
        }
        return fValue;
    }

    /**
     * Returns a number rounded down.
     *
     * @param fValue The value to round
     * @return The given number rounded down
     * @see java.lang.Math#floor(double)
     */
    public static float floor(float fValue) {
        return (float) Math.floor(fValue);
    }

    /**
     * Returns 1/sqrt(fValue)
     *
     * @param fValue The value to process.
     * @return 1/sqrt(fValue)
     * @see java.lang.Math#sqrt(double)
     */
    public static float invSqrt(float fValue) {
        return (float) (1.0f / Math.sqrt(fValue));
    }

    public static float fastInvSqrt(float x) {
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x); // get bits for floating value
        i = 0x5f375a86 - (i >> 1);       // gives initial guess y0
        x = Float.intBitsToFloat(i);     // convert bits back to float
        x = x * (1.5f - xhalf * x * x);  // Newton step, repeating increases accuracy
        return x;
    }

    /**
     * Returns the log base E of a value.
     *
     * @param fValue The value to log.
     * @return The log of fValue base E
     * @see java.lang.Math#log(double)
     */
    public static float log(float fValue) {
        return (float) Math.log(fValue);
    }

    /**
     * Returns the logarithm of value with given base, calculated as
     * log(value)/log(base), so that pow(base, return)==value (contributed by
     * vear)
     *
     * @param value The value to log.
     * @param base Base of logarithm.
     * @return The logarithm of value with given base
     */
    public static float log(float value, float base) {
        return (float) (Math.log(value) / Math.log(base));
    }

    /**
     * Returns a number raised to an exponent power. fBase^fExponent
     *
     * @param fBase The base value (IE 2)
     * @param fExponent The exponent value (IE 3)
     * @return base raised to exponent (IE 8)
     * @see java.lang.Math#pow(double, double)
     */
    public static float pow(float fBase, float fExponent) {
        return (float) Math.pow(fBase, fExponent);
    }

    /**
     * Returns the value squared. fValue ^ 2
     *
     * @param fValue The value to square.
     * @return The square of the given value.
     */
    public static float sqr(float fValue) {
        return fValue * fValue;
    }

    /**
     * Returns the square root of a given value.
     *
     * @param fValue The value to sqrt.
     * @return The square root of the given value.
     * @see java.lang.Math#sqrt(double)
     */
    public static float sqrt(float fValue) {
        return (float) Math.sqrt(fValue);
    }

    /**
     * Returns the tangent of a value. If USE_FAST_TRIG is enabled, an
     * approximate value is returned. Otherwise, a direct value is used.
     *
     * @param fValue The value to tangent, in radians.
     * @return The tangent of fValue.
     * @see java.lang.Math#tan(double)
     */
    public static float tan(float fValue) {
        return (float) Math.tan(fValue);
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0
     * otherwise
     *
     * @param iValue The integer to examine.
     * @return The integer's sign.
     */
    public static int sign(int iValue) {
        if (iValue > 0) {
            return 1;
        }
        if (iValue < 0) {
            return -1;
        }
        return 0;
    }

    /**
     * Returns 1 if the number is positive, -1 if the number is negative, and 0
     * otherwise
     *
     * @param fValue The float to examine.
     * @return The float's sign.
     */
    public static float sign(float fValue) {
        return Math.signum(fValue);
    }

    /**
     * Returns the determinant of a 4x4 matrix.
     */
    public static float determinant(double m00, double m01, double m02,
            double m03, double m10, double m11, double m12, double m13,
            double m20, double m21, double m22, double m23, double m30,
            double m31, double m32, double m33) {

        double det01 = m20 * m31 - m21 * m30;
        double det02 = m20 * m32 - m22 * m30;
        double det03 = m20 * m33 - m23 * m30;
        double det12 = m21 * m32 - m22 * m31;
        double det13 = m21 * m33 - m23 * m31;
        double det23 = m22 * m33 - m23 * m32;
        return (float) (m00 * (m11 * det23 - m12 * det13 + m13 * det12) - m01
                * (m10 * det23 - m12 * det03 + m13 * det02) + m02
                * (m10 * det13 - m11 * det03 + m13 * det01) - m03
                * (m10 * det12 - m11 * det02 + m12 * det01));
    }

    /**
     * Returns a random float between 0 and 1.
     *
     * @return A random float between <tt>0.0f</tt> (inclusive) to
     * <tt>1.0f</tt> (exclusive).
     */
    public static float nextRandomFloat() {
        return rand.nextFloat();
    }

    /**
     * Returns a random integer between min and max.
     *
     * @return A random int between <tt>min</tt> (inclusive) to
     * <tt>max</tt> (inclusive).
     */
    public static int nextRandomInt(int min, int max) {
        return (int) (nextRandomFloat() * (max - min + 1)) + min;
    }

    public static int nextRandomInt() {
        return rand.nextInt();
    }

    /**
     * Takes an value and expresses it in terms of min to max.
     *
     * @param val - the angle to normalize (in radians)
     * @return the normalized angle (also in radians)
     */
    public static float normalize(float val, float min, float max) {
        if (Float.isInfinite(val) || Float.isNaN(val)) {
            return 0f;
        }
        float range = max - min;
        while (val > max) {
            val -= range;
        }
        while (val < min) {
            val += range;
        }
        return val;
    }

    /**
     * @param x the value whose sign is to be adjusted.
     * @param y the value whose sign is to be used.
     * @return x with its sign changed to match the sign of y.
     */
    public static float copysign(float x, float y) {
        if (y >= 0 && x <= -0) {
            return -x;
        } else if (y < 0 && x >= 0) {
            return -x;
        } else {
            return x;
        }
    }

    /**
     * Take a float input and clamp it between min and max.
     *
     * @param input
     * @param min
     * @param max
     * @return clamped input
     */
    public static float clamp(float input, float min, float max) {
        return (input < min) ? min : (input > max) ? max : input;
    }

    public static int clamp(int input, int min, int max) {
        return (input < min) ? min : (input > max) ? max : input;
    }

    /**
     * Clamps the given float to be between 0 and 1.
     *
     * @param input
     * @return input clamped between 0 and 1.
     */
    public static float saturate(float input) {
        return clamp(input, 0f, 1f);
    }

    /**
     * Determine if two floats are approximately equal. This takes into account
     * the magnitude of the floats, since large numbers will have larger
     * differences be close to each other.
     *
     * Should return true for a=100000, b=100001, but false for a=10000,
     * b=10001.
     *
     * @param a The first float to compare
     * @param b The second float to compare
     * @return True if a and b are approximately equal, false otherwise.
     */
    public static boolean approximateEquals(float a, float b) {
        if (a == b) {
            return true;
        } else {
            return (abs(a - b) / Math.max(abs(a), abs(b))) <= 0.00001f;
        }
    }

    /**
     * Converts a single precision (32 bit) floating point value into half
     * precision (16 bit).
     *
     * <p>
     * Source: <a
     * href="ftp://www.fox-toolkit.org/pub/fasthalffloatconversion.pdf</a>
     *
     * @param half The half floating point value as a short.
     * @return floating point value of the half.
     */
    public static float convertHalfToFloat(short half) {
        switch ((int) half) {
            case 0x0000:
                return 0f;
            case 0x8000:
                return -0f;
            case 0x7c00:
                return Float.POSITIVE_INFINITY;
            case 0xfc00:
                return Float.NEGATIVE_INFINITY;
            // TODO: Support for NaN?
            default:
                return Float.intBitsToFloat(((half & 0x8000) << 16)
                        | (((half & 0x7c00) + 0x1C000) << 13)
                        | ((half & 0x03FF) << 13));
        }
    }

    public static short convertFloatToHalf(float flt) {
        if (Float.isNaN(flt)) {
            throw new UnsupportedOperationException("NaN to half conversion not supported!");
        } else if (flt == Float.POSITIVE_INFINITY) {
            return (short) 0x7c00;
        } else if (flt == Float.NEGATIVE_INFINITY) {
            return (short) 0xfc00;
        } else if (flt == 0f) {
            return (short) 0x0000;
        } else if (flt == -0f) {
            return (short) 0x8000;
        } else if (flt > 65504f) {
            // max value supported by half float
            return 0x7bff;
        } else if (flt < -65504f) {
            return (short) (0x7bff | 0x8000);
        } else if (flt > 0f && flt < 3.054738E-5f) {
            return 0x0001;
        } else if (flt < 0f && flt > -3.054738E-5f) {
            return (short) 0x8001;
        }

        int f = Float.floatToIntBits(flt);
        return (short) (((f >> 16) & 0x8000)
                | ((((f & 0x7f800000) - 0x38000000) >> 13) & 0x7c00)
                | ((f >> 13) & 0x03ff));
    }

    /**
     *
     * @param a
     * @param b
     * @param factor de 0 a 1
     * @return
     */
    public static float mix(float a, float b, float factor) {
        return a * (1 - factor) + b * factor;
    }

    /**
     * Mezcla color Usar con cuidado
     *
     * @param a
     * @param b
     * @param factor
     * @return
     */
    public static QColor mix(QColor a, QColor b, float factor) {
        return new QColor(
                mix(a.a, b.a, factor),
                mix(a.r, b.r, factor),
                mix(a.g, b.g, factor),
                mix(a.b, b.b, factor));
    }

    public static QVector3 mix(QVector3 a, QVector3 b, float factor) {
        return new QVector3(
                mix(a.x, b.x, factor),
                mix(a.y, b.y, factor),
                mix(a.z, b.z, factor));
    }

    public static QVector3 max(QVector3 a, QVector3 b) {
        return new QVector3(
                Math.max(a.x, b.x),
                Math.max(a.y, b.y),
                Math.max(a.z, b.z));
    }

    public static QVector3 min(QVector3 a, QVector3 b) {
        return new QVector3(
                Math.min(a.x, b.x),
                Math.min(a.y, b.y),
                Math.min(a.z, b.z));
    }

    /**
     * Linearly interpolates between two translations based on a "progression"
     * value.
     *
     * @param start - the start translation.
     * @param end - the end translation.
     * @param progression - a value between 0 and 1 indicating how far to
     * interpolate between the two translations.
     * @return
     */
    public static QVector3 interpolate(QVector3 start, QVector3 end, float progression) {
        float x = start.x + (end.x - start.x) * progression;
        float y = start.y + (end.y - start.y) * progression;
        float z = start.z + (end.z - start.z) * progression;
        return new QVector3(x, y, z);
    }

    public static float byteToFloat(byte number) {
        return (float) Byte.toUnsignedLong(number);
    }

    public static int rotateNumber(int x, int max) {
        try {
            x = x % max;
        } catch (Exception e) {
        }
        if (x < 0) {
            x = max + x;
        }
        return x;
    }

    /**
     * Calcula un vector reflejo
     * http://asawicki.info/news_1301_reflect_and_refract_functions.html
     * https://www.khronos.org/registry/OpenGL/specs/gl/GLSLangSpec.1.20.pdf
     *
     * @param vectorIncidente
     * @param normal
     * @return
     */
    public static QVector3 reflejarVector(QVector3 vectorIncidente, QVector3 normal) {
        //out = incidentVec - 2.f * Dot(incidentVec, normal) * normal;
        TempVars tmp = TempVars.get();
        tmp.vector3f1.set(vectorIncidente);
        tmp.vector3f2.set(normal);
        tmp.vector3f1.add(tmp.vector3f2.multiply(-2.0f * tmp.vector3f1.dot(normal)));
        tmp.release();
        return tmp.vector3f1;

//        return vector.clone().add(QVector3.multiply(-2.0f * vector.dot(normal), normal));
    }

    /**
     * Calcular el vector refractado
     *
     * @param vector Incidente
     * @param normal Normal de la superficie
     * @param idr Indice de refraccion de la superficie
     * @return
     */
    public static QVector3 refractarVector(QVector3 vector, QVector3 normal, float idr) {
//        return refractarVector2(vector, normal, idr); //esa funcion no necesita invertir el indice de refraccion
        idr = idr > 0.0f ? 1.0f / idr : 0.0f; //indice del aire sobre indice del material
        return refractarVectorGL(vector, normal, idr);
//        return refractarVector2(vector, normal, idr);
    }

    /**
     * Calcula un vector refractado segun metodo de OpenGL
     * http://asawicki.info/news_1301_reflect_and_refract_functions.html
     * https://www.khronos.org/registry/OpenGL/specs/gl/GLSLangSpec.1.20.pdf
     *
     * @param vector
     * @param normal
     * @param idr Indice de refraccion entrante / indice refraccion material
     * @return
     */
    private static QVector3 refractarVectorGL(QVector3 vector, QVector3 normal, float idr) {
        TempVars tmp = TempVars.get();
        float N_dot_I = vector.dot(normal); //cos angulo
        float k = 1.0f - idr * idr * (1.0f - N_dot_I * N_dot_I);
        if (k < 0.f) {
//            return new QVector3();
            tmp.vector3f1.set(0, 0, 0);
        } else {
            tmp.vector3f1.set(vector);
            tmp.vector3f1.multiply(idr);
            tmp.vector3f2.set(normal);
            //--
//            tmp.vector3f2.multiply((float) (idr * N_dot_I - Math.sqrt(k)));            
//            tmp.vector3f1.add(tmp.vector3f2);
            tmp.vector3f2.multiply((float) (idr * N_dot_I + QMath.sqrt(k)));
            tmp.vector3f1.subtract(tmp.vector3f2);

//            tmp.vector3f1.set(QVector3.multiply(idr, vector).add(
//                    QVector3.multiply((float) (idr * N_dot_I + Math.sqrt(k)), normal))
//            );
        }
        tmp.release();
        return tmp.vector3f1;
    }

    /**
     * Vector de refraccion segun
     * https://github.com/fbngrm/raytracer/blob/master/src/cg2/raytracer/Ray.java
     *
     * @param vector
     * @param normal
     * @param idr Indice de refraccion entrante / indice regraccion material
     * @return
     */
    private static QVector3 refractarVector1(QVector3 vector, QVector3 normal, float idr) {
        float N_dot_I = -vector.dot(normal); //cos angulo
        float k = idr * idr * (1.f - N_dot_I * N_dot_I);
        if (k > 1.f) {
            return new QVector3();
        } else {
            float cosT2 = (float) Math.sqrt(1 - k);
            return QVector3.multiply(idr, vector).add(
                    QVector3.multiply((float) (idr * N_dot_I - cosT2), normal)
            );
        }
    }

    /**
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel
     *
     * @param vector
     * @param normal
     * @param idr
     * @return
     */
    private static QVector3 refractarVector2(QVector3 vector, QVector3 normal, float idr) {
        float cosi = clamp(-1, 1, vector.dot(normal));
        float idr_e = 1, idr_s = 1 / idr; //ior; 
        QVector3 n = normal.clone();
        if (cosi < 0) {
            cosi = -cosi;
        } else {
            float aux = idr_e;
            idr_e = idr_s;
            idr_s = aux;
            n.multiply(-1);
        }
        idr = idr_e / idr_s;

        float k = 1 - idr * idr * (1 - cosi * cosi);
        if (k < 0.f) {
            return new QVector3();
        } else {
            return QVector3.multiply(idr, vector).add(QVector3.multiply((float) (idr * cosi - Math.sqrt(k)), normal));
        }

    }

    /**
     * Calcula cuanta luz debe reflejarse
     * https://www.scratchapixel.com/lessons/3d-basic-rendering/introduction-to-shading/reflection-refraction-fresnel
     *
     * @param vector
     * @param normal
     * @param idr
     * @return
     */
    public static float factorFresnel(QVector3 vector, QVector3 normal, float idr) {
        TempVars tmp = TempVars.get();
        tmp.vector3f1.set(vector);
//          factorFresnel= tm.vector3f1.clone().multiply(-1f).dot(tm.vector3f2);
//                factorFresnel = QMath.clamp(tm.vector3f1.dot(tm.vector3f2), -1, 1.0f);//coseno del angulo de 0 a 1
//        return 1.0f-vector.dot(normal);//coseno del angulo
//        float r = 1.0f - QMath.clamp(tmp.vector3f1.multiply(-1.0f).dot(normal), 0, 0.94f);//coseno del angulo de 0 a 0.94
        float r = 1.0f - QMath.clamp(tmp.vector3f1.multiply(-1.0f).dot(normal), 0, 1f);//coseno del angulo de 0 a 1
        tmp.release();
        return r;
    }

// https://elcodigografico.wordpress.com/2014/03/29/coordenadas-baricentricas-en-triangulos/
    public static float barycentricSide(float x, float y, QVector2 pa, QVector2 pb) {
        return (pa.y - pb.y) * x + (pb.x - pa.x) * y + pa.x * pb.y - pb.x * pa.y;
    }

    public static void getBarycentricCoordinates(QVector3 salida, float x, float y, QVector2 v1, QVector2 v2, QVector2 v3) {
        //alpha
        salida.x = barycentricSide(x, y, v2, v3) / barycentricSide(v1.x, v1.y, v2, v3);
        //theta 
        salida.y = barycentricSide(x, y, v3, v1) / barycentricSide(v2.x, v2.y, v3, v1);
        //gamma
        salida.z = barycentricSide(x, y, v1, v2) / barycentricSide(v3.x, v3.y, v1, v2);
    }

//public static QVector3 getBarycentricCoordinatesPrecalc(float x, float y,  Point2D v1, Point2D v2, Point2D v3, float r1, float r2, float r3)
//{
//    alpha = barycentricSide( x, y, v2, v3 ) / r1;
//    theta = barycentricSide( x, y, v3, v1 ) / r2;
//    gamma = barycentricSide( x, y, v1, v2 ) / r3;
//}
    public static QColor calcularColorLuz(QColor colorDifuso, QColor colorEspecular, QColor colorLuz, float intensidad, QVector3 posicion, QVector3 vectorHaciaLuz, QVector3 normal, float exponenteEspecular, float reflectancia) {
        QColor diffuseColour;
        QColor specColour;

        // Diffuse Light
        float diffuseFactor = Math.max(vectorHaciaLuz.dot(normal), 0.0f);
        diffuseColour = colorDifuso.clone().scale(colorLuz.clone().scale(intensidad * diffuseFactor));

        // Specular Light
        QVector3 camera_direction = posicion.clone().invert().normalize();
//        QVector3 camera_direction = posicion.clone().normalize();
        QVector3 vectorDesdeLuz = vectorHaciaLuz.invert();
//        QVector3 from_light_dir = vectorLuz;
        QVector3 luzReflejada = QMath.reflejarVector(vectorDesdeLuz, normal).normalize();
        float specularFactor = Math.max(camera_direction.dot(luzReflejada), 0.0f);
        specularFactor = (float) Math.pow(specularFactor, exponenteEspecular);
        specColour = colorEspecular.clone().scale(colorLuz.clone().scale(intensidad * specularFactor * reflectancia));

        return diffuseColour.add(specColour);
    }

    public static QColor calcularColorLuzPBR(QColor colorDifuso, QColor colorEspecular, QColor colorLuz, float intensidad, QVector3 posicion, QVector3 vectorLuz, QVector3 normal, float exponenteEspecular, float reflectancia) {
        QColor diffuseColour;
        float cosTheta = Math.max(vectorLuz.dot(normal), 0.0f);
//        float atenuacion=calculateAttenuation(posicion, lightPos);
        diffuseColour = colorLuz.clone().scale(intensidad * cosTheta);

        return diffuseColour;
    }

    //---------------------------------------------------- FUNCIONES PARA EL CALCULO DE COLORES BASADOS EN PBR ---------------------------------------
    public static QVector3 fresnelSchlick(float HdotV, QVector3 F0) {
//    return F0 +(1.0 - F0) * pow(1.0 - dot(H,V), 5.0);
        return F0.add(QVector3.unitario_xyz.clone().subtract(F0).multiply(QMath.pow(1.0f - HdotV, 5.0f)));
    }

    public static QVector3 fresnelSchlick(float HdotV, QVector3 F0, float roughness) {
//    return F0 + (max(vec3(1.0 - roughness), F0) - F0) * pow(1.0 - cosTheta, 5.0);
        return F0.add(QMath.max(QVector3.unitario_xyz.clone().add(-roughness), F0).subtract(F0).multiply(QMath.pow(1.0f - HdotV, 5.0f)));
    }

    public static float DistributionGGX(float NdotH, float roughness) {
        float a = roughness * roughness;
        float a2 = a * a;
//        float NdotH = (float) Math.max(N.dot(H), 0.0);
        float NdotH2 = NdotH * NdotH;
        float num = a2;
        float denom = (float) (NdotH2 * (a2 - 1.0) + 1.0);
        denom = PI * denom * denom;
        return num / Math.max(denom, 0.00001f);//previene la division por cero
    }

//    public static float GeometrySchlickGGX(float NdotV, float roughness) {
//        float r = (roughness + 1.0f);
//        float k = (r * r) / 8.0f;
//        return NdotV / (NdotV * (1.0f - k) + k);
//    }
//    public static float GeometrySmith(QVector3 N, QVector3 V, QVector3 L, float roughness) {
//        float NdotV = (float) Math.max(N.dot(V), 0.0);
//        float NdotL = (float) Math.max(N.dot(L), 0.0);
//        float ggx2 = GeometrySchlickGGX(NdotV, roughness);
//        float ggx1 = GeometrySchlickGGX(NdotL, roughness);
//        return ggx1 * ggx2;
//    }
    public static float GeometrySmith(float NdotV, float NdotL, float roughness) {

//        float NdotV = Math.max(N.dot(V), 0.0000001f);
//        float NdotL = Math.max(N.dot(L), 0.0000001f);
        float r = (roughness + 1.0f);
        float k = ((r * r) / 8.0f);
        float ggx1 = NdotV / (NdotV * (1.0f - k) + k);
        float ggx2 = NdotL / (NdotL * (1.0f - k) + k);
        return ggx1 * ggx2;
    }

    public static QVector3 EnvBRDFApprox(QVector3 specularColor, float rugosidoad, float NoV) {
        QVector4 c0 = new QVector4(-1, -0.0275f, -0.572f, 0.022f);
        QVector4 c1 = new QVector4(1, 0.0425f, 1.04f, -0.04f);
        QVector4 r = c0.multiply(rugosidoad).add(c1);
        float a004 = Math.min(r.x * r.x, (float) Math.pow(2, -9.28f * NoV)) * r.x + r.y;
        QVector2 AB = new QVector2(-1.04f, 1.04f).multiply(a004).add(new QVector2(r.z, r.w));
        return specularColor.multiply(AB.x).add(AB.y);
    }
}
