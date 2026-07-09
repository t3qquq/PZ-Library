// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.math.PZMath;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class Keyframe {
    public Quaternion Rotation;
    public Vector3f Position;
    public Vector3f Scale = new Vector3f(1.0F, 1.0F, 1.0F);
    public int Bone;
    public String BoneName;
    public float Time = -1.0F;

    public Keyframe() {
    }

    public Keyframe(Vector3f pos, Quaternion rotation, Vector3f scale) {
        this.Position = new Vector3f(pos);
        this.Rotation = new Quaternion(rotation);
        this.Scale = new Vector3f(scale);
    }

    public void set(Keyframe keyframe) {
        if (keyframe.Position != null) {
            this.setPosition(keyframe.Position);
        }

        if (keyframe.Rotation != null) {
            this.setRotation(keyframe.Rotation);
        }

        if (keyframe.Scale != null) {
            this.setScale(keyframe.Scale);
        }

        this.Time = keyframe.Time;
        this.Bone = keyframe.Bone;
        this.BoneName = keyframe.BoneName;
    }

    public void get(Vector3f out_pos, Quaternion out_rot, Vector3f out_scale) {
        setIfNotNull(out_pos, this.Position, 0.0F, 0.0F, 0.0F);
        setIfNotNull(out_rot, this.Rotation);
        setIfNotNull(out_scale, this.Scale, 1.0F, 1.0F, 1.0F);
    }

    private void setScale(Vector3f vector3f) {
        if (this.Scale == null) {
            this.Scale = new Vector3f();
        }

        this.Scale.set(vector3f);
    }

    private void setRotation(Quaternion quaternion) {
        if (this.Rotation == null) {
            this.Rotation = new Quaternion();
        }

        this.Rotation.set(quaternion);
    }

    private void setPosition(Vector3f vector3f) {
        if (this.Position == null) {
            this.Position = new Vector3f();
        }

        this.Position.set(vector3f);
    }

    public void clear() {
        this.Time = -1.0F;
        this.Position = null;
        this.Rotation = null;
    }

    public void setIdentity() {
        setIdentity(this.Position, this.Rotation, this.Scale);
    }

    public static void setIdentity(Vector3f out_pos, Quaternion out_rot, Vector3f out_scale) {
        setIfNotNull(out_pos, 0.0F, 0.0F, 0.0F);
        setIdentityIfNotNull(out_rot);
        setIfNotNull(out_scale, 1.0F, 1.0F, 1.0F);
    }

    public static Keyframe lerp(Keyframe a, Keyframe b, float time, Keyframe out_result) {
        lerp(a, b, time, out_result.Position, out_result.Rotation, out_result.Scale);
        out_result.Bone = b.Bone;
        out_result.BoneName = b.BoneName;
        out_result.Time = time;
        return out_result;
    }

    public static void setIfNotNull(Vector3f to, Vector3f val, float default_x, float default_y, float default_z) {
        if (to != null) {
            if (val != null) {
                to.set(val);
            } else {
                to.set(default_x, default_y, default_z);
            }
        }
    }

    public static void setIfNotNull(Vector3f to, float x, float y, float z) {
        if (to != null) {
            to.set(x, y, z);
        }
    }

    public static void setIfNotNull(Quaternion to, Quaternion val) {
        if (to != null) {
            if (val != null) {
                to.set(val);
            } else {
                to.setIdentity();
            }
        }
    }

    public static void setIdentityIfNotNull(Quaternion to) {
        if (to != null) {
            to.setIdentity();
        }
    }

    public static void lerp(Keyframe a, Keyframe b, float time, Vector3f out_pos, Quaternion out_rot, Vector3f out_scale) {
        if (b.Time == a.Time) {
            b.get(out_pos, out_rot, out_scale);
        } else {
            float float0 = (time - a.Time) / (b.Time - a.Time);
            if (out_pos != null) {
                PZMath.lerp(out_pos, a.Position, b.Position, float0);
            }

            if (out_rot != null) {
                PZMath.slerp(out_rot, a.Rotation, b.Rotation, float0);
            }

            if (out_scale != null) {
                PZMath.lerp(out_scale, a.Scale, b.Scale, float0);
            }
        }
    }
}
