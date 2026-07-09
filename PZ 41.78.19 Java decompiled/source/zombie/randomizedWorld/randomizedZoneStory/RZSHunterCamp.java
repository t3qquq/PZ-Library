// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;

public class RZSHunterCamp extends RandomizedZoneStoryBase {
    public RZSHunterCamp() {
        this.name = "Hunter Forest Camp";
        this.chance = 5;
        this.minZoneHeight = 6;
        this.minZoneWidth = 6;
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
    }

    public static ArrayList<String> getForestClutter() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("Base.VarmintRifle");
        arrayList.add("Base.223Box");
        arrayList.add("Base.HuntingRifle");
        arrayList.add("Base.308Box");
        arrayList.add("Base.Shotgun");
        arrayList.add("Base.ShotgunShellsBox");
        arrayList.add("Base.DoubleBarrelShotgun");
        arrayList.add("Base.AssaultRifle");
        arrayList.add("Base.556Box");
        return arrayList;
    }

    @Override
    public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
        int int0 = zone.pickedXForZoneStory;
        int int1 = zone.pickedYForZoneStory;
        ArrayList arrayList = getForestClutter();
        this.cleanAreaForStory(this, zone);
        this.addVehicle(zone, this.getSq(zone.x, zone.y, zone.z), null, null, "Base.OffRoad", null, null, "Hunter");
        this.addTileObject(int0, int1, zone.z, "camping_01_6");
        int int2 = Rand.Next(-1, 2);
        int int3 = Rand.Next(-1, 2);
        this.addTentWestEast(int0 + int2 - 2, int1 + int3, zone.z);
        if (Rand.Next(100) < 70) {
            this.addTentNorthSouth(int0 + int2, int1 + int3 - 2, zone.z);
        }

        if (Rand.Next(100) < 30) {
            this.addTentNorthSouth(int0 + int2 + 1, int1 + int3 - 2, zone.z);
        }

        int int4 = Rand.Next(2, 5);

        for (int int5 = 0; int5 < int4; int5++) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), (String)arrayList.get(Rand.Next(arrayList.size())));
        }

        this.addZombiesOnSquare(Rand.Next(2, 5), "Hunter", 0, this.getRandomFreeSquare(this, zone));
    }
}
