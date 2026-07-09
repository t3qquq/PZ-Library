// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.sandbox;

import zombie.scripting.ScriptParser;

public final class CustomDoubleSandboxOption extends CustomSandboxOption {
    public final double min;
    public final double max;
    public final double defaultValue;

    CustomDoubleSandboxOption(String string, double double0, double double1, double double2) {
        super(string);
        this.min = double0;
        this.max = double1;
        this.defaultValue = double2;
    }

    static CustomDoubleSandboxOption parse(ScriptParser.Block block) {
        double double0 = getValueDouble(block, "min", Double.NaN);
        double double1 = getValueDouble(block, "max", Double.NaN);
        double double2 = getValueDouble(block, "default", Double.NaN);
        if (!Double.isNaN(double0) && !Double.isNaN(double1) && !Double.isNaN(double2)) {
            CustomDoubleSandboxOption customDoubleSandboxOption = new CustomDoubleSandboxOption(block.id, double0, double1, double2);
            return !customDoubleSandboxOption.parseCommon(block) ? null : customDoubleSandboxOption;
        } else {
            return null;
        }
    }
}
