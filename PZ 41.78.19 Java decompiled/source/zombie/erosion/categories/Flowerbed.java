// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.obj.ErosionObjSprites;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.sprite.IsoSprite;

public final class Flowerbed extends ErosionCategory {
    private final int[] tileID = new int[]{16, 17, 18, 19, 20, 21, 22, 23, 28, 29, 30, 31};
    private final ArrayList<ErosionObj> objs = new ArrayList<>();

    @Override
    public boolean replaceExistingObject(IsoGridSquare square0, ErosionData.Square square1, ErosionData.Chunk var3, boolean var4, boolean var5) {
        int int0 = square0.getObjects().size();

        for (int int1 = int0 - 1; int1 >= 0; int1--) {
            IsoSprite sprite = square0.getObjects().get(int1).getSprite();
            if (sprite != null && sprite.getName() != null) {
                if (sprite.getName().startsWith("f_flowerbed_1")) {
                    int int2 = Integer.parseInt(sprite.getName().replace("f_flowerbed_1_", ""));
                    if (int2 <= 23) {
                        if (int2 >= 12) {
                            int2 -= 12;
                        }

                        Flowerbed.CategoryData categoryData0 = (Flowerbed.CategoryData)this.setCatModData(square1);
                        categoryData0.hasSpawned = true;
                        categoryData0.gameObj = int2;
                        categoryData0.dispSeason = -1;
                        ErosionObj erosionObj0 = this.objs.get(categoryData0.gameObj);
                        square0.getObjects().get(int1).setName(erosionObj0.name);
                        return true;
                    }
                }

                if (sprite.getName().startsWith("vegetation_ornamental_01")) {
                    int int3 = Integer.parseInt(sprite.getName().replace("vegetation_ornamental_01_", ""));

                    for (int int4 = 0; int4 < this.tileID.length; int4++) {
                        if (this.tileID[int4] == int3) {
                            Flowerbed.CategoryData categoryData1 = (Flowerbed.CategoryData)this.setCatModData(square1);
                            categoryData1.hasSpawned = true;
                            categoryData1.gameObj = int4;
                            categoryData1.dispSeason = -1;
                            ErosionObj erosionObj1 = this.objs.get(categoryData1.gameObj);
                            square0.getObjects().get(int1).setName(erosionObj1.name);
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean validateSpawn(IsoGridSquare var1, ErosionData.Square var2, ErosionData.Chunk var3, boolean var4, boolean var5, boolean var6) {
        return false;
    }

    @Override
    public void update(IsoGridSquare square0, ErosionData.Square square1, ErosionCategory.Data data, ErosionData.Chunk var4, int var5) {
        Flowerbed.CategoryData categoryData = (Flowerbed.CategoryData)data;
        if (!categoryData.doNothing) {
            if (categoryData.gameObj >= 0 && categoryData.gameObj < this.objs.size()) {
                ErosionObj erosionObj = this.objs.get(categoryData.gameObj);
                boolean boolean0 = false;
                byte byte0 = 0;
                int int0 = ErosionMain.getInstance().getSeasons().getSeason();
                boolean boolean1 = false;
                if (int0 == 5) {
                    IsoObject object = erosionObj.getObject(square0, false);
                    if (object != null) {
                        object.setSprite(ErosionMain.getInstance().getSpriteManager().getSprite("blends_natural_01_64"));
                        object.setName(null);
                    }

                    this.clearCatModData(square1);
                } else {
                    this.updateObj(square1, data, square0, erosionObj, boolean0, byte0, int0, boolean1);
                }
            } else {
                this.clearCatModData(square1);
            }
        }
    }

    @Override
    public void init() {
        String string = "vegetation_ornamental_01_";

        for (int int0 = 0; int0 < this.tileID.length; int0++) {
            ErosionObjSprites erosionObjSprites = new ErosionObjSprites(1, "Flowerbed", false, false, false);
            erosionObjSprites.setBase(0, string + this.tileID[int0], 1);
            erosionObjSprites.setBase(0, string + this.tileID[int0], 2);
            erosionObjSprites.setBase(0, string + (this.tileID[int0] + 16), 4);
            ErosionObj erosionObj = new ErosionObj(erosionObjSprites, 30, 0.0F, 0.0F, false);
            this.objs.add(erosionObj);
        }
    }

    @Override
    protected ErosionCategory.Data allocData() {
        return new Flowerbed.CategoryData();
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

        @Override
        public void save(ByteBuffer byteBuffer) {
            super.save(byteBuffer);
            byteBuffer.put((byte)this.gameObj);
        }

        @Override
        public void load(ByteBuffer byteBuffer, int int0) {
            super.load(byteBuffer, int0);
            this.gameObj = byteBuffer.get();
        }
    }
}
