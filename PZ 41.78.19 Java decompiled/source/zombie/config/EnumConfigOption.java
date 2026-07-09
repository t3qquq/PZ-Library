// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.config;

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
