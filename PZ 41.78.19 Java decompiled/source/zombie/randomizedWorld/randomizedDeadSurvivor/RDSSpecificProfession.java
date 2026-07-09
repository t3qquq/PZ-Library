// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedDeadSurvivor;

import java.util.ArrayList;
import zombie.inventory.ItemPickerJava;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.objects.IsoDeadBody;
import zombie.util.list.PZArrayUtil;

/**
 * Create a dead survivor in the kitchen with empty bleach bottle around him
 */
public final class RDSSpecificProfession extends RandomizedDeadSurvivorBase {
    private final ArrayList<String> specificProfessionDistribution = new ArrayList<>();

    @Override
    public void randomizeDeadSurvivor(BuildingDef def) {
        IsoGridSquare square = def.getFreeSquareInRoom();
        if (square != null) {
            IsoDeadBody deadBody = createRandomDeadBody(square.getX(), square.getY(), square.getZ(), null, 0);
            if (deadBody != null) {
                ItemPickerJava.ItemPickerRoom itemPickerRoom = ItemPickerJava.rooms.get(PZArrayUtil.pickRandom(this.specificProfessionDistribution));
                ItemPickerJava.rollItem(itemPickerRoom.Containers.get("counter"), deadBody.getContainer(), true, null, null);
            }
        }
    }

    public RDSSpecificProfession() {
        this.specificProfessionDistribution.add("Carpenter");
        this.specificProfessionDistribution.add("Electrician");
        this.specificProfessionDistribution.add("Farmer");
        this.specificProfessionDistribution.add("Nurse");
    }
}
