// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.textures;

import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.asset.FileTask_LoadImageData;
import zombie.asset.FileTask_LoadPackImage;
import zombie.core.opengl.RenderThread;
import zombie.core.utils.DirectBufferAllocator;
import zombie.fileSystem.FileSystem;

public final class TextureIDAssetManager extends AssetManager {
    public static final TextureIDAssetManager instance = new TextureIDAssetManager();

    @Override
    protected void startLoading(Asset asset) {
        TextureID textureID = (TextureID)asset;
        FileSystem fileSystem = this.getOwner().getFileSystem();
        if (textureID.assetParams != null && textureID.assetParams.subTexture != null) {
            FileSystem.SubTexture subTexture = textureID.assetParams.subTexture;
            FileTask_LoadPackImage fileTask_LoadPackImage = new FileTask_LoadPackImage(
                subTexture.m_pack_name, subTexture.m_page_name, fileSystem, object -> this.onFileTaskFinished(asset, object)
            );
            fileTask_LoadPackImage.setPriority(7);
            AssetTask_RunFileTask assetTask_RunFileTask0 = new AssetTask_RunFileTask(fileTask_LoadPackImage, asset);
            this.setTask(asset, assetTask_RunFileTask0);
            assetTask_RunFileTask0.execute();
        } else {
            FileTask_LoadImageData fileTask_LoadImageData = new FileTask_LoadImageData(
                asset.getPath().getPath(), fileSystem, object -> this.onFileTaskFinished(asset, object)
            );
            fileTask_LoadImageData.setPriority(7);
            AssetTask_RunFileTask assetTask_RunFileTask1 = new AssetTask_RunFileTask(fileTask_LoadImageData, asset);
            this.setTask(asset, assetTask_RunFileTask1);
            assetTask_RunFileTask1.execute();
        }
    }

    @Override
    protected void unloadData(Asset asset) {
        TextureID textureID = (TextureID)asset;
        if (!textureID.isDestroyed()) {
            RenderThread.invokeOnRenderContext(textureID::destroy);
        }
    }

    @Override
    protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
        return new TextureID(assetPath, this, (TextureID.TextureIDAssetParams)assetParams);
    }

    @Override
    protected void destroyAsset(Asset var1) {
    }

    private void onFileTaskFinished(Asset asset, Object object) {
        TextureID textureID = (TextureID)asset;
        if (object instanceof ImageData) {
            textureID.setImageData((ImageData)object);
            this.onLoadingSucceeded(asset);
        } else {
            this.onLoadingFailed(asset);
        }
    }

    public void waitFileTask() {
        while (DirectBufferAllocator.getBytesAllocated() > 52428800L) {
            try {
                Thread.sleep(20L);
            } catch (InterruptedException interruptedException) {
            }
        }
    }
}
