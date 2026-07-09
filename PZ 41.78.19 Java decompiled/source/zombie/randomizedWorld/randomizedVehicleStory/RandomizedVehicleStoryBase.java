// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedVehicleStory;

import java.util.HashMap;
import java.util.Iterator;
import org.joml.Vector2f;
import zombie.SandboxOptions;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.iso.IsoCell;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.vehicles.BaseVehicle;

public class RandomizedVehicleStoryBase extends RandomizedWorldBase {
    private int chance = 0;
    private static int totalChance = 0;
    private static HashMap<RandomizedVehicleStoryBase, Integer> rvsMap = new HashMap<>();
    protected boolean horizontalZone = false;
    protected int zoneWidth = 0;
    public static final float baseChance = 12.5F;
    protected int minX = 0;
    protected int minY = 0;
    protected int maxX = 0;
    protected int maxY = 0;
    protected int minZoneWidth = 0;
    protected int minZoneHeight = 0;

    /**
     * We init a map with every possible stories for this zone
     */
    public static void initAllRVSMapChance(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        totalChance = 0;
        rvsMap.clear();

        for (int int0 = 0; int0 < IsoWorld.instance.getRandomizedVehicleStoryList().size(); int0++) {
            RandomizedVehicleStoryBase randomizedVehicleStoryBase = IsoWorld.instance.getRandomizedVehicleStoryList().get(int0);
            if (randomizedVehicleStoryBase.isValid(zone, chunk, false) && randomizedVehicleStoryBase.isTimeValid(false)) {
                totalChance = totalChance + randomizedVehicleStoryBase.getChance();
                rvsMap.put(randomizedVehicleStoryBase, randomizedVehicleStoryBase.getChance());
            }
        }
    }

    public static boolean doRandomStory(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean force) {
        float float0 = Rand.Next(0.0F, 500.0F);
        switch (SandboxOptions.instance.VehicleStoryChance.getValue()) {
            case 1:
                return false;
            case 2:
                float0 = Rand.Next(0.0F, 1000.0F);
            case 3:
            default:
                break;
            case 4:
                float0 = Rand.Next(0.0F, 300.0F);
                break;
            case 5:
                float0 = Rand.Next(0.0F, 175.0F);
                break;
            case 6:
                float0 = Rand.Next(0.0F, 50.0F);
        }

        if (float0 < 12.5F) {
            if (!chunk.vehicles.isEmpty()) {
                return false;
            } else {
                RandomizedVehicleStoryBase randomizedVehicleStoryBase = null;
                initAllRVSMapChance(zone, chunk);
                randomizedVehicleStoryBase = getRandomStory();
                if (randomizedVehicleStoryBase == null) {
                    return false;
                } else {
                    VehicleStorySpawnData vehicleStorySpawnData = randomizedVehicleStoryBase.initSpawnDataForChunk(zone, chunk);
                    chunk.setRandomVehicleStoryToSpawnLater(vehicleStorySpawnData);
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    private static RandomizedVehicleStoryBase getRandomStory() {
        int int0 = Rand.Next(totalChance);
        Iterator iterator = rvsMap.keySet().iterator();
        int int1 = 0;

        while (iterator.hasNext()) {
            RandomizedVehicleStoryBase randomizedVehicleStoryBase = (RandomizedVehicleStoryBase)iterator.next();
            int1 += rvsMap.get(randomizedVehicleStoryBase);
            if (int0 < int1) {
                return randomizedVehicleStoryBase;
            }
        }

        return null;
    }

    public int getMinZoneWidth() {
        return this.minZoneWidth <= 0 ? 10 : this.minZoneWidth;
    }

    public int getMinZoneHeight() {
        return this.minZoneHeight <= 0 ? 5 : this.minZoneHeight;
    }

    public void randomizeVehicleStory(IsoMetaGrid.Zone zone, IsoChunk chunk) {
    }

    /**
     * Get the center of the chunk according to the zone (so center of the 10x10  chunk AND the zone)
     */
    public IsoGridSquare getCenterOfChunk(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        int int0 = Math.max(zone.x, chunk.wx * 10);
        int int1 = Math.max(zone.y, chunk.wy * 10);
        int int2 = Math.min(zone.x + zone.w, (chunk.wx + 1) * 10);
        int int3 = Math.min(zone.y + zone.h, (chunk.wy + 1) * 10);
        int int4 = 0;
        int int5 = 0;
        if (this.horizontalZone) {
            int5 = (zone.y + zone.y + zone.h) / 2;
            int4 = (int0 + int2) / 2;
        } else {
            int5 = (int1 + int3) / 2;
            int4 = (zone.x + zone.x + zone.w) / 2;
        }

        return IsoCell.getInstance().getGridSquare(int4, int5, zone.z);
    }

    public boolean isValid(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean force) {
        this.horizontalZone = false;
        this.zoneWidth = 0;
        this.debugLine = "";
        if (!force && zone.hourLastSeen != 0) {
            return false;
        } else if (!force && zone.haveConstruction) {
            return false;
        } else if (!"Nav".equals(zone.getType())) {
            this.debugLine = this.debugLine + "Not a 'Nav' zone.";
            return false;
        } else {
            this.minX = Math.max(zone.x, chunk.wx * 10);
            this.minY = Math.max(zone.y, chunk.wy * 10);
            this.maxX = Math.min(zone.x + zone.w, (chunk.wx + 1) * 10);
            this.maxY = Math.min(zone.y + zone.h, (chunk.wy + 1) * 10);
            return this.getSpawnPoint(zone, chunk, null);
        }
    }

    public VehicleStorySpawnData initSpawnDataForChunk(IsoMetaGrid.Zone zone, IsoChunk chunk) {
        int int0 = this.getMinZoneWidth();
        int int1 = this.getMinZoneHeight();
        float[] floats = new float[3];
        if (!this.getSpawnPoint(zone, chunk, floats)) {
            return null;
        } else {
            float float0 = floats[0];
            float float1 = floats[1];
            float float2 = floats[2];
            int[] ints = new int[4];
            VehicleStorySpawner.getInstance().getAABB(float0, float1, int0, int1, float2, ints);
            return new VehicleStorySpawnData(this, zone, float0, float1, float2, ints[0], ints[1], ints[2], ints[3]);
        }
    }

    public boolean getSpawnPoint(IsoMetaGrid.Zone zone, IsoChunk chunk, float[] floats) {
        return this.getRectangleSpawnPoint(zone, chunk, floats) || this.getPolylineSpawnPoint(zone, chunk, floats);
    }

    public boolean getRectangleSpawnPoint(IsoMetaGrid.Zone zone, IsoChunk chunk, float[] floats) {
        if (!zone.isRectangle()) {
            return false;
        } else {
            int int0 = this.getMinZoneWidth();
            int int1 = this.getMinZoneHeight();
            if (zone.w > 30 && zone.h < 15) {
                this.horizontalZone = true;
                this.zoneWidth = zone.h;
                if (zone.getWidth() < int1) {
                    this.debugLine = "Horizontal street is too small, w:" + zone.getWidth() + " h:" + zone.getHeight();
                    return false;
                } else if (zone.getHeight() < int0) {
                    this.debugLine = "Horizontal street is too small, w:" + zone.getWidth() + " h:" + zone.getHeight();
                    return false;
                } else if (floats == null) {
                    return true;
                } else {
                    float float0 = zone.getX();
                    float float1 = zone.getX() + zone.getWidth();
                    float float2 = zone.getY() + zone.getHeight() / 2.0F;
                    floats[0] = PZMath.clamp(chunk.wx * 10 + 5.0F, float0 + int1 / 2.0F, float1 - int1 / 2.0F);
                    floats[1] = float2;
                    floats[2] = Vector2.getDirection(float1 - float0, 0.0F);
                    return true;
                }
            } else if (zone.h > 30 && zone.w < 15) {
                this.horizontalZone = false;
                this.zoneWidth = zone.w;
                if (zone.getWidth() < int0) {
                    this.debugLine = "Vertical street is too small, w:" + zone.getWidth() + " h:" + zone.getHeight();
                    return false;
                } else if (zone.getHeight() < int1) {
                    this.debugLine = "Vertical street is too small, w:" + zone.getWidth() + " h:" + zone.getHeight();
                    return false;
                } else if (floats == null) {
                    return true;
                } else {
                    float float3 = zone.getY();
                    float float4 = zone.getY() + zone.getHeight();
                    float float5 = zone.getX() + zone.getWidth() / 2.0F;
                    floats[0] = float5;
                    floats[1] = PZMath.clamp(chunk.wy * 10 + 5.0F, float3 + int1 / 2.0F, float4 - int1 / 2.0F);
                    floats[2] = Vector2.getDirection(0.0F, float3 - float4);
                    return true;
                }
            } else {
                this.debugLine = "Zone too small or too large";
                return false;
            }
        }
    }

    public boolean getPolylineSpawnPoint(IsoMetaGrid.Zone zone, IsoChunk chunk, float[] floats) {
        if (zone.isPolyline() && zone.polylineWidth > 0) {
            int int0 = this.getMinZoneWidth();
            int int1 = this.getMinZoneHeight();
            if (zone.polylineWidth < int0) {
                this.debugLine = "Polyline zone is too narrow, width:" + zone.polylineWidth;
                return false;
            } else {
                double[] doubles = new double[2];
                int int2 = zone.getClippedSegmentOfPolyline(chunk.wx * 10, chunk.wy * 10, (chunk.wx + 1) * 10, (chunk.wy + 1) * 10, doubles);
                if (int2 == -1) {
                    return false;
                } else {
                    double double0 = doubles[0];
                    double double1 = doubles[1];
                    float float0 = zone.polylineWidth % 2 == 0 ? 0.0F : 0.5F;
                    float float1 = zone.points.get(int2 * 2) + float0;
                    float float2 = zone.points.get(int2 * 2 + 1) + float0;
                    float float3 = zone.points.get(int2 * 2 + 2) + float0;
                    float float4 = zone.points.get(int2 * 2 + 3) + float0;
                    float float5 = float3 - float1;
                    float float6 = float4 - float2;
                    float float7 = Vector2f.length(float5, float6);
                    if (float7 < int1) {
                        return false;
                    } else {
                        this.zoneWidth = zone.polylineWidth;
                        if (floats == null) {
                            return true;
                        } else {
                            float float8 = int1 / 2.0F / float7;
                            float float9 = PZMath.max((float)double0 - float8, float8);
                            float float10 = PZMath.min((float)double1 + float8, 1.0F - float8);
                            float float11 = float1 + float5 * float9;
                            float float12 = float2 + float6 * float9;
                            float float13 = float1 + float5 * float10;
                            float float14 = float2 + float6 * float10;
                            float float15 = Rand.Next(0.0F, 1.0F);
                            if (Core.bDebug) {
                                float15 = (float)(System.currentTimeMillis() / 20L % 360L) / 360.0F;
                            }

                            floats[0] = float11 + (float13 - float11) * float15;
                            floats[1] = float12 + (float14 - float12) * float15;
                            floats[2] = Vector2.getDirection(float5, float6);
                            return true;
                        }
                    }
                }
            }
        } else {
            return false;
        }
    }

    public boolean isFullyStreamedIn(int x1, int y1, int x2, int y2) {
        byte byte0 = 10;
        int int0 = x1 / byte0;
        int int1 = y1 / byte0;
        int int2 = (x2 - 1) / byte0;
        int int3 = (y2 - 1) / byte0;

        for (int int4 = int1; int4 <= int3; int4++) {
            for (int int5 = int0; int5 <= int2; int5++) {
                if (!this.isChunkLoaded(int5, int4)) {
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isChunkLoaded(int wx, int wy) {
        IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(wx, wy) : IsoWorld.instance.CurrentCell.getChunk(wx, wy);
        return chunk != null && chunk.bLoaded;
    }

    public boolean initVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, boolean debug) {
        return false;
    }

    public boolean callVehicleStorySpawner(IsoMetaGrid.Zone zone, IsoChunk chunk, float additionalRotationRadians) {
        float[] floats = new float[3];
        if (!this.getSpawnPoint(zone, chunk, floats)) {
            return false;
        } else {
            this.initVehicleStorySpawner(zone, chunk, false);
            VehicleStorySpawner vehicleStorySpawner = VehicleStorySpawner.getInstance();
            float float0 = floats[2];
            if (Rand.NextBool(2)) {
                float0 += (float) Math.PI;
            }

            float0 += additionalRotationRadians;
            vehicleStorySpawner.spawn(floats[0], floats[1], 0.0F, ++float0, this::spawnElement);
            return true;
        }
    }

    public void spawnElement(VehicleStorySpawner spawner, VehicleStorySpawner.Element element) {
    }

    public BaseVehicle[] addSmashedOverlay(BaseVehicle v1, BaseVehicle v2, int xOffset, int yOffset, boolean _horizontalZone, boolean addBlood) {
        IsoDirections directions0 = v1.getDir();
        IsoDirections directions1 = v2.getDir();
        Object object0 = null;
        Object object1 = null;
        if (!_horizontalZone) {
            object0 = "Front";
            if (directions1 == IsoDirections.W) {
                if (directions0 == IsoDirections.S) {
                    object1 = "Right";
                } else {
                    object1 = "Left";
                }
            } else if (directions0 == IsoDirections.S) {
                object1 = "Left";
            } else {
                object1 = "Right";
            }
        } else {
            if (directions0 == IsoDirections.S) {
                if (xOffset > 0) {
                    object0 = "Left";
                } else {
                    object0 = "Right";
                }
            } else if (xOffset < 0) {
                object0 = "Left";
            } else {
                object0 = "Right";
            }

            object1 = "Front";
        }

        v1 = v1.setSmashed((String)object0);
        v2 = v2.setSmashed((String)object1);
        if (addBlood) {
            v1.setBloodIntensity((String)object0, 1.0F);
            v2.setBloodIntensity((String)object1, 1.0F);
        }

        return new BaseVehicle[]{v1, v2};
    }

    public int getChance() {
        return this.chance;
    }

    public void setChance(int _chance) {
        this.chance = _chance;
    }

    public int getMinimumDays() {
        return this.minimumDays;
    }

    public void setMinimumDays(int minimumDays) {
        this.minimumDays = minimumDays;
    }

    @Override
    public int getMaximumDays() {
        return this.maximumDays;
    }

    @Override
    public void setMaximumDays(int maximumDays) {
        this.maximumDays = maximumDays;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDebugLine() {
        return this.debugLine;
    }

    public void registerCustomOutfits() {
    }
}
