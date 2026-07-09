// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

public final class Language {
    private final int index;
    private final String name;
    private final String text;
    private final String charset;
    private final String base;
    private final boolean azerty;

    Language(int int0, String string0, String string1, String string2, String string3, boolean boolean0) {
        this.index = int0;
        this.name = string0;
        this.text = string1;
        this.charset = string2;
        this.base = string3;
        this.azerty = boolean0;
    }

    public int index() {
        return this.index;
    }

    public String name() {
        return this.name;
    }

    public String text() {
        return this.text;
    }

    public String charset() {
        return this.charset;
    }

    public String base() {
        return this.base;
    }

    public boolean isAzerty() {
        return this.azerty;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Language fromIndex(int _index) {
        return Languages.instance.getByIndex(_index);
    }

    public static Language FromString(String str) {
        Language language = Languages.instance.getByName(str);
        if (language == null) {
            language = Languages.instance.getDefaultLanguage();
        }

        return language;
    }
}
