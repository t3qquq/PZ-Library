// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.network.ServerOptions;

public final class WorldMapRemotePlayer {
    private short changeCount = 0;
    private final short OnlineID;
    private String username = "???";
    private String forename = "???";
    private String surname = "???";
    private String accessLevel = "None";
    private float x;
    private float y;
    private boolean invisible = false;
    private boolean bHasFullData = false;

    public WorldMapRemotePlayer(short short0) {
        this.OnlineID = short0;
    }

    public void setPlayer(IsoPlayer player) {
        boolean boolean0 = false;
        if (!this.username.equals(player.username)) {
            this.username = player.username;
            boolean0 = true;
        }

        if (!this.forename.equals(player.getDescriptor().getForename())) {
            this.forename = player.getDescriptor().getForename();
            boolean0 = true;
        }

        if (!this.surname.equals(player.getDescriptor().getSurname())) {
            this.surname = player.getDescriptor().getSurname();
            boolean0 = true;
        }

        if (!this.accessLevel.equals(player.accessLevel)) {
            this.accessLevel = player.accessLevel;
            boolean0 = true;
        }

        this.x = player.x;
        this.y = player.y;
        if (this.invisible != player.isInvisible()) {
            this.invisible = player.isInvisible();
            boolean0 = true;
        }

        if (boolean0) {
            this.changeCount++;
        }
    }

    public void setFullData(short short0, String string0, String string1, String string2, String string3, float float0, float float1, boolean boolean0) {
        this.changeCount = short0;
        this.username = string0;
        this.forename = string1;
        this.surname = string2;
        this.accessLevel = string3;
        this.x = float0;
        this.y = float1;
        this.invisible = boolean0;
        this.bHasFullData = true;
    }

    public void setPosition(float float0, float float1) {
        this.x = float0;
        this.y = float1;
    }

    public short getOnlineID() {
        return this.OnlineID;
    }

    public String getForename() {
        return this.forename;
    }

    public String getSurname() {
        return this.surname;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public short getChangeCount() {
        return this.changeCount;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public boolean hasFullData() {
        return this.bHasFullData;
    }

    public String getUsername(Boolean boolean0) {
        String string = this.username;
        if (boolean0 && GameClient.bClient && ServerOptions.instance.ShowFirstAndLastName.getValue() && this.isAccessLevel("None")) {
            string = this.forename + " " + this.surname;
            if (ServerOptions.instance.DisplayUserName.getValue()) {
                string = string + " (" + this.username + ")";
            }
        }

        return string;
    }

    public String getUsername() {
        return this.getUsername(false);
    }

    public String getAccessLevel() {
        String string = this.accessLevel;

        return switch (string) {
            case "admin" -> "Admin";
            case "moderator" -> "Moderator";
            case "overseer" -> "Overseer";
            case "gm" -> "GM";
            case "observer" -> "Observer";
            default -> "None";
        };
    }

    public boolean isAccessLevel(String string) {
        return this.getAccessLevel().equalsIgnoreCase(string);
    }
}
