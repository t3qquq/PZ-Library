// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedBuilding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import zombie.core.Rand;
import zombie.inventory.InventoryItem;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;

public final class RBTableStory extends RandomizedBuildingBase {
    public static ArrayList<RBTableStory.StoryDef> allStories = new ArrayList<>();
    private float xOffset = 0.0F;
    private float yOffset = 0.0F;
    private IsoGridSquare currentSquare = null;
    public ArrayList<HashMap<String, Integer>> fullTableMap = new ArrayList<>();
    public IsoObject table1 = null;
    public IsoObject table2 = null;

    public void initStories() {
        if (allStories.isEmpty()) {
            ArrayList arrayList0 = new ArrayList();
            arrayList0.add("livingroom");
            arrayList0.add("kitchen");
            ArrayList arrayList1 = new ArrayList();
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            linkedHashMap.put("BakingPan", 50);
            linkedHashMap.put("CakePrep", 50);
            arrayList1.add(new RBTableStory.StorySpawnItem(linkedHashMap, null, 100));
            arrayList1.add(new RBTableStory.StorySpawnItem(null, "Chocolate", 100));
            arrayList1.add(new RBTableStory.StorySpawnItem(null, "Butter", 70));
            arrayList1.add(new RBTableStory.StorySpawnItem(null, "Flour", 70));
            arrayList1.add(new RBTableStory.StorySpawnItem(null, "Spoon", 100));
            arrayList1.add(new RBTableStory.StorySpawnItem(null, "EggCarton", 100));
            arrayList1.add(new RBTableStory.StorySpawnItem(null, "Egg", 100));
            allStories.add(new RBTableStory.StoryDef(arrayList1, arrayList0));
        }
    }

    @Override
    public boolean isValid(BuildingDef var1, boolean var2) {
        return false;
    }

    @Override
    public void randomizeBuilding(BuildingDef var1) {
        this.initStories();
        if (this.table1 != null && this.table2 != null) {
            if (this.table1.getSquare() != null && this.table1.getSquare().getRoom() != null) {
                ArrayList arrayList = new ArrayList();

                for (int int0 = 0; int0 < allStories.size(); int0++) {
                    RBTableStory.StoryDef storyDef0 = allStories.get(int0);
                    if (storyDef0.rooms == null || storyDef0.rooms.contains(this.table1.getSquare().getRoom().getName())) {
                        arrayList.add(storyDef0);
                    }
                }

                if (!arrayList.isEmpty()) {
                    RBTableStory.StoryDef storyDef1 = (RBTableStory.StoryDef)arrayList.get(Rand.Next(0, arrayList.size()));
                    if (storyDef1 != null) {
                        boolean boolean0 = true;
                        if ((int)this.table1.getY() != (int)this.table2.getY()) {
                            boolean0 = false;
                        }

                        this.doSpawnTable(storyDef1.items, boolean0);
                        if (storyDef1.addBlood) {
                            int int1 = (int)this.table1.getX() - 1;
                            int int2 = (int)this.table1.getX() + 1;
                            int int3 = (int)this.table1.getY() - 1;
                            int int4 = (int)this.table2.getY() + 1;
                            if (boolean0) {
                                int1 = (int)this.table1.getX() - 1;
                                int2 = (int)this.table2.getX() + 1;
                                int3 = (int)this.table1.getY() - 1;
                                int4 = (int)this.table2.getY() + 1;
                            }

                            for (int int5 = int1; int5 < int2 + 1; int5++) {
                                for (int int6 = int3; int6 < int4 + 1; int6++) {
                                    int int7 = Rand.Next(7, 15);

                                    for (int int8 = 0; int8 < int7; int8++) {
                                        this.currentSquare
                                            .getChunk()
                                            .addBloodSplat(int5 + Rand.Next(-0.5F, 0.5F), int6 + Rand.Next(-0.5F, 0.5F), this.table1.getZ(), Rand.Next(8));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void doSpawnTable(ArrayList<RBTableStory.StorySpawnItem> arrayList, boolean boolean0) {
        this.xOffset = 0.0F;
        this.yOffset = 0.0F;
        int int0 = 0;
        if (boolean0) {
            this.xOffset = 0.6F;
            this.yOffset = Rand.Next(0.5F, 1.1F);
        } else {
            this.yOffset = 0.6F;
            this.xOffset = Rand.Next(0.5F, 1.1F);
        }

        for (this.currentSquare = this.table1.getSquare(); int0 < arrayList.size(); int0++) {
            RBTableStory.StorySpawnItem storySpawnItem = (RBTableStory.StorySpawnItem)arrayList.get(int0);
            String string = this.getItemFromSSI(storySpawnItem);
            if (string != null) {
                InventoryItem item = this.currentSquare.AddWorldInventoryItem(string, this.xOffset, this.yOffset, 0.4F);
                if (item != null) {
                    this.increaseOffsets(boolean0, storySpawnItem);
                }
            }
        }
    }

    private void increaseOffsets(boolean boolean0, RBTableStory.StorySpawnItem storySpawnItem) {
        float float0 = 0.15F + storySpawnItem.forcedOffset;
        if (boolean0) {
            this.xOffset += float0;
            if (this.xOffset > 1.0F) {
                this.currentSquare = this.table2.getSquare();
                this.xOffset = 0.35F;
            }

            float float1 = this.yOffset;

            while (Math.abs(float1 - this.yOffset) < 0.11F) {
                this.yOffset = Rand.Next(0.5F, 1.1F);
            }
        } else {
            this.yOffset += float0;
            if (this.yOffset > 1.0F) {
                this.currentSquare = this.table2.getSquare();
                this.yOffset = 0.35F;
            }

            float float2 = this.xOffset;

            while (Math.abs(float2 - this.xOffset) < 0.11F) {
                this.xOffset = Rand.Next(0.5F, 1.1F);
            }
        }
    }

    private String getItemFromSSI(RBTableStory.StorySpawnItem storySpawnItem) {
        if (Rand.Next(100) > storySpawnItem.chanceToSpawn) {
            return null;
        } else if (storySpawnItem.eitherObject != null && !storySpawnItem.eitherObject.isEmpty()) {
            int int0 = Rand.Next(100);
            int int1 = 0;

            for (String string : storySpawnItem.eitherObject.keySet()) {
                int int2 = storySpawnItem.eitherObject.get(string);
                int1 += int2;
                if (int1 >= int0) {
                    return string;
                }
            }

            return null;
        } else {
            return storySpawnItem.object;
        }
    }

    public class StoryDef {
        public ArrayList<RBTableStory.StorySpawnItem> items = null;
        public boolean addBlood = false;
        public ArrayList<String> rooms = null;

        public StoryDef(ArrayList<RBTableStory.StorySpawnItem> arrayList) {
            this.items = arrayList;
        }

        public StoryDef(ArrayList<RBTableStory.StorySpawnItem> arrayList0, ArrayList<String> arrayList1) {
            this.items = arrayList0;
            this.rooms = arrayList1;
        }
    }

    public class StorySpawnItem {
        LinkedHashMap<String, Integer> eitherObject = null;
        String object = null;
        Integer chanceToSpawn = null;
        float forcedOffset = 0.0F;

        public StorySpawnItem(LinkedHashMap<String, Integer> linkedHashMap, String string, Integer integer) {
            this.eitherObject = linkedHashMap;
            this.object = string;
            this.chanceToSpawn = integer;
        }

        public StorySpawnItem(LinkedHashMap<String, Integer> linkedHashMap, String string, Integer integer, float float0) {
            this.eitherObject = linkedHashMap;
            this.object = string;
            this.chanceToSpawn = integer;
            this.forcedOffset = float0;
        }
    }
}
