// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.advancedanimation;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetType;

public class AnimNodeAsset extends Asset {
    public static final AssetType ASSET_TYPE = new AssetType("AnimNode");
    public AnimNode m_animNode;

    protected AnimNodeAsset(AssetPath assetPath, AssetManager assetManager) {
        super(assetPath, assetManager);
    }

    @Override
    public AssetType getType() {
        return ASSET_TYPE;
    }
}
