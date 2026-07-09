// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package org.joml;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.NumberFormat;

public class AxisAngle4f implements Externalizable {
    private static final long serialVersionUID = 1L;
    public float angle;
    public float x;
    public float y;
    public float z;

    public AxisAngle4f() {
        this.z = 1.0F;
    }

    public AxisAngle4f(AxisAngle4f arg0) {
        this.x = arg0.x;
        this.y = arg0.y;
        this.z = arg0.z;
        this.angle = (float)((arg0.angle < 0.0 ? (java.lang.Math.PI * 2) + arg0.angle % (java.lang.Math.PI * 2) : arg0.angle) % (java.lang.Math.PI * 2));
    }

    public AxisAngle4f(Quaternionfc arg0) {
        float float0 = Math.safeAcos(arg0.w());
        float float1 = Math.invsqrt(1.0F - arg0.w() * arg0.w());
        if (Float.isInfinite(float1)) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
        } else {
            this.x = arg0.x() * float1;
            this.y = arg0.y() * float1;
            this.z = arg0.z() * float1;
        }

        this.angle = float0 + float0;
    }

    public AxisAngle4f(float arg0, float arg1, float arg2, float arg3) {
        this.x = arg1;
        this.y = arg2;
        this.z = arg3;
        this.angle = (float)((arg0 < 0.0 ? (java.lang.Math.PI * 2) + arg0 % (java.lang.Math.PI * 2) : arg0) % (java.lang.Math.PI * 2));
    }

    public AxisAngle4f(float arg0, Vector3fc arg1) {
        this(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public AxisAngle4f set(AxisAngle4f arg0) {
        this.x = arg0.x;
        this.y = arg0.y;
        this.z = arg0.z;
        this.angle = arg0.angle;
        this.angle = (float)((this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2));
        return this;
    }

    public AxisAngle4f set(AxisAngle4d arg0) {
        this.x = (float)arg0.x;
        this.y = (float)arg0.y;
        this.z = (float)arg0.z;
        this.angle = (float)arg0.angle;
        this.angle = (float)((this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2));
        return this;
    }

    public AxisAngle4f set(float arg0, float arg1, float arg2, float arg3) {
        this.x = arg1;
        this.y = arg2;
        this.z = arg3;
        this.angle = (float)((arg0 < 0.0 ? (java.lang.Math.PI * 2) + arg0 % (java.lang.Math.PI * 2) : arg0) % (java.lang.Math.PI * 2));
        return this;
    }

    public AxisAngle4f set(float arg0, Vector3fc arg1) {
        return this.set(arg0, arg1.x(), arg1.y(), arg1.z());
    }

    public AxisAngle4f set(Quaternionfc arg0) {
        float float0 = Math.safeAcos(arg0.w());
        float float1 = Math.invsqrt(1.0F - arg0.w() * arg0.w());
        if (Float.isInfinite(float1)) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
        } else {
            this.x = arg0.x() * float1;
            this.y = arg0.y() * float1;
            this.z = arg0.z() * float1;
        }

        this.angle = float0 + float0;
        return this;
    }

    public AxisAngle4f set(Quaterniondc arg0) {
        double double0 = Math.safeAcos(arg0.w());
        double double1 = Math.invsqrt(1.0 - arg0.w() * arg0.w());
        if (Double.isInfinite(double1)) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
        } else {
            this.x = (float)(arg0.x() * double1);
            this.y = (float)(arg0.y() * double1);
            this.z = (float)(arg0.z() * double1);
        }

        this.angle = (float)(double0 + double0);
        return this;
    }

    public AxisAngle4f set(Matrix3fc arg0) {
        float float0 = arg0.m00();
        float float1 = arg0.m01();
        float float2 = arg0.m02();
        float float3 = arg0.m10();
        float float4 = arg0.m11();
        float float5 = arg0.m12();
        float float6 = arg0.m20();
        float float7 = arg0.m21();
        float float8 = arg0.m22();
        float float9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        float float10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        float float11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        float0 *= float9;
        float1 *= float9;
        float2 *= float9;
        float3 *= float10;
        float4 *= float10;
        float5 *= float10;
        float6 *= float11;
        float7 *= float11;
        float8 *= float11;
        float float12 = 1.0E-4F;
        float float13 = 0.001F;
        if (!(Math.abs(float3 - float1) < float12) || !(Math.abs(float6 - float2) < float12) || !(Math.abs(float7 - float5) < float12)) {
            float float14 = Math.sqrt((float5 - float7) * (float5 - float7) + (float6 - float2) * (float6 - float2) + (float1 - float3) * (float1 - float3));
            this.angle = Math.safeAcos((float0 + float4 + float8 - 1.0F) / 2.0F);
            this.x = (float5 - float7) / float14;
            this.y = (float6 - float2) / float14;
            this.z = (float1 - float3) / float14;
            return this;
        } else if (Math.abs(float3 + float1) < float13
            && Math.abs(float6 + float2) < float13
            && Math.abs(float7 + float5) < float13
            && Math.abs(float0 + float4 + float8 - 3.0F) < float13) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
            this.angle = 0.0F;
            return this;
        } else {
            this.angle = (float) java.lang.Math.PI;
            float float15 = (float0 + 1.0F) / 2.0F;
            float float16 = (float4 + 1.0F) / 2.0F;
            float float17 = (float8 + 1.0F) / 2.0F;
            float float18 = (float3 + float1) / 4.0F;
            float float19 = (float6 + float2) / 4.0F;
            float float20 = (float7 + float5) / 4.0F;
            if (float15 > float16 && float15 > float17) {
                this.x = Math.sqrt(float15);
                this.y = float18 / this.x;
                this.z = float19 / this.x;
            } else if (float16 > float17) {
                this.y = Math.sqrt(float16);
                this.x = float18 / this.y;
                this.z = float20 / this.y;
            } else {
                this.z = Math.sqrt(float17);
                this.x = float19 / this.z;
                this.y = float20 / this.z;
            }

            return this;
        }
    }

    public AxisAngle4f set(Matrix3dc arg0) {
        double double0 = arg0.m00();
        double double1 = arg0.m01();
        double double2 = arg0.m02();
        double double3 = arg0.m10();
        double double4 = arg0.m11();
        double double5 = arg0.m12();
        double double6 = arg0.m20();
        double double7 = arg0.m21();
        double double8 = arg0.m22();
        double double9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        double double10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        double double11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        double0 *= double9;
        double1 *= double9;
        double2 *= double9;
        double3 *= double10;
        double4 *= double10;
        double5 *= double10;
        double6 *= double11;
        double7 *= double11;
        double8 *= double11;
        double double12 = 1.0E-4;
        double double13 = 0.001;
        if (!(Math.abs(double3 - double1) < double12) || !(Math.abs(double6 - double2) < double12) || !(Math.abs(double7 - double5) < double12)) {
            double double14 = Math.sqrt(
                (double5 - double7) * (double5 - double7) + (double6 - double2) * (double6 - double2) + (double1 - double3) * (double1 - double3)
            );
            this.angle = (float)Math.safeAcos((double0 + double4 + double8 - 1.0) / 2.0);
            this.x = (float)((double5 - double7) / double14);
            this.y = (float)((double6 - double2) / double14);
            this.z = (float)((double1 - double3) / double14);
            return this;
        } else if (Math.abs(double3 + double1) < double13
            && Math.abs(double6 + double2) < double13
            && Math.abs(double7 + double5) < double13
            && Math.abs(double0 + double4 + double8 - 3.0) < double13) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
            this.angle = 0.0F;
            return this;
        } else {
            this.angle = (float) java.lang.Math.PI;
            double double15 = (double0 + 1.0) / 2.0;
            double double16 = (double4 + 1.0) / 2.0;
            double double17 = (double8 + 1.0) / 2.0;
            double double18 = (double3 + double1) / 4.0;
            double double19 = (double6 + double2) / 4.0;
            double double20 = (double7 + double5) / 4.0;
            if (double15 > double16 && double15 > double17) {
                this.x = (float)Math.sqrt(double15);
                this.y = (float)(double18 / this.x);
                this.z = (float)(double19 / this.x);
            } else if (double16 > double17) {
                this.y = (float)Math.sqrt(double16);
                this.x = (float)(double18 / this.y);
                this.z = (float)(double20 / this.y);
            } else {
                this.z = (float)Math.sqrt(double17);
                this.x = (float)(double19 / this.z);
                this.y = (float)(double20 / this.z);
            }

            return this;
        }
    }

    public AxisAngle4f set(Matrix4fc arg0) {
        float float0 = arg0.m00();
        float float1 = arg0.m01();
        float float2 = arg0.m02();
        float float3 = arg0.m10();
        float float4 = arg0.m11();
        float float5 = arg0.m12();
        float float6 = arg0.m20();
        float float7 = arg0.m21();
        float float8 = arg0.m22();
        float float9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        float float10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        float float11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        float0 *= float9;
        float1 *= float9;
        float2 *= float9;
        float3 *= float10;
        float4 *= float10;
        float5 *= float10;
        float6 *= float11;
        float7 *= float11;
        float8 *= float11;
        float float12 = 1.0E-4F;
        float float13 = 0.001F;
        if (!(Math.abs(float3 - float1) < float12) || !(Math.abs(float6 - float2) < float12) || !(Math.abs(float7 - float5) < float12)) {
            float float14 = Math.sqrt((float5 - float7) * (float5 - float7) + (float6 - float2) * (float6 - float2) + (float1 - float3) * (float1 - float3));
            this.angle = Math.safeAcos((float0 + float4 + float8 - 1.0F) / 2.0F);
            this.x = (float5 - float7) / float14;
            this.y = (float6 - float2) / float14;
            this.z = (float1 - float3) / float14;
            return this;
        } else if (Math.abs(float3 + float1) < float13
            && Math.abs(float6 + float2) < float13
            && Math.abs(float7 + float5) < float13
            && Math.abs(float0 + float4 + float8 - 3.0F) < float13) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
            this.angle = 0.0F;
            return this;
        } else {
            this.angle = (float) java.lang.Math.PI;
            float float15 = (float0 + 1.0F) / 2.0F;
            float float16 = (float4 + 1.0F) / 2.0F;
            float float17 = (float8 + 1.0F) / 2.0F;
            float float18 = (float3 + float1) / 4.0F;
            float float19 = (float6 + float2) / 4.0F;
            float float20 = (float7 + float5) / 4.0F;
            if (float15 > float16 && float15 > float17) {
                this.x = Math.sqrt(float15);
                this.y = float18 / this.x;
                this.z = float19 / this.x;
            } else if (float16 > float17) {
                this.y = Math.sqrt(float16);
                this.x = float18 / this.y;
                this.z = float20 / this.y;
            } else {
                this.z = Math.sqrt(float17);
                this.x = float19 / this.z;
                this.y = float20 / this.z;
            }

            return this;
        }
    }

    public AxisAngle4f set(Matrix4x3fc arg0) {
        float float0 = arg0.m00();
        float float1 = arg0.m01();
        float float2 = arg0.m02();
        float float3 = arg0.m10();
        float float4 = arg0.m11();
        float float5 = arg0.m12();
        float float6 = arg0.m20();
        float float7 = arg0.m21();
        float float8 = arg0.m22();
        float float9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        float float10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        float float11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        float0 *= float9;
        float1 *= float9;
        float2 *= float9;
        float3 *= float10;
        float4 *= float10;
        float5 *= float10;
        float6 *= float11;
        float7 *= float11;
        float8 *= float11;
        float float12 = 1.0E-4F;
        float float13 = 0.001F;
        if (!(Math.abs(float3 - float1) < float12) || !(Math.abs(float6 - float2) < float12) || !(Math.abs(float7 - float5) < float12)) {
            float float14 = Math.sqrt((float5 - float7) * (float5 - float7) + (float6 - float2) * (float6 - float2) + (float1 - float3) * (float1 - float3));
            this.angle = Math.safeAcos((float0 + float4 + float8 - 1.0F) / 2.0F);
            this.x = (float5 - float7) / float14;
            this.y = (float6 - float2) / float14;
            this.z = (float1 - float3) / float14;
            return this;
        } else if (Math.abs(float3 + float1) < float13
            && Math.abs(float6 + float2) < float13
            && Math.abs(float7 + float5) < float13
            && Math.abs(float0 + float4 + float8 - 3.0F) < float13) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
            this.angle = 0.0F;
            return this;
        } else {
            this.angle = (float) java.lang.Math.PI;
            float float15 = (float0 + 1.0F) / 2.0F;
            float float16 = (float4 + 1.0F) / 2.0F;
            float float17 = (float8 + 1.0F) / 2.0F;
            float float18 = (float3 + float1) / 4.0F;
            float float19 = (float6 + float2) / 4.0F;
            float float20 = (float7 + float5) / 4.0F;
            if (float15 > float16 && float15 > float17) {
                this.x = Math.sqrt(float15);
                this.y = float18 / this.x;
                this.z = float19 / this.x;
            } else if (float16 > float17) {
                this.y = Math.sqrt(float16);
                this.x = float18 / this.y;
                this.z = float20 / this.y;
            } else {
                this.z = Math.sqrt(float17);
                this.x = float19 / this.z;
                this.y = float20 / this.z;
            }

            return this;
        }
    }

    public AxisAngle4f set(Matrix4dc arg0) {
        double double0 = arg0.m00();
        double double1 = arg0.m01();
        double double2 = arg0.m02();
        double double3 = arg0.m10();
        double double4 = arg0.m11();
        double double5 = arg0.m12();
        double double6 = arg0.m20();
        double double7 = arg0.m21();
        double double8 = arg0.m22();
        double double9 = Math.invsqrt(arg0.m00() * arg0.m00() + arg0.m01() * arg0.m01() + arg0.m02() * arg0.m02());
        double double10 = Math.invsqrt(arg0.m10() * arg0.m10() + arg0.m11() * arg0.m11() + arg0.m12() * arg0.m12());
        double double11 = Math.invsqrt(arg0.m20() * arg0.m20() + arg0.m21() * arg0.m21() + arg0.m22() * arg0.m22());
        double0 *= double9;
        double1 *= double9;
        double2 *= double9;
        double3 *= double10;
        double4 *= double10;
        double5 *= double10;
        double6 *= double11;
        double7 *= double11;
        double8 *= double11;
        double double12 = 1.0E-4;
        double double13 = 0.001;
        if (!(Math.abs(double3 - double1) < double12) || !(Math.abs(double6 - double2) < double12) || !(Math.abs(double7 - double5) < double12)) {
            double double14 = Math.sqrt(
                (double5 - double7) * (double5 - double7) + (double6 - double2) * (double6 - double2) + (double1 - double3) * (double1 - double3)
            );
            this.angle = (float)Math.safeAcos((double0 + double4 + double8 - 1.0) / 2.0);
            this.x = (float)((double5 - double7) / double14);
            this.y = (float)((double6 - double2) / double14);
            this.z = (float)((double1 - double3) / double14);
            return this;
        } else if (Math.abs(double3 + double1) < double13
            && Math.abs(double6 + double2) < double13
            && Math.abs(double7 + double5) < double13
            && Math.abs(double0 + double4 + double8 - 3.0) < double13) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 1.0F;
            this.angle = 0.0F;
            return this;
        } else {
            this.angle = (float) java.lang.Math.PI;
            double double15 = (double0 + 1.0) / 2.0;
            double double16 = (double4 + 1.0) / 2.0;
            double double17 = (double8 + 1.0) / 2.0;
            double double18 = (double3 + double1) / 4.0;
            double double19 = (double6 + double2) / 4.0;
            double double20 = (double7 + double5) / 4.0;
            if (double15 > double16 && double15 > double17) {
                this.x = (float)Math.sqrt(double15);
                this.y = (float)(double18 / this.x);
                this.z = (float)(double19 / this.x);
            } else if (double16 > double17) {
                this.y = (float)Math.sqrt(double16);
                this.x = (float)(double18 / this.y);
                this.z = (float)(double20 / this.y);
            } else {
                this.z = (float)Math.sqrt(double17);
                this.x = (float)(double19 / this.z);
                this.y = (float)(double20 / this.z);
            }

            return this;
        }
    }

    public Quaternionf get(Quaternionf arg0) {
        return arg0.set(this);
    }

    public Quaterniond get(Quaterniond arg0) {
        return arg0.set(this);
    }

    public Matrix4f get(Matrix4f arg0) {
        return arg0.set(this);
    }

    public Matrix3f get(Matrix3f arg0) {
        return arg0.set(this);
    }

    public Matrix4d get(Matrix4d arg0) {
        return arg0.set(this);
    }

    public Matrix3d get(Matrix3d arg0) {
        return arg0.set(this);
    }

    public AxisAngle4d get(AxisAngle4d arg0) {
        return arg0.set(this);
    }

    public AxisAngle4f get(AxisAngle4f arg0) {
        return arg0.set(this);
    }

    @Override
    public void writeExternal(ObjectOutput arg0) throws IOException {
        arg0.writeFloat(this.angle);
        arg0.writeFloat(this.x);
        arg0.writeFloat(this.y);
        arg0.writeFloat(this.z);
    }

    @Override
    public void readExternal(ObjectInput arg0) throws IOException, ClassNotFoundException {
        this.angle = arg0.readFloat();
        this.x = arg0.readFloat();
        this.y = arg0.readFloat();
        this.z = arg0.readFloat();
    }

    public AxisAngle4f normalize() {
        float float0 = Math.invsqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        this.x *= float0;
        this.y *= float0;
        this.z *= float0;
        return this;
    }

    public AxisAngle4f rotate(float arg0) {
        this.angle += arg0;
        this.angle = (float)((this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2));
        return this;
    }

    public Vector3f transform(Vector3f arg0) {
        return this.transform(arg0, arg0);
    }

    public Vector3f transform(Vector3fc arg0, Vector3f arg1) {
        double double0 = Math.sin(this.angle);
        double double1 = Math.cosFromSin(double0, (double)this.angle);
        float float0 = this.x * arg0.x() + this.y * arg0.y() + this.z * arg0.z();
        arg1.set(
            (float)(arg0.x() * double1 + double0 * (this.y * arg0.z() - this.z * arg0.y()) + (1.0 - double1) * float0 * this.x),
            (float)(arg0.y() * double1 + double0 * (this.z * arg0.x() - this.x * arg0.z()) + (1.0 - double1) * float0 * this.y),
            (float)(arg0.z() * double1 + double0 * (this.x * arg0.y() - this.y * arg0.x()) + (1.0 - double1) * float0 * this.z)
        );
        return arg1;
    }

    public Vector4f transform(Vector4f arg0) {
        return this.transform(arg0, arg0);
    }

    public Vector4f transform(Vector4fc arg0, Vector4f arg1) {
        double double0 = Math.sin(this.angle);
        double double1 = Math.cosFromSin(double0, (double)this.angle);
        float float0 = this.x * arg0.x() + this.y * arg0.y() + this.z * arg0.z();
        arg1.set(
            (float)(arg0.x() * double1 + double0 * (this.y * arg0.z() - this.z * arg0.y()) + (1.0 - double1) * float0 * this.x),
            (float)(arg0.y() * double1 + double0 * (this.z * arg0.x() - this.x * arg0.z()) + (1.0 - double1) * float0 * this.y),
            (float)(arg0.z() * double1 + double0 * (this.x * arg0.y() - this.y * arg0.x()) + (1.0 - double1) * float0 * this.z),
            arg1.w
        );
        return arg1;
    }

    @Override
    public String toString() {
        return Runtime.formatNumbers(this.toString(Options.NUMBER_FORMAT));
    }

    public String toString(NumberFormat numberFormat) {
        return "("
            + Runtime.format(this.x, numberFormat)
            + " "
            + Runtime.format(this.y, numberFormat)
            + " "
            + Runtime.format(this.z, numberFormat)
            + " <| "
            + Runtime.format(this.angle, numberFormat)
            + ")";
    }

    @Override
    public int hashCode() {
        int int0 = 1;
        float float0 = (float)((this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2));
        int0 = 31 * int0 + Float.floatToIntBits(float0);
        int0 = 31 * int0 + Float.floatToIntBits(this.x);
        int0 = 31 * int0 + Float.floatToIntBits(this.y);
        return 31 * int0 + Float.floatToIntBits(this.z);
    }

    @Override
    public boolean equals(Object arg0) {
        if (this == arg0) {
            return true;
        } else if (arg0 == null) {
            return false;
        } else if (this.getClass() != arg0.getClass()) {
            return false;
        } else {
            AxisAngle4f axisAngle4f = (AxisAngle4f)arg0;
            float float0 = (float)((this.angle < 0.0 ? (java.lang.Math.PI * 2) + this.angle % (java.lang.Math.PI * 2) : this.angle) % (java.lang.Math.PI * 2));
            float float1 = (float)(
                (axisAngle4f.angle < 0.0 ? (java.lang.Math.PI * 2) + axisAngle4f.angle % (java.lang.Math.PI * 2) : axisAngle4f.angle) % (java.lang.Math.PI * 2)
            );
            if (Float.floatToIntBits(float0) != Float.floatToIntBits(float1)) {
                return false;
            } else if (Float.floatToIntBits(this.x) != Float.floatToIntBits(axisAngle4f.x)) {
                return false;
            } else {
                return Float.floatToIntBits(this.y) != Float.floatToIntBits(axisAngle4f.y)
                    ? false
                    : Float.floatToIntBits(this.z) == Float.floatToIntBits(axisAngle4f.z);
            }
        }
    }
}
