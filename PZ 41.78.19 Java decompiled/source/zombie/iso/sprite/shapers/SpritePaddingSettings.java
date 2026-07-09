// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso.sprite.shapers;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.TransformerException;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;

public class SpritePaddingSettings {
    private static SpritePaddingSettings.Settings m_settings = null;
    private static String m_settingsFilePath = null;
    private static PredicatedFileWatcher m_fileWatcher = null;

    public static void settingsFileChanged(SpritePaddingSettings.Settings settings) {
        DebugLog.General.println("Settings file changed.");
        m_settings = settings;
    }

    private static void loadSettings() {
        String string = getSettingsFilePath();
        File file = new File(string).getAbsoluteFile();
        if (file.isFile()) {
            try {
                m_settings = PZXmlUtil.parse(SpritePaddingSettings.Settings.class, file.getPath());
            } catch (PZXmlParserException pZXmlParserException) {
                DebugLog.General.printException(pZXmlParserException, "Error parsing file: " + string, LogSeverity.Warning);
                m_settings = new SpritePaddingSettings.Settings();
            }
        } else {
            m_settings = new SpritePaddingSettings.Settings();
            saveSettings();
        }

        if (m_fileWatcher == null) {
            m_fileWatcher = new PredicatedFileWatcher(string, SpritePaddingSettings.Settings.class, SpritePaddingSettings::settingsFileChanged);
            DebugFileWatcher.instance.add(m_fileWatcher);
        }
    }

    private static String getSettingsFilePath() {
        if (m_settingsFilePath == null) {
            m_settingsFilePath = ZomboidFileSystem.instance.getLocalWorkDirSub("SpritePaddingSettings.xml");
        }

        return m_settingsFilePath;
    }

    private static void saveSettings() {
        try {
            PZXmlUtil.write(m_settings, new File(getSettingsFilePath()).getAbsoluteFile());
        } catch (IOException | JAXBException | TransformerException transformerException) {
            transformerException.printStackTrace();
        }
    }

    public static SpritePaddingSettings.Settings getSettings() {
        if (m_settings == null) {
            loadSettings();
        }

        return m_settings;
    }

    public abstract static class GenericZoomBasedSettingGroup {
        public abstract <ZoomBasedSetting> ZoomBasedSetting getCurrentZoomSetting();

        public static <ZoomBasedSetting> ZoomBasedSetting getCurrentZoomSetting(ZoomBasedSetting object0, ZoomBasedSetting object1, ZoomBasedSetting object2) {
            float float0 = Core.getInstance().getCurrentPlayerZoom();
            if (float0 < 1.0F) {
                return (ZoomBasedSetting)object0;
            } else {
                return (ZoomBasedSetting)(float0 == 1.0F ? object1 : object2);
            }
        }
    }

    @XmlRootElement(
        name = "FloorShaperDeDiamondSettings"
    )
    public static class Settings {
        public SpritePadding.IsoPaddingSettings IsoPadding = new SpritePadding.IsoPaddingSettings();
        public FloorShaperDeDiamond.Settings FloorDeDiamond = new FloorShaperDeDiamond.Settings();
        public FloorShaperAttachedSprites.Settings AttachedSprites = new FloorShaperAttachedSprites.Settings();
    }
}
