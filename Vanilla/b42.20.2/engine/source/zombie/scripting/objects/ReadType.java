// Decompiled with Zomboid Decompiler v0.3.2 using Vineflower.
package zombie.scripting.objects;

public enum ReadType {
    BOOK("book"),
    NEWSPAPER("newspaper"),
    PHOTO("photo");

    private final String id;

    ReadType(final String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.id;
    }
}
