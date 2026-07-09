// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.config;

import zombie.debug.DebugLog;

public class DoubleConfigOption extends ConfigOption {
    protected double value;
    protected double defaultValue;
    protected double min;
    protected double max;

    public DoubleConfigOption(String name, double _min, double _max, double _defaultValue) {
        super(name);
        if (!(_defaultValue < _min) && !(_defaultValue > _max)) {
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
        return "double";
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
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
        try {
            double double0 = Double.parseDouble(s);
            this.setValue(double0);
        } catch (NumberFormatException numberFormatException) {
            DebugLog.log("ERROR DoubleConfigOption.parse() \"" + this.name + "\" string=" + s + "\"");
        }
    }

    @Override
    public String getValueAsString() {
        return String.valueOf(this.value);
    }

    @Override
    public void setValueFromObject(Object o) {
        if (o instanceof Double) {
            this.setValue((Double)o);
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
        try {
            double double0 = Double.parseDouble(s);
            return double0 >= this.min && double0 <= this.max;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public void setValue(double _value) {
        if (_value < this.min) {
            DebugLog.log("ERROR: DoubleConfigOption.setValue() \"" + this.name + "\" " + _value + " is less than min=" + this.min);
        } else if (_value > this.max) {
            DebugLog.log("ERROR: DoubleConfigOption.setValue() \"" + this.name + "\" " + _value + " is greater than max=" + this.max);
        } else {
            this.value = _value;
        }
    }

    public double getValue() {
        return this.value;
    }

    public double getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getTooltip() {
        return String.valueOf(this.value);
    }
}
