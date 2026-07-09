// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import java.util.Map;
import java.util.TreeMap;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;

public class AnimationVariableSource implements IAnimationVariableMap {
    private final Map<String, IAnimationVariableSlot> m_GameVariables = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private IAnimationVariableSlot[] m_cachedGameVariableSlots = new IAnimationVariableSlot[0];

    /**
     * Returns the specified variable slot. Or NULL if not found.
     */
    @Override
    public IAnimationVariableSlot getVariable(AnimationVariableHandle handle) {
        if (handle == null) {
            return null;
        } else {
            int int0 = handle.getVariableIndex();
            if (int0 < 0) {
                return null;
            } else if (this.m_cachedGameVariableSlots != null && int0 < this.m_cachedGameVariableSlots.length) {
                IAnimationVariableSlot iAnimationVariableSlot0 = this.m_cachedGameVariableSlots[int0];
                if (iAnimationVariableSlot0 == null) {
                    this.m_cachedGameVariableSlots[int0] = this.m_GameVariables.get(handle.getVariableName());
                    iAnimationVariableSlot0 = this.m_cachedGameVariableSlots[int0];
                }

                return iAnimationVariableSlot0;
            } else {
                IAnimationVariableSlot iAnimationVariableSlot1 = this.m_GameVariables.get(handle.getVariableName());
                if (iAnimationVariableSlot1 == null) {
                    return null;
                } else {
                    IAnimationVariableSlot[] iAnimationVariableSlots0 = new IAnimationVariableSlot[int0 + 1];
                    IAnimationVariableSlot[] iAnimationVariableSlots1 = this.m_cachedGameVariableSlots;
                    if (iAnimationVariableSlots1 != null) {
                        this.m_cachedGameVariableSlots = PZArrayUtil.arrayCopy(
                            iAnimationVariableSlots1, iAnimationVariableSlots0, 0, iAnimationVariableSlots1.length
                        );
                    }

                    iAnimationVariableSlots0[int0] = iAnimationVariableSlot1;
                    this.m_cachedGameVariableSlots = iAnimationVariableSlots0;
                    return iAnimationVariableSlot1;
                }
            }
        }
    }

    /**
     * Returns the specified variable slot. Or NULL if not found.
     */
    @Override
    public IAnimationVariableSlot getVariable(String key) {
        if (StringUtils.isNullOrWhitespace(key)) {
            return null;
        } else {
            String string = key.trim();
            return this.m_GameVariables.get(string);
        }
    }

    /**
     * Returns the specified variable slot.   Creates a new slot if not found.    Returns NULL if key is null, whitespace, or empty.
     */
    @Override
    public IAnimationVariableSlot getOrCreateVariable(String key) {
        if (StringUtils.isNullOrWhitespace(key)) {
            return null;
        } else {
            String string = key.trim();
            Object object = this.m_GameVariables.get(string);
            if (object == null) {
                object = new AnimationVariableGenericSlot(string.toLowerCase());
                this.setVariable((IAnimationVariableSlot)object);
            }

            return (IAnimationVariableSlot)object;
        }
    }

    /**
     * Description copied from interface: IAnimationVariableMap
     */
    @Override
    public void setVariable(IAnimationVariableSlot var) {
        this.m_GameVariables.put(var.getKey(), var);
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackBool(key, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGet, AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackBool(key, callbackGet, callbackSet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackString(key, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackString(key, callbackGet, callbackSet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(key, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(key, callbackGet, callbackSet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackInt(key, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGet, AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackInt(key, callbackGet, callbackSet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, boolean defaultVal, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackBool(key, defaultVal, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        boolean defaultVal,
        AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackBool(key, defaultVal, callbackGet, callbackSet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, String defaultVal, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackString(key, defaultVal, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        String defaultVal,
        AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackString(key, defaultVal, callbackGet, callbackSet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, float defaultVal, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(key, defaultVal, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        float defaultVal,
        AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackFloat(key, defaultVal, callbackGet, callbackSet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(String key, int defaultVal, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGet) {
        this.setVariable(new AnimationVariableSlotCallbackInt(key, defaultVal, callbackGet));
    }

    /**
     * Strong-typed utility function.
     */
    public void setVariable(
        String key,
        int defaultVal,
        AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGet,
        AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSet
    ) {
        this.setVariable(new AnimationVariableSlotCallbackInt(key, defaultVal, callbackGet, callbackSet));
    }

    @Override
    public void setVariable(String key, String value) {
        this.getOrCreateVariable(key).setValue(value);
    }

    @Override
    public void setVariable(String key, boolean value) {
        this.getOrCreateVariable(key).setValue(value);
    }

    @Override
    public void setVariable(String key, float value) {
        this.getOrCreateVariable(key).setValue(value);
    }

    @Override
    public void clearVariable(String key) {
        IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(key);
        if (iAnimationVariableSlot != null) {
            iAnimationVariableSlot.clear();
        }
    }

    @Override
    public void clearVariables() {
        for (IAnimationVariableSlot iAnimationVariableSlot : this.getGameVariables()) {
            iAnimationVariableSlot.clear();
        }
    }

    /**
     * Returns the specified variable. Or an empty string "" if not found.
     */
    @Override
    public String getVariableString(String key) {
        IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(key);
        return iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueString() : "";
    }

    /**
     * Returns the specified variable, as a float.  Attempts to convert the string variable to a float.  If that fails, or if variable not found, returns the defaultValue
     */
    @Override
    public float getVariableFloat(String key, float defaultVal) {
        IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(key);
        return iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueFloat() : defaultVal;
    }

    /**
     * Returns the specified variable, as a boolean.  Attempts to convert the string variable to a boolean.  If that fails, or if variable not found, returns FALSE
     */
    @Override
    public boolean getVariableBoolean(String key) {
        IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(key);
        return iAnimationVariableSlot != null && iAnimationVariableSlot.getValueBool();
    }

    /**
     * Returns the specified variable, as a boolean.  Attempts to convert the string variable to a boolean.  If that fails, or if variable not found, returns defaultVal
     */
    @Override
    public boolean getVariableBoolean(String key, boolean defaultVal) {
        IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(key);
        return iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueBool() : defaultVal;
    }

    /**
     * Returns all Game variables.
     */
    @Override
    public Iterable<IAnimationVariableSlot> getGameVariables() {
        return this.m_GameVariables.values();
    }

    /**
     * Compares (ignoring case) the value of the specified variable.  Returns TRUE if they match.
     */
    @Override
    public boolean isVariable(String name, String val) {
        return StringUtils.equalsIgnoreCase(this.getVariableString(name), val);
    }

    @Override
    public boolean containsVariable(String key) {
        if (StringUtils.isNullOrWhitespace(key)) {
            return false;
        } else {
            String string = key.trim();
            return this.m_GameVariables.containsKey(string);
        }
    }
}
