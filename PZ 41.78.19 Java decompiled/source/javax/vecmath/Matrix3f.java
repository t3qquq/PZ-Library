// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Matrix3f implements Serializable, Cloneable {
    static final long serialVersionUID = 329697160112089834L;
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;
    private static final double EPS = 1.0E-8;

    public Matrix3f(float float0, float float1, float float2, float float3, float float4, float float5, float float6, float float7, float float8) {
        this.m00 = float0;
        this.m01 = float1;
        this.m02 = float2;
        this.m10 = float3;
        this.m11 = float4;
        this.m12 = float5;
        this.m20 = float6;
        this.m21 = float7;
        this.m22 = float8;
    }

    public Matrix3f(float[] floats) {
        this.m00 = floats[0];
        this.m01 = floats[1];
        this.m02 = floats[2];
        this.m10 = floats[3];
        this.m11 = floats[4];
        this.m12 = floats[5];
        this.m20 = floats[6];
        this.m21 = floats[7];
        this.m22 = floats[8];
    }

    public Matrix3f(Matrix3d matrix3d) {
        this.m00 = (float)matrix3d.m00;
        this.m01 = (float)matrix3d.m01;
        this.m02 = (float)matrix3d.m02;
        this.m10 = (float)matrix3d.m10;
        this.m11 = (float)matrix3d.m11;
        this.m12 = (float)matrix3d.m12;
        this.m20 = (float)matrix3d.m20;
        this.m21 = (float)matrix3d.m21;
        this.m22 = (float)matrix3d.m22;
    }

    public Matrix3f(Matrix3f matrix3f1) {
        this.m00 = matrix3f1.m00;
        this.m01 = matrix3f1.m01;
        this.m02 = matrix3f1.m02;
        this.m10 = matrix3f1.m10;
        this.m11 = matrix3f1.m11;
        this.m12 = matrix3f1.m12;
        this.m20 = matrix3f1.m20;
        this.m21 = matrix3f1.m21;
        this.m22 = matrix3f1.m22;
    }

    public Matrix3f() {
        this.m00 = 0.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 0.0F;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 0.0F;
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
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
    }

    public final void setScale(float float0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = (float)(doubles0[0] * float0);
        this.m01 = (float)(doubles0[1] * float0);
        this.m02 = (float)(doubles0[2] * float0);
        this.m10 = (float)(doubles0[3] * float0);
        this.m11 = (float)(doubles0[4] * float0);
        this.m12 = (float)(doubles0[5] * float0);
        this.m20 = (float)(doubles0[6] * float0);
        this.m21 = (float)(doubles0[7] * float0);
        this.m22 = (float)(doubles0[8] * float0);
    }

    public final void setElement(int int0, int int1, float float0) {
        switch (int0) {
            case 0:
                switch (int1) {
                    case 0:
                        this.m00 = float0;
                        return;
                    case 1:
                        this.m01 = float0;
                        return;
                    case 2:
                        this.m02 = float0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
                }
            case 1:
                switch (int1) {
                    case 0:
                        this.m10 = float0;
                        return;
                    case 1:
                        this.m11 = float0;
                        return;
                    case 2:
                        this.m12 = float0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
                }
            case 2:
                switch (int1) {
                    case 0:
                        this.m20 = float0;
                        return;
                    case 1:
                        this.m21 = float0;
                        return;
                    case 2:
                        this.m22 = float0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
                }
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
        }
    }

    public final void getRow(int int0, Vector3f vector3f) {
        if (int0 == 0) {
            vector3f.x = this.m00;
            vector3f.y = this.m01;
            vector3f.z = this.m02;
        } else if (int0 == 1) {
            vector3f.x = this.m10;
            vector3f.y = this.m11;
            vector3f.z = this.m12;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
            }

            vector3f.x = this.m20;
            vector3f.y = this.m21;
            vector3f.z = this.m22;
        }
    }

    public final void getRow(int int0, float[] floats) {
        if (int0 == 0) {
            floats[0] = this.m00;
            floats[1] = this.m01;
            floats[2] = this.m02;
        } else if (int0 == 1) {
            floats[0] = this.m10;
            floats[1] = this.m11;
            floats[2] = this.m12;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
            }

            floats[0] = this.m20;
            floats[1] = this.m21;
            floats[2] = this.m22;
        }
    }

    public final void getColumn(int int0, Vector3f vector3f) {
        if (int0 == 0) {
            vector3f.x = this.m00;
            vector3f.y = this.m10;
            vector3f.z = this.m20;
        } else if (int0 == 1) {
            vector3f.x = this.m01;
            vector3f.y = this.m11;
            vector3f.z = this.m21;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
            }

            vector3f.x = this.m02;
            vector3f.y = this.m12;
            vector3f.z = this.m22;
        }
    }

    public final void getColumn(int int0, float[] floats) {
        if (int0 == 0) {
            floats[0] = this.m00;
            floats[1] = this.m10;
            floats[2] = this.m20;
        } else if (int0 == 1) {
            floats[0] = this.m01;
            floats[1] = this.m11;
            floats[2] = this.m21;
        } else {
            if (int0 != 2) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
            }

            floats[0] = this.m02;
            floats[1] = this.m12;
            floats[2] = this.m22;
        }
    }

    public final float getElement(int int0, int int1) {
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
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
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
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
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

        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
    }

    public final void setRow(int int0, float float0, float float1, float float2) {
        switch (int0) {
            case 0:
                this.m00 = float0;
                this.m01 = float1;
                this.m02 = float2;
                break;
            case 1:
                this.m10 = float0;
                this.m11 = float1;
                this.m12 = float2;
                break;
            case 2:
                this.m20 = float0;
                this.m21 = float1;
                this.m22 = float2;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
        }
    }

    public final void setRow(int int0, Vector3f vector3f) {
        switch (int0) {
            case 0:
                this.m00 = vector3f.x;
                this.m01 = vector3f.y;
                this.m02 = vector3f.z;
                break;
            case 1:
                this.m10 = vector3f.x;
                this.m11 = vector3f.y;
                this.m12 = vector3f.z;
                break;
            case 2:
                this.m20 = vector3f.x;
                this.m21 = vector3f.y;
                this.m22 = vector3f.z;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
        }
    }

    public final void setRow(int int0, float[] floats) {
        switch (int0) {
            case 0:
                this.m00 = floats[0];
                this.m01 = floats[1];
                this.m02 = floats[2];
                break;
            case 1:
                this.m10 = floats[0];
                this.m11 = floats[1];
                this.m12 = floats[2];
                break;
            case 2:
                this.m20 = floats[0];
                this.m21 = floats[1];
                this.m22 = floats[2];
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
        }
    }

    public final void setColumn(int int0, float float0, float float1, float float2) {
        switch (int0) {
            case 0:
                this.m00 = float0;
                this.m10 = float1;
                this.m20 = float2;
                break;
            case 1:
                this.m01 = float0;
                this.m11 = float1;
                this.m21 = float2;
                break;
            case 2:
                this.m02 = float0;
                this.m12 = float1;
                this.m22 = float2;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
        }
    }

    public final void setColumn(int int0, Vector3f vector3f) {
        switch (int0) {
            case 0:
                this.m00 = vector3f.x;
                this.m10 = vector3f.y;
                this.m20 = vector3f.z;
                break;
            case 1:
                this.m01 = vector3f.x;
                this.m11 = vector3f.y;
                this.m21 = vector3f.z;
                break;
            case 2:
                this.m02 = vector3f.x;
                this.m12 = vector3f.y;
                this.m22 = vector3f.z;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
        }
    }

    public final void setColumn(int int0, float[] floats) {
        switch (int0) {
            case 0:
                this.m00 = floats[0];
                this.m10 = floats[1];
                this.m20 = floats[2];
                break;
            case 1:
                this.m01 = floats[0];
                this.m11 = floats[1];
                this.m21 = floats[2];
                break;
            case 2:
                this.m02 = floats[0];
                this.m12 = floats[1];
                this.m22 = floats[2];
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
        }
    }

    public final float getScale() {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        return (float)Matrix3d.max3(doubles1);
    }

    public final void add(float float0) {
        this.m00 += float0;
        this.m01 += float0;
        this.m02 += float0;
        this.m10 += float0;
        this.m11 += float0;
        this.m12 += float0;
        this.m20 += float0;
        this.m21 += float0;
        this.m22 += float0;
    }

    public final void add(float float0, Matrix3f matrix3f0) {
        this.m00 = matrix3f0.m00 + float0;
        this.m01 = matrix3f0.m01 + float0;
        this.m02 = matrix3f0.m02 + float0;
        this.m10 = matrix3f0.m10 + float0;
        this.m11 = matrix3f0.m11 + float0;
        this.m12 = matrix3f0.m12 + float0;
        this.m20 = matrix3f0.m20 + float0;
        this.m21 = matrix3f0.m21 + float0;
        this.m22 = matrix3f0.m22 + float0;
    }

    public final void add(Matrix3f matrix3f1, Matrix3f matrix3f0) {
        this.m00 = matrix3f1.m00 + matrix3f0.m00;
        this.m01 = matrix3f1.m01 + matrix3f0.m01;
        this.m02 = matrix3f1.m02 + matrix3f0.m02;
        this.m10 = matrix3f1.m10 + matrix3f0.m10;
        this.m11 = matrix3f1.m11 + matrix3f0.m11;
        this.m12 = matrix3f1.m12 + matrix3f0.m12;
        this.m20 = matrix3f1.m20 + matrix3f0.m20;
        this.m21 = matrix3f1.m21 + matrix3f0.m21;
        this.m22 = matrix3f1.m22 + matrix3f0.m22;
    }

    public final void add(Matrix3f matrix3f0) {
        this.m00 = this.m00 + matrix3f0.m00;
        this.m01 = this.m01 + matrix3f0.m01;
        this.m02 = this.m02 + matrix3f0.m02;
        this.m10 = this.m10 + matrix3f0.m10;
        this.m11 = this.m11 + matrix3f0.m11;
        this.m12 = this.m12 + matrix3f0.m12;
        this.m20 = this.m20 + matrix3f0.m20;
        this.m21 = this.m21 + matrix3f0.m21;
        this.m22 = this.m22 + matrix3f0.m22;
    }

    public final void sub(Matrix3f matrix3f1, Matrix3f matrix3f0) {
        this.m00 = matrix3f1.m00 - matrix3f0.m00;
        this.m01 = matrix3f1.m01 - matrix3f0.m01;
        this.m02 = matrix3f1.m02 - matrix3f0.m02;
        this.m10 = matrix3f1.m10 - matrix3f0.m10;
        this.m11 = matrix3f1.m11 - matrix3f0.m11;
        this.m12 = matrix3f1.m12 - matrix3f0.m12;
        this.m20 = matrix3f1.m20 - matrix3f0.m20;
        this.m21 = matrix3f1.m21 - matrix3f0.m21;
        this.m22 = matrix3f1.m22 - matrix3f0.m22;
    }

    public final void sub(Matrix3f matrix3f0) {
        this.m00 = this.m00 - matrix3f0.m00;
        this.m01 = this.m01 - matrix3f0.m01;
        this.m02 = this.m02 - matrix3f0.m02;
        this.m10 = this.m10 - matrix3f0.m10;
        this.m11 = this.m11 - matrix3f0.m11;
        this.m12 = this.m12 - matrix3f0.m12;
        this.m20 = this.m20 - matrix3f0.m20;
        this.m21 = this.m21 - matrix3f0.m21;
        this.m22 = this.m22 - matrix3f0.m22;
    }

    public final void transpose() {
        float float0 = this.m10;
        this.m10 = this.m01;
        this.m01 = float0;
        float0 = this.m20;
        this.m20 = this.m02;
        this.m02 = float0;
        float0 = this.m21;
        this.m21 = this.m12;
        this.m12 = float0;
    }

    public final void transpose(Matrix3f matrix3f1) {
        if (this != matrix3f1) {
            this.m00 = matrix3f1.m00;
            this.m01 = matrix3f1.m10;
            this.m02 = matrix3f1.m20;
            this.m10 = matrix3f1.m01;
            this.m11 = matrix3f1.m11;
            this.m12 = matrix3f1.m21;
            this.m20 = matrix3f1.m02;
            this.m21 = matrix3f1.m12;
            this.m22 = matrix3f1.m22;
        } else {
            this.transpose();
        }
    }

    public final void set(Quat4f quat4f) {
        this.m00 = 1.0F - 2.0F * quat4f.y * quat4f.y - 2.0F * quat4f.z * quat4f.z;
        this.m10 = 2.0F * (quat4f.x * quat4f.y + quat4f.w * quat4f.z);
        this.m20 = 2.0F * (quat4f.x * quat4f.z - quat4f.w * quat4f.y);
        this.m01 = 2.0F * (quat4f.x * quat4f.y - quat4f.w * quat4f.z);
        this.m11 = 1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.z * quat4f.z;
        this.m21 = 2.0F * (quat4f.y * quat4f.z + quat4f.w * quat4f.x);
        this.m02 = 2.0F * (quat4f.x * quat4f.z + quat4f.w * quat4f.y);
        this.m12 = 2.0F * (quat4f.y * quat4f.z - quat4f.w * quat4f.x);
        this.m22 = 1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.y * quat4f.y;
    }

    public final void set(AxisAngle4f axisAngle4f) {
        float float0 = (float)Math.sqrt(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z);
        if (float0 < 1.0E-8) {
            this.m00 = 1.0F;
            this.m01 = 0.0F;
            this.m02 = 0.0F;
            this.m10 = 0.0F;
            this.m11 = 1.0F;
            this.m12 = 0.0F;
            this.m20 = 0.0F;
            this.m21 = 0.0F;
            this.m22 = 1.0F;
        } else {
            float0 = 1.0F / float0;
            float float1 = axisAngle4f.x * float0;
            float float2 = axisAngle4f.y * float0;
            float float3 = axisAngle4f.z * float0;
            float float4 = (float)Math.sin(axisAngle4f.angle);
            float float5 = (float)Math.cos(axisAngle4f.angle);
            float float6 = 1.0F - float5;
            float float7 = float1 * float3;
            float float8 = float1 * float2;
            float float9 = float2 * float3;
            this.m00 = float6 * float1 * float1 + float5;
            this.m01 = float6 * float8 - float4 * float3;
            this.m02 = float6 * float7 + float4 * float2;
            this.m10 = float6 * float8 + float4 * float3;
            this.m11 = float6 * float2 * float2 + float5;
            this.m12 = float6 * float9 - float4 * float1;
            this.m20 = float6 * float7 - float4 * float2;
            this.m21 = float6 * float9 + float4 * float1;
            this.m22 = float6 * float3 * float3 + float5;
        }
    }

    public final void set(AxisAngle4d axisAngle4d) {
        double double0 = Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
        if (double0 < 1.0E-8) {
            this.m00 = 1.0F;
            this.m01 = 0.0F;
            this.m02 = 0.0F;
            this.m10 = 0.0F;
            this.m11 = 1.0F;
            this.m12 = 0.0F;
            this.m20 = 0.0F;
            this.m21 = 0.0F;
            this.m22 = 1.0F;
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
            this.m00 = (float)(double6 * double1 * double1 + double5);
            this.m01 = (float)(double6 * double8 - double4 * double3);
            this.m02 = (float)(double6 * double7 + double4 * double2);
            this.m10 = (float)(double6 * double8 + double4 * double3);
            this.m11 = (float)(double6 * double2 * double2 + double5);
            this.m12 = (float)(double6 * double9 - double4 * double1);
            this.m20 = (float)(double6 * double7 - double4 * double2);
            this.m21 = (float)(double6 * double9 + double4 * double1);
            this.m22 = (float)(double6 * double3 * double3 + double5);
        }
    }

    public final void set(Quat4d quat4d) {
        this.m00 = (float)(1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z);
        this.m10 = (float)(2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z));
        this.m20 = (float)(2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y));
        this.m01 = (float)(2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z));
        this.m11 = (float)(1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z);
        this.m21 = (float)(2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x));
        this.m02 = (float)(2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y));
        this.m12 = (float)(2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x));
        this.m22 = (float)(1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y);
    }

    public final void set(float[] floats) {
        this.m00 = floats[0];
        this.m01 = floats[1];
        this.m02 = floats[2];
        this.m10 = floats[3];
        this.m11 = floats[4];
        this.m12 = floats[5];
        this.m20 = floats[6];
        this.m21 = floats[7];
        this.m22 = floats[8];
    }

    public final void set(Matrix3f matrix3f0) {
        this.m00 = matrix3f0.m00;
        this.m01 = matrix3f0.m01;
        this.m02 = matrix3f0.m02;
        this.m10 = matrix3f0.m10;
        this.m11 = matrix3f0.m11;
        this.m12 = matrix3f0.m12;
        this.m20 = matrix3f0.m20;
        this.m21 = matrix3f0.m21;
        this.m22 = matrix3f0.m22;
    }

    public final void set(Matrix3d matrix3d) {
        this.m00 = (float)matrix3d.m00;
        this.m01 = (float)matrix3d.m01;
        this.m02 = (float)matrix3d.m02;
        this.m10 = (float)matrix3d.m10;
        this.m11 = (float)matrix3d.m11;
        this.m12 = (float)matrix3d.m12;
        this.m20 = (float)matrix3d.m20;
        this.m21 = (float)matrix3d.m21;
        this.m22 = (float)matrix3d.m22;
    }

    public final void invert(Matrix3f matrix3f1) {
        this.invertGeneral(matrix3f1);
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    private final void invertGeneral(Matrix3f matrix3f0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        int[] ints = new int[3];
        doubles0[0] = matrix3f0.m00;
        doubles0[1] = matrix3f0.m01;
        doubles0[2] = matrix3f0.m02;
        doubles0[3] = matrix3f0.m10;
        doubles0[4] = matrix3f0.m11;
        doubles0[5] = matrix3f0.m12;
        doubles0[6] = matrix3f0.m20;
        doubles0[7] = matrix3f0.m21;
        doubles0[8] = matrix3f0.m22;
        if (!luDecomposition(doubles0, ints)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix3f12"));
        } else {
            for (int int0 = 0; int0 < 9; int0++) {
                doubles1[int0] = 0.0;
            }

            doubles1[0] = 1.0;
            doubles1[4] = 1.0;
            doubles1[8] = 1.0;
            luBacksubstitution(doubles0, ints, doubles1);
            this.m00 = (float)doubles1[0];
            this.m01 = (float)doubles1[1];
            this.m02 = (float)doubles1[2];
            this.m10 = (float)doubles1[3];
            this.m11 = (float)doubles1[4];
            this.m12 = (float)doubles1[5];
            this.m20 = (float)doubles1[6];
            this.m21 = (float)doubles1[7];
            this.m22 = (float)doubles1[8];
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
                throw new RuntimeException(VecMathI18N.getString("Matrix3f13"));
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

    public final float determinant() {
        return this.m00 * (this.m11 * this.m22 - this.m12 * this.m21)
            + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22)
            + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
    }

    public final void set(float float0) {
        this.m00 = float0;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = float0;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = float0;
    }

    public final void rotX(float float1) {
        float float0 = (float)Math.sin(float1);
        float float2 = (float)Math.cos(float1);
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = float2;
        this.m12 = -float0;
        this.m20 = 0.0F;
        this.m21 = float0;
        this.m22 = float2;
    }

    public final void rotY(float float1) {
        float float0 = (float)Math.sin(float1);
        float float2 = (float)Math.cos(float1);
        this.m00 = float2;
        this.m01 = 0.0F;
        this.m02 = float0;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m20 = -float0;
        this.m21 = 0.0F;
        this.m22 = float2;
    }

    public final void rotZ(float float1) {
        float float0 = (float)Math.sin(float1);
        float float2 = (float)Math.cos(float1);
        this.m00 = float2;
        this.m01 = -float0;
        this.m02 = 0.0F;
        this.m10 = float0;
        this.m11 = float2;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
    }

    public final void mul(float float0) {
        this.m00 *= float0;
        this.m01 *= float0;
        this.m02 *= float0;
        this.m10 *= float0;
        this.m11 *= float0;
        this.m12 *= float0;
        this.m20 *= float0;
        this.m21 *= float0;
        this.m22 *= float0;
    }

    public final void mul(float float0, Matrix3f matrix3f0) {
        this.m00 = float0 * matrix3f0.m00;
        this.m01 = float0 * matrix3f0.m01;
        this.m02 = float0 * matrix3f0.m02;
        this.m10 = float0 * matrix3f0.m10;
        this.m11 = float0 * matrix3f0.m11;
        this.m12 = float0 * matrix3f0.m12;
        this.m20 = float0 * matrix3f0.m20;
        this.m21 = float0 * matrix3f0.m21;
        this.m22 = float0 * matrix3f0.m22;
    }

    public final void mul(Matrix3f matrix3f0) {
        float float0 = this.m00 * matrix3f0.m00 + this.m01 * matrix3f0.m10 + this.m02 * matrix3f0.m20;
        float float1 = this.m00 * matrix3f0.m01 + this.m01 * matrix3f0.m11 + this.m02 * matrix3f0.m21;
        float float2 = this.m00 * matrix3f0.m02 + this.m01 * matrix3f0.m12 + this.m02 * matrix3f0.m22;
        float float3 = this.m10 * matrix3f0.m00 + this.m11 * matrix3f0.m10 + this.m12 * matrix3f0.m20;
        float float4 = this.m10 * matrix3f0.m01 + this.m11 * matrix3f0.m11 + this.m12 * matrix3f0.m21;
        float float5 = this.m10 * matrix3f0.m02 + this.m11 * matrix3f0.m12 + this.m12 * matrix3f0.m22;
        float float6 = this.m20 * matrix3f0.m00 + this.m21 * matrix3f0.m10 + this.m22 * matrix3f0.m20;
        float float7 = this.m20 * matrix3f0.m01 + this.m21 * matrix3f0.m11 + this.m22 * matrix3f0.m21;
        float float8 = this.m20 * matrix3f0.m02 + this.m21 * matrix3f0.m12 + this.m22 * matrix3f0.m22;
        this.m00 = float0;
        this.m01 = float1;
        this.m02 = float2;
        this.m10 = float3;
        this.m11 = float4;
        this.m12 = float5;
        this.m20 = float6;
        this.m21 = float7;
        this.m22 = float8;
    }

    public final void mul(Matrix3f matrix3f2, Matrix3f matrix3f1) {
        if (this != matrix3f2 && this != matrix3f1) {
            this.m00 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m01 * matrix3f1.m10 + matrix3f2.m02 * matrix3f1.m20;
            this.m01 = matrix3f2.m00 * matrix3f1.m01 + matrix3f2.m01 * matrix3f1.m11 + matrix3f2.m02 * matrix3f1.m21;
            this.m02 = matrix3f2.m00 * matrix3f1.m02 + matrix3f2.m01 * matrix3f1.m12 + matrix3f2.m02 * matrix3f1.m22;
            this.m10 = matrix3f2.m10 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m10 + matrix3f2.m12 * matrix3f1.m20;
            this.m11 = matrix3f2.m10 * matrix3f1.m01 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m12 * matrix3f1.m21;
            this.m12 = matrix3f2.m10 * matrix3f1.m02 + matrix3f2.m11 * matrix3f1.m12 + matrix3f2.m12 * matrix3f1.m22;
            this.m20 = matrix3f2.m20 * matrix3f1.m00 + matrix3f2.m21 * matrix3f1.m10 + matrix3f2.m22 * matrix3f1.m20;
            this.m21 = matrix3f2.m20 * matrix3f1.m01 + matrix3f2.m21 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m21;
            this.m22 = matrix3f2.m20 * matrix3f1.m02 + matrix3f2.m21 * matrix3f1.m12 + matrix3f2.m22 * matrix3f1.m22;
        } else {
            float float0 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m01 * matrix3f1.m10 + matrix3f2.m02 * matrix3f1.m20;
            float float1 = matrix3f2.m00 * matrix3f1.m01 + matrix3f2.m01 * matrix3f1.m11 + matrix3f2.m02 * matrix3f1.m21;
            float float2 = matrix3f2.m00 * matrix3f1.m02 + matrix3f2.m01 * matrix3f1.m12 + matrix3f2.m02 * matrix3f1.m22;
            float float3 = matrix3f2.m10 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m10 + matrix3f2.m12 * matrix3f1.m20;
            float float4 = matrix3f2.m10 * matrix3f1.m01 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m12 * matrix3f1.m21;
            float float5 = matrix3f2.m10 * matrix3f1.m02 + matrix3f2.m11 * matrix3f1.m12 + matrix3f2.m12 * matrix3f1.m22;
            float float6 = matrix3f2.m20 * matrix3f1.m00 + matrix3f2.m21 * matrix3f1.m10 + matrix3f2.m22 * matrix3f1.m20;
            float float7 = matrix3f2.m20 * matrix3f1.m01 + matrix3f2.m21 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m21;
            float float8 = matrix3f2.m20 * matrix3f1.m02 + matrix3f2.m21 * matrix3f1.m12 + matrix3f2.m22 * matrix3f1.m22;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m10 = float3;
            this.m11 = float4;
            this.m12 = float5;
            this.m20 = float6;
            this.m21 = float7;
            this.m22 = float8;
        }
    }

    public final void mulNormalize(Matrix3f matrix3f0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        double[] doubles2 = new double[3];
        doubles0[0] = this.m00 * matrix3f0.m00 + this.m01 * matrix3f0.m10 + this.m02 * matrix3f0.m20;
        doubles0[1] = this.m00 * matrix3f0.m01 + this.m01 * matrix3f0.m11 + this.m02 * matrix3f0.m21;
        doubles0[2] = this.m00 * matrix3f0.m02 + this.m01 * matrix3f0.m12 + this.m02 * matrix3f0.m22;
        doubles0[3] = this.m10 * matrix3f0.m00 + this.m11 * matrix3f0.m10 + this.m12 * matrix3f0.m20;
        doubles0[4] = this.m10 * matrix3f0.m01 + this.m11 * matrix3f0.m11 + this.m12 * matrix3f0.m21;
        doubles0[5] = this.m10 * matrix3f0.m02 + this.m11 * matrix3f0.m12 + this.m12 * matrix3f0.m22;
        doubles0[6] = this.m20 * matrix3f0.m00 + this.m21 * matrix3f0.m10 + this.m22 * matrix3f0.m20;
        doubles0[7] = this.m20 * matrix3f0.m01 + this.m21 * matrix3f0.m11 + this.m22 * matrix3f0.m21;
        doubles0[8] = this.m20 * matrix3f0.m02 + this.m21 * matrix3f0.m12 + this.m22 * matrix3f0.m22;
        Matrix3d.compute_svd(doubles0, doubles2, doubles1);
        this.m00 = (float)doubles1[0];
        this.m01 = (float)doubles1[1];
        this.m02 = (float)doubles1[2];
        this.m10 = (float)doubles1[3];
        this.m11 = (float)doubles1[4];
        this.m12 = (float)doubles1[5];
        this.m20 = (float)doubles1[6];
        this.m21 = (float)doubles1[7];
        this.m22 = (float)doubles1[8];
    }

    public final void mulNormalize(Matrix3f matrix3f1, Matrix3f matrix3f0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        double[] doubles2 = new double[3];
        doubles0[0] = matrix3f1.m00 * matrix3f0.m00 + matrix3f1.m01 * matrix3f0.m10 + matrix3f1.m02 * matrix3f0.m20;
        doubles0[1] = matrix3f1.m00 * matrix3f0.m01 + matrix3f1.m01 * matrix3f0.m11 + matrix3f1.m02 * matrix3f0.m21;
        doubles0[2] = matrix3f1.m00 * matrix3f0.m02 + matrix3f1.m01 * matrix3f0.m12 + matrix3f1.m02 * matrix3f0.m22;
        doubles0[3] = matrix3f1.m10 * matrix3f0.m00 + matrix3f1.m11 * matrix3f0.m10 + matrix3f1.m12 * matrix3f0.m20;
        doubles0[4] = matrix3f1.m10 * matrix3f0.m01 + matrix3f1.m11 * matrix3f0.m11 + matrix3f1.m12 * matrix3f0.m21;
        doubles0[5] = matrix3f1.m10 * matrix3f0.m02 + matrix3f1.m11 * matrix3f0.m12 + matrix3f1.m12 * matrix3f0.m22;
        doubles0[6] = matrix3f1.m20 * matrix3f0.m00 + matrix3f1.m21 * matrix3f0.m10 + matrix3f1.m22 * matrix3f0.m20;
        doubles0[7] = matrix3f1.m20 * matrix3f0.m01 + matrix3f1.m21 * matrix3f0.m11 + matrix3f1.m22 * matrix3f0.m21;
        doubles0[8] = matrix3f1.m20 * matrix3f0.m02 + matrix3f1.m21 * matrix3f0.m12 + matrix3f1.m22 * matrix3f0.m22;
        Matrix3d.compute_svd(doubles0, doubles2, doubles1);
        this.m00 = (float)doubles1[0];
        this.m01 = (float)doubles1[1];
        this.m02 = (float)doubles1[2];
        this.m10 = (float)doubles1[3];
        this.m11 = (float)doubles1[4];
        this.m12 = (float)doubles1[5];
        this.m20 = (float)doubles1[6];
        this.m21 = (float)doubles1[7];
        this.m22 = (float)doubles1[8];
    }

    public final void mulTransposeBoth(Matrix3f matrix3f2, Matrix3f matrix3f1) {
        if (this != matrix3f2 && this != matrix3f1) {
            this.m00 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m10 * matrix3f1.m01 + matrix3f2.m20 * matrix3f1.m02;
            this.m01 = matrix3f2.m00 * matrix3f1.m10 + matrix3f2.m10 * matrix3f1.m11 + matrix3f2.m20 * matrix3f1.m12;
            this.m02 = matrix3f2.m00 * matrix3f1.m20 + matrix3f2.m10 * matrix3f1.m21 + matrix3f2.m20 * matrix3f1.m22;
            this.m10 = matrix3f2.m01 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m01 + matrix3f2.m21 * matrix3f1.m02;
            this.m11 = matrix3f2.m01 * matrix3f1.m10 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m21 * matrix3f1.m12;
            this.m12 = matrix3f2.m01 * matrix3f1.m20 + matrix3f2.m11 * matrix3f1.m21 + matrix3f2.m21 * matrix3f1.m22;
            this.m20 = matrix3f2.m02 * matrix3f1.m00 + matrix3f2.m12 * matrix3f1.m01 + matrix3f2.m22 * matrix3f1.m02;
            this.m21 = matrix3f2.m02 * matrix3f1.m10 + matrix3f2.m12 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m12;
            this.m22 = matrix3f2.m02 * matrix3f1.m20 + matrix3f2.m12 * matrix3f1.m21 + matrix3f2.m22 * matrix3f1.m22;
        } else {
            float float0 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m10 * matrix3f1.m01 + matrix3f2.m20 * matrix3f1.m02;
            float float1 = matrix3f2.m00 * matrix3f1.m10 + matrix3f2.m10 * matrix3f1.m11 + matrix3f2.m20 * matrix3f1.m12;
            float float2 = matrix3f2.m00 * matrix3f1.m20 + matrix3f2.m10 * matrix3f1.m21 + matrix3f2.m20 * matrix3f1.m22;
            float float3 = matrix3f2.m01 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m01 + matrix3f2.m21 * matrix3f1.m02;
            float float4 = matrix3f2.m01 * matrix3f1.m10 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m21 * matrix3f1.m12;
            float float5 = matrix3f2.m01 * matrix3f1.m20 + matrix3f2.m11 * matrix3f1.m21 + matrix3f2.m21 * matrix3f1.m22;
            float float6 = matrix3f2.m02 * matrix3f1.m00 + matrix3f2.m12 * matrix3f1.m01 + matrix3f2.m22 * matrix3f1.m02;
            float float7 = matrix3f2.m02 * matrix3f1.m10 + matrix3f2.m12 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m12;
            float float8 = matrix3f2.m02 * matrix3f1.m20 + matrix3f2.m12 * matrix3f1.m21 + matrix3f2.m22 * matrix3f1.m22;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m10 = float3;
            this.m11 = float4;
            this.m12 = float5;
            this.m20 = float6;
            this.m21 = float7;
            this.m22 = float8;
        }
    }

    public final void mulTransposeRight(Matrix3f matrix3f2, Matrix3f matrix3f1) {
        if (this != matrix3f2 && this != matrix3f1) {
            this.m00 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m01 * matrix3f1.m01 + matrix3f2.m02 * matrix3f1.m02;
            this.m01 = matrix3f2.m00 * matrix3f1.m10 + matrix3f2.m01 * matrix3f1.m11 + matrix3f2.m02 * matrix3f1.m12;
            this.m02 = matrix3f2.m00 * matrix3f1.m20 + matrix3f2.m01 * matrix3f1.m21 + matrix3f2.m02 * matrix3f1.m22;
            this.m10 = matrix3f2.m10 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m01 + matrix3f2.m12 * matrix3f1.m02;
            this.m11 = matrix3f2.m10 * matrix3f1.m10 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m12 * matrix3f1.m12;
            this.m12 = matrix3f2.m10 * matrix3f1.m20 + matrix3f2.m11 * matrix3f1.m21 + matrix3f2.m12 * matrix3f1.m22;
            this.m20 = matrix3f2.m20 * matrix3f1.m00 + matrix3f2.m21 * matrix3f1.m01 + matrix3f2.m22 * matrix3f1.m02;
            this.m21 = matrix3f2.m20 * matrix3f1.m10 + matrix3f2.m21 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m12;
            this.m22 = matrix3f2.m20 * matrix3f1.m20 + matrix3f2.m21 * matrix3f1.m21 + matrix3f2.m22 * matrix3f1.m22;
        } else {
            float float0 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m01 * matrix3f1.m01 + matrix3f2.m02 * matrix3f1.m02;
            float float1 = matrix3f2.m00 * matrix3f1.m10 + matrix3f2.m01 * matrix3f1.m11 + matrix3f2.m02 * matrix3f1.m12;
            float float2 = matrix3f2.m00 * matrix3f1.m20 + matrix3f2.m01 * matrix3f1.m21 + matrix3f2.m02 * matrix3f1.m22;
            float float3 = matrix3f2.m10 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m01 + matrix3f2.m12 * matrix3f1.m02;
            float float4 = matrix3f2.m10 * matrix3f1.m10 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m12 * matrix3f1.m12;
            float float5 = matrix3f2.m10 * matrix3f1.m20 + matrix3f2.m11 * matrix3f1.m21 + matrix3f2.m12 * matrix3f1.m22;
            float float6 = matrix3f2.m20 * matrix3f1.m00 + matrix3f2.m21 * matrix3f1.m01 + matrix3f2.m22 * matrix3f1.m02;
            float float7 = matrix3f2.m20 * matrix3f1.m10 + matrix3f2.m21 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m12;
            float float8 = matrix3f2.m20 * matrix3f1.m20 + matrix3f2.m21 * matrix3f1.m21 + matrix3f2.m22 * matrix3f1.m22;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m10 = float3;
            this.m11 = float4;
            this.m12 = float5;
            this.m20 = float6;
            this.m21 = float7;
            this.m22 = float8;
        }
    }

    public final void mulTransposeLeft(Matrix3f matrix3f2, Matrix3f matrix3f1) {
        if (this != matrix3f2 && this != matrix3f1) {
            this.m00 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m10 * matrix3f1.m10 + matrix3f2.m20 * matrix3f1.m20;
            this.m01 = matrix3f2.m00 * matrix3f1.m01 + matrix3f2.m10 * matrix3f1.m11 + matrix3f2.m20 * matrix3f1.m21;
            this.m02 = matrix3f2.m00 * matrix3f1.m02 + matrix3f2.m10 * matrix3f1.m12 + matrix3f2.m20 * matrix3f1.m22;
            this.m10 = matrix3f2.m01 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m10 + matrix3f2.m21 * matrix3f1.m20;
            this.m11 = matrix3f2.m01 * matrix3f1.m01 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m21 * matrix3f1.m21;
            this.m12 = matrix3f2.m01 * matrix3f1.m02 + matrix3f2.m11 * matrix3f1.m12 + matrix3f2.m21 * matrix3f1.m22;
            this.m20 = matrix3f2.m02 * matrix3f1.m00 + matrix3f2.m12 * matrix3f1.m10 + matrix3f2.m22 * matrix3f1.m20;
            this.m21 = matrix3f2.m02 * matrix3f1.m01 + matrix3f2.m12 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m21;
            this.m22 = matrix3f2.m02 * matrix3f1.m02 + matrix3f2.m12 * matrix3f1.m12 + matrix3f2.m22 * matrix3f1.m22;
        } else {
            float float0 = matrix3f2.m00 * matrix3f1.m00 + matrix3f2.m10 * matrix3f1.m10 + matrix3f2.m20 * matrix3f1.m20;
            float float1 = matrix3f2.m00 * matrix3f1.m01 + matrix3f2.m10 * matrix3f1.m11 + matrix3f2.m20 * matrix3f1.m21;
            float float2 = matrix3f2.m00 * matrix3f1.m02 + matrix3f2.m10 * matrix3f1.m12 + matrix3f2.m20 * matrix3f1.m22;
            float float3 = matrix3f2.m01 * matrix3f1.m00 + matrix3f2.m11 * matrix3f1.m10 + matrix3f2.m21 * matrix3f1.m20;
            float float4 = matrix3f2.m01 * matrix3f1.m01 + matrix3f2.m11 * matrix3f1.m11 + matrix3f2.m21 * matrix3f1.m21;
            float float5 = matrix3f2.m01 * matrix3f1.m02 + matrix3f2.m11 * matrix3f1.m12 + matrix3f2.m21 * matrix3f1.m22;
            float float6 = matrix3f2.m02 * matrix3f1.m00 + matrix3f2.m12 * matrix3f1.m10 + matrix3f2.m22 * matrix3f1.m20;
            float float7 = matrix3f2.m02 * matrix3f1.m01 + matrix3f2.m12 * matrix3f1.m11 + matrix3f2.m22 * matrix3f1.m21;
            float float8 = matrix3f2.m02 * matrix3f1.m02 + matrix3f2.m12 * matrix3f1.m12 + matrix3f2.m22 * matrix3f1.m22;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m10 = float3;
            this.m11 = float4;
            this.m12 = float5;
            this.m20 = float6;
            this.m21 = float7;
            this.m22 = float8;
        }
    }

    public final void normalize() {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = (float)doubles0[0];
        this.m01 = (float)doubles0[1];
        this.m02 = (float)doubles0[2];
        this.m10 = (float)doubles0[3];
        this.m11 = (float)doubles0[4];
        this.m12 = (float)doubles0[5];
        this.m20 = (float)doubles0[6];
        this.m21 = (float)doubles0[7];
        this.m22 = (float)doubles0[8];
    }

    public final void normalize(Matrix3f matrix3f0) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[9];
        double[] doubles2 = new double[3];
        doubles0[0] = matrix3f0.m00;
        doubles0[1] = matrix3f0.m01;
        doubles0[2] = matrix3f0.m02;
        doubles0[3] = matrix3f0.m10;
        doubles0[4] = matrix3f0.m11;
        doubles0[5] = matrix3f0.m12;
        doubles0[6] = matrix3f0.m20;
        doubles0[7] = matrix3f0.m21;
        doubles0[8] = matrix3f0.m22;
        Matrix3d.compute_svd(doubles0, doubles2, doubles1);
        this.m00 = (float)doubles1[0];
        this.m01 = (float)doubles1[1];
        this.m02 = (float)doubles1[2];
        this.m10 = (float)doubles1[3];
        this.m11 = (float)doubles1[4];
        this.m12 = (float)doubles1[5];
        this.m20 = (float)doubles1[6];
        this.m21 = (float)doubles1[7];
        this.m22 = (float)doubles1[8];
    }

    public final void normalizeCP() {
        float float0 = 1.0F / (float)Math.sqrt(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20);
        this.m00 *= float0;
        this.m10 *= float0;
        this.m20 *= float0;
        float0 = 1.0F / (float)Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        this.m01 *= float0;
        this.m11 *= float0;
        this.m21 *= float0;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public final void normalizeCP(Matrix3f matrix3f0) {
        float float0 = 1.0F / (float)Math.sqrt(matrix3f0.m00 * matrix3f0.m00 + matrix3f0.m10 * matrix3f0.m10 + matrix3f0.m20 * matrix3f0.m20);
        this.m00 = matrix3f0.m00 * float0;
        this.m10 = matrix3f0.m10 * float0;
        this.m20 = matrix3f0.m20 * float0;
        float0 = 1.0F / (float)Math.sqrt(matrix3f0.m01 * matrix3f0.m01 + matrix3f0.m11 * matrix3f0.m11 + matrix3f0.m21 * matrix3f0.m21);
        this.m01 = matrix3f0.m01 * float0;
        this.m11 = matrix3f0.m11 * float0;
        this.m21 = matrix3f0.m21 * float0;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public boolean equals(Matrix3f matrix3f0) {
        try {
            return this.m00 == matrix3f0.m00
                && this.m01 == matrix3f0.m01
                && this.m02 == matrix3f0.m02
                && this.m10 == matrix3f0.m10
                && this.m11 == matrix3f0.m11
                && this.m12 == matrix3f0.m12
                && this.m20 == matrix3f0.m20
                && this.m21 == matrix3f0.m21
                && this.m22 == matrix3f0.m22;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Matrix3f matrix3f0 = (Matrix3f)object;
            return this.m00 == matrix3f0.m00
                && this.m01 == matrix3f0.m01
                && this.m02 == matrix3f0.m02
                && this.m10 == matrix3f0.m10
                && this.m11 == matrix3f0.m11
                && this.m12 == matrix3f0.m12
                && this.m20 == matrix3f0.m20
                && this.m21 == matrix3f0.m21
                && this.m22 == matrix3f0.m22;
        } catch (ClassCastException classCastException) {
            return false;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    public boolean epsilonEquals(Matrix3f matrix3f0, float float0) {
        boolean boolean0 = true;
        if (Math.abs(this.m00 - matrix3f0.m00) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m01 - matrix3f0.m01) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m02 - matrix3f0.m02) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m10 - matrix3f0.m10) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m11 - matrix3f0.m11) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m12 - matrix3f0.m12) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m20 - matrix3f0.m20) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m21 - matrix3f0.m21) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m22 - matrix3f0.m22) > float0) {
            boolean0 = false;
        }

        return boolean0;
    }

    @Override
    public int hashCode() {
        long long0 = 1L;
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m00);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m01);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m02);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m10);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m11);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m12);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m20);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m21);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m22);
        return (int)(long0 ^ long0 >> 32);
    }

    public final void setZero() {
        this.m00 = 0.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 0.0F;
        this.m12 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 0.0F;
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

    public final void negate(Matrix3f matrix3f0) {
        this.m00 = -matrix3f0.m00;
        this.m01 = -matrix3f0.m01;
        this.m02 = -matrix3f0.m02;
        this.m10 = -matrix3f0.m10;
        this.m11 = -matrix3f0.m11;
        this.m12 = -matrix3f0.m12;
        this.m20 = -matrix3f0.m20;
        this.m21 = -matrix3f0.m21;
        this.m22 = -matrix3f0.m22;
    }

    public final void transform(Tuple3f tuple3f) {
        float float0 = this.m00 * tuple3f.x + this.m01 * tuple3f.y + this.m02 * tuple3f.z;
        float float1 = this.m10 * tuple3f.x + this.m11 * tuple3f.y + this.m12 * tuple3f.z;
        float float2 = this.m20 * tuple3f.x + this.m21 * tuple3f.y + this.m22 * tuple3f.z;
        tuple3f.set(float0, float1, float2);
    }

    public final void transform(Tuple3f tuple3f0, Tuple3f tuple3f1) {
        float float0 = this.m00 * tuple3f0.x + this.m01 * tuple3f0.y + this.m02 * tuple3f0.z;
        float float1 = this.m10 * tuple3f0.x + this.m11 * tuple3f0.y + this.m12 * tuple3f0.z;
        tuple3f1.z = this.m20 * tuple3f0.x + this.m21 * tuple3f0.y + this.m22 * tuple3f0.z;
        tuple3f1.x = float0;
        tuple3f1.y = float1;
    }

    void getScaleRotate(double[] doubles1, double[] doubles2) {
        double[] doubles0 = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
        Matrix3d.compute_svd(doubles0, doubles1, doubles2);
    }

    @Override
    public Object clone() {
        Object object = null;

        try {
            return (Matrix3f)super.clone();
        } catch (CloneNotSupportedException cloneNotSupportedException) {
            throw new InternalError();
        }
    }

    public final float getM00() {
        return this.m00;
    }

    public final void setM00(float float0) {
        this.m00 = float0;
    }

    public final float getM01() {
        return this.m01;
    }

    public final void setM01(float float0) {
        this.m01 = float0;
    }

    public final float getM02() {
        return this.m02;
    }

    public final void setM02(float float0) {
        this.m02 = float0;
    }

    public final float getM10() {
        return this.m10;
    }

    public final void setM10(float float0) {
        this.m10 = float0;
    }

    public final float getM11() {
        return this.m11;
    }

    public final void setM11(float float0) {
        this.m11 = float0;
    }

    public final float getM12() {
        return this.m12;
    }

    public final void setM12(float float0) {
        this.m12 = float0;
    }

    public final float getM20() {
        return this.m20;
    }

    public final void setM20(float float0) {
        this.m20 = float0;
    }

    public final float getM21() {
        return this.m21;
    }

    public final void setM21(float float0) {
        this.m21 = float0;
    }

    public final float getM22() {
        return this.m22;
    }

    public final void setM22(float float0) {
        this.m22 = float0;
    }
}
