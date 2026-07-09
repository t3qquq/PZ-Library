// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

/**
 * An Animation Variable Slot   Used to store a variable's key+value pair, and its current type.
 */
public interface IAnimationVariableSlot {
    /**
     * This variable's unique key
     */
    String getKey();

    /**
     * This variable's value, in String form.
     */
    String getValueString();

    /**
     * This variable's value, as a Float.
     */
    float getValueFloat();

    /**
     * This variable's value, as a Boolean.
     */
    boolean getValueBool();

    /**
     * Set this variable's value
     */
    void setValue(String val);

    /**
     * Set this variable's value
     */
    void setValue(float val);

    /**
     * Set this variable's value
     */
    void setValue(boolean val);

    /**
     * This variable's value type
     */
    AnimationVariableType getType();

    /**
     * Returns TRUE if this variable slot can accept and/or convert the supplied value object.  Returns FALSE if the conversion would result in a loss of data.    Eg. If a String is given to a Float variable, and the string is not of a numeric format, then the string value       would be lost.
     */
    boolean canConvertFrom(String val);

    /**
     * Clear this variable, its value is set to a null-value. Blank for Strings, 0 for Floats, False for Booleans, etc.
     */
    void clear();

    /**
     * Returns TRUE if this variable is not writable. Typically, the value of this variable is specified by an outside  condition, such as whether the character is currently falling, etc.
     */
    default boolean isReadOnly() {
        return false;
    }
}
