// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.function.BiConsumer;

public final class Rasterize {
    final Rasterize.Edge edge1 = new Rasterize.Edge();
    final Rasterize.Edge edge2 = new Rasterize.Edge();
    final Rasterize.Edge edge3 = new Rasterize.Edge();

    void scanLine(int int1, int int2, int int3, BiConsumer<Integer, Integer> biConsumer) {
        for (int int0 = int1; int0 < int2; int0++) {
            biConsumer.accept(int0, int3);
        }
    }

    void scanSpan(Rasterize.Edge edge1x, Rasterize.Edge edge0, int int1, int int3, BiConsumer<Integer, Integer> biConsumer) {
        int int0 = (int)Math.max((double)int1, Math.floor(edge0.y0));
        int int2 = (int)Math.min((double)int3, Math.ceil(edge0.y1));
        if (edge1x.x0 == edge0.x0 && edge1x.y0 == edge0.y0) {
            if (edge1x.x0 + edge0.dy / edge1x.dy * edge1x.dx < edge0.x1) {
                Rasterize.Edge edge2x = edge1x;
                edge1x = edge0;
                edge0 = edge2x;
            }
        } else if (edge1x.x1 - edge0.dy / edge1x.dy * edge1x.dx < edge0.x0) {
            Rasterize.Edge edge3x = edge1x;
            edge1x = edge0;
            edge0 = edge3x;
        }

        double double0 = edge1x.dx / edge1x.dy;
        double double1 = edge0.dx / edge0.dy;
        double double2 = edge1x.dx > 0.0F ? 1.0 : 0.0;
        double double3 = edge0.dx < 0.0F ? 1.0 : 0.0;

        for (int int4 = int0; int4 < int2; int4++) {
            double double4 = double0 * Math.max(0.0, Math.min((double)edge1x.dy, int4 + double2 - edge1x.y0)) + edge1x.x0;
            double double5 = double1 * Math.max(0.0, Math.min((double)edge0.dy, int4 + double3 - edge0.y0)) + edge0.x0;
            this.scanLine((int)Math.floor(double5), (int)Math.ceil(double4), int4, biConsumer);
        }
    }

    void scanTriangle(
        float float0, float float1, float float2, float float3, float float4, float float5, int int0, int int1, BiConsumer<Integer, Integer> biConsumer
    ) {
        Rasterize.Edge edge0 = this.edge1.init(float0, float1, float2, float3);
        Rasterize.Edge edge1x = this.edge2.init(float2, float3, float4, float5);
        Rasterize.Edge edge2x = this.edge3.init(float4, float5, float0, float1);
        if (edge0.dy > edge2x.dy) {
            Rasterize.Edge edge3x = edge0;
            edge0 = edge2x;
            edge2x = edge3x;
        }

        if (edge1x.dy > edge2x.dy) {
            Rasterize.Edge edge4 = edge1x;
            edge1x = edge2x;
            edge2x = edge4;
        }

        if (edge0.dy > 0.0F) {
            this.scanSpan(edge2x, edge0, int0, int1, biConsumer);
        }

        if (edge1x.dy > 0.0F) {
            this.scanSpan(edge2x, edge1x, int0, int1, biConsumer);
        }
    }

    private static final class Edge {
        float x0;
        float y0;
        float x1;
        float y1;
        float dx;
        float dy;

        Rasterize.Edge init(float float3, float float0, float float2, float float1) {
            if (float0 > float1) {
                this.x0 = float2;
                this.y0 = float1;
                this.x1 = float3;
                this.y1 = float0;
            } else {
                this.x0 = float3;
                this.y0 = float0;
                this.x1 = float2;
                this.y1 = float1;
            }

            this.dx = this.x1 - this.x0;
            this.dy = this.y1 - this.y0;
            return this;
        }
    }
}
