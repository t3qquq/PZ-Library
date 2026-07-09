// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Point4f extends Tuple4f implements Serializable {
    static final long serialVersionUID = 4643134103185764459L;

    public Point4f(float float0, float float1, float float2, float float3) {
        super(float0, float1, float2, float3);
    }

    public Point4f(float[] floats) {
        super(floats);
    }

    public Point4f(Point4f point4f1) {
        super(point4f1);
    }

    public Point4f(Point4d point4d) {
        super(point4d);
    }

    public Point4f(Tuple4f tuple4f) {
        super(tuple4f);
    }

    public Point4f(Tuple4d tuple4d) {
        super(tuple4d);
    }

    public Point4f(Tuple3f tuple3f) {
        super(tuple3f.x, tuple3f.y, tuple3f.z, 1.0F);
    }

    public Point4f() {
    }

    public final void set(Tuple3f tuple3f) {
        this.x = tuple3f.x;
        this.y = tuple3f.y;
        this.z = tuple3f.z;
        this.w = 1.0F;
    }

    public final float distanceSquared(Point4f point4f0) {
        float float0 = this.x - point4f0.x;
        float float1 = this.y - point4f0.y;
        float float2 = this.z - point4f0.z;
        float float3 = this.w - point4f0.w;
        return float0 * float0 + float1 * float1 + float2 * float2 + float3 * float3;
    }

    public final float distance(Point4f point4f0) {
        float float0 = this.x - point4f0.x;
        float float1 = this.y - point4f0.y;
        float float2 = this.z - point4f0.z;
        float float3 = this.w - point4f0.w;
        return (float)Math.sqrt(float0 * float0 + float1 * float1 + float2 * float2 + float3 * float3);
    }

    public final float distanceL1(Point4f point4f0) {
        return Math.abs(this.x - point4f0.x) + Math.abs(this.y - point4f0.y) + Math.abs(this.z - point4f0.z) + Math.abs(this.w - point4f0.w);
    }

    public final float distanceLinf(Point4f point4f0) {
        float float0 = Math.max(Math.abs(this.x - point4f0.x), Math.abs(this.y - point4f0.y));
        float float1 = Math.max(Math.abs(this.z - point4f0.z), Math.abs(this.w - point4f0.w));
        return Math.max(float0, float1);
    }

    public final void project(Point4f point4f0) {
        float float0 = 1.0F / point4f0.w;
        this.x = point4f0.x * float0;
        this.y = point4f0.y * float0;
        this.z = point4f0.z * float0;
        this.w = 1.0F;
    }
}
