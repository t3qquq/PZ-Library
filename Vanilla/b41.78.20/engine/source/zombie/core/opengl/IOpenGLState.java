// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.opengl;

public abstract class IOpenGLState<T extends IOpenGLState.Value> {
    protected T currentValue = this.defaultValue();
    private boolean dirty = true;

    public void set(T value) {
        if (this.dirty || !value.equals(this.currentValue)) {
            this.setCurrentValue((T)value);
            this.Set((T)value);
        }
    }

    void setCurrentValue(T value) {
        this.dirty = false;
        this.currentValue.set(value);
    }

    public void setDirty() {
        this.dirty = true;
    }

    T getCurrentValue() {
        return this.currentValue;
    }

    abstract T defaultValue();

    abstract void Set(T var1);

    public interface Value {
        IOpenGLState.Value set(IOpenGLState.Value var1);
    }
}
