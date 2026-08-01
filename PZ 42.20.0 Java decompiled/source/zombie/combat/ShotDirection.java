// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.combat;

public enum ShotDirection {
    NORTH("North"),
    SOUTH("South"),
    LEFT("Left"),
    RIGHT("Right");

    private final String value;

    ShotDirection(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
