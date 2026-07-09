// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import zombie.fileSystem.FileSystem;
import zombie.fileSystem.IFile;
import zombie.fileSystem.IFileTask2Callback;

final class AssetTask_LoadFromFileAsync extends AssetTask implements IFileTask2Callback {
    int m_async_op = -1;
    boolean bStream;

    AssetTask_LoadFromFileAsync(Asset asset, boolean boolean0) {
        super(asset);
        this.bStream = boolean0;
    }

    @Override
    public void execute() {
        FileSystem fileSystem = this.m_asset.getAssetManager().getOwner().getFileSystem();
        int int0 = 4 | (this.bStream ? 16 : 1);
        this.m_async_op = fileSystem.openAsync(fileSystem.getDefaultDevice(), this.m_asset.getPath().m_path, int0, this);
    }

    @Override
    public void cancel() {
        FileSystem fileSystem = this.m_asset.getAssetManager().getOwner().getFileSystem();
        fileSystem.cancelAsync(this.m_async_op);
        this.m_async_op = -1;
    }

    @Override
    public void onFileTaskFinished(IFile iFile, Object object) {
        this.m_async_op = -1;
        if (this.m_asset.m_priv.m_desired_state == Asset.State.READY) {
            if (object != Boolean.TRUE) {
                this.m_asset.m_priv.onLoadingFailed();
            } else if (!this.m_asset.getAssetManager().loadDataFromFile(this.m_asset, iFile)) {
                this.m_asset.m_priv.onLoadingFailed();
            } else {
                this.m_asset.m_priv.onLoadingSucceeded();
            }
        }
    }
}
