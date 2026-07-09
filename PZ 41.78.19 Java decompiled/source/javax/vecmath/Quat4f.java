// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Quat4f extends Tuple4f implements Serializable {
    static final long serialVersionUID = 2675933778405442383L;
    static final double EPS = 1.0E-6;
    static final double EPS2 = 1.0E-30;
    static final double PIO2 = 1.57079632679;

    public Quat4f(float float4, float float3, float float2, float float1) {
        float float0 = (float)(1.0 / Math.sqrt(float4 * float4 + float3 * float3 + float2 * float2 + float1 * float1));
        this.x = float4 * float0;
        this.y = float3 * float0;
        this.z = float2 * float0;
        this.w = float1 * float0;
    }

    public Quat4f(float[] floats) {
        float float0 = (float)(1.0 / Math.sqrt(floats[0] * floats[0] + floats[1] * floats[1] + floats[2] * floats[2] + floats[3] * floats[3]));
        this.x = floats[0] * float0;
        this.y = floats[1] * float0;
        this.z = floats[2] * float0;
        this.w = floats[3] * float0;
    }

    public Quat4f(Quat4f quat4f1) {
        super(quat4f1);
    }

    public Quat4f(Quat4d quat4d) {
        super(quat4d);
    }

    public Quat4f(Tuple4f tuple4f) {
        float float0 = (float)(1.0 / Math.sqrt(tuple4f.x * tuple4f.x + tuple4f.y * tuple4f.y + tuple4f.z * tuple4f.z + tuple4f.w * tuple4f.w));
        this.x = tuple4f.x * float0;
        this.y = tuple4f.y * float0;
        this.z = tuple4f.z * float0;
        this.w = tuple4f.w * float0;
    }

    public Quat4f(Tuple4d tuple4d) {
        double double0 = 1.0 / Math.sqrt(tuple4d.x * tuple4d.x + tuple4d.y * tuple4d.y + tuple4d.z * tuple4d.z + tuple4d.w * tuple4d.w);
        this.x = (float)(tuple4d.x * double0);
        this.y = (float)(tuple4d.y * double0);
        this.z = (float)(tuple4d.z * double0);
        this.w = (float)(tuple4d.w * double0);
    }

    public Quat4f() {
    }

    public final void conjugate(Quat4f quat4f0) {
        this.x = -quat4f0.x;
        this.y = -quat4f0.y;
        this.z = -quat4f0.z;
        this.w = quat4f0.w;
    }

    public final void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public final void mul(Quat4f quat4f2, Quat4f quat4f1) {
        if (this != quat4f2 && this != quat4f1) {
            this.w = quat4f2.w * quat4f1.w - quat4f2.x * quat4f1.x - quat4f2.y * quat4f1.y - quat4f2.z * quat4f1.z;
            this.x = quat4f2.w * quat4f1.x + quat4f1.w * quat4f2.x + quat4f2.y * quat4f1.z - quat4f2.z * quat4f1.y;
            this.y = quat4f2.w * quat4f1.y + quat4f1.w * quat4f2.y - quat4f2.x * quat4f1.z + quat4f2.z * quat4f1.x;
            this.z = quat4f2.w * quat4f1.z + quat4f1.w * quat4f2.z + quat4f2.x * quat4f1.y - quat4f2.y * quat4f1.x;
        } else {
            float float0 = quat4f2.w * quat4f1.w - quat4f2.x * quat4f1.x - quat4f2.y * quat4f1.y - quat4f2.z * quat4f1.z;
            float float1 = quat4f2.w * quat4f1.x + quat4f1.w * quat4f2.x + quat4f2.y * quat4f1.z - quat4f2.z * quat4f1.y;
            float float2 = quat4f2.w * quat4f1.y + quat4f1.w * quat4f2.y - quat4f2.x * quat4f1.z + quat4f2.z * quat4f1.x;
            this.z = quat4f2.w * quat4f1.z + quat4f1.w * quat4f2.z + quat4f2.x * quat4f1.y - quat4f2.y * quat4f1.x;
            this.w = float0;
            this.x = float1;
            this.y = float2;
        }
    }

    public final void mul(Quat4f quat4f0) {
        float float0 = this.w * quat4f0.w - this.x * quat4f0.x - this.y * quat4f0.y - this.z * quat4f0.z;
        float float1 = this.w * quat4f0.x + quat4f0.w * this.x + this.y * quat4f0.z - this.z * quat4f0.y;
        float float2 = this.w * quat4f0.y + quat4f0.w * this.y - this.x * quat4f0.z + this.z * quat4f0.x;
        this.z = this.w * quat4f0.z + quat4f0.w * this.z + this.x * quat4f0.y - this.y * quat4f0.x;
        this.w = float0;
        this.x = float1;
        this.y = float2;
    }

    public final void mulInverse(Quat4f quat4f3, Quat4f quat4f1) {
        Quat4f quat4f0 = new Quat4f(quat4f1);
        quat4f0.inverse();
        this.mul(quat4f3, quat4f0);
    }

    public final void mulInverse(Quat4f quat4f1) {
        Quat4f quat4f0 = new Quat4f(quat4f1);
        quat4f0.inverse();
        this.mul(quat4f0);
    }

    public final void inverse(Quat4f quat4f0) {
        float float0 = 1.0F / (quat4f0.w * quat4f0.w + quat4f0.x * quat4f0.x + quat4f0.y * quat4f0.y + quat4f0.z * quat4f0.z);
        this.w = float0 * quat4f0.w;
        this.x = -float0 * quat4f0.x;
        this.y = -float0 * quat4f0.y;
        this.z = -float0 * quat4f0.z;
    }

    public final void inverse() {
        float float0 = 1.0F / (this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
        this.w *= float0;
        this.x *= -float0;
        this.y *= -float0;
        this.z *= -float0;
    }

    public final void normalize(Quat4f quat4f0) {
        float float0 = quat4f0.x * quat4f0.x + quat4f0.y * quat4f0.y + quat4f0.z * quat4f0.z + quat4f0.w * quat4f0.w;
        if (float0 > 0.0F) {
            float0 = 1.0F / (float)Math.sqrt(float0);
            this.x = float0 * quat4f0.x;
            this.y = float0 * quat4f0.y;
            this.z = float0 * quat4f0.z;
            this.w = float0 * quat4f0.w;
        } else {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
            this.w = 0.0F;
        }
    }

    public final void normalize() {
        float float0 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if (float0 > 0.0F) {
            float0 = 1.0F / (float)Math.sqrt(float0);
            this.x *= float0;
            this.y *= float0;
            this.z *= float0;
            this.w *= float0;
        } else {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
            this.w = 0.0F;
        }
    }

    public final void set(Matrix4f matrix4f) {
        float float0 = 0.25F * (matrix4f.m00 + matrix4f.m11 + matrix4f.m22 + matrix4f.m33);
        if (float0 >= 0.0F) {
            if (float0 >= 1.0E-30) {
                this.w = (float)Math.sqrt(float0);
                float0 = 0.25F / this.w;
                this.x = (matrix4f.m21 - matrix4f.m12) * float0;
                this.y = (matrix4f.m02 - matrix4f.m20) * float0;
                this.z = (matrix4f.m10 - matrix4f.m01) * float0;
            } else {
                this.w = 0.0F;
                float0 = -0.5F * (matrix4f.m11 + matrix4f.m22);
                if (float0 >= 0.0F) {
                    if (float0 >= 1.0E-30) {
                        this.x = (float)Math.sqrt(float0);
                        float0 = 1.0F / (2.0F * this.x);
                        this.y = matrix4f.m10 * float0;
                        this.z = matrix4f.m20 * float0;
                    } else {
                        this.x = 0.0F;
                        float0 = 0.5F * (1.0F - matrix4f.m22);
                        if (float0 >= 1.0E-30) {
                            this.y = (float)Math.sqrt(float0);
                            this.z = matrix4f.m21 / (2.0F * this.y);
                        } else {
                            this.y = 0.0F;
                            this.z = 1.0F;
                        }
                    }
                } else {
                    this.x = 0.0F;
                    this.y = 0.0F;
                    this.z = 1.0F;
                }
            }
        } else {
            this.w = 0.0F;
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
        }
    }

    public final void set(Matrix4d matrix4d) {
        double double0 = 0.25 * (matrix4d.m00 + matrix4d.m11 + matrix4d.m22 + matrix4d.m33);
        if (double0 >= 0.0) {
            if (double0 >= 1.0E-30) {
                this.w = (float)Math.sqrt(double0);
                double0 = 0.25 / this.w;
                this.x = (float)((matrix4d.m21 - matrix4d.m12) * double0);
                this.y = (float)((matrix4d.m02 - matrix4d.m20) * double0);
                this.z = (float)((matrix4d.m10 - matrix4d.m01) * double0);
            } else {
                this.w = 0.0F;
                double0 = -0.5 * (matrix4d.m11 + matrix4d.m22);
                if (double0 >= 0.0) {
                    if (double0 >= 1.0E-30) {
                        this.x = (float)Math.sqrt(double0);
                        double0 = 0.5 / this.x;
                        this.y = (float)(matrix4d.m10 * double0);
                        this.z = (float)(matrix4d.m20 * double0);
                    } else {
                        this.x = 0.0F;
                        double0 = 0.5 * (1.0 - matrix4d.m22);
                        if (double0 >= 1.0E-30) {
                            this.y = (float)Math.sqrt(double0);
                            this.z = (float)(matrix4d.m21 / (2.0 * this.y));
                        } else {
                            this.y = 0.0F;
                            this.z = 1.0F;
                        }
                    }
                } else {
                    this.x = 0.0F;
                    this.y = 0.0F;
                    this.z = 1.0F;
                }
            }
        } else {
            this.w = 0.0F;
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
        }
    }

    public final void set(Matrix3f matrix3f) {
        float float0 = 0.25F * (matrix3f.m00 + matrix3f.m11 + matrix3f.m22 + 1.0F);
        if (float0 >= 0.0F) {
            if (float0 >= 1.0E-30) {
                this.w = (float)Math.sqrt(float0);
                float0 = 0.25F / this.w;
                this.x = (matrix3f.m21 - matrix3f.m12) * float0;
                this.y = (matrix3f.m02 - matrix3f.m20) * float0;
                this.z = (matrix3f.m10 - matrix3f.m01) * float0;
            } else {
                this.w = 0.0F;
                float0 = -0.5F * (matrix3f.m11 + matrix3f.m22);
                if (float0 >= 0.0F) {
                    if (float0 >= 1.0E-30) {
                        this.x = (float)Math.sqrt(float0);
                        float0 = 0.5F / this.x;
                        this.y = matrix3f.m10 * float0;
                        this.z = matrix3f.m20 * float0;
                    } else {
                        this.x = 0.0F;
                        float0 = 0.5F * (1.0F - matrix3f.m22);
                        if (float0 >= 1.0E-30) {
                            this.y = (float)Math.sqrt(float0);
                            this.z = matrix3f.m21 / (2.0F * this.y);
                        } else {
                            this.y = 0.0F;
                            this.z = 1.0F;
                        }
                    }
                } else {
                    this.x = 0.0F;
                    this.y = 0.0F;
                    this.z = 1.0F;
                }
            }
        } else {
            this.w = 0.0F;
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
        }
    }

    public final void set(Matrix3d matrix3d) {
        double double0 = 0.25 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 + 1.0);
        if (double0 >= 0.0) {
            if (double0 >= 1.0E-30) {
                this.w = (float)Math.sqrt(double0);
                double0 = 0.25 / this.w;
                this.x = (float)((matrix3d.m21 - matrix3d.m12) * double0);
                this.y = (float)((matrix3d.m02 - matrix3d.m20) * double0);
                this.z = (float)((matrix3d.m10 - matrix3d.m01) * double0);
            } else {
                this.w = 0.0F;
                double0 = -0.5 * (matrix3d.m11 + matrix3d.m22);
                if (double0 >= 0.0) {
                    if (double0 >= 1.0E-30) {
                        this.x = (float)Math.sqrt(double0);
                        double0 = 0.5 / this.x;
                        this.y = (float)(matrix3d.m10 * double0);
                        this.z = (float)(matrix3d.m20 * double0);
                    } else {
                        this.x = 0.0F;
                        double0 = 0.5 * (1.0 - matrix3d.m22);
                        if (double0 >= 1.0E-30) {
                            this.y = (float)Math.sqrt(double0);
                            this.z = (float)(matrix3d.m21 / (2.0 * this.y));
                        } else {
                            this.y = 0.0F;
                            this.z = 1.0F;
                        }
                    }
                } else {
                    this.x = 0.0F;
                    this.y = 0.0F;
                    this.z = 1.0F;
                }
            }
        } else {
            this.w = 0.0F;
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
        }
    }

    public final void set(AxisAngle4f axisAngle4f) {
        float float0 = (float)Math.sqrt(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z);
        if (float0 < 1.0E-6) {
            this.w = 0.0F;
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
        } else {
            float0 = 1.0F / float0;
            float float1 = (float)Math.sin(axisAngle4f.angle / 2.0);
            this.w = (float)Math.cos(axisAngle4f.angle / 2.0);
            this.x = axisAngle4f.x * float0 * float1;
            this.y = axisAngle4f.y * float0 * float1;
            this.z = axisAngle4f.z * float0 * float1;
        }
    }

    public final void set(AxisAngle4d axisAngle4d) {
        float float0 = (float)(1.0 / Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z));
        if (float0 < 1.0E-6) {
            this.w = 0.0F;
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
        } else {
            float0 = 1.0F / float0;
            float float1 = (float)Math.sin(axisAngle4d.angle / 2.0);
            this.w = (float)Math.cos(axisAngle4d.angle / 2.0);
            this.x = (float)axisAngle4d.x * float0 * float1;
            this.y = (float)axisAngle4d.y * float0 * float1;
            this.z = (float)axisAngle4d.z * float0 * float1;
        }
    }

    public final void interpolate(Quat4f quat4f0, float float0) {
        double double0 = this.x * quat4f0.x + this.y * quat4f0.y + this.z * quat4f0.z + this.w * quat4f0.w;
        if (double0 < 0.0) {
            quat4f0.x = -quat4f0.x;
            quat4f0.y = -quat4f0.y;
            quat4f0.z = -quat4f0.z;
            quat4f0.w = -quat4f0.w;
            double0 = -double0;
        }

        double double1;
        double double2;
        if (1.0 - double0 > 1.0E-6) {
            double double3 = Math.acos(double0);
            double double4 = Math.sin(double3);
            double1 = Math.sin((1.0 - float0) * double3) / double4;
            double2 = Math.sin(float0 * double3) / double4;
        } else {
            double1 = 1.0 - float0;
            double2 = float0;
        }

        this.w = (float)(double1 * this.w + double2 * quat4f0.w);
        this.x = (float)(double1 * this.x + double2 * quat4f0.x);
        this.y = (float)(double1 * this.y + double2 * quat4f0.y);
        this.z = (float)(double1 * this.z + double2 * quat4f0.z);
    }

    public final void interpolate(Quat4f quat4f0, Quat4f quat4f1, float float0) {
        double double0 = quat4f1.x * quat4f0.x + quat4f1.y * quat4f0.y + quat4f1.z * quat4f0.z + quat4f1.w * quat4f0.w;
        if (double0 < 0.0) {
            quat4f0.x = -quat4f0.x;
            quat4f0.y = -quat4f0.y;
            quat4f0.z = -quat4f0.z;
            quat4f0.w = -quat4f0.w;
            double0 = -double0;
        }

        double double1;
        double double2;
        if (1.0 - double0 > 1.0E-6) {
            double double3 = Math.acos(double0);
            double double4 = Math.sin(double3);
            double1 = Math.sin((1.0 - float0) * double3) / double4;
            double2 = Math.sin(float0 * double3) / double4;
        } else {
            double1 = 1.0 - float0;
            double2 = float0;
        }

        this.w = (float)(double1 * quat4f0.w + double2 * quat4f1.w);
        this.x = (float)(double1 * quat4f0.x + double2 * quat4f1.x);
        this.y = (float)(double1 * quat4f0.y + double2 * quat4f1.y);
        this.z = (float)(double1 * quat4f0.z + double2 * quat4f1.z);
    }
}
