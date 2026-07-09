// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

public interface IAnimationVariableSource {
    /**
     * Returns the specified variable slot. Or NULL if not found.
     */
    IAnimationVariableSlot getVariable(AnimationVariableHandle handle);

    /**
     * Returns the specified variable slot. Or NULL if not found.
     */
    IAnimationVariableSlot getVariable(String key);

    /**
     * Returns the specified variable. Or an empty string "" if not found.
     */
    String getVariableString(String name);

    /**
     * Returns the specified variable, as a float.   Attempts to convert the string variable to a float.   If that fails, or if variable not found, returns the defaultValue
     */
    float getVariableFloat(String name, float defaultVal);

    /**
     * Returns the specified variable, as a boolean.   Attempts to convert the string variable to a boolean.   If that fails, or if variable not found, returns FALSE
     */
    boolean getVariableBoolean(String name);

    /**
     * Returns the specified variable, as a boolean.  Attempts to convert the string variable to a boolean.  If that fails, or if variable not found, returns defaultVal
     */
    boolean getVariableBoolean(String key, boolean defaultVal);

    /**
     * Returns all Game variables.
     */
    Iterable<IAnimationVariableSlot> getGameVariables();

    /**
     * Compares (ignoring case) the value of the specified variable.  Returns TRUE if they match.
     */
    boolean isVariable(String name, String val);

    boolean containsVariable(String name);
}
