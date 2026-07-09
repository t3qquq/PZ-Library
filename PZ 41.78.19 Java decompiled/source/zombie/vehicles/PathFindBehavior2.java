// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import gnu.trove.list.array.TFloatArrayList;
import java.util.ArrayList;
import org.joml.Vector2f;
import org.joml.Vector3f;
import se.krka.kahlua.vm.KahluaTable;
import zombie.SandboxOptions;
import zombie.ai.State;
import zombie.ai.WalkingOnTheSpot;
import zombie.ai.astar.AStarPathFinder;
import zombie.ai.astar.IPathfinder;
import zombie.ai.astar.Mover;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieGetDownState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.network.GameClient;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;

public final class PathFindBehavior2 implements PolygonalMap2.IPathfinder {
    private static final Vector2 tempVector2 = new Vector2();
    private static final Vector2f tempVector2f = new Vector2f();
    private static final Vector2 tempVector2_2 = new Vector2();
    private static final Vector3f tempVector3f_1 = new Vector3f();
    private static final PathFindBehavior2.PointOnPath pointOnPath = new PathFindBehavior2.PointOnPath();
    public boolean pathNextIsSet = false;
    public float pathNextX;
    public float pathNextY;
    public ArrayList<IPathfinder> Listeners = new ArrayList<>();
    public PathFindBehavior2.NPCData NPCData = new PathFindBehavior2.NPCData();
    private IsoGameCharacter chr;
    private float startX;
    private float startY;
    private float startZ;
    private float targetX;
    private float targetY;
    private float targetZ;
    private final TFloatArrayList targetXYZ = new TFloatArrayList();
    private final PolygonalMap2.Path path = new PolygonalMap2.Path();
    private int pathIndex;
    private boolean isCancel = true;
    public boolean bStopping = false;
    public final WalkingOnTheSpot walkingOnTheSpot = new WalkingOnTheSpot();
    private final ArrayList<PathFindBehavior2.DebugPt> actualPos = new ArrayList<>();
    private static final ObjectPool<PathFindBehavior2.DebugPt> actualPool = new ObjectPool<>(PathFindBehavior2.DebugPt::new);
    private PathFindBehavior2.Goal goal = PathFindBehavior2.Goal.None;
    private IsoGameCharacter goalCharacter;
    private BaseVehicle goalVehicle;
    private String goalVehicleArea;
    private int goalVehicleSeat;

    public PathFindBehavior2(IsoGameCharacter character) {
        this.chr = character;
    }

    public boolean isGoalNone() {
        return this.goal == PathFindBehavior2.Goal.None;
    }

    public boolean isGoalCharacter() {
        return this.goal == PathFindBehavior2.Goal.Character;
    }

    public boolean isGoalLocation() {
        return this.goal == PathFindBehavior2.Goal.Location;
    }

    public boolean isGoalSound() {
        return this.goal == PathFindBehavior2.Goal.Sound;
    }

    public boolean isGoalVehicleAdjacent() {
        return this.goal == PathFindBehavior2.Goal.VehicleAdjacent;
    }

    public boolean isGoalVehicleArea() {
        return this.goal == PathFindBehavior2.Goal.VehicleArea;
    }

    public boolean isGoalVehicleSeat() {
        return this.goal == PathFindBehavior2.Goal.VehicleSeat;
    }

    public void reset() {
        this.startX = this.chr.getX();
        this.startY = this.chr.getY();
        this.startZ = this.chr.getZ();
        this.targetX = this.startX;
        this.targetY = this.startY;
        this.targetZ = this.startZ;
        this.targetXYZ.resetQuick();
        this.pathIndex = 0;
        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
        this.walkingOnTheSpot.reset(this.startX, this.startY);
    }

    public void pathToCharacter(IsoGameCharacter character) {
        this.isCancel = false;
        this.goal = PathFindBehavior2.Goal.Character;
        this.goalCharacter = character;
        if (character.getVehicle() != null) {
            Vector3f vector3f = character.getVehicle().chooseBestAttackPosition(character, this.chr, tempVector3f_1);
            if (vector3f != null) {
                this.setData(vector3f.x, vector3f.y, (int)character.getVehicle().z);
                return;
            }

            this.setData(character.getVehicle().x, character.getVehicle().y, (int)character.getVehicle().z);
            if (this.chr.DistToSquared(character.getVehicle()) < 100.0F) {
                IsoZombie zombie0 = Type.tryCastTo(this.chr, IsoZombie.class);
                if (zombie0 != null) {
                    zombie0.AllowRepathDelay = 100.0F;
                }

                this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
            }
        }

        this.setData(character.getX(), character.getY(), character.getZ());
    }

    public void pathToLocation(int int2, int int1, int int0) {
        this.isCancel = false;
        this.goal = PathFindBehavior2.Goal.Location;
        this.setData(int2 + 0.5F, int1 + 0.5F, int0);
    }

    public void pathToLocationF(float float0, float float1, float float2) {
        this.isCancel = false;
        this.goal = PathFindBehavior2.Goal.Location;
        this.setData(float0, float1, float2);
    }

    public void pathToSound(int int2, int int1, int int0) {
        this.isCancel = false;
        this.goal = PathFindBehavior2.Goal.Sound;
        this.setData(int2 + 0.5F, int1 + 0.5F, int0);
    }

    public void pathToNearest(TFloatArrayList tFloatArrayList) {
        if (tFloatArrayList != null && !tFloatArrayList.isEmpty()) {
            if (tFloatArrayList.size() % 3 != 0) {
                throw new IllegalArgumentException("locations should be multiples of x,y,z");
            } else {
                this.isCancel = false;
                this.goal = PathFindBehavior2.Goal.Location;
                this.setData(tFloatArrayList.get(0), tFloatArrayList.get(1), tFloatArrayList.get(2));

                for (byte byte0 = 3; byte0 < tFloatArrayList.size(); byte0 += 3) {
                    this.targetXYZ.add(tFloatArrayList.get(byte0));
                    this.targetXYZ.add(tFloatArrayList.get(byte0 + 1));
                    this.targetXYZ.add(tFloatArrayList.get(byte0 + 2));
                }
            }
        } else {
            throw new IllegalArgumentException("locations is null or empty");
        }
    }

    public void pathToNearestTable(KahluaTable table) {
        if (table != null && !table.isEmpty()) {
            if (table.len() % 3 != 0) {
                throw new IllegalArgumentException("locations table should be multiples of x,y,z");
            } else {
                TFloatArrayList tFloatArrayList = new TFloatArrayList(table.size());
                byte byte0 = 1;

                for (int int0 = table.len(); byte0 <= int0; byte0 += 3) {
                    Double double0 = Type.tryCastTo(table.rawget(byte0), Double.class);
                    Double double1 = Type.tryCastTo(table.rawget(byte0 + 1), Double.class);
                    Double double2 = Type.tryCastTo(table.rawget(byte0 + 2), Double.class);
                    if (double0 == null || double1 == null || double2 == null) {
                        throw new IllegalArgumentException("locations table should be multiples of x,y,z");
                    }

                    tFloatArrayList.add(double0.floatValue());
                    tFloatArrayList.add(double1.floatValue());
                    tFloatArrayList.add(double2.floatValue());
                }

                this.pathToNearest(tFloatArrayList);
            }
        } else {
            throw new IllegalArgumentException("locations table is null or empty");
        }
    }

    public void pathToVehicleAdjacent(BaseVehicle vehicle) {
        this.isCancel = false;
        this.goal = PathFindBehavior2.Goal.VehicleAdjacent;
        this.goalVehicle = vehicle;
        VehicleScript vehicleScript = vehicle.getScript();
        Vector3f vector3f0 = vehicleScript.getExtents();
        Vector3f vector3f1 = vehicleScript.getCenterOfMassOffset();
        float float0 = vector3f0.x;
        float float1 = vector3f0.z;
        float float2 = 0.3F;
        float float3 = vector3f1.x - float0 / 2.0F - float2;
        float float4 = vector3f1.z - float1 / 2.0F - float2;
        float float5 = vector3f1.x + float0 / 2.0F + float2;
        float float6 = vector3f1.z + float1 / 2.0F + float2;
        TFloatArrayList tFloatArrayList = new TFloatArrayList();
        Vector3f vector3f2 = vehicle.getWorldPos(float3, vector3f1.y, vector3f1.z, tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(vector3f2.x, vector3f2.y, (int)this.targetZ, vehicle, false, true)) {
            tFloatArrayList.add(vector3f2.x);
            tFloatArrayList.add(vector3f2.y);
            tFloatArrayList.add(this.targetZ);
        }

        vector3f2 = vehicle.getWorldPos(float5, vector3f1.y, vector3f1.z, tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(vector3f2.x, vector3f2.y, (int)this.targetZ, vehicle, false, true)) {
            tFloatArrayList.add(vector3f2.x);
            tFloatArrayList.add(vector3f2.y);
            tFloatArrayList.add(this.targetZ);
        }

        vector3f2 = vehicle.getWorldPos(vector3f1.x, vector3f1.y, float4, tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(vector3f2.x, vector3f2.y, (int)this.targetZ, vehicle, false, true)) {
            tFloatArrayList.add(vector3f2.x);
            tFloatArrayList.add(vector3f2.y);
            tFloatArrayList.add(this.targetZ);
        }

        vector3f2 = vehicle.getWorldPos(vector3f1.x, vector3f1.y, float6, tempVector3f_1);
        if (PolygonalMap2.instance.canStandAt(vector3f2.x, vector3f2.y, (int)this.targetZ, vehicle, false, true)) {
            tFloatArrayList.add(vector3f2.x);
            tFloatArrayList.add(vector3f2.y);
            tFloatArrayList.add(this.targetZ);
        }

        this.setData(tFloatArrayList.get(0), tFloatArrayList.get(1), tFloatArrayList.get(2));

        for (byte byte0 = 3; byte0 < tFloatArrayList.size(); byte0 += 3) {
            this.targetXYZ.add(tFloatArrayList.get(byte0));
            this.targetXYZ.add(tFloatArrayList.get(byte0 + 1));
            this.targetXYZ.add(tFloatArrayList.get(byte0 + 2));
        }
    }

    public void pathToVehicleArea(BaseVehicle vehicle, String string) {
        Vector2 vector = vehicle.getAreaCenter(string);
        if (vector == null) {
            this.targetX = this.chr.getX();
            this.targetY = this.chr.getY();
            this.targetZ = this.chr.getZ();
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
        } else {
            this.isCancel = false;
            this.goal = PathFindBehavior2.Goal.VehicleArea;
            this.goalVehicle = vehicle;
            this.goalVehicleArea = string;
            this.setData(vector.getX(), vector.getY(), (int)vehicle.getZ());
            if (this.chr instanceof IsoPlayer
                && (int)this.chr.z == (int)this.targetZ
                && !PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, this.targetX, this.targetY, (int)this.targetZ, null)) {
                this.path.clear();
                this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                this.path.addNode(this.targetX, this.targetY, this.targetZ);
                this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
            }
        }
    }

    public void pathToVehicleSeat(BaseVehicle vehicle, int int0) {
        VehicleScript.Position position = vehicle.getPassengerPosition(int0, "outside2");
        if (position != null) {
            Vector3f vector3f0 = BaseVehicle.TL_vector3f_pool.get().alloc();
            if (position.area == null) {
                vehicle.getPassengerPositionWorldPos(position, vector3f0);
            } else {
                Vector2 vector0 = BaseVehicle.TL_vector2_pool.get().alloc();
                VehicleScript.Area area0 = vehicle.script.getAreaById(position.area);
                Vector2 vector1 = vehicle.areaPositionWorld4PlayerInteract(area0, vector0);
                vector3f0.x = vector1.x;
                vector3f0.y = vector1.y;
                vector3f0.z = 0.0F;
                BaseVehicle.TL_vector2_pool.get().release(vector0);
            }

            vector3f0.sub(this.chr.x, this.chr.y, this.chr.z);
            if (vector3f0.length() < 2.0F) {
                vehicle.getPassengerPositionWorldPos(position, vector3f0);
                this.setData(vector3f0.x(), vector3f0.y(), (int)vector3f0.z());
                if (this.chr instanceof IsoPlayer && (int)this.chr.z == (int)this.targetZ) {
                    BaseVehicle.TL_vector3f_pool.get().release(vector3f0);
                    this.path.clear();
                    this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                    this.path.addNode(this.targetX, this.targetY, this.targetZ);
                    this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
                    return;
                }
            }

            BaseVehicle.TL_vector3f_pool.get().release(vector3f0);
        }

        position = vehicle.getPassengerPosition(int0, "outside");
        if (position == null) {
            VehiclePart part = vehicle.getPassengerDoor(int0);
            if (part == null) {
                this.targetX = this.chr.getX();
                this.targetY = this.chr.getY();
                this.targetZ = this.chr.getZ();
                this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
            } else {
                this.pathToVehicleArea(vehicle, part.getArea());
            }
        } else {
            this.isCancel = false;
            this.goal = PathFindBehavior2.Goal.VehicleSeat;
            this.goalVehicle = vehicle;
            Vector3f vector3f1 = BaseVehicle.TL_vector3f_pool.get().alloc();
            if (position.area == null) {
                vehicle.getPassengerPositionWorldPos(position, vector3f1);
            } else {
                Vector2 vector2 = BaseVehicle.TL_vector2_pool.get().alloc();
                VehicleScript.Area area1 = vehicle.script.getAreaById(position.area);
                Vector2 vector3 = vehicle.areaPositionWorld4PlayerInteract(area1, vector2);
                vector3f1.x = vector3.x;
                vector3f1.y = vector3.y;
                vector3f1.z = 0.0F;
                BaseVehicle.TL_vector2_pool.get().release(vector2);
            }

            this.setData(vector3f1.x(), vector3f1.y(), (int)vector3f1.z());
            BaseVehicle.TL_vector3f_pool.get().release(vector3f1);
            if (this.chr instanceof IsoPlayer
                && (int)this.chr.z == (int)this.targetZ
                && !PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, this.targetX, this.targetY, (int)this.targetZ, null)) {
                this.path.clear();
                this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                this.path.addNode(this.targetX, this.targetY, this.targetZ);
                this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
            }
        }
    }

    public void cancel() {
        this.isCancel = true;
    }

    public boolean getIsCancelled() {
        return this.isCancel;
    }

    public void setData(float float0, float float1, float float2) {
        this.startX = this.chr.getX();
        this.startY = this.chr.getY();
        this.startZ = this.chr.getZ();
        this.targetX = float0;
        this.targetY = float1;
        this.targetZ = float2;
        this.targetXYZ.resetQuick();
        this.pathIndex = 0;
        PolygonalMap2.instance.cancelRequest(this.chr);
        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notrunning;
        this.bStopping = false;
        actualPool.release(this.actualPos);
        this.actualPos.clear();
    }

    public float getTargetX() {
        return this.targetX;
    }

    public float getTargetY() {
        return this.targetY;
    }

    public float getTargetZ() {
        return this.targetZ;
    }

    public float getPathLength() {
        if (this.path != null && this.path.nodes.size() != 0) {
            if (this.pathIndex + 1 >= this.path.nodes.size()) {
                return (float)Math.sqrt((this.chr.x - this.targetX) * (this.chr.x - this.targetX) + (this.chr.y - this.targetY) * (this.chr.y - this.targetY));
            } else {
                float float0 = (float)Math.sqrt(
                    (this.chr.x - this.path.nodes.get(this.pathIndex + 1).x) * (this.chr.x - this.path.nodes.get(this.pathIndex + 1).x)
                        + (this.chr.y - this.path.nodes.get(this.pathIndex + 1).y) * (this.chr.y - this.path.nodes.get(this.pathIndex + 1).y)
                );

                for (int int0 = this.pathIndex + 2; int0 < this.path.nodes.size(); int0++) {
                    float0 += (float)Math.sqrt(
                        (this.path.nodes.get(int0 - 1).x - this.path.nodes.get(int0).x) * (this.path.nodes.get(int0 - 1).x - this.path.nodes.get(int0).x)
                            + (this.path.nodes.get(int0 - 1).y - this.path.nodes.get(int0).y) * (this.path.nodes.get(int0 - 1).y - this.path.nodes.get(int0).y)
                    );
                }

                return float0;
            }
        } else {
            return (float)Math.sqrt((this.chr.x - this.targetX) * (this.chr.x - this.targetX) + (this.chr.y - this.targetY) * (this.chr.y - this.targetY));
        }
    }

    public IsoGameCharacter getTargetChar() {
        return this.goal == PathFindBehavior2.Goal.Character ? this.goalCharacter : null;
    }

    public boolean isTargetLocation(float float2, float float1, float float0) {
        return this.goal == PathFindBehavior2.Goal.Location && float2 == this.targetX && float1 == this.targetY && (int)float0 == (int)this.targetZ;
    }

    public PathFindBehavior2.BehaviorResult update() {
        if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notrunning) {
            PolygonalMap2.PathFindRequest pathFindRequest = PolygonalMap2.instance
                .addRequest(this, this.chr, this.startX, this.startY, this.startZ, this.targetX, this.targetY, this.targetZ);
            pathFindRequest.targetXYZ.resetQuick();
            pathFindRequest.targetXYZ.addAll(this.targetXYZ);
            this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.notyetfound;
            this.walkingOnTheSpot.reset(this.chr.x, this.chr.y);
            this.updateWhileRunningPathfind();
            return PathFindBehavior2.BehaviorResult.Working;
        } else if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.notyetfound) {
            this.updateWhileRunningPathfind();
            return PathFindBehavior2.BehaviorResult.Working;
        } else if (this.chr.getFinder().progress == AStarPathFinder.PathFindProgress.failed) {
            return PathFindBehavior2.BehaviorResult.Failed;
        } else {
            State state = this.chr.getCurrentState();
            if (Core.bDebug && DebugOptions.instance.PathfindRenderPath.getValue() && this.chr instanceof IsoPlayer) {
                this.actualPos
                    .add(
                        actualPool.alloc()
                            .init(this.chr.x, this.chr.y, this.chr.z, state == ClimbOverFenceState.instance() || state == ClimbThroughWindowState.instance())
                    );
            }

            if (state != ClimbOverFenceState.instance() && state != ClimbThroughWindowState.instance()) {
                if (this.chr.getVehicle() != null) {
                    return PathFindBehavior2.BehaviorResult.Failed;
                } else if (this.walkingOnTheSpot.check(this.chr.x, this.chr.y)) {
                    return PathFindBehavior2.BehaviorResult.Failed;
                } else {
                    this.chr.setMoving(true);
                    this.chr.setPath2(this.path);
                    IsoZombie zombie0 = Type.tryCastTo(this.chr, IsoZombie.class);
                    if (this.goal == PathFindBehavior2.Goal.Character
                        && zombie0 != null
                        && this.goalCharacter != null
                        && this.goalCharacter.getVehicle() != null
                        && this.chr.DistToSquared(this.targetX, this.targetY) < 16.0F) {
                        Vector3f vector3f = this.goalCharacter.getVehicle().chooseBestAttackPosition(this.goalCharacter, this.chr, tempVector3f_1);
                        if (vector3f == null) {
                            return PathFindBehavior2.BehaviorResult.Failed;
                        }

                        if (Math.abs(vector3f.x - this.targetX) > 0.1F || Math.abs(vector3f.y - this.targetY) > 0.1F) {
                            if (Math.abs(this.goalCharacter.getVehicle().getCurrentSpeedKmHour()) > 0.8F) {
                                if (!PolygonalMap2.instance
                                    .lineClearCollide(this.chr.x, this.chr.y, vector3f.x, vector3f.y, (int)this.targetZ, this.goalCharacter)) {
                                    this.path.clear();
                                    this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                                    this.path.addNode(vector3f.x, vector3f.y, vector3f.z);
                                } else if (IsoUtils.DistanceToSquared(vector3f.x, vector3f.y, this.targetX, this.targetY)
                                    > IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, vector3f.x, vector3f.y)) {
                                    return PathFindBehavior2.BehaviorResult.Working;
                                }
                            } else if (zombie0.AllowRepathDelay <= 0.0F) {
                                zombie0.AllowRepathDelay = 6.25F;
                                if (PolygonalMap2.instance.lineClearCollide(this.chr.x, this.chr.y, vector3f.x, vector3f.y, (int)this.targetZ, null)) {
                                    this.setData(vector3f.x, vector3f.y, this.targetZ);
                                    return PathFindBehavior2.BehaviorResult.Working;
                                }

                                this.path.clear();
                                this.path.addNode(this.chr.x, this.chr.y, this.chr.z);
                                this.path.addNode(vector3f.x, vector3f.y, vector3f.z);
                            }
                        }
                    }

                    closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, pointOnPath);
                    this.pathIndex = pointOnPath.pathIndex;
                    if (this.pathIndex == this.path.nodes.size() - 2) {
                        PolygonalMap2.PathNode pathNode0 = this.path.nodes.get(this.path.nodes.size() - 1);
                        if (IsoUtils.DistanceToSquared(this.chr.x, this.chr.y, pathNode0.x, pathNode0.y) <= 0.0025000002F) {
                            this.chr.getDeferredMovement(tempVector2);
                            if (!(tempVector2.getLength() > 0.0F)) {
                                this.pathNextIsSet = false;
                                return PathFindBehavior2.BehaviorResult.Succeeded;
                            }

                            if (zombie0 != null || this.chr instanceof IsoPlayer) {
                                this.chr.setMoving(false);
                            }

                            this.bStopping = true;
                            return PathFindBehavior2.BehaviorResult.Working;
                        }
                    } else if (this.pathIndex < this.path.nodes.size() - 2 && pointOnPath.dist > 0.999F) {
                        this.pathIndex++;
                    }

                    PolygonalMap2.PathNode pathNode1 = this.path.nodes.get(this.pathIndex);
                    PolygonalMap2.PathNode pathNode2 = this.path.nodes.get(this.pathIndex + 1);
                    this.pathNextX = pathNode2.x;
                    this.pathNextY = pathNode2.y;
                    this.pathNextIsSet = true;
                    Vector2 vector = tempVector2.set(this.pathNextX - this.chr.x, this.pathNextY - this.chr.y);
                    vector.normalize();
                    this.chr.getDeferredMovement(tempVector2_2);
                    float float0 = tempVector2_2.getLength();
                    if (zombie0 != null) {
                        zombie0.bRunning = false;
                        if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                            zombie0.bRunning = true;
                        }
                    }

                    float float1 = 1.0F;
                    float float2 = float0 * float1;
                    float float3 = IsoUtils.DistanceTo(this.pathNextX, this.pathNextY, this.chr.x, this.chr.y);
                    if (float2 >= float3) {
                        float0 *= float3 / float2;
                        this.pathIndex++;
                    }

                    if (zombie0 != null) {
                        this.checkCrawlingTransition(pathNode1, pathNode2, float3);
                    }

                    if (zombie0 == null && float3 >= 0.5F) {
                        if (this.checkDoorHoppableWindow(
                            this.chr.x + vector.x * Math.max(0.5F, float0), this.chr.y + vector.y * Math.max(0.5F, float0), this.chr.z
                        )) {
                            return PathFindBehavior2.BehaviorResult.Failed;
                        }

                        if (state != this.chr.getCurrentState()) {
                            return PathFindBehavior2.BehaviorResult.Working;
                        }
                    }

                    if (float0 <= 0.0F) {
                        this.walkingOnTheSpot.reset(this.chr.x, this.chr.y);
                        return PathFindBehavior2.BehaviorResult.Working;
                    } else {
                        tempVector2_2.set(vector);
                        tempVector2_2.setLength(float0);
                        this.chr.MoveUnmodded(tempVector2_2);
                        if (this.isStrafing()) {
                            if ((
                                    this.goal == PathFindBehavior2.Goal.VehicleAdjacent
                                        || this.goal == PathFindBehavior2.Goal.VehicleArea
                                        || this.goal == PathFindBehavior2.Goal.VehicleSeat
                                )
                                && this.goalVehicle != null) {
                                this.chr.faceThisObject(this.goalVehicle);
                            }
                        } else if (!this.chr.isAiming()) {
                            this.chr.faceLocationF(this.pathNextX, this.pathNextY);
                        }

                        return PathFindBehavior2.BehaviorResult.Working;
                    }
                }
            } else {
                if (GameClient.bClient && this.chr instanceof IsoPlayer && !((IsoPlayer)this.chr).isLocalPlayer()) {
                    this.chr.getDeferredMovement(tempVector2_2);
                    this.chr.MoveUnmodded(tempVector2_2);
                }

                return PathFindBehavior2.BehaviorResult.Working;
            }
        }
    }

    private void updateWhileRunningPathfind() {
        if (this.pathNextIsSet) {
            this.moveToPoint(this.pathNextX, this.pathNextY, 1.0F);
        }
    }

    public void moveToPoint(float float1, float float0, float float3) {
        if (!(this.chr instanceof IsoPlayer) || this.chr.getCurrentState() != CollideWithWallState.instance()) {
            IsoZombie zombie0 = Type.tryCastTo(this.chr, IsoZombie.class);
            Vector2 vector = tempVector2.set(float1 - this.chr.x, float0 - this.chr.y);
            if ((int)float1 != (int)this.chr.x || (int)float0 != (int)this.chr.y || !(vector.getLength() <= 0.1F)) {
                vector.normalize();
                this.chr.getDeferredMovement(tempVector2_2);
                float float2 = tempVector2_2.getLength();
                float2 *= float3;
                if (zombie0 != null) {
                    zombie0.bRunning = false;
                    if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                        zombie0.bRunning = true;
                    }
                }

                if (!(float2 <= 0.0F)) {
                    tempVector2_2.set(vector);
                    tempVector2_2.setLength(float2);
                    this.chr.MoveUnmodded(tempVector2_2);
                    this.chr.faceLocation(float1 - 0.5F, float0 - 0.5F);
                    this.chr.getForwardDirection().set(float1 - this.chr.x, float0 - this.chr.y);
                    this.chr.getForwardDirection().normalize();
                }
            }
        }
    }

    public void moveToDir(IsoMovingObject movingObject, float float1) {
        Vector2 vector = tempVector2.set(movingObject.x - this.chr.x, movingObject.y - this.chr.y);
        if (!(vector.getLength() <= 0.1F)) {
            vector.normalize();
            this.chr.getDeferredMovement(tempVector2_2);
            float float0 = tempVector2_2.getLength();
            float0 *= float1;
            if (this.chr instanceof IsoZombie) {
                ((IsoZombie)this.chr).bRunning = false;
                if (SandboxOptions.instance.Lore.Speed.getValue() == 1) {
                    ((IsoZombie)this.chr).bRunning = true;
                }
            }

            if (!(float0 <= 0.0F)) {
                tempVector2_2.set(vector);
                tempVector2_2.setLength(float0);
                this.chr.MoveUnmodded(tempVector2_2);
                this.chr.faceLocation(movingObject.x - 0.5F, movingObject.y - 0.5F);
                this.chr.getForwardDirection().set(movingObject.x - this.chr.x, movingObject.y - this.chr.y);
                this.chr.getForwardDirection().normalize();
            }
        }
    }

    private boolean checkDoorHoppableWindow(float float2, float float1, float float0) {
        IsoGridSquare square0 = this.chr.getCurrentSquare();
        if (square0 == null) {
            return false;
        } else {
            IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare((double)float2, (double)float1, (double)float0);
            if (square1 != null && square1 != square0) {
                int int0 = square1.x - square0.x;
                int int1 = square1.y - square0.y;
                if (int0 != 0 && int1 != 0) {
                    return false;
                } else {
                    IsoObject object0 = this.chr.getCurrentSquare().getDoorTo(square1);
                    if (object0 instanceof IsoDoor door) {
                        if (!door.open) {
                            door.ToggleDoor(this.chr);
                            if (!door.open) {
                                return true;
                            }
                        }
                    } else if (object0 instanceof IsoThumpable thumpable0 && !thumpable0.open) {
                        thumpable0.ToggleDoor(this.chr);
                        if (!thumpable0.open) {
                            return true;
                        }
                    }

                    IsoWindow window = square0.getWindowTo(square1);
                    if (window != null) {
                        if (window.canClimbThrough(this.chr) && (!window.isSmashed() || window.isGlassRemoved())) {
                            this.chr.climbThroughWindow(window);
                            return false;
                        } else {
                            return true;
                        }
                    } else {
                        IsoThumpable thumpable1 = square0.getWindowThumpableTo(square1);
                        if (thumpable1 == null) {
                            IsoObject object1 = square0.getWindowFrameTo(square1);
                            if (object1 != null) {
                                this.chr.climbThroughWindowFrame(object1);
                                return false;
                            } else {
                                if (int0 > 0 && square1.Is(IsoFlagType.HoppableW)) {
                                    this.chr.climbOverFence(IsoDirections.E);
                                } else if (int0 < 0 && square0.Is(IsoFlagType.HoppableW)) {
                                    this.chr.climbOverFence(IsoDirections.W);
                                } else if (int1 < 0 && square0.Is(IsoFlagType.HoppableN)) {
                                    this.chr.climbOverFence(IsoDirections.N);
                                } else if (int1 > 0 && square1.Is(IsoFlagType.HoppableN)) {
                                    this.chr.climbOverFence(IsoDirections.S);
                                }

                                return false;
                            }
                        } else if (thumpable1.isBarricaded()) {
                            return true;
                        } else {
                            this.chr.climbThroughWindow(thumpable1);
                            return false;
                        }
                    }
                }
            } else {
                return false;
            }
        }
    }

    private void checkCrawlingTransition(PolygonalMap2.PathNode pathNode0, PolygonalMap2.PathNode pathNode1, float float0) {
        IsoZombie zombie0 = (IsoZombie)this.chr;
        if (this.pathIndex < this.path.nodes.size() - 2) {
            pathNode0 = this.path.nodes.get(this.pathIndex);
            pathNode1 = this.path.nodes.get(this.pathIndex + 1);
            float0 = IsoUtils.DistanceTo(pathNode1.x, pathNode1.y, this.chr.x, this.chr.y);
        }

        if (zombie0.isCrawling()) {
            if (!zombie0.isCanWalk()) {
                return;
            }

            if (zombie0.isBeingSteppedOn()) {
            }

            if (zombie0.getStateMachine().getPrevious() == ZombieGetDownState.instance() && ZombieGetDownState.instance().isNearStartXY(zombie0)) {
                return;
            }

            this.advanceAlongPath(this.chr.x, this.chr.y, this.chr.z, 0.5F, pointOnPath);
            if (!PolygonalMap2.instance.canStandAt(pointOnPath.x, pointOnPath.y, (int)zombie0.z, null, false, true)) {
                return;
            }

            if (!pathNode1.hasFlag(1) && PolygonalMap2.instance.canStandAt(zombie0.x, zombie0.y, (int)zombie0.z, null, false, true)) {
                zombie0.setVariable("ShouldStandUp", true);
            }
        } else {
            if (pathNode0.hasFlag(1) && pathNode1.hasFlag(1)) {
                zombie0.setVariable("ShouldBeCrawling", true);
                ZombieGetDownState.instance().setParams(this.chr);
                return;
            }

            if (float0 < 0.4F && !pathNode0.hasFlag(1) && pathNode1.hasFlag(1)) {
                zombie0.setVariable("ShouldBeCrawling", true);
                ZombieGetDownState.instance().setParams(this.chr);
            }
        }
    }

    public boolean shouldGetUpFromCrawl() {
        return this.chr.getVariableBoolean("ShouldStandUp");
    }

    public boolean isStrafing() {
        return this.chr.isZombie()
            ? false
            : this.path.nodes.size() == 2
                && IsoUtils.DistanceToSquared(this.startX, this.startY, this.startZ * 3.0F, this.targetX, this.targetY, this.targetZ * 3.0F) < 0.25F;
    }

    public static void closestPointOnPath(
        float float7, float float6, float float1, IsoMovingObject movingObject, PolygonalMap2.Path pathx, PathFindBehavior2.PointOnPath pointOnPathx
    ) {
        IsoCell cell = IsoWorld.instance.CurrentCell;
        pointOnPathx.pathIndex = 0;
        float float0 = Float.MAX_VALUE;

        for (int int0 = 0; int0 < pathx.nodes.size() - 1; int0++) {
            PolygonalMap2.PathNode pathNode0 = pathx.nodes.get(int0);
            PolygonalMap2.PathNode pathNode1 = pathx.nodes.get(int0 + 1);
            if ((int)pathNode0.z == (int)float1 || (int)pathNode1.z == (int)float1) {
                float float2 = pathNode0.x;
                float float3 = pathNode0.y;
                float float4 = pathNode1.x;
                float float5 = pathNode1.y;
                double double0 = ((float7 - float2) * (float4 - float2) + (float6 - float3) * (float5 - float3))
                    / (Math.pow(float4 - float2, 2.0) + Math.pow(float5 - float3, 2.0));
                double double1 = float2 + double0 * (float4 - float2);
                double double2 = float3 + double0 * (float5 - float3);
                if (double0 <= 0.0) {
                    double1 = float2;
                    double2 = float3;
                    double0 = 0.0;
                } else if (double0 >= 1.0) {
                    double1 = float4;
                    double2 = float5;
                    double0 = 1.0;
                }

                int int1 = (int)double1 - (int)float7;
                int int2 = (int)double2 - (int)float6;
                if ((int1 != 0 || int2 != 0) && Math.abs(int1) <= 1 && Math.abs(int2) <= 1) {
                    IsoGridSquare square0 = cell.getGridSquare((int)float7, (int)float6, (int)float1);
                    IsoGridSquare square1 = cell.getGridSquare((int)double1, (int)double2, (int)float1);
                    if (movingObject instanceof IsoZombie) {
                        boolean boolean0 = ((IsoZombie)movingObject).Ghost;
                        ((IsoZombie)movingObject).Ghost = true;

                        try {
                            if (square0 != null && square1 != null && square0.testCollideAdjacent(movingObject, int1, int2, 0)) {
                                continue;
                            }
                        } finally {
                            ((IsoZombie)movingObject).Ghost = boolean0;
                        }
                    } else if (square0 != null && square1 != null && square0.testCollideAdjacent(movingObject, int1, int2, 0)) {
                        continue;
                    }
                }

                float float8 = float1;
                if (Math.abs(int1) <= 1 && Math.abs(int2) <= 1) {
                    IsoGridSquare square2 = cell.getGridSquare((int)pathNode0.x, (int)pathNode0.y, (int)pathNode0.z);
                    IsoGridSquare square3 = cell.getGridSquare((int)pathNode1.x, (int)pathNode1.y, (int)pathNode1.z);
                    float float9 = square2 == null ? pathNode0.z : PolygonalMap2.instance.getApparentZ(square2);
                    float float10 = square3 == null ? pathNode1.z : PolygonalMap2.instance.getApparentZ(square3);
                    float8 = float9 + (float10 - float9) * (float)double0;
                }

                float float11 = IsoUtils.DistanceToSquared(float7, float6, float1, (float)double1, (float)double2, float8);
                if (float11 < float0) {
                    float0 = float11;
                    pointOnPathx.pathIndex = int0;
                    pointOnPathx.dist = double0 == 1.0 ? 1.0F : (float)double0;
                    pointOnPathx.x = (float)double1;
                    pointOnPathx.y = (float)double2;
                }
            }
        }
    }

    void advanceAlongPath(float float0, float float1, float float2, float float3, PathFindBehavior2.PointOnPath pointOnPathx) {
        closestPointOnPath(float0, float1, float2, this.chr, this.path, pointOnPathx);

        for (int int0 = pointOnPathx.pathIndex; int0 < this.path.nodes.size() - 1; int0++) {
            PolygonalMap2.PathNode pathNode0 = this.path.nodes.get(int0);
            PolygonalMap2.PathNode pathNode1 = this.path.nodes.get(int0 + 1);
            double double0 = IsoUtils.DistanceTo2D(float0, float1, pathNode1.x, pathNode1.y);
            if (!(float3 > double0)) {
                pointOnPathx.pathIndex = int0;
                pointOnPathx.dist = pointOnPathx.dist + float3 / IsoUtils.DistanceTo2D(pathNode0.x, pathNode0.y, pathNode1.x, pathNode1.y);
                pointOnPathx.x = pathNode0.x + pointOnPathx.dist * (pathNode1.x - pathNode0.x);
                pointOnPathx.y = pathNode0.y + pointOnPathx.dist * (pathNode1.y - pathNode0.y);
                return;
            }

            float0 = pathNode1.x;
            float1 = pathNode1.y;
            float3 = (float)(float3 - double0);
            pointOnPathx.dist = 0.0F;
        }

        pointOnPathx.pathIndex = this.path.nodes.size() - 1;
        pointOnPathx.dist = 1.0F;
        pointOnPathx.x = this.path.nodes.get(pointOnPathx.pathIndex).x;
        pointOnPathx.y = this.path.nodes.get(pointOnPathx.pathIndex).y;
    }

    public void render() {
        if (this.chr.getCurrentState() == WalkTowardState.instance()) {
            WalkTowardState.instance().calculateTargetLocation((IsoZombie)this.chr, tempVector2);
            tempVector2.x = tempVector2.x - this.chr.x;
            tempVector2.y = tempVector2.y - this.chr.y;
            tempVector2.setLength(Math.min(100.0F, tempVector2.getLength()));
            LineDrawer.addLine(
                this.chr.x, this.chr.y, this.chr.z, this.chr.x + tempVector2.x, this.chr.y + tempVector2.y, this.targetZ, 1.0F, 1.0F, 1.0F, null, true
            );
        } else if (this.chr.getPath2() != null) {
            for (int int0 = 0; int0 < this.path.nodes.size() - 1; int0++) {
                PolygonalMap2.PathNode pathNode0 = this.path.nodes.get(int0);
                PolygonalMap2.PathNode pathNode1 = this.path.nodes.get(int0 + 1);
                float float0 = 1.0F;
                float float1 = 1.0F;
                if ((int)pathNode0.z != (int)pathNode1.z) {
                    float1 = 0.0F;
                }

                LineDrawer.addLine(pathNode0.x, pathNode0.y, pathNode0.z, pathNode1.x, pathNode1.y, pathNode1.z, float0, float1, 0.0F, null, true);
            }

            for (int int1 = 0; int1 < this.path.nodes.size(); int1++) {
                PolygonalMap2.PathNode pathNode2 = this.path.nodes.get(int1);
                float float2 = 1.0F;
                float float3 = 1.0F;
                float float4 = 0.0F;
                if (int1 == 0) {
                    float2 = 0.0F;
                    float4 = 1.0F;
                }

                LineDrawer.addLine(
                    pathNode2.x - 0.05F,
                    pathNode2.y - 0.05F,
                    pathNode2.z,
                    pathNode2.x + 0.05F,
                    pathNode2.y + 0.05F,
                    pathNode2.z,
                    float2,
                    float3,
                    float4,
                    null,
                    false
                );
            }

            closestPointOnPath(this.chr.x, this.chr.y, this.chr.z, this.chr, this.path, pointOnPath);
            LineDrawer.addLine(
                pointOnPath.x - 0.05F,
                pointOnPath.y - 0.05F,
                this.chr.z,
                pointOnPath.x + 0.05F,
                pointOnPath.y + 0.05F,
                this.chr.z,
                0.0F,
                1.0F,
                0.0F,
                null,
                false
            );

            for (int int2 = 0; int2 < this.actualPos.size() - 1; int2++) {
                PathFindBehavior2.DebugPt debugPt0 = this.actualPos.get(int2);
                PathFindBehavior2.DebugPt debugPt1 = this.actualPos.get(int2 + 1);
                LineDrawer.addLine(debugPt0.x, debugPt0.y, this.chr.z, debugPt1.x, debugPt1.y, this.chr.z, 1.0F, 1.0F, 1.0F, null, true);
                LineDrawer.addLine(
                    debugPt0.x - 0.05F,
                    debugPt0.y - 0.05F,
                    this.chr.z,
                    debugPt0.x + 0.05F,
                    debugPt0.y + 0.05F,
                    this.chr.z,
                    1.0F,
                    debugPt0.climbing ? 1.0F : 0.0F,
                    0.0F,
                    null,
                    false
                );
            }
        }
    }

    @Override
    public void Succeeded(PolygonalMap2.Path pathx, Mover var2) {
        this.path.copyFrom(pathx);
        if (!this.isCancel) {
            this.chr.setPath2(this.path);
        }

        if (!pathx.isEmpty()) {
            PolygonalMap2.PathNode pathNode = pathx.nodes.get(pathx.nodes.size() - 1);
            this.targetX = pathNode.x;
            this.targetY = pathNode.y;
            this.targetZ = pathNode.z;
        }

        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.found;
    }

    @Override
    public void Failed(Mover var1) {
        this.chr.getFinder().progress = AStarPathFinder.PathFindProgress.failed;
    }

    public boolean isMovingUsingPathFind() {
        return !this.bStopping && !this.isGoalNone() && !this.isCancel;
    }

    public static enum BehaviorResult {
        Working,
        Failed,
        Succeeded;
    }

    private static final class DebugPt {
        float x;
        float y;
        float z;
        boolean climbing;

        PathFindBehavior2.DebugPt init(float float0, float float1, float float2, boolean boolean0) {
            this.x = float0;
            this.y = float1;
            this.z = float2;
            this.climbing = boolean0;
            return this;
        }
    }

    private static enum Goal {
        None,
        Character,
        Location,
        Sound,
        VehicleAdjacent,
        VehicleArea,
        VehicleSeat;
    }

    public class NPCData {
        public boolean doDirectMovement;
        public int MaxSteps;
        public int nextTileX;
        public int nextTileY;
        public int nextTileZ;
    }

    public static final class PointOnPath {
        int pathIndex;
        float dist;
        float x;
        float y;
    }
}
