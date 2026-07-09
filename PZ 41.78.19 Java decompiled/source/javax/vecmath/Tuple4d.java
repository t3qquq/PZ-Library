// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4d implements Serializable, Cloneable {
    static final long serialVersionUID = -4748953690425311052L;
    public double x;
    public double y;
    public double z;
    public double w;

    public Tuple4d(double double0, double double1, double double2, double double3) {
        this.x = double0;
        this.y = double1;
        this.z = double2;
        this.w = double3;
    }

    public Tuple4d(double[] doubles) {
        this.x = doubles[0];
        this.y = doubles[1];
        this.z = doubles[2];
        this.w = doubles[3];
    }

    public Tuple4d(Tuple4d tuple4d1) {
        this.x = tuple4d1.x;
        this.y = tuple4d1.y;
        this.z = tuple4d1.z;
        this.w = tuple4d1.w;
    }

    public Tuple4d(Tuple4f tuple4f) {
        this.x = tuple4f.x;
        this.y = tuple4f.y;
        this.z = tuple4f.z;
        this.w = tuple4f.w;
    }

    public Tuple4d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 0.0;
    }

    public final void set(double double0, double double1, double double2, double double3) {
        this.x = double0;
        this.y = double1;
        this.z = double2;
        this.w = double3;
    }

    public final void set(double[] doubles) {
        this.x = doubles[0];
        this.y = doubles[1];
        this.z = doubles[2];
        this.w = doubles[3];
    }

    public final void set(Tuple4d tuple4d0) {
        this.x = tuple4d0.x;
        this.y = tuple4d0.y;
        this.z = tuple4d0.z;
        this.w = tuple4d0.w;
    }

    public final void set(Tuple4f tuple4f) {
        this.x = tuple4f.x;
        this.y = tuple4f.y;
        this.z = tuple4f.z;
        this.w = tuple4f.w;
    }

    public final void get(double[] doubles) {
        doubles[0] = this.x;
        doubles[1] = this.y;
        doubles[2] = this.z;
        doubles[3] = this.w;
    }

    public final void get(Tuple4d tuple4d1) {
        tuple4d1.x = this.x;
        tuple4d1.y = this.y;
        tuple4d1.z = this.z;
        tuple4d1.w = this.w;
    }

    public final void add(Tuple4d tuple4d1, Tuple4d tuple4d0) {
        this.x = tuple4d1.x + tuple4d0.x;
        this.y = tuple4d1.y + tuple4d0.y;
        this.z = tuple4d1.z + tuple4d0.z;
        this.w = tuple4d1.w + tuple4d0.w;
    }

    public final void add(Tuple4d tuple4d0) {
        this.x = this.x + tuple4d0.x;
        this.y = this.y + tuple4d0.y;
        this.z = this.z + tuple4d0.z;
        this.w = this.w + tuple4d0.w;
    }

    public final void sub(Tuple4d tuple4d1, Tuple4d tuple4d0) {
        this.x = tuple4d1.x - tuple4d0.x;
        this.y = tuple4d1.y - tuple4d0.y;
        this.z = tuple4d1.z - tuple4d0.z;
        this.w = tuple4d1.w - tuple4d0.w;
    }

    public final void sub(Tuple4d tuple4d0) {
        this.x = this.x - tuple4d0.x;
        this.y = this.y - tuple4d0.y;
        this.z = this.z - tuple4d0.z;
        this.w = this.w - tuple4d0.w;
    }

    public final void negate(Tuple4d tuple4d0) {
        this.x = -tuple4d0.x;
        this.y = -tuple4d0.y;
        this.z = -tuple4d0.z;
        this.w = -tuple4d0.w;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public final void scale(double double0, Tuple4d tuple4d0) {
        this.x = double0 * tuple4d0.x;
        this.y = double0 * tuple4d0.y;
        this.z = double0 * tuple4d0.z;
        this.w = double0 * tuple4d0.w;
    }

    public final void scale(double double0) {
        this.x *= double0;
        this.y *= double0;
        this.z *= double0;
        this.w *= double0;
    }

    public final void scaleAdd(double double0, Tuple4d tuple4d1, Tuple4d tuple4d0) {
        this.x = double0 * tuple4d1.x + tuple4d0.x;
        this.y = double0 * tuple4d1.y + tuple4d0.y;
        this.z = double0 * tuple4d1.z + tuple4d0.z;
        this.w = double0 * tuple4d1.w + tuple4d0.w;
    }

    /** @deprecated */
    public final void scaleAdd(float float0, Tuple4d tuple4d1) {
        this.scaleAdd((double)float0, tuple4d1);
    }

    public final void scaleAdd(double double0, Tuple4d tuple4d0) {
        this.x = double0 * this.x + tuple4d0.x;
        this.y = double0 * this.y + tuple4d0.y;
        this.z = double0 * this.z + tuple4d0.z;
        this.w = double0 * this.w + tuple4d0.w;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    public boolean equals(Tuple4d tuple4d0) {
        try {
            return this.x == tuple4d0.x && this.y == tuple4d0.y && this.z == tuple4d0.z && this.w == tuple4d0.w;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple4d tuple4d0 = (Tuple4d)object;
            return this.x == tuple4d0.x && this.y == tuple4d0.y && this.z == tuple4d0.z && this.w == tuple4d0.w;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple4d tuple4d0, double double1) {
        double double0 = this.x - tuple4d0.x;
        if (Double.isNaN(double0)) {
            return false;
        } else if ((double0 < 0.0 ? -double0 : double0) > double1) {
            return false;
        } else {
            double0 = this.y - tuple4d0.y;
            if (Double.isNaN(double0)) {
                return false;
            } else if ((double0 < 0.0 ? -double0 : double0) > double1) {
                return false;
            } else {
                double0 = this.z - tuple4d0.z;
                if (Double.isNaN(double0)) {
                    return false;
                } else if ((double0 < 0.0 ? -double0 : double0) > double1) {
                    return false;
                } else {
                    double0 = this.w - tuple4d0.w;
                    return Double.isNaN(double0) ? false : !((double0 < 0.0 ? -double0 : double0) > double1);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.x);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.y);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.z);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.w);
        return (int)(long0 ^ long0 >> 32);
    }

    /** @deprecated */
    public final void clamp(float float1, float float0, Tuple4d tuple4d1) {
        this.clamp((double)float1, (double)float0, tuple4d1);
    }

    public final void clamp(double double1, double double0, Tuple4d tuple4d0) {
        if (tuple4d0.x > double0) {
            this.x = double0;
        } else if (tuple4d0.x < double1) {
            this.x = double1;
        } else {
            this.x = tuple4d0.x;
        }

        if (tuple4d0.y > double0) {
            this.y = double0;
        } else if (tuple4d0.y < double1) {
            this.y = double1;
        } else {
            this.y = tuple4d0.y;
        }

        if (tuple4d0.z > double0) {
            this.z = double0;
        } else if (tuple4d0.z < double1) {
            this.z = double1;
        } else {
            this.z = tuple4d0.z;
        }

        if (tuple4d0.w > double0) {
            this.w = double0;
        } else if (tuple4d0.w < double1) {
            this.w = double1;
        } else {
            this.w = tuple4d0.w;
        }
    }

    /** @deprecated */
    public final void clampMin(float float0, Tuple4d tuple4d1) {
        this.clampMin((double)float0, tuple4d1);
    }

    public final void clampMin(double double0, Tuple4d tuple4d0) {
        if (tuple4d0.x < double0) {
            this.x = double0;
        } else {
            this.x = tuple4d0.x;
        }

        if (tuple4d0.y < double0) {
            this.y = double0;
        } else {
            this.y = tuple4d0.y;
        }

        if (tuple4d0.z < double0) {
            this.z = double0;
        } else {
            this.z = tuple4d0.z;
        }

        if (tuple4d0.w < double0) {
            this.w = double0;
        } else {
            this.w = tuple4d0.w;
        }
    }

    /** @deprecated */
    public final void clampMax(float float0, Tuple4d tuple4d1) {
        this.clampMax((double)float0, tuple4d1);
    }

    public final void clampMax(double double0, Tuple4d tuple4d0) {
        if (tuple4d0.x > double0) {
            this.x = double0;
        } else {
            this.x = tuple4d0.x;
        }

        if (tuple4d0.y > double0) {
            this.y = double0;
        } else {
            this.y = tuple4d0.y;
        }

        if (tuple4d0.z > double0) {
            this.z = double0;
        } else {
            this.z = tuple4d0.z;
        }

        if (tuple4d0.w > double0) {
            this.w = double0;
        } else {
            this.w = tuple4d0.z;
        }
    }

    public final void absolute(Tuple4d tuple4d0) {
        this.x = Math.abs(tuple4d0.x);
        this.y = Math.abs(tuple4d0.y);
        this.z = Math.abs(tuple4d0.z);
        this.w = Math.abs(tuple4d0.w);
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

        if (this.w > double0) {
            this.w = double0;
        } else if (this.w < double1) {
            this.w = double1;
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

        if (this.w < double0) {
            this.w = double0;
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

        if (this.w > double0) {
            this.w = double0;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
    }

    /** @deprecated */
    public void interpolate(Tuple4d tuple4d1, Tuple4d tuple4d2, float float0) {
        this.interpolate(tuple4d1, tuple4d2, (double)float0);
    }

    public void interpolate(Tuple4d tuple4d1, Tuple4d tuple4d0, double double0) {
        this.x = (1.0 - double0) * tuple4d1.x + double0 * tuple4d0.x;
        this.y = (1.0 - double0) * tuple4d1.y + double0 * tuple4d0.y;
        this.z = (1.0 - double0) * tuple4d1.z + double0 * tuple4d0.z;
        this.w = (1.0 - double0) * tuple4d1.w + double0 * tuple4d0.w;
    }

    /** @deprecated */
    public void interpolate(Tuple4d tuple4d1, float float0) {
        this.interpolate(tuple4d1, (double)float0);
    }

    public void interpolate(Tuple4d tuple4d0, double double0) {
        this.x = (1.0 - double0) * this.x + double0 * tuple4d0.x;
        this.y = (1.0 - double0) * this.y + double0 * tuple4d0.y;
        this.z = (1.0 - double0) * this.z + double0 * tuple4d0.z;
        this.w = (1.0 - double0) * this.w + double0 * tuple4d0.w;
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

    public final double getW() {
        return this.w;
    }

    public final void setW(double double0) {
        this.w = double0;
    }
}
