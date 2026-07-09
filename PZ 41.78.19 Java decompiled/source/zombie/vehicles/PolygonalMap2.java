// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.vehicles;

import astar.ASearchNode;
import astar.AStar;
import astar.IGoalNode;
import astar.ISearchNode;
import gnu.trove.list.array.TFloatArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import java.awt.geom.Line2D;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import zombie.Lua.LuaManager;
import zombie.ai.KnownBlockedEdges;
import zombie.ai.astar.Mover;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.math.PZMath;
import zombie.core.physics.Transform;
import zombie.core.utils.BooleanGrid;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.iso.IsoChunk;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMetaGrid;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.MPStatistic;
import zombie.network.ServerMap;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.VehicleScript;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;

public final class PolygonalMap2 {
    public static final float RADIUS = 0.3F;
    private static final float RADIUS_DIAGONAL = (float)Math.sqrt(0.18F);
    public static final boolean CLOSE_TO_WALLS = true;
    public static final boolean PATHS_UNDER_VEHICLES = true;
    public static final boolean COLLIDE_CLIPPER = false;
    public static final boolean COLLIDE_BEVEL = false;
    public static final int CXN_FLAG_CAN_PATH = 1;
    public static final int CXN_FLAG_THUMP = 2;
    public static final int NODE_FLAG_CRAWL = 1;
    public static final int NODE_FLAG_CRAWL_INTERIOR = 2;
    public static final int NODE_FLAG_IN_CHUNK_DATA = 4;
    public static final int NODE_FLAG_PERIMETER = 8;
    public static final int NODE_FLAG_KEEP = 65536;
    private static final Vector2 temp = new Vector2();
    private static final Vector3f tempVec3f_1 = new Vector3f();
    private final ArrayList<PolygonalMap2.VehicleCluster> clusters = new ArrayList<>();
    private PolygonalMap2.ClosestPointOnEdge closestPointOnEdge = new PolygonalMap2.ClosestPointOnEdge();
    private final TIntObjectHashMap<PolygonalMap2.Node> squareToNode = new TIntObjectHashMap<>();
    private final ArrayList<PolygonalMap2.Square> tempSquares = new ArrayList<>();
    public static final PolygonalMap2 instance = new PolygonalMap2();
    private final ArrayList<PolygonalMap2.VisibilityGraph> graphs = new ArrayList<>();
    private Clipper clipperThread;
    private final ByteBuffer xyBufferThread = ByteBuffer.allocateDirect(8192);
    private final PolygonalMap2.AdjustStartEndNodeData adjustStartData = new PolygonalMap2.AdjustStartEndNodeData();
    private final PolygonalMap2.AdjustStartEndNodeData adjustGoalData = new PolygonalMap2.AdjustStartEndNodeData();
    private final PolygonalMap2.LineClearCollide lcc = new PolygonalMap2.LineClearCollide();
    private final PolygonalMap2.VGAStar astar = new PolygonalMap2.VGAStar();
    private final PolygonalMap2.TestRequest testRequest = new PolygonalMap2.TestRequest();
    private int testZ = 0;
    private final PathFindBehavior2.PointOnPath pointOnPath = new PathFindBehavior2.PointOnPath();
    private static final int SQUARES_PER_CHUNK = 10;
    private static final int LEVELS_PER_CHUNK = 8;
    private static final int SQUARES_PER_CELL = 300;
    private static final int CHUNKS_PER_CELL = 30;
    private static final int BIT_SOLID = 1;
    private static final int BIT_COLLIDE_W = 2;
    private static final int BIT_COLLIDE_N = 4;
    private static final int BIT_STAIR_TW = 8;
    private static final int BIT_STAIR_MW = 16;
    private static final int BIT_STAIR_BW = 32;
    private static final int BIT_STAIR_TN = 64;
    private static final int BIT_STAIR_MN = 128;
    private static final int BIT_STAIR_BN = 256;
    private static final int BIT_SOLID_FLOOR = 512;
    private static final int BIT_SOLID_TRANS = 1024;
    private static final int BIT_WINDOW_W = 2048;
    private static final int BIT_WINDOW_N = 4096;
    private static final int BIT_CAN_PATH_W = 8192;
    private static final int BIT_CAN_PATH_N = 16384;
    private static final int BIT_THUMP_W = 32768;
    private static final int BIT_THUMP_N = 65536;
    private static final int BIT_THUMPABLE = 131072;
    private static final int BIT_DOOR_E = 262144;
    private static final int BIT_DOOR_S = 524288;
    private static final int BIT_WINDOW_W_UNBLOCKED = 1048576;
    private static final int BIT_WINDOW_N_UNBLOCKED = 2097152;
    private static final int ALL_SOLID_BITS = 1025;
    private static final int ALL_STAIR_BITS = 504;
    private final ConcurrentLinkedQueue<PolygonalMap2.IChunkTask> chunkTaskQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<PolygonalMap2.SquareUpdateTask> squareTaskQueue = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<PolygonalMap2.IVehicleTask> vehicleTaskQueue = new ConcurrentLinkedQueue<>();
    private final ArrayList<PolygonalMap2.Vehicle> vehicles = new ArrayList<>();
    private final HashMap<BaseVehicle, PolygonalMap2.Vehicle> vehicleMap = new HashMap<>();
    private int minX;
    private int minY;
    private int width;
    private int height;
    private PolygonalMap2.Cell[][] cells;
    private final HashMap<BaseVehicle, PolygonalMap2.VehicleState> vehicleState = new HashMap<>();
    private final TObjectProcedure<PolygonalMap2.Node> releaseNodeProc = new TObjectProcedure<PolygonalMap2.Node>() {
        public boolean execute(PolygonalMap2.Node node) {
            node.release();
            return true;
        }
    };
    private boolean rebuild;
    private final PolygonalMap2.Path shortestPath = new PolygonalMap2.Path();
    private final PolygonalMap2.Sync sync = new PolygonalMap2.Sync();
    private final Object renderLock = new Object();
    private PolygonalMap2.PMThread thread;
    private final PolygonalMap2.RequestQueue requests = new PolygonalMap2.RequestQueue();
    private final ConcurrentLinkedQueue<PolygonalMap2.PathFindRequest> requestToMain = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<PolygonalMap2.PathRequestTask> requestTaskQueue = new ConcurrentLinkedQueue<>();
    private final HashMap<Mover, PolygonalMap2.PathFindRequest> requestMap = new HashMap<>();
    public static final int LCC_ZERO = 0;
    public static final int LCC_IGNORE_DOORS = 1;
    public static final int LCC_CLOSE_TO_WALLS = 2;
    public static final int LCC_CHECK_COST = 4;
    public static final int LCC_RENDER = 8;
    public static final int LCC_ALLOW_ON_EDGE = 16;
    private final PolygonalMap2.LineClearCollideMain lccMain = new PolygonalMap2.LineClearCollideMain();
    private final float[] tempFloats = new float[8];
    private final CollideWithObstacles collideWithObstacles = new CollideWithObstacles();
    private final CollideWithObstaclesPoly collideWithObstaclesPoly = new CollideWithObstaclesPoly();

    private void createVehicleCluster(
        PolygonalMap2.VehicleRect vehicleRect1, ArrayList<PolygonalMap2.VehicleRect> arrayList0, ArrayList<PolygonalMap2.VehicleCluster> arrayList1
    ) {
        for (int int0 = 0; int0 < arrayList0.size(); int0++) {
            PolygonalMap2.VehicleRect vehicleRect0 = (PolygonalMap2.VehicleRect)arrayList0.get(int0);
            if (vehicleRect1 != vehicleRect0
                && vehicleRect1.z == vehicleRect0.z
                && (vehicleRect1.cluster == null || vehicleRect1.cluster != vehicleRect0.cluster)
                && vehicleRect1.isAdjacent(vehicleRect0)) {
                if (vehicleRect1.cluster != null) {
                    if (vehicleRect0.cluster == null) {
                        vehicleRect0.cluster = vehicleRect1.cluster;
                        vehicleRect0.cluster.rects.add(vehicleRect0);
                    } else {
                        arrayList1.remove(vehicleRect0.cluster);
                        vehicleRect1.cluster.merge(vehicleRect0.cluster);
                    }
                } else if (vehicleRect0.cluster != null) {
                    if (vehicleRect1.cluster == null) {
                        vehicleRect1.cluster = vehicleRect0.cluster;
                        vehicleRect1.cluster.rects.add(vehicleRect1);
                    } else {
                        arrayList1.remove(vehicleRect1.cluster);
                        vehicleRect0.cluster.merge(vehicleRect1.cluster);
                    }
                } else {
                    PolygonalMap2.VehicleCluster vehicleCluster0 = PolygonalMap2.VehicleCluster.alloc().init();
                    vehicleRect1.cluster = vehicleCluster0;
                    vehicleRect0.cluster = vehicleCluster0;
                    vehicleCluster0.rects.add(vehicleRect1);
                    vehicleCluster0.rects.add(vehicleRect0);
                    arrayList1.add(vehicleCluster0);
                }
            }
        }

        if (vehicleRect1.cluster == null) {
            PolygonalMap2.VehicleCluster vehicleCluster1 = PolygonalMap2.VehicleCluster.alloc().init();
            vehicleRect1.cluster = vehicleCluster1;
            vehicleCluster1.rects.add(vehicleRect1);
            arrayList1.add(vehicleCluster1);
        }
    }

    private void createVehicleClusters() {
        this.clusters.clear();
        ArrayList arrayList = new ArrayList();

        for (int int0 = 0; int0 < this.vehicles.size(); int0++) {
            PolygonalMap2.Vehicle vehicle = this.vehicles.get(int0);
            PolygonalMap2.VehicleRect vehicleRect0 = PolygonalMap2.VehicleRect.alloc();
            vehicle.polyPlusRadius.getAABB(vehicleRect0);
            vehicleRect0.vehicle = vehicle;
            arrayList.add(vehicleRect0);
        }

        if (!arrayList.isEmpty()) {
            for (int int1 = 0; int1 < arrayList.size(); int1++) {
                PolygonalMap2.VehicleRect vehicleRect1 = (PolygonalMap2.VehicleRect)arrayList.get(int1);
                this.createVehicleCluster(vehicleRect1, arrayList, this.clusters);
            }
        }
    }

    private PolygonalMap2.Node getNodeForSquare(PolygonalMap2.Square square) {
        PolygonalMap2.Node node = this.squareToNode.get(square.ID);
        if (node == null) {
            node = PolygonalMap2.Node.alloc().init(square);
            this.squareToNode.put(square.ID, node);
        }

        return node;
    }

    private PolygonalMap2.VisibilityGraph getVisGraphAt(float float0, float float1, int int1) {
        for (int int0 = 0; int0 < this.graphs.size(); int0++) {
            PolygonalMap2.VisibilityGraph visibilityGraph = this.graphs.get(int0);
            if (visibilityGraph.contains(float0, float1, int1)) {
                return visibilityGraph;
            }
        }

        return null;
    }

    private PolygonalMap2.VisibilityGraph getVisGraphAt(float float0, float float1, int int1, int int2) {
        for (int int0 = 0; int0 < this.graphs.size(); int0++) {
            PolygonalMap2.VisibilityGraph visibilityGraph = this.graphs.get(int0);
            if (visibilityGraph.contains(float0, float1, int1, int2)) {
                return visibilityGraph;
            }
        }

        return null;
    }

    private PolygonalMap2.VisibilityGraph getVisGraphForSquare(PolygonalMap2.Square square) {
        for (int int0 = 0; int0 < this.graphs.size(); int0++) {
            PolygonalMap2.VisibilityGraph visibilityGraph = this.graphs.get(int0);
            if (visibilityGraph.contains(square)) {
                return visibilityGraph;
            }
        }

        return null;
    }

    private PolygonalMap2.Connection connectTwoNodes(PolygonalMap2.Node node0, PolygonalMap2.Node node1, int int0) {
        PolygonalMap2.Connection connection = PolygonalMap2.Connection.alloc().init(node0, node1, int0);
        node0.visible.add(connection);
        node1.visible.add(connection);
        return connection;
    }

    private PolygonalMap2.Connection connectTwoNodes(PolygonalMap2.Node node0, PolygonalMap2.Node node1) {
        return this.connectTwoNodes(node0, node1, 0);
    }

    private void breakConnection(PolygonalMap2.Connection connection) {
        connection.node1.visible.remove(connection);
        connection.node2.visible.remove(connection);
        connection.release();
    }

    private void breakConnection(PolygonalMap2.Node node0, PolygonalMap2.Node node1) {
        for (int int0 = 0; int0 < node0.visible.size(); int0++) {
            PolygonalMap2.Connection connection = node0.visible.get(int0);
            if (connection.otherNode(node0) == node1) {
                this.breakConnection(connection);
                break;
            }
        }
    }

    private void addStairNodes() {
        ArrayList arrayList = this.tempSquares;
        arrayList.clear();

        for (int int0 = 0; int0 < this.graphs.size(); int0++) {
            PolygonalMap2.VisibilityGraph visibilityGraph0 = this.graphs.get(int0);
            visibilityGraph0.getStairSquares(arrayList);
        }

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            PolygonalMap2.Square square0 = (PolygonalMap2.Square)arrayList.get(int1);
            PolygonalMap2.Square square1 = null;
            PolygonalMap2.Square square2 = null;
            PolygonalMap2.Square square3 = null;
            PolygonalMap2.Square square4 = null;
            PolygonalMap2.Square square5 = null;
            if (square0.has(8)) {
                square1 = this.getSquare(square0.x - 1, square0.y, square0.z + 1);
                square2 = square0;
                square3 = this.getSquare(square0.x + 1, square0.y, square0.z);
                square4 = this.getSquare(square0.x + 2, square0.y, square0.z);
                square5 = this.getSquare(square0.x + 3, square0.y, square0.z);
            }

            if (square0.has(64)) {
                square1 = this.getSquare(square0.x, square0.y - 1, square0.z + 1);
                square2 = square0;
                square3 = this.getSquare(square0.x, square0.y + 1, square0.z);
                square4 = this.getSquare(square0.x, square0.y + 2, square0.z);
                square5 = this.getSquare(square0.x, square0.y + 3, square0.z);
            }

            if (square1 != null && square2 != null && square3 != null && square4 != null && square5 != null) {
                PolygonalMap2.Node node0 = null;
                PolygonalMap2.Node node1 = null;
                PolygonalMap2.VisibilityGraph visibilityGraph1 = this.getVisGraphForSquare(square1);
                if (visibilityGraph1 == null) {
                    node0 = this.getNodeForSquare(square1);
                } else {
                    node0 = PolygonalMap2.Node.alloc().init(square1);

                    for (PolygonalMap2.Obstacle obstacle0 : visibilityGraph1.obstacles) {
                        if (obstacle0.isNodeInsideOf(node0)) {
                            node0.ignore = true;
                        }
                    }

                    node0.addGraph(visibilityGraph1);
                    visibilityGraph1.addNode(node0);
                    this.squareToNode.put(square1.ID, node0);
                }

                visibilityGraph1 = this.getVisGraphForSquare(square5);
                if (visibilityGraph1 == null) {
                    node1 = this.getNodeForSquare(square5);
                } else {
                    node1 = PolygonalMap2.Node.alloc().init(square5);

                    for (PolygonalMap2.Obstacle obstacle1 : visibilityGraph1.obstacles) {
                        if (obstacle1.isNodeInsideOf(node1)) {
                            node1.ignore = true;
                        }
                    }

                    node1.addGraph(visibilityGraph1);
                    visibilityGraph1.addNode(node1);
                    this.squareToNode.put(square5.ID, node1);
                }

                if (node0 != null && node1 != null) {
                    PolygonalMap2.Node node2 = this.getNodeForSquare(square2);
                    PolygonalMap2.Node node3 = this.getNodeForSquare(square3);
                    PolygonalMap2.Node node4 = this.getNodeForSquare(square4);
                    this.connectTwoNodes(node0, node2);
                    this.connectTwoNodes(node2, node3);
                    this.connectTwoNodes(node3, node4);
                    this.connectTwoNodes(node4, node1);
                }
            }
        }
    }

    private void addCanPathNodes() {
        ArrayList arrayList = this.tempSquares;
        arrayList.clear();

        for (int int0 = 0; int0 < this.graphs.size(); int0++) {
            PolygonalMap2.VisibilityGraph visibilityGraph = this.graphs.get(int0);
            visibilityGraph.getCanPathSquares(arrayList);
        }

        for (int int1 = 0; int1 < arrayList.size(); int1++) {
            PolygonalMap2.Square square0 = (PolygonalMap2.Square)arrayList.get(int1);
            if (!square0.isNonThumpableSolid() && !square0.has(504) && square0.has(512)) {
                if (square0.isCanPathW()) {
                    int int2 = square0.x - 1;
                    int int3 = square0.y;
                    PolygonalMap2.Square square1 = this.getSquare(int2, int3, square0.z);
                    if (square1 != null && !square1.isNonThumpableSolid() && !square1.has(504) && square1.has(512)) {
                        PolygonalMap2.Node node0 = this.getOrCreateCanPathNode(square0);
                        PolygonalMap2.Node node1 = this.getOrCreateCanPathNode(square1);
                        byte byte0 = 1;
                        if (square0.has(163840) || square1.has(131072)) {
                            byte0 |= 2;
                        }

                        this.connectTwoNodes(node0, node1, byte0);
                    }
                }

                if (square0.isCanPathN()) {
                    int int4 = square0.x;
                    int int5 = square0.y - 1;
                    PolygonalMap2.Square square2 = this.getSquare(int4, int5, square0.z);
                    if (square2 != null && !square2.isNonThumpableSolid() && !square2.has(504) && square2.has(512)) {
                        PolygonalMap2.Node node2 = this.getOrCreateCanPathNode(square0);
                        PolygonalMap2.Node node3 = this.getOrCreateCanPathNode(square2);
                        byte byte1 = 1;
                        if (square0.has(196608) || square2.has(131072)) {
                            byte1 |= 2;
                        }

                        this.connectTwoNodes(node2, node3, byte1);
                    }
                }
            }
        }
    }

    private PolygonalMap2.Node getOrCreateCanPathNode(PolygonalMap2.Square square) {
        PolygonalMap2.VisibilityGraph visibilityGraph = this.getVisGraphForSquare(square);
        PolygonalMap2.Node node = this.getNodeForSquare(square);
        if (visibilityGraph != null && !visibilityGraph.nodes.contains(node)) {
            for (PolygonalMap2.Obstacle obstacle : visibilityGraph.obstacles) {
                if (obstacle.isNodeInsideOf(node)) {
                    node.ignore = true;
                    break;
                }
            }

            visibilityGraph.addNode(node);
        }

        return node;
    }

    private PolygonalMap2.Node getPointOutsideObjects(PolygonalMap2.Square square1, float float5, float float7) {
        PolygonalMap2.Square square0 = instance.getSquare(square1.x - 1, square1.y, square1.z);
        PolygonalMap2.Square square2 = instance.getSquare(square1.x - 1, square1.y - 1, square1.z);
        PolygonalMap2.Square square3 = instance.getSquare(square1.x, square1.y - 1, square1.z);
        PolygonalMap2.Square square4 = instance.getSquare(square1.x + 1, square1.y - 1, square1.z);
        PolygonalMap2.Square square5 = instance.getSquare(square1.x + 1, square1.y, square1.z);
        PolygonalMap2.Square square6 = instance.getSquare(square1.x + 1, square1.y + 1, square1.z);
        PolygonalMap2.Square square7 = instance.getSquare(square1.x, square1.y + 1, square1.z);
        PolygonalMap2.Square square8 = instance.getSquare(square1.x - 1, square1.y + 1, square1.z);
        float float0 = square1.x;
        float float1 = square1.y;
        float float2 = square1.x + 1;
        float float3 = square1.y + 1;
        if (square1.isCollideW()) {
            float0 += 0.35000002F;
        }

        if (square1.isCollideN()) {
            float1 += 0.35000002F;
        }

        if (square5 != null && (square5.has(2) || square5.has(504) || square5.isReallySolid())) {
            float2 -= 0.35000002F;
        }

        if (square7 != null && (square7.has(4) || square7.has(504) || square7.isReallySolid())) {
            float3 -= 0.35000002F;
        }

        float float4 = PZMath.clamp(float5, float0, float2);
        float float6 = PZMath.clamp(float7, float1, float3);
        if (float4 <= square1.x + 0.3F && float6 <= square1.y + 0.3F) {
            boolean boolean0 = square2 != null && (square2.has(504) || square2.isReallySolid());
            boolean0 |= square3 != null && square3.has(2);
            boolean0 |= square0 != null && square0.has(4);
            if (boolean0) {
                float float8 = square1.x + 0.3F + 0.05F;
                float float9 = square1.y + 0.3F + 0.05F;
                if (float8 - float4 <= float9 - float6) {
                    float4 = float8;
                } else {
                    float6 = float9;
                }
            }
        }

        if (float4 >= square1.x + 1 - 0.3F && float6 <= square1.y + 0.3F) {
            boolean boolean1 = square4 != null && (square4.has(2) || square4.has(504) || square4.isReallySolid());
            boolean1 |= square5 != null && square5.has(4);
            if (boolean1) {
                float float10 = square1.x + 1 - 0.3F - 0.05F;
                float float11 = square1.y + 0.3F + 0.05F;
                if (float4 - float10 <= float11 - float6) {
                    float4 = float10;
                } else {
                    float6 = float11;
                }
            }
        }

        if (float4 <= square1.x + 0.3F && float6 >= square1.y + 1 - 0.3F) {
            boolean boolean2 = square8 != null && (square8.has(4) || square8.has(504) || square8.isReallySolid());
            boolean2 |= square7 != null && square7.has(2);
            if (boolean2) {
                float float12 = square1.x + 0.3F + 0.05F;
                float float13 = square1.y + 1 - 0.3F - 0.05F;
                if (float12 - float4 <= float6 - float13) {
                    float4 = float12;
                } else {
                    float6 = float13;
                }
            }
        }

        if (float4 >= square1.x + 1 - 0.3F && float6 >= square1.y + 1 - 0.3F) {
            boolean boolean3 = square6 != null && (square6.has(2) || square6.has(4) || square6.has(504) || square6.isReallySolid());
            if (boolean3) {
                float float14 = square1.x + 1 - 0.3F - 0.05F;
                float float15 = square1.y + 1 - 0.3F - 0.05F;
                if (float4 - float14 <= float6 - float15) {
                    float4 = float14;
                } else {
                    float6 = float15;
                }
            }
        }

        return PolygonalMap2.Node.alloc().init(float4, float6, square1.z);
    }

    private void createVisibilityGraph(PolygonalMap2.VehicleCluster vehicleCluster) {
        PolygonalMap2.VisibilityGraph visibilityGraph = PolygonalMap2.VisibilityGraph.alloc().init(vehicleCluster);
        visibilityGraph.addPerimeterEdges();
        this.graphs.add(visibilityGraph);
    }

    private void createVisibilityGraphs() {
        this.createVehicleClusters();
        this.graphs.clear();
        this.squareToNode.clear();

        for (int int0 = 0; int0 < this.clusters.size(); int0++) {
            PolygonalMap2.VehicleCluster vehicleCluster = this.clusters.get(int0);
            this.createVisibilityGraph(vehicleCluster);
        }

        this.addStairNodes();
        this.addCanPathNodes();
    }

    // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
    // Please report this to the Zomboid Decompiler issue tracker at https://github.com/demiurgeQuantified/ZomboidDecompiler/issues with the file name and game version.
    private boolean findPath(PolygonalMap2.PathFindRequest pathFindRequest, boolean boolean0) {
        byte byte0 = 16;
        if (!(pathFindRequest.mover instanceof IsoZombie)) {
            byte0 |= 4;
        }

        if ((int)pathFindRequest.startZ == (int)pathFindRequest.targetZ
            && !this.lcc
                .isNotClear(
                    this, pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.targetX, pathFindRequest.targetY, (int)pathFindRequest.startZ, byte0
                )) {
            pathFindRequest.path.addNode(pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.startZ);
            pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ);
            if (boolean0) {
                for (PolygonalMap2.VisibilityGraph visibilityGraph0 : this.graphs) {
                    visibilityGraph0.render();
                }
            }

            return true;
        } else {
            this.astar.init(this.graphs, this.squareToNode);
            this.astar.knownBlockedEdges.clear();

            for (int int0 = 0; int0 < pathFindRequest.knownBlockedEdges.size(); int0++) {
                KnownBlockedEdges knownBlockedEdges = pathFindRequest.knownBlockedEdges.get(int0);
                PolygonalMap2.Square square0 = this.getSquare(knownBlockedEdges.x, knownBlockedEdges.y, knownBlockedEdges.z);
                if (square0 != null) {
                    this.astar.knownBlockedEdges.put(square0.ID, knownBlockedEdges);
                }
            }

            PolygonalMap2.VisibilityGraph visibilityGraph1 = null;
            PolygonalMap2.VisibilityGraph visibilityGraph2 = null;
            PolygonalMap2.SearchNode searchNode0 = null;
            PolygonalMap2.SearchNode searchNode1 = null;
            boolean boolean1 = false;
            boolean boolean2 = false;
            boolean boolean3 = false /* VF: Semaphore variable */;

            boolean boolean4;
            label1216: {
                boolean boolean5;
                label1217: {
                    label1218: {
                        label1219: {
                            int int1;
                            label1220: {
                                boolean boolean6;
                                label1221: {
                                    label1222: {
                                        try {
                                            boolean3 = true;
                                            PolygonalMap2.Square square1 = this.getSquare(
                                                (int)pathFindRequest.startX, (int)pathFindRequest.startY, (int)pathFindRequest.startZ
                                            );
                                            if (square1 != null && !square1.isReallySolid()) {
                                                if (square1.has(504)) {
                                                    searchNode0 = this.astar.getSearchNode(square1);
                                                } else {
                                                    PolygonalMap2.VisibilityGraph visibilityGraph3 = this.astar.getVisGraphForSquare(square1);
                                                    if (visibilityGraph3 != null) {
                                                        if (!visibilityGraph3.created) {
                                                            visibilityGraph3.create();
                                                        }

                                                        PolygonalMap2.Node node0 = null;
                                                        int1 = visibilityGraph3.getPointOutsideObstacles(
                                                            pathFindRequest.startX, pathFindRequest.startY, pathFindRequest.startZ, this.adjustStartData
                                                        );
                                                        if (int1 == -1) {
                                                            boolean4 = false;
                                                            boolean3 = false;
                                                            break label1216;
                                                        }

                                                        if (int1 == 1) {
                                                            boolean1 = true;
                                                            node0 = this.adjustStartData.node;
                                                            if (this.adjustStartData.isNodeNew) {
                                                                visibilityGraph1 = visibilityGraph3;
                                                            }
                                                        }

                                                        if (node0 == null) {
                                                            node0 = PolygonalMap2.Node.alloc()
                                                                .init(pathFindRequest.startX, pathFindRequest.startY, (int)pathFindRequest.startZ);
                                                            visibilityGraph3.addNode(node0);
                                                            visibilityGraph1 = visibilityGraph3;
                                                        }

                                                        searchNode0 = this.astar.getSearchNode(node0);
                                                    }
                                                }

                                                if (searchNode0 == null) {
                                                    searchNode0 = this.astar.getSearchNode(square1);
                                                }

                                                if (!(pathFindRequest.targetX < 0.0F)
                                                    && !(pathFindRequest.targetY < 0.0F)
                                                    && this.getChunkFromSquarePos((int)pathFindRequest.targetX, (int)pathFindRequest.targetY) != null) {
                                                    square1 = this.getSquare(
                                                        (int)pathFindRequest.targetX, (int)pathFindRequest.targetY, (int)pathFindRequest.targetZ
                                                    );
                                                    if (square1 == null || square1.isReallySolid()) {
                                                        boolean5 = false;
                                                        boolean3 = false;
                                                        break label1219;
                                                    }

                                                    if ((
                                                            (int)pathFindRequest.startX != (int)pathFindRequest.targetX
                                                                || (int)pathFindRequest.startY != (int)pathFindRequest.targetY
                                                                || (int)pathFindRequest.startZ != (int)pathFindRequest.targetZ
                                                        )
                                                        && this.isBlockedInAllDirections(
                                                            (int)pathFindRequest.targetX, (int)pathFindRequest.targetY, (int)pathFindRequest.targetZ
                                                        )) {
                                                        boolean5 = false;
                                                        boolean3 = false;
                                                        break label1217;
                                                    }

                                                    if (square1.has(504)) {
                                                        searchNode1 = this.astar.getSearchNode(square1);
                                                    } else {
                                                        PolygonalMap2.VisibilityGraph visibilityGraph4 = this.astar.getVisGraphForSquare(square1);
                                                        if (visibilityGraph4 != null) {
                                                            if (!visibilityGraph4.created) {
                                                                visibilityGraph4.create();
                                                            }

                                                            PolygonalMap2.Node node1 = null;
                                                            int1 = visibilityGraph4.getPointOutsideObstacles(
                                                                pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, this.adjustGoalData
                                                            );
                                                            if (int1 == -1) {
                                                                boolean4 = false;
                                                                boolean3 = false;
                                                                break label1218;
                                                            }

                                                            if (int1 == 1) {
                                                                boolean2 = true;
                                                                node1 = this.adjustGoalData.node;
                                                                if (this.adjustGoalData.isNodeNew) {
                                                                    visibilityGraph2 = visibilityGraph4;
                                                                }
                                                            }

                                                            if (node1 == null) {
                                                                node1 = PolygonalMap2.Node.alloc()
                                                                    .init(pathFindRequest.targetX, pathFindRequest.targetY, (int)pathFindRequest.targetZ);
                                                                visibilityGraph4.addNode(node1);
                                                                visibilityGraph2 = visibilityGraph4;
                                                            }

                                                            searchNode1 = this.astar.getSearchNode(node1);
                                                        } else {
                                                            for (int int2 = 0; int2 < this.graphs.size(); int2++) {
                                                                PolygonalMap2.VisibilityGraph visibilityGraph5 = this.graphs.get(int2);
                                                                if (visibilityGraph5.contains(square1, 1)) {
                                                                    PolygonalMap2.Node node2 = this.getPointOutsideObjects(
                                                                        square1, pathFindRequest.targetX, pathFindRequest.targetY
                                                                    );
                                                                    visibilityGraph5.addNode(node2);
                                                                    if (node2.x != pathFindRequest.targetX || node2.y != pathFindRequest.targetY) {
                                                                        boolean2 = true;
                                                                        this.adjustGoalData.isNodeNew = false;
                                                                    }

                                                                    visibilityGraph2 = visibilityGraph5;
                                                                    searchNode1 = this.astar.getSearchNode(node2);
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }

                                                    if (searchNode1 == null) {
                                                        searchNode1 = this.astar.getSearchNode(square1);
                                                    }
                                                } else {
                                                    searchNode1 = this.astar.getSearchNode((int)pathFindRequest.targetX, (int)pathFindRequest.targetY);
                                                }

                                                ArrayList arrayList = this.astar.shortestPath(pathFindRequest, searchNode0, searchNode1);
                                                if (arrayList != null) {
                                                    if (arrayList.size() == 1) {
                                                        pathFindRequest.path.addNode(searchNode0);
                                                        if (!boolean2
                                                            && searchNode1.square != null
                                                            && searchNode1.square.x + 0.5F != pathFindRequest.targetX
                                                            && searchNode1.square.y + 0.5F != pathFindRequest.targetY) {
                                                            pathFindRequest.path
                                                                .addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, 0);
                                                        } else {
                                                            pathFindRequest.path.addNode(searchNode1);
                                                        }

                                                        int1 = 1;
                                                        boolean3 = false;
                                                        break label1220;
                                                    }

                                                    this.cleanPath(arrayList, pathFindRequest, boolean1, boolean2, searchNode1);
                                                    if (pathFindRequest.mover instanceof IsoPlayer && !((IsoPlayer)pathFindRequest.mover).isNPC()) {
                                                        this.smoothPath(pathFindRequest.path);
                                                    }

                                                    boolean6 = true;
                                                    boolean3 = false;
                                                    break label1221;
                                                }

                                                boolean3 = false;
                                                break label1222;
                                            }

                                            boolean5 = false;
                                            boolean3 = false;
                                        } finally {
                                            if (boolean3) {
                                                if (boolean0) {
                                                    for (PolygonalMap2.VisibilityGraph visibilityGraph6 : this.graphs) {
                                                        visibilityGraph6.render();
                                                    }
                                                }

                                                if (visibilityGraph1 != null) {
                                                    visibilityGraph1.removeNode(searchNode0.vgNode);
                                                }

                                                if (visibilityGraph2 != null) {
                                                    visibilityGraph2.removeNode(searchNode1.vgNode);
                                                }

                                                for (int int3 = 0; int3 < this.astar.searchNodes.size(); int3++) {
                                                    this.astar.searchNodes.get(int3).release();
                                                }

                                                if (boolean1 && this.adjustStartData.isNodeNew) {
                                                    for (int int4 = 0; int4 < this.adjustStartData.node.edges.size(); int4++) {
                                                        PolygonalMap2.Edge edge0 = this.adjustStartData.node.edges.get(int4);
                                                        edge0.obstacle.unsplit(this.adjustStartData.node, edge0.edgeRing);
                                                    }

                                                    this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                                                }

                                                if (boolean2 && this.adjustGoalData.isNodeNew) {
                                                    for (int int5 = 0; int5 < this.adjustGoalData.node.edges.size(); int5++) {
                                                        PolygonalMap2.Edge edge1 = this.adjustGoalData.node.edges.get(int5);
                                                        edge1.obstacle.unsplit(this.adjustGoalData.node, edge1.edgeRing);
                                                    }

                                                    this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                                                }
                                            }
                                        }

                                        if (boolean0) {
                                            for (PolygonalMap2.VisibilityGraph visibilityGraph7 : this.graphs) {
                                                visibilityGraph7.render();
                                            }
                                        }

                                        if (visibilityGraph1 != null) {
                                            visibilityGraph1.removeNode(searchNode0.vgNode);
                                        }

                                        if (visibilityGraph2 != null) {
                                            visibilityGraph2.removeNode(searchNode1.vgNode);
                                        }

                                        for (int int6 = 0; int6 < this.astar.searchNodes.size(); int6++) {
                                            this.astar.searchNodes.get(int6).release();
                                        }

                                        if (boolean1 && this.adjustStartData.isNodeNew) {
                                            for (int int7 = 0; int7 < this.adjustStartData.node.edges.size(); int7++) {
                                                PolygonalMap2.Edge edge2 = this.adjustStartData.node.edges.get(int7);
                                                edge2.obstacle.unsplit(this.adjustStartData.node, edge2.edgeRing);
                                            }

                                            this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                                        }

                                        if (boolean2 && this.adjustGoalData.isNodeNew) {
                                            for (int int8 = 0; int8 < this.adjustGoalData.node.edges.size(); int8++) {
                                                PolygonalMap2.Edge edge3 = this.adjustGoalData.node.edges.get(int8);
                                                edge3.obstacle.unsplit(this.adjustGoalData.node, edge3.edgeRing);
                                            }

                                            this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                                        }

                                        return boolean5;
                                    }

                                    if (boolean0) {
                                        for (PolygonalMap2.VisibilityGraph visibilityGraph8 : this.graphs) {
                                            visibilityGraph8.render();
                                        }
                                    }

                                    if (visibilityGraph1 != null) {
                                        visibilityGraph1.removeNode(searchNode0.vgNode);
                                    }

                                    if (visibilityGraph2 != null) {
                                        visibilityGraph2.removeNode(searchNode1.vgNode);
                                    }

                                    for (int int9 = 0; int9 < this.astar.searchNodes.size(); int9++) {
                                        this.astar.searchNodes.get(int9).release();
                                    }

                                    if (boolean1 && this.adjustStartData.isNodeNew) {
                                        for (int int10 = 0; int10 < this.adjustStartData.node.edges.size(); int10++) {
                                            PolygonalMap2.Edge edge4 = this.adjustStartData.node.edges.get(int10);
                                            edge4.obstacle.unsplit(this.adjustStartData.node, edge4.edgeRing);
                                        }

                                        this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                                    }

                                    if (boolean2 && this.adjustGoalData.isNodeNew) {
                                        for (int int11 = 0; int11 < this.adjustGoalData.node.edges.size(); int11++) {
                                            PolygonalMap2.Edge edge5 = this.adjustGoalData.node.edges.get(int11);
                                            edge5.obstacle.unsplit(this.adjustGoalData.node, edge5.edgeRing);
                                        }

                                        this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                                    }

                                    return false;
                                }

                                if (boolean0) {
                                    for (PolygonalMap2.VisibilityGraph visibilityGraph9 : this.graphs) {
                                        visibilityGraph9.render();
                                    }
                                }

                                if (visibilityGraph1 != null) {
                                    visibilityGraph1.removeNode(searchNode0.vgNode);
                                }

                                if (visibilityGraph2 != null) {
                                    visibilityGraph2.removeNode(searchNode1.vgNode);
                                }

                                for (int int12 = 0; int12 < this.astar.searchNodes.size(); int12++) {
                                    this.astar.searchNodes.get(int12).release();
                                }

                                if (boolean1 && this.adjustStartData.isNodeNew) {
                                    for (int int13 = 0; int13 < this.adjustStartData.node.edges.size(); int13++) {
                                        PolygonalMap2.Edge edge6 = this.adjustStartData.node.edges.get(int13);
                                        edge6.obstacle.unsplit(this.adjustStartData.node, edge6.edgeRing);
                                    }

                                    this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                                }

                                if (boolean2 && this.adjustGoalData.isNodeNew) {
                                    for (int int14 = 0; int14 < this.adjustGoalData.node.edges.size(); int14++) {
                                        PolygonalMap2.Edge edge7 = this.adjustGoalData.node.edges.get(int14);
                                        edge7.obstacle.unsplit(this.adjustGoalData.node, edge7.edgeRing);
                                    }

                                    this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                                }

                                return boolean6;
                            }

                            if (boolean0) {
                                for (PolygonalMap2.VisibilityGraph visibilityGraph10 : this.graphs) {
                                    visibilityGraph10.render();
                                }
                            }

                            if (visibilityGraph1 != null) {
                                visibilityGraph1.removeNode(searchNode0.vgNode);
                            }

                            if (visibilityGraph2 != null) {
                                visibilityGraph2.removeNode(searchNode1.vgNode);
                            }

                            for (int int15 = 0; int15 < this.astar.searchNodes.size(); int15++) {
                                this.astar.searchNodes.get(int15).release();
                            }

                            if (boolean1 && this.adjustStartData.isNodeNew) {
                                for (int int16 = 0; int16 < this.adjustStartData.node.edges.size(); int16++) {
                                    PolygonalMap2.Edge edge8 = this.adjustStartData.node.edges.get(int16);
                                    edge8.obstacle.unsplit(this.adjustStartData.node, edge8.edgeRing);
                                }

                                this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                            }

                            if (boolean2 && this.adjustGoalData.isNodeNew) {
                                for (int int17 = 0; int17 < this.adjustGoalData.node.edges.size(); int17++) {
                                    PolygonalMap2.Edge edge9 = this.adjustGoalData.node.edges.get(int17);
                                    edge9.obstacle.unsplit(this.adjustGoalData.node, edge9.edgeRing);
                                }

                                this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                            }

                            return (boolean)int1;
                        }

                        if (boolean0) {
                            for (PolygonalMap2.VisibilityGraph visibilityGraph11 : this.graphs) {
                                visibilityGraph11.render();
                            }
                        }

                        if (visibilityGraph1 != null) {
                            visibilityGraph1.removeNode(searchNode0.vgNode);
                        }

                        if (visibilityGraph2 != null) {
                            visibilityGraph2.removeNode(searchNode1.vgNode);
                        }

                        for (int int18 = 0; int18 < this.astar.searchNodes.size(); int18++) {
                            this.astar.searchNodes.get(int18).release();
                        }

                        if (boolean1 && this.adjustStartData.isNodeNew) {
                            for (int int19 = 0; int19 < this.adjustStartData.node.edges.size(); int19++) {
                                PolygonalMap2.Edge edge10 = this.adjustStartData.node.edges.get(int19);
                                edge10.obstacle.unsplit(this.adjustStartData.node, edge10.edgeRing);
                            }

                            this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                        }

                        if (boolean2 && this.adjustGoalData.isNodeNew) {
                            for (int int20 = 0; int20 < this.adjustGoalData.node.edges.size(); int20++) {
                                PolygonalMap2.Edge edge11 = this.adjustGoalData.node.edges.get(int20);
                                edge11.obstacle.unsplit(this.adjustGoalData.node, edge11.edgeRing);
                            }

                            this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                        }

                        return boolean5;
                    }

                    if (boolean0) {
                        for (PolygonalMap2.VisibilityGraph visibilityGraph12 : this.graphs) {
                            visibilityGraph12.render();
                        }
                    }

                    if (visibilityGraph1 != null) {
                        visibilityGraph1.removeNode(searchNode0.vgNode);
                    }

                    if (visibilityGraph2 != null) {
                        visibilityGraph2.removeNode(searchNode1.vgNode);
                    }

                    for (int int21 = 0; int21 < this.astar.searchNodes.size(); int21++) {
                        this.astar.searchNodes.get(int21).release();
                    }

                    if (boolean1 && this.adjustStartData.isNodeNew) {
                        for (int int22 = 0; int22 < this.adjustStartData.node.edges.size(); int22++) {
                            PolygonalMap2.Edge edge12 = this.adjustStartData.node.edges.get(int22);
                            edge12.obstacle.unsplit(this.adjustStartData.node, edge12.edgeRing);
                        }

                        this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                    }

                    if (boolean2 && this.adjustGoalData.isNodeNew) {
                        for (int int23 = 0; int23 < this.adjustGoalData.node.edges.size(); int23++) {
                            PolygonalMap2.Edge edge13 = this.adjustGoalData.node.edges.get(int23);
                            edge13.obstacle.unsplit(this.adjustGoalData.node, edge13.edgeRing);
                        }

                        this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                    }

                    return boolean4;
                }

                if (boolean0) {
                    for (PolygonalMap2.VisibilityGraph visibilityGraph13 : this.graphs) {
                        visibilityGraph13.render();
                    }
                }

                if (visibilityGraph1 != null) {
                    visibilityGraph1.removeNode(searchNode0.vgNode);
                }

                if (visibilityGraph2 != null) {
                    visibilityGraph2.removeNode(searchNode1.vgNode);
                }

                for (int int24 = 0; int24 < this.astar.searchNodes.size(); int24++) {
                    this.astar.searchNodes.get(int24).release();
                }

                if (boolean1 && this.adjustStartData.isNodeNew) {
                    for (int int25 = 0; int25 < this.adjustStartData.node.edges.size(); int25++) {
                        PolygonalMap2.Edge edge14 = this.adjustStartData.node.edges.get(int25);
                        edge14.obstacle.unsplit(this.adjustStartData.node, edge14.edgeRing);
                    }

                    this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
                }

                if (boolean2 && this.adjustGoalData.isNodeNew) {
                    for (int int26 = 0; int26 < this.adjustGoalData.node.edges.size(); int26++) {
                        PolygonalMap2.Edge edge15 = this.adjustGoalData.node.edges.get(int26);
                        edge15.obstacle.unsplit(this.adjustGoalData.node, edge15.edgeRing);
                    }

                    this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
                }

                return boolean5;
            }

            if (boolean0) {
                for (PolygonalMap2.VisibilityGraph visibilityGraph14 : this.graphs) {
                    visibilityGraph14.render();
                }
            }

            if (visibilityGraph1 != null) {
                visibilityGraph1.removeNode(searchNode0.vgNode);
            }

            if (visibilityGraph2 != null) {
                visibilityGraph2.removeNode(searchNode1.vgNode);
            }

            for (int int27 = 0; int27 < this.astar.searchNodes.size(); int27++) {
                this.astar.searchNodes.get(int27).release();
            }

            if (boolean1 && this.adjustStartData.isNodeNew) {
                for (int int28 = 0; int28 < this.adjustStartData.node.edges.size(); int28++) {
                    PolygonalMap2.Edge edge16 = this.adjustStartData.node.edges.get(int28);
                    edge16.obstacle.unsplit(this.adjustStartData.node, edge16.edgeRing);
                }

                this.adjustStartData.graph.edges.remove(this.adjustStartData.newEdge);
            }

            if (boolean2 && this.adjustGoalData.isNodeNew) {
                for (int int29 = 0; int29 < this.adjustGoalData.node.edges.size(); int29++) {
                    PolygonalMap2.Edge edge17 = this.adjustGoalData.node.edges.get(int29);
                    edge17.obstacle.unsplit(this.adjustGoalData.node, edge17.edgeRing);
                }

                this.adjustGoalData.graph.edges.remove(this.adjustGoalData.newEdge);
            }

            return boolean4;
        }
    }

    private void cleanPath(
        ArrayList<ISearchNode> arrayList, PolygonalMap2.PathFindRequest pathFindRequest, boolean var3, boolean boolean2, PolygonalMap2.SearchNode searchNode2
    ) {
        boolean boolean0 = pathFindRequest.mover instanceof IsoPlayer && ((IsoPlayer)pathFindRequest.mover).isNPC();
        PolygonalMap2.Square square0 = null;
        int int0 = -123;
        int int1 = -123;

        for (int int2 = 0; int2 < arrayList.size(); int2++) {
            PolygonalMap2.SearchNode searchNode0 = (PolygonalMap2.SearchNode)arrayList.get(int2);
            float float0 = searchNode0.getX();
            float float1 = searchNode0.getY();
            float float2 = searchNode0.getZ();
            int int3 = searchNode0.vgNode == null ? 0 : searchNode0.vgNode.flags;
            PolygonalMap2.Square square1 = searchNode0.square;
            boolean boolean1 = false;
            if (square1 != null && square0 != null && square1.z == square0.z) {
                int int4 = square1.x - square0.x;
                int int5 = square1.y - square0.y;
                if (int4 == int0 && int5 == int1) {
                    if (pathFindRequest.path.nodes.size() > 1) {
                        boolean1 = true;
                        if (pathFindRequest.path.getLastNode().hasFlag(65536)) {
                            boolean1 = false;
                        }
                    }

                    if (int4 == 0 && int5 == -1 && square0.has(16384)) {
                        boolean1 = false;
                    } else if (int4 == 0 && int5 == 1 && square1.has(16384)) {
                        boolean1 = false;
                    } else if (int4 == -1 && int5 == 0 && square0.has(8192)) {
                        boolean1 = false;
                    } else if (int4 == 1 && int5 == 0 && square1.has(8192)) {
                        boolean1 = false;
                    }
                } else {
                    int0 = int4;
                    int1 = int5;
                }
            } else {
                int1 = -123;
                int0 = -123;
            }

            if (square1 != null) {
                square0 = square1;
            } else {
                square0 = null;
            }

            if (boolean0) {
                boolean1 = false;
            }

            if (boolean1) {
                PolygonalMap2.PathNode pathNode0 = pathFindRequest.path.getLastNode();
                pathNode0.x = square1.x + 0.5F;
                pathNode0.y = square1.y + 0.5F;
            } else {
                if (pathFindRequest.path.nodes.size() > 1) {
                    PolygonalMap2.PathNode pathNode1 = pathFindRequest.path.getLastNode();
                    if (Math.abs(pathNode1.x - float0) < 0.01F && Math.abs(pathNode1.y - float1) < 0.01F && Math.abs(pathNode1.z - float2) < 0.01F) {
                        pathNode1.x = float0;
                        pathNode1.y = float1;
                        pathNode1.z = float2;
                        continue;
                    }
                }

                if (int2 > 0 && searchNode0.square != null) {
                    PolygonalMap2.SearchNode searchNode1 = (PolygonalMap2.SearchNode)arrayList.get(int2 - 1);
                    if (searchNode1.square != null) {
                        int int6 = searchNode0.square.x - searchNode1.square.x;
                        int int7 = searchNode0.square.y - searchNode1.square.y;
                        if (int6 == 0 && int7 == -1 && searchNode1.square.has(16384)) {
                            int3 |= 65536;
                        } else if (int6 == 0 && int7 == 1 && searchNode0.square.has(16384)) {
                            int3 |= 65536;
                        } else if (int6 == -1 && int7 == 0 && searchNode1.square.has(8192)) {
                            int3 |= 65536;
                        } else if (int6 == 1 && int7 == 0 && searchNode0.square.has(8192)) {
                            int3 |= 65536;
                        }
                    }
                }

                pathFindRequest.path.addNode(float0, float1, float2, int3);
            }
        }

        if (pathFindRequest.mover instanceof IsoPlayer && !boolean0) {
            if (pathFindRequest.path.isEmpty()) {
                Object object = null;
            } else {
                pathFindRequest.path.getNode(0);
            }

            if (!boolean2
                && searchNode2.square != null
                && IsoUtils.DistanceToSquared(searchNode2.square.x + 0.5F, searchNode2.square.y + 0.5F, pathFindRequest.targetX, pathFindRequest.targetY)
                    > 0.010000000000000002) {
                pathFindRequest.path.addNode(pathFindRequest.targetX, pathFindRequest.targetY, pathFindRequest.targetZ, 0);
            }
        }

        PolygonalMap2.PathNode pathNode2 = null;

        for (int int8 = 0; int8 < pathFindRequest.path.nodes.size(); int8++) {
            PolygonalMap2.PathNode pathNode3 = pathFindRequest.path.nodes.get(int8);
            PolygonalMap2.PathNode pathNode4 = int8 < pathFindRequest.path.nodes.size() - 1 ? pathFindRequest.path.nodes.get(int8 + 1) : null;
            if (pathNode3.hasFlag(1)) {
                boolean boolean3 = pathNode2 != null && pathNode2.hasFlag(2) || pathNode4 != null && pathNode4.hasFlag(2);
                if (!boolean3) {
                    pathNode3.flags &= -4;
                }
            }

            pathNode2 = pathNode3;
        }
    }

    private void smoothPath(PolygonalMap2.Path path) {
        int int0 = 0;

        while (int0 < path.nodes.size() - 2) {
            PolygonalMap2.PathNode pathNode0 = path.nodes.get(int0);
            PolygonalMap2.PathNode pathNode1 = path.nodes.get(int0 + 1);
            PolygonalMap2.PathNode pathNode2 = path.nodes.get(int0 + 2);
            if ((int)pathNode0.z != (int)pathNode1.z || (int)pathNode0.z != (int)pathNode2.z) {
                int0++;
            } else if (!this.lcc.isNotClear(this, pathNode0.x, pathNode0.y, pathNode2.x, pathNode2.y, (int)pathNode0.z, 20)) {
                path.nodes.remove(int0 + 1);
                path.nodePool.push(pathNode1);
            } else {
                int0++;
            }
        }
    }

    float getApparentZ(IsoGridSquare square) {
        if (square.Has(IsoObjectType.stairsTW) || square.Has(IsoObjectType.stairsTN)) {
            return square.z + 0.75F;
        } else if (square.Has(IsoObjectType.stairsMW) || square.Has(IsoObjectType.stairsMN)) {
            return square.z + 0.5F;
        } else {
            return !square.Has(IsoObjectType.stairsBW) && !square.Has(IsoObjectType.stairsBN) ? square.z : square.z + 0.25F;
        }
    }

    public void render() {
        if (Core.bDebug) {
            boolean boolean0 = DebugOptions.instance.PathfindPathToMouseEnable.getValue()
                && !this.testRequest.done
                && IsoPlayer.getInstance().getPath2() == null;
            if (DebugOptions.instance.PolymapRenderClusters.getValue()) {
                synchronized (this.renderLock) {
                    for (PolygonalMap2.VehicleCluster vehicleCluster : this.clusters) {
                        for (PolygonalMap2.VehicleRect vehicleRect0 : vehicleCluster.rects) {
                            LineDrawer.addLine(
                                vehicleRect0.x,
                                vehicleRect0.y,
                                vehicleRect0.z,
                                vehicleRect0.right(),
                                vehicleRect0.bottom(),
                                vehicleRect0.z,
                                0.0F,
                                0.0F,
                                1.0F,
                                null,
                                false
                            );
                        }

                        PolygonalMap2.VehicleRect vehicleRect1 = vehicleCluster.bounds();
                        vehicleRect1.release();
                    }

                    if (!boolean0) {
                        for (PolygonalMap2.VisibilityGraph visibilityGraph0 : this.graphs) {
                            visibilityGraph0.render();
                        }
                    }
                }
            }

            if (DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
                float float0 = Mouse.getX();
                float float1 = Mouse.getY();
                int int0 = (int)IsoPlayer.getInstance().getZ();
                float float2 = IsoUtils.XToIso(float0, float1, int0);
                float float3 = IsoUtils.YToIso(float0, float1, int0);
                LineDrawer.addLine(IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, int0, float2, float3, int0, 1, 1, 1, null);
                int int1 = 9;
                int1 |= 2;
                if (this.lccMain.isNotClear(this, IsoPlayer.getInstance().x, IsoPlayer.getInstance().y, float2, float3, int0, null, int1)) {
                    Vector2f vector2f = this.resolveCollision(IsoPlayer.getInstance(), float2, float3, PolygonalMap2.L_render.vector2f);
                    LineDrawer.addLine(
                        vector2f.x - 0.05F, vector2f.y - 0.05F, int0, vector2f.x + 0.05F, vector2f.y + 0.05F, int0, 1.0F, 1.0F, 0.0F, null, false
                    );
                }
            }

            if (GameKeyboard.isKeyDown(209) && !GameKeyboard.wasKeyDown(209)) {
                this.testZ = Math.max(this.testZ - 1, 0);
            }

            if (GameKeyboard.isKeyDown(201) && !GameKeyboard.wasKeyDown(201)) {
                this.testZ = Math.min(this.testZ + 1, 7);
            }

            if (boolean0) {
                float float4 = Mouse.getX();
                float float5 = Mouse.getY();
                int int2 = this.testZ;
                float float6 = IsoUtils.XToIso(float4, float5, int2);
                float float7 = IsoUtils.YToIso(float4, float5, int2);
                float float8 = int2;

                for (int int3 = -1; int3 <= 2; int3++) {
                    LineDrawer.addLine(
                        (int)float6 - 1, (int)float7 + int3, (int)float8, (int)float6 + 2, (int)float7 + int3, (int)float8, 0.3F, 0.3F, 0.3F, null, false
                    );
                }

                for (int int4 = -1; int4 <= 2; int4++) {
                    LineDrawer.addLine(
                        (int)float6 + int4, (int)float7 - 1, (int)float8, (int)float6 + int4, (int)float7 + 2, (int)float8, 0.3F, 0.3F, 0.3F, null, false
                    );
                }

                for (int int5 = -1; int5 <= 1; int5++) {
                    for (int int6 = -1; int6 <= 1; int6++) {
                        float float9 = 0.3F;
                        float float10 = 0.0F;
                        float float11 = 0.0F;
                        IsoGridSquare square0 = IsoWorld.instance.CurrentCell.getGridSquare((int)float6 + int6, (int)float7 + int5, (int)float8);
                        if (square0 == null || square0.isSolid() || square0.isSolidTrans() || square0.HasStairs()) {
                            LineDrawer.addLine(
                                (int)float6 + int6,
                                (int)float7 + int5,
                                (int)float8,
                                (int)float6 + int6 + 1,
                                (int)float7 + int5 + 1,
                                (int)float8,
                                float9,
                                float10,
                                float11,
                                null,
                                false
                            );
                        }
                    }
                }

                if (int2 < (int)IsoPlayer.getInstance().getZ()) {
                    LineDrawer.addLine(
                        (int)float6, (int)float7, (int)float8, (int)float6, (int)float7, (int)IsoPlayer.getInstance().getZ(), 0.3F, 0.3F, 0.3F, null, true
                    );
                } else if (int2 > (int)IsoPlayer.getInstance().getZ()) {
                    LineDrawer.addLine(
                        (int)float6, (int)float7, (int)float8, (int)float6, (int)float7, (int)IsoPlayer.getInstance().getZ(), 0.3F, 0.3F, 0.3F, null, true
                    );
                }

                PolygonalMap2.PathFindRequest pathFindRequest = PolygonalMap2.PathFindRequest.alloc()
                    .init(
                        this.testRequest,
                        IsoPlayer.getInstance(),
                        IsoPlayer.getInstance().x,
                        IsoPlayer.getInstance().y,
                        IsoPlayer.getInstance().z,
                        float6,
                        float7,
                        float8
                    );
                if (DebugOptions.instance.PathfindPathToMouseAllowCrawl.getValue()) {
                    pathFindRequest.bCanCrawl = true;
                    if (DebugOptions.instance.PathfindPathToMouseIgnoreCrawlCost.getValue()) {
                        pathFindRequest.bIgnoreCrawlCost = true;
                    }
                }

                if (DebugOptions.instance.PathfindPathToMouseAllowThump.getValue()) {
                    pathFindRequest.bCanThump = true;
                }

                this.testRequest.done = false;
                synchronized (this.renderLock) {
                    boolean boolean1 = DebugOptions.instance.PolymapRenderClusters.getValue();
                    if (this.findPath(pathFindRequest, boolean1) && !pathFindRequest.path.isEmpty()) {
                        for (int int7 = 0; int7 < pathFindRequest.path.nodes.size() - 1; int7++) {
                            PolygonalMap2.PathNode pathNode0 = pathFindRequest.path.nodes.get(int7);
                            PolygonalMap2.PathNode pathNode1 = pathFindRequest.path.nodes.get(int7 + 1);
                            IsoGridSquare square1 = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode0.x, (double)pathNode0.y, (double)pathNode0.z);
                            IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode1.x, (double)pathNode1.y, (double)pathNode1.z);
                            float float12 = square1 == null ? pathNode0.z : this.getApparentZ(square1);
                            float float13 = square2 == null ? pathNode1.z : this.getApparentZ(square2);
                            float float14 = 1.0F;
                            float float15 = 1.0F;
                            float float16 = 0.0F;
                            if (float12 != (int)float12 || float13 != (int)float13) {
                                float15 = 0.0F;
                            }

                            LineDrawer.addLine(pathNode0.x, pathNode0.y, float12, pathNode1.x, pathNode1.y, float13, float14, float15, float16, null, true);
                            LineDrawer.addRect(pathNode0.x - 0.05F, pathNode0.y - 0.05F, float12, 0.1F, 0.1F, float14, float15, float16);
                        }

                        PathFindBehavior2.closestPointOnPath(
                            IsoPlayer.getInstance().x,
                            IsoPlayer.getInstance().y,
                            IsoPlayer.getInstance().z,
                            IsoPlayer.getInstance(),
                            pathFindRequest.path,
                            this.pointOnPath
                        );
                        PolygonalMap2.PathNode pathNode2 = pathFindRequest.path.nodes.get(this.pointOnPath.pathIndex);
                        PolygonalMap2.PathNode pathNode3 = pathFindRequest.path.nodes.get(this.pointOnPath.pathIndex + 1);
                        IsoGridSquare square3 = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode2.x, (double)pathNode2.y, (double)pathNode2.z);
                        IsoGridSquare square4 = IsoWorld.instance.CurrentCell.getGridSquare((double)pathNode3.x, (double)pathNode3.y, (double)pathNode3.z);
                        float float17 = square3 == null ? pathNode2.z : this.getApparentZ(square3);
                        float float18 = square4 == null ? pathNode3.z : this.getApparentZ(square4);
                        float float19 = float17 + (float18 - float17) * this.pointOnPath.dist;
                        LineDrawer.addLine(
                            this.pointOnPath.x - 0.05F,
                            this.pointOnPath.y - 0.05F,
                            float19,
                            this.pointOnPath.x + 0.05F,
                            this.pointOnPath.y + 0.05F,
                            float19,
                            0.0F,
                            1.0F,
                            0.0F,
                            null,
                            true
                        );
                        LineDrawer.addLine(
                            this.pointOnPath.x - 0.05F,
                            this.pointOnPath.y + 0.05F,
                            float19,
                            this.pointOnPath.x + 0.05F,
                            this.pointOnPath.y - 0.05F,
                            float19,
                            0.0F,
                            1.0F,
                            0.0F,
                            null,
                            true
                        );
                        if (GameKeyboard.isKeyDown(207) && !GameKeyboard.wasKeyDown(207)) {
                            Object object = LuaManager.env.rawget("ISPathFindAction_pathToLocationF");
                            if (object != null) {
                                LuaManager.caller.pcall(LuaManager.thread, object, float6, float7, float8);
                            }
                        }
                    }

                    pathFindRequest.release();
                }
            } else {
                for (int int8 = 0; int8 < this.testRequest.path.nodes.size() - 1; int8++) {
                    PolygonalMap2.PathNode pathNode4 = this.testRequest.path.nodes.get(int8);
                    PolygonalMap2.PathNode pathNode5 = this.testRequest.path.nodes.get(int8 + 1);
                    float float20 = 1.0F;
                    float float21 = 1.0F;
                    float float22 = 0.0F;
                    if (pathNode4.z != (int)pathNode4.z || pathNode5.z != (int)pathNode5.z) {
                        float21 = 0.0F;
                    }

                    LineDrawer.addLine(pathNode4.x, pathNode4.y, pathNode4.z, pathNode5.x, pathNode5.y, pathNode5.z, float20, float21, float22, null, true);
                }

                this.testRequest.done = false;
            }

            if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
                float float23 = Mouse.getX();
                float float24 = Mouse.getY();
                int int9 = this.testZ;
                float float25 = IsoUtils.XToIso(float23, float24, int9);
                float float26 = IsoUtils.YToIso(float23, float24, int9);
                PolygonalMap2.VisibilityGraph visibilityGraph1 = this.getVisGraphAt(float25, float26, int9, 1);
                if (visibilityGraph1 != null) {
                    PolygonalMap2.Node node0 = visibilityGraph1.getClosestNodeTo(float25, float26);
                    if (node0 != null) {
                        for (PolygonalMap2.Connection connection : node0.visible) {
                            PolygonalMap2.Node node1 = connection.otherNode(node0);
                            LineDrawer.addLine(node0.x, node0.y, int9, node1.x, node1.y, int9, 1.0F, 0.0F, 0.0F, null, true);
                        }
                    }
                }
            }

            this.updateMain();
        }
    }

    public void squareChanged(IsoGridSquare square) {
        PolygonalMap2.SquareUpdateTask squareUpdateTask = PolygonalMap2.SquareUpdateTask.alloc().init(this, square);
        this.squareTaskQueue.add(squareUpdateTask);
        this.thread.wake();
    }

    public void addChunkToWorld(IsoChunk chunk) {
        PolygonalMap2.ChunkUpdateTask chunkUpdateTask = PolygonalMap2.ChunkUpdateTask.alloc().init(this, chunk);
        this.chunkTaskQueue.add(chunkUpdateTask);
        this.thread.wake();
    }

    public void removeChunkFromWorld(IsoChunk chunk) {
        if (this.thread != null) {
            PolygonalMap2.ChunkRemoveTask chunkRemoveTask = PolygonalMap2.ChunkRemoveTask.alloc().init(this, chunk);
            this.chunkTaskQueue.add(chunkRemoveTask);
            this.thread.wake();
        }
    }

    public void addVehicleToWorld(BaseVehicle vehicle) {
        PolygonalMap2.VehicleAddTask vehicleAddTask = PolygonalMap2.VehicleAddTask.alloc();
        vehicleAddTask.init(this, vehicle);
        this.vehicleTaskQueue.add(vehicleAddTask);
        PolygonalMap2.VehicleState vehicleStatex = PolygonalMap2.VehicleState.alloc().init(vehicle);
        this.vehicleState.put(vehicle, vehicleStatex);
        this.thread.wake();
    }

    public void updateVehicle(BaseVehicle vehicle) {
        PolygonalMap2.VehicleUpdateTask vehicleUpdateTask = PolygonalMap2.VehicleUpdateTask.alloc();
        vehicleUpdateTask.init(this, vehicle);
        this.vehicleTaskQueue.add(vehicleUpdateTask);
        this.thread.wake();
    }

    public void removeVehicleFromWorld(BaseVehicle vehicle) {
        if (this.thread != null) {
            PolygonalMap2.VehicleRemoveTask vehicleRemoveTask = PolygonalMap2.VehicleRemoveTask.alloc();
            vehicleRemoveTask.init(this, vehicle);
            this.vehicleTaskQueue.add(vehicleRemoveTask);
            PolygonalMap2.VehicleState vehicleStatex = this.vehicleState.remove(vehicle);
            if (vehicleStatex != null) {
                vehicleStatex.vehicle = null;
                vehicleStatex.release();
            }

            this.thread.wake();
        }
    }

    private PolygonalMap2.Cell getCellFromSquarePos(int int0, int int1) {
        int0 -= this.minX * 300;
        int1 -= this.minY * 300;
        if (int0 >= 0 && int1 >= 0) {
            int int2 = int0 / 300;
            int int3 = int1 / 300;
            return int2 < this.width && int3 < this.height ? this.cells[int2][int3] : null;
        } else {
            return null;
        }
    }

    private PolygonalMap2.Cell getCellFromChunkPos(int int1, int int0) {
        return this.getCellFromSquarePos(int1 * 10, int0 * 10);
    }

    private PolygonalMap2.Chunk allocChunkIfNeeded(int int0, int int1) {
        PolygonalMap2.Cell cell = this.getCellFromChunkPos(int0, int1);
        return cell == null ? null : cell.allocChunkIfNeeded(int0, int1);
    }

    private PolygonalMap2.Chunk getChunkFromChunkPos(int int0, int int1) {
        PolygonalMap2.Cell cell = this.getCellFromChunkPos(int0, int1);
        return cell == null ? null : cell.getChunkFromChunkPos(int0, int1);
    }

    private PolygonalMap2.Chunk getChunkFromSquarePos(int int0, int int1) {
        PolygonalMap2.Cell cell = this.getCellFromSquarePos(int0, int1);
        return cell == null ? null : cell.getChunkFromChunkPos(int0 / 10, int1 / 10);
    }

    private PolygonalMap2.Square getSquare(int int0, int int1, int int2) {
        PolygonalMap2.Chunk chunk = this.getChunkFromSquarePos(int0, int1);
        return chunk == null ? null : chunk.getSquare(int0, int1, int2);
    }

    private boolean isBlockedInAllDirections(int int0, int int1, int int2) {
        PolygonalMap2.Square square0 = this.getSquare(int0, int1, int2);
        if (square0 == null) {
            return false;
        } else {
            PolygonalMap2.Square square1 = this.getSquare(int0, int1 - 1, int2);
            PolygonalMap2.Square square2 = this.getSquare(int0, int1 + 1, int2);
            PolygonalMap2.Square square3 = this.getSquare(int0 - 1, int1, int2);
            PolygonalMap2.Square square4 = this.getSquare(int0 + 1, int1, int2);
            boolean boolean0 = square1 != null && this.astar.canNotMoveBetween(square0, square1, false);
            boolean boolean1 = square2 != null && this.astar.canNotMoveBetween(square0, square2, false);
            boolean boolean2 = square3 != null && this.astar.canNotMoveBetween(square0, square3, false);
            boolean boolean3 = square4 != null && this.astar.canNotMoveBetween(square0, square4, false);
            return boolean0 && boolean1 && boolean2 && boolean3;
        }
    }

    public void init(IsoMetaGrid metaGrid) {
        this.minX = metaGrid.getMinX();
        this.minY = metaGrid.getMinY();
        this.width = metaGrid.getWidth();
        this.height = metaGrid.getHeight();
        this.cells = new PolygonalMap2.Cell[this.width][this.height];

        for (int int0 = 0; int0 < this.height; int0++) {
            for (int int1 = 0; int1 < this.width; int1++) {
                this.cells[int1][int0] = PolygonalMap2.Cell.alloc().init(this, this.minX + int1, this.minY + int0);
            }
        }

        this.thread = new PolygonalMap2.PMThread();
        this.thread.setName("PolyPathThread");
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public void stop() {
        this.thread.bStop = true;
        this.thread.wake();

        while (this.thread.isAlive()) {
            try {
                Thread.sleep(5L);
            } catch (InterruptedException interruptedException) {
            }
        }

        for (int int0 = 0; int0 < this.height; int0++) {
            for (int int1 = 0; int1 < this.width; int1++) {
                if (this.cells[int1][int0] != null) {
                    this.cells[int1][int0].release();
                }
            }
        }

        for (PolygonalMap2.IChunkTask iChunkTask = this.chunkTaskQueue.poll(); iChunkTask != null; iChunkTask = this.chunkTaskQueue.poll()) {
            iChunkTask.release();
        }

        for (PolygonalMap2.SquareUpdateTask squareUpdateTask = this.squareTaskQueue.poll();
            squareUpdateTask != null;
            squareUpdateTask = this.squareTaskQueue.poll()
        ) {
            squareUpdateTask.release();
        }

        for (PolygonalMap2.IVehicleTask iVehicleTask = this.vehicleTaskQueue.poll(); iVehicleTask != null; iVehicleTask = this.vehicleTaskQueue.poll()) {
            iVehicleTask.release();
        }

        for (PolygonalMap2.PathRequestTask pathRequestTask = this.requestTaskQueue.poll();
            pathRequestTask != null;
            pathRequestTask = this.requestTaskQueue.poll()
        ) {
            pathRequestTask.release();
        }

        while (!this.requests.isEmpty()) {
            this.requests.removeLast().release();
        }

        while (!this.requestToMain.isEmpty()) {
            this.requestToMain.remove().release();
        }

        for (int int2 = 0; int2 < this.vehicles.size(); int2++) {
            PolygonalMap2.Vehicle vehicle = this.vehicles.get(int2);
            vehicle.release();
        }

        for (PolygonalMap2.VehicleState vehicleStatex : this.vehicleState.values()) {
            vehicleStatex.release();
        }

        this.requestMap.clear();
        this.vehicles.clear();
        this.vehicleState.clear();
        this.vehicleMap.clear();
        this.cells = null;
        this.thread = null;
        this.rebuild = true;
    }

    public void updateMain() {
        ArrayList arrayList = IsoWorld.instance.CurrentCell.getVehicles();

        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            BaseVehicle vehicle = (BaseVehicle)arrayList.get(int0);
            PolygonalMap2.VehicleState vehicleStatex = this.vehicleState.get(vehicle);
            if (vehicleStatex != null && vehicleStatex.check()) {
                this.updateVehicle(vehicle);
            }
        }

        for (PolygonalMap2.PathFindRequest pathFindRequest = this.requestToMain.poll(); pathFindRequest != null; pathFindRequest = this.requestToMain.poll()) {
            if (this.requestMap.get(pathFindRequest.mover) == pathFindRequest) {
                this.requestMap.remove(pathFindRequest.mover);
            }

            if (!pathFindRequest.cancel) {
                if (pathFindRequest.path.isEmpty()) {
                    pathFindRequest.finder.Failed(pathFindRequest.mover);
                } else {
                    pathFindRequest.finder.Succeeded(pathFindRequest.path, pathFindRequest.mover);
                }
            }

            pathFindRequest.release();
        }
    }

    public void updateThread() {
        for (PolygonalMap2.IChunkTask iChunkTask = this.chunkTaskQueue.poll(); iChunkTask != null; iChunkTask = this.chunkTaskQueue.poll()) {
            iChunkTask.execute();
            iChunkTask.release();
            this.rebuild = true;
        }

        for (PolygonalMap2.SquareUpdateTask squareUpdateTask = this.squareTaskQueue.poll();
            squareUpdateTask != null;
            squareUpdateTask = this.squareTaskQueue.poll()
        ) {
            squareUpdateTask.execute();
            squareUpdateTask.release();
        }

        for (PolygonalMap2.IVehicleTask iVehicleTask = this.vehicleTaskQueue.poll(); iVehicleTask != null; iVehicleTask = this.vehicleTaskQueue.poll()) {
            iVehicleTask.execute();
            iVehicleTask.release();
            this.rebuild = true;
        }

        for (PolygonalMap2.PathRequestTask pathRequestTask = this.requestTaskQueue.poll();
            pathRequestTask != null;
            pathRequestTask = this.requestTaskQueue.poll()
        ) {
            pathRequestTask.execute();
            pathRequestTask.release();
        }

        if (this.rebuild) {
            for (int int0 = 0; int0 < this.graphs.size(); int0++) {
                PolygonalMap2.VisibilityGraph visibilityGraph = this.graphs.get(int0);
                visibilityGraph.release();
            }

            this.squareToNode.forEachValue(this.releaseNodeProc);
            this.createVisibilityGraphs();
            this.rebuild = false;
            PolygonalMap2.ChunkDataZ.EPOCH++;
        }

        int int1 = 2;

        while (!this.requests.isEmpty()) {
            PolygonalMap2.PathFindRequest pathFindRequest = this.requests.removeFirst();
            if (pathFindRequest.cancel) {
                this.requestToMain.add(pathFindRequest);
            } else {
                try {
                    this.findPath(pathFindRequest, false);
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }

                if (!pathFindRequest.targetXYZ.isEmpty()) {
                    this.shortestPath.copyFrom(pathFindRequest.path);
                    float float0 = pathFindRequest.targetX;
                    float float1 = pathFindRequest.targetY;
                    float float2 = pathFindRequest.targetZ;
                    float float3 = this.shortestPath.isEmpty() ? Float.MAX_VALUE : this.shortestPath.length();

                    for (byte byte0 = 0; byte0 < pathFindRequest.targetXYZ.size(); byte0 += 3) {
                        pathFindRequest.targetX = pathFindRequest.targetXYZ.get(byte0);
                        pathFindRequest.targetY = pathFindRequest.targetXYZ.get(byte0 + 1);
                        pathFindRequest.targetZ = pathFindRequest.targetXYZ.get(byte0 + 2);
                        pathFindRequest.path.clear();
                        this.findPath(pathFindRequest, false);
                        if (!pathFindRequest.path.isEmpty()) {
                            float float4 = pathFindRequest.path.length();
                            if (float4 < float3) {
                                float3 = float4;
                                this.shortestPath.copyFrom(pathFindRequest.path);
                                float0 = pathFindRequest.targetX;
                                float1 = pathFindRequest.targetY;
                                float2 = pathFindRequest.targetZ;
                            }
                        }
                    }

                    pathFindRequest.path.copyFrom(this.shortestPath);
                    pathFindRequest.targetX = float0;
                    pathFindRequest.targetY = float1;
                    pathFindRequest.targetZ = float2;
                }

                this.requestToMain.add(pathFindRequest);
                if (--int1 == 0) {
                    break;
                }
            }
        }
    }

    public PolygonalMap2.PathFindRequest addRequest(
        PolygonalMap2.IPathfinder iPathfinder, Mover mover, float float0, float float1, float float2, float float3, float float4, float float5
    ) {
        this.cancelRequest(mover);
        PolygonalMap2.PathFindRequest pathFindRequest = PolygonalMap2.PathFindRequest.alloc()
            .init(iPathfinder, mover, float0, float1, float2, float3, float4, float5);
        this.requestMap.put(mover, pathFindRequest);
        PolygonalMap2.PathRequestTask pathRequestTask = PolygonalMap2.PathRequestTask.alloc().init(this, pathFindRequest);
        this.requestTaskQueue.add(pathRequestTask);
        this.thread.wake();
        return pathFindRequest;
    }

    public void cancelRequest(Mover mover) {
        PolygonalMap2.PathFindRequest pathFindRequest = this.requestMap.remove(mover);
        if (pathFindRequest != null) {
            pathFindRequest.cancel = true;
        }
    }

    public ArrayList<PolygonalMap2.Point> getPointInLine(float float0, float float1, float float2, float float3, int int0) {
        PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
        ArrayList arrayList = new ArrayList();
        this.supercover(float0, float1, float2, float3, int0, pointPool, arrayList);
        return arrayList;
    }

    private void supercover(
        float float1, float float3, float float0, float float2, int var5, PolygonalMap2.PointPool pointPool, ArrayList<PolygonalMap2.Point> arrayList
    ) {
        double double0 = Math.abs(float0 - float1);
        double double1 = Math.abs(float2 - float3);
        int int0 = (int)Math.floor(float1);
        int int1 = (int)Math.floor(float3);
        int int2 = 1;
        byte byte0;
        double double2;
        if (double0 == 0.0) {
            byte0 = 0;
            double2 = Double.POSITIVE_INFINITY;
        } else if (float0 > float1) {
            byte0 = 1;
            int2 += (int)Math.floor(float0) - int0;
            double2 = (Math.floor(float1) + 1.0 - float1) * double1;
        } else {
            byte0 = -1;
            int2 += int0 - (int)Math.floor(float0);
            double2 = (float1 - Math.floor(float1)) * double1;
        }

        byte byte1;
        if (double1 == 0.0) {
            byte1 = 0;
            double2 -= Double.POSITIVE_INFINITY;
        } else if (float2 > float3) {
            byte1 = 1;
            int2 += (int)Math.floor(float2) - int1;
            double2 -= (Math.floor(float3) + 1.0 - float3) * double0;
        } else {
            byte1 = -1;
            int2 += int1 - (int)Math.floor(float2);
            double2 -= (float3 - Math.floor(float3)) * double0;
        }

        for (; int2 > 0; int2--) {
            PolygonalMap2.Point point = pointPool.alloc().init(int0, int1);
            if (arrayList.contains(point)) {
                pointPool.release(point);
            } else {
                arrayList.add(point);
            }

            if (double2 > 0.0) {
                int1 += byte1;
                double2 -= double0;
            } else {
                int0 += byte0;
                double2 += double1;
            }
        }
    }

    public boolean lineClearCollide(float float0, float float1, float float2, float float3, int int0) {
        return this.lineClearCollide(float0, float1, float2, float3, int0, null);
    }

    public boolean lineClearCollide(float float0, float float1, float float2, float float3, int int0, IsoMovingObject movingObject) {
        return this.lineClearCollide(float0, float1, float2, float3, int0, movingObject, true, true);
    }

    public boolean lineClearCollide(
        float float0, float float1, float float2, float float3, int int0, IsoMovingObject movingObject, boolean boolean0, boolean boolean1
    ) {
        byte byte0 = 0;
        if (boolean0) {
            byte0 |= 1;
        }

        if (boolean1) {
            byte0 |= 2;
        }

        if (Core.bDebug && DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
            byte0 |= 8;
        }

        return this.lineClearCollide(float0, float1, float2, float3, int0, movingObject, byte0);
    }

    public boolean lineClearCollide(float float0, float float1, float float2, float float3, int int0, IsoMovingObject movingObject, int int1) {
        BaseVehicle vehicle = null;
        if (movingObject instanceof IsoGameCharacter) {
            vehicle = ((IsoGameCharacter)movingObject).getVehicle();
        } else if (movingObject instanceof BaseVehicle) {
            vehicle = (BaseVehicle)movingObject;
        }

        return this.lccMain.isNotClear(this, float0, float1, float2, float3, int0, vehicle, int1);
    }

    public Vector2 getCollidepoint(float float0, float float1, float float2, float float3, int int0, IsoMovingObject movingObject, int int1) {
        BaseVehicle vehicle = null;
        if (movingObject instanceof IsoGameCharacter) {
            vehicle = ((IsoGameCharacter)movingObject).getVehicle();
        } else if (movingObject instanceof BaseVehicle) {
            vehicle = (BaseVehicle)movingObject;
        }

        return this.lccMain.getCollidepoint(this, float0, float1, float2, float3, int0, vehicle, int1);
    }

    public boolean canStandAt(float float0, float float1, int int0, IsoMovingObject movingObject, boolean boolean0, boolean boolean1) {
        BaseVehicle vehicle = null;
        if (movingObject instanceof IsoGameCharacter) {
            vehicle = ((IsoGameCharacter)movingObject).getVehicle();
        } else if (movingObject instanceof BaseVehicle) {
            vehicle = (BaseVehicle)movingObject;
        }

        byte byte0 = 0;
        if (boolean0) {
            byte0 |= 1;
        }

        if (boolean1) {
            byte0 |= 2;
        }

        if (Core.bDebug && DebugOptions.instance.PolymapRenderLineClearCollide.getValue()) {
            byte0 |= 8;
        }

        return this.canStandAt(float0, float1, int0, vehicle, byte0);
    }

    public boolean canStandAt(float float0, float float1, int int1, BaseVehicle vehicle, int int0) {
        return this.lccMain.canStandAtOld(this, float0, float1, int1, vehicle, int0);
    }

    public boolean intersectLineWithVehicle(float float8, float float6, float float7, float float5, BaseVehicle vehicle, Vector2 vector) {
        if (vehicle != null && vehicle.getScript() != null) {
            float[] floats = this.tempFloats;
            floats[0] = vehicle.getPoly().x1;
            floats[1] = vehicle.getPoly().y1;
            floats[2] = vehicle.getPoly().x2;
            floats[3] = vehicle.getPoly().y2;
            floats[4] = vehicle.getPoly().x3;
            floats[5] = vehicle.getPoly().y3;
            floats[6] = vehicle.getPoly().x4;
            floats[7] = vehicle.getPoly().y4;
            float float0 = Float.MAX_VALUE;

            for (byte byte0 = 0; byte0 < 8; byte0 += 2) {
                float float1 = floats[byte0 % 8];
                float float2 = floats[(byte0 + 1) % 8];
                float float3 = floats[(byte0 + 2) % 8];
                float float4 = floats[(byte0 + 3) % 8];
                double double0 = (float4 - float2) * (float7 - float8) - (float3 - float1) * (float5 - float6);
                if (double0 == 0.0) {
                    return false;
                }

                double double1 = ((float3 - float1) * (float6 - float2) - (float4 - float2) * (float8 - float1)) / double0;
                double double2 = ((float7 - float8) * (float6 - float2) - (float5 - float6) * (float8 - float1)) / double0;
                if (double1 >= 0.0 && double1 <= 1.0 && double2 >= 0.0 && double2 <= 1.0) {
                    float float9 = (float)(float8 + double1 * (float7 - float8));
                    float float10 = (float)(float6 + double1 * (float5 - float6));
                    float float11 = IsoUtils.DistanceTo(float8, float6, float9, float10);
                    if (float11 < float0) {
                        vector.set(float9, float10);
                        float0 = float11;
                    }
                }
            }

            return float0 < Float.MAX_VALUE;
        } else {
            return false;
        }
    }

    public Vector2f resolveCollision(IsoGameCharacter character, float float0, float float1, Vector2f vector2f) {
        return GameClient.bClient && character.isSkipResolveCollision()
            ? vector2f.set(float0, float1)
            : this.collideWithObstacles.resolveCollision(character, float0, float1, vector2f);
    }

    private static final class AdjustStartEndNodeData {
        PolygonalMap2.Obstacle obstacle;
        PolygonalMap2.Node node;
        PolygonalMap2.Edge newEdge;
        boolean isNodeNew;
        PolygonalMap2.VisibilityGraph graph;
    }

    private static final class Cell {
        PolygonalMap2 map;
        public short cx;
        public short cy;
        public PolygonalMap2.Chunk[][] chunks;
        static final ArrayDeque<PolygonalMap2.Cell> pool = new ArrayDeque<>();

        PolygonalMap2.Cell init(PolygonalMap2 polygonalMap2, int int0, int int1) {
            this.map = polygonalMap2;
            this.cx = (short)int0;
            this.cy = (short)int1;
            return this;
        }

        PolygonalMap2.Chunk getChunkFromChunkPos(int int0, int int1) {
            if (this.chunks == null) {
                return null;
            } else {
                int0 -= this.cx * 30;
                int1 -= this.cy * 30;
                return int0 >= 0 && int0 < 30 && int1 >= 0 && int1 < 30 ? this.chunks[int0][int1] : null;
            }
        }

        PolygonalMap2.Chunk allocChunkIfNeeded(int int0, int int1) {
            int0 -= this.cx * 30;
            int1 -= this.cy * 30;
            if (int0 >= 0 && int0 < 30 && int1 >= 0 && int1 < 30) {
                if (this.chunks == null) {
                    this.chunks = new PolygonalMap2.Chunk[30][30];
                }

                if (this.chunks[int0][int1] == null) {
                    this.chunks[int0][int1] = PolygonalMap2.Chunk.alloc();
                }

                this.chunks[int0][int1].init(this.cx * 30 + int0, this.cy * 30 + int1);
                return this.chunks[int0][int1];
            } else {
                return null;
            }
        }

        void removeChunk(int int0, int int1) {
            if (this.chunks != null) {
                int0 -= this.cx * 30;
                int1 -= this.cy * 30;
                if (int0 >= 0 && int0 < 30 && int1 >= 0 && int1 < 30) {
                    PolygonalMap2.Chunk chunk = this.chunks[int0][int1];
                    if (chunk != null) {
                        chunk.release();
                        this.chunks[int0][int1] = null;
                    }
                }
            }
        }

        static PolygonalMap2.Cell alloc() {
            return pool.isEmpty() ? new PolygonalMap2.Cell() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class Chunk {
        short wx;
        short wy;
        PolygonalMap2.Square[][][] squares = new PolygonalMap2.Square[10][10][8];
        final PolygonalMap2.ChunkData collision = new PolygonalMap2.ChunkData();
        static final ArrayDeque<PolygonalMap2.Chunk> pool = new ArrayDeque<>();

        void init(int int0, int int1) {
            this.wx = (short)int0;
            this.wy = (short)int1;
        }

        PolygonalMap2.Square getSquare(int int0, int int1, int int2) {
            int0 -= this.wx * 10;
            int1 -= this.wy * 10;
            return int0 >= 0 && int0 < 10 && int1 >= 0 && int1 < 10 && int2 >= 0 && int2 < 8 ? this.squares[int0][int1][int2] : null;
        }

        void setData(PolygonalMap2.ChunkUpdateTask chunkUpdateTask) {
            for (int int0 = 0; int0 < 8; int0++) {
                for (int int1 = 0; int1 < 10; int1++) {
                    for (int int2 = 0; int2 < 10; int2++) {
                        PolygonalMap2.Square square = this.squares[int2][int1][int0];
                        int int3 = chunkUpdateTask.data[int2][int1][int0];
                        if (int3 == 0) {
                            if (square != null) {
                                square.release();
                                this.squares[int2][int1][int0] = null;
                            }
                        } else {
                            if (square == null) {
                                square = PolygonalMap2.Square.alloc();
                                this.squares[int2][int1][int0] = square;
                            }

                            square.init(this.wx * 10 + int2, this.wy * 10 + int1, int0);
                            square.bits = int3;
                            square.cost = chunkUpdateTask.cost[int2][int1][int0];
                        }
                    }
                }
            }

            PolygonalMap2.ChunkDataZ.EPOCH++;
        }

        boolean setData(PolygonalMap2.SquareUpdateTask squareUpdateTask) {
            int int0 = squareUpdateTask.x - this.wx * 10;
            int int1 = squareUpdateTask.y - this.wy * 10;
            if (int0 < 0 || int0 >= 10) {
                return false;
            } else if (int1 >= 0 && int1 < 10) {
                PolygonalMap2.Square square = this.squares[int0][int1][squareUpdateTask.z];
                if (squareUpdateTask.bits == 0) {
                    if (square != null) {
                        square.release();
                        this.squares[int0][int1][squareUpdateTask.z] = null;
                        return true;
                    }
                } else {
                    if (square == null) {
                        square = PolygonalMap2.Square.alloc().init(squareUpdateTask.x, squareUpdateTask.y, squareUpdateTask.z);
                        this.squares[int0][int1][squareUpdateTask.z] = square;
                    }

                    if (square.bits != squareUpdateTask.bits || square.cost != squareUpdateTask.cost) {
                        square.bits = squareUpdateTask.bits;
                        square.cost = squareUpdateTask.cost;
                        return true;
                    }
                }

                return false;
            } else {
                return false;
            }
        }

        static PolygonalMap2.Chunk alloc() {
            return pool.isEmpty() ? new PolygonalMap2.Chunk() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class ChunkData {
        final PolygonalMap2.ChunkDataZ[] data = new PolygonalMap2.ChunkDataZ[8];

        public PolygonalMap2.ChunkDataZ init(PolygonalMap2.Chunk chunk, int int0) {
            if (this.data[int0] == null) {
                this.data[int0] = PolygonalMap2.ChunkDataZ.pool.alloc();
                this.data[int0].init(chunk, int0);
            } else if (this.data[int0].epoch != PolygonalMap2.ChunkDataZ.EPOCH) {
                this.data[int0].clear();
                this.data[int0].init(chunk, int0);
            }

            return this.data[int0];
        }

        public void clear() {
            PZArrayUtil.forEach(this.data, chunkDataZ -> {
                if (chunkDataZ != null) {
                    chunkDataZ.clear();
                    PolygonalMap2.ChunkDataZ.pool.release(chunkDataZ);
                }
            });
            Arrays.fill(this.data, null);
        }
    }

    private static final class ChunkDataZ {
        public PolygonalMap2.Chunk chunk;
        public final ArrayList<PolygonalMap2.Obstacle> obstacles = new ArrayList<>();
        public final ArrayList<PolygonalMap2.Node> nodes = new ArrayList<>();
        public int z;
        static short EPOCH = 0;
        short epoch;
        public static final ObjectPool<PolygonalMap2.ChunkDataZ> pool = new ObjectPool<>(PolygonalMap2.ChunkDataZ::new);

        public void init(PolygonalMap2.Chunk chunkx, int int0) {
            this.chunk = chunkx;
            this.z = int0;
            this.epoch = EPOCH;
            if (PolygonalMap2.instance.clipperThread == null) {
                PolygonalMap2.instance.clipperThread = new Clipper();
            }

            Clipper clipper = PolygonalMap2.instance.clipperThread;
            clipper.clear();
            int int1 = chunkx.wx * 10;
            int int2 = chunkx.wy * 10;

            for (int int3 = int2 - 2; int3 < int2 + 10 + 2; int3++) {
                for (int int4 = int1 - 2; int4 < int1 + 10 + 2; int4++) {
                    PolygonalMap2.Square square0 = PolygonalMap2.instance.getSquare(int4, int3, int0);
                    if (square0 != null && square0.has(512)) {
                        if (square0.isReallySolid() || square0.has(128) || square0.has(64) || square0.has(16) || square0.has(8)) {
                            clipper.addAABBBevel(int4 - 0.3F, int3 - 0.3F, int4 + 1.0F + 0.3F, int3 + 1.0F + 0.3F, 0.19800001F);
                        }

                        if (square0.has(2) || square0.has(256)) {
                            clipper.addAABBBevel(int4 - 0.3F, int3 - 0.3F, int4 + 0.3F, int3 + 1.0F + 0.3F, 0.19800001F);
                        }

                        if (square0.has(4) || square0.has(32)) {
                            clipper.addAABBBevel(int4 - 0.3F, int3 - 0.3F, int4 + 1.0F + 0.3F, int3 + 0.3F, 0.19800001F);
                        }

                        if (square0.has(256)) {
                            PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(int4 + 1, int3, int0);
                            if (square1 != null) {
                                clipper.addAABBBevel(int4 + 1 - 0.3F, int3 - 0.3F, int4 + 1 + 0.3F, int3 + 1.0F + 0.3F, 0.19800001F);
                            }
                        }

                        if (square0.has(32)) {
                            PolygonalMap2.Square square2 = PolygonalMap2.instance.getSquare(int4, int3 + 1, int0);
                            if (square2 != null) {
                                clipper.addAABBBevel(int4 - 0.3F, int3 + 1 - 0.3F, int4 + 1.0F + 0.3F, int3 + 1 + 0.3F, 0.19800001F);
                            }
                        }
                    } else {
                        clipper.addAABB(int4, int3, int4 + 1.0F, int3 + 1.0F);
                    }
                }
            }

            ByteBuffer byteBuffer = PolygonalMap2.instance.xyBufferThread;
            int int5 = clipper.generatePolygons();

            for (int int6 = 0; int6 < int5; int6++) {
                byteBuffer.clear();
                clipper.getPolygon(int6, byteBuffer);
                PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init((IsoGridSquare)null);
                this.getEdgesFromBuffer(byteBuffer, obstacle, true);
                short short0 = byteBuffer.getShort();

                for (int int7 = 0; int7 < short0; int7++) {
                    this.getEdgesFromBuffer(byteBuffer, obstacle, false);
                }

                obstacle.calcBounds();
                this.obstacles.add(obstacle);
            }

            int int8 = chunkx.wx * 10;
            int int9 = chunkx.wy * 10;
            int int10 = int8 + 10;
            int int11 = int9 + 10;
            int8 -= 2;
            int9 -= 2;
            int10 += 2;
            int11 += 2;
            PolygonalMap2.ImmutableRectF immutableRectF0 = PolygonalMap2.ImmutableRectF.alloc();
            immutableRectF0.init(int8, int9, int10 - int8, int11 - int9);
            PolygonalMap2.ImmutableRectF immutableRectF1 = PolygonalMap2.ImmutableRectF.alloc();

            for (int int12 = 0; int12 < PolygonalMap2.instance.vehicles.size(); int12++) {
                PolygonalMap2.Vehicle vehicle = PolygonalMap2.instance.vehicles.get(int12);
                PolygonalMap2.VehiclePoly vehiclePoly = vehicle.polyPlusRadius;
                float float0 = Math.min(vehiclePoly.x1, Math.min(vehiclePoly.x2, Math.min(vehiclePoly.x3, vehiclePoly.x4)));
                float float1 = Math.min(vehiclePoly.y1, Math.min(vehiclePoly.y2, Math.min(vehiclePoly.y3, vehiclePoly.y4)));
                float float2 = Math.max(vehiclePoly.x1, Math.max(vehiclePoly.x2, Math.max(vehiclePoly.x3, vehiclePoly.x4)));
                float float3 = Math.max(vehiclePoly.y1, Math.max(vehiclePoly.y2, Math.max(vehiclePoly.y3, vehiclePoly.y4)));
                immutableRectF1.init(float0, float1, float2 - float0, float3 - float1);
                if (immutableRectF0.intersects(immutableRectF1)) {
                    this.addEdgesForVehicle(vehicle);
                }
            }

            immutableRectF0.release();
            immutableRectF1.release();
        }

        private void getEdgesFromBuffer(ByteBuffer byteBuffer, PolygonalMap2.Obstacle obstacle, boolean boolean0) {
            short short0 = byteBuffer.getShort();
            if (short0 < 3) {
                byteBuffer.position(byteBuffer.position() + short0 * 4 * 2);
            } else {
                PolygonalMap2.EdgeRing edgeRing = obstacle.outer;
                if (!boolean0) {
                    edgeRing = PolygonalMap2.EdgeRing.alloc();
                    edgeRing.clear();
                    obstacle.inner.add(edgeRing);
                }

                int int0 = this.nodes.size();

                for (int int1 = 0; int1 < short0; int1++) {
                    float float0 = byteBuffer.getFloat();
                    float float1 = byteBuffer.getFloat();
                    PolygonalMap2.Node node0 = PolygonalMap2.Node.alloc().init(float0, float1, this.z);
                    node0.flags |= 4;
                    this.nodes.add(int0, node0);
                }

                for (int int2 = int0; int2 < this.nodes.size() - 1; int2++) {
                    PolygonalMap2.Node node1 = this.nodes.get(int2);
                    PolygonalMap2.Node node2 = this.nodes.get(int2 + 1);
                    PolygonalMap2.Edge edge0 = PolygonalMap2.Edge.alloc().init(node1, node2, obstacle, edgeRing);
                    edgeRing.add(edge0);
                }

                PolygonalMap2.Node node3 = this.nodes.get(this.nodes.size() - 1);
                PolygonalMap2.Node node4 = this.nodes.get(int0);
                PolygonalMap2.Edge edge1 = PolygonalMap2.Edge.alloc().init(node3, node4, obstacle, edgeRing);
                edgeRing.add(edge1);
            }
        }

        private void addEdgesForVehicle(PolygonalMap2.Vehicle vehicle) {
            PolygonalMap2.VehiclePoly vehiclePoly = vehicle.polyPlusRadius;
            int int0 = (int)vehiclePoly.z;
            PolygonalMap2.Node node0 = PolygonalMap2.Node.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int0);
            PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int0);
            PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int0);
            PolygonalMap2.Node node3 = PolygonalMap2.Node.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int0);
            node0.flags |= 4;
            node1.flags |= 4;
            node2.flags |= 4;
            node3.flags |= 4;
            PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init(vehicle);
            this.obstacles.add(obstacle);
            PolygonalMap2.Edge edge0 = PolygonalMap2.Edge.alloc().init(node0, node1, obstacle, obstacle.outer);
            PolygonalMap2.Edge edge1 = PolygonalMap2.Edge.alloc().init(node1, node2, obstacle, obstacle.outer);
            PolygonalMap2.Edge edge2 = PolygonalMap2.Edge.alloc().init(node2, node3, obstacle, obstacle.outer);
            PolygonalMap2.Edge edge3 = PolygonalMap2.Edge.alloc().init(node3, node0, obstacle, obstacle.outer);
            obstacle.outer.add(edge0);
            obstacle.outer.add(edge1);
            obstacle.outer.add(edge2);
            obstacle.outer.add(edge3);
            obstacle.calcBounds();
            this.nodes.add(node0);
            this.nodes.add(node1);
            this.nodes.add(node2);
            this.nodes.add(node3);
        }

        public void clear() {
            PolygonalMap2.Node.releaseAll(this.nodes);
            this.nodes.clear();
            PolygonalMap2.Obstacle.releaseAll(this.obstacles);
            this.obstacles.clear();
        }
    }

    private static final class ChunkRemoveTask implements PolygonalMap2.IChunkTask {
        PolygonalMap2 map;
        int wx;
        int wy;
        static final ArrayDeque<PolygonalMap2.ChunkRemoveTask> pool = new ArrayDeque<>();

        PolygonalMap2.ChunkRemoveTask init(PolygonalMap2 polygonalMap2, IsoChunk chunk) {
            this.map = polygonalMap2;
            this.wx = chunk.wx;
            this.wy = chunk.wy;
            return this;
        }

        @Override
        public void execute() {
            PolygonalMap2.Cell cell = this.map.getCellFromChunkPos(this.wx, this.wy);
            cell.removeChunk(this.wx, this.wy);
        }

        static PolygonalMap2.ChunkRemoveTask alloc() {
            synchronized (pool) {
                return pool.isEmpty() ? new PolygonalMap2.ChunkRemoveTask() : pool.pop();
            }
        }

        @Override
        public void release() {
            synchronized (pool) {
                assert !pool.contains(this);

                pool.push(this);
            }
        }
    }

    private static final class ChunkUpdateTask implements PolygonalMap2.IChunkTask {
        PolygonalMap2 map;
        int wx;
        int wy;
        final int[][][] data = new int[10][10][8];
        final short[][][] cost = new short[10][10][8];
        static final ArrayDeque<PolygonalMap2.ChunkUpdateTask> pool = new ArrayDeque<>();

        PolygonalMap2.ChunkUpdateTask init(PolygonalMap2 polygonalMap2, IsoChunk chunk) {
            this.map = polygonalMap2;
            this.wx = chunk.wx;
            this.wy = chunk.wy;

            for (int int0 = 0; int0 < 8; int0++) {
                for (int int1 = 0; int1 < 10; int1++) {
                    for (int int2 = 0; int2 < 10; int2++) {
                        IsoGridSquare square = chunk.getGridSquare(int2, int1, int0);
                        if (square == null) {
                            this.data[int2][int1][int0] = 0;
                            this.cost[int2][int1][int0] = 0;
                        } else {
                            this.data[int2][int1][int0] = PolygonalMap2.SquareUpdateTask.getBits(square);
                            this.cost[int2][int1][int0] = PolygonalMap2.SquareUpdateTask.getCost(square);
                        }
                    }
                }
            }

            return this;
        }

        @Override
        public void execute() {
            PolygonalMap2.Chunk chunk = this.map.allocChunkIfNeeded(this.wx, this.wy);
            chunk.setData(this);
        }

        static PolygonalMap2.ChunkUpdateTask alloc() {
            synchronized (pool) {
                return pool.isEmpty() ? new PolygonalMap2.ChunkUpdateTask() : pool.pop();
            }
        }

        @Override
        public void release() {
            synchronized (pool) {
                assert !pool.contains(this);

                pool.push(this);
            }
        }
    }

    private static final class ClosestPointOnEdge {
        PolygonalMap2.Edge edge;
        PolygonalMap2.Node node;
        final Vector2f point = new Vector2f();
        double distSq;
    }

    private static final class ClusterOutline {
        int x;
        int y;
        int z;
        boolean w;
        boolean n;
        boolean e;
        boolean s;
        boolean tw;
        boolean tn;
        boolean te;
        boolean ts;
        boolean inner;
        boolean innerCorner;
        boolean start;
        static final ArrayDeque<PolygonalMap2.ClusterOutline> pool = new ArrayDeque<>();

        PolygonalMap2.ClusterOutline init(int int0, int int1, int int2) {
            this.x = int0;
            this.y = int1;
            this.z = int2;
            this.w = this.n = this.e = this.s = false;
            this.tw = this.tn = this.te = this.ts = false;
            this.inner = this.innerCorner = this.start = false;
            return this;
        }

        static PolygonalMap2.ClusterOutline alloc() {
            return pool.isEmpty() ? new PolygonalMap2.ClusterOutline() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class ClusterOutlineGrid {
        PolygonalMap2.ClusterOutline[] elements;
        int W;
        int H;

        PolygonalMap2.ClusterOutlineGrid setSize(int int0, int int1) {
            if (this.elements == null || this.elements.length < int0 * int1) {
                this.elements = new PolygonalMap2.ClusterOutline[int0 * int1];
            }

            this.W = int0;
            this.H = int1;
            return this;
        }

        void releaseElements() {
            for (int int0 = 0; int0 < this.H; int0++) {
                for (int int1 = 0; int1 < this.W; int1++) {
                    if (this.elements[int1 + int0 * this.W] != null) {
                        this.elements[int1 + int0 * this.W].release();
                        this.elements[int1 + int0 * this.W] = null;
                    }
                }
            }
        }

        void setInner(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            if (clusterOutline != null) {
                clusterOutline.inner = true;
            }
        }

        void setWest(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            if (clusterOutline != null) {
                clusterOutline.w = true;
            }
        }

        void setNorth(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            if (clusterOutline != null) {
                clusterOutline.n = true;
            }
        }

        void setEast(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            if (clusterOutline != null) {
                clusterOutline.e = true;
            }
        }

        void setSouth(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            if (clusterOutline != null) {
                clusterOutline.s = true;
            }
        }

        boolean canTrace_W(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            return clusterOutline != null && clusterOutline.inner && clusterOutline.w && !clusterOutline.tw;
        }

        boolean canTrace_N(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            return clusterOutline != null && clusterOutline.inner && clusterOutline.n && !clusterOutline.tn;
        }

        boolean canTrace_E(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            return clusterOutline != null && clusterOutline.inner && clusterOutline.e && !clusterOutline.te;
        }

        boolean canTrace_S(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            return clusterOutline != null && clusterOutline.inner && clusterOutline.s && !clusterOutline.ts;
        }

        boolean isInner(int int0, int int1, int int2) {
            PolygonalMap2.ClusterOutline clusterOutline = this.get(int0, int1, int2);
            return clusterOutline != null && (clusterOutline.start || clusterOutline.inner);
        }

        PolygonalMap2.ClusterOutline get(int int0, int int1, int int2) {
            if (int0 < 0 || int0 >= this.W) {
                return null;
            } else if (int1 >= 0 && int1 < this.H) {
                if (this.elements[int0 + int1 * this.W] == null) {
                    this.elements[int0 + int1 * this.W] = PolygonalMap2.ClusterOutline.alloc().init(int0, int1, int2);
                }

                return this.elements[int0 + int1 * this.W];
            } else {
                return null;
            }
        }

        void trace_W(PolygonalMap2.ClusterOutline clusterOutline, ArrayList<PolygonalMap2.Node> arrayList, PolygonalMap2.Node node0) {
            int int0 = clusterOutline.x;
            int int1 = clusterOutline.y;
            int int2 = clusterOutline.z;
            if (node0 != null) {
                node0.setXY(int0, int1);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(int0, int1, int2);
                arrayList.add(node1);
            }

            clusterOutline.tw = true;
            if (this.canTrace_S(int0 - 1, int1 - 1, int2)) {
                this.get(int0, int1 - 1, int2).innerCorner = true;
                this.trace_S(this.get(int0 - 1, int1 - 1, int2), arrayList, null);
            } else if (this.canTrace_W(int0, int1 - 1, int2)) {
                this.trace_W(this.get(int0, int1 - 1, int2), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
            } else if (this.canTrace_N(int0, int1, int2)) {
                this.trace_N(clusterOutline, arrayList, null);
            }
        }

        void trace_N(PolygonalMap2.ClusterOutline clusterOutline, ArrayList<PolygonalMap2.Node> arrayList, PolygonalMap2.Node node0) {
            int int0 = clusterOutline.x;
            int int1 = clusterOutline.y;
            int int2 = clusterOutline.z;
            if (node0 != null) {
                node0.setXY(int0 + 1, int1);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(int0 + 1, int1, int2);
                arrayList.add(node1);
            }

            clusterOutline.tn = true;
            if (this.canTrace_W(int0 + 1, int1 - 1, int2)) {
                this.get(int0 + 1, int1, int2).innerCorner = true;
                this.trace_W(this.get(int0 + 1, int1 - 1, int2), arrayList, null);
            } else if (this.canTrace_N(int0 + 1, int1, int2)) {
                this.trace_N(this.get(int0 + 1, int1, int2), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
            } else if (this.canTrace_E(int0, int1, int2)) {
                this.trace_E(clusterOutline, arrayList, null);
            }
        }

        void trace_E(PolygonalMap2.ClusterOutline clusterOutline, ArrayList<PolygonalMap2.Node> arrayList, PolygonalMap2.Node node0) {
            int int0 = clusterOutline.x;
            int int1 = clusterOutline.y;
            int int2 = clusterOutline.z;
            if (node0 != null) {
                node0.setXY(int0 + 1, int1 + 1);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(int0 + 1, int1 + 1, int2);
                arrayList.add(node1);
            }

            clusterOutline.te = true;
            if (this.canTrace_N(int0 + 1, int1 + 1, int2)) {
                this.get(int0, int1 + 1, int2).innerCorner = true;
                this.trace_N(this.get(int0 + 1, int1 + 1, int2), arrayList, null);
            } else if (this.canTrace_E(int0, int1 + 1, int2)) {
                this.trace_E(this.get(int0, int1 + 1, int2), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
            } else if (this.canTrace_S(int0, int1, int2)) {
                this.trace_S(clusterOutline, arrayList, null);
            }
        }

        void trace_S(PolygonalMap2.ClusterOutline clusterOutline, ArrayList<PolygonalMap2.Node> arrayList, PolygonalMap2.Node node0) {
            int int0 = clusterOutline.x;
            int int1 = clusterOutline.y;
            int int2 = clusterOutline.z;
            if (node0 != null) {
                node0.setXY(int0, int1 + 1);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(int0, int1 + 1, int2);
                arrayList.add(node1);
            }

            clusterOutline.ts = true;
            if (this.canTrace_E(int0 - 1, int1 + 1, int2)) {
                this.get(int0 - 1, int1, int2).innerCorner = true;
                this.trace_E(this.get(int0 - 1, int1 + 1, int2), arrayList, null);
            } else if (this.canTrace_S(int0 - 1, int1, int2)) {
                this.trace_S(this.get(int0 - 1, int1, int2), arrayList, (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1));
            } else if (this.canTrace_W(int0, int1, int2)) {
                this.trace_W(clusterOutline, arrayList, null);
            }
        }

        ArrayList<PolygonalMap2.Node> trace(PolygonalMap2.ClusterOutline clusterOutline) {
            int int0 = clusterOutline.x;
            int int1 = clusterOutline.y;
            int int2 = clusterOutline.z;
            ArrayList arrayList = new ArrayList();
            PolygonalMap2.Node node0 = PolygonalMap2.Node.alloc().init(int0, int1, int2);
            arrayList.add(node0);
            clusterOutline.start = true;
            this.trace_N(clusterOutline, arrayList, null);
            PolygonalMap2.Node node1 = (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1);
            float float0 = 0.1F;
            if ((int)(node0.x + float0) == (int)(node1.x + float0) && (int)(node0.y + float0) == (int)(node1.y + float0)) {
                node1.release();
                arrayList.set(arrayList.size() - 1, node0);
            }

            return arrayList;
        }
    }

    private static final class ConnectedRegions {
        PolygonalMap2 map;
        HashSet<PolygonalMap2.Chunk> doneChunks = new HashSet<>();
        int minX;
        int minY;
        int maxX;
        int maxY;
        int MINX;
        int MINY;
        int WIDTH;
        int HEIGHT;
        BooleanGrid visited;
        int[] stack;
        int stackLen;
        int[] choices;
        int choicesLen;

        private ConnectedRegions() {
            this.visited = new BooleanGrid(this.WIDTH, this.WIDTH);
        }

        void findAdjacentChunks(int int0, int int1) {
            this.doneChunks.clear();
            this.minX = this.minY = Integer.MAX_VALUE;
            this.maxX = this.maxY = Integer.MIN_VALUE;
            PolygonalMap2.Chunk chunk = this.map.getChunkFromSquarePos(int0, int1);
            this.findAdjacentChunks(chunk);
        }

        void findAdjacentChunks(PolygonalMap2.Chunk chunk0) {
            if (chunk0 != null && !this.doneChunks.contains(chunk0)) {
                this.minX = Math.min(this.minX, chunk0.wx);
                this.minY = Math.min(this.minY, chunk0.wy);
                this.maxX = Math.max(this.maxX, chunk0.wx);
                this.maxY = Math.max(this.maxY, chunk0.wy);
                this.doneChunks.add(chunk0);
                PolygonalMap2.Chunk chunk1 = this.map.getChunkFromChunkPos(chunk0.wx - 1, chunk0.wy);
                PolygonalMap2.Chunk chunk2 = this.map.getChunkFromChunkPos(chunk0.wx, chunk0.wy - 1);
                PolygonalMap2.Chunk chunk3 = this.map.getChunkFromChunkPos(chunk0.wx + 1, chunk0.wy);
                PolygonalMap2.Chunk chunk4 = this.map.getChunkFromChunkPos(chunk0.wx, chunk0.wy + 1);
                this.findAdjacentChunks(chunk1);
                this.findAdjacentChunks(chunk2);
                this.findAdjacentChunks(chunk3);
                this.findAdjacentChunks(chunk4);
            }
        }

        void floodFill(int int0, int int1) {
            this.findAdjacentChunks(int0, int1);
            this.MINX = this.minX * 10;
            this.MINY = this.minY * 10;
            this.WIDTH = (this.maxX - this.minX + 1) * 10;
            this.HEIGHT = (this.maxY - this.minY + 1) * 10;
            this.visited = new BooleanGrid(this.WIDTH, this.WIDTH);
            this.stack = new int[this.WIDTH * this.WIDTH];
            this.choices = new int[this.WIDTH * this.HEIGHT];
            this.stackLen = 0;
            this.choicesLen = 0;
            if (this.push(int0, int1)) {
                int int2;
                label84:
                while ((int2 = this.pop()) != -1) {
                    int int3 = this.MINX + (int2 & 65535);
                    int int4 = this.MINY + (int2 >> 16) & 65535;

                    while (this.shouldVisit(int3, int4, int3, int4 - 1)) {
                        int4--;
                    }

                    boolean boolean0 = false;
                    boolean boolean1 = false;

                    while (this.visit(int3, int4)) {
                        if (!boolean0 && this.shouldVisit(int3, int4, int3 - 1, int4)) {
                            if (!this.push(int3 - 1, int4)) {
                                return;
                            }

                            boolean0 = true;
                        } else if (boolean0 && !this.shouldVisit(int3, int4, int3 - 1, int4)) {
                            boolean0 = false;
                        } else if (boolean0 && !this.shouldVisit(int3 - 1, int4, int3 - 1, int4 - 1) && !this.push(int3 - 1, int4)) {
                            return;
                        }

                        if (!boolean1 && this.shouldVisit(int3, int4, int3 + 1, int4)) {
                            if (!this.push(int3 + 1, int4)) {
                                return;
                            }

                            boolean1 = true;
                        } else if (boolean1 && !this.shouldVisit(int3, int4, int3 + 1, int4)) {
                            boolean1 = false;
                        } else if (boolean1 && !this.shouldVisit(int3 + 1, int4, int3 + 1, int4 - 1) && !this.push(int3 + 1, int4)) {
                            return;
                        }

                        int4++;
                        if (!this.shouldVisit(int3, int4 - 1, int3, int4)) {
                            continue label84;
                        }
                    }

                    return;
                }

                System.out.println("#choices=" + this.choicesLen);
            }
        }

        boolean shouldVisit(int int2, int int3, int int0, int int1) {
            if (int0 >= this.MINX + this.WIDTH || int0 < this.MINX) {
                return false;
            } else if (int1 < this.MINY + this.WIDTH && int1 >= this.MINY) {
                if (this.visited.getValue(this.gridX(int0), this.gridY(int1))) {
                    return false;
                } else {
                    PolygonalMap2.Square square0 = PolygonalMap2.instance.getSquare(int2, int3, 0);
                    PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(int0, int1, 0);
                    return square0 != null && square1 != null ? !this.isBlocked(square0, square1, false) : false;
                }
            } else {
                return false;
            }
        }

        boolean visit(int int0, int int1) {
            if (this.choicesLen >= this.WIDTH * this.WIDTH) {
                return false;
            } else {
                this.choices[this.choicesLen++] = this.gridY(int1) << 16 | (short)this.gridX(int0);
                this.visited.setValue(this.gridX(int0), this.gridY(int1), true);
                return true;
            }
        }

        boolean push(int int0, int int1) {
            if (this.stackLen >= this.WIDTH * this.WIDTH) {
                return false;
            } else {
                this.stack[this.stackLen++] = this.gridY(int1) << 16 | (short)this.gridX(int0);
                return true;
            }
        }

        int pop() {
            return this.stackLen == 0 ? -1 : this.stack[--this.stackLen];
        }

        int gridX(int int0) {
            return int0 - this.MINX;
        }

        int gridY(int int0) {
            return int0 - this.MINY;
        }

        boolean isBlocked(PolygonalMap2.Square square1, PolygonalMap2.Square square0, boolean boolean5) {
            assert Math.abs(square1.x - square0.x) <= 1;

            assert Math.abs(square1.y - square0.y) <= 1;

            assert square1.z == square0.z;

            assert square1 != square0;

            boolean boolean0 = square0.x < square1.x;
            boolean boolean1 = square0.x > square1.x;
            boolean boolean2 = square0.y < square1.y;
            boolean boolean3 = square0.y > square1.y;
            if (square0.isReallySolid()) {
                return true;
            } else if (square0.y < square1.y && square1.has(64)) {
                return true;
            } else if (square0.x < square1.x && square1.has(8)) {
                return true;
            } else if (square0.y > square1.y && square0.x == square1.x && square0.has(64)) {
                return true;
            } else if (square0.x > square1.x && square0.y == square1.y && square0.has(8)) {
                return true;
            } else if (square0.x != square1.x && square0.has(448)) {
                return true;
            } else if (square0.y != square1.y && square0.has(56)) {
                return true;
            } else if (square0.x != square1.x && square1.has(448)) {
                return true;
            } else if (square0.y != square1.y && square1.has(56)) {
                return true;
            } else if (!square0.has(512) && !square0.has(504)) {
                return true;
            } else {
                boolean boolean4 = boolean2 && square1.has(4) && (square1.x != square0.x || boolean5 || !square1.has(16384));
                boolean boolean6 = boolean0 && square1.has(2) && (square1.y != square0.y || boolean5 || !square1.has(8192));
                boolean boolean7 = boolean3 && square0.has(4) && (square1.x != square0.x || boolean5 || !square0.has(16384));
                boolean boolean8 = boolean1 && square0.has(2) && (square1.y != square0.y || boolean5 || !square0.has(8192));
                if (!boolean4 && !boolean6 && !boolean7 && !boolean8) {
                    boolean boolean9 = square0.x != square1.x && square0.y != square1.y;
                    if (boolean9) {
                        PolygonalMap2.Square square2 = PolygonalMap2.instance.getSquare(square1.x, square0.y, square1.z);
                        PolygonalMap2.Square square3 = PolygonalMap2.instance.getSquare(square0.x, square1.y, square1.z);

                        assert square2 != square1 && square2 != square0;

                        assert square3 != square1 && square3 != square0;

                        if (square0.x == square1.x + 1
                            && square0.y == square1.y + 1
                            && square2 != null
                            && square3 != null
                            && square2.has(4096)
                            && square3.has(2048)) {
                            return true;
                        }

                        if (square0.x == square1.x - 1
                            && square0.y == square1.y - 1
                            && square2 != null
                            && square3 != null
                            && square2.has(2048)
                            && square3.has(4096)) {
                            return true;
                        }

                        if (square2 != null && this.isBlocked(square1, square2, true)) {
                            return true;
                        }

                        if (square3 != null && this.isBlocked(square1, square3, true)) {
                            return true;
                        }

                        if (square2 != null && this.isBlocked(square0, square2, true)) {
                            return true;
                        }

                        if (square3 != null && this.isBlocked(square0, square3, true)) {
                            return true;
                        }
                    }

                    return false;
                } else {
                    return true;
                }
            }
        }
    }

    private static final class Connection {
        PolygonalMap2.Node node1;
        PolygonalMap2.Node node2;
        int flags;
        static final ArrayDeque<PolygonalMap2.Connection> pool = new ArrayDeque<>();

        PolygonalMap2.Connection init(PolygonalMap2.Node node0, PolygonalMap2.Node node1x, int int0) {
            this.node1 = node0;
            this.node2 = node1x;
            this.flags = int0;
            return this;
        }

        PolygonalMap2.Node otherNode(PolygonalMap2.Node node) {
            assert node == this.node1 || node == this.node2;

            return node == this.node1 ? this.node2 : this.node1;
        }

        boolean has(int int0) {
            return (this.flags & int0) != 0;
        }

        static PolygonalMap2.Connection alloc() {
            if (pool.isEmpty()) {
                boolean boolean0 = false;
            } else {
                boolean boolean1 = false;
            }

            return pool.isEmpty() ? new PolygonalMap2.Connection() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class Edge {
        PolygonalMap2.Node node1;
        PolygonalMap2.Node node2;
        PolygonalMap2.Obstacle obstacle;
        PolygonalMap2.EdgeRing edgeRing;
        final ArrayList<PolygonalMap2.Intersection> intersections = new ArrayList<>();
        final Vector2 normal = new Vector2();
        static final ArrayDeque<PolygonalMap2.Edge> pool = new ArrayDeque<>();

        PolygonalMap2.Edge init(PolygonalMap2.Node node0, PolygonalMap2.Node node1x, PolygonalMap2.Obstacle obstaclex, PolygonalMap2.EdgeRing edgeRingx) {
            if (node0 == null) {
                boolean boolean0 = true;
            }

            this.node1 = node0;
            this.node2 = node1x;
            node0.edges.add(this);
            node1x.edges.add(this);
            this.obstacle = obstaclex;
            this.edgeRing = edgeRingx;
            this.intersections.clear();
            this.normal.set(node1x.x - node0.x, node1x.y - node0.y);
            this.normal.normalize();
            this.normal.rotate((float) (Math.PI / 2));
            return this;
        }

        boolean hasNode(PolygonalMap2.Node node) {
            return node == this.node1 || node == this.node2;
        }

        void getClosestPointOnEdge(float float5, float float4, PolygonalMap2.ClosestPointOnEdge closestPointOnEdge) {
            if (this.node1.isConnectedTo(this.node2)) {
                float float0 = this.node1.x;
                float float1 = this.node1.y;
                float float2 = this.node2.x;
                float float3 = this.node2.y;
                double double0 = ((float5 - float0) * (float2 - float0) + (float4 - float1) * (float3 - float1))
                    / (Math.pow(float2 - float0, 2.0) + Math.pow(float3 - float1, 2.0));
                double double1 = float0 + double0 * (float2 - float0);
                double double2 = float1 + double0 * (float3 - float1);
                PolygonalMap2.Node node = null;
                if (double0 <= 0.0) {
                    double1 = float0;
                    double2 = float1;
                    node = this.node1;
                } else if (double0 >= 1.0) {
                    double1 = float2;
                    double2 = float3;
                    node = this.node2;
                }

                double double3 = (float5 - double1) * (float5 - double1) + (float4 - double2) * (float4 - double2);
                if (double3 < closestPointOnEdge.distSq) {
                    closestPointOnEdge.point.set((float)double1, (float)double2);
                    closestPointOnEdge.distSq = double3;
                    closestPointOnEdge.edge = this;
                    closestPointOnEdge.node = node;
                }
            }
        }

        boolean isPointOn(float float5, float float4) {
            if (!this.node1.isConnectedTo(this.node2)) {
                return false;
            } else {
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
        }

        PolygonalMap2.Edge split(PolygonalMap2.Node node) {
            PolygonalMap2.Edge edge0 = alloc().init(node, this.node2, this.obstacle, this.edgeRing);
            this.edgeRing.add(this.edgeRing.indexOf(this) + 1, edge0);
            PolygonalMap2.instance.breakConnection(this.node1, this.node2);
            this.node2.edges.remove(this);
            this.node2 = node;
            this.node2.edges.add(this);
            return edge0;
        }

        static PolygonalMap2.Edge alloc() {
            return pool.isEmpty() ? new PolygonalMap2.Edge() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            this.node1 = null;
            this.node2 = null;
            this.obstacle = null;
            this.edgeRing = null;
            this.intersections.clear();
            pool.push(this);
        }

        static void releaseAll(ArrayList<PolygonalMap2.Edge> arrayList) {
            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                ((PolygonalMap2.Edge)arrayList.get(int0)).release();
            }
        }
    }

    private static final class EdgeRing extends ArrayList<PolygonalMap2.Edge> {
        static final ArrayDeque<PolygonalMap2.EdgeRing> pool = new ArrayDeque<>();

        public boolean add(PolygonalMap2.Edge edge) {
            assert !this.contains(edge);

            return super.add(edge);
        }

        public boolean hasNode(PolygonalMap2.Node node) {
            for (int int0 = 0; int0 < this.size(); int0++) {
                PolygonalMap2.Edge edge = this.get(int0);
                if (edge.hasNode(node)) {
                    return true;
                }
            }

            return false;
        }

        boolean hasAdjacentNodes(PolygonalMap2.Node node1, PolygonalMap2.Node node0) {
            for (int int0 = 0; int0 < this.size(); int0++) {
                PolygonalMap2.Edge edge = this.get(int0);
                if (edge.hasNode(node1) && edge.hasNode(node0)) {
                    return true;
                }
            }

            return false;
        }

        boolean isPointInPolygon_CrossingNumber(float float2, float float0) {
            int int0 = 0;

            for (int int1 = 0; int1 < this.size(); int1++) {
                PolygonalMap2.Edge edge = this.get(int1);
                if (edge.node1.y <= float0 && edge.node2.y > float0 || edge.node1.y > float0 && edge.node2.y <= float0) {
                    float float1 = (float0 - edge.node1.y) / (edge.node2.y - edge.node1.y);
                    if (float2 < edge.node1.x + float1 * (edge.node2.x - edge.node1.x)) {
                        int0++;
                    }
                }
            }

            return int0 % 2 == 1;
        }

        float isLeft(float float3, float float1, float float5, float float0, float float2, float float4) {
            return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
        }

        PolygonalMap2.EdgeRingHit isPointInPolygon_WindingNumber(float float0, float float1, int int2) {
            int int0 = 0;

            for (int int1 = 0; int1 < this.size(); int1++) {
                PolygonalMap2.Edge edge = this.get(int1);
                if ((int2 & 16) != 0 && edge.isPointOn(float0, float1)) {
                    return PolygonalMap2.EdgeRingHit.OnEdge;
                }

                if (edge.node1.y <= float1) {
                    if (edge.node2.y > float1 && this.isLeft(edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y, float0, float1) > 0.0F) {
                        int0++;
                    }
                } else if (edge.node2.y <= float1 && this.isLeft(edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y, float0, float1) < 0.0F) {
                    int0--;
                }
            }

            return int0 == 0 ? PolygonalMap2.EdgeRingHit.Outside : PolygonalMap2.EdgeRingHit.Inside;
        }

        boolean lineSegmentIntersects(float float3, float float1, float float2, float float0) {
            Vector2 vector = PolygonalMap2.L_lineSegmentIntersects.v1;
            vector.set(float2 - float3, float0 - float1);
            float float4 = vector.getLength();
            vector.normalize();
            float float5 = vector.x;
            float float6 = vector.y;

            for (int int0 = 0; int0 < this.size(); int0++) {
                PolygonalMap2.Edge edge = this.get(int0);
                if (!edge.isPointOn(float3, float1) && !edge.isPointOn(float2, float0)) {
                    float float7 = edge.normal.dot(vector);
                    if (float7 >= 0.01F) {
                    }

                    float float8 = edge.node1.x;
                    float float9 = edge.node1.y;
                    float float10 = edge.node2.x;
                    float float11 = edge.node2.y;
                    float float12 = float3 - float8;
                    float float13 = float1 - float9;
                    float float14 = float10 - float8;
                    float float15 = float11 - float9;
                    float float16 = 1.0F / (float15 * float5 - float14 * float6);
                    float float17 = (float14 * float13 - float15 * float12) * float16;
                    if (float17 >= 0.0F && float17 <= float4) {
                        float float18 = (float13 * float5 - float12 * float6) * float16;
                        if (float18 >= 0.0F && float18 <= 1.0F) {
                            return true;
                        }
                    }
                }
            }

            return this.isPointInPolygon_WindingNumber((float3 + float2) / 2.0F, (float1 + float0) / 2.0F, 0) != PolygonalMap2.EdgeRingHit.Outside;
        }

        void getClosestPointOnEdge(float float0, float float1, PolygonalMap2.ClosestPointOnEdge closestPointOnEdge) {
            for (int int0 = 0; int0 < this.size(); int0++) {
                PolygonalMap2.Edge edge = this.get(int0);
                edge.getClosestPointOnEdge(float0, float1, closestPointOnEdge);
            }
        }

        static PolygonalMap2.EdgeRing alloc() {
            return pool.isEmpty() ? new PolygonalMap2.EdgeRing() : pool.pop();
        }

        public void release() {
            PolygonalMap2.Edge.releaseAll(this);
        }

        static void releaseAll(ArrayList<PolygonalMap2.EdgeRing> arrayList) {
            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                ((PolygonalMap2.EdgeRing)arrayList.get(int0)).release();
            }
        }
    }

    private static enum EdgeRingHit {
        OnEdge,
        Inside,
        Outside;
    }

    private static final class GoalNode implements IGoalNode {
        PolygonalMap2.SearchNode searchNode;

        PolygonalMap2.GoalNode init(PolygonalMap2.SearchNode searchNodex) {
            this.searchNode = searchNodex;
            return this;
        }

        @Override
        public boolean inGoal(ISearchNode iSearchNode) {
            if (this.searchNode.tx != -1) {
                PolygonalMap2.SearchNode searchNodex = (PolygonalMap2.SearchNode)iSearchNode;
                int int0 = (int)searchNodex.getX();
                int int1 = (int)searchNodex.getY();
                if (int0 % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(int0 - 1, int1) == null) {
                    return true;
                } else if (int0 % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(int0 + 1, int1) == null) {
                    return true;
                } else {
                    return int1 % 10 == 0 && PolygonalMap2.instance.getChunkFromSquarePos(int0, int1 - 1) == null
                        ? true
                        : int1 % 10 == 9 && PolygonalMap2.instance.getChunkFromSquarePos(int0, int1 + 1) == null;
                }
            } else {
                return iSearchNode == this.searchNode;
            }
        }
    }

    private interface IChunkTask {
        void execute();

        void release();
    }

    public interface IPathfinder {
        void Succeeded(PolygonalMap2.Path var1, Mover var2);

        void Failed(Mover var1);
    }

    private interface IVehicleTask {
        void init(PolygonalMap2 var1, BaseVehicle var2);

        void execute();

        void release();
    }

    private static final class ImmutableRectF {
        private float x;
        private float y;
        private float w;
        private float h;
        static final ArrayDeque<PolygonalMap2.ImmutableRectF> pool = new ArrayDeque<>();

        PolygonalMap2.ImmutableRectF init(float float0, float float1, float float2, float float3) {
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

        boolean intersects(PolygonalMap2.ImmutableRectF immutableRectF0) {
            return this.left() < immutableRectF0.right()
                && this.right() > immutableRectF0.left()
                && this.top() < immutableRectF0.bottom()
                && this.bottom() > immutableRectF0.top();
        }

        static PolygonalMap2.ImmutableRectF alloc() {
            return pool.isEmpty() ? new PolygonalMap2.ImmutableRectF() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class Intersection {
        PolygonalMap2.Edge edge1;
        PolygonalMap2.Edge edge2;
        float dist1;
        float dist2;
        PolygonalMap2.Node nodeSplit;

        Intersection(PolygonalMap2.Edge edge0, PolygonalMap2.Edge edge1x, float float0, float float1, float float2, float float3) {
            this.edge1 = edge0;
            this.edge2 = edge1x;
            this.dist1 = float0;
            this.dist2 = float1;
            this.nodeSplit = PolygonalMap2.Node.alloc().init(float2, float3, edge0.node1.z);
        }

        Intersection(PolygonalMap2.Edge edge0, PolygonalMap2.Edge edge1x, float float0, float float1, PolygonalMap2.Node node) {
            this.edge1 = edge0;
            this.edge2 = edge1x;
            this.dist1 = float0;
            this.dist2 = float1;
            this.nodeSplit = node;
        }

        PolygonalMap2.Edge split(PolygonalMap2.Edge edge) {
            return edge.split(this.nodeSplit);
        }
    }

    static final class L_lineSegmentIntersects {
        static final Vector2 v1 = new Vector2();
    }

    private static final class L_render {
        static final Vector2f vector2f = new Vector2f();
    }

    public static final class LiangBarsky {
        private final double[] p = new double[4];
        private final double[] q = new double[4];

        public boolean lineRectIntersect(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
            return this.lineRectIntersect(float0, float1, float2, float3, float4, float5, float6, float7, null);
        }

        public boolean lineRectIntersect(
            float float2, float float5, float float0, float float1, float float3, float float6, float float4, float float7, double[] doubles
        ) {
            this.p[0] = -float0;
            this.p[1] = float0;
            this.p[2] = -float1;
            this.p[3] = float1;
            this.q[0] = float2 - float3;
            this.q[1] = float4 - float2;
            this.q[2] = float5 - float6;
            this.q[3] = float7 - float5;
            double double0 = 0.0;
            double double1 = 1.0;

            for (int int0 = 0; int0 < 4; int0++) {
                if (this.p[int0] == 0.0) {
                    if (this.q[int0] < 0.0) {
                        return false;
                    }
                } else {
                    double double2 = this.q[int0] / this.p[int0];
                    if (this.p[int0] < 0.0 && double0 < double2) {
                        double0 = double2;
                    } else if (this.p[int0] > 0.0 && double1 > double2) {
                        double1 = double2;
                    }
                }
            }

            if (double0 >= double1) {
                return false;
            } else {
                if (doubles != null) {
                    doubles[0] = double0;
                    doubles[1] = double1;
                }

                return true;
            }
        }
    }

    private static final class LineClearCollide {
        final Vector2 perp = new Vector2();
        final ArrayList<PolygonalMap2.Point> pts = new ArrayList<>();
        final PolygonalMap2.VehicleRect sweepAABB = new PolygonalMap2.VehicleRect();
        final PolygonalMap2.VehicleRect vehicleAABB = new PolygonalMap2.VehicleRect();
        final Vector2[] polyVec = new Vector2[4];
        final Vector2[] vehicleVec = new Vector2[4];
        final PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
        final PolygonalMap2.LiangBarsky LB = new PolygonalMap2.LiangBarsky();

        LineClearCollide() {
            for (int int0 = 0; int0 < 4; int0++) {
                this.polyVec[int0] = new Vector2();
                this.vehicleVec[int0] = new Vector2();
            }
        }

        private float clamp(float float0, float float1, float float2) {
            if (float0 < float1) {
                float0 = float1;
            }

            if (float0 > float2) {
                float0 = float2;
            }

            return float0;
        }

        @Deprecated
        boolean canStandAt(PolygonalMap2 polygonalMap2, float float4, float float3, float float2, float float1, float float0, PolygonalMap2.Vehicle vehicle1) {
            if (((int)float4 != (int)float2 || (int)float3 != (int)float1) && polygonalMap2.isBlockedInAllDirections((int)float2, (int)float1, (int)float0)) {
                return false;
            } else {
                int int0 = (int)Math.floor(float2 - 0.3F);
                int int1 = (int)Math.floor(float1 - 0.3F);
                int int2 = (int)Math.ceil(float2 + 0.3F);
                int int3 = (int)Math.ceil(float1 + 0.3F);

                for (int int4 = int1; int4 < int3; int4++) {
                    for (int int5 = int0; int5 < int2; int5++) {
                        PolygonalMap2.Square square = polygonalMap2.getSquare(int5, int4, (int)float0);
                        boolean boolean0 = float2 >= int5 && float1 >= int4 && float2 < int5 + 1 && float1 < int4 + 1;
                        boolean boolean1 = false;
                        if (!boolean0 && square != null && square.has(448)) {
                            boolean1 = float2 < square.x || float2 >= square.x + 1 || square.has(64) && float1 < square.y;
                        } else if (!boolean0 && square != null && square.has(56)) {
                            boolean1 = float1 < square.y || float1 >= square.y + 1 || square.has(8) && float2 < square.x;
                        }

                        if ((square == null || square.isReallySolid() || boolean1 || !square.has(512)) && boolean0) {
                            return false;
                        }
                    }
                }

                for (int int6 = 0; int6 < polygonalMap2.vehicles.size(); int6++) {
                    PolygonalMap2.Vehicle vehicle0 = polygonalMap2.vehicles.get(int6);
                    if (vehicle0 != vehicle1 && (int)vehicle0.polyPlusRadius.z == (int)float0 && vehicle0.polyPlusRadius.containsPoint(float2, float1)) {
                        return false;
                    }
                }

                return true;
            }
        }

        boolean canStandAtClipper(
            PolygonalMap2 polygonalMap2, float float4, float float3, float float2, float float1, float float0, PolygonalMap2.Vehicle vehicle, int int1
        ) {
            if (((int)float4 != (int)float2 || (int)float3 != (int)float1) && polygonalMap2.isBlockedInAllDirections((int)float2, (int)float1, (int)float0)) {
                return false;
            } else {
                PolygonalMap2.Chunk chunk = polygonalMap2.getChunkFromSquarePos((int)float2, (int)float1);
                if (chunk == null) {
                    return false;
                } else {
                    PolygonalMap2.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, (int)float0);

                    for (int int0 = 0; int0 < chunkDataZ.obstacles.size(); int0++) {
                        PolygonalMap2.Obstacle obstacle = chunkDataZ.obstacles.get(int0);
                        if ((vehicle == null || obstacle.vehicle != vehicle)
                            && obstacle.bounds.containsPoint(float2, float1)
                            && obstacle.isPointInside(float2, float1, int1)) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }

        float isLeft(float float3, float float1, float float5, float float0, float float2, float float4) {
            return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
        }

        boolean isPointInPolygon_WindingNumber(float float1, float float0, PolygonalMap2.VehiclePoly vehiclePoly) {
            this.polyVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
            this.polyVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
            this.polyVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
            this.polyVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
            int int0 = 0;

            for (int int1 = 0; int1 < 4; int1++) {
                Vector2 vector0 = this.polyVec[int1];
                Vector2 vector1 = int1 == 3 ? this.polyVec[0] : this.polyVec[int1 + 1];
                if (vector0.y <= float0) {
                    if (vector1.y > float0 && this.isLeft(vector0.x, vector0.y, vector1.x, vector1.y, float1, float0) > 0.0F) {
                        int0++;
                    }
                } else if (vector1.y <= float0 && this.isLeft(vector0.x, vector0.y, vector1.x, vector1.y, float1, float0) < 0.0F) {
                    int0--;
                }
            }

            return int0 != 0;
        }

        @Deprecated
        boolean isNotClearOld(PolygonalMap2 polygonalMap2, float float1, float float0, float float2, float float3, int int1, int int0) {
            boolean boolean0 = (int0 & 4) != 0;
            PolygonalMap2.Square square = polygonalMap2.getSquare((int)float1, (int)float0, int1);
            if (square != null && square.has(504)) {
                return true;
            } else if (!this.canStandAt(polygonalMap2, float1, float0, float2, float3, int1, null)) {
                return true;
            } else {
                float float4 = float3 - float0;
                float float5 = -(float2 - float1);
                this.perp.set(float4, float5);
                this.perp.normalize();
                float float6 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float7 = float0 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
                float float8 = float2 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float9 = float3 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
                this.perp.set(-float4, -float5);
                this.perp.normalize();
                float float10 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float11 = float0 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
                float float12 = float2 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float13 = float3 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;

                for (int int2 = 0; int2 < this.pts.size(); int2++) {
                    this.pointPool.release(this.pts.get(int2));
                }

                this.pts.clear();
                this.pts.add(this.pointPool.alloc().init((int)float1, (int)float0));
                if ((int)float1 != (int)float2 || (int)float0 != (int)float3) {
                    this.pts.add(this.pointPool.alloc().init((int)float2, (int)float3));
                }

                polygonalMap2.supercover(float6, float7, float8, float9, int1, this.pointPool, this.pts);
                polygonalMap2.supercover(float10, float11, float12, float13, int1, this.pointPool, this.pts);

                for (int int3 = 0; int3 < this.pts.size(); int3++) {
                    PolygonalMap2.Point point = this.pts.get(int3);
                    square = polygonalMap2.getSquare(point.x, point.y, int1);
                    if (boolean0 && square != null && square.cost > 0) {
                        return true;
                    }

                    if (square != null && !square.isReallySolid() && !square.has(504) && square.has(512)) {
                        if (square.isCollideW()) {
                            float float14 = 0.3F;
                            float float15 = 0.3F;
                            float float16 = 0.3F;
                            float float17 = 0.3F;
                            if (float1 < point.x && float2 < point.x) {
                                float14 = 0.0F;
                            } else if (float1 >= point.x && float2 >= point.x) {
                                float16 = 0.0F;
                            }

                            if (float0 < point.y && float3 < point.y) {
                                float15 = 0.0F;
                            } else if (float0 >= point.y + 1 && float3 >= point.y + 1) {
                                float17 = 0.0F;
                            }

                            if (this.LB
                                .lineRectIntersect(
                                    float1,
                                    float0,
                                    float2 - float1,
                                    float3 - float0,
                                    point.x - float14,
                                    point.y - float15,
                                    point.x + float16,
                                    point.y + 1.0F + float17
                                )) {
                                return true;
                            }
                        }

                        if (square.isCollideN()) {
                            float float18 = 0.3F;
                            float float19 = 0.3F;
                            float float20 = 0.3F;
                            float float21 = 0.3F;
                            if (float1 < point.x && float2 < point.x) {
                                float18 = 0.0F;
                            } else if (float1 >= point.x + 1 && float2 >= point.x + 1) {
                                float20 = 0.0F;
                            }

                            if (float0 < point.y && float3 < point.y) {
                                float19 = 0.0F;
                            } else if (float0 >= point.y && float3 >= point.y) {
                                float21 = 0.0F;
                            }

                            if (this.LB
                                .lineRectIntersect(
                                    float1,
                                    float0,
                                    float2 - float1,
                                    float3 - float0,
                                    point.x - float18,
                                    point.y - float19,
                                    point.x + 1.0F + float20,
                                    point.y + float21
                                )) {
                                return true;
                            }
                        }
                    } else {
                        float float22 = 0.3F;
                        float float23 = 0.3F;
                        float float24 = 0.3F;
                        float float25 = 0.3F;
                        if (float1 < point.x && float2 < point.x) {
                            float22 = 0.0F;
                        } else if (float1 >= point.x + 1 && float2 >= point.x + 1) {
                            float24 = 0.0F;
                        }

                        if (float0 < point.y && float3 < point.y) {
                            float23 = 0.0F;
                        } else if (float0 >= point.y + 1 && float3 >= point.y + 1) {
                            float25 = 0.0F;
                        }

                        if (this.LB
                            .lineRectIntersect(
                                float1,
                                float0,
                                float2 - float1,
                                float3 - float0,
                                point.x - float22,
                                point.y - float23,
                                point.x + 1.0F + float24,
                                point.y + 1.0F + float25
                            )) {
                            return true;
                        }
                    }
                }

                float float26 = BaseVehicle.PLUS_RADIUS;
                this.perp.set(float4, float5);
                this.perp.normalize();
                float6 = float1 + this.perp.x * float26;
                float7 = float0 + this.perp.y * float26;
                float8 = float2 + this.perp.x * float26;
                float9 = float3 + this.perp.y * float26;
                this.perp.set(-float4, -float5);
                this.perp.normalize();
                float10 = float1 + this.perp.x * float26;
                float11 = float0 + this.perp.y * float26;
                float12 = float2 + this.perp.x * float26;
                float13 = float3 + this.perp.y * float26;
                float float27 = Math.min(float6, Math.min(float8, Math.min(float10, float12)));
                float float28 = Math.min(float7, Math.min(float9, Math.min(float11, float13)));
                float float29 = Math.max(float6, Math.max(float8, Math.max(float10, float12)));
                float float30 = Math.max(float7, Math.max(float9, Math.max(float11, float13)));
                this.sweepAABB.init((int)float27, (int)float28, (int)Math.ceil(float29) - (int)float27, (int)Math.ceil(float30) - (int)float28, int1);
                this.polyVec[0].set(float6, float7);
                this.polyVec[1].set(float8, float9);
                this.polyVec[2].set(float12, float13);
                this.polyVec[3].set(float10, float11);

                for (int int4 = 0; int4 < polygonalMap2.vehicles.size(); int4++) {
                    PolygonalMap2.Vehicle vehicle = polygonalMap2.vehicles.get(int4);
                    PolygonalMap2.VehicleRect vehicleRect = vehicle.poly.getAABB(this.vehicleAABB);
                    if (vehicleRect.intersects(this.sweepAABB) && this.polyVehicleIntersect(vehicle.poly)) {
                        return true;
                    }
                }

                return false;
            }
        }

        boolean isNotClearClipper(PolygonalMap2 polygonalMap2, float float1, float float0, float float2, float float3, int int1, int int0) {
            boolean boolean0 = (int0 & 4) != 0;
            PolygonalMap2.Square square = polygonalMap2.getSquare((int)float1, (int)float0, int1);
            if (square != null && square.has(504)) {
                return true;
            } else if (!this.canStandAtClipper(polygonalMap2, float1, float0, float2, float3, int1, null, int0)) {
                return true;
            } else {
                float float4 = float1 / 10.0F;
                float float5 = float0 / 10.0F;
                float float6 = float2 / 10.0F;
                float float7 = float3 / 10.0F;
                double double0 = Math.abs(float6 - float4);
                double double1 = Math.abs(float7 - float5);
                int int2 = (int)Math.floor(float4);
                int int3 = (int)Math.floor(float5);
                int int4 = 1;
                byte byte0;
                double double2;
                if (double0 == 0.0) {
                    byte0 = 0;
                    double2 = Double.POSITIVE_INFINITY;
                } else if (float6 > float4) {
                    byte0 = 1;
                    int4 += (int)Math.floor(float6) - int2;
                    double2 = (Math.floor(float4) + 1.0 - float4) * double1;
                } else {
                    byte0 = -1;
                    int4 += int2 - (int)Math.floor(float6);
                    double2 = (float4 - Math.floor(float4)) * double1;
                }

                byte byte1;
                if (double1 == 0.0) {
                    byte1 = 0;
                    double2 -= Double.POSITIVE_INFINITY;
                } else if (float7 > float5) {
                    byte1 = 1;
                    int4 += (int)Math.floor(float7) - int3;
                    double2 -= (Math.floor(float5) + 1.0 - float5) * double0;
                } else {
                    byte1 = -1;
                    int4 += int3 - (int)Math.floor(float7);
                    double2 -= (float5 - Math.floor(float5)) * double0;
                }

                for (; int4 > 0; int4--) {
                    PolygonalMap2.Chunk chunk = PolygonalMap2.instance.getChunkFromChunkPos(int2, int3);
                    if (chunk != null) {
                        PolygonalMap2.ChunkDataZ chunkDataZ = chunk.collision.init(chunk, int1);
                        ArrayList arrayList = chunkDataZ.obstacles;

                        for (int int5 = 0; int5 < arrayList.size(); int5++) {
                            PolygonalMap2.Obstacle obstacle = (PolygonalMap2.Obstacle)arrayList.get(int5);
                            if (obstacle.lineSegmentIntersects(float1, float0, float2, float3)) {
                                return true;
                            }
                        }
                    }

                    if (double2 > 0.0) {
                        int3 += byte1;
                        double2 -= double0;
                    } else {
                        int2 += byte0;
                        double2 += double1;
                    }
                }

                return boolean0 && this.isNotClearCost(float1, float0, float2, float3, int1);
            }
        }

        boolean isNotClearCost(float float5, float float2, float float4, float float1, int int1) {
            float float0 = float1 - float2;
            float float3 = -(float4 - float5);
            this.perp.set(float0, float3);
            this.perp.normalize();
            float float6 = float5 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float7 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float float8 = float4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float9 = float1 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-float0, -float3);
            this.perp.normalize();
            float float10 = float5 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float11 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float float12 = float4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float13 = float1 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;

            for (int int0 = 0; int0 < this.pts.size(); int0++) {
                this.pointPool.release(this.pts.get(int0));
            }

            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)float5, (int)float2));
            if ((int)float5 != (int)float4 || (int)float2 != (int)float1) {
                this.pts.add(this.pointPool.alloc().init((int)float4, (int)float1));
            }

            PolygonalMap2.instance.supercover(float6, float7, float8, float9, int1, this.pointPool, this.pts);
            PolygonalMap2.instance.supercover(float10, float11, float12, float13, int1, this.pointPool, this.pts);

            for (int int2 = 0; int2 < this.pts.size(); int2++) {
                PolygonalMap2.Point point = this.pts.get(int2);
                PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(point.x, point.y, int1);
                if (square != null && square.cost > 0) {
                    return true;
                }
            }

            return false;
        }

        boolean isNotClear(PolygonalMap2 polygonalMap2, float float0, float float1, float float2, float float3, int int0, int int1) {
            return this.isNotClearOld(polygonalMap2, float0, float1, float2, float3, int0, int1);
        }

        boolean polyVehicleIntersect(PolygonalMap2.VehiclePoly vehiclePoly) {
            this.vehicleVec[0].set(vehiclePoly.x1, vehiclePoly.y1);
            this.vehicleVec[1].set(vehiclePoly.x2, vehiclePoly.y2);
            this.vehicleVec[2].set(vehiclePoly.x3, vehiclePoly.y3);
            this.vehicleVec[3].set(vehiclePoly.x4, vehiclePoly.y4);
            boolean boolean0 = false;

            for (int int0 = 0; int0 < 4; int0++) {
                Vector2 vector0 = this.polyVec[int0];
                Vector2 vector1 = int0 == 3 ? this.polyVec[0] : this.polyVec[int0 + 1];

                for (int int1 = 0; int1 < 4; int1++) {
                    Vector2 vector2 = this.vehicleVec[int1];
                    Vector2 vector3 = int1 == 3 ? this.vehicleVec[0] : this.vehicleVec[int1 + 1];
                    if (Line2D.linesIntersect(vector0.x, vector0.y, vector1.x, vector1.y, vector2.x, vector2.y, vector3.x, vector3.y)) {
                        boolean0 = true;
                    }
                }
            }

            return boolean0;
        }
    }

    private static final class LineClearCollideMain {
        final Vector2 perp = new Vector2();
        final ArrayList<PolygonalMap2.Point> pts = new ArrayList<>();
        final PolygonalMap2.VehicleRect sweepAABB = new PolygonalMap2.VehicleRect();
        final PolygonalMap2.VehicleRect vehicleAABB = new PolygonalMap2.VehicleRect();
        final PolygonalMap2.VehiclePoly vehiclePoly = new PolygonalMap2.VehiclePoly();
        final Vector2[] polyVec = new Vector2[4];
        final Vector2[] vehicleVec = new Vector2[4];
        final PolygonalMap2.PointPool pointPool = new PolygonalMap2.PointPool();
        final PolygonalMap2.LiangBarsky LB = new PolygonalMap2.LiangBarsky();

        LineClearCollideMain() {
            for (int int0 = 0; int0 < 4; int0++) {
                this.polyVec[int0] = new Vector2();
                this.vehicleVec[int0] = new Vector2();
            }
        }

        private float clamp(float float0, float float1, float float2) {
            if (float0 < float1) {
                float0 = float1;
            }

            if (float0 > float2) {
                float0 = float2;
            }

            return float0;
        }

        @Deprecated
        boolean canStandAtOld(PolygonalMap2 var1, float float0, float float1, float float2, BaseVehicle vehicle1, int int0) {
            boolean boolean0 = (int0 & 1) != 0;
            boolean boolean1 = (int0 & 2) != 0;
            int int1 = (int)Math.floor(float0 - 0.3F);
            int int2 = (int)Math.floor(float1 - 0.3F);
            int int3 = (int)Math.ceil(float0 + 0.3F);
            int int4 = (int)Math.ceil(float1 + 0.3F);

            for (int int5 = int2; int5 < int4; int5++) {
                for (int int6 = int1; int6 < int3; int6++) {
                    boolean boolean2 = float0 >= int6 && float1 >= int5 && float0 < int6 + 1 && float1 < int5 + 1;
                    IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(int6, int5, (int)float2);
                    boolean boolean3 = false;
                    if (!boolean2 && square != null && square.HasStairsNorth()) {
                        boolean3 = float0 < square.x || float0 >= square.x + 1 || square.Has(IsoObjectType.stairsTN) && float1 < square.y;
                    } else if (!boolean2 && square != null && square.HasStairsWest()) {
                        boolean3 = float1 < square.y || float1 >= square.y + 1 || square.Has(IsoObjectType.stairsTW) && float0 < square.x;
                    }

                    if (square != null
                        && !square.isSolid()
                        && (!square.isSolidTrans() || square.isAdjacentToWindow())
                        && !boolean3
                        && (square.SolidFloorCached ? square.SolidFloor : square.TreatAsSolidFloor())) {
                        if (!boolean1) {
                            if (square.Is(IsoFlagType.collideW) || !boolean0 && square.hasBlockedDoor(false)) {
                                float float3 = int6;
                                float float4 = this.clamp(float1, int5, int5 + 1);
                                float float5 = float0 - float3;
                                float float6 = float1 - float4;
                                float float7 = float5 * float5 + float6 * float6;
                                if (float7 < 0.09F) {
                                    return false;
                                }
                            }

                            if (square.Is(IsoFlagType.collideN) || !boolean0 && square.hasBlockedDoor(true)) {
                                float float8 = this.clamp(float0, int6, int6 + 1);
                                float float9 = int5;
                                float float10 = float0 - float8;
                                float float11 = float1 - float9;
                                float float12 = float10 * float10 + float11 * float11;
                                if (float12 < 0.09F) {
                                    return false;
                                }
                            }
                        }
                    } else if (boolean1) {
                        if (boolean2) {
                            return false;
                        }
                    } else {
                        float float13 = this.clamp(float0, int6, int6 + 1);
                        float float14 = this.clamp(float1, int5, int5 + 1);
                        float float15 = float0 - float13;
                        float float16 = float1 - float14;
                        float float17 = float15 * float15 + float16 * float16;
                        if (float17 < 0.09F) {
                            return false;
                        }
                    }
                }
            }

            int int7 = ((int)float0 - 4) / 10 - 1;
            int int8 = ((int)float1 - 4) / 10 - 1;
            int int9 = (int)Math.ceil((float0 + 4.0F) / 10.0F) + 1;
            int int10 = (int)Math.ceil((float1 + 4.0F) / 10.0F) + 1;

            for (int int11 = int8; int11 < int10; int11++) {
                for (int int12 = int7; int12 < int9; int12++) {
                    IsoChunk chunk = GameServer.bServer
                        ? ServerMap.instance.getChunk(int12, int11)
                        : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int12 * 10, int11 * 10, 0);
                    if (chunk != null) {
                        for (int int13 = 0; int13 < chunk.vehicles.size(); int13++) {
                            BaseVehicle vehicle0 = chunk.vehicles.get(int13);
                            if (vehicle0 != vehicle1
                                && vehicle0.addedToWorld
                                && (int)vehicle0.z == (int)float2
                                && vehicle0.getPolyPlusRadius().containsPoint(float0, float1)) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }

        boolean canStandAtClipper(PolygonalMap2 var1, float float0, float float1, float float2, BaseVehicle vehicle, int int0) {
            return PolygonalMap2.instance.collideWithObstaclesPoly.canStandAt(float0, float1, float2, vehicle, int0);
        }

        public void drawCircle(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7) {
            LineDrawer.DrawIsoCircle(float0, float1, float2, float3, 16, float4, float5, float6, float7);
        }

        boolean isNotClearOld(PolygonalMap2 polygonalMap2, float float1, float float0, float float3, float float2, int int1, BaseVehicle vehicle0, int int0) {
            boolean boolean0 = (int0 & 1) != 0;
            boolean boolean1 = (int0 & 2) != 0;
            boolean boolean2 = (int0 & 4) != 0;
            boolean boolean3 = (int0 & 8) != 0;
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float0, int1);
            if (square != null && square.HasStairs()) {
                return !square.isSameStaircase((int)float3, (int)float2, int1);
            } else if (!this.canStandAtOld(polygonalMap2, float3, float2, int1, vehicle0, int0)) {
                if (boolean3) {
                    this.drawCircle(float3, float2, int1, 0.3F, 1.0F, 0.0F, 0.0F, 1.0F);
                }

                return true;
            } else {
                float float4 = float2 - float0;
                float float5 = -(float3 - float1);
                this.perp.set(float4, float5);
                this.perp.normalize();
                float float6 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float7 = float0 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
                float float8 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float9 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
                this.perp.set(-float4, -float5);
                this.perp.normalize();
                float float10 = float1 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float11 = float0 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
                float float12 = float3 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
                float float13 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;

                for (int int2 = 0; int2 < this.pts.size(); int2++) {
                    this.pointPool.release(this.pts.get(int2));
                }

                this.pts.clear();
                this.pts.add(this.pointPool.alloc().init((int)float1, (int)float0));
                if ((int)float1 != (int)float3 || (int)float0 != (int)float2) {
                    this.pts.add(this.pointPool.alloc().init((int)float3, (int)float2));
                }

                polygonalMap2.supercover(float6, float7, float8, float9, int1, this.pointPool, this.pts);
                polygonalMap2.supercover(float10, float11, float12, float13, int1, this.pointPool, this.pts);
                if (boolean3) {
                    for (int int3 = 0; int3 < this.pts.size(); int3++) {
                        PolygonalMap2.Point point0 = this.pts.get(int3);
                        LineDrawer.addLine(point0.x, point0.y, int1, point0.x + 1.0F, point0.y + 1.0F, int1, 1.0F, 1.0F, 0.0F, null, false);
                    }
                }

                boolean boolean4 = false;

                for (int int4 = 0; int4 < this.pts.size(); int4++) {
                    PolygonalMap2.Point point1 = this.pts.get(int4);
                    square = IsoWorld.instance.CurrentCell.getGridSquare(point1.x, point1.y, int1);
                    if (boolean2 && square != null && PolygonalMap2.SquareUpdateTask.getCost(square) > 0) {
                        return true;
                    }

                    if (square != null
                        && !square.isSolid()
                        && (!square.isSolidTrans() || square.isAdjacentToWindow())
                        && !square.HasStairs()
                        && (square.SolidFloorCached ? square.SolidFloor : square.TreatAsSolidFloor())) {
                        if (square.Is(IsoFlagType.collideW) || !boolean0 && square.hasBlockedDoor(false)) {
                            float float14 = 0.3F;
                            float float15 = 0.3F;
                            float float16 = 0.3F;
                            float float17 = 0.3F;
                            if (float1 < point1.x && float3 < point1.x) {
                                float14 = 0.0F;
                            } else if (float1 >= point1.x && float3 >= point1.x) {
                                float16 = 0.0F;
                            }

                            if (float0 < point1.y && float2 < point1.y) {
                                float15 = 0.0F;
                            } else if (float0 >= point1.y + 1 && float2 >= point1.y + 1) {
                                float17 = 0.0F;
                            }

                            if (this.LB
                                .lineRectIntersect(
                                    float1,
                                    float0,
                                    float3 - float1,
                                    float2 - float0,
                                    point1.x - float14,
                                    point1.y - float15,
                                    point1.x + float16,
                                    point1.y + 1.0F + float17
                                )) {
                                if (!boolean3) {
                                    return true;
                                }

                                LineDrawer.addLine(
                                    point1.x - float14,
                                    point1.y - float15,
                                    int1,
                                    point1.x + float16,
                                    point1.y + 1.0F + float17,
                                    int1,
                                    1.0F,
                                    0.0F,
                                    0.0F,
                                    null,
                                    false
                                );
                                boolean4 = true;
                            }
                        }

                        if (square.Is(IsoFlagType.collideN) || !boolean0 && square.hasBlockedDoor(true)) {
                            float float18 = 0.3F;
                            float float19 = 0.3F;
                            float float20 = 0.3F;
                            float float21 = 0.3F;
                            if (float1 < point1.x && float3 < point1.x) {
                                float18 = 0.0F;
                            } else if (float1 >= point1.x + 1 && float3 >= point1.x + 1) {
                                float20 = 0.0F;
                            }

                            if (float0 < point1.y && float2 < point1.y) {
                                float19 = 0.0F;
                            } else if (float0 >= point1.y && float2 >= point1.y) {
                                float21 = 0.0F;
                            }

                            if (this.LB
                                .lineRectIntersect(
                                    float1,
                                    float0,
                                    float3 - float1,
                                    float2 - float0,
                                    point1.x - float18,
                                    point1.y - float19,
                                    point1.x + 1.0F + float20,
                                    point1.y + float21
                                )) {
                                if (!boolean3) {
                                    return true;
                                }

                                LineDrawer.addLine(
                                    point1.x - float18,
                                    point1.y - float19,
                                    int1,
                                    point1.x + 1.0F + float20,
                                    point1.y + float21,
                                    int1,
                                    1.0F,
                                    0.0F,
                                    0.0F,
                                    null,
                                    false
                                );
                                boolean4 = true;
                            }
                        }
                    } else {
                        float float22 = 0.3F;
                        float float23 = 0.3F;
                        float float24 = 0.3F;
                        float float25 = 0.3F;
                        if (float1 < point1.x && float3 < point1.x) {
                            float22 = 0.0F;
                        } else if (float1 >= point1.x + 1 && float3 >= point1.x + 1) {
                            float24 = 0.0F;
                        }

                        if (float0 < point1.y && float2 < point1.y) {
                            float23 = 0.0F;
                        } else if (float0 >= point1.y + 1 && float2 >= point1.y + 1) {
                            float25 = 0.0F;
                        }

                        if (this.LB
                            .lineRectIntersect(
                                float1,
                                float0,
                                float3 - float1,
                                float2 - float0,
                                point1.x - float22,
                                point1.y - float23,
                                point1.x + 1.0F + float24,
                                point1.y + 1.0F + float25
                            )) {
                            if (!boolean3) {
                                return true;
                            }

                            LineDrawer.addLine(
                                point1.x - float22,
                                point1.y - float23,
                                int1,
                                point1.x + 1.0F + float24,
                                point1.y + 1.0F + float25,
                                int1,
                                1.0F,
                                0.0F,
                                0.0F,
                                null,
                                false
                            );
                            boolean4 = true;
                        }
                    }
                }

                float float26 = BaseVehicle.PLUS_RADIUS;
                this.perp.set(float4, float5);
                this.perp.normalize();
                float6 = float1 + this.perp.x * float26;
                float7 = float0 + this.perp.y * float26;
                float8 = float3 + this.perp.x * float26;
                float9 = float2 + this.perp.y * float26;
                this.perp.set(-float4, -float5);
                this.perp.normalize();
                float10 = float1 + this.perp.x * float26;
                float11 = float0 + this.perp.y * float26;
                float12 = float3 + this.perp.x * float26;
                float13 = float2 + this.perp.y * float26;
                float float27 = Math.min(float6, Math.min(float8, Math.min(float10, float12)));
                float float28 = Math.min(float7, Math.min(float9, Math.min(float11, float13)));
                float float29 = Math.max(float6, Math.max(float8, Math.max(float10, float12)));
                float float30 = Math.max(float7, Math.max(float9, Math.max(float11, float13)));
                this.sweepAABB.init((int)float27, (int)float28, (int)Math.ceil(float29) - (int)float27, (int)Math.ceil(float30) - (int)float28, int1);
                this.polyVec[0].set(float6, float7);
                this.polyVec[1].set(float8, float9);
                this.polyVec[2].set(float12, float13);
                this.polyVec[3].set(float10, float11);
                int int5 = this.sweepAABB.left() / 10 - 1;
                int int6 = this.sweepAABB.top() / 10 - 1;
                int int7 = (int)Math.ceil(this.sweepAABB.right() / 10.0F) + 1;
                int int8 = (int)Math.ceil(this.sweepAABB.bottom() / 10.0F) + 1;

                for (int int9 = int6; int9 < int8; int9++) {
                    for (int int10 = int5; int10 < int7; int10++) {
                        IsoChunk chunk = GameServer.bServer
                            ? ServerMap.instance.getChunk(int10, int9)
                            : IsoWorld.instance.CurrentCell.getChunkForGridSquare(int10 * 10, int9 * 10, 0);
                        if (chunk != null) {
                            for (int int11 = 0; int11 < chunk.vehicles.size(); int11++) {
                                BaseVehicle vehicle1 = chunk.vehicles.get(int11);
                                if (vehicle1 != vehicle0 && vehicle1.VehicleID != -1) {
                                    this.vehiclePoly.init(vehicle1.getPoly());
                                    this.vehiclePoly.getAABB(this.vehicleAABB);
                                    if (this.vehicleAABB.intersects(this.sweepAABB) && this.polyVehicleIntersect(this.vehiclePoly, boolean3)) {
                                        boolean4 = true;
                                        if (!boolean3) {
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                return boolean4;
            }
        }

        boolean isNotClearClipper(PolygonalMap2 polygonalMap2, float float1, float float0, float float3, float float2, int int1, BaseVehicle vehicle, int int0) {
            boolean boolean0 = (int0 & 1) != 0;
            boolean boolean1 = (int0 & 2) != 0;
            boolean boolean2 = (int0 & 4) != 0;
            boolean boolean3 = (int0 & 8) != 0;
            IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare((int)float1, (int)float0, int1);
            if (square != null && square.HasStairs()) {
                return !square.isSameStaircase((int)float3, (int)float2, int1);
            } else if (!this.canStandAtClipper(polygonalMap2, float3, float2, int1, vehicle, int0)) {
                if (boolean3) {
                    this.drawCircle(float3, float2, int1, 0.3F, 1.0F, 0.0F, 0.0F, 1.0F);
                }

                return true;
            } else {
                return PolygonalMap2.instance.collideWithObstaclesPoly.isNotClear(float1, float0, float3, float2, int1, boolean3, vehicle, boolean0, boolean1);
            }
        }

        boolean isNotClear(PolygonalMap2 polygonalMap2, float float0, float float1, float float2, float float3, int int0, BaseVehicle vehicle, int int1) {
            return this.isNotClearOld(polygonalMap2, float0, float1, float2, float3, int0, vehicle, int1);
        }

        Vector2 getCollidepoint(PolygonalMap2 polygonalMap2, float float5, float float2, float float4, float float1, int int2, BaseVehicle var7, int int0) {
            boolean boolean0 = (int0 & 1) != 0;
            boolean boolean1 = (int0 & 2) != 0;
            boolean boolean2 = (int0 & 4) != 0;
            boolean boolean3 = (int0 & 8) != 0;
            float float0 = float1 - float2;
            float float3 = -(float4 - float5);
            this.perp.set(float0, float3);
            this.perp.normalize();
            float float6 = float5 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float7 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float float8 = float4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float9 = float1 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            this.perp.set(-float0, -float3);
            this.perp.normalize();
            float float10 = float5 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float11 = float2 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;
            float float12 = float4 + this.perp.x * PolygonalMap2.RADIUS_DIAGONAL;
            float float13 = float1 + this.perp.y * PolygonalMap2.RADIUS_DIAGONAL;

            for (int int1 = 0; int1 < this.pts.size(); int1++) {
                this.pointPool.release(this.pts.get(int1));
            }

            this.pts.clear();
            this.pts.add(this.pointPool.alloc().init((int)float5, (int)float2));
            if ((int)float5 != (int)float4 || (int)float2 != (int)float1) {
                this.pts.add(this.pointPool.alloc().init((int)float4, (int)float1));
            }

            polygonalMap2.supercover(float6, float7, float8, float9, int2, this.pointPool, this.pts);
            polygonalMap2.supercover(float10, float11, float12, float13, int2, this.pointPool, this.pts);
            this.pts
                .sort(
                    (point1x, point0x) -> (int)(
                        IsoUtils.DistanceManhatten(float5, float2, point1x.x, point1x.y) - IsoUtils.DistanceManhatten(float5, float2, point0x.x, point0x.y)
                    )
                );
            if (boolean3) {
                for (int int3 = 0; int3 < this.pts.size(); int3++) {
                    PolygonalMap2.Point point0 = this.pts.get(int3);
                    LineDrawer.addLine(point0.x, point0.y, int2, point0.x + 1.0F, point0.y + 1.0F, int2, 1.0F, 1.0F, 0.0F, null, false);
                }
            }

            for (int int4 = 0; int4 < this.pts.size(); int4++) {
                PolygonalMap2.Point point1 = this.pts.get(int4);
                IsoGridSquare square = IsoWorld.instance.CurrentCell.getGridSquare(point1.x, point1.y, int2);
                if (boolean2 && square != null && PolygonalMap2.SquareUpdateTask.getCost(square) > 0) {
                    return PolygonalMap2.temp.set(point1.x + 0.5F, point1.y + 0.5F);
                }

                if (square != null
                    && !square.isSolid()
                    && (!square.isSolidTrans() || square.isAdjacentToWindow())
                    && !square.HasStairs()
                    && (square.SolidFloorCached ? square.SolidFloor : square.TreatAsSolidFloor())) {
                    if (square.Is(IsoFlagType.collideW) || !boolean0 && square.hasBlockedDoor(false)) {
                        float float14 = 0.3F;
                        float float15 = 0.3F;
                        float float16 = 0.3F;
                        float float17 = 0.3F;
                        if (float5 < point1.x && float4 < point1.x) {
                            float14 = 0.0F;
                        } else if (float5 >= point1.x && float4 >= point1.x) {
                            float16 = 0.0F;
                        }

                        if (float2 < point1.y && float1 < point1.y) {
                            float15 = 0.0F;
                        } else if (float2 >= point1.y + 1 && float1 >= point1.y + 1) {
                            float17 = 0.0F;
                        }

                        if (this.LB
                            .lineRectIntersect(
                                float5,
                                float2,
                                float4 - float5,
                                float1 - float2,
                                point1.x - float14,
                                point1.y - float15,
                                point1.x + float16,
                                point1.y + 1.0F + float17
                            )) {
                            if (boolean3) {
                                LineDrawer.addLine(
                                    point1.x - float14,
                                    point1.y - float15,
                                    int2,
                                    point1.x + float16,
                                    point1.y + 1.0F + float17,
                                    int2,
                                    1.0F,
                                    0.0F,
                                    0.0F,
                                    null,
                                    false
                                );
                            }

                            return PolygonalMap2.temp.set(point1.x + (float5 - float4 < 0.0F ? -0.5F : 0.5F), point1.y + 0.5F);
                        }
                    }

                    if (square.Is(IsoFlagType.collideN) || !boolean0 && square.hasBlockedDoor(true)) {
                        float float18 = 0.3F;
                        float float19 = 0.3F;
                        float float20 = 0.3F;
                        float float21 = 0.3F;
                        if (float5 < point1.x && float4 < point1.x) {
                            float18 = 0.0F;
                        } else if (float5 >= point1.x + 1 && float4 >= point1.x + 1) {
                            float20 = 0.0F;
                        }

                        if (float2 < point1.y && float1 < point1.y) {
                            float19 = 0.0F;
                        } else if (float2 >= point1.y && float1 >= point1.y) {
                            float21 = 0.0F;
                        }

                        if (this.LB
                            .lineRectIntersect(
                                float5,
                                float2,
                                float4 - float5,
                                float1 - float2,
                                point1.x - float18,
                                point1.y - float19,
                                point1.x + 1.0F + float20,
                                point1.y + float21
                            )) {
                            if (boolean3) {
                                LineDrawer.addLine(
                                    point1.x - float18,
                                    point1.y - float19,
                                    int2,
                                    point1.x + 1.0F + float20,
                                    point1.y + float21,
                                    int2,
                                    1.0F,
                                    0.0F,
                                    0.0F,
                                    null,
                                    false
                                );
                            }

                            return PolygonalMap2.temp.set(point1.x + 0.5F, point1.y + (float2 - float1 < 0.0F ? -0.5F : 0.5F));
                        }
                    }
                } else {
                    float float22 = 0.3F;
                    float float23 = 0.3F;
                    float float24 = 0.3F;
                    float float25 = 0.3F;
                    if (float5 < point1.x && float4 < point1.x) {
                        float22 = 0.0F;
                    } else if (float5 >= point1.x + 1 && float4 >= point1.x + 1) {
                        float24 = 0.0F;
                    }

                    if (float2 < point1.y && float1 < point1.y) {
                        float23 = 0.0F;
                    } else if (float2 >= point1.y + 1 && float1 >= point1.y + 1) {
                        float25 = 0.0F;
                    }

                    if (this.LB
                        .lineRectIntersect(
                            float5,
                            float2,
                            float4 - float5,
                            float1 - float2,
                            point1.x - float22,
                            point1.y - float23,
                            point1.x + 1.0F + float24,
                            point1.y + 1.0F + float25
                        )) {
                        if (boolean3) {
                            LineDrawer.addLine(
                                point1.x - float22,
                                point1.y - float23,
                                int2,
                                point1.x + 1.0F + float24,
                                point1.y + 1.0F + float25,
                                int2,
                                1.0F,
                                0.0F,
                                0.0F,
                                null,
                                false
                            );
                        }

                        return PolygonalMap2.temp.set(point1.x + 0.5F, point1.y + 0.5F);
                    }
                }
            }

            return PolygonalMap2.temp.set(float4, float1);
        }

        boolean polyVehicleIntersect(PolygonalMap2.VehiclePoly vehiclePolyx, boolean boolean1) {
            this.vehicleVec[0].set(vehiclePolyx.x1, vehiclePolyx.y1);
            this.vehicleVec[1].set(vehiclePolyx.x2, vehiclePolyx.y2);
            this.vehicleVec[2].set(vehiclePolyx.x3, vehiclePolyx.y3);
            this.vehicleVec[3].set(vehiclePolyx.x4, vehiclePolyx.y4);
            boolean boolean0 = false;

            for (int int0 = 0; int0 < 4; int0++) {
                Vector2 vector0 = this.polyVec[int0];
                Vector2 vector1 = int0 == 3 ? this.polyVec[0] : this.polyVec[int0 + 1];

                for (int int1 = 0; int1 < 4; int1++) {
                    Vector2 vector2 = this.vehicleVec[int1];
                    Vector2 vector3 = int1 == 3 ? this.vehicleVec[0] : this.vehicleVec[int1 + 1];
                    if (Line2D.linesIntersect(vector0.x, vector0.y, vector1.x, vector1.y, vector2.x, vector2.y, vector3.x, vector3.y)) {
                        if (boolean1) {
                            LineDrawer.addLine(vector0.x, vector0.y, 0.0F, vector1.x, vector1.y, 0.0F, 1.0F, 0.0F, 0.0F, null, true);
                            LineDrawer.addLine(vector2.x, vector2.y, 0.0F, vector3.x, vector3.y, 0.0F, 1.0F, 0.0F, 0.0F, null, true);
                        }

                        boolean0 = true;
                    }
                }
            }

            return boolean0;
        }
    }

    private static final class Node {
        static int nextID = 1;
        final int ID;
        float x;
        float y;
        int z;
        boolean ignore;
        PolygonalMap2.Square square;
        ArrayList<PolygonalMap2.VisibilityGraph> graphs;
        final ArrayList<PolygonalMap2.Edge> edges = new ArrayList<>();
        final ArrayList<PolygonalMap2.Connection> visible = new ArrayList<>();
        int flags = 0;
        static final ArrayList<PolygonalMap2.Obstacle> tempObstacles = new ArrayList<>();
        static final ArrayDeque<PolygonalMap2.Node> pool = new ArrayDeque<>();

        Node() {
            this.ID = nextID++;
        }

        PolygonalMap2.Node init(float float0, float float1, int int0) {
            this.x = float0;
            this.y = float1;
            this.z = int0;
            this.ignore = false;
            this.square = null;
            if (this.graphs != null) {
                this.graphs.clear();
            }

            this.edges.clear();
            this.visible.clear();
            this.flags = 0;
            return this;
        }

        PolygonalMap2.Node init(PolygonalMap2.Square squarex) {
            this.x = squarex.x + 0.5F;
            this.y = squarex.y + 0.5F;
            this.z = squarex.z;
            this.ignore = false;
            this.square = squarex;
            if (this.graphs != null) {
                this.graphs.clear();
            }

            this.edges.clear();
            this.visible.clear();
            this.flags = 0;
            return this;
        }

        PolygonalMap2.Node setXY(float float0, float float1) {
            this.x = float0;
            this.y = float1;
            return this;
        }

        void addGraph(PolygonalMap2.VisibilityGraph visibilityGraph) {
            if (this.graphs == null) {
                this.graphs = new ArrayList<>();
            }

            assert !this.graphs.contains(visibilityGraph);

            this.graphs.add(visibilityGraph);
        }

        boolean sharesEdge(PolygonalMap2.Node node1) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                PolygonalMap2.Edge edge = this.edges.get(int0);
                if (edge.hasNode(node1)) {
                    return true;
                }
            }

            return false;
        }

        boolean sharesShape(PolygonalMap2.Node node1) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                PolygonalMap2.Edge edge0 = this.edges.get(int0);

                for (int int1 = 0; int1 < node1.edges.size(); int1++) {
                    PolygonalMap2.Edge edge1 = node1.edges.get(int1);
                    if (edge0.obstacle != null && edge0.obstacle == edge1.obstacle) {
                        return true;
                    }
                }
            }

            return false;
        }

        void getObstacles(ArrayList<PolygonalMap2.Obstacle> arrayList) {
            for (int int0 = 0; int0 < this.edges.size(); int0++) {
                PolygonalMap2.Edge edge = this.edges.get(int0);
                if (!arrayList.contains(edge.obstacle)) {
                    arrayList.add(edge.obstacle);
                }
            }
        }

        boolean onSameShapeButDoesNotShareAnEdge(PolygonalMap2.Node node1) {
            tempObstacles.clear();
            this.getObstacles(tempObstacles);

            for (int int0 = 0; int0 < tempObstacles.size(); int0++) {
                PolygonalMap2.Obstacle obstacle = tempObstacles.get(int0);
                if (obstacle.hasNode(node1) && !obstacle.hasAdjacentNodes(this, node1)) {
                    return true;
                }
            }

            return false;
        }

        boolean hasFlag(int int0) {
            return (this.flags & int0) != 0;
        }

        boolean isConnectedTo(PolygonalMap2.Node node1) {
            if (this.hasFlag(4)) {
                return true;
            } else {
                for (int int0 = 0; int0 < this.visible.size(); int0++) {
                    PolygonalMap2.Connection connection = this.visible.get(int0);
                    if (connection.node1 == node1 || connection.node2 == node1) {
                        return true;
                    }
                }

                return false;
            }
        }

        static PolygonalMap2.Node alloc() {
            if (pool.isEmpty()) {
                boolean boolean0 = false;
            } else {
                boolean boolean1 = false;
            }

            return pool.isEmpty() ? new PolygonalMap2.Node() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            for (int int0 = this.visible.size() - 1; int0 >= 0; int0--) {
                PolygonalMap2.instance.breakConnection(this.visible.get(int0));
            }

            pool.push(this);
        }

        static void releaseAll(ArrayList<PolygonalMap2.Node> arrayList) {
            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                ((PolygonalMap2.Node)arrayList.get(int0)).release();
            }
        }
    }

    @Deprecated
    private static final class ObjectOutline {
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
        ArrayList<PolygonalMap2.Node> nodes;
        static final ArrayDeque<PolygonalMap2.ObjectOutline> pool = new ArrayDeque<>();

        PolygonalMap2.ObjectOutline init(int int0, int int1, int int2) {
            this.x = int0;
            this.y = int1;
            this.z = int2;
            this.nw = this.nw_w = this.nw_n = this.nw_e = false;
            this.w_w = this.w_e = this.w_cutoff = false;
            this.n_n = this.n_s = this.n_cutoff = false;
            return this;
        }

        static void setSolid(int int0, int int1, int int2, PolygonalMap2.ObjectOutline[][] objectOutlines) {
            setWest(int0, int1, int2, objectOutlines);
            setNorth(int0, int1, int2, objectOutlines);
            setWest(int0 + 1, int1, int2, objectOutlines);
            setNorth(int0, int1 + 1, int2, objectOutlines);
        }

        static void setWest(int int0, int int1, int int2, PolygonalMap2.ObjectOutline[][] objectOutlines) {
            PolygonalMap2.ObjectOutline objectOutline = get(int0, int1, int2, objectOutlines);
            if (objectOutline != null) {
                if (objectOutline.nw) {
                    objectOutline.nw_s = false;
                } else {
                    objectOutline.nw = true;
                    objectOutline.nw_w = true;
                    objectOutline.nw_n = true;
                    objectOutline.nw_e = true;
                    objectOutline.nw_s = false;
                }

                objectOutline.w_w = true;
                objectOutline.w_e = true;
            }

            objectOutline = get(int0, int1 + 1, int2, objectOutlines);
            if (objectOutline == null) {
                if (objectOutline != null) {
                    objectOutline.w_cutoff = true;
                }
            } else if (objectOutline.nw) {
                objectOutline.nw_n = false;
            } else {
                objectOutline.nw = true;
                objectOutline.nw_n = false;
                objectOutline.nw_w = true;
                objectOutline.nw_e = true;
                objectOutline.nw_s = true;
            }
        }

        static void setNorth(int int0, int int1, int int2, PolygonalMap2.ObjectOutline[][] objectOutlines) {
            PolygonalMap2.ObjectOutline objectOutline = get(int0, int1, int2, objectOutlines);
            if (objectOutline != null) {
                if (objectOutline.nw) {
                    objectOutline.nw_e = false;
                } else {
                    objectOutline.nw = true;
                    objectOutline.nw_w = true;
                    objectOutline.nw_n = true;
                    objectOutline.nw_e = false;
                    objectOutline.nw_s = true;
                }

                objectOutline.n_n = true;
                objectOutline.n_s = true;
            }

            objectOutline = get(int0 + 1, int1, int2, objectOutlines);
            if (objectOutline == null) {
                if (objectOutline != null) {
                    objectOutline.n_cutoff = true;
                }
            } else if (objectOutline.nw) {
                objectOutline.nw_w = false;
            } else {
                objectOutline.nw = true;
                objectOutline.nw_n = true;
                objectOutline.nw_w = false;
                objectOutline.nw_e = true;
                objectOutline.nw_s = true;
            }
        }

        static PolygonalMap2.ObjectOutline get(int int0, int int1, int int2, PolygonalMap2.ObjectOutline[][] objectOutlines) {
            if (int0 < 0 || int0 >= objectOutlines.length) {
                return null;
            } else if (int1 >= 0 && int1 < objectOutlines[0].length) {
                if (objectOutlines[int0][int1] == null) {
                    objectOutlines[int0][int1] = alloc().init(int0, int1, int2);
                }

                return objectOutlines[int0][int1];
            } else {
                return null;
            }
        }

        void trace_NW_N(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x + 0.3F, this.y - 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x + 0.3F, this.y - 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.nw_n = false;
            if (this.nw_e) {
                this.trace_NW_E(objectOutlines, null);
            } else if (this.n_n) {
                this.trace_N_N(objectOutlines, this.nodes.get(this.nodes.size() - 1));
            }
        }

        void trace_NW_S(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x - 0.3F, this.y + 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x - 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.nw_s = false;
            if (this.nw_w) {
                this.trace_NW_W(objectOutlines, null);
            } else {
                PolygonalMap2.ObjectOutline objectOutline1 = get(this.x - 1, this.y, this.z, objectOutlines);
                if (objectOutline1 == null) {
                    return;
                }

                if (objectOutline1.n_s) {
                    objectOutline1.nodes = this.nodes;
                    objectOutline1.trace_N_S(objectOutlines, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }

        void trace_NW_W(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x - 0.3F, this.y - 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x - 0.3F, this.y - 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.nw_w = false;
            if (this.nw_n) {
                this.trace_NW_N(objectOutlines, null);
            } else {
                PolygonalMap2.ObjectOutline objectOutline1 = get(this.x, this.y - 1, this.z, objectOutlines);
                if (objectOutline1 == null) {
                    return;
                }

                if (objectOutline1.w_w) {
                    objectOutline1.nodes = this.nodes;
                    objectOutline1.trace_W_W(objectOutlines, this.nodes.get(this.nodes.size() - 1));
                }
            }
        }

        void trace_NW_E(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x + 0.3F, this.y + 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x + 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.nw_e = false;
            if (this.nw_s) {
                this.trace_NW_S(objectOutlines, null);
            } else if (this.w_e) {
                this.trace_W_E(objectOutlines, this.nodes.get(this.nodes.size() - 1));
            }
        }

        void trace_W_E(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x + 0.3F, this.y + 1 - 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x + 0.3F, this.y + 1 - 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.w_e = false;
            if (this.w_cutoff) {
                PolygonalMap2.Node node2 = this.nodes.get(this.nodes.size() - 1);
                node2.setXY(this.x + 0.3F, this.y + 1 + 0.3F);
                node2 = PolygonalMap2.Node.alloc().init(this.x - 0.3F, this.y + 1 + 0.3F, this.z);
                this.nodes.add(node2);
                node2 = PolygonalMap2.Node.alloc().init(this.x - 0.3F, this.y + 1 - 0.3F, this.z);
                this.nodes.add(node2);
                this.trace_W_W(objectOutlines, node2);
            } else {
                PolygonalMap2.ObjectOutline objectOutline1 = get(this.x, this.y + 1, this.z, objectOutlines);
                if (objectOutline1 != null) {
                    if (objectOutline1.nw && objectOutline1.nw_e) {
                        objectOutline1.nodes = this.nodes;
                        objectOutline1.trace_NW_E(objectOutlines, this.nodes.get(this.nodes.size() - 1));
                    } else if (objectOutline1.n_n) {
                        objectOutline1.nodes = this.nodes;
                        objectOutline1.trace_N_N(objectOutlines, null);
                    }
                }
            }
        }

        void trace_W_W(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x - 0.3F, this.y + 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x - 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.w_w = false;
            if (this.nw_w) {
                this.trace_NW_W(objectOutlines, this.nodes.get(this.nodes.size() - 1));
            } else {
                PolygonalMap2.ObjectOutline objectOutline1 = get(this.x - 1, this.y, this.z, objectOutlines);
                if (objectOutline1 == null) {
                    return;
                }

                if (objectOutline1.n_s) {
                    objectOutline1.nodes = this.nodes;
                    objectOutline1.trace_N_S(objectOutlines, null);
                }
            }
        }

        void trace_N_N(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x + 1 - 0.3F, this.y - 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x + 1 - 0.3F, this.y - 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.n_n = false;
            if (this.n_cutoff) {
                PolygonalMap2.Node node2 = this.nodes.get(this.nodes.size() - 1);
                node2.setXY(this.x + 1 + 0.3F, this.y - 0.3F);
                node2 = PolygonalMap2.Node.alloc().init(this.x + 1 + 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(node2);
                node2 = PolygonalMap2.Node.alloc().init(this.x + 1 - 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(node2);
                this.trace_N_S(objectOutlines, node2);
            } else {
                PolygonalMap2.ObjectOutline objectOutline1 = get(this.x + 1, this.y, this.z, objectOutlines);
                if (objectOutline1 != null) {
                    if (objectOutline1.nw_n) {
                        objectOutline1.nodes = this.nodes;
                        objectOutline1.trace_NW_N(objectOutlines, this.nodes.get(this.nodes.size() - 1));
                    } else {
                        objectOutline1 = get(this.x + 1, this.y - 1, this.z, objectOutlines);
                        if (objectOutline1 == null) {
                            return;
                        }

                        if (objectOutline1.w_w) {
                            objectOutline1.nodes = this.nodes;
                            objectOutline1.trace_W_W(objectOutlines, null);
                        }
                    }
                }
            }
        }

        void trace_N_S(PolygonalMap2.ObjectOutline[][] objectOutlines, PolygonalMap2.Node node0) {
            if (node0 != null) {
                node0.setXY(this.x + 0.3F, this.y + 0.3F);
            } else {
                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.x + 0.3F, this.y + 0.3F, this.z);
                this.nodes.add(node1);
            }

            this.n_s = false;
            if (this.nw_s) {
                this.trace_NW_S(objectOutlines, this.nodes.get(this.nodes.size() - 1));
            } else if (this.w_e) {
                this.trace_W_E(objectOutlines, null);
            }
        }

        void trace(PolygonalMap2.ObjectOutline[][] objectOutlines, ArrayList<PolygonalMap2.Node> arrayList) {
            arrayList.clear();
            this.nodes = arrayList;
            PolygonalMap2.Node node = PolygonalMap2.Node.alloc().init(this.x - 0.3F, this.y - 0.3F, this.z);
            arrayList.add(node);
            this.trace_NW_N(objectOutlines, null);
            if (arrayList.size() != 2
                && node.x == ((PolygonalMap2.Node)arrayList.get(arrayList.size() - 1)).x
                && node.y == ((PolygonalMap2.Node)arrayList.get(arrayList.size() - 1)).y) {
                ((PolygonalMap2.Node)arrayList.get(arrayList.size() - 1)).release();
                arrayList.set(arrayList.size() - 1, node);
            } else {
                arrayList.clear();
            }
        }

        static PolygonalMap2.ObjectOutline alloc() {
            return pool.isEmpty() ? new PolygonalMap2.ObjectOutline() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class Obstacle {
        PolygonalMap2.Vehicle vehicle;
        final PolygonalMap2.EdgeRing outer = new PolygonalMap2.EdgeRing();
        final ArrayList<PolygonalMap2.EdgeRing> inner = new ArrayList<>();
        PolygonalMap2.ImmutableRectF bounds;
        PolygonalMap2.Node nodeCrawlFront;
        PolygonalMap2.Node nodeCrawlRear;
        final ArrayList<PolygonalMap2.Node> crawlNodes = new ArrayList<>();
        static final ArrayDeque<PolygonalMap2.Obstacle> pool = new ArrayDeque<>();

        PolygonalMap2.Obstacle init(PolygonalMap2.Vehicle vehiclex) {
            this.vehicle = vehiclex;
            this.outer.clear();
            this.inner.clear();
            this.nodeCrawlFront = this.nodeCrawlRear = null;
            this.crawlNodes.clear();
            return this;
        }

        PolygonalMap2.Obstacle init(IsoGridSquare var1) {
            this.vehicle = null;
            this.outer.clear();
            this.inner.clear();
            this.nodeCrawlFront = this.nodeCrawlRear = null;
            this.crawlNodes.clear();
            return this;
        }

        boolean hasNode(PolygonalMap2.Node node) {
            if (this.outer.hasNode(node)) {
                return true;
            } else {
                for (int int0 = 0; int0 < this.inner.size(); int0++) {
                    PolygonalMap2.EdgeRing edgeRing = this.inner.get(int0);
                    if (edgeRing.hasNode(node)) {
                        return true;
                    }
                }

                return false;
            }
        }

        boolean hasAdjacentNodes(PolygonalMap2.Node node0, PolygonalMap2.Node node1) {
            if (this.outer.hasAdjacentNodes(node0, node1)) {
                return true;
            } else {
                for (int int0 = 0; int0 < this.inner.size(); int0++) {
                    PolygonalMap2.EdgeRing edgeRing = this.inner.get(int0);
                    if (edgeRing.hasAdjacentNodes(node0, node1)) {
                        return true;
                    }
                }

                return false;
            }
        }

        boolean isPointInside(float float0, float float1, int int0) {
            if (this.outer.isPointInPolygon_WindingNumber(float0, float1, int0) != PolygonalMap2.EdgeRingHit.Inside) {
                return false;
            } else if (this.inner.isEmpty()) {
                return true;
            } else {
                for (int int1 = 0; int1 < this.inner.size(); int1++) {
                    PolygonalMap2.EdgeRing edgeRing = this.inner.get(int1);
                    if (edgeRing.isPointInPolygon_WindingNumber(float0, float1, int0) != PolygonalMap2.EdgeRingHit.Outside) {
                        return false;
                    }
                }

                return true;
            }
        }

        boolean isPointInside(float float0, float float1) {
            byte byte0 = 0;
            return this.isPointInside(float0, float1, byte0);
        }

        boolean lineSegmentIntersects(float float0, float float1, float float2, float float3) {
            if (this.outer.lineSegmentIntersects(float0, float1, float2, float3)) {
                return true;
            } else {
                for (int int0 = 0; int0 < this.inner.size(); int0++) {
                    PolygonalMap2.EdgeRing edgeRing = this.inner.get(int0);
                    if (edgeRing.lineSegmentIntersects(float0, float1, float2, float3)) {
                        return true;
                    }
                }

                return false;
            }
        }

        boolean isNodeInsideOf(PolygonalMap2.Node node) {
            if (this.hasNode(node)) {
                return false;
            } else {
                return !this.bounds.containsPoint(node.x, node.y) ? false : this.isPointInside(node.x, node.y);
            }
        }

        void getClosestPointOnEdge(float float0, float float1, PolygonalMap2.ClosestPointOnEdge closestPointOnEdge) {
            closestPointOnEdge.edge = null;
            closestPointOnEdge.node = null;
            closestPointOnEdge.distSq = Double.MAX_VALUE;
            this.outer.getClosestPointOnEdge(float0, float1, closestPointOnEdge);

            for (int int0 = 0; int0 < this.inner.size(); int0++) {
                PolygonalMap2.EdgeRing edgeRing = this.inner.get(int0);
                edgeRing.getClosestPointOnEdge(float0, float1, closestPointOnEdge);
            }
        }

        boolean splitEdgeAtNearestPoint(
            PolygonalMap2.ClosestPointOnEdge closestPointOnEdge, int int0, PolygonalMap2.AdjustStartEndNodeData adjustStartEndNodeData
        ) {
            if (closestPointOnEdge.edge == null) {
                return false;
            } else {
                adjustStartEndNodeData.obstacle = this;
                if (closestPointOnEdge.node == null) {
                    adjustStartEndNodeData.node = PolygonalMap2.Node.alloc().init(closestPointOnEdge.point.x, closestPointOnEdge.point.y, int0);
                    adjustStartEndNodeData.newEdge = closestPointOnEdge.edge.split(adjustStartEndNodeData.node);
                    adjustStartEndNodeData.isNodeNew = true;
                } else {
                    adjustStartEndNodeData.node = closestPointOnEdge.node;
                    adjustStartEndNodeData.newEdge = null;
                    adjustStartEndNodeData.isNodeNew = false;
                }

                return true;
            }
        }

        void unsplit(PolygonalMap2.Node node, ArrayList<PolygonalMap2.Edge> arrayList) {
            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                PolygonalMap2.Edge edge0 = (PolygonalMap2.Edge)arrayList.get(int0);
                if (edge0.node1 == node) {
                    if (int0 > 0) {
                        PolygonalMap2.Edge edge1 = (PolygonalMap2.Edge)arrayList.get(int0 - 1);
                        edge1.node2 = edge0.node2;

                        assert edge0.node2.edges.contains(edge0);

                        edge0.node2.edges.remove(edge0);

                        assert !edge0.node2.edges.contains(edge1);

                        edge0.node2.edges.add(edge1);
                        PolygonalMap2.instance.connectTwoNodes(edge1.node1, edge1.node2);
                    } else {
                        ((PolygonalMap2.Edge)arrayList.get(int0 + 1)).node1 = ((PolygonalMap2.Edge)arrayList.get(arrayList.size() - 1)).node2;
                    }

                    edge0.release();
                    arrayList.remove(int0);
                    break;
                }
            }
        }

        void calcBounds() {
            float float0 = Float.MAX_VALUE;
            float float1 = Float.MAX_VALUE;
            float float2 = Float.MIN_VALUE;
            float float3 = Float.MIN_VALUE;

            for (int int0 = 0; int0 < this.outer.size(); int0++) {
                PolygonalMap2.Edge edge = this.outer.get(int0);
                float0 = Math.min(float0, edge.node1.x);
                float1 = Math.min(float1, edge.node1.y);
                float2 = Math.max(float2, edge.node1.x);
                float3 = Math.max(float3, edge.node1.y);
            }

            if (this.bounds != null) {
                this.bounds.release();
            }

            float float4 = 0.01F;
            this.bounds = PolygonalMap2.ImmutableRectF.alloc()
                .init(float0 - float4, float1 - float4, float2 - float0 + float4 * 2.0F, float3 - float1 + float4 * 2.0F);
        }

        void render(ArrayList<PolygonalMap2.Edge> arrayList, boolean boolean0) {
            if (!arrayList.isEmpty()) {
                float float0 = 0.0F;
                float float1 = boolean0 ? 1.0F : 0.5F;
                float float2 = boolean0 ? 0.0F : 0.5F;

                for (PolygonalMap2.Edge edge : arrayList) {
                    PolygonalMap2.Node node0 = edge.node1;
                    PolygonalMap2.Node node1 = edge.node2;
                    LineDrawer.addLine(node0.x, node0.y, node0.z, node1.x, node1.y, node1.z, float0, float1, float2, null, true);
                    Vector3f vector3f0 = new Vector3f(node1.x - node0.x, node1.y - node0.y, node1.z - node0.z).normalize();
                    Vector3f vector3f1 = new Vector3f(vector3f0).cross(0.0F, 0.0F, 1.0F).normalize();
                    vector3f0.mul(0.9F);
                    LineDrawer.addLine(
                        node1.x - vector3f0.x * 0.1F - vector3f1.x * 0.1F,
                        node1.y - vector3f0.y * 0.1F - vector3f1.y * 0.1F,
                        node1.z,
                        node1.x,
                        node1.y,
                        node1.z,
                        float0,
                        float1,
                        float2,
                        null,
                        true
                    );
                    LineDrawer.addLine(
                        node1.x - vector3f0.x * 0.1F + vector3f1.x * 0.1F,
                        node1.y - vector3f0.y * 0.1F + vector3f1.y * 0.1F,
                        node1.z,
                        node1.x,
                        node1.y,
                        node1.z,
                        float0,
                        float1,
                        float2,
                        null,
                        true
                    );
                    float0 = 1.0F - float0;
                }

                PolygonalMap2.Node node2 = ((PolygonalMap2.Edge)arrayList.get(0)).node1;
                LineDrawer.addLine(node2.x - 0.1F, node2.y - 0.1F, node2.z, node2.x + 0.1F, node2.y + 0.1F, node2.z, 1.0F, 0.0F, 0.0F, null, false);
            }
        }

        void render() {
            this.render(this.outer, true);

            for (int int0 = 0; int0 < this.inner.size(); int0++) {
                this.render(this.inner.get(int0), false);
            }
        }

        void connectCrawlNodes(PolygonalMap2.VisibilityGraph visibilityGraph, PolygonalMap2.Obstacle obstacle1) {
            this.connectCrawlNode(visibilityGraph, obstacle1, this.nodeCrawlFront, obstacle1.nodeCrawlFront);
            this.connectCrawlNode(visibilityGraph, obstacle1, this.nodeCrawlFront, obstacle1.nodeCrawlRear);
            this.connectCrawlNode(visibilityGraph, obstacle1, this.nodeCrawlRear, obstacle1.nodeCrawlFront);
            this.connectCrawlNode(visibilityGraph, obstacle1, this.nodeCrawlRear, obstacle1.nodeCrawlRear);

            for (byte byte0 = 0; byte0 < this.crawlNodes.size(); byte0 += 3) {
                PolygonalMap2.Node node0 = this.crawlNodes.get(byte0);
                PolygonalMap2.Node node1 = this.crawlNodes.get(byte0 + 2);

                for (byte byte1 = 0; byte1 < obstacle1.crawlNodes.size(); byte1 += 3) {
                    PolygonalMap2.Node node2 = obstacle1.crawlNodes.get(byte1);
                    PolygonalMap2.Node node3 = obstacle1.crawlNodes.get(byte1 + 2);
                    this.connectCrawlNode(visibilityGraph, obstacle1, node0, node2);
                    this.connectCrawlNode(visibilityGraph, obstacle1, node0, node3);
                    this.connectCrawlNode(visibilityGraph, obstacle1, node1, node2);
                    this.connectCrawlNode(visibilityGraph, obstacle1, node1, node3);
                }
            }
        }

        void connectCrawlNode(PolygonalMap2.VisibilityGraph visibilityGraph, PolygonalMap2.Obstacle var2, PolygonalMap2.Node node1, PolygonalMap2.Node node0) {
            if (this.isNodeInsideOf(node0)) {
                node0.flags |= 2;
                node1 = this.getClosestInteriorCrawlNode(node0.x, node0.y);
                if (node1 != null) {
                    if (!node1.isConnectedTo(node0)) {
                        PolygonalMap2.instance.connectTwoNodes(node1, node0);
                    }
                }
            } else if (!node1.ignore && !node0.ignore) {
                if (!node1.isConnectedTo(node0)) {
                    if (visibilityGraph.isVisible(node1, node0)) {
                        PolygonalMap2.instance.connectTwoNodes(node1, node0);
                    }
                }
            }
        }

        PolygonalMap2.Node getClosestInteriorCrawlNode(float float2, float float3) {
            PolygonalMap2.Node node0 = null;
            float float0 = Float.MAX_VALUE;

            for (byte byte0 = 0; byte0 < this.crawlNodes.size(); byte0 += 3) {
                PolygonalMap2.Node node1 = this.crawlNodes.get(byte0 + 1);
                float float1 = IsoUtils.DistanceToSquared(node1.x, node1.y, float2, float3);
                if (float1 < float0) {
                    node0 = node1;
                    float0 = float1;
                }
            }

            return node0;
        }

        static PolygonalMap2.Obstacle alloc() {
            return pool.isEmpty() ? new PolygonalMap2.Obstacle() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            this.outer.release();
            this.outer.clear();
            PolygonalMap2.EdgeRing.releaseAll(this.inner);
            this.inner.clear();
            pool.push(this);
        }

        static void releaseAll(ArrayList<PolygonalMap2.Obstacle> arrayList) {
            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                ((PolygonalMap2.Obstacle)arrayList.get(int0)).release();
            }
        }
    }

    private final class PMThread extends Thread {
        public boolean bStop;
        public final Object notifier = new Object();

        @Override
        public void run() {
            while (!this.bStop) {
                try {
                    this.runInner();
                } catch (Exception exception) {
                    ExceptionLogger.logException(exception);
                }
            }
        }

        private void runInner() {
            MPStatistic.getInstance().PolyPathThread.Start();
            PolygonalMap2.this.sync.startFrame();
            synchronized (PolygonalMap2.this.renderLock) {
                PolygonalMap2.instance.updateThread();
            }

            PolygonalMap2.this.sync.endFrame();
            MPStatistic.getInstance().PolyPathThread.End();

            while (this.shouldWait()) {
                synchronized (this.notifier) {
                    try {
                        this.notifier.wait();
                    } catch (InterruptedException interruptedException) {
                    }
                }
            }
        }

        private boolean shouldWait() {
            if (this.bStop) {
                return false;
            } else if (!PolygonalMap2.instance.chunkTaskQueue.isEmpty()) {
                return false;
            } else if (!PolygonalMap2.instance.squareTaskQueue.isEmpty()) {
                return false;
            } else if (!PolygonalMap2.instance.vehicleTaskQueue.isEmpty()) {
                return false;
            } else {
                return !PolygonalMap2.instance.requestTaskQueue.isEmpty() ? false : PolygonalMap2.instance.requests.isEmpty();
            }
        }

        void wake() {
            synchronized (this.notifier) {
                this.notifier.notify();
            }
        }
    }

    public static final class Path {
        final ArrayList<PolygonalMap2.PathNode> nodes = new ArrayList<>();
        final ArrayDeque<PolygonalMap2.PathNode> nodePool = new ArrayDeque<>();

        void clear() {
            for (int int0 = 0; int0 < this.nodes.size(); int0++) {
                this.nodePool.push(this.nodes.get(int0));
            }

            this.nodes.clear();
        }

        boolean isEmpty() {
            return this.nodes.isEmpty();
        }

        PolygonalMap2.PathNode addNode(float float0, float float1, float float2) {
            return this.addNode(float0, float1, float2, 0);
        }

        PolygonalMap2.PathNode addNode(float float0, float float1, float float2, int int0) {
            PolygonalMap2.PathNode pathNode = this.nodePool.isEmpty() ? new PolygonalMap2.PathNode() : this.nodePool.pop();
            pathNode.init(float0, float1, float2, int0);
            this.nodes.add(pathNode);
            return pathNode;
        }

        PolygonalMap2.PathNode addNode(PolygonalMap2.SearchNode searchNode) {
            return this.addNode(searchNode.getX(), searchNode.getY(), searchNode.getZ(), searchNode.vgNode == null ? 0 : searchNode.vgNode.flags);
        }

        PolygonalMap2.PathNode getNode(int int0) {
            return this.nodes.get(int0);
        }

        PolygonalMap2.PathNode getLastNode() {
            return this.nodes.get(this.nodes.size() - 1);
        }

        void copyFrom(PolygonalMap2.Path path1) {
            assert this != path1;

            this.clear();

            for (int int0 = 0; int0 < path1.nodes.size(); int0++) {
                PolygonalMap2.PathNode pathNode = path1.nodes.get(int0);
                this.addNode(pathNode.x, pathNode.y, pathNode.z, pathNode.flags);
            }
        }

        float length() {
            float float0 = 0.0F;

            for (int int0 = 0; int0 < this.nodes.size() - 1; int0++) {
                PolygonalMap2.PathNode pathNode0 = this.nodes.get(int0);
                PolygonalMap2.PathNode pathNode1 = this.nodes.get(int0 + 1);
                float0 += IsoUtils.DistanceTo(pathNode0.x, pathNode0.y, pathNode0.z, pathNode1.x, pathNode1.y, pathNode1.z);
            }

            return float0;
        }

        public boolean crossesSquare(int int3, int int2, int int1) {
            for (int int0 = 0; int0 < this.nodes.size() - 1; int0++) {
                PolygonalMap2.PathNode pathNode0 = this.nodes.get(int0);
                PolygonalMap2.PathNode pathNode1 = this.nodes.get(int0 + 1);
                if ((int)pathNode0.z == int1 || (int)pathNode1.z == int1) {
                    if (Line2D.linesIntersect(pathNode0.x, pathNode0.y, pathNode1.x, pathNode1.y, int3, int2, int3 + 1, int2)) {
                        return true;
                    }

                    if (Line2D.linesIntersect(pathNode0.x, pathNode0.y, pathNode1.x, pathNode1.y, int3 + 1, int2, int3 + 1, int2 + 1)) {
                        return true;
                    }

                    if (Line2D.linesIntersect(pathNode0.x, pathNode0.y, pathNode1.x, pathNode1.y, int3 + 1, int2 + 1, int3, int2 + 1)) {
                        return true;
                    }

                    if (Line2D.linesIntersect(pathNode0.x, pathNode0.y, pathNode1.x, pathNode1.y, int3, int2 + 1, int3, int2)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    static final class PathFindRequest {
        PolygonalMap2.IPathfinder finder;
        Mover mover;
        boolean bCanCrawl;
        boolean bIgnoreCrawlCost;
        boolean bCanThump;
        final ArrayList<KnownBlockedEdges> knownBlockedEdges = new ArrayList<>();
        float startX;
        float startY;
        float startZ;
        float targetX;
        float targetY;
        float targetZ;
        final TFloatArrayList targetXYZ = new TFloatArrayList();
        final PolygonalMap2.Path path = new PolygonalMap2.Path();
        boolean cancel = false;
        static final ArrayDeque<PolygonalMap2.PathFindRequest> pool = new ArrayDeque<>();

        PolygonalMap2.PathFindRequest init(
            PolygonalMap2.IPathfinder iPathfinder, Mover moverx, float float0, float float1, float float2, float float3, float float4, float float5
        ) {
            this.finder = iPathfinder;
            this.mover = moverx;
            this.bCanCrawl = false;
            this.bIgnoreCrawlCost = false;
            this.bCanThump = false;
            IsoZombie zombie0 = Type.tryCastTo(moverx, IsoZombie.class);
            if (zombie0 != null) {
                this.bCanCrawl = zombie0.isCrawling() || zombie0.isCanCrawlUnderVehicle();
                this.bIgnoreCrawlCost = zombie0.isCrawling() && !zombie0.isCanWalk();
                this.bCanThump = true;
            }

            this.startX = float0;
            this.startY = float1;
            this.startZ = float2;
            this.targetX = float3;
            this.targetY = float4;
            this.targetZ = float5;
            this.targetXYZ.resetQuick();
            this.path.clear();
            this.cancel = false;
            IsoGameCharacter character = Type.tryCastTo(moverx, IsoGameCharacter.class);
            if (character != null) {
                ArrayList arrayList = character.getMapKnowledge().getKnownBlockedEdges();

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    KnownBlockedEdges knownBlockedEdgesx = (KnownBlockedEdges)arrayList.get(int0);
                    this.knownBlockedEdges.add(KnownBlockedEdges.alloc().init(knownBlockedEdgesx));
                }
            }

            return this;
        }

        void addTargetXYZ(float float0, float float1, float float2) {
            this.targetXYZ.add(float0);
            this.targetXYZ.add(float1);
            this.targetXYZ.add(float2);
        }

        static PolygonalMap2.PathFindRequest alloc() {
            return pool.isEmpty() ? new PolygonalMap2.PathFindRequest() : pool.pop();
        }

        public void release() {
            KnownBlockedEdges.releaseAll(this.knownBlockedEdges);
            this.knownBlockedEdges.clear();

            assert !pool.contains(this);

            pool.push(this);
        }
    }

    static final class PathNode {
        float x;
        float y;
        float z;
        int flags;

        PolygonalMap2.PathNode init(float float0, float float1, float float2, int int0) {
            this.x = float0;
            this.y = float1;
            this.z = float2;
            this.flags = int0;
            return this;
        }

        PolygonalMap2.PathNode init(PolygonalMap2.PathNode pathNode0) {
            this.x = pathNode0.x;
            this.y = pathNode0.y;
            this.z = pathNode0.z;
            this.flags = pathNode0.flags;
            return this;
        }

        boolean hasFlag(int int0) {
            return (this.flags & int0) != 0;
        }
    }

    private static final class PathRequestTask {
        PolygonalMap2 map;
        PolygonalMap2.PathFindRequest request;
        static final ArrayDeque<PolygonalMap2.PathRequestTask> pool = new ArrayDeque<>();

        PolygonalMap2.PathRequestTask init(PolygonalMap2 polygonalMap2, PolygonalMap2.PathFindRequest pathFindRequest) {
            this.map = polygonalMap2;
            this.request = pathFindRequest;
            return this;
        }

        void execute() {
            if (this.request.mover instanceof IsoPlayer) {
                this.map.requests.playerQ.add(this.request);
            } else if (this.request.mover instanceof IsoZombie && ((IsoZombie)this.request.mover).target != null) {
                this.map.requests.aggroZombieQ.add(this.request);
            } else {
                this.map.requests.otherQ.add(this.request);
            }
        }

        static PolygonalMap2.PathRequestTask alloc() {
            synchronized (pool) {
                return pool.isEmpty() ? new PolygonalMap2.PathRequestTask() : pool.pop();
            }
        }

        public void release() {
            synchronized (pool) {
                assert !pool.contains(this);

                pool.push(this);
            }
        }
    }

    public static final class Point {
        public int x;
        public int y;

        PolygonalMap2.Point init(int int0, int int1) {
            this.x = int0;
            this.y = int1;
            return this;
        }

        @Override
        public boolean equals(Object object) {
            return object instanceof PolygonalMap2.Point && ((PolygonalMap2.Point)object).x == this.x && ((PolygonalMap2.Point)object).y == this.y;
        }
    }

    private static final class PointPool {
        final ArrayDeque<PolygonalMap2.Point> pool = new ArrayDeque<>();

        PolygonalMap2.Point alloc() {
            return this.pool.isEmpty() ? new PolygonalMap2.Point() : this.pool.pop();
        }

        void release(PolygonalMap2.Point point) {
            this.pool.push(point);
        }
    }

    private static final class RequestQueue {
        final ArrayDeque<PolygonalMap2.PathFindRequest> playerQ = new ArrayDeque<>();
        final ArrayDeque<PolygonalMap2.PathFindRequest> aggroZombieQ = new ArrayDeque<>();
        final ArrayDeque<PolygonalMap2.PathFindRequest> otherQ = new ArrayDeque<>();

        boolean isEmpty() {
            return this.playerQ.isEmpty() && this.aggroZombieQ.isEmpty() && this.otherQ.isEmpty();
        }

        PolygonalMap2.PathFindRequest removeFirst() {
            if (!this.playerQ.isEmpty()) {
                return this.playerQ.removeFirst();
            } else {
                return !this.aggroZombieQ.isEmpty() ? this.aggroZombieQ.removeFirst() : this.otherQ.removeFirst();
            }
        }

        PolygonalMap2.PathFindRequest removeLast() {
            if (!this.otherQ.isEmpty()) {
                return this.otherQ.removeLast();
            } else {
                return !this.aggroZombieQ.isEmpty() ? this.aggroZombieQ.removeLast() : this.playerQ.removeLast();
            }
        }
    }

    private static final class SearchNode extends ASearchNode {
        PolygonalMap2.VGAStar astar;
        PolygonalMap2.Node vgNode;
        PolygonalMap2.Square square;
        int tx;
        int ty;
        PolygonalMap2.SearchNode parent;
        static int nextID = 1;
        Integer ID = nextID++;
        private static final double SQRT2 = Math.sqrt(2.0);
        static final ArrayDeque<PolygonalMap2.SearchNode> pool = new ArrayDeque<>();

        SearchNode() {
        }

        PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar vGAStar, PolygonalMap2.Node node) {
            this.setG(0.0);
            this.astar = vGAStar;
            this.vgNode = node;
            this.square = null;
            this.tx = this.ty = -1;
            this.parent = null;
            return this;
        }

        PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar vGAStar, PolygonalMap2.Square squarex) {
            this.setG(0.0);
            this.astar = vGAStar;
            this.vgNode = null;
            this.square = squarex;
            this.tx = this.ty = -1;
            this.parent = null;
            return this;
        }

        PolygonalMap2.SearchNode init(PolygonalMap2.VGAStar vGAStar, int int0, int int1) {
            this.setG(0.0);
            this.astar = vGAStar;
            this.vgNode = null;
            this.square = null;
            this.tx = int0;
            this.ty = int1;
            this.parent = null;
            return this;
        }

        @Override
        public double h() {
            return this.dist(this.astar.goalNode.searchNode);
        }

        @Override
        public double c(ISearchNode iSearchNode) {
            PolygonalMap2.SearchNode searchNode0 = (PolygonalMap2.SearchNode)iSearchNode;
            double double0 = 0.0;
            boolean boolean0 = this.astar.mover instanceof IsoZombie && ((IsoZombie)this.astar.mover).bCrawling;
            boolean boolean1 = !(this.astar.mover instanceof IsoZombie) || ((IsoZombie)this.astar.mover).bCrawling;
            if (boolean1 && this.square != null && searchNode0.square != null) {
                if (this.square.x == searchNode0.square.x - 1 && this.square.y == searchNode0.square.y) {
                    if (searchNode0.square.has(2048)) {
                        double0 = !boolean0 && searchNode0.square.has(1048576) ? 20.0 : 200.0;
                    }
                } else if (this.square.x == searchNode0.square.x + 1 && this.square.y == searchNode0.square.y) {
                    if (this.square.has(2048)) {
                        double0 = !boolean0 && this.square.has(1048576) ? 20.0 : 200.0;
                    }
                } else if (this.square.y == searchNode0.square.y - 1 && this.square.x == searchNode0.square.x) {
                    if (searchNode0.square.has(4096)) {
                        double0 = !boolean0 && searchNode0.square.has(2097152) ? 20.0 : 200.0;
                    }
                } else if (this.square.y == searchNode0.square.y + 1 && this.square.x == searchNode0.square.x && this.square.has(4096)) {
                    double0 = !boolean0 && this.square.has(2097152) ? 20.0 : 200.0;
                }
            }

            if (searchNode0.square != null && searchNode0.square.has(131072)) {
                double0 = 20.0;
            }

            if (this.vgNode != null && searchNode0.vgNode != null) {
                for (int int0 = 0; int0 < this.vgNode.visible.size(); int0++) {
                    PolygonalMap2.Connection connection = this.vgNode.visible.get(int0);
                    if (connection.otherNode(this.vgNode) == searchNode0.vgNode) {
                        if ((this.vgNode.square == null || !this.vgNode.square.has(131072)) && connection.has(2)) {
                            double0 = 20.0;
                        }
                        break;
                    }
                }
            }

            PolygonalMap2.Square square0 = this.square == null ? PolygonalMap2.instance.getSquare((int)this.vgNode.x, (int)this.vgNode.y, 0) : this.square;
            PolygonalMap2.Square square1 = searchNode0.square == null
                ? PolygonalMap2.instance.getSquare((int)searchNode0.vgNode.x, (int)searchNode0.vgNode.y, 0)
                : searchNode0.square;
            if (square0 != null && square1 != null) {
                if (square0.x == square1.x - 1 && square0.y == square1.y) {
                    if (square1.has(32768)) {
                        double0 = 20.0;
                    }
                } else if (square0.x == square1.x + 1 && square0.y == square1.y) {
                    if (square0.has(32768)) {
                        double0 = 20.0;
                    }
                } else if (square0.y == square1.y - 1 && square0.x == square1.x) {
                    if (square1.has(65536)) {
                        double0 = 20.0;
                    }
                } else if (square0.y == square1.y + 1 && square0.x == square1.x && square0.has(65536)) {
                    double0 = 20.0;
                }

                if (boolean0) {
                    if (square0.x == square1.x - 1 && square0.y == square1.y) {
                        if (square1.has(2) && square1.has(8192)) {
                            double0 = 20.0;
                        }
                    } else if (square0.x == square1.x + 1 && square0.y == square1.y) {
                        if (square0.has(2) && square0.has(8192)) {
                            double0 = 20.0;
                        }
                    } else if (square0.y == square1.y - 1 && square0.x == square1.x) {
                        if (square1.has(4) && square1.has(16384)) {
                            double0 = 20.0;
                        }
                    } else if (square0.y == square1.y + 1 && square0.x == square1.x && square0.has(4) && square0.has(16384)) {
                        double0 = 20.0;
                    }
                }
            }

            boolean boolean2 = this.vgNode != null && this.vgNode.hasFlag(2);
            boolean boolean3 = searchNode0.vgNode != null && searchNode0.vgNode.hasFlag(2);
            if (!boolean2 && boolean3 && !this.astar.bIgnoreCrawlCost) {
                double0 += 10.0;
            }

            if (searchNode0.square != null) {
                double0 += searchNode0.square.cost;
            }

            return this.dist(searchNode0) + double0;
        }

        @Override
        public void getSuccessors(ArrayList<ISearchNode> arrayList1) {
            ArrayList arrayList0 = arrayList1;
            if (this.vgNode != null) {
                if (this.vgNode.graphs != null) {
                    for (int int0 = 0; int0 < this.vgNode.graphs.size(); int0++) {
                        PolygonalMap2.VisibilityGraph visibilityGraph = this.vgNode.graphs.get(int0);
                        if (!visibilityGraph.created) {
                            visibilityGraph.create();
                        }
                    }
                }

                for (int int1 = 0; int1 < this.vgNode.visible.size(); int1++) {
                    PolygonalMap2.Connection connection = this.vgNode.visible.get(int1);
                    PolygonalMap2.Node node = connection.otherNode(this.vgNode);
                    PolygonalMap2.SearchNode searchNode1 = this.astar.getSearchNode(node);
                    if ((this.vgNode.square == null || searchNode1.square == null || !this.astar.isKnownBlocked(this.vgNode.square, searchNode1.square))
                        && (this.astar.bCanCrawl || !node.hasFlag(2))
                        && (this.astar.bCanThump || !connection.has(2))) {
                        arrayList0.add(searchNode1);
                    }
                }

                if (!this.vgNode.hasFlag(8)) {
                    return;
                }
            }

            if (this.square != null) {
                for (int int2 = -1; int2 <= 1; int2++) {
                    for (int int3 = -1; int3 <= 1; int3++) {
                        if (int3 != 0 || int2 != 0) {
                            PolygonalMap2.Square square0 = PolygonalMap2.instance.getSquare(this.square.x + int3, this.square.y + int2, this.square.z);
                            if (square0 != null && !this.astar.isSquareInCluster(square0) && !this.astar.canNotMoveBetween(this.square, square0, false)) {
                                PolygonalMap2.SearchNode searchNode2 = this.astar.getSearchNode(square0);
                                if (arrayList0.contains(searchNode2)) {
                                    boolean boolean0 = false;
                                } else {
                                    arrayList0.add(searchNode2);
                                }
                            }
                        }
                    }
                }

                if (this.square.z > 0) {
                    PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y + 1, this.square.z - 1);
                    if (square1 != null && square1.has(64) && !this.astar.isSquareInCluster(square1)) {
                        PolygonalMap2.SearchNode searchNode3 = this.astar.getSearchNode(square1);
                        if (arrayList0.contains(searchNode3)) {
                            boolean boolean1 = false;
                        } else {
                            arrayList0.add(searchNode3);
                        }
                    }

                    square1 = PolygonalMap2.instance.getSquare(this.square.x + 1, this.square.y, this.square.z - 1);
                    if (square1 != null && square1.has(8) && !this.astar.isSquareInCluster(square1)) {
                        PolygonalMap2.SearchNode searchNode4 = this.astar.getSearchNode(square1);
                        if (arrayList0.contains(searchNode4)) {
                            boolean boolean2 = false;
                        } else {
                            arrayList0.add(searchNode4);
                        }
                    }
                }

                if (this.square.z < 8 && this.square.has(64)) {
                    PolygonalMap2.Square square2 = PolygonalMap2.instance.getSquare(this.square.x, this.square.y - 1, this.square.z + 1);
                    if (square2 != null && !this.astar.isSquareInCluster(square2)) {
                        PolygonalMap2.SearchNode searchNode5 = this.astar.getSearchNode(square2);
                        if (arrayList0.contains(searchNode5)) {
                            boolean boolean3 = false;
                        } else {
                            arrayList0.add(searchNode5);
                        }
                    }
                }

                if (this.square.z < 8 && this.square.has(8)) {
                    PolygonalMap2.Square square3 = PolygonalMap2.instance.getSquare(this.square.x - 1, this.square.y, this.square.z + 1);
                    if (square3 != null && !this.astar.isSquareInCluster(square3)) {
                        PolygonalMap2.SearchNode searchNode6 = this.astar.getSearchNode(square3);
                        if (arrayList0.contains(searchNode6)) {
                            boolean boolean4 = false;
                        } else {
                            arrayList0.add(searchNode6);
                        }
                    }
                }
            }
        }

        @Override
        public ISearchNode getParent() {
            return this.parent;
        }

        @Override
        public void setParent(ISearchNode iSearchNode) {
            this.parent = (PolygonalMap2.SearchNode)iSearchNode;
        }

        @Override
        public Integer keyCode() {
            return this.ID;
        }

        public float getX() {
            if (this.square != null) {
                return this.square.x + 0.5F;
            } else {
                return this.vgNode != null ? this.vgNode.x : this.tx;
            }
        }

        public float getY() {
            if (this.square != null) {
                return this.square.y + 0.5F;
            } else {
                return this.vgNode != null ? this.vgNode.y : this.ty;
            }
        }

        public float getZ() {
            if (this.square != null) {
                return this.square.z;
            } else {
                return this.vgNode != null ? this.vgNode.z : 0.0F;
            }
        }

        public double dist(PolygonalMap2.SearchNode searchNode0) {
            if (this.square == null
                || searchNode0.square == null
                || Math.abs(this.square.x - searchNode0.square.x) > 1
                || Math.abs(this.square.y - searchNode0.square.y) > 1) {
                float float0 = this.getX();
                float float1 = this.getY();
                float float2 = searchNode0.getX();
                float float3 = searchNode0.getY();
                return Math.sqrt(Math.pow(float0 - float2, 2.0) + Math.pow(float1 - float3, 2.0));
            } else {
                return this.square.x != searchNode0.square.x && this.square.y != searchNode0.square.y ? SQRT2 : 1.0;
            }
        }

        float getApparentZ() {
            if (this.square == null) {
                return this.vgNode.z;
            } else if (this.square.has(8) || this.square.has(64)) {
                return this.square.z + 0.75F;
            } else if (this.square.has(16) || this.square.has(128)) {
                return this.square.z + 0.5F;
            } else {
                return !this.square.has(32) && !this.square.has(256) ? this.square.z : this.square.z + 0.25F;
            }
        }

        static PolygonalMap2.SearchNode alloc() {
            return pool.isEmpty() ? new PolygonalMap2.SearchNode() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class Square {
        static int nextID = 1;
        Integer ID = nextID++;
        int x;
        int y;
        int z;
        int bits;
        short cost;
        static final ArrayDeque<PolygonalMap2.Square> pool = new ArrayDeque<>();

        Square() {
        }

        PolygonalMap2.Square init(int int0, int int1, int int2) {
            this.x = int0;
            this.y = int1;
            this.z = int2;
            return this;
        }

        boolean has(int int0) {
            return (this.bits & int0) != 0;
        }

        boolean isReallySolid() {
            return this.has(1) || this.has(1024) && !this.isAdjacentToWindow();
        }

        boolean isNonThumpableSolid() {
            return this.isReallySolid() && !this.has(131072);
        }

        boolean isCanPathW() {
            if (this.has(8192)) {
                return true;
            } else {
                PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
                return square1 != null && (square1.has(131072) || square1.has(262144));
            }
        }

        boolean isCanPathN() {
            if (this.has(16384)) {
                return true;
            } else {
                PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
                return square1 != null && (square1.has(131072) || square1.has(524288));
            }
        }

        boolean isCollideW() {
            if (this.has(2)) {
                return true;
            } else {
                PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
                return square1 != null && (square1.has(262144) || square1.has(448) || square1.isReallySolid());
            }
        }

        boolean isCollideN() {
            if (this.has(4)) {
                return true;
            } else {
                PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
                return square1 != null && (square1.has(524288) || square1.has(56) || square1.isReallySolid());
            }
        }

        boolean isThumpW() {
            if (this.has(32768)) {
                return true;
            } else {
                PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.x - 1, this.y, this.z);
                return square1 != null && square1.has(131072);
            }
        }

        boolean isThumpN() {
            if (this.has(65536)) {
                return true;
            } else {
                PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.x, this.y - 1, this.z);
                return square1 != null && square1.has(131072);
            }
        }

        boolean isAdjacentToWindow() {
            if (!this.has(2048) && !this.has(4096)) {
                PolygonalMap2.Square square1 = PolygonalMap2.instance.getSquare(this.x, this.y + 1, this.z);
                if (square1 != null && square1.has(4096)) {
                    return true;
                } else {
                    PolygonalMap2.Square square2 = PolygonalMap2.instance.getSquare(this.x + 1, this.y, this.z);
                    return square2 != null && square2.has(2048);
                }
            } else {
                return true;
            }
        }

        static PolygonalMap2.Square alloc() {
            return pool.isEmpty() ? new PolygonalMap2.Square() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class SquareUpdateTask {
        PolygonalMap2 map;
        int x;
        int y;
        int z;
        int bits;
        short cost;
        static final ArrayDeque<PolygonalMap2.SquareUpdateTask> pool = new ArrayDeque<>();

        PolygonalMap2.SquareUpdateTask init(PolygonalMap2 polygonalMap2, IsoGridSquare square) {
            this.map = polygonalMap2;
            this.x = square.x;
            this.y = square.y;
            this.z = square.z;
            this.bits = getBits(square);
            this.cost = getCost(square);
            return this;
        }

        void execute() {
            PolygonalMap2.Chunk chunk = this.map.getChunkFromChunkPos(this.x / 10, this.y / 10);
            if (chunk != null && chunk.setData(this)) {
                PolygonalMap2.ChunkDataZ.EPOCH++;
                this.map.rebuild = true;
            }
        }

        static int getBits(IsoGridSquare square) {
            int int0 = 0;
            if (square.Is(IsoFlagType.solidfloor)) {
                int0 |= 512;
            }

            if (square.isSolid()) {
                int0 |= 1;
            }

            if (square.isSolidTrans()) {
                int0 |= 1024;
            }

            if (square.Is(IsoFlagType.collideW)) {
                int0 |= 2;
            }

            if (square.Is(IsoFlagType.collideN)) {
                int0 |= 4;
            }

            if (square.Has(IsoObjectType.stairsTW)) {
                int0 |= 8;
            }

            if (square.Has(IsoObjectType.stairsMW)) {
                int0 |= 16;
            }

            if (square.Has(IsoObjectType.stairsBW)) {
                int0 |= 32;
            }

            if (square.Has(IsoObjectType.stairsTN)) {
                int0 |= 64;
            }

            if (square.Has(IsoObjectType.stairsMN)) {
                int0 |= 128;
            }

            if (square.Has(IsoObjectType.stairsBN)) {
                int0 |= 256;
            }

            if (square.Is(IsoFlagType.windowW) || square.Is(IsoFlagType.WindowW)) {
                int0 |= 2050;
                if (isWindowUnblocked(square, false)) {
                    int0 |= 1048576;
                }
            }

            if (square.Is(IsoFlagType.windowN) || square.Is(IsoFlagType.WindowN)) {
                int0 |= 4100;
                if (isWindowUnblocked(square, true)) {
                    int0 |= 2097152;
                }
            }

            if (square.Is(IsoFlagType.canPathW)) {
                int0 |= 8192;
            }

            if (square.Is(IsoFlagType.canPathN)) {
                int0 |= 16384;
            }

            for (int int1 = 0; int1 < square.getSpecialObjects().size(); int1++) {
                IsoObject object = square.getSpecialObjects().get(int1);
                IsoDirections directions = IsoDirections.Max;
                if (object instanceof IsoDoor) {
                    directions = ((IsoDoor)object).getSpriteEdge(false);
                    if (((IsoDoor)object).IsOpen()) {
                        directions = IsoDirections.Max;
                    }
                } else if (object instanceof IsoThumpable && ((IsoThumpable)object).isDoor()) {
                    directions = ((IsoThumpable)object).getSpriteEdge(false);
                    if (((IsoThumpable)object).IsOpen()) {
                        directions = IsoDirections.Max;
                    }
                }

                if (directions == IsoDirections.W) {
                    int0 |= 8192;
                    int0 |= 2;
                } else if (directions == IsoDirections.N) {
                    int0 |= 16384;
                    int0 |= 4;
                } else if (directions == IsoDirections.S) {
                    int0 |= 524288;
                } else if (directions == IsoDirections.E) {
                    int0 |= 262144;
                }
            }

            if (square.Is(IsoFlagType.DoorWallW)) {
                int0 |= 8192;
                int0 |= 2;
            }

            if (square.Is(IsoFlagType.DoorWallN)) {
                int0 |= 16384;
                int0 |= 4;
            }

            if (hasSquareThumpable(square)) {
                int0 |= 8192;
                int0 |= 16384;
                int0 |= 131072;
            }

            if (hasWallThumpableN(square)) {
                int0 |= 81920;
            }

            if (hasWallThumpableW(square)) {
                int0 |= 40960;
            }

            return int0;
        }

        static boolean isWindowUnblocked(IsoGridSquare square, boolean boolean0) {
            for (int int0 = 0; int0 < square.getSpecialObjects().size(); int0++) {
                IsoObject object0 = square.getSpecialObjects().get(int0);
                if (object0 instanceof IsoThumpable thumpable && thumpable.isWindow() && boolean0 == thumpable.north) {
                    if (thumpable.isBarricaded()) {
                        return false;
                    }

                    return true;
                }

                if (object0 instanceof IsoWindow window && boolean0 == window.north) {
                    if (window.isBarricaded()) {
                        return false;
                    }

                    if (window.isInvincible()) {
                        return false;
                    }

                    if (window.IsOpen()) {
                        return true;
                    }

                    if (window.isDestroyed() && window.isGlassRemoved()) {
                        return true;
                    }

                    return false;
                }
            }

            IsoObject object1 = square.getWindowFrame(boolean0);
            return IsoWindowFrame.canClimbThrough(object1, null);
        }

        static boolean hasSquareThumpable(IsoGridSquare square) {
            for (int int0 = 0; int0 < square.getSpecialObjects().size(); int0++) {
                IsoThumpable thumpable = Type.tryCastTo(square.getSpecialObjects().get(int0), IsoThumpable.class);
                if (thumpable != null && thumpable.isThumpable() && thumpable.isBlockAllTheSquare()) {
                    return true;
                }
            }

            for (int int1 = 0; int1 < square.getObjects().size(); int1++) {
                IsoObject object = square.getObjects().get(int1);
                if (object.isMovedThumpable()) {
                    return true;
                }
            }

            return false;
        }

        static boolean hasWallThumpableN(IsoGridSquare square1) {
            IsoGridSquare square0 = square1.getAdjacentSquare(IsoDirections.N);
            if (square0 == null) {
                return false;
            } else {
                for (int int0 = 0; int0 < square1.getSpecialObjects().size(); int0++) {
                    IsoThumpable thumpable = Type.tryCastTo(square1.getSpecialObjects().get(int0), IsoThumpable.class);
                    if (thumpable != null
                        && !thumpable.canClimbThrough(null)
                        && !thumpable.canClimbOver(null)
                        && thumpable.isThumpable()
                        && !thumpable.isBlockAllTheSquare()
                        && !thumpable.isDoor()
                        && thumpable.TestCollide(null, square1, square0)) {
                        return true;
                    }
                }

                return false;
            }
        }

        static boolean hasWallThumpableW(IsoGridSquare square1) {
            IsoGridSquare square0 = square1.getAdjacentSquare(IsoDirections.W);
            if (square0 == null) {
                return false;
            } else {
                for (int int0 = 0; int0 < square1.getSpecialObjects().size(); int0++) {
                    IsoThumpable thumpable = Type.tryCastTo(square1.getSpecialObjects().get(int0), IsoThumpable.class);
                    if (thumpable != null
                        && !thumpable.canClimbThrough(null)
                        && !thumpable.canClimbOver(null)
                        && thumpable.isThumpable()
                        && !thumpable.isBlockAllTheSquare()
                        && !thumpable.isDoor()
                        && thumpable.TestCollide(null, square1, square0)) {
                        return true;
                    }
                }

                return false;
            }
        }

        static short getCost(IsoGridSquare square) {
            short short0 = 0;
            if (square.HasTree() || square.getProperties().Is("Bush")) {
                short0 = (short)(short0 + 5);
            }

            return short0;
        }

        static PolygonalMap2.SquareUpdateTask alloc() {
            synchronized (pool) {
                return pool.isEmpty() ? new PolygonalMap2.SquareUpdateTask() : pool.pop();
            }
        }

        public void release() {
            synchronized (pool) {
                assert !pool.contains(this);

                pool.push(this);
            }
        }
    }

    private static final class Sync {
        private int fps = 20;
        private long period = 1000000000L / this.fps;
        private long excess;
        private long beforeTime = System.nanoTime();
        private long overSleepTime = 0L;

        void begin() {
            this.beforeTime = System.nanoTime();
            this.overSleepTime = 0L;
        }

        void startFrame() {
            this.excess = 0L;
        }

        void endFrame() {
            long long0 = System.nanoTime();
            long long1 = long0 - this.beforeTime;
            long long2 = this.period - long1 - this.overSleepTime;
            if (long2 > 0L) {
                try {
                    Thread.sleep(long2 / 1000000L);
                } catch (InterruptedException interruptedException) {
                }

                this.overSleepTime = System.nanoTime() - long0 - long2;
            } else {
                this.excess -= long2;
                this.overSleepTime = 0L;
            }

            this.beforeTime = System.nanoTime();
        }
    }

    private static final class TestRequest implements PolygonalMap2.IPathfinder {
        final PolygonalMap2.Path path = new PolygonalMap2.Path();
        boolean done;

        @Override
        public void Succeeded(PolygonalMap2.Path pathx, Mover var2) {
            this.path.copyFrom(pathx);
            this.done = true;
        }

        @Override
        public void Failed(Mover var1) {
            this.path.clear();
            this.done = true;
        }
    }

    private static final class VGAStar extends AStar {
        ArrayList<PolygonalMap2.VisibilityGraph> graphs;
        final ArrayList<PolygonalMap2.SearchNode> searchNodes = new ArrayList<>();
        final TIntObjectHashMap<PolygonalMap2.SearchNode> nodeMap = new TIntObjectHashMap<>();
        final PolygonalMap2.GoalNode goalNode = new PolygonalMap2.GoalNode();
        final TIntObjectHashMap<PolygonalMap2.SearchNode> squareToNode = new TIntObjectHashMap<>();
        Mover mover;
        boolean bCanCrawl;
        boolean bIgnoreCrawlCost;
        boolean bCanThump;
        final TIntObjectHashMap<KnownBlockedEdges> knownBlockedEdges = new TIntObjectHashMap<>();
        final PolygonalMap2.VGAStar.InitProc initProc = new PolygonalMap2.VGAStar.InitProc();

        PolygonalMap2.VGAStar init(ArrayList<PolygonalMap2.VisibilityGraph> arrayList, TIntObjectHashMap<PolygonalMap2.Node> tIntObjectHashMap) {
            this.setMaxSteps(5000);
            this.graphs = arrayList;
            this.searchNodes.clear();
            this.nodeMap.clear();
            this.squareToNode.clear();
            this.mover = null;
            tIntObjectHashMap.forEachEntry(this.initProc);
            return this;
        }

        PolygonalMap2.VisibilityGraph getVisGraphForSquare(PolygonalMap2.Square square) {
            for (int int0 = 0; int0 < this.graphs.size(); int0++) {
                PolygonalMap2.VisibilityGraph visibilityGraph = this.graphs.get(int0);
                if (visibilityGraph.contains(square)) {
                    return visibilityGraph;
                }
            }

            return null;
        }

        boolean isSquareInCluster(PolygonalMap2.Square square) {
            return this.getVisGraphForSquare(square) != null;
        }

        PolygonalMap2.SearchNode getSearchNode(PolygonalMap2.Node node) {
            if (node.square != null) {
                return this.getSearchNode(node.square);
            } else {
                PolygonalMap2.SearchNode searchNode = this.nodeMap.get(node.ID);
                if (searchNode == null) {
                    searchNode = PolygonalMap2.SearchNode.alloc().init(this, node);
                    this.searchNodes.add(searchNode);
                    this.nodeMap.put(node.ID, searchNode);
                }

                return searchNode;
            }
        }

        PolygonalMap2.SearchNode getSearchNode(PolygonalMap2.Square square) {
            PolygonalMap2.SearchNode searchNode = this.squareToNode.get(square.ID);
            if (searchNode == null) {
                searchNode = PolygonalMap2.SearchNode.alloc().init(this, square);
                this.searchNodes.add(searchNode);
                this.squareToNode.put(square.ID, searchNode);
            }

            return searchNode;
        }

        PolygonalMap2.SearchNode getSearchNode(int int0, int int1) {
            PolygonalMap2.SearchNode searchNode = PolygonalMap2.SearchNode.alloc().init(this, int0, int1);
            this.searchNodes.add(searchNode);
            return searchNode;
        }

        ArrayList<ISearchNode> shortestPath(
            PolygonalMap2.PathFindRequest pathFindRequest, PolygonalMap2.SearchNode searchNode1, PolygonalMap2.SearchNode searchNode0
        ) {
            this.mover = pathFindRequest.mover;
            this.bCanCrawl = pathFindRequest.bCanCrawl;
            this.bIgnoreCrawlCost = pathFindRequest.bIgnoreCrawlCost;
            this.bCanThump = pathFindRequest.bCanThump;
            this.goalNode.init(searchNode0);
            return this.shortestPath(searchNode1, this.goalNode);
        }

        boolean canNotMoveBetween(PolygonalMap2.Square square1, PolygonalMap2.Square square0, boolean boolean9) {
            assert Math.abs(square1.x - square0.x) <= 1;

            assert Math.abs(square1.y - square0.y) <= 1;

            assert square1.z == square0.z;

            assert square1 != square0;

            if (square1.x == 10921 && square1.y == 10137 && square0.x == square1.x - 1 && square0.y == square1.y) {
                boolean boolean0 = true;
            }

            boolean boolean1 = square0.x < square1.x;
            boolean boolean2 = square0.x > square1.x;
            boolean boolean3 = square0.y < square1.y;
            boolean boolean4 = square0.y > square1.y;
            if (!square0.isNonThumpableSolid() && (this.bCanThump || !square0.isReallySolid())) {
                if (square0.y < square1.y && square1.has(64)) {
                    return true;
                } else if (square0.x < square1.x && square1.has(8)) {
                    return true;
                } else if (square0.y > square1.y && square0.x == square1.x && square0.has(64)) {
                    return true;
                } else if (square0.x > square1.x && square0.y == square1.y && square0.has(8)) {
                    return true;
                } else if (square0.x != square1.x && square0.has(448)) {
                    return true;
                } else if (square0.y != square1.y && square0.has(56)) {
                    return true;
                } else if (square0.x != square1.x && square1.has(448)) {
                    return true;
                } else if (square0.y != square1.y && square1.has(56)) {
                    return true;
                } else if (!square0.has(512) && !square0.has(504)) {
                    return true;
                } else if (this.isKnownBlocked(square1, square0)) {
                    return true;
                } else {
                    if (square1.x == 11920 && square0.y == 6803 && square0.has(131072)) {
                        boolean boolean5 = true;
                    }

                    boolean boolean6 = square1.isCanPathN() && (this.bCanThump || !square1.isThumpN());
                    boolean boolean7 = square1.isCanPathW() && (this.bCanThump || !square1.isThumpW());
                    boolean boolean8 = boolean3 && square1.isCollideN() && (square1.x != square0.x || boolean9 || !boolean6);
                    boolean boolean10 = boolean1 && square1.isCollideW() && (square1.y != square0.y || boolean9 || !boolean7);
                    boolean6 = square0.isCanPathN() && (this.bCanThump || !square0.isThumpN());
                    boolean7 = square0.isCanPathW() && (this.bCanThump || !square0.isThumpW());
                    boolean boolean11 = boolean4 && square0.has(131076) && (square1.x != square0.x || boolean9 || !boolean6);
                    boolean boolean12 = boolean2 && square0.has(131074) && (square1.y != square0.y || boolean9 || !boolean7);
                    if (!boolean8 && !boolean10 && !boolean11 && !boolean12) {
                        boolean boolean13 = square0.x != square1.x && square0.y != square1.y;
                        if (boolean13) {
                            PolygonalMap2.Square square2 = PolygonalMap2.instance.getSquare(square1.x, square0.y, square1.z);
                            PolygonalMap2.Square square3 = PolygonalMap2.instance.getSquare(square0.x, square1.y, square1.z);

                            assert square2 != square1 && square2 != square0;

                            assert square3 != square1 && square3 != square0;

                            if (square0.x == square1.x + 1 && square0.y == square1.y + 1 && square2 != null && square3 != null) {
                                if (square2.has(4096) && square3.has(2048)) {
                                    return true;
                                }

                                if (square2.isThumpN() && square3.isThumpW()) {
                                    return true;
                                }
                            }

                            if (square0.x == square1.x - 1 && square0.y == square1.y - 1 && square2 != null && square3 != null) {
                                if (square2.has(2048) && square3.has(4096)) {
                                    return true;
                                }

                                if (square2.isThumpW() && square3.isThumpN()) {
                                    return true;
                                }
                            }

                            if (square2 != null && this.canNotMoveBetween(square1, square2, true)) {
                                return true;
                            }

                            if (square3 != null && this.canNotMoveBetween(square1, square3, true)) {
                                return true;
                            }

                            if (square2 != null && this.canNotMoveBetween(square0, square2, true)) {
                                return true;
                            }

                            if (square3 != null && this.canNotMoveBetween(square0, square3, true)) {
                                return true;
                            }
                        }

                        return false;
                    } else {
                        return true;
                    }
                }
            } else {
                return true;
            }
        }

        boolean isKnownBlocked(PolygonalMap2.Square square1, PolygonalMap2.Square square0) {
            if (square1.z != square0.z) {
                return false;
            } else {
                KnownBlockedEdges knownBlockedEdges0 = this.knownBlockedEdges.get(square1.ID);
                KnownBlockedEdges knownBlockedEdges1 = this.knownBlockedEdges.get(square0.ID);
                return knownBlockedEdges0 != null && knownBlockedEdges0.isBlocked(square0.x, square0.y)
                    ? true
                    : knownBlockedEdges1 != null && knownBlockedEdges1.isBlocked(square1.x, square1.y);
            }
        }

        final class InitProc implements TIntObjectProcedure<PolygonalMap2.Node> {
            public boolean execute(int int0, PolygonalMap2.Node node) {
                PolygonalMap2.SearchNode searchNode = PolygonalMap2.SearchNode.alloc().init(VGAStar.this, node);
                searchNode.square = node.square;
                VGAStar.this.squareToNode.put(int0, searchNode);
                VGAStar.this.nodeMap.put(node.ID, searchNode);
                VGAStar.this.searchNodes.add(searchNode);
                return true;
            }
        }
    }

    private static final class Vehicle {
        final PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
        final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
        final TFloatArrayList crawlOffsets = new TFloatArrayList();
        float upVectorDot;
        static final ArrayDeque<PolygonalMap2.Vehicle> pool = new ArrayDeque<>();

        static PolygonalMap2.Vehicle alloc() {
            return pool.isEmpty() ? new PolygonalMap2.Vehicle() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class VehicleAddTask implements PolygonalMap2.IVehicleTask {
        PolygonalMap2 map;
        BaseVehicle vehicle;
        final PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
        final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
        final TFloatArrayList crawlOffsets = new TFloatArrayList();
        float upVectorDot;
        static final ArrayDeque<PolygonalMap2.VehicleAddTask> pool = new ArrayDeque<>();

        @Override
        public void init(PolygonalMap2 polygonalMap2, BaseVehicle vehiclex) {
            this.map = polygonalMap2;
            this.vehicle = vehiclex;
            this.poly.init(vehiclex.getPoly());
            this.polyPlusRadius.init(vehiclex.getPolyPlusRadius());
            this.crawlOffsets.resetQuick();
            this.crawlOffsets.addAll(vehiclex.getScript().getCrawlOffsets());
            this.upVectorDot = vehiclex.getUpVectorDot();
        }

        @Override
        public void execute() {
            PolygonalMap2.Vehicle vehiclex = PolygonalMap2.Vehicle.alloc();
            vehiclex.poly.init(this.poly);
            vehiclex.polyPlusRadius.init(this.polyPlusRadius);
            vehiclex.crawlOffsets.resetQuick();
            vehiclex.crawlOffsets.addAll(this.crawlOffsets);
            vehiclex.upVectorDot = this.upVectorDot;
            this.map.vehicles.add(vehiclex);
            this.map.vehicleMap.put(this.vehicle, vehiclex);
            this.vehicle = null;
        }

        static PolygonalMap2.VehicleAddTask alloc() {
            synchronized (pool) {
                return pool.isEmpty() ? new PolygonalMap2.VehicleAddTask() : pool.pop();
            }
        }

        @Override
        public void release() {
            synchronized (pool) {
                assert !pool.contains(this);

                pool.push(this);
            }
        }
    }

    private static final class VehicleCluster {
        int z;
        final ArrayList<PolygonalMap2.VehicleRect> rects = new ArrayList<>();
        static final ArrayDeque<PolygonalMap2.VehicleCluster> pool = new ArrayDeque<>();

        PolygonalMap2.VehicleCluster init() {
            this.rects.clear();
            return this;
        }

        void merge(PolygonalMap2.VehicleCluster vehicleCluster0) {
            for (int int0 = 0; int0 < vehicleCluster0.rects.size(); int0++) {
                PolygonalMap2.VehicleRect vehicleRect = vehicleCluster0.rects.get(int0);
                vehicleRect.cluster = this;
            }

            this.rects.addAll(vehicleCluster0.rects);
            vehicleCluster0.rects.clear();
        }

        PolygonalMap2.VehicleRect bounds() {
            int int0 = Integer.MAX_VALUE;
            int int1 = Integer.MAX_VALUE;
            int int2 = Integer.MIN_VALUE;
            int int3 = Integer.MIN_VALUE;

            for (int int4 = 0; int4 < this.rects.size(); int4++) {
                PolygonalMap2.VehicleRect vehicleRect = this.rects.get(int4);
                int0 = Math.min(int0, vehicleRect.left());
                int1 = Math.min(int1, vehicleRect.top());
                int2 = Math.max(int2, vehicleRect.right());
                int3 = Math.max(int3, vehicleRect.bottom());
            }

            return PolygonalMap2.VehicleRect.alloc().init(int0, int1, int2 - int0, int3 - int1, this.z);
        }

        static PolygonalMap2.VehicleCluster alloc() {
            return pool.isEmpty() ? new PolygonalMap2.VehicleCluster() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    public static final class VehiclePoly {
        public Transform t = new Transform();
        public float x1;
        public float y1;
        public float x2;
        public float y2;
        public float x3;
        public float y3;
        public float x4;
        public float y4;
        public float z;
        public final Vector2[] borders = new Vector2[4];
        private static final Quaternionf tempQuat = new Quaternionf();

        VehiclePoly() {
            for (int int0 = 0; int0 < this.borders.length; int0++) {
                this.borders[int0] = new Vector2();
            }
        }

        PolygonalMap2.VehiclePoly init(PolygonalMap2.VehiclePoly vehiclePoly0) {
            this.x1 = vehiclePoly0.x1;
            this.y1 = vehiclePoly0.y1;
            this.x2 = vehiclePoly0.x2;
            this.y2 = vehiclePoly0.y2;
            this.x3 = vehiclePoly0.x3;
            this.y3 = vehiclePoly0.y3;
            this.x4 = vehiclePoly0.x4;
            this.y4 = vehiclePoly0.y4;
            this.z = vehiclePoly0.z;
            return this;
        }

        PolygonalMap2.VehiclePoly init(BaseVehicle vehicle, float float2) {
            VehicleScript vehicleScript = vehicle.getScript();
            Vector3f vector3f0 = vehicleScript.getExtents();
            Vector3f vector3f1 = vehicleScript.getCenterOfMassOffset();
            float float0 = 1.0F;
            Vector2[] vectors = this.borders;
            Quaternionf quaternionf = tempQuat;
            vehicle.getWorldTransform(this.t);
            this.t.getRotation(quaternionf);
            float float1 = vector3f0.x * float0 + float2 * 2.0F;
            float float3 = vector3f0.z * float0 + float2 * 2.0F;
            float float4 = vector3f0.y * float0 + float2 * 2.0F;
            float1 /= 2.0F;
            float3 /= 2.0F;
            float4 /= 2.0F;
            Vector3f vector3f2 = PolygonalMap2.tempVec3f_1;
            if (quaternionf.x < 0.0F) {
                vehicle.getWorldPos(vector3f1.x - float1, 0.0F, vector3f1.z + float3, vector3f2);
                vectors[0].set(vector3f2.x, vector3f2.y);
                vehicle.getWorldPos(vector3f1.x + float1, float4, vector3f1.z + float3, vector3f2);
                vectors[1].set(vector3f2.x, vector3f2.y);
                vehicle.getWorldPos(vector3f1.x + float1, float4, vector3f1.z - float3, vector3f2);
                vectors[2].set(vector3f2.x, vector3f2.y);
                vehicle.getWorldPos(vector3f1.x - float1, 0.0F, vector3f1.z - float3, vector3f2);
                vectors[3].set(vector3f2.x, vector3f2.y);
                this.z = vehicle.z;
            } else {
                vehicle.getWorldPos(vector3f1.x - float1, float4, vector3f1.z + float3, vector3f2);
                vectors[0].set(vector3f2.x, vector3f2.y);
                vehicle.getWorldPos(vector3f1.x + float1, 0.0F, vector3f1.z + float3, vector3f2);
                vectors[1].set(vector3f2.x, vector3f2.y);
                vehicle.getWorldPos(vector3f1.x + float1, 0.0F, vector3f1.z - float3, vector3f2);
                vectors[2].set(vector3f2.x, vector3f2.y);
                vehicle.getWorldPos(vector3f1.x - float1, float4, vector3f1.z - float3, vector3f2);
                vectors[3].set(vector3f2.x, vector3f2.y);
                this.z = vehicle.z;
            }

            int int0 = 0;

            for (int int1 = 0; int1 < vectors.length; int1++) {
                Vector2 vector0 = vectors[int1];
                Vector2 vector1 = vectors[(int1 + 1) % vectors.length];
                int0 = (int)(int0 + (vector1.x - vector0.x) * (vector1.y + vector0.y));
            }

            if (int0 < 0) {
                Vector2 vector2 = vectors[1];
                Vector2 vector3 = vectors[2];
                Vector2 vector4 = vectors[3];
                vectors[1] = vector4;
                vectors[2] = vector3;
                vectors[3] = vector2;
            }

            this.x1 = vectors[0].x;
            this.y1 = vectors[0].y;
            this.x2 = vectors[1].x;
            this.y2 = vectors[1].y;
            this.x3 = vectors[2].x;
            this.y3 = vectors[2].y;
            this.x4 = vectors[3].x;
            this.y4 = vectors[3].y;
            return this;
        }

        public static Vector2 lineIntersection(Vector2 vector2, Vector2 vector1, Vector2 vector4, Vector2 vector3) {
            Vector2 vector0 = new Vector2();
            float float0 = vector2.y - vector1.y;
            float float1 = vector1.x - vector2.x;
            float float2 = -float0 * vector2.x - float1 * vector2.y;
            float float3 = vector4.y - vector3.y;
            float float4 = vector3.x - vector4.x;
            float float5 = -float3 * vector4.x - float4 * vector4.y;
            float float6 = QuadranglesIntersection.det(float0, float1, float3, float4);
            if (float6 != 0.0F) {
                vector0.x = -QuadranglesIntersection.det(float2, float1, float5, float4) * 1.0F / float6;
                vector0.y = -QuadranglesIntersection.det(float0, float2, float3, float5) * 1.0F / float6;
                return vector0;
            } else {
                return null;
            }
        }

        PolygonalMap2.VehicleRect getAABB(PolygonalMap2.VehicleRect vehicleRect) {
            float float0 = Math.min(this.x1, Math.min(this.x2, Math.min(this.x3, this.x4)));
            float float1 = Math.min(this.y1, Math.min(this.y2, Math.min(this.y3, this.y4)));
            float float2 = Math.max(this.x1, Math.max(this.x2, Math.max(this.x3, this.x4)));
            float float3 = Math.max(this.y1, Math.max(this.y2, Math.max(this.y3, this.y4)));
            return vehicleRect.init(null, (int)float0, (int)float1, (int)Math.ceil(float2) - (int)float0, (int)Math.ceil(float3) - (int)float1, (int)this.z);
        }

        float isLeft(float float3, float float1, float float5, float float0, float float2, float float4) {
            return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
        }

        public boolean containsPoint(float float1, float float0) {
            int int0 = 0;

            for (int int1 = 0; int1 < 4; int1++) {
                Vector2 vector0 = this.borders[int1];
                Vector2 vector1 = int1 == 3 ? this.borders[0] : this.borders[int1 + 1];
                if (vector0.y <= float0) {
                    if (vector1.y > float0 && this.isLeft(vector0.x, vector0.y, vector1.x, vector1.y, float1, float0) > 0.0F) {
                        int0++;
                    }
                } else if (vector1.y <= float0 && this.isLeft(vector0.x, vector0.y, vector1.x, vector1.y, float1, float0) < 0.0F) {
                    int0--;
                }
            }

            return int0 != 0;
        }
    }

    private static final class VehicleRect {
        PolygonalMap2.VehicleCluster cluster;
        PolygonalMap2.Vehicle vehicle;
        int x;
        int y;
        int w;
        int h;
        int z;
        static final ArrayDeque<PolygonalMap2.VehicleRect> pool = new ArrayDeque<>();

        PolygonalMap2.VehicleRect init(PolygonalMap2.Vehicle vehiclex, int int0, int int1, int int2, int int3, int int4) {
            this.cluster = null;
            this.vehicle = vehiclex;
            this.x = int0;
            this.y = int1;
            this.w = int2;
            this.h = int3;
            this.z = int4;
            return this;
        }

        PolygonalMap2.VehicleRect init(int int0, int int1, int int2, int int3, int int4) {
            this.cluster = null;
            this.vehicle = null;
            this.x = int0;
            this.y = int1;
            this.w = int2;
            this.h = int3;
            this.z = int4;
            return this;
        }

        int left() {
            return this.x;
        }

        int top() {
            return this.y;
        }

        int right() {
            return this.x + this.w;
        }

        int bottom() {
            return this.y + this.h;
        }

        boolean containsPoint(float float2, float float1, float float0) {
            return float2 >= this.left() && float2 < this.right() && float1 >= this.top() && float1 < this.bottom() && (int)float0 == this.z;
        }

        boolean containsPoint(float float2, float float1, float float0, int int1) {
            int int0 = this.x - int1;
            int int2 = this.y - int1;
            int int3 = this.right() + int1;
            int int4 = this.bottom() + int1;
            return float2 >= int0 && float2 < int3 && float1 >= int2 && float1 < int4 && (int)float0 == this.z;
        }

        boolean intersects(PolygonalMap2.VehicleRect vehicleRect0) {
            return this.left() < vehicleRect0.right()
                && this.right() > vehicleRect0.left()
                && this.top() < vehicleRect0.bottom()
                && this.bottom() > vehicleRect0.top();
        }

        boolean isAdjacent(PolygonalMap2.VehicleRect vehicleRect1) {
            this.x--;
            this.y--;
            this.w += 2;
            this.h += 2;
            boolean boolean0 = this.intersects(vehicleRect1);
            this.x++;
            this.y++;
            this.w -= 2;
            this.h -= 2;
            return boolean0;
        }

        static PolygonalMap2.VehicleRect alloc() {
            if (pool.isEmpty()) {
                boolean boolean0 = false;
            } else {
                boolean boolean1 = false;
            }

            return pool.isEmpty() ? new PolygonalMap2.VehicleRect() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class VehicleRemoveTask implements PolygonalMap2.IVehicleTask {
        PolygonalMap2 map;
        BaseVehicle vehicle;
        static final ArrayDeque<PolygonalMap2.VehicleRemoveTask> pool = new ArrayDeque<>();

        @Override
        public void init(PolygonalMap2 polygonalMap2, BaseVehicle vehiclex) {
            this.map = polygonalMap2;
            this.vehicle = vehiclex;
        }

        @Override
        public void execute() {
            PolygonalMap2.Vehicle vehiclex = this.map.vehicleMap.remove(this.vehicle);
            if (vehiclex != null) {
                this.map.vehicles.remove(vehiclex);
                vehiclex.release();
            }

            this.vehicle = null;
        }

        static PolygonalMap2.VehicleRemoveTask alloc() {
            synchronized (pool) {
                return pool.isEmpty() ? new PolygonalMap2.VehicleRemoveTask() : pool.pop();
            }
        }

        @Override
        public void release() {
            synchronized (pool) {
                assert !pool.contains(this);

                pool.push(this);
            }
        }
    }

    private static final class VehicleState {
        BaseVehicle vehicle;
        float x;
        float y;
        float z;
        final Vector3f forward = new Vector3f();
        final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
        static final ArrayDeque<PolygonalMap2.VehicleState> pool = new ArrayDeque<>();

        PolygonalMap2.VehicleState init(BaseVehicle vehiclex) {
            this.vehicle = vehiclex;
            this.x = vehiclex.x;
            this.y = vehiclex.y;
            this.z = vehiclex.z;
            vehiclex.getForwardVector(this.forward);
            this.polyPlusRadius.init(vehiclex.getPolyPlusRadius());
            return this;
        }

        boolean check() {
            boolean boolean0 = this.x != this.vehicle.x || this.y != this.vehicle.y || (int)this.z != (int)this.vehicle.z;
            if (!boolean0) {
                BaseVehicle.Vector3fObjectPool vector3fObjectPool = BaseVehicle.TL_vector3f_pool.get();
                Vector3f vector3f = this.vehicle.getForwardVector(vector3fObjectPool.alloc());
                boolean0 = this.forward.dot(vector3f) < 0.999F;
                if (boolean0) {
                    this.forward.set(vector3f);
                }

                vector3fObjectPool.release(vector3f);
            }

            if (boolean0) {
                this.x = this.vehicle.x;
                this.y = this.vehicle.y;
                this.z = this.vehicle.z;
            }

            return boolean0;
        }

        static PolygonalMap2.VehicleState alloc() {
            return pool.isEmpty() ? new PolygonalMap2.VehicleState() : pool.pop();
        }

        void release() {
            assert !pool.contains(this);

            pool.push(this);
        }
    }

    private static final class VehicleUpdateTask implements PolygonalMap2.IVehicleTask {
        PolygonalMap2 map;
        BaseVehicle vehicle;
        final PolygonalMap2.VehiclePoly poly = new PolygonalMap2.VehiclePoly();
        final PolygonalMap2.VehiclePoly polyPlusRadius = new PolygonalMap2.VehiclePoly();
        float upVectorDot;
        static final ArrayDeque<PolygonalMap2.VehicleUpdateTask> pool = new ArrayDeque<>();

        @Override
        public void init(PolygonalMap2 polygonalMap2, BaseVehicle vehiclex) {
            this.map = polygonalMap2;
            this.vehicle = vehiclex;
            this.poly.init(vehiclex.getPoly());
            this.polyPlusRadius.init(vehiclex.getPolyPlusRadius());
            this.upVectorDot = vehiclex.getUpVectorDot();
        }

        @Override
        public void execute() {
            PolygonalMap2.Vehicle vehiclex = this.map.vehicleMap.get(this.vehicle);
            vehiclex.poly.init(this.poly);
            vehiclex.polyPlusRadius.init(this.polyPlusRadius);
            vehiclex.upVectorDot = this.upVectorDot;
            this.vehicle = null;
        }

        static PolygonalMap2.VehicleUpdateTask alloc() {
            synchronized (pool) {
                return pool.isEmpty() ? new PolygonalMap2.VehicleUpdateTask() : pool.pop();
            }
        }

        @Override
        public void release() {
            synchronized (pool) {
                assert !pool.contains(this);

                pool.push(this);
            }
        }
    }

    private static final class VisibilityGraph {
        boolean created;
        PolygonalMap2.VehicleCluster cluster;
        final ArrayList<PolygonalMap2.Node> nodes = new ArrayList<>();
        final ArrayList<PolygonalMap2.Edge> edges = new ArrayList<>();
        final ArrayList<PolygonalMap2.Obstacle> obstacles = new ArrayList<>();
        final ArrayList<PolygonalMap2.Node> intersectNodes = new ArrayList<>();
        final ArrayList<PolygonalMap2.Node> perimeterNodes = new ArrayList<>();
        final ArrayList<PolygonalMap2.Edge> perimeterEdges = new ArrayList<>();
        final ArrayList<PolygonalMap2.Node> obstacleTraceNodes = new ArrayList<>();
        final TIntArrayList splitXY = new TIntArrayList();
        static final PolygonalMap2.VisibilityGraph.CompareIntersection comparator = new PolygonalMap2.VisibilityGraph.CompareIntersection();
        private static final PolygonalMap2.ClusterOutlineGrid clusterOutlineGrid = new PolygonalMap2.ClusterOutlineGrid();
        private static final ArrayDeque<PolygonalMap2.VisibilityGraph> pool = new ArrayDeque<>();

        PolygonalMap2.VisibilityGraph init(PolygonalMap2.VehicleCluster vehicleCluster) {
            this.created = false;
            this.cluster = vehicleCluster;
            this.edges.clear();
            this.nodes.clear();
            this.obstacles.clear();
            this.intersectNodes.clear();
            this.perimeterEdges.clear();
            this.perimeterNodes.clear();
            return this;
        }

        void addEdgesForVehicle(PolygonalMap2.Vehicle vehicle) {
            PolygonalMap2.VehiclePoly vehiclePoly = vehicle.polyPlusRadius;
            int int0 = (int)vehiclePoly.z;
            PolygonalMap2.Node node0 = PolygonalMap2.Node.alloc().init(vehiclePoly.x1, vehiclePoly.y1, int0);
            PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(vehiclePoly.x2, vehiclePoly.y2, int0);
            PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init(vehiclePoly.x3, vehiclePoly.y3, int0);
            PolygonalMap2.Node node3 = PolygonalMap2.Node.alloc().init(vehiclePoly.x4, vehiclePoly.y4, int0);
            PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init(vehicle);
            this.obstacles.add(obstacle);
            PolygonalMap2.Edge edge0 = PolygonalMap2.Edge.alloc().init(node0, node1, obstacle, obstacle.outer);
            PolygonalMap2.Edge edge1 = PolygonalMap2.Edge.alloc().init(node1, node2, obstacle, obstacle.outer);
            PolygonalMap2.Edge edge2 = PolygonalMap2.Edge.alloc().init(node2, node3, obstacle, obstacle.outer);
            PolygonalMap2.Edge edge3 = PolygonalMap2.Edge.alloc().init(node3, node0, obstacle, obstacle.outer);
            obstacle.outer.add(edge0);
            obstacle.outer.add(edge1);
            obstacle.outer.add(edge2);
            obstacle.outer.add(edge3);
            obstacle.calcBounds();
            this.nodes.add(node0);
            this.nodes.add(node1);
            this.nodes.add(node2);
            this.nodes.add(node3);
            this.edges.add(edge0);
            this.edges.add(edge1);
            this.edges.add(edge2);
            this.edges.add(edge3);
            if (!(vehicle.upVectorDot < 0.95F)) {
                obstacle.nodeCrawlFront = PolygonalMap2.Node.alloc()
                    .init((vehiclePoly.x1 + vehiclePoly.x2) / 2.0F, (vehiclePoly.y1 + vehiclePoly.y2) / 2.0F, int0);
                obstacle.nodeCrawlRear = PolygonalMap2.Node.alloc()
                    .init((vehiclePoly.x3 + vehiclePoly.x4) / 2.0F, (vehiclePoly.y3 + vehiclePoly.y4) / 2.0F, int0);
                obstacle.nodeCrawlFront.flags |= 1;
                obstacle.nodeCrawlRear.flags |= 1;
                this.nodes.add(obstacle.nodeCrawlFront);
                this.nodes.add(obstacle.nodeCrawlRear);
                PolygonalMap2.Edge edge4 = edge0.split(obstacle.nodeCrawlFront);
                PolygonalMap2.Edge edge5 = edge2.split(obstacle.nodeCrawlRear);
                this.edges.add(edge4);
                this.edges.add(edge5);
                BaseVehicle.Vector2fObjectPool vector2fObjectPool = BaseVehicle.TL_vector2f_pool.get();
                Vector2f vector2f0 = vector2fObjectPool.alloc();
                Vector2f vector2f1 = vector2fObjectPool.alloc();
                obstacle.crawlNodes.clear();

                for (int int1 = 0; int1 < vehicle.crawlOffsets.size(); int1++) {
                    float float0 = vehicle.crawlOffsets.get(int1);
                    vector2f0.set(node2.x, node2.y);
                    vector2f1.set(node1.x, node1.y);
                    vector2f1.sub(vector2f0).mul(float0).add(vector2f0);
                    PolygonalMap2.Node node4 = PolygonalMap2.Node.alloc().init(vector2f1.x, vector2f1.y, int0);
                    node4.flags |= 1;
                    vector2f0.set(node3.x, node3.y);
                    vector2f1.set(node0.x, node0.y);
                    vector2f1.sub(vector2f0).mul(float0).add(vector2f0);
                    PolygonalMap2.Node node5 = PolygonalMap2.Node.alloc().init(vector2f1.x, vector2f1.y, int0);
                    node5.flags |= 1;
                    PolygonalMap2.Node node6 = PolygonalMap2.Node.alloc().init((node4.x + node5.x) / 2.0F, (node4.y + node5.y) / 2.0F, int0);
                    node6.flags |= 3;
                    obstacle.crawlNodes.add(node4);
                    obstacle.crawlNodes.add(node6);
                    obstacle.crawlNodes.add(node5);
                    this.nodes.add(node4);
                    this.nodes.add(node6);
                    this.nodes.add(node5);
                    PolygonalMap2.Edge edge6 = edge1.split(node4);
                    edge3 = edge3.split(node5);
                    this.edges.add(edge6);
                    this.edges.add(edge3);
                }

                vector2fObjectPool.release(vector2f0);
                vector2fObjectPool.release(vector2f1);
            }
        }

        boolean isVisible(PolygonalMap2.Node node0, PolygonalMap2.Node node1) {
            if (node0.sharesEdge(node1)) {
                return !node0.onSameShapeButDoesNotShareAnEdge(node1);
            } else if (node0.sharesShape(node1)) {
                return false;
            } else {
                for (int int0 = 0; int0 < this.edges.size(); int0++) {
                    PolygonalMap2.Edge edge0 = this.edges.get(int0);
                    if (this.intersects(node0, node1, edge0)) {
                        return false;
                    }
                }

                for (int int1 = 0; int1 < this.perimeterEdges.size(); int1++) {
                    PolygonalMap2.Edge edge1 = this.perimeterEdges.get(int1);
                    if (this.intersects(node0, node1, edge1)) {
                        return false;
                    }
                }

                return true;
            }
        }

        boolean intersects(PolygonalMap2.Node node1, PolygonalMap2.Node node0, PolygonalMap2.Edge edge) {
            return !edge.hasNode(node1) && !edge.hasNode(node0)
                ? Line2D.linesIntersect(node1.x, node1.y, node0.x, node0.y, edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y)
                : false;
        }

        public PolygonalMap2.Intersection getIntersection(PolygonalMap2.Edge edge0, PolygonalMap2.Edge edge1) {
            float float0 = edge0.node1.x;
            float float1 = edge0.node1.y;
            float float2 = edge0.node2.x;
            float float3 = edge0.node2.y;
            float float4 = edge1.node1.x;
            float float5 = edge1.node1.y;
            float float6 = edge1.node2.x;
            float float7 = edge1.node2.y;
            double double0 = (float7 - float5) * (float2 - float0) - (float6 - float4) * (float3 - float1);
            if (double0 == 0.0) {
                return null;
            } else {
                double double1 = ((float6 - float4) * (float1 - float5) - (float7 - float5) * (float0 - float4)) / double0;
                double double2 = ((float2 - float0) * (float1 - float5) - (float3 - float1) * (float0 - float4)) / double0;
                if (double1 >= 0.0 && double1 <= 1.0 && double2 >= 0.0 && double2 <= 1.0) {
                    float float8 = (float)(float0 + double1 * (float2 - float0));
                    float float9 = (float)(float1 + double1 * (float3 - float1));
                    return new PolygonalMap2.Intersection(edge0, edge1, (float)double1, (float)double2, float8, float9);
                } else {
                    return null;
                }
            }
        }

        @Deprecated
        void addWorldObstacles() {
            PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
            vehicleRect.x--;
            vehicleRect.y--;
            vehicleRect.w += 3;
            vehicleRect.h += 3;
            PolygonalMap2.ObjectOutline[][] objectOutlines = new PolygonalMap2.ObjectOutline[vehicleRect.w][vehicleRect.h];
            int int0 = this.cluster.z;

            for (int int1 = vehicleRect.top(); int1 < vehicleRect.bottom() - 1; int1++) {
                for (int int2 = vehicleRect.left(); int2 < vehicleRect.right() - 1; int2++) {
                    PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int2, int1, int0);
                    if (square != null && this.contains(square, 1)) {
                        if (square.has(504) || square.isReallySolid()) {
                            PolygonalMap2.ObjectOutline.setSolid(int2 - vehicleRect.left(), int1 - vehicleRect.top(), int0, objectOutlines);
                        }

                        if (square.has(2)) {
                            PolygonalMap2.ObjectOutline.setWest(int2 - vehicleRect.left(), int1 - vehicleRect.top(), int0, objectOutlines);
                        }

                        if (square.has(4)) {
                            PolygonalMap2.ObjectOutline.setNorth(int2 - vehicleRect.left(), int1 - vehicleRect.top(), int0, objectOutlines);
                        }

                        if (square.has(262144)) {
                            PolygonalMap2.ObjectOutline.setWest(int2 - vehicleRect.left() + 1, int1 - vehicleRect.top(), int0, objectOutlines);
                        }

                        if (square.has(524288)) {
                            PolygonalMap2.ObjectOutline.setNorth(int2 - vehicleRect.left(), int1 - vehicleRect.top() + 1, int0, objectOutlines);
                        }
                    }
                }
            }

            for (int int3 = 0; int3 < vehicleRect.h; int3++) {
                for (int int4 = 0; int4 < vehicleRect.w; int4++) {
                    PolygonalMap2.ObjectOutline objectOutline = PolygonalMap2.ObjectOutline.get(int4, int3, int0, objectOutlines);
                    if (objectOutline != null && objectOutline.nw && objectOutline.nw_w && objectOutline.nw_n) {
                        objectOutline.trace(objectOutlines, this.obstacleTraceNodes);
                        if (!objectOutline.nodes.isEmpty()) {
                            PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init((IsoGridSquare)null);

                            for (int int5 = 0; int5 < objectOutline.nodes.size() - 1; int5++) {
                                PolygonalMap2.Node node0 = objectOutline.nodes.get(int5);
                                PolygonalMap2.Node node1 = objectOutline.nodes.get(int5 + 1);
                                node0.x = node0.x + vehicleRect.left();
                                node0.y = node0.y + vehicleRect.top();
                                if (!this.contains(node0.x, node0.y, node0.z)) {
                                    node0.ignore = true;
                                }

                                PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node0, node1, obstacle, obstacle.outer);
                                obstacle.outer.add(edge);
                                this.nodes.add(node0);
                            }

                            obstacle.calcBounds();
                            this.obstacles.add(obstacle);
                            this.edges.addAll(obstacle.outer);
                        }
                    }
                }
            }

            for (int int6 = 0; int6 < vehicleRect.h; int6++) {
                for (int int7 = 0; int7 < vehicleRect.w; int7++) {
                    if (objectOutlines[int7][int6] != null) {
                        objectOutlines[int7][int6].release();
                    }
                }
            }

            vehicleRect.release();
        }

        void addWorldObstaclesClipper() {
            PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
            vehicleRect.x--;
            vehicleRect.y--;
            vehicleRect.w += 2;
            vehicleRect.h += 2;
            if (PolygonalMap2.instance.clipperThread == null) {
                PolygonalMap2.instance.clipperThread = new Clipper();
            }

            Clipper clipper = PolygonalMap2.instance.clipperThread;
            clipper.clear();
            int int0 = this.cluster.z;

            for (int int1 = vehicleRect.top(); int1 < vehicleRect.bottom(); int1++) {
                for (int int2 = vehicleRect.left(); int2 < vehicleRect.right(); int2++) {
                    PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int2, int1, int0);
                    if (square != null && this.contains(square, 1)) {
                        if (square.has(504) || square.isReallySolid()) {
                            clipper.addAABB(int2 - 0.3F, int1 - 0.3F, int2 + 1 + 0.3F, int1 + 1 + 0.3F);
                        }

                        if (square.has(2)) {
                            clipper.addAABB(int2 - 0.3F, int1 - 0.3F, int2 + 0.3F, int1 + 1 + 0.3F);
                        }

                        if (square.has(4)) {
                            clipper.addAABB(int2 - 0.3F, int1 - 0.3F, int2 + 1 + 0.3F, int1 + 0.3F);
                        }
                    }
                }
            }

            vehicleRect.release();
            ByteBuffer byteBuffer = PolygonalMap2.instance.xyBufferThread;
            int int3 = clipper.generatePolygons();

            for (int int4 = 0; int4 < int3; int4++) {
                byteBuffer.clear();
                clipper.getPolygon(int4, byteBuffer);
                PolygonalMap2.Obstacle obstacle = PolygonalMap2.Obstacle.alloc().init((IsoGridSquare)null);
                this.getEdgesFromBuffer(byteBuffer, obstacle, true, int0);
                short short0 = byteBuffer.getShort();

                for (int int5 = 0; int5 < short0; int5++) {
                    this.getEdgesFromBuffer(byteBuffer, obstacle, false, int0);
                }

                obstacle.calcBounds();
                this.obstacles.add(obstacle);
                this.edges.addAll(obstacle.outer);

                for (int int6 = 0; int6 < obstacle.inner.size(); int6++) {
                    this.edges.addAll(obstacle.inner.get(int6));
                }
            }
        }

        void getEdgesFromBuffer(ByteBuffer byteBuffer, PolygonalMap2.Obstacle obstacle, boolean boolean0, int int2) {
            short short0 = byteBuffer.getShort();
            if (short0 < 3) {
                byteBuffer.position(byteBuffer.position() + short0 * 4 * 2);
            } else {
                PolygonalMap2.EdgeRing edgeRing = obstacle.outer;
                if (!boolean0) {
                    edgeRing = PolygonalMap2.EdgeRing.alloc();
                    edgeRing.clear();
                    obstacle.inner.add(edgeRing);
                }

                int int0 = this.nodes.size();

                for (int int1 = short0 - 1; int1 >= 0; int1--) {
                    float float0 = byteBuffer.getFloat();
                    float float1 = byteBuffer.getFloat();
                    PolygonalMap2.Node node0 = PolygonalMap2.Node.alloc().init(float0, float1, int2);
                    this.nodes.add(node0);
                }

                for (int int3 = int0; int3 < this.nodes.size() - 1; int3++) {
                    PolygonalMap2.Node node1 = this.nodes.get(int3);
                    PolygonalMap2.Node node2 = this.nodes.get(int3 + 1);
                    if (!this.contains(node1.x, node1.y, node1.z)) {
                        node1.ignore = true;
                    }

                    PolygonalMap2.Edge edge0 = PolygonalMap2.Edge.alloc().init(node1, node2, obstacle, edgeRing);
                    edgeRing.add(edge0);
                }

                PolygonalMap2.Node node3 = this.nodes.get(this.nodes.size() - 1);
                PolygonalMap2.Node node4 = this.nodes.get(int0);
                PolygonalMap2.Edge edge1 = PolygonalMap2.Edge.alloc().init(node3, node4, obstacle, edgeRing);
                edgeRing.add(edge1);
            }
        }

        void trySplit(PolygonalMap2.Edge edge, PolygonalMap2.VehicleRect vehicleRect, TIntArrayList tIntArrayList) {
            if (Math.abs(edge.node1.x - edge.node2.x) > Math.abs(edge.node1.y - edge.node2.y)) {
                float float0 = Math.min(edge.node1.x, edge.node2.x);
                float float1 = Math.max(edge.node1.x, edge.node2.x);
                float float2 = edge.node1.y;
                if (vehicleRect.left() > float0
                    && vehicleRect.left() < float1
                    && vehicleRect.top() < float2
                    && vehicleRect.bottom() > float2
                    && !tIntArrayList.contains(vehicleRect.left())
                    && !this.contains(vehicleRect.left() - 0.5F, float2, this.cluster.z)) {
                    tIntArrayList.add(vehicleRect.left());
                }

                if (vehicleRect.right() > float0
                    && vehicleRect.right() < float1
                    && vehicleRect.top() < float2
                    && vehicleRect.bottom() > float2
                    && !tIntArrayList.contains(vehicleRect.right())
                    && !this.contains(vehicleRect.right() + 0.5F, float2, this.cluster.z)) {
                    tIntArrayList.add(vehicleRect.right());
                }
            } else {
                float float3 = Math.min(edge.node1.y, edge.node2.y);
                float float4 = Math.max(edge.node1.y, edge.node2.y);
                float float5 = edge.node1.x;
                if (vehicleRect.top() > float3
                    && vehicleRect.top() < float4
                    && vehicleRect.left() < float5
                    && vehicleRect.right() > float5
                    && !tIntArrayList.contains(vehicleRect.top())
                    && !this.contains(float5, vehicleRect.top() - 0.5F, this.cluster.z)) {
                    tIntArrayList.add(vehicleRect.top());
                }

                if (vehicleRect.bottom() > float3
                    && vehicleRect.bottom() < float4
                    && vehicleRect.left() < float5
                    && vehicleRect.right() > float5
                    && !tIntArrayList.contains(vehicleRect.bottom())
                    && !this.contains(float5, vehicleRect.bottom() + 0.5F, this.cluster.z)) {
                    tIntArrayList.add(vehicleRect.bottom());
                }
            }
        }

        void splitWorldObstacleEdges(PolygonalMap2.EdgeRing edgeRing) {
            for (int int0 = edgeRing.size() - 1; int0 >= 0; int0--) {
                PolygonalMap2.Edge edge0 = edgeRing.get(int0);
                this.splitXY.clear();

                for (int int1 = 0; int1 < this.cluster.rects.size(); int1++) {
                    PolygonalMap2.VehicleRect vehicleRect = this.cluster.rects.get(int1);
                    this.trySplit(edge0, vehicleRect, this.splitXY);
                }

                if (!this.splitXY.isEmpty()) {
                    this.splitXY.sort();
                    if (Math.abs(edge0.node1.x - edge0.node2.x) > Math.abs(edge0.node1.y - edge0.node2.y)) {
                        if (edge0.node1.x < edge0.node2.x) {
                            for (int int2 = this.splitXY.size() - 1; int2 >= 0; int2--) {
                                PolygonalMap2.Node node0 = PolygonalMap2.Node.alloc().init(this.splitXY.get(int2), edge0.node1.y, this.cluster.z);
                                PolygonalMap2.Edge edge1 = edge0.split(node0);
                                this.nodes.add(node0);
                                this.edges.add(edge1);
                            }
                        } else {
                            for (int int3 = 0; int3 < this.splitXY.size(); int3++) {
                                PolygonalMap2.Node node1 = PolygonalMap2.Node.alloc().init(this.splitXY.get(int3), edge0.node1.y, this.cluster.z);
                                PolygonalMap2.Edge edge2 = edge0.split(node1);
                                this.nodes.add(node1);
                                this.edges.add(edge2);
                            }
                        }
                    } else if (edge0.node1.y < edge0.node2.y) {
                        for (int int4 = this.splitXY.size() - 1; int4 >= 0; int4--) {
                            PolygonalMap2.Node node2 = PolygonalMap2.Node.alloc().init(edge0.node1.x, this.splitXY.get(int4), this.cluster.z);
                            PolygonalMap2.Edge edge3 = edge0.split(node2);
                            this.nodes.add(node2);
                            this.edges.add(edge3);
                        }
                    } else {
                        for (int int5 = 0; int5 < this.splitXY.size(); int5++) {
                            PolygonalMap2.Node node3 = PolygonalMap2.Node.alloc().init(edge0.node1.x, this.splitXY.get(int5), this.cluster.z);
                            PolygonalMap2.Edge edge4 = edge0.split(node3);
                            this.nodes.add(node3);
                            this.edges.add(edge4);
                        }
                    }
                }
            }
        }

        void getStairSquares(ArrayList<PolygonalMap2.Square> arrayList) {
            PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
            vehicleRect.x -= 4;
            vehicleRect.w += 4;
            vehicleRect.w++;
            vehicleRect.y -= 4;
            vehicleRect.h += 4;
            vehicleRect.h++;

            for (int int0 = vehicleRect.top(); int0 < vehicleRect.bottom(); int0++) {
                for (int int1 = vehicleRect.left(); int1 < vehicleRect.right(); int1++) {
                    PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int1, int0, this.cluster.z);
                    if (square != null && square.has(72) && !arrayList.contains(square)) {
                        arrayList.add(square);
                    }
                }
            }

            vehicleRect.release();
        }

        void getCanPathSquares(ArrayList<PolygonalMap2.Square> arrayList) {
            PolygonalMap2.VehicleRect vehicleRect = this.cluster.bounds();
            vehicleRect.x--;
            vehicleRect.w += 2;
            vehicleRect.y--;
            vehicleRect.h += 2;

            for (int int0 = vehicleRect.top(); int0 < vehicleRect.bottom(); int0++) {
                for (int int1 = vehicleRect.left(); int1 < vehicleRect.right(); int1++) {
                    PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int1, int0, this.cluster.z);
                    if (square != null && (square.isCanPathW() || square.isCanPathN()) && !arrayList.contains(square)) {
                        arrayList.add(square);
                    }
                }
            }

            vehicleRect.release();
        }

        void connectVehicleCrawlNodes() {
            for (int int0 = 0; int0 < this.obstacles.size(); int0++) {
                PolygonalMap2.Obstacle obstacle0 = this.obstacles.get(int0);
                if (obstacle0.vehicle != null && obstacle0.nodeCrawlFront != null) {
                    for (byte byte0 = 0; byte0 < obstacle0.crawlNodes.size(); byte0 += 3) {
                        PolygonalMap2.Node node0 = obstacle0.crawlNodes.get(byte0);
                        PolygonalMap2.Node node1 = obstacle0.crawlNodes.get(byte0 + 1);
                        PolygonalMap2.Node node2 = obstacle0.crawlNodes.get(byte0 + 2);
                        PolygonalMap2.instance.connectTwoNodes(node0, node1);
                        PolygonalMap2.instance.connectTwoNodes(node2, node1);
                        if (byte0 + 3 < obstacle0.crawlNodes.size()) {
                            PolygonalMap2.Node node3 = obstacle0.crawlNodes.get(byte0 + 3 + 1);
                            PolygonalMap2.instance.connectTwoNodes(node1, node3);
                        }
                    }

                    if (!obstacle0.crawlNodes.isEmpty()) {
                        int int1 = obstacle0.crawlNodes.size() - 2;
                        PolygonalMap2.Node node4 = obstacle0.crawlNodes.get(int1);
                        PolygonalMap2.instance.connectTwoNodes(obstacle0.nodeCrawlFront, node4);
                        byte byte1 = 1;
                        node4 = obstacle0.crawlNodes.get(byte1);
                        PolygonalMap2.instance.connectTwoNodes(obstacle0.nodeCrawlRear, node4);
                    }

                    if (!obstacle0.crawlNodes.isEmpty()) {
                        PolygonalMap2.ImmutableRectF immutableRectF = obstacle0.bounds;
                        int int2 = (int)immutableRectF.x;
                        int int3 = (int)immutableRectF.y;
                        int int4 = (int)Math.ceil(immutableRectF.right());
                        int int5 = (int)Math.ceil(immutableRectF.bottom());

                        for (int int6 = int3; int6 < int5; int6++) {
                            for (int int7 = int2; int7 < int4; int7++) {
                                PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(int7, int6, this.cluster.z);
                                if (square != null && obstacle0.isPointInside(int7 + 0.5F, int6 + 0.5F)) {
                                    PolygonalMap2.Node node5 = PolygonalMap2.instance.getNodeForSquare(square);

                                    for (int int8 = node5.visible.size() - 1; int8 >= 0; int8--) {
                                        PolygonalMap2.Connection connection = node5.visible.get(int8);
                                        if (connection.has(1)) {
                                            PolygonalMap2.Node node6 = connection.otherNode(node5);
                                            PolygonalMap2.Node node7 = obstacle0.getClosestInteriorCrawlNode(node5.x, node5.y);

                                            for (int int9 = 0; int9 < obstacle0.outer.size(); int9++) {
                                                PolygonalMap2.Edge edge0 = obstacle0.outer.get(int9);
                                                float float0 = edge0.node1.x;
                                                float float1 = edge0.node1.y;
                                                float float2 = edge0.node2.x;
                                                float float3 = edge0.node2.y;
                                                float float4 = connection.node1.x;
                                                float float5 = connection.node1.y;
                                                float float6 = connection.node2.x;
                                                float float7 = connection.node2.y;
                                                double double0 = (float7 - float5) * (float2 - float0) - (float6 - float4) * (float3 - float1);
                                                if (double0 != 0.0) {
                                                    double double1 = ((float6 - float4) * (float1 - float5) - (float7 - float5) * (float0 - float4)) / double0;
                                                    double double2 = ((float2 - float0) * (float1 - float5) - (float3 - float1) * (float0 - float4)) / double0;
                                                    if (double1 >= 0.0 && double1 <= 1.0 && double2 >= 0.0 && double2 <= 1.0) {
                                                        float float8 = (float)(float0 + double1 * (float2 - float0));
                                                        float float9 = (float)(float1 + double1 * (float3 - float1));
                                                        PolygonalMap2.Node node8 = PolygonalMap2.Node.alloc().init(float8, float9, this.cluster.z);
                                                        node8.flags |= 1;
                                                        boolean boolean0 = edge0.node1.isConnectedTo(edge0.node2);
                                                        PolygonalMap2.Edge edge1 = edge0.split(node8);
                                                        if (boolean0) {
                                                            PolygonalMap2.instance.connectTwoNodes(edge0.node1, edge0.node2);
                                                            PolygonalMap2.instance.connectTwoNodes(edge1.node1, edge1.node2);
                                                        }

                                                        this.edges.add(edge1);
                                                        this.nodes.add(node8);
                                                        PolygonalMap2.instance.connectTwoNodes(node6, node8, connection.flags & 2 | 1);
                                                        PolygonalMap2.instance.connectTwoNodes(node8, node7, 0);
                                                        break;
                                                    }
                                                }
                                            }

                                            PolygonalMap2.instance.breakConnection(connection);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    for (int int10 = int0 + 1; int10 < this.obstacles.size(); int10++) {
                        PolygonalMap2.Obstacle obstacle1 = this.obstacles.get(int10);
                        if (obstacle1.vehicle != null && obstacle1.nodeCrawlFront != null) {
                            obstacle0.connectCrawlNodes(this, obstacle1);
                            obstacle1.connectCrawlNodes(this, obstacle0);
                        }
                    }
                }
            }
        }

        void checkEdgeIntersection() {
            for (int int0 = 0; int0 < this.obstacles.size(); int0++) {
                PolygonalMap2.Obstacle obstacle0 = this.obstacles.get(int0);

                for (int int1 = int0 + 1; int1 < this.obstacles.size(); int1++) {
                    PolygonalMap2.Obstacle obstacle1 = this.obstacles.get(int1);
                    if (obstacle0.bounds.intersects(obstacle1.bounds)) {
                        this.checkEdgeIntersection(obstacle0.outer, obstacle1.outer);

                        for (int int2 = 0; int2 < obstacle1.inner.size(); int2++) {
                            PolygonalMap2.EdgeRing edgeRing0 = obstacle1.inner.get(int2);
                            this.checkEdgeIntersection(obstacle0.outer, edgeRing0);
                        }

                        for (int int3 = 0; int3 < obstacle0.inner.size(); int3++) {
                            PolygonalMap2.EdgeRing edgeRing1 = obstacle0.inner.get(int3);
                            this.checkEdgeIntersection(edgeRing1, obstacle1.outer);

                            for (int int4 = 0; int4 < obstacle1.inner.size(); int4++) {
                                PolygonalMap2.EdgeRing edgeRing2 = obstacle1.inner.get(int4);
                                this.checkEdgeIntersection(edgeRing1, edgeRing2);
                            }
                        }
                    }
                }
            }

            for (int int5 = 0; int5 < this.obstacles.size(); int5++) {
                PolygonalMap2.Obstacle obstacle2 = this.obstacles.get(int5);
                this.checkEdgeIntersectionSplit(obstacle2.outer);

                for (int int6 = 0; int6 < obstacle2.inner.size(); int6++) {
                    this.checkEdgeIntersectionSplit(obstacle2.inner.get(int6));
                }
            }
        }

        void checkEdgeIntersection(PolygonalMap2.EdgeRing edgeRing0, PolygonalMap2.EdgeRing edgeRing1) {
            for (int int0 = 0; int0 < edgeRing0.size(); int0++) {
                PolygonalMap2.Edge edge0 = edgeRing0.get(int0);

                for (int int1 = 0; int1 < edgeRing1.size(); int1++) {
                    PolygonalMap2.Edge edge1 = edgeRing1.get(int1);
                    if (this.intersects(edge0.node1, edge0.node2, edge1)) {
                        PolygonalMap2.Intersection intersection = this.getIntersection(edge0, edge1);
                        if (intersection != null) {
                            edge0.intersections.add(intersection);
                            edge1.intersections.add(intersection);
                            this.nodes.add(intersection.nodeSplit);
                            this.intersectNodes.add(intersection.nodeSplit);
                        }
                    }
                }
            }
        }

        void checkEdgeIntersectionSplit(PolygonalMap2.EdgeRing edgeRing) {
            for (int int0 = edgeRing.size() - 1; int0 >= 0; int0--) {
                PolygonalMap2.Edge edge0 = edgeRing.get(int0);
                if (!edge0.intersections.isEmpty()) {
                    comparator.edge = edge0;
                    Collections.sort(edge0.intersections, comparator);

                    for (int int1 = edge0.intersections.size() - 1; int1 >= 0; int1--) {
                        PolygonalMap2.Intersection intersection = edge0.intersections.get(int1);
                        PolygonalMap2.Edge edge1 = intersection.split(edge0);
                        this.edges.add(edge1);
                    }
                }
            }
        }

        void checkNodesInObstacles() {
            for (int int0 = 0; int0 < this.nodes.size(); int0++) {
                PolygonalMap2.Node node0 = this.nodes.get(int0);

                for (int int1 = 0; int1 < this.obstacles.size(); int1++) {
                    PolygonalMap2.Obstacle obstacle0 = this.obstacles.get(int1);
                    if (obstacle0.isNodeInsideOf(node0)) {
                        node0.ignore = true;
                        break;
                    }
                }
            }

            for (int int2 = 0; int2 < this.perimeterNodes.size(); int2++) {
                PolygonalMap2.Node node1 = this.perimeterNodes.get(int2);

                for (int int3 = 0; int3 < this.obstacles.size(); int3++) {
                    PolygonalMap2.Obstacle obstacle1 = this.obstacles.get(int3);
                    if (obstacle1.isNodeInsideOf(node1)) {
                        node1.ignore = true;
                        break;
                    }
                }
            }
        }

        void addPerimeterEdges() {
            PolygonalMap2.VehicleRect vehicleRect0 = this.cluster.bounds();
            vehicleRect0.x--;
            vehicleRect0.y--;
            vehicleRect0.w += 2;
            vehicleRect0.h += 2;
            PolygonalMap2.ClusterOutlineGrid clusterOutlineGridx = clusterOutlineGrid.setSize(vehicleRect0.w, vehicleRect0.h);
            int int0 = this.cluster.z;

            for (int int1 = 0; int1 < this.cluster.rects.size(); int1++) {
                PolygonalMap2.VehicleRect vehicleRect1 = this.cluster.rects.get(int1);
                vehicleRect1 = PolygonalMap2.VehicleRect.alloc()
                    .init(vehicleRect1.x - 1, vehicleRect1.y - 1, vehicleRect1.w + 2, vehicleRect1.h + 2, vehicleRect1.z);

                for (int int2 = vehicleRect1.top(); int2 < vehicleRect1.bottom(); int2++) {
                    for (int int3 = vehicleRect1.left(); int3 < vehicleRect1.right(); int3++) {
                        clusterOutlineGridx.setInner(int3 - vehicleRect0.left(), int2 - vehicleRect0.top(), int0);
                    }
                }

                vehicleRect1.release();
            }

            for (int int4 = 0; int4 < vehicleRect0.h; int4++) {
                for (int int5 = 0; int5 < vehicleRect0.w; int5++) {
                    PolygonalMap2.ClusterOutline clusterOutline0 = clusterOutlineGridx.get(int5, int4, int0);
                    if (clusterOutline0.inner) {
                        if (!clusterOutlineGridx.isInner(int5 - 1, int4, int0)) {
                            clusterOutline0.w = true;
                        }

                        if (!clusterOutlineGridx.isInner(int5, int4 - 1, int0)) {
                            clusterOutline0.n = true;
                        }

                        if (!clusterOutlineGridx.isInner(int5 + 1, int4, int0)) {
                            clusterOutline0.e = true;
                        }

                        if (!clusterOutlineGridx.isInner(int5, int4 + 1, int0)) {
                            clusterOutline0.s = true;
                        }
                    }
                }
            }

            for (int int6 = 0; int6 < vehicleRect0.h; int6++) {
                for (int int7 = 0; int7 < vehicleRect0.w; int7++) {
                    PolygonalMap2.ClusterOutline clusterOutline1 = clusterOutlineGridx.get(int7, int6, int0);
                    if (clusterOutline1 != null
                        && (clusterOutline1.w || clusterOutline1.n || clusterOutline1.e || clusterOutline1.s || clusterOutline1.innerCorner)) {
                        PolygonalMap2.Square square = PolygonalMap2.instance.getSquare(vehicleRect0.x + int7, vehicleRect0.y + int6, int0);
                        if (square != null && !square.isNonThumpableSolid() && !square.has(504)) {
                            PolygonalMap2.Node node0 = PolygonalMap2.instance.getNodeForSquare(square);
                            node0.flags |= 8;
                            node0.addGraph(this);
                            this.perimeterNodes.add(node0);
                        }
                    }

                    if (clusterOutline1 != null
                        && clusterOutline1.n
                        && clusterOutline1.w
                        && clusterOutline1.inner
                        && !(clusterOutline1.tw | clusterOutline1.tn | clusterOutline1.te | clusterOutline1.ts)) {
                        ArrayList arrayList = clusterOutlineGridx.trace(clusterOutline1);
                        if (!arrayList.isEmpty()) {
                            for (int int8 = 0; int8 < arrayList.size() - 1; int8++) {
                                PolygonalMap2.Node node1 = (PolygonalMap2.Node)arrayList.get(int8);
                                PolygonalMap2.Node node2 = (PolygonalMap2.Node)arrayList.get(int8 + 1);
                                node1.x = node1.x + vehicleRect0.left();
                                node1.y = node1.y + vehicleRect0.top();
                                PolygonalMap2.Edge edge = PolygonalMap2.Edge.alloc().init(node1, node2, null, null);
                                this.perimeterEdges.add(edge);
                            }

                            if (arrayList.get(arrayList.size() - 1) != arrayList.get(0)) {
                                PolygonalMap2.Node node3 = (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1);
                                node3.x = node3.x + vehicleRect0.left();
                                node3 = (PolygonalMap2.Node)arrayList.get(arrayList.size() - 1);
                                node3.y = node3.y + vehicleRect0.top();
                            }
                        }
                    }
                }
            }

            clusterOutlineGridx.releaseElements();
            vehicleRect0.release();
        }

        void calculateNodeVisibility() {
            ArrayList arrayList = new ArrayList();
            arrayList.addAll(this.nodes);
            arrayList.addAll(this.perimeterNodes);

            for (int int0 = 0; int0 < arrayList.size(); int0++) {
                PolygonalMap2.Node node0 = (PolygonalMap2.Node)arrayList.get(int0);
                if (!node0.ignore && (node0.square == null || !node0.square.has(504))) {
                    for (int int1 = int0 + 1; int1 < arrayList.size(); int1++) {
                        PolygonalMap2.Node node1 = (PolygonalMap2.Node)arrayList.get(int1);
                        if (!node1.ignore && (node1.square == null || !node1.square.has(504)) && (!node0.hasFlag(8) || !node1.hasFlag(8))) {
                            if (node0.isConnectedTo(node1)) {
                                assert node0.square != null && (node0.square.isCanPathW() || node0.square.isCanPathN())
                                    || node1.square != null && (node1.square.isCanPathW() || node1.square.isCanPathN());
                            } else if (this.isVisible(node0, node1)) {
                                PolygonalMap2.instance.connectTwoNodes(node0, node1);
                            }
                        }
                    }
                }
            }
        }

        void addNode(PolygonalMap2.Node node0) {
            if (this.created && !node0.ignore) {
                ArrayList arrayList = new ArrayList();
                arrayList.addAll(this.nodes);
                arrayList.addAll(this.perimeterNodes);

                for (int int0 = 0; int0 < arrayList.size(); int0++) {
                    PolygonalMap2.Node node1 = (PolygonalMap2.Node)arrayList.get(int0);
                    if (!node1.ignore && this.isVisible(node1, node0)) {
                        PolygonalMap2.instance.connectTwoNodes(node0, node1);
                    }
                }
            }

            this.nodes.add(node0);
        }

        void removeNode(PolygonalMap2.Node node) {
            this.nodes.remove(node);

            for (int int0 = node.visible.size() - 1; int0 >= 0; int0--) {
                PolygonalMap2.Connection connection = node.visible.get(int0);
                PolygonalMap2.instance.breakConnection(connection);
            }
        }

        boolean contains(float float0, float float1, int int1) {
            for (int int0 = 0; int0 < this.cluster.rects.size(); int0++) {
                PolygonalMap2.VehicleRect vehicleRect = this.cluster.rects.get(int0);
                if (vehicleRect.containsPoint(float0, float1, int1)) {
                    return true;
                }
            }

            return false;
        }

        boolean contains(float float0, float float1, int int2, int int1) {
            for (int int0 = 0; int0 < this.cluster.rects.size(); int0++) {
                PolygonalMap2.VehicleRect vehicleRect = this.cluster.rects.get(int0);
                if (vehicleRect.containsPoint(float0, float1, int2, int1)) {
                    return true;
                }
            }

            return false;
        }

        boolean contains(PolygonalMap2.Square square) {
            for (int int0 = 0; int0 < this.cluster.rects.size(); int0++) {
                PolygonalMap2.VehicleRect vehicleRect = this.cluster.rects.get(int0);
                if (vehicleRect.containsPoint(square.x + 0.5F, square.y + 0.5F, square.z)) {
                    return true;
                }
            }

            return false;
        }

        boolean contains(PolygonalMap2.Square square, int int1) {
            for (int int0 = 0; int0 < this.cluster.rects.size(); int0++) {
                PolygonalMap2.VehicleRect vehicleRect = this.cluster.rects.get(int0);
                if (vehicleRect.containsPoint(square.x + 0.5F, square.y + 0.5F, square.z, int1)) {
                    return true;
                }
            }

            return false;
        }

        private int getPointOutsideObstacles(float float2, float float3, float float4, PolygonalMap2.AdjustStartEndNodeData adjustStartEndNodeData) {
            PolygonalMap2.ClosestPointOnEdge closestPointOnEdge = PolygonalMap2.instance.closestPointOnEdge;
            double double0 = Double.MAX_VALUE;
            PolygonalMap2.Edge edge = null;
            PolygonalMap2.Node node = null;
            float float0 = 0.0F;
            float float1 = 0.0F;

            for (int int0 = 0; int0 < this.obstacles.size(); int0++) {
                PolygonalMap2.Obstacle obstacle = this.obstacles.get(int0);
                if (obstacle.bounds.containsPoint(float2, float3) && obstacle.isPointInside(float2, float3)) {
                    obstacle.getClosestPointOnEdge(float2, float3, closestPointOnEdge);
                    if (closestPointOnEdge.edge != null && closestPointOnEdge.distSq < double0) {
                        double0 = closestPointOnEdge.distSq;
                        edge = closestPointOnEdge.edge;
                        node = closestPointOnEdge.node;
                        float0 = closestPointOnEdge.point.x;
                        float1 = closestPointOnEdge.point.y;
                    }
                }
            }

            if (edge != null) {
                closestPointOnEdge.edge = edge;
                closestPointOnEdge.node = node;
                closestPointOnEdge.point.set(float0, float1);
                closestPointOnEdge.distSq = double0;
                if (edge.obstacle.splitEdgeAtNearestPoint(closestPointOnEdge, (int)float4, adjustStartEndNodeData)) {
                    adjustStartEndNodeData.graph = this;
                    if (adjustStartEndNodeData.isNodeNew) {
                        this.edges.add(adjustStartEndNodeData.newEdge);
                        this.addNode(adjustStartEndNodeData.node);
                    }

                    return 1;
                } else {
                    return -1;
                }
            } else {
                return 0;
            }
        }

        PolygonalMap2.Node getClosestNodeTo(float float2, float float3) {
            PolygonalMap2.Node node0 = null;
            float float0 = Float.MAX_VALUE;

            for (int int0 = 0; int0 < this.nodes.size(); int0++) {
                PolygonalMap2.Node node1 = this.nodes.get(int0);
                float float1 = IsoUtils.DistanceToSquared(node1.x, node1.y, float2, float3);
                if (float1 < float0) {
                    node0 = node1;
                    float0 = float1;
                }
            }

            return node0;
        }

        void create() {
            for (int int0 = 0; int0 < this.cluster.rects.size(); int0++) {
                PolygonalMap2.VehicleRect vehicleRect = this.cluster.rects.get(int0);
                this.addEdgesForVehicle(vehicleRect.vehicle);
            }

            this.addWorldObstaclesClipper();

            for (int int1 = 0; int1 < this.obstacles.size(); int1++) {
                PolygonalMap2.Obstacle obstacle = this.obstacles.get(int1);
                if (obstacle.vehicle == null) {
                    this.splitWorldObstacleEdges(obstacle.outer);

                    for (int int2 = 0; int2 < obstacle.inner.size(); int2++) {
                        this.splitWorldObstacleEdges(obstacle.inner.get(int2));
                    }
                }
            }

            this.checkEdgeIntersection();
            this.checkNodesInObstacles();
            this.calculateNodeVisibility();
            this.connectVehicleCrawlNodes();
            this.created = true;
        }

        static PolygonalMap2.VisibilityGraph alloc() {
            return pool.isEmpty() ? new PolygonalMap2.VisibilityGraph() : pool.pop();
        }

        void release() {
            for (int int0 = 0; int0 < this.nodes.size(); int0++) {
                if (!PolygonalMap2.instance.squareToNode.containsValue(this.nodes.get(int0))) {
                    this.nodes.get(int0).release();
                }
            }

            for (int int1 = 0; int1 < this.perimeterEdges.size(); int1++) {
                this.perimeterEdges.get(int1).node1.release();
                this.perimeterEdges.get(int1).release();
            }

            for (int int2 = 0; int2 < this.obstacles.size(); int2++) {
                PolygonalMap2.Obstacle obstacle = this.obstacles.get(int2);
                obstacle.release();
            }

            for (int int3 = 0; int3 < this.cluster.rects.size(); int3++) {
                this.cluster.rects.get(int3).release();
            }

            this.cluster.release();

            assert !pool.contains(this);

            pool.push(this);
        }

        void render() {
            float float0 = 1.0F;

            for (PolygonalMap2.Edge edge0 : this.perimeterEdges) {
                LineDrawer.addLine(edge0.node1.x, edge0.node1.y, this.cluster.z, edge0.node2.x, edge0.node2.y, this.cluster.z, float0, 0.5F, 0.5F, null, true);
                float0 = 1.0F - float0;
            }

            for (PolygonalMap2.Obstacle obstacle : this.obstacles) {
                float0 = 1.0F;

                for (PolygonalMap2.Edge edge1 : obstacle.outer) {
                    LineDrawer.addLine(
                        edge1.node1.x, edge1.node1.y, this.cluster.z, edge1.node2.x, edge1.node2.y, this.cluster.z, float0, 0.5F, 0.5F, null, true
                    );
                    float0 = 1.0F - float0;
                }

                for (PolygonalMap2.EdgeRing edgeRing : obstacle.inner) {
                    for (PolygonalMap2.Edge edge2 : edgeRing) {
                        LineDrawer.addLine(
                            edge2.node1.x, edge2.node1.y, this.cluster.z, edge2.node2.x, edge2.node2.y, this.cluster.z, float0, 0.5F, 0.5F, null, true
                        );
                        float0 = 1.0F - float0;
                    }
                }

                if (DebugOptions.instance.PolymapRenderCrawling.getValue()) {
                    for (PolygonalMap2.Node node0 : obstacle.crawlNodes) {
                        LineDrawer.addLine(
                            node0.x - 0.05F, node0.y - 0.05F, this.cluster.z, node0.x + 0.05F, node0.y + 0.05F, this.cluster.z, 0.5F, 1.0F, 0.5F, null, false
                        );

                        for (PolygonalMap2.Connection connection0 : node0.visible) {
                            PolygonalMap2.Node node1 = connection0.otherNode(node0);
                            if (node1.hasFlag(1)) {
                                LineDrawer.addLine(node0.x, node0.y, this.cluster.z, node1.x, node1.y, this.cluster.z, 0.5F, 1.0F, 0.5F, null, true);
                            }
                        }
                    }
                }
            }

            for (PolygonalMap2.Node node2 : this.perimeterNodes) {
                if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
                    for (PolygonalMap2.Connection connection1 : node2.visible) {
                        PolygonalMap2.Node node3 = connection1.otherNode(node2);
                        LineDrawer.addLine(node2.x, node2.y, this.cluster.z, node3.x, node3.y, this.cluster.z, 0.0F, 0.25F, 0.0F, null, true);
                    }
                }

                if (DebugOptions.instance.PolymapRenderNodes.getValue()) {
                    float float1 = 1.0F;
                    float float2 = 0.5F;
                    float float3 = 0.0F;
                    if (node2.ignore) {
                        float2 = 1.0F;
                    }

                    LineDrawer.addLine(
                        node2.x - 0.05F, node2.y - 0.05F, this.cluster.z, node2.x + 0.05F, node2.y + 0.05F, this.cluster.z, float1, float2, float3, null, false
                    );
                }
            }

            for (PolygonalMap2.Node node4 : this.nodes) {
                if (DebugOptions.instance.PolymapRenderConnections.getValue()) {
                    for (PolygonalMap2.Connection connection2 : node4.visible) {
                        PolygonalMap2.Node node5 = connection2.otherNode(node4);
                        if (this.nodes.contains(node5)) {
                            LineDrawer.addLine(node4.x, node4.y, this.cluster.z, node5.x, node5.y, this.cluster.z, 0.0F, 1.0F, 0.0F, null, true);
                        }
                    }
                }

                if (DebugOptions.instance.PolymapRenderNodes.getValue() || node4.ignore) {
                    LineDrawer.addLine(
                        node4.x - 0.05F, node4.y - 0.05F, this.cluster.z, node4.x + 0.05F, node4.y + 0.05F, this.cluster.z, 1.0F, 1.0F, 0.0F, null, false
                    );
                }
            }

            for (PolygonalMap2.Node node6 : this.intersectNodes) {
                LineDrawer.addLine(
                    node6.x - 0.1F, node6.y - 0.1F, this.cluster.z, node6.x + 0.1F, node6.y + 0.1F, this.cluster.z, 1.0F, 0.0F, 0.0F, null, false
                );
            }
        }

        static final class CompareIntersection implements Comparator<PolygonalMap2.Intersection> {
            PolygonalMap2.Edge edge;

            public int compare(PolygonalMap2.Intersection intersection0, PolygonalMap2.Intersection intersection1) {
                float float0 = this.edge == intersection0.edge1 ? intersection0.dist1 : intersection0.dist2;
                float float1 = this.edge == intersection1.edge1 ? intersection1.dist1 : intersection1.dist2;
                if (float0 < float1) {
                    return -1;
                } else {
                    return float0 > float1 ? 1 : 0;
                }
            }
        }
    }
}
