// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.physics;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Transform represents translation and rotation (rigid transform). Scaling and  shearing is not supported.
 */
public final class Transform {
    /**
     * Rotation matrix of this Transform.
     */
    public final Matrix3f basis = new Matrix3f();
    /**
     * Translation vector of this Transform.
     */
    public final Vector3f origin = new Vector3f();

    public Transform() {
    }

    public Transform(Matrix3f mat) {
        this.basis.set(mat);
    }

    public Transform(Matrix4f mat) {
        this.set(mat);
    }

    public Transform(Transform tr) {
        this.set(tr);
    }

    public void set(Transform tr) {
        this.basis.set(tr.basis);
        this.origin.set(tr.origin);
    }

    public void set(Matrix3f mat) {
        this.basis.set(mat);
        this.origin.set(0.0F, 0.0F, 0.0F);
    }

    public void set(Matrix4f mat) {
        mat.get3x3(this.basis);
        mat.getTranslation(this.origin);
    }

    public void transform(Vector3f v) {
        this.basis.transform(v);
        v.add(this.origin);
    }

    public void setIdentity() {
        this.basis.identity();
        this.origin.set(0.0F, 0.0F, 0.0F);
    }

    public void inverse() {
        this.basis.transpose();
        this.origin.negate();
        this.basis.transform(this.origin);
    }

    public void inverse(Transform tr) {
        this.set(tr);
        this.inverse();
    }

    public Quaternionf getRotation(Quaternionf out) {
        this.basis.getUnnormalizedRotation(out);
        return out;
    }

    public void setRotation(Quaternionf q) {
        this.basis.set(q);
    }

    public Matrix4f getMatrix(Matrix4f out) {
        out.set(this.basis);
        out.setTranslation(this.origin);
        return out;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof Transform transform ? this.basis.equals(transform.basis) && this.origin.equals(transform.origin) : false;
    }

    @Override
    public int hashCode() {
        int int0 = 3;
        int0 = 41 * int0 + this.basis.hashCode();
        return 41 * int0 + this.origin.hashCode();
    }
}
