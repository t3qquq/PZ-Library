// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.util.StringUtils;

public final class AnimationVariableSlotBool extends AnimationVariableSlot {
    private boolean m_value;

    public AnimationVariableSlotBool(String string) {
        super(string);
    }

    @Override
    public String getValueString() {
        return this.m_value ? "true" : "false";
    }

    @Override
    public float getValueFloat() {
        return this.m_value ? 1.0F : 0.0F;
    }

    @Override
    public boolean getValueBool() {
        return this.m_value;
    }

    @Override
    public void setValue(String string) {
        this.m_value = StringUtils.tryParseBoolean(string);
    }

    @Override
    public void setValue(float float0) {
        this.m_value = float0 != 0.0;
    }

    @Override
    public void setValue(boolean boolean0) {
        this.m_value = boolean0;
    }

    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Boolean;
    }

    @Override
    public boolean canConvertFrom(String string) {
        return StringUtils.isBoolean(string);
    }

    @Override
    public void clear() {
        this.m_value = false;
    }
}
