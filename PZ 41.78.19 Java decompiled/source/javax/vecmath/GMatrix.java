// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class GMatrix implements Serializable, Cloneable {
    static final long serialVersionUID = 2777097312029690941L;
    private static final boolean debug = false;
    int nRow;
    int nCol;
    double[][] values;
    private static final double EPS = 1.0E-10;

    public GMatrix(int int0, int int1) {
        this.values = new double[int0][int1];
        this.nRow = int0;
        this.nCol = int1;

        for (int int2 = 0; int2 < int0; int2++) {
            for (int int3 = 0; int3 < int1; int3++) {
                this.values[int2][int3] = 0.0;
            }
        }

        int int4;
        if (int0 < int1) {
            int4 = int0;
        } else {
            int4 = int1;
        }

        for (int int5 = 0; int5 < int4; int5++) {
            this.values[int5][int5] = 1.0;
        }
    }

    public GMatrix(int int0, int int1, double[] doubles) {
        this.values = new double[int0][int1];
        this.nRow = int0;
        this.nCol = int1;

        for (int int2 = 0; int2 < int0; int2++) {
            for (int int3 = 0; int3 < int1; int3++) {
                this.values[int2][int3] = doubles[int2 * int1 + int3];
            }
        }
    }

    public GMatrix(GMatrix gMatrix1) {
        this.nRow = gMatrix1.nRow;
        this.nCol = gMatrix1.nCol;
        this.values = new double[this.nRow][this.nCol];

        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                this.values[int0][int1] = gMatrix1.values[int0][int1];
            }
        }
    }

    public final void mul(GMatrix gMatrix0) {
        if (this.nCol == gMatrix0.nRow && this.nCol == gMatrix0.nCol) {
            double[][] doubles = new double[this.nRow][this.nCol];

            for (int int0 = 0; int0 < this.nRow; int0++) {
                for (int int1 = 0; int1 < this.nCol; int1++) {
                    doubles[int0][int1] = 0.0;

                    for (int int2 = 0; int2 < this.nCol; int2++) {
                        doubles[int0][int1] = doubles[int0][int1] + this.values[int0][int2] * gMatrix0.values[int2][int1];
                    }
                }
            }

            this.values = doubles;
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix0"));
        }
    }

    public final void mul(GMatrix gMatrix2, GMatrix gMatrix0) {
        if (gMatrix2.nCol == gMatrix0.nRow && this.nRow == gMatrix2.nRow && this.nCol == gMatrix0.nCol) {
            double[][] doubles = new double[this.nRow][this.nCol];

            for (int int0 = 0; int0 < gMatrix2.nRow; int0++) {
                for (int int1 = 0; int1 < gMatrix0.nCol; int1++) {
                    doubles[int0][int1] = 0.0;

                    for (int int2 = 0; int2 < gMatrix2.nCol; int2++) {
                        doubles[int0][int1] = doubles[int0][int1] + gMatrix2.values[int0][int2] * gMatrix0.values[int2][int1];
                    }
                }
            }

            this.values = doubles;
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix1"));
        }
    }

    public final void mul(GVector gVector0, GVector gVector1) {
        if (this.nRow < gVector0.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix2"));
        } else if (this.nCol < gVector1.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix3"));
        } else {
            for (int int0 = 0; int0 < gVector0.getSize(); int0++) {
                for (int int1 = 0; int1 < gVector1.getSize(); int1++) {
                    this.values[int0][int1] = gVector0.values[int0] * gVector1.values[int1];
                }
            }
        }
    }

    public final void add(GMatrix gMatrix0) {
        if (this.nRow != gMatrix0.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix4"));
        } else if (this.nCol != gMatrix0.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix5"));
        } else {
            for (int int0 = 0; int0 < this.nRow; int0++) {
                for (int int1 = 0; int1 < this.nCol; int1++) {
                    this.values[int0][int1] = this.values[int0][int1] + gMatrix0.values[int0][int1];
                }
            }
        }
    }

    public final void add(GMatrix gMatrix0, GMatrix gMatrix1) {
        if (gMatrix1.nRow != gMatrix0.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix6"));
        } else if (gMatrix1.nCol != gMatrix0.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix7"));
        } else if (this.nCol == gMatrix0.nCol && this.nRow == gMatrix0.nRow) {
            for (int int0 = 0; int0 < this.nRow; int0++) {
                for (int int1 = 0; int1 < this.nCol; int1++) {
                    this.values[int0][int1] = gMatrix0.values[int0][int1] + gMatrix1.values[int0][int1];
                }
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix8"));
        }
    }

    public final void sub(GMatrix gMatrix0) {
        if (this.nRow != gMatrix0.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix9"));
        } else if (this.nCol != gMatrix0.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix28"));
        } else {
            for (int int0 = 0; int0 < this.nRow; int0++) {
                for (int int1 = 0; int1 < this.nCol; int1++) {
                    this.values[int0][int1] = this.values[int0][int1] - gMatrix0.values[int0][int1];
                }
            }
        }
    }

    public final void sub(GMatrix gMatrix0, GMatrix gMatrix1) {
        if (gMatrix1.nRow != gMatrix0.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix10"));
        } else if (gMatrix1.nCol != gMatrix0.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix11"));
        } else if (this.nRow == gMatrix0.nRow && this.nCol == gMatrix0.nCol) {
            for (int int0 = 0; int0 < this.nRow; int0++) {
                for (int int1 = 0; int1 < this.nCol; int1++) {
                    this.values[int0][int1] = gMatrix0.values[int0][int1] - gMatrix1.values[int0][int1];
                }
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix12"));
        }
    }

    public final void negate() {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                this.values[int0][int1] = -this.values[int0][int1];
            }
        }
    }

    public final void negate(GMatrix gMatrix0) {
        if (this.nRow == gMatrix0.nRow && this.nCol == gMatrix0.nCol) {
            for (int int0 = 0; int0 < this.nRow; int0++) {
                for (int int1 = 0; int1 < this.nCol; int1++) {
                    this.values[int0][int1] = -gMatrix0.values[int0][int1];
                }
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix13"));
        }
    }

    public final void setIdentity() {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                this.values[int0][int1] = 0.0;
            }
        }

        int int2;
        if (this.nRow < this.nCol) {
            int2 = this.nRow;
        } else {
            int2 = this.nCol;
        }

        for (int int3 = 0; int3 < int2; int3++) {
            this.values[int3][int3] = 1.0;
        }
    }

    public final void setZero() {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                this.values[int0][int1] = 0.0;
            }
        }
    }

    public final void identityMinus() {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                this.values[int0][int1] = -this.values[int0][int1];
            }
        }

        int int2;
        if (this.nRow < this.nCol) {
            int2 = this.nRow;
        } else {
            int2 = this.nCol;
        }

        for (int int3 = 0; int3 < int2; int3++) {
            this.values[int3][int3]++;
        }
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    public final void invert(GMatrix gMatrix1) {
        this.invertGeneral(gMatrix1);
    }

    public final void copySubMatrix(int int5, int int4, int int1, int int3, int int7, int int6, GMatrix gMatrix1) {
        if (this != gMatrix1) {
            for (int int0 = 0; int0 < int1; int0++) {
                for (int int2 = 0; int2 < int3; int2++) {
                    gMatrix1.values[int7 + int0][int6 + int2] = this.values[int5 + int0][int4 + int2];
                }
            }
        } else {
            double[][] doubles = new double[int1][int3];

            for (int int8 = 0; int8 < int1; int8++) {
                for (int int9 = 0; int9 < int3; int9++) {
                    doubles[int8][int9] = this.values[int5 + int8][int4 + int9];
                }
            }

            for (int int10 = 0; int10 < int1; int10++) {
                for (int int11 = 0; int11 < int3; int11++) {
                    gMatrix1.values[int7 + int10][int6 + int11] = doubles[int10][int11];
                }
            }
        }
    }

    public final void setSize(int int0, int int1) {
        double[][] doubles = new double[int0][int1];
        int int2;
        if (this.nRow < int0) {
            int2 = this.nRow;
        } else {
            int2 = int0;
        }

        int int3;
        if (this.nCol < int1) {
            int3 = this.nCol;
        } else {
            int3 = int1;
        }

        for (int int4 = 0; int4 < int2; int4++) {
            for (int int5 = 0; int5 < int3; int5++) {
                doubles[int4][int5] = this.values[int4][int5];
            }
        }

        this.nRow = int0;
        this.nCol = int1;
        this.values = doubles;
    }

    public final void set(double[] doubles) {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                this.values[int0][int1] = doubles[this.nCol * int0 + int1];
            }
        }
    }

    public final void set(Matrix3f matrix3f) {
        if (this.nCol < 3 || this.nRow < 3) {
            this.nCol = 3;
            this.nRow = 3;
            this.values = new double[this.nRow][this.nCol];
        }

        this.values[0][0] = matrix3f.m00;
        this.values[0][1] = matrix3f.m01;
        this.values[0][2] = matrix3f.m02;
        this.values[1][0] = matrix3f.m10;
        this.values[1][1] = matrix3f.m11;
        this.values[1][2] = matrix3f.m12;
        this.values[2][0] = matrix3f.m20;
        this.values[2][1] = matrix3f.m21;
        this.values[2][2] = matrix3f.m22;

        for (int int0 = 3; int0 < this.nRow; int0++) {
            for (int int1 = 3; int1 < this.nCol; int1++) {
                this.values[int0][int1] = 0.0;
            }
        }
    }

    public final void set(Matrix3d matrix3d) {
        if (this.nRow < 3 || this.nCol < 3) {
            this.values = new double[3][3];
            this.nRow = 3;
            this.nCol = 3;
        }

        this.values[0][0] = matrix3d.m00;
        this.values[0][1] = matrix3d.m01;
        this.values[0][2] = matrix3d.m02;
        this.values[1][0] = matrix3d.m10;
        this.values[1][1] = matrix3d.m11;
        this.values[1][2] = matrix3d.m12;
        this.values[2][0] = matrix3d.m20;
        this.values[2][1] = matrix3d.m21;
        this.values[2][2] = matrix3d.m22;

        for (int int0 = 3; int0 < this.nRow; int0++) {
            for (int int1 = 3; int1 < this.nCol; int1++) {
                this.values[int0][int1] = 0.0;
            }
        }
    }

    public final void set(Matrix4f matrix4f) {
        if (this.nRow < 4 || this.nCol < 4) {
            this.values = new double[4][4];
            this.nRow = 4;
            this.nCol = 4;
        }

        this.values[0][0] = matrix4f.m00;
        this.values[0][1] = matrix4f.m01;
        this.values[0][2] = matrix4f.m02;
        this.values[0][3] = matrix4f.m03;
        this.values[1][0] = matrix4f.m10;
        this.values[1][1] = matrix4f.m11;
        this.values[1][2] = matrix4f.m12;
        this.values[1][3] = matrix4f.m13;
        this.values[2][0] = matrix4f.m20;
        this.values[2][1] = matrix4f.m21;
        this.values[2][2] = matrix4f.m22;
        this.values[2][3] = matrix4f.m23;
        this.values[3][0] = matrix4f.m30;
        this.values[3][1] = matrix4f.m31;
        this.values[3][2] = matrix4f.m32;
        this.values[3][3] = matrix4f.m33;

        for (int int0 = 4; int0 < this.nRow; int0++) {
            for (int int1 = 4; int1 < this.nCol; int1++) {
                this.values[int0][int1] = 0.0;
            }
        }
    }

    public final void set(Matrix4d matrix4d) {
        if (this.nRow < 4 || this.nCol < 4) {
            this.values = new double[4][4];
            this.nRow = 4;
            this.nCol = 4;
        }

        this.values[0][0] = matrix4d.m00;
        this.values[0][1] = matrix4d.m01;
        this.values[0][2] = matrix4d.m02;
        this.values[0][3] = matrix4d.m03;
        this.values[1][0] = matrix4d.m10;
        this.values[1][1] = matrix4d.m11;
        this.values[1][2] = matrix4d.m12;
        this.values[1][3] = matrix4d.m13;
        this.values[2][0] = matrix4d.m20;
        this.values[2][1] = matrix4d.m21;
        this.values[2][2] = matrix4d.m22;
        this.values[2][3] = matrix4d.m23;
        this.values[3][0] = matrix4d.m30;
        this.values[3][1] = matrix4d.m31;
        this.values[3][2] = matrix4d.m32;
        this.values[3][3] = matrix4d.m33;

        for (int int0 = 4; int0 < this.nRow; int0++) {
            for (int int1 = 4; int1 < this.nCol; int1++) {
                this.values[int0][int1] = 0.0;
            }
        }
    }

    public final void set(GMatrix gMatrix0) {
        if (this.nRow < gMatrix0.nRow || this.nCol < gMatrix0.nCol) {
            this.nRow = gMatrix0.nRow;
            this.nCol = gMatrix0.nCol;
            this.values = new double[this.nRow][this.nCol];
        }

        for (int int0 = 0; int0 < Math.min(this.nRow, gMatrix0.nRow); int0++) {
            for (int int1 = 0; int1 < Math.min(this.nCol, gMatrix0.nCol); int1++) {
                this.values[int0][int1] = gMatrix0.values[int0][int1];
            }
        }

        for (int int2 = gMatrix0.nRow; int2 < this.nRow; int2++) {
            for (int int3 = gMatrix0.nCol; int3 < this.nCol; int3++) {
                this.values[int2][int3] = 0.0;
            }
        }
    }

    public final int getNumRow() {
        return this.nRow;
    }

    public final int getNumCol() {
        return this.nCol;
    }

    public final double getElement(int int1, int int0) {
        return this.values[int1][int0];
    }

    public final void setElement(int int1, int int0, double double0) {
        this.values[int1][int0] = double0;
    }

    public final void getRow(int int1, double[] doubles) {
        for (int int0 = 0; int0 < this.nCol; int0++) {
            doubles[int0] = this.values[int1][int0];
        }
    }

    public final void getRow(int int1, GVector gVector) {
        if (gVector.getSize() < this.nCol) {
            gVector.setSize(this.nCol);
        }

        for (int int0 = 0; int0 < this.nCol; int0++) {
            gVector.values[int0] = this.values[int1][int0];
        }
    }

    public final void getColumn(int int1, double[] doubles) {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            doubles[int0] = this.values[int0][int1];
        }
    }

    public final void getColumn(int int1, GVector gVector) {
        if (gVector.getSize() < this.nRow) {
            gVector.setSize(this.nRow);
        }

        for (int int0 = 0; int0 < this.nRow; int0++) {
            gVector.values[int0] = this.values[int0][int1];
        }
    }

    public final void get(Matrix3d matrix3d) {
        if (this.nRow >= 3 && this.nCol >= 3) {
            matrix3d.m00 = this.values[0][0];
            matrix3d.m01 = this.values[0][1];
            matrix3d.m02 = this.values[0][2];
            matrix3d.m10 = this.values[1][0];
            matrix3d.m11 = this.values[1][1];
            matrix3d.m12 = this.values[1][2];
            matrix3d.m20 = this.values[2][0];
            matrix3d.m21 = this.values[2][1];
            matrix3d.m22 = this.values[2][2];
        } else {
            matrix3d.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    matrix3d.m00 = this.values[0][0];
                    if (this.nRow > 1) {
                        matrix3d.m10 = this.values[1][0];
                        if (this.nRow > 2) {
                            matrix3d.m20 = this.values[2][0];
                        }
                    }
                }

                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        matrix3d.m01 = this.values[0][1];
                        if (this.nRow > 1) {
                            matrix3d.m11 = this.values[1][1];
                            if (this.nRow > 2) {
                                matrix3d.m21 = this.values[2][1];
                            }
                        }
                    }

                    if (this.nCol > 2 && this.nRow > 0) {
                        matrix3d.m02 = this.values[0][2];
                        if (this.nRow > 1) {
                            matrix3d.m12 = this.values[1][2];
                            if (this.nRow > 2) {
                                matrix3d.m22 = this.values[2][2];
                            }
                        }
                    }
                }
            }
        }
    }

    public final void get(Matrix3f matrix3f) {
        if (this.nRow >= 3 && this.nCol >= 3) {
            matrix3f.m00 = (float)this.values[0][0];
            matrix3f.m01 = (float)this.values[0][1];
            matrix3f.m02 = (float)this.values[0][2];
            matrix3f.m10 = (float)this.values[1][0];
            matrix3f.m11 = (float)this.values[1][1];
            matrix3f.m12 = (float)this.values[1][2];
            matrix3f.m20 = (float)this.values[2][0];
            matrix3f.m21 = (float)this.values[2][1];
            matrix3f.m22 = (float)this.values[2][2];
        } else {
            matrix3f.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    matrix3f.m00 = (float)this.values[0][0];
                    if (this.nRow > 1) {
                        matrix3f.m10 = (float)this.values[1][0];
                        if (this.nRow > 2) {
                            matrix3f.m20 = (float)this.values[2][0];
                        }
                    }
                }

                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        matrix3f.m01 = (float)this.values[0][1];
                        if (this.nRow > 1) {
                            matrix3f.m11 = (float)this.values[1][1];
                            if (this.nRow > 2) {
                                matrix3f.m21 = (float)this.values[2][1];
                            }
                        }
                    }

                    if (this.nCol > 2 && this.nRow > 0) {
                        matrix3f.m02 = (float)this.values[0][2];
                        if (this.nRow > 1) {
                            matrix3f.m12 = (float)this.values[1][2];
                            if (this.nRow > 2) {
                                matrix3f.m22 = (float)this.values[2][2];
                            }
                        }
                    }
                }
            }
        }
    }

    public final void get(Matrix4d matrix4d) {
        if (this.nRow >= 4 && this.nCol >= 4) {
            matrix4d.m00 = this.values[0][0];
            matrix4d.m01 = this.values[0][1];
            matrix4d.m02 = this.values[0][2];
            matrix4d.m03 = this.values[0][3];
            matrix4d.m10 = this.values[1][0];
            matrix4d.m11 = this.values[1][1];
            matrix4d.m12 = this.values[1][2];
            matrix4d.m13 = this.values[1][3];
            matrix4d.m20 = this.values[2][0];
            matrix4d.m21 = this.values[2][1];
            matrix4d.m22 = this.values[2][2];
            matrix4d.m23 = this.values[2][3];
            matrix4d.m30 = this.values[3][0];
            matrix4d.m31 = this.values[3][1];
            matrix4d.m32 = this.values[3][2];
            matrix4d.m33 = this.values[3][3];
        } else {
            matrix4d.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    matrix4d.m00 = this.values[0][0];
                    if (this.nRow > 1) {
                        matrix4d.m10 = this.values[1][0];
                        if (this.nRow > 2) {
                            matrix4d.m20 = this.values[2][0];
                            if (this.nRow > 3) {
                                matrix4d.m30 = this.values[3][0];
                            }
                        }
                    }
                }

                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        matrix4d.m01 = this.values[0][1];
                        if (this.nRow > 1) {
                            matrix4d.m11 = this.values[1][1];
                            if (this.nRow > 2) {
                                matrix4d.m21 = this.values[2][1];
                                if (this.nRow > 3) {
                                    matrix4d.m31 = this.values[3][1];
                                }
                            }
                        }
                    }

                    if (this.nCol > 2) {
                        if (this.nRow > 0) {
                            matrix4d.m02 = this.values[0][2];
                            if (this.nRow > 1) {
                                matrix4d.m12 = this.values[1][2];
                                if (this.nRow > 2) {
                                    matrix4d.m22 = this.values[2][2];
                                    if (this.nRow > 3) {
                                        matrix4d.m32 = this.values[3][2];
                                    }
                                }
                            }
                        }

                        if (this.nCol > 3 && this.nRow > 0) {
                            matrix4d.m03 = this.values[0][3];
                            if (this.nRow > 1) {
                                matrix4d.m13 = this.values[1][3];
                                if (this.nRow > 2) {
                                    matrix4d.m23 = this.values[2][3];
                                    if (this.nRow > 3) {
                                        matrix4d.m33 = this.values[3][3];
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public final void get(Matrix4f matrix4f) {
        if (this.nRow >= 4 && this.nCol >= 4) {
            matrix4f.m00 = (float)this.values[0][0];
            matrix4f.m01 = (float)this.values[0][1];
            matrix4f.m02 = (float)this.values[0][2];
            matrix4f.m03 = (float)this.values[0][3];
            matrix4f.m10 = (float)this.values[1][0];
            matrix4f.m11 = (float)this.values[1][1];
            matrix4f.m12 = (float)this.values[1][2];
            matrix4f.m13 = (float)this.values[1][3];
            matrix4f.m20 = (float)this.values[2][0];
            matrix4f.m21 = (float)this.values[2][1];
            matrix4f.m22 = (float)this.values[2][2];
            matrix4f.m23 = (float)this.values[2][3];
            matrix4f.m30 = (float)this.values[3][0];
            matrix4f.m31 = (float)this.values[3][1];
            matrix4f.m32 = (float)this.values[3][2];
            matrix4f.m33 = (float)this.values[3][3];
        } else {
            matrix4f.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    matrix4f.m00 = (float)this.values[0][0];
                    if (this.nRow > 1) {
                        matrix4f.m10 = (float)this.values[1][0];
                        if (this.nRow > 2) {
                            matrix4f.m20 = (float)this.values[2][0];
                            if (this.nRow > 3) {
                                matrix4f.m30 = (float)this.values[3][0];
                            }
                        }
                    }
                }

                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        matrix4f.m01 = (float)this.values[0][1];
                        if (this.nRow > 1) {
                            matrix4f.m11 = (float)this.values[1][1];
                            if (this.nRow > 2) {
                                matrix4f.m21 = (float)this.values[2][1];
                                if (this.nRow > 3) {
                                    matrix4f.m31 = (float)this.values[3][1];
                                }
                            }
                        }
                    }

                    if (this.nCol > 2) {
                        if (this.nRow > 0) {
                            matrix4f.m02 = (float)this.values[0][2];
                            if (this.nRow > 1) {
                                matrix4f.m12 = (float)this.values[1][2];
                                if (this.nRow > 2) {
                                    matrix4f.m22 = (float)this.values[2][2];
                                    if (this.nRow > 3) {
                                        matrix4f.m32 = (float)this.values[3][2];
                                    }
                                }
                            }
                        }

                        if (this.nCol > 3 && this.nRow > 0) {
                            matrix4f.m03 = (float)this.values[0][3];
                            if (this.nRow > 1) {
                                matrix4f.m13 = (float)this.values[1][3];
                                if (this.nRow > 2) {
                                    matrix4f.m23 = (float)this.values[2][3];
                                    if (this.nRow > 3) {
                                        matrix4f.m33 = (float)this.values[3][3];
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public final void get(GMatrix gMatrix0) {
        int int0;
        if (this.nCol < gMatrix0.nCol) {
            int0 = this.nCol;
        } else {
            int0 = gMatrix0.nCol;
        }

        int int1;
        if (this.nRow < gMatrix0.nRow) {
            int1 = this.nRow;
        } else {
            int1 = gMatrix0.nRow;
        }

        for (int int2 = 0; int2 < int1; int2++) {
            for (int int3 = 0; int3 < int0; int3++) {
                gMatrix0.values[int2][int3] = this.values[int2][int3];
            }
        }

        for (int int4 = int1; int4 < gMatrix0.nRow; int4++) {
            for (int int5 = 0; int5 < gMatrix0.nCol; int5++) {
                gMatrix0.values[int4][int5] = 0.0;
            }
        }

        for (int int6 = int0; int6 < gMatrix0.nCol; int6++) {
            for (int int7 = 0; int7 < int1; int7++) {
                gMatrix0.values[int7][int6] = 0.0;
            }
        }
    }

    public final void setRow(int int1, double[] doubles) {
        for (int int0 = 0; int0 < this.nCol; int0++) {
            this.values[int1][int0] = doubles[int0];
        }
    }

    public final void setRow(int int1, GVector gVector) {
        for (int int0 = 0; int0 < this.nCol; int0++) {
            this.values[int1][int0] = gVector.values[int0];
        }
    }

    public final void setColumn(int int1, double[] doubles) {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            this.values[int0][int1] = doubles[int0];
        }
    }

    public final void setColumn(int int1, GVector gVector) {
        for (int int0 = 0; int0 < this.nRow; int0++) {
            this.values[int0][int1] = gVector.values[int0];
        }
    }

    public final void mulTransposeBoth(GMatrix gMatrix2, GMatrix gMatrix0) {
        if (gMatrix2.nRow == gMatrix0.nCol && this.nRow == gMatrix2.nCol && this.nCol == gMatrix0.nRow) {
            if (gMatrix2 != this && gMatrix0 != this) {
                for (int int0 = 0; int0 < this.nRow; int0++) {
                    for (int int1 = 0; int1 < this.nCol; int1++) {
                        this.values[int0][int1] = 0.0;

                        for (int int2 = 0; int2 < gMatrix2.nRow; int2++) {
                            this.values[int0][int1] = this.values[int0][int1] + gMatrix2.values[int2][int0] * gMatrix0.values[int1][int2];
                        }
                    }
                }
            } else {
                double[][] doubles = new double[this.nRow][this.nCol];

                for (int int3 = 0; int3 < this.nRow; int3++) {
                    for (int int4 = 0; int4 < this.nCol; int4++) {
                        doubles[int3][int4] = 0.0;

                        for (int int5 = 0; int5 < gMatrix2.nRow; int5++) {
                            doubles[int3][int4] = doubles[int3][int4] + gMatrix2.values[int5][int3] * gMatrix0.values[int4][int5];
                        }
                    }
                }

                this.values = doubles;
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix14"));
        }
    }

    public final void mulTransposeRight(GMatrix gMatrix0, GMatrix gMatrix2) {
        if (gMatrix0.nCol == gMatrix2.nCol && this.nCol == gMatrix2.nRow && this.nRow == gMatrix0.nRow) {
            if (gMatrix0 != this && gMatrix2 != this) {
                for (int int0 = 0; int0 < this.nRow; int0++) {
                    for (int int1 = 0; int1 < this.nCol; int1++) {
                        this.values[int0][int1] = 0.0;

                        for (int int2 = 0; int2 < gMatrix0.nCol; int2++) {
                            this.values[int0][int1] = this.values[int0][int1] + gMatrix0.values[int0][int2] * gMatrix2.values[int1][int2];
                        }
                    }
                }
            } else {
                double[][] doubles = new double[this.nRow][this.nCol];

                for (int int3 = 0; int3 < this.nRow; int3++) {
                    for (int int4 = 0; int4 < this.nCol; int4++) {
                        doubles[int3][int4] = 0.0;

                        for (int int5 = 0; int5 < gMatrix0.nCol; int5++) {
                            doubles[int3][int4] = doubles[int3][int4] + gMatrix0.values[int3][int5] * gMatrix2.values[int4][int5];
                        }
                    }
                }

                this.values = doubles;
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix15"));
        }
    }

    public final void mulTransposeLeft(GMatrix gMatrix0, GMatrix gMatrix2) {
        if (gMatrix0.nRow == gMatrix2.nRow && this.nCol == gMatrix2.nCol && this.nRow == gMatrix0.nCol) {
            if (gMatrix0 != this && gMatrix2 != this) {
                for (int int0 = 0; int0 < this.nRow; int0++) {
                    for (int int1 = 0; int1 < this.nCol; int1++) {
                        this.values[int0][int1] = 0.0;

                        for (int int2 = 0; int2 < gMatrix0.nRow; int2++) {
                            this.values[int0][int1] = this.values[int0][int1] + gMatrix0.values[int2][int0] * gMatrix2.values[int2][int1];
                        }
                    }
                }
            } else {
                double[][] doubles = new double[this.nRow][this.nCol];

                for (int int3 = 0; int3 < this.nRow; int3++) {
                    for (int int4 = 0; int4 < this.nCol; int4++) {
                        doubles[int3][int4] = 0.0;

                        for (int int5 = 0; int5 < gMatrix0.nRow; int5++) {
                            doubles[int3][int4] = doubles[int3][int4] + gMatrix0.values[int5][int3] * gMatrix2.values[int5][int4];
                        }
                    }
                }

                this.values = doubles;
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix16"));
        }
    }

    public final void transpose() {
        if (this.nRow != this.nCol) {
            int int0 = this.nRow;
            this.nRow = this.nCol;
            this.nCol = int0;
            double[][] doubles = new double[this.nRow][this.nCol];

            for (int int1 = 0; int1 < this.nRow; int1++) {
                for (int int2 = 0; int2 < this.nCol; int2++) {
                    doubles[int1][int2] = this.values[int2][int1];
                }
            }

            this.values = doubles;
        } else {
            for (int int3 = 0; int3 < this.nRow; int3++) {
                for (int int4 = 0; int4 < int3; int4++) {
                    double double0 = this.values[int3][int4];
                    this.values[int3][int4] = this.values[int4][int3];
                    this.values[int4][int3] = double0;
                }
            }
        }
    }

    public final void transpose(GMatrix gMatrix0) {
        if (this.nRow == gMatrix0.nCol && this.nCol == gMatrix0.nRow) {
            if (gMatrix0 != this) {
                for (int int0 = 0; int0 < this.nRow; int0++) {
                    for (int int1 = 0; int1 < this.nCol; int1++) {
                        this.values[int0][int1] = gMatrix0.values[int1][int0];
                    }
                }
            } else {
                this.transpose();
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix17"));
        }
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(this.nRow * this.nCol * 8);

        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                stringBuffer.append(this.values[int0][int1]).append(" ");
            }

            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    private static void checkMatrix(GMatrix gMatrix) {
        for (int int0 = 0; int0 < gMatrix.nRow; int0++) {
            for (int int1 = 0; int1 < gMatrix.nCol; int1++) {
                if (Math.abs(gMatrix.values[int0][int1]) < 1.0E-10) {
                    System.out.print(" 0.0     ");
                } else {
                    System.out.print(" " + gMatrix.values[int0][int1]);
                }
            }

            System.out.print("\n");
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + this.nRow;
        long0 = 31L * long0 + this.nCol;

        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.values[int0][int1]);
            }
        }

        return (int)(long0 ^ long0 >> 32);
    }

    public boolean equals(GMatrix gMatrix0) {
        try {
            if (this.nRow == gMatrix0.nRow && this.nCol == gMatrix0.nCol) {
                for (int int0 = 0; int0 < this.nRow; int0++) {
                    for (int int1 = 0; int1 < this.nCol; int1++) {
                        if (this.values[int0][int1] != gMatrix0.values[int0][int1]) {
                            return false;
                        }
                    }
                }

                return true;
            } else {
                return false;
            }
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            GMatrix gMatrix0 = (GMatrix)object;
            if (this.nRow == gMatrix0.nRow && this.nCol == gMatrix0.nCol) {
                for (int int0 = 0; int0 < this.nRow; int0++) {
                    for (int int1 = 0; int1 < this.nCol; int1++) {
                        if (this.values[int0][int1] != gMatrix0.values[int0][int1]) {
                            return false;
                        }
                    }
                }

                return true;
            } else {
                return false;
            }
        } catch (ClassCastException classCastException) {
            return false;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    /** @deprecated */
    public boolean epsilonEquals(GMatrix gMatrix1, float float0) {
        return this.epsilonEquals(gMatrix1, (double)float0);
    }

    public boolean epsilonEquals(GMatrix gMatrix0, double double1) {
        if (this.nRow == gMatrix0.nRow && this.nCol == gMatrix0.nCol) {
            for (int int0 = 0; int0 < this.nRow; int0++) {
                for (int int1 = 0; int1 < this.nCol; int1++) {
                    double double0 = this.values[int0][int1] - gMatrix0.values[int0][int1];
                    if ((double0 < 0.0 ? -double0 : double0) > double1) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public final double trace() {
        int int0;
        if (this.nRow < this.nCol) {
            int0 = this.nRow;
        } else {
            int0 = this.nCol;
        }

        double double0 = 0.0;

        for (int int1 = 0; int1 < int0; int1++) {
            double0 += this.values[int1][int1];
        }

        return double0;
    }

    public final int SVD(GMatrix gMatrix2, GMatrix gMatrix3, GMatrix gMatrix0) {
        if (this.nCol != gMatrix0.nCol || this.nCol != gMatrix0.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix18"));
        } else if (this.nRow != gMatrix2.nRow || this.nRow != gMatrix2.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix25"));
        } else if (this.nRow == gMatrix3.nRow && this.nCol == gMatrix3.nCol) {
            if (this.nRow == 2 && this.nCol == 2 && this.values[1][0] == 0.0) {
                gMatrix2.setIdentity();
                gMatrix0.setIdentity();
                if (this.values[0][1] == 0.0) {
                    return 2;
                } else {
                    double[] doubles0 = new double[1];
                    double[] doubles1 = new double[1];
                    double[] doubles2 = new double[1];
                    double[] doubles3 = new double[1];
                    double[] doubles4 = new double[]{this.values[0][0], this.values[1][1]};
                    compute_2X2(this.values[0][0], this.values[0][1], this.values[1][1], doubles4, doubles0, doubles2, doubles1, doubles3, 0);
                    update_u(0, gMatrix2, doubles2, doubles0);
                    update_v(0, gMatrix0, doubles3, doubles1);
                    return 2;
                }
            } else {
                return computeSVD(this, gMatrix2, gMatrix3, gMatrix0);
            }
        } else {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix26"));
        }
    }

    public final int LUD(GMatrix gMatrix0, GVector gVector) {
        int int0 = gMatrix0.nRow * gMatrix0.nCol;
        double[] doubles = new double[int0];
        int[] ints0 = new int[1];
        int[] ints1 = new int[gMatrix0.nRow];
        if (this.nRow != this.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix19"));
        } else if (this.nRow != gMatrix0.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
        } else if (this.nCol != gMatrix0.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
        } else if (gMatrix0.nRow != gVector.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix20"));
        } else {
            for (int int1 = 0; int1 < this.nRow; int1++) {
                for (int int2 = 0; int2 < this.nCol; int2++) {
                    doubles[int1 * this.nCol + int2] = this.values[int1][int2];
                }
            }

            if (!luDecomposition(gMatrix0.nRow, doubles, ints1, ints0)) {
                throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
            } else {
                for (int int3 = 0; int3 < this.nRow; int3++) {
                    for (int int4 = 0; int4 < this.nCol; int4++) {
                        gMatrix0.values[int3][int4] = doubles[int3 * this.nCol + int4];
                    }
                }

                for (int int5 = 0; int5 < gMatrix0.nRow; int5++) {
                    gVector.values[int5] = ints1[int5];
                }

                return ints0[0];
            }
        }
    }

    public final void setScale(double double0) {
        int int0;
        if (this.nRow < this.nCol) {
            int0 = this.nRow;
        } else {
            int0 = this.nCol;
        }

        for (int int1 = 0; int1 < this.nRow; int1++) {
            for (int int2 = 0; int2 < this.nCol; int2++) {
                this.values[int1][int2] = 0.0;
            }
        }

        for (int int3 = 0; int3 < int0; int3++) {
            this.values[int3][int3] = double0;
        }
    }

    final void invertGeneral(GMatrix gMatrix0) {
        int int0 = gMatrix0.nRow * gMatrix0.nCol;
        double[] doubles0 = new double[int0];
        double[] doubles1 = new double[int0];
        int[] ints0 = new int[gMatrix0.nRow];
        int[] ints1 = new int[1];
        if (gMatrix0.nRow != gMatrix0.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix22"));
        } else {
            for (int int1 = 0; int1 < this.nRow; int1++) {
                for (int int2 = 0; int2 < this.nCol; int2++) {
                    doubles0[int1 * this.nCol + int2] = gMatrix0.values[int1][int2];
                }
            }

            if (!luDecomposition(gMatrix0.nRow, doubles0, ints0, ints1)) {
                throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
            } else {
                for (int int3 = 0; int3 < int0; int3++) {
                    doubles1[int3] = 0.0;
                }

                for (int int4 = 0; int4 < this.nCol; int4++) {
                    doubles1[int4 + int4 * this.nCol] = 1.0;
                }

                luBacksubstitution(gMatrix0.nRow, doubles0, ints0, doubles1);

                for (int int5 = 0; int5 < this.nRow; int5++) {
                    for (int int6 = 0; int6 < this.nCol; int6++) {
                        this.values[int5][int6] = doubles1[int5 * this.nCol + int6];
                    }
                }
            }
        }
    }

    static boolean luDecomposition(int int0, double[] doubles1, int[] ints1, int[] ints0) {
        double[] doubles0 = new double[int0];
        int int1 = 0;
        int int2 = 0;
        ints0[0] = 1;
        int int3 = int0;

        while (int3-- != 0) {
            double double0 = 0.0;
            int int4 = int0;

            while (int4-- != 0) {
                double double1 = doubles1[int1++];
                double1 = Math.abs(double1);
                if (double1 > double0) {
                    double0 = double1;
                }
            }

            if (double0 == 0.0) {
                return false;
            }

            doubles0[int2++] = 1.0 / double0;
        }

        byte byte0 = 0;

        for (int int5 = 0; int5 < int0; int5++) {
            for (int int6 = 0; int6 < int5; int6++) {
                int int7 = byte0 + int0 * int6 + int5;
                double double2 = doubles1[int7];
                int int8 = int6;
                int int9 = byte0 + int0 * int6;

                for (int int10 = byte0 + int5; int8-- != 0; int10 += int0) {
                    double2 -= doubles1[int9] * doubles1[int10];
                    int9++;
                }

                doubles1[int7] = double2;
            }

            double double3 = 0.0;
            int int11 = -1;

            for (int int12 = int5; int12 < int0; int12++) {
                int int13 = byte0 + int0 * int12 + int5;
                double double4 = doubles1[int13];
                int int14 = int5;
                int int15 = byte0 + int0 * int12;

                for (int int16 = byte0 + int5; int14-- != 0; int16 += int0) {
                    double4 -= doubles1[int15] * doubles1[int16];
                    int15++;
                }

                doubles1[int13] = double4;
                double double5;
                if ((double5 = doubles0[int12] * Math.abs(double4)) >= double3) {
                    double3 = double5;
                    int11 = int12;
                }
            }

            if (int11 < 0) {
                throw new RuntimeException(VecMathI18N.getString("GMatrix24"));
            }

            if (int5 != int11) {
                int int17 = int0;
                int int18 = byte0 + int0 * int11;
                int int19 = byte0 + int0 * int5;

                while (int17-- != 0) {
                    double double6 = doubles1[int18];
                    doubles1[int18++] = doubles1[int19];
                    doubles1[int19++] = double6;
                }

                doubles0[int11] = doubles0[int5];
                ints0[0] = -ints0[0];
            }

            ints1[int5] = int11;
            if (doubles1[byte0 + int0 * int5 + int5] == 0.0) {
                return false;
            }

            if (int5 != int0 - 1) {
                double double7 = 1.0 / doubles1[byte0 + int0 * int5 + int5];
                int int20 = byte0 + int0 * (int5 + 1) + int5;

                for (int int21 = int0 - 1 - int5; int21-- != 0; int20 += int0) {
                    doubles1[int20] *= double7;
                }
            }
        }

        return true;
    }

    static void luBacksubstitution(int int1, double[] doubles1, int[] ints, double[] doubles0) {
        byte byte0 = 0;

        for (int int0 = 0; int0 < int1; int0++) {
            int int2 = int0;
            int int3 = -1;

            for (int int4 = 0; int4 < int1; int4++) {
                int int5 = ints[byte0 + int4];
                double double0 = doubles0[int2 + int1 * int5];
                doubles0[int2 + int1 * int5] = doubles0[int2 + int1 * int4];
                if (int3 >= 0) {
                    int int6 = int4 * int1;

                    for (int int7 = int3; int7 <= int4 - 1; int7++) {
                        double0 -= doubles1[int6 + int7] * doubles0[int2 + int1 * int7];
                    }
                } else if (double0 != 0.0) {
                    int3 = int4;
                }

                doubles0[int2 + int1 * int4] = double0;
            }

            for (int int8 = 0; int8 < int1; int8++) {
                int int9 = int1 - 1 - int8;
                int int10 = int1 * int9;
                double double1 = 0.0;

                for (int int11 = 1; int11 <= int8; int11++) {
                    double1 += doubles1[int10 + int1 - int11] * doubles0[int2 + int1 * (int1 - int11)];
                }

                doubles0[int2 + int1 * int9] = (doubles0[int2 + int1 * int9] - double1) / doubles1[int10 + int9];
            }
        }
    }

    static int computeSVD(GMatrix gMatrix1, GMatrix gMatrix5, GMatrix var2, GMatrix gMatrix6) {
        GMatrix gMatrix0 = new GMatrix(gMatrix1.nRow, gMatrix1.nCol);
        GMatrix gMatrix2 = new GMatrix(gMatrix1.nRow, gMatrix1.nCol);
        GMatrix gMatrix3 = new GMatrix(gMatrix1.nRow, gMatrix1.nCol);
        GMatrix gMatrix4 = new GMatrix(gMatrix1);
        int int0;
        int int1;
        if (gMatrix4.nRow >= gMatrix4.nCol) {
            int1 = gMatrix4.nCol;
            int0 = gMatrix4.nCol - 1;
        } else {
            int1 = gMatrix4.nRow;
            int0 = gMatrix4.nRow;
        }

        int int2;
        if (gMatrix4.nRow > gMatrix4.nCol) {
            int2 = gMatrix4.nRow;
        } else {
            int2 = gMatrix4.nCol;
        }

        double[] doubles0 = new double[int2];
        double[] doubles1 = new double[int1];
        double[] doubles2 = new double[int0];
        boolean boolean0 = false;
        gMatrix5.setIdentity();
        gMatrix6.setIdentity();
        int int3 = gMatrix4.nRow;
        int int4 = gMatrix4.nCol;

        for (int int5 = 0; int5 < int1; int5++) {
            if (int3 > 1) {
                double double0 = 0.0;

                for (int int6 = 0; int6 < int3; int6++) {
                    double0 += gMatrix4.values[int6 + int5][int5] * gMatrix4.values[int6 + int5][int5];
                }

                double0 = Math.sqrt(double0);
                if (gMatrix4.values[int5][int5] == 0.0) {
                    doubles0[0] = double0;
                } else {
                    doubles0[0] = gMatrix4.values[int5][int5] + d_sign(double0, gMatrix4.values[int5][int5]);
                }

                for (int int7 = 1; int7 < int3; int7++) {
                    doubles0[int7] = gMatrix4.values[int5 + int7][int5];
                }

                double double1 = 0.0;

                for (int int8 = 0; int8 < int3; int8++) {
                    double1 += doubles0[int8] * doubles0[int8];
                }

                double1 = 2.0 / double1;

                for (int int9 = int5; int9 < gMatrix4.nRow; int9++) {
                    for (int int10 = int5; int10 < gMatrix4.nRow; int10++) {
                        gMatrix2.values[int9][int10] = -double1 * doubles0[int9 - int5] * doubles0[int10 - int5];
                    }
                }

                for (int int11 = int5; int11 < gMatrix4.nRow; int11++) {
                    gMatrix2.values[int11][int11]++;
                }

                double double2 = 0.0;

                for (int int12 = int5; int12 < gMatrix4.nRow; int12++) {
                    double2 += gMatrix2.values[int5][int12] * gMatrix4.values[int12][int5];
                }

                gMatrix4.values[int5][int5] = double2;

                for (int int13 = int5; int13 < gMatrix4.nRow; int13++) {
                    for (int int14 = int5 + 1; int14 < gMatrix4.nCol; int14++) {
                        gMatrix0.values[int13][int14] = 0.0;

                        for (int int15 = int5; int15 < gMatrix4.nCol; int15++) {
                            gMatrix0.values[int13][int14] = gMatrix0.values[int13][int14] + gMatrix2.values[int13][int15] * gMatrix4.values[int15][int14];
                        }
                    }
                }

                for (int int16 = int5; int16 < gMatrix4.nRow; int16++) {
                    for (int int17 = int5 + 1; int17 < gMatrix4.nCol; int17++) {
                        gMatrix4.values[int16][int17] = gMatrix0.values[int16][int17];
                    }
                }

                for (int int18 = int5; int18 < gMatrix4.nRow; int18++) {
                    for (int int19 = 0; int19 < gMatrix4.nCol; int19++) {
                        gMatrix0.values[int18][int19] = 0.0;

                        for (int int20 = int5; int20 < gMatrix4.nCol; int20++) {
                            gMatrix0.values[int18][int19] = gMatrix0.values[int18][int19] + gMatrix2.values[int18][int20] * gMatrix5.values[int20][int19];
                        }
                    }
                }

                for (int int21 = int5; int21 < gMatrix4.nRow; int21++) {
                    for (int int22 = 0; int22 < gMatrix4.nCol; int22++) {
                        gMatrix5.values[int21][int22] = gMatrix0.values[int21][int22];
                    }
                }

                int3--;
            }

            if (int4 > 2) {
                double double3 = 0.0;

                for (int int23 = 1; int23 < int4; int23++) {
                    double3 += gMatrix4.values[int5][int5 + int23] * gMatrix4.values[int5][int5 + int23];
                }

                double3 = Math.sqrt(double3);
                if (gMatrix4.values[int5][int5 + 1] == 0.0) {
                    doubles0[0] = double3;
                } else {
                    doubles0[0] = gMatrix4.values[int5][int5 + 1] + d_sign(double3, gMatrix4.values[int5][int5 + 1]);
                }

                for (int int24 = 1; int24 < int4 - 1; int24++) {
                    doubles0[int24] = gMatrix4.values[int5][int5 + int24 + 1];
                }

                double double4 = 0.0;

                for (int int25 = 0; int25 < int4 - 1; int25++) {
                    double4 += doubles0[int25] * doubles0[int25];
                }

                double4 = 2.0 / double4;

                for (int int26 = int5 + 1; int26 < int4; int26++) {
                    for (int int27 = int5 + 1; int27 < gMatrix4.nCol; int27++) {
                        gMatrix3.values[int26][int27] = -double4 * doubles0[int26 - int5 - 1] * doubles0[int27 - int5 - 1];
                    }
                }

                for (int int28 = int5 + 1; int28 < gMatrix4.nCol; int28++) {
                    gMatrix3.values[int28][int28]++;
                }

                double double5 = 0.0;

                for (int int29 = int5; int29 < gMatrix4.nCol; int29++) {
                    double5 += gMatrix3.values[int29][int5 + 1] * gMatrix4.values[int5][int29];
                }

                gMatrix4.values[int5][int5 + 1] = double5;

                for (int int30 = int5 + 1; int30 < gMatrix4.nRow; int30++) {
                    for (int int31 = int5 + 1; int31 < gMatrix4.nCol; int31++) {
                        gMatrix0.values[int30][int31] = 0.0;

                        for (int int32 = int5 + 1; int32 < gMatrix4.nCol; int32++) {
                            gMatrix0.values[int30][int31] = gMatrix0.values[int30][int31] + gMatrix3.values[int32][int31] * gMatrix4.values[int30][int32];
                        }
                    }
                }

                for (int int33 = int5 + 1; int33 < gMatrix4.nRow; int33++) {
                    for (int int34 = int5 + 1; int34 < gMatrix4.nCol; int34++) {
                        gMatrix4.values[int33][int34] = gMatrix0.values[int33][int34];
                    }
                }

                for (int int35 = 0; int35 < gMatrix4.nRow; int35++) {
                    for (int int36 = int5 + 1; int36 < gMatrix4.nCol; int36++) {
                        gMatrix0.values[int35][int36] = 0.0;

                        for (int int37 = int5 + 1; int37 < gMatrix4.nCol; int37++) {
                            gMatrix0.values[int35][int36] = gMatrix0.values[int35][int36] + gMatrix3.values[int37][int36] * gMatrix6.values[int35][int37];
                        }
                    }
                }

                for (int int38 = 0; int38 < gMatrix4.nRow; int38++) {
                    for (int int39 = int5 + 1; int39 < gMatrix4.nCol; int39++) {
                        gMatrix6.values[int38][int39] = gMatrix0.values[int38][int39];
                    }
                }

                int4--;
            }
        }

        for (int int40 = 0; int40 < int1; int40++) {
            doubles1[int40] = gMatrix4.values[int40][int40];
        }

        for (int int41 = 0; int41 < int0; int41++) {
            doubles2[int41] = gMatrix4.values[int41][int41 + 1];
        }

        if (gMatrix4.nRow == 2 && gMatrix4.nCol == 2) {
            double[] doubles3 = new double[1];
            double[] doubles4 = new double[1];
            double[] doubles5 = new double[1];
            double[] doubles6 = new double[1];
            compute_2X2(doubles1[0], doubles2[0], doubles1[1], doubles1, doubles5, doubles3, doubles6, doubles4, 0);
            update_u(0, gMatrix5, doubles3, doubles5);
            update_v(0, gMatrix6, doubles4, doubles6);
            return 2;
        } else {
            compute_qr(0, doubles2.length - 1, doubles1, doubles2, gMatrix5, gMatrix6);
            return doubles1.length;
        }
    }

    static void compute_qr(int int2, int int3, double[] doubles4, double[] doubles5, GMatrix gMatrix1, GMatrix gMatrix0) {
        double[] doubles0 = new double[1];
        double[] doubles1 = new double[1];
        double[] doubles2 = new double[1];
        double[] doubles3 = new double[1];
        new GMatrix(gMatrix1.nCol, gMatrix0.nRow);
        double double0 = 1.0;
        double double1 = -1.0;
        boolean boolean0 = false;
        double double2 = 0.0;
        double double3 = 0.0;

        for (int int0 = 0; int0 < 2 && !boolean0; int0++) {
            int int1;
            for (int1 = int2; int1 <= int3; int1++) {
                if (int1 == int2) {
                    int int4;
                    if (doubles5.length == doubles4.length) {
                        int4 = int3;
                    } else {
                        int4 = int3 + 1;
                    }

                    double double4 = compute_shift(doubles4[int4 - 1], doubles5[int3], doubles4[int4]);
                    double2 = (Math.abs(doubles4[int1]) - double4) * (d_sign(double0, doubles4[int1]) + double4 / doubles4[int1]);
                    double3 = doubles5[int1];
                }

                double double5 = compute_rot(double2, double3, doubles3, doubles1);
                if (int1 != int2) {
                    doubles5[int1 - 1] = double5;
                }

                double2 = doubles1[0] * doubles4[int1] + doubles3[0] * doubles5[int1];
                doubles5[int1] = doubles1[0] * doubles5[int1] - doubles3[0] * doubles4[int1];
                double3 = doubles3[0] * doubles4[int1 + 1];
                doubles4[int1 + 1] = doubles1[0] * doubles4[int1 + 1];
                update_v(int1, gMatrix0, doubles1, doubles3);
                double5 = compute_rot(double2, double3, doubles2, doubles0);
                doubles4[int1] = double5;
                double2 = doubles0[0] * doubles5[int1] + doubles2[0] * doubles4[int1 + 1];
                doubles4[int1 + 1] = doubles0[0] * doubles4[int1 + 1] - doubles2[0] * doubles5[int1];
                if (int1 < int3) {
                    double3 = doubles2[0] * doubles5[int1 + 1];
                    doubles5[int1 + 1] = doubles0[0] * doubles5[int1 + 1];
                }

                update_u(int1, gMatrix1, doubles0, doubles2);
            }

            if (doubles4.length == doubles5.length) {
                double double6 = compute_rot(double2, double3, doubles3, doubles1);
                double2 = doubles1[0] * doubles4[int1] + doubles3[0] * doubles5[int1];
                doubles5[int1] = doubles1[0] * doubles5[int1] - doubles3[0] * doubles4[int1];
                doubles4[int1 + 1] = doubles1[0] * doubles4[int1 + 1];
                update_v(int1, gMatrix0, doubles1, doubles3);
            }

            while (int3 - int2 > 1 && Math.abs(doubles5[int3]) < 4.89E-15) {
                int3--;
            }

            for (int int5 = int3 - 2; int5 > int2; int5--) {
                if (Math.abs(doubles5[int5]) < 4.89E-15) {
                    compute_qr(int5 + 1, int3, doubles4, doubles5, gMatrix1, gMatrix0);
                    int3 = int5 - 1;

                    while (int3 - int2 > 1 && Math.abs(doubles5[int3]) < 4.89E-15) {
                        int3--;
                    }
                }
            }

            if (int3 - int2 <= 1 && Math.abs(doubles5[int2 + 1]) < 4.89E-15) {
                boolean0 = true;
            }
        }

        if (Math.abs(doubles5[1]) < 4.89E-15) {
            compute_2X2(doubles4[int2], doubles5[int2], doubles4[int2 + 1], doubles4, doubles2, doubles0, doubles3, doubles1, 0);
            doubles5[int2] = 0.0;
            doubles5[int2 + 1] = 0.0;
        }

        update_u(int2, gMatrix1, doubles0, doubles2);
        update_v(int2, gMatrix0, doubles1, doubles3);
    }

    private static void print_se(double[] doubles0, double[] doubles1) {
        System.out.println("\ns =" + doubles0[0] + " " + doubles0[1] + " " + doubles0[2]);
        System.out.println("e =" + doubles1[0] + " " + doubles1[1]);
    }

    private static void update_v(int int1, GMatrix gMatrix, double[] doubles1, double[] doubles0) {
        for (int int0 = 0; int0 < gMatrix.nRow; int0++) {
            double double0 = gMatrix.values[int0][int1];
            gMatrix.values[int0][int1] = doubles1[0] * double0 + doubles0[0] * gMatrix.values[int0][int1 + 1];
            gMatrix.values[int0][int1 + 1] = -doubles0[0] * double0 + doubles1[0] * gMatrix.values[int0][int1 + 1];
        }
    }

    private static void chase_up(double[] doubles3, double[] doubles2, int int0, GMatrix gMatrix1) {
        double[] doubles0 = new double[1];
        double[] doubles1 = new double[1];
        GMatrix gMatrix0 = new GMatrix(gMatrix1.nRow, gMatrix1.nCol);
        GMatrix gMatrix2 = new GMatrix(gMatrix1.nRow, gMatrix1.nCol);
        double double0 = doubles2[int0];
        double double1 = doubles3[int0];

        int int1;
        for (int1 = int0; int1 > 0; int1--) {
            double double2 = compute_rot(double0, double1, doubles1, doubles0);
            double0 = -doubles2[int1 - 1] * doubles1[0];
            double1 = doubles3[int1 - 1];
            doubles3[int1] = double2;
            doubles2[int1 - 1] = doubles2[int1 - 1] * doubles0[0];
            update_v_split(int1, int0 + 1, gMatrix1, doubles0, doubles1, gMatrix0, gMatrix2);
        }

        doubles3[int1 + 1] = compute_rot(double0, double1, doubles1, doubles0);
        update_v_split(int1, int0 + 1, gMatrix1, doubles0, doubles1, gMatrix0, gMatrix2);
    }

    private static void chase_across(double[] doubles3, double[] doubles2, int int0, GMatrix gMatrix1) {
        double[] doubles0 = new double[1];
        double[] doubles1 = new double[1];
        GMatrix gMatrix0 = new GMatrix(gMatrix1.nRow, gMatrix1.nCol);
        GMatrix gMatrix2 = new GMatrix(gMatrix1.nRow, gMatrix1.nCol);
        double double0 = doubles2[int0];
        double double1 = doubles3[int0 + 1];

        int int1;
        for (int1 = int0; int1 < gMatrix1.nCol - 2; int1++) {
            double double2 = compute_rot(double1, double0, doubles1, doubles0);
            double0 = -doubles2[int1 + 1] * doubles1[0];
            double1 = doubles3[int1 + 2];
            doubles3[int1 + 1] = double2;
            doubles2[int1 + 1] = doubles2[int1 + 1] * doubles0[0];
            update_u_split(int0, int1 + 1, gMatrix1, doubles0, doubles1, gMatrix0, gMatrix2);
        }

        doubles3[int1 + 1] = compute_rot(double1, double0, doubles1, doubles0);
        update_u_split(int0, int1 + 1, gMatrix1, doubles0, doubles1, gMatrix0, gMatrix2);
    }

    private static void update_v_split(int int1, int int2, GMatrix gMatrix0, double[] doubles1, double[] doubles0, GMatrix gMatrix2, GMatrix gMatrix1) {
        for (int int0 = 0; int0 < gMatrix0.nRow; int0++) {
            double double0 = gMatrix0.values[int0][int1];
            gMatrix0.values[int0][int1] = doubles1[0] * double0 - doubles0[0] * gMatrix0.values[int0][int2];
            gMatrix0.values[int0][int2] = doubles0[0] * double0 + doubles1[0] * gMatrix0.values[int0][int2];
        }

        System.out.println("topr    =" + int1);
        System.out.println("bottomr =" + int2);
        System.out.println("cosr =" + doubles1[0]);
        System.out.println("sinr =" + doubles0[0]);
        System.out.println("\nm =");
        checkMatrix(gMatrix1);
        System.out.println("\nv =");
        checkMatrix(gMatrix2);
        gMatrix1.mul(gMatrix1, gMatrix2);
        System.out.println("\nt*m =");
        checkMatrix(gMatrix1);
    }

    private static void update_u_split(int int1, int int2, GMatrix gMatrix0, double[] doubles1, double[] doubles0, GMatrix gMatrix2, GMatrix gMatrix1) {
        for (int int0 = 0; int0 < gMatrix0.nCol; int0++) {
            double double0 = gMatrix0.values[int1][int0];
            gMatrix0.values[int1][int0] = doubles1[0] * double0 - doubles0[0] * gMatrix0.values[int2][int0];
            gMatrix0.values[int2][int0] = doubles0[0] * double0 + doubles1[0] * gMatrix0.values[int2][int0];
        }

        System.out.println("\nm=");
        checkMatrix(gMatrix1);
        System.out.println("\nu=");
        checkMatrix(gMatrix2);
        gMatrix1.mul(gMatrix2, gMatrix1);
        System.out.println("\nt*m=");
        checkMatrix(gMatrix1);
    }

    private static void update_u(int int1, GMatrix gMatrix, double[] doubles1, double[] doubles0) {
        for (int int0 = 0; int0 < gMatrix.nCol; int0++) {
            double double0 = gMatrix.values[int1][int0];
            gMatrix.values[int1][int0] = doubles1[0] * double0 + doubles0[0] * gMatrix.values[int1 + 1][int0];
            gMatrix.values[int1 + 1][int0] = -doubles0[0] * double0 + doubles1[0] * gMatrix.values[int1 + 1][int0];
        }
    }

    private static void print_m(GMatrix gMatrix1, GMatrix gMatrix2, GMatrix gMatrix3) {
        GMatrix gMatrix0 = new GMatrix(gMatrix1.nCol, gMatrix1.nRow);
        gMatrix0.mul(gMatrix2, gMatrix0);
        gMatrix0.mul(gMatrix0, gMatrix3);
        System.out.println("\n m = \n" + toString(gMatrix0));
    }

    private static String toString(GMatrix gMatrix) {
        StringBuffer stringBuffer = new StringBuffer(gMatrix.nRow * gMatrix.nCol * 8);

        for (int int0 = 0; int0 < gMatrix.nRow; int0++) {
            for (int int1 = 0; int1 < gMatrix.nCol; int1++) {
                if (Math.abs(gMatrix.values[int0][int1]) < 1.0E-9) {
                    stringBuffer.append("0.0000 ");
                } else {
                    stringBuffer.append(gMatrix.values[int0][int1]).append(" ");
                }
            }

            stringBuffer.append("\n");
        }

        return stringBuffer.toString();
    }

    private static void print_svd(double[] doubles0, double[] doubles1, GMatrix gMatrix2, GMatrix gMatrix1) {
        GMatrix gMatrix0 = new GMatrix(gMatrix2.nCol, gMatrix1.nRow);
        System.out.println(" \ns = ");

        for (int int0 = 0; int0 < doubles0.length; int0++) {
            System.out.println(" " + doubles0[int0]);
        }

        System.out.println(" \ne = ");

        for (int int1 = 0; int1 < doubles1.length; int1++) {
            System.out.println(" " + doubles1[int1]);
        }

        System.out.println(" \nu  = \n" + gMatrix2.toString());
        System.out.println(" \nv  = \n" + gMatrix1.toString());
        gMatrix0.setIdentity();

        for (int int2 = 0; int2 < doubles0.length; int2++) {
            gMatrix0.values[int2][int2] = doubles0[int2];
        }

        for (int int3 = 0; int3 < doubles1.length; int3++) {
            gMatrix0.values[int3][int3 + 1] = doubles1[int3];
        }

        System.out.println(" \nm  = \n" + gMatrix0.toString());
        gMatrix0.mulTransposeLeft(gMatrix2, gMatrix0);
        gMatrix0.mulTransposeRight(gMatrix0, gMatrix1);
        System.out.println(" \n u.transpose*m*v.transpose  = \n" + gMatrix0.toString());
    }

    static double max(double double0, double double1) {
        return double0 > double1 ? double0 : double1;
    }

    static double min(double double0, double double1) {
        return double0 < double1 ? double0 : double1;
    }

    static double compute_shift(double double1, double double3, double double5) {
        double double0 = Math.abs(double1);
        double double2 = Math.abs(double3);
        double double4 = Math.abs(double5);
        double double6 = min(double0, double4);
        double double7 = max(double0, double4);
        double double8;
        if (double6 == 0.0) {
            double8 = 0.0;
            if (double7 != 0.0) {
                double double9 = min(double7, double2) / max(double7, double2);
            }
        } else if (double2 < double7) {
            double double10 = double6 / double7 + 1.0;
            double double11 = (double7 - double6) / double7;
            double double12 = double2 / double7;
            double double13 = double12 * double12;
            double double14 = 2.0 / (Math.sqrt(double10 * double10 + double13) + Math.sqrt(double11 * double11 + double13));
            double8 = double6 * double14;
        } else {
            double double15 = double7 / double2;
            if (double15 == 0.0) {
                double8 = double6 * double7 / double2;
            } else {
                double double16 = double6 / double7 + 1.0;
                double double17 = (double7 - double6) / double7;
                double double18 = double16 * double15;
                double double19 = double17 * double15;
                double double20 = 1.0 / (Math.sqrt(double18 * double18 + 1.0) + Math.sqrt(double19 * double19 + 1.0));
                double8 = double6 * double20 * double15;
                double8 += double8;
            }
        }

        return double8;
    }

    static int compute_2X2(
        double double10,
        double double17,
        double double13,
        double[] doubles0,
        double[] doubles2,
        double[] doubles1,
        double[] doubles4,
        double[] doubles3,
        int int0
    ) {
        double double0 = 2.0;
        double double1 = 1.0;
        double double2 = doubles0[0];
        double double3 = doubles0[1];
        double double4 = 0.0;
        double double5 = 0.0;
        double double6 = 0.0;
        double double7 = 0.0;
        double double8 = 0.0;
        double double9 = double10;
        double double11 = Math.abs(double10);
        double double12 = double13;
        double double14 = Math.abs(double13);
        byte byte0 = 1;
        boolean boolean0;
        if (double14 > double11) {
            boolean0 = true;
        } else {
            boolean0 = false;
        }

        if (boolean0) {
            byte0 = 3;
            double9 = double13;
            double12 = double10;
            double double15 = double11;
            double11 = double14;
            double14 = double15;
        }

        double double16 = Math.abs(double17);
        if (double16 == 0.0) {
            doubles0[1] = double14;
            doubles0[0] = double11;
            double4 = 1.0;
            double5 = 1.0;
            double6 = 0.0;
            double7 = 0.0;
        } else {
            boolean boolean1 = true;
            if (double16 > double11) {
                byte0 = 2;
                if (double11 / double16 < 1.0E-10) {
                    boolean1 = false;
                    double2 = double16;
                    if (double14 > 1.0) {
                        double3 = double11 / (double16 / double14);
                    } else {
                        double3 = double11 / double16 * double14;
                    }

                    double4 = 1.0;
                    double6 = double12 / double17;
                    double7 = 1.0;
                    double5 = double9 / double17;
                }
            }

            if (boolean1) {
                double double18 = double11 - double14;
                double double19;
                if (double18 == double11) {
                    double19 = 1.0;
                } else {
                    double19 = double18 / double11;
                }

                double double20 = double17 / double9;
                double double21 = 2.0 - double19;
                double double22 = double20 * double20;
                double double23 = double21 * double21;
                double double24 = Math.sqrt(double23 + double22);
                double double25;
                if (double19 == 0.0) {
                    double25 = Math.abs(double20);
                } else {
                    double25 = Math.sqrt(double19 * double19 + double22);
                }

                double double26 = (double24 + double25) * 0.5;
                if (double16 > double11) {
                    byte0 = 2;
                    if (double11 / double16 < 1.0E-10) {
                        boolean1 = false;
                        double2 = double16;
                        if (double14 > 1.0) {
                            double3 = double11 / (double16 / double14);
                        } else {
                            double3 = double11 / double16 * double14;
                        }

                        double4 = 1.0;
                        double6 = double12 / double17;
                        double7 = 1.0;
                        double5 = double9 / double17;
                    }
                }

                if (boolean1) {
                    double18 = double11 - double14;
                    if (double18 == double11) {
                        double19 = 1.0;
                    } else {
                        double19 = double18 / double11;
                    }

                    double20 = double17 / double9;
                    double21 = 2.0 - double19;
                    double22 = double20 * double20;
                    double23 = double21 * double21;
                    double24 = Math.sqrt(double23 + double22);
                    if (double19 == 0.0) {
                        double25 = Math.abs(double20);
                    } else {
                        double25 = Math.sqrt(double19 * double19 + double22);
                    }

                    double26 = (double24 + double25) * 0.5;
                    double3 = double14 / double26;
                    double2 = double11 * double26;
                    if (double22 == 0.0) {
                        if (double19 == 0.0) {
                            double21 = d_sign(double0, double9) * d_sign(double1, double17);
                        } else {
                            double21 = double17 / d_sign(double18, double9) + double20 / double21;
                        }
                    } else {
                        double21 = (double20 / (double24 + double21) + double20 / (double25 + double19)) * (double26 + 1.0);
                    }

                    double19 = Math.sqrt(double21 * double21 + 4.0);
                    double5 = 2.0 / double19;
                    double7 = double21 / double19;
                    double4 = (double5 + double7 * double20) / double26;
                    double6 = double12 / double9 * double7 / double26;
                }
            }

            if (boolean0) {
                doubles1[0] = double7;
                doubles2[0] = double5;
                doubles3[0] = double6;
                doubles4[0] = double4;
            } else {
                doubles1[0] = double4;
                doubles2[0] = double6;
                doubles3[0] = double5;
                doubles4[0] = double7;
            }

            if (byte0 == 1) {
                double8 = d_sign(double1, doubles3[0]) * d_sign(double1, doubles1[0]) * d_sign(double1, double10);
            }

            if (byte0 == 2) {
                double8 = d_sign(double1, doubles4[0]) * d_sign(double1, doubles1[0]) * d_sign(double1, double17);
            }

            if (byte0 == 3) {
                double8 = d_sign(double1, doubles4[0]) * d_sign(double1, doubles2[0]) * d_sign(double1, double13);
            }

            doubles0[int0] = d_sign(double2, double8);
            double double27 = double8 * d_sign(double1, double10) * d_sign(double1, double13);
            doubles0[int0 + 1] = d_sign(double3, double27);
        }

        return 0;
    }

    static double compute_rot(double double4, double double3, double[] doubles0, double[] doubles1) {
        double double0;
        double double1;
        double double2;
        if (double3 == 0.0) {
            double0 = 1.0;
            double1 = 0.0;
            double2 = double4;
        } else if (double4 == 0.0) {
            double0 = 0.0;
            double1 = 1.0;
            double2 = double3;
        } else {
            double double5 = double4;
            double double6 = double3;
            double double7 = max(Math.abs(double4), Math.abs(double3));
            if (double7 >= 4.994797680505588E145) {
                int int0;
                for (int0 = 0; double7 >= 4.994797680505588E145; double7 = max(Math.abs(double5), Math.abs(double6))) {
                    int0++;
                    double5 *= 2.002083095183101E-146;
                    double6 *= 2.002083095183101E-146;
                }

                double2 = Math.sqrt(double5 * double5 + double6 * double6);
                double0 = double5 / double2;
                double1 = double6 / double2;

                for (int int1 = 1; int1 <= int0; int1++) {
                    double2 *= 4.994797680505588E145;
                }
            } else if (!(double7 <= 2.002083095183101E-146)) {
                double2 = Math.sqrt(double4 * double4 + double3 * double3);
                double0 = double4 / double2;
                double1 = double3 / double2;
            } else {
                int int2;
                for (int2 = 0; double7 <= 2.002083095183101E-146; double7 = max(Math.abs(double5), Math.abs(double6))) {
                    int2++;
                    double5 *= 4.994797680505588E145;
                    double6 *= 4.994797680505588E145;
                }

                double2 = Math.sqrt(double5 * double5 + double6 * double6);
                double0 = double5 / double2;
                double1 = double6 / double2;

                for (int int3 = 1; int3 <= int2; int3++) {
                    double2 *= 2.002083095183101E-146;
                }
            }

            if (Math.abs(double4) > Math.abs(double3) && double0 < 0.0) {
                double0 = -double0;
                double1 = -double1;
                double2 = -double2;
            }
        }

        doubles0[0] = double1;
        doubles1[0] = double0;
        return double2;
    }

    static double d_sign(double double1, double double2) {
        double double0 = double1 >= 0.0 ? double1 : -double1;
        return double2 >= 0.0 ? double0 : -double0;
    }

    @Override
    public Object clone() {
        GMatrix gMatrix0 = null;

        try {
            gMatrix0 = (GMatrix)super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }

        gMatrix0.values = new double[this.nRow][this.nCol];

        for (int int0 = 0; int0 < this.nRow; int0++) {
            for (int int1 = 0; int1 < this.nCol; int1++) {
                gMatrix0.values[int0][int1] = this.values[int0][int1];
            }
        }

        return gMatrix0;
    }
}
