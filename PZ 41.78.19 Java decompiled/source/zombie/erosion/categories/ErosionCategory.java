// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.erosion.categories;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionData;
import zombie.erosion.ErosionMain;
import zombie.erosion.ErosionRegions;
import zombie.erosion.obj.ErosionObj;
import zombie.erosion.season.ErosionSeason;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;

public abstract class ErosionCategory {
    public int ID;
    public ErosionRegions.Region region;
    protected ErosionCategory.SeasonDisplay[] seasonDisp = new ErosionCategory.SeasonDisplay[6];

    public ErosionCategory() {
        for (int int0 = 0; int0 < 6; int0++) {
            this.seasonDisp[int0] = new ErosionCategory.SeasonDisplay();
        }
    }

    protected ErosionCategory.Data getCatModData(ErosionData.Square square) {
        for (int int0 = 0; int0 < square.regions.size(); int0++) {
            ErosionCategory.Data data = square.regions.get(int0);
            if (data.regionID == this.region.ID && data.categoryID == this.ID) {
                return data;
            }
        }

        return null;
    }

    protected ErosionCategory.Data setCatModData(ErosionData.Square square) {
        ErosionCategory.Data data = this.getCatModData(square);
        if (data == null) {
            data = this.allocData();
            data.regionID = this.region.ID;
            data.categoryID = this.ID;
            square.regions.add(data);
            if (square.regions.size() > 5) {
                DebugLog.log("> 5 regions on a square");
            }
        }

        return data;
    }

    protected IsoObject validWall(IsoGridSquare square0, boolean boolean0, boolean boolean1) {
        if (square0 == null) {
            return null;
        } else {
            IsoGridSquare square1 = boolean0 ? square0.getTileInDirection(IsoDirections.N) : square0.getTileInDirection(IsoDirections.W);
            Object object = null;
            if (square0.isWallTo(square1)) {
                if (boolean0 && square0.Is(IsoFlagType.cutN) && !square0.Is(IsoFlagType.canPathN)
                    || !boolean0 && square0.Is(IsoFlagType.cutW) && !square0.Is(IsoFlagType.canPathW)) {
                    object = square0.getWall(boolean0);
                }
            } else if (boolean1 && (square0.isWindowBlockedTo(square1) || square0.isWindowTo(square1))) {
                object = square0.getWindowTo(square1);
                if (object == null) {
                    object = square0.getWall(boolean0);
                }
            }

            if (object != null) {
                if (square0.getZ() > 0) {
                    String string = ((IsoObject)object).getSprite().getName();
                    return (IsoObject)(string != null && !string.contains("roof") ? object : null);
                } else {
                    return (IsoObject)object;
                }
            } else {
                return null;
            }
        }
    }

    protected float clerp(float float1, float float3, float float2) {
        float float0 = (float)(1.0 - Math.cos(float1 * Math.PI)) / 2.0F;
        return float3 * (1.0F - float0) + float2 * float0;
    }

    protected int currentSeason(float float4, ErosionObj var2) {
        int int0 = 0;
        ErosionSeason erosionSeason = ErosionMain.getInstance().getSeasons();
        int int1 = erosionSeason.getSeason();
        float float0 = erosionSeason.getSeasonDay();
        float float1 = erosionSeason.getSeasonDays();
        float float2 = float1 / 2.0F;
        float float3 = float2 * float4;
        ErosionCategory.SeasonDisplay seasonDisplay0 = this.seasonDisp[int1];
        if (seasonDisplay0.split && float0 >= float2 + float3) {
            int0 = seasonDisplay0.season2;
        } else if ((!seasonDisplay0.split || !(float0 >= float3)) && !(float0 >= float1 * float4)) {
            ErosionCategory.SeasonDisplay seasonDisplay1;
            if (int1 == 5) {
                seasonDisplay1 = this.seasonDisp[4];
            } else if (int1 == 1) {
                seasonDisplay1 = this.seasonDisp[5];
            } else if (int1 == 2) {
                seasonDisplay1 = this.seasonDisp[1];
            } else {
                seasonDisplay1 = this.seasonDisp[2];
            }

            if (seasonDisplay1.split) {
                int0 = seasonDisplay1.season2;
            } else {
                int0 = seasonDisplay1.season1;
            }
        } else {
            int0 = seasonDisplay0.season1;
        }

        return int0;
    }

    protected boolean currentBloom(float float4, ErosionObj erosionObj) {
        boolean boolean0 = false;
        ErosionSeason erosionSeason = ErosionMain.getInstance().getSeasons();
        int int0 = erosionSeason.getSeason();
        if (erosionObj.hasFlower && int0 == 2) {
            float float0 = erosionSeason.getSeasonDay();
            float float1 = erosionSeason.getSeasonDays();
            float float2 = float1 / 2.0F;
            float float3 = float2 * float4;
            float float5 = float1 - float3;
            float float6 = float0 - float3;
            float float7 = float5 * erosionObj.bloomEnd;
            float float8 = float5 * erosionObj.bloomStart;
            float float9 = (float7 - float8) / 2.0F;
            float float10 = float9 * float4;
            float7 = float8 + float9 + float10;
            float8 += float10;
            if (float6 >= float8 && float6 <= float7) {
                boolean0 = true;
            }
        }

        return boolean0;
    }

    public void updateObj(
        ErosionData.Square square1,
        ErosionCategory.Data data,
        IsoGridSquare square0,
        ErosionObj erosionObj,
        boolean boolean0,
        int int0,
        int int1,
        boolean boolean1
    ) {
        if (!data.hasSpawned) {
            if (!erosionObj.placeObject(square0, int0, boolean0, int1, boolean1)) {
                this.clearCatModData(square1);
                return;
            }

            data.hasSpawned = true;
        } else if (data.stage != int0 || data.dispSeason != int1 || data.dispBloom != boolean1) {
            IsoObject object = erosionObj.getObject(square0, false);
            if (object == null) {
                this.clearCatModData(square1);
                return;
            }

            erosionObj.setStageObject(int0, object, int1, boolean1);
        }

        data.stage = int0;
        data.dispSeason = int1;
        data.dispBloom = boolean1;
    }

    protected void clearCatModData(ErosionData.Square square) {
        for (int int0 = 0; int0 < square.regions.size(); int0++) {
            ErosionCategory.Data data = square.regions.get(int0);
            if (data.regionID == this.region.ID && data.categoryID == this.ID) {
                square.regions.remove(int0);
                return;
            }
        }
    }

    public abstract void init();

    public abstract boolean replaceExistingObject(IsoGridSquare var1, ErosionData.Square var2, ErosionData.Chunk var3, boolean var4, boolean var5);

    public abstract boolean validateSpawn(IsoGridSquare var1, ErosionData.Square var2, ErosionData.Chunk var3, boolean var4, boolean var5, boolean var6);

    public abstract void update(IsoGridSquare var1, ErosionData.Square var2, ErosionCategory.Data var3, ErosionData.Chunk var4, int var5);

    protected abstract ErosionCategory.Data allocData();

    public static ErosionCategory.Data loadCategoryData(ByteBuffer byteBuffer, int int0) {
        byte byte0 = byteBuffer.get();
        byte byte1 = byteBuffer.get();
        ErosionCategory erosionCategory = ErosionRegions.getCategory(byte0, byte1);
        ErosionCategory.Data data = erosionCategory.allocData();
        data.regionID = byte0;
        data.categoryID = byte1;
        data.load(byteBuffer, int0);
        return data;
    }

    public abstract void getObjectNames(ArrayList<String> var1);

    public static class Data {
        public int regionID;
        public int categoryID;
        public boolean doNothing;
        public boolean hasSpawned;
        public int stage;
        public int dispSeason;
        public boolean dispBloom;

        public void save(ByteBuffer byteBuffer) {
            byte byte0 = 0;
            if (this.doNothing) {
                byte0 = (byte)(byte0 | 1);
            }

            if (this.hasSpawned) {
                byte0 = (byte)(byte0 | 2);
            }

            if (this.dispBloom) {
                byte0 = (byte)(byte0 | 4);
            }

            if (this.stage == 1) {
                byte0 = (byte)(byte0 | 8);
            } else if (this.stage == 2) {
                byte0 = (byte)(byte0 | 16);
            } else if (this.stage == 3) {
                byte0 = (byte)(byte0 | 32);
            } else if (this.stage == 4) {
                byte0 = (byte)(byte0 | 64);
            } else if (this.stage > 4) {
                byte0 = (byte)(byte0 | 128);
            }

            byteBuffer.put((byte)this.regionID);
            byteBuffer.put((byte)this.categoryID);
            byteBuffer.put((byte)this.dispSeason);
            byteBuffer.put(byte0);
            if (this.stage > 4) {
                byteBuffer.put((byte)this.stage);
            }
        }

        public void load(ByteBuffer byteBuffer, int var2) {
            this.stage = 0;
            this.dispSeason = byteBuffer.get();
            byte byte0 = byteBuffer.get();
            this.doNothing = (byte0 & 1) != 0;
            this.hasSpawned = (byte0 & 2) != 0;
            this.dispBloom = (byte0 & 4) != 0;
            if ((byte0 & 8) != 0) {
                this.stage = 1;
            } else if ((byte0 & 16) != 0) {
                this.stage = 2;
            } else if ((byte0 & 32) != 0) {
                this.stage = 3;
            } else if ((byte0 & 64) != 0) {
                this.stage = 4;
            } else if ((byte0 & 128) != 0) {
                this.stage = byteBuffer.get();
            }
        }
    }

    protected class SeasonDisplay {
        int season1;
        int season2;
        boolean split;
    }
}
