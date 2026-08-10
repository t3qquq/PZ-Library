// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug;

/**
 * Created by LEMMYPC on 31/12/13.
 */
public enum DebugType {
    Packet,
    NetworkFileDebug,
    Network,
    General,
    Lua,
    Mod,
    Sound,
    Zombie,
    Combat,
    Objects,
    Fireplace,
    Radio,
    MapLoading,
    Clothing,
    Animation,
    Asset,
    Script,
    Shader,
    Input,
    Recipe,
    ActionSystem,
    IsoRegion,
    UnitTests,
    FileIO,
    Multiplayer,
    Ownership,
    Death,
    Damage,
    Statistic,
    Vehicle,
    Voice,
    Checksum;

    public static boolean Do(DebugType debugType) {
        return DebugLog.isEnabled(debugType);
    }
}
