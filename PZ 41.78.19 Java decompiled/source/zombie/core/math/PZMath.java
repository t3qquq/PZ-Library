// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.math;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.debug.DebugLog;
import zombie.iso.Vector2;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

public final class PZMath {
    /**
     * The double value that is closer than any other to  pi, the ratio of the circumference of a circle to its  diameter.
     */
    public static final float PI = (float) Math.PI;
    public static final float PI2 = (float) (Math.PI * 2);
    /**
     * Conversion ratios, Degrees to Radians and back
     */
    public static final float degToRads = (float) (Math.PI / 180.0);
    public static final float radToDegs = 180.0F / (float)Math.PI;
    public static final long microsToNanos = 1000L;
    public static final long millisToMicros = 1000L;
    public static final long secondsToMillis = 1000L;
    public static long secondsToNanos = 1000000000L;

    /**
     * Almost Unit Identity   This is a near-identiy function that maps the unit interval into itself. It is the cousin of smoothstep(), in  that it maps 0 to 0, 1 to 1, and has a 0 derivative at the origin, just like smoothstep. However, instead of  having a 0 derivative at 1, it has a derivative of 1 at that point. It's equivalent to the Almost Identiy above  with n=0 and m=1. Since it's a cubic just like smoothstep() it is very fast to evaluate.   https://iquilezles.org/www/articles/functions/functions.htm
     * 
     * @param x value in [0..1]
     * @return value in [0..1]
     */
    public static float almostUnitIdentity(float x) {
        return x * x * (2.0F - x);
    }

    /**
     * Almost Identity   Imagine you don't want to modify a signal unless it's drops to zero or close to it, in which case you want  to replace the value with a small possitive constant. Then, rather than clamping the value and introduce  a discontinuity, you can smoothly blend the signal into the desired clipped value. So, let m be the threshold  (anything above m stays unchanged), and n the value things will take when the signal is zero.  Then, the following function does the soft clipping (in a cubic fashion):   https://iquilezles.org/www/articles/functions/functions.htm
     * 
     * @param x value in [0..1]
     * @param m
     * @param n
     * @return value in [0..1]
     */
    public static float almostIdentity(float x, float m, float n) {
        if (x > m) {
            return x;
        } else {
            float float0 = 2.0F * n - m;
            float float1 = 2.0F * m - 3.0F * n;
            float float2 = x / m;
            return (float0 * float2 + float1) * float2 * float2 + n;
        }
    }

    /**
     * Gain   Remapping the unit interval into the unit interval by expanding the sides and compressing the center, and  keeping 1/2 mapped to 1/2, that can be done with the gain() function. This was a common function in RSL tutorials  (the Renderman Shading Language). k=1 is the identity curve, k<1 produces the classic gain() shape, and k>1  produces "s" shaped curces. The curves are symmetric (and inverse) for k=a and k=1/a.   https://iquilezles.org/www/articles/functions/functions.htm
     */
    public static float gain(float x, float k) {
        float float0 = (float)(0.5 * Math.pow(2.0F * (x < 0.5F ? x : 1.0F - x), k));
        return x < 0.5F ? float0 : 1.0F - float0;
    }

    /**
     * Result is clamped between min and max.
     * @return min <= val <= max
     */
    public static float clamp(float val, float min, float max) {
        float float0 = val;
        if (val < min) {
            float0 = min;
        }

        if (float0 > max) {
            float0 = max;
        }

        return float0;
    }

    public static long clamp(long val, long min, long max) {
        long long0 = val;
        if (val < min) {
            long0 = min;
        }

        if (long0 > max) {
            long0 = max;
        }

        return long0;
    }

    /**
     * Result is clamped between min and max.
     * @return min <= val <= max
     */
    public static int clamp(int val, int min, int max) {
        int int0 = val;
        if (val < min) {
            int0 = min;
        }

        if (int0 > max) {
            int0 = max;
        }

        return int0;
    }

    public static float clampFloat(float val, float min, float max) {
        return clamp(val, min, max);
    }

    public static float clamp_01(float val) {
        return clamp(val, 0.0F, 1.0F);
    }

    public static float lerp(float src, float dest, float alpha) {
        return src + (dest - src) * alpha;
    }

    public static float lerpAngle(float src, float dest, float alpha) {
        float float0 = getClosestAngle(src, dest);
        float float1 = src + alpha * float0;
        return wrap(float1, (float) -Math.PI, (float) Math.PI);
    }

    public static Vector3f lerp(Vector3f out, Vector3f a, Vector3f b, float t) {
        out.set(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t, a.z + (b.z - a.z) * t);
        return out;
    }

    public static Vector2 lerp(Vector2 out, Vector2 a, Vector2 b, float t) {
        out.set(a.x + (b.x - a.x) * t, a.y + (b.y - a.y) * t);
        return out;
    }

    public static float c_lerp(float src, float dest, float alpha) {
        float float0 = (float)(1.0 - Math.cos(alpha * (float) Math.PI)) / 2.0F;
        return src * (1.0F - float0) + dest * float0;
    }

    public static Quaternion slerp(Quaternion result, Quaternion from, Quaternion to, float alpha) {
        double double0 = from.x * to.x + from.y * to.y + from.z * to.z + from.w * to.w;
        double double1 = double0 < 0.0 ? -double0 : double0;
        double double2 = 1.0F - alpha;
        double double3 = alpha;
        if (1.0 - double1 > 0.1) {
            double double4 = org.joml.Math.acos(double1);
            double double5 = org.joml.Math.sin(double4);
            double double6 = 1.0 / double5;
            double2 = org.joml.Math.sin(double4 * (1.0 - alpha)) * double6;
            double3 = org.joml.Math.sin(double4 * alpha) * double6;
        }

        if (double0 < 0.0) {
            double3 = -double3;
        }

        result.set(
            (float)(double2 * from.x + double3 * to.x),
            (float)(double2 * from.y + double3 * to.y),
            (float)(double2 * from.z + double3 * to.z),
            (float)(double2 * from.w + double3 * to.w)
        );
        return result;
    }

    public static float sqrt(float val) {
        return org.joml.Math.sqrt(val);
    }

    public static float lerpFunc_EaseOutQuad(float x) {
        return x * x;
    }

    public static float lerpFunc_EaseInQuad(float x) {
        float float0 = 1.0F - x;
        return 1.0F - float0 * float0;
    }

    public static float lerpFunc_EaseOutInQuad(float x) {
        return x < 0.5F ? lerpFunc_EaseOutQuad(x) * 2.0F : 0.5F + lerpFunc_EaseInQuad(2.0F * x - 1.0F) / 2.0F;
    }

    public static double tryParseDouble(String varStr, double defaultVal) {
        if (StringUtils.isNullOrWhitespace(varStr)) {
            return defaultVal;
        } else {
            try {
                return Double.parseDouble(varStr.trim());
            } catch (NumberFormatException numberFormatException) {
                return defaultVal;
            }
        }
    }

    public static float tryParseFloat(String varStr, float defaultVal) {
        if (StringUtils.isNullOrWhitespace(varStr)) {
            return defaultVal;
        } else {
            try {
                return Float.parseFloat(varStr.trim());
            } catch (NumberFormatException numberFormatException) {
                return defaultVal;
            }
        }
    }

    public static boolean canParseFloat(String varStr) {
        if (StringUtils.isNullOrWhitespace(varStr)) {
            return false;
        } else {
            try {
                Float.parseFloat(varStr.trim());
                return true;
            } catch (NumberFormatException numberFormatException) {
                return false;
            }
        }
    }

    public static int tryParseInt(String varStr, int defaultVal) {
        if (StringUtils.isNullOrWhitespace(varStr)) {
            return defaultVal;
        } else {
            try {
                return Integer.parseInt(varStr.trim());
            } catch (NumberFormatException numberFormatException) {
                return defaultVal;
            }
        }
    }

    public static float degToRad(float degrees) {
        return (float) (Math.PI / 180.0) * degrees;
    }

    public static float radToDeg(float radians) {
        return (180.0F / (float)Math.PI) * radians;
    }

    public static float getClosestAngle(float in_radsA, float in_radsB) {
        float float0 = wrap(in_radsA, (float) (Math.PI * 2));
        float float1 = wrap(in_radsB, (float) (Math.PI * 2));
        float float2 = float1 - float0;
        return wrap(float2, (float) -Math.PI, (float) Math.PI);
    }

    public static float getClosestAngleDegrees(float in_degsA, float in_degsB) {
        float float0 = degToRad(in_degsA);
        float float1 = degToRad(in_degsB);
        float float2 = getClosestAngle(float0, float1);
        return radToDeg(float2);
    }

    public static int sign(float val) {
        return val > 0.0F ? 1 : (val < 0.0F ? -1 : 0);
    }

    public static int fastfloor(double x) {
        int int0 = (int)x;
        return x < int0 ? int0 - 1 : int0;
    }

    public static int fastfloor(float x) {
        int int0 = (int)x;
        return x < int0 ? int0 - 1 : int0;
    }

    public static float floor(float val) {
        return fastfloor(val);
    }

    public static float ceil(float val) {
        return val >= 0.0F ? (int)(val + 0.9999999F) : (int)(val - 1.0E-7F);
    }

    public static float frac(float val) {
        float float0 = floor(val);
        return val - float0;
    }

    public static float wrap(float val, float range) {
        if (range == 0.0F) {
            return 0.0F;
        } else if (range < 0.0F) {
            return 0.0F;
        } else if (val < 0.0F) {
            float float0 = -val / range;
            float float1 = 1.0F - frac(float0);
            return float1 * range;
        } else {
            float float2 = val / range;
            float float3 = frac(float2);
            return float3 * range;
        }
    }

    public static float wrap(float in_val, float in_min, float in_max) {
        float float0 = max(in_max, in_min);
        float float1 = min(in_max, in_min);
        float float2 = float0 - float1;
        float float3 = in_val - float1;
        float float4 = wrap(float3, float2);
        return float1 + float4;
    }

    public static float max(float a, float b) {
        return a > b ? a : b;
    }

    public static int max(int a, int b) {
        return a > b ? a : b;
    }

    public static float min(float a, float b) {
        return a > b ? b : a;
    }

    public static int min(int a, int b) {
        return a > b ? b : a;
    }

    public static float abs(float val) {
        return val * sign(val);
    }

    public static boolean equal(float a, float b) {
        return equal(a, b, 1.0E-7F);
    }

    public static boolean equal(float a, float b, float delta) {
        float float0 = b - a;
        float float1 = abs(float0);
        return float1 < delta;
    }

    public static Matrix4f convertMatrix(org.joml.Matrix4f src, Matrix4f dst) {
        if (dst == null) {
            dst = new Matrix4f();
        }

        dst.m00 = src.m00();
        dst.m01 = src.m01();
        dst.m02 = src.m02();
        dst.m03 = src.m03();
        dst.m10 = src.m10();
        dst.m11 = src.m11();
        dst.m12 = src.m12();
        dst.m13 = src.m13();
        dst.m20 = src.m20();
        dst.m21 = src.m21();
        dst.m22 = src.m22();
        dst.m23 = src.m23();
        dst.m30 = src.m30();
        dst.m31 = src.m31();
        dst.m32 = src.m32();
        dst.m33 = src.m33();
        return dst;
    }

    public static org.joml.Matrix4f convertMatrix(Matrix4f src, org.joml.Matrix4f dst) {
        if (dst == null) {
            dst = new org.joml.Matrix4f();
        }

        return dst.set(
            src.m00, src.m01, src.m02, src.m03, src.m10, src.m11, src.m12, src.m13, src.m20, src.m21, src.m22, src.m23, src.m30, src.m31, src.m32, src.m33
        );
    }

    public static float step(float from, float to, float delta) {
        if (from > to) {
            return max(from + delta, to);
        } else {
            return from < to ? min(from + delta, to) : from;
        }
    }

    public static PZMath.SideOfLine testSideOfLine(float x1, float y1, float x2, float y2, float px, float py) {
        float float0 = (px - x1) * (y2 - y1) - (py - y1) * (x2 - x1);
        return float0 > 0.0F ? PZMath.SideOfLine.Left : (float0 < 0.0F ? PZMath.SideOfLine.Right : PZMath.SideOfLine.OnLine);
    }

    public static float roundToNearest(float val) {
        int int0 = sign(val);
        return floor(val + 0.5F * int0);
    }

    public static int roundToInt(float val) {
        return (int)(roundToNearest(val) + 1.0E-4F);
    }

    public static float roundToIntPlus05(float val) {
        return floor(val) + 0.5F;
    }

    public static float roundFromEdges(float val) {
        float float0 = (int)val;
        float float1 = val - float0;
        if (float1 < 0.2F) {
            return float0 + 0.2F;
        } else {
            return float1 > 0.8F ? float0 + 1.0F - 0.2F : val;
        }
    }

    static {
        PZMath.UnitTests.runAll();
    }

    public static enum SideOfLine {
        Left,
        OnLine,
        Right;
    }

    private static final class UnitTests {
        private static final Runnable[] s_unitTests = new Runnable[0];

        private static void runAll() {
            PZArrayUtil.forEach(s_unitTests, Runnable::run);
        }

        private static final class getClosestAngle {
            public static void run() {
                DebugLog.General.println("runUnitTests_getClosestAngle");
                DebugLog.General.println("a, b, result, expected, pass");
                runUnitTest(0.0F, 0.0F, 0.0F);
                runUnitTest(0.0F, 15.0F, 15.0F);
                runUnitTest(15.0F, 0.0F, -15.0F);
                runUnitTest(0.0F, 179.0F, 179.0F);
                runUnitTest(180.0F, 180.0F, 0.0F);
                runUnitTest(180.0F, 359.0F, 179.0F);
                runUnitTest(90.0F, 180.0F, 90.0F);
                runUnitTest(180.0F, 90.0F, -90.0F);

                for (short short0 = -360; short0 < 360; short0 += 10) {
                    for (short short1 = -360; short1 < 360; short1 += 10) {
                        float float0 = short0;
                        float float1 = short1;
                        runUnitTest_noexp(float0, float1);
                    }
                }

                DebugLog.General.println("runUnitTests_getClosestAngle. Complete");
            }

            private static void runUnitTest_noexp(float float1, float float2) {
                float float0 = PZMath.getClosestAngleDegrees(float1, float2);
                logResult(float1, float2, float0, "N/A", "N/A");
            }

            private static void runUnitTest(float float1, float float2, float float3) {
                float float0 = PZMath.getClosestAngleDegrees(float1, float2);
                boolean boolean0 = PZMath.equal(float3, float0, 1.0E-4F);
                String string = boolean0 ? "pass" : "fail";
                logResult(float1, float2, float0, String.valueOf(float3), string);
            }

            private static void logResult(float float2, float float1, float float0, String string0, String string1) {
                DebugLog.General.println("%f, %f, %f, %s, %s", float2, float1, float0, string0, string1);
            }
        }

        private static final class lerpFunctions {
            public static void run() {
                DebugLog.General.println("UnitTest_lerpFunctions");
                DebugLog.General.println("x,Sqrt,EaseOutQuad,EaseInQuad,EaseOutInQuad");

                for (int int0 = 0; int0 < 100; int0++) {
                    float float0 = int0 / 100.0F;
                    DebugLog.General
                        .println(
                            "%f,%f,%f,%f",
                            float0,
                            PZMath.lerpFunc_EaseOutQuad(float0),
                            PZMath.lerpFunc_EaseInQuad(float0),
                            PZMath.lerpFunc_EaseOutInQuad(float0)
                        );
                }

                DebugLog.General.println("UnitTest_lerpFunctions. Complete");
            }
        }

        public static final class vector2 {
            public static void run() {
                runUnitTest_direction();
            }

            private static void runUnitTest_direction() {
                DebugLog.General.println("runUnitTest_direction");
                DebugLog.General.println("x, y, angle, length, rdir.x, rdir.y, rangle, rlength, pass");
                checkDirection(1.0F, 0.0F);
                checkDirection(1.0F, 1.0F);
                checkDirection(0.0F, 1.0F);
                checkDirection(-1.0F, 1.0F);
                checkDirection(-1.0F, 0.0F);
                checkDirection(-1.0F, -1.0F);
                checkDirection(0.0F, -1.0F);
                checkDirection(1.0F, -1.0F);
                DebugLog.General.println("runUnitTest_direction. Complete");
            }

            private static void checkDirection(float float0, float float1) {
                Vector2 vector0 = new Vector2(float0, float1);
                float float2 = vector0.getDirection();
                float float3 = vector0.getLength();
                Vector2 vector1 = Vector2.fromLengthDirection(float3, float2);
                float float4 = vector1.getDirection();
                float float5 = vector1.getLength();
                boolean boolean0 = PZMath.equal(vector0.x, vector1.x, 1.0E-4F)
                    && PZMath.equal(vector0.y, vector1.y, 1.0E-4F)
                    && PZMath.equal(float2, float4, 1.0E-4F)
                    && PZMath.equal(float3, float5, 1.0E-4F);
                DebugLog.General
                    .println(
                        "%f, %f, %f, %f, %f, %f, %f, %f, %s", float0, float1, float2, float3, vector1.x, vector1.y, float4, float5, boolean0 ? "true" : "false"
                    );
            }
        }
    }
}
