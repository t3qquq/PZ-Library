// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.debug.DebugLog;

public final class AnimationVariableGenericSlot extends AnimationVariableSlot {
    private AnimationVariableType m_type = AnimationVariableType.Void;
    private IAnimationVariableSlot m_valueSlot;

    public AnimationVariableGenericSlot(String string) {
        super(string);
    }

    @Override
    public String getValueString() {
        return this.m_valueSlot != null ? this.m_valueSlot.getValueString() : null;
    }

    @Override
    public float getValueFloat() {
        return this.m_valueSlot != null ? this.m_valueSlot.getValueFloat() : 0.0F;
    }

    @Override
    public boolean getValueBool() {
        return this.m_valueSlot != null && this.m_valueSlot.getValueBool();
    }

    @Override
    public void setValue(String string) {
        if (this.m_valueSlot == null || !this.m_valueSlot.canConvertFrom(string)) {
            this.m_valueSlot = new AnimationVariableSlotString(this.getKey());
            this.setType(this.m_valueSlot.getType());
        }

        this.m_valueSlot.setValue(string);
    }

    @Override
    public void setValue(float float0) {
        if (this.m_valueSlot == null || this.m_type != AnimationVariableType.Float) {
            this.m_valueSlot = new AnimationVariableSlotFloat(this.getKey());
            this.setType(this.m_valueSlot.getType());
        }

        this.m_valueSlot.setValue(float0);
    }

    @Override
    public void setValue(boolean boolean0) {
        if (this.m_valueSlot == null || this.m_type != AnimationVariableType.Boolean) {
            this.m_valueSlot = new AnimationVariableSlotBool(this.getKey());
            this.setType(this.m_valueSlot.getType());
        }

        this.m_valueSlot.setValue(boolean0);
    }

    @Override
    public AnimationVariableType getType() {
        return this.m_type;
    }

    private void setType(AnimationVariableType animationVariableType) {
        if (this.m_type != animationVariableType) {
            if (this.m_type != AnimationVariableType.Void) {
                DebugLog.General.printf("Variable %s converting from %s to %s\n", this.getKey(), this.m_type, animationVariableType);
            }

            this.m_type = animationVariableType;
        }
    }

    @Override
    public boolean canConvertFrom(String var1) {
        return true;
    }

    @Override
    public void clear() {
        this.m_type = AnimationVariableType.Void;
        this.m_valueSlot = null;
    }
}
