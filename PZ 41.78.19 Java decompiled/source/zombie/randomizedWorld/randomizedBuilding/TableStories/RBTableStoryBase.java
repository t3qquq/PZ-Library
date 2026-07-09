// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding.TableStories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.core.Rand;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.randomizedWorld.randomizedBuilding.RandomizedBuildingBase;

public class RBTableStoryBase extends RandomizedBuildingBase {
    public static ArrayList<RBTableStoryBase> allStories = new ArrayList<>();
    public static int totalChance = 0;
    protected int chance = 0;
    protected ArrayList<String> rooms = new ArrayList<>();
    protected boolean need2Tables = false;
    protected boolean ignoreAgainstWall = false;
    protected IsoObject table2 = null;
    protected IsoObject table1 = null;
    protected boolean westTable = false;
    private static final HashMap<RBTableStoryBase, Integer> rbtsmap = new HashMap<>();
    private static final ArrayList<IsoObject> tableObjects = new ArrayList<>();
    public ArrayList<HashMap<String, Integer>> fullTableMap = new ArrayList<>();

    public static void initStories(IsoGridSquare square, IsoObject object) {
        if (allStories.isEmpty()) {
            allStories.add(new RBTSBreakfast());
            allStories.add(new RBTSDinner());
            allStories.add(new RBTSSoup());
            allStories.add(new RBTSSewing());
            allStories.add(new RBTSElectronics());
            allStories.add(new RBTSFoodPreparation());
            allStories.add(new RBTSButcher());
            allStories.add(new RBTSSandwich());
            allStories.add(new RBTSDrink());
        }

        totalChance = 0;
        rbtsmap.clear();

        for (int int0 = 0; int0 < allStories.size(); int0++) {
            RBTableStoryBase rBTableStoryBase = allStories.get(int0);
            if (rBTableStoryBase.isValid(square, object, false) && rBTableStoryBase.isTimeValid(false)) {
                totalChance = totalChance + rBTableStoryBase.chance;
                rbtsmap.put(rBTableStoryBase, rBTableStoryBase.chance);
            }
        }
    }

    public static RBTableStoryBase getRandomStory(IsoGridSquare square, IsoObject object) {
        initStories(square, object);
        int int0 = Rand.Next(totalChance);
        Iterator iterator = rbtsmap.keySet().iterator();
        int int1 = 0;

        while (iterator.hasNext()) {
            RBTableStoryBase rBTableStoryBase = (RBTableStoryBase)iterator.next();
            int1 += rbtsmap.get(rBTableStoryBase);
            if (int0 < int1) {
                rBTableStoryBase.table1 = object;
                return rBTableStoryBase;
            }
        }

        return null;
    }

    public boolean isValid(IsoGridSquare square, IsoObject object, boolean boolean0) {
        if (boolean0) {
            return true;
        } else if (this.rooms != null && square.getRoom() != null && !this.rooms.contains(square.getRoom().getName())) {
            return false;
        } else {
            if (this.need2Tables) {
                this.table2 = this.getSecondTable(object);
                if (this.table2 == null) {
                    return false;
                }
            }

            return !this.ignoreAgainstWall || !square.getWallFull();
        }
    }

    public IsoObject getSecondTable(IsoObject object0) {
        this.westTable = true;
        IsoGridSquare square0 = object0.getSquare();
        if (this.ignoreAgainstWall && square0.getWallFull()) {
            return null;
        } else {
            object0.getSpriteGridObjects(tableObjects);
            IsoGridSquare square1 = square0.getAdjacentSquare(IsoDirections.W);
            IsoObject object1 = this.checkForTable(square1, object0, tableObjects);
            if (object1 == null) {
                square1 = square0.getAdjacentSquare(IsoDirections.E);
                object1 = this.checkForTable(square1, object0, tableObjects);
            }

            if (object1 == null) {
                this.westTable = false;
            }

            if (object1 == null) {
                square1 = square0.getAdjacentSquare(IsoDirections.N);
                object1 = this.checkForTable(square1, object0, tableObjects);
            }

            if (object1 == null) {
                square1 = square0.getAdjacentSquare(IsoDirections.S);
                object1 = this.checkForTable(square1, object0, tableObjects);
            }

            return object1 != null && this.ignoreAgainstWall && square1.getWallFull() ? null : object1;
        }
    }

    private IsoObject checkForTable(IsoGridSquare square, IsoObject object0, ArrayList<IsoObject> arrayList) {
        if (square == null) {
            return null;
        } else if (square.isSomethingTo(object0.getSquare())) {
            return null;
        } else {
            for (int int0 = 0; int0 < square.getObjects().size(); int0++) {
                IsoObject object1 = square.getObjects().get(int0);
                if ((arrayList.isEmpty() || arrayList.contains(object1))
                    && object1.getProperties().isTable()
                    && object1.getContainer() == null
                    && object1 != object0) {
                    return object1;
                }
            }

            return null;
        }
    }
}
