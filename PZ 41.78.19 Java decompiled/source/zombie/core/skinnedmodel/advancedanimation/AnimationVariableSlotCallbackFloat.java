// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;

public final class AnimationVariableSlotCallbackFloat extends AnimationVariableSlotCallback<Float> {
    private float m_defaultValue = 0.0F;

    public AnimationVariableSlotCallbackFloat(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
        super(string, callbackGetStrongTyped);
    }

    public AnimationVariableSlotCallbackFloat(
        String string,
        AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    public AnimationVariableSlotCallbackFloat(String string, float float0, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
        super(string, callbackGetStrongTyped);
        this.m_defaultValue = float0;
    }

    public AnimationVariableSlotCallbackFloat(
        String string,
        float float0,
        AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = float0;
    }

    public Float getDefaultValue() {
        return this.m_defaultValue;
    }

    @Override
    public String getValueString() {
        return this.getValue().toString();
    }

    @Override
    public float getValueFloat() {
        return this.getValue();
    }

    @Override
    public boolean getValueBool() {
        return this.getValueFloat() != 0.0F;
    }

    @Override
    public void setValue(String string) {
        this.trySetValue(PZMath.tryParseFloat(string, 0.0F));
    }

    @Override
    public void setValue(float float0) {
        this.trySetValue(float0);
    }

    @Override
    public void setValue(boolean boolean0) {
        this.trySetValue(boolean0 ? 1.0F : 0.0F);
    }

    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Float;
    }

    @Override
    public boolean canConvertFrom(String var1) {
        return true;
    }

    public interface CallbackGetStrongTyped extends AnimationVariableSlotCallback.CallbackGet<Float> {
    }

    public interface CallbackSetStrongTyped extends AnimationVariableSlotCallback.CallbackSet<Float> {
    }
}
