// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.modding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class ActiveModsFile {
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;

    public boolean write(String string0, ActiveMods activeMods) {
        if (Core.getInstance().isNoSave()) {
            return false;
        } else {
            File file = new File(string0);

            try {
                try (
                    FileWriter fileWriter = new FileWriter(file);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                ) {
                    String string1 = this.toString(activeMods);
                    bufferedWriter.write(string1);
                }

                return true;
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return false;
            }
        }
    }

    private String toString(ActiveMods activeMods) {
        ScriptParser.Block block0 = new ScriptParser.Block();
        block0.setValue("VERSION", String.valueOf(1));
        ScriptParser.Block block1 = block0.addBlock("mods", null);
        ArrayList arrayList0 = activeMods.getMods();

        for (int int0 = 0; int0 < arrayList0.size(); int0++) {
            block1.addValue("mod", (String)arrayList0.get(int0));
        }

        ScriptParser.Block block2 = block0.addBlock("maps", null);
        ArrayList arrayList1 = activeMods.getMapOrder();

        for (int int1 = 0; int1 < arrayList1.size(); int1++) {
            block2.addValue("map", (String)arrayList1.get(int1));
        }

        StringBuilder stringBuilder = new StringBuilder();
        String string = System.lineSeparator();
        block0.prettyPrintElements(0, stringBuilder, string);
        return stringBuilder.toString();
    }

    public boolean read(String string0, ActiveMods activeMods) {
        activeMods.clear();

        try {
            try (
                FileReader fileReader = new FileReader(string0);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
            ) {
                StringBuilder stringBuilder = new StringBuilder();

                for (String string1 = bufferedReader.readLine(); string1 != null; string1 = bufferedReader.readLine()) {
                    stringBuilder.append(string1);
                }

                this.fromString(stringBuilder.toString(), activeMods);
            }

            return true;
        } catch (FileNotFoundException fileNotFoundException) {
            return false;
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
            return false;
        }
    }

    private void fromString(String string0, ActiveMods activeMods) {
        string0 = ScriptParser.stripComments(string0);
        ScriptParser.Block block0 = ScriptParser.parse(string0);
        int int0 = -1;
        ScriptParser.Value value0 = block0.getValue("VERSION");
        if (value0 != null) {
            int0 = PZMath.tryParseInt(value0.getValue(), -1);
        }

        if (int0 >= 1 && int0 <= 1) {
            ScriptParser.Block block1 = block0.getBlock("mods", null);
            if (block1 != null) {
                for (ScriptParser.Value value1 : block1.values) {
                    String string1 = value1.getKey().trim();
                    if (string1.equalsIgnoreCase("mod")) {
                        String string2 = value1.getValue().trim();
                        if (!StringUtils.isNullOrWhitespace(string2)) {
                            activeMods.getMods().add(string2);
                        }
                    }
                }
            }

            ScriptParser.Block block2 = block0.getBlock("maps", null);
            if (block2 != null) {
                for (ScriptParser.Value value2 : block2.values) {
                    String string3 = value2.getKey().trim();
                    if (string3.equalsIgnoreCase("map")) {
                        String string4 = value2.getValue().trim();
                        if (!StringUtils.isNullOrWhitespace(string4)) {
                            activeMods.getMapOrder().add(string4);
                        }
                    }
                }
            }
        }
    }
}
