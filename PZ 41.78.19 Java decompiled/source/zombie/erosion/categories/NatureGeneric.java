// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.erosion.ErosionData;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.erosion.season.ErosionIceQueen;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;

public final class NatureGeneric extends ErosionCategory {
    private ArrayList<ErosionObj> objs = new ArrayList<>();
    private static final int GRASS = 0;
    private static final int FERNS = 1;
    private static final int GENERIC = 2;
    private ArrayList<ArrayList<Integer>> objsRef = new ArrayList<>();
    private int[] spawnChance = new int[100];

    @Override
    public boolean replaceExistingObject(IsoGridSquare square0, ErosionData.Square square1, ErosionData.Chunk var3, boolean var4, boolean var5) {
        int int0 = square0.getObjects().size();

        for (int int1 = int0 - 1; int1 >= 1; int1--) {
            IsoObject object = square0.getObjects().get(int1);
            IsoSprite sprite = object.getSprite();
            if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("blends_grassoverlays")) {
                float float0 = 0.3F;
                float float1 = 12.0F;
                if ("Forest".equals(square0.getZoneType())) {
                    float0 = 0.5F;
                    float1 = 6.0F;
                } else if ("DeepForest".equals(square0.getZoneType())) {
                    float0 = 0.7F;
                    float1 = 3.0F;
                }

                NatureGeneric.CategoryData categoryData = (NatureGeneric.CategoryData)this.setCatModData(square1);
                ArrayList arrayList = this.objsRef.get(0);
                int int2 = square1.noiseMainInt;
                int int3 = square1.rand(square0.x, square0.y, 101);
                if (int3 < int2 / float1) {
                    if (square1.magicNum < float0) {
                        arrayList = this.objsRef.get(1);
                    } else {
                        arrayList = this.objsRef.get(2);
                    }

                    categoryData.notGrass = true;
                    categoryData.maxStage = int2 > 60 ? 1 : 0;
                } else {
                    categoryData.maxStage = int2 > 67 ? 2 : (int2 > 50 ? 1 : 0);
                }

                categoryData.gameObj = (Integer)arrayList.get(square1.rand(square0.x, square0.y, arrayList.size()));
                categoryData.stage = categoryData.maxStage;
                categoryData.spawnTime = 0;
                categoryData.dispSeason = -1;
                ErosionObj erosionObj = this.objs.get(categoryData.gameObj);
                object.setName(erosionObj.name);
                object.doNotSync = true;
                categoryData.hasSpawned = true;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean validateSpawn(IsoGridSquare square0, ErosionData.Square square1, ErosionData.Chunk var3, boolean var4, boolean boolean0, boolean var6) {
        if (square0.getObjects().size() > (boolean0 ? 2 : 1)) {
            return false;
        } else {
            int int0 = square1.noiseMainInt;
            if (square1.rand(square0.x, square0.y, 101) < this.spawnChance[int0]) {
                float float0 = 0.3F;
                float float1 = 12.0F;
                if ("Forest".equals(square0.getZoneType())) {
                    float0 = 0.5F;
                    float1 = 6.0F;
                } else if ("DeepForest".equals(square0.getZoneType())) {
                    float0 = 0.7F;
                    float1 = 3.0F;
                }

                NatureGeneric.CategoryData categoryData = (NatureGeneric.CategoryData)this.setCatModData(square1);
                ArrayList arrayList = this.objsRef.get(0);
                int int1 = square1.rand(square0.x, square0.y, 101);
                if (int1 < int0 / float1) {
                    if (square1.magicNum < float0) {
                        arrayList = this.objsRef.get(1);
                    } else {
                        arrayList = this.objsRef.get(2);
                    }

                    categoryData.notGrass = true;
                    categoryData.maxStage = int0 > 60 ? 1 : 0;
                } else {
                    categoryData.maxStage = int0 > 67 ? 2 : (int0 > 50 ? 1 : 0);
                }

                categoryData.gameObj = (Integer)arrayList.get(square1.rand(square0.x, square0.y, arrayList.size()));
                categoryData.stage = 0;
                categoryData.spawnTime = 100 - int0;
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void update(IsoGridSquare square1, ErosionData.Square square0, ErosionCategory.Data data, ErosionData.Chunk var4, int int0) {
        NatureGeneric.CategoryData categoryData = (NatureGeneric.CategoryData)data;
        if (int0 >= categoryData.spawnTime && !categoryData.doNothing) {
            if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
                ErosionObj erosionObj = this.objs.get(categoryData.gameObj);
                int int1 = categoryData.maxStage;
                int int2 = (int)Math.floor((int0 - categoryData.spawnTime) / (erosionObj.cycleTime / (int1 + 1.0F)));
                if (int2 > int1) {
                    int2 = int1;
                }

                if (int2 >= erosionObj.stages) {
                    int2 = erosionObj.stages - 1;
                }

                if (categoryData.stage == categoryData.maxStage) {
                    int2 = categoryData.maxStage;
                }

                int int3 = 0;
                if (!categoryData.notGrass) {
                    int3 = this.currentSeason(square0.magicNum, erosionObj);
                    int int4 = this.getGroundGrassType(square1);
                    if (int4 == 2) {
                        int3 = Math.max(int3, 3);
                    } else if (int4 == 3) {
                        int3 = Math.max(int3, 4);
                    }
                }

                boolean boolean0 = false;
                boolean boolean1 = false;
                this.updateObj(square0, data, square1, erosionObj, boolean0, int2, int3, boolean1);
            } else {
                categoryData.doNothing = true;
            }
        }
    }

    @Override
    public void init() {
        for (int int0 = 0; int0 < 100; int0++) {
            this.spawnChance[int0] = (int)this.clerp((int0 - 0) / 100.0F, 0.0F, 99.0F);
        }

        this.seasonDisp[5].season1 = 5;
        this.seasonDisp[5].season2 = 0;
        this.seasonDisp[5].split = false;
        this.seasonDisp[1].season1 = 1;
        this.seasonDisp[1].season2 = 0;
        this.seasonDisp[1].split = false;
        this.seasonDisp[2].season1 = 2;
        this.seasonDisp[2].season2 = 3;
        this.seasonDisp[2].split = true;
        this.seasonDisp[4].season1 = 4;
        this.seasonDisp[4].season2 = 5;
        this.seasonDisp[4].split = true;
        int[] ints0 = new int[]{1, 2, 3, 4, 5};
        int[] ints1 = new int[]{2, 1, 0};

        for (int int1 = 0; int1 < 3; int1++) {
            this.objsRef.add(new ArrayList<>());
        }

        for (int int2 = 0; int2 <= 5; int2++) {
            ErosionObjSprites erosionObjSprites0 = new ErosionObjSprites(3, "Grass", false, false, false);

            for (int int3 = 0; int3 < ints0.length; int3++) {
                for (int int4 = 0; int4 < ints1.length; int4++) {
                    int int5 = 0 + int3 * 18 + int4 * 6 + int2;
                    erosionObjSprites0.setBase(ints1[int4], "e_newgrass_1_" + int5, ints0[int3]);
                }
            }

            ErosionObj erosionObj0 = new ErosionObj(erosionObjSprites0, 60, 0.0F, 0.0F, false);
            this.objs.add(erosionObj0);
            this.objsRef.get(0).add(this.objs.size() - 1);
        }

        for (int int6 = 0; int6 <= 15; int6++) {
            ErosionObjSprites erosionObjSprites1 = new ErosionObjSprites(2, "Generic", false, false, false);

            for (int int7 = 0; int7 <= 1; int7++) {
                int int8 = int7 * 16 + int6;
                erosionObjSprites1.setBase(int7, "d_generic_1_" + int8, 0);
            }

            ErosionObj erosionObj1 = new ErosionObj(erosionObjSprites1, 60, 0.0F, 0.0F, true);
            this.objs.add(erosionObj1);
            this.objsRef.get(2).add(this.objs.size() - 1);
        }

        ErosionIceQueen erosionIceQueen = ErosionIceQueen.instance;

        for (int int9 = 0; int9 <= 7; int9++) {
            ErosionObjSprites erosionObjSprites2 = new ErosionObjSprites(2, "Fern", true, false, false);

            for (int int10 = 0; int10 <= 1; int10++) {
                int int11 = 48 + int10 * 32 + int9;
                erosionObjSprites2.setBase(int10, "d_generic_1_" + int11, 0);
                erosionIceQueen.addSprite("d_generic_1_" + int11, "d_generic_1_" + (int11 + 16));
            }

            ErosionObj erosionObj2 = new ErosionObj(erosionObjSprites2, 60, 0.0F, 0.0F, true);
            this.objs.add(erosionObj2);
            this.objsRef.get(1).add(this.objs.size() - 1);
        }
    }

    @Override
    protected ErosionCategory.Data allocData() {
        return new NatureGeneric.CategoryData();
    }

    private int toInt(char char0) {
        switch (char0) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            default:
                return 0;
        }
    }

    private int getGroundGrassType(IsoGridSquare square) {
        IsoObject object = square.getFloor();
        if (object == null) {
            return 0;
        } else {
            IsoSprite sprite = object.getSprite();
            if (sprite != null && sprite.getName() != null && sprite.getName().startsWith("blends_natural_01_")) {
                int int0 = 0;

                for (int int1 = 18; int1 < sprite.getName().length(); int1++) {
                    int0 += this.toInt(sprite.getName().charAt(int1));
                    if (int1 < sprite.getName().length() - 1) {
                        int0 *= 10;
                    }
                }

                int int2 = int0 / 8;
                int int3 = int0 % 8;
                if (int2 == 2 && (int3 == 0 || int3 >= 5)) {
                    return 1;
                }

                if (int2 == 4 && (int3 == 0 || int3 >= 5)) {
                    return 2;
                }

                if (int2 == 6 && (int3 == 0 || int3 >= 5)) {
                    return 3;
                }
            }

            return 0;
        }
    }

    @Override
    public void getObjectNames(ArrayList<String> arrayList) {
        for (int int0 = 0; int0 < this.objs.size(); int0++) {
            if (this.objs.get(int0).name != null && !arrayList.contains(this.objs.get(int0).name)) {
                arrayList.add(this.objs.get(int0).name);
            }
        }
    }

    private static final class CategoryData extends ErosionCategory.Data {
        public int gameObj;
        public int maxStage;
        public int spawnTime;
        public boolean notGrass;

        @Override
        public void save(ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.put((byte)this.maxStage);
            byteBuffer.putShort((short)this.spawnTime);
            byteBuffer.put((byte)(this.notGrass ? 1 : 0));
        }

        @Override
        public void load(ByteBuffer byteBuffer, int int0) {
            super.load(byteBuffer, int0);
            this.gameObj = byteBuffer.get();
            this.maxStage = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
            this.notGrass = byteBuffer.get() == 1;
        }
    }
}
