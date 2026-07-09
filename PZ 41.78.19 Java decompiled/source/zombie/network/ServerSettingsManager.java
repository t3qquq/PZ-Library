// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.network;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;

public class ServerSettingsManager {
    public static final ServerSettingsManager instance = new ServerSettingsManager();
    protected ArrayList<ServerSettings> settings = new ArrayList<>();
    protected ArrayList<String> suffixes = new ArrayList<>();

    public String getSettingsFolder() {
        return ZomboidFileSystem.instance.getCacheDir() + File.separator + "Server";
    }

    public String getNameInSettingsFolder(String name) {
        return this.getSettingsFolder() + File.separator + name;
    }

    public void readAllSettings() {
        this.settings.clear();
        File file = new File(this.getSettingsFolder());
        if (!file.exists()) {
            file.mkdirs();
        } else {
            Filter filter = new Filter<Path>() {
                public boolean accept(Path path) throws IOException {
                    String string = path.getFileName().toString();
                    return !Files.isDirectory(path)
                        && string.endsWith(".ini")
                        && !string.endsWith("_zombies.ini")
                        && ServerSettingsManager.this.isValidName(string.replace(".ini", ""));
                }
            };

            try (DirectoryStream directoryStream = Files.newDirectoryStream(file.toPath(), filter)) {
                for (Path path : directoryStream) {
                    ServerSettings serverSettings = new ServerSettings(path.getFileName().toString().replace(".ini", ""));
                    this.settings.add(serverSettings);
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }
    }

    public int getSettingsCount() {
        return this.settings.size();
    }

    public ServerSettings getSettingsByIndex(int index) {
        return index >= 0 && index < this.settings.size() ? this.settings.get(index) : null;
    }

    public boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        } else {
            return name.contains("/") || name.contains("\\") || name.contains(":") || name.contains(";") || name.contains("\"") || name.contains(".")
                ? false
                : !name.contains("_zombies");
        }
    }

    private boolean anyFilesExist(String string) {
        this.getSuffixes();

        for (int int0 = 0; int0 < this.suffixes.size(); int0++) {
            File file = new File(this.getSettingsFolder() + File.separator + string + this.suffixes.get(int0));
            if (file.exists()) {
                return true;
            }
        }

        return false;
    }

    public boolean isValidNewName(String newName) {
        return !this.isValidName(newName) ? false : !this.anyFilesExist(newName);
    }

    public ArrayList<String> getSuffixes() {
        if (this.suffixes.isEmpty()) {
            this.suffixes.add(".ini");
            this.suffixes.add("_SandboxVars.lua");
            this.suffixes.add("_spawnpoints.lua");
            this.suffixes.add("_spawnregions.lua");
            this.suffixes.add("_zombies.ini");
        }

        return this.suffixes;
    }
}
