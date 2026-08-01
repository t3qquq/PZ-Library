// Decompiled with Zomboid Decompiler v0.3.1 using Vineflower.
package zombie.profanity.locales;

import java.util.regex.Matcher;
import zombie.profanity.Phonizer;

public class LocaleEnglish extends Locale {
    public LocaleEnglish(String tag) {
        super(tag);
    }

    @Override
    protected void Init() {
        this.storeVowelsAmount = 3;
        this.addFilterRawWord("ass");
        this.addPhonizer(new Phonizer("strt", "(?<strt>^(?:KN|GN|PN|AE|WR))") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, m.group(this.getName()).substring(1, 2));
                }
            }
        });
        this.addPhonizer(new Phonizer("dropY", "(?<dropY>(?<=M)B$)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "");
                }
            }
        });
        this.addPhonizer(new Phonizer("dropB", "(?<dropB>(?<=M)B$)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "");
                }
            }
        });
        this.addPhonizer(new Phonizer("z", "(?<z>Z)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "S");
                }
            }
        });
        this.addPhonizer(new Phonizer("ck", "(?<ck>CK)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "K");
                }
            }
        });
        this.addPhonizer(new Phonizer("q", "(?<q>Q)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "K");
                }
            }
        });
        this.addPhonizer(new Phonizer("v", "(?<v>V)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "F");
                }
            }
        });
        this.addPhonizer(new Phonizer("xS", "(?<xS>^X)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "S");
                }
            }
        });
        this.addPhonizer(new Phonizer("xKS", "(?<xKS>(?<=\\w)X)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "KS");
                }
            }
        });
        this.addPhonizer(new Phonizer("ph", "(?<ph>PH)") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "F");
                }
            }
        });
        this.addPhonizer(new Phonizer("c", "(?<c>C(?=[AUOIE]))") {
            @Override
            public void execute(Matcher m, StringBuffer s) {
                if (m.group(this.getName()) != null) {
                    m.appendReplacement(s, "K");
                }
            }
        });
    }
}
