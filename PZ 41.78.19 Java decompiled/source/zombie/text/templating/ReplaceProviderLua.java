// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.text.templating;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedDeque;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.debug.DebugLog;

public class ReplaceProviderLua extends ReplaceProvider {
    private static final ConcurrentLinkedDeque<ReplaceSingle> pool_single = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<ReplaceList> pool_list = new ConcurrentLinkedDeque<>();
    private static final ConcurrentLinkedDeque<ReplaceProviderLua> pool = new ConcurrentLinkedDeque<>();

    private static ReplaceSingle alloc_single() {
        ReplaceSingle replaceSingle = pool_single.poll();
        if (replaceSingle == null) {
            replaceSingle = new ReplaceSingle();
        }

        return replaceSingle;
    }

    private static void release_single(ReplaceSingle replaceSingle) {
        pool_single.offer(replaceSingle);
    }

    private static ReplaceList alloc_list() {
        ReplaceList replaceList = pool_list.poll();
        if (replaceList == null) {
            replaceList = new ReplaceList();
        }

        return replaceList;
    }

    private static void release_list(ReplaceList replaceList) {
        replaceList.getReplacements().clear();
        pool_list.offer(replaceList);
    }

    protected static ReplaceProviderLua Alloc() {
        ReplaceProviderLua replaceProviderLua = pool.poll();
        if (replaceProviderLua == null) {
            replaceProviderLua = new ReplaceProviderLua();
        }

        replaceProviderLua.reset();
        return replaceProviderLua;
    }

    private void reset() {
        for (Entry entry : this.m_keys.entrySet()) {
            if (entry.getValue() instanceof ReplaceList) {
                release_list((ReplaceList)entry.getValue());
            } else {
                release_single((ReplaceSingle)entry.getValue());
            }
        }

        this.m_keys.clear();
    }

    public void release() {
        this.reset();
        pool.offer(this);
    }

    public void fromLuaTable(KahluaTableImpl kahluaTableImpl0) {
        for (Entry entry : kahluaTableImpl0.delegate.entrySet()) {
            if (entry.getKey() instanceof String) {
                if (entry.getValue() instanceof String) {
                    this.addKey((String)entry.getKey(), (String)entry.getValue());
                } else if (entry.getValue() instanceof KahluaTableImpl) {
                    KahluaTableImpl kahluaTableImpl1 = (KahluaTableImpl)entry.getValue();
                    ReplaceList replaceList = alloc_list();

                    for (int int0 = 1; int0 < kahluaTableImpl1.len() + 1; int0++) {
                        replaceList.getReplacements().add((String)kahluaTableImpl1.rawget(int0));
                    }

                    if (replaceList.getReplacements().size() > 0) {
                        this.addReplacer((String)entry.getKey(), replaceList);
                    } else {
                        DebugLog.log("ReplaceProvider -> key '" + entry.getKey() + "' contains no entries, ignoring.");
                        release_list(replaceList);
                    }
                }
            }
        }
    }

    @Override
    public void addKey(String string1, String string0) {
        ReplaceSingle replaceSingle = alloc_single();
        replaceSingle.setValue(string0);
        this.addReplacer(string1, replaceSingle);
    }
}
