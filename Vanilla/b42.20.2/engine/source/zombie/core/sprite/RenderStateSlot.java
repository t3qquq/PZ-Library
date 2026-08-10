// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.core.sprite;

public enum RenderStateSlot {
    Populating(0),
    Ready(1),
    Rendering(2);

    private final int index;

    RenderStateSlot(final int index) {
        this.index = index;
    }

    public int index() {
        return this.index;
    }

    public int count() {
        return 3;
    }
}
