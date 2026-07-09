// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;

public class AnimNodeAssetManager extends AssetManager {
    public static final AnimNodeAssetManager instance = new AnimNodeAssetManager();

    @Override
    protected void startLoading(Asset asset) {
        AnimNodeAsset animNodeAsset = (AnimNodeAsset)asset;
        animNodeAsset.m_animNode = AnimNode.Parse(asset.getPath().getPath());
        if (animNodeAsset.m_animNode == null) {
            this.onLoadingFailed(asset);
        } else {
            this.onLoadingSucceeded(asset);
        }
    }

    @Override
    public void onStateChanged(Asset.State state0, Asset.State state1, Asset asset) {
        super.onStateChanged(state0, state1, asset);
        if (state1 == Asset.State.READY) {
        }
    }

    @Override
    protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams var2) {
        return new AnimNodeAsset(assetPath, this);
    }

    @Override
    protected void destroyAsset(Asset var1) {
    }
}
