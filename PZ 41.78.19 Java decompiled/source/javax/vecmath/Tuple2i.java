// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple2i implements Serializable, Cloneable {
    static final long serialVersionUID = -3555701650170169638L;
    public int x;
    public int y;

    public Tuple2i(int int0, int int1) {
        this.x = int0;
        this.y = int1;
    }

    public Tuple2i(int[] ints) {
        this.x = ints[0];
        this.y = ints[1];
    }

    public Tuple2i(Tuple2i tuple2i1) {
        this.x = tuple2i1.x;
        this.y = tuple2i1.y;
    }

    public Tuple2i() {
        this.x = 0;
        this.y = 0;
    }

    public final void set(int int0, int int1) {
        this.x = int0;
        this.y = int1;
    }

    public final void set(int[] ints) {
        this.x = ints[0];
        this.y = ints[1];
    }

    public final void set(Tuple2i tuple2i0) {
        this.x = tuple2i0.x;
        this.y = tuple2i0.y;
    }

    public final void get(int[] ints) {
        ints[0] = this.x;
        ints[1] = this.y;
    }

    public final void get(Tuple2i tuple2i1) {
        tuple2i1.x = this.x;
        tuple2i1.y = this.y;
    }

    public final void add(Tuple2i tuple2i1, Tuple2i tuple2i0) {
        this.x = tuple2i1.x + tuple2i0.x;
        this.y = tuple2i1.y + tuple2i0.y;
    }

    public final void add(Tuple2i tuple2i0) {
        this.x = this.x + tuple2i0.x;
        this.y = this.y + tuple2i0.y;
    }

    public final void sub(Tuple2i tuple2i1, Tuple2i tuple2i0) {
        this.x = tuple2i1.x - tuple2i0.x;
        this.y = tuple2i1.y - tuple2i0.y;
    }

    public final void sub(Tuple2i tuple2i0) {
        this.x = this.x - tuple2i0.x;
        this.y = this.y - tuple2i0.y;
    }

    public final void negate(Tuple2i tuple2i0) {
        this.x = -tuple2i0.x;
        this.y = -tuple2i0.y;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public final void scale(int int0, Tuple2i tuple2i0) {
        this.x = int0 * tuple2i0.x;
        this.y = int0 * tuple2i0.y;
    }

    public final void scale(int int0) {
        this.x *= int0;
        this.y *= int0;
    }

    public final void scaleAdd(int int0, Tuple2i tuple2i1, Tuple2i tuple2i0) {
        this.x = int0 * tuple2i1.x + tuple2i0.x;
        this.y = int0 * tuple2i1.y + tuple2i0.y;
    }

    public final void scaleAdd(int int0, Tuple2i tuple2i0) {
        this.x = int0 * this.x + tuple2i0.x;
        this.y = int0 * this.y + tuple2i0.y;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple2i tuple2i0 = (Tuple2i)object;
            return this.x == tuple2i0.x && this.y == tuple2i0.y;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + this.x;
        long0 = 31L * long0 + this.y;
        return (int)(long0 ^ long0 >> 32);
    }

    public final void clamp(int int1, int int0, Tuple2i tuple2i0) {
        if (tuple2i0.x > int0) {
            this.x = int0;
        } else if (tuple2i0.x < int1) {
            this.x = int1;
        } else {
            this.x = tuple2i0.x;
        }

        if (tuple2i0.y > int0) {
            this.y = int0;
        } else if (tuple2i0.y < int1) {
            this.y = int1;
        } else {
            this.y = tuple2i0.y;
        }
    }

    public final void clampMin(int int0, Tuple2i tuple2i0) {
        if (tuple2i0.x < int0) {
            this.x = int0;
        } else {
            this.x = tuple2i0.x;
        }

        if (tuple2i0.y < int0) {
            this.y = int0;
        } else {
            this.y = tuple2i0.y;
        }
    }

    public final void clampMax(int int0, Tuple2i tuple2i0) {
        if (tuple2i0.x > int0) {
            this.x = int0;
        } else {
            this.x = tuple2i0.x;
        }

        if (tuple2i0.y > int0) {
            this.y = int0;
        } else {
            this.y = tuple2i0.y;
        }
    }

    public final void absolute(Tuple2i tuple2i0) {
        this.x = Math.abs(tuple2i0.x);
        this.y = Math.abs(tuple2i0.y);
    }

    public final void clamp(int int1, int int0) {
        if (this.x > int0) {
            this.x = int0;
        } else if (this.x < int1) {
            this.x = int1;
        }

        if (this.y > int0) {
            this.y = int0;
        } else if (this.y < int1) {
            this.y = int1;
        }
    }

    public final void clampMin(int int0) {
        if (this.x < int0) {
            this.x = int0;
        }

        if (this.y < int0) {
            this.y = int0;
        }
    }

    public final void clampMax(int int0) {
        if (this.x > int0) {
            this.x = int0;
        }

        if (this.y > int0) {
            this.y = int0;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    public final int getX() {
        return this.x;
    }

    public final void setX(int int0) {
        this.x = int0;
    }

    public final int getY() {
        return this.y;
    }

    public final void setY(int int0) {
        this.y = int0;
    }
}
