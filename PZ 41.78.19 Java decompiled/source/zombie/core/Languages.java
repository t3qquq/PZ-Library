// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.gameStates.ChooseGameInfo;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;

public final class Languages {
    public static final Languages instance = new Languages();
    private final ArrayList<Language> m_languages = new ArrayList<>();
    private Language m_defaultLanguage = new Language(0, "EN", "English", "UTF-8", null, false);

    public Languages() {
        this.m_languages.add(this.m_defaultLanguage);
    }

    public void init() {
        this.m_languages.clear();
        this.m_defaultLanguage = new Language(0, "EN", "English", "UTF-8", null, false);
        this.m_languages.add(this.m_defaultLanguage);
        this.loadTranslateDirectory(ZomboidFileSystem.instance.getMediaPath("lua/shared/Translate"));

        for (String string : ZomboidFileSystem.instance.getModIDs()) {
            ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
            if (mod != null) {
                File file = new File(mod.getDir(), "media/lua/shared/Translate");
                if (file.isDirectory()) {
                    this.loadTranslateDirectory(file.getAbsolutePath());
                }
            }
        }
    }

    public Language getDefaultLanguage() {
        return this.m_defaultLanguage;
    }

    public int getNumLanguages() {
        return this.m_languages.size();
    }

    public Language getByIndex(int int0) {
        return int0 >= 0 && int0 < this.m_languages.size() ? this.m_languages.get(int0) : null;
    }

    public Language getByName(String string) {
        return PZArrayUtil.find(this.m_languages, Lambda.predicate(string, (language, stringx) -> language.name().equalsIgnoreCase(stringx)));
    }

    public int getIndexByName(String string) {
        return PZArrayUtil.indexOf(this.m_languages, Lambda.predicate(string, (language, stringx) -> language.name().equalsIgnoreCase(stringx)));
    }

    private void loadTranslateDirectory(String string) {
        Filter filter = path -> Files.isDirectory(path) && Files.exists(path.resolve("language.txt"));
        Path path0 = FileSystems.getDefault().getPath(string);
        if (Files.exists(path0)) {
            try (DirectoryStream directoryStream = Files.newDirectoryStream(path0, filter)) {
                for (Path path1 : directoryStream) {
                    LanguageFileData languageFileData = this.loadLanguageDirectory(path1.toAbsolutePath());
                    if (languageFileData != null) {
                        int int0 = this.getIndexByName(languageFileData.name);
                        if (int0 == -1) {
                            Language language0 = new Language(
                                this.m_languages.size(),
                                languageFileData.name,
                                languageFileData.text,
                                languageFileData.charset,
                                languageFileData.base,
                                languageFileData.azerty
                            );
                            this.m_languages.add(language0);
                        } else {
                            Language language1 = new Language(
                                int0, languageFileData.name, languageFileData.text, languageFileData.charset, languageFileData.base, languageFileData.azerty
                            );
                            this.m_languages.set(int0, language1);
                            if (languageFileData.name.equals(this.m_defaultLanguage.name())) {
                                this.m_defaultLanguage = language1;
                            }
                        }
                    }
                }
            } catch (Exception exception) {
                ExceptionLogger.logException(exception);
            }
        }
    }

    private LanguageFileData loadLanguageDirectory(Path path) {
        String string0 = path.getFileName().toString();
        LanguageFileData languageFileData = new LanguageFileData();
        languageFileData.name = string0;
        LanguageFile languageFile = new LanguageFile();
        String string1 = path.resolve("language.txt").toString();
        return !languageFile.read(string1, languageFileData) ? null : languageFileData;
    }
}
