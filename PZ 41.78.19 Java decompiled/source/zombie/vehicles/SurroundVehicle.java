// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.util.ArrayList;
import java.util.Objects;
import org.joml.Vector2f;
import org.joml.Vector3f;
import zombie.VirtualZombieManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.iso.IsoUtils;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;

public final class SurroundVehicle {
    private static final ObjectPool<SurroundVehicle.Position> s_positionPool = new ObjectPool<>(SurroundVehicle.Position::new);
    private static final Vector3f s_tempVector3f = new Vector3f();
    private final BaseVehicle m_vehicle;
    public float x1;
    public float y1;
    public float x2;
    public float y2;
    public float x3;
    public float y3;
    public float x4;
    public float y4;
    private float x1p;
    private float y1p;
    private float x2p;
    private float y2p;
    private float x3p;
    private float y3p;
    private float x4p;
    private float y4p;
    private boolean m_bMoved = false;
    private final ArrayList<SurroundVehicle.Position> m_positions = new ArrayList<>();
    private long m_updateMS = 0L;

    public SurroundVehicle(BaseVehicle vehicle) {
        Objects.requireNonNull(vehicle);
        this.m_vehicle = vehicle;
    }

    private void calcPositionsLocal() {
        s_positionPool.release(this.m_positions);
        this.m_positions.clear();
        VehicleScript vehicleScript = this.m_vehicle.getScript();
        if (vehicleScript != null) {
            Vector3f vector3f0 = vehicleScript.getExtents();
            Vector3f vector3f1 = vehicleScript.getCenterOfMassOffset();
            float float0 = vector3f0.x;
            float float1 = vector3f0.z;
            float float2 = 0.005F;
            float float3 = BaseVehicle.PLUS_RADIUS + float2;
            float float4 = vector3f1.x - float0 / 2.0F - float3;
            float float5 = vector3f1.z - float1 / 2.0F - float3;
            float float6 = vector3f1.x + float0 / 2.0F + float3;
            float float7 = vector3f1.z + float1 / 2.0F + float3;
            this.addPositions(float4, vector3f1.z - float1 / 2.0F, float4, vector3f1.z + float1 / 2.0F, SurroundVehicle.PositionSide.Right);
            this.addPositions(float6, vector3f1.z - float1 / 2.0F, float6, vector3f1.z + float1 / 2.0F, SurroundVehicle.PositionSide.Left);
            this.addPositions(float4, float5, float6, float5, SurroundVehicle.PositionSide.Rear);
            this.addPositions(float4, float7, float6, float7, SurroundVehicle.PositionSide.Front);
        }
    }

    private void addPositions(float float5, float float3, float float7, float float12, SurroundVehicle.PositionSide positionSide) {
        Vector3f vector3f = this.m_vehicle.getPassengerLocalPos(0, s_tempVector3f);
        if (vector3f != null) {
            float float0 = 0.3F;
            if (positionSide != SurroundVehicle.PositionSide.Left && positionSide != SurroundVehicle.PositionSide.Right) {
                float float1 = 0.0F;
                float float2 = float3;

                for (float float4 = float1; float4 >= float5 + float0; float4 -= float0 * 2.0F) {
                    this.addPosition(float4, float2, positionSide);
                }

                for (float float6 = float1 + float0 * 2.0F; float6 < float7 - float0; float6 += float0 * 2.0F) {
                    this.addPosition(float6, float2, positionSide);
                }
            } else {
                float float8 = float5;
                float float9 = vector3f.z;

                for (float float10 = float9; float10 >= float3 + float0; float10 -= float0 * 2.0F) {
                    this.addPosition(float8, float10, positionSide);
                }

                for (float float11 = float9 + float0 * 2.0F; float11 < float12 - float0; float11 += float0 * 2.0F) {
                    this.addPosition(float8, float11, positionSide);
                }
            }
        }
    }

    private SurroundVehicle.Position addPosition(float float0, float float1, SurroundVehicle.PositionSide positionSide) {
        SurroundVehicle.Position position = s_positionPool.alloc();
        position.posLocal.set(float0, float1);
        position.side = positionSide;
        this.m_positions.add(position);
        return position;
    }

    private void calcPositionsWorld() {
        for (int int0 = 0; int0 < this.m_positions.size(); int0++) {
            SurroundVehicle.Position position = this.m_positions.get(int0);
            this.m_vehicle.getWorldPos(position.posLocal.x, 0.0F, position.posLocal.y, position.posWorld);
            switch (position.side) {
                case Front:
                case Rear:
                    this.m_vehicle.getWorldPos(position.posLocal.x, 0.0F, 0.0F, position.posAxis);
                    break;
                case Left:
                case Right:
                    this.m_vehicle.getWorldPos(0.0F, 0.0F, position.posLocal.y, position.posAxis);
            }
        }

        PolygonalMap2.VehiclePoly vehiclePoly = this.m_vehicle.getPoly();
        this.x1p = vehiclePoly.x1;
        this.x2p = vehiclePoly.x2;
        this.x3p = vehiclePoly.x3;
        this.x4p = vehiclePoly.x4;
        this.y1p = vehiclePoly.y1;
        this.y2p = vehiclePoly.y2;
        this.y3p = vehiclePoly.y3;
        this.y4p = vehiclePoly.y4;
    }

    private SurroundVehicle.Position getClosestPositionFor(IsoZombie zombie0) {
        if (zombie0 != null && zombie0.getTarget() != null) {
            float float0 = Float.MAX_VALUE;
            SurroundVehicle.Position position0 = null;

            for (int int0 = 0; int0 < this.m_positions.size(); int0++) {
                SurroundVehicle.Position position1 = this.m_positions.get(int0);
                if (!position1.bBlocked) {
                    float float1 = IsoUtils.DistanceToSquared(zombie0.x, zombie0.y, position1.posWorld.x, position1.posWorld.y);
                    if (position1.isOccupied()) {
                        float float2 = IsoUtils.DistanceToSquared(position1.zombie.x, position1.zombie.y, position1.posWorld.x, position1.posWorld.y);
                        if (float2 < float1) {
                            continue;
                        }
                    }

                    float float3 = IsoUtils.DistanceToSquared(zombie0.getTarget().x, zombie0.getTarget().y, position1.posWorld.x, position1.posWorld.y);
                    if (float3 < float0) {
                        float0 = float3;
                        position0 = position1;
                    }
                }
            }

            return position0;
        } else {
            return null;
        }
    }

    public Vector2f getPositionForZombie(IsoZombie zombie, Vector2f out) {
        if ((!zombie.isOnFloor() || zombie.isCanWalk()) && (int)zombie.getZ() == (int)this.m_vehicle.getZ()) {
            float float0 = IsoUtils.DistanceToSquared(zombie.x, zombie.y, this.m_vehicle.x, this.m_vehicle.y);
            if (float0 > 100.0F) {
                return out.set(this.m_vehicle.x, this.m_vehicle.y);
            } else {
                if (this.checkPosition()) {
                    this.m_bMoved = true;
                }

                for (int int0 = 0; int0 < this.m_positions.size(); int0++) {
                    SurroundVehicle.Position position0 = this.m_positions.get(int0);
                    if (position0.bBlocked) {
                        position0.zombie = null;
                    }

                    if (position0.zombie == zombie) {
                        return out.set(position0.posWorld.x, position0.posWorld.y);
                    }
                }

                SurroundVehicle.Position position1 = this.getClosestPositionFor(zombie);
                if (position1 == null) {
                    return null;
                } else {
                    position1.zombie = zombie;
                    position1.targetX = zombie.getTarget().x;
                    position1.targetY = zombie.getTarget().y;
                    return out.set(position1.posWorld.x, position1.posWorld.y);
                }
            }
        } else {
            return out.set(this.m_vehicle.x, this.m_vehicle.y);
        }
    }

    private boolean checkPosition() {
        if (this.m_vehicle.getScript() == null) {
            return false;
        } else {
            if (this.m_positions.isEmpty()) {
                this.calcPositionsLocal();
                this.x1 = -1.0F;
            }

            PolygonalMap2.VehiclePoly vehiclePoly = this.m_vehicle.getPoly();
            if (this.x1 == vehiclePoly.x1
                && this.x2 == vehiclePoly.x2
                && this.x3 == vehiclePoly.x3
                && this.x4 == vehiclePoly.x4
                && this.y1 == vehiclePoly.y1
                && this.y2 == vehiclePoly.y2
                && this.y3 == vehiclePoly.y3
                && this.y4 == vehiclePoly.y4) {
                return false;
            } else {
                this.x1 = vehiclePoly.x1;
                this.x2 = vehiclePoly.x2;
                this.x3 = vehiclePoly.x3;
                this.x4 = vehiclePoly.x4;
                this.y1 = vehiclePoly.y1;
                this.y2 = vehiclePoly.y2;
                this.y3 = vehiclePoly.y3;
                this.y4 = vehiclePoly.y4;
                this.calcPositionsWorld();
                return true;
            }
        }
    }

    private boolean movedSincePositionsWereCalculated() {
        PolygonalMap2.VehiclePoly vehiclePoly = this.m_vehicle.getPoly();
        return this.x1p != vehiclePoly.x1
            || this.x2p != vehiclePoly.x2
            || this.x3p != vehiclePoly.x3
            || this.x4p != vehiclePoly.x4
            || this.y1p != vehiclePoly.y1
            || this.y2p != vehiclePoly.y2
            || this.y3p != vehiclePoly.y3
            || this.y4p != vehiclePoly.y4;
    }

    private boolean hasOccupiedPositions() {
        for (int int0 = 0; int0 < this.m_positions.size(); int0++) {
            SurroundVehicle.Position position = this.m_positions.get(int0);
            if (position.zombie != null) {
                return true;
            }
        }

        return false;
    }

    public void update() {
        if (this.hasOccupiedPositions() && this.checkPosition()) {
            this.m_bMoved = true;
        }

        long long0 = System.currentTimeMillis();
        if (long0 - this.m_updateMS >= 1000L) {
            this.m_updateMS = long0;
            if (this.m_bMoved) {
                this.m_bMoved = false;

                for (int int0 = 0; int0 < this.m_positions.size(); int0++) {
                    SurroundVehicle.Position position0 = this.m_positions.get(int0);
                    position0.zombie = null;
                }
            }

            boolean boolean0 = this.movedSincePositionsWereCalculated();

            for (int int1 = 0; int1 < this.m_positions.size(); int1++) {
                SurroundVehicle.Position position1 = this.m_positions.get(int1);
                if (!boolean0) {
                    position1.checkBlocked(this.m_vehicle);
                }

                if (position1.zombie != null) {
                    float float0 = IsoUtils.DistanceToSquared(position1.zombie.x, position1.zombie.y, this.m_vehicle.x, this.m_vehicle.y);
                    if (float0 > 100.0F) {
                        position1.zombie = null;
                    } else {
                        IsoGameCharacter character = Type.tryCastTo(position1.zombie.getTarget(), IsoGameCharacter.class);
                        if (position1.zombie.isDead()
                            || VirtualZombieManager.instance.isReused(position1.zombie)
                            || position1.zombie.isOnFloor()
                            || character == null
                            || this.m_vehicle.getSeat(character) == -1) {
                            position1.zombie = null;
                        } else if (IsoUtils.DistanceToSquared(position1.targetX, position1.targetY, character.x, character.y) > 0.1F) {
                            position1.zombie = null;
                        }
                    }
                }
            }
        }
    }

    public void render() {
        if (this.hasOccupiedPositions()) {
            for (int int0 = 0; int0 < this.m_positions.size(); int0++) {
                SurroundVehicle.Position position = this.m_positions.get(int0);
                Vector3f vector3f = position.posWorld;
                float float0 = 1.0F;
                float float1 = 1.0F;
                float float2 = 1.0F;
                if (position.isOccupied()) {
                    float2 = 0.0F;
                    float0 = 0.0F;
                } else if (position.bBlocked) {
                    float2 = 0.0F;
                    float1 = 0.0F;
                }

                this.m_vehicle.getController().drawCircle(vector3f.x, vector3f.y, 0.3F, float0, float1, float2, 1.0F);
            }
        }
    }

    public void reset() {
        s_positionPool.release(this.m_positions);
        this.m_positions.clear();
    }

    private static final class Position {
        final Vector2f posLocal = new Vector2f();
        final Vector3f posWorld = new Vector3f();
        final Vector3f posAxis = new Vector3f();
        SurroundVehicle.PositionSide side;
        IsoZombie zombie;
        float targetX;
        float targetY;
        boolean bBlocked;

        boolean isOccupied() {
            return this.zombie != null;
        }

        void checkBlocked(BaseVehicle vehicle) {
            this.bBlocked = PolygonalMap2.instance.lineClearCollide(this.posWorld.x, this.posWorld.y, this.posAxis.x, this.posAxis.y, (int)vehicle.z, vehicle);
            if (!this.bBlocked) {
                this.bBlocked = !PolygonalMap2.instance.canStandAt(this.posWorld.x, this.posWorld.y, (int)vehicle.z, vehicle, false, false);
            }
        }
    }

    private static enum PositionSide {
        Front,
        Rear,
        Left,
        Right;
    }
}
