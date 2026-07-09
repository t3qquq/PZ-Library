// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion;

import java.util.ArrayList;
import zombie.erosion.categories.ErosionCategory;
import zombie.erosion.categories.Flowerbed;
import zombie.erosion.categories.NatureBush;
import zombie.erosion.categories.NatureGeneric;
import zombie.erosion.categories.NaturePlants;
import zombie.erosion.categories.NatureTrees;
import zombie.erosion.categories.StreetCracks;
import zombie.erosion.categories.WallCracks;
import zombie.erosion.categories.WallVines;

public final class ErosionRegions {
    public static final int REGION_NATURE = 0;
    public static final int CATEGORY_TREES = 0;
    public static final int CATEGORY_BUSH = 1;
    public static final int CATEGORY_PLANTS = 2;
    public static final int CATEGORY_GENERIC = 3;
    public static final int REGION_STREET = 1;
    public static final int CATEGORY_STREET_CRACKS = 0;
    public static final int REGION_WALL = 2;
    public static final int CATEGORY_WALL_VINES = 0;
    public static final int CATEGORY_WALL_CRACKS = 1;
    public static final int REGION_FLOWERBED = 3;
    public static final int CATEGORY_FLOWERBED = 0;
    public static final ArrayList<ErosionRegions.Region> regions = new ArrayList<>();

    private static void addRegion(ErosionRegions.Region region) {
        region.ID = regions.size();
        regions.add(region);
    }

    public static ErosionCategory getCategory(int int1, int int0) {
        return regions.get(int1).categories.get(int0);
    }

    public static void init() {
        regions.clear();
        addRegion(
            new ErosionRegions.Region(0, "blends_natural_01", true, true, false)
                .addCategory(0, new NatureTrees())
                .addCategory(1, new NatureBush())
                .addCategory(2, new NaturePlants())
                .addCategory(3, new NatureGeneric())
        );
        addRegion(new ErosionRegions.Region(1, "blends_street", true, true, false).addCategory(0, new StreetCracks()));
        addRegion(new ErosionRegions.Region(2, null, false, false, true).addCategory(0, new WallVines()).addCategory(1, new WallCracks()));
        addRegion(new ErosionRegions.Region(3, null, true, true, false).addCategory(0, new Flowerbed()));

        for (int int0 = 0; int0 < regions.size(); int0++) {
            regions.get(int0).init();
        }
    }

    public static void Reset() {
        for (int int0 = 0; int0 < regions.size(); int0++) {
            regions.get(int0).Reset();
        }

        regions.clear();
    }

    public static final class Region {
        public int ID;
        public String tileNameMatch;
        public boolean checkExterior;
        public boolean isExterior;
        public boolean hasWall;
        public final ArrayList<ErosionCategory> categories = new ArrayList<>();

        public Region(int int0, String string, boolean boolean0, boolean boolean1, boolean boolean2) {
            this.ID = int0;
            this.tileNameMatch = string;
            this.checkExterior = boolean0;
            this.isExterior = boolean1;
            this.hasWall = boolean2;
        }

        public ErosionRegions.Region addCategory(int int0, ErosionCategory erosionCategory) {
            erosionCategory.ID = int0;
            erosionCategory.region = this;
            this.categories.add(erosionCategory);
            return this;
        }

        public void init() {
            for (int int0 = 0; int0 < this.categories.size(); int0++) {
                this.categories.get(int0).init();
            }
        }

        public void Reset() {
            this.categories.clear();
        }
    }
}
