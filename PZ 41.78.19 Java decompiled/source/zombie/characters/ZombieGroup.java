// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.characters;

import java.util.ArrayList;
import zombie.SandboxOptions;
import zombie.ai.ZombieGroupManager;
import zombie.iso.IsoUtils;

public final class ZombieGroup {
    private final ArrayList<IsoZombie> members = new ArrayList<>();
    public float lastSpreadOutTime;

    public ZombieGroup reset() {
        this.members.clear();
        this.lastSpreadOutTime = -1.0F;
        return this;
    }

    public void add(IsoZombie zombie) {
        if (!this.members.contains(zombie)) {
            if (zombie.group != null) {
                zombie.group.remove(zombie);
            }

            this.members.add(zombie);
            zombie.group = this;
        }
    }

    public void remove(IsoZombie zombie) {
        this.members.remove(zombie);
        zombie.group = null;
    }

    public IsoZombie getLeader() {
        return this.members.isEmpty() ? null : this.members.get(0);
    }

    public boolean isEmpty() {
        return this.members.isEmpty();
    }

    public int size() {
        return this.members.size();
    }

    public void update() {
        int int0 = SandboxOptions.instance.zombieConfig.RallyTravelDistance.getValue();

        for (int int1 = 0; int1 < this.members.size(); int1++) {
            IsoZombie zombie0 = this.members.get(int1);
            float float0 = 0.0F;
            if (int1 > 0) {
                float0 = IsoUtils.DistanceToSquared(this.members.get(0).getX(), this.members.get(0).getY(), zombie0.getX(), zombie0.getY());
            }

            if (zombie0.group != this || float0 > int0 * int0 || !ZombieGroupManager.instance.shouldBeInGroup(zombie0)) {
                if (zombie0.group == this) {
                    zombie0.group = null;
                }

                this.members.remove(int1--);
            }
        }
    }
}
