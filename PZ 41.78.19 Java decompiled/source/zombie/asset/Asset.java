// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

import java.util.ArrayList;

public abstract class Asset {
    protected final AssetManager m_asset_manager;
    private AssetPath m_path;
    private int m_ref_count;
    final Asset.PRIVATE m_priv = new Asset.PRIVATE();

    protected Asset(AssetPath assetPath, AssetManager assetManager) {
        this.m_ref_count = 0;
        this.m_path = assetPath;
        this.m_asset_manager = assetManager;
    }

    public abstract AssetType getType();

    public Asset.State getState() {
        return this.m_priv.m_current_state;
    }

    public boolean isEmpty() {
        return this.m_priv.m_current_state == Asset.State.EMPTY;
    }

    public boolean isReady() {
        return this.m_priv.m_current_state == Asset.State.READY;
    }

    public boolean isFailure() {
        return this.m_priv.m_current_state == Asset.State.FAILURE;
    }

    public void onCreated(Asset.State state) {
        this.m_priv.onCreated(state);
    }

    public int getRefCount() {
        return this.m_ref_count;
    }

    public Asset.ObserverCallback getObserverCb() {
        if (this.m_priv.m_cb == null) {
            this.m_priv.m_cb = new Asset.ObserverCallback();
        }

        return this.m_priv.m_cb;
    }

    public AssetPath getPath() {
        return this.m_path;
    }

    public AssetManager getAssetManager() {
        return this.m_asset_manager;
    }

    protected void onBeforeReady() {
    }

    protected void onBeforeEmpty() {
    }

    public void addDependency(Asset dependent_asset) {
        this.m_priv.addDependency(dependent_asset);
    }

    public void removeDependency(Asset dependent_asset) {
        this.m_priv.removeDependency(dependent_asset);
    }

    int addRef() {
        return ++this.m_ref_count;
    }

    int rmRef() {
        return --this.m_ref_count;
    }

    public void setAssetParams(AssetManager.AssetParams params) {
    }

    public static final class ObserverCallback extends ArrayList<AssetStateObserver> {
        public void invoke(Asset.State oldState, Asset.State newState, Asset asset) {
            int int0 = this.size();

            for (int int1 = 0; int1 < int0; int1++) {
                this.get(int1).onStateChanged(oldState, newState, asset);
            }
        }
    }

    final class PRIVATE implements AssetStateObserver {
        Asset.State m_current_state = Asset.State.EMPTY;
        Asset.State m_desired_state = Asset.State.EMPTY;
        int m_empty_dep_count = 1;
        int m_failed_dep_count = 0;
        Asset.ObserverCallback m_cb;
        AssetTask m_task = null;

        void onCreated(Asset.State state) {
            assert this.m_empty_dep_count == 1;

            assert this.m_failed_dep_count == 0;

            this.m_current_state = state;
            this.m_desired_state = Asset.State.READY;
            this.m_failed_dep_count = state == Asset.State.FAILURE ? 1 : 0;
            this.m_empty_dep_count = 0;
        }

        void addDependency(Asset asset) {
            assert this.m_desired_state != Asset.State.EMPTY;

            asset.getObserverCb().add(this);
            if (asset.isEmpty()) {
                this.m_empty_dep_count++;
            }

            if (asset.isFailure()) {
                this.m_failed_dep_count++;
            }

            this.checkState();
        }

        void removeDependency(Asset asset) {
            asset.getObserverCb().remove(this);
            if (asset.isEmpty()) {
                assert this.m_empty_dep_count > 0;

                this.m_empty_dep_count--;
            }

            if (asset.isFailure()) {
                assert this.m_failed_dep_count > 0;

                this.m_failed_dep_count--;
            }

            this.checkState();
        }

        @Override
        public void onStateChanged(Asset.State arg0, Asset.State arg1, Asset arg2) {
            assert arg0 != arg1;

            assert this.m_current_state != Asset.State.EMPTY || this.m_desired_state != Asset.State.EMPTY;

            if (arg0 == Asset.State.EMPTY) {
                assert this.m_empty_dep_count > 0;

                this.m_empty_dep_count--;
            }

            if (arg0 == Asset.State.FAILURE) {
                assert this.m_failed_dep_count > 0;

                this.m_failed_dep_count--;
            }

            if (arg1 == Asset.State.EMPTY) {
                this.m_empty_dep_count++;
            }

            if (arg1 == Asset.State.FAILURE) {
                this.m_failed_dep_count++;
            }

            this.checkState();
        }

        void onLoadingSucceeded() {
            assert this.m_current_state != Asset.State.READY;

            assert this.m_empty_dep_count == 1;

            this.m_empty_dep_count--;
            this.m_task = null;
            this.checkState();
        }

        void onLoadingFailed() {
            assert this.m_current_state != Asset.State.READY;

            assert this.m_empty_dep_count == 1;

            this.m_failed_dep_count++;
            this.m_empty_dep_count--;
            this.m_task = null;
            this.checkState();
        }

        void checkState() {
            Asset.State state = this.m_current_state;
            if (this.m_failed_dep_count > 0 && this.m_current_state != Asset.State.FAILURE) {
                this.m_current_state = Asset.State.FAILURE;
                Asset.this.getAssetManager().onStateChanged(state, this.m_current_state, Asset.this);
                if (this.m_cb != null) {
                    this.m_cb.invoke(state, this.m_current_state, Asset.this);
                }
            }

            if (this.m_failed_dep_count == 0) {
                if (this.m_empty_dep_count == 0 && this.m_current_state != Asset.State.READY && this.m_desired_state != Asset.State.EMPTY) {
                    Asset.this.onBeforeReady();
                    this.m_current_state = Asset.State.READY;
                    Asset.this.getAssetManager().onStateChanged(state, this.m_current_state, Asset.this);
                    if (this.m_cb != null) {
                        this.m_cb.invoke(state, this.m_current_state, Asset.this);
                    }
                }

                if (this.m_empty_dep_count > 0 && this.m_current_state != Asset.State.EMPTY) {
                    Asset.this.onBeforeEmpty();
                    this.m_current_state = Asset.State.EMPTY;
                    Asset.this.getAssetManager().onStateChanged(state, this.m_current_state, Asset.this);
                    if (this.m_cb != null) {
                        this.m_cb.invoke(state, this.m_current_state, Asset.this);
                    }
                }
            }
        }
    }

    public static enum State {
        EMPTY,
        READY,
        FAILURE;
    }
}
