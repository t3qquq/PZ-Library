// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import zombie.UsedFromLua;
import zombie.ZomboidFileSystem;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.config.DoubleConfigOption;
import zombie.core.Core;

@UsedFromLua
public final class WorldMapSettings {
    public static final int VERSION1 = 1;
    public static final int VERSION = 1;
    private static WorldMapSettings instance;
    final ArrayList<ConfigOption> options = new ArrayList<>();
    final WorldMapSettings.WorldMap mWorldMap = new WorldMapSettings.WorldMap();
    final WorldMapSettings.MiniMap mMiniMap = new WorldMapSettings.MiniMap();
    private int readVersion;

    public static WorldMapSettings getInstance() {
        if (instance == null) {
            instance = new WorldMapSettings();
            instance.load();
        }

        return instance;
    }

    private BooleanConfigOption newOption(String name, boolean defaultValue) {
        BooleanConfigOption option = new BooleanConfigOption(name, defaultValue);
        this.options.add(option);
        return option;
    }

    private DoubleConfigOption newOption(String name, double min, double max, double defaultValue) {
        DoubleConfigOption option = new DoubleConfigOption(name, min, max, defaultValue);
        this.options.add(option);
        return option;
    }

    public ConfigOption getOptionByName(String name) {
        for (int i = 0; i < this.options.size(); i++) {
            ConfigOption setting = this.options.get(i);
            if (setting.getName().equals(name)) {
                return setting;
            }
        }

        return null;
    }

    public int getOptionCount() {
        return this.options.size();
    }

    public ConfigOption getOptionByIndex(int index) {
        return this.options.get(index);
    }

    public void setBoolean(String name, boolean value) {
        if (this.getOptionByName(name) instanceof BooleanConfigOption booleanConfigOption) {
            booleanConfigOption.setValue(value);
        }
    }

    public boolean getBoolean(String name) {
        return this.getOptionByName(name) instanceof BooleanConfigOption booleanConfigOption ? booleanConfigOption.getValue() : false;
    }

    public void setDouble(String name, double value) {
        if (this.getOptionByName(name) instanceof DoubleConfigOption doubleConfigOption) {
            doubleConfigOption.setValue(value);
        }
    }

    public double getDouble(String name, double defaultValue) {
        return this.getOptionByName(name) instanceof DoubleConfigOption doubleConfigOption ? doubleConfigOption.getValue() : defaultValue;
    }

    public int getFileVersion() {
        return this.readVersion;
    }

    public void save() {
        if (!Core.getInstance().isNoSave()) {
            String fileName = ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini");
            ConfigFile configFile = new ConfigFile();
            configFile.write(fileName, 1, this.options);
            this.readVersion = 1;
        }
    }

    public void load() {
        this.readVersion = 0;
        String fileName = ZomboidFileSystem.instance.getFileNameInCurrentSave("InGameMap.ini");
        ConfigFile configFile = new ConfigFile();
        if (configFile.read(fileName)) {
            this.readVersion = configFile.getVersion();
            if (this.readVersion >= 1 && this.readVersion <= 1) {
                for (int i = 0; i < configFile.getOptions().size(); i++) {
                    ConfigOption configOption = configFile.getOptions().get(i);

                    try {
                        ConfigOption myOption = this.getOptionByName(configOption.getName());
                        if (myOption != null) {
                            myOption.parse(configOption.getValueAsString());
                        }
                    } catch (Exception var6) {
                    }
                }
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            instance.options.clear();
            instance = null;
        }
    }

    public class MiniMap {
        public DoubleConfigOption zoom = WorldMapSettings.this.newOption("MiniMap.Zoom", 0.0, 24.0, 19.0);
        public BooleanConfigOption isometric = WorldMapSettings.this.newOption("MiniMap.Isometric", true);
        public BooleanConfigOption showSymbols = WorldMapSettings.this.newOption("MiniMap.ShowSymbols", false);
        public BooleanConfigOption startVisible = WorldMapSettings.this.newOption("MiniMap.StartVisible", true);
        public BooleanConfigOption terrainImage = WorldMapSettings.this.newOption("MiniMap.TerrainImage", false);
    }

    public final class WorldMap {
        public DoubleConfigOption centerX = WorldMapSettings.this.newOption("WorldMap.CenterX", -Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
        public DoubleConfigOption centerY = WorldMapSettings.this.newOption("WorldMap.CenterY", -Double.MAX_VALUE, Double.MAX_VALUE, 0.0);
        public BooleanConfigOption highlightStreet = WorldMapSettings.this.newOption("WorldMap.HighlightStreet", true);
        public BooleanConfigOption isometric = WorldMapSettings.this.newOption("WorldMap.Isometric", true);
        public BooleanConfigOption largeStreetLabel = WorldMapSettings.this.newOption("WorldMap.LargeStreetLabel", true);
        public BooleanConfigOption placeNames = WorldMapSettings.this.newOption("WorldMap.PlaceNames", true);
        public BooleanConfigOption players = WorldMapSettings.this.newOption("WorldMap.Players", true);
        public BooleanConfigOption showPrintMedia = WorldMapSettings.this.newOption("WorldMap.ShowPrintMedia", false);
        public BooleanConfigOption showStreetNames = WorldMapSettings.this.newOption("WorldMap.ShowStreetNames", true);
        public BooleanConfigOption showSymbolsUi = WorldMapSettings.this.newOption("WorldMap.ShowSymbolsUI", true);
        public BooleanConfigOption symbols = WorldMapSettings.this.newOption("WorldMap.Symbols", true);
        public BooleanConfigOption terrainImage = WorldMapSettings.this.newOption("WorldMap.TerrainImage", false);
        public DoubleConfigOption zoom = WorldMapSettings.this.newOption("WorldMap.Zoom", 0.0, 24.0, 0.0);
    }
}
