// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

public final class UITransition {
    private float duration;
    private float elapsed;
    private float frac;
    private boolean fadeOut;
    private boolean bIgnoreUpdateTime = false;
    private long updateTimeMS;
    private static long currentTimeMS;
    private static long elapsedTimeMS;

    public static void UpdateAll() {
        long long0 = System.currentTimeMillis();
        elapsedTimeMS = long0 - currentTimeMS;
        currentTimeMS = long0;
    }

    public UITransition() {
        this.duration = 100.0F;
    }

    public void init(float _duration, boolean _fadeOut) {
        this.duration = Math.max(_duration, 1.0F);
        if (this.frac >= 1.0F) {
            this.elapsed = 0.0F;
        } else if (this.fadeOut != _fadeOut) {
            this.elapsed = (1.0F - this.frac) * this.duration;
        } else {
            this.elapsed = this.frac * this.duration;
        }

        this.fadeOut = _fadeOut;
    }

    public void update() {
        if (!this.bIgnoreUpdateTime && this.updateTimeMS != 0L) {
            long long0 = (long)this.duration;
            if (this.updateTimeMS + long0 < currentTimeMS) {
                this.elapsed = this.duration;
            }
        }

        this.updateTimeMS = currentTimeMS;
        this.frac = this.elapsed / this.duration;
        this.elapsed = Math.min(this.elapsed + (float)elapsedTimeMS, this.duration);
    }

    public float fraction() {
        return this.fadeOut ? 1.0F - this.frac : this.frac;
    }

    public void setFadeIn(boolean fadeIn) {
        if (fadeIn) {
            if (this.fadeOut) {
                this.init(100.0F, false);
            }
        } else if (!this.fadeOut) {
            this.init(200.0F, true);
        }
    }

    public void reset() {
        this.elapsed = 0.0F;
    }

    public void setIgnoreUpdateTime(boolean ignore) {
        this.bIgnoreUpdateTime = ignore;
    }

    public float getElapsed() {
        return this.elapsed;
    }

    public void setElapsed(float _elapsed) {
        this.elapsed = _elapsed;
    }
}
