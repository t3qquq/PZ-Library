// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.inventory;

public enum ItemType {
    None(0),
    Weapon(1),
    Food(2),
    Literature(3),
    Drainable(4),
    Clothing(5),
    Key(6),
    KeyRing(7),
    Moveable(8),
    AlarmClock(9),
    AlarmClockClothing(10);

    private int index;

    private ItemType(int int1) {
        this.index = int1;
    }

    public int index() {
        return this.index;
    }

    public static ItemType fromIndex(int value) {
        return ItemType.class.getEnumConstants()[value];
    }
}
