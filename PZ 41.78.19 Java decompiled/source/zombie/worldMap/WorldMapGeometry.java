// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import zombie.core.math.PZMath;
import zombie.vehicles.Clipper;

public final class WorldMapGeometry {
    public WorldMapGeometry.Type m_type;
    public final ArrayList<WorldMapPoints> m_points = new ArrayList<>();
    public int m_minX;
    public int m_minY;
    public int m_maxX;
    public int m_maxY;
    public float[] m_triangles = null;
    public ArrayList<WorldMapGeometry.TrianglesPerZoom> m_trianglesPerZoom = null;
    public int m_vboIndex1 = -1;
    public int m_vboIndex2 = -1;
    public int m_vboIndex3 = -1;
    public int m_vboIndex4 = -1;
    private static Clipper s_clipper = null;
    private static ByteBuffer s_vertices = null;

    public void calculateBounds() {
        this.m_minX = this.m_minY = Integer.MAX_VALUE;
        this.m_maxX = this.m_maxY = Integer.MIN_VALUE;

        for (int int0 = 0; int0 < this.m_points.size(); int0++) {
            WorldMapPoints worldMapPoints = this.m_points.get(int0);
            worldMapPoints.calculateBounds();
            this.m_minX = PZMath.min(this.m_minX, worldMapPoints.m_minX);
            this.m_minY = PZMath.min(this.m_minY, worldMapPoints.m_minY);
            this.m_maxX = PZMath.max(this.m_maxX, worldMapPoints.m_maxX);
            this.m_maxY = PZMath.max(this.m_maxY, worldMapPoints.m_maxY);
        }
    }

    public boolean containsPoint(float float0, float float1) {
        return this.m_type == WorldMapGeometry.Type.Polygon && !this.m_points.isEmpty()
            ? this.isPointInPolygon_WindingNumber(float0, float1, 0) != WorldMapGeometry.PolygonHit.Outside
            : false;
    }

    public void triangulate(double[] doubles) {
        if (s_clipper == null) {
            s_clipper = new Clipper();
        }

        s_clipper.clear();
        WorldMapPoints worldMapPoints0 = this.m_points.get(0);
        if (s_vertices == null || s_vertices.capacity() < worldMapPoints0.size() * 50 * 4) {
            s_vertices = ByteBuffer.allocateDirect(worldMapPoints0.size() * 50 * 4);
        }

        s_vertices.clear();
        if (worldMapPoints0.isClockwise()) {
            for (int int0 = worldMapPoints0.numPoints() - 1; int0 >= 0; int0--) {
                s_vertices.putFloat(worldMapPoints0.getX(int0));
                s_vertices.putFloat(worldMapPoints0.getY(int0));
            }
        } else {
            for (int int1 = 0; int1 < worldMapPoints0.numPoints(); int1++) {
                s_vertices.putFloat(worldMapPoints0.getX(int1));
                s_vertices.putFloat(worldMapPoints0.getY(int1));
            }
        }

        s_clipper.addPath(worldMapPoints0.numPoints(), s_vertices, false);

        for (int int2 = 1; int2 < this.m_points.size(); int2++) {
            s_vertices.clear();
            WorldMapPoints worldMapPoints1 = this.m_points.get(int2);
            if (worldMapPoints1.isClockwise()) {
                for (int int3 = worldMapPoints1.numPoints() - 1; int3 >= 0; int3--) {
                    s_vertices.putFloat(worldMapPoints1.getX(int3));
                    s_vertices.putFloat(worldMapPoints1.getY(int3));
                }
            } else {
                for (int int4 = 0; int4 < worldMapPoints1.numPoints(); int4++) {
                    s_vertices.putFloat(worldMapPoints1.getX(int4));
                    s_vertices.putFloat(worldMapPoints1.getY(int4));
                }
            }

            s_clipper.addPath(worldMapPoints1.numPoints(), s_vertices, true);
        }

        if (this.m_minX < 0 || this.m_minY < 0 || this.m_maxX > 300 || this.m_maxY > 300) {
            short short0 = 900;
            float float0 = -short0;
            float float1 = -short0;
            float float2 = 300 + short0;
            float float3 = -short0;
            float float4 = 300 + short0;
            float float5 = 300 + short0;
            float float6 = -short0;
            float float7 = 300 + short0;
            float float8 = -short0;
            float float9 = 0.0F;
            float float10 = 0.0F;
            float float11 = 0.0F;
            float float12 = 0.0F;
            float float13 = 300.0F;
            float float14 = 300.0F;
            float float15 = 300.0F;
            float float16 = 300.0F;
            float float17 = 0.0F;
            float float18 = -short0;
            float float19 = 0.0F;
            s_vertices.clear();
            s_vertices.putFloat(float0).putFloat(float1);
            s_vertices.putFloat(float2).putFloat(float3);
            s_vertices.putFloat(float4).putFloat(float5);
            s_vertices.putFloat(float6).putFloat(float7);
            s_vertices.putFloat(float8).putFloat(float9);
            s_vertices.putFloat(float10).putFloat(float11);
            s_vertices.putFloat(float12).putFloat(float13);
            s_vertices.putFloat(float14).putFloat(float15);
            s_vertices.putFloat(float16).putFloat(float17);
            s_vertices.putFloat(float18).putFloat(float19);
            s_clipper.addPath(10, s_vertices, true);
        }

        int int5 = s_clipper.generatePolygons(0.0);
        if (int5 > 0) {
            s_vertices.clear();
            int int6 = s_clipper.triangulate(0, s_vertices);
            this.m_triangles = new float[int6 * 2];

            for (int int7 = 0; int7 < int6; int7++) {
                this.m_triangles[int7 * 2] = s_vertices.getFloat();
                this.m_triangles[int7 * 2 + 1] = s_vertices.getFloat();
            }

            if (doubles != null) {
                for (int int8 = 0; int8 < doubles.length; int8++) {
                    double double0 = doubles[int8] - (int8 == 0 ? 0.0 : doubles[int8 - 1]);
                    int5 = s_clipper.generatePolygons(double0);
                    if (int5 > 0) {
                        s_vertices.clear();
                        int6 = s_clipper.triangulate(0, s_vertices);
                        WorldMapGeometry.TrianglesPerZoom trianglesPerZoom = new WorldMapGeometry.TrianglesPerZoom();
                        trianglesPerZoom.m_triangles = new float[int6 * 2];
                        trianglesPerZoom.m_delta = doubles[int8];

                        for (int int9 = 0; int9 < int6; int9++) {
                            trianglesPerZoom.m_triangles[int9 * 2] = s_vertices.getFloat();
                            trianglesPerZoom.m_triangles[int9 * 2 + 1] = s_vertices.getFloat();
                        }

                        if (this.m_trianglesPerZoom == null) {
                            this.m_trianglesPerZoom = new ArrayList<>();
                        }

                        this.m_trianglesPerZoom.add(trianglesPerZoom);
                    }
                }
            }
        }
    }

    WorldMapGeometry.TrianglesPerZoom findTriangles(double double0) {
        if (this.m_trianglesPerZoom == null) {
            return null;
        } else {
            for (int int0 = 0; int0 < this.m_trianglesPerZoom.size(); int0++) {
                WorldMapGeometry.TrianglesPerZoom trianglesPerZoom = this.m_trianglesPerZoom.get(int0);
                if (trianglesPerZoom.m_delta == double0) {
                    return trianglesPerZoom;
                }
            }

            return null;
        }
    }

    public void dispose() {
        this.m_points.clear();
        this.m_triangles = null;
    }

    float isLeft(float float3, float float1, float float5, float float0, float float2, float float4) {
        return (float5 - float3) * (float4 - float1) - (float2 - float3) * (float0 - float1);
    }

    WorldMapGeometry.PolygonHit isPointInPolygon_WindingNumber(float float1, float float0, int var3) {
        int int0 = 0;
        WorldMapPoints worldMapPoints = this.m_points.get(0);

        for (int int1 = 0; int1 < worldMapPoints.numPoints(); int1++) {
            int int2 = worldMapPoints.getX(int1);
            int int3 = worldMapPoints.getY(int1);
            int int4 = worldMapPoints.getX((int1 + 1) % worldMapPoints.numPoints());
            int int5 = worldMapPoints.getY((int1 + 1) % worldMapPoints.numPoints());
            if (int3 <= float0) {
                if (int5 > float0 && this.isLeft(int2, int3, int4, int5, float1, float0) > 0.0F) {
                    int0++;
                }
            } else if (int5 <= float0 && this.isLeft(int2, int3, int4, int5, float1, float0) < 0.0F) {
                int0--;
            }
        }

        return int0 == 0 ? WorldMapGeometry.PolygonHit.Outside : WorldMapGeometry.PolygonHit.Inside;
    }

    private static enum PolygonHit {
        OnEdge,
        Inside,
        Outside;
    }

    public static final class TrianglesPerZoom {
        public float[] m_triangles;
        double m_delta;
    }

    public static enum Type {
        LineString,
        Point,
        Polygon;
    }
}
