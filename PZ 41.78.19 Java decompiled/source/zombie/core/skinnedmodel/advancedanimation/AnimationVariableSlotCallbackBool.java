// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;

public final class AnimationVariableSlotCallbackBool extends AnimationVariableSlotCallback<Boolean> {
    private boolean m_defaultValue = false;

    public AnimationVariableSlotCallbackBool(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
        super(string, callbackGetStrongTyped);
    }

    public AnimationVariableSlotCallbackBool(
        String string,
        AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    public AnimationVariableSlotCallbackBool(String string, boolean boolean0, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
        super(string, callbackGetStrongTyped);
        this.m_defaultValue = boolean0;
    }

    public AnimationVariableSlotCallbackBool(
        String string,
        boolean boolean0,
        AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = boolean0;
    }

    public Boolean getDefaultValue() {
        return this.m_defaultValue;
    }

    @Override
    public String getValueString() {
        return this.getValue() ? "true" : "false";
    }

    @Override
    public float getValueFloat() {
        return this.getValue() ? 1.0F : 0.0F;
    }

    @Override
    public boolean getValueBool() {
        return this.getValue();
    }

    @Override
    public void setValue(String string) {
        this.trySetValue(StringUtils.tryParseBoolean(string));
    }

    @Override
    public void setValue(float float0) {
        this.trySetValue(float0 != 0.0);
    }

    @Override
    public void setValue(boolean boolean0) {
        this.trySetValue(boolean0);
    }

    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Boolean;
    }

    @Override
    public boolean canConvertFrom(String string) {
        return StringUtils.tryParseBoolean(string);
    }

    /**
     * Strong-typed utility type. Useful for auto-typed function overrides, such as
     */
    public interface CallbackGetStrongTyped extends AnimationVariableSlotCallback.CallbackGet<Boolean> {
    }

    public interface CallbackSetStrongTyped extends AnimationVariableSlotCallback.CallbackSet<Boolean> {
    }
}
