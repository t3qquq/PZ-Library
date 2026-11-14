// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.iso.worldgen.biomes;

import java.util.Map;

public class BiomeType {
    public static Map<Class<?>, String> keys = Map.of(
        BiomeType.Hygrometry.class,
        "hygrometry",
        BiomeType.Landscape.class,
        "landscape",
        BiomeType.Plant.class,
        "plant",
        BiomeType.Bush.class,
        "bush",
        BiomeType.Temperature.class,
        "temperature",
        BiomeType.OreLevel.class,
        "ore_level"
    );

    public enum Bush {
        DRY,
        REGULAR,
        FAT,
        NONE;
    }

    public enum Hygrometry {
        FLOODING,
        RAIN,
        DRY,
        NONE;
    }

    public enum Landscape {
        LIGHT_FOREST,
        FOREST,
        PLAIN,
        NONE;
    }

    public enum OreLevel {
        VERY_LOW,
        LOW,
        MEDIUM,
        HIGH,
        VERY_HIGH,
        NONE;
    }

    public enum Plant {
        FLOWER,
        GRASS,
        NONE;
    }

    public enum Temperature {
        COLD,
        MEDIUM,
        HOT,
        NONE;
    }
}
