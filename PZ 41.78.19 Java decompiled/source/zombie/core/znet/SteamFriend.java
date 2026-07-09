// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

import zombie.core.textures.Texture;

/**
 * Created by Gennadiy on 11.06.2015.
 */
public class SteamFriend {
    private String name = "";
    private long steamID;
    private String steamIDString;

    public SteamFriend() {
    }

    public SteamFriend(String _name, long _steamID) {
        this.steamID = _steamID;
        this.steamIDString = SteamUtils.convertSteamIDToString(_steamID);
        this.name = _name;
    }

    public String getName() {
        return this.name;
    }

    public String getSteamID() {
        return this.steamIDString;
    }

    public Texture getAvatar() {
        return Texture.getSteamAvatar(this.steamID);
    }

    public String getState() {
        int int0 = SteamFriends.GetFriendPersonaState(this.steamID);
        switch (int0) {
            case 0:
                return "Offline";
            case 1:
                return "Online";
            case 2:
                return "Busy";
            case 3:
                return "Away";
            case 4:
                return "Snooze";
            case 5:
                return "LookingToTrade";
            case 6:
                return "LookingToPlay";
            default:
                return "Unknown";
        }
    }
}
