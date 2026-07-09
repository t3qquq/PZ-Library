// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;

/**
 * Create some zombies in varsity outfit + 2 naked zombies in bedroom
 */
public final class RDSStudentNight extends RandomizedDeadSurvivorBase {
    private final ArrayList<String> items = new ArrayList<>();
    private final ArrayList<String> otherItems = new ArrayList<>();
    private final ArrayList<String> pantsMaleItems = new ArrayList<>();
    private final ArrayList<String> pantsFemaleItems = new ArrayList<>();
    private final ArrayList<String> topItems = new ArrayList<>();
    private final ArrayList<String> shoesItems = new ArrayList<>();

    public RDSStudentNight() {
        this.name = "Student Night";
        this.setChance(4);
        this.setMaximumDays(60);
        this.otherItems.add("Base.Cigarettes");
        this.otherItems.add("Base.WhiskeyFull");
        this.otherItems.add("Base.Wine");
        this.otherItems.add("Base.Wine2");
        this.items.add("Base.Crisps");
        this.items.add("Base.Crisps2");
        this.items.add("Base.Crisps3");
        this.items.add("Base.Pop");
        this.items.add("Base.Pop2");
        this.items.add("Base.Pop3");
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
    public void randomizeDeadSurvivor(BuildingDef def) {
        RoomDef roomDef0 = this.getLivingRoomOrKitchen(def);
        this.addZombies(def, Rand.Next(2, 5), null, null, roomDef0);
        RoomDef roomDef1 = this.getRoom(def, "bedroom");
        this.addZombies(def, 1, "Naked", 0, roomDef1);
        this.addItemsOnGround(roomDef1, true);
        this.addZombies(def, 1, "Naked", 100, roomDef1);
        this.addItemsOnGround(roomDef1, false);
        this.addRandomItemsOnGround(roomDef0, this.items, Rand.Next(3, 7));
        this.addRandomItemsOnGround(roomDef0, this.otherItems, Rand.Next(2, 6));
        def.bAlarmed = false;
    }

    private void addItemsOnGround(RoomDef roomDef, boolean boolean0) {
        IsoGridSquare square = getRandomSpawnSquare(roomDef);
        this.addRandomItemOnGround(square, this.shoesItems);
        this.addRandomItemOnGround(square, this.topItems);
        this.addRandomItemOnGround(square, boolean0 ? this.pantsMaleItems : this.pantsFemaleItems);
    }
}
