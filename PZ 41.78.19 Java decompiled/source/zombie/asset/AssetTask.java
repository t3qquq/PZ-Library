// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

public abstract class AssetTask {
    public Asset m_asset;

    public AssetTask(Asset asset) {
        this.m_asset = asset;
    }

    public abstract void execute();

    public abstract void cancel();
}
