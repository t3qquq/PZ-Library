// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

public final class MetaObject {
    int type;
    int x;
    int y;
    RoomDef def;
    boolean bUsed = false;

    public MetaObject(int _type, int _x, int _y, RoomDef _def) {
        this.type = _type;
        this.x = _x;
        this.y = _y;
        this.def = _def;
    }

    public RoomDef getRoom() {
        return this.def;
    }

    public boolean getUsed() {
        return this.bUsed;
    }

    public void setUsed(boolean _bUsed) {
        this.bUsed = _bUsed;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getType() {
        return this.type;
    }
}
