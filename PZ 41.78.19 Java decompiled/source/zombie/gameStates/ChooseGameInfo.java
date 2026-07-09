// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.gameStates;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.GameVersion;
import zombie.core.IndieFileLoader;
import zombie.core.Language;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureID;
import zombie.core.znet.SteamWorkshop;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;

public final class ChooseGameInfo {
    private static final HashMap<String, ChooseGameInfo.Map> Maps = new HashMap<>();
    private static final HashMap<String, ChooseGameInfo.Mod> Mods = new HashMap<>();
    private static final HashSet<String> MissingMods = new HashSet<>();
    private static final ArrayList<String> tempStrings = new ArrayList<>();

    private ChooseGameInfo() {
    }

    public static void Reset() {
        Maps.clear();
        Mods.clear();
        MissingMods.clear();
    }

    private static void readTitleDotTxt(ChooseGameInfo.Map map, String string1, Language language) throws IOException {
        String string0 = "media/lua/shared/Translate/" + language.toString() + "/" + string1 + "/title.txt";
        File file = new File(ZomboidFileSystem.instance.getString(string0));

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(language.charset()));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            String string2 = bufferedReader.readLine();
            string2 = StringUtils.stripBOM(string2);
            if (!StringUtils.isNullOrWhitespace(string2)) {
                map.title = string2.trim();
            }
        } catch (FileNotFoundException fileNotFoundException) {
        }
    }

    private static void readDescriptionDotTxt(ChooseGameInfo.Map map, String string1, Language language) throws IOException {
        String string0 = "media/lua/shared/Translate/" + language.toString() + "/" + string1 + "/description.txt";
        File file = new File(ZomboidFileSystem.instance.getString(string0));

        try (
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.forName(language.charset()));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        ) {
            map.desc = "";
            boolean boolean0 = true;

            String string2;
            while ((string2 = bufferedReader.readLine()) != null) {
                if (boolean0) {
                    string2 = StringUtils.stripBOM(string2);
                    boolean0 = false;
                }

                map.desc = map.desc + string2;
            }
        } catch (FileNotFoundException fileNotFoundException) {
        }
    }

    public static ChooseGameInfo.Map getMapDetails(String string0) {
        if (Maps.containsKey(string0)) {
            return Maps.get(string0);
        } else {
            File file = new File(ZomboidFileSystem.instance.getString("media/maps/" + string0 + "/map.info"));
            if (!file.exists()) {
                return null;
            } else {
                ChooseGameInfo.Map map = new ChooseGameInfo.Map();
                map.dir = new File(file.getParent()).getAbsolutePath();
                map.title = string0;
                map.lotsDir = new ArrayList<>();

                try {
                    FileReader fileReader = new FileReader(file.getAbsolutePath());
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String string1 = null;

                    try {
                        while ((string1 = bufferedReader.readLine()) != null) {
                            string1 = string1.trim();
                            if (string1.startsWith("title=")) {
                                map.title = string1.replace("title=", "");
                            } else if (string1.startsWith("lots=")) {
                                map.lotsDir.add(string1.replace("lots=", "").trim());
                            } else if (string1.startsWith("description=")) {
                                if (map.desc == null) {
                                    map.desc = "";
                                }

                                map.desc = map.desc + string1.replace("description=", "");
                            } else if (string1.startsWith("fixed2x=")) {
                                map.bFixed2x = Boolean.parseBoolean(string1.replace("fixed2x=", "").trim());
                            }
                        }
                    } catch (IOException iOException) {
                        Logger.getLogger(ChooseGameInfo.class.getName()).log(Level.SEVERE, null, iOException);
                    }

                    bufferedReader.close();
                    map.thumb = Texture.getSharedTexture(map.dir + "/thumb.png");
                    ArrayList arrayList = new ArrayList();
                    Translator.addLanguageToList(Translator.getLanguage(), arrayList);
                    Translator.addLanguageToList(Translator.getDefaultLanguage(), arrayList);

                    for (int int0 = arrayList.size() - 1; int0 >= 0; int0--) {
                        Language language = (Language)arrayList.get(int0);
                        readTitleDotTxt(map, string0, language);
                        readDescriptionDotTxt(map, string0, language);
                    }
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                    return null;
                }

                Maps.put(string0, map);
                return map;
            }
        }
    }

    public static ChooseGameInfo.Mod getModDetails(String string0) {
        if (MissingMods.contains(string0)) {
            return null;
        } else if (Mods.containsKey(string0)) {
            return Mods.get(string0);
        } else {
            String string1 = ZomboidFileSystem.instance.getModDir(string0);
            if (string1 == null) {
                ArrayList arrayList0 = tempStrings;
                ZomboidFileSystem.instance.getAllModFolders(arrayList0);
                ArrayList arrayList1 = new ArrayList();

                for (int int0 = 0; int0 < arrayList0.size(); int0++) {
                    File file = new File((String)arrayList0.get(int0), "mod.info");
                    arrayList1.clear();
                    ChooseGameInfo.Mod mod0 = ZomboidFileSystem.instance.searchForModInfo(file, string0, arrayList1);

                    for (int int1 = 0; int1 < arrayList1.size(); int1++) {
                        ChooseGameInfo.Mod mod1 = (ChooseGameInfo.Mod)arrayList1.get(int1);
                        Mods.putIfAbsent(mod1.getId(), mod1);
                    }

                    if (mod0 != null) {
                        return mod0;
                    }
                }
            }

            ChooseGameInfo.Mod mod2 = readModInfo(string1);
            if (mod2 == null) {
                MissingMods.add(string0);
            }

            return mod2;
        }
    }

    public static ChooseGameInfo.Mod getAvailableModDetails(String string) {
        ChooseGameInfo.Mod mod = getModDetails(string);
        return mod != null && mod.isAvailable() ? mod : null;
    }

    public static ChooseGameInfo.Mod readModInfo(String string) {
        ChooseGameInfo.Mod mod0 = readModInfoAux(string);
        if (mod0 != null) {
            ChooseGameInfo.Mod mod1 = Mods.get(mod0.getId());
            if (mod1 == null) {
                Mods.put(mod0.getId(), mod0);
            } else if (mod1 != mod0) {
                ZomboidFileSystem.instance.getAllModFolders(tempStrings);
                int int0 = tempStrings.indexOf(mod0.getDir());
                int int1 = tempStrings.indexOf(mod1.getDir());
                if (int0 < int1) {
                    Mods.put(mod0.getId(), mod0);
                }
            }
        }

        return mod0;
    }

    private static ChooseGameInfo.Mod readModInfoAux(String string0) {
        if (string0 != null) {
            ChooseGameInfo.Mod mod = ZomboidFileSystem.instance.getModInfoForDir(string0);
            if (mod.bRead) {
                return mod.bValid ? mod : null;
            }

            mod.bRead = true;
            String string1 = string0 + File.separator + "mod.info";
            File file0 = new File(string1);
            if (!file0.exists()) {
                DebugLog.Mod.warn("can't find \"" + string1 + "\"");
                return null;
            }

            mod.setId(file0.getParentFile().getName());

            try (
                InputStreamReader inputStreamReader = IndieFileLoader.getStreamReader(string1);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ) {
                while (true) {
                    String string2;
                    if ((string2 = bufferedReader.readLine()) == null) {
                        if (mod.getUrl() == null) {
                            mod.setUrl("");
                        }
                        break;
                    }

                    if (string2.contains("name=")) {
                        mod.name = string2.replace("name=", "");
                    } else if (string2.contains("poster=")) {
                        String string3 = string2.replace("poster=", "");
                        if (!StringUtils.isNullOrWhitespace(string3)) {
                            mod.posters.add(string0 + File.separator + string3);
                        }
                    } else if (string2.contains("description=")) {
                        mod.desc = mod.desc + string2.replace("description=", "");
                    } else if (string2.contains("require=")) {
                        mod.setRequire(new ArrayList<>(Arrays.asList(string2.replace("require=", "").split(","))));
                    } else if (string2.contains("id=")) {
                        mod.setId(string2.replace("id=", ""));
                    } else if (string2.contains("url=")) {
                        mod.setUrl(string2.replace("url=", ""));
                    } else if (string2.contains("pack=")) {
                        String string4 = string2.replace("pack=", "").trim();
                        if (string4.isEmpty()) {
                            DebugLog.Mod.error("pack= line requires a file name");
                            return null;
                        }

                        int int0 = TextureID.bUseCompressionOption ? 4 : 0;
                        int0 |= 64;
                        int int1 = string4.indexOf("type=");
                        if (int1 != -1) {
                            String string5 = string4.substring(int1 + "type=".length());
                            byte byte0 = -1;
                            switch (string5.hashCode()) {
                                case 3732:
                                    if (string5.equals("ui")) {
                                        byte0 = 0;
                                    }
                                default:
                                    switch (byte0) {
                                        case 0:
                                            int0 = 2;
                                            break;
                                        default:
                                            DebugLog.Mod.error("unknown pack type=" + string5);
                                    }

                                    int int2 = string4.indexOf(32);
                                    string4 = string4.substring(0, int2).trim();
                            }
                        }

                        String string6 = string4;
                        String string7 = "";
                        if (string4.endsWith(".floor")) {
                            string6 = string4.substring(0, string4.lastIndexOf(46));
                            string7 = ".floor";
                            int0 &= -5;
                        }

                        int int3 = Core.getInstance().getOptionTexture2x() ? 2 : 1;
                        if (Core.SafeModeForced) {
                            int3 = 1;
                        }

                        if (int3 == 2) {
                            File file1 = new File(
                                string0 + File.separator + "media" + File.separator + "texturepacks" + File.separator + string6 + "2x" + string7 + ".pack"
                            );
                            if (file1.isFile()) {
                                DebugLog.Mod.printf("2x version of %s.pack found.\n", string4);
                                string4 = string6 + "2x" + string7;
                            } else {
                                file1 = new File(string0 + File.separator + "media" + File.separator + "texturepacks" + File.separator + string4 + "2x.pack");
                                if (file1.isFile()) {
                                    DebugLog.Mod.printf("2x version of %s.pack found.\n", string4);
                                    string4 = string4 + "2x";
                                } else {
                                    DebugLog.Mod.printf("2x version of %s.pack not found.\n", string4);
                                }
                            }
                        }

                        mod.addPack(string4, int0);
                    } else if (string2.contains("tiledef=")) {
                        String[] strings = string2.replace("tiledef=", "").trim().split("\\s+");
                        if (strings.length != 2) {
                            DebugLog.Mod.error("tiledef= line requires file name and file number");
                            return null;
                        }

                        String string8 = strings[0];

                        int int4;
                        try {
                            int4 = Integer.parseInt(strings[1]);
                        } catch (NumberFormatException numberFormatException) {
                            DebugLog.Mod.error("tiledef= line requires file name and file number");
                            return null;
                        }

                        byte byte1 = 100;
                        short short0 = 8190;
                        short0 = 16382;
                        if (int4 < byte1 || int4 > short0) {
                            DebugLog.Mod
                                .error("tiledef=%s %d file number must be from %d to %d", string8, int4, Integer.valueOf(byte1), Integer.valueOf(short0));
                            return null;
                        }

                        mod.addTileDef(string8, int4);
                    } else if (string2.startsWith("versionMax=")) {
                        String string9 = string2.replace("versionMax=", "").trim();
                        if (!string9.isEmpty()) {
                            try {
                                mod.versionMax = GameVersion.parse(string9);
                            } catch (Exception exception0) {
                                DebugLog.Mod.error("invalid versionMax: " + exception0.getMessage());
                                return null;
                            }
                        }
                    } else if (string2.startsWith("versionMin=")) {
                        String string10 = string2.replace("versionMin=", "").trim();
                        if (!string10.isEmpty()) {
                            try {
                                mod.versionMin = GameVersion.parse(string10);
                            } catch (Exception exception1) {
                                DebugLog.Mod.error("invalid versionMin: " + exception1.getMessage());
                                return null;
                            }
                        }
                    }
                }

                mod.bValid = true;
                return mod;
            } catch (Exception exception2) {
                ExceptionLogger.logException(exception2);
            }
        }

        return null;
    }

    public static final class Map {
        private String dir;
        private Texture thumb;
        private String title;
        private ArrayList<String> lotsDir;
        private String desc;
        private boolean bFixed2x;

        public String getDirectory() {
            return this.dir;
        }

        public void setDirectory(String string) {
            this.dir = string;
        }

        public Texture getThumbnail() {
            return this.thumb;
        }

        public void setThumbnail(Texture texture) {
            this.thumb = texture;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String string) {
            this.title = string;
        }

        public ArrayList<String> getLotDirectories() {
            return this.lotsDir;
        }

        public String getDescription() {
            return this.desc;
        }

        public void setDescription(String string) {
            this.desc = string;
        }

        public boolean isFixed2x() {
            return this.bFixed2x;
        }

        public void setFixed2x(boolean boolean0) {
            this.bFixed2x = boolean0;
        }
    }

    public static final class Mod {
        public String dir;
        public final File baseFile;
        public final File mediaFile;
        public final File actionGroupsFile;
        public final File animSetsFile;
        public final File animsXFile;
        private final ArrayList<String> posters = new ArrayList<>();
        public Texture tex;
        private ArrayList<String> require;
        private String name = "Unnamed Mod";
        private String desc = "";
        private String id;
        private String url;
        private String workshopID;
        private boolean bAvailableDone = false;
        private boolean available = true;
        private GameVersion versionMin;
        private GameVersion versionMax;
        private final ArrayList<ChooseGameInfo.PackFile> packs = new ArrayList<>();
        private final ArrayList<ChooseGameInfo.TileDef> tileDefs = new ArrayList<>();
        private boolean bRead = false;
        private boolean bValid = false;

        public Mod(String _dir) {
            this.dir = _dir;
            File file0 = new File(_dir).getAbsoluteFile();

            try {
                file0 = file0.getCanonicalFile();
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }

            this.baseFile = file0;
            this.mediaFile = new File(file0, "media");
            this.actionGroupsFile = new File(this.mediaFile, "actiongroups");
            this.animSetsFile = new File(this.mediaFile, "AnimSets");
            this.animsXFile = new File(this.mediaFile, "anims_X");
            File file1 = file0.getParentFile();
            if (file1 != null) {
                file1 = file1.getParentFile();
                if (file1 != null) {
                    this.workshopID = SteamWorkshop.instance.getIDFromItemInstallFolder(file1.getAbsolutePath());
                }
            }
        }

        public Texture getTexture() {
            if (this.tex == null) {
                String string = this.posters.isEmpty() ? null : this.posters.get(0);
                if (!StringUtils.isNullOrWhitespace(string)) {
                    this.tex = Texture.getSharedTexture(string);
                }

                if (this.tex == null || this.tex.isFailure()) {
                    if (Core.bDebug && this.tex == null) {
                        DebugLog.Mod.println("failed to load poster " + (string == null ? this.id : string));
                    }

                    this.tex = Texture.getWhite();
                }
            }

            return this.tex;
        }

        public void setTexture(Texture _tex) {
            this.tex = _tex;
        }

        public int getPosterCount() {
            return this.posters.size();
        }

        public String getPoster(int index) {
            return index >= 0 && index < this.posters.size() ? this.posters.get(index) : null;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String _name) {
            this.name = _name;
        }

        public String getDir() {
            return this.dir;
        }

        public String getDescription() {
            return this.desc;
        }

        public ArrayList<String> getRequire() {
            return this.require;
        }

        public void setRequire(ArrayList<String> _require) {
            this.require = _require;
        }

        public String getId() {
            return this.id;
        }

        public void setId(String _id) {
            this.id = _id;
        }

        public boolean isAvailable() {
            if (this.bAvailableDone) {
                return this.available;
            } else {
                this.bAvailableDone = true;
                if (!this.isAvailableSelf()) {
                    this.available = false;
                    return false;
                } else {
                    ChooseGameInfo.tempStrings.clear();
                    ChooseGameInfo.tempStrings.add(this.getId());
                    if (!this.isAvailableRequired(ChooseGameInfo.tempStrings)) {
                        this.available = false;
                        return false;
                    } else {
                        this.available = true;
                        return true;
                    }
                }
            }
        }

        private boolean isAvailableSelf() {
            GameVersion gameVersion = Core.getInstance().getGameVersion();
            return this.versionMin != null && this.versionMin.isGreaterThan(gameVersion)
                ? false
                : this.versionMax == null || !this.versionMax.isLessThan(gameVersion);
        }

        private boolean isAvailableRequired(ArrayList<String> arrayList) {
            if (this.require != null && !this.require.isEmpty()) {
                for (int int0 = 0; int0 < this.require.size(); int0++) {
                    String string = this.require.get(int0).trim();
                    if (!arrayList.contains(string)) {
                        arrayList.add(string);
                        ChooseGameInfo.Mod mod1 = ChooseGameInfo.getModDetails(string);
                        if (mod1 == null) {
                            return false;
                        }

                        if (!mod1.isAvailableSelf()) {
                            return false;
                        }

                        if (!mod1.isAvailableRequired(arrayList)) {
                            return false;
                        }
                    }
                }

                return true;
            } else {
                return true;
            }
        }

        @Deprecated
        public void setAvailable(boolean _available) {
        }

        public String getUrl() {
            return this.url == null ? "" : this.url;
        }

        public void setUrl(String _url) {
            if (_url.startsWith("http://theindiestone.com")
                || _url.startsWith("http://www.theindiestone.com")
                || _url.startsWith("http://pz-mods.net")
                || _url.startsWith("http://www.pz-mods.net")) {
                this.url = _url;
            }
        }

        public GameVersion getVersionMin() {
            return this.versionMin;
        }

        public GameVersion getVersionMax() {
            return this.versionMax;
        }

        public void addPack(String _name, int flags) {
            this.packs.add(new ChooseGameInfo.PackFile(_name, flags));
        }

        public void addTileDef(String _name, int fileNumber) {
            this.tileDefs.add(new ChooseGameInfo.TileDef(_name, fileNumber));
        }

        public ArrayList<ChooseGameInfo.PackFile> getPacks() {
            return this.packs;
        }

        public ArrayList<ChooseGameInfo.TileDef> getTileDefs() {
            return this.tileDefs;
        }

        public String getWorkshopID() {
            return this.workshopID;
        }
    }

    public static final class PackFile {
        public final String name;
        public final int flags;

        public PackFile(String string, int int0) {
            this.name = string;
            this.flags = int0;
        }
    }

    public static final class SpawnOrigin {
        public int x;
        public int y;
        public int w;
        public int h;

        public SpawnOrigin(int int0, int int1, int int2, int int3) {
            this.x = int0;
            this.y = int1;
            this.w = int2;
            this.h = int3;
        }
    }

    public static final class TileDef {
        public String name;
        public int fileNumber;

        public TileDef(String string, int int0) {
            this.name = string;
            this.fileNumber = int0;
        }
    }
}
