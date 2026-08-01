// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.scripting.objects;

import zombie.UsedFromLua;

@UsedFromLua
public enum WallCoveringType {
    PAINT_THUMP("paintThump"),
    PAINT_SIGN("paintSign"),
    PLASTER("plaster"),
    WALLPAPER("wallpaper");

    private final String typeString;

    WallCoveringType(final String typeString) {
        this.typeString = typeString;
    }

    @Override
    public String toString() {
        return this.typeString;
    }

    public static WallCoveringType typeOf(String typeString) {
        for (WallCoveringType type : values()) {
            if (type.toString().equalsIgnoreCase(typeString)) {
                return type;
            }
        }

        throw new IllegalArgumentException("No enum constant WallCoveringType with value " + typeString);
    }
}
