// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.ZomboidFileSystem;
import zombie.characters.HairOutfitDefinitions;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;

@XmlRootElement
public class BeardStyles {
    @XmlElement(
        name = "style"
    )
    public final ArrayList<BeardStyle> m_Styles = new ArrayList<>();
    @XmlTransient
    public static BeardStyles instance;

    public static void init() {
        instance = Parse(
            ZomboidFileSystem.instance.base.getAbsolutePath()
                + File.separator
                + ZomboidFileSystem.processFilePath("media/hairStyles/beardStyles.xml", File.separatorChar)
        );
        if (instance != null) {
            instance.m_Styles.add(0, new BeardStyle());

            for (String string0 : ZomboidFileSystem.instance.getModIDs()) {
                ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string0);
                if (mod != null) {
                    String string1 = ZomboidFileSystem.instance.getModDir(string0);
                    BeardStyles beardStyles = Parse(
                        string1 + File.separator + ZomboidFileSystem.processFilePath("media/hairStyles/beardStyles.xml", File.separatorChar)
                    );
                    if (beardStyles != null) {
                        for (BeardStyle beardStyle0 : beardStyles.m_Styles) {
                            BeardStyle beardStyle1 = instance.FindStyle(beardStyle0.name);
                            if (beardStyle1 == null) {
                                instance.m_Styles.add(beardStyle0);
                            } else {
                                if (DebugLog.isEnabled(DebugType.Clothing)) {
                                    DebugLog.Clothing.println("mod \"%s\" overrides beard \"%s\"", string0, beardStyle0.name);
                                }

                                int int0 = instance.m_Styles.indexOf(beardStyle1);
                                instance.m_Styles.set(int0, beardStyle0);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void Reset() {
        if (instance != null) {
            instance.m_Styles.clear();
            instance = null;
        }
    }

    public static BeardStyles Parse(String filename) {
        try {
            return parse(filename);
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (IOException | JAXBException jAXBException) {
            ExceptionLogger.logException(jAXBException);
        }

        return null;
    }

    public static BeardStyles parse(String filename) throws JAXBException, IOException {
        BeardStyles beardStyles;
        try (FileInputStream fileInputStream = new FileInputStream(filename)) {
            JAXBContext jAXBContext = JAXBContext.newInstance(BeardStyles.class);
            Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
            beardStyles = (BeardStyles)unmarshaller.unmarshal(fileInputStream);
        }

        return beardStyles;
    }

    public BeardStyle FindStyle(String name) {
        for (int int0 = 0; int0 < this.m_Styles.size(); int0++) {
            BeardStyle beardStyle = this.m_Styles.get(int0);
            if (beardStyle.name.equalsIgnoreCase(name)) {
                return beardStyle;
            }
        }

        return null;
    }

    public String getRandomStyle(String outfitName) {
        return HairOutfitDefinitions.instance.getRandomBeard(outfitName, this.m_Styles);
    }

    public BeardStyles getInstance() {
        return instance;
    }

    public ArrayList<BeardStyle> getAllStyles() {
        return this.m_Styles;
    }
}
