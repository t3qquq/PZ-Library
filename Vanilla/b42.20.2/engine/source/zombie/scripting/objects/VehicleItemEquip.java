// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.scripting.objects;

public enum VehicleItemEquip {
    PRIMARY("primary"),
    SECONDARY("secondary");

    private final String id;

    VehicleItemEquip(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
