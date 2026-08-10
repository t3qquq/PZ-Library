// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.asset;

public abstract class AssetTask {
    public Asset m_asset;

    public AssetTask(Asset asset) {
        this.m_asset = asset;
    }

    public abstract void execute();

    public abstract void cancel();
}
