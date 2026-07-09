// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.randomizedWorld.randomizedZoneStory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.SandboxOptions;
import zombie.core.Rand;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoDeadBody;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.randomizedWorld.RandomizedWorldBase;
import zombie.util.Type;

public class RandomizedZoneStoryBase extends RandomizedWorldBase {
    public boolean alwaysDo = false;
    public static final int baseChance = 15;
    public static int totalChance = 0;
    public static final String zoneStory = "ZoneStory";
    public int chance = 0;
    protected int minZoneWidth = 0;
    protected int minZoneHeight = 0;
    public final ArrayList<String> zoneType = new ArrayList<>();
    private static final HashMap<RandomizedZoneStoryBase, Integer> rzsMap = new HashMap<>();

    public static boolean isValidForStory(IsoMetaGrid.Zone zone, boolean force) {
        if (zone.pickedXForZoneStory > 0 && zone.pickedYForZoneStory > 0 && zone.pickedRZStory != null && checkCanSpawnStory(zone, force)) {
            zone.pickedRZStory.randomizeZoneStory(zone);
            zone.pickedRZStory = null;
            zone.pickedXForZoneStory = 0;
            zone.pickedYForZoneStory = 0;
        }

        if (!force && zone.hourLastSeen != 0) {
            return false;
        } else if (!force && zone.haveConstruction) {
            return false;
        } else if ("ZoneStory".equals(zone.type)) {
            doRandomStory(zone);
            return true;
        } else {
            return false;
        }
    }

    public static void initAllRZSMapChance(IsoMetaGrid.Zone zone) {
        totalChance = 0;
        rzsMap.clear();

        for (int int0 = 0; int0 < IsoWorld.instance.getRandomizedZoneList().size(); int0++) {
            RandomizedZoneStoryBase randomizedZoneStoryBase = IsoWorld.instance.getRandomizedZoneList().get(int0);
            if (randomizedZoneStoryBase.isValid(zone, false) && randomizedZoneStoryBase.isTimeValid(false)) {
                totalChance = totalChance + randomizedZoneStoryBase.chance;
                rzsMap.put(randomizedZoneStoryBase, randomizedZoneStoryBase.chance);
            }
        }
    }

    public boolean isValid(IsoMetaGrid.Zone zone, boolean force) {
        boolean boolean0 = false;

        for (int int0 = 0; int0 < this.zoneType.size(); int0++) {
            if (this.zoneType.get(int0).equals(zone.name)) {
                boolean0 = true;
                break;
            }
        }

        return boolean0 && zone.w >= this.minZoneWidth && zone.h >= this.minZoneHeight;
    }

    private static boolean doRandomStory(IsoMetaGrid.Zone zone) {
        zone.hourLastSeen++;
        byte byte0 = 6;
        switch (SandboxOptions.instance.ZoneStoryChance.getValue()) {
            case 1:
                return false;
            case 2:
                byte0 = 2;
            case 3:
            default:
                break;
            case 4:
                byte0 = 12;
                break;
            case 5:
                byte0 = 20;
                break;
            case 6:
                byte0 = 40;
        }

        RandomizedZoneStoryBase randomizedZoneStoryBase0 = null;

        for (int int0 = 0; int0 < IsoWorld.instance.getRandomizedZoneList().size(); int0++) {
            RandomizedZoneStoryBase randomizedZoneStoryBase1 = IsoWorld.instance.getRandomizedZoneList().get(int0);
            if (randomizedZoneStoryBase1.alwaysDo && randomizedZoneStoryBase1.isValid(zone, false) && randomizedZoneStoryBase1.isTimeValid(false)) {
                randomizedZoneStoryBase0 = randomizedZoneStoryBase1;
            }
        }

        if (randomizedZoneStoryBase0 != null) {
            int int1 = zone.x;
            int int2 = zone.y;
            int int3 = zone.x + zone.w - randomizedZoneStoryBase0.minZoneWidth / 2;
            int int4 = zone.y + zone.h - randomizedZoneStoryBase0.minZoneHeight / 2;
            zone.pickedXForZoneStory = Rand.Next(int1, int3 + 1);
            zone.pickedYForZoneStory = Rand.Next(int2, int4 + 1);
            zone.pickedRZStory = randomizedZoneStoryBase0;
            return true;
        } else if (Rand.Next(100) < byte0) {
            initAllRZSMapChance(zone);
            randomizedZoneStoryBase0 = getRandomStory();
            if (randomizedZoneStoryBase0 == null) {
                return false;
            } else {
                int int5 = zone.x;
                int int6 = zone.y;
                int int7 = zone.x + zone.w - randomizedZoneStoryBase0.minZoneWidth / 2;
                int int8 = zone.y + zone.h - randomizedZoneStoryBase0.minZoneHeight / 2;
                zone.pickedXForZoneStory = Rand.Next(int5, int7 + 1);
                zone.pickedYForZoneStory = Rand.Next(int6, int8 + 1);
                zone.pickedRZStory = randomizedZoneStoryBase0;
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Get a random free square in our story zone
     */
    public IsoGridSquare getRandomFreeSquare(RandomizedZoneStoryBase rzs, IsoMetaGrid.Zone zone) {
        IsoGridSquare square = null;

        for (int int0 = 0; int0 < 1000; int0++) {
            int int1 = Rand.Next(zone.pickedXForZoneStory - rzs.minZoneWidth / 2, zone.pickedXForZoneStory + rzs.minZoneWidth / 2);
            int int2 = Rand.Next(zone.pickedYForZoneStory - rzs.minZoneHeight / 2, zone.pickedYForZoneStory + rzs.minZoneHeight / 2);
            square = this.getSq(int1, int2, zone.z);
            if (square != null && square.isFree(false)) {
                return square;
            }
        }

        return null;
    }

    public IsoGridSquare getRandomFreeSquareFullZone(RandomizedZoneStoryBase rzs, IsoMetaGrid.Zone zone) {
        IsoGridSquare square = null;

        for (int int0 = 0; int0 < 1000; int0++) {
            int int1 = Rand.Next(zone.x, zone.x + zone.w);
            int int2 = Rand.Next(zone.y, zone.y + zone.h);
            square = this.getSq(int1, int2, zone.z);
            if (square != null && square.isFree(false)) {
                return square;
            }
        }

        return null;
    }

    private static RandomizedZoneStoryBase getRandomStory() {
        int int0 = Rand.Next(totalChance);
        Iterator iterator = rzsMap.keySet().iterator();
        int int1 = 0;

        while (iterator.hasNext()) {
            RandomizedZoneStoryBase randomizedZoneStoryBase = (RandomizedZoneStoryBase)iterator.next();
            int1 += rzsMap.get(randomizedZoneStoryBase);
            if (int0 < int1) {
                return randomizedZoneStoryBase;
            }
        }

        return null;
    }

    private static boolean checkCanSpawnStory(IsoMetaGrid.Zone zone, boolean var1) {
        int int0 = zone.pickedXForZoneStory - zone.pickedRZStory.minZoneWidth / 2 - 2;
        int int1 = zone.pickedYForZoneStory - zone.pickedRZStory.minZoneHeight / 2 - 2;
        int int2 = zone.pickedXForZoneStory + zone.pickedRZStory.minZoneWidth / 2 + 2;
        int int3 = zone.pickedYForZoneStory + zone.pickedRZStory.minZoneHeight / 2 + 2;
        int int4 = int0 / 10;
        int int5 = int1 / 10;
        int int6 = int2 / 10;
        int int7 = int3 / 10;

        for (int int8 = int5; int8 <= int7; int8++) {
            for (int int9 = int4; int9 <= int6; int9++) {
                IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int9, int8) : IsoWorld.instance.CurrentCell.getChunk(int9, int8);
                if (chunk == null || !chunk.bLoaded) {
                    return false;
                }
            }
        }

        return true;
    }

    public void randomizeZoneStory(IsoMetaGrid.Zone zone) {
    }

    public boolean isValid() {
        return true;
    }

    public void cleanAreaForStory(RandomizedZoneStoryBase rzs, IsoMetaGrid.Zone zone) {
        int int0 = zone.pickedXForZoneStory - rzs.minZoneWidth / 2 - 1;
        int int1 = zone.pickedYForZoneStory - rzs.minZoneHeight / 2 - 1;
        int int2 = zone.pickedXForZoneStory + rzs.minZoneWidth / 2 + 1;
        int int3 = zone.pickedYForZoneStory + rzs.minZoneHeight / 2 + 1;

        for (int int4 = int0; int4 < int2; int4++) {
            for (int int5 = int1; int5 < int3; int5++) {
                IsoGridSquare square = IsoWorld.instance.getCell().getGridSquare(int4, int5, zone.z);
                if (square != null) {
                    square.removeBlood(false, false);

                    for (int int6 = square.getObjects().size() - 1; int6 >= 0; int6--) {
                        IsoObject object0 = square.getObjects().get(int6);
                        if (square.getFloor() != object0) {
                            square.RemoveTileObject(object0);
                        }
                    }

                    for (int int7 = square.getSpecialObjects().size() - 1; int7 >= 0; int7--) {
                        IsoObject object1 = square.getSpecialObjects().get(int7);
                        square.RemoveTileObject(object1);
                    }

                    for (int int8 = square.getStaticMovingObjects().size() - 1; int8 >= 0; int8--) {
                        IsoDeadBody deadBody = Type.tryCastTo(square.getStaticMovingObjects().get(int8), IsoDeadBody.class);
                        if (deadBody != null) {
                            square.removeCorpse(deadBody, false);
                        }
                    }

                    square.RecalcProperties();
                    square.RecalcAllWithNeighbours(true);
                }
            }
        }
    }

    public int getMinimumWidth() {
        return this.minZoneWidth;
    }

    public int getMinimumHeight() {
        return this.minZoneHeight;
    }

    public static enum ZoneType {
        Forest,
        Beach,
        Lake,
        Baseball,
        MusicFestStage,
        MusicFest,
        NewsStory;
    }
}
