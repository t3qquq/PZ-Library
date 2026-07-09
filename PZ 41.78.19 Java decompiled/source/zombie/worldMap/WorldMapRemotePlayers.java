// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.characters.IsoPlayer;

public final class WorldMapRemotePlayers {
    public static final WorldMapRemotePlayers instance = new WorldMapRemotePlayers();
    private final ArrayList<WorldMapRemotePlayer> playerList = new ArrayList<>();
    private final HashMap<Short, WorldMapRemotePlayer> playerLookup = new HashMap<>();

    public WorldMapRemotePlayer getOrCreatePlayerByID(short short0) {
        WorldMapRemotePlayer worldMapRemotePlayer = this.playerLookup.get(short0);
        if (worldMapRemotePlayer == null) {
            worldMapRemotePlayer = new WorldMapRemotePlayer(short0);
            this.playerList.add(worldMapRemotePlayer);
            this.playerLookup.put(short0, worldMapRemotePlayer);
        }

        return worldMapRemotePlayer;
    }

    public WorldMapRemotePlayer getOrCreatePlayer(IsoPlayer player) {
        return this.getOrCreatePlayerByID(player.OnlineID);
    }

    public WorldMapRemotePlayer getPlayerByID(short short0) {
        return this.playerLookup.get(short0);
    }

    public ArrayList<WorldMapRemotePlayer> getPlayers() {
        return this.playerList;
    }

    public void removePlayerByID(short short0) {
        this.playerList.removeIf(worldMapRemotePlayer -> worldMapRemotePlayer.getOnlineID() == short0);
        this.playerLookup.remove(short0);
    }

    public void Reset() {
        this.playerList.clear();
        this.playerLookup.clear();
    }
}
