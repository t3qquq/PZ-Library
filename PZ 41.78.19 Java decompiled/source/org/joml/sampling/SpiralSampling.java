// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml.sampling;

import org.joml.Random;

public class SpiralSampling {
    private final Random rnd;

    public SpiralSampling(long long0) {
        this.rnd = new Random(long0);
    }

    public void createEquiAngle(float float2, int int2, int int1, Callback2d callback2d) {
        for (int int0 = 0; int0 < int1; int0++) {
            float float0 = (float) (java.lang.Math.PI * 2) * (int0 * int2) / int1;
            float float1 = float2 * int0 / (int1 - 1);
            float float3 = (float)Math.sin_roquen_9(float0 + (float) (java.lang.Math.PI / 2)) * float1;
            float float4 = (float)Math.sin_roquen_9(float0) * float1;
            callback2d.onNewSample(float3, float4);
        }
    }

    public void createEquiAngle(float float1, int int0, int int2, float float4, Callback2d callback2d) {
        float float0 = float1 / int0;

        for (int int1 = 0; int1 < int2; int1++) {
            float float2 = (float) (java.lang.Math.PI * 2) * (int1 * int0) / int2;
            float float3 = float1 * int1 / (int2 - 1) + (this.rnd.nextFloat() * 2.0F - 1.0F) * float0 * float4;
            float float5 = (float)Math.sin_roquen_9(float2 + (float) (java.lang.Math.PI / 2)) * float3;
            float float6 = (float)Math.sin_roquen_9(float2) * float3;
            callback2d.onNewSample(float5, float6);
        }
    }
}
