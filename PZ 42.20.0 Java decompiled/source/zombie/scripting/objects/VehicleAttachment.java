// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

public enum VehicleAttachment {
    TRAILER("trailer"),
    TRAILERFRONT("trailerfront");

    private final String id;

    VehicleAttachment(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
