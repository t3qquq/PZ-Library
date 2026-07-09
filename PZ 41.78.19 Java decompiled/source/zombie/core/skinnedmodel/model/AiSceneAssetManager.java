// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import jassimp.AiPostProcessSteps;
import jassimp.AiScene;
import jassimp.Jassimp;
import java.util.EnumSet;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;

@Deprecated
public final class AiSceneAssetManager extends AssetManager {
    public static final AiSceneAssetManager instance = new AiSceneAssetManager();

    @Override
    protected void startLoading(Asset asset) {
        FileSystem fileSystem = asset.getAssetManager().getOwner().getFileSystem();
        AiSceneAssetManager.FileTask_LoadAiScene fileTask_LoadAiScene = new AiSceneAssetManager.FileTask_LoadAiScene(
            asset.getPath().getPath(),
            ((AiSceneAsset)asset).m_post_process_step_set,
            object -> this.onFileTaskFinished((AiSceneAsset)asset, object),
            fileSystem
        );
        AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadAiScene, asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }

    public void onFileTaskFinished(AiSceneAsset aiSceneAsset, Object object) {
        if (object instanceof AiScene) {
            aiSceneAsset.m_scene = (AiScene)object;
            this.onLoadingSucceeded(aiSceneAsset);
        } else {
            this.onLoadingFailed(aiSceneAsset);
        }
    }

    @Override
    protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
        return new AiSceneAsset(assetPath, this, (AiSceneAsset.AiSceneAssetParams)assetParams);
    }

    @Override
    protected void destroyAsset(Asset var1) {
    }

    static class FileTask_LoadAiScene extends FileTask {
        String m_filename;
        EnumSet<AiPostProcessSteps> m_post_process_step_set;

        public FileTask_LoadAiScene(String string, EnumSet<AiPostProcessSteps> enumSet, IFileTaskCallback iFileTaskCallback, FileSystem fileSystem) {
            super(fileSystem, iFileTaskCallback);
            this.m_filename = string;
            this.m_post_process_step_set = enumSet;
        }

        @Override
        public String getErrorMessage() {
            return this.m_filename;
        }

        @Override
        public void done() {
            this.m_filename = null;
            this.m_post_process_step_set = null;
        }

        @Override
        public Object call() throws Exception {
            ZomboidFileSystem.instance.validatePrefix(this.m_filename);
            return Jassimp.importFile(this.m_filename, this.m_post_process_step_set);
        }
    }
}
