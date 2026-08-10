// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.scripting.objects;

public enum VehicleTable {
    HEADLIGHT("headlight"),
    INSTALL("install"),
    UNINSTALL("uninstall");

    private final String id;

    VehicleTable(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
