// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.text.templating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.debug.DebugLog;

/**
 * TurboTuTone.  A generic non-pooled ReplaceProvider that can be used for example in scripting,  where the provider could provide forced overrides for certain template keys.
 */
public class ReplaceProvider implements IReplaceProvider {
    protected final Map<String, IReplace> m_keys = new HashMap<>();

    public void addKey(String key, final String value) {
        this.addReplacer(key, new IReplace() {
            @Override
            public String getString() {
                return value;
            }
        });
    }

    public void addKey(String key, KahluaTableImpl table) {
        try {
            ArrayList arrayList = new ArrayList();

            for (int int0 = 1; int0 < table.len() + 1; int0++) {
                arrayList.add((String)table.rawget(int0));
            }

            if (arrayList.size() > 0) {
                this.addReplacer(key, new ReplaceList(arrayList));
            } else {
                DebugLog.log("ReplaceProvider -> key '" + key + "' contains no entries, ignoring.");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void addReplacer(String key, IReplace replace) {
        if (this.m_keys.containsKey(key.toLowerCase())) {
            DebugLog.log("ReplaceProvider -> Warning: key '" + key + "' replaces an existing key.");
        }

        this.m_keys.put(key.toLowerCase(), replace);
    }

    @Override
    public boolean hasReplacer(String key) {
        return this.m_keys.containsKey(key);
    }

    @Override
    public IReplace getReplacer(String key) {
        return this.m_keys.get(key);
    }
}
