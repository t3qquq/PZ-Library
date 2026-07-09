// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.utils;

public final class UpdateLimit {
    private long delay;
    private long last;
    private long lastPeriod;

    public UpdateLimit(long ms) {
        this.delay = ms;
        this.last = System.currentTimeMillis();
        this.lastPeriod = this.last;
    }

    public UpdateLimit(long ms, long shift) {
        this.delay = ms;
        this.last = System.currentTimeMillis() - shift;
        this.lastPeriod = this.last;
    }

    public void BlockCheck() {
        this.last = System.currentTimeMillis() + this.delay;
    }

    public void Reset(long ms) {
        this.delay = ms;
        this.Reset();
    }

    public void Reset() {
        this.last = System.currentTimeMillis();
        this.lastPeriod = System.currentTimeMillis();
    }

    public void setUpdatePeriod(long ms) {
        this.delay = ms;
    }

    public void setSmoothUpdatePeriod(long ms) {
        this.delay = (long)((float)this.delay + 0.1F * (float)(ms - this.delay));
    }

    public boolean Check() {
        long long0 = System.currentTimeMillis();
        if (long0 - this.last > this.delay) {
            if (long0 - this.last > 3L * this.delay) {
                this.last = long0;
            } else {
                this.last = this.last + this.delay;
            }

            return true;
        } else {
            return false;
        }
    }

    public long getLast() {
        return this.last;
    }

    public void updateTimePeriod() {
        long long0 = System.currentTimeMillis();
        if (long0 - this.last > this.delay) {
            if (long0 - this.last > 3L * this.delay) {
                this.last = long0;
            } else {
                this.last = this.last + this.delay;
            }
        }

        this.lastPeriod = long0;
    }

    public double getTimePeriod() {
        return Math.min(((double)System.currentTimeMillis() - this.lastPeriod) / this.delay, 1.0);
    }

    public long getDelay() {
        return this.delay;
    }
}
