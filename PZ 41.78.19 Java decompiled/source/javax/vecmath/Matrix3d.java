// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Matrix3d implements Serializable, Cloneable {
    static final long serialVersionUID = 6837536777072402710L;
    public double m00;
    public double m01;
    public double m02;
    public double m10;
    public double m11;
    public double m12;
    public double m20;
    public double m21;
    public double m22;
    private static final double EPS = 1.110223024E-16;
    private static final double ERR_EPS = 1.0E-8;
    private static double xin;
    private static double yin;
    private static double zin;
    private static double xout;
    private static double yout;
    private static double zout;

    public Matrix3d(
        double double0, double double1, double double2, double double3, double double4, double double5, double double6, double double7, double double8
    ) {
        this.m00 = double0;
        this.m01 = double1;
        this.m02 = double2;
        this.m10 = double3;
        this.m11 = double4;
        this.m12 = double5;
        this.m20 = double6;
        this.m21 = double7;
        this.m22 = double8;
    }

    public Matrix3d(double[] doubles) {
        this.m00 = doubles[0];
        this.m01 = doubles[1];
        this.m02 = doubles[2];
        this.m10 = doubles[3];
        this.m11 = doubles[4];
        this.m12 = doubles[5];
        this.m20 = doubles[6];
        this.m21 = doubles[7];
        this.m22 = doubles[8];
    }

    public Matrix3d(Matrix3d matrix3d1) {
        this.m00 = matrix3d1.m00;
        this.m01 = matrix3d1.m01;
        this.m02 = matrix3d1.m02;
        this.m10 = matrix3d1.m10;
        this.m11 = matrix3d1.m11;
        this.m12 = matrix3d1.m12;
        this.m20 = matrix3d1.m20;
        this.m21 = matrix3d1.m21;
        this.m22 = matrix3d1.m22;
    }

    public Matrix3d(Matrix3f matrix3f) {
        this.m00 = matrix3f.m00;
        this.m01 = matrix3f.m01;
        this.m02 = matrix3f.m02;
        this.m10 = matrix3f.m10;
        this.m11 = matrix3f.m11;
        this.m12 = matrix3f.m12;
        this.m20 = matrix3f.m20;
        this.m21 = matrix3f.m21;
        this.m22 = matrix3f.m22;
    }

    public Matrix3d() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
    }

    @Override
    public String toString() {
        return this.m00
            + ", "
            + this.m01
            + ", "
            + this.m02
            + "\n"
            + this.m10
            + ", "
            + this.m11
            + ", "
            + this.m12
            + "\n"
            + this.m20
            + ", "
            + this.m21
            + ", "
            + this.m22
            + "\n";
    }

    public final void setIdentity() {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public final void setScale(double double0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = doubles0[0] * double0;
        this.m01 = doubles0[1] * double0;
        this.m02 = doubles0[2] * double0;
        this.m10 = doubles0[3] * double0;
        this.m11 = doubles0[4] * double0;
        this.m12 = doubles0[5] * double0;
        this.m20 = doubles0[6] * double0;
        this.m21 = doubles0[7] * double0;
        this.m22 = doubles0[8] * double0;
    }

    public final void setElement(int int0, int int1, double double0) {
        switch (int0) {
            case 0:
                switch (int1) {
                    case 0:
                        this.m00 = double0;
                        return;
                    case 1:
                        this.m01 = double0;
                        return;
                    case 2:
                        this.m02 = double0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
                }
            case 1:
                switch (int1) {
                    case 0:
                        this.m10 = double0;
                        return;
                    case 1:
                        this.m11 = double0;
                        return;
                    case 2:
                        this.m12 = double0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
                }
            case 2:
                switch (int1) {
                    case 0:
                        this.m20 = double0;
                        return;
                    case 1:
                        this.m21 = double0;
                        return;
                    case 2:
                        this.m22 = double0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
                }
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
        }
    }

    public final double getElement(int int0, int int1) {
        switch (int0) {
            case 0:
                switch (int1) {
                    case 0:
                        return this.m00;
                    case 1:
                        return this.m01;
                    case 2:
                        return this.m02;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
                }
            case 1:
                switch (int1) {
                    case 0:
                        return this.m10;
                    case 1:
                        return this.m11;
                    case 2:
                        return this.m12;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
                }
            case 2:
                switch (int1) {
                    case 0:
                        return this.m20;
                    case 1:
                        return this.m21;
                    case 2:
                        return this.m22;
                }
        }

        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
    }

    public final void getRow(int int0, Vector3d vector3d) {
        if (int0 == 0) {
            vector3d.x = this.m00;
            vector3d.y = this.m01;
            vector3d.z = this.m02;
        } else if (int0 == 1) {
            vector3d.x = this.m10;
            vector3d.y = this.m11;
            vector3d.z = this.m12;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
            }

            vector3d.x = this.m20;
            vector3d.y = this.m21;
            vector3d.z = this.m22;
        }
    }

    public final void getRow(int int0, double[] doubles) {
        if (int0 == 0) {
            doubles[0] = this.m00;
            doubles[1] = this.m01;
            doubles[2] = this.m02;
        } else if (int0 == 1) {
            doubles[0] = this.m10;
            doubles[1] = this.m11;
            doubles[2] = this.m12;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
            }

            doubles[0] = this.m20;
            doubles[1] = this.m21;
            doubles[2] = this.m22;
        }
    }

    public final void getColumn(int int0, Vector3d vector3d) {
        if (int0 == 0) {
            vector3d.x = this.m00;
            vector3d.y = this.m10;
            vector3d.z = this.m20;
        } else if (int0 == 1) {
            vector3d.x = this.m01;
            vector3d.y = this.m11;
            vector3d.z = this.m21;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
            }

            vector3d.x = this.m02;
            vector3d.y = this.m12;
            vector3d.z = this.m22;
        }
    }

    public final void getColumn(int int0, double[] doubles) {
        if (int0 == 0) {
            doubles[0] = this.m00;
            doubles[1] = this.m10;
            doubles[2] = this.m20;
        } else if (int0 == 1) {
            doubles[0] = this.m01;
            doubles[1] = this.m11;
            doubles[2] = this.m21;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
            }

            doubles[0] = this.m02;
            doubles[1] = this.m12;
            doubles[2] = this.m22;
        }
    }

    public final void setRow(int int0, double double0, double double1, double double2) {
        switch (int0) {
            case 0:
                this.m00 = double0;
                this.m01 = double1;
                this.m02 = double2;
                break;
            case 1:
                this.m10 = double0;
                this.m11 = double1;
                this.m12 = double2;
                break;
            case 2:
                this.m20 = double0;
                this.m21 = double1;
                this.m22 = double2;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
        }
    }

    public final void setRow(int int0, Vector3d vector3d) {
        switch (int0) {
            case 0:
                this.m00 = vector3d.x;
                this.m01 = vector3d.y;
                this.m02 = vector3d.z;
                break;
            case 1:
                this.m10 = vector3d.x;
                this.m11 = vector3d.y;
                this.m12 = vector3d.z;
                break;
            case 2:
                this.m20 = vector3d.x;
                this.m21 = vector3d.y;
                this.m22 = vector3d.z;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
        }
    }

    public final void setRow(int int0, double[] doubles) {
        switch (int0) {
            case 0:
                this.m00 = doubles[0];
                this.m01 = doubles[1];
                this.m02 = doubles[2];
                break;
            case 1:
                this.m10 = doubles[0];
                this.m11 = doubles[1];
                this.m12 = doubles[2];
                break;
            case 2:
                this.m20 = doubles[0];
                this.m21 = doubles[1];
                this.m22 = doubles[2];
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
        }
    }

    public final void setColumn(int int0, double double0, double double1, double double2) {
        switch (int0) {
            case 0:
                this.m00 = double0;
                this.m10 = double1;
                this.m20 = double2;
                break;
            case 1:
                this.m01 = double0;
                this.m11 = double1;
                this.m21 = double2;
                break;
            case 2:
                this.m02 = double0;
                this.m12 = double1;
                this.m22 = double2;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
        }
    }

    public final void setColumn(int int0, Vector3d vector3d) {
        switch (int0) {
            case 0:
                this.m00 = vector3d.x;
                this.m10 = vector3d.y;
                this.m20 = vector3d.z;
                break;
            case 1:
                this.m01 = vector3d.x;
                this.m11 = vector3d.y;
                this.m21 = vector3d.z;
                break;
            case 2:
                this.m02 = vector3d.x;
                this.m12 = vector3d.y;
                this.m22 = vector3d.z;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
        }
    }

    public final void setColumn(int int0, double[] doubles) {
        switch (int0) {
            case 0:
                this.m00 = doubles[0];
                this.m10 = doubles[1];
                this.m20 = doubles[2];
                break;
            case 1:
                this.m01 = doubles[0];
                this.m11 = doubles[1];
                this.m21 = doubles[2];
                break;
            case 2:
                this.m02 = doubles[0];
                this.m12 = doubles[1];
                this.m22 = doubles[2];
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
        }
    }

    public final double getScale() {
        double[] doubles0 = new double[3];
        double[] doubles1 = new double[9];
        this.getScaleRotate(doubles0, doubles1);
        return max3(doubles0);
    }

    public final void add(double double0) {
        this.m00 += double0;
        this.m01 += double0;
        this.m02 += double0;
        this.m10 += double0;
        this.m11 += double0;
        this.m12 += double0;
        this.m20 += double0;
        this.m21 += double0;
        this.m22 += double0;
    }

    public final void add(double double0, Matrix3d matrix3d0) {
        this.m00 = matrix3d0.m00 + double0;
        this.m01 = matrix3d0.m01 + double0;
        this.m02 = matrix3d0.m02 + double0;
        this.m10 = matrix3d0.m10 + double0;
        this.m11 = matrix3d0.m11 + double0;
        this.m12 = matrix3d0.m12 + double0;
        this.m20 = matrix3d0.m20 + double0;
        this.m21 = matrix3d0.m21 + double0;
        this.m22 = matrix3d0.m22 + double0;
    }

    public final void add(Matrix3d matrix3d1, Matrix3d matrix3d0) {
        this.m00 = matrix3d1.m00 + matrix3d0.m00;
        this.m01 = matrix3d1.m01 + matrix3d0.m01;
        this.m02 = matrix3d1.m02 + matrix3d0.m02;
        this.m10 = matrix3d1.m10 + matrix3d0.m10;
        this.m11 = matrix3d1.m11 + matrix3d0.m11;
        this.m12 = matrix3d1.m12 + matrix3d0.m12;
        this.m20 = matrix3d1.m20 + matrix3d0.m20;
        this.m21 = matrix3d1.m21 + matrix3d0.m21;
        this.m22 = matrix3d1.m22 + matrix3d0.m22;
    }

    public final void add(Matrix3d matrix3d0) {
        this.m00 = this.m00 + matrix3d0.m00;
        this.m01 = this.m01 + matrix3d0.m01;
        this.m02 = this.m02 + matrix3d0.m02;
        this.m10 = this.m10 + matrix3d0.m10;
        this.m11 = this.m11 + matrix3d0.m11;
        this.m12 = this.m12 + matrix3d0.m12;
        this.m20 = this.m20 + matrix3d0.m20;
        this.m21 = this.m21 + matrix3d0.m21;
        this.m22 = this.m22 + matrix3d0.m22;
    }

    public final void sub(Matrix3d matrix3d1, Matrix3d matrix3d0) {
        this.m00 = matrix3d1.m00 - matrix3d0.m00;
        this.m01 = matrix3d1.m01 - matrix3d0.m01;
        this.m02 = matrix3d1.m02 - matrix3d0.m02;
        this.m10 = matrix3d1.m10 - matrix3d0.m10;
        this.m11 = matrix3d1.m11 - matrix3d0.m11;
        this.m12 = matrix3d1.m12 - matrix3d0.m12;
        this.m20 = matrix3d1.m20 - matrix3d0.m20;
        this.m21 = matrix3d1.m21 - matrix3d0.m21;
        this.m22 = matrix3d1.m22 - matrix3d0.m22;
    }

    public final void sub(Matrix3d matrix3d0) {
        this.m00 = this.m00 - matrix3d0.m00;
        this.m01 = this.m01 - matrix3d0.m01;
        this.m02 = this.m02 - matrix3d0.m02;
        this.m10 = this.m10 - matrix3d0.m10;
        this.m11 = this.m11 - matrix3d0.m11;
        this.m12 = this.m12 - matrix3d0.m12;
        this.m20 = this.m20 - matrix3d0.m20;
        this.m21 = this.m21 - matrix3d0.m21;
        this.m22 = this.m22 - matrix3d0.m22;
    }

    public final void transpose() {
        double double0 = this.m10;
        this.m10 = this.m01;
        this.m01 = double0;
        double0 = this.m20;
        this.m20 = this.m02;
        this.m02 = double0;
        double0 = this.m21;
        this.m21 = this.m12;
        this.m12 = double0;
    }

    public final void transpose(Matrix3d matrix3d1) {
        if (this != matrix3d1) {
            this.m00 = matrix3d1.m00;
            this.m01 = matrix3d1.m10;
            this.m02 = matrix3d1.m20;
            this.m10 = matrix3d1.m01;
            this.m11 = matrix3d1.m11;
            this.m12 = matrix3d1.m21;
            this.m20 = matrix3d1.m02;
            this.m21 = matrix3d1.m12;
            this.m22 = matrix3d1.m22;
        } else {
            this.transpose();
        }
    }

    public final void set(Quat4d quat4d) {
        this.m00 = 1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z;
        this.m10 = 2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z);
        this.m20 = 2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y);
        this.m01 = 2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z);
        this.m11 = 1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z;
        this.m21 = 2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x);
        this.m02 = 2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y);
        this.m12 = 2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x);
        this.m22 = 1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y;
    }

    public final void set(AxisAngle4d axisAngle4d) {
        double double0 = Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
        if (double0 < 1.110223024E-16) {
            this.m00 = 1.0;
            this.m01 = 0.0;
            this.m02 = 0.0;
            this.m10 = 0.0;
            this.m11 = 1.0;
            this.m12 = 0.0;
            this.m20 = 0.0;
            this.m21 = 0.0;
            this.m22 = 1.0;
        } else {
            double0 = 1.0 / double0;
            double double1 = axisAngle4d.x * double0;
            double double2 = axisAngle4d.y * double0;
            double double3 = axisAngle4d.z * double0;
            double double4 = Math.sin(axisAngle4d.angle);
            double double5 = Math.cos(axisAngle4d.angle);
            double double6 = 1.0 - double5;
            double double7 = double1 * double3;
            double double8 = double1 * double2;
            double double9 = double2 * double3;
            this.m00 = double6 * double1 * double1 + double5;
            this.m01 = double6 * double8 - double4 * double3;
            this.m02 = double6 * double7 + double4 * double2;
            this.m10 = double6 * double8 + double4 * double3;
            this.m11 = double6 * double2 * double2 + double5;
            this.m12 = double6 * double9 - double4 * double1;
            this.m20 = double6 * double7 - double4 * double2;
            this.m21 = double6 * double9 + double4 * double1;
            this.m22 = double6 * double3 * double3 + double5;
        }
    }

    public final void set(Quat4f quat4f) {
        this.m00 = 1.0 - 2.0 * quat4f.y * quat4f.y - 2.0 * quat4f.z * quat4f.z;
        this.m10 = 2.0 * (quat4f.x * quat4f.y + quat4f.w * quat4f.z);
        this.m20 = 2.0 * (quat4f.x * quat4f.z - quat4f.w * quat4f.y);
        this.m01 = 2.0 * (quat4f.x * quat4f.y - quat4f.w * quat4f.z);
        this.m11 = 1.0 - 2.0 * quat4f.x * quat4f.x - 2.0 * quat4f.z * quat4f.z;
        this.m21 = 2.0 * (quat4f.y * quat4f.z + quat4f.w * quat4f.x);
        this.m02 = 2.0 * (quat4f.x * quat4f.z + quat4f.w * quat4f.y);
        this.m12 = 2.0 * (quat4f.y * quat4f.z - quat4f.w * quat4f.x);
        this.m22 = 1.0 - 2.0 * quat4f.x * quat4f.x - 2.0 * quat4f.y * quat4f.y;
    }

    public final void set(AxisAngle4f axisAngle4f) {
        double double0 = Math.sqrt(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z);
        if (double0 < 1.110223024E-16) {
            this.m00 = 1.0;
            this.m01 = 0.0;
            this.m02 = 0.0;
            this.m10 = 0.0;
            this.m11 = 1.0;
            this.m12 = 0.0;
            this.m20 = 0.0;
            this.m21 = 0.0;
            this.m22 = 1.0;
        } else {
            double0 = 1.0 / double0;
            double double1 = axisAngle4f.x * double0;
            double double2 = axisAngle4f.y * double0;
            double double3 = axisAngle4f.z * double0;
            double double4 = Math.sin(axisAngle4f.angle);
            double double5 = Math.cos(axisAngle4f.angle);
            double double6 = 1.0 - double5;
            double double7 = double1 * double3;
            double double8 = double1 * double2;
            double double9 = double2 * double3;
            this.m00 = double6 * double1 * double1 + double5;
            this.m01 = double6 * double8 - double4 * double3;
            this.m02 = double6 * double7 + double4 * double2;
            this.m10 = double6 * double8 + double4 * double3;
            this.m11 = double6 * double2 * double2 + double5;
            this.m12 = double6 * double9 - double4 * double1;
            this.m20 = double6 * double7 - double4 * double2;
            this.m21 = double6 * double9 + double4 * double1;
            this.m22 = double6 * double3 * double3 + double5;
        }
    }

    public final void set(Matrix3f matrix3f) {
        this.m00 = matrix3f.m00;
        this.m01 = matrix3f.m01;
        this.m02 = matrix3f.m02;
        this.m10 = matrix3f.m10;
        this.m11 = matrix3f.m11;
        this.m12 = matrix3f.m12;
        this.m20 = matrix3f.m20;
        this.m21 = matrix3f.m21;
        this.m22 = matrix3f.m22;
    }

    public final void set(Matrix3d matrix3d0) {
        this.m00 = matrix3d0.m00;
        this.m01 = matrix3d0.m01;
        this.m02 = matrix3d0.m02;
        this.m10 = matrix3d0.m10;
        this.m11 = matrix3d0.m11;
        this.m12 = matrix3d0.m12;
        this.m20 = matrix3d0.m20;
        this.m21 = matrix3d0.m21;
        this.m22 = matrix3d0.m22;
    }

    public final void set(double[] doubles) {
        this.m00 = doubles[0];
        this.m01 = doubles[1];
        this.m02 = doubles[2];
        this.m10 = doubles[3];
        this.m11 = doubles[4];
        this.m12 = doubles[5];
        this.m20 = doubles[6];
        this.m21 = doubles[7];
        this.m22 = doubles[8];
    }

    public final void invert(Matrix3d matrix3d1) {
        this.invertGeneral(matrix3d1);
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    private final void invertGeneral(Matrix3d matrix3d0) {
        double[] doubles0 = new double[9];
        int[] ints = new int[3];
        double[] doubles1 = new double[]{
            matrix3d0.m00, matrix3d0.m01, matrix3d0.m02, matrix3d0.m10, matrix3d0.m11, matrix3d0.m12, matrix3d0.m20, matrix3d0.m21, matrix3d0.m22
        };
        if (!luDecomposition(doubles1, ints)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix3d12"));
        } else {
            for (int int0 = 0; int0 < 9; int0++) {
                doubles0[int0] = 0.0;
            }

            doubles0[0] = 1.0;
            doubles0[4] = 1.0;
            doubles0[8] = 1.0;
            luBacksubstitution(doubles1, ints, doubles0);
            this.m00 = doubles0[0];
            this.m01 = doubles0[1];
            this.m02 = doubles0[2];
            this.m10 = doubles0[3];
            this.m11 = doubles0[4];
            this.m12 = doubles0[5];
            this.m20 = doubles0[6];
            this.m21 = doubles0[7];
            this.m22 = doubles0[8];
        }
    }

    static boolean luDecomposition(double[] doubles1, int[] ints) {
        double[] doubles0 = new double[3];
        int int0 = 0;
        int int1 = 0;
        int int2 = 3;

        while (int2-- != 0) {
            double double0 = 0.0;
            int int3 = 3;

            while (int3-- != 0) {
                double double1 = doubles1[int0++];
                double1 = Math.abs(double1);
                if (double1 > double0) {
                    double0 = double1;
                }
            }

            if (double0 == 0.0) {
                return false;
            }

            doubles0[int1++] = 1.0 / double0;
        }

        byte byte0 = 0;

        for (int int4 = 0; int4 < 3; int4++) {
            for (int int5 = 0; int5 < int4; int5++) {
                int int6 = byte0 + 3 * int5 + int4;
                double double2 = doubles1[int6];
                int int7 = int5;
                int int8 = byte0 + 3 * int5;

                for (int int9 = byte0 + int4; int7-- != 0; int9 += 3) {
                    double2 -= doubles1[int8] * doubles1[int9];
                    int8++;
                }

                doubles1[int6] = double2;
            }

            double double3 = 0.0;
            int1 = -1;

            for (int int10 = int4; int10 < 3; int10++) {
                int int11 = byte0 + 3 * int10 + int4;
                double double4 = doubles1[int11];
                int int12 = int4;
                int int13 = byte0 + 3 * int10;

                for (int int14 = byte0 + int4; int12-- != 0; int14 += 3) {
                    double4 -= doubles1[int13] * doubles1[int14];
                    int13++;
                }

                doubles1[int11] = double4;
                double double5;
                if ((double5 = doubles0[int10] * Math.abs(double4)) >= double3) {
                    double3 = double5;
                    int1 = int10;
                }
            }

            if (int1 < 0) {
                throw new RuntimeException(VecMathI18N.getString("Matrix3d13"));
            }

            if (int4 != int1) {
                int int15 = 3;
                int int16 = byte0 + 3 * int1;
                int int17 = byte0 + 3 * int4;

                while (int15-- != 0) {
                    double double6 = doubles1[int16];
                    doubles1[int16++] = doubles1[int17];
                    doubles1[int17++] = double6;
                }

                doubles0[int1] = doubles0[int4];
            }

            ints[int4] = int1;
            if (doubles1[byte0 + 3 * int4 + int4] == 0.0) {
                return false;
            }

            if (int4 != 2) {
                double double7 = 1.0 / doubles1[byte0 + 3 * int4 + int4];
                int int18 = byte0 + 3 * (int4 + 1) + int4;

                for (int int19 = 2 - int4; int19-- != 0; int18 += 3) {
                    doubles1[int18] *= double7;
                }
            }
        }

        return true;
    }

    static void luBacksubstitution(double[] doubles1, int[] ints, double[] doubles0) {
        byte byte0 = 0;

        for (int int0 = 0; int0 < 3; int0++) {
            int int1 = int0;
            int int2 = -1;

            for (int int3 = 0; int3 < 3; int3++) {
                int int4 = ints[byte0 + int3];
                double double0 = doubles0[int1 + 3 * int4];
                doubles0[int1 + 3 * int4] = doubles0[int1 + 3 * int3];
                if (int2 >= 0) {
                    int int5 = int3 * 3;

                    for (int int6 = int2; int6 <= int3 - 1; int6++) {
                        double0 -= doubles1[int5 + int6] * doubles0[int1 + 3 * int6];
                    }
                } else if (double0 != 0.0) {
                    int2 = int3;
                }

                doubles0[int1 + 3 * int3] = double0;
            }

            int int7 = 6;
            doubles0[int1 + 6] = doubles0[int1 + 6] / doubles1[int7 + 2];
            int7 -= 3;
            doubles0[int1 + 3] = (doubles0[int1 + 3] - doubles1[int7 + 2] * doubles0[int1 + 6]) / doubles1[int7 + 1];
            int7 -= 3;
            doubles0[int1 + 0] = (doubles0[int1 + 0] - doubles1[int7 + 1] * doubles0[int1 + 3] - doubles1[int7 + 2] * doubles0[int1 + 6]) / doubles1[int7 + 0];
        }
    }

    public final double determinant() {
        return this.m00 * (this.m11 * this.m22 - this.m12 * this.m21)
            + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22)
            + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
    }

    public final void set(double double0) {
        this.m00 = double0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = double0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = double0;
    }

    public final void rotX(double double1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cos(double1);
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = double2;
        this.m12 = -double0;
        this.m20 = 0.0;
        this.m21 = double0;
        this.m22 = double2;
    }

    public final void rotY(double double1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cos(double1);
        this.m00 = double2;
        this.m01 = 0.0;
        this.m02 = double0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = -double0;
        this.m21 = 0.0;
        this.m22 = double2;
    }

    public final void rotZ(double double1) {
        double double0 = Math.sin(double1);
        double double2 = Math.cos(double1);
        this.m00 = double2;
        this.m01 = -double0;
        this.m02 = 0.0;
        this.m10 = double0;
        this.m11 = double2;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public final void mul(double double0) {
        this.m00 *= double0;
        this.m01 *= double0;
        this.m02 *= double0;
        this.m10 *= double0;
        this.m11 *= double0;
        this.m12 *= double0;
        this.m20 *= double0;
        this.m21 *= double0;
        this.m22 *= double0;
    }

    public final void mul(double double0, Matrix3d matrix3d0) {
        this.m00 = double0 * matrix3d0.m00;
        this.m01 = double0 * matrix3d0.m01;
        this.m02 = double0 * matrix3d0.m02;
        this.m10 = double0 * matrix3d0.m10;
        this.m11 = double0 * matrix3d0.m11;
        this.m12 = double0 * matrix3d0.m12;
        this.m20 = double0 * matrix3d0.m20;
        this.m21 = double0 * matrix3d0.m21;
        this.m22 = double0 * matrix3d0.m22;
    }

    public final void mul(Matrix3d matrix3d0) {
        double double0 = this.m00 * matrix3d0.m00 + this.m01 * matrix3d0.m10 + this.m02 * matrix3d0.m20;
        double double1 = this.m00 * matrix3d0.m01 + this.m01 * matrix3d0.m11 + this.m02 * matrix3d0.m21;
        double double2 = this.m00 * matrix3d0.m02 + this.m01 * matrix3d0.m12 + this.m02 * matrix3d0.m22;
        double double3 = this.m10 * matrix3d0.m00 + this.m11 * matrix3d0.m10 + this.m12 * matrix3d0.m20;
        double double4 = this.m10 * matrix3d0.m01 + this.m11 * matrix3d0.m11 + this.m12 * matrix3d0.m21;
        double double5 = this.m10 * matrix3d0.m02 + this.m11 * matrix3d0.m12 + this.m12 * matrix3d0.m22;
        double double6 = this.m20 * matrix3d0.m00 + this.m21 * matrix3d0.m10 + this.m22 * matrix3d0.m20;
        double double7 = this.m20 * matrix3d0.m01 + this.m21 * matrix3d0.m11 + this.m22 * matrix3d0.m21;
        double double8 = this.m20 * matrix3d0.m02 + this.m21 * matrix3d0.m12 + this.m22 * matrix3d0.m22;
        this.m00 = double0;
        this.m01 = double1;
        this.m02 = double2;
        this.m10 = double3;
        this.m11 = double4;
        this.m12 = double5;
        this.m20 = double6;
        this.m21 = double7;
        this.m22 = double8;
    }

    public final void mul(Matrix3d matrix3d2, Matrix3d matrix3d1) {
        if (this != matrix3d2 && this != matrix3d1) {
            this.m00 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m01 * matrix3d1.m10 + matrix3d2.m02 * matrix3d1.m20;
            this.m01 = matrix3d2.m00 * matrix3d1.m01 + matrix3d2.m01 * matrix3d1.m11 + matrix3d2.m02 * matrix3d1.m21;
            this.m02 = matrix3d2.m00 * matrix3d1.m02 + matrix3d2.m01 * matrix3d1.m12 + matrix3d2.m02 * matrix3d1.m22;
            this.m10 = matrix3d2.m10 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m10 + matrix3d2.m12 * matrix3d1.m20;
            this.m11 = matrix3d2.m10 * matrix3d1.m01 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m12 * matrix3d1.m21;
            this.m12 = matrix3d2.m10 * matrix3d1.m02 + matrix3d2.m11 * matrix3d1.m12 + matrix3d2.m12 * matrix3d1.m22;
            this.m20 = matrix3d2.m20 * matrix3d1.m00 + matrix3d2.m21 * matrix3d1.m10 + matrix3d2.m22 * matrix3d1.m20;
            this.m21 = matrix3d2.m20 * matrix3d1.m01 + matrix3d2.m21 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m21;
            this.m22 = matrix3d2.m20 * matrix3d1.m02 + matrix3d2.m21 * matrix3d1.m12 + matrix3d2.m22 * matrix3d1.m22;
        } else {
            double double0 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m01 * matrix3d1.m10 + matrix3d2.m02 * matrix3d1.m20;
            double double1 = matrix3d2.m00 * matrix3d1.m01 + matrix3d2.m01 * matrix3d1.m11 + matrix3d2.m02 * matrix3d1.m21;
            double double2 = matrix3d2.m00 * matrix3d1.m02 + matrix3d2.m01 * matrix3d1.m12 + matrix3d2.m02 * matrix3d1.m22;
            double double3 = matrix3d2.m10 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m10 + matrix3d2.m12 * matrix3d1.m20;
            double double4 = matrix3d2.m10 * matrix3d1.m01 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m12 * matrix3d1.m21;
            double double5 = matrix3d2.m10 * matrix3d1.m02 + matrix3d2.m11 * matrix3d1.m12 + matrix3d2.m12 * matrix3d1.m22;
            double double6 = matrix3d2.m20 * matrix3d1.m00 + matrix3d2.m21 * matrix3d1.m10 + matrix3d2.m22 * matrix3d1.m20;
            double double7 = matrix3d2.m20 * matrix3d1.m01 + matrix3d2.m21 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m21;
            double double8 = matrix3d2.m20 * matrix3d1.m02 + matrix3d2.m21 * matrix3d1.m12 + matrix3d2.m22 * matrix3d1.m22;
            this.m00 = double0;
            this.m01 = double1;
            this.m02 = double2;
            this.m10 = double3;
            this.m11 = double4;
            this.m12 = double5;
            this.m20 = double6;
            this.m21 = double7;
            this.m22 = double8;
        }
    }

    public final void mulNormalize(Matrix3d matrix3d0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        double[] doubles2 = new double[3];
        doubles0[0] = this.m00 * matrix3d0.m00 + this.m01 * matrix3d0.m10 + this.m02 * matrix3d0.m20;
        doubles0[1] = this.m00 * matrix3d0.m01 + this.m01 * matrix3d0.m11 + this.m02 * matrix3d0.m21;
        doubles0[2] = this.m00 * matrix3d0.m02 + this.m01 * matrix3d0.m12 + this.m02 * matrix3d0.m22;
        doubles0[3] = this.m10 * matrix3d0.m00 + this.m11 * matrix3d0.m10 + this.m12 * matrix3d0.m20;
        doubles0[4] = this.m10 * matrix3d0.m01 + this.m11 * matrix3d0.m11 + this.m12 * matrix3d0.m21;
        doubles0[5] = this.m10 * matrix3d0.m02 + this.m11 * matrix3d0.m12 + this.m12 * matrix3d0.m22;
        doubles0[6] = this.m20 * matrix3d0.m00 + this.m21 * matrix3d0.m10 + this.m22 * matrix3d0.m20;
        doubles0[7] = this.m20 * matrix3d0.m01 + this.m21 * matrix3d0.m11 + this.m22 * matrix3d0.m21;
        doubles0[8] = this.m20 * matrix3d0.m02 + this.m21 * matrix3d0.m12 + this.m22 * matrix3d0.m22;
        compute_svd(doubles0, doubles2, doubles1);
        this.m00 = doubles1[0];
        this.m01 = doubles1[1];
        this.m02 = doubles1[2];
        this.m10 = doubles1[3];
        this.m11 = doubles1[4];
        this.m12 = doubles1[5];
        this.m20 = doubles1[6];
        this.m21 = doubles1[7];
        this.m22 = doubles1[8];
    }

    public final void mulNormalize(Matrix3d matrix3d1, Matrix3d matrix3d0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        double[] doubles2 = new double[3];
        doubles0[0] = matrix3d1.m00 * matrix3d0.m00 + matrix3d1.m01 * matrix3d0.m10 + matrix3d1.m02 * matrix3d0.m20;
        doubles0[1] = matrix3d1.m00 * matrix3d0.m01 + matrix3d1.m01 * matrix3d0.m11 + matrix3d1.m02 * matrix3d0.m21;
        doubles0[2] = matrix3d1.m00 * matrix3d0.m02 + matrix3d1.m01 * matrix3d0.m12 + matrix3d1.m02 * matrix3d0.m22;
        doubles0[3] = matrix3d1.m10 * matrix3d0.m00 + matrix3d1.m11 * matrix3d0.m10 + matrix3d1.m12 * matrix3d0.m20;
        doubles0[4] = matrix3d1.m10 * matrix3d0.m01 + matrix3d1.m11 * matrix3d0.m11 + matrix3d1.m12 * matrix3d0.m21;
        doubles0[5] = matrix3d1.m10 * matrix3d0.m02 + matrix3d1.m11 * matrix3d0.m12 + matrix3d1.m12 * matrix3d0.m22;
        doubles0[6] = matrix3d1.m20 * matrix3d0.m00 + matrix3d1.m21 * matrix3d0.m10 + matrix3d1.m22 * matrix3d0.m20;
        doubles0[7] = matrix3d1.m20 * matrix3d0.m01 + matrix3d1.m21 * matrix3d0.m11 + matrix3d1.m22 * matrix3d0.m21;
        doubles0[8] = matrix3d1.m20 * matrix3d0.m02 + matrix3d1.m21 * matrix3d0.m12 + matrix3d1.m22 * matrix3d0.m22;
        compute_svd(doubles0, doubles2, doubles1);
        this.m00 = doubles1[0];
        this.m01 = doubles1[1];
        this.m02 = doubles1[2];
        this.m10 = doubles1[3];
        this.m11 = doubles1[4];
        this.m12 = doubles1[5];
        this.m20 = doubles1[6];
        this.m21 = doubles1[7];
        this.m22 = doubles1[8];
    }

    public final void mulTransposeBoth(Matrix3d matrix3d2, Matrix3d matrix3d1) {
        if (this != matrix3d2 && this != matrix3d1) {
            this.m00 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m10 * matrix3d1.m01 + matrix3d2.m20 * matrix3d1.m02;
            this.m01 = matrix3d2.m00 * matrix3d1.m10 + matrix3d2.m10 * matrix3d1.m11 + matrix3d2.m20 * matrix3d1.m12;
            this.m02 = matrix3d2.m00 * matrix3d1.m20 + matrix3d2.m10 * matrix3d1.m21 + matrix3d2.m20 * matrix3d1.m22;
            this.m10 = matrix3d2.m01 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m01 + matrix3d2.m21 * matrix3d1.m02;
            this.m11 = matrix3d2.m01 * matrix3d1.m10 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m21 * matrix3d1.m12;
            this.m12 = matrix3d2.m01 * matrix3d1.m20 + matrix3d2.m11 * matrix3d1.m21 + matrix3d2.m21 * matrix3d1.m22;
            this.m20 = matrix3d2.m02 * matrix3d1.m00 + matrix3d2.m12 * matrix3d1.m01 + matrix3d2.m22 * matrix3d1.m02;
            this.m21 = matrix3d2.m02 * matrix3d1.m10 + matrix3d2.m12 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m12;
            this.m22 = matrix3d2.m02 * matrix3d1.m20 + matrix3d2.m12 * matrix3d1.m21 + matrix3d2.m22 * matrix3d1.m22;
        } else {
            double double0 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m10 * matrix3d1.m01 + matrix3d2.m20 * matrix3d1.m02;
            double double1 = matrix3d2.m00 * matrix3d1.m10 + matrix3d2.m10 * matrix3d1.m11 + matrix3d2.m20 * matrix3d1.m12;
            double double2 = matrix3d2.m00 * matrix3d1.m20 + matrix3d2.m10 * matrix3d1.m21 + matrix3d2.m20 * matrix3d1.m22;
            double double3 = matrix3d2.m01 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m01 + matrix3d2.m21 * matrix3d1.m02;
            double double4 = matrix3d2.m01 * matrix3d1.m10 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m21 * matrix3d1.m12;
            double double5 = matrix3d2.m01 * matrix3d1.m20 + matrix3d2.m11 * matrix3d1.m21 + matrix3d2.m21 * matrix3d1.m22;
            double double6 = matrix3d2.m02 * matrix3d1.m00 + matrix3d2.m12 * matrix3d1.m01 + matrix3d2.m22 * matrix3d1.m02;
            double double7 = matrix3d2.m02 * matrix3d1.m10 + matrix3d2.m12 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m12;
            double double8 = matrix3d2.m02 * matrix3d1.m20 + matrix3d2.m12 * matrix3d1.m21 + matrix3d2.m22 * matrix3d1.m22;
            this.m00 = double0;
            this.m01 = double1;
            this.m02 = double2;
            this.m10 = double3;
            this.m11 = double4;
            this.m12 = double5;
            this.m20 = double6;
            this.m21 = double7;
            this.m22 = double8;
        }
    }

    public final void mulTransposeRight(Matrix3d matrix3d2, Matrix3d matrix3d1) {
        if (this != matrix3d2 && this != matrix3d1) {
            this.m00 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m01 * matrix3d1.m01 + matrix3d2.m02 * matrix3d1.m02;
            this.m01 = matrix3d2.m00 * matrix3d1.m10 + matrix3d2.m01 * matrix3d1.m11 + matrix3d2.m02 * matrix3d1.m12;
            this.m02 = matrix3d2.m00 * matrix3d1.m20 + matrix3d2.m01 * matrix3d1.m21 + matrix3d2.m02 * matrix3d1.m22;
            this.m10 = matrix3d2.m10 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m01 + matrix3d2.m12 * matrix3d1.m02;
            this.m11 = matrix3d2.m10 * matrix3d1.m10 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m12 * matrix3d1.m12;
            this.m12 = matrix3d2.m10 * matrix3d1.m20 + matrix3d2.m11 * matrix3d1.m21 + matrix3d2.m12 * matrix3d1.m22;
            this.m20 = matrix3d2.m20 * matrix3d1.m00 + matrix3d2.m21 * matrix3d1.m01 + matrix3d2.m22 * matrix3d1.m02;
            this.m21 = matrix3d2.m20 * matrix3d1.m10 + matrix3d2.m21 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m12;
            this.m22 = matrix3d2.m20 * matrix3d1.m20 + matrix3d2.m21 * matrix3d1.m21 + matrix3d2.m22 * matrix3d1.m22;
        } else {
            double double0 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m01 * matrix3d1.m01 + matrix3d2.m02 * matrix3d1.m02;
            double double1 = matrix3d2.m00 * matrix3d1.m10 + matrix3d2.m01 * matrix3d1.m11 + matrix3d2.m02 * matrix3d1.m12;
            double double2 = matrix3d2.m00 * matrix3d1.m20 + matrix3d2.m01 * matrix3d1.m21 + matrix3d2.m02 * matrix3d1.m22;
            double double3 = matrix3d2.m10 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m01 + matrix3d2.m12 * matrix3d1.m02;
            double double4 = matrix3d2.m10 * matrix3d1.m10 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m12 * matrix3d1.m12;
            double double5 = matrix3d2.m10 * matrix3d1.m20 + matrix3d2.m11 * matrix3d1.m21 + matrix3d2.m12 * matrix3d1.m22;
            double double6 = matrix3d2.m20 * matrix3d1.m00 + matrix3d2.m21 * matrix3d1.m01 + matrix3d2.m22 * matrix3d1.m02;
            double double7 = matrix3d2.m20 * matrix3d1.m10 + matrix3d2.m21 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m12;
            double double8 = matrix3d2.m20 * matrix3d1.m20 + matrix3d2.m21 * matrix3d1.m21 + matrix3d2.m22 * matrix3d1.m22;
            this.m00 = double0;
            this.m01 = double1;
            this.m02 = double2;
            this.m10 = double3;
            this.m11 = double4;
            this.m12 = double5;
            this.m20 = double6;
            this.m21 = double7;
            this.m22 = double8;
        }
    }

    public final void mulTransposeLeft(Matrix3d matrix3d2, Matrix3d matrix3d1) {
        if (this != matrix3d2 && this != matrix3d1) {
            this.m00 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m10 * matrix3d1.m10 + matrix3d2.m20 * matrix3d1.m20;
            this.m01 = matrix3d2.m00 * matrix3d1.m01 + matrix3d2.m10 * matrix3d1.m11 + matrix3d2.m20 * matrix3d1.m21;
            this.m02 = matrix3d2.m00 * matrix3d1.m02 + matrix3d2.m10 * matrix3d1.m12 + matrix3d2.m20 * matrix3d1.m22;
            this.m10 = matrix3d2.m01 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m10 + matrix3d2.m21 * matrix3d1.m20;
            this.m11 = matrix3d2.m01 * matrix3d1.m01 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m21 * matrix3d1.m21;
            this.m12 = matrix3d2.m01 * matrix3d1.m02 + matrix3d2.m11 * matrix3d1.m12 + matrix3d2.m21 * matrix3d1.m22;
            this.m20 = matrix3d2.m02 * matrix3d1.m00 + matrix3d2.m12 * matrix3d1.m10 + matrix3d2.m22 * matrix3d1.m20;
            this.m21 = matrix3d2.m02 * matrix3d1.m01 + matrix3d2.m12 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m21;
            this.m22 = matrix3d2.m02 * matrix3d1.m02 + matrix3d2.m12 * matrix3d1.m12 + matrix3d2.m22 * matrix3d1.m22;
        } else {
            double double0 = matrix3d2.m00 * matrix3d1.m00 + matrix3d2.m10 * matrix3d1.m10 + matrix3d2.m20 * matrix3d1.m20;
            double double1 = matrix3d2.m00 * matrix3d1.m01 + matrix3d2.m10 * matrix3d1.m11 + matrix3d2.m20 * matrix3d1.m21;
            double double2 = matrix3d2.m00 * matrix3d1.m02 + matrix3d2.m10 * matrix3d1.m12 + matrix3d2.m20 * matrix3d1.m22;
            double double3 = matrix3d2.m01 * matrix3d1.m00 + matrix3d2.m11 * matrix3d1.m10 + matrix3d2.m21 * matrix3d1.m20;
            double double4 = matrix3d2.m01 * matrix3d1.m01 + matrix3d2.m11 * matrix3d1.m11 + matrix3d2.m21 * matrix3d1.m21;
            double double5 = matrix3d2.m01 * matrix3d1.m02 + matrix3d2.m11 * matrix3d1.m12 + matrix3d2.m21 * matrix3d1.m22;
            double double6 = matrix3d2.m02 * matrix3d1.m00 + matrix3d2.m12 * matrix3d1.m10 + matrix3d2.m22 * matrix3d1.m20;
            double double7 = matrix3d2.m02 * matrix3d1.m01 + matrix3d2.m12 * matrix3d1.m11 + matrix3d2.m22 * matrix3d1.m21;
            double double8 = matrix3d2.m02 * matrix3d1.m02 + matrix3d2.m12 * matrix3d1.m12 + matrix3d2.m22 * matrix3d1.m22;
            this.m00 = double0;
            this.m01 = double1;
            this.m02 = double2;
            this.m10 = double3;
            this.m11 = double4;
            this.m12 = double5;
            this.m20 = double6;
            this.m21 = double7;
            this.m22 = double8;
        }
    }

    public final void normalize() {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = doubles0[0];
        this.m01 = doubles0[1];
        this.m02 = doubles0[2];
        this.m10 = doubles0[3];
        this.m11 = doubles0[4];
        this.m12 = doubles0[5];
        this.m20 = doubles0[6];
        this.m21 = doubles0[7];
        this.m22 = doubles0[8];
    }

    public final void normalize(Matrix3d matrix3d0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        double[] doubles2 = new double[3];
        doubles0[0] = matrix3d0.m00;
        doubles0[1] = matrix3d0.m01;
        doubles0[2] = matrix3d0.m02;
        doubles0[3] = matrix3d0.m10;
        doubles0[4] = matrix3d0.m11;
        doubles0[5] = matrix3d0.m12;
        doubles0[6] = matrix3d0.m20;
        doubles0[7] = matrix3d0.m21;
        doubles0[8] = matrix3d0.m22;
        compute_svd(doubles0, doubles2, doubles1);
        this.m00 = doubles1[0];
        this.m01 = doubles1[1];
        this.m02 = doubles1[2];
        this.m10 = doubles1[3];
        this.m11 = doubles1[4];
        this.m12 = doubles1[5];
        this.m20 = doubles1[6];
        this.m21 = doubles1[7];
        this.m22 = doubles1[8];
    }

    public final void normalizeCP() {
        double double0 = 1.0 / Math.sqrt(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20);
        this.m00 *= double0;
        this.m10 *= double0;
        this.m20 *= double0;
        double0 = 1.0 / Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        this.m01 *= double0;
        this.m11 *= double0;
        this.m21 *= double0;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public final void normalizeCP(Matrix3d matrix3d0) {
        double double0 = 1.0 / Math.sqrt(matrix3d0.m00 * matrix3d0.m00 + matrix3d0.m10 * matrix3d0.m10 + matrix3d0.m20 * matrix3d0.m20);
        this.m00 = matrix3d0.m00 * double0;
        this.m10 = matrix3d0.m10 * double0;
        this.m20 = matrix3d0.m20 * double0;
        double0 = 1.0 / Math.sqrt(matrix3d0.m01 * matrix3d0.m01 + matrix3d0.m11 * matrix3d0.m11 + matrix3d0.m21 * matrix3d0.m21);
        this.m01 = matrix3d0.m01 * double0;
        this.m11 = matrix3d0.m11 * double0;
        this.m21 = matrix3d0.m21 * double0;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public boolean equals(Matrix3d matrix3d0) {
        try {
            return this.m00 == matrix3d0.m00
                && this.m01 == matrix3d0.m01
                && this.m02 == matrix3d0.m02
                && this.m10 == matrix3d0.m10
                && this.m11 == matrix3d0.m11
                && this.m12 == matrix3d0.m12
                && this.m20 == matrix3d0.m20
                && this.m21 == matrix3d0.m21
                && this.m22 == matrix3d0.m22;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Matrix3d matrix3d0 = (Matrix3d)object;
            return this.m00 == matrix3d0.m00
                && this.m01 == matrix3d0.m01
                && this.m02 == matrix3d0.m02
                && this.m10 == matrix3d0.m10
                && this.m11 == matrix3d0.m11
                && this.m12 == matrix3d0.m12
                && this.m20 == matrix3d0.m20
                && this.m21 == matrix3d0.m21
                && this.m22 == matrix3d0.m22;
        } catch (ClassCastException classCastException) {
            return false;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    public boolean epsilonEquals(Matrix3d matrix3d0, double double1) {
        double double0 = this.m00 - matrix3d0.m00;
        if ((double0 < 0.0 ? -double0 : double0) > double1) {
            return false;
        } else {
            double0 = this.m01 - matrix3d0.m01;
            if ((double0 < 0.0 ? -double0 : double0) > double1) {
                return false;
            } else {
                double0 = this.m02 - matrix3d0.m02;
                if ((double0 < 0.0 ? -double0 : double0) > double1) {
                    return false;
                } else {
                    double0 = this.m10 - matrix3d0.m10;
                    if ((double0 < 0.0 ? -double0 : double0) > double1) {
                        return false;
                    } else {
                        double0 = this.m11 - matrix3d0.m11;
                        if ((double0 < 0.0 ? -double0 : double0) > double1) {
                            return false;
                        } else {
                            double0 = this.m12 - matrix3d0.m12;
                            if ((double0 < 0.0 ? -double0 : double0) > double1) {
                                return false;
                            } else {
                                double0 = this.m20 - matrix3d0.m20;
                                if ((double0 < 0.0 ? -double0 : double0) > double1) {
                                    return false;
                                } else {
                                    double0 = this.m21 - matrix3d0.m21;
                                    if ((double0 < 0.0 ? -double0 : double0) > double1) {
                                        return false;
                                    } else {
                                        double0 = this.m22 - matrix3d0.m22;
                                        return !((double0 < 0.0 ? -double0 : double0) > double1);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m00);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m01);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m02);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m10);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m11);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m12);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m20);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m21);
        long0 = 31L * long0 + VecMathUtil.doubleToLongBits(this.m22);
        return (int)(long0 ^ long0 >> 32);
    }

    public final void setZero() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
    }

    public final void negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
    }

    public final void negate(Matrix3d matrix3d0) {
        this.m00 = -matrix3d0.m00;
        this.m01 = -matrix3d0.m01;
        this.m02 = -matrix3d0.m02;
        this.m10 = -matrix3d0.m10;
        this.m11 = -matrix3d0.m11;
        this.m12 = -matrix3d0.m12;
        this.m20 = -matrix3d0.m20;
        this.m21 = -matrix3d0.m21;
        this.m22 = -matrix3d0.m22;
    }

    public final void transform(Tuple3d tuple3d) {
        double double0 = this.m00 * tuple3d.x + this.m01 * tuple3d.y + this.m02 * tuple3d.z;
        double double1 = this.m10 * tuple3d.x + this.m11 * tuple3d.y + this.m12 * tuple3d.z;
        double double2 = this.m20 * tuple3d.x + this.m21 * tuple3d.y + this.m22 * tuple3d.z;
        tuple3d.set(double0, double1, double2);
    }

    public final void transform(Tuple3d tuple3d0, Tuple3d tuple3d1) {
        double double0 = this.m00 * tuple3d0.x + this.m01 * tuple3d0.y + this.m02 * tuple3d0.z;
        double double1 = this.m10 * tuple3d0.x + this.m11 * tuple3d0.y + this.m12 * tuple3d0.z;
        tuple3d1.z = this.m20 * tuple3d0.x + this.m21 * tuple3d0.y + this.m22 * tuple3d0.z;
        tuple3d1.x = double0;
        tuple3d1.y = double1;
    }

    final void getScaleRotate(double[] doubles1, double[] doubles2) {
        double[] doubles0 = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
        compute_svd(doubles0, doubles1, doubles2);
    }

    static void compute_svd(double[] doubles7, double[] doubles8, double[] doubles9) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        double[] doubles2 = new double[9];
        double[] doubles3 = new double[9];
        double[] doubles4 = new double[9];
        double[] doubles5 = new double[3];
        double[] doubles6 = new double[3];
        int int0 = 0;

        for (int int1 = 0; int1 < 9; int1++) {
            doubles4[int1] = doubles7[int1];
        }

        if (doubles7[3] * doubles7[3] < 1.110223024E-16) {
            doubles0[0] = 1.0;
            doubles0[1] = 0.0;
            doubles0[2] = 0.0;
            doubles0[3] = 0.0;
            doubles0[4] = 1.0;
            doubles0[5] = 0.0;
            doubles0[6] = 0.0;
            doubles0[7] = 0.0;
            doubles0[8] = 1.0;
        } else if (doubles7[0] * doubles7[0] < 1.110223024E-16) {
            doubles2[0] = doubles7[0];
            doubles2[1] = doubles7[1];
            doubles2[2] = doubles7[2];
            doubles7[0] = doubles7[3];
            doubles7[1] = doubles7[4];
            doubles7[2] = doubles7[5];
            doubles7[3] = -doubles2[0];
            doubles7[4] = -doubles2[1];
            doubles7[5] = -doubles2[2];
            doubles0[0] = 0.0;
            doubles0[1] = 1.0;
            doubles0[2] = 0.0;
            doubles0[3] = -1.0;
            doubles0[4] = 0.0;
            doubles0[5] = 0.0;
            doubles0[6] = 0.0;
            doubles0[7] = 0.0;
            doubles0[8] = 1.0;
        } else {
            double double0 = 1.0 / Math.sqrt(doubles7[0] * doubles7[0] + doubles7[3] * doubles7[3]);
            double double1 = doubles7[0] * double0;
            double double2 = doubles7[3] * double0;
            doubles2[0] = double1 * doubles7[0] + double2 * doubles7[3];
            doubles2[1] = double1 * doubles7[1] + double2 * doubles7[4];
            doubles2[2] = double1 * doubles7[2] + double2 * doubles7[5];
            doubles7[3] = -double2 * doubles7[0] + double1 * doubles7[3];
            doubles7[4] = -double2 * doubles7[1] + double1 * doubles7[4];
            doubles7[5] = -double2 * doubles7[2] + double1 * doubles7[5];
            doubles7[0] = doubles2[0];
            doubles7[1] = doubles2[1];
            doubles7[2] = doubles2[2];
            doubles0[0] = double1;
            doubles0[1] = double2;
            doubles0[2] = 0.0;
            doubles0[3] = -double2;
            doubles0[4] = double1;
            doubles0[5] = 0.0;
            doubles0[6] = 0.0;
            doubles0[7] = 0.0;
            doubles0[8] = 1.0;
        }

        if (!(doubles7[6] * doubles7[6] < 1.110223024E-16)) {
            if (doubles7[0] * doubles7[0] < 1.110223024E-16) {
                doubles2[0] = doubles7[0];
                doubles2[1] = doubles7[1];
                doubles2[2] = doubles7[2];
                doubles7[0] = doubles7[6];
                doubles7[1] = doubles7[7];
                doubles7[2] = doubles7[8];
                doubles7[6] = -doubles2[0];
                doubles7[7] = -doubles2[1];
                doubles7[8] = -doubles2[2];
                doubles2[0] = doubles0[0];
                doubles2[1] = doubles0[1];
                doubles2[2] = doubles0[2];
                doubles0[0] = doubles0[6];
                doubles0[1] = doubles0[7];
                doubles0[2] = doubles0[8];
                doubles0[6] = -doubles2[0];
                doubles0[7] = -doubles2[1];
                doubles0[8] = -doubles2[2];
            } else {
                double double3 = 1.0 / Math.sqrt(doubles7[0] * doubles7[0] + doubles7[6] * doubles7[6]);
                double double4 = doubles7[0] * double3;
                double double5 = doubles7[6] * double3;
                doubles2[0] = double4 * doubles7[0] + double5 * doubles7[6];
                doubles2[1] = double4 * doubles7[1] + double5 * doubles7[7];
                doubles2[2] = double4 * doubles7[2] + double5 * doubles7[8];
                doubles7[6] = -double5 * doubles7[0] + double4 * doubles7[6];
                doubles7[7] = -double5 * doubles7[1] + double4 * doubles7[7];
                doubles7[8] = -double5 * doubles7[2] + double4 * doubles7[8];
                doubles7[0] = doubles2[0];
                doubles7[1] = doubles2[1];
                doubles7[2] = doubles2[2];
                doubles2[0] = double4 * doubles0[0];
                doubles2[1] = double4 * doubles0[1];
                doubles0[2] = double5;
                doubles2[6] = -doubles0[0] * double5;
                doubles2[7] = -doubles0[1] * double5;
                doubles0[8] = double4;
                doubles0[0] = doubles2[0];
                doubles0[1] = doubles2[1];
                doubles0[6] = doubles2[6];
                doubles0[7] = doubles2[7];
            }
        }

        if (doubles7[2] * doubles7[2] < 1.110223024E-16) {
            doubles1[0] = 1.0;
            doubles1[1] = 0.0;
            doubles1[2] = 0.0;
            doubles1[3] = 0.0;
            doubles1[4] = 1.0;
            doubles1[5] = 0.0;
            doubles1[6] = 0.0;
            doubles1[7] = 0.0;
            doubles1[8] = 1.0;
        } else if (doubles7[1] * doubles7[1] < 1.110223024E-16) {
            doubles2[2] = doubles7[2];
            doubles2[5] = doubles7[5];
            doubles2[8] = doubles7[8];
            doubles7[2] = -doubles7[1];
            doubles7[5] = -doubles7[4];
            doubles7[8] = -doubles7[7];
            doubles7[1] = doubles2[2];
            doubles7[4] = doubles2[5];
            doubles7[7] = doubles2[8];
            doubles1[0] = 1.0;
            doubles1[1] = 0.0;
            doubles1[2] = 0.0;
            doubles1[3] = 0.0;
            doubles1[4] = 0.0;
            doubles1[5] = -1.0;
            doubles1[6] = 0.0;
            doubles1[7] = 1.0;
            doubles1[8] = 0.0;
        } else {
            double double6 = 1.0 / Math.sqrt(doubles7[1] * doubles7[1] + doubles7[2] * doubles7[2]);
            double double7 = doubles7[1] * double6;
            double double8 = doubles7[2] * double6;
            doubles2[1] = double7 * doubles7[1] + double8 * doubles7[2];
            doubles7[2] = -double8 * doubles7[1] + double7 * doubles7[2];
            doubles7[1] = doubles2[1];
            doubles2[4] = double7 * doubles7[4] + double8 * doubles7[5];
            doubles7[5] = -double8 * doubles7[4] + double7 * doubles7[5];
            doubles7[4] = doubles2[4];
            doubles2[7] = double7 * doubles7[7] + double8 * doubles7[8];
            doubles7[8] = -double8 * doubles7[7] + double7 * doubles7[8];
            doubles7[7] = doubles2[7];
            doubles1[0] = 1.0;
            doubles1[1] = 0.0;
            doubles1[2] = 0.0;
            doubles1[3] = 0.0;
            doubles1[4] = double7;
            doubles1[5] = -double8;
            doubles1[6] = 0.0;
            doubles1[7] = double8;
            doubles1[8] = double7;
        }

        if (!(doubles7[7] * doubles7[7] < 1.110223024E-16)) {
            if (doubles7[4] * doubles7[4] < 1.110223024E-16) {
                doubles2[3] = doubles7[3];
                doubles2[4] = doubles7[4];
                doubles2[5] = doubles7[5];
                doubles7[3] = doubles7[6];
                doubles7[4] = doubles7[7];
                doubles7[5] = doubles7[8];
                doubles7[6] = -doubles2[3];
                doubles7[7] = -doubles2[4];
                doubles7[8] = -doubles2[5];
                doubles2[3] = doubles0[3];
                doubles2[4] = doubles0[4];
                doubles2[5] = doubles0[5];
                doubles0[3] = doubles0[6];
                doubles0[4] = doubles0[7];
                doubles0[5] = doubles0[8];
                doubles0[6] = -doubles2[3];
                doubles0[7] = -doubles2[4];
                doubles0[8] = -doubles2[5];
            } else {
                double double9 = 1.0 / Math.sqrt(doubles7[4] * doubles7[4] + doubles7[7] * doubles7[7]);
                double double10 = doubles7[4] * double9;
                double double11 = doubles7[7] * double9;
                doubles2[3] = double10 * doubles7[3] + double11 * doubles7[6];
                doubles7[6] = -double11 * doubles7[3] + double10 * doubles7[6];
                doubles7[3] = doubles2[3];
                doubles2[4] = double10 * doubles7[4] + double11 * doubles7[7];
                doubles7[7] = -double11 * doubles7[4] + double10 * doubles7[7];
                doubles7[4] = doubles2[4];
                doubles2[5] = double10 * doubles7[5] + double11 * doubles7[8];
                doubles7[8] = -double11 * doubles7[5] + double10 * doubles7[8];
                doubles7[5] = doubles2[5];
                doubles2[3] = double10 * doubles0[3] + double11 * doubles0[6];
                doubles0[6] = -double11 * doubles0[3] + double10 * doubles0[6];
                doubles0[3] = doubles2[3];
                doubles2[4] = double10 * doubles0[4] + double11 * doubles0[7];
                doubles0[7] = -double11 * doubles0[4] + double10 * doubles0[7];
                doubles0[4] = doubles2[4];
                doubles2[5] = double10 * doubles0[5] + double11 * doubles0[8];
                doubles0[8] = -double11 * doubles0[5] + double10 * doubles0[8];
                doubles0[5] = doubles2[5];
            }
        }

        doubles3[0] = doubles7[0];
        doubles3[1] = doubles7[4];
        doubles3[2] = doubles7[8];
        doubles5[0] = doubles7[1];
        doubles5[1] = doubles7[5];
        if (!(doubles5[0] * doubles5[0] < 1.110223024E-16) || !(doubles5[1] * doubles5[1] < 1.110223024E-16)) {
            compute_qr(doubles3, doubles5, doubles0, doubles1);
        }

        doubles6[0] = doubles3[0];
        doubles6[1] = doubles3[1];
        doubles6[2] = doubles3[2];
        if (almostEqual(Math.abs(doubles6[0]), 1.0) && almostEqual(Math.abs(doubles6[1]), 1.0) && almostEqual(Math.abs(doubles6[2]), 1.0)) {
            for (int int2 = 0; int2 < 3; int2++) {
                if (doubles6[int2] < 0.0) {
                    int0++;
                }
            }

            if (int0 == 0 || int0 == 2) {
                doubles8[0] = doubles8[1] = doubles8[2] = 1.0;

                for (int int3 = 0; int3 < 9; int3++) {
                    doubles9[int3] = doubles4[int3];
                }

                return;
            }
        }

        transpose_mat(doubles0, doubles2);
        transpose_mat(doubles1, doubles3);
        svdReorder(doubles7, doubles2, doubles3, doubles6, doubles9, doubles8);
    }

    static void svdReorder(double[] doubles7, double[] doubles4, double[] doubles3, double[] doubles2, double[] doubles5, double[] doubles6) {
        int[] ints0 = new int[3];
        int[] ints1 = new int[3];
        double[] doubles0 = new double[3];
        double[] doubles1 = new double[9];
        if (doubles2[0] < 0.0) {
            doubles2[0] = -doubles2[0];
            doubles3[0] = -doubles3[0];
            doubles3[1] = -doubles3[1];
            doubles3[2] = -doubles3[2];
        }

        if (doubles2[1] < 0.0) {
            doubles2[1] = -doubles2[1];
            doubles3[3] = -doubles3[3];
            doubles3[4] = -doubles3[4];
            doubles3[5] = -doubles3[5];
        }

        if (doubles2[2] < 0.0) {
            doubles2[2] = -doubles2[2];
            doubles3[6] = -doubles3[6];
            doubles3[7] = -doubles3[7];
            doubles3[8] = -doubles3[8];
        }

        mat_mul(doubles4, doubles3, doubles1);
        if (almostEqual(Math.abs(doubles2[0]), Math.abs(doubles2[1])) && almostEqual(Math.abs(doubles2[1]), Math.abs(doubles2[2]))) {
            for (int int0 = 0; int0 < 9; int0++) {
                doubles5[int0] = doubles1[int0];
            }

            for (int int1 = 0; int1 < 3; int1++) {
                doubles6[int1] = doubles2[int1];
            }
        } else {
            if (doubles2[0] > doubles2[1]) {
                if (doubles2[0] > doubles2[2]) {
                    if (doubles2[2] > doubles2[1]) {
                        ints0[0] = 0;
                        ints0[1] = 2;
                        ints0[2] = 1;
                    } else {
                        ints0[0] = 0;
                        ints0[1] = 1;
                        ints0[2] = 2;
                    }
                } else {
                    ints0[0] = 2;
                    ints0[1] = 0;
                    ints0[2] = 1;
                }
            } else if (doubles2[1] > doubles2[2]) {
                if (doubles2[2] > doubles2[0]) {
                    ints0[0] = 1;
                    ints0[1] = 2;
                    ints0[2] = 0;
                } else {
                    ints0[0] = 1;
                    ints0[1] = 0;
                    ints0[2] = 2;
                }
            } else {
                ints0[0] = 2;
                ints0[1] = 1;
                ints0[2] = 0;
            }

            doubles0[0] = doubles7[0] * doubles7[0] + doubles7[1] * doubles7[1] + doubles7[2] * doubles7[2];
            doubles0[1] = doubles7[3] * doubles7[3] + doubles7[4] * doubles7[4] + doubles7[5] * doubles7[5];
            doubles0[2] = doubles7[6] * doubles7[6] + doubles7[7] * doubles7[7] + doubles7[8] * doubles7[8];
            byte byte0;
            byte byte1;
            byte byte2;
            if (doubles0[0] > doubles0[1]) {
                if (doubles0[0] > doubles0[2]) {
                    if (doubles0[2] > doubles0[1]) {
                        byte0 = 0;
                        byte2 = 1;
                        byte1 = 2;
                    } else {
                        byte0 = 0;
                        byte1 = 1;
                        byte2 = 2;
                    }
                } else {
                    byte2 = 0;
                    byte0 = 1;
                    byte1 = 2;
                }
            } else if (doubles0[1] > doubles0[2]) {
                if (doubles0[2] > doubles0[0]) {
                    byte1 = 0;
                    byte2 = 1;
                    byte0 = 2;
                } else {
                    byte1 = 0;
                    byte0 = 1;
                    byte2 = 2;
                }
            } else {
                byte2 = 0;
                byte1 = 1;
                byte0 = 2;
            }

            int int2 = ints0[byte0];
            doubles6[0] = doubles2[int2];
            int2 = ints0[byte1];
            doubles6[1] = doubles2[int2];
            int2 = ints0[byte2];
            doubles6[2] = doubles2[int2];
            int2 = ints0[byte0];
            doubles5[0] = doubles1[int2];
            int2 = ints0[byte0] + 3;
            doubles5[3] = doubles1[int2];
            int2 = ints0[byte0] + 6;
            doubles5[6] = doubles1[int2];
            int2 = ints0[byte1];
            doubles5[1] = doubles1[int2];
            int2 = ints0[byte1] + 3;
            doubles5[4] = doubles1[int2];
            int2 = ints0[byte1] + 6;
            doubles5[7] = doubles1[int2];
            int2 = ints0[byte2];
            doubles5[2] = doubles1[int2];
            int2 = ints0[byte2] + 3;
            doubles5[5] = doubles1[int2];
            int2 = ints0[byte2] + 6;
            doubles5[8] = doubles1[int2];
        }
    }

    static int compute_qr(double[] doubles6, double[] doubles5, double[] doubles7, double[] doubles8) {
        double[] doubles0 = new double[2];
        double[] doubles1 = new double[2];
        double[] doubles2 = new double[2];
        double[] doubles3 = new double[2];
        double[] doubles4 = new double[9];
        double double0 = 1.0;
        double double1 = -1.0;
        boolean boolean0 = false;
        byte byte0 = 1;
        if (Math.abs(doubles5[1]) < 4.89E-15 || Math.abs(doubles5[0]) < 4.89E-15) {
            boolean0 = true;
        }

        for (int int0 = 0; int0 < 10 && !boolean0; int0++) {
            double double2 = compute_shift(doubles6[1], doubles5[1], doubles6[2]);
            double double3 = (Math.abs(doubles6[0]) - double2) * (d_sign(double0, doubles6[0]) + double2 / doubles6[0]);
            double double4 = doubles5[0];
            double double5 = compute_rot(double3, double4, doubles3, doubles1, 0, byte0);
            double3 = doubles1[0] * doubles6[0] + doubles3[0] * doubles5[0];
            doubles5[0] = doubles1[0] * doubles5[0] - doubles3[0] * doubles6[0];
            double4 = doubles3[0] * doubles6[1];
            doubles6[1] = doubles1[0] * doubles6[1];
            double5 = compute_rot(double3, double4, doubles2, doubles0, 0, byte0);
            byte0 = 0;
            doubles6[0] = double5;
            double3 = doubles0[0] * doubles5[0] + doubles2[0] * doubles6[1];
            doubles6[1] = doubles0[0] * doubles6[1] - doubles2[0] * doubles5[0];
            double4 = doubles2[0] * doubles5[1];
            doubles5[1] = doubles0[0] * doubles5[1];
            double5 = compute_rot(double3, double4, doubles3, doubles1, 1, byte0);
            doubles5[0] = double5;
            double3 = doubles1[1] * doubles6[1] + doubles3[1] * doubles5[1];
            doubles5[1] = doubles1[1] * doubles5[1] - doubles3[1] * doubles6[1];
            double4 = doubles3[1] * doubles6[2];
            doubles6[2] = doubles1[1] * doubles6[2];
            double5 = compute_rot(double3, double4, doubles2, doubles0, 1, byte0);
            doubles6[1] = double5;
            double3 = doubles0[1] * doubles5[1] + doubles2[1] * doubles6[2];
            doubles6[2] = doubles0[1] * doubles6[2] - doubles2[1] * doubles5[1];
            doubles5[1] = double3;
            double double6 = doubles7[0];
            doubles7[0] = doubles0[0] * double6 + doubles2[0] * doubles7[3];
            doubles7[3] = -doubles2[0] * double6 + doubles0[0] * doubles7[3];
            double6 = doubles7[1];
            doubles7[1] = doubles0[0] * double6 + doubles2[0] * doubles7[4];
            doubles7[4] = -doubles2[0] * double6 + doubles0[0] * doubles7[4];
            double6 = doubles7[2];
            doubles7[2] = doubles0[0] * double6 + doubles2[0] * doubles7[5];
            doubles7[5] = -doubles2[0] * double6 + doubles0[0] * doubles7[5];
            double6 = doubles7[3];
            doubles7[3] = doubles0[1] * double6 + doubles2[1] * doubles7[6];
            doubles7[6] = -doubles2[1] * double6 + doubles0[1] * doubles7[6];
            double6 = doubles7[4];
            doubles7[4] = doubles0[1] * double6 + doubles2[1] * doubles7[7];
            doubles7[7] = -doubles2[1] * double6 + doubles0[1] * doubles7[7];
            double6 = doubles7[5];
            doubles7[5] = doubles0[1] * double6 + doubles2[1] * doubles7[8];
            doubles7[8] = -doubles2[1] * double6 + doubles0[1] * doubles7[8];
            double double7 = doubles8[0];
            doubles8[0] = doubles1[0] * double7 + doubles3[0] * doubles8[1];
            doubles8[1] = -doubles3[0] * double7 + doubles1[0] * doubles8[1];
            double7 = doubles8[3];
            doubles8[3] = doubles1[0] * double7 + doubles3[0] * doubles8[4];
            doubles8[4] = -doubles3[0] * double7 + doubles1[0] * doubles8[4];
            double7 = doubles8[6];
            doubles8[6] = doubles1[0] * double7 + doubles3[0] * doubles8[7];
            doubles8[7] = -doubles3[0] * double7 + doubles1[0] * doubles8[7];
            double7 = doubles8[1];
            doubles8[1] = doubles1[1] * double7 + doubles3[1] * doubles8[2];
            doubles8[2] = -doubles3[1] * double7 + doubles1[1] * doubles8[2];
            double7 = doubles8[4];
            doubles8[4] = doubles1[1] * double7 + doubles3[1] * doubles8[5];
            doubles8[5] = -doubles3[1] * double7 + doubles1[1] * doubles8[5];
            double7 = doubles8[7];
            doubles8[7] = doubles1[1] * double7 + doubles3[1] * doubles8[8];
            doubles8[8] = -doubles3[1] * double7 + doubles1[1] * doubles8[8];
            doubles4[0] = doubles6[0];
            doubles4[1] = doubles5[0];
            doubles4[2] = 0.0;
            doubles4[3] = 0.0;
            doubles4[4] = doubles6[1];
            doubles4[5] = doubles5[1];
            doubles4[6] = 0.0;
            doubles4[7] = 0.0;
            doubles4[8] = doubles6[2];
            if (Math.abs(doubles5[1]) < 4.89E-15 || Math.abs(doubles5[0]) < 4.89E-15) {
                boolean0 = true;
            }
        }

        if (Math.abs(doubles5[1]) < 4.89E-15) {
            compute_2X2(doubles6[0], doubles5[0], doubles6[1], doubles6, doubles2, doubles0, doubles3, doubles1, 0);
            double double8 = doubles7[0];
            doubles7[0] = doubles0[0] * double8 + doubles2[0] * doubles7[3];
            doubles7[3] = -doubles2[0] * double8 + doubles0[0] * doubles7[3];
            double8 = doubles7[1];
            doubles7[1] = doubles0[0] * double8 + doubles2[0] * doubles7[4];
            doubles7[4] = -doubles2[0] * double8 + doubles0[0] * doubles7[4];
            double8 = doubles7[2];
            doubles7[2] = doubles0[0] * double8 + doubles2[0] * doubles7[5];
            doubles7[5] = -doubles2[0] * double8 + doubles0[0] * doubles7[5];
            double double9 = doubles8[0];
            doubles8[0] = doubles1[0] * double9 + doubles3[0] * doubles8[1];
            doubles8[1] = -doubles3[0] * double9 + doubles1[0] * doubles8[1];
            double9 = doubles8[3];
            doubles8[3] = doubles1[0] * double9 + doubles3[0] * doubles8[4];
            doubles8[4] = -doubles3[0] * double9 + doubles1[0] * doubles8[4];
            double9 = doubles8[6];
            doubles8[6] = doubles1[0] * double9 + doubles3[0] * doubles8[7];
            doubles8[7] = -doubles3[0] * double9 + doubles1[0] * doubles8[7];
        } else {
            compute_2X2(doubles6[1], doubles5[1], doubles6[2], doubles6, doubles2, doubles0, doubles3, doubles1, 1);
            double double10 = doubles7[3];
            doubles7[3] = doubles0[0] * double10 + doubles2[0] * doubles7[6];
            doubles7[6] = -doubles2[0] * double10 + doubles0[0] * doubles7[6];
            double10 = doubles7[4];
            doubles7[4] = doubles0[0] * double10 + doubles2[0] * doubles7[7];
            doubles7[7] = -doubles2[0] * double10 + doubles0[0] * doubles7[7];
            double10 = doubles7[5];
            doubles7[5] = doubles0[0] * double10 + doubles2[0] * doubles7[8];
            doubles7[8] = -doubles2[0] * double10 + doubles0[0] * doubles7[8];
            double double11 = doubles8[1];
            doubles8[1] = doubles1[0] * double11 + doubles3[0] * doubles8[2];
            doubles8[2] = -doubles3[0] * double11 + doubles1[0] * doubles8[2];
            double11 = doubles8[4];
            doubles8[4] = doubles1[0] * double11 + doubles3[0] * doubles8[5];
            doubles8[5] = -doubles3[0] * double11 + doubles1[0] * doubles8[5];
            double11 = doubles8[7];
            doubles8[7] = doubles1[0] * double11 + doubles3[0] * doubles8[8];
            doubles8[8] = -doubles3[0] * double11 + doubles1[0] * doubles8[8];
        }

        return 0;
    }

    static double max(double double0, double double1) {
        return double0 > double1 ? double0 : double1;
    }

    static double min(double double0, double double1) {
        return double0 < double1 ? double0 : double1;
    }

    static double d_sign(double double1, double double2) {
        double double0 = double1 >= 0.0 ? double1 : -double1;
        return double2 >= 0.0 ? double0 : -double0;
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
                if (double11 / double16 < 1.110223024E-16) {
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
                    if (double11 / double16 < 1.110223024E-16) {
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

    static double compute_rot(double double4, double double3, double[] doubles0, double[] doubles1, int int4, int var7) {
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

        doubles0[int4] = double1;
        doubles1[int4] = double0;
        return double2;
    }

    static void print_mat(double[] doubles) {
        for (int int0 = 0; int0 < 3; int0++) {
            System.out.println(doubles[int0 * 3 + 0] + " " + doubles[int0 * 3 + 1] + " " + doubles[int0 * 3 + 2] + "\n");
        }
    }

    static void print_det(double[] doubles) {
        double double0 = doubles[0] * doubles[4] * doubles[8]
            + doubles[1] * doubles[5] * doubles[6]
            + doubles[2] * doubles[3] * doubles[7]
            - doubles[2] * doubles[4] * doubles[6]
            - doubles[0] * doubles[5] * doubles[7]
            - doubles[1] * doubles[3] * doubles[8];
        System.out.println("det= " + double0);
    }

    static void mat_mul(double[] doubles2, double[] doubles1, double[] doubles3) {
        double[] doubles0 = new double[]{
            doubles2[0] * doubles1[0] + doubles2[1] * doubles1[3] + doubles2[2] * doubles1[6],
            doubles2[0] * doubles1[1] + doubles2[1] * doubles1[4] + doubles2[2] * doubles1[7],
            doubles2[0] * doubles1[2] + doubles2[1] * doubles1[5] + doubles2[2] * doubles1[8],
            doubles2[3] * doubles1[0] + doubles2[4] * doubles1[3] + doubles2[5] * doubles1[6],
            doubles2[3] * doubles1[1] + doubles2[4] * doubles1[4] + doubles2[5] * doubles1[7],
            doubles2[3] * doubles1[2] + doubles2[4] * doubles1[5] + doubles2[5] * doubles1[8],
            doubles2[6] * doubles1[0] + doubles2[7] * doubles1[3] + doubles2[8] * doubles1[6],
            doubles2[6] * doubles1[1] + doubles2[7] * doubles1[4] + doubles2[8] * doubles1[7],
            doubles2[6] * doubles1[2] + doubles2[7] * doubles1[5] + doubles2[8] * doubles1[8]
        };

        for (int int0 = 0; int0 < 9; int0++) {
            doubles3[int0] = doubles0[int0];
        }
    }

    static void transpose_mat(double[] doubles0, double[] doubles1) {
        doubles1[0] = doubles0[0];
        doubles1[1] = doubles0[3];
        doubles1[2] = doubles0[6];
        doubles1[3] = doubles0[1];
        doubles1[4] = doubles0[4];
        doubles1[5] = doubles0[7];
        doubles1[6] = doubles0[2];
        doubles1[7] = doubles0[5];
        doubles1[8] = doubles0[8];
    }

    static double max3(double[] doubles) {
        if (doubles[0] > doubles[1]) {
            return doubles[0] > doubles[2] ? doubles[0] : doubles[2];
        } else {
            return doubles[1] > doubles[2] ? doubles[1] : doubles[2];
        }
    }

    private static final boolean almostEqual(double double0, double double1) {
        if (double0 == double1) {
            return true;
        } else {
            double double2 = Math.abs(double0 - double1);
            double double3 = Math.abs(double0);
            double double4 = Math.abs(double1);
            double double5 = double3 >= double4 ? double3 : double4;
            return double2 < 1.0E-6 ? true : double2 / double5 < 1.0E-4;
        }
    }

    @Override
    public Object clone() {
        Object object = null;

        try {
            return (Matrix3d)super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    public final double getM00() {
        return this.m00;
    }

    public final void setM00(double double0) {
        this.m00 = double0;
    }

    public final double getM01() {
        return this.m01;
    }

    public final void setM01(double double0) {
        this.m01 = double0;
    }

    public final double getM02() {
        return this.m02;
    }

    public final void setM02(double double0) {
        this.m02 = double0;
    }

    public final double getM10() {
        return this.m10;
    }

    public final void setM10(double double0) {
        this.m10 = double0;
    }

    public final double getM11() {
        return this.m11;
    }

    public final void setM11(double double0) {
        this.m11 = double0;
    }

    public final double getM12() {
        return this.m12;
    }

    public final void setM12(double double0) {
        this.m12 = double0;
    }

    public final double getM20() {
        return this.m20;
    }

    public final void setM20(double double0) {
        this.m20 = double0;
    }

    public final double getM21() {
        return this.m21;
    }

    public final void setM21(double double0) {
        this.m21 = double0;
    }

    public final double getM22() {
        return this.m22;
    }

    public final void setM22(double double0) {
        this.m22 = double0;
    }
}
