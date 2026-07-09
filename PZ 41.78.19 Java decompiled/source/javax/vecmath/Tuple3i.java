// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple3i implements Serializable, Cloneable {
    static final long serialVersionUID = -732740491767276200L;
    public int x;
    public int y;
    public int z;

    public Tuple3i(int int0, int int1, int int2) {
        this.x = int0;
        this.y = int1;
        this.z = int2;
    }

    public Tuple3i(int[] ints) {
        this.x = ints[0];
        this.y = ints[1];
        this.z = ints[2];
    }

    public Tuple3i(Tuple3i tuple3i1) {
        this.x = tuple3i1.x;
        this.y = tuple3i1.y;
        this.z = tuple3i1.z;
    }

    public Tuple3i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public final void set(int int0, int int1, int int2) {
        this.x = int0;
        this.y = int1;
        this.z = int2;
    }

    public final void set(int[] ints) {
        this.x = ints[0];
        this.y = ints[1];
        this.z = ints[2];
    }

    public final void set(Tuple3i tuple3i0) {
        this.x = tuple3i0.x;
        this.y = tuple3i0.y;
        this.z = tuple3i0.z;
    }

    public final void get(int[] ints) {
        ints[0] = this.x;
        ints[1] = this.y;
        ints[2] = this.z;
    }

    public final void get(Tuple3i tuple3i1) {
        tuple3i1.x = this.x;
        tuple3i1.y = this.y;
        tuple3i1.z = this.z;
    }

    public final void add(Tuple3i tuple3i1, Tuple3i tuple3i0) {
        this.x = tuple3i1.x + tuple3i0.x;
        this.y = tuple3i1.y + tuple3i0.y;
        this.z = tuple3i1.z + tuple3i0.z;
    }

    public final void add(Tuple3i tuple3i0) {
        this.x = this.x + tuple3i0.x;
        this.y = this.y + tuple3i0.y;
        this.z = this.z + tuple3i0.z;
    }

    public final void sub(Tuple3i tuple3i1, Tuple3i tuple3i0) {
        this.x = tuple3i1.x - tuple3i0.x;
        this.y = tuple3i1.y - tuple3i0.y;
        this.z = tuple3i1.z - tuple3i0.z;
    }

    public final void sub(Tuple3i tuple3i0) {
        this.x = this.x - tuple3i0.x;
        this.y = this.y - tuple3i0.y;
        this.z = this.z - tuple3i0.z;
    }

    public final void negate(Tuple3i tuple3i0) {
        this.x = -tuple3i0.x;
        this.y = -tuple3i0.y;
        this.z = -tuple3i0.z;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public final void scale(int int0, Tuple3i tuple3i0) {
        this.x = int0 * tuple3i0.x;
        this.y = int0 * tuple3i0.y;
        this.z = int0 * tuple3i0.z;
    }

    public final void scale(int int0) {
        this.x *= int0;
        this.y *= int0;
        this.z *= int0;
    }

    public final void scaleAdd(int int0, Tuple3i tuple3i1, Tuple3i tuple3i0) {
        this.x = int0 * tuple3i1.x + tuple3i0.x;
        this.y = int0 * tuple3i1.y + tuple3i0.y;
        this.z = int0 * tuple3i1.z + tuple3i0.z;
    }

    public final void scaleAdd(int int0, Tuple3i tuple3i0) {
        this.x = int0 * this.x + tuple3i0.x;
        this.y = int0 * this.y + tuple3i0.y;
        this.z = int0 * this.z + tuple3i0.z;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple3i tuple3i0 = (Tuple3i)object;
            return this.x == tuple3i0.x && this.y == tuple3i0.y && this.z == tuple3i0.z;
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
        return (int)(long0 ^ long0 >> 32);
    }

    public final void clamp(int int1, int int0, Tuple3i tuple3i0) {
        if (tuple3i0.x > int0) {
            this.x = int0;
        } else if (tuple3i0.x < int1) {
            this.x = int1;
        } else {
            this.x = tuple3i0.x;
        }

        if (tuple3i0.y > int0) {
            this.y = int0;
        } else if (tuple3i0.y < int1) {
            this.y = int1;
        } else {
            this.y = tuple3i0.y;
        }

        if (tuple3i0.z > int0) {
            this.z = int0;
        } else if (tuple3i0.z < int1) {
            this.z = int1;
        } else {
            this.z = tuple3i0.z;
        }
    }

    public final void clampMin(int int0, Tuple3i tuple3i0) {
        if (tuple3i0.x < int0) {
            this.x = int0;
        } else {
            this.x = tuple3i0.x;
        }

        if (tuple3i0.y < int0) {
            this.y = int0;
        } else {
            this.y = tuple3i0.y;
        }

        if (tuple3i0.z < int0) {
            this.z = int0;
        } else {
            this.z = tuple3i0.z;
        }
    }

    public final void clampMax(int int0, Tuple3i tuple3i0) {
        if (tuple3i0.x > int0) {
            this.x = int0;
        } else {
            this.x = tuple3i0.x;
        }

        if (tuple3i0.y > int0) {
            this.y = int0;
        } else {
            this.y = tuple3i0.y;
        }

        if (tuple3i0.z > int0) {
            this.z = int0;
        } else {
            this.z = tuple3i0.z;
        }
    }

    public final void absolute(Tuple3i tuple3i0) {
        this.x = Math.abs(tuple3i0.x);
        this.y = Math.abs(tuple3i0.y);
        this.z = Math.abs(tuple3i0.z);
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
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
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
}
