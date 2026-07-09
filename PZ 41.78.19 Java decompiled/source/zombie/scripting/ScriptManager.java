// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.scripting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import zombie.GameSounds;
import zombie.SoundManager;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.IndieFileLoader;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.runtime.RuntimeAnimationScript;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.RecipeManager;
import zombie.iso.IsoWorld;
import zombie.iso.MultiStageBuilding;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetChecksum;
import zombie.scripting.objects.AnimationsMesh;
import zombie.scripting.objects.EvolvedRecipe;
import zombie.scripting.objects.Fixing;
import zombie.scripting.objects.GameSoundScript;
import zombie.scripting.objects.Item;
import zombie.scripting.objects.MannequinScript;
import zombie.scripting.objects.ModelScript;
import zombie.scripting.objects.Recipe;
import zombie.scripting.objects.ScriptModule;
import zombie.scripting.objects.SoundTimelineScript;
import zombie.scripting.objects.UniqueRecipe;
import zombie.scripting.objects.VehicleScript;
import zombie.scripting.objects.VehicleTemplate;
import zombie.util.StringUtils;
import zombie.vehicles.VehicleEngineRPM;
import zombie.world.WorldDictionary;

public final class ScriptManager implements IScriptObjectStore {
    public static final ScriptManager instance = new ScriptManager();
    public String currentFileName;
    public final ArrayList<String> scriptsWithVehicles = new ArrayList<>();
    public final ArrayList<String> scriptsWithVehicleTemplates = new ArrayList<>();
    public final HashMap<String, ScriptModule> ModuleMap = new HashMap<>();
    public final ArrayList<ScriptModule> ModuleList = new ArrayList<>();
    private final HashMap<String, Item> FullTypeToItemMap = new HashMap<>();
    private final HashMap<String, SoundTimelineScript> SoundTimelineMap = new HashMap<>();
    public ScriptModule CurrentLoadingModule = null;
    private final HashMap<String, String> ModuleAliases = new HashMap<>();
    private final StringBuilder buf = new StringBuilder();
    private final HashMap<String, ScriptModule> CachedModules = new HashMap<>();
    private final ArrayList<Recipe> recipesTempList = new ArrayList<>();
    private final Stack<EvolvedRecipe> evolvedRecipesTempList = new Stack<>();
    private final Stack<UniqueRecipe> uniqueRecipesTempList = new Stack<>();
    private final ArrayList<Item> itemTempList = new ArrayList<>();
    private final HashMap<String, ArrayList<Item>> tagToItemMap = new HashMap<>();
    private final HashMap<String, ArrayList<Item>> typeToItemMap = new HashMap<>();
    private final ArrayList<AnimationsMesh> animationsMeshTempList = new ArrayList<>();
    private final ArrayList<MannequinScript> mannequinScriptTempList = new ArrayList<>();
    private final ArrayList<ModelScript> modelScriptTempList = new ArrayList<>();
    private final ArrayList<VehicleScript> vehicleScriptTempList = new ArrayList<>();
    private final HashMap<String, String> clothingToItemMap = new HashMap<>();
    private final ArrayList<String> visualDamagesList = new ArrayList<>();
    private static final String Base = "Base";
    private static final String Base_Module = "Base.";
    private String checksum = "";
    private HashMap<String, String> tempFileToModMap;
    private static String currentLoadFileMod;
    private static String currentLoadFileAbsPath;
    public static final String VanillaID = "pz-vanilla";

    public void ParseScript(String totalFile) {
        if (DebugLog.isEnabled(DebugType.Script)) {
            DebugLog.Script.debugln("Parsing...");
        }

        ArrayList arrayList = ScriptParser.parseTokens(totalFile);

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            String string = (String)arrayList.get(int0);
            this.CreateFromToken(string);
        }
    }

    public void update() {
    }

    public void LoadFile(String filename, boolean bLoadJar) throws FileNotFoundException {
        if (DebugLog.isEnabled(DebugType.Script)) {
            DebugLog.Script.debugln(filename + (bLoadJar ? " bLoadJar" : ""));
        }

        if (!GameServer.bServer) {
            Thread.yield();
            Core.getInstance().DoFrameReady();
        }

        if (filename.contains(".tmx")) {
            IsoWorld.mapPath = filename.substring(0, filename.lastIndexOf("/"));
            IsoWorld.mapUseJar = bLoadJar;
            DebugLog.Script.debugln("  file is a .tmx (map) file. Set mapPath to " + IsoWorld.mapPath + (IsoWorld.mapUseJar ? " mapUseJar" : ""));
        } else if (!filename.endsWith(".txt")) {
            DebugLog.Script.warn(" file is not a .txt (script) file: " + filename);
        } else {
            InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(filename, !bLoadJar);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            this.buf.setLength(0);
            Object object = null;
            String string = "";

            label108: {
                try {
                    while (true) {
                        if ((object = bufferedReader.readLine()) == null) {
                            break label108;
                        }

                        this.buf.append((String)object);
                        this.buf.append('\n');
                    }
                } catch (Exception exception0) {
                    DebugLog.Script.error("Exception thrown reading file " + filename + "\n  " + exception0);
                } finally {
                    try {
                        bufferedReader.close();
                        inputStreamReader.close();
                    } catch (Exception exception1) {
                        DebugLog.Script.error("Exception thrown closing file " + filename + "\n  " + exception1);
                        exception1.printStackTrace(DebugLog.Script);
                    }
                }

                return;
            }

            string = this.buf.toString();
            string = ScriptParser.stripComments(string);
            this.currentFileName = filename;
            this.ParseScript(string);
            this.currentFileName = null;
        }
    }

    private void CreateFromToken(String string0) {
        string0 = string0.trim();
        if (string0.indexOf("module") == 0) {
            int int0 = string0.indexOf("{");
            int int1 = string0.lastIndexOf("}");
            String[] strings = string0.split("[{}]");
            String string1 = strings[0];
            string1 = string1.replace("module", "");
            string1 = string1.trim();
            String string2 = string0.substring(int0 + 1, int1);
            ScriptModule scriptModule = this.ModuleMap.get(string1);
            if (scriptModule == null) {
                if (DebugLog.isEnabled(DebugType.Script)) {
                    DebugLog.Script.debugln("Adding new module: " + string1);
                }

                scriptModule = new ScriptModule();
                this.ModuleMap.put(string1, scriptModule);
                this.ModuleList.add(scriptModule);
            }

            scriptModule.Load(string1, string2);
        }
    }

    public void searchFolders(URI base, File fo, ArrayList<String> loadList) {
        if (fo.isDirectory()) {
            String[] strings = fo.list();

            for (int int0 = 0; int0 < strings.length; int0++) {
                this.searchFolders(base, new File(fo.getAbsolutePath() + File.separator + strings[int0]), loadList);
            }
        } else if (fo.getAbsolutePath().toLowerCase().endsWith(".txt")) {
            String string = ZomboidFileSystem.instance.getRelativeFile(base, fo.getAbsolutePath());
            string = string.toLowerCase(Locale.ENGLISH);
            loadList.add(string);
        }
    }

    public static String getItemName(String name) {
        int int0 = name.indexOf(46);
        return int0 == -1 ? name : name.substring(int0 + 1);
    }

    public ScriptModule getModule(String name) {
        return this.getModule(name, true);
    }

    public ScriptModule getModule(String name, boolean defaultToBase) {
        if (name.trim().equals("Base") || name.startsWith("Base.")) {
            return this.ModuleMap.get("Base");
        } else if (this.CachedModules.containsKey(name)) {
            return this.CachedModules.get(name);
        } else {
            ScriptModule scriptModule = null;
            if (this.ModuleAliases.containsKey(name)) {
                name = this.ModuleAliases.get(name);
            }

            if (this.CachedModules.containsKey(name)) {
                return this.CachedModules.get(name);
            } else {
                if (this.ModuleMap.containsKey(name)) {
                    if (this.ModuleMap.get(name).disabled) {
                        scriptModule = null;
                    } else {
                        scriptModule = this.ModuleMap.get(name);
                    }
                }

                if (scriptModule != null) {
                    this.CachedModules.put(name, scriptModule);
                    return scriptModule;
                } else {
                    int int0 = name.indexOf(".");
                    if (int0 != -1) {
                        scriptModule = this.getModule(name.substring(0, int0));
                    }

                    if (scriptModule != null) {
                        this.CachedModules.put(name, scriptModule);
                        return scriptModule;
                    } else {
                        return defaultToBase ? this.ModuleMap.get("Base") : null;
                    }
                }
            }
        }
    }

    public ScriptModule getModuleNoDisableCheck(String name) {
        if (this.ModuleAliases.containsKey(name)) {
            name = this.ModuleAliases.get(name);
        }

        if (this.ModuleMap.containsKey(name)) {
            return this.ModuleMap.get(name);
        } else {
            return name.indexOf(".") != -1 ? this.getModule(name.split("\\.")[0]) : null;
        }
    }

    @Override
    public Item getItem(String name) {
        if (name.contains(".") && this.FullTypeToItemMap.containsKey(name)) {
            return this.FullTypeToItemMap.get(name);
        } else {
            ScriptModule scriptModule = this.getModule(name);
            return scriptModule == null ? null : scriptModule.getItem(getItemName(name));
        }
    }

    public Item FindItem(String name) {
        return this.FindItem(name, true);
    }

    public Item FindItem(String name, boolean moduleDefaultsToBase) {
        if (name.contains(".") && this.FullTypeToItemMap.containsKey(name)) {
            return this.FullTypeToItemMap.get(name);
        } else {
            ScriptModule scriptModule0 = this.getModule(name, moduleDefaultsToBase);
            if (scriptModule0 == null) {
                return null;
            } else {
                Item item = scriptModule0.getItem(getItemName(name));
                if (item == null) {
                    for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
                        ScriptModule scriptModule1 = this.ModuleList.get(int0);
                        if (!scriptModule1.disabled) {
                            item = scriptModule0.getItem(getItemName(name));
                            if (item != null) {
                                return item;
                            }
                        }
                    }
                }

                return item;
            }
        }
    }

    public boolean isDrainableItemType(String itemType) {
        Item item = this.FindItem(itemType);
        return item != null ? item.getType() == Item.Type.Drainable : false;
    }

    @Override
    public Recipe getRecipe(String name) {
        ScriptModule scriptModule = this.getModule(name);
        return scriptModule == null ? null : scriptModule.getRecipe(getItemName(name));
    }

    public VehicleScript getVehicle(String name) {
        ScriptModule scriptModule = this.getModule(name);
        return scriptModule == null ? null : scriptModule.getVehicle(getItemName(name));
    }

    public VehicleTemplate getVehicleTemplate(String name) {
        ScriptModule scriptModule = this.getModule(name);
        return scriptModule == null ? null : scriptModule.getVehicleTemplate(getItemName(name));
    }

    public VehicleEngineRPM getVehicleEngineRPM(String name) {
        ScriptModule scriptModule = this.getModule(name);
        return scriptModule == null ? null : scriptModule.getVehicleEngineRPM(getItemName(name));
    }

    public void CheckExitPoints() {
        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled && scriptModule.CheckExitPoints()) {
                return;
            }
        }
    }

    public ArrayList<Item> getAllItems() {
        if (this.itemTempList.isEmpty()) {
            for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
                ScriptModule scriptModule = this.ModuleList.get(int0);
                if (!scriptModule.disabled) {
                    for (Item item : scriptModule.ItemMap.values()) {
                        this.itemTempList.add(item);
                    }
                }
            }
        }

        return this.itemTempList;
    }

    public ArrayList<Item> getItemsTag(String tag) {
        if (StringUtils.isNullOrWhitespace(tag)) {
            throw new IllegalArgumentException("invalid tag \"" + tag + "\"");
        } else {
            tag = tag.toLowerCase(Locale.ENGLISH);
            ArrayList arrayList0 = this.tagToItemMap.get(tag);
            if (arrayList0 != null) {
                return arrayList0;
            } else {
                arrayList0 = new ArrayList();
                ArrayList arrayList1 = this.getAllItems();

                for (int int0 = 0; int0 < arrayList1.size(); int0++) {
                    Item item = (Item)arrayList1.get(int0);

                    for (int int1 = 0; int1 < item.Tags.size(); int1++) {
                        if (item.Tags.get(int1).equalsIgnoreCase(tag)) {
                            arrayList0.add(item);
                            break;
                        }
                    }
                }

                this.tagToItemMap.put(tag, arrayList0);
                return arrayList0;
            }
        }
    }

    public ArrayList<Item> getItemsByType(String type) {
        if (StringUtils.isNullOrWhitespace(type)) {
            throw new IllegalArgumentException("invalid type \"" + type + "\"");
        } else {
            ArrayList arrayList = this.typeToItemMap.get(type);
            if (arrayList != null) {
                return arrayList;
            } else {
                arrayList = new ArrayList();

                for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
                    ScriptModule scriptModule = this.ModuleList.get(int0);
                    if (!scriptModule.disabled) {
                        Item item = this.FullTypeToItemMap.get(StringUtils.moduleDotType(scriptModule.name, type));
                        if (item != null) {
                            arrayList.add(item);
                        }
                    }
                }

                this.tagToItemMap.put(type, arrayList);
                return arrayList;
            }
        }
    }

    public List<Fixing> getAllFixing(List<Fixing> result) {
        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                result.addAll(scriptModule.FixingMap.values());
            }
        }

        return result;
    }

    public ArrayList<Recipe> getAllRecipes() {
        this.recipesTempList.clear();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                for (int int1 = 0; int1 < scriptModule.RecipeMap.size(); int1++) {
                    Recipe recipe = scriptModule.RecipeMap.get(int1);
                    this.recipesTempList.add(recipe);
                }
            }
        }

        return this.recipesTempList;
    }

    public Stack<EvolvedRecipe> getAllEvolvedRecipes() {
        this.evolvedRecipesTempList.clear();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                for (int int1 = 0; int1 < scriptModule.EvolvedRecipeMap.size(); int1++) {
                    EvolvedRecipe evolvedRecipe = scriptModule.EvolvedRecipeMap.get(int1);
                    this.evolvedRecipesTempList.add(evolvedRecipe);
                }
            }
        }

        return this.evolvedRecipesTempList;
    }

    public Stack<UniqueRecipe> getAllUniqueRecipes() {
        this.uniqueRecipesTempList.clear();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                Iterator iterator = scriptModule.UniqueRecipeMap.iterator();

                while (iterator != null && iterator.hasNext()) {
                    UniqueRecipe uniqueRecipe = (UniqueRecipe)iterator.next();
                    this.uniqueRecipesTempList.add(uniqueRecipe);
                }
            }
        }

        return this.uniqueRecipesTempList;
    }

    public ArrayList<GameSoundScript> getAllGameSounds() {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                arrayList.addAll(scriptModule.GameSoundList);
            }
        }

        return arrayList;
    }

    public ArrayList<RuntimeAnimationScript> getAllRuntimeAnimationScripts() {
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                arrayList.addAll(scriptModule.RuntimeAnimationScriptMap.values());
            }
        }

        return arrayList;
    }

    public AnimationsMesh getAnimationsMesh(String name) {
        ScriptModule scriptModule = this.getModule(name);
        if (scriptModule == null) {
            return null;
        } else {
            name = getItemName(name);
            return scriptModule.AnimationsMeshMap.get(name);
        }
    }

    public ArrayList<AnimationsMesh> getAllAnimationsMeshes() {
        this.animationsMeshTempList.clear();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                this.animationsMeshTempList.addAll(scriptModule.AnimationsMeshMap.values());
            }
        }

        return this.animationsMeshTempList;
    }

    public MannequinScript getMannequinScript(String name) {
        ScriptModule scriptModule = this.getModule(name);
        if (scriptModule == null) {
            return null;
        } else {
            name = getItemName(name);
            return scriptModule.MannequinScriptMap.get(name);
        }
    }

    public ArrayList<MannequinScript> getAllMannequinScripts() {
        this.mannequinScriptTempList.clear();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                this.mannequinScriptTempList.addAll(scriptModule.MannequinScriptMap.values());
            }
        }

        this.mannequinScriptTempList
            .sort((mannequinScript1, mannequinScript0) -> String.CASE_INSENSITIVE_ORDER.compare(mannequinScript1.getName(), mannequinScript0.getName()));
        return this.mannequinScriptTempList;
    }

    public ModelScript getModelScript(String name) {
        ScriptModule scriptModule = this.getModule(name);
        if (scriptModule == null) {
            return null;
        } else {
            name = getItemName(name);
            return scriptModule.ModelScriptMap.get(name);
        }
    }

    public ArrayList<ModelScript> getAllModelScripts() {
        this.modelScriptTempList.clear();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                this.modelScriptTempList.addAll(scriptModule.ModelScriptMap.values());
            }
        }

        return this.modelScriptTempList;
    }

    public ArrayList<VehicleScript> getAllVehicleScripts() {
        this.vehicleScriptTempList.clear();

        for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
            ScriptModule scriptModule = this.ModuleList.get(int0);
            if (!scriptModule.disabled) {
                this.vehicleScriptTempList.addAll(scriptModule.VehicleMap.values());
            }
        }

        return this.vehicleScriptTempList;
    }

    public SoundTimelineScript getSoundTimeline(String eventName) {
        if (this.SoundTimelineMap.isEmpty()) {
            for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
                ScriptModule scriptModule = this.ModuleList.get(int0);
                if (!scriptModule.disabled) {
                    this.SoundTimelineMap.putAll(scriptModule.SoundTimelineMap);
                }
            }
        }

        return this.SoundTimelineMap.get(eventName);
    }

    public void Reset() {
        for (ScriptModule scriptModule : this.ModuleList) {
            scriptModule.Reset();
        }

        this.ModuleMap.clear();
        this.ModuleList.clear();
        this.ModuleAliases.clear();
        this.CachedModules.clear();
        this.FullTypeToItemMap.clear();
        this.itemTempList.clear();
        this.tagToItemMap.clear();
        this.typeToItemMap.clear();
        this.clothingToItemMap.clear();
        this.scriptsWithVehicles.clear();
        this.scriptsWithVehicleTemplates.clear();
        this.SoundTimelineMap.clear();
    }

    public String getChecksum() {
        return this.checksum;
    }

    public static String getCurrentLoadFileMod() {
        return currentLoadFileMod;
    }

    public static String getCurrentLoadFileAbsPath() {
        return currentLoadFileAbsPath;
    }

    public void Load() {
        try {
            WorldDictionary.StartScriptLoading();
            this.tempFileToModMap = new HashMap<>();
            ArrayList arrayList0 = new ArrayList();
            this.searchFolders(ZomboidFileSystem.instance.baseURI, ZomboidFileSystem.instance.getMediaFile("scripts"), arrayList0);

            for (String string0 : arrayList0) {
                this.tempFileToModMap.put(ZomboidFileSystem.instance.getAbsolutePath(string0), "pz-vanilla");
            }

            ArrayList arrayList1 = new ArrayList();
            ArrayList arrayList2 = ZomboidFileSystem.instance.getModIDs();

            for (int int0 = 0; int0 < arrayList2.size(); int0++) {
                String string1 = ZomboidFileSystem.instance.getModDir((String)arrayList2.get(int0));
                if (string1 != null) {
                    File file0 = new File(string1);
                    URI uri = file0.toURI();
                    int int1 = arrayList1.size();
                    File file1 = ZomboidFileSystem.instance.getCanonicalFile(file0, "media");
                    File file2 = ZomboidFileSystem.instance.getCanonicalFile(file1, "scripts");
                    this.searchFolders(uri, file2, arrayList1);
                    if (((String)arrayList2.get(int0)).equals("pz-vanilla")) {
                        throw new RuntimeException("Warning mod id is named pz-vanilla!");
                    }

                    for (int int2 = int1; int2 < arrayList1.size(); int2++) {
                        String string2 = (String)arrayList1.get(int2);
                        this.tempFileToModMap.put(ZomboidFileSystem.instance.getAbsolutePath(string2), (String)arrayList2.get(int0));
                    }
                }
            }

            Comparator comparator = new Comparator<String>() {
                public int compare(String string1, String string3) {
                    String string0 = new File(string1).getName();
                    String string2 = new File(string3).getName();
                    if (string0.startsWith("template_") && !string2.startsWith("template_")) {
                        return -1;
                    } else {
                        return !string0.startsWith("template_") && string2.startsWith("template_") ? 1 : string1.compareTo(string3);
                    }
                }
            };
            Collections.sort(arrayList0, comparator);
            Collections.sort(arrayList1, comparator);
            arrayList0.addAll(arrayList1);
            if (GameClient.bClient || GameServer.bServer) {
                NetChecksum.checksummer.reset(true);
                NetChecksum.GroupOfFiles.initChecksum();
            }

            MultiStageBuilding.stages.clear();
            HashSet hashSet = new HashSet();

            for (String string3 : arrayList0) {
                if (!hashSet.contains(string3)) {
                    hashSet.add(string3);
                    String string4 = ZomboidFileSystem.instance.getAbsolutePath(string3);
                    currentLoadFileAbsPath = string4;
                    currentLoadFileMod = this.tempFileToModMap.get(string4);
                    this.LoadFile(string3, false);
                    if (GameClient.bClient || GameServer.bServer) {
                        NetChecksum.checksummer.addFile(string3, string4);
                    }
                }
            }

            if (GameClient.bClient || GameServer.bServer) {
                this.checksum = NetChecksum.checksummer.checksumToString();
                if (GameServer.bServer) {
                    DebugLog.General.println("scriptChecksum: " + this.checksum);
                }
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }

        this.buf.setLength(0);

        for (int int3 = 0; int3 < this.ModuleList.size(); int3++) {
            ScriptModule scriptModule = this.ModuleList.get(int3);

            for (Item item : scriptModule.ItemMap.values()) {
                this.FullTypeToItemMap.put(item.getFullName(), item);
            }
        }

        this.debugItems();
        this.resolveItemTypes();
        WorldDictionary.ScriptsLoaded();
        RecipeManager.Loaded();
        GameSounds.ScriptsLoaded();
        ModelScript.ScriptsLoaded();
        if (SoundManager.instance != null) {
            SoundManager.instance.debugScriptSounds();
        }

        Translator.debugItemEvolvedRecipeNames();
        Translator.debugItemNames();
        Translator.debugMultiStageBuildNames();
        Translator.debugRecipeNames();
        this.createClothingItemMap();
        this.createZedDmgMap();
    }

    private void debugItems() {
        for (Item item : instance.getAllItems()) {
            if (item.getType() == Item.Type.Drainable && item.getReplaceOnUse() != null) {
                DebugLog.Script.warn("%s ReplaceOnUse instead of ReplaceOnDeplete", item.getFullName());
            }

            if (item.getType() == Item.Type.Weapon && !item.HitSound.equals(item.hitFloorSound)) {
                boolean boolean0 = true;
            }

            if (!StringUtils.isNullOrEmpty(item.worldStaticModel)) {
                ModelScript modelScript = this.getModelScript(item.worldStaticModel);
                if (modelScript != null && modelScript.getAttachmentById("world") != null) {
                    boolean boolean1 = true;
                }
            }
        }
    }

    public ArrayList<Recipe> getAllRecipesFor(String result) {
        ArrayList arrayList0 = this.getAllRecipes();
        ArrayList arrayList1 = new ArrayList();

        for (int int0 = 0; int0 < arrayList0.size(); int0++) {
            String string = ((Recipe)arrayList0.get(int0)).Result.type;
            if (string.contains(".")) {
                string = string.substring(string.indexOf(".") + 1);
            }

            if (string.equals(result)) {
                arrayList1.add((Recipe)arrayList0.get(int0));
            }
        }

        return arrayList1;
    }

    public String getItemTypeForClothingItem(String clothingItem) {
        return this.clothingToItemMap.get(clothingItem);
    }

    public Item getItemForClothingItem(String clothingName) {
        String string = this.getItemTypeForClothingItem(clothingName);
        return string == null ? null : this.FindItem(string);
    }

    private void createZedDmgMap() {
        this.visualDamagesList.clear();
        ScriptModule scriptModule = this.getModule("Base");

        for (Item item : scriptModule.ItemMap.values()) {
            if (!StringUtils.isNullOrWhitespace(item.getBodyLocation()) && "ZedDmg".equals(item.getBodyLocation())) {
                this.visualDamagesList.add(item.getName());
            }
        }
    }

    public ArrayList<String> getZedDmgMap() {
        return this.visualDamagesList;
    }

    private void createClothingItemMap() {
        for (Item item : this.getAllItems()) {
            if (!StringUtils.isNullOrWhitespace(item.getClothingItem())) {
                if (DebugLog.isEnabled(DebugType.Script)) {
                    DebugLog.Script.debugln("ClothingItem \"%s\" <---> Item \"%s\"", item.getClothingItem(), item.getFullName());
                }

                this.clothingToItemMap.put(item.getClothingItem(), item.getFullName());
            }
        }
    }

    private void resolveItemTypes() {
        for (Item item : this.getAllItems()) {
            item.resolveItemTypes();
        }
    }

    public String resolveItemType(ScriptModule module, String itemType) {
        if (StringUtils.isNullOrWhitespace(itemType)) {
            return null;
        } else if (itemType.contains(".")) {
            return itemType;
        } else {
            Item item = module.getItem(itemType);
            if (item != null) {
                return item.getFullName();
            } else {
                for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
                    ScriptModule scriptModule = this.ModuleList.get(int0);
                    if (!scriptModule.disabled) {
                        item = scriptModule.getItem(itemType);
                        if (item != null) {
                            return item.getFullName();
                        }
                    }
                }

                return "???." + itemType;
            }
        }
    }

    public String resolveModelScript(ScriptModule module, String modelScriptName) {
        if (StringUtils.isNullOrWhitespace(modelScriptName)) {
            return null;
        } else if (modelScriptName.contains(".")) {
            return modelScriptName;
        } else {
            ModelScript modelScript = module.getModelScript(modelScriptName);
            if (modelScript != null) {
                return modelScript.getFullType();
            } else {
                for (int int0 = 0; int0 < this.ModuleList.size(); int0++) {
                    ScriptModule scriptModule = this.ModuleList.get(int0);
                    if (scriptModule != module && !scriptModule.disabled) {
                        modelScript = scriptModule.getModelScript(modelScriptName);
                        if (modelScript != null) {
                            return modelScript.getFullType();
                        }
                    }
                }

                return "???." + modelScriptName;
            }
        }
    }

    /**
     * Attempts to get the specific item of "module.type" without defaulting to module "Base".
     */
    public Item getSpecificItem(String name) {
        if (!name.contains(".")) {
            DebugLog.log("ScriptManager.getSpecificItem requires a full type name, cannot find: " + name);
            if (Core.bDebug) {
                throw new RuntimeException("ScriptManager.getSpecificItem requires a full type name, cannot find: " + name);
            } else {
                return null;
            }
        } else if (this.FullTypeToItemMap.containsKey(name)) {
            return this.FullTypeToItemMap.get(name);
        } else {
            int int0 = name.indexOf(".");
            String string0 = name.substring(0, int0);
            String string1 = name.substring(int0 + 1);
            ScriptModule scriptModule = this.getModule(string0, false);
            return scriptModule == null ? null : scriptModule.getSpecificItem(string1);
        }
    }
}
