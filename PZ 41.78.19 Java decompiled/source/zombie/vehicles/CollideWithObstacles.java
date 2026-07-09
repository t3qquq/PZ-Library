// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.joml.Vector2f;
import zombie.characters.IsoGameCharacter;
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

public final class CollideWithObstacles {
    static final float RADIUS = 0.3F;
    private final ArrayList<CollideWithObstacles.CCObstacle> obstacles = new ArrayList<>();
    private final ArrayList<CollideWithObstacles.CCNode> nodes = new ArrayList<>();
    private final ArrayList<CollideWithObstacles.CCIntersection> intersections = new ArrayList<>();
    private final CollideWithObstacles.ImmutableRectF moveBounds = new CollideWithObstacles.ImmutableRectF();
    private final CollideWithObstacles.ImmutableRectF vehicleBounds = new CollideWithObstacles.ImmutableRectF();
    private final Vector2 move = new Vector2();
    private final Vector2 closest = new Vector2();
    private final Vector2 nodeNormal = new Vector2();
    private final Vector2 edgeVec = new Vector2();
    private final ArrayList<BaseVehicle> vehicles = new ArrayList<>();
    CollideWithObstacles.CCObjectOutline[][] oo = new CollideWithObstacles.CCObjectOutline[5][5];
    ArrayList<CollideWithObstacles.CCNode> obstacleTraceNodes = new ArrayList<>();
    CollideWithObstacles.CompareIntersection comparator = new CollideWithObstacles.CompareIntersection();

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

    void getObstaclesInRect(float float3, float float1, float float2, float float0, int int4, int int6, int int0) {
        this.nodes.clear();
        this.obstacles.clear();
        this.moveBounds.init(float3 - 1.0F, float1 - 1.0F, float2 - float3 + 2.0F, float0 - float1 + 2.0F);
        this.getVehiclesInRect(float3 - 1.0F - 4.0F, float1 - 1.0F - 4.0F, float2 + 2.0F + 8.0F, float0 + 2.0F + 8.0F, int0);

        for (int int1 = 0; int1 < this.vehicles.size(); int1++) {
            BaseVehicle vehicle = this.vehicles.get(int1);
            PolygonalMap2.VehiclePoly vehiclePoly = vehicle.getPolyPlusRadius();
            float float4 = Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
            float float5 = Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
            float float6 = Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
            float float7 = Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
            this.vehicleBounds.init(float4, float5, float6 - float4, float7 - float5);
            if (this.moveBounds.intersects(this.vehicleBounds)) {
                int int2 = (int)vehiclePoly.z;
                CollideWithObstacles.CCNode cCNode0 = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int2);
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int2);
                CollideWithObstacles.CCNode cCNode2 = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int2);
                CollideWithObstacles.CCNode cCNode3 = CollideWithObstacles.CCNode.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int2);
                CollideWithObstacles.CCObstacle cCObstacle0 = CollideWithObstacles.CCObstacle.alloc().init();
                CollideWithObstacles.CCEdge cCEdge0 = CollideWithObstacles.CCEdge.alloc().init(cCNode0, cCNode1, cCObstacle0);
                CollideWithObstacles.CCEdge cCEdge1 = CollideWithObstacles.CCEdge.alloc().init(cCNode1, cCNode2, cCObstacle0);
                CollideWithObstacles.CCEdge cCEdge2 = CollideWithObstacles.CCEdge.alloc().init(cCNode2, cCNode3, cCObstacle0);
                CollideWithObstacles.CCEdge cCEdge3 = CollideWithObstacles.CCEdge.alloc().init(cCNode3, cCNode0, cCObstacle0);
                cCObstacle0.edges.add(cCEdge0);
                cCObstacle0.edges.add(cCEdge1);
                cCObstacle0.edges.add(cCEdge2);
                cCObstacle0.edges.add(cCEdge3);
                cCObstacle0.calcBounds();
                this.obstacles.add(cCObstacle0);
                this.nodes.add(cCNode0);
                this.nodes.add(cCNode1);
                this.nodes.add(cCNode2);
                this.nodes.add(cCNode3);
            }
        }

        if (!this.obstacles.isEmpty()) {
            int int3 = int4 - 2;
            int int5 = int6 - 2;
            int int7 = int4 + 2 + 1;
            int int8 = int6 + 2 + 1;

            for (int int9 = int5; int9 < int8; int9++) {
                for (int int10 = int3; int10 < int7; int10++) {
                    CollideWithObstacles.CCObjectOutline.get(int10 - int3, int9 - int5, int0, this.oo).init(int10 - int3, int9 - int5, int0);
                }
            }

            for (int int11 = int5; int11 < int8 - 1; int11++) {
                for (int int12 = int3; int12 < int7 - 1; int12++) {
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int12, int11, int0);
                    if (square != null) {
                        if (square.isSolid()
                            || square.isSolidTrans() && !square.isAdjacentToWindow()
                            || square.Has(IsoObjectType.stairsMN)
                            || square.Has(IsoObjectType.stairsTN)
                            || square.Has(IsoObjectType.stairsMW)
                            || square.Has(IsoObjectType.stairsTW)) {
                            CollideWithObstacles.CCObjectOutline.setSolid(int12 - int3, int11 - int5, int0, this.oo);
                        }

                        boolean boolean0 = square.Is(IsoFlagType.collideW);
                        if (square.Is(IsoFlagType.windowW) || square.Is(IsoFlagType.WindowW)) {
                            boolean0 = true;
                        }

                        if (boolean0 && square.Is(IsoFlagType.doorW)) {
                            boolean0 = false;
                        }

                        boolean boolean1 = square.Is(IsoFlagType.collideN);
                        if (square.Is(IsoFlagType.windowN) || square.Is(IsoFlagType.WindowN)) {
                            boolean1 = true;
                        }

                        if (boolean1 && square.Is(IsoFlagType.doorN)) {
                            boolean1 = false;
                        }

                        if (boolean0 || square.hasBlockedDoor(false) || square.Has(IsoObjectType.stairsBN)) {
                            CollideWithObstacles.CCObjectOutline.setWest(int12 - int3, int11 - int5, int0, this.oo);
                        }

                        if (boolean1 || square.hasBlockedDoor(true) || square.Has(IsoObjectType.stairsBW)) {
                            CollideWithObstacles.CCObjectOutline.setNorth(int12 - int3, int11 - int5, int0, this.oo);
                        }

                        if (square.Has(IsoObjectType.stairsBN) && int12 != int7 - 2) {
                            square = IsoWorld.instance.CurrentCell.getGridSquare(int12 + 1, int11, int0);
                            if (square != null) {
                                CollideWithObstacles.CCObjectOutline.setWest(int12 + 1 - int3, int11 - int5, int0, this.oo);
                            }
                        } else if (square.Has(IsoObjectType.stairsBW) && int11 != int8 - 2) {
                            square = IsoWorld.instance.CurrentCell.getGridSquare(int12, int11 + 1, int0);
                            if (square != null) {
                                CollideWithObstacles.CCObjectOutline.setNorth(int12 - int3, int11 + 1 - int5, int0, this.oo);
                            }
                        }
                    }
                }
            }

            for (int int13 = 0; int13 < int8 - int5; int13++) {
                for (int int14 = 0; int14 < int7 - int3; int14++) {
                    CollideWithObstacles.CCObjectOutline cCObjectOutline = CollideWithObstacles.CCObjectOutline.get(int14, int13, int0, this.oo);
                    if (cCObjectOutline != null && cCObjectOutline.nw && cCObjectOutline.nw_w && cCObjectOutline.nw_n) {
                        cCObjectOutline.trace(this.oo, this.obstacleTraceNodes);
                        if (!cCObjectOutline.nodes.isEmpty()) {
                            CollideWithObstacles.CCObstacle cCObstacle1 = CollideWithObstacles.CCObstacle.alloc().init();
                            CollideWithObstacles.CCNode cCNode4 = cCObjectOutline.nodes.get(cCObjectOutline.nodes.size() - 1);

                            for (int int15 = cCObjectOutline.nodes.size() - 1; int15 > 0; int15--) {
                                CollideWithObstacles.CCNode cCNode5 = cCObjectOutline.nodes.get(int15);
                                CollideWithObstacles.CCNode cCNode6 = cCObjectOutline.nodes.get(int15 - 1);
                                cCNode5.x += int3;
                                cCNode5.y += int5;
                                CollideWithObstacles.CCEdge cCEdge4 = CollideWithObstacles.CCEdge.alloc().init(cCNode5, cCNode6, cCObstacle1);
                                float float8 = cCNode6.x + (cCNode6 != cCNode4 ? int3 : 0.0F);
                                float float9 = cCNode6.y + (cCNode6 != cCNode4 ? int5 : 0.0F);
                                cCEdge4.normal.set(float8 - cCNode5.x, float9 - cCNode5.y);
                                cCEdge4.normal.normalize();
                                cCEdge4.normal.rotate((float)Math.toRadians(90.0));
                                cCObstacle1.edges.add(cCEdge4);
                                this.nodes.add(cCNode5);
                            }

                            cCObstacle1.calcBounds();
                            this.obstacles.add(cCObstacle1);
                        }
                    }
                }
            }
        }
    }

    void checkEdgeIntersection() {
        boolean boolean0 = Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderObstacles.getValue();

        for (int int0 = 0; int0 < this.obstacles.size(); int0++) {
            CollideWithObstacles.CCObstacle cCObstacle0 = this.obstacles.get(int0);

            for (int int1 = int0 + 1; int1 < this.obstacles.size(); int1++) {
                CollideWithObstacles.CCObstacle cCObstacle1 = this.obstacles.get(int1);
                if (cCObstacle0.bounds.intersects(cCObstacle1.bounds)) {
                    for (int int2 = 0; int2 < cCObstacle0.edges.size(); int2++) {
                        CollideWithObstacles.CCEdge cCEdge0 = cCObstacle0.edges.get(int2);

                        for (int int3 = 0; int3 < cCObstacle1.edges.size(); int3++) {
                            CollideWithObstacles.CCEdge cCEdge1 = cCObstacle1.edges.get(int3);
                            CollideWithObstacles.CCIntersection cCIntersection0 = this.getIntersection(cCEdge0, cCEdge1);
                            if (cCIntersection0 != null) {
                                cCEdge0.intersections.add(cCIntersection0);
                                cCEdge1.intersections.add(cCIntersection0);
                                if (boolean0) {
                                    LineDrawer.addLine(
                                        cCIntersection0.nodeSplit.x - 0.1F,
                                        cCIntersection0.nodeSplit.y - 0.1F,
                                        cCEdge0.node1.z,
                                        cCIntersection0.nodeSplit.x + 0.1F,
                                        cCIntersection0.nodeSplit.y + 0.1F,
                                        cCEdge0.node1.z,
                                        1.0F,
                                        0.0F,
                                        0.0F,
                                        null,
                                        false
                                    );
                                }

                                if (!cCEdge0.hasNode(cCIntersection0.nodeSplit) && !cCEdge1.hasNode(cCIntersection0.nodeSplit)) {
                                    this.nodes.add(cCIntersection0.nodeSplit);
                                }

                                this.intersections.add(cCIntersection0);
                            }
                        }
                    }
                }
            }
        }

        for (int int4 = 0; int4 < this.obstacles.size(); int4++) {
            CollideWithObstacles.CCObstacle cCObstacle2 = this.obstacles.get(int4);

            for (int int5 = cCObstacle2.edges.size() - 1; int5 >= 0; int5--) {
                CollideWithObstacles.CCEdge cCEdge2 = cCObstacle2.edges.get(int5);
                if (!cCEdge2.intersections.isEmpty()) {
                    this.comparator.edge = cCEdge2;
                    Collections.sort(cCEdge2.intersections, this.comparator);

                    for (int int6 = cCEdge2.intersections.size() - 1; int6 >= 0; int6--) {
                        CollideWithObstacles.CCIntersection cCIntersection1 = cCEdge2.intersections.get(int6);
                        CollideWithObstacles.CCEdge cCEdge3 = cCIntersection1.split(cCEdge2);
                    }
                }
            }
        }
    }

    boolean collinear(float float4, float float2, float float6, float float1, float float3, float float5) {
        float float0 = (float6 - float4) * (float5 - float2) - (float3 - float4) * (float1 - float2);
        return float0 >= -0.05F && float0 < 0.05F;
    }

    boolean within(float float1, float float0, float float2) {
        return float1 <= float0 && float0 <= float2 || float2 <= float0 && float0 <= float1;
    }

    boolean is_on(float float3, float float0, float float5, float float2, float float4, float float1) {
        return this.collinear(float3, float0, float5, float2, float4, float1)
            && (float3 != float5 ? this.within(float3, float4, float5) : this.within(float0, float1, float2));
    }

    public CollideWithObstacles.CCIntersection getIntersection(CollideWithObstacles.CCEdge cCEdge0, CollideWithObstacles.CCEdge cCEdge1) {
        float float0 = cCEdge0.node1.x;
        float float1 = cCEdge0.node1.y;
        float float2 = cCEdge0.node2.x;
        float float3 = cCEdge0.node2.y;
        float float4 = cCEdge1.node1.x;
        float float5 = cCEdge1.node1.y;
        float float6 = cCEdge1.node2.x;
        float float7 = cCEdge1.node2.y;
        double double0 = (float7 - float5) * (float2 - float0) - (float6 - float4) * (float3 - float1);
        if (double0 > -0.01 && double0 < 0.01) {
            return null;
        } else {
            double double1 = ((float6 - float4) * (float1 - float5) - (float7 - float5) * (float0 - float4)) / double0;
            double double2 = ((float2 - float0) * (float1 - float5) - (float3 - float1) * (float0 - float4)) / double0;
            if (double1 >= 0.0 && double1 <= 1.0 && double2 >= 0.0 && double2 <= 1.0) {
                float float8 = (float)(float0 + double1 * (float2 - float0));
                float float9 = (float)(float1 + double1 * (float3 - float1));
                CollideWithObstacles.CCNode cCNode0 = null;
                CollideWithObstacles.CCNode cCNode1 = null;
                if (double1 < 0.01F) {
                    cCNode0 = cCEdge0.node1;
                } else if (double1 > 0.99F) {
                    cCNode0 = cCEdge0.node2;
                }

                if (double2 < 0.01F) {
                    cCNode1 = cCEdge1.node1;
                } else if (double2 > 0.99F) {
                    cCNode1 = cCEdge1.node2;
                }

                if (cCNode0 != null && cCNode1 != null) {
                    CollideWithObstacles.CCIntersection cCIntersection = CollideWithObstacles.CCIntersection.alloc()
                        .init(cCEdge0, cCEdge1, (float)double1, (float)double2, cCNode0);
                    cCEdge0.intersections.add(cCIntersection);
                    this.intersections.add(cCIntersection);
                    cCIntersection = CollideWithObstacles.CCIntersection.alloc().init(cCEdge0, cCEdge1, (float)double1, (float)double2, cCNode1);
                    cCEdge1.intersections.add(cCIntersection);
                    this.intersections.add(cCIntersection);
                    LineDrawer.addLine(
                        cCIntersection.nodeSplit.x - 0.1F,
                        cCIntersection.nodeSplit.y - 0.1F,
                        cCEdge0.node1.z,
                        cCIntersection.nodeSplit.x + 0.1F,
                        cCIntersection.nodeSplit.y + 0.1F,
                        cCEdge0.node1.z,
                        1.0F,
                        0.0F,
                        0.0F,
                        null,
                        false
                    );
                    return null;
                } else {
                    return cCNode0 == null && cCNode1 == null
                        ? CollideWithObstacles.CCIntersection.alloc().init(cCEdge0, cCEdge1, (float)double1, (float)double2, float8, float9)
                        : CollideWithObstacles.CCIntersection.alloc()
                            .init(cCEdge0, cCEdge1, (float)double1, (float)double2, cCNode0 == null ? cCNode1 : cCNode0);
                }
            } else {
                return null;
            }
        }
    }

    void checkNodesInObstacles() {
        for (int int0 = 0; int0 < this.nodes.size(); int0++) {
            CollideWithObstacles.CCNode cCNode = this.nodes.get(int0);

            for (int int1 = 0; int1 < this.obstacles.size(); int1++) {
                CollideWithObstacles.CCObstacle cCObstacle = this.obstacles.get(int1);
                boolean boolean0 = false;

                for (int int2 = 0; int2 < this.intersections.size(); int2++) {
                    CollideWithObstacles.CCIntersection cCIntersection = this.intersections.get(int2);
                    if (cCIntersection.nodeSplit == cCNode) {
                        if (cCIntersection.edge1.obstacle == cCObstacle || cCIntersection.edge2.obstacle == cCObstacle) {
                            boolean0 = true;
                        }
                        break;
                    }
                }

                if (!boolean0 && cCObstacle.isNodeInsideOf(cCNode)) {
                    cCNode.ignore = true;
                    break;
                }
            }
        }
    }

    boolean isVisible(CollideWithObstacles.CCNode cCNode0, CollideWithObstacles.CCNode cCNode1) {
        return cCNode0.sharesEdge(cCNode1) ? !cCNode0.onSameShapeButDoesNotShareAnEdge(cCNode1) : !cCNode0.sharesShape(cCNode1);
    }

    void calculateNodeVisibility() {
        for (int int0 = 0; int0 < this.obstacles.size(); int0++) {
            CollideWithObstacles.CCObstacle cCObstacle = this.obstacles.get(int0);

            for (int int1 = 0; int1 < cCObstacle.edges.size(); int1++) {
                CollideWithObstacles.CCEdge cCEdge = cCObstacle.edges.get(int1);
                if (!cCEdge.node1.ignore && !cCEdge.node2.ignore && this.isVisible(cCEdge.node1, cCEdge.node2)) {
                    cCEdge.node1.visible.add(cCEdge.node2);
                    cCEdge.node2.visible.add(cCEdge.node1);
                }
            }
        }
    }

    Vector2f resolveCollision(IsoGameCharacter character, float float0, float float1, Vector2f vector2f) {
        vector2f.set(float0, float1);
        if (character.getCurrentSquare() != null && character.getCurrentSquare().HasStairs()) {
            return vector2f;
        } else {
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
                this.move.set(float0 - character.x, float1 - character.y);
                this.move.normalize();

                for (int int0 = 0; int0 < this.nodes.size(); int0++) {
                    this.nodes.get(int0).release();
                }

                for (int int1 = 0; int1 < this.obstacles.size(); int1++) {
                    CollideWithObstacles.CCObstacle cCObstacle0 = this.obstacles.get(int1);

                    for (int int2 = 0; int2 < cCObstacle0.edges.size(); int2++) {
                        cCObstacle0.edges.get(int2).release();
                    }

                    cCObstacle0.release();
                }

                for (int int3 = 0; int3 < this.intersections.size(); int3++) {
                    this.intersections.get(int3).release();
                }

                this.intersections.clear();
                this.getObstaclesInRect(
                    Math.min(float2, float0),
                    Math.min(float3, float1),
                    Math.max(float2, float0),
                    Math.max(float3, float1),
                    (int)character.x,
                    (int)character.y,
                    (int)character.z
                );
                this.checkEdgeIntersection();
                this.checkNodesInObstacles();
                this.calculateNodeVisibility();
                if (boolean0) {
                    for (CollideWithObstacles.CCNode cCNode0 : this.nodes) {
                        for (CollideWithObstacles.CCNode cCNode1 : cCNode0.visible) {
                            LineDrawer.addLine(cCNode0.x, cCNode0.y, cCNode0.z, cCNode1.x, cCNode1.y, cCNode1.z, 0.0F, 1.0F, 0.0F, null, true);
                        }

                        if (DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()
                            && cCNode0.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
                            LineDrawer.addLine(
                                cCNode0.x,
                                cCNode0.y,
                                cCNode0.z,
                                cCNode0.x + this.nodeNormal.x,
                                cCNode0.y + this.nodeNormal.y,
                                cCNode0.z,
                                0.0F,
                                0.0F,
                                1.0F,
                                null,
                                true
                            );
                        }

                        if (cCNode0.ignore) {
                            LineDrawer.addLine(
                                cCNode0.x - 0.05F, cCNode0.y - 0.05F, cCNode0.z, cCNode0.x + 0.05F, cCNode0.y + 0.05F, cCNode0.z, 1.0F, 1.0F, 0.0F, null, false
                            );
                        }
                    }
                }

                CollideWithObstacles.CCEdge cCEdge0 = null;
                CollideWithObstacles.CCNode cCNode2 = null;
                double double0 = Double.MAX_VALUE;

                for (int int4 = 0; int4 < this.obstacles.size(); int4++) {
                    CollideWithObstacles.CCObstacle cCObstacle1 = this.obstacles.get(int4);
                    byte byte0 = 0;
                    if (cCObstacle1.isPointInside(character.x, character.y, byte0)) {
                        for (int int5 = 0; int5 < cCObstacle1.edges.size(); int5++) {
                            CollideWithObstacles.CCEdge cCEdge1 = cCObstacle1.edges.get(int5);
                            if (cCEdge1.node1.visible.contains(cCEdge1.node2)) {
                                CollideWithObstacles.CCNode cCNode3 = cCEdge1.closestPoint(character.x, character.y, this.closest);
                                double double1 = (character.x - this.closest.x) * (character.x - this.closest.x)
                                    + (character.y - this.closest.y) * (character.y - this.closest.y);
                                if (double1 < double0) {
                                    double0 = double1;
                                    cCEdge0 = cCEdge1;
                                    cCNode2 = cCNode3;
                                }
                            }
                        }
                    }
                }

                if (cCEdge0 != null) {
                    float float6 = cCEdge0.normal.dot(this.move);
                    if (float6 >= 0.01F) {
                        cCEdge0 = null;
                    }
                }

                if (cCNode2 != null
                    && cCNode2.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)
                    && this.nodeNormal.dot(this.move) + 0.05F >= this.nodeNormal.dot(this.edgeVec)) {
                    cCNode2 = null;
                    cCEdge0 = null;
                }

                if (cCEdge0 == null) {
                    double double2 = Double.MAX_VALUE;
                    cCEdge0 = null;
                    cCNode2 = null;

                    for (int int6 = 0; int6 < this.obstacles.size(); int6++) {
                        CollideWithObstacles.CCObstacle cCObstacle2 = this.obstacles.get(int6);

                        for (int int7 = 0; int7 < cCObstacle2.edges.size(); int7++) {
                            CollideWithObstacles.CCEdge cCEdge2 = cCObstacle2.edges.get(int7);
                            if (cCEdge2.node1.visible.contains(cCEdge2.node2)) {
                                float float7 = cCEdge2.node1.x;
                                float float8 = cCEdge2.node1.y;
                                float float9 = cCEdge2.node2.x;
                                float float10 = cCEdge2.node2.y;
                                float float11 = float7 + 0.5F * (float9 - float7);
                                float float12 = float8 + 0.5F * (float10 - float8);
                                if (boolean0 && DebugOptions.instance.CollideWithObstaclesRenderNormals.getValue()) {
                                    LineDrawer.addLine(
                                        float11,
                                        float12,
                                        cCEdge2.node1.z,
                                        float11 + cCEdge2.normal.x,
                                        float12 + cCEdge2.normal.y,
                                        cCEdge2.node1.z,
                                        0.0F,
                                        0.0F,
                                        1.0F,
                                        null,
                                        true
                                    );
                                }

                                double double3 = (float10 - float8) * (float4 - float2) - (float9 - float7) * (float5 - float3);
                                if (double3 != 0.0) {
                                    double double4 = ((float9 - float7) * (float3 - float8) - (float10 - float8) * (float2 - float7)) / double3;
                                    double double5 = ((float4 - float2) * (float3 - float8) - (float5 - float3) * (float2 - float7)) / double3;
                                    float float13 = cCEdge2.normal.dot(this.move);
                                    if (!(float13 >= 0.0F) && double4 >= 0.0 && double4 <= 1.0 && double5 >= 0.0 && double5 <= 1.0) {
                                        if (double5 < 0.01 || double5 > 0.99) {
                                            CollideWithObstacles.CCNode cCNode4 = double5 < 0.01 ? cCEdge2.node1 : cCEdge2.node2;
                                            if (cCNode4.getNormalAndEdgeVectors(this.nodeNormal, this.edgeVec)) {
                                                if (!(this.nodeNormal.dot(this.move) + 0.05F >= this.nodeNormal.dot(this.edgeVec))) {
                                                    cCEdge0 = cCEdge2;
                                                    cCNode2 = cCNode4;
                                                    break;
                                                }
                                                continue;
                                            }
                                        }

                                        float float14 = (float)(float2 + double4 * (float4 - float2));
                                        float float15 = (float)(float3 + double4 * (float5 - float3));
                                        double double6 = IsoUtils.DistanceToSquared(float2, float3, float14, float15);
                                        if (double6 < double2) {
                                            double2 = double6;
                                            cCEdge0 = cCEdge2;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (cCNode2 != null) {
                    CollideWithObstacles.CCEdge cCEdge3 = cCEdge0;
                    CollideWithObstacles.CCEdge cCEdge4 = null;

                    for (int int8 = 0; int8 < cCNode2.edges.size(); int8++) {
                        CollideWithObstacles.CCEdge cCEdge5 = cCNode2.edges.get(int8);
                        if (cCEdge5.node1.visible.contains(cCEdge5.node2)
                            && cCEdge5 != cCEdge0
                            && (
                                cCEdge3.node1.x != cCEdge5.node1.x
                                    || cCEdge3.node1.y != cCEdge5.node1.y
                                    || cCEdge3.node2.x != cCEdge5.node2.x
                                    || cCEdge3.node2.y != cCEdge5.node2.y
                            )
                            && (
                                cCEdge3.node1.x != cCEdge5.node2.x
                                    || cCEdge3.node1.y != cCEdge5.node2.y
                                    || cCEdge3.node2.x != cCEdge5.node1.x
                                    || cCEdge3.node2.y != cCEdge5.node1.y
                            )
                            && (!cCEdge3.hasNode(cCEdge5.node1) || !cCEdge3.hasNode(cCEdge5.node2))) {
                            cCEdge4 = cCEdge5;
                        }
                    }

                    if (cCEdge3 != null && cCEdge4 != null) {
                        if (cCEdge0 == cCEdge3) {
                            CollideWithObstacles.CCNode cCNode5 = cCNode2 == cCEdge4.node1 ? cCEdge4.node2 : cCEdge4.node1;
                            this.edgeVec.set(cCNode5.x - cCNode2.x, cCNode5.y - cCNode2.y);
                            this.edgeVec.normalize();
                            if (this.move.dot(this.edgeVec) >= 0.0F) {
                                cCEdge0 = cCEdge4;
                            }
                        } else if (cCEdge0 == cCEdge4) {
                            CollideWithObstacles.CCNode cCNode6 = cCNode2 == cCEdge3.node1 ? cCEdge3.node2 : cCEdge3.node1;
                            this.edgeVec.set(cCNode6.x - cCNode2.x, cCNode6.y - cCNode2.y);
                            this.edgeVec.normalize();
                            if (this.move.dot(this.edgeVec) >= 0.0F) {
                                cCEdge0 = cCEdge3;
                            }
                        }
                    }
                }

                if (cCEdge0 != null) {
                    float float16 = cCEdge0.node1.x;
                    float float17 = cCEdge0.node1.y;
                    float float18 = cCEdge0.node2.x;
                    float float19 = cCEdge0.node2.y;
                    if (boolean0) {
                        LineDrawer.addLine(float16, float17, cCEdge0.node1.z, float18, float19, cCEdge0.node1.z, 0.0F, 1.0F, 1.0F, null, true);
                    }

                    cCEdge0.closestPoint(float0, float1, this.closest);
                    vector2f.set(this.closest.x, this.closest.y);
                }

                return vector2f;
            }
        }
    }

    private static final class CCEdge {
        CollideWithObstacles.CCNode node1;
        CollideWithObstacles.CCNode node2;
        CollideWithObstacles.CCObstacle obstacle;
        final ArrayList<CollideWithObstacles.CCIntersection> intersections = new ArrayList<>();
        final Vector2 normal = new Vector2();
        static ArrayDeque<CollideWithObstacles.CCEdge> pool = new ArrayDeque<>();

        CollideWithObstacles.CCEdge init(CollideWithObstacles.CCNode cCNode1, CollideWithObstacles.CCNode cCNode0, CollideWithObstacles.CCObstacle cCObstacle) {
            if (cCNode1.x == cCNode0.x && cCNode1.y == cCNode0.y) {
                boolean boolean0 = false;
            }

            this.node1 = cCNode1;
            this.node2 = cCNode0;
            cCNode1.edges.add(this);
            cCNode0.edges.add(this);
            this.obstacle = cCObstacle;
            this.intersections.clear();
            this.normal.set(cCNode0.x - cCNode1.x, cCNode0.y - cCNode1.y);
            this.normal.normalize();
            this.normal.rotate((float)Math.toRadians(90.0));
            return this;
        }

        boolean hasNode(CollideWithObstacles.CCNode cCNode) {
            return cCNode == this.node1 || cCNode == this.node2;
        }

        CollideWithObstacles.CCEdge split(CollideWithObstacles.CCNode cCNode) {
            CollideWithObstacles.CCEdge cCEdge0 = alloc().init(cCNode, this.node2, this.obstacle);
            this.obstacle.edges.add(this.obstacle.edges.indexOf(this) + 1, cCEdge0);
            this.node2.edges.remove(this);
            this.node2 = cCNode;
            this.node2.edges.add(this);
            return cCEdge0;
        }

        CollideWithObstacles.CCNode closestPoint(float float5, float float4, Vector2 vector) {
            float float0 = this.node1.x;
            float float1 = this.node1.y;
            float float2 = this.node2.x;
            float float3 = this.node2.y;
            double double0 = ((float5 - float0) * (float2 - float0) + (float4 - float1) * (float3 - float1))
                / (Math.pow(float2 - float0, 2.0) + Math.pow(float3 - float1, 2.0));
            double double1 = 0.001;
            if (double0 <= 0.0 + double1) {
                vector.set(float0, float1);
                return this.node1;
            } else if (double0 >= 1.0 - double1) {
                vector.set(float2, float3);
                return this.node2;
            } else {
                double double2 = float0 + double0 * (float2 - float0);
                double double3 = float1 + double0 * (float3 - float1);
                vector.set((float)double2, (float)double3);
                return null;
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

        static CollideWithObstacles.CCEdge alloc() {
            return pool.isEmpty() ? new CollideWithObstacles.CCEdge() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class CCIntersection {
        CollideWithObstacles.CCEdge edge1;
        CollideWithObstacles.CCEdge edge2;
        float dist1;
        float dist2;
        CollideWithObstacles.CCNode nodeSplit;
        static ArrayDeque<CollideWithObstacles.CCIntersection> pool = new ArrayDeque<>();

        CollideWithObstacles.CCIntersection init(
            CollideWithObstacles.CCEdge cCEdge0, CollideWithObstacles.CCEdge cCEdge1, float float0, float float1, float float2, float float3
        ) {
            this.edge1 = cCEdge0;
            this.edge2 = cCEdge1;
            this.dist1 = float0;
            this.dist2 = float1;
            this.nodeSplit = CollideWithObstacles.CCNode.alloc().init(float2, float3, cCEdge0.node1.z);
            return this;
        }

        CollideWithObstacles.CCIntersection init(
            CollideWithObstacles.CCEdge cCEdge0, CollideWithObstacles.CCEdge cCEdge1, float float0, float float1, CollideWithObstacles.CCNode cCNode
        ) {
            this.edge1 = cCEdge0;
            this.edge2 = cCEdge1;
            this.dist1 = float0;
            this.dist2 = float1;
            this.nodeSplit = cCNode;
            return this;
        }

        CollideWithObstacles.CCEdge split(CollideWithObstacles.CCEdge cCEdge) {
            if (cCEdge.hasNode(this.nodeSplit)) {
                return null;
            } else if (cCEdge.node1.x == this.nodeSplit.x && cCEdge.node1.y == this.nodeSplit.y) {
                return null;
            } else {
                return cCEdge.node2.x == this.nodeSplit.x && cCEdge.node2.y == this.nodeSplit.y ? null : cCEdge.split(this.nodeSplit);
            }
        }

        static CollideWithObstacles.CCIntersection alloc() {
            return pool.isEmpty() ? new CollideWithObstacles.CCIntersection() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class CCNode {
        float x;
        float y;
        int z;
        boolean ignore;
        final ArrayList<CollideWithObstacles.CCEdge> edges = new ArrayList<>();
        final ArrayList<CollideWithObstacles.CCNode> visible = new ArrayList<>();
        static ArrayList<CollideWithObstacles.CCObstacle> tempObstacles = new ArrayList<>();
        static ArrayDeque<CollideWithObstacles.CCNode> pool = new ArrayDeque<>();

        CollideWithObstacles.CCNode init(float float0, float float1, int int0) {
            this.x = float0;
            this.y = float1;
            this.z = int0;
            this.ignore = false;
            this.edges.clear();
            this.visible.clear();
            return this;
        }

        CollideWithObstacles.CCNode setXY(float float0, float float1) {
            this.x = float0;
            this.y = float1;
            return this;
        }

        boolean sharesEdge(CollideWithObstacles.CCNode cCNode1) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int0);
                if (cCEdge.hasNode(cCNode1)) {
                    return true;
                }
            }

            return false;
        }

        boolean sharesShape(CollideWithObstacles.CCNode cCNode1) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge0 = this.edges.get(int0);

                for (int int1 = 0; int1 < cCNode1.edges.size(); int1++) {
                    CollideWithObstacles.CCEdge cCEdge1 = cCNode1.edges.get(int1);
                    if (cCEdge0.obstacle != null && cCEdge0.obstacle == cCEdge1.obstacle) {
                        return true;
                    }
                }
            }

            return false;
        }

        void getObstacles(ArrayList<CollideWithObstacles.CCObstacle> arrayList) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int0);
                if (!arrayList.contains(cCEdge.obstacle)) {
                    arrayList.add(cCEdge.obstacle);
                }
            }
        }

        boolean onSameShapeButDoesNotShareAnEdge(CollideWithObstacles.CCNode cCNode1) {
            tempObstacles.clear();
            this.getObstacles(tempObstacles);

            for (int int0 = 0; int0 < tempObstacles.size(); int0++) {
                CollideWithObstacles.CCObstacle cCObstacle = tempObstacles.get(int0);
                if (cCObstacle.hasNode(cCNode1) && !cCObstacle.hasAdjacentNodes(this, cCNode1)) {
                    return true;
                }
            }

            return false;
        }

        boolean getNormalAndEdgeVectors(Vector2 vector0, Vector2 vector1) {
            CollideWithObstacles.CCEdge cCEdge0 = null;
            CollideWithObstacles.CCEdge cCEdge1 = null;

            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge2 = this.edges.get(int0);
                if (cCEdge2.node1.visible.contains(cCEdge2.node2)) {
                    if (cCEdge0 == null) {
                        cCEdge0 = cCEdge2;
                    } else if (!cCEdge0.hasNode(cCEdge2.node1) || !cCEdge0.hasNode(cCEdge2.node2)) {
                        cCEdge1 = cCEdge2;
                    }
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

        static CollideWithObstacles.CCNode alloc() {
            if (pool.isEmpty()) {
                boolean boolean0 = false;
            } else {
                boolean boolean1 = false;
            }

            return pool.isEmpty() ? new CollideWithObstacles.CCNode() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class CCObjectOutline {
        int x;
        int y;
        int z;
        boolean nw;
        boolean nw_w;
        boolean nw_n;
        boolean nw_e;
        boolean nw_s;
        boolean w_w;
        boolean w_e;
        boolean w_cutoff;
        boolean n_n;
        boolean n_s;
        boolean n_cutoff;
        ArrayList<CollideWithObstacles.CCNode> nodes;
        static ArrayDeque<CollideWithObstacles.CCObjectOutline> pool = new ArrayDeque<>();

        CollideWithObstacles.CCObjectOutline init(int int0, int int1, int int2) {
            this.x = int0;
            this.y = int1;
            this.z = int2;
            this.nw = this.nw_w = this.nw_n = this.nw_e = false;
            this.w_w = this.w_e = this.w_cutoff = false;
            this.n_n = this.n_s = this.n_cutoff = false;
            return this;
        }

        static void setSolid(int int0, int int1, int int2, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines) {
            setWest(int0, int1, int2, cCObjectOutlines);
            setNorth(int0, int1, int2, cCObjectOutlines);
            setWest(int0 + 1, int1, int2, cCObjectOutlines);
            setNorth(int0, int1 + 1, int2, cCObjectOutlines);
        }

        static void setWest(int int0, int int1, int int2, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines) {
            CollideWithObstacles.CCObjectOutline cCObjectOutline = get(int0, int1, int2, cCObjectOutlines);
            if (cCObjectOutline != null) {
                if (cCObjectOutline.nw) {
                    cCObjectOutline.nw_s = false;
                } else {
                    cCObjectOutline.nw = true;
                    cCObjectOutline.nw_w = true;
                    cCObjectOutline.nw_n = true;
                    cCObjectOutline.nw_e = true;
                    cCObjectOutline.nw_s = false;
                }

                cCObjectOutline.w_w = true;
                cCObjectOutline.w_e = true;
            }

            cCObjectOutline = get(int0, int1 + 1, int2, cCObjectOutlines);
            if (cCObjectOutline == null) {
                if (cCObjectOutline != null) {
                    cCObjectOutline.w_cutoff = true;
                }
            } else if (cCObjectOutline.nw) {
                cCObjectOutline.nw_n = false;
            } else {
                cCObjectOutline.nw = true;
                cCObjectOutline.nw_n = false;
                cCObjectOutline.nw_w = true;
                cCObjectOutline.nw_e = true;
                cCObjectOutline.nw_s = true;
            }
        }

        static void setNorth(int int0, int int1, int int2, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines) {
            CollideWithObstacles.CCObjectOutline cCObjectOutline = get(int0, int1, int2, cCObjectOutlines);
            if (cCObjectOutline != null) {
                if (cCObjectOutline.nw) {
                    cCObjectOutline.nw_e = false;
                } else {
                    cCObjectOutline.nw = true;
                    cCObjectOutline.nw_w = true;
                    cCObjectOutline.nw_n = true;
                    cCObjectOutline.nw_e = false;
                    cCObjectOutline.nw_s = true;
                }

                cCObjectOutline.n_n = true;
                cCObjectOutline.n_s = true;
            }

            cCObjectOutline = get(int0 + 1, int1, int2, cCObjectOutlines);
            if (cCObjectOutline == null) {
                if (cCObjectOutline != null) {
                    cCObjectOutline.n_cutoff = true;
                }
            } else if (cCObjectOutline.nw) {
                cCObjectOutline.nw_w = false;
            } else {
                cCObjectOutline.nw = true;
                cCObjectOutline.nw_n = true;
                cCObjectOutline.nw_w = false;
                cCObjectOutline.nw_e = true;
                cCObjectOutline.nw_s = true;
            }
        }

        static CollideWithObstacles.CCObjectOutline get(int int0, int int1, int int2, CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines) {
            if (int0 < 0 || int0 >= cCObjectOutlines.length) {
                return null;
            } else if (int1 >= 0 && int1 < cCObjectOutlines[0].length) {
                if (cCObjectOutlines[int0][int1] == null) {
                    cCObjectOutlines[int0][int1] = alloc().init(int0, int1, int2);
                }

                return cCObjectOutlines[int0][int1];
            } else {
                return null;
            }
        }

        void trace_NW_N(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x + 0.3F, this.y - 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x + 0.3F, this.y - 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.nw_n = false;
            if (this.nw_e) {
                this.trace_NW_E(cCObjectOutlines, null);
            } else if (this.n_n) {
                this.trace_N_N(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
            }
        }

        void trace_NW_S(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x - 0.3F, this.y + 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x - 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.nw_s = false;
            if (this.nw_w) {
                this.trace_NW_W(cCObjectOutlines, null);
            } else {
                CollideWithObstacles.CCObjectOutline cCObjectOutline1 = get(this.x - 1, this.y, this.z, cCObjectOutlines);
                if (cCObjectOutline1 == null) {
                    return;
                }

                if (cCObjectOutline1.n_s) {
                    cCObjectOutline1.nodes = this.nodes;
                    cCObjectOutline1.trace_N_S(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }

        void trace_NW_W(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x - 0.3F, this.y - 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x - 0.3F, this.y - 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.nw_w = false;
            if (this.nw_n) {
                this.trace_NW_N(cCObjectOutlines, null);
            } else {
                CollideWithObstacles.CCObjectOutline cCObjectOutline1 = get(this.x, this.y - 1, this.z, cCObjectOutlines);
                if (cCObjectOutline1 == null) {
                    return;
                }

                if (cCObjectOutline1.w_w) {
                    cCObjectOutline1.nodes = this.nodes;
                    cCObjectOutline1.trace_W_W(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }

        void trace_NW_E(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x + 0.3F, this.y + 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x + 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.nw_e = false;
            if (this.nw_s) {
                this.trace_NW_S(cCObjectOutlines, null);
            } else if (this.w_e) {
                this.trace_W_E(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
            }
        }

        void trace_W_E(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x + 0.3F, this.y + 1 - 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x + 0.3F, this.y + 1 - 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.w_e = false;
            if (this.w_cutoff) {
                CollideWithObstacles.CCNode cCNode2 = this.nodes.get(this.nodes.size() - 1);
                cCNode2.setXY(this.x + 0.3F, this.y + 1 + 0.3F);
                cCNode2 = CollideWithObstacles.CCNode.alloc().init(this.x - 0.3F, this.y + 1 + 0.3F, this.z);
                this.nodes.add(cCNode2);
                cCNode2 = CollideWithObstacles.CCNode.alloc().init(this.x - 0.3F, this.y + 1 - 0.3F, this.z);
                this.nodes.add(cCNode2);
                this.trace_W_W(cCObjectOutlines, cCNode2);
            } else {
                CollideWithObstacles.CCObjectOutline cCObjectOutline1 = get(this.x, this.y + 1, this.z, cCObjectOutlines);
                if (cCObjectOutline1 != null) {
                    if (cCObjectOutline1.nw && cCObjectOutline1.nw_e) {
                        cCObjectOutline1.nodes = this.nodes;
                        cCObjectOutline1.trace_NW_E(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
                    } else if (cCObjectOutline1.n_n) {
                        cCObjectOutline1.nodes = this.nodes;
                        cCObjectOutline1.trace_N_N(cCObjectOutlines, null);
                    }
                }
            }
        }

        void trace_W_W(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x - 0.3F, this.y + 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x - 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.w_w = false;
            if (this.nw_w) {
                this.trace_NW_W(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
            } else {
                CollideWithObstacles.CCObjectOutline cCObjectOutline1 = get(this.x - 1, this.y, this.z, cCObjectOutlines);
                if (cCObjectOutline1 == null) {
                    return;
                }

                if (cCObjectOutline1.n_s) {
                    cCObjectOutline1.nodes = this.nodes;
                    cCObjectOutline1.trace_N_S(cCObjectOutlines, null);
                }
            }
        }

        void trace_N_N(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x + 1 - 0.3F, this.y - 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x + 1 - 0.3F, this.y - 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.n_n = false;
            if (this.n_cutoff) {
                CollideWithObstacles.CCNode cCNode2 = this.nodes.get(this.nodes.size() - 1);
                cCNode2.setXY(this.x + 1 + 0.3F, this.y - 0.3F);
                cCNode2 = CollideWithObstacles.CCNode.alloc().init(this.x + 1 + 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(cCNode2);
                cCNode2 = CollideWithObstacles.CCNode.alloc().init(this.x + 1 - 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(cCNode2);
                this.trace_N_S(cCObjectOutlines, cCNode2);
            } else {
                CollideWithObstacles.CCObjectOutline cCObjectOutline1 = get(this.x + 1, this.y, this.z, cCObjectOutlines);
                if (cCObjectOutline1 != null) {
                    if (cCObjectOutline1.nw_n) {
                        cCObjectOutline1.nodes = this.nodes;
                        cCObjectOutline1.trace_NW_N(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
                    } else {
                        cCObjectOutline1 = get(this.x + 1, this.y - 1, this.z, cCObjectOutlines);
                        if (cCObjectOutline1 == null) {
                            return;
                        }

                        if (cCObjectOutline1.w_w) {
                            cCObjectOutline1.nodes = this.nodes;
                            cCObjectOutline1.trace_W_W(cCObjectOutlines, null);
                        }
                    }
                }
            }
        }

        void trace_N_S(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, CollideWithObstacles.CCNode cCNode0) {
            if (cCNode0 != null) {
                cCNode0.setXY(this.x + 0.3F, this.y + 0.3F);
            } else {
                CollideWithObstacles.CCNode cCNode1 = CollideWithObstacles.CCNode.alloc().init(this.x + 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(cCNode1);
            }

            this.n_s = false;
            if (this.nw_s) {
                this.trace_NW_S(cCObjectOutlines, this.nodes.get(this.nodes.size() - 1));
            } else if (this.w_e) {
                this.trace_W_E(cCObjectOutlines, null);
            }
        }

        void trace(CollideWithObstacles.CCObjectOutline[][] cCObjectOutlines, ArrayList<CollideWithObstacles.CCNode> arrayList) {
            arrayList.clear();
            this.nodes = arrayList;
            CollideWithObstacles.CCNode cCNode = CollideWithObstacles.CCNode.alloc().init(this.x - 0.3F, this.y - 0.3F, this.z);
            arrayList.add(cCNode);
            this.trace_NW_N(cCObjectOutlines, null);
            if (arrayList.size() != 2
                && cCNode.x == ((CollideWithObstacles.CCNode)arrayList.get(arrayList.size() - 1)).x
                && cCNode.y == ((CollideWithObstacles.CCNode)arrayList.get(arrayList.size() - 1)).y) {
                ((CollideWithObstacles.CCNode)arrayList.get(arrayList.size() - 1)).release();
                arrayList.set(arrayList.size() - 1, cCNode);
            } else {
                arrayList.clear();
            }
        }

        static CollideWithObstacles.CCObjectOutline alloc() {
            return pool.isEmpty() ? new CollideWithObstacles.CCObjectOutline() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class CCObstacle {
        final ArrayList<CollideWithObstacles.CCEdge> edges = new ArrayList<>();
        CollideWithObstacles.ImmutableRectF bounds;
        static ArrayDeque<CollideWithObstacles.CCObstacle> pool = new ArrayDeque<>();

        CollideWithObstacles.CCObstacle init() {
            this.edges.clear();
            return this;
        }

        boolean hasNode(CollideWithObstacles.CCNode cCNode) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int0);
                if (cCEdge.hasNode(cCNode)) {
                    return true;
                }
            }

            return false;
        }

        boolean hasAdjacentNodes(CollideWithObstacles.CCNode cCNode1, CollideWithObstacles.CCNode cCNode0) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int0);
                if (cCEdge.hasNode(cCNode1) && cCEdge.hasNode(cCNode0)) {
                    return true;
                }
            }

            return false;
        }

        boolean isPointInPolygon_CrossingNumber(float float2, float float0) {
            int int0 = 0;

            for (int int1 = 0; int1 < this.edges.size(); int1++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int1);
                if (cCEdge.node1.y <= float0 && cCEdge.node2.y > float0 || cCEdge.node1.y > float0 && cCEdge.node2.y <= float0) {
                    float float1 = (float0 - cCEdge.node1.y) / (cCEdge.node2.y - cCEdge.node1.y);
                    if (float2 < cCEdge.node1.x + float1 * (cCEdge.node2.x - cCEdge.node1.x)) {
                        int0++;
                    }
                }
            }

            return int0 % 2 == 1;
        }

        float isLeft(float float3, float float1, float float5, float float0, float float2, float float4) {
            return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
        }

        CollideWithObstacles.EdgeRingHit isPointInPolygon_WindingNumber(float float0, float float1, int int2) {
            int int0 = 0;

            for (int int1 = 0; int1 < this.edges.size(); int1++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int1);
                if ((int2 & 16) != 0 && cCEdge.isPointOn(float0, float1)) {
                    return CollideWithObstacles.EdgeRingHit.OnEdge;
                }

                if (cCEdge.node1.y <= float1) {
                    if (cCEdge.node2.y > float1 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float0, float1) > 0.0F) {
                        int0++;
                    }
                } else if (cCEdge.node2.y <= float1 && this.isLeft(cCEdge.node1.x, cCEdge.node1.y, cCEdge.node2.x, cCEdge.node2.y, float0, float1) < 0.0F) {
                    int0--;
                }
            }

            return int0 == 0 ? CollideWithObstacles.EdgeRingHit.Outside : CollideWithObstacles.EdgeRingHit.Inside;
        }

        boolean isPointInside(float float0, float float1, int int0) {
            return this.isPointInPolygon_WindingNumber(float0, float1, int0) == CollideWithObstacles.EdgeRingHit.Inside;
        }

        boolean isNodeInsideOf(CollideWithObstacles.CCNode cCNode) {
            if (this.hasNode(cCNode)) {
                return false;
            } else if (!this.bounds.containsPoint(cCNode.x, cCNode.y)) {
                return false;
            } else {
                byte byte0 = 0;
                return this.isPointInside(cCNode.x, cCNode.y, byte0);
            }
        }

        CollideWithObstacles.CCNode getClosestPointOnEdge(float float2, float float3, Vector2 vector) {
            double double0 = Double.MAX_VALUE;
            CollideWithObstacles.CCNode cCNode0 = null;
            float float0 = Float.MAX_VALUE;
            float float1 = Float.MAX_VALUE;

            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int0);
                if (cCEdge.node1.visible.contains(cCEdge.node2)) {
                    CollideWithObstacles.CCNode cCNode1 = cCEdge.closestPoint(float2, float3, vector);
                    double double1 = (float2 - vector.x) * (float2 - vector.x) + (float3 - vector.y) * (float3 - vector.y);
                    if (double1 < double0) {
                        float0 = vector.x;
                        float1 = vector.y;
                        cCNode0 = cCNode1;
                        double0 = double1;
                    }
                }
            }

            vector.set(float0, float1);
            return cCNode0;
        }

        void calcBounds() {
            float float0 = Float.MAX_VALUE;
            float float1 = Float.MAX_VALUE;
            float float2 = Float.MIN_VALUE;
            float float3 = Float.MIN_VALUE;

            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                CollideWithObstacles.CCEdge cCEdge = this.edges.get(int0);
                float0 = Math.min(float0, cCEdge.node1.x);
                float1 = Math.min(float1, cCEdge.node1.y);
                float2 = Math.max(float2, cCEdge.node1.x);
                float3 = Math.max(float3, cCEdge.node1.y);
            }

            if (this.bounds != null) {
                this.bounds.release();
            }

            float float4 = 0.01F;
            this.bounds = CollideWithObstacles.ImmutableRectF.alloc()
                .init(float0 - float4, float1 - float4, float2 - float0 + float4 * 2.0F, float3 - float1 + float4 * 2.0F);
        }

        static CollideWithObstacles.CCObstacle alloc() {
            return pool.isEmpty() ? new CollideWithObstacles.CCObstacle() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    static final class CompareIntersection implements Comparator<CollideWithObstacles.CCIntersection> {
        CollideWithObstacles.CCEdge edge;

        public int compare(CollideWithObstacles.CCIntersection cCIntersection0, CollideWithObstacles.CCIntersection cCIntersection1) {
            float float0 = this.edge == cCIntersection0.edge1 ? cCIntersection0.dist1 : cCIntersection0.dist2;
            float float1 = this.edge == cCIntersection1.edge1 ? cCIntersection1.dist1 : cCIntersection1.dist2;
            if (float0 < float1) {
                return -1;
            } else {
                return float0 > float1 ? 1 : 0;
            }
        }
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
        static ArrayDeque<CollideWithObstacles.ImmutableRectF> pool = new ArrayDeque<>();

        CollideWithObstacles.ImmutableRectF init(float float0, float float1, float float2, float float3) {
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

        boolean intersects(CollideWithObstacles.ImmutableRectF immutableRectF0) {
            return this.left() < immutableRectF0.right()
                && this.right() > immutableRectF0.left()
                && this.top() < immutableRectF0.bottom()
                && this.bottom() > immutableRectF0.top();
        }

        static CollideWithObstacles.ImmutableRectF alloc() {
            return pool.isEmpty() ? new CollideWithObstacles.ImmutableRectF() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }
}
