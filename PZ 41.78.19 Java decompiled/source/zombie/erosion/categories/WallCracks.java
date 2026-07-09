// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.erosion.ErosionData;
import zombie.erosion.obj.ErosionObjOverlay;
import zombie.erosion.obj.ErosionObjOverlaySprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;

public final class WallCracks extends ErosionCategory {
    private ArrayList<ErosionObjOverlay> objs = new ArrayList<>();
    private static final int DIRNW = 0;
    private static final int DIRN = 1;
    private static final int DIRW = 2;
    private ArrayList<ArrayList<Integer>> objsRef = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> botRef = new ArrayList<>();
    private ArrayList<ArrayList<Integer>> topRef = new ArrayList<>();
    private int[] spawnChance = new int[100];

    @Override
    public boolean replaceExistingObject(IsoGridSquare var1, ErosionData.Square var2, ErosionData.Chunk var3, boolean var4, boolean var5) {
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
                IsoObject object0 = this.validWall(square1, true, false);
                if (object0 != null) {
                    String string0 = object0.getSprite().getName();
                    if (string0 != null && string0.startsWith("fencing")) {
                        object0 = null;
                    }
                }

                IsoObject object1 = this.validWall(square1, false, false);
                if (object1 != null) {
                    String string1 = object1.getSprite().getName();
                    if (string1 != null && string1.startsWith("fencing")) {
                        object1 = null;
                    }
                }

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

                boolean boolean1 = int0 < 35 && square0.magicNum > 0.3F;
                WallCracks.CategoryData categoryData0 = (WallCracks.CategoryData)this.setCatModData(square0);
                categoryData0.gameObj = this.objsRef.get(byte0).get(square0.rand(square1.x, square1.y, this.objsRef.get(byte0).size()));
                categoryData0.alpha = 0.0F;
                categoryData0.spawnTime = int0;
                if (boolean1) {
                    IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(square1.getX(), square1.getY(), square1.getZ() + 1);
                    if (square2 != null) {
                        IsoObject object2 = this.validWall(square2, byte0 == 1, false);
                        if (object2 != null) {
                            int int2 = square0.rand(square1.x, square1.y, this.botRef.get(byte0).size());
                            categoryData0.gameObj = this.botRef.get(byte0).get(int2);
                            WallCracks.CategoryData categoryData1 = new WallCracks.CategoryData();
                            categoryData1.gameObj = this.topRef.get(byte0).get(int2);
                            categoryData1.alpha = 0.0F;
                            categoryData1.spawnTime = categoryData0.spawnTime;
                            categoryData0.hasTop = categoryData1;
                        }
                    }
                }

                return true;
            }
        }
    }

    @Override
    public void update(IsoGridSquare square0, ErosionData.Square square2, ErosionCategory.Data data, ErosionData.Chunk chunk, int int0) {
        WallCracks.CategoryData categoryData = (WallCracks.CategoryData)data;
        if (int0 >= categoryData.spawnTime && !categoryData.doNothing) {
            if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
                ErosionObjOverlay erosionObjOverlay = this.objs.get(categoryData.gameObj);
                float float0 = categoryData.alpha;
                float float1 = (int0 - categoryData.spawnTime) / 100.0F;
                if (float1 > 1.0F) {
                    float1 = 1.0F;
                }

                if (float1 < 0.0F) {
                    float1 = 0.0F;
                }

                if (float1 != float0) {
                    IsoObject object0 = null;
                    IsoObject object1 = this.validWall(square0, true, false);
                    IsoObject object2 = this.validWall(square0, false, false);
                    if (object1 != null && object2 != null) {
                        object0 = object1;
                    } else if (object1 != null) {
                        object0 = object1;
                    } else if (object2 != null) {
                        object0 = object2;
                    }

                    if (object0 != null) {
                        int int1 = categoryData.curID;
                        byte byte0 = 0;
                        int int2 = erosionObjOverlay.setOverlay(object0, int1, byte0, 0, float1);
                        if (int2 >= 0) {
                            categoryData.alpha = float1;
                            categoryData.curID = int2;
                        }
                    } else {
                        categoryData.doNothing = true;
                    }

                    if (categoryData.hasTop != null) {
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
            this.spawnChance[int0] = int0 <= 50 ? 100 : 0;
        }

        String string = "d_wallcracks_1_";
        int[] ints = new int[]{2, 2, 2, 1, 1, 1, 0, 0, 0};

        for (int int1 = 0; int1 < 3; int1++) {
            this.objsRef.add(new ArrayList<>());
            this.topRef.add(new ArrayList<>());
            this.botRef.add(new ArrayList<>());
        }

        for (int int2 = 0; int2 < ints.length; int2++) {
            for (int int3 = 0; int3 <= 7; int3++) {
                int int4 = int3 * 9 + int2;
                ErosionObjOverlaySprites erosionObjOverlaySprites = new ErosionObjOverlaySprites(1, "WallCracks");
                erosionObjOverlaySprites.setSprite(0, string + int4, 0);
                this.objs.add(new ErosionObjOverlay(erosionObjOverlaySprites, 60, true));
                this.objsRef.get(ints[int2]).add(this.objs.size() - 1);
                if (int3 == 0) {
                    this.botRef.get(ints[int2]).add(this.objs.size() - 1);
                } else if (int3 == 1) {
                    this.topRef.get(ints[int2]).add(this.objs.size() - 1);
                }
            }
        }
    }

    @Override
    protected ErosionCategory.Data allocData() {
        return new WallCracks.CategoryData();
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
        public int spawnTime;
        public int curID = -999999;
        public float alpha;
        public WallCracks.CategoryData hasTop;

        @Override
        public void save(ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
            byteBuffer.putShort((short)this.spawnTime);
            byteBuffer.putInt(this.curID);
            byteBuffer.putFloat(this.alpha);
            if (this.hasTop != null) {
                byteBuffer.put((byte)1);
                byteBuffer.put((byte)this.hasTop.gameObj);
                byteBuffer.putShort((short)this.hasTop.spawnTime);
                byteBuffer.putInt(this.hasTop.curID);
                byteBuffer.putFloat(this.hasTop.alpha);
            } else {
                byteBuffer.put((byte)0);
            }
        }

        @Override
        public void load(ByteBuffer byteBuffer, int int0) {
            super.load(byteBuffer, int0);
            this.gameObj = byteBuffer.get();
            this.spawnTime = byteBuffer.getShort();
            this.curID = byteBuffer.getInt();
            this.alpha = byteBuffer.getFloat();
            boolean boolean0 = byteBuffer.get() == 1;
            if (boolean0) {
                this.hasTop = new WallCracks.CategoryData();
                this.hasTop.gameObj = byteBuffer.get();
                this.hasTop.spawnTime = byteBuffer.getShort();
                this.hasTop.curID = byteBuffer.getInt();
                this.hasTop.alpha = byteBuffer.getFloat();
            }
        }
    }
}
