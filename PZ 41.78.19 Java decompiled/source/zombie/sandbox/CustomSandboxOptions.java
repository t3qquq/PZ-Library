// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.sandbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class CustomSandboxOptions {
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    public static final CustomSandboxOptions instance = new CustomSandboxOptions();
    private final ArrayList<CustomSandboxOption> m_options = new ArrayList<>();

    public void init() {
        ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            String string = (String)arrayList.get(int0);
            ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
            if (mod != null) {
                File file = new File(mod.getDir() + File.separator + "media" + File.separator + "sandbox-options.txt");
                if (file.exists() && !file.isDirectory()) {
                    this.readFile(file.getAbsolutePath());
                }
            }
        }
    }

    public static void Reset() {
        instance.m_options.clear();
    }

    public void initInstance(SandboxOptions sandboxOptions) {
        for (int int0 = 0; int0 < this.m_options.size(); int0++) {
            CustomSandboxOption customSandboxOption = this.m_options.get(int0);
            sandboxOptions.newCustomOption(customSandboxOption);
        }
    }

    private boolean readFile(String string0) {
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

                this.parse(stringBuilder.toString());
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

    private void parse(String string) {
        string = ScriptParser.stripComments(string);
        ScriptParser.Block block0 = ScriptParser.parse(string);
        int int0 = -1;
        ScriptParser.Value value = block0.getValue("VERSION");
        if (value != null) {
            int0 = PZMath.tryParseInt(value.getValue(), -1);
        }

        if (int0 >= 1 && int0 <= 1) {
            for (ScriptParser.Block block1 : block0.children) {
                if (!block1.type.equalsIgnoreCase("option")) {
                    throw new RuntimeException("unknown block type \"" + block1.type + "\"");
                }

                CustomSandboxOption customSandboxOption = this.parseOption(block1);
                if (customSandboxOption == null) {
                    DebugLog.General.warn("failed to parse custom sandbox option \"%s\"", block1.id);
                } else {
                    this.m_options.add(customSandboxOption);
                }
            }
        } else {
            throw new RuntimeException("invalid or missing VERSION");
        }
    }

    private CustomSandboxOption parseOption(ScriptParser.Block block) {
        if (StringUtils.isNullOrWhitespace(block.id)) {
            DebugLog.General.warn("missing or empty option id");
            return null;
        } else {
            ScriptParser.Value value = block.getValue("type");
            if (value != null && !StringUtils.isNullOrWhitespace(value.getValue())) {
                String string = value.getValue().trim();
                switch (string) {
                    case "boolean":
                        return CustomBooleanSandboxOption.parse(block);
                    case "double":
                        return CustomDoubleSandboxOption.parse(block);
                    case "enum":
                        return CustomEnumSandboxOption.parse(block);
                    case "integer":
                        return CustomIntegerSandboxOption.parse(block);
                    case "string":
                        return CustomStringSandboxOption.parse(block);
                    default:
                        DebugLog.General.warn("unknown option type \"%s\"", value.getValue().trim());
                        return null;
                }
            } else {
                DebugLog.General.warn("missing or empty value \"type\"");
                return null;
            }
        }
    }
}
