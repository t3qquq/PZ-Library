// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

public class Interpolationf {
    public static float interpolateTriangle(
        float float7,
        float float13,
        float float19,
        float float5,
        float float1,
        float float18,
        float float4,
        float float2,
        float float17,
        float float11,
        float float9
    ) {
        float float0 = float1 - float2;
        float float3 = float4 - float5;
        float float6 = float7 - float4;
        float float8 = float9 - float2;
        float float10 = float11 - float4;
        float float12 = float13 - float2;
        float float14 = 1.0F / (float0 * float6 + float3 * float12);
        float float15 = (float0 * float10 + float3 * float8) * float14;
        float float16 = (float6 * float8 - float12 * float10) * float14;
        return float15 * float19 + float16 * float18 + (1.0F - float15 - float16) * float17;
    }

    public static Vector2f interpolateTriangle(
        float float7,
        float float13,
        float float20,
        float float23,
        float float5,
        float float1,
        float float19,
        float float22,
        float float4,
        float float2,
        float float18,
        float float21,
        float float11,
        float float9,
        Vector2f vector2f
    ) {
        float float0 = float1 - float2;
        float float3 = float4 - float5;
        float float6 = float7 - float4;
        float float8 = float9 - float2;
        float float10 = float11 - float4;
        float float12 = float13 - float2;
        float float14 = 1.0F / (float0 * float6 + float3 * float12);
        float float15 = (float0 * float10 + float3 * float8) * float14;
        float float16 = (float6 * float8 - float12 * float10) * float14;
        float float17 = 1.0F - float15 - float16;
        vector2f.x = float15 * float20 + float16 * float19 + float17 * float18;
        vector2f.y = float15 * float23 + float16 * float22 + float17 * float21;
        return vector2f;
    }

    public static Vector2f dFdxLinear(
        float float8,
        float float4,
        float float13,
        float float16,
        float float7,
        float float1,
        float float12,
        float float15,
        float float6,
        float float2,
        float float11,
        float float14,
        Vector2f vector2f
    ) {
        float float0 = float1 - float2;
        float float3 = float4 - float2;
        float float5 = float0 * (float8 - float6) + (float6 - float7) * float3;
        float float9 = float5 - float0 + float3;
        float float10 = 1.0F / float5;
        vector2f.x = float10 * (float0 * float13 - float3 * float12 + float9 * float11) - float11;
        vector2f.y = float10 * (float0 * float16 - float3 * float15 + float9 * float14) - float14;
        return vector2f;
    }

    public static Vector2f dFdyLinear(
        float float4,
        float float6,
        float float13,
        float float16,
        float float2,
        float float8,
        float float12,
        float float15,
        float float1,
        float float7,
        float float11,
        float float14,
        Vector2f vector2f
    ) {
        float float0 = float1 - float2;
        float float3 = float4 - float1;
        float float5 = (float8 - float7) * float3 + float0 * (float6 - float7);
        float float9 = float5 - float0 - float3;
        float float10 = 1.0F / float5;
        vector2f.x = float10 * (float0 * float13 + float3 * float12 + float9 * float11) - float11;
        vector2f.y = float10 * (float0 * float16 + float3 * float15 + float9 * float14) - float14;
        return vector2f;
    }

    public static Vector3f interpolateTriangle(
        float float0,
        float float1,
        float float16,
        float float13,
        float float10,
        float float2,
        float float3,
        float float15,
        float float12,
        float float9,
        float float4,
        float float5,
        float float14,
        float float11,
        float float8,
        float float6,
        float float7,
        Vector3f vector3f
    ) {
        interpolationFactorsTriangle(float0, float1, float2, float3, float4, float5, float6, float7, vector3f);
        return vector3f.set(
            vector3f.x * float16 + vector3f.y * float15 + vector3f.z * float14,
            vector3f.x * float13 + vector3f.y * float12 + vector3f.z * float11,
            vector3f.x * float10 + vector3f.y * float9 + vector3f.z * float8
        );
    }

    public static Vector3f interpolationFactorsTriangle(
        float float7, float float13, float float5, float float1, float float4, float float2, float float11, float float9, Vector3f vector3f
    ) {
        float float0 = float1 - float2;
        float float3 = float4 - float5;
        float float6 = float7 - float4;
        float float8 = float9 - float2;
        float float10 = float11 - float4;
        float float12 = float13 - float2;
        float float14 = 1.0F / (float0 * float6 + float3 * float12);
        vector3f.x = (float0 * float10 + float3 * float8) * float14;
        vector3f.y = (float6 * float8 - float12 * float10) * float14;
        vector3f.z = 1.0F - vector3f.x - vector3f.y;
        return vector3f;
    }
}
