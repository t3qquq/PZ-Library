// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.iso.IsoMetaGrid;
import zombie.vehicles.BaseVehicle;

public class RZSSexyTime extends RandomizedZoneStoryBase {
    private final ArrayList<String> pantsMaleItems = new ArrayList<>();
    private final ArrayList<String> pantsFemaleItems = new ArrayList<>();
    private final ArrayList<String> topItems = new ArrayList<>();
    private final ArrayList<String> shoesItems = new ArrayList<>();

    public RZSSexyTime() {
        this.name = "Sexy Time";
        this.chance = 5;
        this.minZoneHeight = 5;
        this.minZoneWidth = 5;
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Beach.toString());
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Forest.toString());
        this.zoneType.add(RandomizedZoneStoryBase.ZoneType.Lake.toString());
        this.shoesItems.add("Base.Shoes_Random");
        this.shoesItems.add("Base.Shoes_TrainerTINT");
        this.pantsMaleItems.add("Base.TrousersMesh_DenimLight");
        this.pantsMaleItems.add("Base.Trousers_DefaultTEXTURE_TINT");
        this.pantsMaleItems.add("Base.Trousers_Denim");
        this.pantsFemaleItems.add("Base.Skirt_Knees");
        this.pantsFemaleItems.add("Base.Skirt_Long");
        this.pantsFemaleItems.add("Base.Skirt_Short");
        this.pantsFemaleItems.add("Base.Skirt_Normal");
        this.topItems.add("Base.Shirt_FormalWhite");
        this.topItems.add("Base.Shirt_FormalWhite_ShortSleeve");
        this.topItems.add("Base.Tshirt_DefaultTEXTURE_TINT");
        this.topItems.add("Base.Tshirt_PoloTINT");
        this.topItems.add("Base.Tshirt_WhiteLongSleeveTINT");
        this.topItems.add("Base.Tshirt_WhiteTINT");
    }

    @Override
    public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
        this.cleanAreaForStory(this, zone);
        BaseVehicle vehicle = this.addVehicle(
            zone, this.getSq(zone.pickedXForZoneStory, zone.pickedYForZoneStory, zone.z), null, null, "Base.VanAmbulance", null, null, null
        );
        if (vehicle != null) {
            vehicle.setAlarmed(false);
        }

        boolean boolean0 = Rand.Next(7) == 0;
        boolean boolean1 = Rand.Next(7) == 0;
        if (boolean0) {
            this.addItemsOnGround(zone, true, vehicle);
            this.addItemsOnGround(zone, true, vehicle);
        } else if (boolean1) {
            this.addItemsOnGround(zone, false, vehicle);
            this.addItemsOnGround(zone, false, vehicle);
        } else {
            this.addItemsOnGround(zone, true, vehicle);
            this.addItemsOnGround(zone, false, vehicle);
        }
    }

    private void addItemsOnGround(IsoMetaGrid.Zone var1, boolean boolean0, BaseVehicle vehicle) {
        byte byte0 = 100;
        if (!boolean0) {
            byte0 = 0;
        }

        ArrayList arrayList = this.addZombiesOnVehicle(1, "Naked", Integer.valueOf(byte0), vehicle);
        if (!arrayList.isEmpty()) {
            IsoZombie zombie0 = (IsoZombie)arrayList.get(0);
            this.addRandomItemOnGround(zombie0.getSquare(), this.shoesItems);
            this.addRandomItemOnGround(zombie0.getSquare(), this.topItems);
            this.addRandomItemOnGround(zombie0.getSquare(), boolean0 ? this.pantsMaleItems : this.pantsFemaleItems);
        }
    }
}
