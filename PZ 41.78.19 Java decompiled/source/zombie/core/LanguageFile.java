// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class LanguageFile {
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;

    public boolean read(String string0, LanguageFileData languageFileData) {
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

                this.fromString(stringBuilder.toString(), languageFileData);
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

    private void fromString(String string, LanguageFileData languageFileData) {
        string = ScriptParser.stripComments(string);
        ScriptParser.Block block = ScriptParser.parse(string);
        int int0 = -1;
        ScriptParser.Value value0 = block.getValue("VERSION");
        if (value0 != null) {
            int0 = PZMath.tryParseInt(value0.getValue(), -1);
        }

        if (int0 >= 1 && int0 <= 1) {
            ScriptParser.Value value1 = block.getValue("text");
            if (value1 != null && !StringUtils.isNullOrWhitespace(value1.getValue())) {
                ScriptParser.Value value2 = block.getValue("charset");
                if (value2 != null && !StringUtils.isNullOrWhitespace(value2.getValue())) {
                    languageFileData.text = value1.getValue().trim();
                    languageFileData.charset = value2.getValue().trim();
                    ScriptParser.Value value3 = block.getValue("base");
                    if (value3 != null && !StringUtils.isNullOrWhitespace(value3.getValue())) {
                        languageFileData.base = value3.getValue().trim();
                    }

                    ScriptParser.Value value4 = block.getValue("azerty");
                    if (value4 != null) {
                        languageFileData.azerty = StringUtils.tryParseBoolean(value4.getValue());
                    }
                } else {
                    throw new RuntimeException("missing or empty value \"charset\"");
                }
            } else {
                throw new RuntimeException("missing or empty value \"text\"");
            }
        } else {
            throw new RuntimeException("invalid or missing VERSION");
        }
    }
}
