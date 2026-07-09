// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.runtime;

import org.lwjgl.util.vector.Quaternion;
import org.lwjgl.util.vector.Vector3f;
import zombie.core.skinnedmodel.animation.Keyframe;

public final class KeyframeUtil {
    static final Quaternion end = new Quaternion();

    public static Vector3f GetKeyFramePosition(Keyframe[] keyframes, float float0, double double0) {
        Vector3f vector3f = new Vector3f();
        if (keyframes.length == 0) {
            return vector3f;
        } else {
            int int0 = 0;

            while (int0 < keyframes.length - 1 && !(float0 < keyframes[int0 + 1].Time)) {
                int0++;
            }

            int int1 = (int0 + 1) % keyframes.length;
            Keyframe keyframe0 = keyframes[int0];
            Keyframe keyframe1 = keyframes[int1];
            float float1 = keyframe0.Time;
            float float2 = keyframe1.Time;
            float float3 = float2 - float1;
            if (float3 < 0.0F) {
                float3 = (float)(float3 + double0);
            }

            if (float3 > 0.0F) {
                float float4 = float2 - float1;
                float float5 = float0 - float1;
                float5 /= float4;
                float float6 = keyframe0.Position.x;
                float float7 = keyframe1.Position.x;
                float float8 = float6 + float5 * (float7 - float6);
                float float9 = keyframe0.Position.y;
                float float10 = keyframe1.Position.y;
                float float11 = float9 + float5 * (float10 - float9);
                float float12 = keyframe0.Position.z;
                float float13 = keyframe1.Position.z;
                float float14 = float12 + float5 * (float13 - float12);
                vector3f.set(float8, float11, float14);
            } else {
                vector3f.set(keyframe0.Position);
            }

            return vector3f;
        }
    }

    public static Quaternion GetKeyFrameRotation(Keyframe[] keyframes, float float0, double double0) {
        Quaternion quaternion0 = new Quaternion();
        if (keyframes.length == 0) {
            return quaternion0;
        } else {
            int int0 = 0;

            while (int0 < keyframes.length - 1 && !(float0 < keyframes[int0 + 1].Time)) {
                int0++;
            }

            int int1 = (int0 + 1) % keyframes.length;
            Keyframe keyframe0 = keyframes[int0];
            Keyframe keyframe1 = keyframes[int1];
            float float1 = keyframe0.Time;
            float float2 = keyframe1.Time;
            float float3 = float2 - float1;
            if (float3 < 0.0F) {
                float3 = (float)(float3 + double0);
            }

            if (float3 > 0.0F) {
                float float4 = (float0 - float1) / float3;
                Quaternion quaternion1 = keyframe0.Rotation;
                Quaternion quaternion2 = keyframe1.Rotation;
                double double1 = quaternion1.getX() * quaternion2.getX()
                    + quaternion1.getY() * quaternion2.getY()
                    + quaternion1.getZ() * quaternion2.getZ()
                    + quaternion1.getW() * quaternion2.getW();
                end.set(quaternion2);
                if (double1 < 0.0) {
                    double1 *= -1.0;
                    end.setX(-end.getX());
                    end.setY(-end.getY());
                    end.setZ(-end.getZ());
                    end.setW(-end.getW());
                }

                double double2;
                double double3;
                if (1.0 - double1 > 1.0E-4) {
                    double double4 = Math.acos(double1);
                    double double5 = Math.sin(double4);
                    double2 = Math.sin((1.0 - float4) * double4) / double5;
                    double3 = Math.sin(float4 * double4) / double5;
                } else {
                    double2 = 1.0 - float4;
                    double3 = float4;
                }

                quaternion0.set(
                    (float)(double2 * quaternion1.getX() + double3 * end.getX()),
                    (float)(double2 * quaternion1.getY() + double3 * end.getY()),
                    (float)(double2 * quaternion1.getZ() + double3 * end.getZ()),
                    (float)(double2 * quaternion1.getW() + double3 * end.getW())
                );
            } else {
                quaternion0.set(keyframe0.Rotation);
            }

            return quaternion0;
        }
    }
}
