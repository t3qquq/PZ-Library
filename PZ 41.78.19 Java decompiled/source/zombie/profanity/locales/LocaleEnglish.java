// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.profanity.locales;

import java.util.regex.Matcher;
import zombie.profanity.Phonizer;

public class LocaleEnglish extends Locale {
    public LocaleEnglish(String string) {
        super(string);
    }

    @Override
    protected void Init() {
        this.storeVowelsAmount = 3;
        this.addFilterRawWord("ass");
        this.addPhonizer(new Phonizer("strt", "(?<strt>^(?:KN|GN|PN|AE|WR))") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, matcher.group(this.getName()).toString().substring(1, 2));
                }
            }
        });
        this.addPhonizer(new Phonizer("dropY", "(?<dropY>(?<=M)B$)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "");
                }
            }
        });
        this.addPhonizer(new Phonizer("dropB", "(?<dropB>(?<=M)B$)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "");
                }
            }
        });
        this.addPhonizer(new Phonizer("z", "(?<z>Z)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "S");
                }
            }
        });
        this.addPhonizer(new Phonizer("ck", "(?<ck>CK)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "K");
                }
            }
        });
        this.addPhonizer(new Phonizer("q", "(?<q>Q)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "K");
                }
            }
        });
        this.addPhonizer(new Phonizer("v", "(?<v>V)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "F");
                }
            }
        });
        this.addPhonizer(new Phonizer("xS", "(?<xS>^X)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "S");
                }
            }
        });
        this.addPhonizer(new Phonizer("xKS", "(?<xKS>(?<=\\w)X)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "KS");
                }
            }
        });
        this.addPhonizer(new Phonizer("ph", "(?<ph>PH)") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "F");
                }
            }
        });
        this.addPhonizer(new Phonizer("c", "(?<c>C(?=[AUOIE]))") {
            @Override
            public void execute(Matcher matcher, StringBuffer stringBuffer) {
                if (matcher.group(this.getName()) != null) {
                    matcher.appendReplacement(stringBuffer, "K");
                }
            }
        });
    }
}
