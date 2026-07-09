// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

public class Vector4i implements Externalizable, Vector4ic {
    private static final long serialVersionUID = 1L;
    public int x;
    public int y;
    public int z;
    public int w;

    public Vector4i() {
        this.w = 1;
    }

    public Vector4i(Vector4ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
    }

    public Vector4i(Vector3ic arg0, int arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
    }

    public Vector4i(Vector2ic arg0, int arg1, int arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
    }

    public Vector4i(Vector3fc arg0, float arg1, int arg2) {
        this.x = Math.roundUsing(arg0.x(), arg2);
        this.y = Math.roundUsing(arg0.y(), arg2);
        this.z = Math.roundUsing(arg0.z(), arg2);
        arg1 = Math.roundUsing(arg1, arg2);
    }

    public Vector4i(Vector4fc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
        this.w = Math.roundUsing(arg0.w(), arg1);
    }

    public Vector4i(Vector4dc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
        this.w = Math.roundUsing(arg0.w(), arg1);
    }

    public Vector4i(int arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        this.w = arg0;
    }

    public Vector4i(int arg0, int arg1, int arg2, int arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
    }

    public Vector4i(int[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        this.w = arg0[3];
    }

    public Vector4i(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector4i(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector4i(IntBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector4i(int arg0, IntBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    @Override
    public int x() {
        return this.x;
    }

    @Override
    public int y() {
        return this.y;
    }

    @Override
    public int z() {
        return this.z;
    }

    @Override
    public int w() {
        return this.w;
    }

    public Vector4i set(Vector4ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg0.w();
        return this;
    }

    public Vector4i set(Vector4dc arg0) {
        this.x = (int)arg0.x();
        this.y = (int)arg0.y();
        this.z = (int)arg0.z();
        this.w = (int)arg0.w();
        return this;
    }

    public Vector4i set(Vector4dc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
        this.w = Math.roundUsing(arg0.w(), arg1);
        return this;
    }

    public Vector4i set(Vector4fc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
        this.w = Math.roundUsing(arg0.w(), arg1);
        return this;
    }

    public Vector4i set(Vector3ic arg0, int arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        this.w = arg1;
        return this;
    }

    public Vector4i set(Vector2ic arg0, int arg1, int arg2) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        this.w = arg2;
        return this;
    }

    public Vector4i set(int arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        this.w = arg0;
        return this;
    }

    public Vector4i set(int arg0, int arg1, int arg2, int arg3) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        this.w = arg3;
        return this;
    }

    public Vector4i set(int[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        this.w = arg0[2];
        return this;
    }

    public Vector4i set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector4i set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector4i set(IntBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector4i set(int arg0, IntBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector4i setFromAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.get(this, arg0);
            return this;
        }
    }

    @Override
    public int get(int arg0) throws IllegalArgumentException {
        switch (arg0) {
            case 0:
                return this.x;
            case 1:
                return this.y;
            case 2:
                return this.z;
            case 3:
                return this.w;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int maxComponent() {
        int int0 = Math.abs(this.x);
        int int1 = Math.abs(this.y);
        int int2 = Math.abs(this.z);
        int int3 = Math.abs(this.w);
        if (int0 >= int1 && int0 >= int2 && int0 >= int3) {
            return 0;
        } else if (int1 >= int2 && int1 >= int3) {
            return 1;
        } else {
            return int2 >= int3 ? 2 : 3;
        }
    }

    @Override
    public int minComponent() {
        int int0 = Math.abs(this.x);
        int int1 = Math.abs(this.y);
        int int2 = Math.abs(this.z);
        int int3 = Math.abs(this.w);
        if (int0 < int1 && int0 < int2 && int0 < int3) {
            return 0;
        } else if (int1 < int2 && int1 < int3) {
            return 1;
        } else {
            return int2 < int3 ? 2 : 3;
        }
    }

    public Vector4i setComponent(int arg0, int arg1) throws IllegalArgumentException {
        switch (arg0) {
            case 0:
                this.x = arg1;
                break;
            case 1:
                this.y = arg1;
                break;
            case 2:
                this.z = arg1;
                break;
            case 3:
                this.w = arg1;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return this;
    }

    @Override
    public IntBuffer get(IntBuffer arg0) {
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public IntBuffer get(int arg0, IntBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public ByteBuffer get(ByteBuffer arg0) {
        MemUtil.INSTANCE.put(this, arg0.position(), arg0);
        return arg0;
    }

    @Override
    public ByteBuffer get(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.put(this, arg0, arg1);
        return arg1;
    }

    @Override
    public Vector4ic getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector4i sub(Vector4ic arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        this.z = this.z - arg0.z();
        this.w = this.w - arg0.w();
        return this;
    }

    public Vector4i sub(int arg0, int arg1, int arg2, int arg3) {
        this.x -= arg0;
        this.y -= arg1;
        this.z -= arg2;
        this.w -= arg3;
        return this;
    }

    @Override
    public Vector4i sub(Vector4ic arg0, Vector4i arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        arg1.z = this.z - arg0.z();
        arg1.w = this.w - arg0.w();
        return arg1;
    }

    @Override
    public Vector4i sub(int arg0, int arg1, int arg2, int arg3, Vector4i arg4) {
        arg4.x = this.x - arg0;
        arg4.y = this.y - arg1;
        arg4.z = this.z - arg2;
        arg4.w = this.w - arg3;
        return arg4;
    }

    public Vector4i add(Vector4ic arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        this.w = this.w + arg0.w();
        return this;
    }

    @Override
    public Vector4i add(Vector4ic arg0, Vector4i arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        arg1.w = this.w + arg0.w();
        return arg1;
    }

    public Vector4i add(int arg0, int arg1, int arg2, int arg3) {
        this.x += arg0;
        this.y += arg1;
        this.z += arg2;
        this.w += arg3;
        return this;
    }

    @Override
    public Vector4i add(int arg0, int arg1, int arg2, int arg3, Vector4i arg4) {
        arg4.x = this.x + arg0;
        arg4.y = this.y + arg1;
        arg4.z = this.z + arg2;
        arg4.w = this.w + arg3;
        return arg4;
    }

    public Vector4i mul(Vector4ic arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        this.z = this.z * arg0.z();
        this.w = this.w * arg0.w();
        return this;
    }

    @Override
    public Vector4i mul(Vector4ic arg0, Vector4i arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        arg1.z = this.z * arg0.z();
        arg1.w = this.w * arg0.w();
        return arg1;
    }

    public Vector4i div(Vector4ic arg0) {
        this.x = this.x / arg0.x();
        this.y = this.y / arg0.y();
        this.z = this.z / arg0.z();
        this.w = this.w / arg0.w();
        return this;
    }

    @Override
    public Vector4i div(Vector4ic arg0, Vector4i arg1) {
        arg1.x = this.x / arg0.x();
        arg1.y = this.y / arg0.y();
        arg1.z = this.z / arg0.z();
        arg1.w = this.w / arg0.w();
        return arg1;
    }

    public Vector4i mul(int arg0) {
        this.x *= arg0;
        this.y *= arg0;
        this.z *= arg0;
        this.w *= arg0;
        return this;
    }

    @Override
    public Vector4i mul(int arg0, Vector4i arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        arg1.z = this.z * arg0;
        arg1.w = this.w * arg0;
        return arg1;
    }

    public Vector4i div(float arg0) {
        float float0 = 1.0F / arg0;
        this.x = (int)(this.x * float0);
        this.y = (int)(this.y * float0);
        this.z = (int)(this.z * float0);
        this.w = (int)(this.w * float0);
        return this;
    }

    @Override
    public Vector4i div(float arg0, Vector4i arg1) {
        float float0 = 1.0F / arg0;
        arg1.x = (int)(this.x * float0);
        arg1.y = (int)(this.y * float0);
        arg1.z = (int)(this.z * float0);
        arg1.w = (int)(this.w * float0);
        return arg1;
    }

    public Vector4i div(int arg0) {
        this.x /= arg0;
        this.y /= arg0;
        this.z /= arg0;
        this.w /= arg0;
        return this;
    }

    @Override
    public Vector4i div(int arg0, Vector4i arg1) {
        arg1.x = this.x / arg0;
        arg1.y = this.y / arg0;
        arg1.z = this.z / arg0;
        arg1.w = this.w / arg0;
        return arg1;
    }

    @Override
    public long lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
    }

    public static long lengthSquared(int arg0, int arg1, int arg2, int arg3) {
        return arg0 * arg0 + arg1 * arg1 + arg2 * arg2 + arg3 * arg3;
    }

    @Override
    public double length() {
        return Math.sqrt((float)(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w));
    }

    public static double length(int arg0, int arg1, int arg2, int arg3) {
        return Math.sqrt((float)(arg0 * arg0 + arg1 * arg1 + arg2 * arg2 + arg3 * arg3));
    }

    @Override
    public double distance(Vector4ic arg0) {
        int int0 = this.x - arg0.x();
        int int1 = this.y - arg0.y();
        int int2 = this.z - arg0.z();
        int int3 = this.w - arg0.w();
        return Math.sqrt(Math.fma((float)int0, (float)int0, Math.fma((float)int1, (float)int1, Math.fma((float)int2, (float)int2, (float)(int3 * int3)))));
    }

    @Override
    public double distance(int arg0, int arg1, int arg2, int arg3) {
        int int0 = this.x - arg0;
        int int1 = this.y - arg1;
        int int2 = this.z - arg2;
        int int3 = this.w - arg3;
        return Math.sqrt(Math.fma((float)int0, (float)int0, Math.fma((float)int1, (float)int1, Math.fma((float)int2, (float)int2, (float)(int3 * int3)))));
    }

    @Override
    public long gridDistance(Vector4ic arg0) {
        return Math.abs(arg0.x() - this.x()) + Math.abs(arg0.y() - this.y()) + Math.abs(arg0.z() - this.z()) + Math.abs(arg0.w() - this.w());
    }

    @Override
    public long gridDistance(int arg0, int arg1, int arg2, int arg3) {
        return Math.abs(arg0 - this.x()) + Math.abs(arg1 - this.y()) + Math.abs(arg2 - this.z()) + Math.abs(arg3 - this.w());
    }

    @Override
    public int distanceSquared(Vector4ic arg0) {
        int int0 = this.x - arg0.x();
        int int1 = this.y - arg0.y();
        int int2 = this.z - arg0.z();
        int int3 = this.w - arg0.w();
        return int0 * int0 + int1 * int1 + int2 * int2 + int3 * int3;
    }

    @Override
    public int distanceSquared(int arg0, int arg1, int arg2, int arg3) {
        int int0 = this.x - arg0;
        int int1 = this.y - arg1;
        int int2 = this.z - arg2;
        int int3 = this.w - arg3;
        return int0 * int0 + int1 * int1 + int2 * int2 + int3 * int3;
    }

    public static double distance(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
        int int0 = arg0 - arg4;
        int int1 = arg1 - arg5;
        int int2 = arg2 - arg6;
        int int3 = arg3 - arg7;
        return Math.sqrt((float)(int0 * int0 + int1 * int1 + int2 * int2 + int3 * int3));
    }

    public static long distanceSquared(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5, int arg6, int arg7) {
        int int0 = arg0 - arg4;
        int int1 = arg1 - arg5;
        int int2 = arg2 - arg6;
        int int3 = arg3 - arg7;
        return int0 * int0 + int1 * int1 + int2 * int2 + int3 * int3;
    }

    @Override
    public int dot(Vector4ic arg0) {
        return this.x * arg0.x() + this.y * arg0.y() + this.z * arg0.z() + this.w * arg0.w();
    }

    public Vector4i zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
        return this;
    }

    public Vector4i negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
        return this;
    }

    @Override
    public Vector4i negate(Vector4i arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        arg0.z = -this.z;
        arg0.w = -this.w;
        return arg0;
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "("
            + numberFormat.format((long)this.x)
            + " "
            + numberFormat.format((long)this.y)
            + " "
            + numberFormat.format((long)this.z)
            + " "
            + numberFormat.format((long)this.w)
            + ")";
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeInt(this.x);
        arg0.writeInt(this.y);
        arg0.writeInt(this.z);
        arg0.writeInt(this.w);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readInt();
        this.y = arg0.readInt();
        this.z = arg0.readInt();
        this.w = arg0.readInt();
    }

    public Vector4i min(Vector4ic arg0) {
        this.x = this.x < arg0.x() ? this.x : arg0.x();
        this.y = this.y < arg0.y() ? this.y : arg0.y();
        this.z = this.z < arg0.z() ? this.z : arg0.z();
        this.w = this.w < arg0.w() ? this.w : arg0.w();
        return this;
    }

    @Override
    public Vector4i min(Vector4ic arg0, Vector4i arg1) {
        arg1.x = this.x < arg0.x() ? this.x : arg0.x();
        arg1.y = this.y < arg0.y() ? this.y : arg0.y();
        arg1.z = this.z < arg0.z() ? this.z : arg0.z();
        arg1.w = this.w < arg0.w() ? this.w : arg0.w();
        return arg1;
    }

    public Vector4i max(Vector4ic arg0) {
        this.x = this.x > arg0.x() ? this.x : arg0.x();
        this.y = this.y > arg0.y() ? this.y : arg0.y();
        this.z = this.z > arg0.z() ? this.z : arg0.z();
        this.w = this.w > arg0.w() ? this.w : arg0.w();
        return this;
    }

    @Override
    public Vector4i max(Vector4ic arg0, Vector4i arg1) {
        arg1.x = this.x > arg0.x() ? this.x : arg0.x();
        arg1.y = this.y > arg0.y() ? this.y : arg0.y();
        arg1.z = this.z > arg0.z() ? this.z : arg0.z();
        arg1.w = this.w > arg0.w() ? this.w : arg0.w();
        return arg1;
    }

    public Vector4i absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
        return this;
    }

    @Override
    public Vector4i absolute(Vector4i arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        arg0.z = Math.abs(this.z);
        arg0.w = Math.abs(this.w);
        return arg0;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + this.x;
        int0 = 31 * int0 + this.y;
        int0 = 31 * int0 + this.z;
        return 31 * int0 + this.w;
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (this.getClass() != arg0.getClass()) {
            return false;
        } else {
            Vector4i vector4i = (Vector4i)arg0;
            if (this.x != vector4i.x) {
                return false;
            } else if (this.y != vector4i.y) {
                return false;
            } else {
                return this.z != vector4i.z ? false : this.w == vector4i.w;
            }
        }
    }

    @Override
    public boolean equals(int arg0, int arg1, int arg2, int arg3) {
        if (this.x != arg0) {
            return false;
        } else if (this.y != arg1) {
            return false;
        } else {
            return this.z != arg2 ? false : this.w == arg3;
        }
    }
}
