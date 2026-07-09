// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.model;

import java.util.HashSet;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.asset.Asset;
import zombie.asset.AssetManager;
import zombie.asset.AssetPath;
import zombie.asset.AssetTask_RunFileTask;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.debug.DebugLog;
import zombie.fileSystem.FileSystem;
import zombie.util.StringUtils;

public final class MeshAssetManager extends AssetManager {
    public static final MeshAssetManager instance = new MeshAssetManager();
    private final HashSet<String> m_watchedFiles = new HashSet<>();
    private final PredicatedFileWatcher m_watcher = new PredicatedFileWatcher(MeshAssetManager::isWatched, MeshAssetManager::watchedFileChanged);

    private MeshAssetManager() {
        DebugFileWatcher.instance.add(this.m_watcher);
    }

    @Override
    protected void startLoading(Asset asset) {
        ModelMesh modelMesh = (ModelMesh)asset;
        FileSystem fileSystem = this.getOwner().getFileSystem();
        FileTask_LoadMesh fileTask_LoadMesh = new FileTask_LoadMesh(modelMesh, fileSystem, object -> this.loadCallback(modelMesh, object));
        fileTask_LoadMesh.setPriority(6);
        AssetTask_RunFileTask assetTask_RunFileTask = new AssetTask_RunFileTask(fileTask_LoadMesh, asset);
        this.setTask(asset, assetTask_RunFileTask);
        assetTask_RunFileTask.execute();
    }

    private void loadCallback(ModelMesh modelMesh, Object object) {
        if (object instanceof ProcessedAiScene) {
            modelMesh.onLoadedX((ProcessedAiScene)object);
            this.onLoadingSucceeded(modelMesh);
        } else if (object instanceof ModelTxt) {
            modelMesh.onLoadedTxt((ModelTxt)object);
            this.onLoadingSucceeded(modelMesh);
        } else {
            DebugLog.General.warn("Failed to load asset: " + modelMesh.getPath());
            this.onLoadingFailed(modelMesh);
        }
    }

    @Override
    protected Asset createAsset(AssetPath assetPath, AssetManager.AssetParams assetParams) {
        return new ModelMesh(assetPath, this, (ModelMesh.MeshAssetParams)assetParams);
    }

    @Override
    protected void destroyAsset(Asset var1) {
    }

    private static boolean isWatched(String string0) {
        if (!StringUtils.endsWithIgnoreCase(string0, ".fbx") && !StringUtils.endsWithIgnoreCase(string0, ".x")) {
            return false;
        } else {
            String string1 = ZomboidFileSystem.instance.getString(string0);
            return instance.m_watchedFiles.contains(string1);
        }
    }

    private static void watchedFileChanged(String string0) {
        DebugLog.Asset.printf("%s changed\n", string0);
        String string1 = ZomboidFileSystem.instance.getString(string0);
        instance.getAssetTable().forEachValue(asset -> {
            ModelMesh modelMesh = (ModelMesh)asset;
            if (!modelMesh.isEmpty() && string1.equalsIgnoreCase(modelMesh.m_fullPath)) {
                ModelMesh.MeshAssetParams meshAssetParams = new ModelMesh.MeshAssetParams();
                meshAssetParams.animationsMesh = modelMesh.m_animationsMesh;
                meshAssetParams.bStatic = modelMesh.bStatic;
                instance.reload(asset, meshAssetParams);
            }

            return true;
        });
    }

    public void addWatchedFile(String string) {
        this.m_watchedFiles.add(string);
    }
}
