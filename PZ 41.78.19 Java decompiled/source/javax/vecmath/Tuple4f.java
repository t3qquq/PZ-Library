// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4f implements Serializable, Cloneable {
    static final long serialVersionUID = 7068460319248845763L;
    public float x;
    public float y;
    public float z;
    public float w;

    public Tuple4f(float float0, float float1, float float2, float float3) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.w = float3;
    }

    public Tuple4f(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
        this.w = floats[3];
    }

    public Tuple4f(Tuple4f tuple4f1) {
        this.x = tuple4f1.x;
        this.y = tuple4f1.y;
        this.z = tuple4f1.z;
        this.w = tuple4f1.w;
    }

    public Tuple4f(Tuple4d tuple4d) {
        this.x = (float)tuple4d.x;
        this.y = (float)tuple4d.y;
        this.z = (float)tuple4d.z;
        this.w = (float)tuple4d.w;
    }

    public Tuple4f() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
        this.w = 0.0F;
    }

    public final void set(float float0, float float1, float float2, float float3) {
        this.x = float0;
        this.y = float1;
        this.z = float2;
        this.w = float3;
    }

    public final void set(float[] floats) {
        this.x = floats[0];
        this.y = floats[1];
        this.z = floats[2];
        this.w = floats[3];
    }

    public final void set(Tuple4f tuple4f0) {
        this.x = tuple4f0.x;
        this.y = tuple4f0.y;
        this.z = tuple4f0.z;
        this.w = tuple4f0.w;
    }

    public final void set(Tuple4d tuple4d) {
        this.x = (float)tuple4d.x;
        this.y = (float)tuple4d.y;
        this.z = (float)tuple4d.z;
        this.w = (float)tuple4d.w;
    }

    public final void get(float[] floats) {
        floats[0] = this.x;
        floats[1] = this.y;
        floats[2] = this.z;
        floats[3] = this.w;
    }

    public final void get(Tuple4f tuple4f1) {
        tuple4f1.x = this.x;
        tuple4f1.y = this.y;
        tuple4f1.z = this.z;
        tuple4f1.w = this.w;
    }

    public final void add(Tuple4f tuple4f1, Tuple4f tuple4f0) {
        this.x = tuple4f1.x + tuple4f0.x;
        this.y = tuple4f1.y + tuple4f0.y;
        this.z = tuple4f1.z + tuple4f0.z;
        this.w = tuple4f1.w + tuple4f0.w;
    }

    public final void add(Tuple4f tuple4f0) {
        this.x = this.x + tuple4f0.x;
        this.y = this.y + tuple4f0.y;
        this.z = this.z + tuple4f0.z;
        this.w = this.w + tuple4f0.w;
    }

    public final void sub(Tuple4f tuple4f1, Tuple4f tuple4f0) {
        this.x = tuple4f1.x - tuple4f0.x;
        this.y = tuple4f1.y - tuple4f0.y;
        this.z = tuple4f1.z - tuple4f0.z;
        this.w = tuple4f1.w - tuple4f0.w;
    }

    public final void sub(Tuple4f tuple4f0) {
        this.x = this.x - tuple4f0.x;
        this.y = this.y - tuple4f0.y;
        this.z = this.z - tuple4f0.z;
        this.w = this.w - tuple4f0.w;
    }

    public final void negate(Tuple4f tuple4f0) {
        this.x = -tuple4f0.x;
        this.y = -tuple4f0.y;
        this.z = -tuple4f0.z;
        this.w = -tuple4f0.w;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public final void scale(float float0, Tuple4f tuple4f0) {
        this.x = float0 * tuple4f0.x;
        this.y = float0 * tuple4f0.y;
        this.z = float0 * tuple4f0.z;
        this.w = float0 * tuple4f0.w;
    }

    public final void scale(float float0) {
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        this.w *= float0;
    }

    public final void scaleAdd(float float0, Tuple4f tuple4f1, Tuple4f tuple4f0) {
        this.x = float0 * tuple4f1.x + tuple4f0.x;
        this.y = float0 * tuple4f1.y + tuple4f0.y;
        this.z = float0 * tuple4f1.z + tuple4f0.z;
        this.w = float0 * tuple4f1.w + tuple4f0.w;
    }

    public final void scaleAdd(float float0, Tuple4f tuple4f0) {
        this.x = float0 * this.x + tuple4f0.x;
        this.y = float0 * this.y + tuple4f0.y;
        this.z = float0 * this.z + tuple4f0.z;
        this.w = float0 * this.w + tuple4f0.w;
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    public boolean equals(Tuple4f tuple4f0) {
        try {
            return this.x == tuple4f0.x && this.y == tuple4f0.y && this.z == tuple4f0.z && this.w == tuple4f0.w;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple4f tuple4f0 = (Tuple4f)object;
            return this.x == tuple4f0.x && this.y == tuple4f0.y && this.z == tuple4f0.z && this.w == tuple4f0.w;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple4f tuple4f0, float float1) {
        float float0 = this.x - tuple4f0.x;
        if (Float.isNaN(float0)) {
            return false;
        } else if ((float0 < 0.0F ? -float0 : float0) > float1) {
            return false;
        } else {
            float0 = this.y - tuple4f0.y;
            if (Float.isNaN(float0)) {
                return false;
            } else if ((float0 < 0.0F ? -float0 : float0) > float1) {
                return false;
            } else {
                float0 = this.z - tuple4f0.z;
                if (Float.isNaN(float0)) {
                    return false;
                } else if ((float0 < 0.0F ? -float0 : float0) > float1) {
                    return false;
                } else {
                    float0 = this.w - tuple4f0.w;
                    return Float.isNaN(float0) ? false : !((float0 < 0.0F ? -float0 : float0) > float1);
                }
            }
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.x);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.y);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.z);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.w);
        return (int)(long0 ^ long0 >> 32);
    }

    public final void clamp(float float1, float float0, Tuple4f tuple4f0) {
        if (tuple4f0.x > float0) {
            this.x = float0;
        } else if (tuple4f0.x < float1) {
            this.x = float1;
        } else {
            this.x = tuple4f0.x;
        }

        if (tuple4f0.y > float0) {
            this.y = float0;
        } else if (tuple4f0.y < float1) {
            this.y = float1;
        } else {
            this.y = tuple4f0.y;
        }

        if (tuple4f0.z > float0) {
            this.z = float0;
        } else if (tuple4f0.z < float1) {
            this.z = float1;
        } else {
            this.z = tuple4f0.z;
        }

        if (tuple4f0.w > float0) {
            this.w = float0;
        } else if (tuple4f0.w < float1) {
            this.w = float1;
        } else {
            this.w = tuple4f0.w;
        }
    }

    public final void clampMin(float float0, Tuple4f tuple4f0) {
        if (tuple4f0.x < float0) {
            this.x = float0;
        } else {
            this.x = tuple4f0.x;
        }

        if (tuple4f0.y < float0) {
            this.y = float0;
        } else {
            this.y = tuple4f0.y;
        }

        if (tuple4f0.z < float0) {
            this.z = float0;
        } else {
            this.z = tuple4f0.z;
        }

        if (tuple4f0.w < float0) {
            this.w = float0;
        } else {
            this.w = tuple4f0.w;
        }
    }

    public final void clampMax(float float0, Tuple4f tuple4f0) {
        if (tuple4f0.x > float0) {
            this.x = float0;
        } else {
            this.x = tuple4f0.x;
        }

        if (tuple4f0.y > float0) {
            this.y = float0;
        } else {
            this.y = tuple4f0.y;
        }

        if (tuple4f0.z > float0) {
            this.z = float0;
        } else {
            this.z = tuple4f0.z;
        }

        if (tuple4f0.w > float0) {
            this.w = float0;
        } else {
            this.w = tuple4f0.z;
        }
    }

    public final void absolute(Tuple4f tuple4f0) {
        this.x = Math.abs(tuple4f0.x);
        this.y = Math.abs(tuple4f0.y);
        this.z = Math.abs(tuple4f0.z);
        this.w = Math.abs(tuple4f0.w);
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

        if (this.w > float0) {
            this.w = float0;
        } else if (this.w < float1) {
            this.w = float1;
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

        if (this.w < float0) {
            this.w = float0;
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

        if (this.w > float0) {
            this.w = float0;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
    }

    public void interpolate(Tuple4f tuple4f1, Tuple4f tuple4f0, float float0) {
        this.x = (1.0F - float0) * tuple4f1.x + float0 * tuple4f0.x;
        this.y = (1.0F - float0) * tuple4f1.y + float0 * tuple4f0.y;
        this.z = (1.0F - float0) * tuple4f1.z + float0 * tuple4f0.z;
        this.w = (1.0F - float0) * tuple4f1.w + float0 * tuple4f0.w;
    }

    public void interpolate(Tuple4f tuple4f0, float float0) {
        this.x = (1.0F - float0) * this.x + float0 * tuple4f0.x;
        this.y = (1.0F - float0) * this.y + float0 * tuple4f0.y;
        this.z = (1.0F - float0) * this.z + float0 * tuple4f0.z;
        this.w = (1.0F - float0) * this.w + float0 * tuple4f0.w;
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

    public final float getW() {
        return this.w;
    }

    public final void setW(float float0) {
        this.w = float0;
    }
}
