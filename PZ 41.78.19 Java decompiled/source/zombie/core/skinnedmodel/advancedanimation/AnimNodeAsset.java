// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
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
