// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;

public class RZSTrapperCamp extends RandomizedZoneStoryBase {
    public RZSTrapperCamp() {
        this.name = "Trappers Forest Camp";
        this.chance = 7;
        this.minZoneHeight = 6;
        this.minZoneWidth = 6;
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
    }

    public static ArrayList<String> getTrapList() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("constructedobjects_01_3");
        arrayList.add("constructedobjects_01_4");
        arrayList.add("constructedobjects_01_7");
        arrayList.add("constructedobjects_01_8");
        arrayList.add("constructedobjects_01_11");
        arrayList.add("constructedobjects_01_13");
        arrayList.add("constructedobjects_01_16");
        return arrayList;
    }

    @Override
    public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
        int int0 = zone.pickedXForZoneStory;
        int int1 = zone.pickedYForZoneStory;
        ArrayList arrayList = getTrapList();
        this.cleanAreaForStory(this, zone);
        this.addTileObject(int0, int1, zone.z, "camping_01_6");
        int int2 = Rand.Next(-1, 2);
        int int3 = Rand.Next(-1, 2);
        this.addTentWestEast(int0 + int2 - 2, int1 + int3, zone.z);
        if (Rand.Next(100) < 70) {
            this.addTentNorthSouth(int0 + int2, int1 + int3 - 2, zone.z);
        }

        int int4 = Rand.Next(2, 5);

        for (int int5 = 0; int5 < int4; int5++) {
            IsoGridSquare square = this.getRandomFreeSquare(this, zone);
            this.addTileObject(square, (String)arrayList.get(Rand.Next(arrayList.size())));
        }

        this.addZombiesOnSquare(Rand.Next(2, 5), "Hunter", 0, this.getRandomFreeSquare(this, zone));
    }
}
