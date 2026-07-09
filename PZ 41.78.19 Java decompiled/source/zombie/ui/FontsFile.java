// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class FontsFile {
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;

    public boolean read(String string0, HashMap<String, FontsFileFont> hashMap) {
        try {
            boolean boolean0;
            try (
                FileReader fileReader = new FileReader(string0);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
            ) {
                StringBuilder stringBuilder = new StringBuilder();

                for (String string1 = bufferedReader.readLine(); string1 != null; string1 = bufferedReader.readLine()) {
                    stringBuilder.append(string1);
                }

                this.fromString(stringBuilder.toString(), hashMap);
                boolean0 = true;
            }

            return boolean0;
        } catch (FileNotFoundException fileNotFoundException) {
            return false;
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
            return false;
        }
    }

    private void fromString(String string, HashMap<String, FontsFileFont> hashMap) {
        string = ScriptParser.stripComments(string);
        ScriptParser.Block block0 = ScriptParser.parse(string);
        int int0 = -1;
        ScriptParser.Value value0 = block0.getValue("VERSION");
        if (value0 != null) {
            int0 = PZMath.tryParseInt(value0.getValue(), -1);
        }

        if (int0 >= 1 && int0 <= 1) {
            for (ScriptParser.Block block1 : block0.children) {
                if (!block1.type.equalsIgnoreCase("font")) {
                    throw new RuntimeException("unknown block type \"" + block1.type + "\"");
                }

                if (StringUtils.isNullOrWhitespace(block1.id)) {
                    DebugLog.General.warn("missing or empty font id");
                } else {
                    ScriptParser.Value value1 = block1.getValue("fnt");
                    ScriptParser.Value value2 = block1.getValue("img");
                    if (value1 != null && !StringUtils.isNullOrWhitespace(value1.getValue())) {
                        FontsFileFont fontsFileFont = new FontsFileFont();
                        fontsFileFont.id = block1.id;
                        fontsFileFont.fnt = value1.getValue().trim();
                        if (value2 != null && !StringUtils.isNullOrWhitespace(value2.getValue())) {
                            fontsFileFont.img = value2.getValue().trim();
                        }

                        hashMap.put(fontsFileFont.id, fontsFileFont);
                    } else {
                        DebugLog.General.warn("missing or empty value \"fnt\"");
                    }
                }
            }
        } else {
            throw new RuntimeException("invalid or missing VERSION");
        }
    }
}
