// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.util.Type;

public final class MovingObjectUpdateSchedulerUpdateBucket {
    public int frameMod;
    ArrayList<IsoMovingObject>[] buckets;

    public MovingObjectUpdateSchedulerUpdateBucket(int int0) {
        this.init(int0);
    }

    public void init(int int0) {
        this.frameMod = int0;
        this.buckets = new ArrayList[int0];

        for (int int1 = 0; int1 < this.buckets.length; int1++) {
            this.buckets[int1] = new ArrayList<>();
        }
    }

    public void clear() {
        for (int int0 = 0; int0 < this.buckets.length; int0++) {
            ArrayList arrayList = this.buckets[int0];
            arrayList.clear();
        }
    }

    public void remove(IsoMovingObject movingObject) {
        for (int int0 = 0; int0 < this.buckets.length; int0++) {
            ArrayList arrayList = this.buckets[int0];
            arrayList.remove(movingObject);
        }
    }

    public void add(IsoMovingObject movingObject) {
        int int0 = movingObject.getID() % this.frameMod;
        this.buckets[int0].add(movingObject);
    }

    public void update(int int0) {
        GameTime.getInstance().PerObjectMultiplier = this.frameMod;
        ArrayList arrayList = this.buckets[int0 % this.frameMod];

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int1);
            if (movingObject instanceof IsoDeadBody) {
                IsoWorld.instance.getCell().getRemoveList().add(movingObject);
            } else {
                IsoZombie zombie0 = Type.tryCastTo(movingObject, IsoZombie.class);
                if (zombie0 != null && VirtualZombieManager.instance.isReused(zombie0)) {
                    DebugLog.log(DebugType.Zombie, "REUSABLE ZOMBIE IN MovingObjectUpdateSchedulerUpdateBucket IGNORED " + movingObject);
                } else {
                    movingObject.preupdate();
                    movingObject.update();
                }
            }
        }

        GameTime.getInstance().PerObjectMultiplier = 1.0F;
    }

    public void postupdate(int int0) {
        GameTime.getInstance().PerObjectMultiplier = this.frameMod;
        ArrayList arrayList = this.buckets[int0 % this.frameMod];

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int1);
            IsoZombie zombie0 = Type.tryCastTo(movingObject, IsoZombie.class);
            if (zombie0 != null && VirtualZombieManager.instance.isReused(zombie0)) {
                DebugLog.log(DebugType.Zombie, "REUSABLE ZOMBIE IN MovingObjectUpdateSchedulerUpdateBucket IGNORED " + movingObject);
            } else {
                movingObject.postupdate();
            }
        }

        GameTime.getInstance().PerObjectMultiplier = 1.0F;
    }

    public void removeObject(IsoMovingObject movingObject) {
        for (int int0 = 0; int0 < this.buckets.length; int0++) {
            ArrayList arrayList = this.buckets[int0];
            arrayList.remove(movingObject);
        }
    }

    public ArrayList<IsoMovingObject> getBucket(int int0) {
        return this.buckets[int0 % this.frameMod];
    }
}
