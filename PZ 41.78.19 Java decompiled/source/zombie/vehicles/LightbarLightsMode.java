// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

/**
 * Created by kroto on 9/20/2017.
 */
public final class LightbarLightsMode {
    private long startTime = 0L;
    private int light = 0;
    private final int modeMax = 3;
    private int mode = 0;

    public int get() {
        return this.mode;
    }

    public void set(int v) {
        if (v > 3) {
            this.mode = 3;
        } else if (v < 0) {
            this.mode = 0;
        } else {
            this.mode = v;
            if (this.mode != 0) {
                this.start();
            }
        }
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void update() {
        long long0 = System.currentTimeMillis() - this.startTime;
        switch (this.mode) {
            case 1:
                long0 %= 1000L;
                if (long0 < 50L) {
                    this.light = 0;
                } else if (long0 < 450L) {
                    this.light = 1;
                } else if (long0 < 550L) {
                    this.light = 0;
                } else if (long0 < 950L) {
                    this.light = 2;
                } else {
                    this.light = 0;
                }
                break;
            case 2:
                long0 %= 1000L;
                if (long0 < 50L) {
                    this.light = 0;
                } else if (long0 < 250L) {
                    this.light = 1;
                } else if (long0 < 300L) {
                    this.light = 0;
                } else if (long0 < 500L) {
                    this.light = 1;
                } else if (long0 < 550L) {
                    this.light = 0;
                } else if (long0 < 750L) {
                    this.light = 2;
                } else if (long0 < 800L) {
                    this.light = 0;
                } else {
                    this.light = 2;
                }
                break;
            case 3:
                long0 %= 300L;
                if (long0 < 25L) {
                    this.light = 0;
                } else if (long0 < 125L) {
                    this.light = 1;
                } else if (long0 < 175L) {
                    this.light = 0;
                } else if (long0 < 275L) {
                    this.light = 2;
                } else {
                    this.light = 0;
                }
                break;
            default:
                this.light = 0;
        }
    }

    public int getLightTexIndex() {
        return this.light;
    }

    public boolean isEnable() {
        return this.mode != 0;
    }
}
