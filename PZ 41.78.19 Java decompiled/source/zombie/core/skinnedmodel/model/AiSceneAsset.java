// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import jassimp.AiPostProcessSteps;
import jassimp.AiScene;
import java.util.EnumSet;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;

@Deprecated
public final class AiSceneAsset extends Asset {
    AiScene m_scene;
    EnumSet<AiPostProcessSteps> m_post_process_step_set;
    AiSceneAsset.AiSceneAssetParams assetParams;
    public static final AssetType ASSET_TYPE = new AssetType("AiScene");

    protected AiSceneAsset(AssetPath assetPath, AssetManager assetManager, AiSceneAsset.AiSceneAssetParams aiSceneAssetParams) {
        super(assetPath, assetManager);
        this.assetParams = aiSceneAssetParams;
        this.m_scene = null;
        this.m_post_process_step_set = aiSceneAssetParams.post_process_step_set;
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }

    public static final class AiSceneAssetParams extends AssetManager.AssetParams {
        EnumSet<AiPostProcessSteps> post_process_step_set;
    }
}
