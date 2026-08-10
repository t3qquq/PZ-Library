// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.interfaces;

public interface IDestroyable {
    /**
     * destory the object
     */
    void destroy();

    /**
     * returns if the object is destryed or not
     */
    boolean isDestroyed();
}
