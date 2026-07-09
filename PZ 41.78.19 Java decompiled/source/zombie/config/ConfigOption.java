// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.config;

public abstract class ConfigOption {
    protected final String name;

    public ConfigOption(String _name) {
        if (_name != null && !_name.isEmpty() && !_name.contains("=")) {
            this.name = _name;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public String getName() {
        return this.name;
    }

    public abstract String getType();

    public abstract void resetToDefault();

    public abstract void setDefaultToCurrentValue();

    public abstract void parse(String s);

    public abstract String getValueAsString();

    public String getValueAsLuaString() {
        return this.getValueAsString();
    }

    public abstract void setValueFromObject(Object o);

    public abstract Object getValueAsObject();

    public abstract boolean isValidString(String s);

    public abstract String getTooltip();
}
