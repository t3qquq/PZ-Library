// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.DirectoryStream.Filter;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.znet.SteamUtils;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.gameStates.ChooseGameInfo;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.modding.ActiveMods;
import zombie.modding.ActiveModsFile;
import zombie.network.CoopMaster;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.ResettableLazyValue;
import zombie.util.StringUtils;

public final class ZomboidFileSystem {
    public static final ZomboidFileSystem instance = new ZomboidFileSystem();
    private final ArrayList<String> loadList = new ArrayList<>();
    private final Map<String, String> modIdToDir = new HashMap<>();
    private final Map<String, ChooseGameInfo.Mod> modDirToMod = new HashMap<>();
    private ArrayList<String> modFolders;
    private ArrayList<String> modFoldersOrder;
    public final HashMap<String, String> ActiveFileMap = new HashMap<>();
    private final HashSet<String> AllAbsolutePaths = new HashSet<>();
    private final ResettableLazyValue<List<Path>> allowedPrefixes = new ResettableLazyValue<>(() -> {
        ArrayList arrayList = new ArrayList();
        instance.getAllModFolders(arrayList);
        arrayList.add(instance.getCacheDir());
        arrayList.add(instance.base.getAbsoluteFile().getAbsolutePath());
        Collections.reverse(arrayList);
        return arrayList.stream().map(ZomboidFileSystem::normalizeToPath).toList();
    });
    public File base;
    public URI baseURI;
    private File workdir;
    private URI workdirURI;
    private File localWorkdir;
    private File anims;
    private URI animsURI;
    private File animsX;
    private URI animsXURI;
    private File animSets;
    private URI animSetsURI;
    private File actiongroups;
    private URI actiongroupsURI;
    private File cacheDir;
    private final ConcurrentHashMap<String, String> RelativeMap = new ConcurrentHashMap<>();
    public final ThreadLocal<Boolean> IgnoreActiveFileMap = ThreadLocal.withInitial(() -> Boolean.FALSE);
    private final ConcurrentHashMap<String, URI> CanonicalURIMap = new ConcurrentHashMap<>();
    private final ArrayList<String> mods = new ArrayList<>();
    private final HashSet<String> LoadedPacks = new HashSet<>();
    private FileGuidTable m_fileGuidTable = null;
    private boolean m_fileGuidTableWatcherActive = false;
    private final PredicatedFileWatcher m_modFileWatcher = new PredicatedFileWatcher(this::isModFile, this::onModFileChanged);
    private final HashSet<String> m_watchedModFolders = new HashSet<>();
    private long m_modsChangedTime = 0L;

    private ZomboidFileSystem() {
    }

    public void init() throws IOException {
        this.base = new File("./").getAbsoluteFile().getCanonicalFile();
        this.baseURI = this.base.toURI();
        this.workdir = new File(this.base, "media").getAbsoluteFile().getCanonicalFile();
        this.workdirURI = this.workdir.toURI();
        this.localWorkdir = this.base.toPath().relativize(this.workdir.toPath()).toFile();
        this.anims = new File(this.workdir, "anims");
        this.animsURI = this.anims.toURI();
        this.animsX = new File(this.workdir, "anims_X");
        this.animsXURI = this.animsX.toURI();
        this.animSets = new File(this.workdir, "AnimSets");
        this.animSetsURI = this.animSets.toURI();
        this.actiongroups = new File(this.workdir, "actiongroups");
        this.actiongroupsURI = this.actiongroups.toURI();
        this.searchFolders(this.workdir);

        for (int int0 = 0; int0 < this.loadList.size(); int0++) {
            String string0 = this.getRelativeFile(this.loadList.get(int0));
            File file = new File(this.loadList.get(int0)).getAbsoluteFile();
            String string1 = file.getAbsolutePath();
            if (file.isDirectory()) {
                string1 = string1 + File.separator;
            }

            this.ActiveFileMap.put(string0.toLowerCase(Locale.ENGLISH), string1);
            this.AllAbsolutePaths.add(string1);
        }

        this.loadList.clear();
    }

    public File getCanonicalFile(File file, String string) {
        if (!file.isDirectory()) {
            return new File(file, string);
        } else {
            File[] files = file.listFiles((var1x, string0) -> string0.equalsIgnoreCase(string));
            return files != null && files.length != 0 ? files[0] : new File(file, string);
        }
    }

    public String getGameModeCacheDir() {
        if (Core.GameMode == null) {
            Core.GameMode = "Sandbox";
        }

        String string = this.getSaveDir();
        return string + File.separator + Core.GameMode;
    }

    public String getCurrentSaveDir() {
        return this.getGameModeCacheDir() + File.separator + Core.GameSaveWorld;
    }

    public String getFileNameInCurrentSave(String string) {
        return this.getCurrentSaveDir() + File.separator + string;
    }

    public String getFileNameInCurrentSave(String string1, String string0) {
        return this.getFileNameInCurrentSave(string1 + File.separator + string0);
    }

    public String getFileNameInCurrentSave(String string2, String string1, String string0) {
        return this.getFileNameInCurrentSave(string2 + File.separator + string1 + File.separator + string0);
    }

    public File getFileInCurrentSave(String string) {
        return new File(this.getFileNameInCurrentSave(string));
    }

    public File getFileInCurrentSave(String string0, String string1) {
        return new File(this.getFileNameInCurrentSave(string0, string1));
    }

    public File getFileInCurrentSave(String string0, String string1, String string2) {
        return new File(this.getFileNameInCurrentSave(string0, string1, string2));
    }

    public String getSaveDir() {
        String string = this.getCacheDirSub("Saves");
        ensureFolderExists(string);
        return string;
    }

    public String getSaveDirSub(String string) {
        return this.getSaveDir() + File.separator + string;
    }

    public String getScreenshotDir() {
        String string = this.getCacheDirSub("Screenshots");
        ensureFolderExists(string);
        return string;
    }

    public String getScreenshotDirSub(String string) {
        return this.getScreenshotDir() + File.separator + string;
    }

    public void setCacheDir(String string) {
        string = string.replace("/", File.separator);
        this.cacheDir = new File(string).getAbsoluteFile();
        ensureFolderExists(this.cacheDir);
    }

    public String getCacheDir() {
        if (this.cacheDir == null) {
            String string0 = System.getProperty("deployment.user.cachedir");
            if (string0 == null || System.getProperty("os.name").startsWith("Win")) {
                string0 = System.getProperty("user.home");
            }

            String string1 = string0 + File.separator + "Zomboid";
            this.setCacheDir(string1);
        }

        return this.cacheDir.getPath();
    }

    public String getCacheDirSub(String string) {
        return this.getCacheDir() + File.separator + string;
    }

    public String getMessagingDir() {
        String string = this.getCacheDirSub("messaging");
        ensureFolderExists(string);
        return string;
    }

    public String getMessagingDirSub(String string) {
        return this.getMessagingDir() + File.separator + string;
    }

    public File getMediaRootFile() {
        assert this.workdir != null;

        return this.workdir;
    }

    public String getMediaRootPath() {
        return this.workdir.getPath();
    }

    public File getMediaFile(String string) {
        assert this.workdir != null;

        return new File(this.workdir, string);
    }

    public String getMediaPath(String string) {
        return this.getMediaFile(string).getPath();
    }

    public String getAbsoluteWorkDir() {
        return this.workdir.getPath();
    }

    public String getLocalWorkDir() {
        return this.localWorkdir.getPath();
    }

    public String getLocalWorkDirSub(String string) {
        return this.getLocalWorkDir() + File.separator + string;
    }

    public String getAnimSetsPath() {
        return this.animSets.getPath();
    }

    public String getActionGroupsPath() {
        return this.actiongroups.getPath();
    }

    public static boolean ensureFolderExists(String string) {
        return ensureFolderExists(new File(string).getAbsoluteFile());
    }

    public static boolean ensureFolderExists(File file) {
        return file.exists() || file.mkdirs();
    }

    public void searchFolders(File file) {
        if (!GameServer.bServer) {
            Thread.yield();
            Core.getInstance().DoFrameReady();
        }

        if (file.isDirectory()) {
            String string = file.getAbsolutePath().replace("\\", "/").replace("./", "");
            if (string.contains("media/maps/")) {
                this.loadList.add(string);
            }

            String[] strings = file.list();

            for (int int0 = 0; int0 < strings.length; int0++) {
                this.searchFolders(new File(file.getAbsolutePath() + File.separator + strings[int0]));
            }
        } else {
            this.loadList.add(file.getAbsolutePath().replace("\\", "/").replace("./", ""));
        }
    }

    public Object[] getAllPathsContaining(String string) {
        ArrayList arrayList = new ArrayList();

        for (Entry entry : this.ActiveFileMap.entrySet()) {
            if (((String)entry.getKey()).contains(string)) {
                arrayList.add((String)entry.getValue());
            }
        }

        return arrayList.toArray();
    }

    public Object[] getAllPathsContaining(String string1, String string0) {
        ArrayList arrayList = new ArrayList();

        for (Entry entry : this.ActiveFileMap.entrySet()) {
            if (((String)entry.getKey()).contains(string1) && ((String)entry.getKey()).contains(string0)) {
                arrayList.add((String)entry.getValue());
            }
        }

        return arrayList.toArray();
    }

    public synchronized String getString(String string0) {
        if (this.IgnoreActiveFileMap.get()) {
            return string0;
        } else {
            String string1 = string0.toLowerCase(Locale.ENGLISH);
            String string2 = this.RelativeMap.get(string1);
            if (string2 != null) {
                string1 = string2;
            } else {
                String string3 = this.getRelativeFile(string0);
                string1 = string3.toLowerCase(Locale.ENGLISH);
                this.RelativeMap.put(string1, string1);
            }

            String string4 = this.ActiveFileMap.get(string1);
            return string4 != null ? string4 : string0;
        }
    }

    public synchronized boolean isKnownFile(String string0) {
        if (this.AllAbsolutePaths.contains(string0)) {
            return true;
        } else {
            String string1 = string0.toLowerCase(Locale.ENGLISH);
            String string2 = this.RelativeMap.get(string1);
            if (string2 != null) {
                string1 = string2;
            } else {
                String string3 = this.getRelativeFile(string0);
                string1 = string3.toLowerCase(Locale.ENGLISH);
                this.RelativeMap.put(string1, string1);
            }

            String string4 = this.ActiveFileMap.get(string1);
            return string4 != null;
        }
    }

    public String getAbsolutePath(String string1) {
        String string0 = string1.toLowerCase(Locale.ENGLISH);
        return this.ActiveFileMap.get(string0);
    }

    public void Reset() {
        this.loadList.clear();
        this.ActiveFileMap.clear();
        this.AllAbsolutePaths.clear();
        this.CanonicalURIMap.clear();
        this.modIdToDir.clear();
        this.modDirToMod.clear();
        this.mods.clear();
        this.modFolders = null;
        ActiveMods.Reset();
        if (this.m_fileGuidTable != null) {
            this.m_fileGuidTable.clear();
            this.m_fileGuidTable = null;
        }

        this.allowedPrefixes.reset();
    }

    public File getCanonicalFile(File file) {
        try {
            return file.getCanonicalFile();
        } catch (Exception exception) {
            return file.getAbsoluteFile();
        }
    }

    public File getCanonicalFile(String string) {
        return this.getCanonicalFile(new File(string));
    }

    public String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (Exception exception) {
            return file.getAbsolutePath();
        }
    }

    public String getCanonicalPath(String string) {
        return this.getCanonicalPath(new File(string));
    }

    public URI getCanonicalURI(String string) {
        URI uri = this.CanonicalURIMap.get(string);
        if (uri == null) {
            uri = this.getCanonicalFile(string).toURI();
            this.CanonicalURIMap.put(string, uri);
        }

        return uri;
    }

    public void resetModFolders() {
        this.modFolders = null;
        this.allowedPrefixes.reset();
    }

    public void getInstalledItemModsFolders(ArrayList<String> arrayList) {
        if (SteamUtils.isSteamModeEnabled()) {
            String[] strings = SteamWorkshop.instance.GetInstalledItemFolders();
            if (strings != null) {
                for (String string : strings) {
                    File file = new File(string + File.separator + "mods");
                    if (file.exists()) {
                        arrayList.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void getStagedItemModsFolders(ArrayList<String> arrayList1) {
        if (SteamUtils.isSteamModeEnabled()) {
            ArrayList arrayList0 = SteamWorkshop.instance.getStageFolders();

            for (int int0 = 0; int0 < arrayList0.size(); int0++) {
                File file = new File((String)arrayList0.get(int0) + File.separator + "Contents" + File.separator + "mods");
                if (file.exists()) {
                    arrayList1.add(file.getAbsolutePath());
                }
            }
        }
    }

    private void getAllModFoldersAux(String string0, List<String> list) {
        Filter filter = new Filter<Path>() {
            public boolean accept(Path path) throws IOException {
                return Files.isDirectory(path) && Files.exists(path.resolve("mod.info"));
            }
        };
        Path path0 = FileSystems.getDefault().getPath(string0);
        if (Files.exists(path0)) {
            try (DirectoryStream directoryStream = Files.newDirectoryStream(path0, filter)) {
                for (Path path1 : directoryStream) {
                    if (path1.getFileName().toString().toLowerCase().equals("examplemod")) {
                        DebugLog.Mod.println("refusing to list " + path1.getFileName());
                    } else {
                        String string1 = path1.toAbsolutePath().toString();
                        if (!this.m_watchedModFolders.contains(string1)) {
                            this.m_watchedModFolders.add(string1);
                            DebugFileWatcher.instance.addDirectory(string1);
                            Path path2 = path1.resolve("media");
                            if (Files.exists(path2)) {
                                DebugFileWatcher.instance.addDirectoryRecurse(path2.toAbsolutePath().toString());
                            }
                        }

                        list.add(string1);
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void setModFoldersOrder(String string) {
        this.modFoldersOrder = new ArrayList<>(Arrays.asList(string.split(",")));
    }

    public void getAllModFolders(List<String> list) {
        if (this.modFolders == null) {
            this.modFolders = new ArrayList<>();
            if (this.modFoldersOrder == null) {
                this.setModFoldersOrder("workshop,steam,mods");
            }

            ArrayList arrayList = new ArrayList();

            for (int int0 = 0; int0 < this.modFoldersOrder.size(); int0++) {
                String string0 = this.modFoldersOrder.get(int0);
                if ("workshop".equals(string0)) {
                    this.getStagedItemModsFolders(arrayList);
                }

                if ("steam".equals(string0)) {
                    this.getInstalledItemModsFolders(arrayList);
                }

                if ("mods".equals(string0)) {
                    arrayList.add(Core.getMyDocumentFolder() + File.separator + "mods");
                }
            }

            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                String string1 = (String)arrayList.get(int1);
                if (!this.m_watchedModFolders.contains(string1)) {
                    this.m_watchedModFolders.add(string1);
                    DebugFileWatcher.instance.addDirectory(string1);
                }

                this.getAllModFoldersAux(string1, this.modFolders);
            }

            DebugFileWatcher.instance.add(this.m_modFileWatcher);
        }

        list.clear();
        list.addAll(this.modFolders);
    }

    public ArrayList<ChooseGameInfo.Mod> getWorkshopItemMods(long long0) {
        ArrayList arrayList = new ArrayList();
        if (!SteamUtils.isSteamModeEnabled()) {
            return arrayList;
        } else {
            String string = SteamWorkshop.instance.GetItemInstallFolder(long0);
            if (string == null) {
                return arrayList;
            } else {
                File file0 = new File(string + File.separator + "mods");
                if (file0.exists() && file0.isDirectory()) {
                    File[] files = file0.listFiles();

                    for (File file1 : files) {
                        if (file1.isDirectory()) {
                            ChooseGameInfo.Mod mod = ChooseGameInfo.readModInfo(file1.getAbsolutePath());
                            if (mod != null) {
                                arrayList.add(mod);
                            }
                        }
                    }

                    return arrayList;
                } else {
                    return arrayList;
                }
            }
        }
    }

    public ChooseGameInfo.Mod searchForModInfo(File file0, String string, ArrayList<ChooseGameInfo.Mod> arrayList) {
        if (file0.isDirectory()) {
            String[] strings = file0.list();
            if (strings == null) {
                return null;
            }

            for (int int0 = 0; int0 < strings.length; int0++) {
                File file1 = new File(file0.getAbsolutePath() + File.separator + strings[int0]);
                ChooseGameInfo.Mod mod0 = this.searchForModInfo(file1, string, arrayList);
                if (mod0 != null) {
                    return mod0;
                }
            }
        } else if (file0.getAbsolutePath().endsWith("mod.info")) {
            ChooseGameInfo.Mod mod1 = ChooseGameInfo.readModInfo(file0.getAbsoluteFile().getParent());
            if (mod1 == null) {
                return null;
            }

            if (!StringUtils.isNullOrWhitespace(mod1.getId())) {
                this.modIdToDir.put(mod1.getId(), mod1.getDir());
                arrayList.add(mod1);
            }

            if (mod1.getId().equals(string)) {
                return mod1;
            }
        }

        return null;
    }

    public void loadMod(String string0) {
        if (this.getModDir(string0) != null) {
            if (CoopMaster.instance != null) {
                CoopMaster.instance.update();
            }

            DebugLog.Mod.println("loading " + string0);
            File file = new File(this.getModDir(string0));
            URI uri = file.toURI();
            this.loadList.clear();
            this.searchFolders(file);

            for (int int0 = 0; int0 < this.loadList.size(); int0++) {
                String string1 = this.getRelativeFile(uri, this.loadList.get(int0));
                string1 = string1.toLowerCase(Locale.ENGLISH);
                if (this.ActiveFileMap.containsKey(string1) && !string1.endsWith("mod.info") && !string1.endsWith("poster.png")) {
                    DebugLog.Mod.println("mod \"" + string0 + "\" overrides " + string1);
                }

                String string2 = new File(this.loadList.get(int0)).getAbsolutePath();
                this.ActiveFileMap.put(string1, string2);
                this.AllAbsolutePaths.add(string2);
            }

            this.loadList.clear();
        }
    }

    private ArrayList<String> readLoadedDotTxt() {
        String string0 = Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + "loaded.txt";
        File file = new File(string0);
        if (!file.exists()) {
            return null;
        } else {
            ArrayList arrayList = new ArrayList();

            try (
                FileReader fileReader = new FileReader(string0);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
            ) {
                for (String string1 = bufferedReader.readLine(); string1 != null; string1 = bufferedReader.readLine()) {
                    string1 = string1.trim();
                    if (!string1.isEmpty()) {
                        arrayList.add(string1);
                    }
                }
            } catch (Exception exception0) {
                ExceptionLogger.logException(exception0);
                arrayList = null;
            }

            try {
                file.delete();
            } catch (Exception exception1) {
                ExceptionLogger.logException(exception1);
            }

            return arrayList;
        }
    }

    private ActiveMods readDefaultModsTxt() {
        ActiveMods activeMods = ActiveMods.getById("default");
        ArrayList arrayList = this.readLoadedDotTxt();
        if (arrayList != null) {
            activeMods.getMods().addAll(arrayList);
            this.saveModsFile();
        }

        activeMods.clear();
        String string = Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + "default.txt";

        try {
            ActiveModsFile activeModsFile = new ActiveModsFile();
            if (activeModsFile.read(string, activeMods)) {
            }
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }

        return activeMods;
    }

    public void loadMods(String string) {
        if (Core.OptionModsEnabled) {
            if (GameClient.bClient) {
                ArrayList arrayList = new ArrayList();
                this.loadTranslationMods(arrayList);
                arrayList.addAll(GameClient.instance.ServerMods);
                this.loadMods(arrayList);
            } else {
                ActiveMods activeMods = ActiveMods.getById(string);
                if (!"default".equalsIgnoreCase(string)) {
                    ActiveMods.setLoadedMods(activeMods);
                    this.loadMods(activeMods.getMods());
                } else {
                    try {
                        activeMods = this.readDefaultModsTxt();
                        activeMods.checkMissingMods();
                        activeMods.checkMissingMaps();
                        ActiveMods.setLoadedMods(activeMods);
                        this.loadMods(activeMods.getMods());
                    } catch (Exception exception) {
                        ExceptionLogger.logException(exception);
                    }
                }
            }
        }
    }

    private boolean isTranslationMod(String string0) {
        ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
        if (mod == null) {
            return false;
        } else {
            boolean boolean0 = false;
            File file = new File(mod.getDir());
            URI uri = file.toURI();
            this.loadList.clear();
            this.searchFolders(file);

            for (int int0 = 0; int0 < this.loadList.size(); int0++) {
                String string1 = this.getRelativeFile(uri, this.loadList.get(int0));
                if (string1.endsWith(".lua")) {
                    return false;
                }

                if (string1.startsWith("media/maps/")) {
                    return false;
                }

                if (string1.startsWith("media/scripts/")) {
                    return false;
                }

                if (string1.startsWith("media/lua/")) {
                    if (!string1.startsWith("media/lua/shared/Translate/")) {
                        return false;
                    }

                    boolean0 = true;
                }
            }

            this.loadList.clear();
            return boolean0;
        }
    }

    private void loadTranslationMods(ArrayList<String> arrayList1) {
        if (GameClient.bClient) {
            ActiveMods activeMods = this.readDefaultModsTxt();
            ArrayList arrayList0 = new ArrayList();
            if (this.loadModsAux(activeMods.getMods(), arrayList0) == null) {
                for (String string : arrayList0) {
                    if (this.isTranslationMod(string)) {
                        DebugLog.Mod.println("loading translation mod \"" + string + "\"");
                        if (!arrayList1.contains(string)) {
                            arrayList1.add(string);
                        }
                    }
                }
            }
        }
    }

    private String loadModAndRequired(String string0, ArrayList<String> arrayList) {
        if (string0.isEmpty()) {
            return null;
        } else if (string0.toLowerCase().equals("examplemod")) {
            DebugLog.Mod.warn("refusing to load " + string0);
            return null;
        } else if (arrayList.contains(string0)) {
            return null;
        } else {
            ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
            if (mod == null) {
                if (GameServer.bServer) {
                    GameServer.ServerMods.remove(string0);
                }

                DebugLog.Mod.warn("required mod \"" + string0 + "\" not found");
                return string0;
            } else {
                if (mod.getRequire() != null) {
                    String string1 = this.loadModsAux(mod.getRequire(), arrayList);
                    if (string1 != null) {
                        return string1;
                    }
                }

                arrayList.add(string0);
                return null;
            }
        }
    }

    public String loadModsAux(ArrayList<String> arrayList0, ArrayList<String> arrayList1) {
        for (String string0 : arrayList0) {
            String string1 = this.loadModAndRequired(string0, arrayList1);
            if (string1 != null) {
                return string1;
            }
        }

        return null;
    }

    public void loadMods(ArrayList<String> arrayList) {
        this.mods.clear();

        for (String string0 : arrayList) {
            this.loadModAndRequired(string0, this.mods);
        }

        for (String string1 : this.mods) {
            this.loadMod(string1);
        }
    }

    public ArrayList<String> getModIDs() {
        return this.mods;
    }

    public String getModDir(String string) {
        return this.modIdToDir.get(string);
    }

    public ChooseGameInfo.Mod getModInfoForDir(String string) {
        ChooseGameInfo.Mod mod = this.modDirToMod.get(string);
        if (mod == null) {
            mod = new ChooseGameInfo.Mod(string);
            this.modDirToMod.put(string, mod);
        }

        return mod;
    }

    public String getRelativeFile(File file) {
        return this.getRelativeFile(this.baseURI, file.getAbsolutePath());
    }

    public String getRelativeFile(String string) {
        return this.getRelativeFile(this.baseURI, string);
    }

    public String getRelativeFile(URI uri, File file) {
        return this.getRelativeFile(uri, file.getAbsolutePath());
    }

    public String getRelativeFile(URI uri2, String string0) {
        URI uri0 = this.getCanonicalURI(string0);
        URI uri1 = this.getCanonicalURI(uri2.getPath()).relativize(uri0);
        if (uri1.equals(uri0)) {
            return string0;
        } else {
            String string1 = uri1.getPath();
            if (string0.endsWith("/") && !string1.endsWith("/")) {
                string1 = string1 + "/";
            }

            return string1;
        }
    }

    public String getAnimName(URI uri, File file) {
        String string0 = this.getRelativeFile(uri, file);
        String string1 = string0.toLowerCase(Locale.ENGLISH);
        int int0 = string1.lastIndexOf(46);
        if (int0 > -1) {
            string1 = string1.substring(0, int0);
        }

        if (string1.startsWith("anims/")) {
            string1 = string1.substring("anims/".length());
        } else if (string1.startsWith("anims_x/")) {
            string1 = string1.substring("anims_x/".length());
        }

        return string1;
    }

    public String resolveRelativePath(String string0, String string1) {
        Path path0 = Paths.get(string0);
        Path path1 = path0.getParent();
        Path path2 = path1.resolve(string1);
        String string2 = path2.toString();
        return this.getRelativeFile(string2);
    }

    public void saveModsFile() {
        try {
            ensureFolderExists(Core.getMyDocumentFolder() + File.separator + "mods");
            String string = Core.getMyDocumentFolder() + File.separator + "mods" + File.separator + "default.txt";
            ActiveModsFile activeModsFile = new ActiveModsFile();
            activeModsFile.write(string, ActiveMods.getById("default"));
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
        }
    }

    public void loadModPackFiles() {
        for (String string0 : this.mods) {
            try {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
                if (mod != null) {
                    for (ChooseGameInfo.PackFile packFile : mod.getPacks()) {
                        String string1 = this.getRelativeFile("media/texturepacks/" + packFile.name + ".pack");
                        string1 = string1.toLowerCase(Locale.ENGLISH);
                        if (!this.ActiveFileMap.containsKey(string1)) {
                            DebugLog.Mod.warn("pack file \"" + packFile.name + "\" needed by " + string0 + " not found");
                        } else {
                            String string2 = instance.getString("media/texturepacks/" + packFile.name + ".pack");
                            if (!this.LoadedPacks.contains(string2)) {
                                GameWindow.LoadTexturePack(packFile.name, packFile.flags, string0);
                                this.LoadedPacks.add(string2);
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }

        GameWindow.setTexturePackLookup();
    }

    public void loadModTileDefs() {
        HashSet hashSet = new HashSet();

        for (String string0 : this.mods) {
            try {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
                if (mod != null) {
                    for (ChooseGameInfo.TileDef tileDef : mod.getTileDefs()) {
                        if (hashSet.contains(tileDef.fileNumber)) {
                            DebugLog.Mod.error("tiledef fileNumber " + tileDef.fileNumber + " used by more than one mod");
                        } else {
                            String string1 = tileDef.name;
                            String string2 = this.getRelativeFile("media/" + string1 + ".tiles");
                            string2 = string2.toLowerCase(Locale.ENGLISH);
                            if (!this.ActiveFileMap.containsKey(string2)) {
                                DebugLog.Mod.error("tiledef file \"" + tileDef.name + "\" needed by " + string0 + " not found");
                            } else {
                                string1 = this.ActiveFileMap.get(string2);
                                IsoWorld.instance.LoadTileDefinitions(IsoSpriteManager.instance, string1, tileDef.fileNumber);
                                hashSet.add(tileDef.fileNumber);
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void loadModTileDefPropertyStrings() {
        HashSet hashSet = new HashSet();

        for (String string0 : this.mods) {
            try {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
                if (mod != null) {
                    for (ChooseGameInfo.TileDef tileDef : mod.getTileDefs()) {
                        if (hashSet.contains(tileDef.fileNumber)) {
                            DebugLog.Mod.error("tiledef fileNumber " + tileDef.fileNumber + " used by more than one mod");
                        } else {
                            String string1 = tileDef.name;
                            String string2 = this.getRelativeFile("media/" + string1 + ".tiles");
                            string2 = string2.toLowerCase(Locale.ENGLISH);
                            if (!this.ActiveFileMap.containsKey(string2)) {
                                DebugLog.Mod.error("tiledef file \"" + tileDef.name + "\" needed by " + string0 + " not found");
                            } else {
                                string1 = this.ActiveFileMap.get(string2);
                                IsoWorld.instance.LoadTileDefinitionsPropertyStrings(IsoSpriteManager.instance, string1, tileDef.fileNumber);
                                hashSet.add(tileDef.fileNumber);
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void loadFileGuidTable() {
        File file = instance.getMediaFile("fileGuidTable.xml");

        try (FileInputStream fileInputStream0 = new FileInputStream(file)) {
            JAXBContext jAXBContext0 = JAXBContext.newInstance(FileGuidTable.class);
            Unmarshaller unmarshaller0 = jAXBContext0.createUnmarshaller();
            this.m_fileGuidTable = (FileGuidTable)unmarshaller0.unmarshal(fileInputStream0);
            this.m_fileGuidTable.setModID("game");
        } catch (IOException | JAXBException jAXBException) {
            System.err.println("Failed to load file Guid table.");
            ExceptionLogger.logException(jAXBException);
            return;
        }

        try {
            JAXBContext jAXBContext1 = JAXBContext.newInstance(FileGuidTable.class);
            Unmarshaller unmarshaller1 = jAXBContext1.createUnmarshaller();

            for (String string : this.getModIDs()) {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
                if (mod != null) {
                    try (FileInputStream fileInputStream1 = new FileInputStream(this.getModDir(string) + "/media/fileGuidTable.xml")) {
                        FileGuidTable fileGuidTable = (FileGuidTable)unmarshaller1.unmarshal(fileInputStream1);
                        fileGuidTable.setModID(string);
                        this.m_fileGuidTable.mergeFrom(fileGuidTable);
                    } catch (FileNotFoundException fileNotFoundException) {
                    } catch (Exception exception0) {
                        ExceptionLogger.logException(exception0);
                    }
                }
            }
        } catch (Exception exception1) {
            ExceptionLogger.logException(exception1);
        }

        this.m_fileGuidTable.loaded();
        if (!this.m_fileGuidTableWatcherActive) {
            DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/fileGuidTable.xml", var1x -> this.loadFileGuidTable()));
            this.m_fileGuidTableWatcherActive = true;
        }
    }

    public FileGuidTable getFileGuidTable() {
        if (this.m_fileGuidTable == null) {
            this.loadFileGuidTable();
        }

        return this.m_fileGuidTable;
    }

    public String getFilePathFromGuid(String string) {
        FileGuidTable fileGuidTable = this.getFileGuidTable();
        return fileGuidTable != null ? fileGuidTable.getFilePathFromGuid(string) : null;
    }

    public String getGuidFromFilePath(String string) {
        FileGuidTable fileGuidTable = this.getFileGuidTable();
        return fileGuidTable != null ? fileGuidTable.getGuidFromFilePath(string) : null;
    }

    public String resolveFileOrGUID(String string1) {
        String string0 = string1;
        String string2 = this.getFilePathFromGuid(string1);
        if (string2 != null) {
            string0 = string2;
        }

        String string3 = string0.toLowerCase(Locale.ENGLISH);
        return this.ActiveFileMap.containsKey(string3) ? this.ActiveFileMap.get(string3) : string0;
    }

    public boolean isValidFilePathGuid(String string) {
        return this.getFilePathFromGuid(string) != null;
    }

    public static File[] listAllDirectories(String string, FileFilter fileFilter, boolean boolean0) {
        File file = new File(string).getAbsoluteFile();
        return listAllDirectories(file, fileFilter, boolean0);
    }

    public static File[] listAllDirectories(File file, FileFilter fileFilter, boolean boolean0) {
        if (!file.isDirectory()) {
            return new File[0];
        } else {
            ArrayList arrayList = new ArrayList();
            listAllDirectoriesInternal(file, fileFilter, boolean0, arrayList);
            return arrayList.toArray(new File[0]);
        }
    }

    private static void listAllDirectoriesInternal(File file0, FileFilter fileFilter, boolean boolean0, ArrayList<File> arrayList) {
        File[] files = file0.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (!file1.isFile() && file1.isDirectory()) {
                    if (fileFilter.accept(file1)) {
                        arrayList.add(file1);
                    }

                    if (boolean0) {
                        listAllFilesInternal(file1, fileFilter, true, arrayList);
                    }
                }
            }
        }
    }

    public static File[] listAllFiles(String string, FileFilter fileFilter, boolean boolean0) {
        File file = new File(string).getAbsoluteFile();
        return listAllFiles(file, fileFilter, boolean0);
    }

    public static File[] listAllFiles(File file, FileFilter fileFilter, boolean boolean0) {
        if (!file.isDirectory()) {
            return new File[0];
        } else {
            ArrayList arrayList = new ArrayList();
            listAllFilesInternal(file, fileFilter, boolean0, arrayList);
            return arrayList.toArray(new File[0]);
        }
    }

    private static void listAllFilesInternal(File file0, FileFilter fileFilter, boolean boolean0, ArrayList<File> arrayList) {
        File[] files = file0.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isFile()) {
                    if (fileFilter.accept(file1)) {
                        arrayList.add(file1);
                    }
                } else if (file1.isDirectory() && boolean0) {
                    listAllFilesInternal(file1, fileFilter, true, arrayList);
                }
            }
        }
    }

    public void walkGameAndModFiles(String string0, boolean boolean0, ZomboidFileSystem.IWalkFilesVisitor iWalkFilesVisitor) {
        this.walkGameAndModFilesInternal(this.base, string0, boolean0, iWalkFilesVisitor);
        ArrayList arrayList = this.getModIDs();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            String string1 = this.getModDir((String)arrayList.get(int0));
            if (string1 != null) {
                this.walkGameAndModFilesInternal(new File(string1), string0, boolean0, iWalkFilesVisitor);
            }
        }
    }

    private void walkGameAndModFilesInternal(File file1, String string, boolean boolean0, ZomboidFileSystem.IWalkFilesVisitor iWalkFilesVisitor) {
        File file0 = new File(file1, string);
        if (file0.isDirectory()) {
            File[] files = file0.listFiles();
            if (files != null) {
                for (File file2 : files) {
                    iWalkFilesVisitor.visit(file2, string);
                    if (boolean0 && file2.isDirectory()) {
                        this.walkGameAndModFilesInternal(file1, string + "/" + file2.getName(), true, iWalkFilesVisitor);
                    }
                }
            }
        }
    }

    public String[] resolveAllDirectories(String string, FileFilter fileFilter, boolean boolean0) {
        ArrayList arrayList = new ArrayList();
        this.walkGameAndModFiles(string, boolean0, (file, string1) -> {
            if (file.isDirectory() && fileFilter.accept(file)) {
                String string0 = string1 + "/" + file.getName();
                if (!arrayList.contains(string0)) {
                    arrayList.add(string0);
                }
            }
        });
        return arrayList.toArray(new String[0]);
    }

    public String[] resolveAllFiles(String string, FileFilter fileFilter, boolean boolean0) {
        ArrayList arrayList = new ArrayList();
        this.walkGameAndModFiles(string, boolean0, (file, string1) -> {
            if (file.isFile() && fileFilter.accept(file)) {
                String string0 = string1 + "/" + file.getName();
                if (!arrayList.contains(string0)) {
                    arrayList.add(string0);
                }
            }
        });
        return arrayList.toArray(new String[0]);
    }

    public String normalizeFolderPath(String string) {
        string = string.toLowerCase(Locale.ENGLISH).replace('\\', '/');
        string = string + "/";
        return string.replace("///", "/").replace("//", "/");
    }

    public static String processFilePath(String string, char char0) {
        if (char0 != '\\') {
            string = string.replace('\\', char0);
        }

        if (char0 != '/') {
            string = string.replace('/', char0);
        }

        return string;
    }

    public boolean tryDeleteFile(String string) {
        if (StringUtils.isNullOrWhitespace(string)) {
            return false;
        } else {
            try {
                return this.deleteFile(string);
            } catch (AccessControlException | IOException iOException) {
                ExceptionLogger.logException(iOException, String.format("Failed to delete file: \"%s\"", string), DebugLog.FileIO, LogSeverity.General);
                return false;
            }
        }
    }

    public boolean deleteFile(String string) throws IOException {
        File file = new File(string).getAbsoluteFile();
        if (!file.isFile()) {
            throw new FileNotFoundException(String.format("File path not found: \"%s\"", string));
        } else if (file.delete()) {
            DebugLog.FileIO.debugln("File deleted successfully: \"%s\"", string);
            return true;
        } else {
            DebugLog.FileIO.debugln("Failed to delete file: \"%s\"", string);
            return false;
        }
    }

    public void update() {
        if (this.m_modsChangedTime != 0L) {
            long long0 = System.currentTimeMillis();
            if (this.m_modsChangedTime <= long0) {
                this.m_modsChangedTime = 0L;
                this.modFolders = null;
                this.modIdToDir.clear();
                this.modDirToMod.clear();
                this.allowedPrefixes.reset();
                ChooseGameInfo.Reset();

                for (String string : this.getModIDs()) {
                    ChooseGameInfo.getModDetails(string);
                }

                LuaEventManager.triggerEvent("OnModsModified");
            }
        }
    }

    private boolean isModFile(String string0) {
        if (this.m_modsChangedTime > 0L) {
            return false;
        } else if (this.modFolders == null) {
            return false;
        } else {
            string0 = string0.toLowerCase().replace('\\', '/');
            if (string0.endsWith("/mods/default.txt")) {
                return false;
            } else {
                for (int int0 = 0; int0 < this.modFolders.size(); int0++) {
                    String string1 = this.modFolders.get(int0).toLowerCase().replace('\\', '/');
                    if (string0.startsWith(string1)) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    private void onModFileChanged(String var1) {
        this.m_modsChangedTime = System.currentTimeMillis() + 2000L;
    }

    public void cleanMultiplayerSaves() {
        DebugLog.FileIO.println("Start cleaning save fs");
        String string0 = this.getSaveDir();
        String string1 = string0 + File.separator + "Multiplayer" + File.separator;
        File file0 = new File(string1);
        if (!file0.exists()) {
            file0.mkdir();
        }

        try {
            File[] files = file0.listFiles();

            for (File file1 : files) {
                DebugLog.FileIO.println("Checking " + file1.getAbsoluteFile() + " dir");
                if (file1.isDirectory()) {
                    File file2 = new File(file1.toString() + File.separator + "map.bin");
                    if (file2.exists()) {
                        DebugLog.FileIO.println("Processing " + file1.getAbsoluteFile() + " dir");

                        try {
                            Stream stream = Files.walk(file1.toPath());
                            stream.forEach(path -> {
                                if (path.getFileName().toString().matches("map_\\d+_\\d+.bin")) {
                                    DebugLog.FileIO.println("Delete " + path.getFileName().toString());
                                    path.toFile().delete();
                                }
                            });
                        } catch (IOException iOException) {
                            throw new RuntimeException(iOException);
                        }
                    }
                }
            }
        } catch (RuntimeException runtimeException) {
            runtimeException.printStackTrace();
        }
    }

    public void resetDefaultModsForNewRelease(String string1) {
        ensureFolderExists(this.getCacheDirSub("mods"));
        String string0 = this.getCacheDirSub("mods") + File.separator + "reset-mods-" + string1 + ".txt";
        File file = new File(string0);
        if (!file.exists()) {
            try (
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            ) {
                String string2 = "If this file does not exist, default.txt will be reset to empty (no mods active).";
                bufferedWriter.write(string2);
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
                return;
            }

            ActiveMods activeMods = ActiveMods.getById("default");
            activeMods.clear();
            this.saveModsFile();
        }
    }

    private static Path normalizeToPath(String string) {
        return Path.of(Path.of(string).normalize().toAbsolutePath().toString().toLowerCase());
    }

    public void validatePrefix(String string) {
        Path path0 = normalizeToPath(string);
        List list = this.allowedPrefixes.get();
        int int0 = list.size();

        for (int int1 = 0; int1 < int0; int1++) {
            Path path1 = (Path)list.get(int1);
            if (path0.startsWith(path1)) {
                return;
            }
        }

        throw new IllegalArgumentException("Invalid prefix found for: %s".formatted(string));
    }

    public interface IWalkFilesVisitor {
        void visit(File var1, String var2);
    }
}
