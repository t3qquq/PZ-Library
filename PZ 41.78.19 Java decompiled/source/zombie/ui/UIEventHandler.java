// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ui;

public interface UIEventHandler {
    void DoubleClick(String name, int x, int y);

    void ModalClick(String name, String chosen);

    void Selected(String name, int Selected, int LastSelected);
}
