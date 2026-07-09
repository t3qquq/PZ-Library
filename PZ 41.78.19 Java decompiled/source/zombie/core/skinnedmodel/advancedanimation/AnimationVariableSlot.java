// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

public abstract class AnimationVariableSlot implements IAnimationVariableSlot {
    private final String m_key;

    protected AnimationVariableSlot(String string) {
        this.m_key = string.toLowerCase().trim();
    }

    @Override
    public String getKey() {
        return this.m_key;
    }
}
