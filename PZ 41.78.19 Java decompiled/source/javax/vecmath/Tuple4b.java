// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4b implements Serializable, Cloneable {
    static final long serialVersionUID = -8226727741811898211L;
    public byte x;
    public byte y;
    public byte z;
    public byte w;

    public Tuple4b(byte byte0, byte byte1, byte byte2, byte byte3) {
        this.x = byte0;
        this.y = byte1;
        this.z = byte2;
        this.w = byte3;
    }

    public Tuple4b(byte[] bytes) {
        this.x = bytes[0];
        this.y = bytes[1];
        this.z = bytes[2];
        this.w = bytes[3];
    }

    public Tuple4b(Tuple4b tuple4b1) {
        this.x = tuple4b1.x;
        this.y = tuple4b1.y;
        this.z = tuple4b1.z;
        this.w = tuple4b1.w;
    }

    public Tuple4b() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    @Override
    public String toString() {
        return "(" + (this.x & 0xFF) + ", " + (this.y & 0xFF) + ", " + (this.z & 0xFF) + ", " + (this.w & 0xFF) + ")";
    }

    public final void get(byte[] bytes) {
        bytes[0] = this.x;
        bytes[1] = this.y;
        bytes[2] = this.z;
        bytes[3] = this.w;
    }

    public final void get(Tuple4b tuple4b1) {
        tuple4b1.x = this.x;
        tuple4b1.y = this.y;
        tuple4b1.z = this.z;
        tuple4b1.w = this.w;
    }

    public final void set(Tuple4b tuple4b0) {
        this.x = tuple4b0.x;
        this.y = tuple4b0.y;
        this.z = tuple4b0.z;
        this.w = tuple4b0.w;
    }

    public final void set(byte[] bytes) {
        this.x = bytes[0];
        this.y = bytes[1];
        this.z = bytes[2];
        this.w = bytes[3];
    }

    public boolean equals(Tuple4b tuple4b0) {
        try {
            return this.x == tuple4b0.x && this.y == tuple4b0.y && this.z == tuple4b0.z && this.w == tuple4b0.w;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple4b tuple4b0 = (Tuple4b)object;
            return this.x == tuple4b0.x && this.y == tuple4b0.y && this.z == tuple4b0.z && this.w == tuple4b0.w;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.x & 0xFF) << 0 | (this.y & 0xFF) << 8 | (this.z & 0xFF) << 16 | (this.w & 0xFF) << 24;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    public final byte getX() {
        return this.x;
    }

    public final void setX(byte byte0) {
        this.x = byte0;
    }

    public final byte getY() {
        return this.y;
    }

    public final void setY(byte byte0) {
        this.y = byte0;
    }

    public final byte getZ() {
        return this.z;
    }

    public final void setZ(byte byte0) {
        this.z = byte0;
    }

    public final byte getW() {
        return this.w;
    }

    public final void setW(byte byte0) {
        this.w = byte0;
    }
}
