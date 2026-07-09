// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;
import zombie.util.StringUtils;

public final class AnimationVariableSlotCallbackString extends AnimationVariableSlotCallback<String> {
    private String m_defaultValue = "";

    public AnimationVariableSlotCallbackString(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
        super(string, callbackGetStrongTyped);
    }

    public AnimationVariableSlotCallbackString(
        String string,
        AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string, callbackGetStrongTyped, callbackSetStrongTyped);
    }

    public AnimationVariableSlotCallbackString(
        String string0, String string1, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped
    ) {
        super(string0, callbackGetStrongTyped);
        this.m_defaultValue = string1;
    }

    public AnimationVariableSlotCallbackString(
        String string0,
        String string1,
        AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped,
        AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped
    ) {
        super(string0, callbackGetStrongTyped, callbackSetStrongTyped);
        this.m_defaultValue = string1;
    }

    public String getDefaultValue() {
        return this.m_defaultValue;
    }

    @Override
    public String getValueString() {
        return this.getValue();
    }

    @Override
    public float getValueFloat() {
        return PZMath.tryParseFloat(this.getValue(), 0.0F);
    }

    @Override
    public boolean getValueBool() {
        return StringUtils.tryParseBoolean(this.getValue());
    }

    @Override
    public void setValue(String string) {
        this.trySetValue(string);
    }

    @Override
    public void setValue(float float0) {
        this.trySetValue(String.valueOf(float0));
    }

    @Override
    public void setValue(boolean boolean0) {
        this.trySetValue(boolean0 ? "true" : "false");
    }

    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.String;
    }

    @Override
    public boolean canConvertFrom(String var1) {
        return true;
    }

    public interface CallbackGetStrongTyped extends AnimationVariableSlotCallback.CallbackGet<String> {
    }

    public interface CallbackSetStrongTyped extends AnimationVariableSlotCallback.CallbackSet<String> {
    }
}
