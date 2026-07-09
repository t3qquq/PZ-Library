// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.text.templating;

public class ReplaceSingle implements IReplace {
    private String value = "";

    public ReplaceSingle() {
    }

    public ReplaceSingle(String string) {
        this.value = string;
    }

    protected String getValue() {
        return this.value;
    }

    protected void setValue(String string) {
        this.value = string;
    }

    @Override
    public String getString() {
        return this.value;
    }
}
