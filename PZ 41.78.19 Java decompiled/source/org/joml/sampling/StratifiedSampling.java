// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml.sampling;

import org.joml.Random;

public class StratifiedSampling {
    private final Random rnd;

    public StratifiedSampling(long long0) {
        this.rnd = new Random(long0);
    }

    public void generateRandom(int int1, Callback2d callback2d) {
        for (int int0 = 0; int0 < int1; int0++) {
            for (int int2 = 0; int2 < int1; int2++) {
                float float0 = (this.rnd.nextFloat() / int1 + (float)int2 / int1) * 2.0F - 1.0F;
                float float1 = (this.rnd.nextFloat() / int1 + (float)int0 / int1) * 2.0F - 1.0F;
                callback2d.onNewSample(float0, float1);
            }
        }
    }

    public void generateCentered(int int1, float float1, Callback2d callback2d) {
        float float0 = float1 * 0.5F;
        float float2 = 1.0F - float1;

        for (int int0 = 0; int0 < int1; int0++) {
            for (int int2 = 0; int2 < int1; int2++) {
                float float3 = ((float0 + this.rnd.nextFloat() * float2) / int1 + (float)int2 / int1) * 2.0F - 1.0F;
                float float4 = ((float0 + this.rnd.nextFloat() * float2) / int1 + (float)int0 / int1) * 2.0F - 1.0F;
                callback2d.onNewSample(float3, float4);
            }
        }
    }
}
