// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import zombie.core.PerformanceSettings;

public final class FPSTracking {
    private final double[] lastFPS = new double[20];
    private int lastFPSCount = 0;
    private long timeAtLastUpdate;
    private final long[] last10 = new long[10];
    private int last10index = 0;

    public void init() {
        for (int int0 = 0; int0 < 20; int0++) {
            this.lastFPS[int0] = PerformanceSettings.getLockFPS();
        }

        this.timeAtLastUpdate = System.nanoTime();
    }

    public long frameStep() {
        long long0 = System.nanoTime();
        long long1 = long0 - this.timeAtLastUpdate;
        if (long1 > 0L) {
            float float0 = 0.0F;
            double double0 = long1 / 1.0E9;
            double double1 = 1.0 / double0;
            this.lastFPS[this.lastFPSCount] = double1;
            this.lastFPSCount++;
            if (this.lastFPSCount >= 5) {
                this.lastFPSCount = 0;
            }

            for (int int0 = 0; int0 < 5; int0++) {
                float0 = (float)(float0 + this.lastFPS[int0]);
            }

            float0 /= 5.0F;
            GameWindow.averageFPS = float0;
            GameTime.instance.FPSMultiplier = (float)(60.0 / double1);
            if (GameTime.instance.FPSMultiplier > 5.0F) {
                GameTime.instance.FPSMultiplier = 5.0F;
            }
        }

        this.timeAtLastUpdate = long0;
        this.updateFPS(long1);
        return long1;
    }

    public void updateFPS(long timeDiff) {
        this.last10[this.last10index++] = timeDiff;
        if (this.last10index >= this.last10.length) {
            this.last10index = 0;
        }

        float float0 = 11110.0F;
        float float1 = -11110.0F;

        for (long long0 : this.last10) {
            if (long0 != 0L) {
                if ((float)long0 < float0) {
                    float0 = (float)long0;
                }

                if ((float)long0 > float1) {
                    float1 = (float)long0;
                }
            }
        }
    }
}
