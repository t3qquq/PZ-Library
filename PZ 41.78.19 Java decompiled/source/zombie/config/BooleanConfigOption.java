// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.config;

import zombie.debug.DebugLog;

public class BooleanConfigOption extends ConfigOption {
    protected boolean value;
    protected boolean defaultValue;

    public BooleanConfigOption(String name, boolean _defaultValue) {
        super(name);
        this.value = _defaultValue;
        this.defaultValue = _defaultValue;
    }

    @Override
    public String getType() {
        return "boolean";
    }

    @Override
    public void resetToDefault() {
        this.setValue(this.defaultValue);
    }

    @Override
    public void setDefaultToCurrentValue() {
        this.defaultValue = this.value;
    }

    @Override
    public void parse(String s) {
        if (this.isValidString(s)) {
            this.setValue(s.equalsIgnoreCase("true") || s.equalsIgnoreCase("1"));
        } else {
            DebugLog.log("ERROR BooleanConfigOption.parse() \"" + this.name + "\" string=" + s + "\"");
        }
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(this.value);
    }

    @Override
    public void setValueFromObject(Object o) {
        if (o instanceof Boolean) {
            this.setValue((Boolean)o);
        } else if (o instanceof Double) {
            this.setValue((Double)o != 0.0);
        } else if (o instanceof String) {
            this.parse((String)o);
        }
    }

    @Override
    public Object getValueAsObject() {
        return this.value;
    }

    @Override
    public boolean isValidString(String s) {
        return s != null && (s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("0"));
    }

    public boolean getValue() {
        return this.value;
    }

    public void setValue(boolean _value) {
        this.value = _value;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getTooltip() {
        return String.valueOf(this.value);
    }
}
