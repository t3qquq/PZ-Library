// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.InventoryContainer;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.objects.IsoDeadBody;

public class RZSForestCampEaten extends RandomizedZoneStoryBase {
    public RZSForestCampEaten() {
        this.name = "Forest Camp Eaten";
        this.chance = 10;
        this.minZoneHeight = 6;
        this.minZoneWidth = 10;
        this.minimumDays = 30;
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
    }

    @Override
    public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
        int int0 = zone.pickedXForZoneStory;
        int int1 = zone.pickedYForZoneStory;
        ArrayList arrayList0 = RZSForestCamp.getForestClutter();
        ArrayList arrayList1 = RZSForestCamp.getCoolerClutter();
        ArrayList arrayList2 = RZSForestCamp.getFireClutter();
        this.cleanAreaForStory(this, zone);
        this.addTileObject(int0, int1, zone.z, "camping_01_6");
        this.addItemOnGround(this.getSq(int0, int1, zone.z), (String)arrayList2.get(Rand.Next(arrayList2.size())));
        int int2 = 0;
        byte byte0 = 0;
        this.addTentNorthSouth(int0 - 4, int1 + byte0 - 2, zone.z);
        int2 += Rand.Next(1, 3);
        this.addTentNorthSouth(int0 - 3 + int2, int1 + byte0 - 2, zone.z);
        int2 += Rand.Next(1, 3);
        this.addTentNorthSouth(int0 - 2 + int2, int1 + byte0 - 2, zone.z);
        if (Rand.NextBool(1)) {
            int2 += Rand.Next(1, 3);
            this.addTentNorthSouth(int0 - 1 + int2, int1 + byte0 - 2, zone.z);
        }

        if (Rand.NextBool(2)) {
            int2 += Rand.Next(1, 3);
            this.addTentNorthSouth(int0 + int2, int1 + byte0 - 2, zone.z);
        }

        InventoryContainer inventoryContainer = (InventoryContainer)InventoryItemFactory.CreateItem("Base.Cooler");
        int int3 = Rand.Next(2, 5);

        for (int int4 = 0; int4 < int3; int4++) {
            inventoryContainer.getItemContainer().AddItem((String)arrayList1.get(Rand.Next(arrayList1.size())));
        }

        this.addItemOnGround(this.getRandomFreeSquare(this, zone), inventoryContainer);
        int3 = Rand.Next(3, 7);

        for (int int5 = 0; int5 < int3; int5++) {
            this.addItemOnGround(this.getRandomFreeSquare(this, zone), (String)arrayList0.get(Rand.Next(arrayList0.size())));
        }

        ArrayList arrayList3 = this.addZombiesOnSquare(1, "Camper", null, this.getRandomFreeSquare(this, zone));
        IsoZombie zombie0 = arrayList3.isEmpty() ? null : (IsoZombie)arrayList3.get(0);
        int int6 = Rand.Next(3, 7);
        Object object = null;

        for (int int7 = 0; int7 < int6; int7++) {
            object = createRandomDeadBody(this.getRandomFreeSquare(this, zone), null, Rand.Next(5, 10), 0, "Camper");
            if (object != null) {
                this.addBloodSplat(((IsoDeadBody)object).getSquare(), 10);
            }
        }

        object = createRandomDeadBody(this.getSq(int0, int1 + 3, zone.z), null, Rand.Next(5, 10), 0, "Camper");
        if (object != null) {
            this.addBloodSplat(((IsoDeadBody)object).getSquare(), 10);
            if (zombie0 != null) {
                zombie0.faceLocationF(((IsoDeadBody)object).x, ((IsoDeadBody)object).y);
                zombie0.setX(((IsoDeadBody)object).x + 1.0F);
                zombie0.setY(((IsoDeadBody)object).y);
                zombie0.setEatBodyTarget((IsoMovingObject)object, true);
            }
        }
    }
}
