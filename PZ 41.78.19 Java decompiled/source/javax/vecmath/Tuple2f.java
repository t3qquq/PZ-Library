// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple2f implements Serializable, Cloneable {
    static final long serialVersionUID = 9011180388985266884L;
    public float x;
    public float y;

    public Tuple2f(float float0, float float1) {
        this.x = float0;
        this.y = float1;
    }

    public Tuple2f(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
    }

    public Tuple2f(Tuple2f tuple2f1) {
        this.x = tuple2f1.x;
        this.y = tuple2f1.y;
    }

    public Tuple2f(Tuple2d tuple2d) {
        this.x = (float)tuple2d.x;
        this.y = (float)tuple2d.y;
    }

    public Tuple2f() {
        this.x = 0.0F;
        this.y = 0.0F;
    }

    public final void set(float float0, float float1) {
        this.x = float0;
        this.y = float1;
    }

    public final void set(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
    }

    public final void set(Tuple2f tuple2f0) {
        this.x = tuple2f0.x;
        this.y = tuple2f0.y;
    }

    public final void set(Tuple2d tuple2d) {
        this.x = (float)tuple2d.x;
        this.y = (float)tuple2d.y;
    }

    public final void get(float[] floats) {
        floats[0] = this.x;
        floats[1] = this.y;
    }

    public final void add(Tuple2f tuple2f1, Tuple2f tuple2f0) {
        this.x = tuple2f1.x + tuple2f0.x;
        this.y = tuple2f1.y + tuple2f0.y;
    }

    public final void add(Tuple2f tuple2f0) {
        this.x = this.x + tuple2f0.x;
        this.y = this.y + tuple2f0.y;
    }

    public final void sub(Tuple2f tuple2f1, Tuple2f tuple2f0) {
        this.x = tuple2f1.x - tuple2f0.x;
        this.y = tuple2f1.y - tuple2f0.y;
    }

    public final void sub(Tuple2f tuple2f0) {
        this.x = this.x - tuple2f0.x;
        this.y = this.y - tuple2f0.y;
    }

    public final void negate(Tuple2f tuple2f0) {
        this.x = -tuple2f0.x;
        this.y = -tuple2f0.y;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public final void scale(float float0, Tuple2f tuple2f0) {
        this.x = float0 * tuple2f0.x;
        this.y = float0 * tuple2f0.y;
    }

    public final void scale(float float0) {
        this.x *= float0;
        this.y *= float0;
    }

    public final void scaleAdd(float float0, Tuple2f tuple2f1, Tuple2f tuple2f0) {
        this.x = float0 * tuple2f1.x + tuple2f0.x;
        this.y = float0 * tuple2f1.y + tuple2f0.y;
    }

    public final void scaleAdd(float float0, Tuple2f tuple2f0) {
        this.x = float0 * this.x + tuple2f0.x;
        this.y = float0 * this.y + tuple2f0.y;
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.x);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.y);
        return (int)(long0 ^ long0 >> 32);
    }

    public boolean equals(Tuple2f tuple2f0) {
        try {
            return this.x == tuple2f0.x && this.y == tuple2f0.y;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple2f tuple2f0 = (Tuple2f)object;
            return this.x == tuple2f0.x && this.y == tuple2f0.y;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple2f tuple2f0, float float1) {
        float float0 = this.x - tuple2f0.x;
        if (Float.isNaN(float0)) {
            return false;
        } else if ((float0 < 0.0F ? -float0 : float0) > float1) {
            return false;
        } else {
            float0 = this.y - tuple2f0.y;
            return Float.isNaN(float0) ? false : !((float0 < 0.0F ? -float0 : float0) > float1);
        }
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public final void clamp(float float1, float float0, Tuple2f tuple2f0) {
        if (tuple2f0.x > float0) {
            this.x = float0;
        } else if (tuple2f0.x < float1) {
            this.x = float1;
        } else {
            this.x = tuple2f0.x;
        }

        if (tuple2f0.y > float0) {
            this.y = float0;
        } else if (tuple2f0.y < float1) {
            this.y = float1;
        } else {
            this.y = tuple2f0.y;
        }
    }

    public final void clampMin(float float0, Tuple2f tuple2f0) {
        if (tuple2f0.x < float0) {
            this.x = float0;
        } else {
            this.x = tuple2f0.x;
        }

        if (tuple2f0.y < float0) {
            this.y = float0;
        } else {
            this.y = tuple2f0.y;
        }
    }

    public final void clampMax(float float0, Tuple2f tuple2f0) {
        if (tuple2f0.x > float0) {
            this.x = float0;
        } else {
            this.x = tuple2f0.x;
        }

        if (tuple2f0.y > float0) {
            this.y = float0;
        } else {
            this.y = tuple2f0.y;
        }
    }

    public final void absolute(Tuple2f tuple2f0) {
        this.x = Math.abs(tuple2f0.x);
        this.y = Math.abs(tuple2f0.y);
    }

    public final void clamp(float float1, float float0) {
        if (this.x > float0) {
            this.x = float0;
        } else if (this.x < float1) {
            this.x = float1;
        }

        if (this.y > float0) {
            this.y = float0;
        } else if (this.y < float1) {
            this.y = float1;
        }
    }

    public final void clampMin(float float0) {
        if (this.x < float0) {
            this.x = float0;
        }

        if (this.y < float0) {
            this.y = float0;
        }
    }

    public final void clampMax(float float0) {
        if (this.x > float0) {
            this.x = float0;
        }

        if (this.y > float0) {
            this.y = float0;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
    }

    public final void interpolate(Tuple2f tuple2f1, Tuple2f tuple2f0, float float0) {
        this.x = (1.0F - float0) * tuple2f1.x + float0 * tuple2f0.x;
        this.y = (1.0F - float0) * tuple2f1.y + float0 * tuple2f0.y;
    }

    public final void interpolate(Tuple2f tuple2f0, float float0) {
        this.x = (1.0F - float0) * this.x + float0 * tuple2f0.x;
        this.y = (1.0F - float0) * this.y + float0 * tuple2f0.y;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    public final float getX() {
        return this.x;
    }

    public final void setX(float float0) {
        this.x = float0;
    }

    public final float getY() {
        return this.y;
    }

    public final void setY(float float0) {
        this.y = float0;
    }
}
