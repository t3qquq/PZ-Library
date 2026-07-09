// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.text.templating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import se.krka.kahlua.j2se.KahluaTableImpl;
import zombie.debug.DebugLog;

public class TemplateTextBuilder implements ITemplateBuilder {
    private static final String fieldStart = "\\$\\{";
    private static final String fieldEnd = "\\}";
    private static final String regex = "\\$\\{([^}]+)\\}";
    private static final Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
    private Map<String, IReplace> m_keys = new HashMap<>();

    protected TemplateTextBuilder() {
    }

    @Override
    public void Reset() {
        this.m_keys.clear();
    }

    @Override
    public String Build(String string) {
        return this.format(string, null);
    }

    @Override
    public String Build(String string, IReplaceProvider iReplaceProvider) {
        return this.format(string, iReplaceProvider);
    }

    @Override
    public String Build(String string1, KahluaTableImpl kahluaTableImpl) {
        ReplaceProviderLua replaceProviderLua = ReplaceProviderLua.Alloc();
        replaceProviderLua.fromLuaTable(kahluaTableImpl);
        String string0 = this.format(string1, replaceProviderLua);
        replaceProviderLua.release();
        return string0;
    }

    private String format(String string0, IReplaceProvider iReplaceProvider) {
        Matcher matcher = pattern.matcher(string0);
        String string1 = string0;

        while (matcher.find()) {
            String string2 = matcher.group(1).toLowerCase().trim();
            String string3 = null;
            if (iReplaceProvider != null && iReplaceProvider.hasReplacer(string2)) {
                string3 = iReplaceProvider.getReplacer(string2).getString();
            } else {
                IReplace iReplace = this.m_keys.get(string2);
                if (iReplace != null) {
                    string3 = iReplace.getString();
                }
            }

            if (string3 == null) {
                string3 = "missing_" + string2;
            }

            string1 = string1.replaceFirst("\\$\\{([^}]+)\\}", string3);
        }

        return string1;
    }

    @Override
    public void RegisterKey(String string, KahluaTableImpl kahluaTableImpl) {
        try {
            ArrayList arrayList = new ArrayList();

            for (int int0 = 1; int0 < kahluaTableImpl.len() + 1; int0++) {
                arrayList.add((String)kahluaTableImpl.rawget(int0));
            }

            if (arrayList.size() > 0) {
                this.localRegisterKey(string, new ReplaceList(arrayList));
            } else {
                DebugLog.log("TemplateTextBuilder -> key '" + string + "' contains no entries, ignoring.");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void RegisterKey(String string, IReplace iReplace) {
        this.localRegisterKey(string, iReplace);
    }

    private void localRegisterKey(String string, IReplace iReplace) {
        if (this.m_keys.containsKey(string.toLowerCase().trim())) {
            DebugLog.log("TemplateTextBuilder -> Warning: key '" + string + "' replaces an existing key.");
        }

        this.m_keys.put(string.toLowerCase().trim(), iReplace);
    }
}
