// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.runtime;

import java.util.List;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.ModelManager;
import zombie.core.skinnedmodel.animation.AnimationClip;
import zombie.core.skinnedmodel.animation.Keyframe;
import zombie.scripting.ScriptParser;

public final class CopyFrame implements IRuntimeAnimationCommand {
    protected int m_frame;
    protected int m_FPS = 30;
    protected String m_source;
    protected int m_sourceFrame;
    protected int m_sourceFPS = 30;

    @Override
    public void parse(ScriptParser.Block block) {
        for (ScriptParser.Value value : block.values) {
            String string0 = value.getKey().trim();
            String string1 = value.getValue().trim();
            if ("source".equalsIgnoreCase(string0)) {
                this.m_source = string1;
            } else if ("frame".equalsIgnoreCase(string0)) {
                this.m_frame = PZMath.tryParseInt(string1, 1);
            } else if ("sourceFrame".equalsIgnoreCase(string0)) {
                this.m_sourceFrame = PZMath.tryParseInt(string1, 1);
            }
        }
    }

    @Override
    public void exec(List<Keyframe> list) {
        AnimationClip animationClip = ModelManager.instance.getAnimationClip(this.m_source);

        for (int int0 = 0; int0 < 60; int0++) {
            Keyframe[] keyframes = animationClip.getBoneFramesAt(int0);
            if (keyframes.length != 0) {
                Keyframe keyframe0 = keyframes[0];
                Keyframe keyframe1 = new Keyframe();
                keyframe1.Bone = keyframe0.Bone;
                keyframe1.BoneName = keyframe0.BoneName;
                keyframe1.Time = (float)(this.m_frame - 1) / this.m_FPS;
                keyframe1.Position = KeyframeUtil.GetKeyFramePosition(keyframes, (float)(this.m_sourceFrame - 1) / this.m_sourceFPS, animationClip.Duration);
                keyframe1.Rotation = KeyframeUtil.GetKeyFrameRotation(keyframes, (float)(this.m_sourceFrame - 1) / this.m_sourceFPS, animationClip.Duration);
                keyframe1.Scale = keyframe0.Scale;
                list.add(keyframe1);
            }
        }
    }
}
