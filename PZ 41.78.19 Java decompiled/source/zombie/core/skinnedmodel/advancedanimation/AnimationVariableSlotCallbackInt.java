// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;

public final class AnimationVariableSlotCallbackInt extends AnimationVariableSlotCallback<Integer> {
    private int m_defaultValue = 0;

    public AnimationVariableSlotCallbackInt(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
        super(string, callbackGetStrongTyped);
    }

    public AnimationVariableSlotCallbackInt(
        String string,
        AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    public AnimationVariableSlotCallbackInt(String string, int int0, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
        super(string, callbackGetStrongTyped);
        this.m_defaultValue = int0;
    }

    public AnimationVariableSlotCallbackInt(
        String string,
        int int0,
        AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = int0;
    }

    public Integer getDefaultValue() {
        return this.m_defaultValue;
    }

    @Override
    public String getValueString() {
        return this.getValue().toString();
    }

    @Override
    public float getValueFloat() {
        return this.getValue().intValue();
    }

    @Override
    public boolean getValueBool() {
        return this.getValueFloat() != 0.0F;
    }

    @Override
    public void setValue(String string) {
        this.trySetValue(PZMath.tryParseInt(string, 0));
    }

    @Override
    public void setValue(float float0) {
        this.trySetValue((int)float0);
    }

    @Override
    public void setValue(boolean boolean0) {
        this.trySetValue(boolean0 ? 1 : 0);
    }

    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Float;
    }

    @Override
    public boolean canConvertFrom(String var1) {
        return true;
    }

    public interface CallbackGetStrongTyped extends AnimationVariableSlotCallback.CallbackGet<Integer> {
    }

    public interface CallbackSetStrongTyped extends AnimationVariableSlotCallback.CallbackSet<Integer> {
    }
}
