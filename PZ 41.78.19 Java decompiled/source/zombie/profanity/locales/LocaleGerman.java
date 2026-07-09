// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.profanity.locales;

import java.util.regex.Matcher;
import zombie.profanity.Phonizer;

public class LocaleGerman extends LocaleEnglish {
    public LocaleGerman(String string) {
        super(string);
    }

    @Override
    protected void Init() {
        this.storeVowelsAmount = 3;
        super.Init();
        this.addPhonizer(new Phonizer("ringelS", "(?<ringelS>\u00c3\u0178)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "S");
                }
            }
        });
    }
}
