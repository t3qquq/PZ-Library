// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import java.util.ArrayList;
import java.util.List;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.network.Server;

public class ServerBrowser {
    private static IServerBrowserCallback m_callbackInterface = null;

    public static boolean init() {
        boolean boolean0 = false;
        if (SteamUtils.isSteamModeEnabled()) {
            boolean0 = n_Init();
        }

        return boolean0;
    }

    public static void shutdown() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Shutdown();
        }
    }

    public static void RefreshInternetServers() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_RefreshInternetServers();
        }
    }

    public static int GetServerCount() {
        int int0 = 0;
        if (SteamUtils.isSteamModeEnabled()) {
            int0 = n_GetServerCount();
        }

        return int0;
    }

    public static GameServerDetails GetServerDetails(int int0) {
        GameServerDetails gameServerDetails = null;
        if (SteamUtils.isSteamModeEnabled()) {
            gameServerDetails = n_GetServerDetails(int0);
        }

        return gameServerDetails;
    }

    public static void Release() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Release();
        }
    }

    public static boolean IsRefreshing() {
        boolean boolean0 = false;
        if (SteamUtils.isSteamModeEnabled()) {
            boolean0 = n_IsRefreshing();
        }

        return boolean0;
    }

    public static boolean QueryServer(String string, int int0) {
        boolean boolean0 = false;
        if (SteamUtils.isSteamModeEnabled()) {
            boolean0 = n_QueryServer(string, int0);
        }

        return boolean0;
    }

    public static GameServerDetails GetServerDetails(String string, int int0) {
        GameServerDetails gameServerDetails = null;
        if (SteamUtils.isSteamModeEnabled()) {
            gameServerDetails = n_GetServerDetails(string, int0);
        }

        return gameServerDetails;
    }

    public static void ReleaseServerQuery(String string, int int0) {
        if (SteamUtils.isSteamModeEnabled()) {
            n_ReleaseServerQuery(string, int0);
        }
    }

    public static List<GameServerDetails> GetServerList() {
        ArrayList arrayList = new ArrayList();
        if (SteamUtils.isSteamModeEnabled()) {
            try {
                while (IsRefreshing()) {
                    Thread.sleep(100L);
                    SteamUtils.runLoop();
                }
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }

            for (int int0 = 0; int0 < GetServerCount(); int0++) {
                GameServerDetails gameServerDetails = GetServerDetails(int0);
                if (gameServerDetails.steamId != 0L) {
                    arrayList.add(gameServerDetails);
                }
            }
        }

        return arrayList;
    }

    public static GameServerDetails GetServerDetailsSync(String string, int int0) {
        GameServerDetails gameServerDetails = null;
        if (SteamUtils.isSteamModeEnabled()) {
            gameServerDetails = GetServerDetails(string, int0);
            if (gameServerDetails == null) {
                QueryServer(string, int0);

                try {
                    while (gameServerDetails == null) {
                        Thread.sleep(100L);
                        SteamUtils.runLoop();
                        gameServerDetails = GetServerDetails(string, int0);
                    }
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }
        }

        return gameServerDetails;
    }

    public static boolean RequestServerRules(String string, int int0) {
        return n_RequestServerRules(string, int0);
    }

    public static void setCallbackInterface(IServerBrowserCallback iServerBrowserCallback) {
        m_callbackInterface = iServerBrowserCallback;
    }

    private static native boolean n_Init();

    private static native void n_Shutdown();

    private static native void n_RefreshInternetServers();

    private static native int n_GetServerCount();

    private static native GameServerDetails n_GetServerDetails(int var0);

    private static native void n_Release();

    private static native boolean n_IsRefreshing();

    private static native boolean n_QueryServer(String var0, int var1);

    private static native GameServerDetails n_GetServerDetails(String var0, int var1);

    private static native void n_ReleaseServerQuery(String var0, int var1);

    private static native boolean n_RequestServerRules(String var0, int var1);

    private static void onServerRespondedCallback(int int0) {
        if (m_callbackInterface != null) {
            m_callbackInterface.OnServerResponded(int0);
        }

        LuaEventManager.triggerEvent("OnSteamServerResponded", int0);
    }

    private static void onServerFailedToRespondCallback(int int0) {
        if (m_callbackInterface != null) {
            m_callbackInterface.OnServerFailedToRespond(int0);
        }
    }

    private static void onRefreshCompleteCallback() {
        if (m_callbackInterface != null) {
            m_callbackInterface.OnRefreshComplete();
        }

        LuaEventManager.triggerEvent("OnSteamRefreshInternetServers");
    }

    private static void onServerRespondedCallback(String string, int int0) {
        if (m_callbackInterface != null) {
            m_callbackInterface.OnServerResponded(string, int0);
        }

        GameServerDetails gameServerDetails = GetServerDetails(string, int0);
        if (gameServerDetails != null) {
            Server server = new Server();
            server.setName(gameServerDetails.name);
            server.setDescription("");
            server.setSteamId(Long.toString(gameServerDetails.steamId));
            server.setPing(Integer.toString(gameServerDetails.ping));
            server.setPlayers(Integer.toString(gameServerDetails.numPlayers));
            server.setMaxPlayers(Integer.toString(gameServerDetails.maxPlayers));
            server.setOpen(true);
            if (gameServerDetails.tags.contains("hidden")) {
                server.setOpen(false);
            }

            server.setIp(gameServerDetails.address);
            server.setPort(Integer.toString(gameServerDetails.port));
            server.setMods("");
            if (!gameServerDetails.tags.replace("hidden", "").replace("hosted", "").replace(";", "").isEmpty()) {
                server.setMods(gameServerDetails.tags.replace(";hosted", "").replace("hidden", ""));
            }

            server.setHosted(gameServerDetails.tags.endsWith(";hosted"));
            server.setVersion("");
            server.setLastUpdate(1);
            server.setPasswordProtected(gameServerDetails.passwordProtected);
            ReleaseServerQuery(string, int0);
            LuaEventManager.triggerEvent("OnSteamServerResponded2", string, (double)int0, server);
        }
    }

    private static void onServerFailedToRespondCallback(String string, int int0) {
        if (m_callbackInterface != null) {
            m_callbackInterface.OnServerFailedToRespond(string, int0);
        }

        LuaEventManager.triggerEvent("OnSteamServerFailedToRespond2", string, (double)int0);
    }

    private static void onRulesRefreshComplete(String string, int int0, String[] strings) {
        if (m_callbackInterface != null) {
            m_callbackInterface.OnSteamRulesRefreshComplete(string, int0);
        }

        KahluaTable table = LuaManager.platform.newTable();

        for (byte byte0 = 0; byte0 < strings.length; byte0 += 2) {
            table.rawset(strings[byte0], strings[byte0 + 1]);
        }

        LuaEventManager.triggerEvent("OnSteamRulesRefreshComplete", string, (double)int0, table);
    }
}
