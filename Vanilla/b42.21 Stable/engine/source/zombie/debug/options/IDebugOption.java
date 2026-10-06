// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.debug.options;

public interface IDebugOption {
    String getName();

    IDebugOptionGroup getParent();

    void setParent(IDebugOptionGroup parent);

    void onFullPathChanged();
}
