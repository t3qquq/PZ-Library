// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding.TableStories;

import zombie.core.Rand;
import zombie.iso.BuildingDef;

public final class RBTSButcher extends RBTableStoryBase {
    public RBTSButcher() {
        this.chance = 3;
        this.ignoreAgainstWall = true;
        this.rooms.add("livingroom");
        this.rooms.add("kitchen");
    }

    @Override
    public void randomizeBuilding(BuildingDef var1) {
        String string0 = "Base.DeadRabbit";
        String string1 = "Base.Rabbitmeat";
        int int0 = Rand.Next(0, 4);
        switch (int0) {
            case 0:
                string0 = "Base.DeadBird";
                string1 = "Base.Smallbirdmeat";
                break;
            case 1:
                string0 = "Base.DeadSquirrel";
                string1 = "Base.Smallanimalmeat";
                break;
            case 2:
                string0 = "Base.Panfish";
                string1 = "Base.FishFillet";
                break;
            case 3:
                string0 = "Base.BaitFish";
                string1 = "Base.FishFillet";
                break;
            case 4:
                string0 = "Base.Catfish";
                string1 = "Base.FishFillet";
        }

        this.addWorldItem(string0, this.table1.getSquare(), 0.453F, 0.64F, this.table1.getSurfaceOffsetNoTable() / 96.0F, 1);
        this.addWorldItem(string1, this.table1.getSquare(), 0.835F, 0.851F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
        this.addWorldItem("Base.KitchenKnife", this.table1.getSquare(), 0.742F, 0.445F, this.table1.getSurfaceOffsetNoTable() / 96.0F);
    }
}
