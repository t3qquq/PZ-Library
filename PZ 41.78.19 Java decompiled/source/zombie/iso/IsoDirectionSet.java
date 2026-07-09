// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

public class IsoDirectionSet {
    public int set = 0;

    public static IsoDirections rotate(IsoDirections dir, int amount) {
        amount += dir.index();
        amount %= 8;
        return IsoDirections.fromIndex(amount);
    }

    public IsoDirections getNext() {
        for (int int0 = 0; int0 < 8; int0++) {
            int int1 = 1 << int0;
            if ((this.set & int1) != 0) {
                this.set ^= int1;
                return IsoDirections.fromIndex(int0);
            }
        }

        return IsoDirections.Max;
    }
}
