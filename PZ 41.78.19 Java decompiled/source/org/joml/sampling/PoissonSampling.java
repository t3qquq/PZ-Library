// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml.sampling;

import java.util.ArrayList;
import org.joml.Random;
import org.joml.Vector2f;

public class PoissonSampling {
    public static class Disk {
        private final Vector2f[] grid;
        private final float diskRadius;
        private final float diskRadiusSquared;
        private final float minDist;
        private final float minDistSquared;
        private final float cellSize;
        private final int numCells;
        private final Random rnd;
        private final ArrayList processList;

        public Disk(long long0, float float0, float float1, int int0, Callback2d callback2d) {
            this.diskRadius = float0;
            this.diskRadiusSquared = float0 * float0;
            this.minDist = float1;
            this.minDistSquared = float1 * float1;
            this.rnd = new Random(long0);
            this.cellSize = float1 / (float)Math.sqrt(2.0);
            this.numCells = (int)(float0 * 2.0F / this.cellSize) + 1;
            this.grid = new Vector2f[this.numCells * this.numCells];
            this.processList = new ArrayList();
            this.compute(int0, callback2d);
        }

        private void compute(int int2, Callback2d callback2d) {
            float float0;
            float float1;
            do {
                float0 = this.rnd.nextFloat() * 2.0F - 1.0F;
                float1 = this.rnd.nextFloat() * 2.0F - 1.0F;
            } while (float0 * float0 + float1 * float1 > 1.0F);

            Vector2f vector2f0 = new Vector2f(float0, float1);
            this.processList.add(vector2f0);
            callback2d.onNewSample(vector2f0.x, vector2f0.y);
            this.insert(vector2f0);

            while (!this.processList.isEmpty()) {
                int int0 = this.rnd.nextInt(this.processList.size());
                Vector2f vector2f1 = (Vector2f)this.processList.get(int0);
                boolean boolean0 = false;

                for (int int1 = 0; int1 < int2; int1++) {
                    float float2 = this.rnd.nextFloat() * (float) (java.lang.Math.PI * 2);
                    float float3 = this.minDist * (this.rnd.nextFloat() + 1.0F);
                    float0 = (float)(float3 * Math.sin_roquen_9(float2 + (java.lang.Math.PI / 2)));
                    float1 = (float)(float3 * Math.sin_roquen_9(float2));
                    float0 += vector2f1.x;
                    float1 += vector2f1.y;
                    if (!(float0 * float0 + float1 * float1 > this.diskRadiusSquared) && !this.searchNeighbors(float0, float1)) {
                        boolean0 = true;
                        callback2d.onNewSample(float0, float1);
                        Vector2f vector2f2 = new Vector2f(float0, float1);
                        this.processList.add(vector2f2);
                        this.insert(vector2f2);
                        break;
                    }
                }

                if (!boolean0) {
                    this.processList.remove(int0);
                }
            }
        }

        private boolean searchNeighbors(float float1, float float0) {
            int int0 = (int)((float0 + this.diskRadius) / this.cellSize);
            int int1 = (int)((float1 + this.diskRadius) / this.cellSize);
            if (this.grid[int0 * this.numCells + int1] != null) {
                return true;
            } else {
                int int2 = Math.max(0, int1 - 1);
                int int3 = Math.max(0, int0 - 1);
                int int4 = Math.min(int1 + 1, this.numCells - 1);
                int int5 = Math.min(int0 + 1, this.numCells - 1);

                for (int int6 = int3; int6 <= int5; int6++) {
                    for (int int7 = int2; int7 <= int4; int7++) {
                        Vector2f vector2f = this.grid[int6 * this.numCells + int7];
                        if (vector2f != null) {
                            float float2 = vector2f.x - float1;
                            float float3 = vector2f.y - float0;
                            if (float2 * float2 + float3 * float3 < this.minDistSquared) {
                                return true;
                            }
                        }
                    }
                }

                return false;
            }
        }

        private void insert(Vector2f vector2f) {
            int int0 = (int)((vector2f.y + this.diskRadius) / this.cellSize);
            int int1 = (int)((vector2f.x + this.diskRadius) / this.cellSize);
            this.grid[int0 * this.numCells + int1] = vector2f;
        }
    }
}
