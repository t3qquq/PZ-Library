// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
