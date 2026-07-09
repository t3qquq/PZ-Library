// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector2f;
import org.joml.Vector3f;
import zombie.GameWindow;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.iso.IsoChunk;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.ObjectPool;
import zombie.util.list.PZArrayUtil;

public class CollideWithObstaclesPoly {
    static final float RADIUS = 0.3F;
    private final ArrayList<CollideWithObstaclesPoly.CCObstacle> obstacles = new ArrayList<>();
    private final ArrayList<CollideWithObstaclesPoly.CCNode> nodes = new ArrayList<>();
    private final CollideWithObstaclesPoly.ImmutableRectF moveBounds = new CollideWithObstaclesPoly.ImmutableRectF();
    private final CollideWithObstaclesPoly.ImmutableRectF vehicleBounds = new CollideWithObstaclesPoly.ImmutableRectF();
    private static final Vector2 move = new Vector2();
    private static final Vector2 nodeNormal = new Vector2();
    private static final Vector2 edgeVec = new Vector2();
    private final ArrayList<BaseVehicle> vehicles = new ArrayList<>();
    private Clipper clipper;
    private final ByteBuffer xyBuffer = ByteBuffer.allocateDirect(8192);
    private final CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge = new CollideWithObstaclesPoly.ClosestPointOnEdge();

    void getVehiclesInRect(float float0, float float1, float float2, float float3, int int7) {
        this.vehicles.clear();
        int int0 = (int)(float0 / 10.0F);
        int int1 = (int)(float1 / 10.0F);
        int int2 = (int)Math.ceil(float2 / 10.0F);
        int int3 = (int)Math.ceil(float3 / 10.0F);

        for (int int4 = int1; int4 < int3; int4++) {
            for (int int5 = int0; int5 < int2; int5++) {
                IsoChunk chunk = GameServer.bServer
                    ? ServerMap.instance.getChunk(int5, int4)
                    : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int5 * 10, int4 * 10, 0);
                if (chunk != null) {
                    for (int int6 = 0; int6 < chunk.vehicles.size(); int6++) {
                        BaseVehicle vehicle = chunk.vehicles.get(int6);
                        if (vehicle.getScript() != null && (int)vehicle.z == int7) {
                            this.vehicles.add(vehicle);
                        }
                    }
                }
            }
        }
    }

    void getObstaclesInRect(float float3, float float1, float float2, float float0, int int4, int int5, int int8, boolean boolean0) {
        if (this.clipper == null) {
            this.clipper = new Clipper();
        }

        this.clipper.clear();
        this.moveBounds.init(float3 - 2.0F, float1 - 2.0F, float2 - float3 + 4.0F, float0 - float1 + 4.0F);
        int int0 = (int)(this.moveBounds.x / 10.0F);
        int int1 = (int)(this.moveBounds.y / 10.0F);
        int int2 = (int)Math.ceil(this.moveBounds.right() / 10.0F);
        int int3 = (int)Math.ceil(this.moveBounds.bottom() / 10.0F);
        if (Math.abs(float2 - float3) < 2.0F && Math.abs(float0 - float1) < 2.0F) {
            int0 = int4 / 10;
            int1 = int5 / 10;
            int2 = int0 + 1;
            int3 = int1 + 1;
        }

        for (int int6 = int1; int6 < int3; int6++) {
            for (int int7 = int0; int7 < int2; int7++) {
                IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int7, int6) : IsoWorld.instance.CurrentCell.getChunk(int7, int6);
                if (chunk != null) {
                    CollideWithObstaclesPoly.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, int8, this);
                    ArrayList arrayList = boolean0 ? chunkDataZ.worldVehicleUnion : chunkDataZ.worldVehicleSeparate;

                    for (int int9 = 0; int9 < arrayList.size(); int9++) {
                        CollideWithObstaclesPoly.CCObstacle cCObstacle = (CollideWithObstaclesPoly.CCObstacle)arrayList.get(int9);
                        if (cCObstacle.bounds.intersects(this.moveBounds)) {
                            this.obstacles.add(cCObstacle);
                        }
                    }

                    this.nodes.addAll(chunkDataZ.nodes);
                }
            }
        }
    }

    public Vector2f resolveCollision(IsoGameCharacter character, float float0, float float1, Vector2f vector2f) {
        vector2f.set(float0, float1);
        boolean boolean0 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();
        float float2 = character.x;
        float float3 = character.y;
        float float4 = float0;
        float float5 = float1;
        if (boolean0) {
            LineDrawer.addLine(float2, float3, (int)character.z, float0, float1, (int)character.z, 1.0F, 1.0F, 1.0F, null, true);
        }

        if (float2 == float0 && float3 == float1) {
            return vector2f;
        } else {
            move.set(float0 - character.x, float1 - character.y);
            move.normalize();
            this.nodes.clear();
            this.obstacles.clear();
            this.getObstaclesInRect(
                Math.min(float2, float0),
                Math.min(float3, float1),
                Math.max(float2, float0),
                Math.max(float3, float1),
                (int)character.x,
                (int)character.y,
                (int)character.z,
                true
            );
            this.closestPointOnEdge.edge = null;
            this.closestPointOnEdge.node = null;
            this.closestPointOnEdge.distSq = Double.MAX_VALUE;

            for (int int0 = 0; int0 < this.obstacles.size(); int0++) {
                CollideWithObstaclesPoly.CCObstacle cCObstacle0 = this.obstacles.get(int0);
                byte byte0 = 0;
                if (cCObstacle0.isPointInside(character.x, character.y, byte0)) {
                    cCObstacle0.getClosestPointOnEdge(character.x, character.y, this.closestPointOnEdge);
                }
            }

            CollideWithObstaclesPoly.CCEdge cCEdge0 = this.closestPointOnEdge.edge;
            CollideWithObstaclesPoly.CCNode cCNode0 = this.closestPointOnEdge.node;
            if (cCEdge0 != null) {
                float float6 = cCEdge0.normal.dot(move);
                if (float6 >= 0.01F) {
                    cCEdge0 = null;
                }
            }

            if (cCNode0 != null && cCNode0.getNormalAndEdgeVectors(nodeNormal, edgeVec) && nodeNormal.dot(move) + 0.05F >= nodeNormal.dot(edgeVec)) {
                cCNode0 = null;
                cCEdge0 = null;
            }

            if (cCEdge0 == null) {
                this.closestPointOnEdge.edge = null;
                this.closestPointOnEdge.node = null;
                this.closestPointOnEdge.distSq = Double.MAX_VALUE;

                for (int int1 = 0; int1 < this.obstacles.size(); int1++) {
                    CollideWithObstaclesPoly.CCObstacle cCObstacle1 = this.obstacles.get(int1);
                    cCObstacle1.lineSegmentIntersect(float2, float3, float4, float5, this.closestPointOnEdge, boolean0);
                }

                cCEdge0 = this.closestPointOnEdge.edge;
                cCNode0 = this.closestPointOnEdge.node;
            }

            if (cCNode0 != null) {
                move.set(float0 - character.x, float1 - character.y);
                move.normalize();
                CollideWithObstaclesPoly.CCEdge cCEdge1 = cCEdge0;
                CollideWithObstaclesPoly.CCEdge cCEdge2 = null;

                for (int int2 = 0; int2 < cCNode0.edges.size(); int2++) {
                    CollideWithObstaclesPoly.CCEdge cCEdge3 = cCNode0.edges.get(int2);
                    if (cCEdge3 != cCEdge0
                        && (
                            cCEdge1.node1.x != cCEdge3.node1.x
                                || cCEdge1.node1.y != cCEdge3.node1.y
                                || cCEdge1.node2.x != cCEdge3.node2.x
                                || cCEdge1.node2.y != cCEdge3.node2.y
                        )
                        && (
                            cCEdge1.node1.x != cCEdge3.node2.x
                                || cCEdge1.node1.y != cCEdge3.node2.y
                                || cCEdge1.node2.x != cCEdge3.node1.x
                                || cCEdge1.node2.y != cCEdge3.node1.y
                        )
                        && (!cCEdge1.hasNode(cCEdge3.node1) || !cCEdge1.hasNode(cCEdge3.node2))) {
                        cCEdge2 = cCEdge3;
                    }
                }

                if (cCEdge1 != null && cCEdge2 != null) {
                    if (cCEdge0 == cCEdge1) {
                        CollideWithObstaclesPoly.CCNode cCNode1 = cCNode0 == cCEdge2.node1 ? cCEdge2.node2 : cCEdge2.node1;
                        edgeVec.set(cCNode1.x - cCNode0.x, cCNode1.y - cCNode0.y);
                        edgeVec.normalize();
                        if (move.dot(edgeVec) >= 0.0F) {
                            cCEdge0 = cCEdge2;
                        }
                    } else if (cCEdge0 == cCEdge2) {
                        CollideWithObstaclesPoly.CCNode cCNode2 = cCNode0 == cCEdge1.node1 ? cCEdge1.node2 : cCEdge1.node1;
                        edgeVec.set(cCNode2.x - cCNode0.x, cCNode2.y - cCNode0.y);
                        edgeVec.normalize();
                        if (move.dot(edgeVec) >= 0.0F) {
                            cCEdge0 = cCEdge1;
                        }
                    }
                }
            }

            if (cCEdge0 != null) {
                if (boolean0) {
                    float float7 = cCEdge0.node1.x;
                    float float8 = cCEdge0.node1.y;
                    float float9 = cCEdge0.node2.x;
                    float float10 = cCEdge0.node2.y;
                    LineDrawer.addLine(float7, float8, cCEdge0.node1.z, float9, float10, cCEdge0.node1.z, 0.0F, 1.0F, 1.0F, null, true);
                }

                this.closestPointOnEdge.distSq = Double.MAX_VALUE;
                cCEdge0.getClosestPointOnEdge(float0, float1, this.closestPointOnEdge);
                vector2f.set(this.closestPointOnEdge.point.x, this.closestPointOnEdge.point.y);
            }

            return vector2f;
        }
    }

    boolean canStandAt(float float1, float float3, float float6, BaseVehicle vehicle, int int0) {
        boolean boolean0 = (int0 & 1) != 0;
        boolean boolean1 = (int0 & 2) != 0;
        float float0 = float1 - 0.3F;
        float float2 = float3 - 0.3F;
        float float4 = float1 + 0.3F;
        float float5 = float3 + 0.3F;
        this.nodes.clear();
        this.obstacles.clear();
        this.getObstaclesInRect(
            Math.min(float0, float4),
            Math.min(float2, float5),
            Math.max(float0, float4),
            Math.max(float2, float5),
            (int)float1,
            (int)float3,
            (int)float6,
            vehicle == null
        );

        for (int int1 = 0; int1 < this.obstacles.size(); int1++) {
            CollideWithObstaclesPoly.CCObstacle cCObstacle = this.obstacles.get(int1);
            if ((vehicle == null || cCObstacle.vehicle != vehicle) && cCObstacle.isPointInside(float1, float3, int0)) {
                return false;
            }
        }

        return true;
    }

    public boolean isNotClear(
        float float1, float float3, float float5, float float7, int int3, boolean boolean0, BaseVehicle vehicle, boolean var8, boolean var9
    ) {
        float float0 = float1;
        float float2 = float3;
        float float4 = float5;
        float float6 = float7;
        float1 /= 10.0F;
        float3 /= 10.0F;
        float5 /= 10.0F;
        float7 /= 10.0F;
        double double0 = Math.abs(float5 - float1);
        double double1 = Math.abs(float7 - float3);
        int int0 = (int)Math.floor(float1);
        int int1 = (int)Math.floor(float3);
        int int2 = 1;
        byte byte0;
        double double2;
        if (double0 == 0.0) {
            byte0 = 0;
            double2 = Double.POSITIVE_INFINITY;
        } else if (float5 > float1) {
            byte0 = 1;
            int2 += (int)Math.floor(float5) - int0;
            double2 = (Math.floor(float1) + 1.0 - float1) * double1;
        } else {
            byte0 = -1;
            int2 += int0 - (int)Math.floor(float5);
            double2 = (float1 - Math.floor(float1)) * double1;
        }

        byte byte1;
        if (double1 == 0.0) {
            byte1 = 0;
            double2 -= Double.POSITIVE_INFINITY;
        } else if (float7 > float3) {
            byte1 = 1;
            int2 += (int)Math.floor(float7) - int1;
            double2 -= (Math.floor(float3) + 1.0 - float3) * double0;
        } else {
            byte1 = -1;
            int2 += int1 - (int)Math.floor(float7);
            double2 -= (float3 - Math.floor(float3)) * double0;
        }

        for (; int2 > 0; int2--) {
            IsoChunk chunk = GameServer.bServer ? ServerMap.instance.getChunk(int0, int1) : IsoWorld.instance.CurrentCell.getChunk(int0, int1);
            if (chunk != null) {
                if (boolean0) {
                    LineDrawer.addRect(int0 * 10, int1 * 10, int3, 10.0F, 10.0F, 1.0F, 1.0F, 1.0F);
                }

                CollideWithObstaclesPoly.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, int3, this);
                ArrayList arrayList = vehicle == null ? chunkDataZ.worldVehicleUnion : chunkDataZ.worldVehicleSeparate;

                for (int int4 = 0; int4 < arrayList.size(); int4++) {
                    CollideWithObstaclesPoly.CCObstacle cCObstacle = (CollideWithObstaclesPoly.CCObstacle)arrayList.get(int4);
                    if ((vehicle == null || cCObstacle.vehicle != vehicle) && cCObstacle.lineSegmentIntersects(float0, float2, float4, float6, boolean0)) {
                        return true;
                    }
                }
            }

            if (double2 > 0.0) {
                int1 += byte1;
                double2 -= double0;
            } else {
                int0 += byte0;
                double2 += double1;
            }
        }

        return false;
    }

    private void vehicleMoved(PolygonalMap2.VehiclePoly vehiclePoly) {
        byte byte0 = 2;
        int int0 = (int)Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
        int int1 = (int)Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
        int int2 = (int)Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
        int int3 = (int)Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
        int int4 = (int)vehiclePoly.z;
        int int5 = (int0 - byte0) / 10;
        int int6 = (int1 - byte0) / 10;
        int int7 = (int)Math.ceil((int2 + byte0 - 1.0F) / 10.0F);
        int int8 = (int)Math.ceil((int3 + byte0 - 1.0F) / 10.0F);

        for (int int9 = int6; int9 <= int8; int9++) {
            for (int int10 = int5; int10 <= int7; int10++) {
                IsoChunk chunk = IsoWorld.instance.CurrentCell.getChunk(int10, int9);
                if (chunk != null && chunk.collision.data[int4] != null) {
                    CollideWithObstaclesPoly.ChunkDataZ chunkDataZ = chunk.collision.data[int4];
                    chunk.collision.data[int4] = null;
                    chunkDataZ.clear();
                    CollideWithObstaclesPoly.ChunkDataZ.pool.release(chunkDataZ);
                }
            }
        }
    }

    public void vehicleMoved(PolygonalMap2.VehiclePoly vehiclePoly0, PolygonalMap2.VehiclePoly vehiclePoly1) {
        this.vehicleMoved(vehiclePoly0);
        this.vehicleMoved(vehiclePoly1);
    }

    public void render() {
        boolean boolean0 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();
        if (boolean0) {
            IsoPlayer player = IsoPlayer.getInstance();
            if (player == null) {
                return;
            }

            this.nodes.clear();
            this.obstacles.clear();
            this.getObstaclesInRect(player.x, player.y, player.x, player.y, (int)player.x, (int)player.y, (int)player.z, true);
            if (DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
                for (CollideWithObstaclesPoly.CCNode cCNode : this.nodes) {
                    if (cCNode.getNormalAndEdgeVectors(nodeNormal, edgeVec)) {
                        LineDrawer.addLine(
                            cCNode.x, cCNode.y, cCNode.z, cCNode.x + nodeNormal.x, cCNode.y + nodeNormal.y, cCNode.z, 0.0F, 0.0F, 1.0F, null, true
                        );
                    }
                }
            }

            for (CollideWithObstaclesPoly.CCObstacle cCObstacle : this.obstacles) {
                cCObstacle.render();
            }
        }
    }

    private static final class CCEdge {
        CollideWithObstaclesPoly.CCNode node1;
        CollideWithObstaclesPoly.CCNode node2;
        CollideWithObstaclesPoly.CCObstacle obstacle;
        final Vector2 normal = new Vector2();
        static final ObjectPool<CollideWithObstaclesPoly.CCEdge> pool = new ObjectPool<>(CollideWithObstaclesPoly.CCEdge::new);

        CollideWithObstaclesPoly.CCEdge init(
            CollideWithObstaclesPoly.CCNode cCNode1, CollideWithObstaclesPoly.CCNode cCNode0, CollideWithObstaclesPoly.CCObstacle cCObstacle
        ) {
            if (cCNode1.x == cCNode0.x && cCNode1.y == cCNode0.y) {
                boolean boolean0 = false;
            }

            this.node1 = cCNode1;
            this.node2 = cCNode0;
            cCNode1.edges.add(this);
            cCNode0.edges.add(this);
            this.obstacle = cCObstacle;
            this.normal.set(cCNode0.x - cCNode1.x, cCNode0.y - cCNode1.y);
            this.normal.normalize();
            this.normal.rotate((float)Math.toRadians(90.0));
            return this;
        }

        boolean hasNode(CollideWithObstaclesPoly.CCNode cCNode) {
            return cCNode == this.node1 || cCNode == this.node2;
        }

        void getClosestPointOnEdge(float float5, float float4, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge) {
            float float0 = this.node1.x;
            float float1 = this.node1.y;
            float float2 = this.node2.x;
            float float3 = this.node2.y;
            double double0 = ((float5 - float0) * (float2 - float0) + (float4 - float1) * (float3 - float1))
                / (Math.pow(float2 - float0, 2.0) + Math.pow(float3 - float1, 2.0));
            double double1 = float0 + double0 * (float2 - float0);
            double double2 = float1 + double0 * (float3 - float1);
            double double3 = 0.001;
            CollideWithObstaclesPoly.CCNode cCNode = null;
            if (double0 <= 0.0 + double3) {
                double1 = float0;
                double2 = float1;
                cCNode = this.node1;
            } else if (double0 >= 1.0 - double3) {
                double1 = float2;
                double2 = float3;
                cCNode = this.node2;
            }

            double double4 = (float5 - double1) * (float5 - double1) + (float4 - double2) * (float4 - double2);
            if (double4 < closestPointOnEdge.distSq) {
                closestPointOnEdge.point.set((float)double1, (float)double2);
                closestPointOnEdge.distSq = double4;
                closestPointOnEdge.edge = this;
                closestPointOnEdge.node = cCNode;
            }
        }

        boolean isPointOn(float float5, float float4) {
            float float0 = this.node1.x;
            float float1 = this.node1.y;
            float float2 = this.node2.x;
            float float3 = this.node2.y;
            double double0 = ((float5 - float0) * (float2 - float0) + (float4 - float1) * (float3 - float1))
                / (Math.pow(float2 - float0, 2.0) + Math.pow(float3 - float1, 2.0));
            double double1 = float0 + double0 * (float2 - float0);
            double double2 = float1 + double0 * (float3 - float1);
            if (double0 <= 0.0) {
                double1 = float0;
                double2 = float1;
            } else if (double0 >= 1.0) {
                double1 = float2;
                double2 = float3;
            }

            double double3 = (float5 - double1) * (float5 - double1) + (float4 - double2) * (float4 - double2);
            return double3 < 1.0E-6;
        }

        static CollideWithObstaclesPoly.CCEdge alloc() {
            return pool.alloc();
        }

        void release() {
            pool.release(this);
        }

        static void releaseAll(ArrayList<CollideWithObstaclesPoly.CCEdge> arrayList) {
            pool.releaseAll(arrayList);
        }
    }

    private static final class CCEdgeRing extends ArrayList<CollideWithObstaclesPoly.CCEdge> {
        static final ObjectPool<CollideWithObstaclesPoly.CCEdgeRing> pool = new ObjectPool<CollideWithObstaclesPoly.CCEdgeRing>(
            CollideWithObstaclesPoly.CCEdgeRing::new
        ) {
            public void release(CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing) {
                CollideWithObstaclesPoly.CCEdge.releaseAll(cCEdgeRing);
                this.clear();
                super.release(cCEdgeRing);
            }
        };

        float isLeft(float float3, float float1, float float5, float float0, float float2, float float4) {
            return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
        }

        CollideWithObstaclesPoly.EdgeRingHit isPointInPolygon_WindingNumber(float float0, float float1, int int2) {
            int int0 = 0;

            for (int int1 = 0; int1 < this.size(); int1++) {
                CollideWithObstaclesPoly.CCEdge cCEdge = this.get(int1);
                if ((int2 & 16) != 0 && cCEdge.isPointOn(float0, float1)) {
                    return CollideWithObstaclesPoly.EdgeRingHit.OnEdge;
                }

                if (cCEdge.node1.y <= float1) {
                    if (cCEdge.node2.y > float1 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float0, float1) > 0.0F) {
                        int0++;
                    }
                } else if (cCEdge.node2.y <= float1 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float0, float1) < 0.0F) {
                    int0--;
                }
            }

            return int0 == 0 ? CollideWithObstaclesPoly.EdgeRingHit.Outside : CollideWithObstaclesPoly.EdgeRingHit.Inside;
        }

        boolean lineSegmentIntersects(float float3, float float1, float float2, float float0, boolean boolean0, boolean boolean1) {
            CollideWithObstaclesPoly.move.set(float2 - float3, float0 - float1);
            float float4 = CollideWithObstaclesPoly.move.getLength();
            CollideWithObstaclesPoly.move.normalize();
            float float5 = CollideWithObstaclesPoly.move.x;
            float float6 = CollideWithObstaclesPoly.move.y;

            for (int int0 = 0; int0 < this.size(); int0++) {
                CollideWithObstaclesPoly.CCEdge cCEdge = this.get(int0);
                if (!cCEdge.isPointOn(float3, float1) && !cCEdge.isPointOn(float2, float0)) {
                    float float7 = cCEdge.normal.dot(CollideWithObstaclesPoly.move);
                    if (!(float7 >= 0.01F)) {
                        float float8 = cCEdge.node1.x;
                        float float9 = cCEdge.node1.y;
                        float float10 = cCEdge.node2.x;
                        float float11 = cCEdge.node2.y;
                        float float12 = float3 - float8;
                        float float13 = float1 - float9;
                        float float14 = float10 - float8;
                        float float15 = float11 - float9;
                        float float16 = 1.0F / (float15 * float5 - float14 * float6);
                        float float17 = (float14 * float13 - float15 * float12) * float16;
                        if (float17 >= 0.0F && float17 <= float4) {
                            float float18 = (float13 * float5 - float12 * float6) * float16;
                            if (float18 >= 0.0F && float18 <= 1.0F) {
                                float float19 = float3 + float17 * float5;
                                float float20 = float1 + float17 * float6;
                                if (boolean0) {
                                    this.render(boolean1);
                                    LineDrawer.addRect(float19 - 0.05F, float20 - 0.05F, cCEdge.node1.z, 0.1F, 0.1F, 1.0F, 1.0F, 1.0F);
                                }

                                return true;
                            }
                        }
                    }
                }
            }

            return this.isPointInPolygon_WindingNumber((float3 + float2) / 2.0F, (float1 + float0) / 2.0F, 0) != CollideWithObstaclesPoly.EdgeRingHit.Outside;
        }

        void lineSegmentIntersect(
            float float3, float float1, float float2, float float0, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge, boolean boolean0
        ) {
            CollideWithObstaclesPoly.move.set(float2 - float3, float0 - float1).normalize();

            for (int int0 = 0; int0 < this.size(); int0++) {
                CollideWithObstaclesPoly.CCEdge cCEdge = this.get(int0);
                float float4 = cCEdge.normal.dot(CollideWithObstaclesPoly.move);
                if (!(float4 >= 0.0F)) {
                    float float5 = cCEdge.node1.x;
                    float float6 = cCEdge.node1.y;
                    float float7 = cCEdge.node2.x;
                    float float8 = cCEdge.node2.y;
                    float float9 = float5 + 0.5F * (float7 - float5);
                    float float10 = float6 + 0.5F * (float8 - float6);
                    if (boolean0 && DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
                        LineDrawer.addLine(
                            float9, float10, cCEdge.node1.z, float9 + cCEdge.normal.x, float10 + cCEdge.normal.y, cCEdge.node1.z, 0.0F, 0.0F, 1.0F, null, true
                        );
                    }

                    double double0 = (float8 - float6) * (float2 - float3) - (float7 - float5) * (float0 - float1);
                    if (double0 != 0.0) {
                        double double1 = ((float7 - float5) * (float1 - float6) - (float8 - float6) * (float3 - float5)) / double0;
                        double double2 = ((float2 - float3) * (float1 - float6) - (float0 - float1) * (float3 - float5)) / double0;
                        if (double1 >= 0.0 && double1 <= 1.0 && double2 >= 0.0 && double2 <= 1.0) {
                            if (double2 < 0.01 || double2 > 0.99) {
                                CollideWithObstaclesPoly.CCNode cCNode = double2 < 0.01 ? cCEdge.node1 : cCEdge.node2;
                                double double3 = IsoUtils.DistanceToSquared(float3, float1, cCNode.x, cCNode.y);
                                if (double3 >= closestPointOnEdge.distSq) {
                                    continue;
                                }

                                if (cCNode.getNormalAndEdgeVectors(CollideWithObstaclesPoly.nodeNormal, CollideWithObstaclesPoly.edgeVec)) {
                                    if (!(
                                        CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.move) + 0.05F
                                            >= CollideWithObstaclesPoly.nodeNormal.dot(CollideWithObstaclesPoly.edgeVec)
                                    )) {
                                        closestPointOnEdge.edge = cCEdge;
                                        closestPointOnEdge.node = cCNode;
                                        closestPointOnEdge.distSq = double3;
                                    }
                                    continue;
                                }
                            }

                            float float11 = (float)(float3 + double1 * (float2 - float3));
                            float float12 = (float)(float1 + double1 * (float0 - float1));
                            double double4 = IsoUtils.DistanceToSquared(float3, float1, float11, float12);
                            if (double4 < closestPointOnEdge.distSq) {
                                closestPointOnEdge.edge = cCEdge;
                                closestPointOnEdge.node = null;
                                closestPointOnEdge.distSq = double4;
                            }
                        }
                    }
                }
            }
        }

        void getClosestPointOnEdge(float float0, float float1, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge) {
            for (int int0 = 0; int0 < this.size(); int0++) {
                CollideWithObstaclesPoly.CCEdge cCEdge = this.get(int0);
                cCEdge.getClosestPointOnEdge(float0, float1, closestPointOnEdge);
            }
        }

        void render(boolean boolean0) {
            if (!this.isEmpty()) {
                float float0 = 0.0F;
                float float1 = boolean0 ? 1.0F : 0.5F;
                float float2 = boolean0 ? 0.0F : 0.5F;
                BaseVehicle.Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();

                for (CollideWithObstaclesPoly.CCEdge cCEdge : this) {
                    CollideWithObstaclesPoly.CCNode cCNode0 = cCEdge.node1;
                    CollideWithObstaclesPoly.CCNode cCNode1 = cCEdge.node2;
                    LineDrawer.addLine(cCNode0.x, cCNode0.y, cCNode0.z, cCNode1.x, cCNode1.y, cCNode1.z, float0, float1, float2, null, true);
                    boolean boolean1 = false;
                    if (boolean1) {
                        Vector3f vector3f0 = vector3fObjectPool.alloc()
                            .set(cCNode1.x - cCNode0.x, cCNode1.y - cCNode0.y, (float)(cCNode1.z - cCNode0.z))
                            .normalize();
                        Vector3f vector3f1 = vector3fObjectPool.alloc().set(vector3f0).cross(0.0F, 0.0F, 1.0F).normalize();
                        vector3f0.mul(0.9F);
                        LineDrawer.addLine(
                            cCNode1.x - vector3f0.x * 0.1F - vector3f1.x * 0.1F,
                            cCNode1.y - vector3f0.y * 0.1F - vector3f1.y * 0.1F,
                            cCNode1.z,
                            cCNode1.x,
                            cCNode1.y,
                            cCNode1.z,
                            float0,
                            float1,
                            float2,
                            null,
                            true
                        );
                        LineDrawer.addLine(
                            cCNode1.x - vector3f0.x * 0.1F + vector3f1.x * 0.1F,
                            cCNode1.y - vector3f0.y * 0.1F + vector3f1.y * 0.1F,
                            cCNode1.z,
                            cCNode1.x,
                            cCNode1.y,
                            cCNode1.z,
                            float0,
                            float1,
                            float2,
                            null,
                            true
                        );
                        vector3fObjectPool.release(vector3f0);
                        vector3fObjectPool.release(vector3f1);
                    }
                }

                CollideWithObstaclesPoly.CCNode cCNode2 = this.get(0).node1;
                LineDrawer.addRect(cCNode2.x - 0.1F, cCNode2.y - 0.1F, cCNode2.z, 0.2F, 0.2F, 1.0F, 0.0F, 0.0F);
            }
        }

        static void releaseAll(ArrayList<CollideWithObstaclesPoly.CCEdgeRing> arrayList) {
            pool.releaseAll(arrayList);
        }
    }

    private static final class CCNode {
        float x;
        float y;
        int z;
        final ArrayList<CollideWithObstaclesPoly.CCEdge> edges = new ArrayList<>();
        static final ObjectPool<CollideWithObstaclesPoly.CCNode> pool = new ObjectPool<>(CollideWithObstaclesPoly.CCNode::new);

        CollideWithObstaclesPoly.CCNode init(float float0, float float1, int int0) {
            this.x = float0;
            this.y = float1;
            this.z = int0;
            this.edges.clear();
            return this;
        }

        CollideWithObstaclesPoly.CCNode setXY(float float0, float float1) {
            this.x = float0;
            this.y = float1;
            return this;
        }

        boolean getNormalAndEdgeVectors(Vector2 vector0, Vector2 vector1) {
            CollideWithObstaclesPoly.CCEdge cCEdge0 = null;
            CollideWithObstaclesPoly.CCEdge cCEdge1 = null;

            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstaclesPoly.CCEdge cCEdge2 = this.edges.get(int0);
                if (cCEdge0 == null) {
                    cCEdge0 = cCEdge2;
                } else if (!cCEdge0.hasNode(cCEdge2.node1) || !cCEdge0.hasNode(cCEdge2.node2)) {
                    cCEdge1 = cCEdge2;
                }
            }

            if (cCEdge0 != null && cCEdge1 != null) {
                float float0 = cCEdge0.normal.x + cCEdge1.normal.x;
                float float1 = cCEdge0.normal.y + cCEdge1.normal.y;
                vector0.set(float0, float1);
                vector0.normalize();
                if (cCEdge0.node1 == this) {
                    vector1.set(cCEdge0.node2.x - cCEdge0.node1.x, cCEdge0.node2.y - cCEdge0.node1.y);
                } else {
                    vector1.set(cCEdge0.node1.x - cCEdge0.node2.x, cCEdge0.node1.y - cCEdge0.node2.y);
                }

                vector1.normalize();
                return true;
            } else {
                return false;
            }
        }

        static CollideWithObstaclesPoly.CCNode alloc() {
            return pool.alloc();
        }

        void release() {
            pool.release(this);
        }

        static void releaseAll(ArrayList<CollideWithObstaclesPoly.CCNode> arrayList) {
            pool.releaseAll(arrayList);
        }
    }

    private static final class CCObstacle {
        final CollideWithObstaclesPoly.CCEdgeRing outer = new CollideWithObstaclesPoly.CCEdgeRing();
        final ArrayList<CollideWithObstaclesPoly.CCEdgeRing> inner = new ArrayList<>();
        BaseVehicle vehicle = null;
        CollideWithObstaclesPoly.ImmutableRectF bounds;
        static final ObjectPool<CollideWithObstaclesPoly.CCObstacle> pool = new ObjectPool<CollideWithObstaclesPoly.CCObstacle>(
            CollideWithObstaclesPoly.CCObstacle::new
        ) {
            public void release(CollideWithObstaclesPoly.CCObstacle cCObstacle) {
                CollideWithObstaclesPoly.CCEdge.releaseAll(cCObstacle.outer);
                CollideWithObstaclesPoly.CCEdgeRing.releaseAll(cCObstacle.inner);
                cCObstacle.outer.clear();
                cCObstacle.inner.clear();
                cCObstacle.vehicle = null;
                super.release(cCObstacle);
            }
        };

        CollideWithObstaclesPoly.CCObstacle init() {
            this.outer.clear();
            this.inner.clear();
            this.vehicle = null;
            return this;
        }

        boolean isPointInside(float float0, float float1, int int0) {
            if (this.outer.isPointInPolygon_WindingNumber(float0, float1, int0) != CollideWithObstaclesPoly.EdgeRingHit.Inside) {
                return false;
            } else if (this.inner.isEmpty()) {
                return true;
            } else {
                for (int int1 = 0; int1 < this.inner.size(); int1++) {
                    CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = this.inner.get(int1);
                    if (cCEdgeRing.isPointInPolygon_WindingNumber(float0, float1, int0) != CollideWithObstaclesPoly.EdgeRingHit.Outside) {
                        return false;
                    }
                }

                return true;
            }
        }

        boolean lineSegmentIntersects(float float0, float float1, float float2, float float3, boolean boolean0) {
            if (this.outer.lineSegmentIntersects(float0, float1, float2, float3, boolean0, true)) {
                return true;
            } else {
                for (int int0 = 0; int0 < this.inner.size(); int0++) {
                    CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = this.inner.get(int0);
                    if (cCEdgeRing.lineSegmentIntersects(float0, float1, float2, float3, boolean0, false)) {
                        return true;
                    }
                }

                return false;
            }
        }

        void lineSegmentIntersect(
            float float0, float float1, float float2, float float3, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge, boolean boolean0
        ) {
            this.outer.lineSegmentIntersect(float0, float1, float2, float3, closestPointOnEdge, boolean0);

            for (int int0 = 0; int0 < this.inner.size(); int0++) {
                CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = this.inner.get(int0);
                cCEdgeRing.lineSegmentIntersect(float0, float1, float2, float3, closestPointOnEdge, boolean0);
            }
        }

        void getClosestPointOnEdge(float float0, float float1, CollideWithObstaclesPoly.ClosestPointOnEdge closestPointOnEdge) {
            this.outer.getClosestPointOnEdge(float0, float1, closestPointOnEdge);

            for (int int0 = 0; int0 < this.inner.size(); int0++) {
                CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = this.inner.get(int0);
                cCEdgeRing.getClosestPointOnEdge(float0, float1, closestPointOnEdge);
            }
        }

        void calcBounds() {
            float float0 = Float.MAX_VALUE;
            float float1 = Float.MAX_VALUE;
            float float2 = Float.MIN_VALUE;
            float float3 = Float.MIN_VALUE;

            for (int int0 = 0; int0 < this.outer.size(); int0++) {
                CollideWithObstaclesPoly.CCEdge cCEdge = this.outer.get(int0);
                float0 = Math.min(float0, cCEdge.node1.x);
                float1 = Math.min(float1, cCEdge.node1.y);
                float2 = Math.max(float2, cCEdge.node1.x);
                float3 = Math.max(float3, cCEdge.node1.y);
            }

            if (this.bounds != null) {
                this.bounds.release();
            }

            float float4 = 0.01F;
            this.bounds = CollideWithObstaclesPoly.ImmutableRectF.alloc()
                .init(float0 - float4, float1 - float4, float2 - float0 + float4 * 2.0F, float3 - float1 + float4 * 2.0F);
        }

        void render() {
            this.outer.render(true);

            for (int int0 = 0; int0 < this.inner.size(); int0++) {
                this.inner.get(int0).render(false);
            }
        }

        static CollideWithObstaclesPoly.CCObstacle alloc() {
            return pool.alloc();
        }

        void release() {
            pool.release(this);
        }

        static void releaseAll(ArrayList<CollideWithObstaclesPoly.CCObstacle> arrayList) {
            pool.releaseAll(arrayList);
        }
    }

    public static final class ChunkData {
        final CollideWithObstaclesPoly.ChunkDataZ[] data = new CollideWithObstaclesPoly.ChunkDataZ[8];
        private boolean bClear = false;

        public CollideWithObstaclesPoly.ChunkDataZ init(IsoChunk chunk, int int0, CollideWithObstaclesPoly collideWithObstaclesPoly) {
            assert Thread.currentThread() == GameWindow.GameThread;

            if (this.bClear) {
                this.bClear = false;
                this.clearInner();
            }

            if (this.data[int0] == null) {
                this.data[int0] = CollideWithObstaclesPoly.ChunkDataZ.pool.alloc();
                this.data[int0].init(chunk, int0, collideWithObstaclesPoly);
            }

            return this.data[int0];
        }

        private void clearInner() {
            PZArrayUtil.forEach(this.data, chunkDataZ -> {
                if (chunkDataZ != null) {
                    chunkDataZ.clear();
                    CollideWithObstaclesPoly.ChunkDataZ.pool.release(chunkDataZ);
                }
            });
            Arrays.fill(this.data, null);
        }

        public void clear() {
            this.bClear = true;
        }
    }

    public static final class ChunkDataZ {
        public final ArrayList<CollideWithObstaclesPoly.CCObstacle> worldVehicleUnion = new ArrayList<>();
        public final ArrayList<CollideWithObstaclesPoly.CCObstacle> worldVehicleSeparate = new ArrayList<>();
        public final ArrayList<CollideWithObstaclesPoly.CCNode> nodes = new ArrayList<>();
        public int z;
        public static final ObjectPool<CollideWithObstaclesPoly.ChunkDataZ> pool = new ObjectPool<>(CollideWithObstaclesPoly.ChunkDataZ::new);

        public void init(IsoChunk chunk, int int0, CollideWithObstaclesPoly collideWithObstaclesPoly) {
            this.z = int0;
            Clipper clipper = collideWithObstaclesPoly.clipper;
            clipper.clear();
            float float0 = 0.19800001F;
            int int1 = chunk.wx * 10;
            int int2 = chunk.wy * 10;

            for (int int3 = int2 - 2; int3 < int2 + 10 + 2; int3++) {
                for (int int4 = int1 - 2; int4 < int1 + 10 + 2; int4++) {
                    IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int3, int0);
                    if (square0 != null && !square0.getObjects().isEmpty()) {
                        if (square0.isSolid() || square0.isSolidTrans() && !square0.isAdjacentToWindow()) {
                            clipper.addAABBBevel(int4 - 0.3F, int3 - 0.3F, int4 + 1.0F + 0.3F, int3 + 1.0F + 0.3F, float0);
                        }

                        boolean boolean0 = square0.Is(IsoFlagType.collideW) || square0.hasBlockedDoor(false) || square0.HasStairsNorth();
                        if (square0.Is(IsoFlagType.windowW) || square0.Is(IsoFlagType.WindowW)) {
                            boolean0 = true;
                        }

                        if (boolean0) {
                            if (!this.isCollideW(int4, int3 - 1, int0)) {
                            }

                            boolean boolean1 = false;
                            if (!this.isCollideW(int4, int3 + 1, int0)) {
                            }

                            boolean boolean2 = false;
                            clipper.addAABBBevel(int4 - 0.3F, int3 - (boolean1 ? 0.0F : 0.3F), int4 + 0.3F, int3 + 1.0F + (boolean2 ? 0.0F : 0.3F), float0);
                        }

                        boolean boolean3 = square0.Is(IsoFlagType.collideN) || square0.hasBlockedDoor(true) || square0.HasStairsWest();
                        if (square0.Is(IsoFlagType.windowN) || square0.Is(IsoFlagType.WindowN)) {
                            boolean3 = true;
                        }

                        if (boolean3) {
                            if (!this.isCollideN(int4 - 1, int3, int0)) {
                            }

                            boolean boolean4 = false;
                            if (!this.isCollideN(int4 + 1, int3, int0)) {
                            }

                            boolean boolean5 = false;
                            clipper.addAABBBevel(int4 - (boolean4 ? 0.0F : 0.3F), int3 - 0.3F, int4 + 1.0F + (boolean5 ? 0.0F : 0.3F), int3 + 0.3F, float0);
                        }

                        if (square0.HasStairsNorth()) {
                            IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare(int4 + 1, int3, int0);
                            if (square1 != null) {
                                clipper.addAABBBevel(int4 + 1 - 0.3F, int3 - 0.3F, int4 + 1 + 0.3F, int3 + 1.0F + 0.3F, float0);
                            }

                            if (square0.Has(IsoObjectType.stairsTN)) {
                                IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int3, int0 - 1);
                                if (square2 == null || !square2.Has(IsoObjectType.stairsTN)) {
                                    clipper.addAABBBevel(int4 - 0.3F, int3 - 0.3F, int4 + 1.0F + 0.3F, int3 + 0.3F, float0);
                                    float float1 = 0.1F;
                                    clipper.clipAABB(int4 + 0.3F, int3 - float1, int4 + 1.0F - 0.3F, int3 + 0.3F);
                                }
                            }
                        }

                        if (square0.HasStairsWest()) {
                            IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int3 + 1, int0);
                            if (square3 != null) {
                                clipper.addAABBBevel(int4 - 0.3F, int3 + 1 - 0.3F, int4 + 1.0F + 0.3F, int3 + 1 + 0.3F, float0);
                            }

                            if (square0.Has(IsoObjectType.stairsTW)) {
                                IsoGridSquare square4 = IsoWorld.instance.CurrentCell.getGridSquare(int4, int3, int0 - 1);
                                if (square4 == null || !square4.Has(IsoObjectType.stairsTW)) {
                                    clipper.addAABBBevel(int4 - 0.3F, int3 - 0.3F, int4 + 0.3F, int3 + 1.0F + 0.3F, float0);
                                    float float2 = 0.1F;
                                    clipper.clipAABB(int4 - float2, int3 + 0.3F, int4 + 0.3F, int3 + 1.0F - 0.3F);
                                }
                            }
                        }
                    }
                }
            }

            ByteBuffer byteBuffer = collideWithObstaclesPoly.xyBuffer;

            assert this.worldVehicleSeparate.isEmpty();

            this.clipperToObstacles(clipper, byteBuffer, this.worldVehicleSeparate);
            int int5 = chunk.wx * 10;
            int int6 = chunk.wy * 10;
            int int7 = int5 + 10;
            int int8 = int6 + 10;
            int5 -= 2;
            int6 -= 2;
            int7 += 2;
            int8 += 2;
            CollideWithObstaclesPoly.ImmutableRectF immutableRectF = collideWithObstaclesPoly.moveBounds.init(int5, int6, int7 - int5, int8 - int6);
            collideWithObstaclesPoly.getVehiclesInRect(int5 - 5, int6 - 5, int7 + 5, int8 + 5, int0);

            for (int int9 = 0; int9 < collideWithObstaclesPoly.vehicles.size(); int9++) {
                BaseVehicle vehicle = collideWithObstaclesPoly.vehicles.get(int9);
                PolygonalMap2.VehiclePoly vehiclePoly = vehicle.getPolyPlusRadius();
                float float3 = Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
                float float4 = Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
                float float5 = Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
                float float6 = Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
                collideWithObstaclesPoly.vehicleBounds.init(float3, float4, float5 - float3, float6 - float4);
                if (immutableRectF.intersects(collideWithObstaclesPoly.vehicleBounds)) {
                    clipper.addPolygon(
                        vehiclePoly.x1, vehiclePoly.y1, vehiclePoly.x4, vehiclePoly.y4, vehiclePoly.x3, vehiclePoly.y3, vehiclePoly.x2, vehiclePoly.y2
                    );
                    CollideWithObstaclesPoly.CCNode cCNode0 = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int0);
                    CollideWithObstaclesPoly.CCNode cCNode1 = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int0);
                    CollideWithObstaclesPoly.CCNode cCNode2 = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int0);
                    CollideWithObstaclesPoly.CCNode cCNode3 = CollideWithObstaclesPoly.CCNode.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int0);
                    CollideWithObstaclesPoly.CCObstacle cCObstacle = CollideWithObstaclesPoly.CCObstacle.alloc().init();
                    cCObstacle.vehicle = vehicle;
                    CollideWithObstaclesPoly.CCEdge cCEdge0 = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode0, cCNode1, cCObstacle);
                    CollideWithObstaclesPoly.CCEdge cCEdge1 = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode1, cCNode2, cCObstacle);
                    CollideWithObstaclesPoly.CCEdge cCEdge2 = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode2, cCNode3, cCObstacle);
                    CollideWithObstaclesPoly.CCEdge cCEdge3 = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode3, cCNode0, cCObstacle);
                    cCObstacle.outer.add(cCEdge0);
                    cCObstacle.outer.add(cCEdge1);
                    cCObstacle.outer.add(cCEdge2);
                    cCObstacle.outer.add(cCEdge3);
                    cCObstacle.calcBounds();
                    this.worldVehicleSeparate.add(cCObstacle);
                    this.nodes.add(cCNode0);
                    this.nodes.add(cCNode1);
                    this.nodes.add(cCNode2);
                    this.nodes.add(cCNode3);
                }
            }

            assert this.worldVehicleUnion.isEmpty();

            this.clipperToObstacles(clipper, byteBuffer, this.worldVehicleUnion);
        }

        private void getEdgesFromBuffer(ByteBuffer byteBuffer, CollideWithObstaclesPoly.CCObstacle cCObstacle, boolean boolean0) {
            short short0 = byteBuffer.getShort();
            if (short0 < 3) {
                byteBuffer.position(byteBuffer.position() + short0 * 4 * 2);
            } else {
                CollideWithObstaclesPoly.CCEdgeRing cCEdgeRing = cCObstacle.outer;
                if (!boolean0) {
                    cCEdgeRing = CollideWithObstaclesPoly.CCEdgeRing.pool.alloc();
                    cCEdgeRing.clear();
                    cCObstacle.inner.add(cCEdgeRing);
                }

                int int0 = this.nodes.size();

                for (int int1 = 0; int1 < short0; int1++) {
                    float float0 = byteBuffer.getFloat();
                    float float1 = byteBuffer.getFloat();
                    CollideWithObstaclesPoly.CCNode cCNode0 = CollideWithObstaclesPoly.CCNode.alloc().init(float0, float1, this.z);
                    this.nodes.add(int0, cCNode0);
                }

                for (int int2 = int0; int2 < this.nodes.size() - 1; int2++) {
                    CollideWithObstaclesPoly.CCNode cCNode1 = this.nodes.get(int2);
                    CollideWithObstaclesPoly.CCNode cCNode2 = this.nodes.get(int2 + 1);
                    CollideWithObstaclesPoly.CCEdge cCEdge = CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode1, cCNode2, cCObstacle);
                    cCEdgeRing.add(cCEdge);
                }

                CollideWithObstaclesPoly.CCNode cCNode3 = this.nodes.get(this.nodes.size() - 1);
                CollideWithObstaclesPoly.CCNode cCNode4 = this.nodes.get(int0);
                cCEdgeRing.add(CollideWithObstaclesPoly.CCEdge.alloc().init(cCNode3, cCNode4, cCObstacle));
            }
        }

        private void clipperToObstacles(Clipper clipper, ByteBuffer byteBuffer, ArrayList<CollideWithObstaclesPoly.CCObstacle> arrayList) {
            int int0 = clipper.generatePolygons();

            for (int int1 = 0; int1 < int0; int1++) {
                byteBuffer.clear();
                clipper.getPolygon(int1, byteBuffer);
                CollideWithObstaclesPoly.CCObstacle cCObstacle = CollideWithObstaclesPoly.CCObstacle.alloc().init();
                this.getEdgesFromBuffer(byteBuffer, cCObstacle, true);
                short short0 = byteBuffer.getShort();

                for (int int2 = 0; int2 < short0; int2++) {
                    this.getEdgesFromBuffer(byteBuffer, cCObstacle, false);
                }

                cCObstacle.calcBounds();
                arrayList.add(cCObstacle);
            }
        }

        boolean isCollideW(int int0, int int1, int int2) {
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            return square != null && (square.Is(IsoFlagType.collideW) || square.hasBlockedDoor(false) || square.HasStairsNorth());
        }

        boolean isCollideN(int int0, int int1, int int2) {
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            return square != null && (square.Is(IsoFlagType.collideN) || square.hasBlockedDoor(true) || square.HasStairsWest());
        }

        boolean isOpenDoorAt(int int0, int int1, int int2, boolean boolean0) {
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int0, int1, int2);
            return square != null && square.getDoor(boolean0) != null && !square.hasBlockedDoor(boolean0);
        }

        public void clear() {
            CollideWithObstaclesPoly.CCNode.releaseAll(this.nodes);
            this.nodes.clear();
            CollideWithObstaclesPoly.CCObstacle.releaseAll(this.worldVehicleUnion);
            this.worldVehicleUnion.clear();
            CollideWithObstaclesPoly.CCObstacle.releaseAll(this.worldVehicleSeparate);
            this.worldVehicleSeparate.clear();
        }
    }

    private static final class ClosestPointOnEdge {
        CollideWithObstaclesPoly.CCEdge edge;
        CollideWithObstaclesPoly.CCNode node;
        final Vector2f point = new Vector2f();
        double distSq;
    }

    private static enum EdgeRingHit {
        OnEdge,
        Inside,
        Outside;
    }

    private static final class ImmutableRectF {
        private float x;
        private float y;
        private float w;
        private float h;
        static final ArrayDeque<CollideWithObstaclesPoly.ImmutableRectF> pool = new ArrayDeque<>();

        CollideWithObstaclesPoly.ImmutableRectF init(float float0, float float1, float float2, float float3) {
            this.x = float0;
            this.y = float1;
            this.w = float2;
            this.h = float3;
            return this;
        }

        float left() {
            return this.x;
        }

        float top() {
            return this.y;
        }

        float right() {
            return this.x + this.w;
        }

        float bottom() {
            return this.y + this.h;
        }

        float width() {
            return this.w;
        }

        float height() {
            return this.h;
        }

        boolean containsPoint(float float1, float float0) {
            return float1 >= this.left() && float1 < this.right() && float0 >= this.top() && float0 < this.bottom();
        }

        boolean intersects(CollideWithObstaclesPoly.ImmutableRectF immutableRectF0) {
            return this.left() < immutableRectF0.right()
                && this.right() > immutableRectF0.left()
                && this.top() < immutableRectF0.bottom()
                && this.bottom() > immutableRectF0.top();
        }

        static CollideWithObstaclesPoly.ImmutableRectF alloc() {
            return pool.isEmpty() ? new CollideWithObstaclesPoly.ImmutableRectF() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }
}
