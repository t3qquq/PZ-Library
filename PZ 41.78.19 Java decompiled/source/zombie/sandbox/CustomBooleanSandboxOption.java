// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.sandbox;

import zombie.scripting.ScriptParser;

public final class CustomBooleanSandboxOption extends CustomSandboxOption {
    public final boolean defaultValue;

    CustomBooleanSandboxOption(String string, boolean boolean0) {
        super(string);
        this.defaultValue = boolean0;
    }

    static CustomBooleanSandboxOption parse(ScriptParser.Block block) {
        ScriptParser.Value value = block.getValue("default");
        if (value == null) {
            return null;
        } else {
            boolean boolean0 = Boolean.parseBoolean(value.getValue().trim());
            CustomBooleanSandboxOption customBooleanSandboxOption = new CustomBooleanSandboxOption(block.id, boolean0);
            return !customBooleanSandboxOption.parseCommon(block) ? null : customBooleanSandboxOption;
        }
    }
}
