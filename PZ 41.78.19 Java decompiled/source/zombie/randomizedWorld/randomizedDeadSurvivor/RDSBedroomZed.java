// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.core.Rand;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.RoomDef;

/**
 * Create 2 naked zombies in the bedroom with clothing lying around
 */
public final class RDSBedroomZed extends RandomizedDeadSurvivorBase {
    private final ArrayList<String> pantsMaleItems = new ArrayList<>();
    private final ArrayList<String> pantsFemaleItems = new ArrayList<>();
    private final ArrayList<String> topItems = new ArrayList<>();
    private final ArrayList<String> shoesItems = new ArrayList<>();

    public RDSBedroomZed() {
        this.name = "Bedroom Zed";
        this.setChance(7);
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
        RoomDef roomDef = this.getRoom(def, "bedroom");
        boolean boolean0 = Rand.Next(7) == 0;
        boolean boolean1 = Rand.Next(7) == 0;
        if (boolean0) {
            this.addZombies(def, 2, "Naked", 0, roomDef);
            this.addItemsOnGround(roomDef, true);
            this.addItemsOnGround(roomDef, true);
        } else if (boolean1) {
            this.addZombies(def, 2, "Naked", 100, roomDef);
            this.addItemsOnGround(roomDef, false);
            this.addItemsOnGround(roomDef, false);
        } else {
            this.addZombies(def, 1, "Naked", 0, roomDef);
            this.addItemsOnGround(roomDef, true);
            this.addZombies(def, 1, "Naked", 100, roomDef);
            this.addItemsOnGround(roomDef, false);
        }
    }

    private void addItemsOnGround(RoomDef roomDef, boolean boolean0) {
        IsoGridSquare square = getRandomSpawnSquare(roomDef);
        this.addRandomItemOnGround(square, this.shoesItems);
        this.addRandomItemOnGround(square, this.topItems);
        this.addRandomItemOnGround(square, boolean0 ? this.pantsMaleItems : this.pantsFemaleItems);
    }
}
