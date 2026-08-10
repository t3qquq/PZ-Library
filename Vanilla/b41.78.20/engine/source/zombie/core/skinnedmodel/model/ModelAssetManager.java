// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;

public final class ModelAssetManager extends AssetManager {
    public static final ModelAssetManager instance = new ModelAssetManager();

    @Override
    protected void startLoading(Asset var1) {
    }

    @Override
    protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
        return new Model(assetPath, this, (Model.ModelAssetParams)assetParams);
    }

    @Override
    protected void destroyAsset(Asset var1) {
    }
}
