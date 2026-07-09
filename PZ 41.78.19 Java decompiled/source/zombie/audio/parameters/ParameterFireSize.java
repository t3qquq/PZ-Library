// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.audio.parameters;

import zombie.audio.FMODLocalParameter;

public final class ParameterFireSize extends FMODLocalParameter {
    private int size = 0;

    public ParameterFireSize() {
        super("FireSize");
    }

    @Override
    public float calculateCurrentValue() {
        return this.size;
    }

    public void setSize(int _size) {
        this.size = _size;
    }
}
