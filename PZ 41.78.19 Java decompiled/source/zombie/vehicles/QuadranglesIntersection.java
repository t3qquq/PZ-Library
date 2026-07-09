// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import org.joml.Vector4f;
import zombie.iso.Vector2;

public final class QuadranglesIntersection {
    private static final float EPS = 0.001F;

    public static boolean IsQuadranglesAreIntersected(Vector2[] vectors1, Vector2[] vectors0) {
        if (vectors1 != null && vectors0 != null && vectors1.length == 4 && vectors0.length == 4) {
            if (lineIntersection(vectors1[0], vectors1[1], vectors0[0], vectors0[1])) {
                return true;
            } else if (lineIntersection(vectors1[0], vectors1[1], vectors0[1], vectors0[2])) {
                return true;
            } else if (lineIntersection(vectors1[0], vectors1[1], vectors0[2], vectors0[3])) {
                return true;
            } else if (lineIntersection(vectors1[0], vectors1[1], vectors0[3], vectors0[0])) {
                return true;
            } else if (lineIntersection(vectors1[1], vectors1[2], vectors0[0], vectors0[1])) {
                return true;
            } else if (lineIntersection(vectors1[1], vectors1[2], vectors0[1], vectors0[2])) {
                return true;
            } else if (lineIntersection(vectors1[1], vectors1[2], vectors0[2], vectors0[3])) {
                return true;
            } else if (lineIntersection(vectors1[1], vectors1[2], vectors0[3], vectors0[0])) {
                return true;
            } else if (lineIntersection(vectors1[2], vectors1[3], vectors0[0], vectors0[1])) {
                return true;
            } else if (lineIntersection(vectors1[2], vectors1[3], vectors0[1], vectors0[2])) {
                return true;
            } else if (lineIntersection(vectors1[2], vectors1[3], vectors0[2], vectors0[3])) {
                return true;
            } else if (lineIntersection(vectors1[2], vectors1[3], vectors0[3], vectors0[0])) {
                return true;
            } else if (lineIntersection(vectors1[3], vectors1[0], vectors0[0], vectors0[1])) {
                return true;
            } else if (lineIntersection(vectors1[3], vectors1[0], vectors0[1], vectors0[2])) {
                return true;
            } else if (lineIntersection(vectors1[3], vectors1[0], vectors0[2], vectors0[3])) {
                return true;
            } else if (lineIntersection(vectors1[3], vectors1[0], vectors0[3], vectors0[0])) {
                return true;
            } else {
                return IsPointInTriangle(vectors1[0], vectors0[0], vectors0[1], vectors0[2])
                        || IsPointInTriangle(vectors1[0], vectors0[0], vectors0[2], vectors0[3])
                    ? true
                    : IsPointInTriangle(vectors0[0], vectors1[0], vectors1[1], vectors1[2])
                        || IsPointInTriangle(vectors0[0], vectors1[0], vectors1[2], vectors1[3]);
            }
        } else {
            System.out.println("ERROR: IsQuadranglesAreIntersected");
            return false;
        }
    }

    public static boolean IsPointInTriangle(Vector2 vector, Vector2[] vectors) {
        return IsPointInTriangle(vector, vectors[0], vectors[1], vectors[2]) || IsPointInTriangle(vector, vectors[0], vectors[2], vectors[3]);
    }

    public static float det(float float2, float float0, float float1, float float3) {
        return float2 * float3 - float0 * float1;
    }

    private static boolean between(float float0, float float1, double double0) {
        return Math.min(float0, float1) <= double0 + 0.001F && double0 <= Math.max(float0, float1) + 0.001F;
    }

    private static boolean intersect_1(float float2, float float3, float float6, float float7) {
        float float0;
        float float1;
        if (float2 > float3) {
            float1 = float2;
            float0 = float3;
        } else {
            float0 = float2;
            float1 = float3;
        }

        float float4;
        float float5;
        if (float6 > float7) {
            float5 = float6;
            float4 = float7;
        } else {
            float4 = float6;
            float5 = float7;
        }

        return Math.max(float0, float4) <= Math.min(float1, float5);
    }

    public static boolean lineIntersection(Vector2 vector1, Vector2 vector0, Vector2 vector3, Vector2 vector2) {
        float float0 = vector1.y - vector0.y;
        float float1 = vector0.x - vector1.x;
        float float2 = -float0 * vector1.x - float1 * vector1.y;
        float float3 = vector3.y - vector2.y;
        float float4 = vector2.x - vector3.x;
        float float5 = -float3 * vector3.x - float4 * vector3.y;
        float float6 = det(float0, float1, float3, float4);
        if (float6 != 0.0F) {
            double double0 = -det(float2, float1, float5, float4) * 1.0 / float6;
            double double1 = -det(float0, float2, float3, float5) * 1.0 / float6;
            return between(vector1.x, vector0.x, double0)
                && between(vector1.y, vector0.y, double1)
                && between(vector3.x, vector2.x, double0)
                && between(vector3.y, vector2.y, double1);
        } else {
            return det(float0, float2, float3, float5) == 0.0F
                && det(float1, float2, float4, float5) == 0.0F
                && intersect_1(vector1.x, vector0.x, vector3.x, vector2.x)
                && intersect_1(vector1.y, vector0.y, vector3.y, vector2.y);
        }
    }

    public static boolean IsQuadranglesAreTransposed2(Vector4f vector4f1, Vector4f vector4f0) {
        if (IsPointInQuadrilateral(new Vector2(vector4f1.x, vector4f1.y), vector4f0.x, vector4f0.z, vector4f0.y, vector4f0.w)) {
            return true;
        } else if (IsPointInQuadrilateral(new Vector2(vector4f1.z, vector4f1.y), vector4f0.x, vector4f0.z, vector4f0.y, vector4f0.w)) {
            return true;
        } else if (IsPointInQuadrilateral(new Vector2(vector4f1.x, vector4f1.w), vector4f0.x, vector4f0.z, vector4f0.y, vector4f0.w)) {
            return true;
        } else if (IsPointInQuadrilateral(new Vector2(vector4f1.z, vector4f1.w), vector4f0.x, vector4f0.z, vector4f0.y, vector4f0.w)) {
            return true;
        } else if (IsPointInQuadrilateral(new Vector2(vector4f0.x, vector4f0.y), vector4f1.x, vector4f1.z, vector4f1.y, vector4f1.w)) {
            return true;
        } else if (IsPointInQuadrilateral(new Vector2(vector4f0.z, vector4f0.y), vector4f1.x, vector4f1.z, vector4f1.y, vector4f1.w)) {
            return true;
        } else {
            return IsPointInQuadrilateral(new Vector2(vector4f0.x, vector4f0.w), vector4f1.x, vector4f1.z, vector4f1.y, vector4f1.w)
                ? true
                : IsPointInQuadrilateral(new Vector2(vector4f0.z, vector4f0.w), vector4f1.x, vector4f1.z, vector4f1.y, vector4f1.w);
        }
    }

    private static boolean IsPointInQuadrilateral(Vector2 vector, float float0, float float2, float float1, float float3) {
        return IsPointInTriangle(vector, new Vector2(float0, float1), new Vector2(float0, float3), new Vector2(float2, float3))
            ? true
            : IsPointInTriangle(vector, new Vector2(float2, float3), new Vector2(float2, float1), new Vector2(float0, float1));
    }

    private static boolean IsPointInTriangle(Vector2 vector0, Vector2 vector1, Vector2 vector2, Vector2 vector3) {
        float float0 = (vector1.x - vector0.x) * (vector2.y - vector1.y) - (vector2.x - vector1.x) * (vector1.y - vector0.y);
        float float1 = (vector2.x - vector0.x) * (vector3.y - vector2.y) - (vector3.x - vector2.x) * (vector2.y - vector0.y);
        float float2 = (vector3.x - vector0.x) * (vector1.y - vector3.y) - (vector1.x - vector3.x) * (vector3.y - vector0.y);
        return float0 >= 0.0F && float1 >= 0.0F && float2 >= 0.0F || float0 <= 0.0F && float1 <= 0.0F && float2 <= 0.0F;
    }
}
