// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Vector2f extends Tuple2f implements Serializable {
    static final long serialVersionUID = -2168194326883512320L;

    public Vector2f(float float0, float float1) {
        super(float0, float1);
    }

    public Vector2f(float[] floats) {
        super(floats);
    }

    public Vector2f(Vector2f vector2f1) {
        super(vector2f1);
    }

    public Vector2f(Vector2d vector2d) {
        super(vector2d);
    }

    public Vector2f(Tuple2f tuple2f) {
        super(tuple2f);
    }

    public Vector2f(Tuple2d tuple2d) {
        super(tuple2d);
    }

    public Vector2f() {
    }

    public final float dot(Vector2f vector2f0) {
        return this.x * vector2f0.x + this.y * vector2f0.y;
    }

    public final float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public final float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public final void normalize(Vector2f vector2f0) {
        float float0 = (float)(1.0 / Math.sqrt(vector2f0.x * vector2f0.x + vector2f0.y * vector2f0.y));
        this.x = vector2f0.x * float0;
        this.y = vector2f0.y * float0;
    }

    public final void normalize() {
        float float0 = (float)(1.0 / Math.sqrt(this.x * this.x + this.y * this.y));
        this.x *= float0;
        this.y *= float0;
    }

    public final float angle(Vector2f vector2f0) {
        double double0 = this.dot(vector2f0) / (this.length() * vector2f0.length());
        if (double0 < -1.0) {
            double0 = -1.0;
        }

        if (double0 > 1.0) {
            double0 = 1.0;
        }

        return (float)Math.acos(double0);
    }
}
