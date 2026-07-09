// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package se.krka.kahlua.integration.expose;

public class MethodParameter {
    private final String name;
    private final String type;
    private final String description;

    public MethodParameter(String string0, String string1, String string2) {
        this.name = string0;
        this.type = string1;
        this.description = string2;
    }

    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getDescription() {
        return this.description;
    }
}
