// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.HelperFunctions;
import zombie.util.Pool;
import zombie.util.PooledObject;

public class BoneTransform extends PooledObject {
    private boolean m_matrixValid = true;
    private final Matrix4f m_matrix = new Matrix4f();
    private final HelperFunctions.TransformResult_QPS m_transformResult = new HelperFunctions.TransformResult_QPS(this.m_matrix);
    private boolean m_prsValid = true;
    private final Vector3f m_pos = new Vector3f();
    private final Quaternion m_rot = new Quaternion();
    private final Vector3f m_scale = new Vector3f();
    private static final Pool<BoneTransform> s_pool = new Pool<>(BoneTransform::new);

    protected BoneTransform() {
        this.setIdentity();
    }

    public void setIdentity() {
        this.m_matrixValid = true;
        this.m_matrix.setIdentity();
        this.m_prsValid = true;
        this.m_pos.set(0.0F, 0.0F, 0.0F);
        this.m_rot.setIdentity();
        this.m_scale.set(1.0F, 1.0F, 1.0F);
    }

    public void set(BoneTransform rhs) {
        this.m_matrixValid = rhs.m_matrixValid;
        this.m_prsValid = rhs.m_prsValid;
        this.m_pos.set(rhs.m_pos);
        this.m_rot.set(rhs.m_rot);
        this.m_scale.set(rhs.m_scale);
        this.m_matrix.load(rhs.m_matrix);
    }

    public void set(Vector3f pos, Quaternion rot, Vector3f scale) {
        if (this.m_matrixValid || !this.m_prsValid || !this.m_pos.equals(pos) || !this.m_rot.equals(rot) || !this.m_scale.equals(scale)) {
            this.m_matrixValid = false;
            this.m_prsValid = true;
            this.m_pos.set(pos);
            this.m_rot.set(rot);
            this.m_scale.set(scale);
        }
    }

    public void set(Matrix4f matrix) {
        this.m_matrixValid = true;
        this.m_matrix.load(matrix);
        this.m_prsValid = false;
    }

    public void mul(Matrix4f a, Matrix4f b) {
        this.m_matrixValid = true;
        this.m_prsValid = false;
        Matrix4f.mul(a, b, this.m_matrix);
    }

    public void getMatrix(Matrix4f out_result) {
        out_result.load(this.getValidMatrix_Internal());
    }

    public void getPRS(Vector3f out_pos, Quaternion out_rot, Vector3f out_scale) {
        this.validatePRS();
        out_pos.set(this.m_pos);
        out_rot.set(this.m_rot);
        out_scale.set(this.m_scale);
    }

    public void getPosition(Vector3f out_pos) {
        this.validatePRS();
        out_pos.set(this.m_pos);
    }

    private Matrix4f getValidMatrix_Internal() {
        this.validateMatrix();
        return this.m_matrix;
    }

    private void validateMatrix() {
        if (!this.m_matrixValid) {
            this.validateInternal();
            this.m_matrixValid = true;
            HelperFunctions.CreateFromQuaternionPositionScale(this.m_pos, this.m_rot, this.m_scale, this.m_transformResult);
        }
    }

    protected void validatePRS() {
        if (!this.m_prsValid) {
            this.validateInternal();
            this.m_prsValid = true;
            HelperFunctions.getPosition(this.m_matrix, this.m_pos);
            HelperFunctions.getRotation(this.m_matrix, this.m_rot);
            this.m_scale.set(1.0F, 1.0F, 1.0F);
        }
    }

    protected void validateInternal() {
        if (!this.m_prsValid && !this.m_matrixValid) {
            throw new RuntimeException("Neither the matrix nor the PosRotScale values in this object are listed as valid.");
        }
    }

    public static void mul(BoneTransform a, Matrix4f b, Matrix4f out_result) {
        Matrix4f.mul(a.getValidMatrix_Internal(), b, out_result);
    }

    public static BoneTransform alloc() {
        return s_pool.alloc();
    }
}
