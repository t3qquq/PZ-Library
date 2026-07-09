// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.debug.DebugLog;

public abstract class AnimationVariableSlotCallback<VariableType> extends AnimationVariableSlot {
    private final AnimationVariableSlotCallback.CallbackGet<VariableType> m_callbackGet;
    private final AnimationVariableSlotCallback.CallbackSet<VariableType> m_callbackSet;

    protected AnimationVariableSlotCallback(String string, AnimationVariableSlotCallback.CallbackGet<VariableType> callbackGet) {
        this(string, callbackGet, null);
    }

    protected AnimationVariableSlotCallback(
        String string, AnimationVariableSlotCallback.CallbackGet<VariableType> callbackGet, AnimationVariableSlotCallback.CallbackSet<VariableType> callbackSet
    ) {
        super(string);
        this.m_callbackGet = callbackGet;
        this.m_callbackSet = callbackSet;
    }

    public VariableType getValue() {
        return this.m_callbackGet.call();
    }

    public abstract VariableType getDefaultValue();

    public boolean trySetValue(VariableType object) {
        if (this.isReadOnly()) {
            DebugLog.General.warn("Trying to set read-only variable \"%s\"", super.getKey());
            return false;
        } else {
            this.m_callbackSet.call((VariableType)object);
            return true;
        }
    }

    @Override
    public boolean isReadOnly() {
        return this.m_callbackSet == null;
    }

    @Override
    public void clear() {
        if (!this.isReadOnly()) {
            this.trySetValue(this.getDefaultValue());
        }
    }

    public interface CallbackGet<VariableType> {
        VariableType call();
    }

    public interface CallbackSet<VariableType> {
        void call(VariableType var1);
    }
}
