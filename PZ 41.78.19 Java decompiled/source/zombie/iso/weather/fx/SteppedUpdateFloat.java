// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.weather.fx;

/**
 * TurboTuTone.
 */
public class SteppedUpdateFloat {
    private float current;
    private float step;
    private float target;
    private float min;
    private float max;

    public SteppedUpdateFloat(float _current, float _step, float _min, float _max) {
        this.current = _current;
        this.step = _step;
        this.target = _current;
        this.min = _min;
        this.max = _max;
    }

    public float value() {
        return this.current;
    }

    public void setTarget(float _target) {
        this.target = this.clamp(this.min, this.max, _target);
    }

    public float getTarget() {
        return this.target;
    }

    public void overrideCurrentValue(float f) {
        this.current = f;
    }

    private float clamp(float float2, float float1, float float0) {
        float0 = Math.min(float1, float0);
        return Math.max(float2, float0);
    }

    public void update(float delta) {
        if (this.current != this.target) {
            if (this.target > this.current) {
                this.current = this.current + this.step * delta;
                if (this.current > this.target) {
                    this.current = this.target;
                }
            } else if (this.target < this.current) {
                this.current = this.current - this.step * delta;
                if (this.current < this.target) {
                    this.current = this.target;
                }
            }
        }
    }
}
