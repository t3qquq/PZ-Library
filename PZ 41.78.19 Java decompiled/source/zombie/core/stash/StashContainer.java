// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.stash;

public final class StashContainer {
    public String room;
    public String containerSprite;
    public String containerType;
    public int contX = -1;
    public int contY = -1;
    public int contZ = -1;
    public String containerItem;

    public StashContainer(String string0, String string1, String string2) {
        if (string0 == null) {
            this.room = "all";
        } else {
            this.room = string0;
        }

        this.containerSprite = string1;
        this.containerType = string2;
    }
}
