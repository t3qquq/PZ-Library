// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.znet;

public class GameServerDetails {
    public String address;
    public int port;
    public long steamId;
    public String name;
    public String gamedir;
    public String map;
    public String gameDescription;
    public String tags;
    public int ping;
    public int numPlayers;
    public int maxPlayers;
    public boolean passwordProtected;
    public int version;

    public GameServerDetails() {
    }

    public GameServerDetails(
        String string0,
        int int0,
        long long0,
        String string1,
        String string2,
        String string3,
        String string4,
        String string5,
        int int1,
        int int2,
        int int3,
        boolean boolean0,
        int int4
    ) {
        this.address = string0;
        this.port = int0;
        this.steamId = long0;
        this.name = string1;
        this.gamedir = string2;
        this.map = string3;
        this.gameDescription = string4;
        this.tags = string5;
        this.ping = int1;
        this.numPlayers = int2;
        this.maxPlayers = int3;
        this.passwordProtected = boolean0;
        this.version = int4;
    }
}
