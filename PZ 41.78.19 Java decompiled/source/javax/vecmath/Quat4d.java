// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package javax.vecmath;

import java.io.Serializable;

public class Quat4d extends Tuple4d implements Serializable {
    static final long serialVersionUID = 7577479888820201099L;
    static final double EPS = 1.0E-12;
    static final double EPS2 = 1.0E-30;
    static final double PIO2 = 1.57079632679;

    public Quat4d(double double4, double double3, double double2, double double1) {
        double double0 = 1.0 / Math.sqrt(double4 * double4 + double3 * double3 + double2 * double2 + double1 * double1);
        this.x = double4 * double0;
        this.y = double3 * double0;
        this.z = double2 * double0;
        this.w = double1 * double0;
    }

    public Quat4d(double[] doubles) {
        double double0 = 1.0 / Math.sqrt(doubles[0] * doubles[0] + doubles[1] * doubles[1] + doubles[2] * doubles[2] + doubles[3] * doubles[3]);
        this.x = doubles[0] * double0;
        this.y = doubles[1] * double0;
        this.z = doubles[2] * double0;
        this.w = doubles[3] * double0;
    }

    public Quat4d(Quat4d quat4d1) {
        super(quat4d1);
    }

    public Quat4d(Quat4f quat4f) {
        super(quat4f);
    }

    public Quat4d(Tuple4f tuple4f) {
        double double0 = 1.0 / Math.sqrt(tuple4f.x * tuple4f.x + tuple4f.y * tuple4f.y + tuple4f.z * tuple4f.z + tuple4f.w * tuple4f.w);
        this.x = tuple4f.x * double0;
        this.y = tuple4f.y * double0;
        this.z = tuple4f.z * double0;
        this.w = tuple4f.w * double0;
    }

    public Quat4d(Tuple4d tuple4d) {
        double double0 = 1.0 / Math.sqrt(tuple4d.x * tuple4d.x + tuple4d.y * tuple4d.y + tuple4d.z * tuple4d.z + tuple4d.w * tuple4d.w);
        this.x = tuple4d.x * double0;
        this.y = tuple4d.y * double0;
        this.z = tuple4d.z * double0;
        this.w = tuple4d.w * double0;
    }

    public Quat4d() {
    }

    public final void conjugate(Quat4d quat4d0) {
        this.x = -quat4d0.x;
        this.y = -quat4d0.y;
        this.z = -quat4d0.z;
        this.w = quat4d0.w;
    }

    public final void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public final void mul(Quat4d quat4d2, Quat4d quat4d1) {
        if (this != quat4d2 && this != quat4d1) {
            this.w = quat4d2.w * quat4d1.w - quat4d2.x * quat4d1.x - quat4d2.y * quat4d1.y - quat4d2.z * quat4d1.z;
            this.x = quat4d2.w * quat4d1.x + quat4d1.w * quat4d2.x + quat4d2.y * quat4d1.z - quat4d2.z * quat4d1.y;
            this.y = quat4d2.w * quat4d1.y + quat4d1.w * quat4d2.y - quat4d2.x * quat4d1.z + quat4d2.z * quat4d1.x;
            this.z = quat4d2.w * quat4d1.z + quat4d1.w * quat4d2.z + quat4d2.x * quat4d1.y - quat4d2.y * quat4d1.x;
        } else {
            double double0 = quat4d2.w * quat4d1.w - quat4d2.x * quat4d1.x - quat4d2.y * quat4d1.y - quat4d2.z * quat4d1.z;
            double double1 = quat4d2.w * quat4d1.x + quat4d1.w * quat4d2.x + quat4d2.y * quat4d1.z - quat4d2.z * quat4d1.y;
            double double2 = quat4d2.w * quat4d1.y + quat4d1.w * quat4d2.y - quat4d2.x * quat4d1.z + quat4d2.z * quat4d1.x;
            this.z = quat4d2.w * quat4d1.z + quat4d1.w * quat4d2.z + quat4d2.x * quat4d1.y - quat4d2.y * quat4d1.x;
            this.w = double0;
            this.x = double1;
            this.y = double2;
        }
    }

    public final void mul(Quat4d quat4d0) {
        double double0 = this.w * quat4d0.w - this.x * quat4d0.x - this.y * quat4d0.y - this.z * quat4d0.z;
        double double1 = this.w * quat4d0.x + quat4d0.w * this.x + this.y * quat4d0.z - this.z * quat4d0.y;
        double double2 = this.w * quat4d0.y + quat4d0.w * this.y - this.x * quat4d0.z + this.z * quat4d0.x;
        this.z = this.w * quat4d0.z + quat4d0.w * this.z + this.x * quat4d0.y - this.y * quat4d0.x;
        this.w = double0;
        this.x = double1;
        this.y = double2;
    }

    public final void mulInverse(Quat4d quat4d3, Quat4d quat4d1) {
        Quat4d quat4d0 = new Quat4d(quat4d1);
        quat4d0.inverse();
        this.mul(quat4d3, quat4d0);
    }

    public final void mulInverse(Quat4d quat4d1) {
        Quat4d quat4d0 = new Quat4d(quat4d1);
        quat4d0.inverse();
        this.mul(quat4d0);
    }

    public final void inverse(Quat4d quat4d0) {
        double double0 = 1.0 / (quat4d0.w * quat4d0.w + quat4d0.x * quat4d0.x + quat4d0.y * quat4d0.y + quat4d0.z * quat4d0.z);
        this.w = double0 * quat4d0.w;
        this.x = -double0 * quat4d0.x;
        this.y = -double0 * quat4d0.y;
        this.z = -double0 * quat4d0.z;
    }

    public final void inverse() {
        double double0 = 1.0 / (this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
        this.w *= double0;
        this.x *= -double0;
        this.y *= -double0;
        this.z *= -double0;
    }

    public final void normalize(Quat4d quat4d0) {
        double double0 = quat4d0.x * quat4d0.x + quat4d0.y * quat4d0.y + quat4d0.z * quat4d0.z + quat4d0.w * quat4d0.w;
        if (double0 > 0.0) {
            double0 = 1.0 / Math.sqrt(double0);
            this.x = double0 * quat4d0.x;
            this.y = double0 * quat4d0.y;
            this.z = double0 * quat4d0.z;
            this.w = double0 * quat4d0.w;
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
            this.w = 0.0;
        }
    }

    public final void normalize() {
        double double0 = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if (double0 > 0.0) {
            double0 = 1.0 / Math.sqrt(double0);
            this.x *= double0;
            this.y *= double0;
            this.z *= double0;
            this.w *= double0;
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
            this.w = 0.0;
        }
    }

    public final void set(Matrix4f matrix4f) {
        double double0 = 0.25 * (matrix4f.m00 + matrix4f.m11 + matrix4f.m22 + matrix4f.m33);
        if (double0 >= 0.0) {
            if (double0 >= 1.0E-30) {
                this.w = Math.sqrt(double0);
                double0 = 0.25 / this.w;
                this.x = (matrix4f.m21 - matrix4f.m12) * double0;
                this.y = (matrix4f.m02 - matrix4f.m20) * double0;
                this.z = (matrix4f.m10 - matrix4f.m01) * double0;
            } else {
                this.w = 0.0;
                double0 = -0.5 * (matrix4f.m11 + matrix4f.m22);
                if (double0 >= 0.0) {
                    if (double0 >= 1.0E-30) {
                        this.x = Math.sqrt(double0);
                        double0 = 1.0 / (2.0 * this.x);
                        this.y = matrix4f.m10 * double0;
                        this.z = matrix4f.m20 * double0;
                    } else {
                        this.x = 0.0;
                        double0 = 0.5 * (1.0 - matrix4f.m22);
                        if (double0 >= 1.0E-30) {
                            this.y = Math.sqrt(double0);
                            this.z = matrix4f.m21 / (2.0 * this.y);
                        } else {
                            this.y = 0.0;
                            this.z = 1.0;
                        }
                    }
                } else {
                    this.x = 0.0;
                    this.y = 0.0;
                    this.z = 1.0;
                }
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        }
    }

    public final void set(Matrix4d matrix4d) {
        double double0 = 0.25 * (matrix4d.m00 + matrix4d.m11 + matrix4d.m22 + matrix4d.m33);
        if (double0 >= 0.0) {
            if (double0 >= 1.0E-30) {
                this.w = Math.sqrt(double0);
                double0 = 0.25 / this.w;
                this.x = (matrix4d.m21 - matrix4d.m12) * double0;
                this.y = (matrix4d.m02 - matrix4d.m20) * double0;
                this.z = (matrix4d.m10 - matrix4d.m01) * double0;
            } else {
                this.w = 0.0;
                double0 = -0.5 * (matrix4d.m11 + matrix4d.m22);
                if (double0 >= 0.0) {
                    if (double0 >= 1.0E-30) {
                        this.x = Math.sqrt(double0);
                        double0 = 0.5 / this.x;
                        this.y = matrix4d.m10 * double0;
                        this.z = matrix4d.m20 * double0;
                    } else {
                        this.x = 0.0;
                        double0 = 0.5 * (1.0 - matrix4d.m22);
                        if (double0 >= 1.0E-30) {
                            this.y = Math.sqrt(double0);
                            this.z = matrix4d.m21 / (2.0 * this.y);
                        } else {
                            this.y = 0.0;
                            this.z = 1.0;
                        }
                    }
                } else {
                    this.x = 0.0;
                    this.y = 0.0;
                    this.z = 1.0;
                }
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        }
    }

    public final void set(Matrix3f matrix3f) {
        double double0 = 0.25 * (matrix3f.m00 + matrix3f.m11 + matrix3f.m22 + 1.0);
        if (double0 >= 0.0) {
            if (double0 >= 1.0E-30) {
                this.w = Math.sqrt(double0);
                double0 = 0.25 / this.w;
                this.x = (matrix3f.m21 - matrix3f.m12) * double0;
                this.y = (matrix3f.m02 - matrix3f.m20) * double0;
                this.z = (matrix3f.m10 - matrix3f.m01) * double0;
            } else {
                this.w = 0.0;
                double0 = -0.5 * (matrix3f.m11 + matrix3f.m22);
                if (double0 >= 0.0) {
                    if (double0 >= 1.0E-30) {
                        this.x = Math.sqrt(double0);
                        double0 = 0.5 / this.x;
                        this.y = matrix3f.m10 * double0;
                        this.z = matrix3f.m20 * double0;
                    } else {
                        this.x = 0.0;
                        double0 = 0.5 * (1.0 - matrix3f.m22);
                        if (double0 >= 1.0E-30) {
                            this.y = Math.sqrt(double0);
                            this.z = matrix3f.m21 / (2.0 * this.y);
                        }

                        this.y = 0.0;
                        this.z = 1.0;
                    }
                } else {
                    this.x = 0.0;
                    this.y = 0.0;
                    this.z = 1.0;
                }
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        }
    }

    public final void set(Matrix3d matrix3d) {
        double double0 = 0.25 * (matrix3d.m00 + matrix3d.m11 + matrix3d.m22 + 1.0);
        if (double0 >= 0.0) {
            if (double0 >= 1.0E-30) {
                this.w = Math.sqrt(double0);
                double0 = 0.25 / this.w;
                this.x = (matrix3d.m21 - matrix3d.m12) * double0;
                this.y = (matrix3d.m02 - matrix3d.m20) * double0;
                this.z = (matrix3d.m10 - matrix3d.m01) * double0;
            } else {
                this.w = 0.0;
                double0 = -0.5 * (matrix3d.m11 + matrix3d.m22);
                if (double0 >= 0.0) {
                    if (double0 >= 1.0E-30) {
                        this.x = Math.sqrt(double0);
                        double0 = 0.5 / this.x;
                        this.y = matrix3d.m10 * double0;
                        this.z = matrix3d.m20 * double0;
                    } else {
                        this.x = 0.0;
                        double0 = 0.5 * (1.0 - matrix3d.m22);
                        if (double0 >= 1.0E-30) {
                            this.y = Math.sqrt(double0);
                            this.z = matrix3d.m21 / (2.0 * this.y);
                        } else {
                            this.y = 0.0;
                            this.z = 1.0;
                        }
                    }
                } else {
                    this.x = 0.0;
                    this.y = 0.0;
                    this.z = 1.0;
                }
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
        }
    }

    public final void set(AxisAngle4f axisAngle4f) {
        double double0 = Math.sqrt(axisAngle4f.x * axisAngle4f.x + axisAngle4f.y * axisAngle4f.y + axisAngle4f.z * axisAngle4f.z);
        if (double0 < 1.0E-12) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        } else {
            double double1 = Math.sin(axisAngle4f.angle / 2.0);
            double0 = 1.0 / double0;
            this.w = Math.cos(axisAngle4f.angle / 2.0);
            this.x = axisAngle4f.x * double0 * double1;
            this.y = axisAngle4f.y * double0 * double1;
            this.z = axisAngle4f.z * double0 * double1;
        }
    }

    public final void set(AxisAngle4d axisAngle4d) {
        double double0 = Math.sqrt(axisAngle4d.x * axisAngle4d.x + axisAngle4d.y * axisAngle4d.y + axisAngle4d.z * axisAngle4d.z);
        if (double0 < 1.0E-12) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        } else {
            double0 = 1.0 / double0;
            double double1 = Math.sin(axisAngle4d.angle / 2.0);
            this.w = Math.cos(axisAngle4d.angle / 2.0);
            this.x = axisAngle4d.x * double0 * double1;
            this.y = axisAngle4d.y * double0 * double1;
            this.z = axisAngle4d.z * double0 * double1;
        }
    }

    public final void interpolate(Quat4d quat4d0, double double5) {
        double double0 = this.x * quat4d0.x + this.y * quat4d0.y + this.z * quat4d0.z + this.w * quat4d0.w;
        if (double0 < 0.0) {
            quat4d0.x = -quat4d0.x;
            quat4d0.y = -quat4d0.y;
            quat4d0.z = -quat4d0.z;
            quat4d0.w = -quat4d0.w;
            double0 = -double0;
        }

        double double1;
        double double2;
        if (1.0 - double0 > 1.0E-12) {
            double double3 = Math.acos(double0);
            double double4 = Math.sin(double3);
            double1 = Math.sin((1.0 - double5) * double3) / double4;
            double2 = Math.sin(double5 * double3) / double4;
        } else {
            double1 = 1.0 - double5;
            double2 = double5;
        }

        this.w = double1 * this.w + double2 * quat4d0.w;
        this.x = double1 * this.x + double2 * quat4d0.x;
        this.y = double1 * this.y + double2 * quat4d0.y;
        this.z = double1 * this.z + double2 * quat4d0.z;
    }

    public final void interpolate(Quat4d quat4d0, Quat4d quat4d1, double double5) {
        double double0 = quat4d1.x * quat4d0.x + quat4d1.y * quat4d0.y + quat4d1.z * quat4d0.z + quat4d1.w * quat4d0.w;
        if (double0 < 0.0) {
            quat4d0.x = -quat4d0.x;
            quat4d0.y = -quat4d0.y;
            quat4d0.z = -quat4d0.z;
            quat4d0.w = -quat4d0.w;
            double0 = -double0;
        }

        double double1;
        double double2;
        if (1.0 - double0 > 1.0E-12) {
            double double3 = Math.acos(double0);
            double double4 = Math.sin(double3);
            double1 = Math.sin((1.0 - double5) * double3) / double4;
            double2 = Math.sin(double5 * double3) / double4;
        } else {
            double1 = 1.0 - double5;
            double2 = double5;
        }

        this.w = double1 * quat4d0.w + double2 * quat4d1.w;
        this.x = double1 * quat4d0.x + double2 * quat4d1.x;
        this.y = double1 * quat4d0.y + double2 * quat4d1.y;
        this.z = double1 * quat4d0.z + double2 * quat4d1.z;
    }
}
