// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.asset;

public abstract class AssetTask {
    public Asset asset;

    public AssetTask(Asset asset) {
        this.asset = asset;
    }

    public abstract void execute();

    public abstract void cancel();
}
