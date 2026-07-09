// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;
import zombie.util.StringUtils;

public final class AnimationVariableSlotString extends AnimationVariableSlot {
    private String m_value;

    public AnimationVariableSlotString(String string) {
        super(string);
    }

    @Override
    public String getValueString() {
        return this.m_value;
    }

    @Override
    public float getValueFloat() {
        return PZMath.tryParseFloat(this.m_value, 0.0F);
    }

    @Override
    public boolean getValueBool() {
        return StringUtils.tryParseBoolean(this.m_value);
    }

    @Override
    public void setValue(String string) {
        this.m_value = string;
    }

    @Override
    public void setValue(float float0) {
        this.m_value = String.valueOf(float0);
    }

    @Override
    public void setValue(boolean boolean0) {
        this.m_value = boolean0 ? "true" : "false";
    }

    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.String;
    }

    @Override
    public boolean canConvertFrom(String var1) {
        return true;
    }

    @Override
    public void clear() {
        this.m_value = "";
    }
}
