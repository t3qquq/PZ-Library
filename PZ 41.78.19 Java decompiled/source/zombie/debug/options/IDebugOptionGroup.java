// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.debug.options;

public interface IDebugOptionGroup extends IDebugOption {
    Iterable<IDebugOption> getChildren();

    void addChild(IDebugOption childOption);

    void onChildAdded(IDebugOption newChild);

    void onDescendantAdded(IDebugOption newDescendant);
}
