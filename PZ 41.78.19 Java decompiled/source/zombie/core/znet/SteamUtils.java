// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import zombie.Lua.LuaEventManager;
import zombie.core.logger.ExceptionLogger;
import zombie.core.opengl.RenderThread;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.network.CoopSlave;
import zombie.network.GameServer;
import zombie.network.ServerWorldDatabase;

public class SteamUtils {
    private static boolean m_steamEnabled;
    private static boolean m_netEnabled;
    private static boolean m_floatingGamepadTextInputVisible = false;
    private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);
    private static final BigInteger MAX_ULONG = new BigInteger("FFFFFFFFFFFFFFFF", 16);
    private static List<IJoinRequestCallback> m_joinRequestCallbacks;
    public static final int k_EGamepadTextInputModeNormal = 0;
    public static final int k_EGamepadTextInputModePassword = 1;
    public static final int k_EGamepadTextInputLineModeSingleLine = 0;
    public static final int k_EGamepadTextInputLineModeMultipleLines = 1;
    public static final int k_EFloatingGamepadTextInputModeSingleLine = 0;
    public static final int k_EFloatingGamepadTextInputModeMultipleLines = 1;
    public static final int k_EFloatingGamepadTextInputModeEmail = 2;
    public static final int k_EFloatingGamepadTextInputModeNumeric = 3;

    private static void loadLibrary(String string) {
        DebugLog.log("Loading " + string + "...");
        System.loadLibrary(string);
    }

    public static void init() {
        m_steamEnabled = System.getProperty("zomboid.steam") != null && System.getProperty("zomboid.steam").equals("1");
        DebugLog.log("Loading networking libraries...");
        String string0 = "";
        if ("1".equals(System.getProperty("zomboid.debuglibs.znet"))) {
            DebugLog.log("***** Loading debug versions of libraries");
            string0 = "d";
        }

        try {
            if (System.getProperty("os.name").contains("OS X")) {
                if (m_steamEnabled) {
                    loadLibrary("steam_api");
                    loadLibrary("RakNet");
                    loadLibrary("ZNetJNI");
                } else {
                    loadLibrary("RakNet");
                    loadLibrary("ZNetNoSteam");
                }
            } else if (System.getProperty("os.name").startsWith("Win")) {
                if (System.getProperty("sun.arch.data.model").equals("64")) {
                    if (m_steamEnabled) {
                        loadLibrary("steam_api64");
                        loadLibrary("RakNet64" + string0);
                        loadLibrary("ZNetJNI64" + string0);
                    } else {
                        loadLibrary("RakNet64" + string0);
                        loadLibrary("ZNetNoSteam64" + string0);
                    }
                } else if (m_steamEnabled) {
                    loadLibrary("steam_api");
                    loadLibrary("RakNet32" + string0);
                    loadLibrary("ZNetJNI32" + string0);
                } else {
                    loadLibrary("RakNet32" + string0);
                    loadLibrary("ZNetNoSteam32" + string0);
                }
            } else if (System.getProperty("sun.arch.data.model").equals("64")) {
                if (m_steamEnabled) {
                    loadLibrary("steam_api");
                    loadLibrary("RakNet64");
                    loadLibrary("ZNetJNI64");
                } else {
                    loadLibrary("RakNet64");
                    loadLibrary("ZNetNoSteam64");
                }
            } else if (m_steamEnabled) {
                loadLibrary("steam_api");
                loadLibrary("RakNet32");
                loadLibrary("ZNetJNI32");
            } else {
                loadLibrary("RakNet32");
                loadLibrary("ZNetNoSteam32");
            }

            m_netEnabled = true;
        } catch (UnsatisfiedLinkError unsatisfiedLinkError) {
            m_steamEnabled = false;
            m_netEnabled = false;
            ExceptionLogger.logException(unsatisfiedLinkError);
            if (System.getProperty("os.name").startsWith("Win")) {
                DebugLog.log("One of the game's DLLs could not be loaded.");
                DebugLog.log("  Your system may be missing a DLL needed by the game's DLL.");
                DebugLog.log("  You may need to install the Microsoft Visual C++ Redistributable 2013.");
                File file = new File("../_CommonRedist/vcredist/");
                if (file.exists()) {
                    DebugLog.log("  This file is provided in " + file.getAbsolutePath());
                }
            }
        }

        String string1 = System.getProperty("zomboid.znetlog");
        if (m_netEnabled && string1 != null) {
            try {
                int int0 = Integer.parseInt(string1);
                ZNet.SetLogLevel(int0);
            } catch (NumberFormatException numberFormatException) {
                ExceptionLogger.logException(numberFormatException);
            }
        }

        if (!m_netEnabled) {
            DebugLog.log("Failed to load networking libraries");
        } else {
            ZNet.init();
            ZNet.SetLogLevel(DebugLog.getLogLevel(DebugType.Network));
            synchronized (RenderThread.m_contextLock) {
                if (!m_steamEnabled) {
                    DebugLog.log("SteamUtils started without Steam");
                } else if (n_Init(GameServer.bServer)) {
                    DebugLog.log("SteamUtils initialised successfully");
                } else {
                    DebugLog.log("Could not initialise SteamUtils");
                    m_steamEnabled = false;
                }
            }
        }

        m_joinRequestCallbacks = new ArrayList<>();
    }

    public static void shutdown() {
        if (m_steamEnabled) {
            n_Shutdown();
        }
    }

    public static void runLoop() {
        if (m_steamEnabled) {
            n_RunLoop();
        }
    }

    public static boolean isSteamModeEnabled() {
        return m_steamEnabled;
    }

    public static boolean isOverlayEnabled() {
        return m_steamEnabled && n_IsOverlayEnabled();
    }

    public static String convertSteamIDToString(long long0) {
        BigInteger bigInteger = BigInteger.valueOf(long0);
        if (bigInteger.signum() < 0) {
            bigInteger.add(TWO_64);
        }

        return bigInteger.toString();
    }

    public static boolean isValidSteamID(String string) {
        try {
            BigInteger bigInteger = new BigInteger(string);
            return bigInteger.signum() >= 0 && bigInteger.compareTo(MAX_ULONG) <= 0;
        } catch (NumberFormatException numberFormatException) {
            return false;
        }
    }

    public static long convertStringToSteamID(String string) {
        try {
            BigInteger bigInteger = new BigInteger(string);
            return bigInteger.signum() >= 0 && bigInteger.compareTo(MAX_ULONG) <= 0 ? bigInteger.longValue() : -1L;
        } catch (NumberFormatException numberFormatException) {
            return -1L;
        }
    }

    public static void addJoinRequestCallback(IJoinRequestCallback iJoinRequestCallback) {
        m_joinRequestCallbacks.add(iJoinRequestCallback);
    }

    public static void removeJoinRequestCallback(IJoinRequestCallback iJoinRequestCallback) {
        m_joinRequestCallbacks.remove(iJoinRequestCallback);
    }

    public static boolean isRunningOnSteamDeck() {
        return n_IsSteamRunningOnSteamDeck();
    }

    public static boolean showGamepadTextInput(boolean boolean1, boolean boolean0, String string0, int int0, String string1) {
        return n_ShowGamepadTextInput(boolean1 ? 1 : 0, boolean0 ? 1 : 0, string0, int0, string1);
    }

    public static boolean showFloatingGamepadTextInput(boolean boolean0, int int0, int int1, int int2, int int3) {
        if (m_floatingGamepadTextInputVisible) {
            return true;
        } else {
            m_floatingGamepadTextInputVisible = n_ShowFloatingGamepadTextInput(boolean0 ? 1 : 0, int0, int1, int2, int3);
            return m_floatingGamepadTextInputVisible;
        }
    }

    public static boolean isFloatingGamepadTextInputVisible() {
        return m_floatingGamepadTextInputVisible;
    }

    private static native boolean n_Init(boolean var0);

    private static native void n_Shutdown();

    private static native void n_RunLoop();

    private static native boolean n_IsOverlayEnabled();

    private static native boolean n_IsSteamRunningOnSteamDeck();

    private static native boolean n_ShowGamepadTextInput(int var0, int var1, String var2, int var3, String var4);

    private static native boolean n_ShowFloatingGamepadTextInput(int var0, int var1, int var2, int var3, int var4);

    private static void joinRequestCallback(long long0, String string0) {
        DebugLog.log("Got Join Request");

        for (IJoinRequestCallback iJoinRequestCallback : m_joinRequestCallbacks) {
            iJoinRequestCallback.onJoinRequest(long0, string0);
        }

        if (string0.contains("+connect ")) {
            String string1 = string0.substring(9);
            System.setProperty("args.server.connect", string1);
            LuaEventManager.triggerEvent("OnSteamGameJoin");
        }
    }

    private static int clientInitiateConnectionCallback(long long0) {
        if (CoopSlave.instance == null) {
            ServerWorldDatabase.LogonResult logonResult = ServerWorldDatabase.instance.authClient(long0);
            return logonResult.bAuthorized ? 0 : 1;
        } else {
            return !CoopSlave.instance.isHost(long0) && !CoopSlave.instance.isInvited(long0) ? 2 : 0;
        }
    }

    private static int validateOwnerCallback(long long0, long long1) {
        if (CoopSlave.instance != null) {
            return 0;
        } else {
            ServerWorldDatabase.LogonResult logonResult = ServerWorldDatabase.instance.authOwner(long0, long1);
            return logonResult.bAuthorized ? 0 : 1;
        }
    }

    private static void gamepadTextInputDismissedCallback(String string) {
        if (string == null) {
            DebugLog.log("null");
        } else {
            DebugLog.log(string);
        }
    }

    private static void floatingGamepadTextInputDismissedCallback() {
        m_floatingGamepadTextInputVisible = false;
    }
}
