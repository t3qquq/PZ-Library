// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml.sampling;

import org.joml.Random;

public class UniformSampling {
    public static class Disk {
        private final Random rnd;

        public Disk(long long0, int int0, Callback2d callback2d) {
            this.rnd = new Random(long0);
            this.generate(int0, callback2d);
        }

        private void generate(int int1, Callback2d callback2d) {
            for (int int0 = 0; int0 < int1; int0++) {
                float float0 = this.rnd.nextFloat();
                float float1 = this.rnd.nextFloat() * 2.0F * (float) java.lang.Math.PI;
                float float2 = Math.sqrt(float0);
                float float3 = float2 * (float)Math.sin_roquen_9(float1 + (java.lang.Math.PI / 2));
                float float4 = float2 * (float)Math.sin_roquen_9(float1);
                callback2d.onNewSample(float3, float4);
            }
        }
    }

    public static class Sphere {
        private final Random rnd;

        public Sphere(long long0, int int0, Callback3d callback3d) {
            this.rnd = new Random(long0);
            this.generate(int0, callback3d);
        }

        public void generate(int int1, Callback3d callback3d) {
            int int0 = 0;

            while (int0 < int1) {
                float float0 = this.rnd.nextFloat() * 2.0F - 1.0F;
                float float1 = this.rnd.nextFloat() * 2.0F - 1.0F;
                if (!(float0 * float0 + float1 * float1 >= 1.0F)) {
                    float float2 = (float)Math.sqrt(1.0 - float0 * float0 - float1 * float1);
                    float float3 = 2.0F * float0 * float2;
                    float float4 = 2.0F * float1 * float2;
                    float float5 = 1.0F - 2.0F * (float0 * float0 + float1 * float1);
                    callback3d.onNewSample(float3, float4, float5);
                    int0++;
                }
            }
        }
    }
}
