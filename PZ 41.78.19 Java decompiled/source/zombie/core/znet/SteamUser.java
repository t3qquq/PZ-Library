// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

public class SteamUser {
    public static long GetSteamID() {
        return SteamUtils.isSteamModeEnabled() ? n_GetSteamID() : 0L;
    }

    public static String GetSteamIDString() {
        if (SteamUtils.isSteamModeEnabled()) {
            long long0 = n_GetSteamID();
            return SteamUtils.convertSteamIDToString(long0);
        } else {
            return null;
        }
    }

    private static native long n_GetSteamID();
}
