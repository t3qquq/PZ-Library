// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.PerformanceSettings;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.HelperFunctions;

/**
 * Created by LEMMY on 3/15/2016.
 * @deprecated
 */
@Deprecated
public class StaticAnimation {
    private int framesPerSecond;
    public String name;
    public Matrix4f[][] Matrices;
    private Matrix4f[] RootMotion;
    public AnimationClip Clip;
    private int currentKeyframe = 0;
    private float currentTimeValue = 0.0F;
    private Keyframe[] Pose;
    private Keyframe[] PrevPose;
    private float lastTime = 0.0F;

    public StaticAnimation(AnimationClip clip) {
        this.Clip = clip;
        this.framesPerSecond = PerformanceSettings.BaseStaticAnimFramerate;
        this.Matrices = new Matrix4f[(int)(this.framesPerSecond * this.Clip.Duration)][60];
        this.RootMotion = new Matrix4f[(int)(this.framesPerSecond * this.Clip.Duration)];
        this.Pose = new Keyframe[60];
        this.PrevPose = new Keyframe[60];
        this.Create();
        Arrays.fill(this.Pose, null);
        this.Pose = null;
        Arrays.fill(this.PrevPose, null);
        this.PrevPose = null;
    }

    private Keyframe getNextKeyFrame(int int2, int int1, Keyframe keyframe1) {
        Keyframe[] keyframes = this.Clip.getKeyframes();

        for (int int0 = int1; int0 < keyframes.length; int0++) {
            Keyframe keyframe0 = keyframes[int0];
            if (keyframe0.Bone == int2 && keyframe0.Time > this.currentTimeValue && keyframe1 != keyframe0) {
                return keyframe0;
            }
        }

        return null;
    }

    public Quaternion getRotation(Quaternion out, int bone) {
        if (this.PrevPose[bone] != null && PerformanceSettings.InterpolateAnims) {
            float float0 = (this.currentTimeValue - this.PrevPose[bone].Time) / (this.Pose[bone].Time - this.PrevPose[bone].Time);
            if (this.Pose[bone].Time - this.PrevPose[bone].Time == 0.0F) {
                float0 = 0.0F;
            }

            return PZMath.slerp(out, this.PrevPose[bone].Rotation, this.Pose[bone].Rotation, float0);
        } else {
            out.set(this.Pose[bone].Rotation);
            return out;
        }
    }

    public Vector3f getPosition(Vector3f out, int bone) {
        if (this.PrevPose[bone] != null && PerformanceSettings.InterpolateAnims) {
            float float0 = (this.currentTimeValue - this.PrevPose[bone].Time) / (this.Pose[bone].Time - this.PrevPose[bone].Time);
            if (this.Pose[bone].Time - this.PrevPose[bone].Time == 0.0F) {
                float0 = 0.0F;
            }

            PZMath.lerp(out, this.PrevPose[bone].Position, this.Pose[bone].Position, float0);
            return out;
        } else {
            out.set(this.Pose[bone].Position);
            return out;
        }
    }

    public void getPose() {
        Keyframe[] keyframes = this.Clip.getKeyframes();

        for (this.currentKeyframe = 0; this.currentKeyframe < keyframes.length; this.currentKeyframe++) {
            Keyframe keyframe0 = keyframes[this.currentKeyframe];
            if (this.currentKeyframe == keyframes.length - 1 || !(keyframe0.Time <= this.currentTimeValue)) {
                if (PerformanceSettings.InterpolateAnims) {
                    for (int int0 = 0; int0 < 60; int0++) {
                        if (this.Pose[int0] == null || this.currentTimeValue >= this.Pose[int0].Time) {
                            Keyframe keyframe1 = this.getNextKeyFrame(int0, this.currentKeyframe, this.Pose[int0]);
                            if (keyframe1 != null) {
                                this.PrevPose[keyframe1.Bone] = this.Pose[keyframe1.Bone];
                                this.Pose[keyframe1.Bone] = keyframe1;
                            } else {
                                this.PrevPose[int0] = null;
                            }
                        }
                    }
                }
                break;
            }

            if (keyframe0.Bone >= 0) {
                this.Pose[keyframe0.Bone] = keyframe0;
            }

            this.lastTime = keyframe0.Time;
        }
    }

    public void Create() {
        float float0 = this.Matrices.length;
        double double0 = (double)this.Clip.Duration / float0;
        double double1 = 0.0;
        int int0 = 0;

        for (Matrix4f matrix4f0 = new Matrix4f(); int0 < float0; int0++) {
            this.currentTimeValue = (float)double1;
            this.getPose();

            for (int int1 = 0; int1 < 60; int1++) {
                if (this.Pose[int1] == null) {
                    this.Matrices[int0][int1] = matrix4f0;
                } else {
                    Quaternion quaternion = new Quaternion();
                    this.getRotation(quaternion, int1);
                    Vector3f vector3f = new Vector3f();
                    this.getPosition(vector3f, int1);
                    Matrix4f matrix4f1 = HelperFunctions.CreateFromQuaternionPositionScale(vector3f, quaternion, new Vector3f(1.0F, 1.0F, 1.0F), new Matrix4f());
                    this.Matrices[int0][int1] = matrix4f1;
                }
            }

            double1 += double0;
        }
    }

    public Keyframe interpolate(List<Keyframe> frames, float time) {
        int int0 = 0;
        Keyframe keyframe0 = null;
        Object object = null;

        while (int0 < frames.size()) {
            Keyframe keyframe1 = (Keyframe)frames.get(int0);
            if (keyframe1.Time > time && keyframe0.Time <= time) {
                Quaternion quaternion = new Quaternion();
                Vector3f vector3f = new Vector3f();
                float float0 = (time - keyframe0.Time) / (keyframe1.Time - keyframe0.Time);
                PZMath.slerp(quaternion, keyframe0.Rotation, keyframe1.Rotation, float0);
                PZMath.lerp(vector3f, keyframe0.Position, keyframe1.Position, float0);
                Keyframe keyframe2 = new Keyframe();
                keyframe2.Position = vector3f;
                keyframe2.Rotation = quaternion;
                keyframe2.Scale = new Vector3f(1.0F, 1.0F, 1.0F);
                keyframe2.Time = keyframe0.Time + (keyframe1.Time - keyframe0.Time) * float0;
                return keyframe2;
            }

            int0++;
            keyframe0 = keyframe1;
        }

        return (Keyframe)frames.get(frames.size() - 1);
    }

    public void interpolate(List<Keyframe> frames) {
        if (!frames.isEmpty()) {
            if (!((Keyframe)frames.get(0)).Position.equals(((Keyframe)frames.get(frames.size() - 1)).Position)) {
                float float0 = this.Matrices.length + 1;
                double double0 = (double)this.Clip.Duration / float0;
                double double1 = 0.0;
                ArrayList arrayList = new ArrayList();

                for (int int0 = 0; int0 < float0 - 1.0F; double1 += double0) {
                    Keyframe keyframe = this.interpolate(frames, (float)double1);
                    arrayList.add(keyframe);
                    int0++;
                }

                frames.clear();
                frames.addAll(arrayList);
            }
        }
    }

    public void doRootMotion(List<Keyframe> frames) {
        float float0 = this.Matrices.length;
        if (frames.size() > 3) {
            for (int int0 = 0; int0 < float0 && int0 < frames.size(); int0++) {
                Keyframe keyframe = (Keyframe)frames.get(int0);
                Quaternion quaternion = keyframe.Rotation;
                Vector3f vector3f = keyframe.Position;
                Matrix4f matrix4f = HelperFunctions.CreateFromQuaternionPositionScale(vector3f, quaternion, keyframe.Scale, new Matrix4f());
                this.RootMotion[int0] = matrix4f;
            }
        }
    }
}
