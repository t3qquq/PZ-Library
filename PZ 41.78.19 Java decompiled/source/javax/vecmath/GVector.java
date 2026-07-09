// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class GVector implements Serializable, Cloneable {
    private int length;
    double[] values;
    static final long serialVersionUID = 1398850036893875112L;

    public GVector(int int0) {
        this.length = int0;
        this.values = new double[int0];

        for (int int1 = 0; int1 < int0; int1++) {
            this.values[int1] = 0.0;
        }
    }

    public GVector(double[] doubles) {
        this.length = doubles.length;
        this.values = new double[doubles.length];

        for (int int0 = 0; int0 < this.length; int0++) {
            this.values[int0] = doubles[int0];
        }
    }

    public GVector(GVector gVector1) {
        this.values = new double[gVector1.length];
        this.length = gVector1.length;

        for (int int0 = 0; int0 < this.length; int0++) {
            this.values[int0] = gVector1.values[int0];
        }
    }

    public GVector(Tuple2f tuple2f) {
        this.values = new double[2];
        this.values[0] = tuple2f.x;
        this.values[1] = tuple2f.y;
        this.length = 2;
    }

    public GVector(Tuple3f tuple3f) {
        this.values = new double[3];
        this.values[0] = tuple3f.x;
        this.values[1] = tuple3f.y;
        this.values[2] = tuple3f.z;
        this.length = 3;
    }

    public GVector(Tuple3d tuple3d) {
        this.values = new double[3];
        this.values[0] = tuple3d.x;
        this.values[1] = tuple3d.y;
        this.values[2] = tuple3d.z;
        this.length = 3;
    }

    public GVector(Tuple4f tuple4f) {
        this.values = new double[4];
        this.values[0] = tuple4f.x;
        this.values[1] = tuple4f.y;
        this.values[2] = tuple4f.z;
        this.values[3] = tuple4f.w;
        this.length = 4;
    }

    public GVector(Tuple4d tuple4d) {
        this.values = new double[4];
        this.values[0] = tuple4d.x;
        this.values[1] = tuple4d.y;
        this.values[2] = tuple4d.z;
        this.values[3] = tuple4d.w;
        this.length = 4;
    }

    public GVector(double[] doubles, int int0) {
        this.length = int0;
        this.values = new double[int0];

        for (int int1 = 0; int1 < int0; int1++) {
            this.values[int1] = doubles[int1];
        }
    }

    public final double norm() {
        double double0 = 0.0;

        for (int int0 = 0; int0 < this.length; int0++) {
            double0 += this.values[int0] * this.values[int0];
        }

        return Math.sqrt(double0);
    }

    public final double normSquared() {
        double double0 = 0.0;

        for (int int0 = 0; int0 < this.length; int0++) {
            double0 += this.values[int0] * this.values[int0];
        }

        return double0;
    }

    public final void normalize(GVector gVector0) {
        double double0 = 0.0;
        if (this.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector0"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                double0 += gVector0.values[int0] * gVector0.values[int0];
            }

            double double1 = 1.0 / Math.sqrt(double0);

            for (int int1 = 0; int1 < this.length; int1++) {
                this.values[int1] = gVector0.values[int1] * double1;
            }
        }
    }

    public final void normalize() {
        double double0 = 0.0;

        for (int int0 = 0; int0 < this.length; int0++) {
            double0 += this.values[int0] * this.values[int0];
        }

        double double1 = 1.0 / Math.sqrt(double0);

        for (int int1 = 0; int1 < this.length; int1++) {
            this.values[int1] = this.values[int1] * double1;
        }
    }

    public final void scale(double double0, GVector gVector0) {
        if (this.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector1"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = gVector0.values[int0] * double0;
            }
        }
    }

    public final void scale(double double0) {
        for (int int0 = 0; int0 < this.length; int0++) {
            this.values[int0] = this.values[int0] * double0;
        }
    }

    public final void scaleAdd(double double0, GVector gVector0, GVector gVector1) {
        if (gVector1.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector2"));
        } else if (this.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector3"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = gVector0.values[int0] * double0 + gVector1.values[int0];
            }
        }
    }

    public final void add(GVector gVector0) {
        if (this.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector4"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = this.values[int0] + gVector0.values[int0];
            }
        }
    }

    public final void add(GVector gVector1, GVector gVector0) {
        if (gVector1.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector5"));
        } else if (this.length != gVector1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector6"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = gVector1.values[int0] + gVector0.values[int0];
            }
        }
    }

    public final void sub(GVector gVector0) {
        if (this.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector7"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = this.values[int0] - gVector0.values[int0];
            }
        }
    }

    public final void sub(GVector gVector1, GVector gVector0) {
        if (gVector1.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector8"));
        } else if (this.length != gVector1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector9"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = gVector1.values[int0] - gVector0.values[int0];
            }
        }
    }

    public final void mul(GMatrix gMatrix, GVector gVector0) {
        if (gMatrix.getNumCol() != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector10"));
        } else if (this.length != gMatrix.getNumRow()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector11"));
        } else {
            double[] doubles;
            if (gVector0 != this) {
                doubles = gVector0.values;
            } else {
                doubles = (double[])this.values.clone();
            }

            for (int int0 = this.length - 1; int0 >= 0; int0--) {
                this.values[int0] = 0.0;

                for (int int1 = gVector0.length - 1; int1 >= 0; int1--) {
                    this.values[int0] = this.values[int0] + gMatrix.values[int0][int1] * doubles[int1];
                }
            }
        }
    }

    public final void mul(GVector gVector0, GMatrix gMatrix) {
        if (gMatrix.getNumRow() != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector12"));
        } else if (this.length != gMatrix.getNumCol()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector13"));
        } else {
            double[] doubles;
            if (gVector0 != this) {
                doubles = gVector0.values;
            } else {
                doubles = (double[])this.values.clone();
            }

            for (int int0 = this.length - 1; int0 >= 0; int0--) {
                this.values[int0] = 0.0;

                for (int int1 = gVector0.length - 1; int1 >= 0; int1--) {
                    this.values[int0] = this.values[int0] + gMatrix.values[int1][int0] * doubles[int1];
                }
            }
        }
    }

    public final void negate() {
        for (int int0 = this.length - 1; int0 >= 0; int0--) {
            this.values[int0] = this.values[int0] * -1.0;
        }
    }

    public final void zero() {
        for (int int0 = 0; int0 < this.length; int0++) {
            this.values[int0] = 0.0;
        }
    }

    public final void setSize(int int0) {
        double[] doubles = new double[int0];
        int int1;
        if (this.length < int0) {
            int1 = this.length;
        } else {
            int1 = int0;
        }

        for (int int2 = 0; int2 < int1; int2++) {
            doubles[int2] = this.values[int2];
        }

        this.length = int0;
        this.values = doubles;
    }

    public final void set(double[] doubles) {
        for (int int0 = this.length - 1; int0 >= 0; int0--) {
            this.values[int0] = doubles[int0];
        }
    }

    public final void set(GVector gVector0) {
        if (this.length < gVector0.length) {
            this.length = gVector0.length;
            this.values = new double[this.length];

            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = gVector0.values[int0];
            }
        } else {
            for (int int1 = 0; int1 < gVector0.length; int1++) {
                this.values[int1] = gVector0.values[int1];
            }

            for (int int2 = gVector0.length; int2 < this.length; int2++) {
                this.values[int2] = 0.0;
            }
        }
    }

    public final void set(Tuple2f tuple2f) {
        if (this.length < 2) {
            this.length = 2;
            this.values = new double[2];
        }

        this.values[0] = tuple2f.x;
        this.values[1] = tuple2f.y;

        for (int int0 = 2; int0 < this.length; int0++) {
            this.values[int0] = 0.0;
        }
    }

    public final void set(Tuple3f tuple3f) {
        if (this.length < 3) {
            this.length = 3;
            this.values = new double[3];
        }

        this.values[0] = tuple3f.x;
        this.values[1] = tuple3f.y;
        this.values[2] = tuple3f.z;

        for (int int0 = 3; int0 < this.length; int0++) {
            this.values[int0] = 0.0;
        }
    }

    public final void set(Tuple3d tuple3d) {
        if (this.length < 3) {
            this.length = 3;
            this.values = new double[3];
        }

        this.values[0] = tuple3d.x;
        this.values[1] = tuple3d.y;
        this.values[2] = tuple3d.z;

        for (int int0 = 3; int0 < this.length; int0++) {
            this.values[int0] = 0.0;
        }
    }

    public final void set(Tuple4f tuple4f) {
        if (this.length < 4) {
            this.length = 4;
            this.values = new double[4];
        }

        this.values[0] = tuple4f.x;
        this.values[1] = tuple4f.y;
        this.values[2] = tuple4f.z;
        this.values[3] = tuple4f.w;

        for (int int0 = 4; int0 < this.length; int0++) {
            this.values[int0] = 0.0;
        }
    }

    public final void set(Tuple4d tuple4d) {
        if (this.length < 4) {
            this.length = 4;
            this.values = new double[4];
        }

        this.values[0] = tuple4d.x;
        this.values[1] = tuple4d.y;
        this.values[2] = tuple4d.z;
        this.values[3] = tuple4d.w;

        for (int int0 = 4; int0 < this.length; int0++) {
            this.values[int0] = 0.0;
        }
    }

    public final int getSize() {
        return this.values.length;
    }

    public final double getElement(int int0) {
        return this.values[int0];
    }

    public final void setElement(int int0, double double0) {
        this.values[int0] = double0;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(this.length * 8);

        for (int int0 = 0; int0 < this.length; int0++) {
            stringBuffer.append(this.values[int0]).append(" ");
        }

        return stringBuffer.toString();
    }

    @Override
    public int hashCode() {
        long long0 = 1L;

        for (int int0 = 0; int0 < this.length; int0++) {
            long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.values[int0]);
        }

        return (int)(long0 ^ long0 >> 32);
    }

    public boolean equals(GVector gVector0) {
        try {
            if (this.length != gVector0.length) {
                return false;
            } else {
                for (int int0 = 0; int0 < this.length; int0++) {
                    if (this.values[int0] != gVector0.values[int0]) {
                        return false;
                    }
                }

                return true;
            }
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            GVector gVector0 = (GVector)object;
            if (this.length != gVector0.length) {
                return false;
            } else {
                for (int int0 = 0; int0 < this.length; int0++) {
                    if (this.values[int0] != gVector0.values[int0]) {
                        return false;
                    }
                }

                return true;
            }
        } catch (ClassCastException classCastException) {
            return false;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    public boolean epsilonEquals(GVector gVector0, double double1) {
        if (this.length != gVector0.length) {
            return false;
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                double double0 = this.values[int0] - gVector0.values[int0];
                if ((double0 < 0.0 ? -double0 : double0) > double1) {
                    return false;
                }
            }

            return true;
        }
    }

    public final double dot(GVector gVector0) {
        if (this.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector14"));
        } else {
            double double0 = 0.0;

            for (int int0 = 0; int0 < this.length; int0++) {
                double0 += this.values[int0] * gVector0.values[int0];
            }

            return double0;
        }
    }

    public final void SVDBackSolve(GMatrix gMatrix1, GMatrix gMatrix0, GMatrix gMatrix2, GVector gVector0) {
        if (gMatrix1.nRow != gVector0.getSize() || gMatrix1.nRow != gMatrix1.nCol || gMatrix1.nRow != gMatrix0.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector15"));
        } else if (gMatrix0.nCol == this.values.length && gMatrix0.nCol == gMatrix2.nCol && gMatrix0.nCol == gMatrix2.nRow) {
            GMatrix gMatrix3 = new GMatrix(gMatrix1.nRow, gMatrix0.nCol);
            gMatrix3.mul(gMatrix1, gMatrix2);
            gMatrix3.mulTransposeRight(gMatrix1, gMatrix0);
            gMatrix3.invert();
            this.mul(gMatrix3, gVector0);
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector23"));
        }
    }

    public final void LUDBackSolve(GMatrix gMatrix, GVector gVector0, GVector gVector1) {
        int int0 = gMatrix.nRow * gMatrix.nCol;
        double[] doubles0 = new double[int0];
        double[] doubles1 = new double[int0];
        int[] ints = new int[gVector0.getSize()];
        if (gMatrix.nRow != gVector0.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector16"));
        } else if (gMatrix.nRow != gVector1.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector24"));
        } else if (gMatrix.nRow != gMatrix.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector25"));
        } else {
            for (int int1 = 0; int1 < gMatrix.nRow; int1++) {
                for (int int2 = 0; int2 < gMatrix.nCol; int2++) {
                    doubles0[int1 * gMatrix.nCol + int2] = gMatrix.values[int1][int2];
                }
            }

            for (int int3 = 0; int3 < int0; int3++) {
                doubles1[int3] = 0.0;
            }

            for (int int4 = 0; int4 < gMatrix.nRow; int4++) {
                doubles1[int4 * gMatrix.nCol] = gVector0.values[int4];
            }

            for (int int5 = 0; int5 < gMatrix.nCol; int5++) {
                ints[int5] = (int)gVector1.values[int5];
            }

            GMatrix.luBacksubstitution(gMatrix.nRow, doubles0, ints, doubles1);

            for (int int6 = 0; int6 < gMatrix.nRow; int6++) {
                this.values[int6] = doubles1[int6 * gMatrix.nCol];
            }
        }
    }

    public final double angle(GVector gVector0) {
        return Math.acos(this.dot(gVector0) / (this.norm() * gVector0.norm()));
    }

    /** @deprecated */
    public final void interpolate(GVector gVector1, GVector gVector2, float float0) {
        this.interpolate(gVector1, gVector2, (double)float0);
    }

    /** @deprecated */
    public final void interpolate(GVector gVector1, float float0) {
        this.interpolate(gVector1, (double)float0);
    }

    public final void interpolate(GVector gVector0, GVector gVector1, double double0) {
        if (gVector1.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector20"));
        } else if (this.length != gVector0.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector21"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = (1.0 - double0) * gVector0.values[int0] + double0 * gVector1.values[int0];
            }
        }
    }

    public final void interpolate(GVector gVector1, double double0) {
        if (gVector1.length != this.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector22"));
        } else {
            for (int int0 = 0; int0 < this.length; int0++) {
                this.values[int0] = (1.0 - double0) * this.values[int0] + double0 * gVector1.values[int0];
            }
        }
    }

    @Override
    public Object clone() {
        GVector gVector0 = null;

        try {
            gVector0 = (GVector)super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }

        gVector0.values = new double[this.length];

        for (int int0 = 0; int0 < this.length; int0++) {
            gVector0.values[int0] = this.values[int0];
        }

        return gVector0;
    }
}
