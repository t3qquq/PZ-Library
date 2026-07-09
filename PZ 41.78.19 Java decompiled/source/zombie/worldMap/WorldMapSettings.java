// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;

public final class WorldMapSettings {
    public static int VERSION1 = 1;
    public static int VERSION = VERSION1;
    private static WorldMapSettings instance;
    final ArrayList<ConfigOption> m_options = new ArrayList<>();
    final WorldMapSettings.WorldMap mWorldMap = new WorldMapSettings.WorldMap();
    final WorldMapSettings.MiniMap mMiniMap = new WorldMapSettings.MiniMap();
    private int m_readVersion = 0;

    public static WorldMapSettings getInstance() {
        if (instance == null) {
            instance = new WorldMapSettings();
            instance.load();
        }

        return instance;
    }

    private BooleanConfigOption newOption(String string, boolean boolean0) {
        BooleanConfigOption booleanConfigOption = new BooleanConfigOption(string, boolean0);
        this.m_options.add(booleanConfigOption);
        return booleanConfigOption;
    }

    private DoubleConfigOption newOption(String string, double double0, double double1, double double2) {
        DoubleConfigOption doubleConfigOption = new DoubleConfigOption(string, double0, double1, double2);
        this.m_options.add(doubleConfigOption);
        return doubleConfigOption;
    }

    public ConfigOption getOptionByName(String string) {
        for (int int0 = 0; int0 < this.m_options.size(); int0++) {
            ConfigOption configOption = this.m_options.get(int0);
            if (configOption.getName().equals(string)) {
                return configOption;
            }
        }

        return null;
    }

    public int getOptionCount() {
        return this.m_options.size();
    }

    public ConfigOption getOptionByIndex(int int0) {
        return this.m_options.get(int0);
    }

    public void setBoolean(String string, boolean boolean0) {
        ConfigOption configOption = this.getOptionByName(string);
        if (configOption instanceof BooleanConfigOption) {
            ((BooleanConfigOption)configOption).setValue(boolean0);
        }
    }

    public boolean getBoolean(String string) {
        ConfigOption configOption = this.getOptionByName(string);
        return configOption instanceof BooleanConfigOption ? ((BooleanConfigOption)configOption).getValue() : false;
    }

    public void setDouble(String string, double double0) {
        ConfigOption configOption = this.getOptionByName(string);
        if (configOption instanceof DoubleConfigOption) {
            ((DoubleConfigOption)configOption).setValue(double0);
        }
    }

    public double getDouble(String string, double double0) {
        ConfigOption configOption = this.getOptionByName(string);
        return configOption instanceof DoubleConfigOption ? ((DoubleConfigOption)configOption).getValue() : double0;
    }

    public int getFileVersion() {
        return this.m_readVersion;
    }

    public void save() {
        String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini");
        ConfigFile configFile = new ConfigFile();
        configFile.write(string, VERSION, this.m_options);
        this.m_readVersion = VERSION;
    }

    public void load() {
        this.m_readVersion = 0;
        String string = ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini");
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(string)) {
            this.m_readVersion = configFile.getVersion();
            if (this.m_readVersion >= VERSION1 && this.m_readVersion <= VERSION) {
                for (int int0 = 0; int0 < configFile.getOptions().size(); int0++) {
                    ConfigOption configOption0 = configFile.getOptions().get(int0);

                    try {
                        ConfigOption configOption1 = this.getOptionByName(configOption0.getName());
                        if (configOption1 != null) {
                            configOption1.parse(configOption0.getValueAsString());
                        }
                    } catch (Exception exception) {
                    }
                }
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            instance.m_options.clear();
            instance = null;
        }
    }

    public class MiniMap {
        public DoubleConfigOption Zoom = WorldMapSettings.this.newOption("MiniMap.Zoom", 0.0, 24.0, 19.0);
        public BooleanConfigOption Isometric = WorldMapSettings.this.newOption("MiniMap.Isometric", true);
        public BooleanConfigOption ShowSymbols = WorldMapSettings.this.newOption("MiniMap.ShowSymbols", false);
        public BooleanConfigOption StartVisible = WorldMapSettings.this.newOption("MiniMap.StartVisible", true);
    }

    public final class WorldMap {
        public DoubleConfigOption CenterX = WorldMapSettings.this.newOption("WorldMap.CenterX", -Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
        public DoubleConfigOption CenterY = WorldMapSettings.this.newOption("WorldMap.CenterY", -Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
        public DoubleConfigOption Zoom = WorldMapSettings.this.newOption("WorldMap.Zoom", 0.0, 24.0, 0.0);
        public BooleanConfigOption Isometric = WorldMapSettings.this.newOption("WorldMap.Isometric", true);
        public BooleanConfigOption ShowSymbolsUI = WorldMapSettings.this.newOption("WorldMap.ShowSymbolsUI", true);
    }
}
