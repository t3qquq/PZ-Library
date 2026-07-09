// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;

public class RZSBeachParty extends RandomizedZoneStoryBase {
    public RZSBeachParty() {
        this.name = "Beach Party";
        this.chance = 10;
        this.minZoneHeight = 13;
        this.minZoneWidth = 13;
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Beach.toString());
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Lake.toString());
    }

    public static ArrayList<String> getBeachClutter() {
        ArrayList arrayList = new ArrayList();
        arrayList.add("Base.Crisps");
        arrayList.add("Base.Crisps3");
        arrayList.add("Base.Pop");
        arrayList.add("Base.WhiskeyFull");
        arrayList.add("Base.Cigarettes");
        arrayList.add("Base.BeerBottle");
        arrayList.add("Base.BeerBottle");
        arrayList.add("Base.BeerCan");
        arrayList.add("Base.BeerCan");
        arrayList.add("Base.BeerCan");
        arrayList.add("Base.BeerCan");
        arrayList.add("Base.BeerCan");
        arrayList.add("Base.BeerCan");
        return arrayList;
    }

    @Override
    public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
        int int0 = zone.pickedXForZoneStory;
        int int1 = zone.pickedYForZoneStory;
        ArrayList arrayList0 = getBeachClutter();
        ArrayList arrayList1 = RZSForestCamp.getCoolerClutter();
        if (Rand.NextBool(2)) {
            this.addTileObject(int0, int1, zone.z, "camping_01_6");
        }

        int int2 = Rand.Next(1, 4);

        for (int int3 = 0; int3 < int2; int3++) {
            int int4 = Rand.Next(4) + 1;
            switch (int4) {
                case 1:
                    int4 = 25;
                    break;
                case 2:
                    int4 = 26;
                    break;
                case 3:
                    int4 = 28;
                    break;
                case 4:
                    int4 = 31;
            }

            IsoGridSquare square = this.getRandomFreeSquare(this, zone);
            this.addTileObject(square, "furniture_seating_outdoor_01_" + int4);
            if (int4 == 25) {
                square = this.getSq(square.x, square.y + 1, square.z);
                this.addTileObject(square, "furniture_seating_outdoor_01_24");
            } else if (int4 == 26) {
                square = this.getSq(square.x + 1, square.y, square.z);
                this.addTileObject(square, "furniture_seating_outdoor_01_27");
            } else if (int4 == 28) {
                square = this.getSq(square.x, square.y - 1, square.z);
                this.addTileObject(square, "furniture_seating_outdoor_01_29");
            } else {
                square = this.getSq(square.x - 1, square.y, square.z);
                this.addTileObject(square, "furniture_seating_outdoor_01_30");
            }
        }

        int2 = Rand.Next(1, 3);

        for (int int5 = 0; int5 < int2; int5++) {
            this.addTileObject(this.getRandomFreeSquare(this, zone), "furniture_seating_outdoor_01_" + Rand.Next(16, 20));
        }

        InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
        int int6 = Rand.Next(4, 8);

        for (int int7 = 0; int7 < int6; int7++) {
            inventoryContainer.getItemContainer().AddItem((String)arrayList1.get(Rand.Next(arrayList1.size())));
        }

        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
        int6 = Rand.Next(3, 7);

        for (int int8 = 0; int8 < int6; int8++) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), (String)arrayList0.get(Rand.Next(arrayList0.size())));
        }

        int int9 = Rand.Next(3, 8);

        for (int int10 = 0; int10 < int9; int10++) {
            this.addZombiesOnSquare(1, "Swimmer", null, this.getRandomFreeSquare(this, zone));
        }

        int9 = Rand.Next(1, 3);

        for (int int11 = 0; int11 < int9; int11++) {
            this.addZombiesOnSquare(1, "Tourist", null, this.getRandomFreeSquare(this, zone));
        }
    }
}
