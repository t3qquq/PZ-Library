// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.sandbox;

import zombie.scripting.ScriptParser;

public final class CustomIntegerSandboxOption extends CustomSandboxOption {
    public final int min;
    public final int max;
    public final int defaultValue;

    CustomIntegerSandboxOption(String string, int int0, int int1, int int2) {
        super(string);
        this.min = int0;
        this.max = int1;
        this.defaultValue = int2;
    }

    static CustomIntegerSandboxOption parse(ScriptParser.Block block) {
        int int0 = getValueInt(block, "min", Integer.MIN_VALUE);
        int int1 = getValueInt(block, "max", Integer.MIN_VALUE);
        int int2 = getValueInt(block, "default", Integer.MIN_VALUE);
        if (int0 != Integer.MIN_VALUE && int1 != Integer.MIN_VALUE && int2 != Integer.MIN_VALUE) {
            CustomIntegerSandboxOption customIntegerSandboxOption = new CustomIntegerSandboxOption(block.id, int0, int1, int2);
            return !customIntegerSandboxOption.parseCommon(block) ? null : customIntegerSandboxOption;
        } else {
            return null;
        }
    }
}
