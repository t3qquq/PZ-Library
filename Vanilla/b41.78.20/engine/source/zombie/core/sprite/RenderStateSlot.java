// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.sprite;

public enum RenderStateSlot {
    Populating(0),
    Ready(1),
    Rendering(2);

    private final int m_index;

    private RenderStateSlot(int int1) {
        this.m_index = int1;
    }

    public int index() {
        return this.m_index;
    }

    public int count() {
        return 3;
    }
}
