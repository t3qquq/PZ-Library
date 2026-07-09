// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.util.LinkedList;
import zombie.characters.IsoZombie;
import zombie.core.raknet.UdpConnection;

public class NetworkZombieList {
    final LinkedList<NetworkZombieList.NetworkZombie> networkZombies = new LinkedList<>();
    public Object lock = new Object();

    public NetworkZombieList.NetworkZombie getNetworkZombie(UdpConnection udpConnection) {
        if (udpConnection == null) {
            return null;
        } else {
            for (NetworkZombieList.NetworkZombie networkZombie0 : this.networkZombies) {
                if (networkZombie0.connection == udpConnection) {
                    return networkZombie0;
                }
            }

            NetworkZombieList.NetworkZombie networkZombie1 = new NetworkZombieList.NetworkZombie(udpConnection);
            this.networkZombies.add(networkZombie1);
            return networkZombie1;
        }
    }

    public static class NetworkZombie {
        final LinkedList<IsoZombie> zombies = new LinkedList<>();
        final UdpConnection connection;

        public NetworkZombie(UdpConnection udpConnection) {
            this.connection = udpConnection;
        }
    }
}
