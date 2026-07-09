// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class GeometryUtils {
    public static void perpendicular(float float4, float float1, float float2, Vector3f vector3f0, Vector3f vector3f1) {
        float float0 = float2 * float2 + float1 * float1;
        float float3 = float2 * float2 + float4 * float4;
        float float5 = float1 * float1 + float4 * float4;
        float float6;
        if (float0 > float3 && float0 > float5) {
            vector3f0.x = 0.0F;
            vector3f0.y = float2;
            vector3f0.z = -float1;
            float6 = float0;
        } else if (float3 > float5) {
            vector3f0.x = -float2;
            vector3f0.y = 0.0F;
            vector3f0.z = float4;
            float6 = float3;
        } else {
            vector3f0.x = float1;
            vector3f0.y = -float4;
            vector3f0.z = 0.0F;
            float6 = float5;
        }

        float float7 = Math.invsqrt(float6);
        vector3f0.x *= float7;
        vector3f0.y *= float7;
        vector3f0.z *= float7;
        vector3f1.x = float1 * vector3f0.z - float2 * vector3f0.y;
        vector3f1.y = float2 * vector3f0.x - float4 * vector3f0.z;
        vector3f1.z = float4 * vector3f0.y - float1 * vector3f0.x;
    }

    public static void perpendicular(Vector3fc vector3fc, Vector3f vector3f0, Vector3f vector3f1) {
        perpendicular(vector3fc.x(), vector3fc.y(), vector3fc.z(), vector3f0, vector3f1);
    }

    public static void normal(Vector3fc vector3fc2, Vector3fc vector3fc1, Vector3fc vector3fc0, Vector3f vector3f) {
        normal(
            vector3fc2.x(),
            vector3fc2.y(),
            vector3fc2.z(),
            vector3fc1.x(),
            vector3fc1.y(),
            vector3fc1.z(),
            vector3fc0.x(),
            vector3fc0.y(),
            vector3fc0.z(),
            vector3f
        );
    }

    public static void normal(
        float float7, float float1, float float3, float float6, float float5, float float2, float float8, float float0, float float4, Vector3f vector3f
    ) {
        vector3f.x = (float5 - float1) * (float4 - float3) - (float2 - float3) * (float0 - float1);
        vector3f.y = (float2 - float3) * (float8 - float7) - (float6 - float7) * (float4 - float3);
        vector3f.z = (float6 - float7) * (float0 - float1) - (float5 - float1) * (float8 - float7);
        vector3f.normalize();
    }

    public static void tangent(
        Vector3fc vector3fc0, Vector2fc vector2fc0, Vector3fc vector3fc2, Vector2fc vector2fc1, Vector3fc vector3fc1, Vector2fc vector2fc2, Vector3f vector3f
    ) {
        float float0 = vector2fc1.y() - vector2fc0.y();
        float float1 = vector2fc2.y() - vector2fc0.y();
        float float2 = 1.0F / ((vector2fc1.x() - vector2fc0.x()) * float1 - (vector2fc2.x() - vector2fc0.x()) * float0);
        vector3f.x = float2 * (float1 * (vector3fc2.x() - vector3fc0.x()) - float0 * (vector3fc1.x() - vector3fc0.x()));
        vector3f.y = float2 * (float1 * (vector3fc2.y() - vector3fc0.y()) - float0 * (vector3fc1.y() - vector3fc0.y()));
        vector3f.z = float2 * (float1 * (vector3fc2.z() - vector3fc0.z()) - float0 * (vector3fc1.z() - vector3fc0.z()));
        vector3f.normalize();
    }

    public static void bitangent(
        Vector3fc vector3fc0, Vector2fc vector2fc0, Vector3fc vector3fc2, Vector2fc vector2fc1, Vector3fc vector3fc1, Vector2fc vector2fc2, Vector3f vector3f
    ) {
        float float0 = vector2fc1.x() - vector2fc0.x();
        float float1 = vector2fc2.x() - vector2fc0.x();
        float float2 = 1.0F / (float0 * (vector2fc2.y() - vector2fc0.y()) - float1 * (vector2fc1.y() - vector2fc0.y()));
        vector3f.x = float2 * (-float1 * (vector3fc2.x() - vector3fc0.x()) + float0 * (vector3fc1.x() - vector3fc0.x()));
        vector3f.y = float2 * (-float1 * (vector3fc2.y() - vector3fc0.y()) + float0 * (vector3fc1.y() - vector3fc0.y()));
        vector3f.z = float2 * (-float1 * (vector3fc2.z() - vector3fc0.z()) + float0 * (vector3fc1.z() - vector3fc0.z()));
        vector3f.normalize();
    }

    public static void tangentBitangent(
        Vector3fc vector3fc0,
        Vector2fc vector2fc0,
        Vector3fc vector3fc2,
        Vector2fc vector2fc1,
        Vector3fc vector3fc1,
        Vector2fc vector2fc2,
        Vector3f vector3f0,
        Vector3f vector3f1
    ) {
        float float0 = vector2fc1.y() - vector2fc0.y();
        float float1 = vector2fc2.y() - vector2fc0.y();
        float float2 = vector2fc1.x() - vector2fc0.x();
        float float3 = vector2fc2.x() - vector2fc0.x();
        float float4 = 1.0F / (float2 * float1 - float3 * float0);
        vector3f0.x = float4 * (float1 * (vector3fc2.x() - vector3fc0.x()) - float0 * (vector3fc1.x() - vector3fc0.x()));
        vector3f0.y = float4 * (float1 * (vector3fc2.y() - vector3fc0.y()) - float0 * (vector3fc1.y() - vector3fc0.y()));
        vector3f0.z = float4 * (float1 * (vector3fc2.z() - vector3fc0.z()) - float0 * (vector3fc1.z() - vector3fc0.z()));
        vector3f0.normalize();
        vector3f1.x = float4 * (-float3 * (vector3fc2.x() - vector3fc0.x()) + float2 * (vector3fc1.x() - vector3fc0.x()));
        vector3f1.y = float4 * (-float3 * (vector3fc2.y() - vector3fc0.y()) + float2 * (vector3fc1.y() - vector3fc0.y()));
        vector3f1.z = float4 * (-float3 * (vector3fc2.z() - vector3fc0.z()) + float2 * (vector3fc1.z() - vector3fc0.z()));
        vector3f1.normalize();
    }
}
