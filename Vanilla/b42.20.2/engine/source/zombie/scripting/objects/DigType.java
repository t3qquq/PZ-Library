// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.scripting.objects;

import zombie.UsedFromLua;

@UsedFromLua
public enum DigType {
    HOE("Hoe"),
    PICK_AXE("PickAxe"),
    SHOVEL("Shovel"),
    TROWEL("Trowel");

    private final String id;

    DigType(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
