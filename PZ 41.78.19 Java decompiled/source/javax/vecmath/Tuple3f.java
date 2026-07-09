// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple3f implements Serializable, Cloneable {
    static final long serialVersionUID = 5019834619484343712L;
    public float x;
    public float y;
    public float z;

    public Tuple3f(float float0, float float1, float float2) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
    }

    public Tuple3f(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
    }

    public Tuple3f(Tuple3f tuple3f1) {
        this.x = tuple3f1.x;
        this.y = tuple3f1.y;
        this.z = tuple3f1.z;
    }

    public Tuple3f(Tuple3d tuple3d) {
        this.x = (float)tuple3d.x;
        this.y = (float)tuple3d.y;
        this.z = (float)tuple3d.z;
    }

    public Tuple3f() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    public final void set(float float0, float float1, float float2) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
    }

    public final void set(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
    }

    public final void set(Tuple3f tuple3f0) {
        this.x = tuple3f0.x;
        this.y = tuple3f0.y;
        this.z = tuple3f0.z;
    }

    public final void set(Tuple3d tuple3d) {
        this.x = (float)tuple3d.x;
        this.y = (float)tuple3d.y;
        this.z = (float)tuple3d.z;
    }

    public final void get(float[] floats) {
        floats[0] = this.x;
        floats[1] = this.y;
        floats[2] = this.z;
    }

    public final void get(Tuple3f tuple3f1) {
        tuple3f1.x = this.x;
        tuple3f1.y = this.y;
        tuple3f1.z = this.z;
    }

    public final void add(Tuple3f tuple3f1, Tuple3f tuple3f0) {
        this.x = tuple3f1.x + tuple3f0.x;
        this.y = tuple3f1.y + tuple3f0.y;
        this.z = tuple3f1.z + tuple3f0.z;
    }

    public final void add(Tuple3f tuple3f0) {
        this.x = this.x + tuple3f0.x;
        this.y = this.y + tuple3f0.y;
        this.z = this.z + tuple3f0.z;
    }

    public final void sub(Tuple3f tuple3f1, Tuple3f tuple3f0) {
        this.x = tuple3f1.x - tuple3f0.x;
        this.y = tuple3f1.y - tuple3f0.y;
        this.z = tuple3f1.z - tuple3f0.z;
    }

    public final void sub(Tuple3f tuple3f0) {
        this.x = this.x - tuple3f0.x;
        this.y = this.y - tuple3f0.y;
        this.z = this.z - tuple3f0.z;
    }

    public final void negate(Tuple3f tuple3f0) {
        this.x = -tuple3f0.x;
        this.y = -tuple3f0.y;
        this.z = -tuple3f0.z;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public final void scale(float float0, Tuple3f tuple3f0) {
        this.x = float0 * tuple3f0.x;
        this.y = float0 * tuple3f0.y;
        this.z = float0 * tuple3f0.z;
    }

    public final void scale(float float0) {
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
    }

    public final void scaleAdd(float float0, Tuple3f tuple3f1, Tuple3f tuple3f0) {
        this.x = float0 * tuple3f1.x + tuple3f0.x;
        this.y = float0 * tuple3f1.y + tuple3f0.y;
        this.z = float0 * tuple3f1.z + tuple3f0.z;
    }

    public final void scaleAdd(float float0, Tuple3f tuple3f0) {
        this.x = float0 * this.x + tuple3f0.x;
        this.y = float0 * this.y + tuple3f0.y;
        this.z = float0 * this.z + tuple3f0.z;
    }

    public boolean equals(Tuple3f tuple3f0) {
        try {
            return this.x == tuple3f0.x && this.y == tuple3f0.y && this.z == tuple3f0.z;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple3f tuple3f0 = (Tuple3f)object;
            return this.x == tuple3f0.x && this.y == tuple3f0.y && this.z == tuple3f0.z;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple3f tuple3f0, float float1) {
        float float0 = this.x - tuple3f0.x;
        if (Float.isNaN(float0)) {
            return false;
        } else if ((float0 < 0.0F ? -float0 : float0) > float1) {
            return false;
        } else {
            float0 = this.y - tuple3f0.y;
            if (Float.isNaN(float0)) {
                return false;
            } else if ((float0 < 0.0F ? -float0 : float0) > float1) {
                return false;
            } else {
                float0 = this.z - tuple3f0.z;
                return Float.isNaN(float0) ? false : !((float0 < 0.0F ? -float0 : float0) > float1);
            }
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.x);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.y);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.z);
        return (int)(long0 ^ long0 >> 32);
    }

    public final void clamp(float float1, float float0, Tuple3f tuple3f0) {
        if (tuple3f0.x > float0) {
            this.x = float0;
        } else if (tuple3f0.x < float1) {
            this.x = float1;
        } else {
            this.x = tuple3f0.x;
        }

        if (tuple3f0.y > float0) {
            this.y = float0;
        } else if (tuple3f0.y < float1) {
            this.y = float1;
        } else {
            this.y = tuple3f0.y;
        }

        if (tuple3f0.z > float0) {
            this.z = float0;
        } else if (tuple3f0.z < float1) {
            this.z = float1;
        } else {
            this.z = tuple3f0.z;
        }
    }

    public final void clampMin(float float0, Tuple3f tuple3f0) {
        if (tuple3f0.x < float0) {
            this.x = float0;
        } else {
            this.x = tuple3f0.x;
        }

        if (tuple3f0.y < float0) {
            this.y = float0;
        } else {
            this.y = tuple3f0.y;
        }

        if (tuple3f0.z < float0) {
            this.z = float0;
        } else {
            this.z = tuple3f0.z;
        }
    }

    public final void clampMax(float float0, Tuple3f tuple3f0) {
        if (tuple3f0.x > float0) {
            this.x = float0;
        } else {
            this.x = tuple3f0.x;
        }

        if (tuple3f0.y > float0) {
            this.y = float0;
        } else {
            this.y = tuple3f0.y;
        }

        if (tuple3f0.z > float0) {
            this.z = float0;
        } else {
            this.z = tuple3f0.z;
        }
    }

    public final void absolute(Tuple3f tuple3f0) {
        this.x = Math.abs(tuple3f0.x);
        this.y = Math.abs(tuple3f0.y);
        this.z = Math.abs(tuple3f0.z);
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

        if (this.z > float0) {
            this.z = float0;
        } else if (this.z < float1) {
            this.z = float1;
        }
    }

    public final void clampMin(float float0) {
        if (this.x < float0) {
            this.x = float0;
        }

        if (this.y < float0) {
            this.y = float0;
        }

        if (this.z < float0) {
            this.z = float0;
        }
    }

    public final void clampMax(float float0) {
        if (this.x > float0) {
            this.x = float0;
        }

        if (this.y > float0) {
            this.y = float0;
        }

        if (this.z > float0) {
            this.z = float0;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
    }

    public final void interpolate(Tuple3f tuple3f1, Tuple3f tuple3f0, float float0) {
        this.x = (1.0F - float0) * tuple3f1.x + float0 * tuple3f0.x;
        this.y = (1.0F - float0) * tuple3f1.y + float0 * tuple3f0.y;
        this.z = (1.0F - float0) * tuple3f1.z + float0 * tuple3f0.z;
    }

    public final void interpolate(Tuple3f tuple3f0, float float0) {
        this.x = (1.0F - float0) * this.x + float0 * tuple3f0.x;
        this.y = (1.0F - float0) * this.y + float0 * tuple3f0.y;
        this.z = (1.0F - float0) * this.z + float0 * tuple3f0.z;
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

    public final float getZ() {
        return this.z;
    }

    public final void setZ(float float0) {
        this.z = float0;
    }
}
