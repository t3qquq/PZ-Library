// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Matrix3x2dStack extends Matrix3x2d {
    private static final long serialVersionUID = 1L;
    private Matrix3x2d[] mats;
    private int curr;

    public Matrix3x2dStack(int int0) {
        if (int0 < 1) {
            throw new IllegalArgumentException("stackSize must be >= 1");
        } else {
            this.mats = new Matrix3x2d[int0 - 1];

            for (int int1 = 0; int1 < this.mats.length; int1++) {
                this.mats[int1] = new Matrix3x2d();
            }
        }
    }

    public Matrix3x2dStack() {
    }

    public Matrix3x2dStack clear() {
        this.curr = 0;
        this.identity();
        return this;
    }

    public Matrix3x2dStack pushMatrix() {
        if (this.curr == this.mats.length) {
            throw new IllegalStateException("max stack size of " + (this.curr + 1) + " reached");
        } else {
            this.mats[this.curr++].set(this);
            return this;
        }
    }

    public Matrix3x2dStack popMatrix() {
        if (this.curr == 0) {
            throw new IllegalStateException("already at the buttom of the stack");
        } else {
            this.set(this.mats[--this.curr]);
            return this;
        }
    }

    @Override
    public int hashCode() {
        int int0 = super.hashCode();
        int0 = 31 * int0 + this.curr;

        for (int int1 = 0; int1 < this.curr; int1++) {
            int0 = 31 * int0 + this.mats[int1].hashCode();
        }

        return int0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (!super.equals(object)) {
            return false;
        } else {
            if (object instanceof Matrix3x2dStack matrix3x2dStack1) {
                if (this.curr != matrix3x2dStack1.curr) {
                    return false;
                }

                for (int int0 = 0; int0 < this.curr; int0++) {
                    if (!this.mats[int0].equals(matrix3x2dStack1.mats[int0])) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        super.writeExternal(objectOutput);
        objectOutput.writeInt(this.curr);

        for (int int0 = 0; int0 < this.curr; int0++) {
            objectOutput.writeObject(this.mats[int0]);
        }
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException {
        super.readExternal(objectInput);
        this.curr = objectInput.readInt();
        this.mats = new Matrix3x2dStack[this.curr];

        for (int int0 = 0; int0 < this.curr; int0++) {
            Matrix3x2d matrix3x2d = new Matrix3x2d();
            matrix3x2d.readExternal(objectInput);
            this.mats[int0] = matrix3x2d;
        }
    }
}
