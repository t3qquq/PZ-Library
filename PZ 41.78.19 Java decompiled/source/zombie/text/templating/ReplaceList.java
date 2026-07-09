// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.text.templating;

import java.util.ArrayList;

public class ReplaceList implements IReplace {
    private final ArrayList<String> replacements;

    public ReplaceList() {
        this.replacements = new ArrayList<>();
    }

    public ReplaceList(ArrayList<String> arrayList) {
        this.replacements = arrayList;
    }

    protected ArrayList<String> getReplacements() {
        return this.replacements;
    }

    @Override
    public String getString() {
        return this.replacements.size() == 0 ? "!ERROR_EMPTY_LIST!" : this.replacements.get(TemplateText.RandNext(this.replacements.size()));
    }
}
