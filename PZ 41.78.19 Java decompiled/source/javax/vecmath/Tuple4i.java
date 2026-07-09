// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4i implements Serializable, Cloneable {
    static final long serialVersionUID = 8064614250942616720L;
    public int x;
    public int y;
    public int z;
    public int w;

    public Tuple4i(int int0, int int1, int int2, int int3) {
        this.x = int0;
        this.y = int1;
        this.z = int2;
        this.w = int3;
    }

    public Tuple4i(int[] ints) {
        this.x = ints[0];
        this.y = ints[1];
        this.z = ints[2];
        this.w = ints[3];
    }

    public Tuple4i(Tuple4i tuple4i1) {
        this.x = tuple4i1.x;
        this.y = tuple4i1.y;
        this.z = tuple4i1.z;
        this.w = tuple4i1.w;
    }

    public Tuple4i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public final void set(int int0, int int1, int int2, int int3) {
        this.x = int0;
        this.y = int1;
        this.z = int2;
        this.w = int3;
    }

    public final void set(int[] ints) {
        this.x = ints[0];
        this.y = ints[1];
        this.z = ints[2];
        this.w = ints[3];
    }

    public final void set(Tuple4i tuple4i0) {
        this.x = tuple4i0.x;
        this.y = tuple4i0.y;
        this.z = tuple4i0.z;
        this.w = tuple4i0.w;
    }

    public final void get(int[] ints) {
        ints[0] = this.x;
        ints[1] = this.y;
        ints[2] = this.z;
        ints[3] = this.w;
    }

    public final void get(Tuple4i tuple4i1) {
        tuple4i1.x = this.x;
        tuple4i1.y = this.y;
        tuple4i1.z = this.z;
        tuple4i1.w = this.w;
    }

    public final void add(Tuple4i tuple4i1, Tuple4i tuple4i0) {
        this.x = tuple4i1.x + tuple4i0.x;
        this.y = tuple4i1.y + tuple4i0.y;
        this.z = tuple4i1.z + tuple4i0.z;
        this.w = tuple4i1.w + tuple4i0.w;
    }

    public final void add(Tuple4i tuple4i0) {
        this.x = this.x + tuple4i0.x;
        this.y = this.y + tuple4i0.y;
        this.z = this.z + tuple4i0.z;
        this.w = this.w + tuple4i0.w;
    }

    public final void sub(Tuple4i tuple4i1, Tuple4i tuple4i0) {
        this.x = tuple4i1.x - tuple4i0.x;
        this.y = tuple4i1.y - tuple4i0.y;
        this.z = tuple4i1.z - tuple4i0.z;
        this.w = tuple4i1.w - tuple4i0.w;
    }

    public final void sub(Tuple4i tuple4i0) {
        this.x = this.x - tuple4i0.x;
        this.y = this.y - tuple4i0.y;
        this.z = this.z - tuple4i0.z;
        this.w = this.w - tuple4i0.w;
    }

    public final void negate(Tuple4i tuple4i0) {
        this.x = -tuple4i0.x;
        this.y = -tuple4i0.y;
        this.z = -tuple4i0.z;
        this.w = -tuple4i0.w;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public final void scale(int int0, Tuple4i tuple4i0) {
        this.x = int0 * tuple4i0.x;
        this.y = int0 * tuple4i0.y;
        this.z = int0 * tuple4i0.z;
        this.w = int0 * tuple4i0.w;
    }

    public final void scale(int int0) {
        this.x *= int0;
        this.y *= int0;
        this.z *= int0;
        this.w *= int0;
    }

    public final void scaleAdd(int int0, Tuple4i tuple4i1, Tuple4i tuple4i0) {
        this.x = int0 * tuple4i1.x + tuple4i0.x;
        this.y = int0 * tuple4i1.y + tuple4i0.y;
        this.z = int0 * tuple4i1.z + tuple4i0.z;
        this.w = int0 * tuple4i1.w + tuple4i0.w;
    }

    public final void scaleAdd(int int0, Tuple4i tuple4i0) {
        this.x = int0 * this.x + tuple4i0.x;
        this.y = int0 * this.y + tuple4i0.y;
        this.z = int0 * this.z + tuple4i0.z;
        this.w = int0 * this.w + tuple4i0.w;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple4i tuple4i0 = (Tuple4i)object;
            return this.x == tuple4i0.x && this.y == tuple4i0.y && this.z == tuple4i0.z && this.w == tuple4i0.w;
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
        long0 = 31L * long0 + this.z;
        long0 = 31L * long0 + this.w;
        return (int)(long0 ^ long0 >> 32);
    }

    public final void clamp(int int1, int int0, Tuple4i tuple4i0) {
        if (tuple4i0.x > int0) {
            this.x = int0;
        } else if (tuple4i0.x < int1) {
            this.x = int1;
        } else {
            this.x = tuple4i0.x;
        }

        if (tuple4i0.y > int0) {
            this.y = int0;
        } else if (tuple4i0.y < int1) {
            this.y = int1;
        } else {
            this.y = tuple4i0.y;
        }

        if (tuple4i0.z > int0) {
            this.z = int0;
        } else if (tuple4i0.z < int1) {
            this.z = int1;
        } else {
            this.z = tuple4i0.z;
        }

        if (tuple4i0.w > int0) {
            this.w = int0;
        } else if (tuple4i0.w < int1) {
            this.w = int1;
        } else {
            this.w = tuple4i0.w;
        }
    }

    public final void clampMin(int int0, Tuple4i tuple4i0) {
        if (tuple4i0.x < int0) {
            this.x = int0;
        } else {
            this.x = tuple4i0.x;
        }

        if (tuple4i0.y < int0) {
            this.y = int0;
        } else {
            this.y = tuple4i0.y;
        }

        if (tuple4i0.z < int0) {
            this.z = int0;
        } else {
            this.z = tuple4i0.z;
        }

        if (tuple4i0.w < int0) {
            this.w = int0;
        } else {
            this.w = tuple4i0.w;
        }
    }

    public final void clampMax(int int0, Tuple4i tuple4i0) {
        if (tuple4i0.x > int0) {
            this.x = int0;
        } else {
            this.x = tuple4i0.x;
        }

        if (tuple4i0.y > int0) {
            this.y = int0;
        } else {
            this.y = tuple4i0.y;
        }

        if (tuple4i0.z > int0) {
            this.z = int0;
        } else {
            this.z = tuple4i0.z;
        }

        if (tuple4i0.w > int0) {
            this.w = int0;
        } else {
            this.w = tuple4i0.z;
        }
    }

    public final void absolute(Tuple4i tuple4i0) {
        this.x = Math.abs(tuple4i0.x);
        this.y = Math.abs(tuple4i0.y);
        this.z = Math.abs(tuple4i0.z);
        this.w = Math.abs(tuple4i0.w);
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

        if (this.z > int0) {
            this.z = int0;
        } else if (this.z < int1) {
            this.z = int1;
        }

        if (this.w > int0) {
            this.w = int0;
        } else if (this.w < int1) {
            this.w = int1;
        }
    }

    public final void clampMin(int int0) {
        if (this.x < int0) {
            this.x = int0;
        }

        if (this.y < int0) {
            this.y = int0;
        }

        if (this.z < int0) {
            this.z = int0;
        }

        if (this.w < int0) {
            this.w = int0;
        }
    }

    public final void clampMax(int int0) {
        if (this.x > int0) {
            this.x = int0;
        }

        if (this.y > int0) {
            this.y = int0;
        }

        if (this.z > int0) {
            this.z = int0;
        }

        if (this.w > int0) {
            this.w = int0;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
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

    public final int getZ() {
        return this.z;
    }

    public final void setZ(int int0) {
        this.z = int0;
    }

    public final int getW() {
        return this.w;
    }

    public final void setW(int int0) {
        this.w = int0;
    }
}
