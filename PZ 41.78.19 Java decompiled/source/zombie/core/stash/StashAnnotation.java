// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.stash;

import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import zombie.util.Type;

public final class StashAnnotation {
    public String symbol;
    public String text;
    public float x;
    public float y;
    public float r;
    public float g;
    public float b;

    public void fromLua(KahluaTable table) {
        KahluaTableImpl kahluaTableImpl = (KahluaTableImpl)table;
        this.symbol = Type.tryCastTo(table.rawget("symbol"), String.class);
        this.text = Type.tryCastTo(table.rawget("text"), String.class);
        this.x = kahluaTableImpl.rawgetFloat("x");
        this.y = kahluaTableImpl.rawgetFloat("y");
        this.r = kahluaTableImpl.rawgetFloat("r");
        this.g = kahluaTableImpl.rawgetFloat("g");
        this.b = kahluaTableImpl.rawgetFloat("b");
    }
}
