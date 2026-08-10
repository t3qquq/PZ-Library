// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

public interface IAnimationVariableMap extends IAnimationVariableSource {
    /**
     * Returns the specified variable slot.   Creates a new slot if not found.
     */
    IAnimationVariableSlot getOrCreateVariable(String key);

    /**
     * Set the specified animation variable slot. Overwriting an existing slot if necessary.
     */
    void setVariable(IAnimationVariableSlot slot);

    void setVariable(String key, String value);

    void setVariable(String key, boolean value);

    void setVariable(String key, float value);

    void clearVariable(String key);

    void clearVariables();
}
