// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.config;

import zombie.debug.DebugLog;

public class IntegerConfigOption extends ConfigOption {
    protected int value;
    protected int defaultValue;
    protected int min;
    protected int max;

    public IntegerConfigOption(String name, int _min, int _max, int _defaultValue) {
        super(name);
        if (_defaultValue >= _min && _defaultValue <= _max) {
            this.value = _defaultValue;
            this.defaultValue = _defaultValue;
            this.min = _min;
            this.max = _max;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public String getType() {
        return "integer";
    }

    @Override
    public void resetToDefault() {
        this.setValue(this.defaultValue);
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    @Override
    public void setDefaultToCurrentValue() {
        this.defaultValue = this.value;
    }

    @Override
    public void parse(String s) {
        try {
            double double0 = Double.parseDouble(s);
            this.setValue((int)double0);
        } catch (NumberFormatException numberFormatException) {
            DebugLog.log("ERROR IntegerConfigOption.parse() \"" + this.name + "\" string=\"" + s + "\"");
        }
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(this.value);
    }

    @Override
    public void setValueFromObject(Object o) {
        if (o instanceof Double) {
            this.setValue(((Double)o).intValue());
        } else if (o instanceof String) {
            this.parse((String)o);
        }
    }

    @Override
    public Object getValueAsObject() {
        return (double)this.value;
    }

    @Override
    public boolean isValidString(String s) {
        try {
            int int0 = Integer.parseInt(s);
            return int0 >= this.min && int0 <= this.max;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public void setValue(int _value) {
        if (_value < this.min) {
            DebugLog.log("ERROR: IntegerConfigOption.setValue() \"" + this.name + "\" " + _value + " is less than min=" + this.min);
        } else if (_value > this.max) {
            DebugLog.log("ERROR: IntegerConfigOption.setValue() \"" + this.name + "\" " + _value + " is greater than max=" + this.max);
        } else {
            this.value = _value;
        }
    }

    public int getValue() {
        return this.value;
    }

    public int getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getTooltip() {
        return String.valueOf(this.value);
    }
}
