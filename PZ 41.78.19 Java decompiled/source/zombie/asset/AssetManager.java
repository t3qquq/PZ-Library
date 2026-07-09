// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import gnu.trove.map.hash.THashMap;
import java.util.ArrayList;
import zombie.debug.DebugLog;
import zombie.fileSystem.IFile;

public abstract class AssetManager implements AssetStateObserver {
    private final AssetManager.AssetTable m_assets = new AssetManager.AssetTable();
    private AssetManagers m_owner;
    private boolean m_is_unload_enabled = false;

    public void create(AssetType type, AssetManagers owner) {
        owner.add(type, this);
        this.m_owner = owner;
    }

    public void destroy() {
        this.m_assets.forEachValue(asset -> {
            if (!asset.isEmpty()) {
                DebugLog.Asset.println("Leaking asset " + asset.getPath());
            }

            this.destroyAsset(asset);
            return true;
        });
    }

    public void removeUnreferenced() {
        if (this.m_is_unload_enabled) {
            ArrayList arrayList = new ArrayList();
            this.m_assets.forEachValue(assetx -> {
                if (assetx.getRefCount() == 0) {
                    arrayList.add(assetx);
                }

                return true;
            });

            for (Asset asset : arrayList) {
                this.m_assets.remove(asset.getPath());
                this.destroyAsset(asset);
            }
        }
    }

    public Asset load(AssetPath path) {
        return this.load(path, null);
    }

    public Asset load(AssetPath path, AssetManager.AssetParams params) {
        if (!path.isValid()) {
            return null;
        } else {
            Asset asset = this.get(path);
            if (asset == null) {
                asset = this.createAsset(path, params);
                this.m_assets.put(path.getPath(), asset);
            }

            if (asset.isEmpty() && asset.m_priv.m_desired_state == Asset.State.EMPTY) {
                this.doLoad(asset, params);
            }

            asset.addRef();
            return asset;
        }
    }

    public void load(Asset asset) {
        if (asset.isEmpty() && asset.m_priv.m_desired_state == Asset.State.EMPTY) {
            this.doLoad(asset, null);
        }

        asset.addRef();
    }

    public void unload(AssetPath path) {
        Asset asset = this.get(path);
        if (asset != null) {
            this.unload(asset);
        }
    }

    public void unload(Asset asset) {
        int int0 = asset.rmRef();

        assert int0 >= 0;

        if (int0 == 0 && this.m_is_unload_enabled) {
            this.doUnload(asset);
        }
    }

    public void reload(AssetPath path) {
        Asset asset = this.get(path);
        if (asset != null) {
            this.reload(asset);
        }
    }

    public void reload(Asset asset) {
        this.reload(asset, null);
    }

    public void reload(Asset asset, AssetManager.AssetParams params) {
        this.doUnload(asset);
        this.doLoad(asset, params);
    }

    public void enableUnload(boolean enable) {
        this.m_is_unload_enabled = enable;
        if (enable) {
            this.m_assets.forEachValue(asset -> {
                if (asset.getRefCount() == 0) {
                    this.doUnload(asset);
                }

                return true;
            });
        }
    }

    private void doLoad(Asset asset, AssetManager.AssetParams assetParams) {
        if (asset.m_priv.m_desired_state != Asset.State.READY) {
            asset.m_priv.m_desired_state = Asset.State.READY;
            asset.setAssetParams(assetParams);
            this.startLoading(asset);
        }
    }

    private void doUnload(Asset asset) {
        if (asset.m_priv.m_task != null) {
            asset.m_priv.m_task.cancel();
            asset.m_priv.m_task = null;
        }

        asset.m_priv.m_desired_state = Asset.State.EMPTY;
        this.unloadData(asset);

        assert asset.m_priv.m_empty_dep_count <= 1;

        asset.m_priv.m_empty_dep_count = 1;
        asset.m_priv.m_failed_dep_count = 0;
        asset.m_priv.checkState();
    }

    @Override
    public void onStateChanged(Asset.State old_state, Asset.State new_state, Asset asset) {
    }

    protected void startLoading(Asset asset) {
        if (asset.m_priv.m_task == null) {
            asset.m_priv.m_task = new AssetTask_LoadFromFileAsync(asset, false);
            asset.m_priv.m_task.execute();
        }
    }

    protected final void onLoadingSucceeded(Asset asset) {
        asset.m_priv.onLoadingSucceeded();
    }

    protected final void onLoadingFailed(Asset asset) {
        asset.m_priv.onLoadingFailed();
    }

    protected final void setTask(Asset asset, AssetTask assetTask) {
        if (asset.m_priv.m_task != null) {
            if (assetTask == null) {
                asset.m_priv.m_task = null;
            }
        } else {
            asset.m_priv.m_task = assetTask;
        }
    }

    protected boolean loadDataFromFile(Asset var1, IFile var2) {
        throw new RuntimeException("not implemented");
    }

    protected void unloadData(Asset var1) {
    }

    public AssetManager.AssetTable getAssetTable() {
        return this.m_assets;
    }

    public AssetManagers getOwner() {
        return this.m_owner;
    }

    protected abstract Asset createAsset(AssetPath var1, AssetManager.AssetParams var2);

    protected abstract void destroyAsset(Asset var1);

    protected Asset get(AssetPath assetPath) {
        return this.m_assets.get(assetPath.getPath());
    }

    public static class AssetParams {
    }

    public static final class AssetTable extends THashMap<String, Asset> {
    }
}
