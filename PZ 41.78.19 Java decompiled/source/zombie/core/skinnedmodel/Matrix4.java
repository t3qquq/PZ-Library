// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

import java.nio.FloatBuffer;
import org.lwjglx.BufferUtils;

public class Matrix4 {
    private FloatBuffer matrix = FloatBuffer.allocate(16);
    public static Matrix4 Identity = new Matrix4();
    private FloatBuffer direct;

    public Matrix4() {
    }

    public Matrix4(float[] floats) {
        this();
        this.put(floats);
    }

    public Matrix4(Matrix4 matrix41) {
        this();
        this.put(matrix41);
    }

    public Matrix4 clear() {
        for (int int0 = 0; int0 < 16; int0++) {
            this.matrix.put(int0, 0.0F);
        }

        return this;
    }

    public Matrix4 clearToIdentity() {
        return this.clear().put(0, 1.0F).put(5, 1.0F).put(10, 1.0F).put(15, 1.0F);
    }

    public Matrix4 clearToOrtho(float float5, float float4, float float3, float float2, float float1, float float0) {
        return this.clear()
            .put(0, 2.0F / (float4 - float5))
            .put(5, 2.0F / (float2 - float3))
            .put(10, -2.0F / (float0 - float1))
            .put(12, -(float4 + float5) / (float4 - float5))
            .put(13, -(float2 + float3) / (float2 - float3))
            .put(14, -(float0 + float1) / (float0 - float1))
            .put(15, 1.0F);
    }

    public Matrix4 clearToPerspective(float float1, float float4, float float5, float float2, float float3) {
        float float0 = 1.0F / (float)Math.tan(float1 / 2.0F);
        return this.clear()
            .put(0, float0 / (float4 / float5))
            .put(5, float0)
            .put(10, (float3 + float2) / (float2 - float3))
            .put(14, 2.0F * float3 * float2 / (float2 - float3))
            .put(11, -1.0F);
    }

    public float get(int int0) {
        return this.matrix.get(int0);
    }

    public Matrix4 put(int int0, float float0) {
        this.matrix.put(int0, float0);
        return this;
    }

    public Matrix4 put(int int0, Vector3 vector3, float float0) {
        this.put(int0 * 4 + 0, vector3.x());
        this.put(int0 * 4 + 1, vector3.y());
        this.put(int0 * 4 + 2, vector3.z());
        this.put(int0 * 4 + 3, float0);
        return this;
    }

    public Matrix4 put(float[] floats) {
        if (floats.length < 16) {
            throw new IllegalArgumentException("float array must have at least 16 values.");
        } else {
            this.matrix.position(0);
            this.matrix.put(floats, 0, 16);
            return this;
        }
    }

    public Matrix4 put(Matrix4 matrix40) {
        FloatBuffer floatBuffer = matrix40.getBuffer();

        while (floatBuffer.hasRemaining()) {
            this.matrix.put(floatBuffer.get());
        }

        return this;
    }

    public Matrix4 mult(float[] floats1) {
        float[] floats0 = new float[16];

        for (byte byte0 = 0; byte0 < 16; byte0 += 4) {
            floats0[byte0 + 0] = this.get(0) * floats1[byte0]
                + this.get(4) * floats1[byte0 + 1]
                + this.get(8) * floats1[byte0 + 2]
                + this.get(12) * floats1[byte0 + 3];
            floats0[byte0 + 1] = this.get(1) * floats1[byte0]
                + this.get(5) * floats1[byte0 + 1]
                + this.get(9) * floats1[byte0 + 2]
                + this.get(13) * floats1[byte0 + 3];
            floats0[byte0 + 2] = this.get(2) * floats1[byte0]
                + this.get(6) * floats1[byte0 + 1]
                + this.get(10) * floats1[byte0 + 2]
                + this.get(14) * floats1[byte0 + 3];
            floats0[byte0 + 3] = this.get(3) * floats1[byte0]
                + this.get(7) * floats1[byte0 + 1]
                + this.get(11) * floats1[byte0 + 2]
                + this.get(15) * floats1[byte0 + 3];
        }

        this.put(floats0);
        return this;
    }

    public Matrix4 mult(Matrix4 matrix40) {
        float[] floats = new float[16];

        for (byte byte0 = 0; byte0 < 16; byte0 += 4) {
            floats[byte0 + 0] = this.get(0) * matrix40.get(byte0)
                + this.get(4) * matrix40.get(byte0 + 1)
                + this.get(8) * matrix40.get(byte0 + 2)
                + this.get(12) * matrix40.get(byte0 + 3);
            floats[byte0 + 1] = this.get(1) * matrix40.get(byte0)
                + this.get(5) * matrix40.get(byte0 + 1)
                + this.get(9) * matrix40.get(byte0 + 2)
                + this.get(13) * matrix40.get(byte0 + 3);
            floats[byte0 + 2] = this.get(2) * matrix40.get(byte0)
                + this.get(6) * matrix40.get(byte0 + 1)
                + this.get(10) * matrix40.get(byte0 + 2)
                + this.get(14) * matrix40.get(byte0 + 3);
            floats[byte0 + 3] = this.get(3) * matrix40.get(byte0)
                + this.get(7) * matrix40.get(byte0 + 1)
                + this.get(11) * matrix40.get(byte0 + 2)
                + this.get(15) * matrix40.get(byte0 + 3);
        }

        this.put(floats);
        return this;
    }

    public Matrix4 transpose() {
        float float0 = this.get(1);
        this.put(1, this.get(4));
        this.put(4, float0);
        float0 = this.get(2);
        this.put(2, this.get(8));
        this.put(8, float0);
        float0 = this.get(3);
        this.put(3, this.get(12));
        this.put(12, float0);
        float0 = this.get(7);
        this.put(7, this.get(13));
        this.put(13, float0);
        float0 = this.get(11);
        this.put(11, this.get(14));
        this.put(14, float0);
        float0 = this.get(6);
        this.put(6, this.get(9));
        this.put(9, float0);
        return this;
    }

    public Matrix4 translate(float float0, float float1, float float2) {
        float[] floats = new float[16];
        floats[0] = 1.0F;
        floats[5] = 1.0F;
        floats[10] = 1.0F;
        floats[15] = 1.0F;
        floats[12] = float0;
        floats[13] = float1;
        floats[14] = float2;
        return this.mult(floats);
    }

    public Matrix4 translate(Vector3 vector3) {
        return this.translate(vector3.x(), vector3.y(), vector3.z());
    }

    public Matrix4 scale(float float0, float float1, float float2) {
        float[] floats = new float[16];
        floats[0] = float0;
        floats[5] = float1;
        floats[10] = float2;
        floats[15] = 1.0F;
        return this.mult(floats);
    }

    public Matrix4 scale(Vector3 vector3) {
        return this.scale(vector3.x(), vector3.y(), vector3.z());
    }

    public Matrix4 rotate(float float1, float float4, float float5, float float6) {
        float float0 = (float)Math.cos(float1);
        float float2 = (float)Math.sin(float1);
        float float3 = 1.0F - float0;
        Vector3 vector3 = new Vector3(float4, float5, float6).normalize();
        float[] floats = new float[16];
        floats[0] = vector3.x() * vector3.x() + (1.0F - vector3.x() * vector3.x()) * float0;
        floats[4] = vector3.x() * vector3.y() * float3 - vector3.z() * float2;
        floats[8] = vector3.x() * vector3.z() * float3 + vector3.y() * float2;
        floats[1] = vector3.y() * vector3.x() * float3 + vector3.z() * float2;
        floats[5] = vector3.y() * vector3.y() + (1.0F - vector3.y() * vector3.y()) * float0;
        floats[9] = vector3.y() * vector3.z() * float3 - vector3.x() * float2;
        floats[2] = vector3.z() * vector3.x() * float3 - vector3.y() * float2;
        floats[6] = vector3.z() * vector3.y() * float3 + vector3.x() * float2;
        floats[10] = vector3.z() * vector3.z() + (1.0F - vector3.z() * vector3.z()) * float0;
        floats[15] = 1.0F;
        return this.mult(floats);
    }

    public Matrix4 rotate(float float0, Vector3 vector3) {
        return this.rotate(float0, vector3.x(), vector3.y(), vector3.z());
    }

    public FloatBuffer getBuffer() {
        if (this.direct == null) {
            this.direct = BufferUtils.createFloatBuffer(16);
        }

        this.direct.clear();
        this.direct.put(this.matrix.position(16).flip());
        this.direct.flip();
        return this.direct;
    }

    static {
        Identity.clearToIdentity();
    }
}
