// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model.jassimp;

import jassimp.AiMesh;

public class ImportedSkeletonParams extends ProcessedAiSceneParams {
    AiMesh mesh = null;

    ImportedSkeletonParams() {
    }

    public static ImportedSkeletonParams create(ProcessedAiSceneParams processedAiSceneParams, AiMesh aiMesh) {
        ImportedSkeletonParams importedSkeletonParams = new ImportedSkeletonParams();
        importedSkeletonParams.set(processedAiSceneParams);
        importedSkeletonParams.mesh = aiMesh;
        return importedSkeletonParams;
    }
}
