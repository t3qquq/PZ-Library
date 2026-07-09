// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
