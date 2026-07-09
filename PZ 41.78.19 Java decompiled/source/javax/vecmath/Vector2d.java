// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Vector2d extends Tuple2d implements Serializable {
    static final long serialVersionUID = 8572646365302599857L;

    public Vector2d(double double0, double double1) {
        super(double0, double1);
    }

    public Vector2d(double[] doubles) {
        super(doubles);
    }

    public Vector2d(Vector2d vector2d1) {
        super(vector2d1);
    }

    public Vector2d(Vector2f vector2f) {
        super(vector2f);
    }

    public Vector2d(Tuple2d tuple2d) {
        super(tuple2d);
    }

    public Vector2d(Tuple2f tuple2f) {
        super(tuple2f);
    }

    public Vector2d() {
    }

    public final double dot(Vector2d vector2d0) {
        return this.x * vector2d0.x + this.y * vector2d0.y;
    }

    public final double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public final double lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public final void normalize(Vector2d vector2d0) {
        double double0 = 1.0 / Math.sqrt(vector2d0.x * vector2d0.x + vector2d0.y * vector2d0.y);
        this.x = vector2d0.x * double0;
        this.y = vector2d0.y * double0;
    }

    public final void normalize() {
        double double0 = 1.0 / Math.sqrt(this.x * this.x + this.y * this.y);
        this.x *= double0;
        this.y *= double0;
    }

    public final double angle(Vector2d vector2d0) {
        double double0 = this.dot(vector2d0) / (this.length() * vector2d0.length());
        if (double0 < -1.0) {
            double0 = -1.0;
        }

        if (double0 > 1.0) {
            double0 = 1.0;
        }

        return Math.acos(double0);
    }
}
