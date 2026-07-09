// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Point3d extends Tuple3d implements Serializable {
    static final long serialVersionUID = 5718062286069042927L;

    public Point3d(double double0, double double1, double double2) {
        super(double0, double1, double2);
    }

    public Point3d(double[] doubles) {
        super(doubles);
    }

    public Point3d(Point3d point3d1) {
        super(point3d1);
    }

    public Point3d(Point3f point3f) {
        super(point3f);
    }

    public Point3d(Tuple3f tuple3f) {
        super(tuple3f);
    }

    public Point3d(Tuple3d tuple3d) {
        super(tuple3d);
    }

    public Point3d() {
    }

    public final double distanceSquared(Point3d point3d0) {
        double double0 = this.x - point3d0.x;
        double double1 = this.y - point3d0.y;
        double double2 = this.z - point3d0.z;
        return double0 * double0 + double1 * double1 + double2 * double2;
    }

    public final double distance(Point3d point3d0) {
        double double0 = this.x - point3d0.x;
        double double1 = this.y - point3d0.y;
        double double2 = this.z - point3d0.z;
        return Math.sqrt(double0 * double0 + double1 * double1 + double2 * double2);
    }

    public final double distanceL1(Point3d point3d0) {
        return Math.abs(this.x - point3d0.x) + Math.abs(this.y - point3d0.y) + Math.abs(this.z - point3d0.z);
    }

    public final double distanceLinf(Point3d point3d0) {
        double double0 = Math.max(Math.abs(this.x - point3d0.x), Math.abs(this.y - point3d0.y));
        return Math.max(double0, Math.abs(this.z - point3d0.z));
    }

    public final void project(Point4d point4d) {
        double double0 = 1.0 / point4d.w;
        this.x = point4d.x * double0;
        this.y = point4d.y * double0;
        this.z = point4d.z * double0;
    }
}
