// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.animation.debug;

import java.util.List;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.iso.Vector2;

public final class AnimationTrackRecordingFrame extends GenericNameWeightRecordingFrame {
    private Vector2 m_deferredMovement = new Vector2();

    public AnimationTrackRecordingFrame(String fileKey) {
        super(fileKey);
    }

    @Override
    public void reset() {
        super.reset();
        this.m_deferredMovement.set(0.0F, 0.0F);
    }

    public void logAnimWeights(List<AnimationTrack> list, int[] ints, float[] floats, Vector2 vector) {
        for (int int0 = 0; int0 < ints.length; int0++) {
            int int1 = ints[int0];
            if (int1 < 0) {
                break;
            }

            float float0 = floats[int0];
            AnimationTrack animationTrack = (AnimationTrack)list.get(int1);
            String string = animationTrack.name;
            int int2 = animationTrack.getLayerIdx();
            this.logWeight(string, int2, float0);
        }

        this.m_deferredMovement.set(vector);
    }

    public Vector2 getDeferredMovement() {
        return this.m_deferredMovement;
    }

    @Override
    public void writeHeader(StringBuilder logLine) {
        logLine.append(",");
        logLine.append("dm.x").append(",").append("dm.y");
        super.writeHeader(logLine);
    }

    @Override
    protected void writeData(StringBuilder stringBuilder) {
        stringBuilder.append(",");
        stringBuilder.append(this.getDeferredMovement().x).append(",").append(this.getDeferredMovement().y);
        super.writeData(stringBuilder);
    }
}
