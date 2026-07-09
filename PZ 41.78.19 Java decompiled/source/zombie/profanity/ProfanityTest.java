// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.profanity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import zombie.ZomboidFileSystem;
import zombie.profanity.locales.Locale;

public class ProfanityTest {
    public static void runTest() {
        ProfanityFilter profanityFilter = ProfanityFilter.getInstance();
        System.out.println("");
        loadDictionary();
        testString(
            1,
            "profane stuff:  f u c k. sex xex h4rd \u00c3\u0178hit knight hello, @ $ $ H O L E   ass-hole f-u-c-k f_u_c_k_ @$$h0le fu'ckeerr: sdsi: KUNT as'as!! ffffuuuccckkkerrr"
        );
    }

    public static void testString(int int0, String string1) {
        ProfanityFilter profanityFilter = ProfanityFilter.getInstance();
        String string0 = "";
        System.out.println("Benchmarking " + int0 + " iterations: ");
        System.out.println("Original: " + string1);
        long long0 = System.nanoTime();

        for (int int1 = 0; int1 < int0; int1++) {
            string0 = profanityFilter.filterString(string1);
        }

        long long1 = System.nanoTime();
        long long2 = long1 - long0;
        System.out.println("Done, time spent: " + (float)long2 / 1.0E9F + " seconds");
        System.out.println("Result: " + string0);
        System.out.println("");
    }

    public static void loadDictionary() {
        System.out.println("");
        System.out.println("Dictionary: ");
        long long0 = System.nanoTime();
        ProfanityFilter profanityFilter = ProfanityFilter.getInstance();

        try {
            File file = ZomboidFileSystem.instance.getMediaFile("profanity" + File.separator + "Dictionary.txt");
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            new StringBuffer();
            int int0 = 0;
            int int1 = 0;

            String string0;
            for (Locale locale = profanityFilter.getLocale(); (string0 = bufferedReader.readLine()) != null; int0++) {
                String string1 = locale.returnMatchSetForWord(string0);
                if (string1 != null) {
                    System.out.println("Found match: " + string0.trim() + ", Phonized: " + locale.returnPhonizedWord(string0.trim()) + ", Set: " + string1);
                    int1++;
                }
            }

            fileReader.close();
            System.out
                .println("Profanity filter tested " + profanityFilter.getFilterWordsCount() + " blacklisted words against " + int0 + " words from dictionary.");
            System.out.println("Found " + int1 + " matches.");
        } catch (IOException iOException) {
            iOException.printStackTrace();
        }

        long long1 = System.nanoTime();
        long long2 = long1 - long0;
        System.out.println("Done, time spent: " + (float)long2 / 1.0E9F + " seconds");
        System.out.println("");
    }
}
