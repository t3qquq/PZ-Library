// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.config;

import zombie.UsedFromLua;

@UsedFromLua
public class EnumConfigOption extends IntegerConfigOption {
    public EnumConfigOption(String name, int numValues, int defaultValue) {
        super(name, 1, numValues, defaultValue);
    }

    @Override
    public String getType() {
        return "enum";
    }

    public int getNumValues() {
        return this.max;
    }
}
