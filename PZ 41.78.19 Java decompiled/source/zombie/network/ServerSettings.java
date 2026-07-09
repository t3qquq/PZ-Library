// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.File;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.core.Translator;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.profanity.ProfanityFilter;
import zombie.util.StringUtils;

public class ServerSettings {
    protected String name;
    protected ServerOptions serverOptions;
    protected SandboxOptions sandboxOptions;
    protected ArrayList<SpawnRegions.Region> spawnRegions;
    protected ArrayList<SpawnRegions.Profession> spawnPoints;
    private boolean valid = true;
    private String errorMsg = null;

    public ServerSettings(String _name) {
        ZomboidFileSystem.instance.validatePrefix(_name);
        this.errorMsg = null;
        this.valid = true;
        this.name = _name;
        String string = ProfanityFilter.getInstance().validateString(_name, true, true, true);
        if (!StringUtils.isNullOrEmpty(string)) {
            this.errorMsg = Translator.getText("UI_BadWordCheck", string);
            this.valid = false;
        }
    }

    public String getName() {
        return this.name;
    }

    public void resetToDefault() {
        this.serverOptions = new ServerOptions();
        this.sandboxOptions = new SandboxOptions();
        this.spawnRegions = new SpawnRegions().getDefaultServerRegions();
        this.spawnPoints = null;
    }

    public boolean loadFiles() {
        this.serverOptions = new ServerOptions();
        this.serverOptions.loadServerTextFile(this.name);
        this.sandboxOptions = new SandboxOptions();
        this.sandboxOptions.loadServerLuaFile(this.name);
        this.sandboxOptions.loadServerZombiesFile(this.name);
        SpawnRegions spawnRegionsx = new SpawnRegions();
        this.spawnRegions = spawnRegionsx.loadRegionsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnregions.lua"));
        if (this.spawnRegions == null) {
            this.spawnRegions = spawnRegionsx.getDefaultServerRegions();
        }

        this.spawnPoints = spawnRegionsx.loadPointsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnpoints.lua"));
        return true;
    }

    public boolean saveFiles() {
        if (this.serverOptions == null) {
            return false;
        } else {
            this.serverOptions.saveServerTextFile(this.name);
            this.sandboxOptions.saveServerLuaFile(this.name);
            if (this.spawnRegions != null) {
                new SpawnRegions().saveRegionsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnregions.lua"), this.spawnRegions);
            }

            if (this.spawnPoints != null) {
                new SpawnRegions().savePointsFile(ServerSettingsManager.instance.getNameInSettingsFolder(this.name + "_spawnpoints.lua"), this.spawnPoints);
            }

            this.tryDeleteFile(this.name + "_zombies.ini");
            return true;
        }
    }

    private boolean tryDeleteFile(String string) {
        try {
            File file = new File(ServerSettingsManager.instance.getNameInSettingsFolder(string));
            if (file.exists()) {
                DebugLog.log("deleting " + file.getAbsolutePath());
                file.delete();
            }

            return true;
        } catch (Exception exception) {
            ExceptionLogger.logException(exception);
            return false;
        }
    }

    public boolean deleteFiles() {
        this.tryDeleteFile(this.name + ".ini");
        this.tryDeleteFile(this.name + "_SandboxVars.lua");
        this.tryDeleteFile(this.name + "_spawnregions.lua");
        this.tryDeleteFile(this.name + "_spawnpoints.lua");
        this.tryDeleteFile(this.name + "_zombies.ini");
        return true;
    }

    public boolean duplicateFiles(String newName) {
        if (!ServerSettingsManager.instance.isValidNewName(newName)) {
            return false;
        } else {
            ServerSettings serverSettings = new ServerSettings(this.name);
            serverSettings.loadFiles();
            if (serverSettings.spawnRegions != null) {
                for (SpawnRegions.Region region : serverSettings.spawnRegions) {
                    if (region.serverfile != null && region.serverfile.equals(this.name + "_spawnpoints.lua")) {
                        region.serverfile = newName + "_spawnpoints.lua";
                    }
                }
            }

            serverSettings.name = newName;
            serverSettings.saveFiles();
            return true;
        }
    }

    public boolean rename(String newName) {
        if (!ServerSettingsManager.instance.isValidNewName(newName)) {
            return false;
        } else {
            this.loadFiles();
            this.deleteFiles();
            if (this.spawnRegions != null) {
                for (SpawnRegions.Region region : this.spawnRegions) {
                    if (region.serverfile != null && region.serverfile.equals(this.name + "_spawnpoints.lua")) {
                        region.serverfile = newName + "_spawnpoints.lua";
                    }
                }
            }

            this.name = newName;
            this.saveFiles();
            return true;
        }
    }

    public ServerOptions getServerOptions() {
        return this.serverOptions;
    }

    public SandboxOptions getSandboxOptions() {
        return this.sandboxOptions;
    }

    public int getNumSpawnRegions() {
        return this.spawnRegions.size();
    }

    public String getSpawnRegionName(int index) {
        return this.spawnRegions.get(index).name;
    }

    public String getSpawnRegionFile(int index) {
        SpawnRegions.Region region = this.spawnRegions.get(index);
        return region.file != null ? region.file : region.serverfile;
    }

    public void clearSpawnRegions() {
        this.spawnRegions.clear();
    }

    public void addSpawnRegion(String _name, String file) {
        if (_name != null && file != null) {
            SpawnRegions.Region region = new SpawnRegions.Region();
            region.name = _name;
            if (file.startsWith("media")) {
                region.file = file;
            } else {
                region.serverfile = file;
            }

            this.spawnRegions.add(region);
        } else {
            throw new NullPointerException();
        }
    }

    public void removeSpawnRegion(int index) {
        this.spawnRegions.remove(index);
    }

    public KahluaTable loadSpawnPointsFile(String file) {
        SpawnRegions spawnRegionsx = new SpawnRegions();
        return spawnRegionsx.loadPointsTable(ServerSettingsManager.instance.getNameInSettingsFolder(file));
    }

    public boolean saveSpawnPointsFile(String file, KahluaTable professionsTable) {
        SpawnRegions spawnRegionsx = new SpawnRegions();
        return spawnRegionsx.savePointsTable(ServerSettingsManager.instance.getNameInSettingsFolder(file), professionsTable);
    }

    public boolean isValid() {
        return this.valid;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }
}
