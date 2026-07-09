// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.profanity.locales;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import zombie.ZomboidFileSystem;
import zombie.profanity.Phonizer;
import zombie.profanity.ProfanityFilter;

public abstract class Locale {
    protected String id;
    protected int storeVowelsAmount = 3;
    protected String phoneticRules = "";
    protected Map<String, Phonizer> phonizers = new HashMap<>();
    protected Map<String, String> filterWords = new HashMap<>();
    protected List<String> filterWordsRaw = new ArrayList<>();
    protected List<String> filterContains = new ArrayList<>();
    protected ArrayList<String> whitelistWords = new ArrayList<>();
    protected Pattern pattern;
    private Pattern preProcessLeet = Pattern.compile("(?<leet>[\\$@34701])\\k<leet>*|(?<nonWord>[^A-Z\\s\\$@34701]+)");
    private Pattern preProcessDoubles = Pattern.compile("(?<doublechar>[A-Z])\\k<doublechar>+");
    private Pattern preProcessVowels = Pattern.compile("(?<vowel>[AOUIE])");

    protected Locale(String string) {
        this.id = string;
        this.Init();
        this.finalizeData();
        this.loadFilterWords();
        this.loadFilterContains();
        this.loadWhiteListWords();
        ProfanityFilter.printDebug("Done init locale: " + this.id);
    }

    public String getID() {
        return this.id;
    }

    public String getPhoneticRules() {
        return this.phoneticRules;
    }

    public int getFilterWordsCount() {
        return this.filterWords.size();
    }

    protected abstract void Init();

    public void addWhiteListWord(String string) {
        string = string.toUpperCase().trim();
        if (!this.whitelistWords.contains(string)) {
            this.whitelistWords.add(string);
        }
    }

    public void removeWhiteListWord(String string) {
        string = string.toUpperCase().trim();
        if (this.whitelistWords.contains(string)) {
            this.whitelistWords.remove(string);
        }
    }

    public boolean isWhiteListedWord(String string) {
        return this.whitelistWords.contains(string.toUpperCase().trim());
    }

    public void addFilterWord(String string1) {
        String string0 = this.phonizeWord(string1);
        if (string0.length() > 2) {
            String string2 = "";
            if (this.filterWords.containsKey(string0)) {
                string2 = string2 + this.filterWords.get(string0) + ",";
            }

            ProfanityFilter.printDebug("Adding word: " + string1 + ", Phonized: " + string0);
            this.filterWords.put(string0, string2 + string1.toLowerCase());
        } else {
            ProfanityFilter.printDebug("Refusing word: " + string1 + ", Phonized: " + string0 + ", null or phonized < 2 characters");
        }
    }

    public void removeFilterWord(String string1) {
        String string0 = this.phonizeWord(string1);
        if (this.filterWords.containsKey(string0)) {
            this.filterWords.remove(string0);
        }
    }

    public void addFilterContains(String string) {
        if (string != null && !string.isEmpty() && !this.filterContains.contains(string.toUpperCase())) {
            this.filterContains.add(string.toUpperCase());
        }
    }

    public void removeFilterContains(String string) {
        this.filterContains.remove(string.toUpperCase());
    }

    public void addFilterRawWord(String string) {
        if (string != null && !string.isEmpty() && !this.filterWordsRaw.contains(string.toUpperCase())) {
            this.filterWordsRaw.add(string.toUpperCase());
        }
    }

    public void removeFilterWordRaw(String string) {
        this.filterWordsRaw.remove(string.toUpperCase());
    }

    protected String repeatString(int int0, char char0) {
        char[] chars = new char[int0];
        Arrays.fill(chars, char0);
        return new String(chars);
    }

    protected boolean containsIgnoreCase(String string1, String string0) {
        if (string1 != null && string0 != null) {
            int int0 = string0.length();
            if (int0 == 0) {
                return true;
            } else {
                for (int int1 = string1.length() - int0; int1 >= 0; int1--) {
                    if (string1.regionMatches(true, int1, string0, 0, int0)) {
                        return true;
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }

    public String filterWord(String string) {
        return this.filterWord(string, false);
    }

    public String filterWord(String string0, boolean boolean0) {
        if (this.isWhiteListedWord(string0)) {
            return string0;
        } else {
            String string1 = this.phonizeWord(string0);
            if (this.filterWords.containsKey(string1)) {
                return new String(new char[string0.length()]).replace('\u0000', '*');
            } else {
                if (this.filterWordsRaw.size() > 0) {
                    for (int int0 = 0; int0 < this.filterWordsRaw.size(); int0++) {
                        if (string0.equalsIgnoreCase(this.filterWordsRaw.get(int0))) {
                            return new String(new char[string0.length()]).replace('\u0000', '*');
                        }
                    }
                }

                if (boolean0) {
                    for (int int1 = 0; int1 < this.filterContains.size(); int1++) {
                        String string2 = this.filterContains.get(int1);
                        if (this.containsIgnoreCase(string0, string2)) {
                            string0 = string0.replaceAll("(?i)" + Pattern.quote(string2), this.repeatString(string2.length(), '*'));
                        }
                    }
                }

                return string0;
            }
        }
    }

    public String validateWord(String string0, boolean boolean0) {
        if (this.isWhiteListedWord(string0)) {
            return null;
        } else {
            String string1 = this.phonizeWord(string0);
            if (this.filterWords.containsKey(string1)) {
                return string0;
            } else {
                if (this.filterWordsRaw.size() > 0) {
                    for (int int0 = 0; int0 < this.filterWordsRaw.size(); int0++) {
                        if (string0.equalsIgnoreCase(this.filterWordsRaw.get(int0))) {
                            return string0;
                        }
                    }
                }

                if (boolean0) {
                    for (int int1 = 0; int1 < this.filterContains.size(); int1++) {
                        String string2 = this.filterContains.get(int1);
                        if (this.containsIgnoreCase(string0, string2)) {
                            return string2.toLowerCase();
                        }
                    }
                }

                return null;
            }
        }
    }

    public String returnMatchSetForWord(String string1) {
        String string0 = this.phonizeWord(string1);
        return this.filterWords.containsKey(string0) ? this.filterWords.get(string0) : null;
    }

    public String returnPhonizedWord(String string) {
        return this.phonizeWord(string);
    }

    protected String phonizeWord(String string) {
        string = string.toUpperCase().trim();
        if (this.whitelistWords.contains(string)) {
            return string;
        } else {
            string = this.preProcessWord(string);
            if (this.phonizers.size() <= 0) {
                return string;
            } else {
                Matcher matcher = this.pattern.matcher(string);
                StringBuffer stringBuffer = new StringBuffer();

                while (matcher.find()) {
                    for (Entry entry : this.phonizers.entrySet()) {
                        if (matcher.group((String)entry.getKey()) != null) {
                            ((Phonizer)entry.getValue()).execute(matcher, stringBuffer);
                            break;
                        }
                    }
                }

                matcher.appendTail(stringBuffer);
                return stringBuffer.toString();
            }
        }
    }

    private String preProcessWord(String string0) {
        Matcher matcher = this.preProcessLeet.matcher(string0);
        StringBuffer stringBuffer = new StringBuffer();

        while (matcher.find()) {
            if (matcher.group("leet") != null) {
                String string1 = matcher.group("leet").toString();
                switch (string1) {
                    case "$":
                        matcher.appendReplacement(stringBuffer, "S");
                        break;
                    case "4":
                    case "@":
                        matcher.appendReplacement(stringBuffer, "A");
                        break;
                    case "3":
                        matcher.appendReplacement(stringBuffer, "E");
                        break;
                    case "7":
                        matcher.appendReplacement(stringBuffer, "T");
                        break;
                    case "0":
                        matcher.appendReplacement(stringBuffer, "O");
                        break;
                    case "1":
                        matcher.appendReplacement(stringBuffer, "I");
                }
            } else if (matcher.group("nonWord") != null) {
                matcher.appendReplacement(stringBuffer, "");
            }
        }

        matcher.appendTail(stringBuffer);
        matcher = this.preProcessDoubles.matcher(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.capacity());

        while (matcher.find()) {
            if (matcher.group("doublechar") != null) {
                matcher.appendReplacement(stringBuffer, "${doublechar}");
            }
        }

        matcher.appendTail(stringBuffer);
        matcher = this.preProcessVowels.matcher(stringBuffer.toString());
        stringBuffer.delete(0, stringBuffer.capacity());
        int int0 = 0;

        while (matcher.find()) {
            if (matcher.group("vowel") != null) {
                if (int0 < this.storeVowelsAmount) {
                    matcher.appendReplacement(stringBuffer, "${vowel}");
                    int0++;
                } else {
                    matcher.appendReplacement(stringBuffer, "");
                }
            }
        }

        matcher.appendTail(stringBuffer);
        return stringBuffer.toString();
    }

    protected void addPhonizer(Phonizer phonizer) {
        if (phonizer != null && !this.phonizers.containsKey(phonizer.getName())) {
            this.phonizers.put(phonizer.getName(), phonizer);
        }
    }

    protected void finalizeData() {
        this.phoneticRules = "";
        int int0 = this.phonizers.size();
        int int1 = 0;

        for (Phonizer phonizer : this.phonizers.values()) {
            this.phoneticRules = this.phoneticRules + phonizer.getRegex();
            if (++int1 < int0) {
                this.phoneticRules = this.phoneticRules + "|";
            }
        }

        ProfanityFilter.printDebug("PhoneticRules: " + this.phoneticRules);
        this.pattern = Pattern.compile(this.phoneticRules);
    }

    protected void loadFilterWords() {
        try {
            String string0 = ZomboidFileSystem.instance.getString(ProfanityFilter.LOCALES_DIR + "blacklist_" + this.id + ".txt");
            File file = new File(string0);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String string1;
            int int0;
            for (int0 = 0; (string1 = bufferedReader.readLine()) != null; int0++) {
                this.addFilterWord(string1);
            }

            fileReader.close();
            ProfanityFilter.printDebug("BlackList, " + int0 + " added.");
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    protected void loadFilterContains() {
        try {
            String string0 = ZomboidFileSystem.instance.getString(ProfanityFilter.LOCALES_DIR + "blacklist_contains_" + this.id + ".txt");
            File file = new File(string0);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int int0 = 0;

            String string1;
            while ((string1 = bufferedReader.readLine()) != null) {
                if (!string1.startsWith("//")) {
                    this.addFilterContains(string1);
                    int0++;
                }
            }

            fileReader.close();
            ProfanityFilter.printDebug("BlackList contains, " + int0 + " added.");
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }

    protected void loadWhiteListWords() {
        try {
            String string0 = ZomboidFileSystem.instance.getString(ProfanityFilter.LOCALES_DIR + "whitelist_" + this.id + ".txt");
            File file = new File(string0);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String string1;
            int int0;
            for (int0 = 0; (string1 = bufferedReader.readLine()) != null; int0++) {
                this.addWhiteListWord(string1);
            }

            fileReader.close();
            ProfanityFilter.printDebug("WhiteList, " + int0 + " added.");
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }
    }
}
