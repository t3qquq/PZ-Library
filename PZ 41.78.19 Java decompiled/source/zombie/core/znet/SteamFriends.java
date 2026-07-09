// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import zombie.Lua.LuaEventManager;
import zombie.core.textures.Texture;
import zombie.network.GameClient;
import zombie.network.GameServer;

public class SteamFriends {
    public static final int k_EPersonaStateOffline = 0;
    public static final int k_EPersonaStateOnline = 1;
    public static final int k_EPersonaStateBusy = 2;
    public static final int k_EPersonaStateAway = 3;
    public static final int k_EPersonaStateSnooze = 4;
    public static final int k_EPersonaStateLookingToTrade = 5;
    public static final int k_EPersonaStateLookingToPlay = 6;

    public static void init() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Init();
        }
    }

    public static void shutdown() {
        if (SteamUtils.isSteamModeEnabled()) {
            n_Shutdown();
        }
    }

    public static native void n_Init();

    public static native void n_Shutdown();

    public static native String GetPersonaName();

    public static native int GetFriendCount();

    public static native long GetFriendByIndex(int var0);

    public static native String GetFriendPersonaName(long var0);

    public static native int GetFriendPersonaState(long var0);

    public static native boolean InviteUserToGame(long var0, String var2);

    public static native void ActivateGameOverlay(String var0);

    public static native void ActivateGameOverlayToUser(String var0, long var1);

    public static native void ActivateGameOverlayToWebPage(String var0);

    public static native void SetPlayedWith(long var0);

    public static native void UpdateRichPresenceConnectionInfo(String var0, String var1);

    public static List<SteamFriend> GetFriendList() {
        ArrayList arrayList = new ArrayList();
        int int0 = GetFriendCount();

        for (int int1 = 0; int1 < int0; int1++) {
            long long0 = GetFriendByIndex(int1);
            String string = GetFriendPersonaName(long0);
            arrayList.add(new SteamFriend(string, long0));
        }

        return arrayList;
    }

    public static native int CreateSteamAvatar(long var0, ByteBuffer var2);

    private static void onStatusChangedCallback(long long0) {
        if (GameClient.bClient || GameServer.bServer) {
            LuaEventManager.triggerEvent("OnSteamFriendStatusChanged", Long.toString(long0));
        }
    }

    private static void onAvatarChangedCallback(long long0) {
        Texture.steamAvatarChanged(long0);
    }

    private static void onProfileNameChanged(long var0) {
    }
}
