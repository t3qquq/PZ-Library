// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.obj.ErosionObjOverlay;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoSprite;

public final class WallVines extends ErosionCategory {
    private ArrayList<ErosionObjOverlay> objs = new ArrayList<>();
    private static final int DIRNW = 0;
    private static final int DIRN = 1;
    private static final int DIRW = 2;
    private int[][] objsRef = new int[3][2];
    private HashMap<String, Integer> spriteToObj = new HashMap<>();
    private HashMap<String, Integer> spriteToStage = new HashMap<>();
    private int[] spawnChance = new int[100];

    @Override
    public boolean replaceExistingObject(IsoGridSquare square0, ErosionData.Square square1, ErosionData.Chunk var3, boolean var4, boolean var5) {
        int int0 = square0.getObjects().size();

        for (int int1 = int0 - 1; int1 >= 1; int1--) {
            IsoObject object = square0.getObjects().get(int1);
            if (object.AttachedAnimSprite != null) {
                for (int int2 = 0; int2 < object.AttachedAnimSprite.size(); int2++) {
                    IsoSprite sprite = object.AttachedAnimSprite.get(int2).parentSprite;
                    if (sprite != null
                        && sprite.getName() != null
                        && sprite.getName().startsWith("f_wallvines_1_")
                        && this.spriteToObj.containsKey(sprite.getName())) {
                        WallVines.CategoryData categoryData = (WallVines.CategoryData)this.setCatModData(square1);
                        categoryData.gameObj = this.spriteToObj.get(sprite.getName());
                        int int3 = this.spriteToStage.get(sprite.getName());
                        categoryData.stage = int3;
                        categoryData.maxStage = 2;
                        categoryData.spawnTime = 0;
                        object.AttachedAnimSprite.remove(int2);
                        if (object.AttachedAnimSprite != null && int2 < object.AttachedAnimSprite.size()) {
                            object.AttachedAnimSprite.remove(int2);
                        }

                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean validateSpawn(IsoGridSquare square1, ErosionData.Square square0, ErosionData.Chunk var3, boolean boolean0, boolean var5, boolean var6) {
        if (!boolean0) {
            return false;
        } else {
            int int0 = square0.noiseMainInt;
            int int1 = this.spawnChance[int0];
            if (int1 == 0) {
                return false;
            } else if (square0.rand(square1.x, square1.y, 101) >= int1) {
                return false;
            } else {
                byte byte0 = -1;
                IsoObject object0 = this.validWall(square1, true, true);
                IsoObject object1 = this.validWall(square1, false, true);
                if (object0 != null && object1 != null) {
                    byte0 = 0;
                } else if (object0 != null) {
                    byte0 = 1;
                } else {
                    if (object1 == null) {
                        return false;
                    }

                    byte0 = 2;
                }

                WallVines.CategoryData categoryData0 = (WallVines.CategoryData)this.setCatModData(square0);
                categoryData0.gameObj = this.objsRef[byte0][square0.rand(square1.x, square1.y, this.objsRef[byte0].length)];
                categoryData0.maxStage = int0 > 65 ? 3 : (int0 > 60 ? 2 : (int0 > 55 ? 1 : 0));
                categoryData0.stage = 0;
                categoryData0.spawnTime = 100 - int0;
                if (categoryData0.maxStage == 3) {
                    IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(square1.getX(), square1.getY(), square1.getZ() + 1);
                    if (square2 != null) {
                        IsoObject object2 = this.validWall(square2, byte0 == 1, true);
                        ErosionObjOverlay erosionObjOverlay = this.objs.get(categoryData0.gameObj);
                        if (object2 != null && erosionObjOverlay != null) {
                            WallVines.CategoryData categoryData1 = new WallVines.CategoryData();
                            categoryData1.gameObj = this.objsRef[byte0][square0.rand(square1.x, square1.y, this.objsRef[byte0].length)];
                            categoryData1.maxStage = int0 > 75 ? 2 : (int0 > 70 ? 1 : 0);
                            categoryData1.stage = 0;
                            categoryData1.spawnTime = categoryData0.spawnTime + (int)(erosionObjOverlay.cycleTime / (categoryData0.maxStage + 1.0F) * 4.0F);
                            categoryData0.hasTop = categoryData1;
                        } else {
                            categoryData0.maxStage = 2;
                        }
                    } else {
                        categoryData0.maxStage = 2;
                    }
                }

                return true;
            }
        }
    }

    @Override
    public void update(IsoGridSquare square0, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int0) {
        WallVines.CategoryData categoryData = (WallVines.CategoryData)data;
        if (int0 >= categoryData.spawnTime && !categoryData.doNothing) {
            if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
                ErosionObjOverlay erosionObjOverlay = this.objs.get(categoryData.gameObj);
                int int1 = categoryData.maxStage;
                int int2 = (int)Math.floor((int0 - categoryData.spawnTime) / (erosionObjOverlay.cycleTime / (int1 + 1.0F)));
                if (int2 < categoryData.stage) {
                    int2 = categoryData.stage;
                }

                if (int2 > int1) {
                    int2 = int1;
                }

                if (int2 > erosionObjOverlay.stages) {
                    int2 = erosionObjOverlay.stages;
                }

                if (int2 == 3 && categoryData.hasTop != null && categoryData.hasTop.spawnTime > int0) {
                    int2 = 2;
                }

                int int3 = ErosionMain.getInstance().getSeasons().getSeason();
                if (int2 != categoryData.stage || categoryData.dispSeason != int3) {
                    IsoObject object0 = null;
                    IsoObject object1 = this.validWall(square0, true, true);
                    IsoObject object2 = this.validWall(square0, false, true);
                    if (object1 != null && object2 != null) {
                        object0 = object1;
                    } else if (object1 != null) {
                        object0 = object1;
                    } else if (object2 != null) {
                        object0 = object2;
                    }

                    categoryData.dispSeason = int3;
                    if (object0 != null) {
                        int int4 = categoryData.curID;
                        int int5 = erosionObjOverlay.setOverlay(object0, int4, int2, int3, 0.0F);
                        if (int5 >= 0) {
                            categoryData.curID = int5;
                        }
                    } else {
                        categoryData.doNothing = true;
                    }

                    if (int2 == 3 && categoryData.hasTop != null) {
                        IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(square0.getX(), square0.getY(), square0.getZ() + 1);
                        if (square1 != null) {
                            this.update(square1, square2, categoryData.hasTop, chunk, int0);
                        }
                    }
                }
            } else {
                categoryData.doNothing = true;
            }
        }
    }

    @Override
    public void init() {
        for (int int0 = 0; int0 < 100; int0++) {
            this.spawnChance[int0] = int0 >= 50 ? 100 : 0;
        }

        this.seasonDisp[5].season1 = 5;
        this.seasonDisp[5].season2 = 0;
        this.seasonDisp[5].split = false;
        this.seasonDisp[1].season1 = 1;
        this.seasonDisp[1].season2 = 0;
        this.seasonDisp[1].split = false;
        this.seasonDisp[2].season1 = 2;
        this.seasonDisp[2].season2 = 4;
        this.seasonDisp[2].split = true;
        this.seasonDisp[4].season1 = 4;
        this.seasonDisp[4].season2 = 5;
        this.seasonDisp[4].split = true;
        String string = "f_wallvines_1_";
        int[] ints0 = new int[]{5, 2, 4, 1};
        int[] ints1 = new int[]{2, 2, 1, 1, 0, 0};
        int[] ints2 = new int[3];

        for (int int1 = 0; int1 < ints1.length; int1++) {
            ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(4, "WallVines");

            for (int int2 = 0; int2 <= 3; int2++) {
                for (int int3 = 0; int3 <= 2; int3++) {
                    int int4 = int3 * 24 + int2 * 6 + int1;
                    erosionObjOverlaySprites.setSprite(int2, string + int4, ints0[int3]);
                    if (int3 == 2) {
                        erosionObjOverlaySprites.setSprite(int2, string + int4, ints0[int3 + 1]);
                    }

                    this.spriteToObj.put(string + int4, this.objs.size());
                    this.spriteToStage.put(string + int4, int2);
                }
            }

            this.objs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, false));
            this.objsRef[ints1[int1]][ints2[ints1[int1]]++] = this.objs.size() - 1;
        }
    }

    @Override
    protected ErosionCategory.Data allocData() {
        return new WallVines.CategoryData();
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
        public int curID = -999999;
        public WallVines.CategoryData hasTop;

        @Override
        public void save(ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.put((byte)this.maxStage);
            byteBuffer.putShort((short)this.spawnTime);
            byteBuffer.putInt(this.curID);
            if (this.hasTop != null) {
                byteBuffer.put((byte)1);
                byteBuffer.put((byte)this.hasTop.gameObj);
                byteBuffer.putShort((short)this.hasTop.spawnTime);
                byteBuffer.putInt(this.hasTop.curID);
            } else {
                byteBuffer.put((byte)0);
            }
        }

        @Override
        public void load(ByteBuffer byteBuffer, int int0) {
            super.load(byteBuffer, int0);
            this.gameObj = byteBuffer.get();
            this.maxStage = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
            this.curID = byteBuffer.getInt();
            boolean boolean0 = byteBuffer.get() == 1;
            if (boolean0) {
                this.hasTop = new WallVines.CategoryData();
                this.hasTop.gameObj = byteBuffer.get();
                this.hasTop.spawnTime = byteBuffer.getShort();
                this.hasTop.curID = byteBuffer.getInt();
            }
        }
    }
}
