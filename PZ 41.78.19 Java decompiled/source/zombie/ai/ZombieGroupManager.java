// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.VirtualZombieManager;
import zombie.ai.states.PathFindState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieIdleState;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.ZombieGroup;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.network.GameClient;
import zombie.network.GameServer;

public final class ZombieGroupManager {
    public static final ZombieGroupManager instance = new ZombieGroupManager();
    private final ArrayList<ZombieGroup> groups = new ArrayList<>();
    private final ArrayDeque<ZombieGroup> freeGroups = new ArrayDeque<>();
    private final Vector2 tempVec2 = new Vector2();
    private final Vector3 tempVec3 = new Vector3();
    private float tickCount = 30.0F;

    public void preupdate() {
        this.tickCount = this.tickCount + GameTime.getInstance().getMultiplier() / 1.6F;
        if (this.tickCount >= 30.0F) {
            this.tickCount = 0.0F;
        }

        int int0 = SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue();

        for (int int1 = 0; int1 < this.groups.size(); int1++) {
            ZombieGroup zombieGroup = this.groups.get(int1);
            zombieGroup.update();
            if (zombieGroup.isEmpty()) {
                this.freeGroups.push(zombieGroup);
                this.groups.remove(int1--);
            }
        }
    }

    public void Reset() {
        this.freeGroups.addAll(this.groups);
        this.groups.clear();
    }

    public boolean shouldBeInGroup(IsoZombie zombie0) {
        if (zombie0 == null) {
            return false;
        } else if (SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue() <= 1) {
            return false;
        } else if (!Core.getInstance().isZombieGroupSound()) {
            return false;
        } else if (zombie0.isUseless()) {
            return false;
        } else if (zombie0.isDead() || zombie0.isFakeDead()) {
            return false;
        } else if (zombie0.isSitAgainstWall()) {
            return false;
        } else if (zombie0.target != null) {
            return false;
        } else if (zombie0.getCurrentBuilding() != null) {
            return false;
        } else if (VirtualZombieManager.instance.isReused(zombie0)) {
            return false;
        } else {
            IsoGridSquare square = zombie0.getSquare();
            IsoMetaGrid.Zone zone = square == null ? null : square.getZone();
            return zone == null || !"Forest".equals(zone.getType()) && !"DeepForest".equals(zone.getType());
        }
    }

    public void update(IsoZombie zombie0) {
        if (!GameClient.bClient || !zombie0.isRemoteZombie()) {
            if (!this.shouldBeInGroup(zombie0)) {
                if (zombie0.group != null) {
                    zombie0.group.remove(zombie0);
                }
            } else if (this.tickCount == 0.0F) {
                if (zombie0.group == null) {
                    ZombieGroup zombieGroup0 = this.findNearestGroup(zombie0.getX(), zombie0.getY(), zombie0.getZ());
                    if (zombieGroup0 == null) {
                        zombieGroup0 = this.freeGroups.isEmpty() ? new ZombieGroup() : this.freeGroups.pop().reset();
                        zombieGroup0.add(zombie0);
                        this.groups.add(zombieGroup0);
                        return;
                    }

                    zombieGroup0.add(zombie0);
                }

                if (zombie0.getCurrentState() == ZombieIdleState.instance()) {
                    if (zombie0 == zombie0.group.getLeader()) {
                        float float0 = (float)GameTime.getInstance().getWorldAgeHours();
                        zombie0.group.lastSpreadOutTime = Math.min(zombie0.group.lastSpreadOutTime, float0);
                        if (!(zombie0.group.lastSpreadOutTime + 0.083333336F > float0)) {
                            zombie0.group.lastSpreadOutTime = float0;
                            int int0 = SandboxOptions.instance.zombieConfig.RallyGroupSeparation.getValue();
                            Vector2 vector = this.tempVec2.set(0.0F, 0.0F);

                            for (int int1 = 0; int1 < this.groups.size(); int1++) {
                                ZombieGroup zombieGroup1 = this.groups.get(int1);
                                if (zombieGroup1.getLeader() != null
                                    && zombieGroup1 != zombie0.group
                                    && (int)zombieGroup1.getLeader().getZ() == (int)zombie0.getZ()) {
                                    float float1 = zombieGroup1.getLeader().getX();
                                    float float2 = zombieGroup1.getLeader().getY();
                                    float float3 = IsoUtils.DistanceToSquared(zombie0.x, zombie0.y, float1, float2);
                                    if (!(float3 > int0 * int0)) {
                                        vector.x = vector.x - float1 + zombie0.x;
                                        vector.y = vector.y - float2 + zombie0.y;
                                    }
                                }
                            }

                            int int2 = this.lineClearCollideCount(
                                zombie0,
                                zombie0.getCell(),
                                (int)(zombie0.x + vector.x),
                                (int)(zombie0.y + vector.y),
                                (int)zombie0.z,
                                (int)zombie0.x,
                                (int)zombie0.y,
                                (int)zombie0.z,
                                10,
                                this.tempVec3
                            );
                            if (int2 >= 1) {
                                if (GameClient.bClient || GameServer.bServer || !(IsoPlayer.getInstance().getHoursSurvived() < 2.0)) {
                                    if (!(this.tempVec3.x < 0.0F)
                                        && !(this.tempVec3.y < 0.0F)
                                        && IsoWorld.instance.MetaGrid.isValidChunk((int)this.tempVec3.x / 10, (int)this.tempVec3.y / 10)) {
                                        zombie0.pathToLocation((int)(this.tempVec3.x + 0.5F), (int)(this.tempVec3.y + 0.5F), (int)this.tempVec3.z);
                                        if (zombie0.getCurrentState() == PathFindState.instance() || zombie0.getCurrentState() == WalkTowardState.instance()) {
                                            zombie0.setLastHeardSound(zombie0.getPathTargetX(), zombie0.getPathTargetY(), zombie0.getPathTargetZ());
                                            zombie0.AllowRepathDelay = 400.0F;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        float float4 = zombie0.group.getLeader().getX();
                        float float5 = zombie0.group.getLeader().getY();
                        int int3 = SandboxOptions.instance.zombieConfig.RallyGroupRadius.getValue();
                        if (!(IsoUtils.DistanceToSquared(zombie0.x, zombie0.y, float4, float5) < int3 * int3)) {
                            if (GameClient.bClient || GameServer.bServer || !(IsoPlayer.getInstance().getHoursSurvived() < 2.0) || Core.bDebug) {
                                int int4 = (int)(float4 + Rand.Next(-int3, int3));
                                int int5 = (int)(float5 + Rand.Next(-int3, int3));
                                if (int4 >= 0 && int5 >= 0 && IsoWorld.instance.MetaGrid.isValidChunk(int4 / 10, int5 / 10)) {
                                    zombie0.pathToLocation(int4, int5, (int)zombie0.group.getLeader().getZ());
                                    if (zombie0.getCurrentState() == PathFindState.instance() || zombie0.getCurrentState() == WalkTowardState.instance()) {
                                        zombie0.setLastHeardSound(zombie0.getPathTargetX(), zombie0.getPathTargetY(), zombie0.getPathTargetZ());
                                        zombie0.AllowRepathDelay = 400.0F;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public ZombieGroup findNearestGroup(float float3, float float4, float float1) {
        ZombieGroup zombieGroup0 = null;
        float float0 = Float.MAX_VALUE;
        int int0 = SandboxOptions.instance.zombieConfig.RallyTravelDistance.getValue();

        for (int int1 = 0; int1 < this.groups.size(); int1++) {
            ZombieGroup zombieGroup1 = this.groups.get(int1);
            if (zombieGroup1.isEmpty()) {
                this.groups.remove(int1--);
            } else if ((int)zombieGroup1.getLeader().getZ() == (int)float1
                && zombieGroup1.size() < SandboxOptions.instance.zombieConfig.RallyGroupSize.getValue()) {
                float float2 = IsoUtils.DistanceToSquared(float3, float4, zombieGroup1.getLeader().getX(), zombieGroup1.getLeader().getY());
                if (float2 < int0 * int0 && float2 < float0) {
                    float0 = float2;
                    zombieGroup0 = zombieGroup1;
                }
            }
        }

        return zombieGroup0;
    }

    private int lineClearCollideCount(
        IsoMovingObject movingObject, IsoCell cell, int int5, int int2, int int8, int int6, int int3, int int9, int int12, Vector3 vector
    ) {
        int int0 = 0;
        int int1 = int2 - int3;
        int int4 = int5 - int6;
        int int7 = int8 - int9;
        float float0 = 0.5F;
        float float1 = 0.5F;
        IsoGridSquare square0 = cell.getGridSquare(int6, int3, int9);
        vector.set(int6, int3, int9);
        if (Math.abs(int4) > Math.abs(int1) && Math.abs(int4) > Math.abs(int7)) {
            float float2 = (float)int1 / int4;
            float float3 = (float)int7 / int4;
            float0 += int3;
            float1 += int9;
            int4 = int4 < 0 ? -1 : 1;
            float2 *= int4;
            float3 *= int4;

            while (int6 != int5) {
                int6 += int4;
                float0 += float2;
                float1 += float3;
                IsoGridSquare square1 = cell.getGridSquare(int6, (int)float0, (int)float1);
                if (square1 != null && square0 != null) {
                    boolean boolean0 = square1.testCollideAdjacent(
                        movingObject, square0.getX() - square1.getX(), square0.getY() - square1.getY(), square0.getZ() - square1.getZ()
                    );
                    if (boolean0) {
                        return int0;
                    }
                }

                square0 = square1;
                int int10 = (int)float0;
                int int11 = (int)float1;
                vector.set(int6, int10, int11);
                if (++int0 >= int12) {
                    return int0;
                }
            }
        } else if (Math.abs(int1) >= Math.abs(int4) && Math.abs(int1) > Math.abs(int7)) {
            float float4 = (float)int4 / int1;
            float float5 = (float)int7 / int1;
            float0 += int6;
            float1 += int9;
            int1 = int1 < 0 ? -1 : 1;
            float4 *= int1;
            float5 *= int1;

            while (int3 != int2) {
                int3 += int1;
                float0 += float4;
                float1 += float5;
                IsoGridSquare square2 = cell.getGridSquare((int)float0, int3, (int)float1);
                if (square2 != null && square0 != null) {
                    boolean boolean1 = square2.testCollideAdjacent(
                        movingObject, square0.getX() - square2.getX(), square0.getY() - square2.getY(), square0.getZ() - square2.getZ()
                    );
                    if (boolean1) {
                        return int0;
                    }
                }

                square0 = square2;
                int int13 = (int)float0;
                int int14 = (int)float1;
                vector.set(int13, int3, int14);
                if (++int0 >= int12) {
                    return int0;
                }
            }
        } else {
            float float6 = (float)int4 / int7;
            float float7 = (float)int1 / int7;
            float0 += int6;
            float1 += int3;
            int7 = int7 < 0 ? -1 : 1;
            float6 *= int7;
            float7 *= int7;

            while (int9 != int8) {
                int9 += int7;
                float0 += float6;
                float1 += float7;
                IsoGridSquare square3 = cell.getGridSquare((int)float0, (int)float1, int9);
                if (square3 != null && square0 != null) {
                    boolean boolean2 = square3.testCollideAdjacent(
                        movingObject, square0.getX() - square3.getX(), square0.getY() - square3.getY(), square0.getZ() - square3.getZ()
                    );
                    if (boolean2) {
                        return int0;
                    }
                }

                square0 = square3;
                int int15 = (int)float0;
                int int16 = (int)float1;
                vector.set(int15, int16, int9);
                if (++int0 >= int12) {
                    return int0;
                }
            }
        }

        return int0;
    }
}
