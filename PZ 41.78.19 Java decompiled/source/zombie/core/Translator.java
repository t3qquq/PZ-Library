// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import se.krka.kahlua.vm.KahluaTable;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.skills.PerkFactory;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;

public final class Translator {
    private static ArrayList<Language> availableLanguage = null;
    public static boolean debug = false;
    private static FileWriter debugFile = null;
    private static boolean debugErrors = false;
    private static final HashSet<String> debugItemEvolvedRecipeName = new HashSet<>();
    private static final HashSet<String> debugItem = new HashSet<>();
    private static final HashSet<String> debugMultiStageBuild = new HashSet<>();
    private static final HashSet<String> debugRecipe = new HashSet<>();
    private static final HashMap<String, String> moodles = new HashMap<>();
    private static final HashMap<String, String> ui = new HashMap<>();
    private static final HashMap<String, String> survivalGuide = new HashMap<>();
    private static final HashMap<String, String> contextMenu = new HashMap<>();
    private static final HashMap<String, String> farming = new HashMap<>();
    private static final HashMap<String, String> recipe = new HashMap<>();
    private static final HashMap<String, String> igui = new HashMap<>();
    private static final HashMap<String, String> sandbox = new HashMap<>();
    private static final HashMap<String, String> tooltip = new HashMap<>();
    private static final HashMap<String, String> challenge = new HashMap<>();
    private static final HashSet<String> missing = new HashSet<>();
    private static ArrayList<String> azertyLanguages = null;
    private static final HashMap<String, String> news = new HashMap<>();
    private static final HashMap<String, String> stash = new HashMap<>();
    private static final HashMap<String, String> multiStageBuild = new HashMap<>();
    private static final HashMap<String, String> moveables = new HashMap<>();
    private static final HashMap<String, String> makeup = new HashMap<>();
    private static final HashMap<String, String> gameSound = new HashMap<>();
    private static final HashMap<String, String> dynamicRadio = new HashMap<>();
    private static final HashMap<String, String> items = new HashMap<>();
    private static final HashMap<String, String> itemName = new HashMap<>();
    private static final HashMap<String, String> itemEvolvedRecipeName = new HashMap<>();
    private static final HashMap<String, String> recordedMedia = new HashMap<>();
    private static final HashMap<String, String> recordedMedia_EN = new HashMap<>();
    public static Language language = null;
    private static final String newsHeader = "<IMAGE:media/ui/dot.png> <SIZE:small> ";

    public static void loadFiles() {
        language = null;
        availableLanguage = null;
        File file = new File(ZomboidFileSystem.instance.getCacheDir() + File.separator + "translationProblems.txt");
        if (debug) {
            try {
                if (debugFile != null) {
                    debugFile.close();
                }

                debugFile = new FileWriter(file);
            } catch (IOException iOException) {
                iOException.printStackTrace();
            }
        }

        moodles.clear();
        ui.clear();
        survivalGuide.clear();
        items.clear();
        itemName.clear();
        contextMenu.clear();
        farming.clear();
        recipe.clear();
        igui.clear();
        sandbox.clear();
        tooltip.clear();
        challenge.clear();
        news.clear();
        missing.clear();
        stash.clear();
        multiStageBuild.clear();
        moveables.clear();
        makeup.clear();
        gameSound.clear();
        dynamicRadio.clear();
        itemEvolvedRecipeName.clear();
        recordedMedia.clear();
        DebugLog.log("translator: language is " + getLanguage());
        debugErrors = false;
        fillMapFromFile("Tooltip", tooltip);
        fillMapFromFile("IG_UI", igui);
        fillMapFromFile("Recipes", recipe);
        fillMapFromFile("Farming", farming);
        fillMapFromFile("ContextMenu", contextMenu);
        fillMapFromFile("SurvivalGuide", survivalGuide);
        fillMapFromFile("UI", ui);
        fillMapFromFile("Items", items);
        fillMapFromFile("ItemName", itemName);
        fillMapFromFile("Moodles", moodles);
        fillMapFromFile("Sandbox", sandbox);
        fillMapFromFile("Challenge", challenge);
        fillMapFromFile("Stash", stash);
        fillMapFromFile("MultiStageBuild", multiStageBuild);
        fillMapFromFile("Moveables", moveables);
        fillMapFromFile("MakeUp", makeup);
        fillMapFromFile("GameSound", gameSound);
        fillMapFromFile("DynamicRadio", dynamicRadio);
        fillMapFromFile("EvolvedRecipeName", itemEvolvedRecipeName);
        fillMapFromFile("Recorded_Media", recordedMedia);
        fillNewsFromFile(news);
        if (debug) {
            if (debugErrors) {
                DebugLog.log("translator: errors detected, please see " + file.getAbsolutePath());
            }

            debugItemEvolvedRecipeName.clear();
            debugItem.clear();
            debugMultiStageBuild.clear();
            debugRecipe.clear();
        }

        PerkFactory.initTranslations();
    }

    private static void fillNewsFromFile(HashMap<String, String> hashMap1) {
        HashMap hashMap0 = new HashMap();
        ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            String string = ZomboidFileSystem.instance.getModDir((String)arrayList.get(int0));
            if (string != null) {
                tryFillNewsFromFile(string, hashMap1, hashMap0, getLanguage());
                if (getLanguage() != getDefaultLanguage()) {
                    tryFillNewsFromFile(string, hashMap1, hashMap0, getDefaultLanguage());
                }
            }
        }

        tryFillNewsFromFile(".", hashMap1, hashMap0, getLanguage());
        if (getLanguage() != getDefaultLanguage()) {
            tryFillNewsFromFile(".", hashMap1, hashMap0, getDefaultLanguage());
        }

        for (Translator.News newsx : hashMap0.values()) {
            hashMap1.put("News_" + newsx.version + "_Disclaimer", newsx.toRichText());
        }

        hashMap0.clear();
    }

    private static void tryFillNewsFromFile(String string, HashMap<String, String> var1, HashMap<String, Translator.News> hashMap, Language languagex) {
        File file = new File(
            string
                + File.separator
                + "media"
                + File.separator
                + "lua"
                + File.separator
                + "shared"
                + File.separator
                + "Translate"
                + File.separator
                + languagex
                + File.separator
                + "News_"
                + languagex
                + ".txt"
        );
        if (file.exists()) {
            doNews(file, hashMap, languagex);
        }
    }

    private static void doNews(File file, HashMap<String, Translator.News> hashMap, Language languagex) {
        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(languagex.charset()));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            Translator.News newsx = null;
            ArrayList arrayList = null;

            String string0;
            while ((string0 = bufferedReader.readLine()) != null) {
                if (!string0.trim().isEmpty()) {
                    if (string0.startsWith("[VERSION]")) {
                        String string1 = string0.replaceFirst("\\[VERSION\\]", "").trim();
                        if (hashMap.containsKey(string1)) {
                            newsx = null;
                            arrayList = null;
                        } else {
                            hashMap.put(string1, newsx = new Translator.News(string1));
                            arrayList = null;
                        }
                    }

                    if (newsx != null) {
                        if (string0.startsWith("[SECTION]")) {
                            String string2 = string0.replaceFirst("\\[SECTION\\]", "").trim();
                            arrayList = newsx.getOrCreateSectionList(string2);
                        } else if (string0.startsWith("[NEWS]")) {
                            arrayList = newsx.getOrCreateSectionList("[New]");
                        } else if (string0.startsWith("[BALANCE]")) {
                            arrayList = newsx.getOrCreateSectionList("[Balance]");
                        } else if (string0.startsWith("[BUG FIX]")) {
                            arrayList = newsx.getOrCreateSectionList("[Bug Fix]");
                        } else if (arrayList != null) {
                            addNewsLine(string0, arrayList);
                        }
                    }
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    private static void addNewsLine(String string, ArrayList<String> arrayList) {
        if (string.startsWith("[BOLD]")) {
            string = string.replaceFirst("\\[BOLD\\]", "<IMAGE:media/ui/dot.png> <SIZE:medium>");
            arrayList.add(string + " <LINE> ");
        } else if (string.startsWith("[DOT2]")) {
            string = string.replaceFirst("\\[DOT2\\]", "<IMAGE:media/ui/dot2.png> <SIZE:small>");
            arrayList.add(string + " <LINE> ");
        } else if (string.startsWith("[NODOT]")) {
            string = string.replaceFirst("\\[NODOT\\]", " <SIZE:small> ");
            string = string + " <LINE> ";
            arrayList.add(string);
        } else {
            byte byte0 = 7;
            arrayList.add("<IMAGE:media/ui/dot.png> <SIZE:small> " + " <INDENT:%d> ".formatted(21 - byte0) + string + " <INDENT:0> <LINE> ");
        }
    }

    public static ArrayList<String> getNewsVersions() {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(news.keySet());

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            String string = (String)arrayList.get(int0);
            string = string.replace("News_", "");
            string = string.replace("_Disclaimer", "");
            arrayList.set(int0, string);
        }

        Collections.sort(arrayList);
        return arrayList;
    }

    private static void tryFillMapFromFile(String string1, String string0, HashMap<String, String> hashMap, Language languagex) {
        File file = new File(
            string1
                + File.separator
                + "media"
                + File.separator
                + "lua"
                + File.separator
                + "shared"
                + File.separator
                + "Translate"
                + File.separator
                + languagex
                + File.separator
                + string0
                + "_"
                + languagex
                + ".txt"
        );
        if (file.exists()) {
            parseFile(file, hashMap, languagex);
        }
    }

    private static void tryFillMapFromMods(String string1, HashMap<String, String> hashMap, Language languagex) {
        ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();

        for (int int0 = arrayList.size() - 1; int0 >= 0; int0--) {
            String string0 = ZomboidFileSystem.instance.getModDir((String)arrayList.get(int0));
            if (string0 != null) {
                tryFillMapFromFile(string0, string1, hashMap, languagex);
            }
        }
    }

    public static void addLanguageToList(Language _language, ArrayList<Language> languages) {
        if (_language != null) {
            if (!languages.contains(_language)) {
                languages.add(_language);
                if (_language.base() != null) {
                    _language = Languages.instance.getByName(_language.base());
                    addLanguageToList(_language, languages);
                }
            }
        }
    }

    private static void fillMapFromFile(String string, HashMap<String, String> hashMap) {
        ArrayList arrayList = new ArrayList();
        addLanguageToList(getLanguage(), arrayList);
        addLanguageToList(getDefaultLanguage(), arrayList);

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            Language languagex = (Language)arrayList.get(int0);
            tryFillMapFromMods(string, hashMap, languagex);
            tryFillMapFromFile(ZomboidFileSystem.instance.base.getPath(), string, hashMap, languagex);
        }

        arrayList.clear();
    }

    private static void parseFile(File file, HashMap<String, String> hashMap, Language languagex) {
        String string0 = null;

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(languagex.charset()));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            bufferedReader.readLine();
            boolean boolean0 = false;
            String string1 = "";
            String string2 = "";
            int int0 = 1;
            String string3 = file.getName().replace("_" + getDefaultLanguage(), "_" + getLanguage());

            while ((string0 = bufferedReader.readLine()) != null) {
                int0++;

                try {
                    if (string0.contains("=") && string0.contains("\"")) {
                        if (string0.trim().startsWith("Recipe_")) {
                            string1 = string0.split("=")[0].replaceAll("Recipe_", "").replaceAll("_", " ").trim();
                            string2 = string0.split("=")[1];
                            string2 = string2.substring(string2.indexOf("\"") + 1, string2.lastIndexOf("\""));
                        } else if (string0.trim().startsWith("DisplayName")) {
                            String[] strings0 = string0.split("=");
                            if (string0.trim().startsWith("DisplayName_")) {
                                string1 = strings0[0].replaceAll("DisplayName_", "").trim();
                            } else {
                                string1 = strings0[0].replaceAll("DisplayName", "").trim();
                            }

                            if ("Anti_depressants".equals(string1)) {
                                string1 = "Antidepressants";
                            }

                            string2 = strings0[1];
                            string2 = string2.substring(string2.indexOf("\"") + 1, string2.lastIndexOf("\""));
                        } else if (string0.trim().startsWith("EvolvedRecipeName_")) {
                            String[] strings1 = string0.split("=");
                            string1 = strings1[0].replaceAll("EvolvedRecipeName_", "").trim();
                            string2 = strings1[1];
                            int int1 = string2.indexOf("\"");
                            int int2 = string2.lastIndexOf("\"");
                            string2 = string2.substring(int1 + 1, int2);
                        } else if (string0.trim().startsWith("ItemName_")) {
                            String[] strings2 = string0.split("=");
                            string1 = strings2[0].replaceAll("ItemName_", "").trim();
                            string2 = strings2[1];
                            int int3 = string2.indexOf("\"");
                            int int4 = string2.lastIndexOf("\"");
                            string2 = string2.substring(int3 + 1, int4);
                        } else {
                            string1 = string0.split("=")[0].trim();
                            string2 = string0.substring(string0.indexOf("=") + 1);
                            string2 = string2.substring(string2.indexOf("\"") + 1, string2.lastIndexOf("\""));
                            if (string0.contains("..")) {
                                boolean0 = true;
                            }
                        }
                    } else if (string0.contains("--") || string0.trim().isEmpty() || !string0.trim().endsWith("..") && !boolean0) {
                        boolean0 = false;
                    } else {
                        boolean0 = true;
                        string2 = string2 + string0.substring(string0.indexOf("\"") + 1, string0.lastIndexOf("\""));
                    }

                    if (!boolean0 || !string0.trim().endsWith("..")) {
                        if (!string1.isEmpty()) {
                            if (!hashMap.containsKey(string1)) {
                                hashMap.put(string1, string2);
                                if (hashMap == recordedMedia && languagex == getDefaultLanguage()) {
                                    recordedMedia_EN.put(string1, string2);
                                }

                                if (debug && languagex == getDefaultLanguage() && getLanguage() != getDefaultLanguage()) {
                                    if (string3 != null) {
                                        debugwrite(string3 + "\r\n");
                                        string3 = null;
                                    }

                                    debugwrite("\t" + string1 + " = \"" + string2 + "\",\r\n");
                                    debugErrors = true;
                                }
                            } else if (debug && languagex == getDefaultLanguage() && getLanguage() != getDefaultLanguage()) {
                                String string4 = (String)hashMap.get(string1);
                                if (countSubstitutions(string4) != countSubstitutions(string2)) {
                                    debugwrite(
                                        "wrong number of % substitutions in "
                                            + string1
                                            + "    "
                                            + getDefaultLanguage()
                                            + "=\""
                                            + string2
                                            + "\"    "
                                            + getLanguage()
                                            + "=\""
                                            + string4
                                            + "\"\r\n"
                                    );
                                    debugErrors = true;
                                }
                            }
                        }

                        boolean0 = false;
                        string2 = "";
                        string1 = "";
                    }
                } catch (Exception exception0) {
                    if (debug) {
                        if (string3 != null) {
                            debugwrite(string3 + "\r\n");
                            string3 = null;
                        }

                        debugwrite("line " + int0 + ": " + string1 + " = " + string2 + "\r\n");
                        if (debugFile != null) {
                            exception0.printStackTrace(new PrintWriter(debugFile));
                        }

                        debugwrite("\r\n");
                        debugErrors = true;
                    }
                }
            }
        } catch (Exception exception1) {
            exception1.printStackTrace();
        }
    }

    /**
     * Return the translated text for the selected language  If we don't fnid any translation for the selected language, we return the default text (in English)
     */
    public static String getText(String desc) {
        return getTextInternal(desc, false);
    }

    public static String getTextOrNull(String desc) {
        return getTextInternal(desc, true);
    }

    private static String getTextInternal(String string1, boolean boolean0) {
        if (ui == null) {
            loadFiles();
        }

        String string0 = null;
        if (string1.startsWith("UI_")) {
            string0 = ui.get(string1);
        } else if (string1.startsWith("Moodles_")) {
            string0 = moodles.get(string1);
        } else if (string1.startsWith("SurvivalGuide_")) {
            string0 = survivalGuide.get(string1);
        } else if (string1.startsWith("Farming_")) {
            string0 = farming.get(string1);
        } else if (string1.startsWith("IGUI_")) {
            string0 = igui.get(string1);
        } else if (string1.startsWith("ContextMenu_")) {
            string0 = contextMenu.get(string1);
        } else if (string1.startsWith("GameSound_")) {
            string0 = gameSound.get(string1);
        } else if (string1.startsWith("Sandbox_")) {
            string0 = sandbox.get(string1);
        } else if (string1.startsWith("Tooltip_")) {
            string0 = tooltip.get(string1);
        } else if (string1.startsWith("Challenge_")) {
            string0 = challenge.get(string1);
        } else if (string1.startsWith("MakeUp")) {
            string0 = makeup.get(string1);
        } else if (string1.startsWith("News_")) {
            string0 = news.get(string1);
        } else if (string1.startsWith("Stash_")) {
            string0 = stash.get(string1);
        } else if (string1.startsWith("RM_")) {
            string0 = recordedMedia.get(string1);
        }

        String string2 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "*" : null;
        if (string0 == null) {
            if (boolean0) {
                return null;
            }

            if (!missing.contains(string1)) {
                if (Core.bDebug) {
                    DebugLog.log("ERROR: Missing translation \"" + string1 + "\"");
                }

                if (debug) {
                    debugwrite("ERROR: Missing translation \"" + string1 + "\"\r\n");
                }

                missing.add(string1);
            }

            string0 = string1;
            string2 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "!" : null;
        }

        if (string0.contains("<br>")) {
            return string0.replaceAll("<br>", "\n");
        } else {
            return string2 == null ? string0 : string2 + string0;
        }
    }

    private static int countSubstitutions(String string) {
        int int0 = 0;
        if (string.contains("%1")) {
            int0++;
        }

        if (string.contains("%2")) {
            int0++;
        }

        if (string.contains("%3")) {
            int0++;
        }

        if (string.contains("%4")) {
            int0++;
        }

        return int0;
    }

    private static String subst(String string0, String string1, Object object) {
        if (object != null) {
            if (object instanceof Double) {
                double double0 = (Double)object;
                string0 = string0.replaceAll(string1, double0 == (long)double0 ? Long.toString((long)double0) : object.toString());
            } else {
                string0 = string0.replaceAll(string1, Matcher.quoteReplacement(object.toString()));
            }
        }

        return string0;
    }

    public static String getText(String desc, Object arg1) {
        String string = getText(desc);
        return subst(string, "%1", arg1);
    }

    public static String getText(String desc, Object arg1, Object arg2) {
        String string = getText(desc);
        string = subst(string, "%1", arg1);
        return subst(string, "%2", arg2);
    }

    public static String getText(String desc, Object arg1, Object arg2, Object arg3) {
        String string = getText(desc);
        string = subst(string, "%1", arg1);
        string = subst(string, "%2", arg2);
        return subst(string, "%3", arg3);
    }

    public static String getText(String desc, Object arg1, Object arg2, Object arg3, Object arg4) {
        String string = getText(desc);
        string = subst(string, "%1", arg1);
        string = subst(string, "%2", arg2);
        string = subst(string, "%3", arg3);
        return subst(string, "%4", arg4);
    }

    public static String getTextOrNull(String desc, Object arg1) {
        String string = getTextOrNull(desc);
        return string == null ? null : subst(string, "%1", arg1);
    }

    public static String getTextOrNull(String desc, Object arg1, Object arg2) {
        String string = getTextOrNull(desc);
        if (string == null) {
            return null;
        } else {
            string = subst(string, "%1", arg1);
            return subst(string, "%2", arg2);
        }
    }

    public static String getTextOrNull(String desc, Object arg1, Object arg2, Object arg3) {
        String string = getTextOrNull(desc);
        if (string == null) {
            return null;
        } else {
            string = subst(string, "%1", arg1);
            string = subst(string, "%2", arg2);
            return subst(string, "%3", arg3);
        }
    }

    public static String getTextOrNull(String desc, Object arg1, Object arg2, Object arg3, Object arg4) {
        String string = getTextOrNull(desc);
        if (string == null) {
            return null;
        } else {
            string = subst(string, "%1", arg1);
            string = subst(string, "%2", arg2);
            string = subst(string, "%3", arg3);
            return subst(string, "%4", arg4);
        }
    }

    private static String getDefaultText(String string) {
        return changeSomeStuff((String)((KahluaTable)LuaManager.env.rawget(string.split("_")[0] + "_" + getDefaultLanguage().name())).rawget(string));
    }

    private static String changeSomeStuff(String string) {
        return string;
    }

    public static void setLanguage(Language newlanguage) {
        if (newlanguage == null) {
            newlanguage = getDefaultLanguage();
        }

        language = newlanguage;
    }

    public static void setLanguage(int languageId) {
        Language languagex = Languages.instance.getByIndex(languageId);
        setLanguage(languagex);
    }

    public static Language getLanguage() {
        if (language == null) {
            String string = Core.getInstance().getOptionLanguageName();
            if (!StringUtils.isNullOrWhitespace(string)) {
                language = Languages.instance.getByName(string);
            }
        }

        if (language == null) {
            language = Languages.instance.getByName(System.getProperty("user.language").toUpperCase());
        }

        if (language == null) {
            language = getDefaultLanguage();
        }

        return language;
    }

    public static String getCharset() {
        return getLanguage().charset();
    }

    public static ArrayList<Language> getAvailableLanguage() {
        if (availableLanguage == null) {
            availableLanguage = new ArrayList<>();

            for (int int0 = 0; int0 < Languages.instance.getNumLanguages(); int0++) {
                availableLanguage.add(Languages.instance.getByIndex(int0));
            }
        }

        return availableLanguage;
    }

    public static String getDisplayItemName(String trim) {
        Object object = null;
        object = items.get(trim.replaceAll(" ", "_").replaceAll("-", "_"));
        return (String)(object == null ? trim : object);
    }

    public static String getItemNameFromFullType(String fullType) {
        if (!fullType.contains(".")) {
            throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
        } else {
            String string = itemName.get(fullType);
            if (string == null) {
                if (debug && getLanguage() != getDefaultLanguage() && !debugItem.contains(fullType)) {
                    debugItem.add(fullType);
                }

                Item item = ScriptManager.instance.getItem(fullType);
                if (item == null) {
                    string = fullType;
                } else {
                    string = item.getDisplayName();
                }

                itemName.put(fullType, string);
            }

            return string;
        }
    }

    public static void setDefaultItemEvolvedRecipeName(String fullType, String english) {
        if (getLanguage() == getDefaultLanguage()) {
            if (!fullType.contains(".")) {
                throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
            } else if (!itemEvolvedRecipeName.containsKey(fullType)) {
                itemEvolvedRecipeName.put(fullType, english);
            }
        }
    }

    public static String getItemEvolvedRecipeName(String fullType) {
        if (!fullType.contains(".")) {
            throw new IllegalArgumentException("fullType must contain \".\" i.e. module.type");
        } else {
            String string = itemEvolvedRecipeName.get(fullType);
            if (string == null) {
                if (debug && getLanguage() != getDefaultLanguage() && !debugItemEvolvedRecipeName.contains(fullType)) {
                    debugItemEvolvedRecipeName.add(fullType);
                }

                Item item = ScriptManager.instance.getItem(fullType);
                if (item == null) {
                    string = fullType;
                } else {
                    string = item.getDisplayName();
                }

                itemEvolvedRecipeName.put(fullType, string);
            }

            return string;
        }
    }

    public static String getMoveableDisplayName(String name) {
        String string0 = name.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("'", "").replaceAll("\\.", "");
        String string1 = moveables.get(string0);
        if (string1 == null) {
            return Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "!" + name : name;
        } else {
            return Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "*" + string1 : string1;
        }
    }

    public static String getMoveableDisplayNameOrNull(String name) {
        String string0 = name.replaceAll(" ", "_").replaceAll("-", "_").replaceAll("'", "").replaceAll("\\.", "");
        String string1 = moveables.get(string0);
        if (string1 == null) {
            return null;
        } else {
            return Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "*" + string1 : string1;
        }
    }

    public static String getMultiStageBuild(String name) {
        String string = multiStageBuild.get("MultiStageBuild_" + name);
        if (string == null) {
            if (debug && getLanguage() != getDefaultLanguage() && !debugMultiStageBuild.contains(name)) {
                debugMultiStageBuild.add(name);
            }

            return name;
        } else {
            return string;
        }
    }

    public static String getRecipeName(String name) {
        String string = null;
        string = recipe.get(name);
        if (string != null && !string.isEmpty()) {
            return string;
        } else {
            if (debug && getLanguage() != getDefaultLanguage() && !debugRecipe.contains(name)) {
                debugRecipe.add(name);
            }

            return name;
        }
    }

    public static Language getDefaultLanguage() {
        return Languages.instance.getDefaultLanguage();
    }

    public static void debugItemEvolvedRecipeNames() {
        if (debug && !debugItemEvolvedRecipeName.isEmpty()) {
            debugwrite("EvolvedRecipeName_" + getLanguage() + ".txt\r\n");
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(debugItemEvolvedRecipeName);
            Collections.sort(arrayList);

            for (String string : arrayList) {
                debugwrite("\tEvolvedRecipeName_" + string + " = \"" + itemEvolvedRecipeName.get(string) + "\",\r\n");
            }

            debugItemEvolvedRecipeName.clear();
        }
    }

    public static void debugItemNames() {
        if (debug && !debugItem.isEmpty()) {
            debugwrite("ItemName_" + getLanguage() + ".txt\r\n");
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(debugItem);
            Collections.sort(arrayList);

            for (String string : arrayList) {
                debugwrite("\tItemName_" + string + " = \"" + itemName.get(string) + "\",\r\n");
            }

            debugItem.clear();
        }
    }

    public static void debugMultiStageBuildNames() {
        if (debug && !debugMultiStageBuild.isEmpty()) {
            debugwrite("MultiStageBuild_" + getLanguage() + ".txt\r\n");
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(debugMultiStageBuild);
            Collections.sort(arrayList);

            for (String string : arrayList) {
                debugwrite("\tMultiStageBuild_" + string + " = \"\",\r\n");
            }

            debugMultiStageBuild.clear();
        }
    }

    public static void debugRecipeNames() {
        if (debug && !debugRecipe.isEmpty()) {
            debugwrite("Recipes_" + getLanguage() + ".txt\r\n");
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(debugRecipe);
            Collections.sort(arrayList);

            for (String string : arrayList) {
                debugwrite("\tRecipe_" + string.replace(" ", "_") + " = \"\",\r\n");
            }

            debugRecipe.clear();
        }
    }

    private static void debugwrite(String string) {
        if (debugFile != null) {
            try {
                debugFile.write(string);
                debugFile.flush();
            } catch (IOException iOException) {
            }
        }
    }

    public static ArrayList<String> getAzertyMap() {
        if (azertyLanguages == null) {
            azertyLanguages = new ArrayList<>();
            azertyLanguages.add("FR");
        }

        return azertyLanguages;
    }

    public static String getRadioText(String s) {
        String string = dynamicRadio.get(s);
        return string == null ? s : string;
    }

    public static String getTextMediaEN(String desc) {
        if (ui == null) {
            loadFiles();
        }

        String string0 = null;
        if (desc.startsWith("RM_")) {
            string0 = recordedMedia_EN.get(desc);
        }

        String string1 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "*" : null;
        if (string0 == null) {
            if (!missing.contains(desc) && Core.bDebug) {
                if (Core.bDebug) {
                    DebugLog.log("ERROR: Missing translation \"" + desc + "\"");
                }

                if (debug) {
                    debugwrite("ERROR: Missing translation \"" + desc + "\"\r\n");
                }

                missing.add(desc);
            }

            string0 = desc;
            string1 = Core.bDebug && DebugOptions.instance.TranslationPrefix.getValue() ? "!" : null;
        }

        if (string0.contains("<br>")) {
            return string0.replaceAll("<br>", "\n");
        } else {
            return string1 == null ? string0 : string1 + string0;
        }
    }

    private static final class News {
        String version;
        final ArrayList<String> sectionNames = new ArrayList<>();
        final HashMap<String, ArrayList<String>> sectionLists = new HashMap<>();

        News(String string) {
            this.version = string;
        }

        ArrayList<String> getOrCreateSectionList(String string) {
            if (this.sectionNames.contains(string)) {
                return this.sectionLists.get(string);
            } else {
                this.sectionNames.add(string);
                ArrayList arrayList = new ArrayList();
                this.sectionLists.put(string, arrayList);
                return arrayList;
            }
        }

        String toRichText() {
            StringBuilder stringBuilder = new StringBuilder("");

            for (String string0 : this.sectionNames) {
                ArrayList arrayList = this.sectionLists.get(string0);
                if (!arrayList.isEmpty()) {
                    stringBuilder.append("<LINE> <LEFT> <SIZE:medium> %s <LINE> <LINE> ".formatted(string0));

                    for (String string1 : arrayList) {
                        stringBuilder.append(string1);
                    }
                }
            }

            return stringBuilder.toString();
        }
    }
}
