// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.popman.ObjectPool;

public final class IsoPuddlesCompute {
    private static final float Pi = 3.1415F;
    private static float puddlesDirNE;
    private static float puddlesDirNW;
    private static float puddlesDirAll;
    private static float puddlesDirNone;
    private static float puddlesSize;
    private static boolean hd_quality = true;
    private static final Vector2f add = new Vector2f(1.0F, 0.0F);
    private static final Vector3f add_xyy = new Vector3f(1.0F, 0.0F, 0.0F);
    private static final Vector3f add_xxy = new Vector3f(1.0F, 1.0F, 0.0F);
    private static final Vector3f add_xxx = new Vector3f(1.0F, 1.0F, 1.0F);
    private static final Vector3f add_xyx = new Vector3f(1.0F, 0.0F, 1.0F);
    private static final Vector3f add_yxy = new Vector3f(0.0F, 1.0F, 0.0F);
    private static final Vector3f add_yyx = new Vector3f(0.0F, 0.0F, 1.0F);
    private static final Vector3f add_yxx = new Vector3f(0.0F, 1.0F, 1.0F);
    private static final Vector3f HashVector31 = new Vector3f(17.1F, 31.7F, 32.6F);
    private static final Vector3f HashVector32 = new Vector3f(29.5F, 13.3F, 42.6F);
    private static final ObjectPool<Vector3f> pool_vector3f = new ObjectPool<>(Vector3f::new);
    private static final ArrayList<Vector3f> allocated_vector3f = new ArrayList<>();
    private static final Vector2f temp_vector2f = new Vector2f();

    private static Vector3f allocVector3f(float float0, float float1, float float2) {
        Vector3f vector3f = pool_vector3f.alloc().set(float0, float1, float2);
        allocated_vector3f.add(vector3f);
        return vector3f;
    }

    private static Vector3f allocVector3f(Vector3f vector3f) {
        return allocVector3f(vector3f.x, vector3f.y, vector3f.z);
    }

    private static Vector3f floor(Vector3f vector3f) {
        return allocVector3f((float)Math.floor(vector3f.x), (float)Math.floor(vector3f.y), (float)Math.floor(vector3f.z));
    }

    private static Vector3f fract(Vector3f vector3f) {
        return allocVector3f(fract(vector3f.x), fract(vector3f.y), fract(vector3f.z));
    }

    private static float fract(float float0) {
        return (float)(float0 - Math.floor(float0));
    }

    private static float mix(float float2, float float0, float float1) {
        return float2 * (1.0F - float1) + float0 * float1;
    }

    private static float FuncHash(Vector3f vector3f1) {
        Vector3f vector3f0 = allocVector3f(vector3f1.dot(HashVector31), vector3f1.dot(HashVector32), 0.0F);
        return fract((float)(Math.sin(vector3f0.x * 2.1 + 1.1) + Math.sin(vector3f0.y * 2.5 + 1.5)));
    }

    private static float FuncNoise(Vector3f vector3f1) {
        Vector3f vector3f0 = floor(vector3f1);
        Vector3f vector3f2 = fract(vector3f1);
        Vector3f vector3f3 = allocVector3f(
            vector3f2.x * vector3f2.x * (4.5F - 3.5F * vector3f2.x),
            vector3f2.y * vector3f2.y * (4.5F - 3.5F * vector3f2.y),
            vector3f2.z * vector3f2.z * (4.5F - 3.5F * vector3f2.z)
        );
        float float0 = mix(FuncHash(vector3f0), FuncHash(allocVector3f(vector3f0).add(add_xyy)), vector3f3.x);
        float float1 = mix(FuncHash(allocVector3f(vector3f0).add(add_yxy)), FuncHash(allocVector3f(vector3f0).add(add_xxy)), vector3f3.x);
        float float2 = mix(FuncHash(allocVector3f(vector3f0).add(add_yyx)), FuncHash(allocVector3f(vector3f0).add(add_xyx)), vector3f3.x);
        float float3 = mix(FuncHash(allocVector3f(vector3f0).add(add_yxx)), FuncHash(allocVector3f(vector3f0).add(add_xxx)), vector3f3.x);
        float float4 = mix(float0, float1, vector3f3.y);
        float float5 = mix(float2, float3, vector3f3.y);
        return mix(float4, float5, vector3f3.z);
    }

    private static float PerlinNoise(Vector3f vector3f) {
        if (hd_quality) {
            vector3f.mul(0.5F);
            float float0 = 0.5F * FuncNoise(vector3f);
            vector3f.mul(3.0F);
            float0 = (float)(float0 + 0.25 * FuncNoise(vector3f));
            vector3f.mul(3.0F);
            float0 = (float)(float0 + 0.125 * FuncNoise(vector3f));
            return (float)(
                float0 * Math.min(1.0, 2.0 * FuncNoise(allocVector3f(vector3f).mul(0.02F)) * Math.min(1.0, 1.0 * FuncNoise(allocVector3f(vector3f).mul(0.1F))))
            );
        } else {
            return FuncNoise(vector3f) * 0.5F;
        }
    }

    private static float getPuddles(Vector2f vector2f) {
        float float0 = puddlesDirNE;
        float float1 = puddlesDirNW;
        float float2 = puddlesDirAll;
        vector2f.mul(10.0F);
        float float3 = 1.02F * puddlesSize;
        float3 = (float)(
            float3
                + float0
                    * Math.sin((vector2f.x * 1.0 + vector2f.y * 2.0) * 3.1415F * 1.0)
                    * Math.cos((vector2f.x * 1.0 + vector2f.y * 2.0) * 3.1415F * 1.0)
                    * 2.0
        );
        float3 = (float)(
            float3
                + float1
                    * Math.sin((vector2f.x * 1.0 - vector2f.y * 2.0) * 3.1415F * 1.0)
                    * Math.cos((vector2f.x * 1.0 - vector2f.y * 2.0) * 3.1415F * 1.0)
                    * 2.0
        );
        float3 = (float)(float3 + float2 * 0.3);
        float float4 = PerlinNoise(allocVector3f(vector2f.x * 1.0F, 0.0F, vector2f.y * 2.0F));
        float float5 = Math.min(0.7F, float3 * float4);
        float4 = Math.min(0.7F, PerlinNoise(allocVector3f(vector2f.x * 0.7F, 1.0F, vector2f.y * 0.7F)));
        return float5 + float4;
    }

    public static float computePuddle(IsoGridSquare square) {
        pool_vector3f.release(allocated_vector3f);
        allocated_vector3f.clear();
        hd_quality = PerformanceSettings.PuddlesQuality == 0;
        if (!Core.getInstance().getUseShaders()) {
            return -0.1F;
        } else if (Core.getInstance().getPerfPuddlesOnLoad() == 3 || Core.getInstance().getPerfPuddles() == 3) {
            return -0.1F;
        } else if (Core.getInstance().getPerfPuddles() > 0 && square.z > 0) {
            return -0.1F;
        } else {
            IsoPuddles puddles = IsoPuddles.getInstance();
            puddlesSize = puddles.getPuddlesSize();
            if (puddlesSize <= 0.0F) {
                return -0.1F;
            } else {
                Vector4f vector4f = puddles.getShaderOffsetMain();
                vector4f.x -= 90000.0F;
                vector4f.y -= 640000.0F;
                int int0 = (int)IsoCamera.frameState.OffX;
                int int1 = (int)IsoCamera.frameState.OffY;
                float float0 = IsoUtils.XToScreen(square.x + 0.5F - square.z * 3.0F, square.y + 0.5F - square.z * 3.0F, 0.0F, 0) - int0;
                float float1 = IsoUtils.YToScreen(square.x + 0.5F - square.z * 3.0F, square.y + 0.5F - square.z * 3.0F, 0.0F, 0) - int1;
                float0 /= IsoCamera.frameState.OffscreenWidth;
                float1 /= IsoCamera.frameState.OffscreenHeight;
                if (Core.getInstance().getPerfPuddles() <= 1) {
                    square.getPuddles().recalcIfNeeded();
                    puddlesDirNE = (square.getPuddles().pdne[0] + square.getPuddles().pdne[2]) * 0.5F;
                    puddlesDirNW = (square.getPuddles().pdnw[0] + square.getPuddles().pdnw[2]) * 0.5F;
                    puddlesDirAll = (square.getPuddles().pda[0] + square.getPuddles().pda[2]) * 0.5F;
                    puddlesDirNone = (square.getPuddles().pnon[0] + square.getPuddles().pnon[2]) * 0.5F;
                } else {
                    puddlesDirNE = 0.0F;
                    puddlesDirNW = 0.0F;
                    puddlesDirAll = 1.0F;
                    puddlesDirNone = 0.0F;
                }

                Vector2f vector2f = temp_vector2f.set(
                    (float0 * vector4f.z + vector4f.x) * 8.0E-4F + square.z * 7.0F, (float1 * vector4f.w + vector4f.y) * 8.0E-4F + square.z * 7.0F
                );
                float float2 = (float)Math.pow(getPuddles(vector2f), 2.0);
                float float3 = (float)Math.min(Math.pow(float2, 0.3), 1.0) + float2;
                return float3 * puddlesSize - 0.34F;
            }
        }
    }
}
