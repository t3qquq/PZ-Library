// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

public class SystemDisabler {
    public static boolean doCharacterStats = true;
    public static boolean doZombieCreation = true;
    public static boolean doSurvivorCreation = false;
    public static boolean doPlayerCreation = true;
    public static boolean doOverridePOVCharacters = true;
    public static boolean doVehiclesEverywhere = false;
    public static boolean doWorldSyncEnable = false;
    public static boolean doObjectStateSyncEnable = false;
    private static boolean doAllowDebugConnections = false;
    private static boolean doOverrideServerConnectDebugCheck = false;
    private static boolean doHighFriction = false;
    private static boolean doVehicleLowRider = false;
    public static boolean doEnableDetectOpenGLErrorsInTexture = false;
    public static boolean doVehiclesWithoutTextures = false;
    public static boolean zombiesDontAttack = false;
    public static boolean zombiesSwitchOwnershipEachUpdate = false;
    private static boolean doMainLoopDealWithNetData = true;
    public static boolean useNetworkCharacter = false;
    private static boolean bEnableAdvancedSoundOptions = false;
    public static boolean doKickInDebug = true;

    public static void setDoCharacterStats(boolean bDo) {
        doCharacterStats = bDo;
    }

    public static void setDoZombieCreation(boolean bDo) {
        doZombieCreation = bDo;
    }

    public static void setDoSurvivorCreation(boolean bDo) {
        doSurvivorCreation = bDo;
    }

    public static void setDoPlayerCreation(boolean bDo) {
        doPlayerCreation = bDo;
    }

    public static void setOverridePOVCharacters(boolean bDo) {
        doOverridePOVCharacters = bDo;
    }

    public static void setVehiclesEverywhere(boolean bDo) {
        doVehiclesEverywhere = bDo;
    }

    public static void setWorldSyncEnable(boolean bDo) {
        doWorldSyncEnable = bDo;
    }

    public static void setObjectStateSyncEnable(boolean bDo) {
        doObjectStateSyncEnable = bDo;
    }

    public static boolean getAllowDebugConnections() {
        return doAllowDebugConnections;
    }

    public static boolean getOverrideServerConnectDebugCheck() {
        return doOverrideServerConnectDebugCheck;
    }

    public static boolean getdoHighFriction() {
        return doHighFriction;
    }

    public static boolean getdoVehicleLowRider() {
        return doVehicleLowRider;
    }

    public static boolean getDoMainLoopDealWithNetData() {
        return doMainLoopDealWithNetData;
    }

    public static void setEnableAdvancedSoundOptions(boolean enable) {
        bEnableAdvancedSoundOptions = enable;
    }

    public static boolean getEnableAdvancedSoundOptions() {
        return bEnableAdvancedSoundOptions;
    }

    public static void Reset() {
        doCharacterStats = true;
        doZombieCreation = true;
        doSurvivorCreation = false;
        doPlayerCreation = true;
        doOverridePOVCharacters = true;
        doVehiclesEverywhere = false;
        doAllowDebugConnections = false;
        doWorldSyncEnable = false;
        doObjectStateSyncEnable = false;
        doMainLoopDealWithNetData = true;
        bEnableAdvancedSoundOptions = false;
    }
}
