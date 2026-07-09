// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

public class Vector3i implements Externalizable, Vector3ic {
    private static final long serialVersionUID = 1L;
    public int x;
    public int y;
    public int z;

    public Vector3i() {
    }

    public Vector3i(int arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
    }

    public Vector3i(int arg0, int arg1, int arg2) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
    }

    public Vector3i(Vector3ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
    }

    public Vector3i(Vector2ic arg0, int arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
    }

    public Vector3i(float arg0, float arg1, float arg2, int arg3) {
        this.x = Math.roundUsing(arg0, arg3);
        this.y = Math.roundUsing(arg1, arg3);
        this.z = Math.roundUsing(arg2, arg3);
    }

    public Vector3i(double arg0, double arg1, double arg2, int arg3) {
        this.x = Math.roundUsing(arg0, arg3);
        this.y = Math.roundUsing(arg1, arg3);
        this.z = Math.roundUsing(arg2, arg3);
    }

    public Vector3i(Vector2fc arg0, float arg1, int arg2) {
        this.x = Math.roundUsing(arg0.x(), arg2);
        this.y = Math.roundUsing(arg0.y(), arg2);
        this.z = Math.roundUsing(arg1, arg2);
    }

    public Vector3i(Vector3fc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
    }

    public Vector3i(Vector2dc arg0, float arg1, int arg2) {
        this.x = Math.roundUsing(arg0.x(), arg2);
        this.y = Math.roundUsing(arg0.y(), arg2);
        this.z = Math.roundUsing(arg1, arg2);
    }

    public Vector3i(Vector3dc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
    }

    public Vector3i(int[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
    }

    public Vector3i(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector3i(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector3i(IntBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector3i(int arg0, IntBuffer arg1) {
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

    public Vector3i set(Vector3ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg0.z();
        return this;
    }

    public Vector3i set(Vector3dc arg0) {
        this.x = (int)arg0.x();
        this.y = (int)arg0.y();
        this.z = (int)arg0.z();
        return this;
    }

    public Vector3i set(Vector3dc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
        return this;
    }

    public Vector3i set(Vector3fc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        this.z = Math.roundUsing(arg0.z(), arg1);
        return this;
    }

    public Vector3i set(Vector2ic arg0, int arg1) {
        this.x = arg0.x();
        this.y = arg0.y();
        this.z = arg1;
        return this;
    }

    public Vector3i set(int arg0) {
        this.x = arg0;
        this.y = arg0;
        this.z = arg0;
        return this;
    }

    public Vector3i set(int arg0, int arg1, int arg2) {
        this.x = arg0;
        this.y = arg1;
        this.z = arg2;
        return this;
    }

    public Vector3i set(int[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        this.z = arg0[2];
        return this;
    }

    public Vector3i set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector3i set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector3i set(IntBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector3i set(int arg0, IntBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector3i setFromAddress(long arg0) {
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
            default:
                throw new IllegalArgumentException();
        }
    }

    public Vector3i setComponent(int arg0, int arg1) throws IllegalArgumentException {
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
    public Vector3ic getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector3i sub(Vector3ic arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        this.z = this.z - arg0.z();
        return this;
    }

    @Override
    public Vector3i sub(Vector3ic arg0, Vector3i arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        arg1.z = this.z - arg0.z();
        return arg1;
    }

    public Vector3i sub(int arg0, int arg1, int arg2) {
        this.x -= arg0;
        this.y -= arg1;
        this.z -= arg2;
        return this;
    }

    @Override
    public Vector3i sub(int arg0, int arg1, int arg2, Vector3i arg3) {
        arg3.x = this.x - arg0;
        arg3.y = this.y - arg1;
        arg3.z = this.z - arg2;
        return arg3;
    }

    public Vector3i add(Vector3ic arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        this.z = this.z + arg0.z();
        return this;
    }

    @Override
    public Vector3i add(Vector3ic arg0, Vector3i arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        arg1.z = this.z + arg0.z();
        return arg1;
    }

    public Vector3i add(int arg0, int arg1, int arg2) {
        this.x += arg0;
        this.y += arg1;
        this.z += arg2;
        return this;
    }

    @Override
    public Vector3i add(int arg0, int arg1, int arg2, Vector3i arg3) {
        arg3.x = this.x + arg0;
        arg3.y = this.y + arg1;
        arg3.z = this.z + arg2;
        return arg3;
    }

    public Vector3i mul(int arg0) {
        this.x *= arg0;
        this.y *= arg0;
        this.z *= arg0;
        return this;
    }

    @Override
    public Vector3i mul(int arg0, Vector3i arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        arg1.z = this.z * arg0;
        return arg1;
    }

    public Vector3i mul(Vector3ic arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        this.z = this.z * arg0.z();
        return this;
    }

    @Override
    public Vector3i mul(Vector3ic arg0, Vector3i arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        arg1.z = this.z * arg0.z();
        return arg1;
    }

    public Vector3i mul(int arg0, int arg1, int arg2) {
        this.x *= arg0;
        this.y *= arg1;
        this.z *= arg2;
        return this;
    }

    @Override
    public Vector3i mul(int arg0, int arg1, int arg2, Vector3i arg3) {
        arg3.x = this.x * arg0;
        arg3.y = this.y * arg1;
        arg3.z = this.z * arg2;
        return arg3;
    }

    public Vector3i div(float arg0) {
        float float0 = 1.0F / arg0;
        this.x = (int)(this.x * float0);
        this.y = (int)(this.y * float0);
        this.z = (int)(this.z * float0);
        return this;
    }

    @Override
    public Vector3i div(float arg0, Vector3i arg1) {
        float float0 = 1.0F / arg0;
        arg1.x = (int)(this.x * float0);
        arg1.y = (int)(this.y * float0);
        arg1.z = (int)(this.z * float0);
        return arg1;
    }

    public Vector3i div(int arg0) {
        this.x /= arg0;
        this.y /= arg0;
        this.z /= arg0;
        return this;
    }

    @Override
    public Vector3i div(int arg0, Vector3i arg1) {
        arg1.x = this.x / arg0;
        arg1.y = this.y / arg0;
        arg1.z = this.z / arg0;
        return arg1;
    }

    @Override
    public long lengthSquared() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public static long lengthSquared(int arg0, int arg1, int arg2) {
        return arg0 * arg0 + arg1 * arg1 + arg2 * arg2;
    }

    @Override
    public double length() {
        return Math.sqrt((float)(this.x * this.x + this.y * this.y + this.z * this.z));
    }

    public static double length(int arg0, int arg1, int arg2) {
        return Math.sqrt((float)(arg0 * arg0 + arg1 * arg1 + arg2 * arg2));
    }

    @Override
    public double distance(Vector3ic arg0) {
        int int0 = this.x - arg0.x();
        int int1 = this.y - arg0.y();
        int int2 = this.z - arg0.z();
        return Math.sqrt((float)(int0 * int0 + int1 * int1 + int2 * int2));
    }

    @Override
    public double distance(int arg0, int arg1, int arg2) {
        int int0 = this.x - arg0;
        int int1 = this.y - arg1;
        int int2 = this.z - arg2;
        return Math.sqrt((float)(int0 * int0 + int1 * int1 + int2 * int2));
    }

    @Override
    public long gridDistance(Vector3ic arg0) {
        return Math.abs(arg0.x() - this.x()) + Math.abs(arg0.y() - this.y()) + Math.abs(arg0.z() - this.z());
    }

    @Override
    public long gridDistance(int arg0, int arg1, int arg2) {
        return Math.abs(arg0 - this.x()) + Math.abs(arg1 - this.y()) + Math.abs(arg2 - this.z());
    }

    @Override
    public long distanceSquared(Vector3ic arg0) {
        int int0 = this.x - arg0.x();
        int int1 = this.y - arg0.y();
        int int2 = this.z - arg0.z();
        return int0 * int0 + int1 * int1 + int2 * int2;
    }

    @Override
    public long distanceSquared(int arg0, int arg1, int arg2) {
        int int0 = this.x - arg0;
        int int1 = this.y - arg1;
        int int2 = this.z - arg2;
        return int0 * int0 + int1 * int1 + int2 * int2;
    }

    public static double distance(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        return Math.sqrt((float)distanceSquared(arg0, arg1, arg2, arg3, arg4, arg5));
    }

    public static long distanceSquared(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        int int0 = arg0 - arg3;
        int int1 = arg1 - arg4;
        int int2 = arg2 - arg5;
        return int0 * int0 + int1 * int1 + int2 * int2;
    }

    public Vector3i zero() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        return this;
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "(" + numberFormat.format((long)this.x) + " " + numberFormat.format((long)this.y) + " " + numberFormat.format((long)this.z) + ")";
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeInt(this.x);
        arg0.writeInt(this.y);
        arg0.writeInt(this.z);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readInt();
        this.y = arg0.readInt();
        this.z = arg0.readInt();
    }

    public Vector3i negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        return this;
    }

    @Override
    public Vector3i negate(Vector3i arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        arg0.z = -this.z;
        return arg0;
    }

    public Vector3i min(Vector3ic arg0) {
        this.x = this.x < arg0.x() ? this.x : arg0.x();
        this.y = this.y < arg0.y() ? this.y : arg0.y();
        this.z = this.z < arg0.z() ? this.z : arg0.z();
        return this;
    }

    @Override
    public Vector3i min(Vector3ic arg0, Vector3i arg1) {
        arg1.x = this.x < arg0.x() ? this.x : arg0.x();
        arg1.y = this.y < arg0.y() ? this.y : arg0.y();
        arg1.z = this.z < arg0.z() ? this.z : arg0.z();
        return arg1;
    }

    public Vector3i max(Vector3ic arg0) {
        this.x = this.x > arg0.x() ? this.x : arg0.x();
        this.y = this.y > arg0.y() ? this.y : arg0.y();
        this.z = this.z > arg0.z() ? this.z : arg0.z();
        return this;
    }

    @Override
    public Vector3i max(Vector3ic arg0, Vector3i arg1) {
        arg1.x = this.x > arg0.x() ? this.x : arg0.x();
        arg1.y = this.y > arg0.y() ? this.y : arg0.y();
        arg1.z = this.z > arg0.z() ? this.z : arg0.z();
        return arg1;
    }

    @Override
    public int maxComponent() {
        float float0 = Math.abs(this.x);
        float float1 = Math.abs(this.y);
        float float2 = Math.abs(this.z);
        if (float0 >= float1 && float0 >= float2) {
            return 0;
        } else {
            return float1 >= float2 ? 1 : 2;
        }
    }

    @Override
    public int minComponent() {
        float float0 = Math.abs(this.x);
        float float1 = Math.abs(this.y);
        float float2 = Math.abs(this.z);
        if (float0 < float1 && float0 < float2) {
            return 0;
        } else {
            return float1 < float2 ? 1 : 2;
        }
    }

    public Vector3i absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        return this;
    }

    @Override
    public Vector3i absolute(Vector3i arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        arg0.z = Math.abs(this.z);
        return arg0;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + this.x;
        int0 = 31 * int0 + this.y;
        return 31 * int0 + this.z;
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
            Vector3i vector3i = (Vector3i)arg0;
            if (this.x != vector3i.x) {
                return false;
            } else {
                return this.y != vector3i.y ? false : this.z == vector3i.z;
            }
        }
    }

    @Override
    public boolean equals(int arg0, int arg1, int arg2) {
        if (this.x != arg0) {
            return false;
        } else {
            return this.y != arg1 ? false : this.z == arg2;
        }
    }
}
