// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.profanity;

import java.util.regex.Matcher;

public class Phonizer {
    private String name;
    private String regex;

    public Phonizer(String string0, String string1) {
        this.name = string0;
        this.regex = string1;
    }

    public String getName() {
        return this.name;
    }

    public String getRegex() {
        return this.regex;
    }

    public void execute(Matcher matcher, StringBuffer stringBuffer) {
        if (matcher.group(this.name) != null) {
            matcher.appendReplacement(stringBuffer, "${" + this.name + "}");
        }
    }
}
