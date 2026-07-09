// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.sandbox;

import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class CustomEnumSandboxOption extends CustomSandboxOption {
    public final int numValues;
    public final int defaultValue;
    public String m_valueTranslation;

    CustomEnumSandboxOption(String string, int int0, int int1) {
        super(string);
        this.numValues = int0;
        this.defaultValue = int1;
    }

    static CustomEnumSandboxOption parse(ScriptParser.Block block) {
        int int0 = getValueInt(block, "numValues", -1);
        int int1 = getValueInt(block, "default", -1);
        if (int0 > 0 && int1 > 0) {
            CustomEnumSandboxOption customEnumSandboxOption = new CustomEnumSandboxOption(block.id, int0, int1);
            if (!customEnumSandboxOption.parseCommon(block)) {
                return null;
            } else {
                ScriptParser.Value value = block.getValue("valueTranslation");
                if (value != null) {
                    customEnumSandboxOption.m_valueTranslation = StringUtils.discardNullOrWhitespace(value.getValue().trim());
                }

                return customEnumSandboxOption;
            }
        } else {
            return null;
        }
    }
}
