// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiScene;
import org.lwjgl.util.vector.Quaternion;
import zombie.core.skinnedmodel.model.SkinningData;

public class ProcessedAiSceneParams {
    public AiScene scene = null;
    public JAssImpImporter.LoadMode mode = JAssImpImporter.LoadMode.Normal;
    public SkinningData skinnedTo = null;
    public String meshName = null;
    public float animBonesScaleModifier = 1.0F;
    public Quaternion animBonesRotateModifier = null;

    ProcessedAiSceneParams() {
    }

    public static ProcessedAiSceneParams create() {
        return new ProcessedAiSceneParams();
    }

    protected void set(ProcessedAiSceneParams processedAiSceneParams0) {
        this.scene = processedAiSceneParams0.scene;
        this.mode = processedAiSceneParams0.mode;
        this.skinnedTo = processedAiSceneParams0.skinnedTo;
        this.meshName = processedAiSceneParams0.meshName;
        this.animBonesScaleModifier = processedAiSceneParams0.animBonesScaleModifier;
        this.animBonesRotateModifier = processedAiSceneParams0.animBonesRotateModifier;
    }
}
