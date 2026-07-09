// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Matrix4f implements Serializable, Cloneable {
    static final long serialVersionUID = -8405036035410109353L;
    public float m00;
    public float m01;
    public float m02;
    public float m03;
    public float m10;
    public float m11;
    public float m12;
    public float m13;
    public float m20;
    public float m21;
    public float m22;
    public float m23;
    public float m30;
    public float m31;
    public float m32;
    public float m33;
    private static final double EPS = 1.0E-8;

    public Matrix4f(
        float float0,
        float float1,
        float float2,
        float float3,
        float float4,
        float float5,
        float float6,
        float float7,
        float float8,
        float float9,
        float float10,
        float float11,
        float float12,
        float float13,
        float float14,
        float float15
    ) {
        this.m00 = float0;
        this.m01 = float1;
        this.m02 = float2;
        this.m03 = float3;
        this.m10 = float4;
        this.m11 = float5;
        this.m12 = float6;
        this.m13 = float7;
        this.m20 = float8;
        this.m21 = float9;
        this.m22 = float10;
        this.m23 = float11;
        this.m30 = float12;
        this.m31 = float13;
        this.m32 = float14;
        this.m33 = float15;
    }

    public Matrix4f(float[] floats) {
        this.m00 = floats[0];
        this.m01 = floats[1];
        this.m02 = floats[2];
        this.m03 = floats[3];
        this.m10 = floats[4];
        this.m11 = floats[5];
        this.m12 = floats[6];
        this.m13 = floats[7];
        this.m20 = floats[8];
        this.m21 = floats[9];
        this.m22 = floats[10];
        this.m23 = floats[11];
        this.m30 = floats[12];
        this.m31 = floats[13];
        this.m32 = floats[14];
        this.m33 = floats[15];
    }

    public Matrix4f(Quat4f quat4f, Vector3f vector3f, float float0) {
        this.m00 = (float)(float0 * (1.0 - 2.0 * quat4f.y * quat4f.y - 2.0 * quat4f.z * quat4f.z));
        this.m10 = (float)(float0 * (2.0 * (quat4f.x * quat4f.y + quat4f.w * quat4f.z)));
        this.m20 = (float)(float0 * (2.0 * (quat4f.x * quat4f.z - quat4f.w * quat4f.y)));
        this.m01 = (float)(float0 * (2.0 * (quat4f.x * quat4f.y - quat4f.w * quat4f.z)));
        this.m11 = (float)(float0 * (1.0 - 2.0 * quat4f.x * quat4f.x - 2.0 * quat4f.z * quat4f.z));
        this.m21 = (float)(float0 * (2.0 * (quat4f.y * quat4f.z + quat4f.w * quat4f.x)));
        this.m02 = (float)(float0 * (2.0 * (quat4f.x * quat4f.z + quat4f.w * quat4f.y)));
        this.m12 = (float)(float0 * (2.0 * (quat4f.y * quat4f.z - quat4f.w * quat4f.x)));
        this.m22 = (float)(float0 * (1.0 - 2.0 * quat4f.x * quat4f.x - 2.0 * quat4f.y * quat4f.y));
        this.m03 = vector3f.x;
        this.m13 = vector3f.y;
        this.m23 = vector3f.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public Matrix4f(Matrix4d matrix4d) {
        this.m00 = (float)matrix4d.m00;
        this.m01 = (float)matrix4d.m01;
        this.m02 = (float)matrix4d.m02;
        this.m03 = (float)matrix4d.m03;
        this.m10 = (float)matrix4d.m10;
        this.m11 = (float)matrix4d.m11;
        this.m12 = (float)matrix4d.m12;
        this.m13 = (float)matrix4d.m13;
        this.m20 = (float)matrix4d.m20;
        this.m21 = (float)matrix4d.m21;
        this.m22 = (float)matrix4d.m22;
        this.m23 = (float)matrix4d.m23;
        this.m30 = (float)matrix4d.m30;
        this.m31 = (float)matrix4d.m31;
        this.m32 = (float)matrix4d.m32;
        this.m33 = (float)matrix4d.m33;
    }

    public Matrix4f(Matrix4f matrix4f1) {
        this.m00 = matrix4f1.m00;
        this.m01 = matrix4f1.m01;
        this.m02 = matrix4f1.m02;
        this.m03 = matrix4f1.m03;
        this.m10 = matrix4f1.m10;
        this.m11 = matrix4f1.m11;
        this.m12 = matrix4f1.m12;
        this.m13 = matrix4f1.m13;
        this.m20 = matrix4f1.m20;
        this.m21 = matrix4f1.m21;
        this.m22 = matrix4f1.m22;
        this.m23 = matrix4f1.m23;
        this.m30 = matrix4f1.m30;
        this.m31 = matrix4f1.m31;
        this.m32 = matrix4f1.m32;
        this.m33 = matrix4f1.m33;
    }

    public Matrix4f(Matrix3f matrix3f, Vector3f vector3f, float float0) {
        this.m00 = matrix3f.m00 * float0;
        this.m01 = matrix3f.m01 * float0;
        this.m02 = matrix3f.m02 * float0;
        this.m03 = vector3f.x;
        this.m10 = matrix3f.m10 * float0;
        this.m11 = matrix3f.m11 * float0;
        this.m12 = matrix3f.m12 * float0;
        this.m13 = vector3f.y;
        this.m20 = matrix3f.m20 * float0;
        this.m21 = matrix3f.m21 * float0;
        this.m22 = matrix3f.m22 * float0;
        this.m23 = vector3f.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public Matrix4f() {
        this.m00 = 0.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 0.0F;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 0.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 0.0F;
    }

    @Override
    public String toString() {
        return this.m00
            + ", "
            + this.m01
            + ", "
            + this.m02
            + ", "
            + this.m03
            + "\n"
            + this.m10
            + ", "
            + this.m11
            + ", "
            + this.m12
            + ", "
            + this.m13
            + "\n"
            + this.m20
            + ", "
            + this.m21
            + ", "
            + this.m22
            + ", "
            + this.m23
            + "\n"
            + this.m30
            + ", "
            + this.m31
            + ", "
            + this.m32
            + ", "
            + this.m33
            + "\n";
    }

    public final void setIdentity() {
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
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
                    case 3:
                        this.m03 = float0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
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
                    case 3:
                        this.m13 = float0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
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
                    case 3:
                        this.m23 = float0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
                }
            case 3:
                switch (int1) {
                    case 0:
                        this.m30 = float0;
                        return;
                    case 1:
                        this.m31 = float0;
                        return;
                    case 2:
                        this.m32 = float0;
                        return;
                    case 3:
                        this.m33 = float0;
                        return;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
                }
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f0"));
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
                    case 3:
                        return this.m03;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
                }
            case 1:
                switch (int1) {
                    case 0:
                        return this.m10;
                    case 1:
                        return this.m11;
                    case 2:
                        return this.m12;
                    case 3:
                        return this.m13;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
                }
            case 2:
                switch (int1) {
                    case 0:
                        return this.m20;
                    case 1:
                        return this.m21;
                    case 2:
                        return this.m22;
                    case 3:
                        return this.m23;
                    default:
                        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
                }
            case 3:
                switch (int1) {
                    case 0:
                        return this.m30;
                    case 1:
                        return this.m31;
                    case 2:
                        return this.m32;
                    case 3:
                        return this.m33;
                }
        }

        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f1"));
    }

    public final void getRow(int int0, Vector4f vector4f) {
        if (int0 == 0) {
            vector4f.x = this.m00;
            vector4f.y = this.m01;
            vector4f.z = this.m02;
            vector4f.w = this.m03;
        } else if (int0 == 1) {
            vector4f.x = this.m10;
            vector4f.y = this.m11;
            vector4f.z = this.m12;
            vector4f.w = this.m13;
        } else if (int0 == 2) {
            vector4f.x = this.m20;
            vector4f.y = this.m21;
            vector4f.z = this.m22;
            vector4f.w = this.m23;
        } else {
            if (int0 != 3) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f2"));
            }

            vector4f.x = this.m30;
            vector4f.y = this.m31;
            vector4f.z = this.m32;
            vector4f.w = this.m33;
        }
    }

    public final void getRow(int int0, float[] floats) {
        if (int0 == 0) {
            floats[0] = this.m00;
            floats[1] = this.m01;
            floats[2] = this.m02;
            floats[3] = this.m03;
        } else if (int0 == 1) {
            floats[0] = this.m10;
            floats[1] = this.m11;
            floats[2] = this.m12;
            floats[3] = this.m13;
        } else if (int0 == 2) {
            floats[0] = this.m20;
            floats[1] = this.m21;
            floats[2] = this.m22;
            floats[3] = this.m23;
        } else {
            if (int0 != 3) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f2"));
            }

            floats[0] = this.m30;
            floats[1] = this.m31;
            floats[2] = this.m32;
            floats[3] = this.m33;
        }
    }

    public final void getColumn(int int0, Vector4f vector4f) {
        if (int0 == 0) {
            vector4f.x = this.m00;
            vector4f.y = this.m10;
            vector4f.z = this.m20;
            vector4f.w = this.m30;
        } else if (int0 == 1) {
            vector4f.x = this.m01;
            vector4f.y = this.m11;
            vector4f.z = this.m21;
            vector4f.w = this.m31;
        } else if (int0 == 2) {
            vector4f.x = this.m02;
            vector4f.y = this.m12;
            vector4f.z = this.m22;
            vector4f.w = this.m32;
        } else {
            if (int0 != 3) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f4"));
            }

            vector4f.x = this.m03;
            vector4f.y = this.m13;
            vector4f.z = this.m23;
            vector4f.w = this.m33;
        }
    }

    public final void getColumn(int int0, float[] floats) {
        if (int0 == 0) {
            floats[0] = this.m00;
            floats[1] = this.m10;
            floats[2] = this.m20;
            floats[3] = this.m30;
        } else if (int0 == 1) {
            floats[0] = this.m01;
            floats[1] = this.m11;
            floats[2] = this.m21;
            floats[3] = this.m31;
        } else if (int0 == 2) {
            floats[0] = this.m02;
            floats[1] = this.m12;
            floats[2] = this.m22;
            floats[3] = this.m32;
        } else {
            if (int0 != 3) {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f4"));
            }

            floats[0] = this.m03;
            floats[1] = this.m13;
            floats[2] = this.m23;
            floats[3] = this.m33;
        }
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

    public final void get(Matrix3d matrix3d) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        matrix3d.m00 = doubles0[0];
        matrix3d.m01 = doubles0[1];
        matrix3d.m02 = doubles0[2];
        matrix3d.m10 = doubles0[3];
        matrix3d.m11 = doubles0[4];
        matrix3d.m12 = doubles0[5];
        matrix3d.m20 = doubles0[6];
        matrix3d.m21 = doubles0[7];
        matrix3d.m22 = doubles0[8];
    }

    public final void get(Matrix3f matrix3f) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        matrix3f.m00 = (float)doubles0[0];
        matrix3f.m01 = (float)doubles0[1];
        matrix3f.m02 = (float)doubles0[2];
        matrix3f.m10 = (float)doubles0[3];
        matrix3f.m11 = (float)doubles0[4];
        matrix3f.m12 = (float)doubles0[5];
        matrix3f.m20 = (float)doubles0[6];
        matrix3f.m21 = (float)doubles0[7];
        matrix3f.m22 = (float)doubles0[8];
    }

    public final float get(Matrix3f matrix3f, Vector3f vector3f) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        matrix3f.m00 = (float)doubles0[0];
        matrix3f.m01 = (float)doubles0[1];
        matrix3f.m02 = (float)doubles0[2];
        matrix3f.m10 = (float)doubles0[3];
        matrix3f.m11 = (float)doubles0[4];
        matrix3f.m12 = (float)doubles0[5];
        matrix3f.m20 = (float)doubles0[6];
        matrix3f.m21 = (float)doubles0[7];
        matrix3f.m22 = (float)doubles0[8];
        vector3f.x = this.m03;
        vector3f.y = this.m13;
        vector3f.z = this.m23;
        return (float)Matrix3d.max3(doubles1);
    }

    public final void get(Quat4f quat4f) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        double double0 = 0.25 * (1.0 + doubles0[0] + doubles0[4] + doubles0[8]);
        if (!((double0 < 0.0 ? -double0 : double0) < 1.0E-30)) {
            quat4f.w = (float)Math.sqrt(double0);
            double0 = 0.25 / quat4f.w;
            quat4f.x = (float)((doubles0[7] - doubles0[5]) * double0);
            quat4f.y = (float)((doubles0[2] - doubles0[6]) * double0);
            quat4f.z = (float)((doubles0[3] - doubles0[1]) * double0);
        } else {
            quat4f.w = 0.0F;
            double0 = -0.5 * (doubles0[4] + doubles0[8]);
            if (!((double0 < 0.0 ? -double0 : double0) < 1.0E-30)) {
                quat4f.x = (float)Math.sqrt(double0);
                double0 = 0.5 / quat4f.x;
                quat4f.y = (float)(doubles0[3] * double0);
                quat4f.z = (float)(doubles0[6] * double0);
            } else {
                quat4f.x = 0.0F;
                double0 = 0.5 * (1.0 - doubles0[8]);
                if (!((double0 < 0.0 ? -double0 : double0) < 1.0E-30)) {
                    quat4f.y = (float)Math.sqrt(double0);
                    quat4f.z = (float)(doubles0[7] / (2.0 * quat4f.y));
                } else {
                    quat4f.y = 0.0F;
                    quat4f.z = 1.0F;
                }
            }
        }
    }

    public final void get(Vector3f vector3f) {
        vector3f.x = this.m03;
        vector3f.y = this.m13;
        vector3f.z = this.m23;
    }

    public final void getRotationScale(Matrix3f matrix3f) {
        matrix3f.m00 = this.m00;
        matrix3f.m01 = this.m01;
        matrix3f.m02 = this.m02;
        matrix3f.m10 = this.m10;
        matrix3f.m11 = this.m11;
        matrix3f.m12 = this.m12;
        matrix3f.m20 = this.m20;
        matrix3f.m21 = this.m21;
        matrix3f.m22 = this.m22;
    }

    public final float getScale() {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        return (float)Matrix3d.max3(doubles1);
    }

    public final void setRotationScale(Matrix3f matrix3f) {
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

    public final void setRow(int int0, float float0, float float1, float float2, float float3) {
        switch (int0) {
            case 0:
                this.m00 = float0;
                this.m01 = float1;
                this.m02 = float2;
                this.m03 = float3;
                break;
            case 1:
                this.m10 = float0;
                this.m11 = float1;
                this.m12 = float2;
                this.m13 = float3;
                break;
            case 2:
                this.m20 = float0;
                this.m21 = float1;
                this.m22 = float2;
                this.m23 = float3;
                break;
            case 3:
                this.m30 = float0;
                this.m31 = float1;
                this.m32 = float2;
                this.m33 = float3;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
        }
    }

    public final void setRow(int int0, Vector4f vector4f) {
        switch (int0) {
            case 0:
                this.m00 = vector4f.x;
                this.m01 = vector4f.y;
                this.m02 = vector4f.z;
                this.m03 = vector4f.w;
                break;
            case 1:
                this.m10 = vector4f.x;
                this.m11 = vector4f.y;
                this.m12 = vector4f.z;
                this.m13 = vector4f.w;
                break;
            case 2:
                this.m20 = vector4f.x;
                this.m21 = vector4f.y;
                this.m22 = vector4f.z;
                this.m23 = vector4f.w;
                break;
            case 3:
                this.m30 = vector4f.x;
                this.m31 = vector4f.y;
                this.m32 = vector4f.z;
                this.m33 = vector4f.w;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
        }
    }

    public final void setRow(int int0, float[] floats) {
        switch (int0) {
            case 0:
                this.m00 = floats[0];
                this.m01 = floats[1];
                this.m02 = floats[2];
                this.m03 = floats[3];
                break;
            case 1:
                this.m10 = floats[0];
                this.m11 = floats[1];
                this.m12 = floats[2];
                this.m13 = floats[3];
                break;
            case 2:
                this.m20 = floats[0];
                this.m21 = floats[1];
                this.m22 = floats[2];
                this.m23 = floats[3];
                break;
            case 3:
                this.m30 = floats[0];
                this.m31 = floats[1];
                this.m32 = floats[2];
                this.m33 = floats[3];
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f6"));
        }
    }

    public final void setColumn(int int0, float float0, float float1, float float2, float float3) {
        switch (int0) {
            case 0:
                this.m00 = float0;
                this.m10 = float1;
                this.m20 = float2;
                this.m30 = float3;
                break;
            case 1:
                this.m01 = float0;
                this.m11 = float1;
                this.m21 = float2;
                this.m31 = float3;
                break;
            case 2:
                this.m02 = float0;
                this.m12 = float1;
                this.m22 = float2;
                this.m32 = float3;
                break;
            case 3:
                this.m03 = float0;
                this.m13 = float1;
                this.m23 = float2;
                this.m33 = float3;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
        }
    }

    public final void setColumn(int int0, Vector4f vector4f) {
        switch (int0) {
            case 0:
                this.m00 = vector4f.x;
                this.m10 = vector4f.y;
                this.m20 = vector4f.z;
                this.m30 = vector4f.w;
                break;
            case 1:
                this.m01 = vector4f.x;
                this.m11 = vector4f.y;
                this.m21 = vector4f.z;
                this.m31 = vector4f.w;
                break;
            case 2:
                this.m02 = vector4f.x;
                this.m12 = vector4f.y;
                this.m22 = vector4f.z;
                this.m32 = vector4f.w;
                break;
            case 3:
                this.m03 = vector4f.x;
                this.m13 = vector4f.y;
                this.m23 = vector4f.z;
                this.m33 = vector4f.w;
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
        }
    }

    public final void setColumn(int int0, float[] floats) {
        switch (int0) {
            case 0:
                this.m00 = floats[0];
                this.m10 = floats[1];
                this.m20 = floats[2];
                this.m30 = floats[3];
                break;
            case 1:
                this.m01 = floats[0];
                this.m11 = floats[1];
                this.m21 = floats[2];
                this.m31 = floats[3];
                break;
            case 2:
                this.m02 = floats[0];
                this.m12 = floats[1];
                this.m22 = floats[2];
                this.m32 = floats[3];
                break;
            case 3:
                this.m03 = floats[0];
                this.m13 = floats[1];
                this.m23 = floats[2];
                this.m33 = floats[3];
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4f9"));
        }
    }

    public final void add(float float0) {
        this.m00 += float0;
        this.m01 += float0;
        this.m02 += float0;
        this.m03 += float0;
        this.m10 += float0;
        this.m11 += float0;
        this.m12 += float0;
        this.m13 += float0;
        this.m20 += float0;
        this.m21 += float0;
        this.m22 += float0;
        this.m23 += float0;
        this.m30 += float0;
        this.m31 += float0;
        this.m32 += float0;
        this.m33 += float0;
    }

    public final void add(float float0, Matrix4f matrix4f0) {
        this.m00 = matrix4f0.m00 + float0;
        this.m01 = matrix4f0.m01 + float0;
        this.m02 = matrix4f0.m02 + float0;
        this.m03 = matrix4f0.m03 + float0;
        this.m10 = matrix4f0.m10 + float0;
        this.m11 = matrix4f0.m11 + float0;
        this.m12 = matrix4f0.m12 + float0;
        this.m13 = matrix4f0.m13 + float0;
        this.m20 = matrix4f0.m20 + float0;
        this.m21 = matrix4f0.m21 + float0;
        this.m22 = matrix4f0.m22 + float0;
        this.m23 = matrix4f0.m23 + float0;
        this.m30 = matrix4f0.m30 + float0;
        this.m31 = matrix4f0.m31 + float0;
        this.m32 = matrix4f0.m32 + float0;
        this.m33 = matrix4f0.m33 + float0;
    }

    public final void add(Matrix4f matrix4f1, Matrix4f matrix4f0) {
        this.m00 = matrix4f1.m00 + matrix4f0.m00;
        this.m01 = matrix4f1.m01 + matrix4f0.m01;
        this.m02 = matrix4f1.m02 + matrix4f0.m02;
        this.m03 = matrix4f1.m03 + matrix4f0.m03;
        this.m10 = matrix4f1.m10 + matrix4f0.m10;
        this.m11 = matrix4f1.m11 + matrix4f0.m11;
        this.m12 = matrix4f1.m12 + matrix4f0.m12;
        this.m13 = matrix4f1.m13 + matrix4f0.m13;
        this.m20 = matrix4f1.m20 + matrix4f0.m20;
        this.m21 = matrix4f1.m21 + matrix4f0.m21;
        this.m22 = matrix4f1.m22 + matrix4f0.m22;
        this.m23 = matrix4f1.m23 + matrix4f0.m23;
        this.m30 = matrix4f1.m30 + matrix4f0.m30;
        this.m31 = matrix4f1.m31 + matrix4f0.m31;
        this.m32 = matrix4f1.m32 + matrix4f0.m32;
        this.m33 = matrix4f1.m33 + matrix4f0.m33;
    }

    public final void add(Matrix4f matrix4f0) {
        this.m00 = this.m00 + matrix4f0.m00;
        this.m01 = this.m01 + matrix4f0.m01;
        this.m02 = this.m02 + matrix4f0.m02;
        this.m03 = this.m03 + matrix4f0.m03;
        this.m10 = this.m10 + matrix4f0.m10;
        this.m11 = this.m11 + matrix4f0.m11;
        this.m12 = this.m12 + matrix4f0.m12;
        this.m13 = this.m13 + matrix4f0.m13;
        this.m20 = this.m20 + matrix4f0.m20;
        this.m21 = this.m21 + matrix4f0.m21;
        this.m22 = this.m22 + matrix4f0.m22;
        this.m23 = this.m23 + matrix4f0.m23;
        this.m30 = this.m30 + matrix4f0.m30;
        this.m31 = this.m31 + matrix4f0.m31;
        this.m32 = this.m32 + matrix4f0.m32;
        this.m33 = this.m33 + matrix4f0.m33;
    }

    public final void sub(Matrix4f matrix4f1, Matrix4f matrix4f0) {
        this.m00 = matrix4f1.m00 - matrix4f0.m00;
        this.m01 = matrix4f1.m01 - matrix4f0.m01;
        this.m02 = matrix4f1.m02 - matrix4f0.m02;
        this.m03 = matrix4f1.m03 - matrix4f0.m03;
        this.m10 = matrix4f1.m10 - matrix4f0.m10;
        this.m11 = matrix4f1.m11 - matrix4f0.m11;
        this.m12 = matrix4f1.m12 - matrix4f0.m12;
        this.m13 = matrix4f1.m13 - matrix4f0.m13;
        this.m20 = matrix4f1.m20 - matrix4f0.m20;
        this.m21 = matrix4f1.m21 - matrix4f0.m21;
        this.m22 = matrix4f1.m22 - matrix4f0.m22;
        this.m23 = matrix4f1.m23 - matrix4f0.m23;
        this.m30 = matrix4f1.m30 - matrix4f0.m30;
        this.m31 = matrix4f1.m31 - matrix4f0.m31;
        this.m32 = matrix4f1.m32 - matrix4f0.m32;
        this.m33 = matrix4f1.m33 - matrix4f0.m33;
    }

    public final void sub(Matrix4f matrix4f0) {
        this.m00 = this.m00 - matrix4f0.m00;
        this.m01 = this.m01 - matrix4f0.m01;
        this.m02 = this.m02 - matrix4f0.m02;
        this.m03 = this.m03 - matrix4f0.m03;
        this.m10 = this.m10 - matrix4f0.m10;
        this.m11 = this.m11 - matrix4f0.m11;
        this.m12 = this.m12 - matrix4f0.m12;
        this.m13 = this.m13 - matrix4f0.m13;
        this.m20 = this.m20 - matrix4f0.m20;
        this.m21 = this.m21 - matrix4f0.m21;
        this.m22 = this.m22 - matrix4f0.m22;
        this.m23 = this.m23 - matrix4f0.m23;
        this.m30 = this.m30 - matrix4f0.m30;
        this.m31 = this.m31 - matrix4f0.m31;
        this.m32 = this.m32 - matrix4f0.m32;
        this.m33 = this.m33 - matrix4f0.m33;
    }

    public final void transpose() {
        float float0 = this.m10;
        this.m10 = this.m01;
        this.m01 = float0;
        float0 = this.m20;
        this.m20 = this.m02;
        this.m02 = float0;
        float0 = this.m30;
        this.m30 = this.m03;
        this.m03 = float0;
        float0 = this.m21;
        this.m21 = this.m12;
        this.m12 = float0;
        float0 = this.m31;
        this.m31 = this.m13;
        this.m13 = float0;
        float0 = this.m32;
        this.m32 = this.m23;
        this.m23 = float0;
    }

    public final void transpose(Matrix4f matrix4f1) {
        if (this != matrix4f1) {
            this.m00 = matrix4f1.m00;
            this.m01 = matrix4f1.m10;
            this.m02 = matrix4f1.m20;
            this.m03 = matrix4f1.m30;
            this.m10 = matrix4f1.m01;
            this.m11 = matrix4f1.m11;
            this.m12 = matrix4f1.m21;
            this.m13 = matrix4f1.m31;
            this.m20 = matrix4f1.m02;
            this.m21 = matrix4f1.m12;
            this.m22 = matrix4f1.m22;
            this.m23 = matrix4f1.m32;
            this.m30 = matrix4f1.m03;
            this.m31 = matrix4f1.m13;
            this.m32 = matrix4f1.m23;
            this.m33 = matrix4f1.m33;
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
        this.m03 = 0.0F;
        this.m13 = 0.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
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

        this.m03 = 0.0F;
        this.m13 = 0.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
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
        this.m03 = 0.0F;
        this.m13 = 0.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
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
            float float0 = (float)Math.sin(axisAngle4d.angle);
            float float1 = (float)Math.cos(axisAngle4d.angle);
            float float2 = 1.0F - float1;
            float float3 = (float)(double1 * double3);
            float float4 = (float)(double1 * double2);
            float float5 = (float)(double2 * double3);
            this.m00 = float2 * (float)(double1 * double1) + float1;
            this.m01 = float2 * float4 - float0 * (float)double3;
            this.m02 = float2 * float3 + float0 * (float)double2;
            this.m10 = float2 * float4 + float0 * (float)double3;
            this.m11 = float2 * (float)(double2 * double2) + float1;
            this.m12 = float2 * float5 - float0 * (float)double1;
            this.m20 = float2 * float3 - float0 * (float)double2;
            this.m21 = float2 * float5 + float0 * (float)double1;
            this.m22 = float2 * (float)(double3 * double3) + float1;
        }

        this.m03 = 0.0F;
        this.m13 = 0.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(Quat4d quat4d, Vector3d vector3d, double double0) {
        this.m00 = (float)(double0 * (1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z));
        this.m10 = (float)(double0 * (2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z)));
        this.m20 = (float)(double0 * (2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y)));
        this.m01 = (float)(double0 * (2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z)));
        this.m11 = (float)(double0 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z));
        this.m21 = (float)(double0 * (2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x)));
        this.m02 = (float)(double0 * (2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y)));
        this.m12 = (float)(double0 * (2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x)));
        this.m22 = (float)(double0 * (1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y));
        this.m03 = (float)vector3d.x;
        this.m13 = (float)vector3d.y;
        this.m23 = (float)vector3d.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(Quat4f quat4f, Vector3f vector3f, float float0) {
        this.m00 = float0 * (1.0F - 2.0F * quat4f.y * quat4f.y - 2.0F * quat4f.z * quat4f.z);
        this.m10 = float0 * (2.0F * (quat4f.x * quat4f.y + quat4f.w * quat4f.z));
        this.m20 = float0 * (2.0F * (quat4f.x * quat4f.z - quat4f.w * quat4f.y));
        this.m01 = float0 * (2.0F * (quat4f.x * quat4f.y - quat4f.w * quat4f.z));
        this.m11 = float0 * (1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.z * quat4f.z);
        this.m21 = float0 * (2.0F * (quat4f.y * quat4f.z + quat4f.w * quat4f.x));
        this.m02 = float0 * (2.0F * (quat4f.x * quat4f.z + quat4f.w * quat4f.y));
        this.m12 = float0 * (2.0F * (quat4f.y * quat4f.z - quat4f.w * quat4f.x));
        this.m22 = float0 * (1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.y * quat4f.y);
        this.m03 = vector3f.x;
        this.m13 = vector3f.y;
        this.m23 = vector3f.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(Matrix4d matrix4d) {
        this.m00 = (float)matrix4d.m00;
        this.m01 = (float)matrix4d.m01;
        this.m02 = (float)matrix4d.m02;
        this.m03 = (float)matrix4d.m03;
        this.m10 = (float)matrix4d.m10;
        this.m11 = (float)matrix4d.m11;
        this.m12 = (float)matrix4d.m12;
        this.m13 = (float)matrix4d.m13;
        this.m20 = (float)matrix4d.m20;
        this.m21 = (float)matrix4d.m21;
        this.m22 = (float)matrix4d.m22;
        this.m23 = (float)matrix4d.m23;
        this.m30 = (float)matrix4d.m30;
        this.m31 = (float)matrix4d.m31;
        this.m32 = (float)matrix4d.m32;
        this.m33 = (float)matrix4d.m33;
    }

    public final void set(Matrix4f matrix4f0) {
        this.m00 = matrix4f0.m00;
        this.m01 = matrix4f0.m01;
        this.m02 = matrix4f0.m02;
        this.m03 = matrix4f0.m03;
        this.m10 = matrix4f0.m10;
        this.m11 = matrix4f0.m11;
        this.m12 = matrix4f0.m12;
        this.m13 = matrix4f0.m13;
        this.m20 = matrix4f0.m20;
        this.m21 = matrix4f0.m21;
        this.m22 = matrix4f0.m22;
        this.m23 = matrix4f0.m23;
        this.m30 = matrix4f0.m30;
        this.m31 = matrix4f0.m31;
        this.m32 = matrix4f0.m32;
        this.m33 = matrix4f0.m33;
    }

    public final void invert(Matrix4f matrix4f1) {
        this.invertGeneral(matrix4f1);
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    final void invertGeneral(Matrix4f matrix4f0) {
        double[] doubles0 = new double[16];
        double[] doubles1 = new double[16];
        int[] ints = new int[4];
        doubles0[0] = matrix4f0.m00;
        doubles0[1] = matrix4f0.m01;
        doubles0[2] = matrix4f0.m02;
        doubles0[3] = matrix4f0.m03;
        doubles0[4] = matrix4f0.m10;
        doubles0[5] = matrix4f0.m11;
        doubles0[6] = matrix4f0.m12;
        doubles0[7] = matrix4f0.m13;
        doubles0[8] = matrix4f0.m20;
        doubles0[9] = matrix4f0.m21;
        doubles0[10] = matrix4f0.m22;
        doubles0[11] = matrix4f0.m23;
        doubles0[12] = matrix4f0.m30;
        doubles0[13] = matrix4f0.m31;
        doubles0[14] = matrix4f0.m32;
        doubles0[15] = matrix4f0.m33;
        if (!luDecomposition(doubles0, ints)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix4f12"));
        } else {
            for (int int0 = 0; int0 < 16; int0++) {
                doubles1[int0] = 0.0;
            }

            doubles1[0] = 1.0;
            doubles1[5] = 1.0;
            doubles1[10] = 1.0;
            doubles1[15] = 1.0;
            luBacksubstitution(doubles0, ints, doubles1);
            this.m00 = (float)doubles1[0];
            this.m01 = (float)doubles1[1];
            this.m02 = (float)doubles1[2];
            this.m03 = (float)doubles1[3];
            this.m10 = (float)doubles1[4];
            this.m11 = (float)doubles1[5];
            this.m12 = (float)doubles1[6];
            this.m13 = (float)doubles1[7];
            this.m20 = (float)doubles1[8];
            this.m21 = (float)doubles1[9];
            this.m22 = (float)doubles1[10];
            this.m23 = (float)doubles1[11];
            this.m30 = (float)doubles1[12];
            this.m31 = (float)doubles1[13];
            this.m32 = (float)doubles1[14];
            this.m33 = (float)doubles1[15];
        }
    }

    static boolean luDecomposition(double[] doubles1, int[] ints) {
        double[] doubles0 = new double[4];
        int int0 = 0;
        int int1 = 0;
        int int2 = 4;

        while (int2-- != 0) {
            double double0 = 0.0;
            int int3 = 4;

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

        for (int int4 = 0; int4 < 4; int4++) {
            for (int int5 = 0; int5 < int4; int5++) {
                int int6 = byte0 + 4 * int5 + int4;
                double double2 = doubles1[int6];
                int int7 = int5;
                int int8 = byte0 + 4 * int5;

                for (int int9 = byte0 + int4; int7-- != 0; int9 += 4) {
                    double2 -= doubles1[int8] * doubles1[int9];
                    int8++;
                }

                doubles1[int6] = double2;
            }

            double double3 = 0.0;
            int1 = -1;

            for (int int10 = int4; int10 < 4; int10++) {
                int int11 = byte0 + 4 * int10 + int4;
                double double4 = doubles1[int11];
                int int12 = int4;
                int int13 = byte0 + 4 * int10;

                for (int int14 = byte0 + int4; int12-- != 0; int14 += 4) {
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
                throw new RuntimeException(VecMathI18N.getString("Matrix4f13"));
            }

            if (int4 != int1) {
                int int15 = 4;
                int int16 = byte0 + 4 * int1;
                int int17 = byte0 + 4 * int4;

                while (int15-- != 0) {
                    double double6 = doubles1[int16];
                    doubles1[int16++] = doubles1[int17];
                    doubles1[int17++] = double6;
                }

                doubles0[int1] = doubles0[int4];
            }

            ints[int4] = int1;
            if (doubles1[byte0 + 4 * int4 + int4] == 0.0) {
                return false;
            }

            if (int4 != 3) {
                double double7 = 1.0 / doubles1[byte0 + 4 * int4 + int4];
                int int18 = byte0 + 4 * (int4 + 1) + int4;

                for (int int19 = 3 - int4; int19-- != 0; int18 += 4) {
                    doubles1[int18] *= double7;
                }
            }
        }

        return true;
    }

    static void luBacksubstitution(double[] doubles1, int[] ints, double[] doubles0) {
        byte byte0 = 0;

        for (int int0 = 0; int0 < 4; int0++) {
            int int1 = int0;
            int int2 = -1;

            for (int int3 = 0; int3 < 4; int3++) {
                int int4 = ints[byte0 + int3];
                double double0 = doubles0[int1 + 4 * int4];
                doubles0[int1 + 4 * int4] = doubles0[int1 + 4 * int3];
                if (int2 >= 0) {
                    int int5 = int3 * 4;

                    for (int int6 = int2; int6 <= int3 - 1; int6++) {
                        double0 -= doubles1[int5 + int6] * doubles0[int1 + 4 * int6];
                    }
                } else if (double0 != 0.0) {
                    int2 = int3;
                }

                doubles0[int1 + 4 * int3] = double0;
            }

            int int7 = 12;
            doubles0[int1 + 12] = doubles0[int1 + 12] / doubles1[int7 + 3];
            int7 -= 4;
            doubles0[int1 + 8] = (doubles0[int1 + 8] - doubles1[int7 + 3] * doubles0[int1 + 12]) / doubles1[int7 + 2];
            int7 -= 4;
            doubles0[int1 + 4] = (doubles0[int1 + 4] - doubles1[int7 + 2] * doubles0[int1 + 8] - doubles1[int7 + 3] * doubles0[int1 + 12]) / doubles1[int7 + 1];
            int7 -= 4;
            doubles0[int1 + 0] = (
                    doubles0[int1 + 0]
                        - doubles1[int7 + 1] * doubles0[int1 + 4]
                        - doubles1[int7 + 2] * doubles0[int1 + 8]
                        - doubles1[int7 + 3] * doubles0[int1 + 12]
                )
                / doubles1[int7 + 0];
        }
    }

    public final float determinant() {
        float float0 = this.m00
            * (
                this.m11 * this.m22 * this.m33
                    + this.m12 * this.m23 * this.m31
                    + this.m13 * this.m21 * this.m32
                    - this.m13 * this.m22 * this.m31
                    - this.m11 * this.m23 * this.m32
                    - this.m12 * this.m21 * this.m33
            );
        float0 -= this.m01
            * (
                this.m10 * this.m22 * this.m33
                    + this.m12 * this.m23 * this.m30
                    + this.m13 * this.m20 * this.m32
                    - this.m13 * this.m22 * this.m30
                    - this.m10 * this.m23 * this.m32
                    - this.m12 * this.m20 * this.m33
            );
        float0 += this.m02
            * (
                this.m10 * this.m21 * this.m33
                    + this.m11 * this.m23 * this.m30
                    + this.m13 * this.m20 * this.m31
                    - this.m13 * this.m21 * this.m30
                    - this.m10 * this.m23 * this.m31
                    - this.m11 * this.m20 * this.m33
            );
        return float0
            - this.m03
                * (
                    this.m10 * this.m21 * this.m32
                        + this.m11 * this.m22 * this.m30
                        + this.m12 * this.m20 * this.m31
                        - this.m12 * this.m21 * this.m30
                        - this.m10 * this.m22 * this.m31
                        - this.m11 * this.m20 * this.m32
                );
    }

    public final void set(Matrix3f matrix3f) {
        this.m00 = matrix3f.m00;
        this.m01 = matrix3f.m01;
        this.m02 = matrix3f.m02;
        this.m03 = 0.0F;
        this.m10 = matrix3f.m10;
        this.m11 = matrix3f.m11;
        this.m12 = matrix3f.m12;
        this.m13 = 0.0F;
        this.m20 = matrix3f.m20;
        this.m21 = matrix3f.m21;
        this.m22 = matrix3f.m22;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(Matrix3d matrix3d) {
        this.m00 = (float)matrix3d.m00;
        this.m01 = (float)matrix3d.m01;
        this.m02 = (float)matrix3d.m02;
        this.m03 = 0.0F;
        this.m10 = (float)matrix3d.m10;
        this.m11 = (float)matrix3d.m11;
        this.m12 = (float)matrix3d.m12;
        this.m13 = 0.0F;
        this.m20 = (float)matrix3d.m20;
        this.m21 = (float)matrix3d.m21;
        this.m22 = (float)matrix3d.m22;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(float float0) {
        this.m00 = float0;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = float0;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = float0;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(float[] floats) {
        this.m00 = floats[0];
        this.m01 = floats[1];
        this.m02 = floats[2];
        this.m03 = floats[3];
        this.m10 = floats[4];
        this.m11 = floats[5];
        this.m12 = floats[6];
        this.m13 = floats[7];
        this.m20 = floats[8];
        this.m21 = floats[9];
        this.m22 = floats[10];
        this.m23 = floats[11];
        this.m30 = floats[12];
        this.m31 = floats[13];
        this.m32 = floats[14];
        this.m33 = floats[15];
    }

    public final void set(Vector3f vector3f) {
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = vector3f.x;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m13 = vector3f.y;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
        this.m23 = vector3f.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(float float0, Vector3f vector3f) {
        this.m00 = float0;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = vector3f.x;
        this.m10 = 0.0F;
        this.m11 = float0;
        this.m12 = 0.0F;
        this.m13 = vector3f.y;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = float0;
        this.m23 = vector3f.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(Vector3f vector3f, float float0) {
        this.m00 = float0;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = float0 * vector3f.x;
        this.m10 = 0.0F;
        this.m11 = float0;
        this.m12 = 0.0F;
        this.m13 = float0 * vector3f.y;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = float0;
        this.m23 = float0 * vector3f.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(Matrix3f matrix3f, Vector3f vector3f, float float0) {
        this.m00 = matrix3f.m00 * float0;
        this.m01 = matrix3f.m01 * float0;
        this.m02 = matrix3f.m02 * float0;
        this.m03 = vector3f.x;
        this.m10 = matrix3f.m10 * float0;
        this.m11 = matrix3f.m11 * float0;
        this.m12 = matrix3f.m12 * float0;
        this.m13 = vector3f.y;
        this.m20 = matrix3f.m20 * float0;
        this.m21 = matrix3f.m21 * float0;
        this.m22 = matrix3f.m22 * float0;
        this.m23 = vector3f.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void set(Matrix3d matrix3d, Vector3d vector3d, double double0) {
        this.m00 = (float)(matrix3d.m00 * double0);
        this.m01 = (float)(matrix3d.m01 * double0);
        this.m02 = (float)(matrix3d.m02 * double0);
        this.m03 = (float)vector3d.x;
        this.m10 = (float)(matrix3d.m10 * double0);
        this.m11 = (float)(matrix3d.m11 * double0);
        this.m12 = (float)(matrix3d.m12 * double0);
        this.m13 = (float)vector3d.y;
        this.m20 = (float)(matrix3d.m20 * double0);
        this.m21 = (float)(matrix3d.m21 * double0);
        this.m22 = (float)(matrix3d.m22 * double0);
        this.m23 = (float)vector3d.z;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void setTranslation(Vector3f vector3f) {
        this.m03 = vector3f.x;
        this.m13 = vector3f.y;
        this.m23 = vector3f.z;
    }

    public final void rotX(float float1) {
        float float0 = (float)Math.sin(float1);
        float float2 = (float)Math.cos(float1);
        this.m00 = 1.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = float2;
        this.m12 = -float0;
        this.m13 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = float0;
        this.m22 = float2;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void rotY(float float1) {
        float float0 = (float)Math.sin(float1);
        float float2 = (float)Math.cos(float1);
        this.m00 = float2;
        this.m01 = 0.0F;
        this.m02 = float0;
        this.m03 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 1.0F;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = -float0;
        this.m21 = 0.0F;
        this.m22 = float2;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void rotZ(float float1) {
        float float0 = (float)Math.sin(float1);
        float float2 = (float)Math.cos(float1);
        this.m00 = float2;
        this.m01 = -float0;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = float0;
        this.m11 = float2;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 1.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 1.0F;
    }

    public final void mul(float float0) {
        this.m00 *= float0;
        this.m01 *= float0;
        this.m02 *= float0;
        this.m03 *= float0;
        this.m10 *= float0;
        this.m11 *= float0;
        this.m12 *= float0;
        this.m13 *= float0;
        this.m20 *= float0;
        this.m21 *= float0;
        this.m22 *= float0;
        this.m23 *= float0;
        this.m30 *= float0;
        this.m31 *= float0;
        this.m32 *= float0;
        this.m33 *= float0;
    }

    public final void mul(float float0, Matrix4f matrix4f0) {
        this.m00 = matrix4f0.m00 * float0;
        this.m01 = matrix4f0.m01 * float0;
        this.m02 = matrix4f0.m02 * float0;
        this.m03 = matrix4f0.m03 * float0;
        this.m10 = matrix4f0.m10 * float0;
        this.m11 = matrix4f0.m11 * float0;
        this.m12 = matrix4f0.m12 * float0;
        this.m13 = matrix4f0.m13 * float0;
        this.m20 = matrix4f0.m20 * float0;
        this.m21 = matrix4f0.m21 * float0;
        this.m22 = matrix4f0.m22 * float0;
        this.m23 = matrix4f0.m23 * float0;
        this.m30 = matrix4f0.m30 * float0;
        this.m31 = matrix4f0.m31 * float0;
        this.m32 = matrix4f0.m32 * float0;
        this.m33 = matrix4f0.m33 * float0;
    }

    public final void mul(Matrix4f matrix4f0) {
        float float0 = this.m00 * matrix4f0.m00 + this.m01 * matrix4f0.m10 + this.m02 * matrix4f0.m20 + this.m03 * matrix4f0.m30;
        float float1 = this.m00 * matrix4f0.m01 + this.m01 * matrix4f0.m11 + this.m02 * matrix4f0.m21 + this.m03 * matrix4f0.m31;
        float float2 = this.m00 * matrix4f0.m02 + this.m01 * matrix4f0.m12 + this.m02 * matrix4f0.m22 + this.m03 * matrix4f0.m32;
        float float3 = this.m00 * matrix4f0.m03 + this.m01 * matrix4f0.m13 + this.m02 * matrix4f0.m23 + this.m03 * matrix4f0.m33;
        float float4 = this.m10 * matrix4f0.m00 + this.m11 * matrix4f0.m10 + this.m12 * matrix4f0.m20 + this.m13 * matrix4f0.m30;
        float float5 = this.m10 * matrix4f0.m01 + this.m11 * matrix4f0.m11 + this.m12 * matrix4f0.m21 + this.m13 * matrix4f0.m31;
        float float6 = this.m10 * matrix4f0.m02 + this.m11 * matrix4f0.m12 + this.m12 * matrix4f0.m22 + this.m13 * matrix4f0.m32;
        float float7 = this.m10 * matrix4f0.m03 + this.m11 * matrix4f0.m13 + this.m12 * matrix4f0.m23 + this.m13 * matrix4f0.m33;
        float float8 = this.m20 * matrix4f0.m00 + this.m21 * matrix4f0.m10 + this.m22 * matrix4f0.m20 + this.m23 * matrix4f0.m30;
        float float9 = this.m20 * matrix4f0.m01 + this.m21 * matrix4f0.m11 + this.m22 * matrix4f0.m21 + this.m23 * matrix4f0.m31;
        float float10 = this.m20 * matrix4f0.m02 + this.m21 * matrix4f0.m12 + this.m22 * matrix4f0.m22 + this.m23 * matrix4f0.m32;
        float float11 = this.m20 * matrix4f0.m03 + this.m21 * matrix4f0.m13 + this.m22 * matrix4f0.m23 + this.m23 * matrix4f0.m33;
        float float12 = this.m30 * matrix4f0.m00 + this.m31 * matrix4f0.m10 + this.m32 * matrix4f0.m20 + this.m33 * matrix4f0.m30;
        float float13 = this.m30 * matrix4f0.m01 + this.m31 * matrix4f0.m11 + this.m32 * matrix4f0.m21 + this.m33 * matrix4f0.m31;
        float float14 = this.m30 * matrix4f0.m02 + this.m31 * matrix4f0.m12 + this.m32 * matrix4f0.m22 + this.m33 * matrix4f0.m32;
        float float15 = this.m30 * matrix4f0.m03 + this.m31 * matrix4f0.m13 + this.m32 * matrix4f0.m23 + this.m33 * matrix4f0.m33;
        this.m00 = float0;
        this.m01 = float1;
        this.m02 = float2;
        this.m03 = float3;
        this.m10 = float4;
        this.m11 = float5;
        this.m12 = float6;
        this.m13 = float7;
        this.m20 = float8;
        this.m21 = float9;
        this.m22 = float10;
        this.m23 = float11;
        this.m30 = float12;
        this.m31 = float13;
        this.m32 = float14;
        this.m33 = float15;
    }

    public final void mul(Matrix4f matrix4f2, Matrix4f matrix4f1) {
        if (this != matrix4f2 && this != matrix4f1) {
            this.m00 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m01 * matrix4f1.m10 + matrix4f2.m02 * matrix4f1.m20 + matrix4f2.m03 * matrix4f1.m30;
            this.m01 = matrix4f2.m00 * matrix4f1.m01 + matrix4f2.m01 * matrix4f1.m11 + matrix4f2.m02 * matrix4f1.m21 + matrix4f2.m03 * matrix4f1.m31;
            this.m02 = matrix4f2.m00 * matrix4f1.m02 + matrix4f2.m01 * matrix4f1.m12 + matrix4f2.m02 * matrix4f1.m22 + matrix4f2.m03 * matrix4f1.m32;
            this.m03 = matrix4f2.m00 * matrix4f1.m03 + matrix4f2.m01 * matrix4f1.m13 + matrix4f2.m02 * matrix4f1.m23 + matrix4f2.m03 * matrix4f1.m33;
            this.m10 = matrix4f2.m10 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m10 + matrix4f2.m12 * matrix4f1.m20 + matrix4f2.m13 * matrix4f1.m30;
            this.m11 = matrix4f2.m10 * matrix4f1.m01 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m12 * matrix4f1.m21 + matrix4f2.m13 * matrix4f1.m31;
            this.m12 = matrix4f2.m10 * matrix4f1.m02 + matrix4f2.m11 * matrix4f1.m12 + matrix4f2.m12 * matrix4f1.m22 + matrix4f2.m13 * matrix4f1.m32;
            this.m13 = matrix4f2.m10 * matrix4f1.m03 + matrix4f2.m11 * matrix4f1.m13 + matrix4f2.m12 * matrix4f1.m23 + matrix4f2.m13 * matrix4f1.m33;
            this.m20 = matrix4f2.m20 * matrix4f1.m00 + matrix4f2.m21 * matrix4f1.m10 + matrix4f2.m22 * matrix4f1.m20 + matrix4f2.m23 * matrix4f1.m30;
            this.m21 = matrix4f2.m20 * matrix4f1.m01 + matrix4f2.m21 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m21 + matrix4f2.m23 * matrix4f1.m31;
            this.m22 = matrix4f2.m20 * matrix4f1.m02 + matrix4f2.m21 * matrix4f1.m12 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m23 * matrix4f1.m32;
            this.m23 = matrix4f2.m20 * matrix4f1.m03 + matrix4f2.m21 * matrix4f1.m13 + matrix4f2.m22 * matrix4f1.m23 + matrix4f2.m23 * matrix4f1.m33;
            this.m30 = matrix4f2.m30 * matrix4f1.m00 + matrix4f2.m31 * matrix4f1.m10 + matrix4f2.m32 * matrix4f1.m20 + matrix4f2.m33 * matrix4f1.m30;
            this.m31 = matrix4f2.m30 * matrix4f1.m01 + matrix4f2.m31 * matrix4f1.m11 + matrix4f2.m32 * matrix4f1.m21 + matrix4f2.m33 * matrix4f1.m31;
            this.m32 = matrix4f2.m30 * matrix4f1.m02 + matrix4f2.m31 * matrix4f1.m12 + matrix4f2.m32 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m32;
            this.m33 = matrix4f2.m30 * matrix4f1.m03 + matrix4f2.m31 * matrix4f1.m13 + matrix4f2.m32 * matrix4f1.m23 + matrix4f2.m33 * matrix4f1.m33;
        } else {
            float float0 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m01 * matrix4f1.m10 + matrix4f2.m02 * matrix4f1.m20 + matrix4f2.m03 * matrix4f1.m30;
            float float1 = matrix4f2.m00 * matrix4f1.m01 + matrix4f2.m01 * matrix4f1.m11 + matrix4f2.m02 * matrix4f1.m21 + matrix4f2.m03 * matrix4f1.m31;
            float float2 = matrix4f2.m00 * matrix4f1.m02 + matrix4f2.m01 * matrix4f1.m12 + matrix4f2.m02 * matrix4f1.m22 + matrix4f2.m03 * matrix4f1.m32;
            float float3 = matrix4f2.m00 * matrix4f1.m03 + matrix4f2.m01 * matrix4f1.m13 + matrix4f2.m02 * matrix4f1.m23 + matrix4f2.m03 * matrix4f1.m33;
            float float4 = matrix4f2.m10 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m10 + matrix4f2.m12 * matrix4f1.m20 + matrix4f2.m13 * matrix4f1.m30;
            float float5 = matrix4f2.m10 * matrix4f1.m01 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m12 * matrix4f1.m21 + matrix4f2.m13 * matrix4f1.m31;
            float float6 = matrix4f2.m10 * matrix4f1.m02 + matrix4f2.m11 * matrix4f1.m12 + matrix4f2.m12 * matrix4f1.m22 + matrix4f2.m13 * matrix4f1.m32;
            float float7 = matrix4f2.m10 * matrix4f1.m03 + matrix4f2.m11 * matrix4f1.m13 + matrix4f2.m12 * matrix4f1.m23 + matrix4f2.m13 * matrix4f1.m33;
            float float8 = matrix4f2.m20 * matrix4f1.m00 + matrix4f2.m21 * matrix4f1.m10 + matrix4f2.m22 * matrix4f1.m20 + matrix4f2.m23 * matrix4f1.m30;
            float float9 = matrix4f2.m20 * matrix4f1.m01 + matrix4f2.m21 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m21 + matrix4f2.m23 * matrix4f1.m31;
            float float10 = matrix4f2.m20 * matrix4f1.m02 + matrix4f2.m21 * matrix4f1.m12 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m23 * matrix4f1.m32;
            float float11 = matrix4f2.m20 * matrix4f1.m03 + matrix4f2.m21 * matrix4f1.m13 + matrix4f2.m22 * matrix4f1.m23 + matrix4f2.m23 * matrix4f1.m33;
            float float12 = matrix4f2.m30 * matrix4f1.m00 + matrix4f2.m31 * matrix4f1.m10 + matrix4f2.m32 * matrix4f1.m20 + matrix4f2.m33 * matrix4f1.m30;
            float float13 = matrix4f2.m30 * matrix4f1.m01 + matrix4f2.m31 * matrix4f1.m11 + matrix4f2.m32 * matrix4f1.m21 + matrix4f2.m33 * matrix4f1.m31;
            float float14 = matrix4f2.m30 * matrix4f1.m02 + matrix4f2.m31 * matrix4f1.m12 + matrix4f2.m32 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m32;
            float float15 = matrix4f2.m30 * matrix4f1.m03 + matrix4f2.m31 * matrix4f1.m13 + matrix4f2.m32 * matrix4f1.m23 + matrix4f2.m33 * matrix4f1.m33;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m03 = float3;
            this.m10 = float4;
            this.m11 = float5;
            this.m12 = float6;
            this.m13 = float7;
            this.m20 = float8;
            this.m21 = float9;
            this.m22 = float10;
            this.m23 = float11;
            this.m30 = float12;
            this.m31 = float13;
            this.m32 = float14;
            this.m33 = float15;
        }
    }

    public final void mulTransposeBoth(Matrix4f matrix4f2, Matrix4f matrix4f1) {
        if (this != matrix4f2 && this != matrix4f1) {
            this.m00 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m10 * matrix4f1.m01 + matrix4f2.m20 * matrix4f1.m02 + matrix4f2.m30 * matrix4f1.m03;
            this.m01 = matrix4f2.m00 * matrix4f1.m10 + matrix4f2.m10 * matrix4f1.m11 + matrix4f2.m20 * matrix4f1.m12 + matrix4f2.m30 * matrix4f1.m13;
            this.m02 = matrix4f2.m00 * matrix4f1.m20 + matrix4f2.m10 * matrix4f1.m21 + matrix4f2.m20 * matrix4f1.m22 + matrix4f2.m30 * matrix4f1.m23;
            this.m03 = matrix4f2.m00 * matrix4f1.m30 + matrix4f2.m10 * matrix4f1.m31 + matrix4f2.m20 * matrix4f1.m32 + matrix4f2.m30 * matrix4f1.m33;
            this.m10 = matrix4f2.m01 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m01 + matrix4f2.m21 * matrix4f1.m02 + matrix4f2.m31 * matrix4f1.m03;
            this.m11 = matrix4f2.m01 * matrix4f1.m10 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m21 * matrix4f1.m12 + matrix4f2.m31 * matrix4f1.m13;
            this.m12 = matrix4f2.m01 * matrix4f1.m20 + matrix4f2.m11 * matrix4f1.m21 + matrix4f2.m21 * matrix4f1.m22 + matrix4f2.m31 * matrix4f1.m23;
            this.m13 = matrix4f2.m01 * matrix4f1.m30 + matrix4f2.m11 * matrix4f1.m31 + matrix4f2.m21 * matrix4f1.m32 + matrix4f2.m31 * matrix4f1.m33;
            this.m20 = matrix4f2.m02 * matrix4f1.m00 + matrix4f2.m12 * matrix4f1.m01 + matrix4f2.m22 * matrix4f1.m02 + matrix4f2.m32 * matrix4f1.m03;
            this.m21 = matrix4f2.m02 * matrix4f1.m10 + matrix4f2.m12 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m12 + matrix4f2.m32 * matrix4f1.m13;
            this.m22 = matrix4f2.m02 * matrix4f1.m20 + matrix4f2.m12 * matrix4f1.m21 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m32 * matrix4f1.m23;
            this.m23 = matrix4f2.m02 * matrix4f1.m30 + matrix4f2.m12 * matrix4f1.m31 + matrix4f2.m22 * matrix4f1.m32 + matrix4f2.m32 * matrix4f1.m33;
            this.m30 = matrix4f2.m03 * matrix4f1.m00 + matrix4f2.m13 * matrix4f1.m01 + matrix4f2.m23 * matrix4f1.m02 + matrix4f2.m33 * matrix4f1.m03;
            this.m31 = matrix4f2.m03 * matrix4f1.m10 + matrix4f2.m13 * matrix4f1.m11 + matrix4f2.m23 * matrix4f1.m12 + matrix4f2.m33 * matrix4f1.m13;
            this.m32 = matrix4f2.m03 * matrix4f1.m20 + matrix4f2.m13 * matrix4f1.m21 + matrix4f2.m23 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m23;
            this.m33 = matrix4f2.m03 * matrix4f1.m30 + matrix4f2.m13 * matrix4f1.m31 + matrix4f2.m23 * matrix4f1.m32 + matrix4f2.m33 * matrix4f1.m33;
        } else {
            float float0 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m10 * matrix4f1.m01 + matrix4f2.m20 * matrix4f1.m02 + matrix4f2.m30 * matrix4f1.m03;
            float float1 = matrix4f2.m00 * matrix4f1.m10 + matrix4f2.m10 * matrix4f1.m11 + matrix4f2.m20 * matrix4f1.m12 + matrix4f2.m30 * matrix4f1.m13;
            float float2 = matrix4f2.m00 * matrix4f1.m20 + matrix4f2.m10 * matrix4f1.m21 + matrix4f2.m20 * matrix4f1.m22 + matrix4f2.m30 * matrix4f1.m23;
            float float3 = matrix4f2.m00 * matrix4f1.m30 + matrix4f2.m10 * matrix4f1.m31 + matrix4f2.m20 * matrix4f1.m32 + matrix4f2.m30 * matrix4f1.m33;
            float float4 = matrix4f2.m01 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m01 + matrix4f2.m21 * matrix4f1.m02 + matrix4f2.m31 * matrix4f1.m03;
            float float5 = matrix4f2.m01 * matrix4f1.m10 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m21 * matrix4f1.m12 + matrix4f2.m31 * matrix4f1.m13;
            float float6 = matrix4f2.m01 * matrix4f1.m20 + matrix4f2.m11 * matrix4f1.m21 + matrix4f2.m21 * matrix4f1.m22 + matrix4f2.m31 * matrix4f1.m23;
            float float7 = matrix4f2.m01 * matrix4f1.m30 + matrix4f2.m11 * matrix4f1.m31 + matrix4f2.m21 * matrix4f1.m32 + matrix4f2.m31 * matrix4f1.m33;
            float float8 = matrix4f2.m02 * matrix4f1.m00 + matrix4f2.m12 * matrix4f1.m01 + matrix4f2.m22 * matrix4f1.m02 + matrix4f2.m32 * matrix4f1.m03;
            float float9 = matrix4f2.m02 * matrix4f1.m10 + matrix4f2.m12 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m12 + matrix4f2.m32 * matrix4f1.m13;
            float float10 = matrix4f2.m02 * matrix4f1.m20 + matrix4f2.m12 * matrix4f1.m21 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m32 * matrix4f1.m23;
            float float11 = matrix4f2.m02 * matrix4f1.m30 + matrix4f2.m12 * matrix4f1.m31 + matrix4f2.m22 * matrix4f1.m32 + matrix4f2.m32 * matrix4f1.m33;
            float float12 = matrix4f2.m03 * matrix4f1.m00 + matrix4f2.m13 * matrix4f1.m01 + matrix4f2.m23 * matrix4f1.m02 + matrix4f2.m33 * matrix4f1.m03;
            float float13 = matrix4f2.m03 * matrix4f1.m10 + matrix4f2.m13 * matrix4f1.m11 + matrix4f2.m23 * matrix4f1.m12 + matrix4f2.m33 * matrix4f1.m13;
            float float14 = matrix4f2.m03 * matrix4f1.m20 + matrix4f2.m13 * matrix4f1.m21 + matrix4f2.m23 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m23;
            float float15 = matrix4f2.m03 * matrix4f1.m30 + matrix4f2.m13 * matrix4f1.m31 + matrix4f2.m23 * matrix4f1.m32 + matrix4f2.m33 * matrix4f1.m33;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m03 = float3;
            this.m10 = float4;
            this.m11 = float5;
            this.m12 = float6;
            this.m13 = float7;
            this.m20 = float8;
            this.m21 = float9;
            this.m22 = float10;
            this.m23 = float11;
            this.m30 = float12;
            this.m31 = float13;
            this.m32 = float14;
            this.m33 = float15;
        }
    }

    public final void mulTransposeRight(Matrix4f matrix4f2, Matrix4f matrix4f1) {
        if (this != matrix4f2 && this != matrix4f1) {
            this.m00 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m01 * matrix4f1.m01 + matrix4f2.m02 * matrix4f1.m02 + matrix4f2.m03 * matrix4f1.m03;
            this.m01 = matrix4f2.m00 * matrix4f1.m10 + matrix4f2.m01 * matrix4f1.m11 + matrix4f2.m02 * matrix4f1.m12 + matrix4f2.m03 * matrix4f1.m13;
            this.m02 = matrix4f2.m00 * matrix4f1.m20 + matrix4f2.m01 * matrix4f1.m21 + matrix4f2.m02 * matrix4f1.m22 + matrix4f2.m03 * matrix4f1.m23;
            this.m03 = matrix4f2.m00 * matrix4f1.m30 + matrix4f2.m01 * matrix4f1.m31 + matrix4f2.m02 * matrix4f1.m32 + matrix4f2.m03 * matrix4f1.m33;
            this.m10 = matrix4f2.m10 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m01 + matrix4f2.m12 * matrix4f1.m02 + matrix4f2.m13 * matrix4f1.m03;
            this.m11 = matrix4f2.m10 * matrix4f1.m10 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m12 * matrix4f1.m12 + matrix4f2.m13 * matrix4f1.m13;
            this.m12 = matrix4f2.m10 * matrix4f1.m20 + matrix4f2.m11 * matrix4f1.m21 + matrix4f2.m12 * matrix4f1.m22 + matrix4f2.m13 * matrix4f1.m23;
            this.m13 = matrix4f2.m10 * matrix4f1.m30 + matrix4f2.m11 * matrix4f1.m31 + matrix4f2.m12 * matrix4f1.m32 + matrix4f2.m13 * matrix4f1.m33;
            this.m20 = matrix4f2.m20 * matrix4f1.m00 + matrix4f2.m21 * matrix4f1.m01 + matrix4f2.m22 * matrix4f1.m02 + matrix4f2.m23 * matrix4f1.m03;
            this.m21 = matrix4f2.m20 * matrix4f1.m10 + matrix4f2.m21 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m12 + matrix4f2.m23 * matrix4f1.m13;
            this.m22 = matrix4f2.m20 * matrix4f1.m20 + matrix4f2.m21 * matrix4f1.m21 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m23 * matrix4f1.m23;
            this.m23 = matrix4f2.m20 * matrix4f1.m30 + matrix4f2.m21 * matrix4f1.m31 + matrix4f2.m22 * matrix4f1.m32 + matrix4f2.m23 * matrix4f1.m33;
            this.m30 = matrix4f2.m30 * matrix4f1.m00 + matrix4f2.m31 * matrix4f1.m01 + matrix4f2.m32 * matrix4f1.m02 + matrix4f2.m33 * matrix4f1.m03;
            this.m31 = matrix4f2.m30 * matrix4f1.m10 + matrix4f2.m31 * matrix4f1.m11 + matrix4f2.m32 * matrix4f1.m12 + matrix4f2.m33 * matrix4f1.m13;
            this.m32 = matrix4f2.m30 * matrix4f1.m20 + matrix4f2.m31 * matrix4f1.m21 + matrix4f2.m32 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m23;
            this.m33 = matrix4f2.m30 * matrix4f1.m30 + matrix4f2.m31 * matrix4f1.m31 + matrix4f2.m32 * matrix4f1.m32 + matrix4f2.m33 * matrix4f1.m33;
        } else {
            float float0 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m01 * matrix4f1.m01 + matrix4f2.m02 * matrix4f1.m02 + matrix4f2.m03 * matrix4f1.m03;
            float float1 = matrix4f2.m00 * matrix4f1.m10 + matrix4f2.m01 * matrix4f1.m11 + matrix4f2.m02 * matrix4f1.m12 + matrix4f2.m03 * matrix4f1.m13;
            float float2 = matrix4f2.m00 * matrix4f1.m20 + matrix4f2.m01 * matrix4f1.m21 + matrix4f2.m02 * matrix4f1.m22 + matrix4f2.m03 * matrix4f1.m23;
            float float3 = matrix4f2.m00 * matrix4f1.m30 + matrix4f2.m01 * matrix4f1.m31 + matrix4f2.m02 * matrix4f1.m32 + matrix4f2.m03 * matrix4f1.m33;
            float float4 = matrix4f2.m10 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m01 + matrix4f2.m12 * matrix4f1.m02 + matrix4f2.m13 * matrix4f1.m03;
            float float5 = matrix4f2.m10 * matrix4f1.m10 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m12 * matrix4f1.m12 + matrix4f2.m13 * matrix4f1.m13;
            float float6 = matrix4f2.m10 * matrix4f1.m20 + matrix4f2.m11 * matrix4f1.m21 + matrix4f2.m12 * matrix4f1.m22 + matrix4f2.m13 * matrix4f1.m23;
            float float7 = matrix4f2.m10 * matrix4f1.m30 + matrix4f2.m11 * matrix4f1.m31 + matrix4f2.m12 * matrix4f1.m32 + matrix4f2.m13 * matrix4f1.m33;
            float float8 = matrix4f2.m20 * matrix4f1.m00 + matrix4f2.m21 * matrix4f1.m01 + matrix4f2.m22 * matrix4f1.m02 + matrix4f2.m23 * matrix4f1.m03;
            float float9 = matrix4f2.m20 * matrix4f1.m10 + matrix4f2.m21 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m12 + matrix4f2.m23 * matrix4f1.m13;
            float float10 = matrix4f2.m20 * matrix4f1.m20 + matrix4f2.m21 * matrix4f1.m21 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m23 * matrix4f1.m23;
            float float11 = matrix4f2.m20 * matrix4f1.m30 + matrix4f2.m21 * matrix4f1.m31 + matrix4f2.m22 * matrix4f1.m32 + matrix4f2.m23 * matrix4f1.m33;
            float float12 = matrix4f2.m30 * matrix4f1.m00 + matrix4f2.m31 * matrix4f1.m01 + matrix4f2.m32 * matrix4f1.m02 + matrix4f2.m33 * matrix4f1.m03;
            float float13 = matrix4f2.m30 * matrix4f1.m10 + matrix4f2.m31 * matrix4f1.m11 + matrix4f2.m32 * matrix4f1.m12 + matrix4f2.m33 * matrix4f1.m13;
            float float14 = matrix4f2.m30 * matrix4f1.m20 + matrix4f2.m31 * matrix4f1.m21 + matrix4f2.m32 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m23;
            float float15 = matrix4f2.m30 * matrix4f1.m30 + matrix4f2.m31 * matrix4f1.m31 + matrix4f2.m32 * matrix4f1.m32 + matrix4f2.m33 * matrix4f1.m33;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m03 = float3;
            this.m10 = float4;
            this.m11 = float5;
            this.m12 = float6;
            this.m13 = float7;
            this.m20 = float8;
            this.m21 = float9;
            this.m22 = float10;
            this.m23 = float11;
            this.m30 = float12;
            this.m31 = float13;
            this.m32 = float14;
            this.m33 = float15;
        }
    }

    public final void mulTransposeLeft(Matrix4f matrix4f2, Matrix4f matrix4f1) {
        if (this != matrix4f2 && this != matrix4f1) {
            this.m00 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m10 * matrix4f1.m10 + matrix4f2.m20 * matrix4f1.m20 + matrix4f2.m30 * matrix4f1.m30;
            this.m01 = matrix4f2.m00 * matrix4f1.m01 + matrix4f2.m10 * matrix4f1.m11 + matrix4f2.m20 * matrix4f1.m21 + matrix4f2.m30 * matrix4f1.m31;
            this.m02 = matrix4f2.m00 * matrix4f1.m02 + matrix4f2.m10 * matrix4f1.m12 + matrix4f2.m20 * matrix4f1.m22 + matrix4f2.m30 * matrix4f1.m32;
            this.m03 = matrix4f2.m00 * matrix4f1.m03 + matrix4f2.m10 * matrix4f1.m13 + matrix4f2.m20 * matrix4f1.m23 + matrix4f2.m30 * matrix4f1.m33;
            this.m10 = matrix4f2.m01 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m10 + matrix4f2.m21 * matrix4f1.m20 + matrix4f2.m31 * matrix4f1.m30;
            this.m11 = matrix4f2.m01 * matrix4f1.m01 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m21 * matrix4f1.m21 + matrix4f2.m31 * matrix4f1.m31;
            this.m12 = matrix4f2.m01 * matrix4f1.m02 + matrix4f2.m11 * matrix4f1.m12 + matrix4f2.m21 * matrix4f1.m22 + matrix4f2.m31 * matrix4f1.m32;
            this.m13 = matrix4f2.m01 * matrix4f1.m03 + matrix4f2.m11 * matrix4f1.m13 + matrix4f2.m21 * matrix4f1.m23 + matrix4f2.m31 * matrix4f1.m33;
            this.m20 = matrix4f2.m02 * matrix4f1.m00 + matrix4f2.m12 * matrix4f1.m10 + matrix4f2.m22 * matrix4f1.m20 + matrix4f2.m32 * matrix4f1.m30;
            this.m21 = matrix4f2.m02 * matrix4f1.m01 + matrix4f2.m12 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m21 + matrix4f2.m32 * matrix4f1.m31;
            this.m22 = matrix4f2.m02 * matrix4f1.m02 + matrix4f2.m12 * matrix4f1.m12 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m32 * matrix4f1.m32;
            this.m23 = matrix4f2.m02 * matrix4f1.m03 + matrix4f2.m12 * matrix4f1.m13 + matrix4f2.m22 * matrix4f1.m23 + matrix4f2.m32 * matrix4f1.m33;
            this.m30 = matrix4f2.m03 * matrix4f1.m00 + matrix4f2.m13 * matrix4f1.m10 + matrix4f2.m23 * matrix4f1.m20 + matrix4f2.m33 * matrix4f1.m30;
            this.m31 = matrix4f2.m03 * matrix4f1.m01 + matrix4f2.m13 * matrix4f1.m11 + matrix4f2.m23 * matrix4f1.m21 + matrix4f2.m33 * matrix4f1.m31;
            this.m32 = matrix4f2.m03 * matrix4f1.m02 + matrix4f2.m13 * matrix4f1.m12 + matrix4f2.m23 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m32;
            this.m33 = matrix4f2.m03 * matrix4f1.m03 + matrix4f2.m13 * matrix4f1.m13 + matrix4f2.m23 * matrix4f1.m23 + matrix4f2.m33 * matrix4f1.m33;
        } else {
            float float0 = matrix4f2.m00 * matrix4f1.m00 + matrix4f2.m10 * matrix4f1.m10 + matrix4f2.m20 * matrix4f1.m20 + matrix4f2.m30 * matrix4f1.m30;
            float float1 = matrix4f2.m00 * matrix4f1.m01 + matrix4f2.m10 * matrix4f1.m11 + matrix4f2.m20 * matrix4f1.m21 + matrix4f2.m30 * matrix4f1.m31;
            float float2 = matrix4f2.m00 * matrix4f1.m02 + matrix4f2.m10 * matrix4f1.m12 + matrix4f2.m20 * matrix4f1.m22 + matrix4f2.m30 * matrix4f1.m32;
            float float3 = matrix4f2.m00 * matrix4f1.m03 + matrix4f2.m10 * matrix4f1.m13 + matrix4f2.m20 * matrix4f1.m23 + matrix4f2.m30 * matrix4f1.m33;
            float float4 = matrix4f2.m01 * matrix4f1.m00 + matrix4f2.m11 * matrix4f1.m10 + matrix4f2.m21 * matrix4f1.m20 + matrix4f2.m31 * matrix4f1.m30;
            float float5 = matrix4f2.m01 * matrix4f1.m01 + matrix4f2.m11 * matrix4f1.m11 + matrix4f2.m21 * matrix4f1.m21 + matrix4f2.m31 * matrix4f1.m31;
            float float6 = matrix4f2.m01 * matrix4f1.m02 + matrix4f2.m11 * matrix4f1.m12 + matrix4f2.m21 * matrix4f1.m22 + matrix4f2.m31 * matrix4f1.m32;
            float float7 = matrix4f2.m01 * matrix4f1.m03 + matrix4f2.m11 * matrix4f1.m13 + matrix4f2.m21 * matrix4f1.m23 + matrix4f2.m31 * matrix4f1.m33;
            float float8 = matrix4f2.m02 * matrix4f1.m00 + matrix4f2.m12 * matrix4f1.m10 + matrix4f2.m22 * matrix4f1.m20 + matrix4f2.m32 * matrix4f1.m30;
            float float9 = matrix4f2.m02 * matrix4f1.m01 + matrix4f2.m12 * matrix4f1.m11 + matrix4f2.m22 * matrix4f1.m21 + matrix4f2.m32 * matrix4f1.m31;
            float float10 = matrix4f2.m02 * matrix4f1.m02 + matrix4f2.m12 * matrix4f1.m12 + matrix4f2.m22 * matrix4f1.m22 + matrix4f2.m32 * matrix4f1.m32;
            float float11 = matrix4f2.m02 * matrix4f1.m03 + matrix4f2.m12 * matrix4f1.m13 + matrix4f2.m22 * matrix4f1.m23 + matrix4f2.m32 * matrix4f1.m33;
            float float12 = matrix4f2.m03 * matrix4f1.m00 + matrix4f2.m13 * matrix4f1.m10 + matrix4f2.m23 * matrix4f1.m20 + matrix4f2.m33 * matrix4f1.m30;
            float float13 = matrix4f2.m03 * matrix4f1.m01 + matrix4f2.m13 * matrix4f1.m11 + matrix4f2.m23 * matrix4f1.m21 + matrix4f2.m33 * matrix4f1.m31;
            float float14 = matrix4f2.m03 * matrix4f1.m02 + matrix4f2.m13 * matrix4f1.m12 + matrix4f2.m23 * matrix4f1.m22 + matrix4f2.m33 * matrix4f1.m32;
            float float15 = matrix4f2.m03 * matrix4f1.m03 + matrix4f2.m13 * matrix4f1.m13 + matrix4f2.m23 * matrix4f1.m23 + matrix4f2.m33 * matrix4f1.m33;
            this.m00 = float0;
            this.m01 = float1;
            this.m02 = float2;
            this.m03 = float3;
            this.m10 = float4;
            this.m11 = float5;
            this.m12 = float6;
            this.m13 = float7;
            this.m20 = float8;
            this.m21 = float9;
            this.m22 = float10;
            this.m23 = float11;
            this.m30 = float12;
            this.m31 = float13;
            this.m32 = float14;
            this.m33 = float15;
        }
    }

    public boolean equals(Matrix4f matrix4f0) {
        try {
            return this.m00 == matrix4f0.m00
                && this.m01 == matrix4f0.m01
                && this.m02 == matrix4f0.m02
                && this.m03 == matrix4f0.m03
                && this.m10 == matrix4f0.m10
                && this.m11 == matrix4f0.m11
                && this.m12 == matrix4f0.m12
                && this.m13 == matrix4f0.m13
                && this.m20 == matrix4f0.m20
                && this.m21 == matrix4f0.m21
                && this.m22 == matrix4f0.m22
                && this.m23 == matrix4f0.m23
                && this.m30 == matrix4f0.m30
                && this.m31 == matrix4f0.m31
                && this.m32 == matrix4f0.m32
                && this.m33 == matrix4f0.m33;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    @Override
    public boolean equals(Object object) {
        try {
            Matrix4f matrix4f0 = (Matrix4f)object;
            return this.m00 == matrix4f0.m00
                && this.m01 == matrix4f0.m01
                && this.m02 == matrix4f0.m02
                && this.m03 == matrix4f0.m03
                && this.m10 == matrix4f0.m10
                && this.m11 == matrix4f0.m11
                && this.m12 == matrix4f0.m12
                && this.m13 == matrix4f0.m13
                && this.m20 == matrix4f0.m20
                && this.m21 == matrix4f0.m21
                && this.m22 == matrix4f0.m22
                && this.m23 == matrix4f0.m23
                && this.m30 == matrix4f0.m30
                && this.m31 == matrix4f0.m31
                && this.m32 == matrix4f0.m32
                && this.m33 == matrix4f0.m33;
        } catch (ClassCastException classCastException) {
            return false;
        } catch (NullPointerException nullPointerException) {
            return false;
        }
    }

    public boolean epsilonEquals(Matrix4f matrix4f0, float float0) {
        boolean boolean0 = true;
        if (Math.abs(this.m00 - matrix4f0.m00) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m01 - matrix4f0.m01) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m02 - matrix4f0.m02) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m03 - matrix4f0.m03) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m10 - matrix4f0.m10) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m11 - matrix4f0.m11) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m12 - matrix4f0.m12) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m13 - matrix4f0.m13) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m20 - matrix4f0.m20) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m21 - matrix4f0.m21) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m22 - matrix4f0.m22) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m23 - matrix4f0.m23) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m30 - matrix4f0.m30) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m31 - matrix4f0.m31) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m32 - matrix4f0.m32) > float0) {
            boolean0 = false;
        }

        if (Math.abs(this.m33 - matrix4f0.m33) > float0) {
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
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m03);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m10);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m11);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m12);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m13);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m20);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m21);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m22);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m23);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m30);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m31);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m32);
        long0 = 31L * long0 + VecMathUtil.floatToIntBits(this.m33);
        return (int)(long0 ^ long0 >> 32);
    }

    public final void transform(Tuple4f tuple4f0, Tuple4f tuple4f1) {
        float float0 = this.m00 * tuple4f0.x + this.m01 * tuple4f0.y + this.m02 * tuple4f0.z + this.m03 * tuple4f0.w;
        float float1 = this.m10 * tuple4f0.x + this.m11 * tuple4f0.y + this.m12 * tuple4f0.z + this.m13 * tuple4f0.w;
        float float2 = this.m20 * tuple4f0.x + this.m21 * tuple4f0.y + this.m22 * tuple4f0.z + this.m23 * tuple4f0.w;
        tuple4f1.w = this.m30 * tuple4f0.x + this.m31 * tuple4f0.y + this.m32 * tuple4f0.z + this.m33 * tuple4f0.w;
        tuple4f1.x = float0;
        tuple4f1.y = float1;
        tuple4f1.z = float2;
    }

    public final void transform(Tuple4f tuple4f) {
        float float0 = this.m00 * tuple4f.x + this.m01 * tuple4f.y + this.m02 * tuple4f.z + this.m03 * tuple4f.w;
        float float1 = this.m10 * tuple4f.x + this.m11 * tuple4f.y + this.m12 * tuple4f.z + this.m13 * tuple4f.w;
        float float2 = this.m20 * tuple4f.x + this.m21 * tuple4f.y + this.m22 * tuple4f.z + this.m23 * tuple4f.w;
        tuple4f.w = this.m30 * tuple4f.x + this.m31 * tuple4f.y + this.m32 * tuple4f.z + this.m33 * tuple4f.w;
        tuple4f.x = float0;
        tuple4f.y = float1;
        tuple4f.z = float2;
    }

    public final void transform(Point3f point3f0, Point3f point3f1) {
        float float0 = this.m00 * point3f0.x + this.m01 * point3f0.y + this.m02 * point3f0.z + this.m03;
        float float1 = this.m10 * point3f0.x + this.m11 * point3f0.y + this.m12 * point3f0.z + this.m13;
        point3f1.z = this.m20 * point3f0.x + this.m21 * point3f0.y + this.m22 * point3f0.z + this.m23;
        point3f1.x = float0;
        point3f1.y = float1;
    }

    public final void transform(Point3f point3f) {
        float float0 = this.m00 * point3f.x + this.m01 * point3f.y + this.m02 * point3f.z + this.m03;
        float float1 = this.m10 * point3f.x + this.m11 * point3f.y + this.m12 * point3f.z + this.m13;
        point3f.z = this.m20 * point3f.x + this.m21 * point3f.y + this.m22 * point3f.z + this.m23;
        point3f.x = float0;
        point3f.y = float1;
    }

    public final void transform(Vector3f vector3f0, Vector3f vector3f1) {
        float float0 = this.m00 * vector3f0.x + this.m01 * vector3f0.y + this.m02 * vector3f0.z;
        float float1 = this.m10 * vector3f0.x + this.m11 * vector3f0.y + this.m12 * vector3f0.z;
        vector3f1.z = this.m20 * vector3f0.x + this.m21 * vector3f0.y + this.m22 * vector3f0.z;
        vector3f1.x = float0;
        vector3f1.y = float1;
    }

    public final void transform(Vector3f vector3f) {
        float float0 = this.m00 * vector3f.x + this.m01 * vector3f.y + this.m02 * vector3f.z;
        float float1 = this.m10 * vector3f.x + this.m11 * vector3f.y + this.m12 * vector3f.z;
        vector3f.z = this.m20 * vector3f.x + this.m21 * vector3f.y + this.m22 * vector3f.z;
        vector3f.x = float0;
        vector3f.y = float1;
    }

    public final void setRotation(Matrix3d matrix3d) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = (float)(matrix3d.m00 * doubles1[0]);
        this.m01 = (float)(matrix3d.m01 * doubles1[1]);
        this.m02 = (float)(matrix3d.m02 * doubles1[2]);
        this.m10 = (float)(matrix3d.m10 * doubles1[0]);
        this.m11 = (float)(matrix3d.m11 * doubles1[1]);
        this.m12 = (float)(matrix3d.m12 * doubles1[2]);
        this.m20 = (float)(matrix3d.m20 * doubles1[0]);
        this.m21 = (float)(matrix3d.m21 * doubles1[1]);
        this.m22 = (float)(matrix3d.m22 * doubles1[2]);
    }

    public final void setRotation(Matrix3f matrix3f) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = (float)(matrix3f.m00 * doubles1[0]);
        this.m01 = (float)(matrix3f.m01 * doubles1[1]);
        this.m02 = (float)(matrix3f.m02 * doubles1[2]);
        this.m10 = (float)(matrix3f.m10 * doubles1[0]);
        this.m11 = (float)(matrix3f.m11 * doubles1[1]);
        this.m12 = (float)(matrix3f.m12 * doubles1[2]);
        this.m20 = (float)(matrix3f.m20 * doubles1[0]);
        this.m21 = (float)(matrix3f.m21 * doubles1[1]);
        this.m22 = (float)(matrix3f.m22 * doubles1[2]);
    }

    public final void setRotation(Quat4f quat4f) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = (float)((1.0F - 2.0F * quat4f.y * quat4f.y - 2.0F * quat4f.z * quat4f.z) * doubles1[0]);
        this.m10 = (float)(2.0F * (quat4f.x * quat4f.y + quat4f.w * quat4f.z) * doubles1[0]);
        this.m20 = (float)(2.0F * (quat4f.x * quat4f.z - quat4f.w * quat4f.y) * doubles1[0]);
        this.m01 = (float)(2.0F * (quat4f.x * quat4f.y - quat4f.w * quat4f.z) * doubles1[1]);
        this.m11 = (float)((1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.z * quat4f.z) * doubles1[1]);
        this.m21 = (float)(2.0F * (quat4f.y * quat4f.z + quat4f.w * quat4f.x) * doubles1[1]);
        this.m02 = (float)(2.0F * (quat4f.x * quat4f.z + quat4f.w * quat4f.y) * doubles1[2]);
        this.m12 = (float)(2.0F * (quat4f.y * quat4f.z - quat4f.w * quat4f.x) * doubles1[2]);
        this.m22 = (float)((1.0F - 2.0F * quat4f.x * quat4f.x - 2.0F * quat4f.y * quat4f.y) * doubles1[2]);
    }

    public final void setRotation(Quat4d quat4d) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        this.m00 = (float)((1.0 - 2.0 * quat4d.y * quat4d.y - 2.0 * quat4d.z * quat4d.z) * doubles1[0]);
        this.m10 = (float)(2.0 * (quat4d.x * quat4d.y + quat4d.w * quat4d.z) * doubles1[0]);
        this.m20 = (float)(2.0 * (quat4d.x * quat4d.z - quat4d.w * quat4d.y) * doubles1[0]);
        this.m01 = (float)(2.0 * (quat4d.x * quat4d.y - quat4d.w * quat4d.z) * doubles1[1]);
        this.m11 = (float)((1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.z * quat4d.z) * doubles1[1]);
        this.m21 = (float)(2.0 * (quat4d.y * quat4d.z + quat4d.w * quat4d.x) * doubles1[1]);
        this.m02 = (float)(2.0 * (quat4d.x * quat4d.z + quat4d.w * quat4d.y) * doubles1[2]);
        this.m12 = (float)(2.0 * (quat4d.y * quat4d.z - quat4d.w * quat4d.x) * doubles1[2]);
        this.m22 = (float)((1.0 - 2.0 * quat4d.x * quat4d.x - 2.0 * quat4d.y * quat4d.y) * doubles1[2]);
    }

    public final void setRotation(AxisAngle4f axisAngle4f) {
        double[] doubles0 = new double[9];
        double[] doubles1 = new double[3];
        this.getScaleRotate(doubles1, doubles0);
        double double0 = Math.sqrt(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z);
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
            double double1 = axisAngle4f.x * double0;
            double double2 = axisAngle4f.y * double0;
            double double3 = axisAngle4f.z * double0;
            double double4 = Math.sin(axisAngle4f.angle);
            double double5 = Math.cos(axisAngle4f.angle);
            double double6 = 1.0 - double5;
            double double7 = axisAngle4f.x * axisAngle4f.z;
            double double8 = axisAngle4f.x * axisAngle4f.y;
            double double9 = axisAngle4f.y * axisAngle4f.z;
            this.m00 = (float)((double6 * double1 * double1 + double5) * doubles1[0]);
            this.m01 = (float)((double6 * double8 - double4 * double3) * doubles1[1]);
            this.m02 = (float)((double6 * double7 + double4 * double2) * doubles1[2]);
            this.m10 = (float)((double6 * double8 + double4 * double3) * doubles1[0]);
            this.m11 = (float)((double6 * double2 * double2 + double5) * doubles1[1]);
            this.m12 = (float)((double6 * double9 - double4 * double1) * doubles1[2]);
            this.m20 = (float)((double6 * double7 - double4 * double2) * doubles1[0]);
            this.m21 = (float)((double6 * double9 + double4 * double1) * doubles1[1]);
            this.m22 = (float)((double6 * double3 * double3 + double5) * doubles1[2]);
        }
    }

    public final void setZero() {
        this.m00 = 0.0F;
        this.m01 = 0.0F;
        this.m02 = 0.0F;
        this.m03 = 0.0F;
        this.m10 = 0.0F;
        this.m11 = 0.0F;
        this.m12 = 0.0F;
        this.m13 = 0.0F;
        this.m20 = 0.0F;
        this.m21 = 0.0F;
        this.m22 = 0.0F;
        this.m23 = 0.0F;
        this.m30 = 0.0F;
        this.m31 = 0.0F;
        this.m32 = 0.0F;
        this.m33 = 0.0F;
    }

    public final void negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m03 = -this.m03;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m13 = -this.m13;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
        this.m23 = -this.m23;
        this.m30 = -this.m30;
        this.m31 = -this.m31;
        this.m32 = -this.m32;
        this.m33 = -this.m33;
    }

    public final void negate(Matrix4f matrix4f0) {
        this.m00 = -matrix4f0.m00;
        this.m01 = -matrix4f0.m01;
        this.m02 = -matrix4f0.m02;
        this.m03 = -matrix4f0.m03;
        this.m10 = -matrix4f0.m10;
        this.m11 = -matrix4f0.m11;
        this.m12 = -matrix4f0.m12;
        this.m13 = -matrix4f0.m13;
        this.m20 = -matrix4f0.m20;
        this.m21 = -matrix4f0.m21;
        this.m22 = -matrix4f0.m22;
        this.m23 = -matrix4f0.m23;
        this.m30 = -matrix4f0.m30;
        this.m31 = -matrix4f0.m31;
        this.m32 = -matrix4f0.m32;
        this.m33 = -matrix4f0.m33;
    }

    private final void getScaleRotate(double[] doubles1, double[] doubles2) {
        double[] doubles0 = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
        Matrix3d.compute_svd(doubles0, doubles1, doubles2);
    }

    @Override
    public Object clone() {
        Object object = null;

        try {
            return (Matrix4f)super.clone();
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

    public final float getM03() {
        return this.m03;
    }

    public final void setM03(float float0) {
        this.m03 = float0;
    }

    public final float getM13() {
        return this.m13;
    }

    public final void setM13(float float0) {
        this.m13 = float0;
    }

    public final float getM23() {
        return this.m23;
    }

    public final void setM23(float float0) {
        this.m23 = float0;
    }

    public final float getM30() {
        return this.m30;
    }

    public final void setM30(float float0) {
        this.m30 = float0;
    }

    public final float getM31() {
        return this.m31;
    }

    public final void setM31(float float0) {
        this.m31 = float0;
    }

    public final float getM32() {
        return this.m32;
    }

    public final void setM32(float float0) {
        this.m32 = float0;
    }

    public final float getM33() {
        return this.m33;
    }

    public final void setM33(float float0) {
        this.m33 = float0;
    }
}
