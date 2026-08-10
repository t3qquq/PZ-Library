// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation.sharedskele;

import java.util.HashMap;
import zombie.core.skinnedmodel.animation.AnimationClip;

public class SharedSkeleAnimationRepository {
    private final HashMap<AnimationClip, SharedSkeleAnimationTrack> m_tracksMap = new HashMap<>();

    public SharedSkeleAnimationTrack getTrack(AnimationClip clip) {
        return this.m_tracksMap.get(clip);
    }

    public void setTrack(AnimationClip clip, SharedSkeleAnimationTrack track) {
        this.m_tracksMap.put(clip, track);
    }
}
