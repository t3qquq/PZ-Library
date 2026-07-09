// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Quaternion;

/**
 * Created by LEMMYATI on 03/01/14.
 */
public final class AnimationClip {
    public final String Name;
    public StaticAnimation staticClip;
    private final AnimationClip.KeyframeByBoneIndexElement[] m_KeyFramesByBoneIndex;
    public float Duration;
    private final List<Keyframe> m_rootMotionKeyframes = new ArrayList<>();
    private final Keyframe[] KeyframeArray;
    private static final Quaternion orientation = new Quaternion(-0.07107F, 0.0F, 0.0F, 0.07107F);

    public AnimationClip(float duration, List<Keyframe> keyframes, String name, boolean bKeepLastFrame) {
        this.Duration = duration;
        this.KeyframeArray = keyframes.toArray(new Keyframe[0]);
        this.Name = name;
        this.m_KeyFramesByBoneIndex = new AnimationClip.KeyframeByBoneIndexElement[60];
        ArrayList arrayList = new ArrayList();
        int int0 = this.KeyframeArray.length - (bKeepLastFrame ? 0 : 1);

        for (int int1 = 0; int1 < 60; int1++) {
            arrayList.clear();

            for (int int2 = 0; int2 < int0; int2++) {
                Keyframe keyframe = this.KeyframeArray[int2];
                if (keyframe.Bone == int1) {
                    arrayList.add(keyframe);
                }
            }

            this.m_KeyFramesByBoneIndex[int1] = new AnimationClip.KeyframeByBoneIndexElement(arrayList);
        }
    }

    public Keyframe[] getBoneFramesAt(int idx) {
        return this.m_KeyFramesByBoneIndex[idx].m_keyframes;
    }

    public int getRootMotionFrameCount() {
        return this.m_rootMotionKeyframes.size();
    }

    public Keyframe getRootMotionFrameAt(int idx) {
        return this.m_rootMotionKeyframes.get(idx);
    }

    public Keyframe[] getKeyframes() {
        return this.KeyframeArray;
    }

    public float getTranslationLength(BoneAxis deferredBoneAxis) {
        float float0 = this.KeyframeArray[this.KeyframeArray.length - 1].Position.x - this.KeyframeArray[0].Position.x;
        float float1;
        if (deferredBoneAxis == BoneAxis.Y) {
            float1 = -this.KeyframeArray[this.KeyframeArray.length - 1].Position.z + this.KeyframeArray[0].Position.z;
        } else {
            float1 = this.KeyframeArray[this.KeyframeArray.length - 1].Position.y - this.KeyframeArray[0].Position.y;
        }

        return (float)Math.sqrt(float0 * float0 + float1 * float1);
    }

    private static class KeyframeByBoneIndexElement {
        final Keyframe[] m_keyframes;

        KeyframeByBoneIndexElement(List<Keyframe> list) {
            this.m_keyframes = list.toArray(new Keyframe[0]);
        }
    }
}
