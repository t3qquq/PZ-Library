// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.asset;

public interface AssetStateObserver {
    void onStateChanged(Asset.State oldState, Asset.State newState, Asset asset);
}
