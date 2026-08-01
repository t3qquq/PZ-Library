// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.popman;

import java.util.LinkedList;
import zombie.characters.IsoZombie;
import zombie.network.IConnection;

public class NetworkZombieList {
    final LinkedList<NetworkZombieList.NetworkZombie> networkZombies = new LinkedList<>();
    public Object lock = new Object();

    public NetworkZombieList.NetworkZombie getNetworkZombie(IConnection connection) {
        if (connection == null) {
            return null;
        }

        for (NetworkZombieList.NetworkZombie nz : this.networkZombies) {
            if (nz.connection == connection) {
                return nz;
            }
        }

        NetworkZombieList.NetworkZombie nz = new NetworkZombieList.NetworkZombie(connection);
        this.networkZombies.add(nz);
        return nz;
    }

    public static class NetworkZombie {
        public final LinkedList<IsoZombie> zombies = new LinkedList<>();
        final IConnection connection;

        public NetworkZombie(IConnection connection) {
            this.connection = connection;
        }
    }
}
