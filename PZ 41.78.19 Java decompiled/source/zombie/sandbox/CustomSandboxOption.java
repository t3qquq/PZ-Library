// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.sandbox;

import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public class CustomSandboxOption {
    public final String m_id;
    public String m_page;
    public String m_translation;

    CustomSandboxOption(String string) {
        this.m_id = string;
    }

    static double getValueDouble(ScriptParser.Block block, String string, double double0) {
        ScriptParser.Value value = block.getValue(string);
        return value == null ? double0 : PZMath.tryParseDouble(value.getValue().trim(), double0);
    }

    static float getValueFloat(ScriptParser.Block block, String string, float float0) {
        ScriptParser.Value value = block.getValue(string);
        return value == null ? float0 : PZMath.tryParseFloat(value.getValue().trim(), float0);
    }

    static int getValueInt(ScriptParser.Block block, String string, int int0) {
        ScriptParser.Value value = block.getValue(string);
        return value == null ? int0 : PZMath.tryParseInt(value.getValue().trim(), int0);
    }

    boolean parseCommon(ScriptParser.Block block) {
        ScriptParser.Value value0 = block.getValue("page");
        if (value0 != null) {
            this.m_page = StringUtils.discardNullOrWhitespace(value0.getValue().trim());
        }

        ScriptParser.Value value1 = block.getValue("translation");
        if (value1 != null) {
            this.m_translation = StringUtils.discardNullOrWhitespace(value1.getValue().trim());
        }

        return true;
    }
}
