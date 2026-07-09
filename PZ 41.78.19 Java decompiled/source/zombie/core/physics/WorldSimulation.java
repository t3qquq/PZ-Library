// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.physics;

import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;

public final class WorldSimulation {
    public static WorldSimulation instance = new WorldSimulation();
    public static final boolean LEVEL_ZERO_ONLY = true;
    public HashMap<Integer, IsoMovingObject> physicsObjectMap = new HashMap<>();
    public boolean created = false;
    public float offsetX = 0.0F;
    public float offsetY = 0.0F;
    public long time;
    private final ArrayList<BaseVehicle> collideVehicles = new ArrayList<>(4);
    private final Vector3f tempVector3f = new Vector3f();
    private final Vector3f tempVector3f_2 = new Vector3f();
    private final Transform tempTransform = new Transform();
    private final Quaternionf javaxQuat4f = new Quaternionf();
    private final float[] ff = new float[8192];
    private final float[] wheelSteer = new float[4];
    private final float[] wheelRotation = new float[4];
    private final float[] wheelSkidInfo = new float[4];
    private final float[] wheelSuspensionLength = new float[4];

    public void create() {
        if (!this.created) {
            this.offsetX = IsoWorld.instance.MetaGrid.getMinX() * 300;
            this.offsetY = IsoWorld.instance.MetaGrid.getMinY() * 300;
            this.time = GameTime.getServerTimeMills();
            IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[0];
            Bullet.initWorld((int)this.offsetX, (int)this.offsetY, chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth);

            for (int int0 = 0; int0 < 4; int0++) {
                this.wheelSteer[int0] = 0.0F;
                this.wheelRotation[int0] = 0.0F;
                this.wheelSkidInfo[int0] = 0.0F;
                this.wheelSuspensionLength[int0] = 0.0F;
            }

            this.created = true;
        }
    }

    public void destroy() {
        Bullet.destroyWorld();
    }

    private void updatePhysic(float float0) {
        MPStatistic.getInstance().Bullet.Start();
        Bullet.stepSimulation(float0, 2, 0.016666668F);
        MPStatistic.getInstance().Bullet.End();
        this.time = GameTime.getServerTimeMills();
    }

    public void update() {
        WorldSimulation.s_performance.worldSimulationUpdate.invokeAndMeasure(this, WorldSimulation::updateInternal);
    }

    private void updateInternal() {
        if (this.created) {
            this.updatePhysic(GameTime.instance.getRealworldSecondsSinceLastUpdate());
            this.collideVehicles.clear();
            int int0 = Bullet.getVehicleCount();
            int int1 = 0;

            while (int1 < int0) {
                MPStatistic.getInstance().Bullet.Start();
                int int2 = Bullet.getVehiclePhysics(int1, this.ff);
                MPStatistic.getInstance().Bullet.End();
                if (int2 <= 0) {
                    break;
                }

                int1 += int2;
                int int3 = 0;

                for (int int4 = 0; int4 < int2; int4++) {
                    int int5 = (int)this.ff[int3++];
                    float float0 = this.ff[int3++];
                    float float1 = this.ff[int3++];
                    float float2 = this.ff[int3++];
                    this.tempTransform.origin.set(float0, float1, float2);
                    float float3 = this.ff[int3++];
                    float float4 = this.ff[int3++];
                    float float5 = this.ff[int3++];
                    float float6 = this.ff[int3++];
                    this.javaxQuat4f.set(float3, float4, float5, float6);
                    this.tempTransform.setRotation(this.javaxQuat4f);
                    float float7 = this.ff[int3++];
                    float float8 = this.ff[int3++];
                    float float9 = this.ff[int3++];
                    this.tempVector3f.set(float7, float8, float9);
                    float float10 = this.ff[int3++];
                    float float11 = this.ff[int3++];
                    int int6 = (int)this.ff[int3++];

                    for (int int7 = 0; int7 < int6; int7++) {
                        this.wheelSteer[int7] = this.ff[int3++];
                        this.wheelRotation[int7] = this.ff[int3++];
                        this.wheelSkidInfo[int7] = this.ff[int3++];
                        this.wheelSuspensionLength[int7] = this.ff[int3++];
                    }

                    int int8 = (int)(
                        float0 * 100.0F + float1 * 100.0F + float2 * 100.0F + float3 * 100.0F + float4 * 100.0F + float5 * 100.0F + float6 * 100.0F
                    );
                    BaseVehicle vehicle0 = VehicleManager.instance.getVehicleByID((short)int5);
                    if (vehicle0 != null
                        && (
                            !GameClient.bClient
                                || !vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.Remote)
                                    && !vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.RemoteCollide)
                        )) {
                        if (vehicle0.VehicleID == int5 && float11 > 0.5F) {
                            this.collideVehicles.add(vehicle0);
                            vehicle0.authSimulationHash = int8;
                        }

                        if (GameClient.bClient && vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.LocalCollide)) {
                            if (vehicle0.authSimulationHash != int8) {
                                vehicle0.authSimulationTime = System.currentTimeMillis();
                                vehicle0.authSimulationHash = int8;
                            }

                            if (System.currentTimeMillis() - vehicle0.authSimulationTime > 1000L) {
                                VehicleManager.instance.sendCollide(vehicle0, vehicle0.getDriver(), false);
                                vehicle0.authSimulationTime = 0L;
                            }
                        }

                        if (!vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.Remote)
                            || !vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.RemoteCollide)) {
                            if (GameClient.bClient && vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
                                vehicle0.jniSpeed = 0.0F;
                            } else {
                                vehicle0.jniSpeed = float10;
                            }
                        }

                        if (!GameClient.bClient
                            || !vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)
                                && !vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.Remote)
                                && !vehicle0.isNetPlayerAuthorization(BaseVehicle.Authorization.RemoteCollide)) {
                            if (this.compareTransform(this.tempTransform, vehicle0.getPoly().t)) {
                                vehicle0.polyDirty = true;
                            }

                            vehicle0.jniTransform.set(this.tempTransform);
                            vehicle0.jniLinearVelocity.set(this.tempVector3f);
                            vehicle0.jniIsCollide = float11 > 0.5F;

                            for (int int9 = 0; int9 < int6; int9++) {
                                vehicle0.wheelInfo[int9].steering = this.wheelSteer[int9];
                                vehicle0.wheelInfo[int9].rotation = this.wheelRotation[int9];
                                vehicle0.wheelInfo[int9].skidInfo = this.wheelSkidInfo[int9];
                                vehicle0.wheelInfo[int9].suspensionLength = this.wheelSuspensionLength[int9];
                            }
                        }
                    }
                }
            }

            if (GameClient.bClient) {
                IsoPlayer player = IsoPlayer.players[IsoPlayer.getPlayerIndex()];
                if (player != null) {
                    BaseVehicle vehicle1 = player.getVehicle();
                    if (vehicle1 != null && vehicle1.isNetPlayerId(player.getOnlineID()) && this.collideVehicles.contains(vehicle1)) {
                        for (BaseVehicle vehicle2 : this.collideVehicles) {
                            if (vehicle2.DistTo(vehicle1) < 8.0F && vehicle2.isNetPlayerAuthorization(BaseVehicle.Authorization.Server)) {
                                VehicleManager.instance.sendCollide(vehicle2, player, true);
                                vehicle2.authorizationClientCollide(player);
                            }
                        }
                    }
                }
            }

            MPStatistic.getInstance().Bullet.Start();
            int int10 = Bullet.getObjectPhysics(this.ff);
            MPStatistic.getInstance().Bullet.End();
            int int11 = 0;

            for (int int12 = 0; int12 < int10; int12++) {
                int int13 = (int)this.ff[int11++];
                float float12 = this.ff[int11++];
                float float13 = this.ff[int11++];
                float float14 = this.ff[int11++];
                float12 += this.offsetX;
                float14 += this.offsetY;
                IsoMovingObject movingObject = this.physicsObjectMap.get(int13);
                if (movingObject != null) {
                    movingObject.removeFromSquare();
                    movingObject.setX(float12 + 0.18F);
                    movingObject.setY(float14);
                    movingObject.setZ(Math.max(0.0F, float13 / 3.0F / 0.82F));
                    movingObject.setCurrent(
                        IsoWorld.instance.getCell().getGridSquare((double)movingObject.getX(), (double)movingObject.getY(), (double)movingObject.getZ())
                    );
                }
            }
        }
    }

    private boolean compareTransform(Transform transform1, Transform transform0) {
        if (!(Math.abs(transform1.origin.x - transform0.origin.x) > 0.01F)
            && !(Math.abs(transform1.origin.z - transform0.origin.z) > 0.01F)
            && (int)transform1.origin.y == (int)transform0.origin.y) {
            byte byte0 = 2;
            transform1.basis.getColumn(byte0, this.tempVector3f_2);
            float float0 = this.tempVector3f_2.x;
            float float1 = this.tempVector3f_2.z;
            transform0.basis.getColumn(byte0, this.tempVector3f_2);
            float float2 = this.tempVector3f_2.x;
            float float3 = this.tempVector3f_2.z;
            return Math.abs(float0 - float2) > 0.001F || Math.abs(float1 - float3) > 0.001F;
        } else {
            return true;
        }
    }

    public int setOwnVehiclePhysics(int int0, float[] floats) {
        return Bullet.setOwnVehiclePhysics(int0, floats);
    }

    public void activateChunkMap(int int0) {
        this.create();
        IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int0];
        if (!GameServer.bServer) {
            Bullet.activateChunkMap(int0, chunkMap.getWorldXMin(), chunkMap.getWorldYMin(), IsoChunkMap.ChunkGridWidth);
        }
    }

    public void deactivateChunkMap(int int0) {
        if (this.created) {
            Bullet.deactivateChunkMap(int0);
        }
    }

    public void scrollGroundLeft(int int0) {
        if (this.created) {
            Bullet.scrollChunkMapLeft(int0);
        }
    }

    public void scrollGroundRight(int int0) {
        if (this.created) {
            Bullet.scrollChunkMapRight(int0);
        }
    }

    public void scrollGroundUp(int int0) {
        if (this.created) {
            Bullet.scrollChunkMapUp(int0);
        }
    }

    public void scrollGroundDown(int int0) {
        if (this.created) {
            Bullet.scrollChunkMapDown(int0);
        }
    }

    public static TextureDraw.GenericDrawer getDrawer(int int0) {
        PhysicsDebugRenderer physicsDebugRenderer = PhysicsDebugRenderer.alloc();
        physicsDebugRenderer.init(IsoPlayer.players[int0]);
        return physicsDebugRenderer;
    }

    private static class s_performance {
        static final PerformanceProfileProbe worldSimulationUpdate = new PerformanceProfileProbe("WorldSimulation.update");
    }
}
