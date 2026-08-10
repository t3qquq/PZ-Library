// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import org.lwjgl.util.vector.Matrix4f;

public interface AnimTrackSampler {
    float getTotalTime();

    boolean isLooped();

    void moveToTime(float time);

    float getCurrentTime();

    void getBoneMatrix(int boneIdx, Matrix4f out_matrix);

    int getNumBones();
}
