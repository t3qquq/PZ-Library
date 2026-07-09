// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Point2f extends Tuple2f implements Serializable {
    static final long serialVersionUID = -4801347926528714435L;

    public Point2f(float float0, float float1) {
        super(float0, float1);
    }

    public Point2f(float[] floats) {
        super(floats);
    }

    public Point2f(Point2f point2f1) {
        super(point2f1);
    }

    public Point2f(Point2d point2d) {
        super(point2d);
    }

    public Point2f(Tuple2d tuple2d) {
        super(tuple2d);
    }

    public Point2f(Tuple2f tuple2f) {
        super(tuple2f);
    }

    public Point2f() {
    }

    public final float distanceSquared(Point2f point2f0) {
        float float0 = this.x - point2f0.x;
        float float1 = this.y - point2f0.y;
        return float0 * float0 + float1 * float1;
    }

    public final float distance(Point2f point2f0) {
        float float0 = this.x - point2f0.x;
        float float1 = this.y - point2f0.y;
        return (float)Math.sqrt(float0 * float0 + float1 * float1);
    }

    public final float distanceL1(Point2f point2f0) {
        return Math.abs(this.x - point2f0.x) + Math.abs(this.y - point2f0.y);
    }

    public final float distanceLinf(Point2f point2f0) {
        return Math.max(Math.abs(this.x - point2f0.x), Math.abs(this.y - point2f0.y));
    }
}
