// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import java.util.Comparator;
import zombie.iso.IsoUtils;

public abstract class BaseZombieSoundManager {
    protected final ArrayList<IsoZombie> characters = new ArrayList<>();
    private final long[] soundTime;
    private final int staleSlotMS;
    private final Comparator<IsoZombie> comp = new Comparator<IsoZombie>() {
        public int compare(IsoZombie zombie0, IsoZombie zombie1) {
            float float0 = BaseZombieSoundManager.this.getClosestListener(zombie0.x, zombie0.y, zombie0.z);
            float float1 = BaseZombieSoundManager.this.getClosestListener(zombie1.x, zombie1.y, zombie1.z);
            if (float0 > float1) {
                return 1;
            } else {
                return float0 < float1 ? -1 : 0;
            }
        }
    };

    public BaseZombieSoundManager(int int0, int int1) {
        this.soundTime = new long[int0];
        this.staleSlotMS = int1;
    }

    public void addCharacter(IsoZombie zombie0) {
        if (!this.characters.contains(zombie0)) {
            this.characters.add(zombie0);
        }
    }

    public void update() {
        if (!this.characters.isEmpty()) {
            this.characters.sort(this.comp);
            long long0 = System.currentTimeMillis();

            for (int int0 = 0; int0 < this.soundTime.length && int0 < this.characters.size(); int0++) {
                IsoZombie zombie0 = this.characters.get(int0);
                if (zombie0.getCurrentSquare() != null) {
                    int int1 = this.getFreeSoundSlot(long0);
                    if (int1 == -1) {
                        break;
                    }

                    this.playSound(zombie0);
                    this.soundTime[int1] = long0;
                }
            }

            this.postUpdate();
            this.characters.clear();
        }
    }

    public abstract void playSound(IsoZombie var1);

    public abstract void postUpdate();

    private float getClosestListener(float float5, float float6, float float7) {
        float float0 = Float.MAX_VALUE;

        for (int int0 = 0; int0 < IsoPlayer.numPlayers; int0++) {
            IsoPlayer player = IsoPlayer.players[int0];
            if (player != null && player.getCurrentSquare() != null) {
                float float1 = player.getX();
                float float2 = player.getY();
                float float3 = player.getZ();
                float float4 = IsoUtils.DistanceToSquared(float1, float2, float3 * 3.0F, float5, float6, float7 * 3.0F);
                if (player.Traits.HardOfHearing.isSet()) {
                    float4 *= 4.5F;
                }

                if (float4 < float0) {
                    float0 = float4;
                }
            }
        }

        return float0;
    }

    private int getFreeSoundSlot(long long1) {
        long long0 = Long.MAX_VALUE;
        int int0 = -1;

        for (int int1 = 0; int1 < this.soundTime.length; int1++) {
            if (this.soundTime[int1] < long0) {
                long0 = this.soundTime[int1];
                int0 = int1;
            }
        }

        return long1 - long0 < this.staleSlotMS ? -1 : int0;
    }
}
