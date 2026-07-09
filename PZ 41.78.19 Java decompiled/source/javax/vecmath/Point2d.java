// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Point2d extends Tuple2d implements Serializable {
    static final long serialVersionUID = 1133748791492571954L;

    public Point2d(double double0, double double1) {
        super(double0, double1);
    }

    public Point2d(double[] doubles) {
        super(doubles);
    }

    public Point2d(Point2d point2d1) {
        super(point2d1);
    }

    public Point2d(Point2f point2f) {
        super(point2f);
    }

    public Point2d(Tuple2d tuple2d) {
        super(tuple2d);
    }

    public Point2d(Tuple2f tuple2f) {
        super(tuple2f);
    }

    public Point2d() {
    }

    public final double distanceSquared(Point2d point2d0) {
        double double0 = this.x - point2d0.x;
        double double1 = this.y - point2d0.y;
        return double0 * double0 + double1 * double1;
    }

    public final double distance(Point2d point2d0) {
        double double0 = this.x - point2d0.x;
        double double1 = this.y - point2d0.y;
        return Math.sqrt(double0 * double0 + double1 * double1);
    }

    public final double distanceL1(Point2d point2d0) {
        return Math.abs(this.x - point2d0.x) + Math.abs(this.y - point2d0.y);
    }

    public final double distanceLinf(Point2d point2d0) {
        return Math.max(Math.abs(this.x - point2d0.x), Math.abs(this.y - point2d0.y));
    }
}
