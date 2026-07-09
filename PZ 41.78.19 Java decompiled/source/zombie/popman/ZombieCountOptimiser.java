// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.popman;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.network.GameClient;
import zombie.network.MPStatistics;

public class ZombieCountOptimiser {
    private static int zombieCountForDelete = 0;
    public static final int maxZombieCount = 500;
    public static final int minZombieDistance = 20;
    public static final ArrayList<IsoZombie> zombiesForDelete = new ArrayList<>();

    public static void startCount() {
        zombieCountForDelete = (int)(1.0F * Math.max(0, GameClient.IDToZombieMap.values().length - 500));
    }

    public static void incrementZombie(IsoZombie zombie0) {
        if (zombieCountForDelete > 0 && Rand.Next(10) == 0 && zombie0.canBeDeletedUnnoticed(20.0F) && !zombie0.isReanimatedPlayer()) {
            synchronized (zombiesForDelete) {
                zombiesForDelete.add(zombie0);
            }

            zombieCountForDelete--;
            MPStatistics.clientZombieCulled();
        }
    }
}
