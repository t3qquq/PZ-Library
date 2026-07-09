// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.radio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import zombie.core.Language;
import zombie.core.Translator;

/**
 * Turbo
 */
public final class RadioTranslationData {
    private String filePath;
    private String guid;
    private String language;
    private Language languageEnum;
    private int version = -1;
    private final ArrayList<String> translators = new ArrayList<>();
    private final Map<String, String> translations = new HashMap<>();

    public RadioTranslationData(String file) {
        this.filePath = file;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getGuid() {
        return this.guid;
    }

    public String getLanguage() {
        return this.language;
    }

    public Language getLanguageEnum() {
        return this.languageEnum;
    }

    public int getVersion() {
        return this.version;
    }

    public int getTranslationCount() {
        return this.translations.size();
    }

    public ArrayList<String> getTranslators() {
        return this.translators;
    }

    public boolean validate() {
        return this.guid != null && this.language != null && this.version >= 0;
    }

    public boolean loadTranslations() {
        boolean boolean0 = false;
        if (Translator.getLanguage() != this.languageEnum) {
            System.out.println("Radio translations trying to load language that is not the current language...");
            return false;
        } else {
            try {
                File file = new File(this.filePath);
                if (file.exists() && !file.isDirectory()) {
                    BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(this.filePath), Charset.forName(this.languageEnum.charset()))
                    );
                    String string0 = null;
                    boolean boolean1 = false;
                    ArrayList arrayList = new ArrayList();

                    while ((string0 = bufferedReader.readLine()) != null) {
                        string0 = string0.trim();
                        if (string0.equals("[Translations]")) {
                            boolean1 = true;
                        } else if (boolean1) {
                            if (!string0.equals("[Collection]")) {
                                if (string0.equals("[/Translations]")) {
                                    boolean0 = true;
                                    break;
                                }

                                String[] strings0 = string0.split("=", 2);
                                if (strings0.length == 2) {
                                    String string1 = strings0[0].trim();
                                    String string2 = strings0[1].trim();
                                    this.translations.put(string1, string2);
                                }
                            } else {
                                String string3 = null;

                                while ((string0 = bufferedReader.readLine()) != null) {
                                    string0 = string0.trim();
                                    if (string0.equals("[/Collection]")) {
                                        break;
                                    }

                                    String[] strings1 = string0.split("=", 2);
                                    if (strings1.length == 2) {
                                        String string4 = strings1[0].trim();
                                        String string5 = strings1[1].trim();
                                        if (string4.equals("text")) {
                                            string3 = string5;
                                        } else if (string4.equals("member")) {
                                            arrayList.add(string5);
                                        }
                                    }
                                }

                                if (string3 != null && arrayList.size() > 0) {
                                    for (String string6 : arrayList) {
                                        this.translations.put(string6, string3);
                                    }
                                }

                                arrayList.clear();
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                boolean0 = false;
            }

            return boolean0;
        }
    }

    public String getTranslation(String _guid) {
        return this.translations.containsKey(_guid) ? this.translations.get(_guid) : null;
    }

    public static RadioTranslationData ReadFile(String file) {
        RadioTranslationData radioTranslationData = new RadioTranslationData(file);
        File _file = new File(file);
        if (_file.exists() && !_file.isDirectory()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                String string0 = null;

                while ((string0 = bufferedReader.readLine()) != null) {
                    String[] strings0 = string0.split("=");
                    if (strings0.length > 1) {
                        String string1 = strings0[0].trim();
                        String string2 = "";

                        for (int int0 = 1; int0 < strings0.length; int0++) {
                            string2 = string2 + strings0[int0];
                        }

                        string2 = string2.trim();
                        if (string1.equals("guid")) {
                            radioTranslationData.guid = string2;
                        } else if (string1.equals("language")) {
                            radioTranslationData.language = string2;
                        } else if (string1.equals("version")) {
                            radioTranslationData.version = Integer.parseInt(string2);
                        } else if (string1.equals("translator")) {
                            String[] strings1 = string2.split(",");
                            if (strings1.length > 0) {
                                for (String string3 : strings1) {
                                    radioTranslationData.translators.add(string3);
                                }
                            }
                        }
                    }

                    string0 = string0.trim();
                    if (string0.equals("[/Info]")) {
                        break;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        boolean boolean0 = false;
        if (radioTranslationData.language != null) {
            for (Language languagex : Translator.getAvailableLanguage()) {
                if (languagex.toString().equals(radioTranslationData.language)) {
                    radioTranslationData.languageEnum = languagex;
                    boolean0 = true;
                    break;
                }
            }
        }

        if (!boolean0 && radioTranslationData.language != null) {
            System.out.println("Language " + radioTranslationData.language + " not found");
            return null;
        } else {
            return radioTranslationData.guid != null && radioTranslationData.language != null && radioTranslationData.version >= 0
                ? radioTranslationData
                : null;
        }
    }
}
