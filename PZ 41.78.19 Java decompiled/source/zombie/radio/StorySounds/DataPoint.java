// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio.StorySounds;

/**
 * Turbo
 */
public final class DataPoint {
    protected float time = 0.0F;
    protected float intensity = 0.0F;

    public DataPoint(float _time, float _intensity) {
        this.setTime(_time);
        this.setIntensity(_intensity);
    }

    public float getTime() {
        return this.time;
    }

    public void setTime(float _time) {
        if (_time < 0.0F) {
            _time = 0.0F;
        }

        if (_time > 1.0F) {
            _time = 1.0F;
        }

        this.time = _time;
    }

    public float getIntensity() {
        return this.intensity;
    }

    public void setIntensity(float _intensity) {
        if (_intensity < 0.0F) {
            _intensity = 0.0F;
        }

        if (_intensity > 1.0F) {
            _intensity = 1.0F;
        }

        this.intensity = _intensity;
    }
}
