// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class Vector3 {
    private float x;
    private float y;
    private float z;

    public Vector3() {
        this(0.0F, 0.0F, 0.0F);
    }

    public Vector3(float _x, float _y, float _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
    }

    public Vector3(Vector3 vec) {
        this.set(vec);
    }

    public float x() {
        return this.x;
    }

    public Vector3 x(float _x) {
        this.x = _x;
        return this;
    }

    public float y() {
        return this.y;
    }

    public Vector3 y(float _y) {
        this.y = _y;
        return this;
    }

    public float z() {
        return this.z;
    }

    public Vector3 z(float _z) {
        this.z = _z;
        return this;
    }

    public Vector3 set(float _x, float _y, float _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        return this;
    }

    public Vector3 set(Vector3 vec) {
        return this.set(vec.x(), vec.y(), vec.z());
    }

    public Vector3 reset() {
        this.x = this.y = this.z = 0.0F;
        return this;
    }

    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector3 normalize() {
        float float0 = this.length();
        this.x /= float0;
        this.y /= float0;
        this.z /= float0;
        return this;
    }

    public float dot(Vector3 vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }

    public Vector3 cross(Vector3 vec) {
        return new Vector3(this.y() * vec.z() - vec.y() * this.z(), vec.z() * this.x() - this.z() * vec.x(), this.x() * vec.y() - vec.x() * this.y());
    }

    public Vector3 add(float _x, float _y, float _z) {
        this.x += _x;
        this.y += _y;
        this.z += _z;
        return this;
    }

    public Vector3 add(Vector3 vec) {
        return this.add(vec.x(), vec.y(), vec.z());
    }

    public Vector3 sub(float _x, float _y, float _z) {
        this.x -= _x;
        this.y -= _y;
        this.z -= _z;
        return this;
    }

    public Vector3 sub(Vector3 vec) {
        return this.sub(vec.x(), vec.y(), vec.z());
    }

    public Vector3 mul(float f) {
        return this.mul(f, f, f);
    }

    public Vector3 mul(float _x, float _y, float _z) {
        this.x *= _x;
        this.y *= _y;
        this.z *= _z;
        return this;
    }

    public Vector3 mul(Vector3 vec) {
        return this.mul(vec.x(), vec.y(), vec.z());
    }
}
