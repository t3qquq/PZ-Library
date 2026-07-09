// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.util;

import java.util.HashMap;

public final class SharedStrings {
    private final HashMap<String, String> strings = new HashMap<>();

    public String get(String s) {
        String string = this.strings.get(s);
        if (string == null) {
            this.strings.put(s, s);
            string = s;
        }

        return string;
    }

    public void clear() {
        this.strings.clear();
    }
}
