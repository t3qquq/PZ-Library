// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.config;

public class StringConfigOption extends ConfigOption {
    protected String value;
    protected String defaultValue;
    protected int maxLength;

    public StringConfigOption(String name, String _defaultValue, int _maxLength) {
        super(name);
        if (_defaultValue == null) {
            _defaultValue = "";
        }

        this.value = _defaultValue;
        this.defaultValue = _defaultValue;
        this.maxLength = _maxLength;
    }

    @Override
    public String getType() {
        return "string";
    }

    @Override
    public void resetToDefault() {
        this.value = this.defaultValue;
    }

    @Override
    public void setDefaultToCurrentValue() {
        this.defaultValue = this.value;
    }

    @Override
    public void parse(String s) {
        this.setValueFromObject(s);
    }

    @Override
    public String getValueAsString() {
        return this.value;
    }

    @Override
    public String getValueAsLuaString() {
        return String.format("\"%s\"", this.value.replace("\\", "\\\\").replace("\"", "\\\""));
    }

    @Override
    public void setValueFromObject(Object o) {
        if (o == null) {
            this.value = "";
        } else if (o instanceof String) {
            this.value = (String)o;
        } else {
            this.value = o.toString();
        }
    }

    @Override
    public Object getValueAsObject() {
        return this.value;
    }

    @Override
    public boolean isValidString(String s) {
        return true;
    }

    public void setValue(String _value) {
        if (_value == null) {
            _value = "";
        }

        if (this.maxLength > 0 && _value.length() > this.maxLength) {
            _value = _value.substring(0, this.maxLength);
        }

        this.value = _value;
    }

    public String getValue() {
        return this.value;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getTooltip() {
        return this.value;
    }
}
