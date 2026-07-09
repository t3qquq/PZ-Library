// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Point4d extends Tuple4d implements Serializable {
    static final long serialVersionUID = 1733471895962736949L;

    public Point4d(double double0, double double1, double double2, double double3) {
        super(double0, double1, double2, double3);
    }

    public Point4d(double[] doubles) {
        super(doubles);
    }

    public Point4d(Point4d point4d1) {
        super(point4d1);
    }

    public Point4d(Point4f point4f) {
        super(point4f);
    }

    public Point4d(Tuple4f tuple4f) {
        super(tuple4f);
    }

    public Point4d(Tuple4d tuple4d) {
        super(tuple4d);
    }

    public Point4d(Tuple3d tuple3d) {
        super(tuple3d.x, tuple3d.y, tuple3d.z, 1.0);
    }

    public Point4d() {
    }

    public final void set(Tuple3d tuple3d) {
        this.x = tuple3d.x;
        this.y = tuple3d.y;
        this.z = tuple3d.z;
        this.w = 1.0;
    }

    public final double distanceSquared(Point4d point4d0) {
        double double0 = this.x - point4d0.x;
        double double1 = this.y - point4d0.y;
        double double2 = this.z - point4d0.z;
        double double3 = this.w - point4d0.w;
        return double0 * double0 + double1 * double1 + double2 * double2 + double3 * double3;
    }

    public final double distance(Point4d point4d0) {
        double double0 = this.x - point4d0.x;
        double double1 = this.y - point4d0.y;
        double double2 = this.z - point4d0.z;
        double double3 = this.w - point4d0.w;
        return Math.sqrt(double0 * double0 + double1 * double1 + double2 * double2 + double3 * double3);
    }

    public final double distanceL1(Point4d point4d0) {
        return Math.abs(this.x - point4d0.x) + Math.abs(this.y - point4d0.y) + Math.abs(this.z - point4d0.z) + Math.abs(this.w - point4d0.w);
    }

    public final double distanceLinf(Point4d point4d0) {
        double double0 = Math.max(Math.abs(this.x - point4d0.x), Math.abs(this.y - point4d0.y));
        double double1 = Math.max(Math.abs(this.z - point4d0.z), Math.abs(this.w - point4d0.w));
        return Math.max(double0, double1);
    }

    public final void project(Point4d point4d0) {
        double double0 = 1.0 / point4d0.w;
        this.x = point4d0.x * double0;
        this.y = point4d0.y * double0;
        this.z = point4d0.z * double0;
        this.w = 1.0;
    }
}
