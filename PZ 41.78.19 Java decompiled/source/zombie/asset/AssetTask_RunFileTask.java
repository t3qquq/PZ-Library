// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;

public final class AssetTask_RunFileTask extends AssetTask {
    protected final FileTask m_file_task;
    int m_async_op = -1;

    public AssetTask_RunFileTask(FileTask fileTask, Asset asset) {
        super(asset);
        this.m_file_task = fileTask;
    }

    @Override
    public void execute() {
        FileSystem fileSystem = this.m_asset.getAssetManager().getOwner().getFileSystem();
        this.m_async_op = fileSystem.runAsync(this.m_file_task);
    }

    @Override
    public void cancel() {
        FileSystem fileSystem = this.m_asset.getAssetManager().getOwner().getFileSystem();
        fileSystem.cancelAsync(this.m_async_op);
        this.m_async_op = -1;
    }
}
