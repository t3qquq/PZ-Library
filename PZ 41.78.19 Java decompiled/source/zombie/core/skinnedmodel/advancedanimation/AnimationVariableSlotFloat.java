// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.core.math.PZMath;

public final class AnimationVariableSlotFloat extends AnimationVariableSlot {
    private float m_value = 0.0F;

    public AnimationVariableSlotFloat(String string) {
        super(string);
    }

    @Override
    public String getValueString() {
        return String.valueOf(this.m_value);
    }

    @Override
    public float getValueFloat() {
        return this.m_value;
    }

    @Override
    public boolean getValueBool() {
        return this.m_value != 0.0F;
    }

    @Override
    public void setValue(String string) {
        this.m_value = PZMath.tryParseFloat(string, 0.0F);
    }

    @Override
    public void setValue(float float0) {
        this.m_value = float0;
    }

    @Override
    public void setValue(boolean boolean0) {
        this.m_value = boolean0 ? 1.0F : 0.0F;
    }

    @Override
    public AnimationVariableType getType() {
        return AnimationVariableType.Float;
    }

    @Override
    public boolean canConvertFrom(String string) {
        return PZMath.canParseFloat(string);
    }

    @Override
    public void clear() {
        this.m_value = 0.0F;
    }
}
