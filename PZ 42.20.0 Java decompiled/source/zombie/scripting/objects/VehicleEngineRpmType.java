// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

public enum VehicleEngineRpmType {
    FIREBIRD("firebird"),
    VAN("van");

    private final String id;

    VehicleEngineRpmType(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
