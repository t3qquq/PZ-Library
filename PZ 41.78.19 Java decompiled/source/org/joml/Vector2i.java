// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.text.NumberFormat;

public class Vector2i implements Externalizable, Vector2ic {
    private static final long serialVersionUID = 1L;
    public int x;
    public int y;

    public Vector2i() {
    }

    public Vector2i(int arg0) {
        this.x = arg0;
        this.y = arg0;
    }

    public Vector2i(int arg0, int arg1) {
        this.x = arg0;
        this.y = arg1;
    }

    public Vector2i(float arg0, float arg1, int arg2) {
        this.x = Math.roundUsing(arg0, arg2);
        this.y = Math.roundUsing(arg1, arg2);
    }

    public Vector2i(double arg0, double arg1, int arg2) {
        this.x = Math.roundUsing(arg0, arg2);
        this.y = Math.roundUsing(arg1, arg2);
    }

    public Vector2i(Vector2ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
    }

    public Vector2i(Vector2fc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
    }

    public Vector2i(Vector2dc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
    }

    public Vector2i(int[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
    }

    public Vector2i(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector2i(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
    }

    public Vector2i(IntBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
    }

    public Vector2i(int arg0, IntBuffer arg1) {
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

    public Vector2i set(int arg0) {
        this.x = arg0;
        this.y = arg0;
        return this;
    }

    public Vector2i set(int arg0, int arg1) {
        this.x = arg0;
        this.y = arg1;
        return this;
    }

    public Vector2i set(Vector2ic arg0) {
        this.x = arg0.x();
        this.y = arg0.y();
        return this;
    }

    public Vector2i set(Vector2dc arg0) {
        this.x = (int)arg0.x();
        this.y = (int)arg0.y();
        return this;
    }

    public Vector2i set(Vector2dc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        return this;
    }

    public Vector2i set(Vector2fc arg0, int arg1) {
        this.x = Math.roundUsing(arg0.x(), arg1);
        this.y = Math.roundUsing(arg0.y(), arg1);
        return this;
    }

    public Vector2i set(int[] arg0) {
        this.x = arg0[0];
        this.y = arg0[1];
        return this;
    }

    public Vector2i set(ByteBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector2i set(int arg0, ByteBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector2i set(IntBuffer arg0) {
        MemUtil.INSTANCE.get(this, arg0.position(), arg0);
        return this;
    }

    public Vector2i set(int arg0, IntBuffer arg1) {
        MemUtil.INSTANCE.get(this, arg0, arg1);
        return this;
    }

    public Vector2i setFromAddress(long arg0) {
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
            default:
                throw new IllegalArgumentException();
        }
    }

    public Vector2i setComponent(int arg0, int arg1) throws IllegalArgumentException {
        switch (arg0) {
            case 0:
                this.x = arg1;
                break;
            case 1:
                this.y = arg1;
                break;
            default:
                throw new IllegalArgumentException();
        }

        return this;
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
    public Vector2ic getToAddress(long arg0) {
        if (Options.NO_UNSAFE) {
            throw new UnsupportedOperationException("Not supported when using joml.nounsafe");
        } else {
            MemUtil.MemUtilUnsafe.put(this, arg0);
            return this;
        }
    }

    public Vector2i sub(Vector2ic arg0) {
        this.x = this.x - arg0.x();
        this.y = this.y - arg0.y();
        return this;
    }

    @Override
    public Vector2i sub(Vector2ic arg0, Vector2i arg1) {
        arg1.x = this.x - arg0.x();
        arg1.y = this.y - arg0.y();
        return arg1;
    }

    public Vector2i sub(int arg0, int arg1) {
        this.x -= arg0;
        this.y -= arg1;
        return this;
    }

    @Override
    public Vector2i sub(int arg0, int arg1, Vector2i arg2) {
        arg2.x = this.x - arg0;
        arg2.y = this.y - arg1;
        return arg2;
    }

    @Override
    public long lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public static long lengthSquared(int arg0, int arg1) {
        return arg0 * arg0 + arg1 * arg1;
    }

    @Override
    public double length() {
        return Math.sqrt((float)(this.x * this.x + this.y * this.y));
    }

    public static double length(int arg0, int arg1) {
        return Math.sqrt((float)(arg0 * arg0 + arg1 * arg1));
    }

    @Override
    public double distance(Vector2ic arg0) {
        int int0 = this.x - arg0.x();
        int int1 = this.y - arg0.y();
        return Math.sqrt((float)(int0 * int0 + int1 * int1));
    }

    @Override
    public double distance(int arg0, int arg1) {
        int int0 = this.x - arg0;
        int int1 = this.y - arg1;
        return Math.sqrt((float)(int0 * int0 + int1 * int1));
    }

    @Override
    public long distanceSquared(Vector2ic arg0) {
        int int0 = this.x - arg0.x();
        int int1 = this.y - arg0.y();
        return int0 * int0 + int1 * int1;
    }

    @Override
    public long distanceSquared(int arg0, int arg1) {
        int int0 = this.x - arg0;
        int int1 = this.y - arg1;
        return int0 * int0 + int1 * int1;
    }

    @Override
    public long gridDistance(Vector2ic arg0) {
        return Math.abs(arg0.x() - this.x()) + Math.abs(arg0.y() - this.y());
    }

    @Override
    public long gridDistance(int arg0, int arg1) {
        return Math.abs(arg0 - this.x()) + Math.abs(arg1 - this.y());
    }

    public static double distance(int arg0, int arg1, int arg2, int arg3) {
        int int0 = arg0 - arg2;
        int int1 = arg1 - arg3;
        return Math.sqrt((float)(int0 * int0 + int1 * int1));
    }

    public static long distanceSquared(int arg0, int arg1, int arg2, int arg3) {
        int int0 = arg0 - arg2;
        int int1 = arg1 - arg3;
        return int0 * int0 + int1 * int1;
    }

    public Vector2i add(Vector2ic arg0) {
        this.x = this.x + arg0.x();
        this.y = this.y + arg0.y();
        return this;
    }

    @Override
    public Vector2i add(Vector2ic arg0, Vector2i arg1) {
        arg1.x = this.x + arg0.x();
        arg1.y = this.y + arg0.y();
        return arg1;
    }

    public Vector2i add(int arg0, int arg1) {
        this.x += arg0;
        this.y += arg1;
        return this;
    }

    @Override
    public Vector2i add(int arg0, int arg1, Vector2i arg2) {
        arg2.x = this.x + arg0;
        arg2.y = this.y + arg1;
        return arg2;
    }

    public Vector2i mul(int arg0) {
        this.x *= arg0;
        this.y *= arg0;
        return this;
    }

    @Override
    public Vector2i mul(int arg0, Vector2i arg1) {
        arg1.x = this.x * arg0;
        arg1.y = this.y * arg0;
        return arg1;
    }

    public Vector2i mul(Vector2ic arg0) {
        this.x = this.x * arg0.x();
        this.y = this.y * arg0.y();
        return this;
    }

    @Override
    public Vector2i mul(Vector2ic arg0, Vector2i arg1) {
        arg1.x = this.x * arg0.x();
        arg1.y = this.y * arg0.y();
        return arg1;
    }

    public Vector2i mul(int arg0, int arg1) {
        this.x *= arg0;
        this.y *= arg1;
        return this;
    }

    @Override
    public Vector2i mul(int arg0, int arg1, Vector2i arg2) {
        arg2.x = this.x * arg0;
        arg2.y = this.y * arg1;
        return arg2;
    }

    public Vector2i div(float arg0) {
        float float0 = 1.0F / arg0;
        this.x = (int)(this.x * float0);
        this.y = (int)(this.y * float0);
        return this;
    }

    @Override
    public Vector2i div(float arg0, Vector2i arg1) {
        float float0 = 1.0F / arg0;
        arg1.x = (int)(this.x * float0);
        arg1.y = (int)(this.y * float0);
        return arg1;
    }

    public Vector2i div(int arg0) {
        this.x /= arg0;
        this.y /= arg0;
        return this;
    }

    @Override
    public Vector2i div(int arg0, Vector2i arg1) {
        arg1.x = this.x / arg0;
        arg1.y = this.y / arg0;
        return arg1;
    }

    public Vector2i zero() {
        this.x = 0;
        this.y = 0;
        return this;
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeInt(this.x);
        arg0.writeInt(this.y);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.x = arg0.readInt();
        this.y = arg0.readInt();
    }

    public Vector2i negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }

    @Override
    public Vector2i negate(Vector2i arg0) {
        arg0.x = -this.x;
        arg0.y = -this.y;
        return arg0;
    }

    public Vector2i min(Vector2ic arg0) {
        this.x = this.x < arg0.x() ? this.x : arg0.x();
        this.y = this.y < arg0.y() ? this.y : arg0.y();
        return this;
    }

    @Override
    public Vector2i min(Vector2ic arg0, Vector2i arg1) {
        arg1.x = this.x < arg0.x() ? this.x : arg0.x();
        arg1.y = this.y < arg0.y() ? this.y : arg0.y();
        return arg1;
    }

    public Vector2i max(Vector2ic arg0) {
        this.x = this.x > arg0.x() ? this.x : arg0.x();
        this.y = this.y > arg0.y() ? this.y : arg0.y();
        return this;
    }

    @Override
    public Vector2i max(Vector2ic arg0, Vector2i arg1) {
        arg1.x = this.x > arg0.x() ? this.x : arg0.x();
        arg1.y = this.y > arg0.y() ? this.y : arg0.y();
        return arg1;
    }

    @Override
    public int maxComponent() {
        int int0 = Math.abs(this.x);
        int int1 = Math.abs(this.y);
        return int0 >= int1 ? 0 : 1;
    }

    @Override
    public int minComponent() {
        int int0 = Math.abs(this.x);
        int int1 = Math.abs(this.y);
        return int0 < int1 ? 0 : 1;
    }

    public Vector2i absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        return this;
    }

    @Override
    public Vector2i absolute(Vector2i arg0) {
        arg0.x = Math.abs(this.x);
        arg0.y = Math.abs(this.y);
        return arg0;
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        int0 = 31 * int0 + this.x;
        return 31 * int0 + this.y;
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
            Vector2i vector2i = (Vector2i)arg0;
            return this.x != vector2i.x ? false : this.y == vector2i.y;
        }
    }

    @Override
    public boolean equals(int arg0, int arg1) {
        return this.x != arg0 ? false : this.y == arg1;
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "(" + numberFormat.format((long)this.x) + " " + numberFormat.format((long)this.y) + ")";
    }
}
