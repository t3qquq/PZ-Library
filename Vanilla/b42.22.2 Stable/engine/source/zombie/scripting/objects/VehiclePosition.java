// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.scripting.objects;

public enum VehiclePosition {
    OUTSIDE("outside"),
    INSIDE("inside"),
    OUTSIDE2("outside2");

    private final String id;

    VehiclePosition(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
