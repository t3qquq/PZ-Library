// Decompiled on 월 8월 10 10:22:41 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.util.ArrayList;

/**
 * Created by ChrisWood (Tanglewood Games Limited) on 01/11/2017.
 */
public class IsoGridStack {
    public ArrayList<ArrayList<IsoGridSquare>> Squares;

    public IsoGridStack(int count) {
        this.Squares = new ArrayList<>(count);

        for (int int0 = 0; int0 < count; int0++) {
            this.Squares.add(new ArrayList<>(5000));
        }
    }
}
