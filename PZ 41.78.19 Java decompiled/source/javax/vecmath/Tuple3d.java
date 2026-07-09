// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple3d implements Serializable, Cloneable {
    static final long serialVersionUID = 5542096614926168415L;
    public double x;
    public double y;
    public double z;

    public Tuple3d(double double0, double double1, double double2) {
        this.x = double0;
        this.y = double1;
        this.z = double2;
    }

    public Tuple3d(double[] doubles) {
        this.x = doubles[0];
        this.y = doubles[1];
        this.z = doubles[2];
    }

    public Tuple3d(Tuple3d tuple3d1) {
        this.x = tuple3d1.x;
        this.y = tuple3d1.y;
        this.z = tuple3d1.z;
    }

    public Tuple3d(Tuple3f tuple3f) {
        this.x = tuple3f.x;
        this.y = tuple3f.y;
        this.z = tuple3f.z;
    }

    public Tuple3d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
    }

    public final void set(double double0, double double1, double double2) {
        this.x = double0;
        this.y = double1;
        this.z = double2;
    }

    public final void set(double[] doubles) {
        this.x = doubles[0];
        this.y = doubles[1];
        this.z = doubles[2];
    }

    public final void set(Tuple3d tuple3d0) {
        this.x = tuple3d0.x;
        this.y = tuple3d0.y;
        this.z = tuple3d0.z;
    }

    public final void set(Tuple3f tuple3f) {
        this.x = tuple3f.x;
        this.y = tuple3f.y;
        this.z = tuple3f.z;
    }

    public final void get(double[] doubles) {
        doubles[0] = this.x;
        doubles[1] = this.y;
        doubles[2] = this.z;
    }

    public final void get(Tuple3d tuple3d1) {
        tuple3d1.x = this.x;
        tuple3d1.y = this.y;
        tuple3d1.z = this.z;
    }

    public final void add(Tuple3d tuple3d1, Tuple3d tuple3d0) {
        this.x = tuple3d1.x + tuple3d0.x;
        this.y = tuple3d1.y + tuple3d0.y;
        this.z = tuple3d1.z + tuple3d0.z;
    }

    public final void add(Tuple3d tuple3d0) {
        this.x = this.x + tuple3d0.x;
        this.y = this.y + tuple3d0.y;
        this.z = this.z + tuple3d0.z;
    }

    public final void sub(Tuple3d tuple3d1, Tuple3d tuple3d0) {
        this.x = tuple3d1.x - tuple3d0.x;
        this.y = tuple3d1.y - tuple3d0.y;
        this.z = tuple3d1.z - tuple3d0.z;
    }

    public final void sub(Tuple3d tuple3d0) {
        this.x = this.x - tuple3d0.x;
        this.y = this.y - tuple3d0.y;
        this.z = this.z - tuple3d0.z;
    }

    public final void negate(Tuple3d tuple3d0) {
        this.x = -tuple3d0.x;
        this.y = -tuple3d0.y;
        this.z = -tuple3d0.z;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public final void scale(double double0, Tuple3d tuple3d0) {
        this.x = double0 * tuple3d0.x;
        this.y = double0 * tuple3d0.y;
        this.z = double0 * tuple3d0.z;
    }

    public final void scale(double double0) {
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
    }

    public final void scaleAdd(double double0, Tuple3d tuple3d1, Tuple3d tuple3d0) {
        this.x = double0 * tuple3d1.x + tuple3d0.x;
        this.y = double0 * tuple3d1.y + tuple3d0.y;
        this.z = double0 * tuple3d1.z + tuple3d0.z;
    }

    /** @deprecated */
    public final void scaleAdd(double double0, Tuple3f tuple3f) {
        this.scaleAdd(double0, new Point3d(tuple3f));
    }

    public final void scaleAdd(double double0, Tuple3d tuple3d0) {
        this.x = double0 * this.x + tuple3d0.x;
        this.y = double0 * this.y + tuple3d0.y;
        this.z = double0 * this.z + tuple3d0.z;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.x);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.y);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.z);
        return (int)(long0 ^ long0 >> 32);
    }

    public boolean equals(Tuple3d tuple3d0) {
        try {
            return this.x == tuple3d0.x && this.y == tuple3d0.y && this.z == tuple3d0.z;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple3d tuple3d0 = (Tuple3d)object;
            return this.x == tuple3d0.x && this.y == tuple3d0.y && this.z == tuple3d0.z;
        } catch (ClassCastException classCastException) {
            return false;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple3d tuple3d0, double double1) {
        double double0 = this.x - tuple3d0.x;
        if (Double.isNaN(double0)) {
            return false;
        } else if ((double0 < 0.0 ? -double0 : double0) > double1) {
            return false;
        } else {
            double0 = this.y - tuple3d0.y;
            if (Double.isNaN(double0)) {
                return false;
            } else if ((double0 < 0.0 ? -double0 : double0) > double1) {
                return false;
            } else {
                double0 = this.z - tuple3d0.z;
                return Double.isNaN(double0) ? false : !((double0 < 0.0 ? -double0 : double0) > double1);
            }
        }
    }

    /** @deprecated */
    public final void clamp(float float1, float float0, Tuple3d tuple3d1) {
        this.clamp((double)float1, (double)float0, tuple3d1);
    }

    public final void clamp(double double1, double double0, Tuple3d tuple3d0) {
        if (tuple3d0.x > double0) {
            this.x = double0;
        } else if (tuple3d0.x < double1) {
            this.x = double1;
        } else {
            this.x = tuple3d0.x;
        }

        if (tuple3d0.y > double0) {
            this.y = double0;
        } else if (tuple3d0.y < double1) {
            this.y = double1;
        } else {
            this.y = tuple3d0.y;
        }

        if (tuple3d0.z > double0) {
            this.z = double0;
        } else if (tuple3d0.z < double1) {
            this.z = double1;
        } else {
            this.z = tuple3d0.z;
        }
    }

    /** @deprecated */
    public final void clampMin(float float0, Tuple3d tuple3d1) {
        this.clampMin((double)float0, tuple3d1);
    }

    public final void clampMin(double double0, Tuple3d tuple3d0) {
        if (tuple3d0.x < double0) {
            this.x = double0;
        } else {
            this.x = tuple3d0.x;
        }

        if (tuple3d0.y < double0) {
            this.y = double0;
        } else {
            this.y = tuple3d0.y;
        }

        if (tuple3d0.z < double0) {
            this.z = double0;
        } else {
            this.z = tuple3d0.z;
        }
    }

    /** @deprecated */
    public final void clampMax(float float0, Tuple3d tuple3d1) {
        this.clampMax((double)float0, tuple3d1);
    }

    public final void clampMax(double double0, Tuple3d tuple3d0) {
        if (tuple3d0.x > double0) {
            this.x = double0;
        } else {
            this.x = tuple3d0.x;
        }

        if (tuple3d0.y > double0) {
            this.y = double0;
        } else {
            this.y = tuple3d0.y;
        }

        if (tuple3d0.z > double0) {
            this.z = double0;
        } else {
            this.z = tuple3d0.z;
        }
    }

    public final void absolute(Tuple3d tuple3d0) {
        this.x = Math.abs(tuple3d0.x);
        this.y = Math.abs(tuple3d0.y);
        this.z = Math.abs(tuple3d0.z);
    }

    /** @deprecated */
    public final void clamp(float float1, float float0) {
        this.clamp((double)float1, (double)float0);
    }

    public final void clamp(double double1, double double0) {
        if (this.x > double0) {
            this.x = double0;
        } else if (this.x < double1) {
            this.x = double1;
        }

        if (this.y > double0) {
            this.y = double0;
        } else if (this.y < double1) {
            this.y = double1;
        }

        if (this.z > double0) {
            this.z = double0;
        } else if (this.z < double1) {
            this.z = double1;
        }
    }

    /** @deprecated */
    public final void clampMin(float float0) {
        this.clampMin((double)float0);
    }

    public final void clampMin(double double0) {
        if (this.x < double0) {
            this.x = double0;
        }

        if (this.y < double0) {
            this.y = double0;
        }

        if (this.z < double0) {
            this.z = double0;
        }
    }

    /** @deprecated */
    public final void clampMax(float float0) {
        this.clampMax((double)float0);
    }

    public final void clampMax(double double0) {
        if (this.x > double0) {
            this.x = double0;
        }

        if (this.y > double0) {
            this.y = double0;
        }

        if (this.z > double0) {
            this.z = double0;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }

    /** @deprecated */
    public final void interpolate(Tuple3d tuple3d1, Tuple3d tuple3d2, float float0) {
        this.interpolate(tuple3d1, tuple3d2, (double)float0);
    }

    public final void interpolate(Tuple3d tuple3d1, Tuple3d tuple3d0, double double0) {
        this.x = (1.0 - double0) * tuple3d1.x + double0 * tuple3d0.x;
        this.y = (1.0 - double0) * tuple3d1.y + double0 * tuple3d0.y;
        this.z = (1.0 - double0) * tuple3d1.z + double0 * tuple3d0.z;
    }

    /** @deprecated */
    public final void interpolate(Tuple3d tuple3d1, float float0) {
        this.interpolate(tuple3d1, (double)float0);
    }

    public final void interpolate(Tuple3d tuple3d0, double double0) {
        this.x = (1.0 - double0) * this.x + double0 * tuple3d0.x;
        this.y = (1.0 - double0) * this.y + double0 * tuple3d0.y;
        this.z = (1.0 - double0) * this.z + double0 * tuple3d0.z;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    public final double getX() {
        return this.x;
    }

    public final void setX(double double0) {
        this.x = double0;
    }

    public final double getY() {
        return this.y;
    }

    public final void setY(double double0) {
        this.y = double0;
    }

    public final double getZ() {
        return this.z;
    }

    public final void setZ(double double0) {
        this.z = double0;
    }
}
