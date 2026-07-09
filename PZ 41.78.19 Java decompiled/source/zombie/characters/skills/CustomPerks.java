// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters.skills;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.gameStates.ChooseGameInfo;
import zombie.scripting.ScriptParser;
import zombie.util.StringUtils;

public final class CustomPerks {
    private static final int VERSION1 = 1;
    private static final int VERSION = 1;
    public static final CustomPerks instance = new CustomPerks();
    private final ArrayList<CustomPerk> m_perks = new ArrayList<>();

    public void init() {
        ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            String string = (String)arrayList.get(int0);
            ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
            if (mod != null) {
                File file = new File(mod.getDir() + File.separator + "media" + File.separator + "perks.txt");
                if (file.exists() && !file.isDirectory()) {
                    this.readFile(file.getAbsolutePath());
                }
            }
        }

        for (CustomPerk customPerk0 : this.m_perks) {
            PerkFactory.Perk perk0 = PerkFactory.Perks.FromString(customPerk0.m_id);
            if (perk0 == null || perk0 == PerkFactory.Perks.None || perk0 == PerkFactory.Perks.MAX) {
                perk0 = new PerkFactory.Perk(customPerk0.m_id);
                perk0.setCustom();
            }
        }

        for (CustomPerk customPerk1 : this.m_perks) {
            PerkFactory.Perk perk1 = PerkFactory.Perks.FromString(customPerk1.m_id);
            PerkFactory.Perk perk2 = PerkFactory.Perks.FromString(customPerk1.m_parent);
            if (perk2 == null || perk2 == PerkFactory.Perks.None || perk2 == PerkFactory.Perks.MAX) {
                perk2 = PerkFactory.Perks.None;
            }

            int[] ints = customPerk1.m_xp;
            PerkFactory.AddPerk(
                perk1,
                customPerk1.m_translation,
                perk2,
                ints[0],
                ints[1],
                ints[2],
                ints[3],
                ints[4],
                ints[5],
                ints[6],
                ints[7],
                ints[8],
                ints[9],
                customPerk1.m_bPassive
            );
        }
    }

    public void initLua() {
        KahluaTable table = (KahluaTable)LuaManager.env.rawget("Perks");

        for (CustomPerk customPerk : this.m_perks) {
            PerkFactory.Perk perk = PerkFactory.Perks.FromString(customPerk.m_id);
            table.rawset(perk.getId(), perk);
        }
    }

    public static void Reset() {
        instance.m_perks.clear();
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
                if (!block1.type.equalsIgnoreCase("perk")) {
                    throw new RuntimeException("unknown block type \"" + block1.type + "\"");
                }

                CustomPerk customPerk = this.parsePerk(block1);
                if (customPerk == null) {
                    DebugLog.General.warn("failed to parse custom perk \"%s\"", block1.id);
                } else {
                    this.m_perks.add(customPerk);
                }
            }
        } else {
            throw new RuntimeException("invalid or missing VERSION");
        }
    }

    private CustomPerk parsePerk(ScriptParser.Block block) {
        if (StringUtils.isNullOrWhitespace(block.id)) {
            DebugLog.General.warn("missing or empty perk id");
            return null;
        } else {
            CustomPerk customPerk = new CustomPerk(block.id);
            ScriptParser.Value value0 = block.getValue("parent");
            if (value0 != null && !StringUtils.isNullOrWhitespace(value0.getValue())) {
                customPerk.m_parent = value0.getValue().trim();
            }

            ScriptParser.Value value1 = block.getValue("translation");
            if (value1 != null) {
                customPerk.m_translation = StringUtils.discardNullOrWhitespace(value1.getValue().trim());
            }

            if (StringUtils.isNullOrWhitespace(customPerk.m_translation)) {
                customPerk.m_translation = customPerk.m_id;
            }

            ScriptParser.Value value2 = block.getValue("passive");
            if (value2 != null) {
                customPerk.m_bPassive = StringUtils.tryParseBoolean(value2.getValue().trim());
            }

            for (int int0 = 1; int0 <= 10; int0++) {
                ScriptParser.Value value3 = block.getValue("xp" + int0);
                if (value3 != null) {
                    int int1 = PZMath.tryParseInt(value3.getValue().trim(), -1);
                    if (int1 > 0) {
                        customPerk.m_xp[int0 - 1] = int1;
                    }
                }
            }

            return customPerk;
        }
    }
}
