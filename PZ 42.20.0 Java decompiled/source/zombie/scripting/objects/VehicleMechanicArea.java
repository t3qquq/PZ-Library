// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

public enum VehicleMechanicArea {
    BACK("Back"),
    ENGINE("Engine"),
    INTERIOR("Interior"),
    LEFT("Left"),
    RIGHT("Right"),
    UNDER("Under");

    private final String id;

    VehicleMechanicArea(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
