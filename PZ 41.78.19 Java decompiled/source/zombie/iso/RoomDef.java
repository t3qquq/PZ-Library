// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.joml.Vector2f;
import zombie.iso.areas.IsoRoom;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.util.list.PZArrayUtil;

public final class RoomDef {
    private static final ArrayList<IsoGridSquare> squareChoices = new ArrayList<>();
    public boolean bExplored = false;
    public boolean bDoneSpawn = false;
    public int IndoorZombies = 0;
    public int spawnCount = -1;
    public boolean bLightsActive = false;
    public String name;
    public int level;
    public BuildingDef building;
    public int ID = -1;
    public final ArrayList<RoomDef.RoomRect> rects = new ArrayList<>(1);
    public final ArrayList<MetaObject> objects = new ArrayList<>(0);
    public int x = 100000;
    public int y = 100000;
    public int x2 = -10000;
    public int y2 = -10000;
    public int area;
    private final HashMap<String, Integer> proceduralSpawnedContainer = new HashMap<>();
    private boolean roofFixed = false;
    public long metaID;

    public RoomDef(int _ID, String _name) {
        this.ID = _ID;
        this.name = _name;
    }

    public int getID() {
        return this.ID;
    }

    public boolean isExplored() {
        return this.bExplored;
    }

    public boolean isInside(int _x, int _y, int z) {
        int int0 = this.building.x;
        int int1 = this.building.y;

        for (int int2 = 0; int2 < this.rects.size(); int2++) {
            int int3 = this.rects.get(int2).x;
            int int4 = this.rects.get(int2).y;
            int int5 = this.rects.get(int2).getX2();
            int int6 = this.rects.get(int2).getY2();
            if (_x >= int3 && _y >= int4 && _x < int5 && _y < int6 && z == this.level) {
                return true;
            }
        }

        return false;
    }

    public boolean intersects(int _x, int _y, int w, int h) {
        for (int int0 = 0; int0 < this.rects.size(); int0++) {
            RoomDef.RoomRect roomRect = this.rects.get(int0);
            if (_x + w > roomRect.getX() && _x < roomRect.getX2() && _y + h > roomRect.getY() && _y < roomRect.getY2()) {
                return true;
            }
        }

        return false;
    }

    public float getAreaOverlapping(IsoChunk chunk) {
        return this.getAreaOverlapping(chunk.wx * 10, chunk.wy * 10, 10, 10);
    }

    public float getAreaOverlapping(int _x, int _y, int w, int h) {
        int int0 = 0;
        int int1 = 0;

        for (int int2 = 0; int2 < this.rects.size(); int2++) {
            RoomDef.RoomRect roomRect = this.rects.get(int2);
            int0 += roomRect.w * roomRect.h;
            int int3 = Math.max(_x, roomRect.x);
            int int4 = Math.max(_y, roomRect.y);
            int int5 = Math.min(_x + w, roomRect.x + roomRect.w);
            int int6 = Math.min(_y + h, roomRect.y + roomRect.h);
            if (int5 >= int3 && int6 >= int4) {
                int1 += (int5 - int3) * (int6 - int4);
            }
        }

        return int1 <= 0 ? 0.0F : (float)int1 / int0;
    }

    public void forEachChunk(BiConsumer<RoomDef, IsoChunk> consumer) {
        HashSet hashSet = new HashSet();

        for (int int0 = 0; int0 < this.rects.size(); int0++) {
            RoomDef.RoomRect roomRect = this.rects.get(int0);
            int int1 = roomRect.x / 10;
            int int2 = roomRect.y / 10;
            int int3 = (roomRect.x + roomRect.w) / 10;
            int int4 = (roomRect.y + roomRect.h) / 10;
            if ((roomRect.x + roomRect.w) % 10 == 0) {
                int3--;
            }

            if ((roomRect.y + roomRect.h) % 10 == 0) {
                int4--;
            }

            for (int int5 = int2; int5 <= int4; int5++) {
                for (int int6 = int1; int6 <= int3; int6++) {
                    IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int6, int5) : IsoWorld.instance.CurrentCell.getChunk(int6, int5);
                    if (chunk != null) {
                        hashSet.add(chunk);
                    }
                }
            }
        }

        hashSet.forEach(chunkx -> consumer.accept(this, chunkx));
        hashSet.clear();
    }

    public IsoRoom getIsoRoom() {
        return IsoWorld.instance.MetaGrid.getMetaGridFromTile(this.x, this.y).info.getRoom(this.ID);
    }

    public ArrayList<MetaObject> getObjects() {
        return this.objects;
    }

    public ArrayList<MetaObject> getMetaObjects() {
        return this.objects;
    }

    public void refreshSquares() {
        this.getIsoRoom().refreshSquares();
    }

    public BuildingDef getBuilding() {
        return this.building;
    }

    public void setBuilding(BuildingDef def) {
        this.building = def;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<RoomDef.RoomRect> getRects() {
        return this.rects;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public int getX2() {
        return this.x2;
    }

    public int getY2() {
        return this.y2;
    }

    public int getW() {
        return this.x2 - this.x;
    }

    public int getH() {
        return this.y2 - this.y;
    }

    public int getZ() {
        return this.level;
    }

    public void CalculateBounds() {
        for (int int0 = 0; int0 < this.rects.size(); int0++) {
            RoomDef.RoomRect roomRect = this.rects.get(int0);
            if (roomRect.x < this.x) {
                this.x = roomRect.x;
            }

            if (roomRect.y < this.y) {
                this.y = roomRect.y;
            }

            if (roomRect.x + roomRect.w > this.x2) {
                this.x2 = roomRect.x + roomRect.w;
            }

            if (roomRect.y + roomRect.h > this.y2) {
                this.y2 = roomRect.y + roomRect.h;
            }

            this.area = this.area + roomRect.w * roomRect.h;
        }
    }

    public long calculateMetaID(int cellX, int cellY) {
        int int0 = Integer.MAX_VALUE;
        int int1 = Integer.MAX_VALUE;

        for (int int2 = 0; int2 < this.rects.size(); int2++) {
            RoomDef.RoomRect roomRect = this.rects.get(int2);
            if (roomRect.x <= int0 && roomRect.y < int1) {
                int0 = roomRect.x;
                int1 = roomRect.y;
            }
        }

        int0 -= cellX * 300;
        int1 -= cellY * 300;
        return (long)this.level << 32 | (long)int1 << 16 | int0;
    }

    public int getArea() {
        return this.area;
    }

    public void setExplored(boolean explored) {
        this.bExplored = explored;
    }

    public IsoGridSquare getFreeSquare() {
        return this.getRandomSquare(square -> square.isFree(false));
    }

    public IsoGridSquare getRandomSquare(Predicate<IsoGridSquare> predicate) {
        squareChoices.clear();

        for (int int0 = 0; int0 < this.rects.size(); int0++) {
            RoomDef.RoomRect roomRect = this.rects.get(int0);

            for (int int1 = roomRect.getX(); int1 < roomRect.getX2(); int1++) {
                for (int int2 = roomRect.getY(); int2 < roomRect.getY2(); int2++) {
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int1, int2, this.getZ());
                    if (square != null && predicate != null && predicate.test(square) || predicate == null) {
                        squareChoices.add(square);
                    }
                }
            }
        }

        return PZArrayUtil.pickRandom(squareChoices);
    }

    public boolean isEmptyOutside() {
        return "emptyoutside".equalsIgnoreCase(this.name);
    }

    public HashMap<String, Integer> getProceduralSpawnedContainer() {
        return this.proceduralSpawnedContainer;
    }

    public boolean isRoofFixed() {
        return this.roofFixed;
    }

    public void setRoofFixed(boolean b) {
        this.roofFixed = b;
    }

    public float getClosestPoint(float _x, float _y, Vector2f closestXY) {
        float float0 = Float.MAX_VALUE;
        Vector2f vector2f = new Vector2f();

        for (int int0 = 0; int0 < this.rects.size(); int0++) {
            RoomDef.RoomRect roomRect = this.rects.get(int0);
            float float1 = roomRect.getClosestPoint(_x, _y, vector2f);
            if (float1 < float0) {
                float0 = float1;
                closestXY.set(vector2f);
            }
        }

        return float0;
    }

    public void Dispose() {
        this.building = null;
        this.rects.clear();
        this.objects.clear();
        this.proceduralSpawnedContainer.clear();
    }

    public static class RoomRect {
        public int x;
        public int y;
        public int w;
        public int h;

        public RoomRect(int _x, int _y, int _w, int _h) {
            this.x = _x;
            this.y = _y;
            this.w = _w;
            this.h = _h;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getX2() {
            return this.x + this.w;
        }

        public int getY2() {
            return this.y + this.h;
        }

        public int getW() {
            return this.w;
        }

        public int getH() {
            return this.h;
        }

        public float getClosestPoint(float _x, float _y, Vector2f closestXY) {
            float float0 = Float.MAX_VALUE;
            float0 = this.getClosestPointOnEdge(this.x + 0.5F, this.y + 0.5F, this.x + this.w - 0.5F, this.y + 0.5F, _x, _y, float0, closestXY);
            float0 = this.getClosestPointOnEdge(
                this.x + this.w - 0.5F, this.y + 0.5F, this.x + this.w - 0.5F, this.y + this.h - 0.5F, _x, _y, float0, closestXY
            );
            float0 = this.getClosestPointOnEdge(
                this.x + this.w - 0.5F, this.y + this.h - 0.5F, this.x + 0.5F, this.y + this.h - 0.5F, _x, _y, float0, closestXY
            );
            return this.getClosestPointOnEdge(this.x + 0.5F, this.y + this.h - 0.5F, this.x + 0.5F, this.y + 0.5F, _x, _y, float0, closestXY);
        }

        private float getClosestPointOnEdge(float float3, float float1, float float2, float float0, float float5, float float4, float float6, Vector2f vector2f) {
            double double0 = ((float5 - float3) * (float2 - float3) + (float4 - float1) * (float0 - float1))
                / (Math.pow(float2 - float3, 2.0) + Math.pow(float0 - float1, 2.0));
            double double1 = float3 + double0 * (float2 - float3);
            double double2 = float1 + double0 * (float0 - float1);
            if (double0 <= 0.0) {
                double1 = float3;
                double2 = float1;
            } else if (double0 >= 1.0) {
                double1 = float2;
                double2 = float0;
            }

            double double3 = (float5 - double1) * (float5 - double1) + (float4 - double2) * (float4 - double2);
            if (double3 < float6) {
                if (float3 == float2) {
                    double2 = (int)double2 + 0.5F;
                } else {
                    double1 = (int)double1 + 0.5F;
                }

                vector2f.set(double1, double2);
                return (float)double3;
            } else {
                return float6;
            }
        }
    }
}
