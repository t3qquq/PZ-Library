// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple3b implements Serializable, Cloneable {
    static final long serialVersionUID = -483782685323607044L;
    public byte x;
    public byte y;
    public byte z;

    public Tuple3b(byte byte0, byte byte1, byte byte2) {
        this.x = byte0;
        this.y = byte1;
        this.z = byte2;
    }

    public Tuple3b(byte[] bytes) {
        this.x = bytes[0];
        this.y = bytes[1];
        this.z = bytes[2];
    }

    public Tuple3b(Tuple3b tuple3b1) {
        this.x = tuple3b1.x;
        this.y = tuple3b1.y;
        this.z = tuple3b1.z;
    }

    public Tuple3b() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    @Override
    public String toString() {
        return "(" + (this.x & 0xFF) + ", " + (this.y & 0xFF) + ", " + (this.z & 0xFF) + ")";
    }

    public final void get(byte[] bytes) {
        bytes[0] = this.x;
        bytes[1] = this.y;
        bytes[2] = this.z;
    }

    public final void get(Tuple3b tuple3b1) {
        tuple3b1.x = this.x;
        tuple3b1.y = this.y;
        tuple3b1.z = this.z;
    }

    public final void set(Tuple3b tuple3b0) {
        this.x = tuple3b0.x;
        this.y = tuple3b0.y;
        this.z = tuple3b0.z;
    }

    public final void set(byte[] bytes) {
        this.x = bytes[0];
        this.y = bytes[1];
        this.z = bytes[2];
    }

    public boolean equals(Tuple3b tuple3b0) {
        try {
            return this.x == tuple3b0.x && this.y == tuple3b0.y && this.z == tuple3b0.z;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Tuple3b tuple3b0 = (Tuple3b)object;
            return this.x == tuple3b0.x && this.y == tuple3b0.y && this.z == tuple3b0.z;
        } catch (NullPointerException nullPointerException) {
            return false;
        } catch (ClassCastException classCastException) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.x & 0xFF) << 0 | (this.y & 0xFF) << 8 | (this.z & 0xFF) << 16;
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
}
